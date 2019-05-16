package web_app.services.mappers;

import org.jetbrains.annotations.NotNull;
import web_app.services.exceptions.InternalServerException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InternalServerExceptionMapper implements ExceptionMapper<InternalServerException> {

    @Override
    public Response toResponse(@NotNull InternalServerException e) {
        return Response.serverError().entity(e.getErrorInfo()).build();
    }
}
