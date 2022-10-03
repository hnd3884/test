package com.sun.security.auth.module;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.Callback;
import javax.naming.directory.Attribute;
import javax.naming.NamingEnumeration;
import javax.naming.directory.SearchResult;
import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.security.auth.login.FailedLoginException;
import java.security.Principal;
import java.util.Set;
import javax.security.auth.login.LoginException;
import java.util.Iterator;
import javax.naming.directory.SearchControls;
import java.util.Hashtable;
import java.util.regex.Matcher;
import javax.naming.ldap.LdapContext;
import java.util.Map;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.Subject;
import com.sun.security.auth.UserPrincipal;
import com.sun.security.auth.LdapPrincipal;
import java.util.regex.Pattern;
import java.util.ResourceBundle;
import jdk.Exported;
import javax.security.auth.spi.LoginModule;

@Exported
public class LdapLoginModule implements LoginModule
{
    private static final ResourceBundle rb;
    private static final String USERNAME_KEY = "javax.security.auth.login.name";
    private static final String PASSWORD_KEY = "javax.security.auth.login.password";
    private static final String USER_PROVIDER = "userProvider";
    private static final String USER_FILTER = "userFilter";
    private static final String AUTHC_IDENTITY = "authIdentity";
    private static final String AUTHZ_IDENTITY = "authzIdentity";
    private static final String USERNAME_TOKEN = "{USERNAME}";
    private static final Pattern USERNAME_PATTERN;
    private String userProvider;
    private String userFilter;
    private String authcIdentity;
    private String authzIdentity;
    private String authzIdentityAttr;
    private boolean useSSL;
    private boolean authFirst;
    private boolean authOnly;
    private boolean useFirstPass;
    private boolean tryFirstPass;
    private boolean storePass;
    private boolean clearPass;
    private boolean debug;
    private boolean succeeded;
    private boolean commitSucceeded;
    private String username;
    private char[] password;
    private LdapPrincipal ldapPrincipal;
    private UserPrincipal userPrincipal;
    private UserPrincipal authzPrincipal;
    private Subject subject;
    private CallbackHandler callbackHandler;
    private Map<String, Object> sharedState;
    private Map<String, ?> options;
    private LdapContext ctx;
    private Matcher identityMatcher;
    private Matcher filterMatcher;
    private Hashtable<String, Object> ldapEnvironment;
    private SearchControls constraints;
    
    public LdapLoginModule() {
        this.authzIdentityAttr = null;
        this.useSSL = true;
        this.authFirst = false;
        this.authOnly = false;
        this.useFirstPass = false;
        this.tryFirstPass = false;
        this.storePass = false;
        this.clearPass = false;
        this.debug = false;
        this.succeeded = false;
        this.commitSucceeded = false;
        this.identityMatcher = null;
        this.filterMatcher = null;
        this.constraints = null;
    }
    
    @Override
    public void initialize(final Subject subject, final CallbackHandler callbackHandler, final Map<String, ?> sharedState, final Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.sharedState = (Map<String, Object>)sharedState;
        this.options = options;
        (this.ldapEnvironment = new Hashtable<String, Object>(9)).put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
        for (final String s : options.keySet()) {
            if (s.indexOf(".") > -1) {
                this.ldapEnvironment.put(s, options.get(s));
            }
        }
        this.userProvider = (String)options.get("userProvider");
        if (this.userProvider != null) {
            this.ldapEnvironment.put("java.naming.provider.url", this.userProvider);
        }
        this.authcIdentity = (String)options.get("authIdentity");
        if (this.authcIdentity != null && this.authcIdentity.indexOf("{USERNAME}") != -1) {
            this.identityMatcher = LdapLoginModule.USERNAME_PATTERN.matcher(this.authcIdentity);
        }
        this.userFilter = (String)options.get("userFilter");
        if (this.userFilter != null) {
            if (this.userFilter.indexOf("{USERNAME}") != -1) {
                this.filterMatcher = LdapLoginModule.USERNAME_PATTERN.matcher(this.userFilter);
            }
            (this.constraints = new SearchControls()).setSearchScope(2);
            this.constraints.setReturningAttributes(new String[0]);
        }
        this.authzIdentity = (String)options.get("authzIdentity");
        if (this.authzIdentity != null && this.authzIdentity.startsWith("{") && this.authzIdentity.endsWith("}")) {
            if (this.constraints != null) {
                this.authzIdentityAttr = this.authzIdentity.substring(1, this.authzIdentity.length() - 1);
                this.constraints.setReturningAttributes(new String[] { this.authzIdentityAttr });
            }
            this.authzIdentity = null;
        }
        if (this.authcIdentity != null) {
            if (this.userFilter != null) {
                this.authFirst = true;
            }
            else {
                this.authOnly = true;
            }
        }
        if ("false".equalsIgnoreCase((String)options.get("useSSL"))) {
            this.useSSL = false;
            this.ldapEnvironment.remove("java.naming.security.protocol");
        }
        else {
            this.ldapEnvironment.put("java.naming.security.protocol", "ssl");
        }
        this.tryFirstPass = "true".equalsIgnoreCase((String)options.get("tryFirstPass"));
        this.useFirstPass = "true".equalsIgnoreCase((String)options.get("useFirstPass"));
        this.storePass = "true".equalsIgnoreCase((String)options.get("storePass"));
        this.clearPass = "true".equalsIgnoreCase((String)options.get("clearPass"));
        this.debug = "true".equalsIgnoreCase((String)options.get("debug"));
        if (this.debug) {
            if (this.authFirst) {
                System.out.println("\t\t[LdapLoginModule] authentication-first mode; " + (this.useSSL ? "SSL enabled" : "SSL disabled"));
            }
            else if (this.authOnly) {
                System.out.println("\t\t[LdapLoginModule] authentication-only mode; " + (this.useSSL ? "SSL enabled" : "SSL disabled"));
            }
            else {
                System.out.println("\t\t[LdapLoginModule] search-first mode; " + (this.useSSL ? "SSL enabled" : "SSL disabled"));
            }
        }
    }
    
    @Override
    public boolean login() throws LoginException {
        if (this.userProvider == null) {
            throw new LoginException("Unable to locate the LDAP directory service");
        }
        if (this.debug) {
            System.out.println("\t\t[LdapLoginModule] user provider: " + this.userProvider);
        }
        Label_0185: {
            if (this.tryFirstPass) {
                try {
                    this.attemptAuthentication(true);
                    this.succeeded = true;
                    if (this.debug) {
                        System.out.println("\t\t[LdapLoginModule] tryFirstPass succeeded");
                    }
                    return true;
                }
                catch (final LoginException ex) {
                    this.cleanState();
                    if (this.debug) {
                        System.out.println("\t\t[LdapLoginModule] tryFirstPass failed: " + ex.toString());
                    }
                    break Label_0185;
                }
            }
            if (this.useFirstPass) {
                try {
                    this.attemptAuthentication(true);
                    this.succeeded = true;
                    if (this.debug) {
                        System.out.println("\t\t[LdapLoginModule] useFirstPass succeeded");
                    }
                    return true;
                }
                catch (final LoginException ex2) {
                    this.cleanState();
                    if (this.debug) {
                        System.out.println("\t\t[LdapLoginModule] useFirstPass failed");
                    }
                    throw ex2;
                }
            }
            try {
                this.attemptAuthentication(false);
                this.succeeded = true;
                if (this.debug) {
                    System.out.println("\t\t[LdapLoginModule] authentication succeeded");
                }
                return true;
            }
            catch (final LoginException ex3) {
                this.cleanState();
                if (this.debug) {
                    System.out.println("\t\t[LdapLoginModule] authentication failed");
                }
                throw ex3;
            }
        }
    }
    
    @Override
    public boolean commit() throws LoginException {
        if (!this.succeeded) {
            return false;
        }
        if (this.subject.isReadOnly()) {
            this.cleanState();
            throw new LoginException("Subject is read-only");
        }
        final Set<Principal> principals = this.subject.getPrincipals();
        if (!principals.contains(this.ldapPrincipal)) {
            principals.add(this.ldapPrincipal);
        }
        if (this.debug) {
            System.out.println("\t\t[LdapLoginModule] added LdapPrincipal \"" + this.ldapPrincipal + "\" to Subject");
        }
        if (!principals.contains(this.userPrincipal)) {
            principals.add(this.userPrincipal);
        }
        if (this.debug) {
            System.out.println("\t\t[LdapLoginModule] added UserPrincipal \"" + this.userPrincipal + "\" to Subject");
        }
        if (this.authzPrincipal != null && !principals.contains(this.authzPrincipal)) {
            principals.add(this.authzPrincipal);
            if (this.debug) {
                System.out.println("\t\t[LdapLoginModule] added UserPrincipal \"" + this.authzPrincipal + "\" to Subject");
            }
        }
        this.cleanState();
        return this.commitSucceeded = true;
    }
    
    @Override
    public boolean abort() throws LoginException {
        if (this.debug) {
            System.out.println("\t\t[LdapLoginModule] aborted authentication");
        }
        if (!this.succeeded) {
            return false;
        }
        if (this.succeeded && !this.commitSucceeded) {
            this.succeeded = false;
            this.cleanState();
            this.ldapPrincipal = null;
            this.userPrincipal = null;
            this.authzPrincipal = null;
        }
        else {
            this.logout();
        }
        return true;
    }
    
    @Override
    public boolean logout() throws LoginException {
        if (this.subject.isReadOnly()) {
            this.cleanState();
            throw new LoginException("Subject is read-only");
        }
        final Set<Principal> principals = this.subject.getPrincipals();
        principals.remove(this.ldapPrincipal);
        principals.remove(this.userPrincipal);
        if (this.authzIdentity != null) {
            principals.remove(this.authzPrincipal);
        }
        this.cleanState();
        this.succeeded = false;
        this.commitSucceeded = false;
        this.ldapPrincipal = null;
        this.userPrincipal = null;
        this.authzPrincipal = null;
        if (this.debug) {
            System.out.println("\t\t[LdapLoginModule] logged out Subject");
        }
        return true;
    }
    
    private void attemptAuthentication(final boolean b) throws LoginException {
        this.getUsernamePassword(b);
        if (this.password == null || this.password.length == 0) {
            throw new FailedLoginException("No password was supplied");
        }
        String s;
        if (this.authFirst || this.authOnly) {
            final String replaceUsernameToken = this.replaceUsernameToken(this.identityMatcher, this.authcIdentity, this.username);
            this.ldapEnvironment.put("java.naming.security.credentials", this.password);
            this.ldapEnvironment.put("java.naming.security.principal", replaceUsernameToken);
            if (this.debug) {
                System.out.println("\t\t[LdapLoginModule] attempting to authenticate user: " + this.username);
            }
            try {
                this.ctx = new InitialLdapContext(this.ldapEnvironment, null);
            }
            catch (final NamingException ex) {
                throw (LoginException)new FailedLoginException("Cannot bind to LDAP server").initCause(ex);
            }
            if (this.userFilter != null) {
                s = this.findUserDN(this.ctx);
            }
            else {
                s = replaceUsernameToken;
            }
        }
        else {
            try {
                this.ctx = new InitialLdapContext(this.ldapEnvironment, null);
            }
            catch (final NamingException ex2) {
                throw (LoginException)new FailedLoginException("Cannot connect to LDAP server").initCause(ex2);
            }
            s = this.findUserDN(this.ctx);
            try {
                this.ctx.addToEnvironment("java.naming.security.authentication", "simple");
                this.ctx.addToEnvironment("java.naming.security.principal", s);
                this.ctx.addToEnvironment("java.naming.security.credentials", this.password);
                if (this.debug) {
                    System.out.println("\t\t[LdapLoginModule] attempting to authenticate user: " + this.username);
                }
                this.ctx.reconnect(null);
            }
            catch (final NamingException ex3) {
                throw (LoginException)new FailedLoginException("Cannot bind to LDAP server").initCause(ex3);
            }
        }
        if (this.storePass && !this.sharedState.containsKey("javax.security.auth.login.name") && !this.sharedState.containsKey("javax.security.auth.login.password")) {
            this.sharedState.put("javax.security.auth.login.name", this.username);
            this.sharedState.put("javax.security.auth.login.password", this.password);
        }
        this.userPrincipal = new UserPrincipal(this.username);
        if (this.authzIdentity != null) {
            this.authzPrincipal = new UserPrincipal(this.authzIdentity);
        }
        try {
            this.ldapPrincipal = new LdapPrincipal(s);
        }
        catch (final InvalidNameException ex4) {
            if (this.debug) {
                System.out.println("\t\t[LdapLoginModule] cannot create LdapPrincipal: bad DN");
            }
            throw (LoginException)new FailedLoginException("Cannot create LdapPrincipal").initCause(ex4);
        }
    }
    
    private String findUserDN(final LdapContext ldapContext) throws LoginException {
        String nameInNamespace = "";
        if (this.userFilter == null) {
            if (this.debug) {
                System.out.println("\t\t[LdapLoginModule] cannot search for entry belonging to user: " + this.username);
            }
            throw new FailedLoginException("Cannot find user's LDAP entry");
        }
        if (this.debug) {
            System.out.println("\t\t[LdapLoginModule] searching for entry belonging to user: " + this.username);
        }
        try {
            final NamingEnumeration<SearchResult> search = ldapContext.search("", this.replaceUsernameToken(this.filterMatcher, this.userFilter, this.escapeUsernameChars()), this.constraints);
            if (search.hasMore()) {
                final SearchResult searchResult = search.next();
                nameInNamespace = searchResult.getNameInNamespace();
                if (this.debug) {
                    System.out.println("\t\t[LdapLoginModule] found entry: " + nameInNamespace);
                }
                if (this.authzIdentityAttr != null) {
                    final Attribute value = searchResult.getAttributes().get(this.authzIdentityAttr);
                    if (value != null) {
                        final Object value2 = value.get();
                        if (value2 instanceof String) {
                            this.authzIdentity = (String)value2;
                        }
                    }
                }
                search.close();
            }
            else if (this.debug) {
                System.out.println("\t\t[LdapLoginModule] user's entry not found");
            }
        }
        catch (final NamingException ex) {}
        if (nameInNamespace.equals("")) {
            throw new FailedLoginException("Cannot find user's LDAP entry");
        }
        return nameInNamespace;
    }
    
    private String escapeUsernameChars() {
        final int length = this.username.length();
        final StringBuilder sb = new StringBuilder(length + 16);
        for (int i = 0; i < length; ++i) {
            final char char1 = this.username.charAt(i);
            switch (char1) {
                case 42: {
                    sb.append("\\\\2A");
                    break;
                }
                case 40: {
                    sb.append("\\\\28");
                    break;
                }
                case 41: {
                    sb.append("\\\\29");
                    break;
                }
                case 92: {
                    sb.append("\\\\5C");
                    break;
                }
                case 0: {
                    sb.append("\\\\00");
                    break;
                }
                default: {
                    sb.append(char1);
                    break;
                }
            }
        }
        return sb.toString();
    }
    
    private String replaceUsernameToken(final Matcher matcher, final String s, final String s2) {
        return (matcher != null) ? matcher.replaceAll(s2) : s;
    }
    
    private void getUsernamePassword(final boolean b) throws LoginException {
        if (b) {
            this.username = this.sharedState.get("javax.security.auth.login.name");
            this.password = this.sharedState.get("javax.security.auth.login.password");
            return;
        }
        if (this.callbackHandler == null) {
            throw new LoginException("No CallbackHandler available to acquire authentication information from the user");
        }
        final Callback[] array = { new NameCallback(LdapLoginModule.rb.getString("username.")), new PasswordCallback(LdapLoginModule.rb.getString("password."), false) };
        try {
            this.callbackHandler.handle(array);
            this.username = ((NameCallback)array[0]).getName();
            final char[] password = ((PasswordCallback)array[1]).getPassword();
            System.arraycopy(password, 0, this.password = new char[password.length], 0, password.length);
            ((PasswordCallback)array[1]).clearPassword();
        }
        catch (final IOException ex) {
            throw new LoginException(ex.toString());
        }
        catch (final UnsupportedCallbackException ex2) {
            throw new LoginException("Error: " + ex2.getCallback().toString() + " not available to acquire authentication information from the user");
        }
    }
    
    private void cleanState() {
        this.username = null;
        if (this.password != null) {
            Arrays.fill(this.password, ' ');
            this.password = null;
        }
        try {
            if (this.ctx != null) {
                this.ctx.close();
            }
        }
        catch (final NamingException ex) {}
        this.ctx = null;
        if (this.clearPass) {
            this.sharedState.remove("javax.security.auth.login.name");
            this.sharedState.remove("javax.security.auth.login.password");
        }
    }
    
    static {
        rb = AccessController.doPrivileged((PrivilegedAction<ResourceBundle>)new PrivilegedAction<ResourceBundle>() {
            @Override
            public ResourceBundle run() {
                return ResourceBundle.getBundle("sun.security.util.AuthResources");
            }
        });
        USERNAME_PATTERN = Pattern.compile("\\{USERNAME\\}");
    }
}
