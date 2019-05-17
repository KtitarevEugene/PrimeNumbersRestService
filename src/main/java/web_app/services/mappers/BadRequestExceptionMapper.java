package web_app.services.mappers;

import org.jetbrains.annotations.NotNull;
import web_app.services.exceptions.BadRequestException;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {

    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response toResponse(@NotNull BadRequestException e) {

        return Response.status(Response.Status.BAD_REQUEST).entity(e.getBaseResponseModel()).build();
    }
}
