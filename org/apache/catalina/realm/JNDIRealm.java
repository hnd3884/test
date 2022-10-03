package org.apache.catalina.realm;

import java.util.Collections;
import java.net.URISyntaxException;
import java.net.URI;
import javax.naming.CompositeName;
import org.apache.catalina.LifecycleException;
import javax.net.ssl.SSLSession;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.ExtendedRequest;
import javax.naming.ldap.StartTlsRequest;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.SecureRandom;
import javax.net.ssl.TrustManager;
import javax.net.ssl.KeyManager;
import javax.naming.directory.InitialDirContext;
import java.text.MessageFormat;
import java.util.Hashtable;
import javax.security.auth.login.LoginContext;
import javax.naming.ServiceUnavailableException;
import javax.naming.CommunicationException;
import java.io.IOException;
import javax.naming.directory.Attribute;
import java.util.Iterator;
import java.util.Set;
import javax.naming.Name;
import javax.naming.NameParser;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import javax.naming.AuthenticationException;
import javax.naming.NamingEnumeration;
import javax.naming.directory.SearchResult;
import javax.naming.PartialResultException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.Attributes;
import javax.naming.NameNotFoundException;
import javax.naming.directory.DirContext;
import java.util.ArrayList;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.GSSContext;
import java.security.cert.X509Certificate;
import java.util.List;
import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import java.security.Principal;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.tomcat.util.collections.SynchronizedStack;
import java.util.concurrent.locks.Lock;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.naming.ldap.StartTlsResponse;

public class JNDIRealm extends RealmBase
{
    protected String authentication;
    protected String connectionName;
    protected String connectionPassword;
    protected String connectionURL;
    protected String contextFactory;
    protected String derefAliases;
    public static final String DEREF_ALIASES = "java.naming.ldap.derefAliases";
    @Deprecated
    protected static final String name = "JNDIRealm";
    protected String protocol;
    protected boolean adCompat;
    protected String referrals;
    protected String userBase;
    protected String userSearch;
    private boolean userSearchAsUser;
    protected boolean userSubtree;
    protected String userPassword;
    protected String userRoleAttribute;
    protected String[] userPatternArray;
    protected String userPattern;
    protected String roleBase;
    protected String userRoleName;
    protected String roleName;
    protected String roleSearch;
    protected boolean roleSubtree;
    protected boolean roleNested;
    protected boolean roleSearchAsUser;
    protected String alternateURL;
    protected int connectionAttempt;
    protected String commonRole;
    protected String connectionTimeout;
    protected String readTimeout;
    protected long sizeLimit;
    protected int timeLimit;
    protected boolean useDelegatedCredential;
    protected String spnegoDelegationQop;
    private boolean useStartTls;
    private StartTlsResponse tls;
    private String[] cipherSuitesArray;
    private HostnameVerifier hostnameVerifier;
    private SSLSocketFactory sslSocketFactory;
    private String sslSocketFactoryClassName;
    private String cipherSuites;
    private String hostNameVerifierClassName;
    private String sslProtocol;
    private boolean forceDnHexEscape;
    protected JNDIConnection singleConnection;
    protected final Lock singleConnectionLock;
    protected SynchronizedStack<JNDIConnection> connectionPool;
    protected int connectionPoolSize;
    protected boolean useContextClassLoader;
    
    public JNDIRealm() {
        this.authentication = null;
        this.connectionName = null;
        this.connectionPassword = null;
        this.connectionURL = null;
        this.contextFactory = "com.sun.jndi.ldap.LdapCtxFactory";
        this.derefAliases = null;
        this.protocol = null;
        this.adCompat = false;
        this.referrals = null;
        this.userBase = "";
        this.userSearch = null;
        this.userSearchAsUser = false;
        this.userSubtree = false;
        this.userPassword = null;
        this.userRoleAttribute = null;
        this.userPatternArray = null;
        this.userPattern = null;
        this.roleBase = "";
        this.userRoleName = null;
        this.roleName = null;
        this.roleSearch = null;
        this.roleSubtree = false;
        this.roleNested = false;
        this.roleSearchAsUser = false;
        this.connectionAttempt = 0;
        this.commonRole = null;
        this.connectionTimeout = "5000";
        this.readTimeout = "5000";
        this.sizeLimit = 0L;
        this.timeLimit = 0;
        this.useDelegatedCredential = true;
        this.spnegoDelegationQop = "auth-conf";
        this.useStartTls = false;
        this.tls = null;
        this.cipherSuitesArray = null;
        this.hostnameVerifier = null;
        this.sslSocketFactory = null;
        this.forceDnHexEscape = false;
        this.singleConnection = new JNDIConnection();
        this.singleConnectionLock = new ReentrantLock();
        this.connectionPool = null;
        this.connectionPoolSize = 1;
        this.useContextClassLoader = true;
    }
    
    public boolean getForceDnHexEscape() {
        return this.forceDnHexEscape;
    }
    
    public void setForceDnHexEscape(final boolean forceDnHexEscape) {
        this.forceDnHexEscape = forceDnHexEscape;
    }
    
    public String getAuthentication() {
        return this.authentication;
    }
    
    public void setAuthentication(final String authentication) {
        this.authentication = authentication;
    }
    
    public String getConnectionName() {
        return this.connectionName;
    }
    
    public void setConnectionName(final String connectionName) {
        this.connectionName = connectionName;
    }
    
    public String getConnectionPassword() {
        return this.connectionPassword;
    }
    
    public void setConnectionPassword(final String connectionPassword) {
        this.connectionPassword = connectionPassword;
    }
    
    public String getConnectionURL() {
        return this.connectionURL;
    }
    
    public void setConnectionURL(final String connectionURL) {
        this.connectionURL = connectionURL;
    }
    
    public String getContextFactory() {
        return this.contextFactory;
    }
    
    public void setContextFactory(final String contextFactory) {
        this.contextFactory = contextFactory;
    }
    
    public String getDerefAliases() {
        return this.derefAliases;
    }
    
    public void setDerefAliases(final String derefAliases) {
        this.derefAliases = derefAliases;
    }
    
    public String getProtocol() {
        return this.protocol;
    }
    
    public void setProtocol(final String protocol) {
        this.protocol = protocol;
    }
    
    public boolean getAdCompat() {
        return this.adCompat;
    }
    
    public void setAdCompat(final boolean adCompat) {
        this.adCompat = adCompat;
    }
    
    public String getReferrals() {
        return this.referrals;
    }
    
    public void setReferrals(final String referrals) {
        this.referrals = referrals;
    }
    
    public String getUserBase() {
        return this.userBase;
    }
    
    public void setUserBase(final String userBase) {
        this.userBase = userBase;
    }
    
    public String getUserSearch() {
        return this.userSearch;
    }
    
    public void setUserSearch(final String userSearch) {
        this.userSearch = userSearch;
        this.singleConnection = this.create();
    }
    
    public boolean isUserSearchAsUser() {
        return this.userSearchAsUser;
    }
    
    public void setUserSearchAsUser(final boolean userSearchAsUser) {
        this.userSearchAsUser = userSearchAsUser;
    }
    
    public boolean getUserSubtree() {
        return this.userSubtree;
    }
    
    public void setUserSubtree(final boolean userSubtree) {
        this.userSubtree = userSubtree;
    }
    
    public String getUserRoleName() {
        return this.userRoleName;
    }
    
    public void setUserRoleName(final String userRoleName) {
        this.userRoleName = userRoleName;
    }
    
    public String getRoleBase() {
        return this.roleBase;
    }
    
    public void setRoleBase(final String roleBase) {
        this.roleBase = roleBase;
        this.singleConnection = this.create();
    }
    
    public String getRoleName() {
        return this.roleName;
    }
    
    public void setRoleName(final String roleName) {
        this.roleName = roleName;
    }
    
    public String getRoleSearch() {
        return this.roleSearch;
    }
    
    public void setRoleSearch(final String roleSearch) {
        this.roleSearch = roleSearch;
        this.singleConnection = this.create();
    }
    
    public boolean isRoleSearchAsUser() {
        return this.roleSearchAsUser;
    }
    
    public void setRoleSearchAsUser(final boolean roleSearchAsUser) {
        this.roleSearchAsUser = roleSearchAsUser;
    }
    
    public boolean getRoleSubtree() {
        return this.roleSubtree;
    }
    
    public void setRoleSubtree(final boolean roleSubtree) {
        this.roleSubtree = roleSubtree;
    }
    
    public boolean getRoleNested() {
        return this.roleNested;
    }
    
    public void setRoleNested(final boolean roleNested) {
        this.roleNested = roleNested;
    }
    
    public String getUserPassword() {
        return this.userPassword;
    }
    
    public void setUserPassword(final String userPassword) {
        this.userPassword = userPassword;
    }
    
    public String getUserRoleAttribute() {
        return this.userRoleAttribute;
    }
    
    public void setUserRoleAttribute(final String userRoleAttribute) {
        this.userRoleAttribute = userRoleAttribute;
    }
    
    public String getUserPattern() {
        return this.userPattern;
    }
    
    public void setUserPattern(final String userPattern) {
        this.userPattern = userPattern;
        if (userPattern == null) {
            this.userPatternArray = null;
        }
        else {
            this.userPatternArray = this.parseUserPatternString(userPattern);
            this.singleConnection = this.create();
        }
    }
    
    public String getAlternateURL() {
        return this.alternateURL;
    }
    
    public void setAlternateURL(final String alternateURL) {
        this.alternateURL = alternateURL;
    }
    
    public String getCommonRole() {
        return this.commonRole;
    }
    
    public void setCommonRole(final String commonRole) {
        this.commonRole = commonRole;
    }
    
    public String getConnectionTimeout() {
        return this.connectionTimeout;
    }
    
    public void setConnectionTimeout(final String timeout) {
        this.connectionTimeout = timeout;
    }
    
    public String getReadTimeout() {
        return this.readTimeout;
    }
    
    public void setReadTimeout(final String timeout) {
        this.readTimeout = timeout;
    }
    
    public long getSizeLimit() {
        return this.sizeLimit;
    }
    
    public void setSizeLimit(final long sizeLimit) {
        this.sizeLimit = sizeLimit;
    }
    
    public int getTimeLimit() {
        return this.timeLimit;
    }
    
    public void setTimeLimit(final int timeLimit) {
        this.timeLimit = timeLimit;
    }
    
    public boolean isUseDelegatedCredential() {
        return this.useDelegatedCredential;
    }
    
    public void setUseDelegatedCredential(final boolean useDelegatedCredential) {
        this.useDelegatedCredential = useDelegatedCredential;
    }
    
    public String getSpnegoDelegationQop() {
        return this.spnegoDelegationQop;
    }
    
    public void setSpnegoDelegationQop(final String spnegoDelegationQop) {
        this.spnegoDelegationQop = spnegoDelegationQop;
    }
    
    public boolean getUseStartTls() {
        return this.useStartTls;
    }
    
    public void setUseStartTls(final boolean useStartTls) {
        this.useStartTls = useStartTls;
    }
    
    private String[] getCipherSuitesArray() {
        if (this.cipherSuites == null || this.cipherSuitesArray != null) {
            return this.cipherSuitesArray;
        }
        if (this.cipherSuites.trim().isEmpty()) {
            this.containerLog.warn((Object)JNDIRealm.sm.getString("jndiRealm.emptyCipherSuites"));
            this.cipherSuitesArray = null;
        }
        else {
            this.cipherSuitesArray = this.cipherSuites.trim().split("\\s*,\\s*");
            this.containerLog.debug((Object)JNDIRealm.sm.getString("jndiRealm.cipherSuites", new Object[] { Arrays.toString(this.cipherSuitesArray) }));
        }
        return this.cipherSuitesArray;
    }
    
    public void setCipherSuites(final String suites) {
        this.cipherSuites = suites;
    }
    
    public int getConnectionPoolSize() {
        return this.connectionPoolSize;
    }
    
    public void setConnectionPoolSize(final int connectionPoolSize) {
        this.connectionPoolSize = connectionPoolSize;
    }
    
    public String getHostnameVerifierClassName() {
        if (this.hostnameVerifier == null) {
            return "";
        }
        return this.hostnameVerifier.getClass().getCanonicalName();
    }
    
    public void setHostnameVerifierClassName(final String verifierClassName) {
        if (verifierClassName != null) {
            this.hostNameVerifierClassName = verifierClassName.trim();
        }
        else {
            this.hostNameVerifierClassName = null;
        }
    }
    
    public HostnameVerifier getHostnameVerifier() {
        if (this.hostnameVerifier != null) {
            return this.hostnameVerifier;
        }
        if (this.hostNameVerifierClassName == null || this.hostNameVerifierClassName.equals("")) {
            return null;
        }
        try {
            final Object o = this.constructInstance(this.hostNameVerifierClassName);
            if (o instanceof HostnameVerifier) {
                return this.hostnameVerifier = (HostnameVerifier)o;
            }
            throw new IllegalArgumentException(JNDIRealm.sm.getString("jndiRealm.invalidHostnameVerifier", new Object[] { this.hostNameVerifierClassName }));
        }
        catch (final ReflectiveOperationException | SecurityException e) {
            throw new IllegalArgumentException(JNDIRealm.sm.getString("jndiRealm.invalidHostnameVerifier", new Object[] { this.hostNameVerifierClassName }), e);
        }
    }
    
    public void setSslSocketFactoryClassName(final String factoryClassName) {
        this.sslSocketFactoryClassName = factoryClassName;
    }
    
    public void setSslProtocol(final String protocol) {
        this.sslProtocol = protocol;
    }
    
    private String[] getSupportedSslProtocols() {
        try {
            final SSLContext sslContext = SSLContext.getDefault();
            return sslContext.getSupportedSSLParameters().getProtocols();
        }
        catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException(JNDIRealm.sm.getString("jndiRealm.exception"), e);
        }
    }
    
    private Object constructInstance(final String className) throws ReflectiveOperationException {
        final Class<?> clazz = Class.forName(className);
        return clazz.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
    }
    
    public void setUseContextClassLoader(final boolean useContext) {
        this.useContextClassLoader = useContext;
    }
    
    public boolean isUseContextClassLoader() {
        return this.useContextClassLoader;
    }
    
    @Override
    public Principal authenticate(final String username, final String credentials) {
        ClassLoader ocl = null;
        JNDIConnection connection = null;
        Principal principal = null;
        try {
            if (!this.isUseContextClassLoader()) {
                ocl = Thread.currentThread().getContextClassLoader();
                Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            }
            connection = this.get();
            try {
                principal = this.authenticate(connection, username, credentials);
            }
            catch (final NullPointerException | NamingException e) {
                this.containerLog.info((Object)JNDIRealm.sm.getString("jndiRealm.exception.retry"), (Throwable)e);
                this.close(connection);
                this.closePooledConnections();
                connection = this.get();
                principal = this.authenticate(connection, username, credentials);
            }
            this.release(connection);
            return principal;
        }
        catch (final NamingException e2) {
            this.containerLog.error((Object)JNDIRealm.sm.getString("jndiRealm.exception"), (Throwable)e2);
            this.close(connection);
            this.closePooledConnections();
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug((Object)"Returning null principal.");
            }
            return null;
        }
        finally {
            if (!this.isUseContextClassLoader()) {
                Thread.currentThread().setContextClassLoader(ocl);
            }
        }
    }
    
    public Principal authenticate(final JNDIConnection connection, final String username, final String credentials) throws NamingException {
        if (username == null || username.equals("") || credentials == null || credentials.equals("")) {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug((Object)"username null or empty: returning null principal.");
            }
            return null;
        }
        ClassLoader ocl = null;
        try {
            if (!this.isUseContextClassLoader()) {
                ocl = Thread.currentThread().getContextClassLoader();
                Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            }
            if (this.userPatternArray != null) {
                for (int curUserPattern = 0; curUserPattern < this.userPatternArray.length; ++curUserPattern) {
                    final User user = this.getUser(connection, username, credentials, curUserPattern);
                    if (user != null) {
                        try {
                            if (this.checkCredentials(connection.context, user, credentials)) {
                                final List<String> roles = this.getRoles(connection, user);
                                if (this.containerLog.isDebugEnabled()) {
                                    this.containerLog.debug((Object)("Found roles: " + roles.toString()));
                                }
                                return new GenericPrincipal(username, credentials, roles);
                            }
                        }
                        catch (final InvalidNameException ine) {
                            this.containerLog.warn((Object)JNDIRealm.sm.getString("jndiRealm.exception"), (Throwable)ine);
                        }
                    }
                }
                return null;
            }
            final User user2 = this.getUser(connection, username, credentials);
            if (user2 == null) {
                return null;
            }
            if (!this.checkCredentials(connection.context, user2, credentials)) {
                return null;
            }
            final List<String> roles2 = this.getRoles(connection, user2);
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug((Object)("Found roles: " + roles2.toString()));
            }
            return new GenericPrincipal(username, credentials, roles2);
        }
        finally {
            if (!this.isUseContextClassLoader()) {
                Thread.currentThread().setContextClassLoader(ocl);
            }
        }
    }
    
    @Override
    public Principal authenticate(final String username) {
        ClassLoader ocl = null;
        try {
            if (!this.isUseContextClassLoader()) {
                ocl = Thread.currentThread().getContextClassLoader();
                Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            }
            return super.authenticate(username);
        }
        finally {
            if (!this.isUseContextClassLoader()) {
                Thread.currentThread().setContextClassLoader(ocl);
            }
        }
    }
    
    @Override
    public Principal authenticate(final String username, final String clientDigest, final String nonce, final String nc, final String cnonce, final String qop, final String realm, final String md5a2) {
        ClassLoader ocl = null;
        try {
            if (!this.isUseContextClassLoader()) {
                ocl = Thread.currentThread().getContextClassLoader();
                Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            }
            return super.authenticate(username, clientDigest, nonce, nc, cnonce, qop, realm, md5a2);
        }
        finally {
            if (!this.isUseContextClassLoader()) {
                Thread.currentThread().setContextClassLoader(ocl);
            }
        }
    }
    
    @Override
    public Principal authenticate(final X509Certificate[] certs) {
        ClassLoader ocl = null;
        try {
            if (!this.isUseContextClassLoader()) {
                ocl = Thread.currentThread().getContextClassLoader();
                Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            }
            return super.authenticate(certs);
        }
        finally {
            if (!this.isUseContextClassLoader()) {
                Thread.currentThread().setContextClassLoader(ocl);
            }
        }
    }
    
    @Override
    public Principal authenticate(final GSSContext gssContext, final boolean storeCred) {
        ClassLoader ocl = null;
        try {
            if (!this.isUseContextClassLoader()) {
                ocl = Thread.currentThread().getContextClassLoader();
                Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            }
            return super.authenticate(gssContext, storeCred);
        }
        finally {
            if (!this.isUseContextClassLoader()) {
                Thread.currentThread().setContextClassLoader(ocl);
            }
        }
    }
    
    @Override
    public Principal authenticate(final GSSName gssName, final GSSCredential gssCredential) {
        ClassLoader ocl = null;
        try {
            if (!this.isUseContextClassLoader()) {
                ocl = Thread.currentThread().getContextClassLoader();
                Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            }
            return super.authenticate(gssName, gssCredential);
        }
        finally {
            if (!this.isUseContextClassLoader()) {
                Thread.currentThread().setContextClassLoader(ocl);
            }
        }
    }
    
    protected User getUser(final JNDIConnection connection, final String username) throws NamingException {
        return this.getUser(connection, username, null, -1);
    }
    
    protected User getUser(final JNDIConnection connection, final String username, final String credentials) throws NamingException {
        return this.getUser(connection, username, credentials, -1);
    }
    
    protected User getUser(final JNDIConnection connection, final String username, final String credentials, final int curUserPattern) throws NamingException {
        User user = null;
        final List<String> list = new ArrayList<String>();
        if (this.userPassword != null) {
            list.add(this.userPassword);
        }
        if (this.userRoleName != null) {
            list.add(this.userRoleName);
        }
        if (this.userRoleAttribute != null) {
            list.add(this.userRoleAttribute);
        }
        final String[] attrIds = new String[list.size()];
        list.toArray(attrIds);
        if (this.userPatternArray != null && curUserPattern >= 0) {
            user = this.getUserByPattern(connection, username, credentials, attrIds, curUserPattern);
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug((Object)("Found user by pattern [" + user + "]"));
            }
        }
        else {
            final boolean thisUserSearchAsUser = this.isUserSearchAsUser();
            try {
                if (thisUserSearchAsUser) {
                    this.userCredentialsAdd(connection.context, username, credentials);
                }
                user = this.getUserBySearch(connection, username, attrIds);
            }
            finally {
                if (thisUserSearchAsUser) {
                    this.userCredentialsRemove(connection.context);
                }
            }
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug((Object)("Found user by search [" + user + "]"));
            }
        }
        if (this.userPassword == null && credentials != null && user != null) {
            return new User(user.getUserName(), user.getDN(), credentials, user.getRoles(), user.getUserRoleId());
        }
        return user;
    }
    
    protected User getUserByPattern(final DirContext context, final String username, final String[] attrIds, final String dn) throws NamingException {
        if (attrIds == null || attrIds.length == 0) {
            return new User(username, dn, null, null, null);
        }
        Attributes attrs = null;
        try {
            attrs = context.getAttributes(dn, attrIds);
        }
        catch (final NameNotFoundException e) {
            return null;
        }
        if (attrs == null) {
            return null;
        }
        String password = null;
        if (this.userPassword != null) {
            password = this.getAttributeValue(this.userPassword, attrs);
        }
        String userRoleAttrValue = null;
        if (this.userRoleAttribute != null) {
            userRoleAttrValue = this.getAttributeValue(this.userRoleAttribute, attrs);
        }
        ArrayList<String> roles = null;
        if (this.userRoleName != null) {
            roles = this.addAttributeValues(this.userRoleName, attrs, roles);
        }
        return new User(username, dn, password, roles, userRoleAttrValue);
    }
    
    protected User getUserByPattern(final JNDIConnection connection, final String username, final String credentials, final String[] attrIds, final int curUserPattern) throws NamingException {
        User user = null;
        if (username == null || this.userPatternArray[curUserPattern] == null) {
            return null;
        }
        final String dn = connection.userPatternFormatArray[curUserPattern].format(new String[] { this.doAttributeValueEscaping(username) });
        try {
            user = this.getUserByPattern(connection.context, username, attrIds, dn);
        }
        catch (final NameNotFoundException e) {
            return null;
        }
        catch (final NamingException e2) {
            try {
                this.userCredentialsAdd(connection.context, dn, credentials);
                user = this.getUserByPattern(connection.context, username, attrIds, dn);
            }
            finally {
                this.userCredentialsRemove(connection.context);
            }
        }
        return user;
    }
    
    protected User getUserBySearch(final JNDIConnection connection, final String username, String[] attrIds) throws NamingException {
        if (username == null || connection.userSearchFormat == null) {
            return null;
        }
        final String filter = connection.userSearchFormat.format(new String[] { this.doFilterEscaping(username) });
        final SearchControls constraints = new SearchControls();
        if (this.userSubtree) {
            constraints.setSearchScope(2);
        }
        else {
            constraints.setSearchScope(1);
        }
        constraints.setCountLimit(this.sizeLimit);
        constraints.setTimeLimit(this.timeLimit);
        if (attrIds == null) {
            attrIds = new String[0];
        }
        constraints.setReturningAttributes(attrIds);
        final NamingEnumeration<SearchResult> results = connection.context.search(this.userBase, filter, constraints);
        try {
            try {
                if (results == null || !results.hasMore()) {
                    return null;
                }
            }
            catch (final PartialResultException ex) {
                if (!this.adCompat) {
                    throw ex;
                }
                return null;
            }
            final SearchResult result = results.next();
            try {
                if (results.hasMore()) {
                    if (this.containerLog.isInfoEnabled()) {
                        this.containerLog.info((Object)JNDIRealm.sm.getString("jndiRealm.multipleEntries", new Object[] { username }));
                    }
                    return null;
                }
            }
            catch (final PartialResultException ex2) {
                if (!this.adCompat) {
                    throw ex2;
                }
            }
            final String dn = this.getDistinguishedName(connection.context, this.userBase, result);
            if (this.containerLog.isTraceEnabled()) {
                this.containerLog.trace((Object)("  entry found for " + username + " with dn " + dn));
            }
            final Attributes attrs = result.getAttributes();
            if (attrs == null) {
                return null;
            }
            String password = null;
            if (this.userPassword != null) {
                password = this.getAttributeValue(this.userPassword, attrs);
            }
            String userRoleAttrValue = null;
            if (this.userRoleAttribute != null) {
                userRoleAttrValue = this.getAttributeValue(this.userRoleAttribute, attrs);
            }
            ArrayList<String> roles = null;
            if (this.userRoleName != null) {
                roles = this.addAttributeValues(this.userRoleName, attrs, roles);
            }
            return new User(username, dn, password, roles, userRoleAttrValue);
        }
        finally {
            if (results != null) {
                results.close();
            }
        }
    }
    
    protected boolean checkCredentials(final DirContext context, final User user, final String credentials) throws NamingException {
        boolean validated = false;
        if (this.userPassword == null) {
            validated = this.bindAsUser(context, user, credentials);
        }
        else {
            validated = this.compareCredentials(context, user, credentials);
        }
        if (this.containerLog.isTraceEnabled()) {
            if (validated) {
                this.containerLog.trace((Object)JNDIRealm.sm.getString("jndiRealm.authenticateSuccess", new Object[] { user.getUserName() }));
            }
            else {
                this.containerLog.trace((Object)JNDIRealm.sm.getString("jndiRealm.authenticateFailure", new Object[] { user.getUserName() }));
            }
        }
        return validated;
    }
    
    protected boolean compareCredentials(final DirContext context, final User info, final String credentials) throws NamingException {
        if (this.containerLog.isTraceEnabled()) {
            this.containerLog.trace((Object)"  validating credentials");
        }
        if (info == null || credentials == null) {
            return false;
        }
        final String password = info.getPassword();
        return this.getCredentialHandler().matches(credentials, password);
    }
    
    protected boolean bindAsUser(final DirContext context, final User user, final String credentials) throws NamingException {
        if (credentials == null || user == null) {
            return false;
        }
        final String dn = user.getDN();
        if (dn == null) {
            return false;
        }
        if (this.containerLog.isTraceEnabled()) {
            this.containerLog.trace((Object)"  validating credentials by binding as the user");
        }
        this.userCredentialsAdd(context, dn, credentials);
        boolean validated = false;
        try {
            if (this.containerLog.isTraceEnabled()) {
                this.containerLog.trace((Object)("  binding as " + dn));
            }
            context.getAttributes("", null);
            validated = true;
        }
        catch (final AuthenticationException e) {
            if (this.containerLog.isTraceEnabled()) {
                this.containerLog.trace((Object)"  bind attempt failed");
            }
        }
        this.userCredentialsRemove(context);
        return validated;
    }
    
    private void userCredentialsAdd(final DirContext context, final String dn, final String credentials) throws NamingException {
        context.addToEnvironment("java.naming.security.principal", dn);
        context.addToEnvironment("java.naming.security.credentials", credentials);
    }
    
    private void userCredentialsRemove(final DirContext context) throws NamingException {
        if (this.connectionName != null) {
            context.addToEnvironment("java.naming.security.principal", this.connectionName);
        }
        else {
            context.removeFromEnvironment("java.naming.security.principal");
        }
        if (this.connectionPassword != null) {
            context.addToEnvironment("java.naming.security.credentials", this.connectionPassword);
        }
        else {
            context.removeFromEnvironment("java.naming.security.credentials");
        }
    }
    
    protected List<String> getRoles(final JNDIConnection connection, final User user) throws NamingException {
        if (user == null) {
            return null;
        }
        final String dn = user.getDN();
        final String username = user.getUserName();
        final String userRoleId = user.getUserRoleId();
        if (dn == null || username == null) {
            return null;
        }
        if (this.containerLog.isTraceEnabled()) {
            this.containerLog.trace((Object)("  getRoles(" + dn + ")"));
        }
        final List<String> list = new ArrayList<String>();
        final List<String> userRoles = user.getRoles();
        if (userRoles != null) {
            list.addAll(userRoles);
        }
        if (this.commonRole != null) {
            list.add(this.commonRole);
        }
        if (this.containerLog.isTraceEnabled()) {
            this.containerLog.trace((Object)("  Found " + list.size() + " user internal roles"));
            this.containerLog.trace((Object)("  Found user internal roles " + list.toString()));
        }
        if (connection.roleFormat == null || this.roleName == null) {
            return list;
        }
        String filter = connection.roleFormat.format(new String[] { this.doFilterEscaping(dn), this.doFilterEscaping(this.doAttributeValueEscaping(username)), this.doFilterEscaping(this.doAttributeValueEscaping(userRoleId)) });
        final SearchControls controls = new SearchControls();
        if (this.roleSubtree) {
            controls.setSearchScope(2);
        }
        else {
            controls.setSearchScope(1);
        }
        controls.setReturningAttributes(new String[] { this.roleName });
        String base = null;
        if (connection.roleBaseFormat != null) {
            final NameParser np = connection.context.getNameParser("");
            final Name name = np.parse(dn);
            final String[] nameParts = new String[name.size()];
            for (int i = 0; i < name.size(); ++i) {
                nameParts[i] = convertToHexEscape(name.get(i));
            }
            base = connection.roleBaseFormat.format(nameParts);
        }
        else {
            base = "";
        }
        NamingEnumeration<SearchResult> results = this.searchAsUser(connection.context, user, base, filter, controls, this.isRoleSearchAsUser());
        if (results == null) {
            return list;
        }
        final Map<String, String> groupMap = new HashMap<String, String>();
        try {
            while (results.hasMore()) {
                final SearchResult result = results.next();
                final Attributes attrs = result.getAttributes();
                if (attrs == null) {
                    continue;
                }
                final String dname = this.getDistinguishedName(connection.context, base, result);
                final String name2 = this.getAttributeValue(this.roleName, attrs);
                if (name2 == null || dname == null) {
                    continue;
                }
                groupMap.put(dname, name2);
            }
        }
        catch (final PartialResultException ex) {
            if (!this.adCompat) {
                throw ex;
            }
        }
        finally {
            results.close();
        }
        if (this.containerLog.isTraceEnabled()) {
            final Set<Map.Entry<String, String>> entries = groupMap.entrySet();
            this.containerLog.trace((Object)("  Found " + entries.size() + " direct roles"));
            for (final Map.Entry<String, String> entry : entries) {
                this.containerLog.trace((Object)("  Found direct role " + entry.getKey() + " -> " + entry.getValue()));
            }
        }
        if (this.getRoleNested()) {
            Map<String, String> newThisRound;
            for (Map<String, String> newGroups = new HashMap<String, String>(groupMap); !newGroups.isEmpty(); newGroups = newThisRound) {
                newThisRound = new HashMap<String, String>();
                for (final Map.Entry<String, String> group : newGroups.entrySet()) {
                    filter = connection.roleFormat.format(new String[] { this.doFilterEscaping(group.getKey()), this.doFilterEscaping(this.doAttributeValueEscaping(group.getValue())), this.doFilterEscaping(this.doAttributeValueEscaping(group.getValue())) });
                    if (this.containerLog.isTraceEnabled()) {
                        this.containerLog.trace((Object)("Perform a nested group search with base " + this.roleBase + " and filter " + filter));
                    }
                    results = this.searchAsUser(connection.context, user, base, filter, controls, this.isRoleSearchAsUser());
                    try {
                        while (results.hasMore()) {
                            final SearchResult result2 = results.next();
                            final Attributes attrs2 = result2.getAttributes();
                            if (attrs2 == null) {
                                continue;
                            }
                            final String dname2 = this.getDistinguishedName(connection.context, this.roleBase, result2);
                            final String name3 = this.getAttributeValue(this.roleName, attrs2);
                            if (name3 == null || dname2 == null || groupMap.keySet().contains(dname2)) {
                                continue;
                            }
                            groupMap.put(dname2, name3);
                            newThisRound.put(dname2, name3);
                            if (!this.containerLog.isTraceEnabled()) {
                                continue;
                            }
                            this.containerLog.trace((Object)("  Found nested role " + dname2 + " -> " + name3));
                        }
                    }
                    catch (final PartialResultException ex2) {
                        if (!this.adCompat) {
                            throw ex2;
                        }
                        continue;
                    }
                    finally {
                        results.close();
                    }
                }
            }
        }
        list.addAll(groupMap.values());
        return list;
    }
    
    private NamingEnumeration<SearchResult> searchAsUser(final DirContext context, final User user, final String base, final String filter, final SearchControls controls, final boolean searchAsUser) throws NamingException {
        NamingEnumeration<SearchResult> results;
        try {
            if (searchAsUser) {
                this.userCredentialsAdd(context, user.getDN(), user.getPassword());
            }
            results = context.search(base, filter, controls);
        }
        finally {
            if (searchAsUser) {
                this.userCredentialsRemove(context);
            }
        }
        return results;
    }
    
    private String getAttributeValue(final String attrId, final Attributes attrs) throws NamingException {
        if (this.containerLog.isTraceEnabled()) {
            this.containerLog.trace((Object)("  retrieving attribute " + attrId));
        }
        if (attrId == null || attrs == null) {
            return null;
        }
        final Attribute attr = attrs.get(attrId);
        if (attr == null) {
            return null;
        }
        final Object value = attr.get();
        if (value == null) {
            return null;
        }
        String valueString = null;
        if (value instanceof byte[]) {
            valueString = new String((byte[])value);
        }
        else {
            valueString = value.toString();
        }
        return valueString;
    }
    
    private ArrayList<String> addAttributeValues(final String attrId, final Attributes attrs, ArrayList<String> values) throws NamingException {
        if (this.containerLog.isTraceEnabled()) {
            this.containerLog.trace((Object)("  retrieving values for attribute " + attrId));
        }
        if (attrId == null || attrs == null) {
            return values;
        }
        if (values == null) {
            values = new ArrayList<String>();
        }
        final Attribute attr = attrs.get(attrId);
        if (attr == null) {
            return values;
        }
        final NamingEnumeration<?> e = attr.getAll();
        try {
            while (e.hasMore()) {
                final String value = (String)e.next();
                values.add(value);
            }
        }
        catch (final PartialResultException ex) {
            if (!this.adCompat) {
                throw ex;
            }
        }
        finally {
            e.close();
        }
        return values;
    }
    
    protected void close(final JNDIConnection connection) {
        if (connection == null || connection.context == null) {
            if (this.connectionPool == null) {
                this.singleConnectionLock.unlock();
            }
            return;
        }
        if (this.tls != null) {
            try {
                this.tls.close();
            }
            catch (final IOException e) {
                this.containerLog.error((Object)JNDIRealm.sm.getString("jndiRealm.tlsClose"), (Throwable)e);
            }
        }
        try {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug((Object)"Closing directory context");
            }
            connection.context.close();
        }
        catch (final NamingException e2) {
            this.containerLog.error((Object)JNDIRealm.sm.getString("jndiRealm.close"), (Throwable)e2);
        }
        connection.context = null;
        if (this.connectionPool == null) {
            this.singleConnectionLock.unlock();
        }
    }
    
    protected void closePooledConnections() {
        if (this.connectionPool != null) {
            synchronized (this.connectionPool) {
                JNDIConnection connection = null;
                while ((connection = (JNDIConnection)this.connectionPool.pop()) != null) {
                    this.close(connection);
                }
            }
        }
    }
    
    @Deprecated
    @Override
    protected String getName() {
        return "JNDIRealm";
    }
    
    @Override
    protected String getPassword(final String username) {
        final String userPassword = this.getUserPassword();
        if (userPassword == null || userPassword.isEmpty()) {
            return null;
        }
        JNDIConnection connection = null;
        User user = null;
        try {
            connection = this.get();
            try {
                user = this.getUser(connection, username, null);
            }
            catch (final NullPointerException | NamingException e) {
                this.containerLog.info((Object)JNDIRealm.sm.getString("jndiRealm.exception.retry"), (Throwable)e);
                this.close(connection);
                this.closePooledConnections();
                connection = this.get();
                user = this.getUser(connection, username, null);
            }
            this.release(connection);
            if (user == null) {
                return null;
            }
            return user.getPassword();
        }
        catch (final NamingException e2) {
            this.containerLog.error((Object)JNDIRealm.sm.getString("jndiRealm.exception"), (Throwable)e2);
            return null;
        }
    }
    
    @Override
    protected Principal getPrincipal(final String username) {
        return this.getPrincipal(username, null);
    }
    
    @Override
    protected Principal getPrincipal(final GSSName gssName, final GSSCredential gssCredential) {
        String name = gssName.toString();
        if (this.isStripRealmForGss()) {
            final int i = name.indexOf(64);
            if (i > 0) {
                name = name.substring(0, i);
            }
        }
        return this.getPrincipal(name, gssCredential);
    }
    
    @Override
    protected Principal getPrincipal(final String username, final GSSCredential gssCredential) {
        JNDIConnection connection = null;
        Principal principal = null;
        try {
            connection = this.get();
            try {
                principal = this.getPrincipal(connection, username, gssCredential);
            }
            catch (final CommunicationException | ServiceUnavailableException e) {
                this.containerLog.info((Object)JNDIRealm.sm.getString("jndiRealm.exception.retry"), (Throwable)e);
                this.close(connection);
                this.closePooledConnections();
                connection = this.get();
                principal = this.getPrincipal(connection, username, gssCredential);
            }
            this.release(connection);
            return principal;
        }
        catch (final NamingException e) {
            this.containerLog.error((Object)JNDIRealm.sm.getString("jndiRealm.exception"), (Throwable)e);
            this.close(connection);
            this.closePooledConnections();
            return null;
        }
    }
    
    protected Principal getPrincipal(final JNDIConnection connection, final String username, final GSSCredential gssCredential) throws NamingException {
        User user = null;
        List<String> roles = null;
        Hashtable<?, ?> preservedEnvironment = null;
        final DirContext context = connection.context;
        try {
            if (gssCredential != null && this.isUseDelegatedCredential()) {
                preservedEnvironment = context.getEnvironment();
                context.addToEnvironment("java.naming.security.authentication", "GSSAPI");
                context.addToEnvironment("javax.security.sasl.server.authentication", "true");
                context.addToEnvironment("javax.security.sasl.qop", this.spnegoDelegationQop);
            }
            user = this.getUser(connection, username);
            if (user != null) {
                roles = this.getRoles(connection, user);
            }
        }
        finally {
            if (gssCredential != null && this.isUseDelegatedCredential()) {
                this.restoreEnvironmentParameter(context, "java.naming.security.authentication", preservedEnvironment);
                this.restoreEnvironmentParameter(context, "javax.security.sasl.server.authentication", preservedEnvironment);
                this.restoreEnvironmentParameter(context, "javax.security.sasl.qop", preservedEnvironment);
            }
        }
        if (user != null) {
            return new GenericPrincipal(user.getUserName(), user.getPassword(), roles, null, null, gssCredential);
        }
        return null;
    }
    
    private void restoreEnvironmentParameter(final DirContext context, final String parameterName, final Hashtable<?, ?> preservedEnvironment) {
        try {
            context.removeFromEnvironment(parameterName);
            if (preservedEnvironment != null && preservedEnvironment.containsKey(parameterName)) {
                context.addToEnvironment(parameterName, preservedEnvironment.get(parameterName));
            }
        }
        catch (final NamingException ex) {}
    }
    
    protected JNDIConnection get() throws NamingException {
        JNDIConnection connection = null;
        if (this.connectionPool != null) {
            connection = (JNDIConnection)this.connectionPool.pop();
            if (connection == null) {
                connection = this.create();
            }
        }
        else {
            this.singleConnectionLock.lock();
            connection = this.singleConnection;
        }
        if (connection.context == null) {
            this.open(connection);
        }
        return connection;
    }
    
    protected void release(final JNDIConnection connection) {
        if (this.connectionPool != null) {
            if (!this.connectionPool.push((Object)connection)) {
                this.close(connection);
            }
        }
        else {
            this.singleConnectionLock.unlock();
        }
    }
    
    protected JNDIConnection create() {
        final JNDIConnection connection = new JNDIConnection();
        if (this.userSearch != null) {
            connection.userSearchFormat = new MessageFormat(this.userSearch);
        }
        if (this.userPattern != null) {
            final int len = this.userPatternArray.length;
            connection.userPatternFormatArray = new MessageFormat[len];
            for (int i = 0; i < len; ++i) {
                connection.userPatternFormatArray[i] = new MessageFormat(this.userPatternArray[i]);
            }
        }
        if (this.roleBase != null) {
            connection.roleBaseFormat = new MessageFormat(this.roleBase);
        }
        if (this.roleSearch != null) {
            connection.roleFormat = new MessageFormat(this.roleSearch);
        }
        return connection;
    }
    
    protected void open(final JNDIConnection connection) throws NamingException {
        try {
            connection.context = this.createDirContext(this.getDirectoryContextEnvironment());
        }
        catch (final Exception e) {
            if (this.alternateURL == null || this.alternateURL.length() == 0) {
                throw e;
            }
            this.connectionAttempt = 1;
            this.containerLog.info((Object)JNDIRealm.sm.getString("jndiRealm.exception.retry"), (Throwable)e);
            connection.context = this.createDirContext(this.getDirectoryContextEnvironment());
        }
        finally {
            this.connectionAttempt = 0;
        }
    }
    
    @Override
    public boolean isAvailable() {
        return this.connectionPool != null || this.singleConnection.context != null;
    }
    
    private DirContext createDirContext(final Hashtable<String, String> env) throws NamingException {
        if (this.useStartTls) {
            return this.createTlsDirContext(env);
        }
        return new InitialDirContext(env);
    }
    
    private SSLSocketFactory getSSLSocketFactory() {
        if (this.sslSocketFactory != null) {
            return this.sslSocketFactory;
        }
        SSLSocketFactory result;
        if (this.sslSocketFactoryClassName != null && !this.sslSocketFactoryClassName.trim().equals("")) {
            result = this.createSSLSocketFactoryFromClassName(this.sslSocketFactoryClassName);
        }
        else {
            result = this.createSSLContextFactoryFromProtocol(this.sslProtocol);
        }
        return this.sslSocketFactory = result;
    }
    
    private SSLSocketFactory createSSLSocketFactoryFromClassName(final String className) {
        try {
            final Object o = this.constructInstance(className);
            if (o instanceof SSLSocketFactory) {
                return this.sslSocketFactory;
            }
            throw new IllegalArgumentException(JNDIRealm.sm.getString("jndiRealm.invalidSslSocketFactory", new Object[] { className }));
        }
        catch (final ReflectiveOperationException | SecurityException e) {
            throw new IllegalArgumentException(JNDIRealm.sm.getString("jndiRealm.invalidSslSocketFactory", new Object[] { className }), e);
        }
    }
    
    private SSLSocketFactory createSSLContextFactoryFromProtocol(final String protocol) {
        try {
            SSLContext sslContext;
            if (protocol != null) {
                sslContext = SSLContext.getInstance(protocol);
                sslContext.init(null, null, null);
            }
            else {
                sslContext = SSLContext.getDefault();
            }
            return sslContext.getSocketFactory();
        }
        catch (final NoSuchAlgorithmException | KeyManagementException e) {
            final List<String> allowedProtocols = Arrays.asList(this.getSupportedSslProtocols());
            throw new IllegalArgumentException(JNDIRealm.sm.getString("jndiRealm.invalidSslProtocol", new Object[] { protocol, allowedProtocols }), e);
        }
    }
    
    private DirContext createTlsDirContext(final Hashtable<String, String> env) throws NamingException {
        final Map<String, Object> savedEnv = new HashMap<String, Object>();
        for (final String key : Arrays.asList("java.naming.security.authentication", "java.naming.security.credentials", "java.naming.security.principal", "java.naming.security.protocol")) {
            final Object entry = env.remove(key);
            if (entry != null) {
                savedEnv.put(key, entry);
            }
        }
        LdapContext result = null;
        try {
            result = new InitialLdapContext(env, null);
            this.tls = (StartTlsResponse)result.extendedOperation(new StartTlsRequest());
            if (this.getHostnameVerifier() != null) {
                this.tls.setHostnameVerifier(this.getHostnameVerifier());
            }
            if (this.getCipherSuitesArray() != null) {
                this.tls.setEnabledCipherSuites(this.getCipherSuitesArray());
            }
            try {
                final SSLSession negotiate = this.tls.negotiate(this.getSSLSocketFactory());
                this.containerLog.debug((Object)JNDIRealm.sm.getString("jndiRealm.negotiatedTls", new Object[] { negotiate.getProtocol() }));
            }
            catch (final IOException e) {
                throw new NamingException(e.getMessage());
            }
        }
        finally {
            if (result != null) {
                for (final Map.Entry<String, Object> savedEntry : savedEnv.entrySet()) {
                    result.addToEnvironment(savedEntry.getKey(), savedEntry.getValue());
                }
            }
        }
        return result;
    }
    
    protected Hashtable<String, String> getDirectoryContextEnvironment() {
        final Hashtable<String, String> env = new Hashtable<String, String>();
        if (this.containerLog.isDebugEnabled() && this.connectionAttempt == 0) {
            this.containerLog.debug((Object)("Connecting to URL " + this.connectionURL));
        }
        else if (this.containerLog.isDebugEnabled() && this.connectionAttempt > 0) {
            this.containerLog.debug((Object)("Connecting to URL " + this.alternateURL));
        }
        env.put("java.naming.factory.initial", this.contextFactory);
        if (this.connectionName != null) {
            env.put("java.naming.security.principal", this.connectionName);
        }
        if (this.connectionPassword != null) {
            env.put("java.naming.security.credentials", this.connectionPassword);
        }
        if (this.connectionURL != null && this.connectionAttempt == 0) {
            env.put("java.naming.provider.url", this.connectionURL);
        }
        else if (this.alternateURL != null && this.connectionAttempt > 0) {
            env.put("java.naming.provider.url", this.alternateURL);
        }
        if (this.authentication != null) {
            env.put("java.naming.security.authentication", this.authentication);
        }
        if (this.protocol != null) {
            env.put("java.naming.security.protocol", this.protocol);
        }
        if (this.referrals != null) {
            env.put("java.naming.referral", this.referrals);
        }
        if (this.derefAliases != null) {
            env.put("java.naming.ldap.derefAliases", this.derefAliases);
        }
        if (this.connectionTimeout != null) {
            env.put("com.sun.jndi.ldap.connect.timeout", this.connectionTimeout);
        }
        if (this.readTimeout != null) {
            env.put("com.sun.jndi.ldap.read.timeout", this.readTimeout);
        }
        return env;
    }
    
    @Override
    protected void startInternal() throws LifecycleException {
        if (this.connectionPoolSize != 1) {
            this.connectionPool = (SynchronizedStack<JNDIConnection>)new SynchronizedStack(128, this.connectionPoolSize);
        }
        ClassLoader ocl = null;
        JNDIConnection connection = null;
        try {
            if (!this.isUseContextClassLoader()) {
                ocl = Thread.currentThread().getContextClassLoader();
                Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            }
            connection = this.get();
        }
        catch (final NamingException e) {
            this.containerLog.error((Object)JNDIRealm.sm.getString("jndiRealm.open"), (Throwable)e);
        }
        finally {
            this.release(connection);
            if (!this.isUseContextClassLoader()) {
                Thread.currentThread().setContextClassLoader(ocl);
            }
        }
        super.startInternal();
    }
    
    @Override
    protected void stopInternal() throws LifecycleException {
        super.stopInternal();
        if (this.connectionPool == null) {
            this.singleConnectionLock.lock();
            this.close(this.singleConnection);
        }
        else {
            this.closePooledConnections();
            this.connectionPool = null;
        }
    }
    
    protected String[] parseUserPatternString(final String userPatternString) {
        if (userPatternString == null) {
            return null;
        }
        final List<String> pathList = new ArrayList<String>();
        int startParenLoc = userPatternString.indexOf(40);
        if (startParenLoc == -1) {
            return new String[] { userPatternString };
        }
        for (int startingPoint = 0; startParenLoc > -1; startParenLoc = userPatternString.indexOf(40, startingPoint)) {
            int endParenLoc = 0;
            while (userPatternString.charAt(startParenLoc + 1) == '|' || (startParenLoc != 0 && userPatternString.charAt(startParenLoc - 1) == '\\')) {
                startParenLoc = userPatternString.indexOf(40, startParenLoc + 1);
            }
            for (endParenLoc = userPatternString.indexOf(41, startParenLoc + 1); userPatternString.charAt(endParenLoc - 1) == '\\'; endParenLoc = userPatternString.indexOf(41, endParenLoc + 1)) {}
            final String nextPathPart = userPatternString.substring(startParenLoc + 1, endParenLoc);
            pathList.add(nextPathPart);
            startingPoint = endParenLoc + 1;
        }
        return pathList.toArray(new String[0]);
    }
    
    @Deprecated
    protected String doRFC2254Encoding(final String inString) {
        return this.doFilterEscaping(inString);
    }
    
    protected String doFilterEscaping(final String inString) {
        if (inString == null) {
            return null;
        }
        final StringBuilder buf = new StringBuilder(inString.length());
        for (int i = 0; i < inString.length(); ++i) {
            final char c = inString.charAt(i);
            switch (c) {
                case '\\': {
                    buf.append("\\5c");
                    break;
                }
                case '*': {
                    buf.append("\\2a");
                    break;
                }
                case '(': {
                    buf.append("\\28");
                    break;
                }
                case ')': {
                    buf.append("\\29");
                    break;
                }
                case '\0': {
                    buf.append("\\00");
                    break;
                }
                default: {
                    buf.append(c);
                    break;
                }
            }
        }
        return buf.toString();
    }
    
    protected String getDistinguishedName(final DirContext context, final String base, final SearchResult result) throws NamingException {
        final String resultName = result.getName();
        Name name;
        if (result.isRelative()) {
            if (this.containerLog.isTraceEnabled()) {
                this.containerLog.trace((Object)("  search returned relative name: " + resultName));
            }
            final NameParser parser = context.getNameParser("");
            final Name contextName = parser.parse(context.getNameInNamespace());
            final Name baseName = parser.parse(base);
            final Name entryName = parser.parse(new CompositeName(resultName).get(0));
            name = contextName.addAll(baseName);
            name = name.addAll(entryName);
        }
        else {
            if (this.containerLog.isTraceEnabled()) {
                this.containerLog.trace((Object)("  search returned absolute name: " + resultName));
            }
            try {
                final NameParser parser = context.getNameParser("");
                final URI userNameUri = new URI(resultName);
                final String pathComponent = userNameUri.getPath();
                if (pathComponent.length() < 1) {
                    throw new InvalidNameException("Search returned unparseable absolute name: " + resultName);
                }
                name = parser.parse(pathComponent.substring(1));
            }
            catch (final URISyntaxException e) {
                throw new InvalidNameException("Search returned unparseable absolute name: " + resultName);
            }
        }
        if (this.getForceDnHexEscape()) {
            return convertToHexEscape(name.toString());
        }
        return name.toString();
    }
    
    protected String doAttributeValueEscaping(final String input) {
        if (input == null) {
            return null;
        }
        final int len = input.length();
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < len; ++i) {
            final char c = input.charAt(i);
            switch (c) {
                case ' ': {
                    if (i == 0 || i == len - 1) {
                        result.append("\\20");
                        break;
                    }
                    result.append(c);
                    break;
                }
                case '#': {
                    if (i == 0) {
                        result.append("\\23");
                        break;
                    }
                    result.append(c);
                    break;
                }
                case '\"': {
                    result.append("\\22");
                    break;
                }
                case '+': {
                    result.append("\\2B");
                    break;
                }
                case ',': {
                    result.append("\\2C");
                    break;
                }
                case ';': {
                    result.append("\\3B");
                    break;
                }
                case '<': {
                    result.append("\\3C");
                    break;
                }
                case '>': {
                    result.append("\\3E");
                    break;
                }
                case '\\': {
                    result.append("\\5C");
                    break;
                }
                case '\0': {
                    result.append("\\00");
                    break;
                }
                default: {
                    result.append(c);
                    break;
                }
            }
        }
        return result.toString();
    }
    
    protected static String convertToHexEscape(final String input) {
        if (input.indexOf(92) == -1) {
            return input;
        }
        final StringBuilder result = new StringBuilder(input.length() + 6);
        boolean previousSlash = false;
        for (int i = 0; i < input.length(); ++i) {
            final char c = input.charAt(i);
            if (previousSlash) {
                switch (c) {
                    case ' ': {
                        result.append("\\20");
                        break;
                    }
                    case '\"': {
                        result.append("\\22");
                        break;
                    }
                    case '#': {
                        result.append("\\23");
                        break;
                    }
                    case '+': {
                        result.append("\\2B");
                        break;
                    }
                    case ',': {
                        result.append("\\2C");
                        break;
                    }
                    case ';': {
                        result.append("\\3B");
                        break;
                    }
                    case '<': {
                        result.append("\\3C");
                        break;
                    }
                    case '=': {
                        result.append("\\3D");
                        break;
                    }
                    case '>': {
                        result.append("\\3E");
                        break;
                    }
                    case '\\': {
                        result.append("\\5C");
                        break;
                    }
                    default: {
                        result.append('\\');
                        result.append(c);
                        break;
                    }
                }
                previousSlash = false;
            }
            else if (c == '\\') {
                previousSlash = true;
            }
            else {
                result.append(c);
            }
        }
        if (previousSlash) {
            result.append('\\');
        }
        return result.toString();
    }
    
    protected static class User
    {
        private final String username;
        private final String dn;
        private final String password;
        private final List<String> roles;
        private final String userRoleId;
        
        public User(final String username, final String dn, final String password, final List<String> roles, final String userRoleId) {
            this.username = username;
            this.dn = dn;
            this.password = password;
            if (roles == null) {
                this.roles = Collections.emptyList();
            }
            else {
                this.roles = Collections.unmodifiableList((List<? extends String>)roles);
            }
            this.userRoleId = userRoleId;
        }
        
        public String getUserName() {
            return this.username;
        }
        
        public String getDN() {
            return this.dn;
        }
        
        public String getPassword() {
            return this.password;
        }
        
        public List<String> getRoles() {
            return this.roles;
        }
        
        public String getUserRoleId() {
            return this.userRoleId;
        }
    }
    
    protected static class JNDIConnection
    {
        protected MessageFormat userSearchFormat;
        protected MessageFormat[] userPatternFormatArray;
        protected MessageFormat roleBaseFormat;
        protected MessageFormat roleFormat;
        protected DirContext context;
        
        protected JNDIConnection() {
            this.userSearchFormat = null;
            this.userPatternFormatArray = null;
            this.roleBaseFormat = null;
            this.roleFormat = null;
            this.context = null;
        }
    }
}
