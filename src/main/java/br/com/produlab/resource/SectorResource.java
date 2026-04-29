package br.com.produlab.resource;

import br.com.produlab.entity.Sector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("/v1/sector")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SectorResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(SectorResource.class);
    @Context
    SecurityContext ctx;
    @GET
    public Response getAll(){
        LOGGER.info("Get All Sectors | User {}",ctx.getUserPrincipal().getName());
        return Response.ok(Sector.listAll()).build();
    }
}
