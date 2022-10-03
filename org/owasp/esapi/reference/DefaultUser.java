package org.owasp.esapi.reference;

import org.owasp.esapi.errors.AuthenticationHostException;
import org.owasp.esapi.EncoderConstants;
import org.owasp.esapi.errors.AuthenticationLoginException;
import java.util.Collections;
import org.owasp.esapi.errors.EncryptionException;
import java.util.Iterator;
import org.owasp.esapi.errors.AuthenticationException;
import org.owasp.esapi.errors.AuthenticationAccountsException;
import java.util.HashSet;
import org.owasp.esapi.ESAPI;
import java.util.Locale;
import java.util.HashMap;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Set;
import org.owasp.esapi.Logger;
import java.io.Serializable;
import org.owasp.esapi.User;

public class DefaultUser implements User, Serializable
{
    private static final long serialVersionUID = 1L;
    private static final int IDLE_TIMEOUT_LENGTH;
    private static final int ABSOLUTE_TIMEOUT_LENGTH;
    private final transient Logger logger;
    long accountId;
    private String accountName;
    private String screenName;
    private String csrfToken;
    private Set<String> roles;
    private boolean locked;
    private boolean loggedIn;
    private boolean enabled;
    private String lastHostAddress;
    private Date lastPasswordChangeTime;
    private Date lastLoginTime;
    private Date lastFailedLoginTime;
    private Date expirationTime;
    private transient Set<HttpSession> sessions;
    private transient HashMap eventMap;
    private int failedLoginCount;
    private Locale locale;
    private static final int MAX_ROLE_LENGTH = 250;
    
    public DefaultUser(final String accountName) {
        this.logger = ESAPI.getLogger("DefaultUser");
        this.accountId = 0L;
        this.accountName = "";
        this.screenName = "";
        this.csrfToken = this.resetCSRFToken();
        this.roles = new HashSet<String>();
        this.locked = false;
        this.loggedIn = true;
        this.enabled = false;
        this.lastPasswordChangeTime = new Date(0L);
        this.lastLoginTime = new Date(0L);
        this.lastFailedLoginTime = new Date(0L);
        this.expirationTime = new Date(Long.MAX_VALUE);
        this.sessions = new HashSet<HttpSession>();
        this.eventMap = new HashMap();
        this.failedLoginCount = 0;
        this.accountName = accountName.toLowerCase();
        long id;
        do {
            id = Math.abs(ESAPI.randomizer().getRandomLong());
        } while (ESAPI.authenticator().getUser(id) != null || id == 0L);
        this.accountId = id;
    }
    
    @Override
    public void addRole(final String role) throws AuthenticationException {
        final String roleName = role.toLowerCase();
        if (ESAPI.validator().isValidInput("addRole", roleName, "RoleName", 250, false)) {
            this.roles.add(roleName);
            this.logger.info(Logger.SECURITY_SUCCESS, "Role " + roleName + " added to " + this.getAccountName());
            return;
        }
        throw new AuthenticationAccountsException("Add role failed", "Attempt to add invalid role " + roleName + " to " + this.getAccountName());
    }
    
    @Override
    public void addRoles(final Set<String> newRoles) throws AuthenticationException {
        for (final String newRole : newRoles) {
            this.addRole(newRole);
        }
    }
    
    @Override
    public void changePassword(final String oldPassword, final String newPassword1, final String newPassword2) throws AuthenticationException, EncryptionException {
        ESAPI.authenticator().changePassword(this, oldPassword, newPassword1, newPassword2);
    }
    
    @Override
    public void disable() {
        this.enabled = false;
        this.logger.info(Logger.SECURITY_SUCCESS, "Account disabled: " + this.getAccountName());
    }
    
    @Override
    public void enable() {
        this.enabled = true;
        this.logger.info(Logger.SECURITY_SUCCESS, "Account enabled: " + this.getAccountName());
    }
    
    @Override
    public long getAccountId() {
        return this.accountId;
    }
    
    @Override
    public String getAccountName() {
        return this.accountName;
    }
    
    @Override
    public String getCSRFToken() {
        return this.csrfToken;
    }
    
    @Override
    public Date getExpirationTime() {
        return (Date)this.expirationTime.clone();
    }
    
    @Override
    public int getFailedLoginCount() {
        return this.failedLoginCount;
    }
    
    void setFailedLoginCount(final int count) {
        this.failedLoginCount = count;
    }
    
    @Override
    public Date getLastFailedLoginTime() {
        return (Date)this.lastFailedLoginTime.clone();
    }
    
    @Override
    public String getLastHostAddress() {
        if (this.lastHostAddress == null) {
            return "unknown";
        }
        return this.lastHostAddress;
    }
    
    @Override
    public Date getLastLoginTime() {
        return (Date)this.lastLoginTime.clone();
    }
    
    @Override
    public Date getLastPasswordChangeTime() {
        return (Date)this.lastPasswordChangeTime.clone();
    }
    
    @Override
    public String getName() {
        return this.getAccountName();
    }
    
    @Override
    public Set<String> getRoles() {
        return Collections.unmodifiableSet((Set<? extends String>)this.roles);
    }
    
    @Override
    public String getScreenName() {
        return this.screenName;
    }
    
    @Override
    public void addSession(final HttpSession s) {
        this.sessions.add(s);
    }
    
    @Override
    public void removeSession(final HttpSession s) {
        this.sessions.remove(s);
    }
    
    @Override
    public Set getSessions() {
        return this.sessions;
    }
    
    @Override
    public void incrementFailedLoginCount() {
        ++this.failedLoginCount;
    }
    
    @Override
    public boolean isAnonymous() {
        return false;
    }
    
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
    
    @Override
    public boolean isExpired() {
        return this.getExpirationTime().before(new Date());
    }
    
    @Override
    public boolean isInRole(final String role) {
        return this.roles.contains(role.toLowerCase());
    }
    
    @Override
    public boolean isLocked() {
        return this.locked;
    }
    
    @Override
    public boolean isLoggedIn() {
        return this.loggedIn;
    }
    
    @Override
    public boolean isSessionAbsoluteTimeout() {
        final HttpSession session = ESAPI.httpUtilities().getCurrentRequest().getSession(false);
        if (session == null) {
            return true;
        }
        final Date deadline = new Date(session.getCreationTime() + DefaultUser.ABSOLUTE_TIMEOUT_LENGTH);
        final Date now = new Date();
        return now.after(deadline);
    }
    
    @Override
    public boolean isSessionTimeout() {
        final HttpSession session = ESAPI.httpUtilities().getCurrentRequest().getSession(false);
        if (session == null) {
            return true;
        }
        final Date deadline = new Date(session.getLastAccessedTime() + DefaultUser.IDLE_TIMEOUT_LENGTH);
        final Date now = new Date();
        return now.after(deadline);
    }
    
    @Override
    public void lock() {
        this.locked = true;
        this.logger.info(Logger.SECURITY_SUCCESS, "Account locked: " + this.getAccountName());
    }
    
    @Override
    public void loginWithPassword(final String password) throws AuthenticationException {
        if (password == null || password.equals("")) {
            this.setLastFailedLoginTime(new Date());
            this.incrementFailedLoginCount();
            throw new AuthenticationLoginException("Login failed", "Missing password: " + this.accountName);
        }
        if (!this.isEnabled()) {
            this.setLastFailedLoginTime(new Date());
            this.incrementFailedLoginCount();
            throw new AuthenticationLoginException("Login failed", "Disabled user attempt to login: " + this.accountName);
        }
        if (this.isLocked()) {
            this.setLastFailedLoginTime(new Date());
            this.incrementFailedLoginCount();
            throw new AuthenticationLoginException("Login failed", "Locked user attempt to login: " + this.accountName);
        }
        if (this.isExpired()) {
            this.setLastFailedLoginTime(new Date());
            this.incrementFailedLoginCount();
            throw new AuthenticationLoginException("Login failed", "Expired user attempt to login: " + this.accountName);
        }
        this.logout();
        if (this.verifyPassword(password)) {
            this.loggedIn = true;
            ESAPI.httpUtilities().changeSessionIdentifier(ESAPI.currentRequest());
            ESAPI.authenticator().setCurrentUser(this);
            this.setLastLoginTime(new Date());
            this.setLastHostAddress(ESAPI.httpUtilities().getCurrentRequest().getRemoteAddr());
            this.logger.trace(Logger.SECURITY_SUCCESS, "User logged in: " + this.accountName);
            return;
        }
        this.loggedIn = false;
        this.setLastFailedLoginTime(new Date());
        this.incrementFailedLoginCount();
        if (this.getFailedLoginCount() >= ESAPI.securityConfiguration().getAllowedLoginAttempts()) {
            this.lock();
        }
        throw new AuthenticationLoginException("Login failed", "Incorrect password provided for " + this.getAccountName());
    }
    
    @Override
    public void logout() {
        ESAPI.httpUtilities().killCookie(ESAPI.currentRequest(), ESAPI.currentResponse(), "rtoken");
        final HttpSession session = ESAPI.currentRequest().getSession(false);
        if (session != null) {
            this.removeSession(session);
            session.invalidate();
        }
        ESAPI.httpUtilities().killCookie(ESAPI.currentRequest(), ESAPI.currentResponse(), ESAPI.securityConfiguration().getHttpSessionIdName());
        this.loggedIn = false;
        this.logger.info(Logger.SECURITY_SUCCESS, "Logout successful");
        ESAPI.authenticator().setCurrentUser(User.ANONYMOUS);
    }
    
    @Override
    public void removeRole(final String role) {
        this.roles.remove(role.toLowerCase());
        this.logger.trace(Logger.SECURITY_SUCCESS, "Role " + role + " removed from " + this.getAccountName());
    }
    
    @Override
    public String resetCSRFToken() {
        return this.csrfToken = ESAPI.randomizer().getRandomString(8, EncoderConstants.CHAR_ALPHANUMERICS);
    }
    
    private void setAccountId(final long accountId) {
        this.accountId = accountId;
    }
    
    @Override
    public void setAccountName(final String accountName) {
        String old = this.getAccountName();
        this.accountName = accountName.toLowerCase();
        if (old != null) {
            if (old.equals("")) {
                old = "[nothing]";
            }
            this.logger.info(Logger.SECURITY_SUCCESS, "Account name changed from " + old + " to " + this.getAccountName());
        }
    }
    
    @Override
    public void setExpirationTime(final Date expirationTime) {
        this.expirationTime = new Date(expirationTime.getTime());
        this.logger.info(Logger.SECURITY_SUCCESS, "Account expiration time set to " + expirationTime + " for " + this.getAccountName());
    }
    
    @Override
    public void setLastFailedLoginTime(final Date lastFailedLoginTime) {
        this.lastFailedLoginTime = lastFailedLoginTime;
        this.logger.info(Logger.SECURITY_SUCCESS, "Set last failed login time to " + lastFailedLoginTime + " for " + this.getAccountName());
    }
    
    @Override
    public void setLastHostAddress(final String remoteHost) throws AuthenticationHostException {
        if (this.lastHostAddress != null && !this.lastHostAddress.equals(remoteHost)) {
            throw new AuthenticationHostException("Host change", "User session just jumped from " + this.lastHostAddress + " to " + remoteHost);
        }
        this.lastHostAddress = remoteHost;
    }
    
    @Override
    public void setLastLoginTime(final Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
        this.logger.info(Logger.SECURITY_SUCCESS, "Set last successful login time to " + lastLoginTime + " for " + this.getAccountName());
    }
    
    @Override
    public void setLastPasswordChangeTime(final Date lastPasswordChangeTime) {
        this.lastPasswordChangeTime = lastPasswordChangeTime;
        this.logger.info(Logger.SECURITY_SUCCESS, "Set last password change time to " + lastPasswordChangeTime + " for " + this.getAccountName());
    }
    
    @Override
    public void setRoles(final Set<String> roles) throws AuthenticationException {
        this.roles = new HashSet<String>();
        this.addRoles(roles);
        this.logger.info(Logger.SECURITY_SUCCESS, "Adding roles " + roles + " to " + this.getAccountName());
    }
    
    @Override
    public void setScreenName(final String screenName) {
        this.screenName = screenName;
        this.logger.info(Logger.SECURITY_SUCCESS, "ScreenName changed to " + screenName + " for " + this.getAccountName());
    }
    
    @Override
    public String toString() {
        return "USER:" + this.accountName;
    }
    
    @Override
    public void unlock() {
        this.locked = false;
        this.failedLoginCount = 0;
        this.logger.info(Logger.SECURITY_SUCCESS, "Account unlocked: " + this.getAccountName());
    }
    
    @Override
    public boolean verifyPassword(final String password) {
        return ESAPI.authenticator().verifyPassword(this, password);
    }
    
    public final Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
    
    @Override
    public Locale getLocale() {
        return this.locale;
    }
    
    @Override
    public void setLocale(final Locale locale) {
        this.locale = locale;
    }
    
    @Override
    public HashMap getEventMap() {
        return this.eventMap;
    }
    
    static {
        IDLE_TIMEOUT_LENGTH = ESAPI.securityConfiguration().getSessionIdleTimeoutLength();
        ABSOLUTE_TIMEOUT_LENGTH = ESAPI.securityConfiguration().getSessionAbsoluteTimeoutLength();
    }
}
