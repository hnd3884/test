package com.sun.mail.imap;

import javax.mail.Quota;
import javax.mail.StoreClosedException;
import com.sun.mail.iap.BadCommandException;
import javax.mail.Folder;
import javax.mail.PasswordAuthentication;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.util.Vector;
import java.util.Map;
import com.sun.mail.iap.ConnectionException;
import java.util.HashMap;
import com.sun.mail.imap.protocol.IMAPProtocol;
import java.io.IOException;
import com.sun.mail.util.SocketConnectException;
import com.sun.mail.util.MailConnectException;
import com.sun.mail.iap.ProtocolException;
import javax.mail.MessagingException;
import com.sun.mail.iap.CommandFailedException;
import javax.mail.AuthenticationFailedException;
import com.sun.mail.imap.protocol.IMAPReferralException;
import java.util.List;
import java.util.Properties;
import com.sun.mail.imap.protocol.ListInfo;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.Locale;
import com.sun.mail.util.PropUtil;
import com.sun.mail.iap.Response;
import javax.mail.URLName;
import javax.mail.Session;
import java.lang.reflect.Constructor;
import com.sun.mail.util.MailLogger;
import com.sun.mail.imap.protocol.Namespaces;
import com.sun.mail.iap.ResponseHandler;
import javax.mail.QuotaAwareStore;
import javax.mail.Store;

public class IMAPStore extends Store implements QuotaAwareStore, ResponseHandler
{
    public static final int RESPONSE = 1000;
    public static final String ID_NAME = "name";
    public static final String ID_VERSION = "version";
    public static final String ID_OS = "os";
    public static final String ID_OS_VERSION = "os-version";
    public static final String ID_VENDOR = "vendor";
    public static final String ID_SUPPORT_URL = "support-url";
    public static final String ID_ADDRESS = "address";
    public static final String ID_DATE = "date";
    public static final String ID_COMMAND = "command";
    public static final String ID_ARGUMENTS = "arguments";
    public static final String ID_ENVIRONMENT = "environment";
    protected final String name;
    protected final int defaultPort;
    protected final boolean isSSL;
    private final int blksize;
    private boolean ignoreSize;
    private final int statusCacheTimeout;
    private final int appendBufferSize;
    private final int minIdleTime;
    private volatile int port;
    protected String host;
    protected String user;
    protected String password;
    protected String proxyAuthUser;
    protected String authorizationID;
    protected String saslRealm;
    private Namespaces namespaces;
    private boolean enableStartTLS;
    private boolean requireStartTLS;
    private boolean usingSSL;
    private boolean enableSASL;
    private String[] saslMechanisms;
    private boolean forcePasswordRefresh;
    private boolean enableResponseEvents;
    private boolean enableImapEvents;
    private String guid;
    private boolean throwSearchException;
    private boolean peek;
    private boolean closeFoldersOnStoreFailure;
    private boolean enableCompress;
    private boolean finalizeCleanClose;
    private volatile boolean connectionFailed;
    private volatile boolean forceClose;
    private final Object connectionFailedLock;
    private boolean debugusername;
    private boolean debugpassword;
    protected MailLogger logger;
    private boolean messageCacheDebug;
    private volatile Constructor<?> folderConstructor;
    private volatile Constructor<?> folderConstructorLI;
    private final ConnectionPool pool;
    private ResponseHandler nonStoreResponseHandler;
    
    public IMAPStore(final Session session, final URLName url) {
        this(session, url, "imap", false);
    }
    
    protected IMAPStore(final Session session, final URLName url, String name, boolean isSSL) {
        super(session, url);
        this.port = -1;
        this.enableStartTLS = false;
        this.requireStartTLS = false;
        this.usingSSL = false;
        this.enableSASL = false;
        this.forcePasswordRefresh = false;
        this.enableResponseEvents = false;
        this.enableImapEvents = false;
        this.throwSearchException = false;
        this.peek = false;
        this.closeFoldersOnStoreFailure = true;
        this.enableCompress = false;
        this.finalizeCleanClose = false;
        this.connectionFailed = false;
        this.forceClose = false;
        this.connectionFailedLock = new Object();
        this.folderConstructor = null;
        this.folderConstructorLI = null;
        this.nonStoreResponseHandler = new ResponseHandler() {
            @Override
            public void handleResponse(final Response r) {
                if (r.isOK() || r.isNO() || r.isBAD() || r.isBYE()) {
                    IMAPStore.this.handleResponseCode(r);
                }
                if (r.isBYE()) {
                    IMAPStore.this.logger.fine("IMAPStore non-store connection dead");
                }
            }
        };
        final Properties props = session.getProperties();
        if (url != null) {
            name = url.getProtocol();
        }
        this.name = name;
        if (!isSSL) {
            isSSL = PropUtil.getBooleanProperty(props, "mail." + name + ".ssl.enable", false);
        }
        if (isSSL) {
            this.defaultPort = 993;
        }
        else {
            this.defaultPort = 143;
        }
        this.isSSL = isSSL;
        this.debug = session.getDebug();
        this.debugusername = PropUtil.getBooleanProperty(props, "mail.debug.auth.username", true);
        this.debugpassword = PropUtil.getBooleanProperty(props, "mail.debug.auth.password", false);
        this.logger = new MailLogger(this.getClass(), "DEBUG " + name.toUpperCase(Locale.ENGLISH), session.getDebug(), session.getDebugOut());
        final boolean partialFetch = PropUtil.getBooleanProperty(props, "mail." + name + ".partialfetch", true);
        if (!partialFetch) {
            this.blksize = -1;
            this.logger.config("mail.imap.partialfetch: false");
        }
        else {
            this.blksize = PropUtil.getIntProperty(props, "mail." + name + ".fetchsize", 16384);
            if (this.logger.isLoggable(Level.CONFIG)) {
                this.logger.config("mail.imap.fetchsize: " + this.blksize);
            }
        }
        this.ignoreSize = PropUtil.getBooleanProperty(props, "mail." + name + ".ignorebodystructuresize", false);
        if (this.logger.isLoggable(Level.CONFIG)) {
            this.logger.config("mail.imap.ignorebodystructuresize: " + this.ignoreSize);
        }
        this.statusCacheTimeout = PropUtil.getIntProperty(props, "mail." + name + ".statuscachetimeout", 1000);
        if (this.logger.isLoggable(Level.CONFIG)) {
            this.logger.config("mail.imap.statuscachetimeout: " + this.statusCacheTimeout);
        }
        this.appendBufferSize = PropUtil.getIntProperty(props, "mail." + name + ".appendbuffersize", -1);
        if (this.logger.isLoggable(Level.CONFIG)) {
            this.logger.config("mail.imap.appendbuffersize: " + this.appendBufferSize);
        }
        this.minIdleTime = PropUtil.getIntProperty(props, "mail." + name + ".minidletime", 10);
        if (this.logger.isLoggable(Level.CONFIG)) {
            this.logger.config("mail.imap.minidletime: " + this.minIdleTime);
        }
        String s = session.getProperty("mail." + name + ".proxyauth.user");
        if (s != null) {
            this.proxyAuthUser = s;
            if (this.logger.isLoggable(Level.CONFIG)) {
                this.logger.config("mail.imap.proxyauth.user: " + this.proxyAuthUser);
            }
        }
        this.enableStartTLS = PropUtil.getBooleanProperty(props, "mail." + name + ".starttls.enable", false);
        if (this.enableStartTLS) {
            this.logger.config("enable STARTTLS");
        }
        this.requireStartTLS = PropUtil.getBooleanProperty(props, "mail." + name + ".starttls.required", false);
        if (this.requireStartTLS) {
            this.logger.config("require STARTTLS");
        }
        this.enableSASL = PropUtil.getBooleanProperty(props, "mail." + name + ".sasl.enable", false);
        if (this.enableSASL) {
            this.logger.config("enable SASL");
        }
        if (this.enableSASL) {
            s = session.getProperty("mail." + name + ".sasl.mechanisms");
            if (s != null && s.length() > 0) {
                if (this.logger.isLoggable(Level.CONFIG)) {
                    this.logger.config("SASL mechanisms allowed: " + s);
                }
                final List<String> v = new ArrayList<String>(5);
                final StringTokenizer st = new StringTokenizer(s, " ,");
                while (st.hasMoreTokens()) {
                    final String m = st.nextToken();
                    if (m.length() > 0) {
                        v.add(m);
                    }
                }
                v.toArray(this.saslMechanisms = new String[v.size()]);
            }
        }
        s = session.getProperty("mail." + name + ".sasl.authorizationid");
        if (s != null) {
            this.authorizationID = s;
            this.logger.log(Level.CONFIG, "mail.imap.sasl.authorizationid: {0}", this.authorizationID);
        }
        s = session.getProperty("mail." + name + ".sasl.realm");
        if (s != null) {
            this.saslRealm = s;
            this.logger.log(Level.CONFIG, "mail.imap.sasl.realm: {0}", this.saslRealm);
        }
        this.forcePasswordRefresh = PropUtil.getBooleanProperty(props, "mail." + name + ".forcepasswordrefresh", false);
        if (this.forcePasswordRefresh) {
            this.logger.config("enable forcePasswordRefresh");
        }
        this.enableResponseEvents = PropUtil.getBooleanProperty(props, "mail." + name + ".enableresponseevents", false);
        if (this.enableResponseEvents) {
            this.logger.config("enable IMAP response events");
        }
        this.enableImapEvents = PropUtil.getBooleanProperty(props, "mail." + name + ".enableimapevents", false);
        if (this.enableImapEvents) {
            this.logger.config("enable IMAP IDLE events");
        }
        this.messageCacheDebug = PropUtil.getBooleanProperty(props, "mail." + name + ".messagecache.debug", false);
        this.guid = session.getProperty("mail." + name + ".yahoo.guid");
        if (this.guid != null) {
            this.logger.log(Level.CONFIG, "mail.imap.yahoo.guid: {0}", this.guid);
        }
        this.throwSearchException = PropUtil.getBooleanProperty(props, "mail." + name + ".throwsearchexception", false);
        if (this.throwSearchException) {
            this.logger.config("throw SearchException");
        }
        this.peek = PropUtil.getBooleanProperty(props, "mail." + name + ".peek", false);
        if (this.peek) {
            this.logger.config("peek");
        }
        this.closeFoldersOnStoreFailure = PropUtil.getBooleanProperty(props, "mail." + name + ".closefoldersonstorefailure", true);
        if (this.closeFoldersOnStoreFailure) {
            this.logger.config("closeFoldersOnStoreFailure");
        }
        this.enableCompress = PropUtil.getBooleanProperty(props, "mail." + name + ".compress.enable", false);
        if (this.enableCompress) {
            this.logger.config("enable COMPRESS");
        }
        this.finalizeCleanClose = PropUtil.getBooleanProperty(props, "mail." + name + ".finalizecleanclose", false);
        if (this.finalizeCleanClose) {
            this.logger.config("close connection cleanly in finalize");
        }
        s = session.getProperty("mail." + name + ".folder.class");
        if (s != null) {
            this.logger.log(Level.CONFIG, "IMAP: folder class: {0}", s);
            try {
                final ClassLoader cl = this.getClass().getClassLoader();
                Class<?> folderClass = null;
                try {
                    folderClass = Class.forName(s, false, cl);
                }
                catch (final ClassNotFoundException ex1) {
                    folderClass = Class.forName(s);
                }
                final Class<?>[] c = { String.class, Character.TYPE, IMAPStore.class, Boolean.class };
                this.folderConstructor = folderClass.getConstructor(c);
                final Class<?>[] c2 = { ListInfo.class, IMAPStore.class };
                this.folderConstructorLI = folderClass.getConstructor(c2);
            }
            catch (final Exception ex2) {
                this.logger.log(Level.CONFIG, "IMAP: failed to load folder class", ex2);
            }
        }
        this.pool = new ConnectionPool(name, this.logger, session);
    }
    
    @Override
    protected synchronized boolean protocolConnect(final String host, final int pport, final String user, final String password) throws MessagingException {
        IMAPProtocol protocol = null;
        if (host == null || password == null || user == null) {
            if (this.logger.isLoggable(Level.FINE)) {
                this.logger.fine("protocolConnect returning false, host=" + host + ", user=" + this.traceUser(user) + ", password=" + this.tracePassword(password));
            }
            return false;
        }
        if (pport != -1) {
            this.port = pport;
        }
        else {
            this.port = PropUtil.getIntProperty(this.session.getProperties(), "mail." + this.name + ".port", this.port);
        }
        if (this.port == -1) {
            this.port = this.defaultPort;
        }
        try {
            final boolean poolEmpty;
            synchronized (this.pool) {
                poolEmpty = this.pool.authenticatedConnections.isEmpty();
            }
            if (poolEmpty) {
                if (this.logger.isLoggable(Level.FINE)) {
                    this.logger.fine("trying to connect to host \"" + host + "\", port " + this.port + ", isSSL " + this.isSSL);
                }
                protocol = this.newIMAPProtocol(host, this.port);
                if (this.logger.isLoggable(Level.FINE)) {
                    this.logger.fine("protocolConnect login, host=" + host + ", user=" + this.traceUser(user) + ", password=" + this.tracePassword(password));
                }
                protocol.addResponseHandler(this.nonStoreResponseHandler);
                this.login(protocol, user, password);
                protocol.removeResponseHandler(this.nonStoreResponseHandler);
                protocol.addResponseHandler(this);
                this.usingSSL = protocol.isSSL();
                this.host = host;
                this.user = user;
                this.password = password;
                synchronized (this.pool) {
                    this.pool.authenticatedConnections.addElement(protocol);
                }
            }
        }
        catch (final IMAPReferralException ex) {
            if (protocol != null) {
                protocol.disconnect();
            }
            protocol = null;
            throw new ReferralException(ex.getUrl(), ex.getMessage());
        }
        catch (final CommandFailedException cex) {
            if (protocol != null) {
                protocol.disconnect();
            }
            protocol = null;
            final Response r = cex.getResponse();
            throw new AuthenticationFailedException((r != null) ? r.getRest() : cex.getMessage());
        }
        catch (final ProtocolException pex) {
            if (protocol != null) {
                protocol.disconnect();
            }
            protocol = null;
            throw new MessagingException(pex.getMessage(), pex);
        }
        catch (final SocketConnectException scex) {
            throw new MailConnectException(scex);
        }
        catch (final IOException ioex) {
            throw new MessagingException(ioex.getMessage(), ioex);
        }
        return true;
    }
    
    protected IMAPProtocol newIMAPProtocol(final String host, final int port) throws IOException, ProtocolException {
        return new IMAPProtocol(this.name, host, port, this.session.getProperties(), this.isSSL, this.logger);
    }
    
    private void login(final IMAPProtocol p, final String u, final String pw) throws ProtocolException {
        if ((this.enableStartTLS || this.requireStartTLS) && !p.isSSL()) {
            if (p.hasCapability("STARTTLS")) {
                p.startTLS();
                p.capability();
            }
            else if (this.requireStartTLS) {
                this.logger.fine("STARTTLS required but not supported by server");
                throw new ProtocolException("STARTTLS required but not supported by server");
            }
        }
        if (p.isAuthenticated()) {
            return;
        }
        this.preLogin(p);
        if (this.guid != null) {
            final Map<String, String> gmap = new HashMap<String, String>();
            gmap.put("GUID", this.guid);
            p.id(gmap);
        }
        p.getCapabilities().put("__PRELOGIN__", "");
        String authzid;
        if (this.authorizationID != null) {
            authzid = this.authorizationID;
        }
        else if (this.proxyAuthUser != null) {
            authzid = this.proxyAuthUser;
        }
        else {
            authzid = null;
        }
        if (this.enableSASL) {
            try {
                p.sasllogin(this.saslMechanisms, this.saslRealm, authzid, u, pw);
                if (!p.isAuthenticated()) {
                    throw new CommandFailedException("SASL authentication failed");
                }
            }
            catch (final UnsupportedOperationException ex) {}
        }
        if (!p.isAuthenticated()) {
            this.authenticate(p, authzid, u, pw);
        }
        if (this.proxyAuthUser != null) {
            p.proxyauth(this.proxyAuthUser);
        }
        if (p.hasCapability("__PRELOGIN__")) {
            try {
                p.capability();
            }
            catch (final ConnectionException cex) {
                throw cex;
            }
            catch (final ProtocolException ex2) {}
        }
        if (this.enableCompress && p.hasCapability("COMPRESS=DEFLATE")) {
            p.compress();
        }
        if (p.hasCapability("UTF8=ACCEPT") || p.hasCapability("UTF8=ONLY")) {
            p.enable("UTF8=ACCEPT");
        }
    }
    
    private void authenticate(final IMAPProtocol p, final String authzid, final String user, final String password) throws ProtocolException {
        final String defaultAuthenticationMechanisms = "PLAIN LOGIN NTLM XOAUTH2";
        String mechs = this.session.getProperty("mail." + this.name + ".auth.mechanisms");
        if (mechs == null) {
            mechs = defaultAuthenticationMechanisms;
        }
        final StringTokenizer st = new StringTokenizer(mechs);
        while (st.hasMoreTokens()) {
            String m = st.nextToken();
            m = m.toUpperCase(Locale.ENGLISH);
            if (mechs == defaultAuthenticationMechanisms) {
                final String dprop = "mail." + this.name + ".auth." + m.toLowerCase(Locale.ENGLISH) + ".disable";
                final boolean disabled = PropUtil.getBooleanProperty(this.session.getProperties(), dprop, m.equals("XOAUTH2"));
                if (disabled) {
                    if (this.logger.isLoggable(Level.FINE)) {
                        this.logger.fine("mechanism " + m + " disabled by property: " + dprop);
                        continue;
                    }
                    continue;
                }
            }
            if (p.hasCapability("AUTH=" + m) || (m.equals("LOGIN") && p.hasCapability("AUTH-LOGIN"))) {
                if (m.equals("PLAIN")) {
                    p.authplain(authzid, user, password);
                }
                else if (m.equals("LOGIN")) {
                    p.authlogin(user, password);
                }
                else if (m.equals("NTLM")) {
                    p.authntlm(authzid, user, password);
                }
                else {
                    if (!m.equals("XOAUTH2")) {
                        this.logger.log(Level.FINE, "no authenticator for mechanism {0}", m);
                        continue;
                    }
                    p.authoauth2(user, password);
                }
                return;
            }
            this.logger.log(Level.FINE, "mechanism {0} not supported by server", m);
        }
        if (!p.hasCapability("LOGINDISABLED")) {
            p.login(user, password);
            return;
        }
        throw new ProtocolException("No login methods supported!");
    }
    
    protected void preLogin(final IMAPProtocol p) throws ProtocolException {
    }
    
    public synchronized boolean isSSL() {
        return this.usingSSL;
    }
    
    public synchronized void setUsername(final String user) {
        this.user = user;
    }
    
    public synchronized void setPassword(final String password) {
        this.password = password;
    }
    
    IMAPProtocol getProtocol(final IMAPFolder folder) throws MessagingException {
        IMAPProtocol p = null;
        while (p == null) {
            synchronized (this.pool) {
                if (this.pool.authenticatedConnections.isEmpty() || (this.pool.authenticatedConnections.size() == 1 && (this.pool.separateStoreConnection || this.pool.storeConnectionInUse))) {
                    this.logger.fine("no connections in the pool, creating a new one");
                    try {
                        if (this.forcePasswordRefresh) {
                            this.refreshPassword();
                        }
                        p = this.newIMAPProtocol(this.host, this.port);
                        p.addResponseHandler(this.nonStoreResponseHandler);
                        this.login(p, this.user, this.password);
                        p.removeResponseHandler(this.nonStoreResponseHandler);
                    }
                    catch (final Exception ex1) {
                        if (p != null) {
                            try {
                                p.disconnect();
                            }
                            catch (final Exception ex2) {}
                        }
                        p = null;
                    }
                    if (p == null) {
                        throw new MessagingException("connection failure");
                    }
                }
                else {
                    if (this.logger.isLoggable(Level.FINE)) {
                        this.logger.fine("connection available -- size: " + this.pool.authenticatedConnections.size());
                    }
                    p = this.pool.authenticatedConnections.lastElement();
                    this.pool.authenticatedConnections.removeElement(p);
                    final long lastUsed = System.currentTimeMillis() - p.getTimestamp();
                    if (lastUsed > this.pool.serverTimeoutInterval) {
                        try {
                            p.removeResponseHandler(this);
                            p.addResponseHandler(this.nonStoreResponseHandler);
                            p.noop();
                            p.removeResponseHandler(this.nonStoreResponseHandler);
                            p.addResponseHandler(this);
                        }
                        catch (final ProtocolException pex) {
                            try {
                                p.removeResponseHandler(this.nonStoreResponseHandler);
                                p.disconnect();
                            }
                            catch (final RuntimeException ex3) {}
                            p = null;
                            continue;
                        }
                    }
                    if (this.proxyAuthUser != null && !this.proxyAuthUser.equals(p.getProxyAuthUser()) && p.hasCapability("X-UNAUTHENTICATE")) {
                        try {
                            p.removeResponseHandler(this);
                            p.addResponseHandler(this.nonStoreResponseHandler);
                            p.unauthenticate();
                            this.login(p, this.user, this.password);
                            p.removeResponseHandler(this.nonStoreResponseHandler);
                            p.addResponseHandler(this);
                        }
                        catch (final ProtocolException pex) {
                            try {
                                p.removeResponseHandler(this.nonStoreResponseHandler);
                                p.disconnect();
                            }
                            catch (final RuntimeException ex4) {}
                            p = null;
                            continue;
                        }
                    }
                    p.removeResponseHandler(this);
                }
                this.timeoutConnections();
                if (folder == null) {
                    continue;
                }
                if (this.pool.folders == null) {
                    this.pool.folders = (Vector<IMAPFolder>)new Vector();
                }
                this.pool.folders.addElement(folder);
            }
        }
        return p;
    }
    
    private IMAPProtocol getStoreProtocol() throws ProtocolException {
        IMAPProtocol p = null;
        while (p == null) {
            synchronized (this.pool) {
                this.waitIfIdle();
                if (this.pool.authenticatedConnections.isEmpty()) {
                    this.pool.logger.fine("getStoreProtocol() - no connections in the pool, creating a new one");
                    try {
                        if (this.forcePasswordRefresh) {
                            this.refreshPassword();
                        }
                        p = this.newIMAPProtocol(this.host, this.port);
                        this.login(p, this.user, this.password);
                    }
                    catch (final Exception ex1) {
                        if (p != null) {
                            try {
                                p.logout();
                            }
                            catch (final Exception ex3) {}
                        }
                        p = null;
                    }
                    if (p == null) {
                        throw new ConnectionException("failed to create new store connection");
                    }
                    p.addResponseHandler(this);
                    this.pool.authenticatedConnections.addElement(p);
                }
                else {
                    if (this.pool.logger.isLoggable(Level.FINE)) {
                        this.pool.logger.fine("getStoreProtocol() - connection available -- size: " + this.pool.authenticatedConnections.size());
                    }
                    p = this.pool.authenticatedConnections.firstElement();
                    if (this.proxyAuthUser != null && !this.proxyAuthUser.equals(p.getProxyAuthUser()) && p.hasCapability("X-UNAUTHENTICATE")) {
                        p.unauthenticate();
                        this.login(p, this.user, this.password);
                    }
                }
                Label_0313: {
                    if (this.pool.storeConnectionInUse) {
                        try {
                            p = null;
                            this.pool.wait();
                            break Label_0313;
                        }
                        catch (final InterruptedException ex2) {
                            Thread.currentThread().interrupt();
                            throw new ProtocolException("Interrupted getStoreProtocol", ex2);
                        }
                    }
                    this.pool.storeConnectionInUse = true;
                    this.pool.logger.fine("getStoreProtocol() -- storeConnectionInUse");
                }
                this.timeoutConnections();
            }
        }
        return p;
    }
    
    IMAPProtocol getFolderStoreProtocol() throws ProtocolException {
        final IMAPProtocol p = this.getStoreProtocol();
        p.removeResponseHandler(this);
        p.addResponseHandler(this.nonStoreResponseHandler);
        return p;
    }
    
    private void refreshPassword() {
        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.fine("refresh password, user: " + this.traceUser(this.user));
        }
        InetAddress addr;
        try {
            addr = InetAddress.getByName(this.host);
        }
        catch (final UnknownHostException e) {
            addr = null;
        }
        final PasswordAuthentication pa = this.session.requestPasswordAuthentication(addr, this.port, this.name, null, this.user);
        if (pa != null) {
            this.user = pa.getUserName();
            this.password = pa.getPassword();
        }
    }
    
    boolean allowReadOnlySelect() {
        return PropUtil.getBooleanProperty(this.session.getProperties(), "mail." + this.name + ".allowreadonlyselect", false);
    }
    
    boolean hasSeparateStoreConnection() {
        return this.pool.separateStoreConnection;
    }
    
    MailLogger getConnectionPoolLogger() {
        return this.pool.logger;
    }
    
    boolean getMessageCacheDebug() {
        return this.messageCacheDebug;
    }
    
    boolean isConnectionPoolFull() {
        synchronized (this.pool) {
            if (this.pool.logger.isLoggable(Level.FINE)) {
                this.pool.logger.fine("connection pool current size: " + this.pool.authenticatedConnections.size() + "   pool size: " + this.pool.poolSize);
            }
            return this.pool.authenticatedConnections.size() >= this.pool.poolSize;
        }
    }
    
    void releaseProtocol(final IMAPFolder folder, final IMAPProtocol protocol) {
        synchronized (this.pool) {
            if (protocol != null) {
                if (!this.isConnectionPoolFull()) {
                    protocol.addResponseHandler(this);
                    this.pool.authenticatedConnections.addElement(protocol);
                    if (this.logger.isLoggable(Level.FINE)) {
                        this.logger.fine("added an Authenticated connection -- size: " + this.pool.authenticatedConnections.size());
                    }
                }
                else {
                    this.logger.fine("pool is full, not adding an Authenticated connection");
                    try {
                        protocol.logout();
                    }
                    catch (final ProtocolException ex) {}
                }
            }
            if (this.pool.folders != null) {
                this.pool.folders.removeElement(folder);
            }
            this.timeoutConnections();
        }
    }
    
    private void releaseStoreProtocol(final IMAPProtocol protocol) {
        if (protocol == null) {
            this.cleanup();
            return;
        }
        final boolean failed;
        synchronized (this.connectionFailedLock) {
            failed = this.connectionFailed;
            this.connectionFailed = false;
        }
        synchronized (this.pool) {
            this.pool.storeConnectionInUse = false;
            this.pool.notifyAll();
            this.pool.logger.fine("releaseStoreProtocol()");
            this.timeoutConnections();
        }
        assert !Thread.holdsLock(this.pool);
        if (failed) {
            this.cleanup();
        }
    }
    
    void releaseFolderStoreProtocol(final IMAPProtocol protocol) {
        if (protocol == null) {
            return;
        }
        protocol.removeResponseHandler(this.nonStoreResponseHandler);
        protocol.addResponseHandler(this);
        synchronized (this.pool) {
            this.pool.storeConnectionInUse = false;
            this.pool.notifyAll();
            this.pool.logger.fine("releaseFolderStoreProtocol()");
            this.timeoutConnections();
        }
    }
    
    private void emptyConnectionPool(final boolean force) {
        synchronized (this.pool) {
            for (int index = this.pool.authenticatedConnections.size() - 1; index >= 0; --index) {
                try {
                    final IMAPProtocol p = this.pool.authenticatedConnections.elementAt(index);
                    p.removeResponseHandler(this);
                    if (force) {
                        p.disconnect();
                    }
                    else {
                        p.logout();
                    }
                }
                catch (final ProtocolException ex) {}
            }
            this.pool.authenticatedConnections.removeAllElements();
        }
        this.pool.logger.fine("removed all authenticated connections from pool");
    }
    
    private void timeoutConnections() {
        synchronized (this.pool) {
            if (System.currentTimeMillis() - this.pool.lastTimePruned > this.pool.pruningInterval && this.pool.authenticatedConnections.size() > 1) {
                if (this.pool.logger.isLoggable(Level.FINE)) {
                    this.pool.logger.fine("checking for connections to prune: " + (System.currentTimeMillis() - this.pool.lastTimePruned));
                    this.pool.logger.fine("clientTimeoutInterval: " + this.pool.clientTimeoutInterval);
                }
                for (int index = this.pool.authenticatedConnections.size() - 1; index > 0; --index) {
                    final IMAPProtocol p = this.pool.authenticatedConnections.elementAt(index);
                    if (this.pool.logger.isLoggable(Level.FINE)) {
                        this.pool.logger.fine("protocol last used: " + (System.currentTimeMillis() - p.getTimestamp()));
                    }
                    if (System.currentTimeMillis() - p.getTimestamp() > this.pool.clientTimeoutInterval) {
                        this.pool.logger.fine("authenticated connection timed out, logging out the connection");
                        p.removeResponseHandler(this);
                        this.pool.authenticatedConnections.removeElementAt(index);
                        try {
                            p.logout();
                        }
                        catch (final ProtocolException ex) {}
                    }
                }
                this.pool.lastTimePruned = System.currentTimeMillis();
            }
        }
    }
    
    int getFetchBlockSize() {
        return this.blksize;
    }
    
    boolean ignoreBodyStructureSize() {
        return this.ignoreSize;
    }
    
    @Override
    Session getSession() {
        return this.session;
    }
    
    int getStatusCacheTimeout() {
        return this.statusCacheTimeout;
    }
    
    int getAppendBufferSize() {
        return this.appendBufferSize;
    }
    
    int getMinIdleTime() {
        return this.minIdleTime;
    }
    
    boolean throwSearchException() {
        return this.throwSearchException;
    }
    
    boolean getPeek() {
        return this.peek;
    }
    
    public synchronized boolean hasCapability(final String capability) throws MessagingException {
        IMAPProtocol p = null;
        try {
            p = this.getStoreProtocol();
            return p.hasCapability(capability);
        }
        catch (final ProtocolException pex) {
            throw new MessagingException(pex.getMessage(), pex);
        }
        finally {
            this.releaseStoreProtocol(p);
        }
    }
    
    public void setProxyAuthUser(final String user) {
        this.proxyAuthUser = user;
    }
    
    public String getProxyAuthUser() {
        return this.proxyAuthUser;
    }
    
    @Override
    public synchronized boolean isConnected() {
        if (!super.isConnected()) {
            return false;
        }
        IMAPProtocol p = null;
        try {
            p = this.getStoreProtocol();
            p.noop();
        }
        catch (final ProtocolException ex) {}
        finally {
            this.releaseStoreProtocol(p);
        }
        return super.isConnected();
    }
    
    @Override
    public synchronized void close() throws MessagingException {
        this.cleanup();
        this.closeAllFolders(false);
        this.emptyConnectionPool(false);
    }
    
    @Override
    protected void finalize() throws Throwable {
        if (!this.finalizeCleanClose) {
            synchronized (this.connectionFailedLock) {
                this.connectionFailed = true;
                this.forceClose = true;
            }
            this.closeFoldersOnStoreFailure = true;
        }
        try {
            this.close();
        }
        finally {
            super.finalize();
        }
    }
    
    private synchronized void cleanup() {
        if (!super.isConnected()) {
            this.logger.fine("IMAPStore cleanup, not connected");
            return;
        }
        final boolean force;
        synchronized (this.connectionFailedLock) {
            force = this.forceClose;
            this.forceClose = false;
            this.connectionFailed = false;
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.fine("IMAPStore cleanup, force " + force);
        }
        if (!force || this.closeFoldersOnStoreFailure) {
            this.closeAllFolders(force);
        }
        this.emptyConnectionPool(force);
        try {
            super.close();
        }
        catch (final MessagingException ex) {}
        this.logger.fine("IMAPStore cleanup done");
    }
    
    private void closeAllFolders(final boolean force) {
        List<IMAPFolder> foldersCopy = null;
        boolean done = true;
        while (true) {
            synchronized (this.pool) {
                if (this.pool.folders != null) {
                    done = false;
                    foldersCopy = this.pool.folders;
                    this.pool.folders = null;
                }
                else {
                    done = true;
                }
            }
            if (done) {
                break;
            }
            for (int i = 0, fsize = foldersCopy.size(); i < fsize; ++i) {
                final IMAPFolder f = foldersCopy.get(i);
                try {
                    if (force) {
                        this.logger.fine("force folder to close");
                        f.forceClose();
                    }
                    else {
                        this.logger.fine("close folder");
                        f.close(false);
                    }
                }
                catch (final MessagingException ex) {}
                catch (final IllegalStateException ex2) {}
            }
        }
    }
    
    @Override
    public synchronized Folder getDefaultFolder() throws MessagingException {
        this.checkConnected();
        return new DefaultFolder(this);
    }
    
    @Override
    public synchronized Folder getFolder(final String name) throws MessagingException {
        this.checkConnected();
        return this.newIMAPFolder(name, '\uffff');
    }
    
    @Override
    public synchronized Folder getFolder(final URLName url) throws MessagingException {
        this.checkConnected();
        return this.newIMAPFolder(url.getFile(), '\uffff');
    }
    
    protected IMAPFolder newIMAPFolder(final String fullName, final char separator, final Boolean isNamespace) {
        IMAPFolder f = null;
        if (this.folderConstructor != null) {
            try {
                final Object[] o = { fullName, separator, this, isNamespace };
                f = (IMAPFolder)this.folderConstructor.newInstance(o);
            }
            catch (final Exception ex) {
                this.logger.log(Level.FINE, "exception creating IMAPFolder class", ex);
            }
        }
        if (f == null) {
            f = new IMAPFolder(fullName, separator, this, isNamespace);
        }
        return f;
    }
    
    protected IMAPFolder newIMAPFolder(final String fullName, final char separator) {
        return this.newIMAPFolder(fullName, separator, null);
    }
    
    protected IMAPFolder newIMAPFolder(final ListInfo li) {
        IMAPFolder f = null;
        if (this.folderConstructorLI != null) {
            try {
                final Object[] o = { li, this };
                f = (IMAPFolder)this.folderConstructorLI.newInstance(o);
            }
            catch (final Exception ex) {
                this.logger.log(Level.FINE, "exception creating IMAPFolder class LI", ex);
            }
        }
        if (f == null) {
            f = new IMAPFolder(li, this);
        }
        return f;
    }
    
    @Override
    public Folder[] getPersonalNamespaces() throws MessagingException {
        final Namespaces ns = this.getNamespaces();
        if (ns == null || ns.personal == null) {
            return super.getPersonalNamespaces();
        }
        return this.namespaceToFolders(ns.personal, null);
    }
    
    @Override
    public Folder[] getUserNamespaces(final String user) throws MessagingException {
        final Namespaces ns = this.getNamespaces();
        if (ns == null || ns.otherUsers == null) {
            return super.getUserNamespaces(user);
        }
        return this.namespaceToFolders(ns.otherUsers, user);
    }
    
    @Override
    public Folder[] getSharedNamespaces() throws MessagingException {
        final Namespaces ns = this.getNamespaces();
        if (ns == null || ns.shared == null) {
            return super.getSharedNamespaces();
        }
        return this.namespaceToFolders(ns.shared, null);
    }
    
    private synchronized Namespaces getNamespaces() throws MessagingException {
        this.checkConnected();
        IMAPProtocol p = null;
        if (this.namespaces == null) {
            try {
                p = this.getStoreProtocol();
                this.namespaces = p.namespace();
            }
            catch (final BadCommandException ex) {}
            catch (final ConnectionException cex) {
                throw new StoreClosedException(this, cex.getMessage());
            }
            catch (final ProtocolException pex) {
                throw new MessagingException(pex.getMessage(), pex);
            }
            finally {
                this.releaseStoreProtocol(p);
            }
        }
        return this.namespaces;
    }
    
    private Folder[] namespaceToFolders(final Namespaces.Namespace[] ns, final String user) {
        final Folder[] fa = new Folder[ns.length];
        for (int i = 0; i < fa.length; ++i) {
            String name = ns[i].prefix;
            if (user == null) {
                final int len = name.length();
                if (len > 0 && name.charAt(len - 1) == ns[i].delimiter) {
                    name = name.substring(0, len - 1);
                }
            }
            else {
                name += user;
            }
            fa[i] = this.newIMAPFolder(name, ns[i].delimiter, user == null);
        }
        return fa;
    }
    
    @Override
    public synchronized Quota[] getQuota(final String root) throws MessagingException {
        this.checkConnected();
        Quota[] qa = null;
        IMAPProtocol p = null;
        try {
            p = this.getStoreProtocol();
            qa = p.getQuotaRoot(root);
        }
        catch (final BadCommandException bex) {
            throw new MessagingException("QUOTA not supported", bex);
        }
        catch (final ConnectionException cex) {
            throw new StoreClosedException(this, cex.getMessage());
        }
        catch (final ProtocolException pex) {
            throw new MessagingException(pex.getMessage(), pex);
        }
        finally {
            this.releaseStoreProtocol(p);
        }
        return qa;
    }
    
    @Override
    public synchronized void setQuota(final Quota quota) throws MessagingException {
        this.checkConnected();
        IMAPProtocol p = null;
        try {
            p = this.getStoreProtocol();
            p.setQuota(quota);
        }
        catch (final BadCommandException bex) {
            throw new MessagingException("QUOTA not supported", bex);
        }
        catch (final ConnectionException cex) {
            throw new StoreClosedException(this, cex.getMessage());
        }
        catch (final ProtocolException pex) {
            throw new MessagingException(pex.getMessage(), pex);
        }
        finally {
            this.releaseStoreProtocol(p);
        }
    }
    
    private void checkConnected() {
        assert Thread.holdsLock(this);
        if (!super.isConnected()) {
            throw new IllegalStateException("Not connected");
        }
    }
    
    @Override
    public void handleResponse(final Response r) {
        if (r.isOK() || r.isNO() || r.isBAD() || r.isBYE()) {
            this.handleResponseCode(r);
        }
        if (r.isBYE()) {
            this.logger.fine("IMAPStore connection dead");
            synchronized (this.connectionFailedLock) {
                this.connectionFailed = true;
                if (r.isSynthetic()) {
                    this.forceClose = true;
                }
            }
        }
    }
    
    public void idle() throws MessagingException {
        IMAPProtocol p = null;
        assert !Thread.holdsLock(this.pool);
        synchronized (this) {
            this.checkConnected();
        }
        boolean needNotification = false;
        try {
            synchronized (this.pool) {
                p = this.getStoreProtocol();
                if (this.pool.idleState != 0) {
                    try {
                        this.pool.wait();
                    }
                    catch (final InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        throw new MessagingException("idle interrupted", ex);
                    }
                    return;
                }
                p.idleStart();
                needNotification = true;
                this.pool.idleState = 1;
                this.pool.idleProtocol = p;
            }
            while (true) {
                final Response r = p.readIdleResponse();
                synchronized (this.pool) {
                    if (r == null || !p.processIdleResponse(r)) {
                        this.pool.idleState = 0;
                        this.pool.idleProtocol = null;
                        this.pool.notifyAll();
                        needNotification = false;
                        break;
                    }
                }
                if (this.enableImapEvents && r.isUnTagged()) {
                    this.notifyStoreListeners(1000, r.toString());
                }
            }
            final int minidle = this.getMinIdleTime();
            if (minidle > 0) {
                try {
                    Thread.sleep(minidle);
                }
                catch (final InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        catch (final BadCommandException bex) {
            throw new MessagingException("IDLE not supported", bex);
        }
        catch (final ConnectionException cex) {
            throw new StoreClosedException(this, cex.getMessage());
        }
        catch (final ProtocolException pex) {
            throw new MessagingException(pex.getMessage(), pex);
        }
        finally {
            if (needNotification) {
                synchronized (this.pool) {
                    this.pool.idleState = 0;
                    this.pool.idleProtocol = null;
                    this.pool.notifyAll();
                }
            }
            this.releaseStoreProtocol(p);
        }
    }
    
    private void waitIfIdle() throws ProtocolException {
        assert Thread.holdsLock(this.pool);
        while (this.pool.idleState != 0) {
            if (this.pool.idleState == 1) {
                this.pool.idleProtocol.idleAbort();
                this.pool.idleState = 2;
            }
            try {
                this.pool.wait();
                continue;
            }
            catch (final InterruptedException ex) {
                throw new ProtocolException("Interrupted waitIfIdle", ex);
            }
            break;
        }
    }
    
    public synchronized Map<String, String> id(final Map<String, String> clientParams) throws MessagingException {
        this.checkConnected();
        Map<String, String> serverParams = null;
        IMAPProtocol p = null;
        try {
            p = this.getStoreProtocol();
            serverParams = p.id(clientParams);
        }
        catch (final BadCommandException bex) {
            throw new MessagingException("ID not supported", bex);
        }
        catch (final ConnectionException cex) {
            throw new StoreClosedException(this, cex.getMessage());
        }
        catch (final ProtocolException pex) {
            throw new MessagingException(pex.getMessage(), pex);
        }
        finally {
            this.releaseStoreProtocol(p);
        }
        return serverParams;
    }
    
    void handleResponseCode(final Response r) {
        if (this.enableResponseEvents) {
            this.notifyStoreListeners(1000, r.toString());
        }
        String s = r.getRest();
        boolean isAlert = false;
        if (s.startsWith("[")) {
            final int i = s.indexOf(93);
            if (i > 0 && s.substring(0, i + 1).equalsIgnoreCase("[ALERT]")) {
                isAlert = true;
            }
            s = s.substring(i + 1).trim();
        }
        if (isAlert) {
            this.notifyStoreListeners(1, s);
        }
        else if (r.isUnTagged() && s.length() > 0) {
            this.notifyStoreListeners(2, s);
        }
    }
    
    private String traceUser(final String user) {
        return this.debugusername ? user : "<user name suppressed>";
    }
    
    private String tracePassword(final String password) {
        return this.debugpassword ? password : ((password == null) ? "<null>" : "<non-null>");
    }
    
    static class ConnectionPool
    {
        private Vector<IMAPProtocol> authenticatedConnections;
        private Vector<IMAPFolder> folders;
        private boolean storeConnectionInUse;
        private long lastTimePruned;
        private final boolean separateStoreConnection;
        private final long clientTimeoutInterval;
        private final long serverTimeoutInterval;
        private final int poolSize;
        private final long pruningInterval;
        private final MailLogger logger;
        private static final int RUNNING = 0;
        private static final int IDLE = 1;
        private static final int ABORTING = 2;
        private int idleState;
        private IMAPProtocol idleProtocol;
        
        ConnectionPool(final String name, final MailLogger plogger, final Session session) {
            this.authenticatedConnections = new Vector<IMAPProtocol>();
            this.storeConnectionInUse = false;
            this.idleState = 0;
            this.lastTimePruned = System.currentTimeMillis();
            final Properties props = session.getProperties();
            final boolean debug = PropUtil.getBooleanProperty(props, "mail." + name + ".connectionpool.debug", false);
            this.logger = plogger.getSubLogger("connectionpool", "DEBUG IMAP CP", debug);
            final int size = PropUtil.getIntProperty(props, "mail." + name + ".connectionpoolsize", -1);
            if (size > 0) {
                this.poolSize = size;
                if (this.logger.isLoggable(Level.CONFIG)) {
                    this.logger.config("mail.imap.connectionpoolsize: " + this.poolSize);
                }
            }
            else {
                this.poolSize = 1;
            }
            final int connectionPoolTimeout = PropUtil.getIntProperty(props, "mail." + name + ".connectionpooltimeout", -1);
            if (connectionPoolTimeout > 0) {
                this.clientTimeoutInterval = connectionPoolTimeout;
                if (this.logger.isLoggable(Level.CONFIG)) {
                    this.logger.config("mail.imap.connectionpooltimeout: " + this.clientTimeoutInterval);
                }
            }
            else {
                this.clientTimeoutInterval = 45000L;
            }
            final int serverTimeout = PropUtil.getIntProperty(props, "mail." + name + ".servertimeout", -1);
            if (serverTimeout > 0) {
                this.serverTimeoutInterval = serverTimeout;
                if (this.logger.isLoggable(Level.CONFIG)) {
                    this.logger.config("mail.imap.servertimeout: " + this.serverTimeoutInterval);
                }
            }
            else {
                this.serverTimeoutInterval = 1800000L;
            }
            final int pruning = PropUtil.getIntProperty(props, "mail." + name + ".pruninginterval", -1);
            if (pruning > 0) {
                this.pruningInterval = pruning;
                if (this.logger.isLoggable(Level.CONFIG)) {
                    this.logger.config("mail.imap.pruninginterval: " + this.pruningInterval);
                }
            }
            else {
                this.pruningInterval = 60000L;
            }
            this.separateStoreConnection = PropUtil.getBooleanProperty(props, "mail." + name + ".separatestoreconnection", false);
            if (this.separateStoreConnection) {
                this.logger.config("dedicate a store connection");
            }
        }
    }
}
