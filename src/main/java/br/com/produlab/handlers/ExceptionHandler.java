package br.com.produlab.handlers;

import br.com.produlab.resource.AuthenticationResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionHandler implements ExceptionMapper<Exception> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionMapper.class);

    @Override
    public Response toResponse(Exception e) {
        LOGGER.error("Unexpected exception.",e);
        return Response.serverError().entity(e).build();
    }
}
