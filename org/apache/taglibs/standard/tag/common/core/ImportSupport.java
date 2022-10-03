package org.apache.taglibs.standard.tag.common.core;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.io.Writer;
import java.io.PrintWriter;
import javax.servlet.ServletOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.InputStream;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.io.InputStreamReader;
import java.net.URL;
import java.io.StringReader;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import javax.servlet.jsp.JspException;
import java.io.IOException;
import org.apache.taglibs.standard.util.UrlUtil;
import javax.servlet.jsp.JspTagException;
import org.apache.taglibs.standard.resources.Resources;
import java.io.Reader;
import javax.servlet.jsp.tagext.TryCatchFinally;
import javax.servlet.jsp.tagext.BodyTagSupport;

public abstract class ImportSupport extends BodyTagSupport implements TryCatchFinally, ParamParent
{
    public static final String DEFAULT_ENCODING = "ISO-8859-1";
    protected String url;
    protected String context;
    protected String charEncoding;
    private String var;
    private int scope;
    private String varReader;
    private Reader r;
    private boolean isAbsoluteUrl;
    private ParamSupport.ParamManager params;
    private String urlWithParams;
    
    public ImportSupport() {
        this.init();
    }
    
    private void init() {
        final String s = null;
        this.urlWithParams = s;
        this.charEncoding = s;
        this.context = s;
        this.varReader = s;
        this.var = s;
        this.url = s;
        this.params = null;
        this.scope = 1;
    }
    
    public int doStartTag() throws JspException {
        if (this.context != null && (!this.context.startsWith("/") || !this.url.startsWith("/"))) {
            throw new JspTagException(Resources.getMessage("IMPORT_BAD_RELATIVE"));
        }
        this.urlWithParams = null;
        this.params = new ParamSupport.ParamManager();
        if (this.url == null || this.url.equals("")) {
            throw new NullAttributeException("import", "url");
        }
        this.isAbsoluteUrl = UrlUtil.isAbsoluteUrl(this.url);
        try {
            if (this.varReader != null) {
                this.r = this.acquireReader();
                this.pageContext.setAttribute(this.varReader, (Object)this.r);
            }
        }
        catch (final IOException ex) {
            throw new JspTagException(ex.toString(), (Throwable)ex);
        }
        return 1;
    }
    
    public int doEndTag() throws JspException {
        try {
            if (this.varReader == null) {
                if (this.var != null) {
                    this.pageContext.setAttribute(this.var, (Object)this.acquireString(), this.scope);
                }
                else {
                    this.pageContext.getOut().print(this.acquireString());
                }
            }
            return 6;
        }
        catch (final IOException ex) {
            throw new JspTagException(ex.toString(), (Throwable)ex);
        }
    }
    
    public void doCatch(final Throwable t) throws Throwable {
        throw t;
    }
    
    public void doFinally() {
        try {
            if (this.varReader != null) {
                if (this.r != null) {
                    this.r.close();
                }
                this.pageContext.removeAttribute(this.varReader, 1);
            }
        }
        catch (final IOException ex) {}
    }
    
    public void release() {
        this.init();
        super.release();
    }
    
    public void setVar(final String var) {
        this.var = var;
    }
    
    public void setVarReader(final String varReader) {
        this.varReader = varReader;
    }
    
    public void setScope(final String scope) {
        this.scope = Util.getScope(scope);
    }
    
    public void addParameter(final String name, final String value) {
        this.params.addParameter(name, value);
    }
    
    private String acquireString() throws IOException, JspException {
        if (this.isAbsoluteUrl) {
            final BufferedReader r = new BufferedReader(this.acquireReader());
            final StringBuffer sb = new StringBuffer();
            int i;
            while ((i = r.read()) != -1) {
                sb.append((char)i);
            }
            return sb.toString();
        }
        if (!(this.pageContext.getRequest() instanceof HttpServletRequest) || !(this.pageContext.getResponse() instanceof HttpServletResponse)) {
            throw new JspTagException(Resources.getMessage("IMPORT_REL_WITHOUT_HTTP"));
        }
        ServletContext c = null;
        String targetUrl = this.targetUrl();
        if (this.context != null) {
            c = this.pageContext.getServletContext().getContext(this.context);
        }
        else {
            c = this.pageContext.getServletContext();
            if (!targetUrl.startsWith("/")) {
                final String sp = ((HttpServletRequest)this.pageContext.getRequest()).getServletPath();
                targetUrl = sp.substring(0, sp.lastIndexOf(47)) + '/' + targetUrl;
            }
        }
        if (c == null) {
            throw new JspTagException(Resources.getMessage("IMPORT_REL_WITHOUT_DISPATCHER", this.context, targetUrl));
        }
        final RequestDispatcher rd = c.getRequestDispatcher(stripSession(targetUrl));
        if (rd == null) {
            throw new JspTagException(stripSession(targetUrl));
        }
        final ImportResponseWrapper irw = new ImportResponseWrapper((HttpServletResponse)this.pageContext.getResponse());
        try {
            rd.include(this.pageContext.getRequest(), (ServletResponse)irw);
        }
        catch (final IOException ex) {
            throw new JspException((Throwable)ex);
        }
        catch (final RuntimeException ex2) {
            throw new JspException((Throwable)ex2);
        }
        catch (final ServletException ex3) {
            Throwable rc;
            for (rc = ex3.getRootCause(); rc instanceof ServletException; rc = ((ServletException)rc).getRootCause()) {}
            if (rc == null) {
                throw new JspException((Throwable)ex3);
            }
            throw new JspException(rc);
        }
        if (irw.getStatus() < 200 || irw.getStatus() > 299) {
            throw new JspTagException(irw.getStatus() + " " + stripSession(targetUrl));
        }
        return irw.getString();
    }
    
    private Reader acquireReader() throws IOException, JspException {
        if (!this.isAbsoluteUrl) {
            return new StringReader(this.acquireString());
        }
        final String target = this.targetUrl();
        try {
            final URL u = new URL(target);
            final URLConnection uc = u.openConnection();
            final InputStream i = uc.getInputStream();
            Reader r = null;
            String charSet;
            if (this.charEncoding != null && !this.charEncoding.equals("")) {
                charSet = this.charEncoding;
            }
            else {
                final String contentType = uc.getContentType();
                if (contentType != null) {
                    charSet = Util.getContentTypeAttribute(contentType, "charset");
                    if (charSet == null) {
                        charSet = "ISO-8859-1";
                    }
                }
                else {
                    charSet = "ISO-8859-1";
                }
            }
            try {
                r = new InputStreamReader(i, charSet);
            }
            catch (final Exception ex) {
                r = new InputStreamReader(i, "ISO-8859-1");
            }
            if (uc instanceof HttpURLConnection) {
                final int status = ((HttpURLConnection)uc).getResponseCode();
                if (status < 200 || status > 299) {
                    throw new JspTagException(status + " " + target);
                }
            }
            return r;
        }
        catch (final IOException ex2) {
            throw new JspException(Resources.getMessage("IMPORT_ABS_ERROR", target, ex2), (Throwable)ex2);
        }
        catch (final RuntimeException ex3) {
            throw new JspException(Resources.getMessage("IMPORT_ABS_ERROR", target, ex3), (Throwable)ex3);
        }
    }
    
    private String targetUrl() {
        if (this.urlWithParams == null) {
            this.urlWithParams = this.params.aggregateParams(this.url);
        }
        return this.urlWithParams;
    }
    
    public static String stripSession(final String url) {
        final StringBuffer u = new StringBuffer(url);
        int sessionStart;
        while ((sessionStart = u.toString().indexOf(";jsessionid=")) != -1) {
            int sessionEnd = u.toString().indexOf(";", sessionStart + 1);
            if (sessionEnd == -1) {
                sessionEnd = u.toString().indexOf("?", sessionStart + 1);
            }
            if (sessionEnd == -1) {
                sessionEnd = u.length();
            }
            u.delete(sessionStart, sessionEnd);
        }
        return u.toString();
    }
    
    private class ImportResponseWrapper extends HttpServletResponseWrapper
    {
        private StringWriter sw;
        private ByteArrayOutputStream bos;
        private ServletOutputStream sos;
        private boolean isWriterUsed;
        private boolean isStreamUsed;
        private int status;
        
        public ImportResponseWrapper(final HttpServletResponse response) {
            super(response);
            this.sw = new StringWriter();
            this.bos = new ByteArrayOutputStream();
            this.sos = new ServletOutputStream() {
                public void write(final int b) throws IOException {
                    ImportResponseWrapper.this.bos.write(b);
                }
            };
            this.status = 200;
        }
        
        public PrintWriter getWriter() {
            if (this.isStreamUsed) {
                throw new IllegalStateException(Resources.getMessage("IMPORT_ILLEGAL_STREAM"));
            }
            this.isWriterUsed = true;
            return new PrintWriter(this.sw);
        }
        
        public ServletOutputStream getOutputStream() {
            if (this.isWriterUsed) {
                throw new IllegalStateException(Resources.getMessage("IMPORT_ILLEGAL_WRITER"));
            }
            this.isStreamUsed = true;
            return this.sos;
        }
        
        public void setContentType(final String x) {
        }
        
        public void setLocale(final Locale x) {
        }
        
        public void setStatus(final int status) {
            this.status = status;
        }
        
        public int getStatus() {
            return this.status;
        }
        
        public String getString() throws UnsupportedEncodingException {
            if (this.isWriterUsed) {
                return this.sw.toString();
            }
            if (!this.isStreamUsed) {
                return "";
            }
            if (ImportSupport.this.charEncoding != null && !ImportSupport.this.charEncoding.equals("")) {
                return this.bos.toString(ImportSupport.this.charEncoding);
            }
            return this.bos.toString("ISO-8859-1");
        }
    }
}
