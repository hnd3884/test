package org.apache.catalina.filters;

import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import java.nio.charset.Charset;
import javax.servlet.FilterConfig;
import org.apache.juli.logging.LogFactory;
import org.apache.juli.logging.Log;

public class AddDefaultCharsetFilter extends FilterBase
{
    private final Log log;
    private static final String DEFAULT_ENCODING = "ISO-8859-1";
    private String encoding;
    
    public AddDefaultCharsetFilter() {
        this.log = LogFactory.getLog((Class)AddDefaultCharsetFilter.class);
    }
    
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }
    
    @Override
    protected Log getLogger() {
        return this.log;
    }
    
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        if (this.encoding == null || this.encoding.length() == 0 || this.encoding.equalsIgnoreCase("default")) {
            this.encoding = "ISO-8859-1";
        }
        else if (this.encoding.equalsIgnoreCase("system")) {
            this.encoding = Charset.defaultCharset().name();
        }
        else if (!Charset.isSupported(this.encoding)) {
            throw new IllegalArgumentException(AddDefaultCharsetFilter.sm.getString("addDefaultCharset.unsupportedCharset", new Object[] { this.encoding }));
        }
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        if (response instanceof HttpServletResponse) {
            final ResponseWrapper wrapped = new ResponseWrapper((HttpServletResponse)response, this.encoding);
            chain.doFilter(request, (ServletResponse)wrapped);
        }
        else {
            chain.doFilter(request, response);
        }
    }
    
    public static class ResponseWrapper extends HttpServletResponseWrapper
    {
        private String encoding;
        
        public ResponseWrapper(final HttpServletResponse response, final String encoding) {
            super(response);
            this.encoding = encoding;
        }
        
        public void setContentType(final String contentType) {
            if (contentType != null && contentType.startsWith("text/")) {
                if (!contentType.contains("charset=")) {
                    super.setContentType(contentType + ";charset=" + this.encoding);
                }
                else {
                    super.setContentType(contentType);
                    this.encoding = this.getCharacterEncoding();
                }
            }
            else {
                super.setContentType(contentType);
            }
        }
        
        public void setHeader(final String name, final String value) {
            if (name.trim().equalsIgnoreCase("content-type")) {
                this.setContentType(value);
            }
            else {
                super.setHeader(name, value);
            }
        }
        
        public void addHeader(final String name, final String value) {
            if (name.trim().equalsIgnoreCase("content-type")) {
                this.setContentType(value);
            }
            else {
                super.addHeader(name, value);
            }
        }
        
        public void setCharacterEncoding(final String charset) {
            super.setCharacterEncoding(charset);
            this.encoding = charset;
        }
    }
}
