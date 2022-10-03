package com.me.mdm.api.common.filter;

import java.net.URI;
import com.me.mdm.api.error.APIHTTPException;
import com.me.ems.framework.common.api.response.APIResponse;
import java.util.logging.Level;
import javax.ws.rs.WebApplicationException;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ExceptionMapper;

@Provider
public class MDMExceptionMapper implements ExceptionMapper<Throwable>
{
    @Context
    private ContainerRequestContext containerRequestContext;
    private static Logger logger;
    
    public Response toResponse(final Throwable throwable) {
        final int statusCode = -1;
        final URI urlInfo = this.containerRequestContext.getUriInfo().getAbsolutePath();
        final String requestMethod = this.containerRequestContext.getMethod();
        final String seperator = " | ";
        ApiFactoryProvider.getAuthUtilAccessAPI().flushCredentials();
        if (throwable instanceof WebApplicationException) {
            final WebApplicationException webAppException = (WebApplicationException)throwable;
            final Response errorResponse = webAppException.getResponse();
            MDMExceptionMapper.logger.log(Level.SEVERE, "{0}{1}{2}{3}{4}{5}{6}", new Object[] { statusCode, seperator, requestMethod, seperator, urlInfo, seperator, errorResponse.getStatusInfo().getReasonPhrase() });
            return APIResponse.errorResponse(errorResponse);
        }
        if (!(throwable instanceof APIHTTPException)) {
            MDMExceptionMapper.logger.log(Level.SEVERE, throwable, () -> n + s + s2 + s + uri);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity((Object)new APIHTTPException("COM0004", new Object[0]).toString()).build();
        }
        MDMExceptionMapper.logger.log(Level.SEVERE, throwable, () -> n2 + s3 + s4 + s3 + uri2);
        if (((APIHTTPException)throwable).getStatusCode() == 0) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity((Object)new APIHTTPException("COM0004", new Object[0]).toString()).build();
        }
        return Response.status(((APIHTTPException)throwable).getStatusCode()).entity((Object)throwable.toString()).build();
    }
    
    static {
        MDMExceptionMapper.logger = Logger.getLogger("MDMApiLogger");
    }
}
