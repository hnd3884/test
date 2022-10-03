package com.me.mdm.api.common.filter;

import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.container.ResourceInfo;
import javax.annotation.Priority;
import javax.ws.rs.ext.Provider;
import com.me.ems.framework.common.api.filters.RestAuthenticationFilter;

@Provider
@Priority(1000)
public class MDMRestAuthenticationFilter extends RestAuthenticationFilter
{
    @Context
    private ResourceInfo resourceInfo;
    @Context
    private HttpServletRequest request;
    private static Logger logger;
    
    public void filter(final ContainerRequestContext containerRequestContext) throws IOException {
        super.filter(containerRequestContext);
    }
    
    static {
        MDMRestAuthenticationFilter.logger = Logger.getLogger(RestAuthenticationFilter.class.getName());
    }
}
