package org.owasp.esapi;

import org.owasp.esapi.errors.EncryptionException;
import java.util.Set;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.owasp.esapi.errors.AuthenticationException;

public interface Authenticator
{
    void clearCurrent();
    
    User login() throws AuthenticationException;
    
    User login(final HttpServletRequest p0, final HttpServletResponse p1) throws AuthenticationException;
    
    boolean verifyPassword(final User p0, final String p1);
    
    void logout();
    
    User createUser(final String p0, final String p1, final String p2) throws AuthenticationException;
    
    String generateStrongPassword();
    
    String generateStrongPassword(final User p0, final String p1);
    
    void changePassword(final User p0, final String p1, final String p2, final String p3) throws AuthenticationException;
    
    User getUser(final long p0);
    
    User getUser(final String p0);
    
    Set getUserNames();
    
    User getCurrentUser();
    
    void setCurrentUser(final User p0);
    
    String hashPassword(final String p0, final String p1) throws EncryptionException;
    
    void removeUser(final String p0) throws AuthenticationException;
    
    void verifyAccountNameStrength(final String p0) throws AuthenticationException;
    
    void verifyPasswordStrength(final String p0, final String p1, final User p2) throws AuthenticationException;
    
    boolean exists(final String p0);
}
