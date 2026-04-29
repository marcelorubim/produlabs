package br.com.produlab;

import br.com.produlab.dto.AuthenticationRequest;
import br.com.produlab.dto.UpdateCredentialsRequest;
import br.com.produlab.entity.Laboratory;
import br.com.produlab.entity.User;
import br.com.produlab.entity.UserProfile;
import br.com.produlab.exception.InvalidPasswordException;
import br.com.produlab.service.UserService;
import br.com.produlab.util.JWTUtil;
import com.nimbusds.jose.JOSEException;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import javax.transaction.Transactional;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserResourceTest {
    @Inject
    JWTUtil jwtUtil;

    private String token;
    private String tokenUserProfile;

    @BeforeEach
    @Transactional
    public void setupUser() {
        try {
            this.token = jwtUtil.generateTokenString(User.findById(1L));
            this.tokenUserProfile = jwtUtil.generateTokenString(User.findById(2L));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException | ParseException | JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(2)
    public void testAdminAddUser() {
        User user = new User();
        user.fullName = "Marcelo Rubim";
        user.password = "Abcd1234";
        user.email = "admin@gmail.com";
        user.userProfile = UserProfile.USER;
        given().contentType("application/json")
                .body(user).header("Authorization", "Bearer "+token).when().put("/v1/user")
                .then()
                .statusCode(200);
    }

    @Test
    @Order(3)
    public void testManagerAddUser() {
        User user = new User();
        user.fullName = "Marcelo Rubim";
        user.password = "Abcd1234";
        user.email = "manager@gmail.com";
        user.userProfile = UserProfile.MANAGER;
        given().contentType("application/json")
                .body(user).header("Authorization", "Bearer "+token).when().put("/v1/user")
                .then()
                .statusCode(200);
    }

    @Test
    @Order(4)
    public void testUserWithoutPermission() throws NoSuchAlgorithmException, InvalidKeySpecException, JOSEException, ParseException, IOException {
        User user = new User();
        user.fullName = "Marcelo Rubim";
        user.password = "Abcd1234";
        user.email = "marcelorubim@gmail.com";
        user.userProfile = UserProfile.USER;
        Laboratory laboratory = new Laboratory();
        laboratory.initials = "HAG";
        laboratory.name = "Aguas Claras";
        user.userLaboratories = new ArrayList<>();
        user.userLaboratories.add(laboratory);
        given().contentType("application/json")
                .body(user).header("Authorization", "Bearer "+this.tokenUserProfile).when().put("/v1/user")
                .then()
                .statusCode(403);
    }

    @Test
    @Order(5)
    public void testGetUsuario() {
        given().contentType("application/json")
                .when().header("Authorization", "Bearer "+token).get("/v1/user/1")
                .then()
                .statusCode(200)
                .body("fullName",equalTo("Marcelo Rubim"));
    }

    @Test
    @Order(6)
    public void testGetUsuarioNaoEncontrado() {
        given().contentType("application/json")
                .when().header("Authorization", "Bearer "+token).get("/v1/user/666")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(1)
    public void testGetUsuarios() {
        given().contentType("application/json")
                .when().header("Authorization", "Bearer "+token).get("/v1/user")
                .then()
                .statusCode(200)
                .body("size()", is(3));
    }

    @Test
    @Order(7)
    public void testDeleteUser() {
        given().contentType("application/json")
                .when().header("Authorization", "Bearer "+token).delete("/v1/user/4")
                .then()
                .statusCode(200);
    }
    @Test
    @Order(8)
    public void testUpdateUser() {
        User user = User.findById(1L);
        user.fullName = "UserName Updated";
        given().contentType("application/json")
                .body(user).header("Authorization", "Bearer "+token).when().put("/v1/user")
                .then()
                .statusCode(200);
    }

    @Test
    @Order(9)
    public void testResetCredential() {
        given().contentType("application/json")
                .header("Authorization", "Bearer "+token).when().get("/v1/user/reset/1")
                .then()
                .statusCode(200);
    }
}
