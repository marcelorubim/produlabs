package br.com.produlab;

import br.com.produlab.entity.*;
import br.com.produlab.util.JWTUtil;
import com.nimbusds.jose.JOSEException;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.*;

import javax.transaction.Transactional;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.Calendar;
import static org.hamcrest.CoreMatchers.is;


import static io.restassured.RestAssured.given;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HistoryResourceTest {
    private String token;

    @BeforeEach
    @Transactional
    void setupUser() {
        try {
            this.token = JWTUtil.generateTokenString(User.findById(1L));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException | ParseException | JOSEException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    @Order(1)
    public void testGetPeriods(){
        given().contentType("application/json")
                .when()
                .header("Authorization", "Bearer "+token)
                .get("/v1/history/period")
                .then()
                .statusCode(200);
    }
    @Disabled
    @Test
    @Order(2)
    public void testGetExams(){
        given().contentType("application/json")
                .when()
                .header("Authorization", "Bearer "+token)
                .param("month",1)
                .param("year",2019)
                .get("/v1/history/exam")
                .then()
                .statusCode(200);
    }

    @Test
    @Order(3)
    public void testGetLaboratoryExams(){
        given().contentType("application/json")
                .when()
                .header("Authorization", "Bearer "+token)
                .param("month",1)
                .param("year",2019)
                .get("/v1/history/exam/laboratory/1")
                .then()
                .statusCode(200);
    }

    @Test
    @Order(4)
    public void testGetPatient(){
        given().contentType("application/json")
                .when()
                .header("Authorization", "Bearer "+token)
                .param("month",1)
                .param("year",2019)
                .get("/v1/history/patient")
                .then()
                .statusCode(200);
    }

    @Test
    @Order(5)
    public void testGetPatientLaboratory(){
        given().contentType("application/json")
                .when()
                .header("Authorization", "Bearer "+token)
                .param("month",1)
                .param("year",2019)
                .get("/v1/history/patient/laboratory/1")
                .then()
                .statusCode(200)
                .body("patientsValue",is(5689));
    }

    @Test
    @Order(6)
    public void testGetPatientLaboratoryYear(){
        given().contentType("application/json")
                .when()
                .header("Authorization", "Bearer "+token)
                .param("year",2019)
                .get("/v1/history/patient/laboratory/1")
                .then()
                .statusCode(200);
    }

    @Test
    @Order(7)
    public void testSaveExamHistory(){
        ExamHistory examHistory = new ExamHistory();
        examHistory.exam = Exam.findById(1L);
        examHistory.state = ExamHistoryState.DRAFT;
        examHistory.emergencyValue = 100;
        examHistory.hospitalizationValue = 100;
        examHistory.ambulatoryValue = 100;
        examHistory.period = LocalDate.of(2020,12,01);
        examHistory.id = new ExamHistoryId();
        examHistory.id.laboratoryID = 1L;
        examHistory.id.examID = 1L;
        examHistory.id.period = examHistory.period;
        given().contentType("application/json")
                .when()
                .header("Authorization", "Bearer "+token)
                .body(examHistory)
                .post("/v1/history/exam")
                .then()
                .statusCode(200);
    }

    @Test
    @Order(8)
    public void testUpdateExamHistory(){
        ExamHistory examHistory = (ExamHistory) ExamHistory.listAll().get(0);
        examHistory.emergencyValue = 400;
        given().contentType("application/json")
                .when()
                .header("Authorization", "Bearer "+token)
                .body(examHistory)
                .post("/v1/history/exam")
                .then()
                .statusCode(200);
    }

    @Test
    @Order(9)
    public void testAddPatientHistory(){
        PatientsHistory patientsHistory = new PatientsHistory();
        patientsHistory.state = ExamHistoryState.DRAFT;
        patientsHistory.patientsValue = 100;
        patientsHistory.laboratory = Laboratory.findById(1L);
        patientsHistory.period = LocalDate.now().minus(1,ChronoUnit.MONTHS);
        patientsHistory.id = new PatientHistoryId();
        patientsHistory.id.laboratoryID = 1L;
        patientsHistory.id.period = patientsHistory.period;
        given().contentType("application/json")
                .when()
                .header("Authorization", "Bearer "+token)
                .body(patientsHistory)
                .post("/v1/history/patient")
                .then()
                .statusCode(200);
    }

    @Test
    @Order(10)
    public void testUpdatePatientHistory(){
        PatientsHistory patientsHistory = (PatientsHistory) PatientsHistory.listAll().get(0);
        patientsHistory.patientsValue = 200;
        given().contentType("application/json")
                .when()
                .header("Authorization", "Bearer "+token)
                .body(patientsHistory)
                .post("/v1/history/patient")
                .then()
                .statusCode(200);
    }

    @Test
    @Order(11)
    public void testUpdateLaboratoryHistoryState(){
        given().contentType("application/json")
                .when()
                .header("Authorization", "Bearer "+token)
                .post("/v1/history/laboratory/1/20190101/SENT")
                .then()
                .statusCode(200);
    }

    @Test
    @Order(12)
    public void testLaboratorySummary(){
        given().contentType("application/json")
                .when()
                .header("Authorization", "Bearer "+token)
                .param("year",2019)
                .param("month",12)
                .get("/v1/history/laboratory/summary")
                .then()
                .statusCode(200)
                .body("size()",is(4))
                .body("get(0).ambulatoryValue",is(287));
    }

}
