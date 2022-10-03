package javax.servlet.http;

import java.util.Collection;
import javax.servlet.ServletException;
import java.io.IOException;
import java.security.Principal;
import java.util.Enumeration;
import javax.servlet.ServletRequest;

public interface HttpServletRequest extends ServletRequest
{
    public static final String BASIC_AUTH = "BASIC";
    public static final String FORM_AUTH = "FORM";
    public static final String CLIENT_CERT_AUTH = "CLIENT_CERT";
    public static final String DIGEST_AUTH = "DIGEST";
    
    String getAuthType();
    
    Cookie[] getCookies();
    
    long getDateHeader(final String p0);
    
    String getHeader(final String p0);
    
    Enumeration<String> getHeaders(final String p0);
    
    Enumeration<String> getHeaderNames();
    
    int getIntHeader(final String p0);
    
    String getMethod();
    
    String getPathInfo();
    
    String getPathTranslated();
    
    String getContextPath();
    
    String getQueryString();
    
    String getRemoteUser();
    
    boolean isUserInRole(final String p0);
    
    Principal getUserPrincipal();
    
    String getRequestedSessionId();
    
    String getRequestURI();
    
    StringBuffer getRequestURL();
    
    String getServletPath();
    
    HttpSession getSession(final boolean p0);
    
    HttpSession getSession();
    
    String changeSessionId();
    
    boolean isRequestedSessionIdValid();
    
    boolean isRequestedSessionIdFromCookie();
    
    boolean isRequestedSessionIdFromURL();
    
    @Deprecated
    boolean isRequestedSessionIdFromUrl();
    
    boolean authenticate(final HttpServletResponse p0) throws IOException, ServletException;
    
    void login(final String p0, final String p1) throws ServletException;
    
    void logout() throws ServletException;
    
    Collection<Part> getParts() throws IOException, ServletException;
    
    Part getPart(final String p0) throws IOException, ServletException;
    
     <T extends HttpUpgradeHandler> T upgrade(final Class<T> p0) throws IOException, ServletException;
}
