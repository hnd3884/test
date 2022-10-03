package org.apache.catalina.filters;

import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import org.apache.juli.logging.LogFactory;
import org.apache.juli.logging.Log;

public final class RemoteHostFilter extends RequestFilter
{
    private final Log log;
    
    public RemoteHostFilter() {
        this.log = LogFactory.getLog((Class)RemoteHostFilter.class);
    }
    
    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        this.process(request.getRemoteHost(), request, response, chain);
    }
    
    @Override
    protected Log getLogger() {
        return this.log;
    }
}
