package com.zoho.security.policy;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import java.util.logging.Level;
import javax.servlet.FilterConfig;
import java.util.logging.Logger;
import javax.servlet.Filter;

public class SecurityPolicyFilter implements Filter
{
    private static final Logger LOGGER;
    
    public void init(final FilterConfig filterConfig) throws ServletException {
        final String cacheImplClassString = filterConfig.getInitParameter("cache-impl");
        if (cacheImplClassString == null) {
            return;
        }
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
            SecurityPolicyValidator.cacheImpl = (SecurityPolicyCache)cl.loadClass(cacheImplClassString).newInstance();
        }
        catch (final InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            SecurityPolicyFilter.LOGGER.log(Level.SEVERE, "Exception occurred while creating instance of SecurityPolicyCache impl. Name : {0}, Exception : {1}", new Object[] { cacheImplClassString, e.getMessage() });
        }
    }
    
    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain fc) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest)req;
        final HttpServletResponse response = (HttpServletResponse)res;
        try {
            SecurityPolicyValidator.validatePolicy(request);
        }
        catch (final SecurityPolicyException exception) {
            request.setAttribute(SecurityPolicyException.class.getName(), (Object)exception);
            response.sendError(400, exception.getMessage());
            return;
        }
        fc.doFilter((ServletRequest)request, (ServletResponse)response);
    }
    
    public void destroy() {
    }
    
    static {
        LOGGER = Logger.getLogger(SecurityPolicyFilter.class.getName());
    }
}
