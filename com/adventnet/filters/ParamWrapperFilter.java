package com.adventnet.filters;

import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.logging.LogFactory;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import javax.servlet.FilterConfig;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import javax.servlet.Filter;

public class ParamWrapperFilter implements Filter
{
    private static final Log LOG;
    private static final String DEFAULT_BLACKLIST_PATTERN = "(.*\\.|^|.*|\\[('|\"))(c|C)lass(\\.|('|\")]|\\[).*";
    private static final String INIT_PARAM_NAME = "excludeParams";
    private Pattern pattern;
    
    public void init(final FilterConfig filterConfig) throws ServletException {
        final String initParameter = filterConfig.getInitParameter("excludeParams");
        String toCompile;
        if (initParameter != null && initParameter.trim().length() > 0) {
            toCompile = initParameter;
        }
        else {
            toCompile = "(.*\\.|^|.*|\\[('|\"))(c|C)lass(\\.|('|\")]|\\[).*";
        }
        this.pattern = Pattern.compile(toCompile, 32);
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        ParamFilteredRequest wrapper = null;
        if (request instanceof HttpServletRequest) {
            wrapper = new ParamFilteredRequest(request, this.pattern);
        }
        if (wrapper != null) {
            chain.doFilter((ServletRequest)wrapper, response);
        }
        else {
            chain.doFilter(request, response);
        }
    }
    
    public void destroy() {
    }
    
    static {
        LOG = LogFactory.getLog((Class)ParamWrapperFilter.class);
    }
    
    static class ParamFilteredRequest extends HttpServletRequestWrapper
    {
        private final Pattern pattern;
        private boolean read_stream;
        
        public ParamFilteredRequest(final ServletRequest request, final Pattern pattern) {
            super((HttpServletRequest)request);
            this.read_stream = false;
            this.pattern = pattern;
        }
        
        public Enumeration getParameterNames() {
            final List finalParameterNames = new ArrayList();
            final List parameterNames = Collections.list((Enumeration<Object>)super.getParameterNames());
            for (final String parameterName : parameterNames) {
                if (!this.pattern.matcher(parameterName).matches()) {
                    finalParameterNames.add(parameterName);
                }
            }
            return Collections.enumeration((Collection<Object>)finalParameterNames);
        }
        
        private void logCatchedException(final IOException ex) {
            ParamWrapperFilter.LOG.error((Object)"[ParamFilteredRequest]: Exception catched: ", (Throwable)ex);
        }
    }
}
