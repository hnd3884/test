package com.me.ems.framework.common.api.filters;

import java.io.IOException;
import com.me.ems.summaryserver.summary.probedistribution.ProbeDistributionInitializer;
import java.net.URI;
import java.util.logging.Level;
import com.me.ems.summaryserver.factory.ProbeMgmtFactoryProvider;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.container.ContainerResponseFilter;

@Provider
public class RestResponseFilter implements ContainerResponseFilter
{
    Logger apiLogger;
    @Context
    private HttpServletRequest servletRequest;
    
    public RestResponseFilter() {
        this.apiLogger = Logger.getLogger("emsRestAPILogger");
    }
    
    public void filter(final ContainerRequestContext containerRequestContext, final ContainerResponseContext containerResponseContext) throws IOException {
        final Boolean isAPILogin = (Boolean)containerRequestContext.getProperty("isAPILogin");
        if (isAPILogin != null && isAPILogin) {
            ApiFactoryProvider.getAuthUtilAccessAPI().flushCredentials();
        }
        final int statusCode = containerResponseContext.getStatus();
        final URI urlInfo = containerRequestContext.getUriInfo().getAbsolutePath();
        final String requestMethod = containerRequestContext.getMethod();
        if (isAPILogin == null || !isAPILogin) {
            final ProbeDistributionInitializer probeDistributionInitializer = ProbeMgmtFactoryProvider.getProbeDistributionInitializer();
            boolean isProbeRequest = false;
            if (this.servletRequest.getAttribute("isProbeRequest") != null) {
                isProbeRequest = Boolean.parseBoolean(this.servletRequest.getAttribute("isProbeRequest").toString());
            }
            if (probeDistributionInitializer != null && isProbeRequest) {
                probeDistributionInitializer.addToProbeQueue(this.servletRequest, statusCode, containerRequestContext, 2);
            }
        }
        final String separator = " | ";
        this.apiLogger.log(Level.FINEST, statusCode + separator + requestMethod + separator + urlInfo + separator);
    }
}
