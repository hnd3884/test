package javax.servlet.http;

import javax.servlet.AsyncEvent;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import javax.servlet.WriteListener;
import java.io.UnsupportedEncodingException;
import java.io.PrintWriter;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import java.text.MessageFormat;
import javax.servlet.ServletOutputStream;
import java.util.Enumeration;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.servlet.AsyncListener;
import javax.servlet.DispatcherType;
import java.io.IOException;
import javax.servlet.ServletException;
import java.util.ResourceBundle;
import javax.servlet.GenericServlet;

public abstract class HttpServlet extends GenericServlet
{
    private static final long serialVersionUID = 1L;
    private static final String METHOD_DELETE = "DELETE";
    private static final String METHOD_HEAD = "HEAD";
    private static final String METHOD_GET = "GET";
    private static final String METHOD_OPTIONS = "OPTIONS";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_PUT = "PUT";
    private static final String METHOD_TRACE = "TRACE";
    private static final String HEADER_IFMODSINCE = "If-Modified-Since";
    private static final String HEADER_LASTMOD = "Last-Modified";
    private static final String LSTRING_FILE = "javax.servlet.http.LocalStrings";
    private static final ResourceBundle lStrings;
    
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final String msg = HttpServlet.lStrings.getString("http.method_get_not_supported");
        this.sendMethodNotAllowed(req, resp, msg);
    }
    
    protected long getLastModified(final HttpServletRequest req) {
        return -1L;
    }
    
    protected void doHead(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        if (DispatcherType.INCLUDE.equals(req.getDispatcherType())) {
            this.doGet(req, resp);
        }
        else {
            final NoBodyResponse response = new NoBodyResponse(resp);
            this.doGet(req, response);
            if (req.isAsyncStarted()) {
                req.getAsyncContext().addListener(new NoBodyAsyncContextListener(response));
            }
            else {
                response.setContentLength();
            }
        }
    }
    
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final String msg = HttpServlet.lStrings.getString("http.method_post_not_supported");
        this.sendMethodNotAllowed(req, resp, msg);
    }
    
    protected void doPut(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final String msg = HttpServlet.lStrings.getString("http.method_put_not_supported");
        this.sendMethodNotAllowed(req, resp, msg);
    }
    
    protected void doDelete(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final String msg = HttpServlet.lStrings.getString("http.method_delete_not_supported");
        this.sendMethodNotAllowed(req, resp, msg);
    }
    
    private void sendMethodNotAllowed(final HttpServletRequest req, final HttpServletResponse resp, final String msg) throws IOException {
        final String protocol = req.getProtocol();
        if (protocol.length() == 0 || protocol.endsWith("0.9") || protocol.endsWith("1.0")) {
            resp.sendError(400, msg);
        }
        else {
            resp.sendError(405, msg);
        }
    }
    
    private static Method[] getAllDeclaredMethods(final Class<?> c) {
        if (c.equals(HttpServlet.class)) {
            return null;
        }
        final Method[] parentMethods = getAllDeclaredMethods(c.getSuperclass());
        Method[] thisMethods = c.getDeclaredMethods();
        if (parentMethods != null && parentMethods.length > 0) {
            final Method[] allMethods = new Method[parentMethods.length + thisMethods.length];
            System.arraycopy(parentMethods, 0, allMethods, 0, parentMethods.length);
            System.arraycopy(thisMethods, 0, allMethods, parentMethods.length, thisMethods.length);
            thisMethods = allMethods;
        }
        return thisMethods;
    }
    
    protected void doOptions(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final Method[] methods = getAllDeclaredMethods(this.getClass());
        boolean ALLOW_GET = false;
        boolean ALLOW_HEAD = false;
        boolean ALLOW_POST = false;
        boolean ALLOW_PUT = false;
        boolean ALLOW_DELETE = false;
        boolean ALLOW_TRACE = true;
        final boolean ALLOW_OPTIONS = true;
        Class<?> clazz = null;
        try {
            clazz = Class.forName("org.apache.catalina.connector.RequestFacade");
            final Method getAllowTrace = clazz.getMethod("getAllowTrace", (Class<?>[])null);
            ALLOW_TRACE = (boolean)getAllowTrace.invoke(req, (Object[])null);
        }
        catch (final ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {}
        for (int i = 0; i < methods.length; ++i) {
            final Method m = methods[i];
            if (m.getName().equals("doGet")) {
                ALLOW_GET = true;
                ALLOW_HEAD = true;
            }
            if (m.getName().equals("doPost")) {
                ALLOW_POST = true;
            }
            if (m.getName().equals("doPut")) {
                ALLOW_PUT = true;
            }
            if (m.getName().equals("doDelete")) {
                ALLOW_DELETE = true;
            }
        }
        String allow = null;
        if (ALLOW_GET) {
            allow = "GET";
        }
        if (ALLOW_HEAD) {
            if (allow == null) {
                allow = "HEAD";
            }
            else {
                allow += ", HEAD";
            }
        }
        if (ALLOW_POST) {
            if (allow == null) {
                allow = "POST";
            }
            else {
                allow += ", POST";
            }
        }
        if (ALLOW_PUT) {
            if (allow == null) {
                allow = "PUT";
            }
            else {
                allow += ", PUT";
            }
        }
        if (ALLOW_DELETE) {
            if (allow == null) {
                allow = "DELETE";
            }
            else {
                allow += ", DELETE";
            }
        }
        if (ALLOW_TRACE) {
            if (allow == null) {
                allow = "TRACE";
            }
            else {
                allow += ", TRACE";
            }
        }
        if (ALLOW_OPTIONS) {
            if (allow == null) {
                allow = "OPTIONS";
            }
            else {
                allow += ", OPTIONS";
            }
        }
        resp.setHeader("Allow", allow);
    }
    
    protected void doTrace(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final String CRLF = "\r\n";
        final StringBuilder buffer = new StringBuilder("TRACE ").append(req.getRequestURI()).append(" ").append(req.getProtocol());
        final Enumeration<String> reqHeaderEnum = req.getHeaderNames();
        while (reqHeaderEnum.hasMoreElements()) {
            final String headerName = reqHeaderEnum.nextElement();
            buffer.append(CRLF).append(headerName).append(": ").append(req.getHeader(headerName));
        }
        buffer.append(CRLF);
        final int responseLength = buffer.length();
        resp.setContentType("message/http");
        resp.setContentLength(responseLength);
        final ServletOutputStream out = resp.getOutputStream();
        out.print(buffer.toString());
        out.close();
    }
    
    protected void service(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final String method = req.getMethod();
        if (method.equals("GET")) {
            final long lastModified = this.getLastModified(req);
            if (lastModified == -1L) {
                this.doGet(req, resp);
            }
            else {
                long ifModifiedSince;
                try {
                    ifModifiedSince = req.getDateHeader("If-Modified-Since");
                }
                catch (final IllegalArgumentException iae) {
                    ifModifiedSince = -1L;
                }
                if (ifModifiedSince < lastModified / 1000L * 1000L) {
                    this.maybeSetLastModified(resp, lastModified);
                    this.doGet(req, resp);
                }
                else {
                    resp.setStatus(304);
                }
            }
        }
        else if (method.equals("HEAD")) {
            final long lastModified = this.getLastModified(req);
            this.maybeSetLastModified(resp, lastModified);
            this.doHead(req, resp);
        }
        else if (method.equals("POST")) {
            this.doPost(req, resp);
        }
        else if (method.equals("PUT")) {
            this.doPut(req, resp);
        }
        else if (method.equals("DELETE")) {
            this.doDelete(req, resp);
        }
        else if (method.equals("OPTIONS")) {
            this.doOptions(req, resp);
        }
        else if (method.equals("TRACE")) {
            this.doTrace(req, resp);
        }
        else {
            String errMsg = HttpServlet.lStrings.getString("http.method_not_implemented");
            final Object[] errArgs = { method };
            errMsg = MessageFormat.format(errMsg, errArgs);
            resp.sendError(501, errMsg);
        }
    }
    
    private void maybeSetLastModified(final HttpServletResponse resp, final long lastModified) {
        if (resp.containsHeader("Last-Modified")) {
            return;
        }
        if (lastModified >= 0L) {
            resp.setDateHeader("Last-Modified", lastModified);
        }
    }
    
    @Override
    public void service(final ServletRequest req, final ServletResponse res) throws ServletException, IOException {
        HttpServletRequest request;
        HttpServletResponse response;
        try {
            request = (HttpServletRequest)req;
            response = (HttpServletResponse)res;
        }
        catch (final ClassCastException e) {
            throw new ServletException(HttpServlet.lStrings.getString("http.non_http"));
        }
        this.service(request, response);
    }
    
    static {
        lStrings = ResourceBundle.getBundle("javax.servlet.http.LocalStrings");
    }
    
    private static class NoBodyResponse extends HttpServletResponseWrapper
    {
        private final NoBodyOutputStream noBodyOutputStream;
        private ServletOutputStream originalOutputStream;
        private NoBodyPrintWriter noBodyWriter;
        private boolean didSetContentLength;
        
        private NoBodyResponse(final HttpServletResponse r) {
            super(r);
            this.noBodyOutputStream = new NoBodyOutputStream(this);
        }
        
        private void setContentLength() {
            if (!this.didSetContentLength) {
                if (this.noBodyWriter != null) {
                    this.noBodyWriter.flush();
                }
                super.setContentLengthLong(this.noBodyOutputStream.getWrittenByteCount());
            }
        }
        
        @Override
        public void setContentLength(final int len) {
            super.setContentLength(len);
            this.didSetContentLength = true;
        }
        
        @Override
        public void setContentLengthLong(final long len) {
            super.setContentLengthLong(len);
            this.didSetContentLength = true;
        }
        
        @Override
        public void setHeader(final String name, final String value) {
            super.setHeader(name, value);
            this.checkHeader(name);
        }
        
        @Override
        public void addHeader(final String name, final String value) {
            super.addHeader(name, value);
            this.checkHeader(name);
        }
        
        @Override
        public void setIntHeader(final String name, final int value) {
            super.setIntHeader(name, value);
            this.checkHeader(name);
        }
        
        @Override
        public void addIntHeader(final String name, final int value) {
            super.addIntHeader(name, value);
            this.checkHeader(name);
        }
        
        private void checkHeader(final String name) {
            if ("content-length".equalsIgnoreCase(name)) {
                this.didSetContentLength = true;
            }
        }
        
        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            this.originalOutputStream = this.getResponse().getOutputStream();
            return this.noBodyOutputStream;
        }
        
        @Override
        public PrintWriter getWriter() throws UnsupportedEncodingException {
            if (this.noBodyWriter == null) {
                this.noBodyWriter = new NoBodyPrintWriter(this.noBodyOutputStream, this.getCharacterEncoding());
            }
            return this.noBodyWriter;
        }
        
        @Override
        public void reset() {
            super.reset();
            this.resetBuffer();
            this.originalOutputStream = null;
        }
        
        @Override
        public void resetBuffer() {
            this.noBodyOutputStream.resetBuffer();
            if (this.noBodyWriter != null) {
                this.noBodyWriter.resetBuffer();
            }
        }
    }
    
    private static class NoBodyOutputStream extends ServletOutputStream
    {
        private static final String LSTRING_FILE = "javax.servlet.http.LocalStrings";
        private static final ResourceBundle lStrings;
        private final NoBodyResponse response;
        private boolean flushed;
        private long writtenByteCount;
        
        private NoBodyOutputStream(final NoBodyResponse response) {
            this.flushed = false;
            this.writtenByteCount = 0L;
            this.response = response;
        }
        
        private long getWrittenByteCount() {
            return this.writtenByteCount;
        }
        
        @Override
        public void write(final int b) throws IOException {
            ++this.writtenByteCount;
            this.checkCommit();
        }
        
        @Override
        public void write(final byte[] buf, final int offset, final int len) throws IOException {
            if (buf == null) {
                throw new NullPointerException(NoBodyOutputStream.lStrings.getString("err.io.nullArray"));
            }
            if (offset < 0 || len < 0 || offset + len > buf.length) {
                String msg = NoBodyOutputStream.lStrings.getString("err.io.indexOutOfBounds");
                final Object[] msgArgs = { offset, len, buf.length };
                msg = MessageFormat.format(msg, msgArgs);
                throw new IndexOutOfBoundsException(msg);
            }
            this.writtenByteCount += len;
            this.checkCommit();
        }
        
        @Override
        public boolean isReady() {
            return true;
        }
        
        @Override
        public void setWriteListener(final WriteListener listener) {
            this.response.originalOutputStream.setWriteListener(listener);
        }
        
        private void checkCommit() throws IOException {
            if (!this.flushed && this.writtenByteCount > this.response.getBufferSize()) {
                this.response.flushBuffer();
                this.flushed = true;
            }
        }
        
        private void resetBuffer() {
            if (this.flushed) {
                throw new IllegalStateException(NoBodyOutputStream.lStrings.getString("err.state.commit"));
            }
            this.writtenByteCount = 0L;
        }
        
        static {
            lStrings = ResourceBundle.getBundle("javax.servlet.http.LocalStrings");
        }
    }
    
    private static class NoBodyPrintWriter extends PrintWriter
    {
        private final NoBodyOutputStream out;
        private final String encoding;
        private PrintWriter pw;
        
        public NoBodyPrintWriter(final NoBodyOutputStream out, final String encoding) throws UnsupportedEncodingException {
            super(out);
            this.out = out;
            this.encoding = encoding;
            final Writer osw = new OutputStreamWriter(out, encoding);
            this.pw = new PrintWriter(osw);
        }
        
        private void resetBuffer() {
            this.out.resetBuffer();
            Writer osw = null;
            try {
                osw = new OutputStreamWriter(this.out, this.encoding);
            }
            catch (final UnsupportedEncodingException ex) {}
            this.pw = new PrintWriter(osw);
        }
        
        @Override
        public void flush() {
            this.pw.flush();
        }
        
        @Override
        public void close() {
            this.pw.close();
        }
        
        @Override
        public boolean checkError() {
            return this.pw.checkError();
        }
        
        @Override
        public void write(final int c) {
            this.pw.write(c);
        }
        
        @Override
        public void write(final char[] buf, final int off, final int len) {
            this.pw.write(buf, off, len);
        }
        
        @Override
        public void write(final char[] buf) {
            this.pw.write(buf);
        }
        
        @Override
        public void write(final String s, final int off, final int len) {
            this.pw.write(s, off, len);
        }
        
        @Override
        public void write(final String s) {
            this.pw.write(s);
        }
        
        @Override
        public void print(final boolean b) {
            this.pw.print(b);
        }
        
        @Override
        public void print(final char c) {
            this.pw.print(c);
        }
        
        @Override
        public void print(final int i) {
            this.pw.print(i);
        }
        
        @Override
        public void print(final long l) {
            this.pw.print(l);
        }
        
        @Override
        public void print(final float f) {
            this.pw.print(f);
        }
        
        @Override
        public void print(final double d) {
            this.pw.print(d);
        }
        
        @Override
        public void print(final char[] s) {
            this.pw.print(s);
        }
        
        @Override
        public void print(final String s) {
            this.pw.print(s);
        }
        
        @Override
        public void print(final Object obj) {
            this.pw.print(obj);
        }
        
        @Override
        public void println() {
            this.pw.println();
        }
        
        @Override
        public void println(final boolean x) {
            this.pw.println(x);
        }
        
        @Override
        public void println(final char x) {
            this.pw.println(x);
        }
        
        @Override
        public void println(final int x) {
            this.pw.println(x);
        }
        
        @Override
        public void println(final long x) {
            this.pw.println(x);
        }
        
        @Override
        public void println(final float x) {
            this.pw.println(x);
        }
        
        @Override
        public void println(final double x) {
            this.pw.println(x);
        }
        
        @Override
        public void println(final char[] x) {
            this.pw.println(x);
        }
        
        @Override
        public void println(final String x) {
            this.pw.println(x);
        }
        
        @Override
        public void println(final Object x) {
            this.pw.println(x);
        }
    }
    
    private static class NoBodyAsyncContextListener implements AsyncListener
    {
        private final NoBodyResponse noBodyResponse;
        
        public NoBodyAsyncContextListener(final NoBodyResponse noBodyResponse) {
            this.noBodyResponse = noBodyResponse;
        }
        
        @Override
        public void onComplete(final AsyncEvent event) throws IOException {
            this.noBodyResponse.setContentLength();
        }
        
        @Override
        public void onTimeout(final AsyncEvent event) throws IOException {
        }
        
        @Override
        public void onError(final AsyncEvent event) throws IOException {
        }
        
        @Override
        public void onStartAsync(final AsyncEvent event) throws IOException {
        }
    }
}
