package br.com.produlab;

import br.com.produlab.entity.User;
import br.com.produlab.entity.UserProfile;
import br.com.produlab.util.JWTUtil;
import com.nimbusds.jose.JOSEException;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;

import static io.restassured.RestAssured.given;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
public class SectorsResourceTest {
    @Inject
    JWTUtil jwtUtil;

    private String token;

    @BeforeEach
    @Transactional
    void setupUser() {
        try {
            this.token = jwtUtil.generateTokenString(User.findById(1L));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException | ParseException | JOSEException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    public void testGetSectors(){
        given().contentType("application/json")
                .when()
                .header("Authorization", "Bearer "+token)
                .get("/v1/sector")
                .then()
                .statusCode(200);
    }
}
