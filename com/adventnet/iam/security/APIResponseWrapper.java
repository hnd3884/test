package com.adventnet.iam.security;

import java.util.Collection;
import javax.servlet.http.Cookie;
import java.io.PrintWriter;
import javax.servlet.ServletOutputStream;
import java.util.Locale;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

public class APIResponseWrapper implements HttpServletResponse
{
    public void flushBuffer() throws IOException {
    }
    
    public int getBufferSize() {
        return 0;
    }
    
    public String getCharacterEncoding() {
        return null;
    }
    
    public String getContentType() {
        return null;
    }
    
    public Locale getLocale() {
        return null;
    }
    
    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }
    
    public PrintWriter getWriter() throws IOException {
        return null;
    }
    
    public boolean isCommitted() {
        return false;
    }
    
    public void reset() {
    }
    
    public void resetBuffer() {
    }
    
    public void setBufferSize(final int arg0) {
    }
    
    public void setCharacterEncoding(final String arg0) {
    }
    
    public void setContentLength(final int arg0) {
    }
    
    public void setContentType(final String arg0) {
    }
    
    public void setLocale(final Locale arg0) {
    }
    
    public void addCookie(final Cookie arg0) {
    }
    
    public void addDateHeader(final String arg0, final long arg1) {
    }
    
    public void addHeader(final String arg0, final String arg1) {
    }
    
    public void addIntHeader(final String arg0, final int arg1) {
    }
    
    public boolean containsHeader(final String arg0) {
        return false;
    }
    
    public String encodeRedirectURL(final String arg0) {
        return null;
    }
    
    public String encodeRedirectUrl(final String arg0) {
        return null;
    }
    
    public String encodeURL(final String arg0) {
        return null;
    }
    
    public String encodeUrl(final String arg0) {
        return null;
    }
    
    public void sendError(final int arg0) throws IOException {
    }
    
    public void sendError(final int arg0, final String arg1) throws IOException {
    }
    
    public void sendRedirect(final String arg0) throws IOException {
    }
    
    public void setDateHeader(final String arg0, final long arg1) {
    }
    
    public void setHeader(final String arg0, final String arg1) {
    }
    
    public void setIntHeader(final String arg0, final int arg1) {
    }
    
    public void setStatus(final int arg0) {
    }
    
    public void setStatus(final int arg0, final String arg1) {
    }
    
    public String getHeader(final String arg0) {
        return null;
    }
    
    public Collection<String> getHeaderNames() {
        return null;
    }
    
    public Collection<String> getHeaders(final String arg0) {
        return null;
    }
    
    public int getStatus() {
        return 0;
    }
    
    public void setContentLengthLong(final long len) {
    }
}
