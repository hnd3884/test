package org.owasp.esapi;

import org.owasp.esapi.errors.IntrusionException;
import java.io.IOException;
import javax.servlet.ServletException;
import java.io.File;
import java.util.List;
import org.owasp.esapi.errors.ValidationException;
import org.owasp.esapi.errors.EncryptionException;
import java.util.Map;
import org.owasp.esapi.errors.AuthenticationException;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import org.owasp.esapi.errors.AccessControlException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;

public interface HTTPUtilities
{
    public static final String REMEMBER_TOKEN_COOKIE_NAME = "rtoken";
    public static final int MAX_COOKIE_LEN = 4096;
    public static final int MAX_COOKIE_PAIRS = 20;
    public static final String CSRF_TOKEN_NAME = "ctoken";
    public static final String ESAPI_STATE = "estate";
    public static final int PARAMETER = 0;
    public static final int HEADER = 1;
    public static final int COOKIE = 2;
    
    void addCookie(final Cookie p0);
    
    void addCookie(final HttpServletResponse p0, final Cookie p1);
    
    String addCSRFToken(final String p0);
    
    void addHeader(final String p0, final String p1);
    
    void addHeader(final HttpServletResponse p0, final String p1, final String p2);
    
    void assertSecureRequest() throws AccessControlException;
    
    void assertSecureChannel() throws AccessControlException;
    
    void assertSecureRequest(final HttpServletRequest p0) throws AccessControlException;
    
    void assertSecureChannel(final HttpServletRequest p0) throws AccessControlException;
    
    HttpSession changeSessionIdentifier() throws AuthenticationException;
    
    HttpSession changeSessionIdentifier(final HttpServletRequest p0) throws AuthenticationException;
    
    void clearCurrent();
    
    String decryptHiddenField(final String p0);
    
    Map<String, String> decryptQueryString(final String p0) throws EncryptionException;
    
    Map<String, String> decryptStateFromCookie() throws EncryptionException;
    
    Map<String, String> decryptStateFromCookie(final HttpServletRequest p0) throws EncryptionException;
    
    String encryptHiddenField(final String p0) throws EncryptionException;
    
    String encryptQueryString(final String p0) throws EncryptionException;
    
    void encryptStateInCookie(final Map<String, String> p0) throws EncryptionException;
    
    void encryptStateInCookie(final HttpServletResponse p0, final Map<String, String> p1) throws EncryptionException;
    
    String getCookie(final String p0) throws ValidationException;
    
    String getCookie(final HttpServletRequest p0, final String p1) throws ValidationException;
    
    String getCSRFToken();
    
    HttpServletRequest getCurrentRequest();
    
    HttpServletResponse getCurrentResponse();
    
    List getFileUploads() throws ValidationException;
    
    List getFileUploads(final HttpServletRequest p0) throws ValidationException;
    
    List getFileUploads(final HttpServletRequest p0, final File p1) throws ValidationException;
    
    List getFileUploads(final HttpServletRequest p0, final File p1, final List p2) throws ValidationException;
    
    String getHeader(final String p0) throws ValidationException;
    
    String getHeader(final HttpServletRequest p0, final String p1) throws ValidationException;
    
    String getParameter(final String p0) throws ValidationException;
    
    String getParameter(final HttpServletRequest p0, final String p1) throws ValidationException;
    
    void killAllCookies();
    
    void killAllCookies(final HttpServletRequest p0, final HttpServletResponse p1);
    
    void killCookie(final String p0);
    
    void killCookie(final HttpServletRequest p0, final HttpServletResponse p1, final String p2);
    
    void logHTTPRequest();
    
    void logHTTPRequest(final HttpServletRequest p0, final Logger p1);
    
    void logHTTPRequest(final HttpServletRequest p0, final Logger p1, final List p2);
    
    void sendForward(final String p0) throws AccessControlException, ServletException, IOException;
    
    void sendForward(final HttpServletRequest p0, final HttpServletResponse p1, final String p2) throws AccessControlException, ServletException, IOException;
    
    void sendRedirect(final String p0) throws AccessControlException, IOException;
    
    void sendRedirect(final HttpServletResponse p0, final String p1) throws AccessControlException, IOException;
    
    void setContentType();
    
    void setContentType(final HttpServletResponse p0);
    
    void setCurrentHTTP(final HttpServletRequest p0, final HttpServletResponse p1);
    
    void setHeader(final String p0, final String p1);
    
    void setHeader(final HttpServletResponse p0, final String p1, final String p2);
    
    void setNoCacheHeaders();
    
    void setNoCacheHeaders(final HttpServletResponse p0);
    
    String setRememberToken(final String p0, final int p1, final String p2, final String p3);
    
    String setRememberToken(final HttpServletRequest p0, final HttpServletResponse p1, final String p2, final int p3, final String p4, final String p5);
    
    void verifyCSRFToken();
    
    void verifyCSRFToken(final HttpServletRequest p0) throws IntrusionException;
    
     <T> T getSessionAttribute(final String p0);
    
     <T> T getSessionAttribute(final HttpSession p0, final String p1);
    
     <T> T getRequestAttribute(final String p0);
    
     <T> T getRequestAttribute(final HttpServletRequest p0, final String p1);
}
