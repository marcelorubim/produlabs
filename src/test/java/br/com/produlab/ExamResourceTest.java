package br.com.produlab;

import br.com.produlab.entity.Exam;
import br.com.produlab.entity.Sector;
import br.com.produlab.entity.User;
import br.com.produlab.entity.UserProfile;
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
import java.util.Calendar;

import static io.restassured.RestAssured.given;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
public class ExamResourceTest {

    @Inject
    JWTUtil jwtUtil;

    private String token;

    @BeforeEach
    void setupUser() {
        try {
            this.token = jwtUtil.generateTokenString(User.findById(1L));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException | ParseException | JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetAll(){
        given().contentType("application/json")
                .header("Authorization", "Bearer "+this.token).when().get("/v1/exam")
                .then()
                .statusCode(200);
    }

    @Test
    public void testGetOne(){
        given().contentType("application/json")
                .header("Authorization", "Bearer "+this.token).when().get("/v1/exam/1")
                .then()
                .statusCode(200);
    }


    @Test
    public void testAddExam(){
        Exam exam = new Exam();
        exam.name = "Exam 1";
        exam.sector = Sector.findById(1L);
        given().contentType("application/json")
                .header("Authorization", "Bearer "+this.token).body(exam).when().put("/v1/exam")
                .then()
                .statusCode(200);
    }

    @Test
    public void testDelete(){
        given().contentType("application/json")
                .header("Authorization", "Bearer "+this.token)
                .when()
                .delete("/v1/exam/2")
                .then()
                .statusCode(200);
    }

}
