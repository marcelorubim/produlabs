package br.com.produlab.resource;

import br.com.produlab.dto.AuthenticationRequest;
import br.com.produlab.dto.Error;
import br.com.produlab.dto.UpdateCredentialsRequest;
import br.com.produlab.exception.InvalidPasswordException;
import br.com.produlab.service.AuthenticationService;
import br.com.produlab.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v1/authenticate")
@PermitAll
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthenticationResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationResource.class);

    @Inject
    AuthenticationService authenticationService;

    @Inject
    UserService userService;

    @POST
    public Response authenticate(AuthenticationRequest authenticationRequest) {
        LOGGER.info("Authenticate | {}", authenticationRequest.getEmail());
        return Response.ok(authenticationService.authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword())).build();
    }

    @POST
    @PermitAll
    @Path("/updateCredentials")
    public Response updateCredentials(UpdateCredentialsRequest updateCredentialsRequest) {
        LOGGER.info("Update Credentials | {}", updateCredentialsRequest.getEmail());
        try {
            userService.updateCredentials(updateCredentialsRequest.getEmail(), updateCredentialsRequest.getPassword(), updateCredentialsRequest.getNewPassword());
        } catch (InvalidPasswordException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new Error("Invalid password")).build();
        }
        return Response.ok().build();
    }

    @POST
    @PermitAll
    @Path("/resetCredendials")
    public Response resetCredendials(UpdateCredentialsRequest updateCredentialsRequest) {
        LOGGER.info("Reset Credentials | {}", updateCredentialsRequest.getEmail());
        userService.resetCredendials(updateCredentialsRequest.getEmail());
        return Response.ok().build();
    }


}
