package org.apache.catalina.ssi;

import java.io.IOException;
import java.util.regex.Matcher;
import java.io.Reader;
import java.util.Date;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import java.util.regex.Pattern;
import javax.servlet.FilterConfig;
import javax.servlet.Filter;

public class SSIFilter implements Filter
{
    protected FilterConfig config;
    protected int debug;
    protected Long expires;
    protected boolean isVirtualWebappRelative;
    protected Pattern contentTypeRegEx;
    protected final Pattern shtmlRegEx;
    protected boolean allowExec;
    
    public SSIFilter() {
        this.config = null;
        this.debug = 0;
        this.expires = null;
        this.isVirtualWebappRelative = false;
        this.contentTypeRegEx = null;
        this.shtmlRegEx = Pattern.compile("text/x-server-parsed-html(;.*)?");
        this.allowExec = false;
    }
    
    public void init(final FilterConfig config) throws ServletException {
        this.config = config;
        if (config.getInitParameter("debug") != null) {
            this.debug = Integer.parseInt(config.getInitParameter("debug"));
        }
        if (config.getInitParameter("contentType") != null) {
            this.contentTypeRegEx = Pattern.compile(config.getInitParameter("contentType"));
        }
        else {
            this.contentTypeRegEx = this.shtmlRegEx;
        }
        this.isVirtualWebappRelative = Boolean.parseBoolean(config.getInitParameter("isVirtualWebappRelative"));
        if (config.getInitParameter("expires") != null) {
            this.expires = Long.valueOf(config.getInitParameter("expires"));
        }
        this.allowExec = Boolean.parseBoolean(config.getInitParameter("allowExec"));
        if (this.debug > 0) {
            config.getServletContext().log("SSIFilter.init() SSI invoker started with 'debug'=" + this.debug);
        }
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest req = (HttpServletRequest)request;
        final HttpServletResponse res = (HttpServletResponse)response;
        final ByteArrayServletOutputStream basos = new ByteArrayServletOutputStream();
        final ResponseIncludeWrapper responseIncludeWrapper = new ResponseIncludeWrapper(res, basos);
        chain.doFilter((ServletRequest)req, (ServletResponse)responseIncludeWrapper);
        responseIncludeWrapper.flushOutputStreamOrWriter();
        byte[] bytes = basos.toByteArray();
        final String contentType = responseIncludeWrapper.getContentType();
        if (contentType != null && this.contentTypeRegEx.matcher(contentType).matches()) {
            final String encoding = res.getCharacterEncoding();
            final SSIExternalResolver ssiExternalResolver = new SSIServletExternalResolver(this.config.getServletContext(), req, res, this.isVirtualWebappRelative, this.debug, encoding);
            final SSIProcessor ssiProcessor = new SSIProcessor(ssiExternalResolver, this.debug, this.allowExec);
            final Reader reader = new InputStreamReader(new ByteArrayInputStream(bytes), encoding);
            final ByteArrayOutputStream ssiout = new ByteArrayOutputStream();
            final PrintWriter writer = new PrintWriter(new OutputStreamWriter(ssiout, encoding));
            final long lastModified = ssiProcessor.process(reader, responseIncludeWrapper.getLastModified(), writer);
            writer.flush();
            bytes = ssiout.toByteArray();
            if (this.expires != null) {
                res.setDateHeader("expires", new Date().getTime() + this.expires * 1000L);
            }
            if (lastModified > 0L) {
                res.setDateHeader("last-modified", lastModified);
            }
            res.setContentLength(bytes.length);
            final Matcher shtmlMatcher = this.shtmlRegEx.matcher(responseIncludeWrapper.getContentType());
            if (shtmlMatcher.matches()) {
                final String enc = shtmlMatcher.group(1);
                res.setContentType("text/html" + ((enc != null) ? enc : ""));
            }
        }
        OutputStream out = null;
        try {
            out = (OutputStream)res.getOutputStream();
        }
        catch (final IllegalStateException ex) {}
        if (out == null) {
            res.getWriter().write(new String(bytes));
        }
        else {
            out.write(bytes);
        }
    }
    
    public void destroy() {
    }
}
