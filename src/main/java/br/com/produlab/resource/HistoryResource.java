package br.com.produlab.resource;

import br.com.produlab.dto.LaboratorySummary;
import br.com.produlab.entity.*;
import br.com.produlab.service.HistoryService;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("/v1/history")
@PermitAll
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"user","admin","manager"})
@DenyAll
public class HistoryResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(HistoryResource.class);

    @Inject
    HistoryService historyService;
    @Inject
    JsonWebToken jwt;
    @Context
    SecurityContext ctx;

    @GET
    @Path("/download/laboratory/{id}")
    @PermitAll
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response download(@PathParam("id") Long id,@QueryParam("month") Integer month, @QueryParam("year") Integer year) throws IOException {
        LocalDate period = LocalDate.of(year,month,1);
        Laboratory laboratory = (Laboratory) Laboratory.findByIdOptional(id).orElseThrow(NotFoundException::new);
        List<ExamHistory> examsHistory = ExamHistory.findLaboratoryHistoryByPeriod(id,period,ExamHistoryState.SENT);
        List<Exam> exams =Exam.listAll();
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheetExams = workbook.createSheet("Exames");
        var rowNum = 0;
        Row header = sheetExams.createRow(rowNum++);
        var headerCellNum = 0;
        header.createCell(headerCellNum++).setCellValue("Exame");
        header.createCell(headerCellNum++).setCellValue("Código SUS");
        header.createCell(headerCellNum++).setCellValue("Internação");
        header.createCell(headerCellNum++).setCellValue("Ambulatório");
        header.createCell(headerCellNum++).setCellValue("Emergência");
        header.createCell(headerCellNum++).setCellValue("Total");
        for (Exam exam: exams) {
            Row row = sheetExams.createRow(rowNum++);
            var cellNum = 0;
            row.createCell(cellNum++).setCellValue(exam.name);
            row.createCell(cellNum++).setCellValue(exam.codeSUS);
            var ambulatoryValue = examsHistory.stream().filter(examHistory1 -> examHistory1.exam.id.equals(exam.id)).reduce(0,(acc, curr) -> acc+curr.ambulatoryValue,(Integer::sum));
            var hospitalizationValue = examsHistory.stream().filter(examHistory1 -> examHistory1.exam.id.equals(exam.id)).reduce(0,(acc, curr) -> acc+curr.hospitalizationValue,(Integer::sum));
            var emergencyValue = examsHistory.stream().filter(examHistory1 -> examHistory1.exam.id.equals(exam.id)).reduce(0,(acc, curr) -> acc+curr.emergencyValue,(Integer::sum));
            row.createCell(cellNum++).setCellValue(hospitalizationValue);
            row.createCell(cellNum++).setCellValue(ambulatoryValue);
            row.createCell(cellNum++).setCellValue(emergencyValue);
            row.createCell(cellNum).setCellValue(emergencyValue+ambulatoryValue+emergencyValue);

        }
        StreamingOutput out = workbook::write;

        return Response
                .ok(out)
                .header("Content-Disposition",  "attachment; filename=\""+laboratory.initials+"_"+period.format(DateTimeFormatter.ofPattern("MMyyyy"))+".xls\"")
                .build();
    }

    @GET
    @Path("/period")
    @RolesAllowed({"user","admin","manager"})
    public Response getPeriods(){
        final var formatter = DateTimeFormatter.ofPattern("yyyyMM");
        var periods = ExamHistory
                .listAll()
                .stream()
                .map(panacheEntityBase ->  ((ExamHistory)panacheEntityBase).period.format(formatter) )
                .collect(Collectors.toSet());
        return Response.ok(periods).build();
    }

    @POST
    @PUT
    @Path("/laboratory/{id}/{period}/{state}")
    @RolesAllowed({"admin","manager"})
    public Response updateLaboratoryHistoryState(@PathParam("id") Long id,@PathParam("period") String period,@PathParam("state") ExamHistoryState examHistoryState){
        LOGGER.info("Update Laboratory Exam History | User {} | Laboratory {} | Period {}",ctx.getUserPrincipal().getName(),id,period);
        LocalDate p = LocalDate.parse(period,DateTimeFormatter.BASIC_ISO_DATE);
        historyService.updateState(id,p,examHistoryState);
        return Response.ok().build();
    }

    @GET
    @Path("/exam")
    @RolesAllowed({"user","admin","manager"})
    public Response getExamHistory(@QueryParam("month") Integer month, @QueryParam("year") Integer year ){
        LOGGER.info("Get Exam History | User {} | Month {} | Year {}",ctx.getUserPrincipal().getName(),month,year);
        if(month==null){
            return Response.ok(ExamHistory.findHistoryByYear(year,ExamHistoryState.SENT)).build();
        }
        return Response.ok(ExamsHistorySummary.findByPeriod(LocalDate.of(year,month,1))).build();
    }

    @GET
    @Path("/laboratory/summary")
    @RolesAllowed({"user","admin","manager"})
    public Response getLaboratorySummary(@QueryParam("month") Integer month, @QueryParam("year") Integer year ){
        var examHistory = ExamHistory.findByPeriod(LocalDate.of(year,month,1));
        return Response.ok(examHistory
                .stream()
                .filter(e -> ExamHistoryState.SENT.equals(e.state))
                .map(e -> e.laboratory)
                .distinct()
                .map(laboratory ->  {
                    var l = new LaboratorySummary();
                    l.setLaboratory(laboratory);
                    l.setAmbulatoryValue(examHistory.stream().filter(e -> e.laboratory.id.equals(laboratory.id)).reduce(0,(acc,curr) -> acc+curr.ambulatoryValue,Integer::sum));
                    l.setHospitalizationValue(examHistory.stream().filter(e -> e.laboratory.id.equals(laboratory.id)).reduce(0,(acc,curr) -> acc+curr.hospitalizationValue,Integer::sum));
                    l.setEmergencyValue(examHistory.stream().filter(e -> e.laboratory.id.equals(laboratory.id)).reduce(0,(acc,curr) -> acc+curr.emergencyValue,Integer::sum));
                    return l;
                })
                .collect(Collectors.toList()))
                .build();
    }

    @GET
    @Path("/exam/laboratory/{id}")
    @RolesAllowed({"user","admin","manager"})
    public Response getExamHistory(@PathParam("id") Long id,@QueryParam("month") Integer month, @QueryParam("year") Integer year ){
        LOGGER.info("Get Laboratory Exam History | User {} | Laboratory {} | Month {} | Year {}",ctx.getUserPrincipal().getName(),id,month,year);
        if(month==null){
            return Response.ok(ExamHistory.findLaboratoryHistoryByYear(id,year,ExamHistoryState.SENT)).build();
        }
        return Response.ok(ExamHistory.findLaboratoryHistoryByPeriod(id,LocalDate.of(year,month,1))).build();
    }


    @POST
    @PUT
    @Path("/exam")
    @RolesAllowed({"admin","manager"})
    public Response saveExamHistory(ExamHistory examHistory){
        User currentUser = User.findById(Long.valueOf((ctx.getUserPrincipal().getName())));
        return Response.ok(historyService.saveExamHistory(examHistory,currentUser)).build();
    }
    @GET
    @Path("/patient")
    @RolesAllowed({"user","admin","manager"})
    public Response getPatientHistory(@QueryParam("month") Integer month, @QueryParam("year") Integer year ){
        LocalDate period = LocalDate.of(year,month == null || month == 0 ? 1 : month,1);
        List<PatientsHistory> result = month == null || month == 0 ? PatientsHistory.findByYear(year,ExamHistoryState.SENT) : PatientsHistory.findByPeriod(period,ExamHistoryState.SENT);
        JsonObject json = Json
                .createObjectBuilder()
                .add("period",period.format(DateTimeFormatter.BASIC_ISO_DATE))
                .add("patientsValue",result.stream().reduce(
                        0,
                        (acc, curr) -> acc+curr.patientsValue,
                        Integer::sum))
                .build();
        return Response.ok(json).build();
    }

    @GET
    @Path("/patient/laboratory/{id}")
    @RolesAllowed({"user","admin","manager"})
    public Response getPatientHistory(@PathParam("id") Long id,@QueryParam("month") Integer month, @QueryParam("year") Integer year ){
        if(month==null || month==0){
            var result = PatientsHistory.findLaboratoryHistoryByPeriod(id,year,ExamHistoryState.SENT);
            JsonObject json = Json
                    .createObjectBuilder()
                    .add("period",LocalDate.of(year,1,1).format(DateTimeFormatter.BASIC_ISO_DATE))
                    .add("patientsValue",result.stream().reduce(
                            0,
                            (acc, curr) -> acc+curr.patientsValue,
                            Integer::sum))
                    .build();
            return Response.ok(json).build();
        }
        return Response.ok(PatientsHistory.findLaboratoryHistoryByPeriod(id,LocalDate.of(year,month,1))).build();
    }

    @POST
    @PUT
    @Path("/patient")
    @RolesAllowed({"admin","manager"})
    public Response savePatientHistory(PatientsHistory patientsHistory){
        User currentUser = User.findById(Long.valueOf((ctx.getUserPrincipal().getName())));
        historyService.savePatientHistory(patientsHistory,currentUser);
        return Response.ok().build();
    }
}


