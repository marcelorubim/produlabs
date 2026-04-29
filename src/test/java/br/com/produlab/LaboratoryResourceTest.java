package br.com.produlab;

import br.com.produlab.entity.*;
import br.com.produlab.util.JWTUtil;
import com.nimbusds.jose.JOSEException;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import javax.transaction.Transactional;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.Calendar;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
public class LaboratoryResourceTest {
    private String token;

    @BeforeEach
    @Transactional
    public void setupUser() {
        try {
            this.token = JWTUtil.generateTokenString(User.findById(1L));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException | ParseException | JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Transactional
    public void testGetOne(){
        given().contentType("application/json")
                .header("Authorization", "Bearer "+token).when().get("/v1/laboratory/1")
                .then()
                .statusCode(200).body("name",startsWith("Hospital Regional Asa Norte"));
    }

    @Test
    public void testGetAll(){
        given().contentType("application/json")
                .header("Authorization", "Bearer "+token).when().get("/v1/laboratory/1")
                .then()
                .statusCode(200);
    }



    @Test
    public void testAddLaboratory(){
        Laboratory laboratory = new Laboratory();
        laboratory.name = "Guará";
        laboratory.initials = "HRG";
        laboratory.region = "Sul";
        given().contentType("application/json")
                .body(laboratory).header("Authorization", "Bearer "+token).when().put("/v1/laboratory")
                .then()
                .statusCode(200);
    }

    @Test
    public void testExamHistory(){
        ExamHistory examHistory = new ExamHistory();
        examHistory.ambulatoryValue = 100;
        examHistory.emergencyValue = 101;
        examHistory.hospitalizationValue = 102;
        examHistory.state = ExamHistoryState.DRAFT;
        given().contentType("application/json")
                .body(examHistory).header("Authorization", "Bearer "+token).when().post("/v1/laboratory/1/exam/1/20191201")
                .then()
                .statusCode(200);
    }

    @Test
    public void testGetLaboratories() {
        given().contentType("application/json")
                .when().header("Authorization", "Bearer "+token).get("/v1/laboratory")
                .then()
                .statusCode(200)
                .body("size()", is(16));
    }
}
