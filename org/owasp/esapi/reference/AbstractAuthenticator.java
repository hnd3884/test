package org.owasp.esapi.reference;

import java.util.Date;
import org.owasp.esapi.errors.AuthenticationLoginException;
import org.owasp.esapi.errors.AccessControlException;
import javax.servlet.http.HttpServletResponse;
import org.owasp.esapi.errors.AuthenticationCredentialsException;
import org.owasp.esapi.errors.EnterpriseSecurityException;
import org.owasp.esapi.errors.AuthenticationException;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import org.owasp.esapi.HTTPUtilities;
import org.owasp.esapi.User;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;
import org.owasp.esapi.Authenticator;

public abstract class AbstractAuthenticator implements Authenticator
{
    protected static final String USER = "ESAPIUserSessionKey";
    private final Logger logger;
    private final ThreadLocalUser currentUser;
    
    public AbstractAuthenticator() {
        this.logger = ESAPI.getLogger("Authenticator");
        this.currentUser = new ThreadLocalUser();
    }
    
    @Override
    public void clearCurrent() {
        this.currentUser.setUser(null);
    }
    
    @Override
    public boolean exists(final String accountName) {
        return this.getUser(accountName) != null;
    }
    
    @Override
    public User getCurrentUser() {
        User user = this.currentUser.get();
        if (user == null) {
            user = User.ANONYMOUS;
        }
        return user;
    }
    
    protected User getUserFromSession() {
        final HTTPUtilities httpUtils = ESAPI.httpUtilities();
        final HttpServletRequest req = httpUtils.getCurrentRequest();
        final HttpSession session = req.getSession(false);
        if (session == null) {
            return null;
        }
        return ESAPI.httpUtilities().getSessionAttribute("ESAPIUserSessionKey");
    }
    
    protected DefaultUser getUserFromRememberToken() {
        try {
            final String token = ESAPI.httpUtilities().getCookie(ESAPI.currentRequest(), "rtoken");
            if (token == null) {
                return null;
            }
            final String[] data = ESAPI.encryptor().unseal(token).split("\\|");
            if (data.length != 2) {
                this.logger.warning(Logger.SECURITY_FAILURE, "Found corrupt or expired remember token");
                ESAPI.httpUtilities().killCookie(ESAPI.currentRequest(), ESAPI.currentResponse(), "rtoken");
                return null;
            }
            final String username = data[0];
            final String password = data[1];
            final DefaultUser user = (DefaultUser)this.getUser(username);
            if (user == null) {
                this.logger.warning(Logger.SECURITY_FAILURE, "Found valid remember token but no user matching " + username);
                return null;
            }
            this.logger.info(Logger.SECURITY_SUCCESS, "Logging in user with remember token: " + user.getAccountName());
            user.loginWithPassword(password);
            return user;
        }
        catch (final AuthenticationException ae) {
            this.logger.warning(Logger.SECURITY_FAILURE, "Login via remember me cookie failed", ae);
        }
        catch (final EnterpriseSecurityException e) {
            this.logger.warning(Logger.SECURITY_FAILURE, "Remember token was missing, corrupt, or expired");
        }
        ESAPI.httpUtilities().killCookie(ESAPI.currentRequest(), ESAPI.currentResponse(), "rtoken");
        return null;
    }
    
    private User loginWithUsernameAndPassword(final HttpServletRequest request) throws AuthenticationException {
        String username = request.getParameter(ESAPI.securityConfiguration().getUsernameParameterName());
        final String password = request.getParameter(ESAPI.securityConfiguration().getPasswordParameterName());
        User user = this.getCurrentUser();
        if (user != null && !user.isAnonymous()) {
            this.logger.warning(Logger.SECURITY_SUCCESS, "User requested relogin. Performing logout then authentication");
            user.logout();
        }
        if (username == null || password == null) {
            if (username == null) {
                username = "unspecified user";
            }
            throw new AuthenticationCredentialsException("Authentication failed", "Authentication failed for " + username + " because of null username or password");
        }
        user = this.getUser(username);
        if (user == null) {
            throw new AuthenticationCredentialsException("Authentication failed", "Authentication failed because user " + username + " doesn't exist");
        }
        user.loginWithPassword(password);
        request.setAttribute(user.getCSRFToken(), (Object)"authenticated");
        return user;
    }
    
    @Override
    public User login() throws AuthenticationException {
        return this.login(ESAPI.currentRequest(), ESAPI.currentResponse());
    }
    
    @Override
    public User login(final HttpServletRequest request, final HttpServletResponse response) throws AuthenticationException {
        if (request == null || response == null) {
            throw new AuthenticationCredentialsException("Invalid request", "Request or response objects were null");
        }
        DefaultUser user = (DefaultUser)this.getUserFromSession();
        if (user == null) {
            user = this.getUserFromRememberToken();
        }
        if (user == null) {
            user = (DefaultUser)this.loginWithUsernameAndPassword(request);
        }
        user.setLastHostAddress(request.getRemoteHost());
        try {
            ESAPI.httpUtilities().assertSecureRequest(ESAPI.currentRequest());
        }
        catch (final AccessControlException e) {
            throw new AuthenticationException("Attempt to login with an insecure request", e.getLogMessage(), e);
        }
        if (user.isAnonymous()) {
            user.logout();
            throw new AuthenticationLoginException("Login failed", "Anonymous user cannot be set to current user. User: " + user.getAccountName());
        }
        if (!user.isEnabled()) {
            user.logout();
            user.incrementFailedLoginCount();
            user.setLastFailedLoginTime(new Date());
            throw new AuthenticationLoginException("Login failed", "Disabled user cannot be set to current user. User: " + user.getAccountName());
        }
        if (user.isLocked()) {
            user.logout();
            user.incrementFailedLoginCount();
            user.setLastFailedLoginTime(new Date());
            throw new AuthenticationLoginException("Login failed", "Locked user cannot be set to current user. User: " + user.getAccountName());
        }
        if (user.isExpired()) {
            user.logout();
            user.incrementFailedLoginCount();
            user.setLastFailedLoginTime(new Date());
            throw new AuthenticationLoginException("Login failed", "Expired user cannot be set to current user. User: " + user.getAccountName());
        }
        if (user.isSessionTimeout()) {
            user.logout();
            user.incrementFailedLoginCount();
            user.setLastFailedLoginTime(new Date());
            throw new AuthenticationLoginException("Login failed", "Session inactivity timeout: " + user.getAccountName());
        }
        if (user.isSessionAbsoluteTimeout()) {
            user.logout();
            user.incrementFailedLoginCount();
            user.setLastFailedLoginTime(new Date());
            throw new AuthenticationLoginException("Login failed", "Session absolute timeout: " + user.getAccountName());
        }
        user.setLocale(request.getLocale());
        final HttpSession session = request.getSession();
        user.addSession(session);
        session.setAttribute("ESAPIUserSessionKey", (Object)user);
        this.setCurrentUser(user);
        return user;
    }
    
    @Override
    public void logout() {
        final User user = this.getCurrentUser();
        if (user != null && !user.isAnonymous()) {
            user.logout();
        }
    }
    
    @Override
    public void setCurrentUser(final User user) {
        this.currentUser.setUser(user);
    }
    
    private class ThreadLocalUser extends InheritableThreadLocal<User>
    {
        public User initialValue() {
            return User.ANONYMOUS;
        }
        
        public User getUser() {
            return super.get();
        }
        
        public void setUser(final User newUser) {
            super.set(newUser);
        }
    }
}
