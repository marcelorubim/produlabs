package br.com.produlab;

import br.com.produlab.dto.AuthenticationRequest;
import br.com.produlab.dto.UpdateCredentialsRequest;
import br.com.produlab.entity.User;
import br.com.produlab.entity.UserProfile;
import br.com.produlab.service.UserService;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import javax.transaction.Transactional;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthenticationResourceTest {
    @Inject
    UserService userService;

    @Test
    @Order(1)
    public void testAutenticar() {
        AuthenticationRequest a = new AuthenticationRequest();
        a.setEmail("marcelorubim@gmail.com");
        a.setPassword("12345678");

        given().contentType("application/json")
                .body(a).when().post("/v1/authenticate")
                .then()
                .statusCode(200);
    }

    @Test
    @Order(2)
    public void testEmailNaoCadastrado() {
        AuthenticationRequest a = new AuthenticationRequest();
        a.setEmail("rubim.marcelo@gmail.com");
        a.setPassword("Kass050591");

        given().contentType("application/json")
                .body(a).when().post("/v1/authenticate")
                .then()
                .statusCode(401);
    }

    @Test
    @Order(3)
    public void testEmailOrPasswordInvalid() {
        AuthenticationRequest a = new AuthenticationRequest();
        a.setEmail("rubim.marcelo@gmail.com");
        a.setPassword("123456789");

        given().contentType("application/json")
                .body(a).when().post("/v1/authenticate")
                .then()
                .statusCode(401);
    }

    @Test
    @Order(4)
    public void testUpdateCredentials(){
        String email = "marcelorubim@gmail.com";
        String password = "Efgh5678";

        UpdateCredentialsRequest ucr = new UpdateCredentialsRequest();
        ucr.setEmail(email);
        ucr.setPassword("12345678");
        ucr.setNewPassword(password);
        given().when().contentType("application/json").body(ucr).post("/v1/authenticate/updateCredentials").then().statusCode(200);

    }

    @Test
    @Order(5)
    public void testInvalidPassword() {
        String email = "marcelorubim@gmail.com";
        String password = "12345";

        UpdateCredentialsRequest ucr = new UpdateCredentialsRequest();
        ucr.setEmail(email);
        ucr.setPassword("12345678");
        ucr.setNewPassword(password);
        given().when().contentType("application/json").body(ucr).post("/v1/authenticate/updateCredentials").then()
                .statusCode(400).body("message",equalTo("Invalid password"));
    }

    @Test
    @Order(6)
    public void testResetCredentialByEmail() {
        UpdateCredentialsRequest updateCredentialsRequest = new UpdateCredentialsRequest();
        updateCredentialsRequest.setEmail("marcelorubim@gmail.com");
        given().contentType("application/json")
                .body(updateCredentialsRequest)
                .when()
                .post("/v1/authenticate/resetCredendials")
                .then()
                .statusCode(200);
    }
}
