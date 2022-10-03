package com.sun.mail.smtp;

import com.sun.mail.auth.Ntlm;
import java.io.ByteArrayOutputStream;
import com.sun.mail.util.BASE64EncoderStream;
import com.sun.mail.util.ASCIIUtility;
import java.nio.charset.StandardCharsets;
import java.io.BufferedOutputStream;
import com.sun.mail.util.SocketConnectException;
import com.sun.mail.util.MailConnectException;
import com.sun.mail.util.SocketFetcher;
import javax.mail.internet.AddressException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.StringReader;
import java.io.InputStream;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.ParseException;
import java.io.IOException;
import javax.mail.internet.MimePart;
import javax.mail.SendFailedException;
import javax.mail.internet.InternetAddress;
import javax.mail.Message;
import java.lang.reflect.Constructor;
import javax.mail.AuthenticationFailedException;
import java.util.Locale;
import javax.net.ssl.SSLSocket;
import java.util.List;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.util.Properties;
import java.util.logging.Level;
import com.sun.mail.util.PropUtil;
import java.util.HashMap;
import javax.mail.URLName;
import javax.mail.Session;
import com.sun.mail.util.TraceOutputStream;
import com.sun.mail.util.TraceInputStream;
import java.net.Socket;
import java.io.OutputStream;
import com.sun.mail.util.LineInputStream;
import java.io.BufferedInputStream;
import com.sun.mail.util.MailLogger;
import java.util.Map;
import java.util.Hashtable;
import javax.mail.MessagingException;
import javax.mail.Address;
import javax.mail.internet.MimeMessage;
import javax.mail.Transport;

public class SMTPTransport extends Transport
{
    private String name;
    private int defaultPort;
    private boolean isSSL;
    private String host;
    private MimeMessage message;
    private Address[] addresses;
    private Address[] validSentAddr;
    private Address[] validUnsentAddr;
    private Address[] invalidAddr;
    private boolean sendPartiallyFailed;
    private MessagingException exception;
    private SMTPOutputStream dataStream;
    private Hashtable<String, String> extMap;
    private Map<String, Authenticator> authenticators;
    private String defaultAuthenticationMechanisms;
    private boolean quitWait;
    private String saslRealm;
    private String authorizationID;
    private boolean enableSASL;
    private boolean useCanonicalHostName;
    private String[] saslMechanisms;
    private String ntlmDomain;
    private boolean reportSuccess;
    private boolean useStartTLS;
    private boolean requireStartTLS;
    private boolean useRset;
    private boolean noopStrict;
    private MailLogger logger;
    private MailLogger traceLogger;
    private String localHostName;
    private String lastServerResponse;
    private int lastReturnCode;
    private boolean notificationDone;
    private SaslAuthenticator saslAuthenticator;
    private boolean noauthdebug;
    private boolean debugusername;
    private boolean debugpassword;
    private boolean allowutf8;
    private int chunkSize;
    private static final String[] ignoreList;
    private static final byte[] CRLF;
    private static final String UNKNOWN = "UNKNOWN";
    private static final String[] UNKNOWN_SA;
    private BufferedInputStream serverInput;
    private LineInputStream lineInputStream;
    private OutputStream serverOutput;
    private Socket serverSocket;
    private TraceInputStream traceInput;
    private TraceOutputStream traceOutput;
    private static char[] hexchar;
    
    public SMTPTransport(final Session session, final URLName urlname) {
        this(session, urlname, "smtp", false);
    }
    
    protected SMTPTransport(final Session session, final URLName urlname, String name, boolean isSSL) {
        super(session, urlname);
        this.name = "smtp";
        this.defaultPort = 25;
        this.isSSL = false;
        this.sendPartiallyFailed = false;
        this.authenticators = new HashMap<String, Authenticator>();
        this.quitWait = false;
        this.saslRealm = "UNKNOWN";
        this.authorizationID = "UNKNOWN";
        this.enableSASL = false;
        this.useCanonicalHostName = false;
        this.saslMechanisms = SMTPTransport.UNKNOWN_SA;
        this.ntlmDomain = "UNKNOWN";
        this.noopStrict = true;
        this.noauthdebug = true;
        final Properties props = session.getProperties();
        this.logger = new MailLogger(this.getClass(), "DEBUG SMTP", session.getDebug(), session.getDebugOut());
        this.traceLogger = this.logger.getSubLogger("protocol", null);
        this.noauthdebug = !PropUtil.getBooleanProperty(props, "mail.debug.auth", false);
        this.debugusername = PropUtil.getBooleanProperty(props, "mail.debug.auth.username", true);
        this.debugpassword = PropUtil.getBooleanProperty(props, "mail.debug.auth.password", false);
        if (urlname != null) {
            name = urlname.getProtocol();
        }
        this.name = name;
        if (!isSSL) {
            isSSL = PropUtil.getBooleanProperty(props, "mail." + name + ".ssl.enable", false);
        }
        if (isSSL) {
            this.defaultPort = 465;
        }
        else {
            this.defaultPort = 25;
        }
        this.isSSL = isSSL;
        this.quitWait = PropUtil.getBooleanProperty(props, "mail." + name + ".quitwait", true);
        this.reportSuccess = PropUtil.getBooleanProperty(props, "mail." + name + ".reportsuccess", false);
        this.useStartTLS = PropUtil.getBooleanProperty(props, "mail." + name + ".starttls.enable", false);
        this.requireStartTLS = PropUtil.getBooleanProperty(props, "mail." + name + ".starttls.required", false);
        this.useRset = PropUtil.getBooleanProperty(props, "mail." + name + ".userset", false);
        this.noopStrict = PropUtil.getBooleanProperty(props, "mail." + name + ".noop.strict", true);
        this.enableSASL = PropUtil.getBooleanProperty(props, "mail." + name + ".sasl.enable", false);
        if (this.enableSASL) {
            this.logger.config("enable SASL");
        }
        this.useCanonicalHostName = PropUtil.getBooleanProperty(props, "mail." + name + ".sasl.usecanonicalhostname", false);
        if (this.useCanonicalHostName) {
            this.logger.config("use canonical host name");
        }
        this.allowutf8 = PropUtil.getBooleanProperty(props, "mail.mime.allowutf8", false);
        if (this.allowutf8) {
            this.logger.config("allow UTF-8");
        }
        this.chunkSize = PropUtil.getIntProperty(props, "mail." + name + ".chunksize", -1);
        if (this.chunkSize > 0 && this.logger.isLoggable(Level.CONFIG)) {
            this.logger.config("chunk size " + this.chunkSize);
        }
        final Authenticator[] a = { new LoginAuthenticator(), new PlainAuthenticator(), new DigestMD5Authenticator(), new NtlmAuthenticator(), new OAuth2Authenticator() };
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < a.length; ++i) {
            this.authenticators.put(a[i].getMechanism(), a[i]);
            sb.append(a[i].getMechanism()).append(' ');
        }
        this.defaultAuthenticationMechanisms = sb.toString();
    }
    
    public synchronized String getLocalHost() {
        if (this.localHostName == null || this.localHostName.length() <= 0) {
            this.localHostName = this.session.getProperty("mail." + this.name + ".localhost");
        }
        if (this.localHostName == null || this.localHostName.length() <= 0) {
            this.localHostName = this.session.getProperty("mail." + this.name + ".localaddress");
        }
        try {
            if (this.localHostName == null || this.localHostName.length() <= 0) {
                final InetAddress localHost = InetAddress.getLocalHost();
                this.localHostName = localHost.getCanonicalHostName();
                if (this.localHostName == null) {
                    this.localHostName = "[" + localHost.getHostAddress() + "]";
                }
            }
        }
        catch (final UnknownHostException ex) {}
        if ((this.localHostName == null || this.localHostName.length() <= 0) && this.serverSocket != null && this.serverSocket.isBound()) {
            final InetAddress localHost = this.serverSocket.getLocalAddress();
            this.localHostName = localHost.getCanonicalHostName();
            if (this.localHostName == null) {
                this.localHostName = "[" + localHost.getHostAddress() + "]";
            }
        }
        return this.localHostName;
    }
    
    public synchronized void setLocalHost(final String localhost) {
        this.localHostName = localhost;
    }
    
    public synchronized void connect(final Socket socket) throws MessagingException {
        this.serverSocket = socket;
        super.connect();
    }
    
    public synchronized String getAuthorizationId() {
        if (this.authorizationID == "UNKNOWN") {
            this.authorizationID = this.session.getProperty("mail." + this.name + ".sasl.authorizationid");
        }
        return this.authorizationID;
    }
    
    public synchronized void setAuthorizationID(final String authzid) {
        this.authorizationID = authzid;
    }
    
    public synchronized boolean getSASLEnabled() {
        return this.enableSASL;
    }
    
    public synchronized void setSASLEnabled(final boolean enableSASL) {
        this.enableSASL = enableSASL;
    }
    
    public synchronized String getSASLRealm() {
        if (this.saslRealm == "UNKNOWN") {
            this.saslRealm = this.session.getProperty("mail." + this.name + ".sasl.realm");
            if (this.saslRealm == null) {
                this.saslRealm = this.session.getProperty("mail." + this.name + ".saslrealm");
            }
        }
        return this.saslRealm;
    }
    
    public synchronized void setSASLRealm(final String saslRealm) {
        this.saslRealm = saslRealm;
    }
    
    public synchronized boolean getUseCanonicalHostName() {
        return this.useCanonicalHostName;
    }
    
    public synchronized void setUseCanonicalHostName(final boolean useCanonicalHostName) {
        this.useCanonicalHostName = useCanonicalHostName;
    }
    
    public synchronized String[] getSASLMechanisms() {
        if (this.saslMechanisms == SMTPTransport.UNKNOWN_SA) {
            final List<String> v = new ArrayList<String>(5);
            final String s = this.session.getProperty("mail." + this.name + ".sasl.mechanisms");
            if (s != null && s.length() > 0) {
                if (this.logger.isLoggable(Level.FINE)) {
                    this.logger.fine("SASL mechanisms allowed: " + s);
                }
                final StringTokenizer st = new StringTokenizer(s, " ,");
                while (st.hasMoreTokens()) {
                    final String m = st.nextToken();
                    if (m.length() > 0) {
                        v.add(m);
                    }
                }
            }
            v.toArray(this.saslMechanisms = new String[v.size()]);
        }
        if (this.saslMechanisms == null) {
            return null;
        }
        return this.saslMechanisms.clone();
    }
    
    public synchronized void setSASLMechanisms(String[] mechanisms) {
        if (mechanisms != null) {
            mechanisms = mechanisms.clone();
        }
        this.saslMechanisms = mechanisms;
    }
    
    public synchronized String getNTLMDomain() {
        if (this.ntlmDomain == "UNKNOWN") {
            this.ntlmDomain = this.session.getProperty("mail." + this.name + ".auth.ntlm.domain");
        }
        return this.ntlmDomain;
    }
    
    public synchronized void setNTLMDomain(final String ntlmDomain) {
        this.ntlmDomain = ntlmDomain;
    }
    
    public synchronized boolean getReportSuccess() {
        return this.reportSuccess;
    }
    
    public synchronized void setReportSuccess(final boolean reportSuccess) {
        this.reportSuccess = reportSuccess;
    }
    
    public synchronized boolean getStartTLS() {
        return this.useStartTLS;
    }
    
    public synchronized void setStartTLS(final boolean useStartTLS) {
        this.useStartTLS = useStartTLS;
    }
    
    public synchronized boolean getRequireStartTLS() {
        return this.requireStartTLS;
    }
    
    public synchronized void setRequireStartTLS(final boolean requireStartTLS) {
        this.requireStartTLS = requireStartTLS;
    }
    
    public synchronized boolean isSSL() {
        return this.serverSocket instanceof SSLSocket;
    }
    
    public synchronized boolean getUseRset() {
        return this.useRset;
    }
    
    public synchronized void setUseRset(final boolean useRset) {
        this.useRset = useRset;
    }
    
    public synchronized boolean getNoopStrict() {
        return this.noopStrict;
    }
    
    public synchronized void setNoopStrict(final boolean noopStrict) {
        this.noopStrict = noopStrict;
    }
    
    public synchronized String getLastServerResponse() {
        return this.lastServerResponse;
    }
    
    public synchronized int getLastReturnCode() {
        return this.lastReturnCode;
    }
    
    @Override
    protected synchronized boolean protocolConnect(String host, int port, final String user, final String password) throws MessagingException {
        final Properties props = this.session.getProperties();
        final boolean useAuth = PropUtil.getBooleanProperty(props, "mail." + this.name + ".auth", false);
        if (useAuth && (user == null || password == null)) {
            if (this.logger.isLoggable(Level.FINE)) {
                this.logger.fine("need username and password for authentication");
                this.logger.fine("protocolConnect returning false, host=" + host + ", user=" + this.traceUser(user) + ", password=" + this.tracePassword(password));
            }
            return false;
        }
        final boolean useEhlo = PropUtil.getBooleanProperty(props, "mail." + this.name + ".ehlo", true);
        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.fine("useEhlo " + useEhlo + ", useAuth " + useAuth);
        }
        if (port == -1) {
            port = PropUtil.getIntProperty(props, "mail." + this.name + ".port", -1);
        }
        if (port == -1) {
            port = this.defaultPort;
        }
        if (host == null || host.length() == 0) {
            host = "localhost";
        }
        boolean connected = false;
        try {
            if (this.serverSocket != null) {
                this.openServer();
            }
            else {
                this.openServer(host, port);
            }
            boolean succeed = false;
            if (useEhlo) {
                succeed = this.ehlo(this.getLocalHost());
            }
            if (!succeed) {
                this.helo(this.getLocalHost());
            }
            if (this.useStartTLS || this.requireStartTLS) {
                if (this.serverSocket instanceof SSLSocket) {
                    this.logger.fine("STARTTLS requested but already using SSL");
                }
                else if (this.supportsExtension("STARTTLS")) {
                    this.startTLS();
                    this.ehlo(this.getLocalHost());
                }
                else if (this.requireStartTLS) {
                    this.logger.fine("STARTTLS required but not supported");
                    throw new MessagingException("STARTTLS is required but host does not support STARTTLS");
                }
            }
            if (this.allowutf8 && !this.supportsExtension("SMTPUTF8")) {
                this.logger.log(Level.INFO, "mail.mime.allowutf8 set but server doesn't advertise SMTPUTF8 support");
            }
            if ((useAuth || (user != null && password != null)) && (this.supportsExtension("AUTH") || this.supportsExtension("AUTH=LOGIN"))) {
                if (this.logger.isLoggable(Level.FINE)) {
                    this.logger.fine("protocolConnect login, host=" + host + ", user=" + this.traceUser(user) + ", password=" + this.tracePassword(password));
                }
                connected = this.authenticate(user, password);
                return connected;
            }
            connected = true;
            return true;
        }
        finally {
            if (!connected) {
                try {
                    this.closeConnection();
                }
                catch (final MessagingException ex) {}
            }
        }
    }
    
    private boolean authenticate(final String user, final String passwd) throws MessagingException {
        String mechs = this.session.getProperty("mail." + this.name + ".auth.mechanisms");
        if (mechs == null) {
            mechs = this.defaultAuthenticationMechanisms;
        }
        String authzid = this.getAuthorizationId();
        if (authzid == null) {
            authzid = user;
        }
        if (this.enableSASL) {
            this.logger.fine("Authenticate with SASL");
            try {
                if (this.sasllogin(this.getSASLMechanisms(), this.getSASLRealm(), authzid, user, passwd)) {
                    return true;
                }
                this.logger.fine("SASL authentication failed");
                return false;
            }
            catch (final UnsupportedOperationException ex) {
                this.logger.log(Level.FINE, "SASL support failed", ex);
            }
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.fine("Attempt to authenticate using mechanisms: " + mechs);
        }
        final StringTokenizer st = new StringTokenizer(mechs);
        while (st.hasMoreTokens()) {
            String m = st.nextToken();
            m = m.toUpperCase(Locale.ENGLISH);
            final Authenticator a = this.authenticators.get(m);
            if (a == null) {
                this.logger.log(Level.FINE, "no authenticator for mechanism {0}", m);
            }
            else {
                if (this.supportsAuthentication(m)) {
                    if (mechs == this.defaultAuthenticationMechanisms) {
                        final String dprop = "mail." + this.name + ".auth." + m.toLowerCase(Locale.ENGLISH) + ".disable";
                        final boolean disabled = PropUtil.getBooleanProperty(this.session.getProperties(), dprop, !a.enabled());
                        if (disabled) {
                            if (this.logger.isLoggable(Level.FINE)) {
                                this.logger.fine("mechanism " + m + " disabled by property: " + dprop);
                                continue;
                            }
                            continue;
                        }
                    }
                    this.logger.log(Level.FINE, "Using mechanism {0}", m);
                    return a.authenticate(this.host, authzid, user, passwd);
                }
                this.logger.log(Level.FINE, "mechanism {0} not supported by server", m);
            }
        }
        throw new AuthenticationFailedException("No authentication mechanisms supported by both server and client");
    }
    
    private boolean sasllogin(final String[] allowed, final String realm, final String authzid, final String u, final String p) throws MessagingException {
        String serviceHost;
        if (this.useCanonicalHostName) {
            serviceHost = this.serverSocket.getInetAddress().getCanonicalHostName();
        }
        else {
            serviceHost = this.host;
        }
        if (this.saslAuthenticator == null) {
            try {
                final Class<?> sac = Class.forName("com.sun.mail.smtp.SMTPSaslAuthenticator");
                final Constructor<?> c = sac.getConstructor(SMTPTransport.class, String.class, Properties.class, MailLogger.class, String.class);
                this.saslAuthenticator = (SaslAuthenticator)c.newInstance(this, this.name, this.session.getProperties(), this.logger, serviceHost);
            }
            catch (final Exception ex) {
                this.logger.log(Level.FINE, "Can't load SASL authenticator", ex);
                return false;
            }
        }
        List<String> v;
        if (allowed != null && allowed.length > 0) {
            v = new ArrayList<String>(allowed.length);
            for (int i = 0; i < allowed.length; ++i) {
                if (this.supportsAuthentication(allowed[i])) {
                    v.add(allowed[i]);
                }
            }
        }
        else {
            v = new ArrayList<String>();
            if (this.extMap != null) {
                final String a = this.extMap.get("AUTH");
                if (a != null) {
                    final StringTokenizer st = new StringTokenizer(a);
                    while (st.hasMoreTokens()) {
                        v.add(st.nextToken());
                    }
                }
            }
        }
        final String[] mechs = v.toArray(new String[v.size()]);
        try {
            if (this.noauthdebug && this.isTracing()) {
                this.logger.fine("SASL AUTH command trace suppressed");
                this.suspendTracing();
            }
            return this.saslAuthenticator.authenticate(mechs, realm, authzid, u, p);
        }
        finally {
            this.resumeTracing();
        }
    }
    
    @Override
    public synchronized void sendMessage(final Message message, final Address[] addresses) throws MessagingException, SendFailedException {
        this.sendMessageStart((message != null) ? message.getSubject() : "");
        this.checkConnected();
        if (!(message instanceof MimeMessage)) {
            this.logger.fine("Can only send RFC822 msgs");
            throw new MessagingException("SMTP can only send RFC822 messages");
        }
        for (int i = 0; i < addresses.length; ++i) {
            if (!(addresses[i] instanceof InternetAddress)) {
                throw new MessagingException(addresses[i] + " is not an InternetAddress");
            }
        }
        if (addresses.length == 0) {
            throw new SendFailedException("No recipient addresses");
        }
        this.message = (MimeMessage)message;
        this.addresses = addresses;
        this.validUnsentAddr = addresses;
        this.expandGroups();
        boolean use8bit = false;
        if (message instanceof SMTPMessage) {
            use8bit = ((SMTPMessage)message).getAllow8bitMIME();
        }
        if (!use8bit) {
            use8bit = PropUtil.getBooleanProperty(this.session.getProperties(), "mail." + this.name + ".allow8bitmime", false);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.fine("use8bit " + use8bit);
        }
        while (true) {
            if (use8bit && this.supportsExtension("8BITMIME") && this.convertTo8Bit(this.message)) {
                try {
                    this.message.saveChanges();
                }
                catch (final MessagingException ex2) {}
                try {
                    this.mailFrom();
                    this.rcptTo();
                    if (this.chunkSize > 0 && this.supportsExtension("CHUNKING")) {
                        this.message.writeTo(this.bdat(), SMTPTransport.ignoreList);
                        this.finishBdat();
                    }
                    else {
                        this.message.writeTo(this.data(), SMTPTransport.ignoreList);
                        this.finishData();
                    }
                    if (this.sendPartiallyFailed) {
                        this.logger.fine("Sending partially failed because of invalid destination addresses");
                        this.notifyTransportListeners(3, this.validSentAddr, this.validUnsentAddr, this.invalidAddr, this.message);
                        throw new SMTPSendFailedException(".", this.lastReturnCode, this.lastServerResponse, this.exception, this.validSentAddr, this.validUnsentAddr, this.invalidAddr);
                    }
                    this.logger.fine("message successfully delivered to mail server");
                    this.notifyTransportListeners(1, this.validSentAddr, this.validUnsentAddr, this.invalidAddr, this.message);
                }
                catch (final MessagingException mex) {
                    this.logger.log(Level.FINE, "MessagingException while sending", mex);
                    if (mex.getNextException() instanceof IOException) {
                        this.logger.fine("nested IOException, closing");
                        try {
                            this.closeConnection();
                        }
                        catch (final MessagingException ex3) {}
                    }
                    this.addressesFailed();
                    this.notifyTransportListeners(2, this.validSentAddr, this.validUnsentAddr, this.invalidAddr, this.message);
                    throw mex;
                }
                catch (final IOException ex) {
                    this.logger.log(Level.FINE, "IOException while sending, closing", ex);
                    try {
                        this.closeConnection();
                    }
                    catch (final MessagingException ex4) {}
                    this.addressesFailed();
                    this.notifyTransportListeners(2, this.validSentAddr, this.validUnsentAddr, this.invalidAddr, this.message);
                    throw new MessagingException("IOException while sending message", ex);
                }
                finally {
                    final Address[] validSentAddr = null;
                    this.invalidAddr = validSentAddr;
                    this.validUnsentAddr = validSentAddr;
                    this.validSentAddr = validSentAddr;
                    this.addresses = null;
                    this.message = null;
                    this.exception = null;
                    this.sendPartiallyFailed = false;
                    this.notificationDone = false;
                }
                this.sendMessageEnd();
                return;
            }
            continue;
        }
    }
    
    private void addressesFailed() {
        if (this.validSentAddr != null) {
            if (this.validUnsentAddr != null) {
                final Address[] newa = new Address[this.validSentAddr.length + this.validUnsentAddr.length];
                System.arraycopy(this.validSentAddr, 0, newa, 0, this.validSentAddr.length);
                System.arraycopy(this.validUnsentAddr, 0, newa, this.validSentAddr.length, this.validUnsentAddr.length);
                this.validSentAddr = null;
                this.validUnsentAddr = newa;
            }
            else {
                this.validUnsentAddr = this.validSentAddr;
                this.validSentAddr = null;
            }
        }
    }
    
    @Override
    public synchronized void close() throws MessagingException {
        if (!super.isConnected()) {
            return;
        }
        try {
            if (this.serverSocket != null) {
                this.sendCommand("QUIT");
                if (this.quitWait) {
                    final int resp = this.readServerResponse();
                    if (resp != 221 && resp != -1 && this.logger.isLoggable(Level.FINE)) {
                        this.logger.fine("QUIT failed with " + resp);
                    }
                }
            }
        }
        finally {
            this.closeConnection();
        }
    }
    
    private void closeConnection() throws MessagingException {
        try {
            if (this.serverSocket != null) {
                this.serverSocket.close();
            }
        }
        catch (final IOException ioex) {
            throw new MessagingException("Server Close Failed", ioex);
        }
        finally {
            this.serverSocket = null;
            this.serverOutput = null;
            this.serverInput = null;
            this.lineInputStream = null;
            if (super.isConnected()) {
                super.close();
            }
        }
    }
    
    @Override
    public synchronized boolean isConnected() {
        if (!super.isConnected()) {
            return false;
        }
        try {
            if (this.useRset) {
                this.sendCommand("RSET");
            }
            else {
                this.sendCommand("NOOP");
            }
            final int resp = this.readServerResponse();
            Label_0068: {
                if (resp >= 0) {
                    if (this.noopStrict) {
                        if (resp != 250) {
                            break Label_0068;
                        }
                    }
                    else if (resp == 421) {
                        break Label_0068;
                    }
                    return true;
                }
                try {
                    this.closeConnection();
                }
                catch (final MessagingException ex2) {}
            }
            return false;
        }
        catch (final Exception ex) {
            try {
                this.closeConnection();
            }
            catch (final MessagingException ex3) {}
            return false;
        }
    }
    
    @Override
    protected void notifyTransportListeners(final int type, final Address[] validSent, final Address[] validUnsent, final Address[] invalid, final Message msg) {
        if (!this.notificationDone) {
            super.notifyTransportListeners(type, validSent, validUnsent, invalid, msg);
            this.notificationDone = true;
        }
    }
    
    private void expandGroups() {
        List<Address> groups = null;
        for (int i = 0; i < this.addresses.length; ++i) {
            final InternetAddress a = (InternetAddress)this.addresses[i];
            if (a.isGroup()) {
                if (groups == null) {
                    groups = new ArrayList<Address>();
                    for (int k = 0; k < i; ++k) {
                        groups.add(this.addresses[k]);
                    }
                }
                try {
                    final InternetAddress[] ia = a.getGroup(true);
                    if (ia != null) {
                        for (int j = 0; j < ia.length; ++j) {
                            groups.add(ia[j]);
                        }
                    }
                    else {
                        groups.add(a);
                    }
                }
                catch (final ParseException pex) {
                    groups.add(a);
                }
            }
            else if (groups != null) {
                groups.add(a);
            }
        }
        if (groups != null) {
            final InternetAddress[] newa = new InternetAddress[groups.size()];
            groups.toArray(newa);
            this.addresses = newa;
        }
    }
    
    private boolean convertTo8Bit(final MimePart part) {
        boolean changed = false;
        try {
            if (part.isMimeType("text/*")) {
                final String enc = part.getEncoding();
                if (enc != null && (enc.equalsIgnoreCase("quoted-printable") || enc.equalsIgnoreCase("base64"))) {
                    InputStream is = null;
                    try {
                        is = part.getInputStream();
                        if (this.is8Bit(is)) {
                            part.setContent(part.getContent(), part.getContentType());
                            part.setHeader("Content-Transfer-Encoding", "8bit");
                            changed = true;
                        }
                    }
                    finally {
                        if (is != null) {
                            try {
                                is.close();
                            }
                            catch (final IOException ex) {}
                        }
                    }
                }
            }
            else if (part.isMimeType("multipart/*")) {
                final MimeMultipart mp = (MimeMultipart)part.getContent();
                for (int count = mp.getCount(), i = 0; i < count; ++i) {
                    if (this.convertTo8Bit((MimePart)mp.getBodyPart(i))) {
                        changed = true;
                    }
                }
            }
        }
        catch (final IOException ex2) {}
        catch (final MessagingException ex3) {}
        return changed;
    }
    
    private boolean is8Bit(final InputStream is) {
        int linelen = 0;
        boolean need8bit = false;
        try {
            int b;
            while ((b = is.read()) >= 0) {
                b &= 0xFF;
                if (b == 13 || b == 10) {
                    linelen = 0;
                }
                else {
                    if (b == 0) {
                        return false;
                    }
                    if (++linelen > 998) {
                        return false;
                    }
                }
                if (b > 127) {
                    need8bit = true;
                }
            }
        }
        catch (final IOException ex) {
            return false;
        }
        if (need8bit) {
            this.logger.fine("found an 8bit part");
        }
        return need8bit;
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            this.closeConnection();
        }
        catch (final MessagingException ex) {}
        finally {
            super.finalize();
        }
    }
    
    protected void helo(final String domain) throws MessagingException {
        if (domain != null) {
            this.issueCommand("HELO " + domain, 250);
        }
        else {
            this.issueCommand("HELO", 250);
        }
    }
    
    protected boolean ehlo(final String domain) throws MessagingException {
        String cmd;
        if (domain != null) {
            cmd = "EHLO " + domain;
        }
        else {
            cmd = "EHLO";
        }
        this.sendCommand(cmd);
        final int resp = this.readServerResponse();
        if (resp == 250) {
            final BufferedReader rd = new BufferedReader(new StringReader(this.lastServerResponse));
            this.extMap = new Hashtable<String, String>();
            try {
                boolean first = true;
                String line;
                while ((line = rd.readLine()) != null) {
                    if (first) {
                        first = false;
                    }
                    else {
                        if (line.length() < 5) {
                            continue;
                        }
                        line = line.substring(4);
                        final int i = line.indexOf(32);
                        String arg = "";
                        if (i > 0) {
                            arg = line.substring(i + 1);
                            line = line.substring(0, i);
                        }
                        if (this.logger.isLoggable(Level.FINE)) {
                            this.logger.fine("Found extension \"" + line + "\", arg \"" + arg + "\"");
                        }
                        this.extMap.put(line.toUpperCase(Locale.ENGLISH), arg);
                    }
                }
            }
            catch (final IOException ex) {}
        }
        return resp == 250;
    }
    
    protected void mailFrom() throws MessagingException {
        String from = null;
        if (this.message instanceof SMTPMessage) {
            from = ((SMTPMessage)this.message).getEnvelopeFrom();
        }
        if (from == null || from.length() <= 0) {
            from = this.session.getProperty("mail." + this.name + ".from");
        }
        if (from == null || from.length() <= 0) {
            final Address[] fa;
            Address me;
            if (this.message != null && (fa = this.message.getFrom()) != null && fa.length > 0) {
                me = fa[0];
            }
            else {
                me = InternetAddress.getLocalAddress(this.session);
            }
            if (me == null) {
                throw new MessagingException("can't determine local email address");
            }
            from = ((InternetAddress)me).getAddress();
        }
        String cmd = "MAIL FROM:" + this.normalizeAddress(from);
        if (this.allowutf8 && this.supportsExtension("SMTPUTF8")) {
            cmd += " SMTPUTF8";
        }
        if (this.supportsExtension("DSN")) {
            String ret = null;
            if (this.message instanceof SMTPMessage) {
                ret = ((SMTPMessage)this.message).getDSNRet();
            }
            if (ret == null) {
                ret = this.session.getProperty("mail." + this.name + ".dsn.ret");
            }
            if (ret != null) {
                cmd = cmd + " RET=" + ret;
            }
        }
        if (this.supportsExtension("AUTH")) {
            String submitter = null;
            if (this.message instanceof SMTPMessage) {
                submitter = ((SMTPMessage)this.message).getSubmitter();
            }
            if (submitter == null) {
                submitter = this.session.getProperty("mail." + this.name + ".submitter");
            }
            if (submitter != null) {
                try {
                    final String s = xtext(submitter, this.allowutf8 && this.supportsExtension("SMTPUTF8"));
                    cmd = cmd + " AUTH=" + s;
                }
                catch (final IllegalArgumentException ex) {
                    if (this.logger.isLoggable(Level.FINE)) {
                        this.logger.log(Level.FINE, "ignoring invalid submitter: " + submitter, ex);
                    }
                }
            }
        }
        String ext = null;
        if (this.message instanceof SMTPMessage) {
            ext = ((SMTPMessage)this.message).getMailExtension();
        }
        if (ext == null) {
            ext = this.session.getProperty("mail." + this.name + ".mailextension");
        }
        if (ext != null && ext.length() > 0) {
            cmd = cmd + " " + ext;
        }
        try {
            this.issueSendCommand(cmd, 250);
        }
        catch (final SMTPSendFailedException ex2) {
            final int retCode = ex2.getReturnCode();
            switch (retCode) {
                case 501:
                case 503:
                case 550:
                case 551:
                case 553: {
                    try {
                        ex2.setNextException(new SMTPSenderFailedException(new InternetAddress(from), cmd, retCode, ex2.getMessage()));
                    }
                    catch (final AddressException ex3) {}
                    break;
                }
            }
            throw ex2;
        }
    }
    
    protected void rcptTo() throws MessagingException {
        final List<InternetAddress> valid = new ArrayList<InternetAddress>();
        final List<InternetAddress> validUnsent = new ArrayList<InternetAddress>();
        final List<InternetAddress> invalid = new ArrayList<InternetAddress>();
        int retCode = -1;
        MessagingException mex = null;
        boolean sendFailed = false;
        MessagingException sfex = null;
        final Address[] validSentAddr = null;
        this.invalidAddr = validSentAddr;
        this.validUnsentAddr = validSentAddr;
        this.validSentAddr = validSentAddr;
        boolean sendPartial = false;
        if (this.message instanceof SMTPMessage) {
            sendPartial = ((SMTPMessage)this.message).getSendPartial();
        }
        if (!sendPartial) {
            sendPartial = PropUtil.getBooleanProperty(this.session.getProperties(), "mail." + this.name + ".sendpartial", false);
        }
        if (sendPartial) {
            this.logger.fine("sendPartial set");
        }
        boolean dsn = false;
        String notify = null;
        if (this.supportsExtension("DSN")) {
            if (this.message instanceof SMTPMessage) {
                notify = ((SMTPMessage)this.message).getDSNNotify();
            }
            if (notify == null) {
                notify = this.session.getProperty("mail." + this.name + ".dsn.notify");
            }
            if (notify != null) {
                dsn = true;
            }
        }
        for (int i = 0; i < this.addresses.length; ++i) {
            sfex = null;
            final InternetAddress ia = (InternetAddress)this.addresses[i];
            String cmd = "RCPT TO:" + this.normalizeAddress(ia.getAddress());
            if (dsn) {
                cmd = cmd + " NOTIFY=" + notify;
            }
            this.sendCommand(cmd);
            retCode = this.readServerResponse();
            switch (retCode) {
                case 250:
                case 251: {
                    valid.add(ia);
                    if (!this.reportSuccess) {
                        break;
                    }
                    sfex = new SMTPAddressSucceededException(ia, cmd, retCode, this.lastServerResponse);
                    if (mex == null) {
                        mex = sfex;
                        break;
                    }
                    mex.setNextException(sfex);
                    break;
                }
                case 501:
                case 503:
                case 550:
                case 551:
                case 553: {
                    if (!sendPartial) {
                        sendFailed = true;
                    }
                    invalid.add(ia);
                    sfex = new SMTPAddressFailedException(ia, cmd, retCode, this.lastServerResponse);
                    if (mex == null) {
                        mex = sfex;
                        break;
                    }
                    mex.setNextException(sfex);
                    break;
                }
                case 450:
                case 451:
                case 452:
                case 552: {
                    if (!sendPartial) {
                        sendFailed = true;
                    }
                    validUnsent.add(ia);
                    sfex = new SMTPAddressFailedException(ia, cmd, retCode, this.lastServerResponse);
                    if (mex == null) {
                        mex = sfex;
                        break;
                    }
                    mex.setNextException(sfex);
                    break;
                }
                default: {
                    if (retCode >= 400 && retCode <= 499) {
                        validUnsent.add(ia);
                    }
                    else {
                        if (retCode < 500 || retCode > 599) {
                            if (this.logger.isLoggable(Level.FINE)) {
                                this.logger.fine("got response code " + retCode + ", with response: " + this.lastServerResponse);
                            }
                            final String _lsr = this.lastServerResponse;
                            final int _lrc = this.lastReturnCode;
                            if (this.serverSocket != null) {
                                this.issueCommand("RSET", -1);
                            }
                            this.lastServerResponse = _lsr;
                            this.lastReturnCode = _lrc;
                            throw new SMTPAddressFailedException(ia, cmd, retCode, _lsr);
                        }
                        invalid.add(ia);
                    }
                    if (!sendPartial) {
                        sendFailed = true;
                    }
                    sfex = new SMTPAddressFailedException(ia, cmd, retCode, this.lastServerResponse);
                    if (mex == null) {
                        mex = sfex;
                        break;
                    }
                    mex.setNextException(sfex);
                    break;
                }
            }
        }
        if (sendPartial && valid.size() == 0) {
            sendFailed = true;
        }
        if (sendFailed) {
            invalid.toArray(this.invalidAddr = new Address[invalid.size()]);
            this.validUnsentAddr = new Address[valid.size() + validUnsent.size()];
            int i = 0;
            for (int j = 0; j < valid.size(); ++j) {
                this.validUnsentAddr[i++] = valid.get(j);
            }
            for (int j = 0; j < validUnsent.size(); ++j) {
                this.validUnsentAddr[i++] = validUnsent.get(j);
            }
        }
        else if (this.reportSuccess || (sendPartial && (invalid.size() > 0 || validUnsent.size() > 0))) {
            this.sendPartiallyFailed = true;
            this.exception = mex;
            invalid.toArray(this.invalidAddr = new Address[invalid.size()]);
            validUnsent.toArray(this.validUnsentAddr = new Address[validUnsent.size()]);
            valid.toArray(this.validSentAddr = new Address[valid.size()]);
        }
        else {
            this.validSentAddr = this.addresses;
        }
        if (this.logger.isLoggable(Level.FINE)) {
            if (this.validSentAddr != null && this.validSentAddr.length > 0) {
                this.logger.fine("Verified Addresses");
                for (int l = 0; l < this.validSentAddr.length; ++l) {
                    this.logger.fine("  " + this.validSentAddr[l]);
                }
            }
            if (this.validUnsentAddr != null && this.validUnsentAddr.length > 0) {
                this.logger.fine("Valid Unsent Addresses");
                for (int k = 0; k < this.validUnsentAddr.length; ++k) {
                    this.logger.fine("  " + this.validUnsentAddr[k]);
                }
            }
            if (this.invalidAddr != null && this.invalidAddr.length > 0) {
                this.logger.fine("Invalid Addresses");
                for (int m = 0; m < this.invalidAddr.length; ++m) {
                    this.logger.fine("  " + this.invalidAddr[m]);
                }
            }
        }
        if (sendFailed) {
            this.logger.fine("Sending failed because of invalid destination addresses");
            this.notifyTransportListeners(2, this.validSentAddr, this.validUnsentAddr, this.invalidAddr, this.message);
            final String lsr = this.lastServerResponse;
            final int lrc = this.lastReturnCode;
            try {
                if (this.serverSocket != null) {
                    this.issueCommand("RSET", -1);
                }
            }
            catch (final MessagingException ex) {
                try {
                    this.close();
                }
                catch (final MessagingException ex2) {
                    this.logger.log(Level.FINE, "close failed", ex2);
                }
            }
            finally {
                this.lastServerResponse = lsr;
                this.lastReturnCode = lrc;
            }
            throw new SendFailedException("Invalid Addresses", mex, this.validSentAddr, this.validUnsentAddr, this.invalidAddr);
        }
    }
    
    protected OutputStream data() throws MessagingException {
        assert Thread.holdsLock(this);
        this.issueSendCommand("DATA", 354);
        return this.dataStream = new SMTPOutputStream(this.serverOutput);
    }
    
    protected void finishData() throws IOException, MessagingException {
        assert Thread.holdsLock(this);
        this.dataStream.ensureAtBOL();
        this.issueSendCommand(".", 250);
    }
    
    protected OutputStream bdat() throws MessagingException {
        assert Thread.holdsLock(this);
        return this.dataStream = new BDATOutputStream(this.serverOutput, this.chunkSize);
    }
    
    protected void finishBdat() throws IOException, MessagingException {
        assert Thread.holdsLock(this);
        this.dataStream.ensureAtBOL();
        this.dataStream.close();
    }
    
    protected void startTLS() throws MessagingException {
        this.issueCommand("STARTTLS", 220);
        try {
            this.serverSocket = SocketFetcher.startTLS(this.serverSocket, this.host, this.session.getProperties(), "mail." + this.name);
            this.initStreams();
        }
        catch (final IOException ioex) {
            this.closeConnection();
            throw new MessagingException("Could not convert socket to TLS", ioex);
        }
    }
    
    private void openServer(final String host, int port) throws MessagingException {
        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.fine("trying to connect to host \"" + host + "\", port " + port + ", isSSL " + this.isSSL);
        }
        try {
            final Properties props = this.session.getProperties();
            this.serverSocket = SocketFetcher.getSocket(host, port, props, "mail." + this.name, this.isSSL);
            port = this.serverSocket.getPort();
            this.host = host;
            this.initStreams();
            int r = -1;
            if ((r = this.readServerResponse()) != 220) {
                this.serverSocket.close();
                this.serverSocket = null;
                this.serverOutput = null;
                this.serverInput = null;
                this.lineInputStream = null;
                if (this.logger.isLoggable(Level.FINE)) {
                    this.logger.fine("could not connect to host \"" + host + "\", port: " + port + ", response: " + r);
                }
                throw new MessagingException("Could not connect to SMTP host: " + host + ", port: " + port + ", response: " + r);
            }
            if (this.logger.isLoggable(Level.FINE)) {
                this.logger.fine("connected to host \"" + host + "\", port: " + port);
            }
        }
        catch (final UnknownHostException uhex) {
            throw new MessagingException("Unknown SMTP host: " + host, uhex);
        }
        catch (final SocketConnectException scex) {
            throw new MailConnectException(scex);
        }
        catch (final IOException ioe) {
            throw new MessagingException("Could not connect to SMTP host: " + host + ", port: " + port, ioe);
        }
    }
    
    private void openServer() throws MessagingException {
        int port = -1;
        this.host = "UNKNOWN";
        try {
            port = this.serverSocket.getPort();
            this.host = this.serverSocket.getInetAddress().getHostName();
            if (this.logger.isLoggable(Level.FINE)) {
                this.logger.fine("starting protocol to host \"" + this.host + "\", port " + port);
            }
            this.initStreams();
            int r = -1;
            if ((r = this.readServerResponse()) != 220) {
                this.serverSocket.close();
                this.serverSocket = null;
                this.serverOutput = null;
                this.serverInput = null;
                this.lineInputStream = null;
                if (this.logger.isLoggable(Level.FINE)) {
                    this.logger.fine("got bad greeting from host \"" + this.host + "\", port: " + port + ", response: " + r);
                }
                throw new MessagingException("Got bad greeting from SMTP host: " + this.host + ", port: " + port + ", response: " + r);
            }
            if (this.logger.isLoggable(Level.FINE)) {
                this.logger.fine("protocol started to host \"" + this.host + "\", port: " + port);
            }
        }
        catch (final IOException ioe) {
            throw new MessagingException("Could not start protocol to SMTP host: " + this.host + ", port: " + port, ioe);
        }
    }
    
    private void initStreams() throws IOException {
        final boolean quote = PropUtil.getBooleanProperty(this.session.getProperties(), "mail.debug.quote", false);
        (this.traceInput = new TraceInputStream(this.serverSocket.getInputStream(), this.traceLogger)).setQuote(quote);
        (this.traceOutput = new TraceOutputStream(this.serverSocket.getOutputStream(), this.traceLogger)).setQuote(quote);
        this.serverOutput = new BufferedOutputStream(this.traceOutput);
        this.serverInput = new BufferedInputStream(this.traceInput);
        this.lineInputStream = new LineInputStream(this.serverInput);
    }
    
    private boolean isTracing() {
        return this.traceLogger.isLoggable(Level.FINEST);
    }
    
    private void suspendTracing() {
        if (this.traceLogger.isLoggable(Level.FINEST)) {
            this.traceInput.setTrace(false);
            this.traceOutput.setTrace(false);
        }
    }
    
    private void resumeTracing() {
        if (this.traceLogger.isLoggable(Level.FINEST)) {
            this.traceInput.setTrace(true);
            this.traceOutput.setTrace(true);
        }
    }
    
    public synchronized void issueCommand(final String cmd, final int expect) throws MessagingException {
        this.sendCommand(cmd);
        final int resp = this.readServerResponse();
        if (expect != -1 && resp != expect) {
            throw new MessagingException(this.lastServerResponse);
        }
    }
    
    private void issueSendCommand(final String cmd, final int expect) throws MessagingException {
        this.sendCommand(cmd);
        final int ret;
        if ((ret = this.readServerResponse()) != expect) {
            final int vsl = (this.validSentAddr == null) ? 0 : this.validSentAddr.length;
            final int vul = (this.validUnsentAddr == null) ? 0 : this.validUnsentAddr.length;
            final Address[] valid = new Address[vsl + vul];
            if (vsl > 0) {
                System.arraycopy(this.validSentAddr, 0, valid, 0, vsl);
            }
            if (vul > 0) {
                System.arraycopy(this.validUnsentAddr, 0, valid, vsl, vul);
            }
            this.validSentAddr = null;
            this.validUnsentAddr = valid;
            if (this.logger.isLoggable(Level.FINE)) {
                this.logger.fine("got response code " + ret + ", with response: " + this.lastServerResponse);
            }
            final String _lsr = this.lastServerResponse;
            final int _lrc = this.lastReturnCode;
            if (this.serverSocket != null) {
                this.issueCommand("RSET", -1);
            }
            this.lastServerResponse = _lsr;
            this.lastReturnCode = _lrc;
            throw new SMTPSendFailedException(cmd, ret, this.lastServerResponse, this.exception, this.validSentAddr, this.validUnsentAddr, this.invalidAddr);
        }
    }
    
    public synchronized int simpleCommand(final String cmd) throws MessagingException {
        this.sendCommand(cmd);
        return this.readServerResponse();
    }
    
    protected int simpleCommand(final byte[] cmd) throws MessagingException {
        assert Thread.holdsLock(this);
        this.sendCommand(cmd);
        return this.readServerResponse();
    }
    
    protected void sendCommand(final String cmd) throws MessagingException {
        this.sendCommand(this.toBytes(cmd));
    }
    
    private void sendCommand(final byte[] cmdBytes) throws MessagingException {
        assert Thread.holdsLock(this);
        try {
            this.serverOutput.write(cmdBytes);
            this.serverOutput.write(SMTPTransport.CRLF);
            this.serverOutput.flush();
        }
        catch (final IOException ex) {
            throw new MessagingException("Can't send command to SMTP host", ex);
        }
    }
    
    protected int readServerResponse() throws MessagingException {
        assert Thread.holdsLock(this);
        String serverResponse = "";
        int returnCode = 0;
        final StringBuilder buf = new StringBuilder(100);
        try {
            String line = null;
            do {
                line = this.lineInputStream.readLine();
                if (line == null) {
                    serverResponse = buf.toString();
                    if (serverResponse.length() == 0) {
                        serverResponse = "[EOF]";
                    }
                    this.lastServerResponse = serverResponse;
                    this.lastReturnCode = -1;
                    this.logger.log(Level.FINE, "EOF: {0}", serverResponse);
                    return -1;
                }
                buf.append(line);
                buf.append("\n");
            } while (this.isNotLastLine(line));
            serverResponse = buf.toString();
        }
        catch (final IOException ioex) {
            this.logger.log(Level.FINE, "exception reading response", ioex);
            this.lastServerResponse = "";
            this.lastReturnCode = 0;
            throw new MessagingException("Exception reading response", ioex);
        }
        if (serverResponse.length() >= 3) {
            try {
                returnCode = Integer.parseInt(serverResponse.substring(0, 3));
            }
            catch (final NumberFormatException nfe) {
                try {
                    this.close();
                }
                catch (final MessagingException mex) {
                    this.logger.log(Level.FINE, "close failed", mex);
                }
                returnCode = -1;
            }
            catch (final StringIndexOutOfBoundsException ex) {
                try {
                    this.close();
                }
                catch (final MessagingException mex) {
                    this.logger.log(Level.FINE, "close failed", mex);
                }
                returnCode = -1;
            }
        }
        else {
            returnCode = -1;
        }
        if (returnCode == -1) {
            this.logger.log(Level.FINE, "bad server response: {0}", serverResponse);
        }
        this.lastServerResponse = serverResponse;
        return this.lastReturnCode = returnCode;
    }
    
    protected void checkConnected() {
        if (!super.isConnected()) {
            throw new IllegalStateException("Not connected");
        }
    }
    
    private boolean isNotLastLine(final String line) {
        return line != null && line.length() >= 4 && line.charAt(3) == '-';
    }
    
    private String normalizeAddress(final String addr) {
        if (!addr.startsWith("<") && !addr.endsWith(">")) {
            return "<" + addr + ">";
        }
        return addr;
    }
    
    public boolean supportsExtension(final String ext) {
        return this.extMap != null && this.extMap.get(ext.toUpperCase(Locale.ENGLISH)) != null;
    }
    
    public String getExtensionParameter(final String ext) {
        return (this.extMap == null) ? null : this.extMap.get(ext.toUpperCase(Locale.ENGLISH));
    }
    
    protected boolean supportsAuthentication(final String auth) {
        assert Thread.holdsLock(this);
        if (this.extMap == null) {
            return false;
        }
        final String a = this.extMap.get("AUTH");
        if (a == null) {
            return false;
        }
        final StringTokenizer st = new StringTokenizer(a);
        while (st.hasMoreTokens()) {
            final String tok = st.nextToken();
            if (tok.equalsIgnoreCase(auth)) {
                return true;
            }
        }
        if (auth.equalsIgnoreCase("LOGIN") && this.supportsExtension("AUTH=LOGIN")) {
            this.logger.fine("use AUTH=LOGIN hack");
            return true;
        }
        return false;
    }
    
    protected static String xtext(final String s) {
        return xtext(s, false);
    }
    
    protected static String xtext(final String s, final boolean utf8) {
        StringBuilder sb = null;
        byte[] bytes;
        if (utf8) {
            bytes = s.getBytes(StandardCharsets.UTF_8);
        }
        else {
            bytes = ASCIIUtility.getBytes(s);
        }
        for (int i = 0; i < bytes.length; ++i) {
            final char c = (char)(bytes[i] & 0xFF);
            if (!utf8 && c >= '\u0080') {
                throw new IllegalArgumentException("Non-ASCII character in SMTP submitter: " + s);
            }
            if (c < '!' || c > '~' || c == '+' || c == '=') {
                if (sb == null) {
                    sb = new StringBuilder(s.length() + 4);
                    sb.append(s.substring(0, i));
                }
                sb.append('+');
                sb.append(SMTPTransport.hexchar[(c & '\u00f0') >> 4]);
                sb.append(SMTPTransport.hexchar[c & '\u000f']);
            }
            else if (sb != null) {
                sb.append(c);
            }
        }
        return (sb != null) ? sb.toString() : s;
    }
    
    private String traceUser(final String user) {
        return this.debugusername ? user : "<user name suppressed>";
    }
    
    private String tracePassword(final String password) {
        return this.debugpassword ? password : ((password == null) ? "<null>" : "<non-null>");
    }
    
    private byte[] toBytes(final String s) {
        if (this.allowutf8) {
            return s.getBytes(StandardCharsets.UTF_8);
        }
        return ASCIIUtility.getBytes(s);
    }
    
    private void sendMessageStart(final String subject) {
    }
    
    private void sendMessageEnd() {
    }
    
    static {
        ignoreList = new String[] { "Bcc", "Content-Length" };
        CRLF = new byte[] { 13, 10 };
        UNKNOWN_SA = new String[0];
        SMTPTransport.hexchar = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    }
    
    private abstract class Authenticator
    {
        protected int resp;
        private final String mech;
        private final boolean enabled;
        
        Authenticator(final SMTPTransport smtpTransport, final String mech) {
            this(smtpTransport, mech, true);
        }
        
        Authenticator(final String mech, final boolean enabled) {
            this.mech = mech.toUpperCase(Locale.ENGLISH);
            this.enabled = enabled;
        }
        
        String getMechanism() {
            return this.mech;
        }
        
        boolean enabled() {
            return this.enabled;
        }
        
        boolean authenticate(final String host, final String authzid, final String user, final String passwd) throws MessagingException {
            Throwable thrown = null;
            try {
                final String ir = this.getInitialResponse(host, authzid, user, passwd);
                if (SMTPTransport.this.noauthdebug && SMTPTransport.this.isTracing()) {
                    SMTPTransport.this.logger.fine("AUTH " + this.mech + " command trace suppressed");
                    SMTPTransport.this.suspendTracing();
                }
                if (ir != null) {
                    this.resp = SMTPTransport.this.simpleCommand("AUTH " + this.mech + " " + ((ir.length() == 0) ? "=" : ir));
                }
                else {
                    this.resp = SMTPTransport.this.simpleCommand("AUTH " + this.mech);
                }
                if (this.resp == 530) {
                    SMTPTransport.this.startTLS();
                    if (ir != null) {
                        this.resp = SMTPTransport.this.simpleCommand("AUTH " + this.mech + " " + ir);
                    }
                    else {
                        this.resp = SMTPTransport.this.simpleCommand("AUTH " + this.mech);
                    }
                }
                if (this.resp == 334) {
                    this.doAuth(host, authzid, user, passwd);
                }
                if (SMTPTransport.this.noauthdebug && SMTPTransport.this.isTracing()) {
                    SMTPTransport.this.logger.fine("AUTH " + this.mech + " " + ((this.resp == 235) ? "succeeded" : "failed"));
                }
                SMTPTransport.this.resumeTracing();
                if (this.resp != 235) {
                    SMTPTransport.this.closeConnection();
                    if (thrown != null) {
                        if (thrown instanceof Error) {
                            throw (Error)thrown;
                        }
                        if (thrown instanceof Exception) {
                            throw new AuthenticationFailedException(SMTPTransport.this.getLastServerResponse(), (Exception)thrown);
                        }
                        assert false : "unknown Throwable";
                    }
                    throw new AuthenticationFailedException(SMTPTransport.this.getLastServerResponse());
                }
                return true;
            }
            catch (final IOException ex) {
                SMTPTransport.this.logger.log(Level.FINE, "AUTH " + this.mech + " failed", ex);
            }
            catch (final Throwable t) {
                SMTPTransport.this.logger.log(Level.FINE, "AUTH " + this.mech + " failed", t);
                thrown = t;
            }
            finally {
                if (SMTPTransport.this.noauthdebug && SMTPTransport.this.isTracing()) {
                    SMTPTransport.this.logger.fine("AUTH " + this.mech + " " + ((this.resp == 235) ? "succeeded" : "failed"));
                }
                SMTPTransport.this.resumeTracing();
                if (this.resp != 235) {
                    SMTPTransport.this.closeConnection();
                    if (thrown != null) {
                        if (thrown instanceof Error) {
                            throw (Error)thrown;
                        }
                        if (thrown instanceof Exception) {
                            throw new AuthenticationFailedException(SMTPTransport.this.getLastServerResponse(), (Exception)thrown);
                        }
                        assert false : "unknown Throwable";
                    }
                    throw new AuthenticationFailedException(SMTPTransport.this.getLastServerResponse());
                }
            }
            return true;
        }
        
        String getInitialResponse(final String host, final String authzid, final String user, final String passwd) throws MessagingException, IOException {
            return null;
        }
        
        abstract void doAuth(final String p0, final String p1, final String p2, final String p3) throws MessagingException, IOException;
    }
    
    private class LoginAuthenticator extends Authenticator
    {
        LoginAuthenticator() {
            super("LOGIN");
        }
        
        @Override
        void doAuth(final String host, final String authzid, final String user, final String passwd) throws MessagingException, IOException {
            this.resp = SMTPTransport.this.simpleCommand(BASE64EncoderStream.encode(user.getBytes(StandardCharsets.UTF_8)));
            if (this.resp == 334) {
                this.resp = SMTPTransport.this.simpleCommand(BASE64EncoderStream.encode(passwd.getBytes(StandardCharsets.UTF_8)));
            }
        }
    }
    
    private class PlainAuthenticator extends Authenticator
    {
        PlainAuthenticator() {
            super("PLAIN");
        }
        
        @Override
        String getInitialResponse(final String host, final String authzid, final String user, final String passwd) throws MessagingException, IOException {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final OutputStream b64os = new BASE64EncoderStream(bos, Integer.MAX_VALUE);
            if (authzid != null) {
                b64os.write(authzid.getBytes(StandardCharsets.UTF_8));
            }
            b64os.write(0);
            b64os.write(user.getBytes(StandardCharsets.UTF_8));
            b64os.write(0);
            b64os.write(passwd.getBytes(StandardCharsets.UTF_8));
            b64os.flush();
            return ASCIIUtility.toString(bos.toByteArray());
        }
        
        @Override
        void doAuth(final String host, final String authzid, final String user, final String passwd) throws MessagingException, IOException {
            throw new AuthenticationFailedException("PLAIN asked for more");
        }
    }
    
    private class DigestMD5Authenticator extends Authenticator
    {
        private DigestMD5 md5support;
        
        DigestMD5Authenticator() {
            super("DIGEST-MD5");
        }
        
        private synchronized DigestMD5 getMD5() {
            if (this.md5support == null) {
                this.md5support = new DigestMD5(SMTPTransport.this.logger);
            }
            return this.md5support;
        }
        
        @Override
        void doAuth(final String host, final String authzid, final String user, final String passwd) throws MessagingException, IOException {
            final DigestMD5 md5 = this.getMD5();
            assert md5 != null;
            final byte[] b = md5.authClient(host, user, passwd, SMTPTransport.this.getSASLRealm(), SMTPTransport.this.getLastServerResponse());
            this.resp = SMTPTransport.this.simpleCommand(b);
            if (this.resp == 334) {
                if (!md5.authServer(SMTPTransport.this.getLastServerResponse())) {
                    this.resp = -1;
                }
                else {
                    this.resp = SMTPTransport.this.simpleCommand(new byte[0]);
                }
            }
        }
    }
    
    private class NtlmAuthenticator extends Authenticator
    {
        private Ntlm ntlm;
        private int flags;
        
        NtlmAuthenticator() {
            super("NTLM");
        }
        
        @Override
        String getInitialResponse(final String host, final String authzid, final String user, final String passwd) throws MessagingException, IOException {
            this.ntlm = new Ntlm(SMTPTransport.this.getNTLMDomain(), SMTPTransport.this.getLocalHost(), user, passwd, SMTPTransport.this.logger);
            this.flags = PropUtil.getIntProperty(SMTPTransport.this.session.getProperties(), "mail." + SMTPTransport.this.name + ".auth.ntlm.flags", 0);
            final String type1 = this.ntlm.generateType1Msg(this.flags);
            return type1;
        }
        
        @Override
        void doAuth(final String host, final String authzid, final String user, final String passwd) throws MessagingException, IOException {
            assert this.ntlm != null;
            final String type3 = this.ntlm.generateType3Msg(SMTPTransport.this.getLastServerResponse().substring(4).trim());
            this.resp = SMTPTransport.this.simpleCommand(type3);
        }
    }
    
    private class OAuth2Authenticator extends Authenticator
    {
        OAuth2Authenticator() {
            super("XOAUTH2", false);
        }
        
        @Override
        String getInitialResponse(final String host, final String authzid, final String user, final String passwd) throws MessagingException, IOException {
            final String resp = "user=" + user + "\u0001auth=Bearer " + passwd + "\u0001\u0001";
            final byte[] b = BASE64EncoderStream.encode(resp.getBytes(StandardCharsets.UTF_8));
            return ASCIIUtility.toString(b);
        }
        
        @Override
        void doAuth(final String host, final String authzid, final String user, final String passwd) throws MessagingException, IOException {
            throw new AuthenticationFailedException("OAUTH2 asked for more");
        }
    }
    
    private class BDATOutputStream extends SMTPOutputStream
    {
        public BDATOutputStream(final OutputStream out, final int size) {
            super(new ChunkedOutputStream(out, size));
        }
        
        @Override
        public void close() throws IOException {
            this.out.close();
        }
    }
    
    private class ChunkedOutputStream extends OutputStream
    {
        private final OutputStream out;
        private final byte[] buf;
        private int count;
        
        public ChunkedOutputStream(final OutputStream out, final int size) {
            this.count = 0;
            this.out = out;
            this.buf = new byte[size];
        }
        
        @Override
        public void write(final int b) throws IOException {
            this.buf[this.count++] = (byte)b;
            if (this.count >= this.buf.length) {
                this.flush();
            }
        }
        
        @Override
        public void write(final byte[] b, int off, int len) throws IOException {
            while (len > 0) {
                final int size = Math.min(this.buf.length - this.count, len);
                if (size == this.buf.length) {
                    this.bdat(b, off, size, false);
                }
                else {
                    System.arraycopy(b, off, this.buf, this.count, size);
                    this.count += size;
                }
                off += size;
                len -= size;
                if (this.count >= this.buf.length) {
                    this.flush();
                }
            }
        }
        
        @Override
        public void flush() throws IOException {
            this.bdat(this.buf, 0, this.count, false);
            this.count = 0;
        }
        
        @Override
        public void close() throws IOException {
            this.bdat(this.buf, 0, this.count, true);
            this.count = 0;
        }
        
        private void bdat(final byte[] b, final int off, final int len, final boolean last) throws IOException {
            if (len <= 0) {
                if (!last) {
                    return;
                }
            }
            try {
                if (last) {
                    SMTPTransport.this.sendCommand("BDAT " + len + " LAST");
                }
                else {
                    SMTPTransport.this.sendCommand("BDAT " + len);
                }
                this.out.write(b, off, len);
                this.out.flush();
                final int ret = SMTPTransport.this.readServerResponse();
                if (ret != 250) {
                    throw new IOException(SMTPTransport.this.lastServerResponse);
                }
            }
            catch (final MessagingException mex) {
                throw new IOException("BDAT write exception", mex);
            }
        }
    }
}
