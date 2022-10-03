package javax.servlet;

import java.util.Locale;
import java.io.BufferedReader;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

public interface ServletRequest
{
    Object getAttribute(final String p0);
    
    Enumeration<String> getAttributeNames();
    
    String getCharacterEncoding();
    
    void setCharacterEncoding(final String p0) throws UnsupportedEncodingException;
    
    int getContentLength();
    
    long getContentLengthLong();
    
    String getContentType();
    
    ServletInputStream getInputStream() throws IOException;
    
    String getParameter(final String p0);
    
    Enumeration<String> getParameterNames();
    
    String[] getParameterValues(final String p0);
    
    Map<String, String[]> getParameterMap();
    
    String getProtocol();
    
    String getScheme();
    
    String getServerName();
    
    int getServerPort();
    
    BufferedReader getReader() throws IOException;
    
    String getRemoteAddr();
    
    String getRemoteHost();
    
    void setAttribute(final String p0, final Object p1);
    
    void removeAttribute(final String p0);
    
    Locale getLocale();
    
    Enumeration<Locale> getLocales();
    
    boolean isSecure();
    
    RequestDispatcher getRequestDispatcher(final String p0);
    
    @Deprecated
    String getRealPath(final String p0);
    
    int getRemotePort();
    
    String getLocalName();
    
    String getLocalAddr();
    
    int getLocalPort();
    
    ServletContext getServletContext();
    
    AsyncContext startAsync() throws IllegalStateException;
    
    AsyncContext startAsync(final ServletRequest p0, final ServletResponse p1) throws IllegalStateException;
    
    boolean isAsyncStarted();
    
    boolean isAsyncSupported();
    
    AsyncContext getAsyncContext();
    
    DispatcherType getDispatcherType();
}
