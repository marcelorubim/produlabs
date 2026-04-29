package br.com.produlab.service;

import br.com.produlab.dto.AuthenticationResponse;
import br.com.produlab.entity.User;
import br.com.produlab.util.JWTUtil;
import com.nimbusds.jose.JOSEException;
import io.quarkus.security.UnauthorizedException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.time.LocalDateTime;

@RequestScoped
public class AuthenticationService {

    @Inject
    JWTUtil jwtUtil;

    @Transactional
    public AuthenticationResponse authenticate(String email, String senha){
        User user = null;
        try {
            user = User.findByEmailSenha(email,encodePassword(senha)).orElseThrow(() -> new UnauthorizedException());
            if(user.passwordExpired){
                return new AuthenticationResponse(user,null,true);
            }
            updateLoginData(user);
            return new AuthenticationResponse(user, jwtUtil.generateTokenString(user));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException | ParseException | JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    private void updateLoginData(User user){
        if(user.numberLogins == null){
            user.numberLogins = 0;
        }
        user.numberLogins += 1;
        user.lastLogin = LocalDateTime.now();
        user.persist();
    }

    public String encodePassword(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(
                password.getBytes(StandardCharsets.UTF_8));
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
