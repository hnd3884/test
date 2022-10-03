package com.fasterxml.jackson.jaxrs.base;

import javax.ws.rs.core.Response;
import com.fasterxml.jackson.databind.JsonMappingException;
import javax.ws.rs.ext.ExceptionMapper;

public class JsonMappingExceptionMapper implements ExceptionMapper<JsonMappingException>
{
    public Response toResponse(final JsonMappingException exception) {
        return Response.status(Response.Status.BAD_REQUEST).entity((Object)exception.getMessage()).type("text/plain").build();
    }
}
