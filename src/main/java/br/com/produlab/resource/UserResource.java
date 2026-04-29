package br.com.produlab.resource;

import br.com.produlab.dto.Error;
import br.com.produlab.dto.UpdateCredentialsRequest;
import br.com.produlab.entity.User;
import br.com.produlab.exception.InvalidPasswordException;
import br.com.produlab.service.UserService;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.Optional;

@Path("/v1/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"admin","manager"})
public class UserResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserResource.class);

    @Inject
    UserService userService;
    @Inject
    JsonWebToken jwt;
    @Context
    SecurityContext ctx;

    @GET
    @Path("/{id}")
    public Response getByID(@PathParam("id") Long id){
        LOGGER.info("Get User | User {} | ID {}",ctx.getUserPrincipal().getName(),id);

        Optional<User> optional = User.findByIdOptional(id);
        User user = optional.orElseThrow(() -> new NotFoundException());
        // Removed debug log that could expose sensitive information
        return Response.ok(user).build();
    }

    @GET
    public Response getAll(){
        LOGGER.info("Get All Users | User {}",ctx.getUserPrincipal().getName());
        return Response.ok(User.listAll()).build();
    }

    @PUT
    @POST
    @RolesAllowed({"admin"})

    public Response addUser(User user){
        LOGGER.info("Add User | User {} | NewUser {}",ctx.getUserPrincipal().getName(),user);
        User currentUser = User.findById(Long.valueOf((ctx.getUserPrincipal().getName())));

        userService.addUser(user,currentUser);
        return Response.ok().build();
    }


    @DELETE
    @Path("/{id}")
    @RolesAllowed({"admin"})
    public Response delete(@PathParam("id") Long id){
        LOGGER.info("Delete User | User {} | ID {}",ctx.getUserPrincipal().getName(),id);
        User currentUser = User.findById(Long.valueOf((ctx.getUserPrincipal().getName())));
        userService.deleteUser(currentUser,id);
        return Response.ok().build();
    }

    @GET
    @Path("/reset/{id}")
    public Response reset(@PathParam("id") Long id){
//        LOGGER.info("Delete User | User {} | ID {}",ctx.getUserPrincipal().getName(),id);
        User currentUser = User.findById(Long.valueOf((ctx.getUserPrincipal().getName())));
        userService.sentEmail(currentUser,"Teste","Testessssssssssss");
        return Response.ok().build();
    }

}
