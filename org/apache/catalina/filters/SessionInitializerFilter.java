package org.apache.catalina.filters;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.Filter;

public class SessionInitializerFilter implements Filter
{
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        ((HttpServletRequest)request).getSession();
        chain.doFilter(request, response);
    }
    
    public void init(final FilterConfig filterConfig) throws ServletException {
    }
    
    public void destroy() {
    }
}
