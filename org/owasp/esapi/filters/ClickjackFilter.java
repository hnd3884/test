package org.owasp.esapi.filters;

import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.FilterConfig;
import javax.servlet.Filter;

public class ClickjackFilter implements Filter
{
    private String mode;
    
    public ClickjackFilter() {
        this.mode = "DENY";
    }
    
    public void init(final FilterConfig filterConfig) {
        final String configMode = filterConfig.getInitParameter("mode");
        if (configMode != null && (configMode.equals("DENY") || configMode.equals("SAMEORIGIN"))) {
            this.mode = configMode;
        }
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final HttpServletResponse res = (HttpServletResponse)response;
        res.addHeader("X-FRAME-OPTIONS", this.mode);
        chain.doFilter(request, response);
    }
    
    public void destroy() {
    }
}
