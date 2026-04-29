package br.com.produlab.service;

import br.com.produlab.entity.Laboratory;
import br.com.produlab.entity.User;
import br.com.produlab.exception.InvalidPasswordException;
import br.com.produlab.resource.UserResource;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import io.quarkus.mailer.ReactiveMailer;
import io.quarkus.security.UnauthorizedException;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@RequestScoped
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Inject
    ReactiveMailer reactiveMailer;
    private static final String PADRAO_SENHA = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$";

    @Inject
    AuthenticationService authenticationService;

    @Transactional
    public void addUser(User user, User currentUser) {
        if (user.userLaboratories != null && !user.userLaboratories.isEmpty()) {
            user.userLaboratories = user.userLaboratories.stream().map(laboratory -> (Laboratory) Laboratory.findById(laboratory.id)).collect(Collectors.toList());
        }
        var isNew = user.id == null;
        if (isNew) {
            var userPassword = generateUserPassword(user);
            user.createdBy = currentUser;
            user.persist();
            sentEmail(user, "ProduLabs - Novo Usuário", String.format("<p>Prezado, <b>%s</b>!</p><br><p>Bem vindo ao sistema ProduLab!</p><p>Seguem suas informações de login</p><p>E-Mail: <b>%s</b></p><p>Senha: <b>%s</b></p><br/></br/>Acesse o sistema pelo endereço <a href='https://produlabs.com.br'>https://produlabs.com.br</a>", user.fullName, user.email, userPassword));
        } else {
            User u = User.findById(user.id);
            u.userLaboratories = user.userLaboratories;
            u.fullName = user.fullName;
            u.email = user.email;
            u.persist();
        }

    }

    private String generateUserPassword(User user) {
        try {
            String userPassword = RandomStringUtils.random(8, true, true);
            user.password = authenticationService.encodePassword(userPassword);
            return userPassword;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    @Transactional
    public void deleteUser(User currentUser, Long id) {
        User user = User.findById(id);
        user.deletedAt = LocalDateTime.now();
        user.deletedBy = currentUser;
        user.persistAndFlush();
    }

    private void validatePassword(String password) throws InvalidPasswordException {
        if (password == null || password.length() < 8) {
            throw new InvalidPasswordException();
        }
//        if (!password.matches(PADRAO_SENHA)) {
//            throw new InvalidPasswordException();
//        }
    }

    @Transactional
    public void updateCredentials(String email, String password, String newPassword) throws
            InvalidPasswordException {
        validatePassword(newPassword);
        try {
            User user = User.findByEmailSenha(email, authenticationService.encodePassword(password)).orElseThrow(() -> new UnauthorizedException());
            user.password = authenticationService.encodePassword(newPassword);
            user.passwordExpired = false;
            user.persistAndFlush();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    public void sentEmail(User user, String subject, String message) {
        LOGGER.info("Sent Email | User {} | Subject {}", user.email, subject);
        CompletionStage<Void> stage = reactiveMailer.send((Mail.withHtml(user.email, subject, message)));

    }

    @Transactional
    public void resetCredendials(String email) {
        User user = User.findByEmail(email).orElseThrow(() -> new NotFoundException());
        var userPassword = generateUserPassword(user);
        user.passwordExpired = true;
        user.persist();
        sentEmail(user, "ProduLabs - Senha reiniciada", String.format("<p>Prezado, <b>%s</b>!</p><br><p>Foi solicitado o envio de uma nova senha!</p><p>Seguem suas informações de login</p><p>E-Mail: <b>%s</b></p><p>Senha: <b>%s</b></p><br/></br/>Acesse o sistema pelo endereço <a href='https://produlabs.com.br'>https://produlabs.com.br</a>", user.fullName, user.email, userPassword));

    }
}
