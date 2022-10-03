package org.owasp.esapi;

import java.util.HashSet;
import java.util.HashMap;
import org.owasp.esapi.errors.AuthenticationHostException;
import javax.servlet.http.HttpSession;
import java.util.Date;
import org.owasp.esapi.errors.EncryptionException;
import java.util.Set;
import org.owasp.esapi.errors.AuthenticationException;
import java.util.Locale;
import java.io.Serializable;
import java.security.Principal;

public interface User extends Principal, Serializable
{
    public static final User ANONYMOUS = new User() {
        private static final long serialVersionUID = -1850916950784965502L;
        private String csrfToken = "";
        private Set sessions = new HashSet();
        private Locale locale = null;
        
        @Override
        public void addRole(final String role) throws AuthenticationException {
            throw new RuntimeException("Invalid operation for the anonymous user");
        }
        
        @Override
        public void addRoles(final Set newRoles) throws AuthenticationException {
            throw new RuntimeException("Invalid operation for the anonymous user");
        }
        
        @Override
        public void changePassword(final String oldPassword, final String newPassword1, final String newPassword2) throws AuthenticationException, EncryptionException {
            throw new RuntimeException("Invalid operation for the anonymous user");
        }
        
        @Override
        public void disable() {
            throw new RuntimeException("Invalid operation for the anonymous user");
        }
        
        @Override
        public void enable() {
            throw new RuntimeException("Invalid operation for the anonymous user");
        }
        
        @Override
        public long getAccountId() {
            return 0L;
        }
        
        @Override
        public String getAccountName() {
            return "Anonymous";
        }
        
        @Override
        public String getName() {
            return this.getAccountName();
        }
        
        @Override
        public String getCSRFToken() {
            return this.csrfToken;
        }
        
        @Override
        public Date getExpirationTime() {
            return null;
        }
        
        @Override
        public int getFailedLoginCount() {
            return 0;
        }
        
        @Override
        public Date getLastFailedLoginTime() throws AuthenticationException {
            return null;
        }
        
        @Override
        public String getLastHostAddress() {
            return "unknown";
        }
        
        @Override
        public Date getLastLoginTime() {
            return null;
        }
        
        @Override
        public Date getLastPasswordChangeTime() {
            return null;
        }
        
        @Override
        public Set<String> getRoles() {
            return new HashSet<String>();
        }
        
        @Override
        public String getScreenName() {
            return "Anonymous";
        }
        
        @Override
        public void addSession(final HttpSession s) {
        }
        
        @Override
        public void removeSession(final HttpSession s) {
        }
        
        @Override
        public Set getSessions() {
            return this.sessions;
        }
        
        @Override
        public void incrementFailedLoginCount() {
            throw new RuntimeException("Invalid operation for the anonymous user");
        }
        
        @Override
        public boolean isAnonymous() {
            return true;
        }
        
        @Override
        public boolean isEnabled() {
            return false;
        }
        
        @Override
        public boolean isExpired() {
            return false;
        }
        
        @Override
        public boolean isInRole(final String role) {
            return false;
        }
        
        @Override
        public boolean isLocked() {
            return false;
        }
        
        @Override
        public boolean isLoggedIn() {
            return false;
        }
        
        @Override
        public boolean isSessionAbsoluteTimeout() {
            return false;
        }
        
        @Override
        public boolean isSessionTimeout() {
            return false;
        }
        
        @Override
        public void lock() {
            throw new RuntimeException("Invalid operation for the anonymous user");
        }
        
        @Override
        public void loginWithPassword(final String password) throws AuthenticationException {
            throw new RuntimeException("Invalid operation for the anonymous user");
        }
        
        @Override
        public void logout() {
            throw new RuntimeException("Invalid operation for the anonymous user");
        }
        
        @Override
        public void removeRole(final String role) throws AuthenticationException {
            throw new RuntimeException("Invalid operation for the anonymous user");
        }
        
        @Override
        public String resetCSRFToken() throws AuthenticationException {
            return this.csrfToken = ESAPI.randomizer().getRandomString(8, EncoderConstants.CHAR_ALPHANUMERICS);
        }
        
        @Override
        public void setAccountName(final String accountName) {
            throw new RuntimeException("Invalid operation for the anonymous user");
        }
        
        @Override
        public void setExpirationTime(final Date expirationTime) {
            throw new RuntimeException("Invalid operation for the anonymous user");
        }
        
        @Override
        public void setRoles(final Set roles) throws AuthenticationException {
            throw new RuntimeException("Invalid operation for the anonymous user");
        }
        
        @Override
        public void setScreenName(final String screenName) {
            throw new RuntimeException("Invalid operation for the anonymous user");
        }
        
        @Override
        public void unlock() {
            throw new RuntimeException("Invalid operation for the anonymous user");
        }
        
        @Override
        public boolean verifyPassword(final String password) throws EncryptionException {
            throw new RuntimeException("Invalid operation for the anonymous user");
        }
        
        @Override
        public void setLastFailedLoginTime(final Date lastFailedLoginTime) {
            throw new RuntimeException("Invalid operation for the anonymous user");
        }
        
        @Override
        public void setLastLoginTime(final Date lastLoginTime) {
            throw new RuntimeException("Invalid operation for the anonymous user");
        }
        
        @Override
        public void setLastHostAddress(final String remoteHost) {
            throw new RuntimeException("Invalid operation for the anonymous user");
        }
        
        @Override
        public void setLastPasswordChangeTime(final Date lastPasswordChangeTime) {
            throw new RuntimeException("Invalid operation for the anonymous user");
        }
        
        @Override
        public HashMap getEventMap() {
            throw new RuntimeException("Invalid operation for the anonymous user");
        }
        
        @Override
        public Locale getLocale() {
            return this.locale;
        }
        
        @Override
        public void setLocale(final Locale locale) {
            this.locale = locale;
        }
    };
    
    Locale getLocale();
    
    void setLocale(final Locale p0);
    
    void addRole(final String p0) throws AuthenticationException;
    
    void addRoles(final Set<String> p0) throws AuthenticationException;
    
    void changePassword(final String p0, final String p1, final String p2) throws AuthenticationException, EncryptionException;
    
    void disable();
    
    void enable();
    
    long getAccountId();
    
    String getAccountName();
    
    String getCSRFToken();
    
    Date getExpirationTime();
    
    int getFailedLoginCount();
    
    String getLastHostAddress();
    
    Date getLastFailedLoginTime() throws AuthenticationException;
    
    Date getLastLoginTime();
    
    Date getLastPasswordChangeTime();
    
    Set<String> getRoles();
    
    String getScreenName();
    
    void addSession(final HttpSession p0);
    
    void removeSession(final HttpSession p0);
    
    Set getSessions();
    
    void incrementFailedLoginCount();
    
    boolean isAnonymous();
    
    boolean isEnabled();
    
    boolean isExpired();
    
    boolean isInRole(final String p0);
    
    boolean isLocked();
    
    boolean isLoggedIn();
    
    boolean isSessionAbsoluteTimeout();
    
    boolean isSessionTimeout();
    
    void lock();
    
    void loginWithPassword(final String p0) throws AuthenticationException;
    
    void logout();
    
    void removeRole(final String p0) throws AuthenticationException;
    
    String resetCSRFToken() throws AuthenticationException;
    
    void setAccountName(final String p0);
    
    void setExpirationTime(final Date p0);
    
    void setRoles(final Set<String> p0) throws AuthenticationException;
    
    void setScreenName(final String p0);
    
    void unlock();
    
    boolean verifyPassword(final String p0) throws EncryptionException;
    
    void setLastFailedLoginTime(final Date p0);
    
    void setLastHostAddress(final String p0) throws AuthenticationHostException;
    
    void setLastLoginTime(final Date p0);
    
    void setLastPasswordChangeTime(final Date p0);
    
    HashMap getEventMap();
}
