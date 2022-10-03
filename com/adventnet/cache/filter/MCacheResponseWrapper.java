package com.adventnet.cache.filter;

import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.ArrayList;
import java.io.OutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import java.util.List;
import javax.servlet.ServletOutputStream;
import java.io.PrintWriter;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponseWrapper;

public class MCacheResponseWrapper extends HttpServletResponseWrapper
{
    private static final Logger LOGGER;
    private PrintWriter writer;
    private ServletOutputStream stream;
    private int status;
    private String type;
    private List<String[]> headers;
    private List<Cookie> cookies;
    
    public MCacheResponseWrapper(final HttpServletResponse response, final OutputStream stream) {
        super(response);
        this.writer = null;
        this.stream = null;
        this.status = 200;
        this.type = null;
        try {
            this.stream = new MCacheResponseStream(stream);
            this.headers = new ArrayList<String[]>();
            this.cookies = new ArrayList<Cookie>();
        }
        catch (final IOException exp) {
            MCacheResponseWrapper.LOGGER.log(Level.SEVERE, "Exception at initialization", exp);
        }
    }
    
    public void setContentType(final String type) {
        super.setContentType(this.type = type);
    }
    
    public String getContentType() {
        return this.type;
    }
    
    public void flushBuffer() throws IOException {
        this.flush();
        super.flushBuffer();
    }
    
    public ServletOutputStream getOutputStream() throws IOException {
        return this.stream;
    }
    
    public PrintWriter getWriter() throws IOException {
        if (this.writer == null) {
            this.writer = new PrintWriter(new OutputStreamWriter((OutputStream)this.stream, this.getCharacterEncoding()), true);
        }
        return this.writer;
    }
    
    public void setStatus(final int status) {
        super.setStatus(status);
        this.status = status;
    }
    
    public void sendError(final int status, final String string) throws IOException {
        super.sendError(status, string);
        this.status = status;
    }
    
    public void sendError(final int status) throws IOException {
        super.sendError(status);
        this.status = status;
    }
    
    public void setStatus(final int status, final String string) {
        super.setStatus(status, string);
        this.status = status;
    }
    
    public void sendRedirect(final String location) throws IOException {
        this.status = 302;
        super.sendRedirect(location);
    }
    
    public int getStatus() {
        return this.status;
    }
    
    public void addHeader(final String name, final String value) {
        final String[] header = { name, value };
        this.headers.add(header);
        super.addHeader(name, value);
    }
    
    public void setHeader(final String name, final String value) {
        this.addHeader(name, value);
    }
    
    public List<String[]> getHeaders() {
        return this.headers;
    }
    
    public void addCookie(final Cookie cookie) {
        this.cookies.add(cookie);
        super.addCookie(cookie);
    }
    
    public List<Cookie> getCookies() {
        return this.cookies;
    }
    
    public void reset() {
        super.reset();
        this.cookies.clear();
        this.headers.clear();
        this.type = null;
        this.status = 200;
    }
    
    public void flush() throws IOException {
        if (this.writer != null) {
            this.writer.flush();
        }
        this.stream.flush();
    }
    
    static {
        LOGGER = Logger.getLogger(MCacheResponseWrapper.class.getName());
    }
}
