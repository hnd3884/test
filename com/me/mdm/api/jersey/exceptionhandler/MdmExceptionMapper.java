package com.me.mdm.api.jersey.exceptionhandler;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ExceptionMapper;

@Provider
public class MdmExceptionMapper implements ExceptionMapper<Throwable>
{
    private static Logger logger;
    
    public Response toResponse(final Throwable throwable) {
        if (throwable instanceof APIHTTPException) {
            MdmExceptionMapper.logger.log(Level.SEVERE, "APIHTTPException in toResponse of MdmExceptionMapper", throwable);
            return Response.status(((APIHTTPException)throwable).getStatusCode()).entity((Object)throwable.toString()).build();
        }
        MdmExceptionMapper.logger.log(Level.SEVERE, "Generic Exception in toResponse of MdmExceptionMapper", throwable);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity((Object)throwable.toString()).build();
    }
    
    static {
        MdmExceptionMapper.logger = Logger.getLogger("MDMApiLogger");
    }
}
