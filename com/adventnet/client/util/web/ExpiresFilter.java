package com.adventnet.client.util.web;

import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import java.util.logging.Level;
import javax.servlet.FilterConfig;
import java.util.logging.Logger;
import javax.servlet.Filter;

public class ExpiresFilter implements Filter, WebConstants
{
    private static Logger logger;
    private long expires;
    private long value;
    
    public ExpiresFilter() {
        this.expires = 86400000L;
        this.value = 8640000L;
    }
    
    public void init(final FilterConfig filterConfig) {
        ExpiresFilter.logger.log(Level.FINEST, "State Filter initialized");
        final String value = filterConfig.getInitParameter("expires");
        if (value != null) {
            this.expires = Long.parseLong(value) * 1000L;
        }
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        ExpiresFilter.logger.log(Level.FINEST, "doFilter called for {0}", ((HttpServletRequest)request).getRequestURI());
        ((HttpServletResponse)response).setDateHeader("Expires", System.currentTimeMillis() + this.expires);
        ((HttpServletResponse)response).setHeader("Cache-Control", "public");
        ((HttpServletResponse)response).addHeader("Cache-Control", "max-age=" + this.value);
        chain.doFilter(request, response);
    }
    
    public void destroy() {
    }
    
    static {
        ExpiresFilter.logger = Logger.getLogger(ExpiresFilter.class.getName());
    }
}
