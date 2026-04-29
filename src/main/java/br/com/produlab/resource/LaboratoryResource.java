package br.com.produlab.resource;

import br.com.produlab.entity.*;
import br.com.produlab.service.LaboratoryService;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Path("/v1/laboratory")
@RolesAllowed({"user","admin","manager"})
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LaboratoryResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(LaboratoryResource.class);

    @Inject
    LaboratoryService laboratoryService;
    @Inject
    JsonWebToken jwt;

    @Context
    SecurityContext ctx;

    @GET
    public Response getAll(){
        LOGGER.info("Get All Laboratories | User {}",ctx.getUserPrincipal().getName());

        return Response.ok(Laboratory.listAll()).build();
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") Long id){
        LOGGER.info("Get Laboratory | User {} | ID {}",ctx.getUserPrincipal().getName(),id);

        // Removed debug log that could expose sensitive information
        Optional<Laboratory> optional = Laboratory.findByIdOptional(id);
        Laboratory laboratory = optional.orElseThrow(() -> new NotFoundException());
        return Response.ok(laboratory).build();
    }

    @PUT
    @POST
    @RolesAllowed({"admin","manager"})
    public Response addLaboratory(Laboratory laboratory){
        LOGGER.info("Add Laboratory | User {} | Laboratory {}",ctx.getUserPrincipal().getName(),laboratory);
        User currentUser = User.findById(Long.valueOf((ctx.getUserPrincipal().getName())));
        laboratoryService.addLaboratory(laboratory,currentUser);
        return Response.ok().build();
    }

    @POST
    @RolesAllowed({"admin","manager"})
    @Path("/{id}/exam/{examID}/{period}")
    public Response addExamData(@PathParam("id") Long id, @PathParam("examID") Long examID, @PathParam("period") String period, ExamHistory examHistory){
        User currentUser = User.findById(Long.valueOf((ctx.getUserPrincipal().getName())));
        LOGGER.info("Add Exam History | User {} | Laboratory {} | Exam {} | Hist. {}",currentUser.id,id,examID,examHistory);
        Laboratory laboratory = (Laboratory) Laboratory.findByIdOptional(id).orElseThrow(() -> new NotFoundException());
        Exam exam = (Exam) Exam.findByIdOptional(examID).orElseThrow(() -> new NotFoundException());
        LocalDate periodDate = LocalDate.parse(period,DateTimeFormatter.BASIC_ISO_DATE);
        laboratoryService.addExamHistory(laboratory,exam,periodDate,currentUser,examHistory);
        return Response.ok().build();
    }
}
