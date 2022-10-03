package com.me.mdm.api.common.filter;

import java.io.IOException;
import java.net.URI;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerRequestContext;
import java.util.logging.Logger;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.container.ContainerResponseFilter;

@Provider
public class MDMRestResponseFilter implements ContainerResponseFilter
{
    Logger apiLogger;
    
    public MDMRestResponseFilter() {
        this.apiLogger = Logger.getLogger("MDMApiLogger");
    }
    
    public void filter(final ContainerRequestContext containerRequestContext, final ContainerResponseContext containerResponseContext) throws IOException {
        final int statusCode = containerResponseContext.getStatus();
        final URI urlInfo = containerRequestContext.getUriInfo().getAbsolutePath();
        final String requestMethod = containerRequestContext.getMethod();
        final String seperator = " | ";
        this.apiLogger.log(Level.INFO, "{0}{1}{2}{3}{4}{5}", new Object[] { statusCode, seperator, requestMethod, seperator, urlInfo, seperator });
        ApiFactoryProvider.getAuthUtilAccessAPI().flushCredentials();
    }
}
