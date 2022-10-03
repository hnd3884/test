package org.apache.catalina.realm;

import org.apache.juli.logging.LogFactory;
import java.util.Iterator;
import java.security.NoSuchAlgorithmException;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.buf.HexUtils;
import java.security.MessageDigest;
import org.apache.catalina.Service;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.Server;
import org.apache.tomcat.util.buf.B2CConverter;
import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.util.SessionConfig;
import javax.servlet.annotation.ServletSecurity;
import org.apache.catalina.Wrapper;
import java.io.IOException;
import org.apache.catalina.connector.Response;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import java.util.ArrayList;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Request;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSContext;
import java.security.cert.X509Certificate;
import org.apache.tomcat.util.security.MD5Encoder;
import org.apache.tomcat.util.security.ConcurrentMessageDigest;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.security.Principal;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import org.apache.tomcat.util.res.StringManager;
import org.apache.catalina.CredentialHandler;
import org.apache.catalina.Container;
import java.util.List;
import org.apache.juli.logging.Log;
import org.apache.catalina.GSSRealm;
import org.apache.catalina.util.LifecycleMBeanBase;

public abstract class RealmBase extends LifecycleMBeanBase implements GSSRealm
{
    private static final Log log;
    private static final List<Class<? extends DigestCredentialHandlerBase>> credentialHandlerClasses;
    protected Container container;
    protected Log containerLog;
    private CredentialHandler credentialHandler;
    protected static final StringManager sm;
    protected final PropertyChangeSupport support;
    protected boolean validate;
    protected String x509UsernameRetrieverClassName;
    protected X509UsernameRetriever x509UsernameRetriever;
    protected AllRolesMode allRolesMode;
    protected boolean stripRealmForGss;
    private int transportGuaranteeRedirectStatus;
    protected String realmPath;
    
    public RealmBase() {
        this.container = null;
        this.containerLog = null;
        this.support = new PropertyChangeSupport(this);
        this.validate = true;
        this.allRolesMode = AllRolesMode.STRICT_MODE;
        this.stripRealmForGss = true;
        this.transportGuaranteeRedirectStatus = 302;
        this.realmPath = "/realm0";
    }
    
    public int getTransportGuaranteeRedirectStatus() {
        return this.transportGuaranteeRedirectStatus;
    }
    
    public void setTransportGuaranteeRedirectStatus(final int transportGuaranteeRedirectStatus) {
        this.transportGuaranteeRedirectStatus = transportGuaranteeRedirectStatus;
    }
    
    @Override
    public CredentialHandler getCredentialHandler() {
        return this.credentialHandler;
    }
    
    @Override
    public void setCredentialHandler(final CredentialHandler credentialHandler) {
        this.credentialHandler = credentialHandler;
    }
    
    @Override
    public Container getContainer() {
        return this.container;
    }
    
    @Override
    public void setContainer(final Container container) {
        final Container oldContainer = this.container;
        this.container = container;
        this.support.firePropertyChange("container", oldContainer, this.container);
    }
    
    public String getAllRolesMode() {
        return this.allRolesMode.toString();
    }
    
    public void setAllRolesMode(final String allRolesMode) {
        this.allRolesMode = AllRolesMode.toMode(allRolesMode);
    }
    
    public boolean getValidate() {
        return this.validate;
    }
    
    public void setValidate(final boolean validate) {
        this.validate = validate;
    }
    
    public String getX509UsernameRetrieverClassName() {
        return this.x509UsernameRetrieverClassName;
    }
    
    public void setX509UsernameRetrieverClassName(final String className) {
        this.x509UsernameRetrieverClassName = className;
    }
    
    public boolean isStripRealmForGss() {
        return this.stripRealmForGss;
    }
    
    public void setStripRealmForGss(final boolean stripRealmForGss) {
        this.stripRealmForGss = stripRealmForGss;
    }
    
    @Override
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        this.support.addPropertyChangeListener(listener);
    }
    
    @Override
    public Principal authenticate(final String username) {
        if (username == null) {
            return null;
        }
        if (this.containerLog.isTraceEnabled()) {
            this.containerLog.trace((Object)RealmBase.sm.getString("realmBase.authenticateSuccess", new Object[] { username }));
        }
        return this.getPrincipal(username);
    }
    
    @Override
    public Principal authenticate(final String username, final String credentials) {
        if (username == null || credentials == null) {
            if (this.containerLog.isTraceEnabled()) {
                this.containerLog.trace((Object)RealmBase.sm.getString("realmBase.authenticateFailure", new Object[] { username }));
            }
            return null;
        }
        final String serverCredentials = this.getPassword(username);
        if (serverCredentials == null) {
            this.getCredentialHandler().mutate(credentials);
            if (this.containerLog.isTraceEnabled()) {
                this.containerLog.trace((Object)RealmBase.sm.getString("realmBase.authenticateFailure", new Object[] { username }));
            }
            return null;
        }
        final boolean validated = this.getCredentialHandler().matches(credentials, serverCredentials);
        if (validated) {
            if (this.containerLog.isTraceEnabled()) {
                this.containerLog.trace((Object)RealmBase.sm.getString("realmBase.authenticateSuccess", new Object[] { username }));
            }
            return this.getPrincipal(username);
        }
        if (this.containerLog.isTraceEnabled()) {
            this.containerLog.trace((Object)RealmBase.sm.getString("realmBase.authenticateFailure", new Object[] { username }));
        }
        return null;
    }
    
    @Override
    public Principal authenticate(final String username, final String clientDigest, final String nonce, final String nc, final String cnonce, final String qop, final String realm, final String md5a2) {
        String md5a3 = this.getDigest(username, realm);
        if (md5a3 == null) {
            return null;
        }
        md5a3 = md5a3.toLowerCase(Locale.ENGLISH);
        String serverDigestValue;
        if (qop == null) {
            serverDigestValue = md5a3 + ":" + nonce + ":" + md5a2;
        }
        else {
            serverDigestValue = md5a3 + ":" + nonce + ":" + nc + ":" + cnonce + ":" + qop + ":" + md5a2;
        }
        byte[] valueBytes = null;
        try {
            valueBytes = serverDigestValue.getBytes(this.getDigestCharset());
        }
        catch (final UnsupportedEncodingException uee) {
            RealmBase.log.error((Object)("Illegal digestEncoding: " + this.getDigestEncoding()), (Throwable)uee);
            throw new IllegalArgumentException(uee.getMessage());
        }
        final String serverDigest = MD5Encoder.encode(ConcurrentMessageDigest.digestMD5(new byte[][] { valueBytes }));
        if (RealmBase.log.isDebugEnabled()) {
            RealmBase.log.debug((Object)("Digest : " + clientDigest + " Username:" + username + " ClientDigest:" + clientDigest + " nonce:" + nonce + " nc:" + nc + " cnonce:" + cnonce + " qop:" + qop + " realm:" + realm + "md5a2:" + md5a2 + " Server digest:" + serverDigest));
        }
        if (serverDigest.equals(clientDigest)) {
            return this.getPrincipal(username);
        }
        return null;
    }
    
    @Override
    public Principal authenticate(final X509Certificate[] certs) {
        if (certs == null || certs.length < 1) {
            return null;
        }
        if (RealmBase.log.isDebugEnabled()) {
            RealmBase.log.debug((Object)"Authenticating client certificate chain");
        }
        if (this.validate) {
            for (final X509Certificate cert : certs) {
                if (RealmBase.log.isDebugEnabled()) {
                    RealmBase.log.debug((Object)(" Checking validity for '" + cert.getSubjectDN().getName() + "'"));
                }
                try {
                    cert.checkValidity();
                }
                catch (final Exception e) {
                    if (RealmBase.log.isDebugEnabled()) {
                        RealmBase.log.debug((Object)"  Validity exception", (Throwable)e);
                    }
                    return null;
                }
            }
        }
        return this.getPrincipal(certs[0]);
    }
    
    @Override
    public Principal authenticate(final GSSContext gssContext, final boolean storeCred) {
        if (gssContext.isEstablished()) {
            GSSName gssName = null;
            try {
                gssName = gssContext.getSrcName();
            }
            catch (final GSSException e) {
                RealmBase.log.warn((Object)RealmBase.sm.getString("realmBase.gssNameFail"), (Throwable)e);
            }
            if (gssName != null) {
                GSSCredential gssCredential = null;
                if (storeCred) {
                    if (gssContext.getCredDelegState()) {
                        try {
                            gssCredential = gssContext.getDelegCred();
                        }
                        catch (final GSSException e2) {
                            RealmBase.log.warn((Object)RealmBase.sm.getString("realmBase.delegatedCredentialFail", new Object[] { gssName }), (Throwable)e2);
                        }
                    }
                    else if (RealmBase.log.isDebugEnabled()) {
                        RealmBase.log.debug((Object)RealmBase.sm.getString("realmBase.credentialNotDelegated", new Object[] { gssName }));
                    }
                }
                return this.getPrincipal(gssName, gssCredential);
            }
        }
        else {
            RealmBase.log.error((Object)RealmBase.sm.getString("realmBase.gssContextNotEstablished"));
        }
        return null;
    }
    
    @Override
    public Principal authenticate(final GSSName gssName, final GSSCredential gssCredential) {
        if (gssName == null) {
            return null;
        }
        return this.getPrincipal(gssName, gssCredential);
    }
    
    @Override
    public void backgroundProcess() {
    }
    
    @Override
    public SecurityConstraint[] findSecurityConstraints(final Request request, final Context context) {
        ArrayList<SecurityConstraint> results = null;
        final SecurityConstraint[] constraints = context.findConstraints();
        if (constraints == null || constraints.length == 0) {
            if (RealmBase.log.isDebugEnabled()) {
                RealmBase.log.debug((Object)"  No applicable constraints defined");
            }
            return null;
        }
        String uri = request.getRequestPathMB().toString();
        if (uri == null || uri.length() == 0) {
            uri = "/";
        }
        final String method = request.getMethod();
        boolean found = false;
        for (int i = 0; i < constraints.length; ++i) {
            final SecurityCollection[] collections = constraints[i].findCollections();
            if (collections != null) {
                if (RealmBase.log.isDebugEnabled()) {
                    RealmBase.log.debug((Object)("  Checking constraint '" + constraints[i] + "' against " + method + " " + uri + " --> " + constraints[i].included(uri, method)));
                }
                for (final SecurityCollection securityCollection : collections) {
                    final String[] patterns = securityCollection.findPatterns();
                    if (patterns != null) {
                        for (final String pattern : patterns) {
                            if (uri.equals(pattern) || (pattern.length() == 0 && uri.equals("/"))) {
                                found = true;
                                if (securityCollection.findMethod(method)) {
                                    if (results == null) {
                                        results = new ArrayList<SecurityConstraint>();
                                    }
                                    results.add(constraints[i]);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (found) {
            return this.resultsToArray(results);
        }
        int longest = -1;
        for (int i = 0; i < constraints.length; ++i) {
            final SecurityCollection[] collection = constraints[i].findCollections();
            if (collection != null) {
                if (RealmBase.log.isDebugEnabled()) {
                    RealmBase.log.debug((Object)("  Checking constraint '" + constraints[i] + "' against " + method + " " + uri + " --> " + constraints[i].included(uri, method)));
                }
                for (final SecurityCollection securityCollection2 : collection) {
                    final String[] patterns2 = securityCollection2.findPatterns();
                    if (patterns2 != null) {
                        boolean matched = false;
                        int length = -1;
                        for (final String pattern2 : patterns2) {
                            if (pattern2.startsWith("/") && pattern2.endsWith("/*") && pattern2.length() >= longest) {
                                if (pattern2.length() == 2) {
                                    matched = true;
                                    length = pattern2.length();
                                }
                                else if (pattern2.regionMatches(0, uri, 0, pattern2.length() - 1) || (pattern2.length() - 2 == uri.length() && pattern2.regionMatches(0, uri, 0, pattern2.length() - 2))) {
                                    matched = true;
                                    length = pattern2.length();
                                }
                            }
                        }
                        if (matched) {
                            if (length > longest) {
                                found = false;
                                if (results != null) {
                                    results.clear();
                                }
                                longest = length;
                            }
                            if (securityCollection2.findMethod(method)) {
                                found = true;
                                if (results == null) {
                                    results = new ArrayList<SecurityConstraint>();
                                }
                                results.add(constraints[i]);
                            }
                        }
                    }
                }
            }
        }
        if (found) {
            return this.resultsToArray(results);
        }
        for (int i = 0; i < constraints.length; ++i) {
            final SecurityCollection[] collection = constraints[i].findCollections();
            if (collection != null) {
                if (RealmBase.log.isDebugEnabled()) {
                    RealmBase.log.debug((Object)("  Checking constraint '" + constraints[i] + "' against " + method + " " + uri + " --> " + constraints[i].included(uri, method)));
                }
                boolean matched2 = false;
                int pos = -1;
                for (int j = 0; j < collection.length; ++j) {
                    final String[] patterns = collection[j].findPatterns();
                    if (patterns != null) {
                        for (int k = 0; k < patterns.length && !matched2; ++k) {
                            final String pattern3 = patterns[k];
                            if (pattern3.startsWith("*.")) {
                                final int slash = uri.lastIndexOf(47);
                                final int dot = uri.lastIndexOf(46);
                                if (slash >= 0 && dot > slash && dot != uri.length() - 1 && uri.length() - dot == pattern3.length() - 1 && pattern3.regionMatches(1, uri, dot, uri.length() - dot)) {
                                    matched2 = true;
                                    pos = j;
                                }
                            }
                        }
                    }
                }
                if (matched2) {
                    found = true;
                    if (collection[pos].findMethod(method)) {
                        if (results == null) {
                            results = new ArrayList<SecurityConstraint>();
                        }
                        results.add(constraints[i]);
                    }
                }
            }
        }
        if (found) {
            return this.resultsToArray(results);
        }
        for (int i = 0; i < constraints.length; ++i) {
            final SecurityCollection[] collection = constraints[i].findCollections();
            if (collection != null) {
                if (RealmBase.log.isDebugEnabled()) {
                    RealmBase.log.debug((Object)("  Checking constraint '" + constraints[i] + "' against " + method + " " + uri + " --> " + constraints[i].included(uri, method)));
                }
                for (final SecurityCollection securityCollection2 : collection) {
                    final String[] patterns2 = securityCollection2.findPatterns();
                    if (patterns2 != null) {
                        boolean matched = false;
                        for (final String pattern4 : patterns2) {
                            if (pattern4.equals("/")) {
                                matched = true;
                                break;
                            }
                        }
                        if (matched) {
                            if (results == null) {
                                results = new ArrayList<SecurityConstraint>();
                            }
                            results.add(constraints[i]);
                        }
                    }
                }
            }
        }
        if (results == null && RealmBase.log.isDebugEnabled()) {
            RealmBase.log.debug((Object)"  No applicable constraint located");
        }
        return this.resultsToArray(results);
    }
    
    private SecurityConstraint[] resultsToArray(final ArrayList<SecurityConstraint> results) {
        if (results == null || results.size() == 0) {
            return null;
        }
        final SecurityConstraint[] array = new SecurityConstraint[results.size()];
        results.toArray(array);
        return array;
    }
    
    @Override
    public boolean hasResourcePermission(final Request request, final Response response, final SecurityConstraint[] constraints, final Context context) throws IOException {
        if (constraints == null || constraints.length == 0) {
            return true;
        }
        final Principal principal = request.getPrincipal();
        boolean status = false;
        boolean denyfromall = false;
        for (final SecurityConstraint constraint : constraints) {
            String[] roles;
            if (constraint.getAllRoles()) {
                roles = request.getContext().findSecurityRoles();
            }
            else {
                roles = constraint.findAuthRoles();
            }
            if (roles == null) {
                roles = new String[0];
            }
            if (RealmBase.log.isDebugEnabled()) {
                RealmBase.log.debug((Object)("  Checking roles " + principal));
            }
            if (constraint.getAuthenticatedUsers() && principal != null) {
                if (RealmBase.log.isDebugEnabled()) {
                    RealmBase.log.debug((Object)"Passing all authenticated users");
                }
                status = true;
            }
            else if (roles.length == 0 && !constraint.getAllRoles() && !constraint.getAuthenticatedUsers()) {
                if (constraint.getAuthConstraint()) {
                    if (RealmBase.log.isDebugEnabled()) {
                        RealmBase.log.debug((Object)"No roles");
                    }
                    status = false;
                    denyfromall = true;
                    break;
                }
                if (RealmBase.log.isDebugEnabled()) {
                    RealmBase.log.debug((Object)"Passing all access");
                }
                status = true;
            }
            else if (principal == null) {
                if (RealmBase.log.isDebugEnabled()) {
                    RealmBase.log.debug((Object)"  No user authenticated, cannot grant access");
                }
            }
            else {
                for (final String role : roles) {
                    if (this.hasRole(request.getWrapper(), principal, role)) {
                        status = true;
                        if (RealmBase.log.isDebugEnabled()) {
                            RealmBase.log.debug((Object)("Role found:  " + role));
                        }
                    }
                    else if (RealmBase.log.isDebugEnabled()) {
                        RealmBase.log.debug((Object)("No role found:  " + role));
                    }
                }
            }
        }
        if (!denyfromall && this.allRolesMode != AllRolesMode.STRICT_MODE && !status && principal != null) {
            if (RealmBase.log.isDebugEnabled()) {
                RealmBase.log.debug((Object)("Checking for all roles mode: " + this.allRolesMode));
            }
            for (final SecurityConstraint constraint : constraints) {
                if (constraint.getAllRoles()) {
                    if (this.allRolesMode == AllRolesMode.AUTH_ONLY_MODE) {
                        if (RealmBase.log.isDebugEnabled()) {
                            RealmBase.log.debug((Object)"Granting access for role-name=*, auth-only");
                        }
                        status = true;
                        break;
                    }
                    final String[] roles = request.getContext().findSecurityRoles();
                    if (roles.length == 0 && this.allRolesMode == AllRolesMode.STRICT_AUTH_ONLY_MODE) {
                        if (RealmBase.log.isDebugEnabled()) {
                            RealmBase.log.debug((Object)"Granting access for role-name=*, strict auth-only");
                        }
                        status = true;
                        break;
                    }
                }
            }
        }
        if (!status) {
            response.sendError(403, RealmBase.sm.getString("realmBase.forbidden"));
        }
        return status;
    }
    
    @Override
    public boolean hasRole(final Wrapper wrapper, final Principal principal, String role) {
        if (wrapper != null) {
            final String realRole = wrapper.findSecurityReference(role);
            if (realRole != null) {
                role = realRole;
            }
        }
        if (principal == null || role == null) {
            return false;
        }
        final boolean result = this.hasRoleInternal(principal, role);
        if (RealmBase.log.isDebugEnabled()) {
            final String name = principal.getName();
            if (result) {
                RealmBase.log.debug((Object)RealmBase.sm.getString("realmBase.hasRoleSuccess", new Object[] { name, role }));
            }
            else {
                RealmBase.log.debug((Object)RealmBase.sm.getString("realmBase.hasRoleFailure", new Object[] { name, role }));
            }
        }
        return result;
    }
    
    protected boolean hasRoleInternal(final Principal principal, final String role) {
        if (!(principal instanceof GenericPrincipal)) {
            return false;
        }
        final GenericPrincipal gp = (GenericPrincipal)principal;
        return gp.hasRole(role);
    }
    
    @Override
    public boolean hasUserDataPermission(final Request request, final Response response, final SecurityConstraint[] constraints) throws IOException {
        if (constraints == null || constraints.length == 0) {
            if (RealmBase.log.isDebugEnabled()) {
                RealmBase.log.debug((Object)"  No applicable security constraint defined");
            }
            return true;
        }
        for (final SecurityConstraint constraint : constraints) {
            final String userConstraint = constraint.getUserConstraint();
            if (userConstraint == null) {
                if (RealmBase.log.isDebugEnabled()) {
                    RealmBase.log.debug((Object)"  No applicable user data constraint defined");
                }
                return true;
            }
            if (userConstraint.equals(ServletSecurity.TransportGuarantee.NONE.name())) {
                if (RealmBase.log.isDebugEnabled()) {
                    RealmBase.log.debug((Object)"  User data constraint has no restrictions");
                }
                return true;
            }
        }
        if (request.getRequest().isSecure()) {
            if (RealmBase.log.isDebugEnabled()) {
                RealmBase.log.debug((Object)"  User data constraint already satisfied");
            }
            return true;
        }
        final int redirectPort = request.getConnector().getRedirectPort();
        if (redirectPort <= 0) {
            if (RealmBase.log.isDebugEnabled()) {
                RealmBase.log.debug((Object)"  SSL redirect is disabled");
            }
            response.sendError(403, request.getRequestURI());
            return false;
        }
        final StringBuilder file = new StringBuilder();
        final String protocol = "https";
        final String host = request.getServerName();
        file.append(protocol).append("://").append(host);
        if (redirectPort != 443) {
            file.append(':').append(redirectPort);
        }
        file.append(request.getRequestURI());
        final String requestedSessionId = request.getRequestedSessionId();
        if (requestedSessionId != null && request.isRequestedSessionIdFromURL()) {
            file.append(';');
            file.append(SessionConfig.getSessionUriParamName(request.getContext()));
            file.append('=');
            file.append(requestedSessionId);
        }
        final String queryString = request.getQueryString();
        if (queryString != null) {
            file.append('?');
            file.append(queryString);
        }
        if (RealmBase.log.isDebugEnabled()) {
            RealmBase.log.debug((Object)("  Redirecting to " + file.toString()));
        }
        response.sendRedirect(file.toString(), this.transportGuaranteeRedirectStatus);
        return false;
    }
    
    @Override
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        this.support.removePropertyChangeListener(listener);
    }
    
    @Override
    public boolean isAvailable() {
        return true;
    }
    
    @Override
    protected void initInternal() throws LifecycleException {
        super.initInternal();
        if (this.container != null) {
            this.containerLog = this.container.getLogger();
        }
        this.x509UsernameRetriever = createUsernameRetriever(this.x509UsernameRetrieverClassName);
    }
    
    @Override
    protected void startInternal() throws LifecycleException {
        if (this.credentialHandler == null) {
            this.credentialHandler = new MessageDigestCredentialHandler();
        }
        this.setState(LifecycleState.STARTING);
    }
    
    @Override
    protected void stopInternal() throws LifecycleException {
        this.setState(LifecycleState.STOPPING);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Realm[");
        sb.append(this.getName());
        sb.append(']');
        return sb.toString();
    }
    
    protected boolean hasMessageDigest() {
        final CredentialHandler ch = this.credentialHandler;
        return ch instanceof MessageDigestCredentialHandler && ((MessageDigestCredentialHandler)ch).getAlgorithm() != null;
    }
    
    protected String getDigest(final String username, final String realmName) {
        if (this.hasMessageDigest()) {
            return this.getPassword(username);
        }
        final String digestValue = username + ":" + realmName + ":" + this.getPassword(username);
        byte[] valueBytes = null;
        try {
            valueBytes = digestValue.getBytes(this.getDigestCharset());
        }
        catch (final UnsupportedEncodingException uee) {
            RealmBase.log.error((Object)("Illegal digestEncoding: " + this.getDigestEncoding()), (Throwable)uee);
            throw new IllegalArgumentException(uee.getMessage());
        }
        return MD5Encoder.encode(ConcurrentMessageDigest.digestMD5(new byte[][] { valueBytes }));
    }
    
    private String getDigestEncoding() {
        final CredentialHandler ch = this.credentialHandler;
        if (ch instanceof MessageDigestCredentialHandler) {
            return ((MessageDigestCredentialHandler)ch).getEncoding();
        }
        return null;
    }
    
    private Charset getDigestCharset() throws UnsupportedEncodingException {
        final String charset = this.getDigestEncoding();
        if (charset == null) {
            return StandardCharsets.ISO_8859_1;
        }
        return B2CConverter.getCharset(charset);
    }
    
    @Deprecated
    protected abstract String getName();
    
    protected abstract String getPassword(final String p0);
    
    protected Principal getPrincipal(final X509Certificate usercert) {
        final String username = this.x509UsernameRetriever.getUsername(usercert);
        if (RealmBase.log.isDebugEnabled()) {
            RealmBase.log.debug((Object)RealmBase.sm.getString("realmBase.gotX509Username", new Object[] { username }));
        }
        return this.getPrincipal(username);
    }
    
    protected abstract Principal getPrincipal(final String p0);
    
    @Deprecated
    protected Principal getPrincipal(final String username, final GSSCredential gssCredential) {
        final Principal p = this.getPrincipal(username);
        if (p instanceof GenericPrincipal) {
            ((GenericPrincipal)p).setGssCredential(gssCredential);
        }
        return p;
    }
    
    protected Principal getPrincipal(final GSSName gssName, final GSSCredential gssCredential) {
        String name = gssName.toString();
        if (this.isStripRealmForGss()) {
            final int i = name.indexOf(64);
            if (i > 0) {
                name = name.substring(0, i);
            }
        }
        final Principal p = this.getPrincipal(name);
        if (p instanceof GenericPrincipal) {
            ((GenericPrincipal)p).setGssCredential(gssCredential);
        }
        return p;
    }
    
    protected Server getServer() {
        Container c = this.container;
        if (c instanceof Context) {
            c = c.getParent();
        }
        if (c instanceof Host) {
            c = c.getParent();
        }
        if (c instanceof Engine) {
            final Service s = ((Engine)c).getService();
            if (s != null) {
                return s.getServer();
            }
        }
        return null;
    }
    
    @Deprecated
    public static final String Digest(final String credentials, final String algorithm, final String encoding) {
        try {
            final MessageDigest md = (MessageDigest)MessageDigest.getInstance(algorithm).clone();
            if (encoding == null) {
                md.update(credentials.getBytes());
            }
            else {
                md.update(credentials.getBytes(encoding));
            }
            return HexUtils.toHexString(md.digest());
        }
        catch (final Exception ex) {
            RealmBase.log.error((Object)ex);
            return credentials;
        }
    }
    
    public static void main(final String[] args) {
        int saltLength = -1;
        int iterations = -1;
        int keyLength = -1;
        String encoding = Charset.defaultCharset().name();
        String algorithm = null;
        String handlerClassName = null;
        if (args.length == 0) {
            usage();
            return;
        }
        int argIndex;
        for (argIndex = 0; args.length > argIndex + 2 && args[argIndex].length() == 2 && args[argIndex].charAt(0) == '-'; argIndex += 2) {
            switch (args[argIndex].charAt(1)) {
                case 'a': {
                    algorithm = args[argIndex + 1];
                    break;
                }
                case 'e': {
                    encoding = args[argIndex + 1];
                    break;
                }
                case 'i': {
                    iterations = Integer.parseInt(args[argIndex + 1]);
                    break;
                }
                case 's': {
                    saltLength = Integer.parseInt(args[argIndex + 1]);
                    break;
                }
                case 'k': {
                    keyLength = Integer.parseInt(args[argIndex + 1]);
                    break;
                }
                case 'h': {
                    handlerClassName = args[argIndex + 1];
                    break;
                }
                default: {
                    usage();
                    return;
                }
            }
        }
        if (algorithm == null && handlerClassName == null) {
            algorithm = "SHA-512";
        }
        CredentialHandler handler = null;
        if (handlerClassName == null) {
            for (final Class<? extends DigestCredentialHandlerBase> clazz : RealmBase.credentialHandlerClasses) {
                try {
                    handler = (CredentialHandler)clazz.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
                    if (IntrospectionUtils.setProperty((Object)handler, "algorithm", algorithm)) {
                        break;
                    }
                    continue;
                }
                catch (final ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        else {
            try {
                final Class<?> clazz2 = Class.forName(handlerClassName);
                handler = (DigestCredentialHandlerBase)clazz2.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
                IntrospectionUtils.setProperty((Object)handler, "algorithm", algorithm);
            }
            catch (final ReflectiveOperationException e2) {
                throw new RuntimeException(e2);
            }
        }
        if (handler == null) {
            throw new RuntimeException(new NoSuchAlgorithmException(algorithm));
        }
        IntrospectionUtils.setProperty((Object)handler, "encoding", encoding);
        if (iterations > 0) {
            IntrospectionUtils.setProperty((Object)handler, "iterations", Integer.toString(iterations));
        }
        if (saltLength > -1) {
            IntrospectionUtils.setProperty((Object)handler, "saltLength", Integer.toString(saltLength));
        }
        if (keyLength > 0) {
            IntrospectionUtils.setProperty((Object)handler, "keyLength", Integer.toString(keyLength));
        }
        while (argIndex < args.length) {
            final String credential = args[argIndex];
            System.out.print(credential + ":");
            System.out.println(handler.mutate(credential));
            ++argIndex;
        }
    }
    
    private static void usage() {
        System.out.println("Usage: RealmBase [-a <algorithm>] [-e <encoding>] [-i <iterations>] [-s <salt-length>] [-k <key-length>] [-h <handler-class-name>] <credentials>");
    }
    
    public String getObjectNameKeyProperties() {
        final StringBuilder keyProperties = new StringBuilder("type=Realm");
        keyProperties.append(this.getRealmSuffix());
        keyProperties.append(this.container.getMBeanKeyProperties());
        return keyProperties.toString();
    }
    
    public String getDomainInternal() {
        return this.container.getDomain();
    }
    
    public String getRealmPath() {
        return this.realmPath;
    }
    
    public void setRealmPath(final String theRealmPath) {
        this.realmPath = theRealmPath;
    }
    
    protected String getRealmSuffix() {
        return ",realmPath=" + this.getRealmPath();
    }
    
    private static X509UsernameRetriever createUsernameRetriever(final String className) throws LifecycleException {
        if (null == className || className.trim().isEmpty()) {
            return new X509SubjectDnRetriever();
        }
        try {
            final Class<? extends X509UsernameRetriever> clazz = (Class<? extends X509UsernameRetriever>)Class.forName(className);
            return (X509UsernameRetriever)clazz.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
        }
        catch (final ReflectiveOperationException e) {
            throw new LifecycleException(RealmBase.sm.getString("realmBase.createUsernameRetriever.newInstance", new Object[] { className }), e);
        }
        catch (final ClassCastException e2) {
            throw new LifecycleException(RealmBase.sm.getString("realmBase.createUsernameRetriever.ClassCastException", new Object[] { className }), e2);
        }
    }
    
    @Override
    public String[] getRoles(final Principal principal) {
        if (principal instanceof GenericPrincipal) {
            return ((GenericPrincipal)principal).getRoles();
        }
        final String className = principal.getClass().getSimpleName();
        throw new IllegalStateException(RealmBase.sm.getString("realmBase.cannotGetRoles", new Object[] { className }));
    }
    
    static {
        log = LogFactory.getLog((Class)RealmBase.class);
        (credentialHandlerClasses = new ArrayList<Class<? extends DigestCredentialHandlerBase>>()).add(MessageDigestCredentialHandler.class);
        RealmBase.credentialHandlerClasses.add(SecretKeyCredentialHandler.class);
        sm = StringManager.getManager((Class)RealmBase.class);
    }
    
    protected static class AllRolesMode
    {
        private final String name;
        public static final AllRolesMode STRICT_MODE;
        public static final AllRolesMode AUTH_ONLY_MODE;
        public static final AllRolesMode STRICT_AUTH_ONLY_MODE;
        
        static AllRolesMode toMode(final String name) {
            AllRolesMode mode;
            if (name.equalsIgnoreCase(AllRolesMode.STRICT_MODE.name)) {
                mode = AllRolesMode.STRICT_MODE;
            }
            else if (name.equalsIgnoreCase(AllRolesMode.AUTH_ONLY_MODE.name)) {
                mode = AllRolesMode.AUTH_ONLY_MODE;
            }
            else {
                if (!name.equalsIgnoreCase(AllRolesMode.STRICT_AUTH_ONLY_MODE.name)) {
                    throw new IllegalStateException("Unknown mode, must be one of: strict, authOnly, strictAuthOnly");
                }
                mode = AllRolesMode.STRICT_AUTH_ONLY_MODE;
            }
            return mode;
        }
        
        private AllRolesMode(final String name) {
            this.name = name;
        }
        
        @Override
        public boolean equals(final Object o) {
            boolean equals = false;
            if (o instanceof AllRolesMode) {
                final AllRolesMode mode = (AllRolesMode)o;
                equals = this.name.equals(mode.name);
            }
            return equals;
        }
        
        @Override
        public int hashCode() {
            return this.name.hashCode();
        }
        
        @Override
        public String toString() {
            return this.name;
        }
        
        static {
            STRICT_MODE = new AllRolesMode("strict");
            AUTH_ONLY_MODE = new AllRolesMode("authOnly");
            STRICT_AUTH_ONLY_MODE = new AllRolesMode("strictAuthOnly");
        }
    }
}
