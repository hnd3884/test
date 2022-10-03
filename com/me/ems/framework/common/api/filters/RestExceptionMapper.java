package com.me.ems.framework.common.api.filters;

import java.net.URI;
import com.me.ems.framework.common.api.utils.APIException;
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
public class RestExceptionMapper implements ExceptionMapper<Throwable>
{
    @Context
    private javax.inject.Provider<ContainerRequestContext> containerRequestContextProvider;
    Logger apiLogger;
    
    public RestExceptionMapper() {
        this.apiLogger = Logger.getLogger("emsRestAPILogger");
    }
    
    public Response toResponse(final Throwable throwable) {
        final ContainerRequestContext containerRequestContext = (ContainerRequestContext)this.containerRequestContextProvider.get();
        int statusCode = -1;
        final URI urlInfo = containerRequestContext.getUriInfo().getAbsolutePath();
        final String requestMethod = containerRequestContext.getMethod();
        final String seperator = " | ";
        final Boolean isAPILogin = (Boolean)containerRequestContext.getProperty("isAPILogin");
        if (isAPILogin != null && isAPILogin) {
            ApiFactoryProvider.getAuthUtilAccessAPI().flushCredentials();
        }
        if (throwable instanceof WebApplicationException) {
            final WebApplicationException webAppException = (WebApplicationException)throwable;
            final Response errorResponse = webAppException.getResponse();
            statusCode = errorResponse.getStatus();
            this.apiLogger.log(Level.INFO, statusCode + seperator + requestMethod + seperator + urlInfo + seperator + errorResponse.getStatusInfo().getReasonPhrase());
            return APIResponse.errorResponse(errorResponse);
        }
        if (throwable instanceof APIException) {
            final Response errorResponse2 = APIResponse.errorResponse((APIException)throwable);
            statusCode = errorResponse2.getStatus();
            final String errorCode = ((APIException)throwable).getErrorCode();
            final String errorMessage = ((APIException)throwable).getErrorMsg();
            this.apiLogger.log(Level.INFO, statusCode + seperator + requestMethod + seperator + urlInfo + seperator + errorCode + errorMessage);
            return errorResponse2;
        }
        this.apiLogger.log(Level.INFO, Response.Status.INTERNAL_SERVER_ERROR + seperator + requestMethod + seperator + urlInfo + seperator + "GENERIC0002" + throwable.getMessage());
        return APIResponse.errorResponse("GENERIC0002", throwable.getMessage(), new String[0]);
    }
}
