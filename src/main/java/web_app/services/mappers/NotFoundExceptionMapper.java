package web_app.services.mappers;

import org.jetbrains.annotations.NotNull;
import web_app.services.exceptions.NotFoundException;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response toResponse(@NotNull NotFoundException e) {

        return Response.status(Response.Status.NOT_FOUND).entity(e.getErrorInfo()).build();
    }
}
