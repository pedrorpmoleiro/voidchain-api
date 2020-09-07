package pt.ipleiria.estg.dei.pi.voidchain.api.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * The Catch all exception mapper will catch all exceptions and return them to the client.
 */
@Provider
public class CatchAllExceptionMapper implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception e) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
    }
}
