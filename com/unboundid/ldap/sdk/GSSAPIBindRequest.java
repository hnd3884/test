package com.unboundid.ldap.sdk;

import java.util.Iterator;
import javax.security.sasl.RealmCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.Callback;
import java.util.Collection;
import com.unboundid.util.InternalUseOnly;
import javax.security.sasl.SaslClient;
import java.util.Map;
import javax.security.sasl.Sasl;
import java.util.HashMap;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import java.util.logging.Level;
import com.unboundid.util.DebugType;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.File;
import java.util.ArrayList;
import com.unboundid.util.Validator;
import java.util.Set;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.security.PrivilegedExceptionAction;
import javax.security.auth.callback.CallbackHandler;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class GSSAPIBindRequest extends SASLBindRequest implements CallbackHandler, PrivilegedExceptionAction<Object>
{
    public static final String GSSAPI_MECHANISM_NAME = "GSSAPI";
    private static final String PROPERTY_KDC_ADDRESS = "java.security.krb5.kdc";
    private static final String PROPERTY_REALM = "java.security.krb5.realm";
    private static final String PROPERTY_CONFIG_FILE = "java.security.auth.login.config";
    private static final String PROPERTY_SUBJECT_CREDS_ONLY = "javax.security.auth.useSubjectCredsOnly";
    private static final String DEFAULT_CONFIG_FILE;
    private static final String DEFAULT_KDC_ADDRESS;
    private static final String DEFAULT_REALM;
    private static final long serialVersionUID = 2511890818146955112L;
    private final ASN1OctetString password;
    private final AtomicReference<LDAPConnection> conn;
    private final boolean enableGSSAPIDebugging;
    private final Boolean isInitiator;
    private final boolean refreshKrb5Config;
    private final boolean renewTGT;
    private final boolean requireCachedCredentials;
    private final boolean useKeyTab;
    private final boolean useSubjectCredentialsOnly;
    private final boolean useTicketCache;
    private int messageID;
    private final List<SASLQualityOfProtection> allowedQoP;
    private final List<String> unhandledCallbackMessages;
    private Set<String> suppressedSystemProperties;
    private final String authenticationID;
    private final String authorizationID;
    private final String configFilePath;
    private final String jaasClientName;
    private final String kdcAddress;
    private final String keyTabPath;
    private final String realm;
    private final String saslClientServerName;
    private final String servicePrincipalProtocol;
    private final String ticketCachePath;
    
    public GSSAPIBindRequest(final String authenticationID, final String password) throws LDAPException {
        this(new GSSAPIBindRequestProperties(authenticationID, password), new Control[0]);
    }
    
    public GSSAPIBindRequest(final String authenticationID, final byte[] password) throws LDAPException {
        this(new GSSAPIBindRequestProperties(authenticationID, password), new Control[0]);
    }
    
    public GSSAPIBindRequest(final String authenticationID, final String password, final Control[] controls) throws LDAPException {
        this(new GSSAPIBindRequestProperties(authenticationID, password), controls);
    }
    
    public GSSAPIBindRequest(final String authenticationID, final byte[] password, final Control[] controls) throws LDAPException {
        this(new GSSAPIBindRequestProperties(authenticationID, password), controls);
    }
    
    public GSSAPIBindRequest(final String authenticationID, final String authorizationID, final String password, final String realm, final String kdcAddress, final String configFilePath) throws LDAPException {
        this(new GSSAPIBindRequestProperties(authenticationID, authorizationID, new ASN1OctetString(password), realm, kdcAddress, configFilePath), new Control[0]);
    }
    
    public GSSAPIBindRequest(final String authenticationID, final String authorizationID, final byte[] password, final String realm, final String kdcAddress, final String configFilePath) throws LDAPException {
        this(new GSSAPIBindRequestProperties(authenticationID, authorizationID, new ASN1OctetString(password), realm, kdcAddress, configFilePath), new Control[0]);
    }
    
    public GSSAPIBindRequest(final String authenticationID, final String authorizationID, final String password, final String realm, final String kdcAddress, final String configFilePath, final Control[] controls) throws LDAPException {
        this(new GSSAPIBindRequestProperties(authenticationID, authorizationID, new ASN1OctetString(password), realm, kdcAddress, configFilePath), controls);
    }
    
    public GSSAPIBindRequest(final String authenticationID, final String authorizationID, final byte[] password, final String realm, final String kdcAddress, final String configFilePath, final Control[] controls) throws LDAPException {
        this(new GSSAPIBindRequestProperties(authenticationID, authorizationID, new ASN1OctetString(password), realm, kdcAddress, configFilePath), controls);
    }
    
    public GSSAPIBindRequest(final GSSAPIBindRequestProperties gssapiProperties, final Control... controls) throws LDAPException {
        super(controls);
        Validator.ensureNotNull(gssapiProperties);
        this.authenticationID = gssapiProperties.getAuthenticationID();
        this.password = gssapiProperties.getPassword();
        this.realm = gssapiProperties.getRealm();
        this.allowedQoP = gssapiProperties.getAllowedQoP();
        this.kdcAddress = gssapiProperties.getKDCAddress();
        this.jaasClientName = gssapiProperties.getJAASClientName();
        this.saslClientServerName = gssapiProperties.getSASLClientServerName();
        this.servicePrincipalProtocol = gssapiProperties.getServicePrincipalProtocol();
        this.enableGSSAPIDebugging = gssapiProperties.enableGSSAPIDebugging();
        this.useKeyTab = gssapiProperties.useKeyTab();
        this.useSubjectCredentialsOnly = gssapiProperties.useSubjectCredentialsOnly();
        this.useTicketCache = gssapiProperties.useTicketCache();
        this.requireCachedCredentials = gssapiProperties.requireCachedCredentials();
        this.refreshKrb5Config = gssapiProperties.refreshKrb5Config();
        this.renewTGT = gssapiProperties.renewTGT();
        this.keyTabPath = gssapiProperties.getKeyTabPath();
        this.ticketCachePath = gssapiProperties.getTicketCachePath();
        this.isInitiator = gssapiProperties.getIsInitiator();
        this.suppressedSystemProperties = gssapiProperties.getSuppressedSystemProperties();
        this.unhandledCallbackMessages = new ArrayList<String>(5);
        this.conn = new AtomicReference<LDAPConnection>();
        this.messageID = -1;
        final String authzID = gssapiProperties.getAuthorizationID();
        if (authzID == null) {
            this.authorizationID = null;
        }
        else {
            this.authorizationID = authzID;
        }
        final String cfgPath = gssapiProperties.getConfigFilePath();
        if (cfgPath == null) {
            if (GSSAPIBindRequest.DEFAULT_CONFIG_FILE == null) {
                this.configFilePath = getConfigFilePath(gssapiProperties);
            }
            else {
                this.configFilePath = GSSAPIBindRequest.DEFAULT_CONFIG_FILE;
            }
        }
        else {
            this.configFilePath = cfgPath;
        }
    }
    
    @Override
    public String getSASLMechanismName() {
        return "GSSAPI";
    }
    
    public String getAuthenticationID() {
        return this.authenticationID;
    }
    
    public String getAuthorizationID() {
        return this.authorizationID;
    }
    
    public String getPasswordString() {
        if (this.password == null) {
            return null;
        }
        return this.password.stringValue();
    }
    
    public byte[] getPasswordBytes() {
        if (this.password == null) {
            return null;
        }
        return this.password.getValue();
    }
    
    public String getRealm() {
        return this.realm;
    }
    
    public List<SASLQualityOfProtection> getAllowedQoP() {
        return this.allowedQoP;
    }
    
    public String getKDCAddress() {
        return this.kdcAddress;
    }
    
    public String getConfigFilePath() {
        return this.configFilePath;
    }
    
    public String getServicePrincipalProtocol() {
        return this.servicePrincipalProtocol;
    }
    
    public boolean refreshKrb5Config() {
        return this.refreshKrb5Config;
    }
    
    public boolean useKeyTab() {
        return this.useKeyTab;
    }
    
    public String getKeyTabPath() {
        return this.keyTabPath;
    }
    
    public boolean useTicketCache() {
        return this.useTicketCache;
    }
    
    public boolean requireCachedCredentials() {
        return this.requireCachedCredentials;
    }
    
    public String getTicketCachePath() {
        return this.ticketCachePath;
    }
    
    public boolean renewTGT() {
        return this.renewTGT;
    }
    
    public boolean useSubjectCredentialsOnly() {
        return this.useSubjectCredentialsOnly;
    }
    
    public Boolean getIsInitiator() {
        return this.isInitiator;
    }
    
    public Set<String> getSuppressedSystemProperties() {
        return this.suppressedSystemProperties;
    }
    
    public boolean enableGSSAPIDebugging() {
        return this.enableGSSAPIDebugging;
    }
    
    private static String getConfigFilePath(final GSSAPIBindRequestProperties properties) throws LDAPException {
        try {
            final File f = File.createTempFile("GSSAPIBindRequest-JAAS-Config-", ".conf");
            f.deleteOnExit();
            final PrintWriter w = new PrintWriter(new FileWriter(f));
            try {
                try {
                    final Class<?> sunModuleClass = Class.forName("com.sun.security.auth.module.Krb5LoginModule");
                    if (sunModuleClass != null) {
                        writeSunJAASConfig(w, properties);
                        return f.getAbsolutePath();
                    }
                }
                catch (final ClassNotFoundException cnfe) {
                    Debug.debugException(cnfe);
                }
                try {
                    final Class<?> ibmModuleClass = Class.forName("com.ibm.security.auth.module.Krb5LoginModule");
                    if (ibmModuleClass != null) {
                        writeIBMJAASConfig(w, properties);
                        return f.getAbsolutePath();
                    }
                }
                catch (final ClassNotFoundException cnfe) {
                    Debug.debugException(cnfe);
                }
                throw new LDAPException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_GSSAPI_CANNOT_CREATE_JAAS_CONFIG.get(LDAPMessages.ERR_GSSAPI_NO_SUPPORTED_JAAS_MODULE.get()));
            }
            finally {
                w.close();
            }
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_GSSAPI_CANNOT_CREATE_JAAS_CONFIG.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    private static void writeSunJAASConfig(final PrintWriter w, final GSSAPIBindRequestProperties p) {
        w.println(p.getJAASClientName() + " {");
        w.println("  com.sun.security.auth.module.Krb5LoginModule required");
        w.println("  client=true");
        if (p.getIsInitiator() != null) {
            w.println("  isInitiator=" + p.getIsInitiator());
        }
        if (p.refreshKrb5Config()) {
            w.println("  refreshKrb5Config=true");
        }
        if (p.useKeyTab()) {
            w.println("  useKeyTab=true");
            if (p.getKeyTabPath() != null) {
                w.println("  keyTab=\"" + p.getKeyTabPath() + '\"');
            }
        }
        if (p.useTicketCache()) {
            w.println("  useTicketCache=true");
            w.println("  renewTGT=" + p.renewTGT());
            w.println("  doNotPrompt=" + p.requireCachedCredentials());
            final String ticketCachePath = p.getTicketCachePath();
            if (ticketCachePath != null) {
                w.println("  ticketCache=\"" + ticketCachePath + '\"');
            }
        }
        else {
            w.println("  useTicketCache=false");
        }
        if (p.enableGSSAPIDebugging()) {
            w.println(" debug=true");
        }
        w.println("  ;");
        w.println("};");
    }
    
    private static void writeIBMJAASConfig(final PrintWriter w, final GSSAPIBindRequestProperties p) {
        w.println(p.getJAASClientName() + " {");
        w.println("  com.ibm.security.auth.module.Krb5LoginModule required");
        if (p.getIsInitiator() == null || p.getIsInitiator()) {
            w.println("  credsType=initiator");
        }
        else {
            w.println("  credsType=acceptor");
        }
        if (p.refreshKrb5Config()) {
            w.println("  refreshKrb5Config=true");
        }
        if (p.useKeyTab()) {
            w.println("  useKeyTab=true");
            if (p.getKeyTabPath() != null) {
                w.println("  keyTab=\"" + p.getKeyTabPath() + '\"');
            }
        }
        if (p.useTicketCache()) {
            final String ticketCachePath = p.getTicketCachePath();
            if (ticketCachePath == null) {
                if (p.requireCachedCredentials()) {
                    w.println("  useDefaultCcache=true");
                }
            }
            else {
                final File f = new File(ticketCachePath);
                final String path = f.getAbsolutePath().replace('\\', '/');
                w.println("  useCcache=\"file://" + path + '\"');
            }
        }
        else {
            w.println("  useDefaultCcache=false");
        }
        if (p.enableGSSAPIDebugging()) {
            w.println(" debug=true");
        }
        w.println("  ;");
        w.println("};");
    }
    
    @Override
    protected BindResult process(final LDAPConnection connection, final int depth) throws LDAPException {
        if (!this.conn.compareAndSet(null, connection)) {
            throw new LDAPException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_GSSAPI_MULTIPLE_CONCURRENT_REQUESTS.get());
        }
        this.setProperty("java.security.auth.login.config", this.configFilePath);
        this.setProperty("javax.security.auth.useSubjectCredsOnly", String.valueOf(this.useSubjectCredentialsOnly));
        if (Debug.debugEnabled(DebugType.LDAP)) {
            Debug.debug(Level.CONFIG, DebugType.LDAP, "Using config file property java.security.auth.login.config = '" + this.configFilePath + "'.");
            Debug.debug(Level.CONFIG, DebugType.LDAP, "Using subject creds only property javax.security.auth.useSubjectCredsOnly = '" + this.useSubjectCredentialsOnly + "'.");
        }
        if (this.kdcAddress == null) {
            if (GSSAPIBindRequest.DEFAULT_KDC_ADDRESS == null) {
                this.clearProperty("java.security.krb5.kdc");
                if (Debug.debugEnabled(DebugType.LDAP)) {
                    Debug.debug(Level.CONFIG, DebugType.LDAP, "Clearing kdcAddress property 'java.security.krb5.kdc'.");
                }
            }
            else {
                this.setProperty("java.security.krb5.kdc", GSSAPIBindRequest.DEFAULT_KDC_ADDRESS);
                if (Debug.debugEnabled(DebugType.LDAP)) {
                    Debug.debug(Level.CONFIG, DebugType.LDAP, "Using default kdcAddress property java.security.krb5.kdc = '" + GSSAPIBindRequest.DEFAULT_KDC_ADDRESS + "'.");
                }
            }
        }
        else {
            this.setProperty("java.security.krb5.kdc", this.kdcAddress);
            if (Debug.debugEnabled(DebugType.LDAP)) {
                Debug.debug(Level.CONFIG, DebugType.LDAP, "Using kdcAddress property java.security.krb5.kdc = '" + this.kdcAddress + "'.");
            }
        }
        Label_0439: {
            if (this.realm == null) {
                if (GSSAPIBindRequest.DEFAULT_REALM == null) {
                    this.clearProperty("java.security.krb5.realm");
                    if (Debug.debugEnabled(DebugType.LDAP)) {
                        Debug.debug(Level.CONFIG, DebugType.LDAP, "Clearing realm property 'java.security.krb5.realm'.");
                    }
                    break Label_0439;
                }
                else {
                    this.setProperty("java.security.krb5.realm", GSSAPIBindRequest.DEFAULT_REALM);
                    if (Debug.debugEnabled(DebugType.LDAP)) {
                        Debug.debug(Level.CONFIG, DebugType.LDAP, "Using default realm property java.security.krb5.realm = '" + GSSAPIBindRequest.DEFAULT_REALM + "'.");
                    }
                    break Label_0439;
                }
            }
            else {
                this.setProperty("java.security.krb5.realm", this.realm);
                if (!Debug.debugEnabled(DebugType.LDAP)) {
                    break Label_0439;
                }
            }
            Debug.debug(Level.CONFIG, DebugType.LDAP, "Using realm property java.security.krb5.realm = '" + this.realm + "'.");
            try {
                LoginContext context;
                try {
                    context = new LoginContext(this.jaasClientName, this);
                    context.login();
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    throw new LDAPException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_GSSAPI_CANNOT_INITIALIZE_JAAS_CONTEXT.get(StaticUtils.getExceptionMessage(e)), e);
                }
                try {
                    return Subject.doAs(context.getSubject(), (PrivilegedExceptionAction<BindResult>)this);
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    if (e instanceof LDAPException) {
                        throw (LDAPException)e;
                    }
                    throw new LDAPException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_GSSAPI_AUTHENTICATION_FAILED.get(StaticUtils.getExceptionMessage(e)), e);
                }
            }
            finally {
                this.conn.set(null);
            }
        }
    }
    
    @InternalUseOnly
    @Override
    public Object run() throws LDAPException {
        this.unhandledCallbackMessages.clear();
        final LDAPConnection connection = this.conn.get();
        final HashMap<String, Object> saslProperties = new HashMap<String, Object>(StaticUtils.computeMapCapacity(2));
        saslProperties.put("javax.security.sasl.qop", SASLQualityOfProtection.toString(this.allowedQoP));
        saslProperties.put("javax.security.sasl.server.authentication", "true");
        SaslClient saslClient;
        try {
            String serverName = this.saslClientServerName;
            if (serverName == null) {
                serverName = connection.getConnectedAddress();
            }
            final String[] mechanisms = { "GSSAPI" };
            saslClient = Sasl.createSaslClient(mechanisms, this.authorizationID, this.servicePrincipalProtocol, serverName, saslProperties, this);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_GSSAPI_CANNOT_CREATE_SASL_CLIENT.get(StaticUtils.getExceptionMessage(e)), e);
        }
        final SASLHelper helper = new SASLHelper(this, connection, "GSSAPI", saslClient, this.getControls(), this.getResponseTimeoutMillis(connection), this.unhandledCallbackMessages);
        try {
            return helper.processSASLBind();
        }
        finally {
            this.messageID = helper.getMessageID();
        }
    }
    
    @Override
    public GSSAPIBindRequest getRebindRequest(final String host, final int port) {
        try {
            final GSSAPIBindRequestProperties gssapiProperties = new GSSAPIBindRequestProperties(this.authenticationID, this.authorizationID, this.password, this.realm, this.kdcAddress, this.configFilePath);
            gssapiProperties.setAllowedQoP(this.allowedQoP);
            gssapiProperties.setServicePrincipalProtocol(this.servicePrincipalProtocol);
            gssapiProperties.setUseTicketCache(this.useTicketCache);
            gssapiProperties.setRequireCachedCredentials(this.requireCachedCredentials);
            gssapiProperties.setRenewTGT(this.renewTGT);
            gssapiProperties.setUseSubjectCredentialsOnly(this.useSubjectCredentialsOnly);
            gssapiProperties.setTicketCachePath(this.ticketCachePath);
            gssapiProperties.setEnableGSSAPIDebugging(this.enableGSSAPIDebugging);
            gssapiProperties.setJAASClientName(this.jaasClientName);
            gssapiProperties.setSASLClientServerName(this.saslClientServerName);
            gssapiProperties.setSuppressedSystemProperties(this.suppressedSystemProperties);
            return new GSSAPIBindRequest(gssapiProperties, this.getControls());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return null;
        }
    }
    
    @InternalUseOnly
    @Override
    public void handle(final Callback[] callbacks) throws UnsupportedCallbackException {
        for (final Callback callback : callbacks) {
            if (callback instanceof NameCallback) {
                ((NameCallback)callback).setName(this.authenticationID);
            }
            else if (callback instanceof PasswordCallback) {
                if (this.password == null) {
                    throw new UnsupportedCallbackException(callback, LDAPMessages.ERR_GSSAPI_NO_PASSWORD_AVAILABLE.get());
                }
                ((PasswordCallback)callback).setPassword(this.password.stringValue().toCharArray());
            }
            else if (callback instanceof RealmCallback) {
                final RealmCallback rc = (RealmCallback)callback;
                if (this.realm == null) {
                    this.unhandledCallbackMessages.add(LDAPMessages.ERR_GSSAPI_REALM_REQUIRED_BUT_NONE_PROVIDED.get(rc.getPrompt()));
                }
                else {
                    rc.setText(this.realm);
                }
            }
            else {
                if (Debug.debugEnabled(DebugType.LDAP)) {
                    Debug.debug(Level.WARNING, DebugType.LDAP, "Unexpected GSSAPI SASL callback of type " + callback.getClass().getName());
                }
                this.unhandledCallbackMessages.add(LDAPMessages.ERR_GSSAPI_UNEXPECTED_CALLBACK.get(callback.getClass().getName()));
            }
        }
    }
    
    @Override
    public int getLastMessageID() {
        return this.messageID;
    }
    
    @Override
    public GSSAPIBindRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public GSSAPIBindRequest duplicate(final Control[] controls) {
        try {
            final GSSAPIBindRequestProperties gssapiProperties = new GSSAPIBindRequestProperties(this.authenticationID, this.authorizationID, this.password, this.realm, this.kdcAddress, this.configFilePath);
            gssapiProperties.setAllowedQoP(this.allowedQoP);
            gssapiProperties.setServicePrincipalProtocol(this.servicePrincipalProtocol);
            gssapiProperties.setUseTicketCache(this.useTicketCache);
            gssapiProperties.setRequireCachedCredentials(this.requireCachedCredentials);
            gssapiProperties.setRenewTGT(this.renewTGT);
            gssapiProperties.setRefreshKrb5Config(this.refreshKrb5Config);
            gssapiProperties.setUseKeyTab(this.useKeyTab);
            gssapiProperties.setKeyTabPath(this.keyTabPath);
            gssapiProperties.setUseSubjectCredentialsOnly(this.useSubjectCredentialsOnly);
            gssapiProperties.setTicketCachePath(this.ticketCachePath);
            gssapiProperties.setEnableGSSAPIDebugging(this.enableGSSAPIDebugging);
            gssapiProperties.setJAASClientName(this.jaasClientName);
            gssapiProperties.setSASLClientServerName(this.saslClientServerName);
            gssapiProperties.setIsInitiator(this.isInitiator);
            gssapiProperties.setSuppressedSystemProperties(this.suppressedSystemProperties);
            final GSSAPIBindRequest bindRequest = new GSSAPIBindRequest(gssapiProperties, controls);
            bindRequest.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
            return bindRequest;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return null;
        }
    }
    
    private void clearProperty(final String name) {
        if (!this.suppressedSystemProperties.contains(name)) {
            StaticUtils.clearSystemProperty(name);
        }
    }
    
    private void setProperty(final String name, final String value) {
        if (!this.suppressedSystemProperties.contains(name)) {
            StaticUtils.setSystemProperty(name, value);
        }
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("GSSAPIBindRequest(authenticationID='");
        buffer.append(this.authenticationID);
        buffer.append('\'');
        if (this.authorizationID != null) {
            buffer.append(", authorizationID='");
            buffer.append(this.authorizationID);
            buffer.append('\'');
        }
        if (this.realm != null) {
            buffer.append(", realm='");
            buffer.append(this.realm);
            buffer.append('\'');
        }
        buffer.append(", qop='");
        buffer.append(SASLQualityOfProtection.toString(this.allowedQoP));
        buffer.append('\'');
        if (this.kdcAddress != null) {
            buffer.append(", kdcAddress='");
            buffer.append(this.kdcAddress);
            buffer.append('\'');
        }
        if (this.isInitiator != null) {
            buffer.append(", isInitiator=");
            buffer.append(this.isInitiator);
        }
        buffer.append(", jaasClientName='");
        buffer.append(this.jaasClientName);
        buffer.append("', configFilePath='");
        buffer.append(this.configFilePath);
        buffer.append("', servicePrincipalProtocol='");
        buffer.append(this.servicePrincipalProtocol);
        buffer.append("', enableGSSAPIDebugging=");
        buffer.append(this.enableGSSAPIDebugging);
        final Control[] controls = this.getControls();
        if (controls.length > 0) {
            buffer.append(", controls={");
            for (int i = 0; i < controls.length; ++i) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append(controls[i]);
            }
            buffer.append('}');
        }
        buffer.append(')');
    }
    
    @Override
    public void toCode(final List<String> lineList, final String requestID, final int indentSpaces, final boolean includeProcessing) {
        ToCodeHelper.generateMethodCall(lineList, indentSpaces, "GSSAPIBindRequestProperties", requestID + "RequestProperties", "new GSSAPIBindRequestProperties", ToCodeArgHelper.createString(this.authenticationID, "Authentication ID"), ToCodeArgHelper.createString("---redacted-password---", "Password"));
        if (this.authorizationID != null) {
            ToCodeHelper.generateMethodCall(lineList, indentSpaces, null, null, requestID + "RequestProperties.setAuthorizationID", ToCodeArgHelper.createString(this.authorizationID, null));
        }
        if (this.realm != null) {
            ToCodeHelper.generateMethodCall(lineList, indentSpaces, null, null, requestID + "RequestProperties.setRealm", ToCodeArgHelper.createString(this.realm, null));
        }
        final ArrayList<String> qopValues = new ArrayList<String>(3);
        for (final SASLQualityOfProtection qop : this.allowedQoP) {
            qopValues.add("SASLQualityOfProtection." + qop.name());
        }
        ToCodeHelper.generateMethodCall(lineList, indentSpaces, null, null, requestID + "RequestProperties.setAllowedQoP", ToCodeArgHelper.createRaw(qopValues, null));
        if (this.kdcAddress != null) {
            ToCodeHelper.generateMethodCall(lineList, indentSpaces, null, null, requestID + "RequestProperties.setKDCAddress", ToCodeArgHelper.createString(this.kdcAddress, null));
        }
        if (this.jaasClientName != null) {
            ToCodeHelper.generateMethodCall(lineList, indentSpaces, null, null, requestID + "RequestProperties.setJAASClientName", ToCodeArgHelper.createString(this.jaasClientName, null));
        }
        if (this.configFilePath != null) {
            ToCodeHelper.generateMethodCall(lineList, indentSpaces, null, null, requestID + "RequestProperties.setConfigFilePath", ToCodeArgHelper.createString(this.configFilePath, null));
        }
        if (this.saslClientServerName != null) {
            ToCodeHelper.generateMethodCall(lineList, indentSpaces, null, null, requestID + "RequestProperties.setSASLClientServerName", ToCodeArgHelper.createString(this.saslClientServerName, null));
        }
        if (this.servicePrincipalProtocol != null) {
            ToCodeHelper.generateMethodCall(lineList, indentSpaces, null, null, requestID + "RequestProperties.setServicePrincipalProtocol", ToCodeArgHelper.createString(this.servicePrincipalProtocol, null));
        }
        ToCodeHelper.generateMethodCall(lineList, indentSpaces, null, null, requestID + "RequestProperties.setRefreshKrb5Config", ToCodeArgHelper.createBoolean(this.refreshKrb5Config, null));
        ToCodeHelper.generateMethodCall(lineList, indentSpaces, null, null, requestID + "RequestProperties.setUseKeyTab", ToCodeArgHelper.createBoolean(this.useKeyTab, null));
        if (this.keyTabPath != null) {
            ToCodeHelper.generateMethodCall(lineList, indentSpaces, null, null, requestID + "RequestProperties.setKeyTabPath", ToCodeArgHelper.createString(this.keyTabPath, null));
        }
        ToCodeHelper.generateMethodCall(lineList, indentSpaces, null, null, requestID + "RequestProperties.setUseSubjectCredentialsOnly", ToCodeArgHelper.createBoolean(this.useSubjectCredentialsOnly, null));
        ToCodeHelper.generateMethodCall(lineList, indentSpaces, null, null, requestID + "RequestProperties.setUseTicketCache", ToCodeArgHelper.createBoolean(this.useTicketCache, null));
        ToCodeHelper.generateMethodCall(lineList, indentSpaces, null, null, requestID + "RequestProperties.setRequireCachedCredentials", ToCodeArgHelper.createBoolean(this.requireCachedCredentials, null));
        if (this.ticketCachePath != null) {
            ToCodeHelper.generateMethodCall(lineList, indentSpaces, null, null, requestID + "RequestProperties.setTicketCachePath", ToCodeArgHelper.createString(this.ticketCachePath, null));
        }
        ToCodeHelper.generateMethodCall(lineList, indentSpaces, null, null, requestID + "RequestProperties.setRenewTGT", ToCodeArgHelper.createBoolean(this.renewTGT, null));
        if (this.isInitiator != null) {
            ToCodeHelper.generateMethodCall(lineList, indentSpaces, null, null, requestID + "RequestProperties.setIsInitiator", ToCodeArgHelper.createBoolean(this.isInitiator, null));
        }
        if (this.suppressedSystemProperties != null && !this.suppressedSystemProperties.isEmpty()) {
            final ArrayList<ToCodeArgHelper> suppressedArgs = new ArrayList<ToCodeArgHelper>(this.suppressedSystemProperties.size());
            for (final String s : this.suppressedSystemProperties) {
                suppressedArgs.add(ToCodeArgHelper.createString(s, null));
            }
            ToCodeHelper.generateMethodCall(lineList, indentSpaces, "List<String>", requestID + "SuppressedProperties", "Arrays.asList", suppressedArgs);
            ToCodeHelper.generateMethodCall(lineList, indentSpaces, null, null, requestID + "RequestProperties.setSuppressedSystemProperties", ToCodeArgHelper.createRaw(requestID + "SuppressedProperties", null));
        }
        ToCodeHelper.generateMethodCall(lineList, indentSpaces, null, null, requestID + "RequestProperties.setEnableGSSAPIDebugging", ToCodeArgHelper.createBoolean(this.enableGSSAPIDebugging, null));
        final ArrayList<ToCodeArgHelper> constructorArgs = new ArrayList<ToCodeArgHelper>(2);
        constructorArgs.add(ToCodeArgHelper.createRaw(requestID + "RequestProperties", null));
        final Control[] controls = this.getControls();
        if (controls.length > 0) {
            constructorArgs.add(ToCodeArgHelper.createControlArray(controls, "Bind Controls"));
        }
        ToCodeHelper.generateMethodCall(lineList, indentSpaces, "GSSAPIBindRequest", requestID + "Request", "new GSSAPIBindRequest", constructorArgs);
        if (includeProcessing) {
            final StringBuilder buffer = new StringBuilder();
            for (int i = 0; i < indentSpaces; ++i) {
                buffer.append(' ');
            }
            final String indent = buffer.toString();
            lineList.add("");
            lineList.add(indent + "try");
            lineList.add(indent + '{');
            lineList.add(indent + "  BindResult " + requestID + "Result = connection.bind(" + requestID + "Request);");
            lineList.add(indent + "  // The bind was processed successfully.");
            lineList.add(indent + '}');
            lineList.add(indent + "catch (LDAPException e)");
            lineList.add(indent + '{');
            lineList.add(indent + "  // The bind failed.  Maybe the following will " + "help explain why.");
            lineList.add(indent + "  // Note that the connection is now likely in " + "an unauthenticated state.");
            lineList.add(indent + "  ResultCode resultCode = e.getResultCode();");
            lineList.add(indent + "  String message = e.getMessage();");
            lineList.add(indent + "  String matchedDN = e.getMatchedDN();");
            lineList.add(indent + "  String[] referralURLs = e.getReferralURLs();");
            lineList.add(indent + "  Control[] responseControls = " + "e.getResponseControls();");
            lineList.add(indent + '}');
        }
    }
    
    static {
        DEFAULT_CONFIG_FILE = StaticUtils.getSystemProperty("java.security.auth.login.config");
        DEFAULT_KDC_ADDRESS = StaticUtils.getSystemProperty("java.security.krb5.kdc");
        DEFAULT_REALM = StaticUtils.getSystemProperty("java.security.krb5.realm");
    }
}
