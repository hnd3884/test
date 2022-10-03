package com.fasterxml.jackson.jaxrs.base;

import javax.ws.rs.core.Response;
import com.fasterxml.jackson.core.JsonParseException;
import javax.ws.rs.ext.ExceptionMapper;

public class JsonParseExceptionMapper implements ExceptionMapper<JsonParseException>
{
    public Response toResponse(final JsonParseException exception) {
        return Response.status(Response.Status.BAD_REQUEST).entity((Object)exception.getMessage()).type("text/plain").build();
    }
}
