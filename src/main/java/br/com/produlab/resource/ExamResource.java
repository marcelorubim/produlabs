package br.com.produlab.resource;

import br.com.produlab.entity.Exam;
import br.com.produlab.entity.Laboratory;
import br.com.produlab.entity.User;
import br.com.produlab.service.ExamService;
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
import java.util.Optional;

@Path("/v1/exam")
@RolesAllowed({"user","admin","manager"})
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ExamResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExamResource.class);

    @Inject
    JsonWebToken jwt;

    @Context
    SecurityContext ctx;

    @Inject
    ExamService examService;

    @GET
    public Response getAll(){
        LOGGER.info("Get All Exams | User {}",ctx.getUserPrincipal().getName());
        return Response.ok(Exam.listAll()).build();
    }

    @GET
    @Path("/{id}")
    public Response getByID(@PathParam("id") Long id){
        LOGGER.info("Get Exame | User {} | ID {}",ctx.getUserPrincipal().getName(),id);

        Optional<Exam> optional = Exam.findByIdOptional(id);
        Exam exam = optional.orElseThrow(() -> new NotFoundException());
        return Response.ok(exam).build();
    }

    @PUT
    @POST
    public Response addExam(Exam exam){
        LOGGER.info("Add/Update Exam | User {} | Exam {}",ctx.getUserPrincipal().getName(),exam);
        return Response.ok(examService.addExam(exam)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteByID(@PathParam("id") Long id){
        LOGGER.info("Delete Exam | User {} | ID {}",ctx.getUserPrincipal().getName(),id);
        User currentUser = User.findById(Long.valueOf((ctx.getUserPrincipal().getName())));
        Optional<Exam> optional = Exam.findByIdOptional(id);
        Exam exam = optional.orElseThrow(() -> new NotFoundException());
        examService.deleteExam(exam,currentUser);
        return Response.ok().build();
    }
}
