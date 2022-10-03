package com.me.devicemanagement.onpremise.webclient.filter;

import java.io.IOException;
import com.me.devicemanagement.framework.server.util.SecurityUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import java.util.logging.Level;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;
import java.security.SecureRandom;
import com.me.devicemanagement.onpremise.start.StartupUtil;
import java.io.File;
import javax.servlet.FilterConfig;
import java.util.Properties;
import java.util.logging.Logger;
import javax.servlet.Filter;

public class InternalFilter implements Filter
{
    private static Logger logger;
    private Properties authorization;
    
    public InternalFilter() {
        this.authorization = null;
    }
    
    public void init(final FilterConfig filterConfig) throws ServletException {
        final String auKeyPath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "Tomcat" + File.separator + ".internalKey";
        try {
            if (this.authorization == null) {
                this.authorization = StartupUtil.getProperties(auKeyPath);
            }
            if (this.authorization.isEmpty()) {
                final String auKey = RandomStringUtils.random(16, 47, 123, true, true, (char[])null, (Random)new SecureRandom());
                this.authorization.setProperty("auKey", auKey);
                StartupUtil.storeProperties(this.authorization, auKeyPath);
            }
        }
        catch (final Exception e) {
            InternalFilter.logger.log(Level.WARNING, "Exception while initialing the internal filter key.", e);
        }
    }
    
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest)servletRequest;
        final HttpServletResponse response = (HttpServletResponse)servletResponse;
        final String authKey = request.getHeader("authToken");
        if (!this.isAllowed(authKey)) {
            InternalFilter.logger.log(Level.WARNING, "Rejecting request due to Invalid Authorization");
            response.sendError(401, "UnAuthorized");
            return;
        }
        InternalFilter.logger.log(Level.INFO, "Authenticated Servlet URL : " + SecurityUtil.getNormalizedRequestURI(request));
        filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
    }
    
    public void destroy() {
    }
    
    private Boolean isAllowed(final String auth) {
        if (auth != null && !auth.isEmpty()) {
            return this.authorization.getProperty("auKey").equals(auth);
        }
        return Boolean.FALSE;
    }
    
    static {
        InternalFilter.logger = Logger.getLogger(InternalFilter.class.getName());
    }
}
