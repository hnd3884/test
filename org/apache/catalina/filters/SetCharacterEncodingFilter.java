package org.apache.catalina.filters;

import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import org.apache.juli.logging.LogFactory;
import org.apache.juli.logging.Log;

public class SetCharacterEncodingFilter extends FilterBase
{
    private final Log log;
    private String encoding;
    private boolean ignore;
    
    public SetCharacterEncodingFilter() {
        this.log = LogFactory.getLog((Class)SetCharacterEncodingFilter.class);
        this.encoding = null;
        this.ignore = false;
    }
    
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }
    
    public String getEncoding() {
        return this.encoding;
    }
    
    public void setIgnore(final boolean ignore) {
        this.ignore = ignore;
    }
    
    public boolean isIgnore() {
        return this.ignore;
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        if (this.ignore || request.getCharacterEncoding() == null) {
            final String characterEncoding = this.selectEncoding(request);
            if (characterEncoding != null) {
                request.setCharacterEncoding(characterEncoding);
            }
        }
        chain.doFilter(request, response);
    }
    
    @Override
    protected Log getLogger() {
        return this.log;
    }
    
    protected String selectEncoding(final ServletRequest request) {
        return this.encoding;
    }
}
