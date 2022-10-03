package com.adventnet.filters;

import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import java.util.logging.Logger;
import javax.servlet.FilterConfig;
import javax.servlet.Filter;

public final class ParamFilter implements Filter
{
    FilterConfig filterConfig;
    private static Logger logger;
    
    public ParamFilter() {
        this.filterConfig = null;
    }
    
    public void init(final FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final String regex = this.filterConfig.getInitParameter("excludeParams");
        chain.doFilter((ServletRequest)new ParamFilteredRequest(request, regex), response);
    }
    
    public void destroy() {
    }
    
    static {
        ParamFilter.logger = Logger.getLogger(ParamFilter.class.getName());
    }
    
    static class ParamFilteredRequest extends HttpServletRequestWrapper
    {
        private HttpServletRequest originalRequest;
        private String regex;
        
        public ParamFilteredRequest(final ServletRequest request, final String regex) {
            super((HttpServletRequest)request);
            this.originalRequest = (HttpServletRequest)request;
            this.regex = regex;
        }
        
        public Enumeration getParameterNames() {
            final List<String> requestParameterNames = (List<String>)Collections.list((Enumeration<Object>)super.getParameterNames());
            final List finalParameterNames = new ArrayList();
            for (final String parameterName : requestParameterNames) {
                if (!parameterName.matches(this.regex)) {
                    finalParameterNames.add(parameterName);
                    ParamFilter.logger.log(Level.FINER, "Param : " + parameterName);
                }
            }
            return Collections.enumeration((Collection<Object>)finalParameterNames);
        }
    }
}
