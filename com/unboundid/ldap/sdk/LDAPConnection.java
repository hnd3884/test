package com.unboundid.ldap.sdk;

import java.util.HashMap;
import com.unboundid.ldap.protocol.LDAPResponse;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldif.LDIFException;
import java.util.Collection;
import com.unboundid.ldap.protocol.AbandonRequestProtocolOp;
import java.util.List;
import com.unboundid.ldap.protocol.ProtocolOp;
import com.unboundid.ldap.protocol.LDAPMessage;
import com.unboundid.ldap.protocol.UnbindRequestProtocolOp;
import java.net.Socket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSession;
import java.util.logging.Level;
import com.unboundid.util.DebugType;
import javax.security.sasl.SaslClient;
import com.unboundid.util.Validator;
import java.net.InetAddress;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.util.SynchronizedSocketFactory;
import com.unboundid.util.SynchronizedSSLSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.util.Timer;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicBoolean;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.util.WeakHashSet;
import javax.net.SocketFactory;
import java.util.concurrent.atomic.AtomicLong;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import java.io.Closeable;

@ThreadSafety(level = ThreadSafetyLevel.MOSTLY_THREADSAFE)
public final class LDAPConnection implements FullLDAPInterface, ReferralConnector, Closeable
{
    private static final AtomicLong NEXT_CONNECTION_ID;
    private static final SocketFactory DEFAULT_SOCKET_FACTORY;
    private static final WeakHashSet<Schema> SCHEMA_SET;
    private AbstractConnectionPool connectionPool;
    private final AtomicBoolean needsReconnect;
    private final AtomicReference<DisconnectInfo> disconnectInfo;
    private volatile BindRequest lastBindRequest;
    private volatile boolean closeRequested;
    private volatile boolean unbindRequestSent;
    private volatile ExtendedRequest startTLSRequest;
    private int reconnectPort;
    private volatile LDAPConnectionInternals connectionInternals;
    private LDAPConnectionOptions connectionOptions;
    private final LDAPConnectionStatistics connectionStatistics;
    private final long connectionID;
    private long lastReconnectTime;
    private volatile long lastCommunicationTime;
    private Map<String, Object> attachments;
    private volatile ReferralConnector referralConnector;
    private volatile Schema cachedSchema;
    private volatile ServerSet serverSet;
    private SocketFactory lastUsedSocketFactory;
    private volatile SocketFactory socketFactory;
    private StackTraceElement[] connectStackTrace;
    private String connectionName;
    private String connectionPoolName;
    private String hostPort;
    private String reconnectAddress;
    private Timer timer;
    
    public LDAPConnection() {
        this(null, null);
    }
    
    public LDAPConnection(final LDAPConnectionOptions connectionOptions) {
        this(null, connectionOptions);
    }
    
    public LDAPConnection(final SocketFactory socketFactory) {
        this(socketFactory, null);
    }
    
    public LDAPConnection(final SocketFactory socketFactory, final LDAPConnectionOptions connectionOptions) {
        this.reconnectPort = -1;
        this.needsReconnect = new AtomicBoolean(false);
        this.disconnectInfo = new AtomicReference<DisconnectInfo>();
        this.lastCommunicationTime = -1L;
        this.connectionID = LDAPConnection.NEXT_CONNECTION_ID.getAndIncrement();
        if (connectionOptions == null) {
            this.connectionOptions = new LDAPConnectionOptions();
        }
        else {
            this.connectionOptions = connectionOptions.duplicate();
        }
        SocketFactory f;
        if (socketFactory == null) {
            f = LDAPConnection.DEFAULT_SOCKET_FACTORY;
        }
        else {
            f = socketFactory;
        }
        if (this.connectionOptions.allowConcurrentSocketFactoryUse()) {
            this.socketFactory = f;
        }
        else if (f instanceof SSLSocketFactory) {
            this.socketFactory = new SynchronizedSSLSocketFactory((SSLSocketFactory)f);
        }
        else {
            this.socketFactory = new SynchronizedSocketFactory(f);
        }
        this.attachments = null;
        this.connectionStatistics = new LDAPConnectionStatistics();
        this.connectionName = null;
        this.connectionPoolName = null;
        this.cachedSchema = null;
        this.timer = null;
        this.serverSet = null;
        this.referralConnector = this.connectionOptions.getReferralConnector();
        if (this.referralConnector == null) {
            this.referralConnector = this;
        }
    }
    
    public LDAPConnection(final String host, final int port) throws LDAPException {
        this(null, null, host, port);
    }
    
    public LDAPConnection(final LDAPConnectionOptions connectionOptions, final String host, final int port) throws LDAPException {
        this(null, connectionOptions, host, port);
    }
    
    public LDAPConnection(final SocketFactory socketFactory, final String host, final int port) throws LDAPException {
        this(socketFactory, null, host, port);
    }
    
    public LDAPConnection(final SocketFactory socketFactory, final LDAPConnectionOptions connectionOptions, final String host, final int port) throws LDAPException {
        this(socketFactory, connectionOptions);
        this.connect(host, port);
    }
    
    public LDAPConnection(final String host, final int port, final String bindDN, final String bindPassword) throws LDAPException {
        this(null, null, host, port, bindDN, bindPassword);
    }
    
    public LDAPConnection(final LDAPConnectionOptions connectionOptions, final String host, final int port, final String bindDN, final String bindPassword) throws LDAPException {
        this(null, connectionOptions, host, port, bindDN, bindPassword);
    }
    
    public LDAPConnection(final SocketFactory socketFactory, final String host, final int port, final String bindDN, final String bindPassword) throws LDAPException {
        this(socketFactory, null, host, port, bindDN, bindPassword);
    }
    
    public LDAPConnection(final SocketFactory socketFactory, final LDAPConnectionOptions connectionOptions, final String host, final int port, final String bindDN, final String bindPassword) throws LDAPException {
        this(socketFactory, connectionOptions, host, port);
        try {
            this.bind(new SimpleBindRequest(bindDN, bindPassword));
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            this.setDisconnectInfo(DisconnectType.BIND_FAILED, null, le);
            this.close();
            throw le;
        }
    }
    
    @ThreadSafety(level = ThreadSafetyLevel.METHOD_NOT_THREADSAFE)
    public void connect(final String host, final int port) throws LDAPException {
        this.connect(host, port, this.connectionOptions.getConnectTimeoutMillis());
    }
    
    @ThreadSafety(level = ThreadSafetyLevel.METHOD_NOT_THREADSAFE)
    public void connect(final String host, final int port, final int timeout) throws LDAPException {
        InetAddress inetAddress;
        try {
            inetAddress = this.connectionOptions.getNameResolver().getByName(host);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.CONNECT_ERROR, LDAPMessages.ERR_CONN_RESOLVE_ERROR.get(host, StaticUtils.getExceptionMessage(e)), e);
        }
        this.connect(host, inetAddress, port, timeout);
    }
    
    @ThreadSafety(level = ThreadSafetyLevel.METHOD_NOT_THREADSAFE)
    public void connect(final InetAddress inetAddress, final int port, final int timeout) throws LDAPException {
        this.connect(this.connectionOptions.getNameResolver().getHostName(inetAddress), inetAddress, port, timeout);
    }
    
    @ThreadSafety(level = ThreadSafetyLevel.METHOD_NOT_THREADSAFE)
    public void connect(final String host, final InetAddress inetAddress, final int port, final int timeout) throws LDAPException {
        Validator.ensureNotNull(host, inetAddress, port);
        this.needsReconnect.set(false);
        this.hostPort = host + ':' + port;
        this.lastCommunicationTime = -1L;
        this.startTLSRequest = null;
        if (this.isConnected()) {
            this.setDisconnectInfo(DisconnectType.RECONNECT, null, null);
            this.close();
        }
        this.lastUsedSocketFactory = this.socketFactory;
        this.reconnectAddress = host;
        this.reconnectPort = port;
        this.cachedSchema = null;
        this.unbindRequestSent = false;
        this.disconnectInfo.set(null);
        try {
            this.connectionStatistics.incrementNumConnects();
            (this.connectionInternals = new LDAPConnectionInternals(this, this.connectionOptions, this.lastUsedSocketFactory, host, inetAddress, port, timeout)).startConnectionReader();
            this.lastCommunicationTime = System.currentTimeMillis();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            this.setDisconnectInfo(DisconnectType.LOCAL_ERROR, null, e);
            this.connectionInternals = null;
            throw new LDAPException(ResultCode.CONNECT_ERROR, LDAPMessages.ERR_CONN_CONNECT_ERROR.get(this.getHostPort(), StaticUtils.getExceptionMessage(e)), e);
        }
        if (this.connectionOptions.useSchema()) {
            try {
                this.cachedSchema = getCachedSchema(this);
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
        }
    }
    
    public void reconnect() throws LDAPException {
        this.needsReconnect.set(false);
        if (System.currentTimeMillis() - this.lastReconnectTime < 1000L) {
            throw new LDAPException(ResultCode.SERVER_DOWN, LDAPMessages.ERR_CONN_MULTIPLE_FAILURES.get());
        }
        BindRequest bindRequest = null;
        if (this.lastBindRequest != null) {
            bindRequest = this.lastBindRequest.getRebindRequest(this.reconnectAddress, this.reconnectPort);
            if (bindRequest == null) {
                throw new LDAPException(ResultCode.SERVER_DOWN, LDAPMessages.ERR_CONN_CANNOT_REAUTHENTICATE.get(this.getHostPort()));
            }
        }
        final ExtendedRequest startTLSExtendedRequest = this.startTLSRequest;
        this.setDisconnectInfo(DisconnectType.RECONNECT, null, null);
        this.terminate(null);
        try {
            Thread.sleep(1000L);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
                throw new LDAPException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_CONN_INTERRUPTED_DURING_RECONNECT.get(), e);
            }
        }
        this.connect(this.reconnectAddress, this.reconnectPort);
        if (startTLSExtendedRequest != null) {
            try {
                final ExtendedResult startTLSResult = this.processExtendedOperation(startTLSExtendedRequest);
                if (startTLSResult.getResultCode() != ResultCode.SUCCESS) {
                    throw new LDAPException(startTLSResult);
                }
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                this.setDisconnectInfo(DisconnectType.SECURITY_PROBLEM, null, le);
                this.terminate(null);
                throw le;
            }
        }
        if (bindRequest != null) {
            try {
                this.bind(bindRequest);
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                this.setDisconnectInfo(DisconnectType.BIND_FAILED, null, le);
                this.terminate(null);
                throw le;
            }
        }
        this.lastReconnectTime = System.currentTimeMillis();
    }
    
    void setNeedsReconnect() {
        this.needsReconnect.set(true);
    }
    
    public boolean isConnected() {
        final LDAPConnectionInternals internals = this.connectionInternals;
        if (internals == null) {
            return false;
        }
        if (!internals.isConnected()) {
            this.setClosed();
            return false;
        }
        return !this.needsReconnect.get();
    }
    
    void convertToTLS(final SSLSocketFactory sslSocketFactory) throws LDAPException {
        final LDAPConnectionInternals internals = this.connectionInternals;
        if (internals == null) {
            throw new LDAPException(ResultCode.SERVER_DOWN, LDAPMessages.ERR_CONN_NOT_ESTABLISHED.get());
        }
        internals.convertToTLS(sslSocketFactory);
    }
    
    void applySASLQoP(final SaslClient saslClient) throws LDAPException {
        final LDAPConnectionInternals internals = this.connectionInternals;
        if (internals == null) {
            throw new LDAPException(ResultCode.SERVER_DOWN, LDAPMessages.ERR_CONN_NOT_ESTABLISHED.get());
        }
        internals.applySASLQoP(saslClient);
    }
    
    public LDAPConnectionOptions getConnectionOptions() {
        return this.connectionOptions;
    }
    
    public void setConnectionOptions(final LDAPConnectionOptions connectionOptions) {
        if (connectionOptions == null) {
            this.connectionOptions = new LDAPConnectionOptions();
        }
        else {
            final LDAPConnectionOptions newOptions = connectionOptions.duplicate();
            if (Debug.debugEnabled(DebugType.LDAP) && newOptions.useSynchronousMode() && !connectionOptions.useSynchronousMode() && this.isConnected()) {
                Debug.debug(Level.WARNING, DebugType.LDAP, "A call to LDAPConnection.setConnectionOptions() with useSynchronousMode=true will have no effect for this connection because it is already established.  The useSynchronousMode option must be set before the connection is established to have any effect.");
            }
            this.connectionOptions = newOptions;
        }
        final ReferralConnector rc = this.connectionOptions.getReferralConnector();
        if (rc == null) {
            this.referralConnector = this;
        }
        else {
            this.referralConnector = rc;
        }
    }
    
    public SocketFactory getLastUsedSocketFactory() {
        return this.lastUsedSocketFactory;
    }
    
    public SocketFactory getSocketFactory() {
        return this.socketFactory;
    }
    
    public void setSocketFactory(final SocketFactory socketFactory) {
        if (socketFactory == null) {
            this.socketFactory = LDAPConnection.DEFAULT_SOCKET_FACTORY;
        }
        else {
            this.socketFactory = socketFactory;
        }
    }
    
    public SSLSession getSSLSession() {
        final LDAPConnectionInternals internals = this.connectionInternals;
        if (internals == null) {
            return null;
        }
        final Socket socket = internals.getSocket();
        if (socket != null && socket instanceof SSLSocket) {
            final SSLSocket sslSocket = (SSLSocket)socket;
            return sslSocket.getSession();
        }
        return null;
    }
    
    public long getConnectionID() {
        return this.connectionID;
    }
    
    public String getConnectionName() {
        return this.connectionName;
    }
    
    public void setConnectionName(final String connectionName) {
        if (this.connectionPool == null) {
            this.connectionName = connectionName;
            if (this.connectionInternals != null) {
                final LDAPConnectionReader reader = this.connectionInternals.getConnectionReader();
                reader.updateThreadName();
            }
        }
    }
    
    public AbstractConnectionPool getConnectionPool() {
        return this.connectionPool;
    }
    
    public String getConnectionPoolName() {
        return this.connectionPoolName;
    }
    
    void setConnectionPoolName(final String connectionPoolName) {
        this.connectionPoolName = connectionPoolName;
        if (this.connectionInternals != null) {
            final LDAPConnectionReader reader = this.connectionInternals.getConnectionReader();
            reader.updateThreadName();
        }
    }
    
    ServerSet getServerSet() {
        return this.serverSet;
    }
    
    void setServerSet(final ServerSet serverSet) {
        this.serverSet = serverSet;
    }
    
    public String getHostPort() {
        if (this.hostPort == null) {
            return "";
        }
        return this.hostPort;
    }
    
    public String getConnectedAddress() {
        final LDAPConnectionInternals internals = this.connectionInternals;
        if (internals == null) {
            return null;
        }
        return internals.getHost();
    }
    
    public String getConnectedIPAddress() {
        final LDAPConnectionInternals internals = this.connectionInternals;
        if (internals == null) {
            return null;
        }
        return internals.getInetAddress().getHostAddress();
    }
    
    public InetAddress getConnectedInetAddress() {
        final LDAPConnectionInternals internals = this.connectionInternals;
        if (internals == null) {
            return null;
        }
        return internals.getInetAddress();
    }
    
    public int getConnectedPort() {
        final LDAPConnectionInternals internals = this.connectionInternals;
        if (internals == null) {
            return -1;
        }
        return internals.getPort();
    }
    
    public StackTraceElement[] getConnectStackTrace() {
        return this.connectStackTrace;
    }
    
    void setConnectStackTrace(final StackTraceElement[] connectStackTrace) {
        this.connectStackTrace = connectStackTrace;
    }
    
    @ThreadSafety(level = ThreadSafetyLevel.METHOD_NOT_THREADSAFE)
    @Override
    public void close() {
        this.close(StaticUtils.NO_CONTROLS);
    }
    
    @ThreadSafety(level = ThreadSafetyLevel.METHOD_NOT_THREADSAFE)
    public void close(final Control[] controls) {
        this.closeRequested = true;
        this.setDisconnectInfo(DisconnectType.UNBIND, null, null);
        if (this.connectionPool == null) {
            this.terminate(controls);
        }
        else {
            this.connectionPool.releaseDefunctConnection(this);
        }
    }
    
    @ThreadSafety(level = ThreadSafetyLevel.METHOD_NOT_THREADSAFE)
    public void closeWithoutUnbind() {
        this.closeRequested = true;
        this.setDisconnectInfo(DisconnectType.CLOSED_WITHOUT_UNBIND, null, null);
        if (this.connectionPool == null) {
            this.setClosed();
        }
        else {
            this.connectionPool.releaseDefunctConnection(this);
        }
    }
    
    void terminate(final Control[] controls) {
        if (this.isConnected() && !this.unbindRequestSent) {
            try {
                this.unbindRequestSent = true;
                this.setDisconnectInfo(DisconnectType.UNBIND, null, null);
                final int messageID = this.nextMessageID();
                if (Debug.debugEnabled(DebugType.LDAP)) {
                    Debug.debugLDAPRequest(Level.INFO, createUnbindRequestString(controls), messageID, this);
                }
                this.connectionStatistics.incrementNumUnbindRequests();
                this.sendMessage(new LDAPMessage(messageID, new UnbindRequestProtocolOp(), controls), this.connectionOptions.getResponseTimeoutMillis(OperationType.UNBIND));
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
        }
        this.setClosed();
    }
    
    private static String createUnbindRequestString(final Control... controls) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("UnbindRequest(");
        if (controls != null && controls.length > 0) {
            buffer.append("controls={");
            for (int i = 0; i < controls.length; ++i) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append(controls[i]);
            }
            buffer.append('}');
        }
        buffer.append(')');
        return buffer.toString();
    }
    
    boolean closeRequested() {
        return this.closeRequested;
    }
    
    boolean unbindRequestSent() {
        return this.unbindRequestSent;
    }
    
    void setConnectionPool(final AbstractConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }
    
    @Override
    public RootDSE getRootDSE() throws LDAPException {
        return RootDSE.getRootDSE(this);
    }
    
    @Override
    public Schema getSchema() throws LDAPException {
        return Schema.getSchema(this, "");
    }
    
    @Override
    public Schema getSchema(final String entryDN) throws LDAPException {
        return Schema.getSchema(this, entryDN);
    }
    
    @Override
    public SearchResultEntry getEntry(final String dn) throws LDAPException {
        return this.getEntry(dn, (String[])null);
    }
    
    @Override
    public SearchResultEntry getEntry(final String dn, final String... attributes) throws LDAPException {
        final Filter filter = Filter.createPresenceFilter("objectClass");
        SearchResult result;
        try {
            final SearchRequest searchRequest = new SearchRequest(dn, SearchScope.BASE, DereferencePolicy.NEVER, 1, 0, false, filter, attributes);
            result = this.search(searchRequest);
        }
        catch (final LDAPException le) {
            if (le.getResultCode().equals(ResultCode.NO_SUCH_OBJECT)) {
                return null;
            }
            throw le;
        }
        if (!result.getResultCode().equals(ResultCode.SUCCESS)) {
            throw new LDAPException(result);
        }
        final List<SearchResultEntry> entryList = result.getSearchEntries();
        if (entryList.isEmpty()) {
            return null;
        }
        return entryList.get(0);
    }
    
    public void abandon(final AsyncRequestID requestID) throws LDAPException {
        this.abandon(requestID, null);
    }
    
    public void abandon(final AsyncRequestID requestID, final Control[] controls) throws LDAPException {
        if (this.synchronousMode()) {
            throw new LDAPException(ResultCode.NOT_SUPPORTED, LDAPMessages.ERR_ABANDON_NOT_SUPPORTED_IN_SYNCHRONOUS_MODE.get());
        }
        final int messageID = requestID.getMessageID();
        try {
            this.connectionInternals.getConnectionReader().deregisterResponseAcceptor(messageID);
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
        this.connectionStatistics.incrementNumAbandonRequests();
        final int abandonMessageID = this.nextMessageID();
        if (Debug.debugEnabled(DebugType.LDAP)) {
            Debug.debugLDAPRequest(Level.INFO, createAbandonRequestString(messageID, controls), abandonMessageID, this);
        }
        this.sendMessage(new LDAPMessage(abandonMessageID, new AbandonRequestProtocolOp(messageID), controls), this.connectionOptions.getResponseTimeoutMillis(OperationType.ABANDON));
    }
    
    void abandon(final int messageID, final Control... controls) throws LDAPException {
        try {
            this.connectionInternals.getConnectionReader().deregisterResponseAcceptor(messageID);
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
        this.connectionStatistics.incrementNumAbandonRequests();
        final int abandonMessageID = this.nextMessageID();
        if (Debug.debugEnabled(DebugType.LDAP)) {
            Debug.debugLDAPRequest(Level.INFO, createAbandonRequestString(messageID, controls), abandonMessageID, this);
        }
        this.sendMessage(new LDAPMessage(abandonMessageID, new AbandonRequestProtocolOp(messageID), controls), this.connectionOptions.getResponseTimeoutMillis(OperationType.ABANDON));
    }
    
    private static String createAbandonRequestString(final int idToAbandon, final Control... controls) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("AbandonRequest(idToAbandon=");
        buffer.append(idToAbandon);
        if (controls != null && controls.length > 0) {
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
        return buffer.toString();
    }
    
    @Override
    public LDAPResult add(final String dn, final Attribute... attributes) throws LDAPException {
        Validator.ensureNotNull(dn, attributes);
        return this.add(new AddRequest(dn, attributes));
    }
    
    @Override
    public LDAPResult add(final String dn, final Collection<Attribute> attributes) throws LDAPException {
        Validator.ensureNotNull(dn, attributes);
        return this.add(new AddRequest(dn, attributes));
    }
    
    @Override
    public LDAPResult add(final Entry entry) throws LDAPException {
        Validator.ensureNotNull(entry);
        return this.add(new AddRequest(entry));
    }
    
    @Override
    public LDAPResult add(final String... ldifLines) throws LDIFException, LDAPException {
        return this.add(new AddRequest(ldifLines));
    }
    
    @Override
    public LDAPResult add(final AddRequest addRequest) throws LDAPException {
        Validator.ensureNotNull(addRequest);
        final LDAPResult ldapResult = addRequest.process(this, 1);
        switch (ldapResult.getResultCode().intValue()) {
            case 0:
            case 16654: {
                return ldapResult;
            }
            default: {
                throw new LDAPException(ldapResult);
            }
        }
    }
    
    @Override
    public LDAPResult add(final ReadOnlyAddRequest addRequest) throws LDAPException {
        return this.add((AddRequest)addRequest);
    }
    
    public AsyncRequestID asyncAdd(final AddRequest addRequest, final AsyncResultListener resultListener) throws LDAPException {
        Validator.ensureNotNull(addRequest);
        if (this.synchronousMode()) {
            throw new LDAPException(ResultCode.NOT_SUPPORTED, LDAPMessages.ERR_ASYNC_NOT_SUPPORTED_IN_SYNCHRONOUS_MODE.get());
        }
        AsyncResultListener listener;
        if (resultListener == null) {
            listener = DiscardAsyncListener.getInstance();
        }
        else {
            listener = resultListener;
        }
        return addRequest.processAsync(this, listener);
    }
    
    public AsyncRequestID asyncAdd(final ReadOnlyAddRequest addRequest, final AsyncResultListener resultListener) throws LDAPException {
        if (this.synchronousMode()) {
            throw new LDAPException(ResultCode.NOT_SUPPORTED, LDAPMessages.ERR_ASYNC_NOT_SUPPORTED_IN_SYNCHRONOUS_MODE.get());
        }
        return this.asyncAdd((AddRequest)addRequest, resultListener);
    }
    
    @ThreadSafety(level = ThreadSafetyLevel.METHOD_NOT_THREADSAFE)
    @Override
    public BindResult bind(final String bindDN, final String password) throws LDAPException {
        return this.bind(new SimpleBindRequest(bindDN, password));
    }
    
    @ThreadSafety(level = ThreadSafetyLevel.METHOD_NOT_THREADSAFE)
    @Override
    public BindResult bind(final BindRequest bindRequest) throws LDAPException {
        Validator.ensureNotNull(bindRequest);
        final BindResult bindResult = this.processBindOperation(bindRequest);
        switch (bindResult.getResultCode().intValue()) {
            case 0: {
                return bindResult;
            }
            case 14: {
                throw new SASLBindInProgressException(bindResult);
            }
            default: {
                throw new LDAPBindException(bindResult);
            }
        }
    }
    
    @Override
    public CompareResult compare(final String dn, final String attributeName, final String assertionValue) throws LDAPException {
        Validator.ensureNotNull(dn, attributeName, assertionValue);
        return this.compare(new CompareRequest(dn, attributeName, assertionValue));
    }
    
    @Override
    public CompareResult compare(final CompareRequest compareRequest) throws LDAPException {
        Validator.ensureNotNull(compareRequest);
        final LDAPResult result = compareRequest.process(this, 1);
        switch (result.getResultCode().intValue()) {
            case 5:
            case 6: {
                return new CompareResult(result);
            }
            default: {
                throw new LDAPException(result);
            }
        }
    }
    
    @Override
    public CompareResult compare(final ReadOnlyCompareRequest compareRequest) throws LDAPException {
        return this.compare((CompareRequest)compareRequest);
    }
    
    public AsyncRequestID asyncCompare(final CompareRequest compareRequest, final AsyncCompareResultListener resultListener) throws LDAPException {
        Validator.ensureNotNull(compareRequest);
        if (this.synchronousMode()) {
            throw new LDAPException(ResultCode.NOT_SUPPORTED, LDAPMessages.ERR_ASYNC_NOT_SUPPORTED_IN_SYNCHRONOUS_MODE.get());
        }
        AsyncCompareResultListener listener;
        if (resultListener == null) {
            listener = DiscardAsyncListener.getInstance();
        }
        else {
            listener = resultListener;
        }
        return compareRequest.processAsync(this, listener);
    }
    
    public AsyncRequestID asyncCompare(final ReadOnlyCompareRequest compareRequest, final AsyncCompareResultListener resultListener) throws LDAPException {
        if (this.synchronousMode()) {
            throw new LDAPException(ResultCode.NOT_SUPPORTED, LDAPMessages.ERR_ASYNC_NOT_SUPPORTED_IN_SYNCHRONOUS_MODE.get());
        }
        return this.asyncCompare((CompareRequest)compareRequest, resultListener);
    }
    
    @Override
    public LDAPResult delete(final String dn) throws LDAPException {
        return this.delete(new DeleteRequest(dn));
    }
    
    @Override
    public LDAPResult delete(final DeleteRequest deleteRequest) throws LDAPException {
        Validator.ensureNotNull(deleteRequest);
        final LDAPResult ldapResult = deleteRequest.process(this, 1);
        switch (ldapResult.getResultCode().intValue()) {
            case 0:
            case 16654: {
                return ldapResult;
            }
            default: {
                throw new LDAPException(ldapResult);
            }
        }
    }
    
    @Override
    public LDAPResult delete(final ReadOnlyDeleteRequest deleteRequest) throws LDAPException {
        return this.delete((DeleteRequest)deleteRequest);
    }
    
    public AsyncRequestID asyncDelete(final DeleteRequest deleteRequest, final AsyncResultListener resultListener) throws LDAPException {
        Validator.ensureNotNull(deleteRequest);
        if (this.synchronousMode()) {
            throw new LDAPException(ResultCode.NOT_SUPPORTED, LDAPMessages.ERR_ASYNC_NOT_SUPPORTED_IN_SYNCHRONOUS_MODE.get());
        }
        AsyncResultListener listener;
        if (resultListener == null) {
            listener = DiscardAsyncListener.getInstance();
        }
        else {
            listener = resultListener;
        }
        return deleteRequest.processAsync(this, listener);
    }
    
    public AsyncRequestID asyncDelete(final ReadOnlyDeleteRequest deleteRequest, final AsyncResultListener resultListener) throws LDAPException {
        if (this.synchronousMode()) {
            throw new LDAPException(ResultCode.NOT_SUPPORTED, LDAPMessages.ERR_ASYNC_NOT_SUPPORTED_IN_SYNCHRONOUS_MODE.get());
        }
        return this.asyncDelete((DeleteRequest)deleteRequest, resultListener);
    }
    
    @ThreadSafety(level = ThreadSafetyLevel.METHOD_NOT_THREADSAFE)
    @Override
    public ExtendedResult processExtendedOperation(final String requestOID) throws LDAPException {
        Validator.ensureNotNull(requestOID);
        return this.processExtendedOperation(new ExtendedRequest(requestOID));
    }
    
    @ThreadSafety(level = ThreadSafetyLevel.METHOD_NOT_THREADSAFE)
    @Override
    public ExtendedResult processExtendedOperation(final String requestOID, final ASN1OctetString requestValue) throws LDAPException {
        Validator.ensureNotNull(requestOID);
        return this.processExtendedOperation(new ExtendedRequest(requestOID, requestValue));
    }
    
    @ThreadSafety(level = ThreadSafetyLevel.METHOD_NOT_THREADSAFE)
    @Override
    public ExtendedResult processExtendedOperation(final ExtendedRequest extendedRequest) throws LDAPException {
        Validator.ensureNotNull(extendedRequest);
        final ExtendedResult extendedResult = extendedRequest.process(this, 1);
        if (extendedResult.getOID() == null && extendedResult.getValue() == null) {
            switch (extendedResult.getResultCode().intValue()) {
                case 1:
                case 2:
                case 51:
                case 52:
                case 80:
                case 81:
                case 82:
                case 83:
                case 84:
                case 85:
                case 90:
                case 91: {
                    throw new LDAPException(extendedResult);
                }
            }
        }
        if (extendedResult.getResultCode() == ResultCode.SUCCESS && extendedRequest.getOID().equals("1.3.6.1.4.1.1466.20037")) {
            this.startTLSRequest = extendedRequest.duplicate();
        }
        return extendedResult;
    }
    
    @Override
    public LDAPResult modify(final String dn, final Modification mod) throws LDAPException {
        Validator.ensureNotNull(dn, mod);
        return this.modify(new ModifyRequest(dn, mod));
    }
    
    @Override
    public LDAPResult modify(final String dn, final Modification... mods) throws LDAPException {
        Validator.ensureNotNull(dn, mods);
        return this.modify(new ModifyRequest(dn, mods));
    }
    
    @Override
    public LDAPResult modify(final String dn, final List<Modification> mods) throws LDAPException {
        Validator.ensureNotNull(dn, mods);
        return this.modify(new ModifyRequest(dn, mods));
    }
    
    @Override
    public LDAPResult modify(final String... ldifModificationLines) throws LDIFException, LDAPException {
        Validator.ensureNotNull(ldifModificationLines);
        return this.modify(new ModifyRequest(ldifModificationLines));
    }
    
    @Override
    public LDAPResult modify(final ModifyRequest modifyRequest) throws LDAPException {
        Validator.ensureNotNull(modifyRequest);
        final LDAPResult ldapResult = modifyRequest.process(this, 1);
        switch (ldapResult.getResultCode().intValue()) {
            case 0:
            case 16654: {
                return ldapResult;
            }
            default: {
                throw new LDAPException(ldapResult);
            }
        }
    }
    
    @Override
    public LDAPResult modify(final ReadOnlyModifyRequest modifyRequest) throws LDAPException {
        return this.modify((ModifyRequest)modifyRequest);
    }
    
    public AsyncRequestID asyncModify(final ModifyRequest modifyRequest, final AsyncResultListener resultListener) throws LDAPException {
        Validator.ensureNotNull(modifyRequest);
        if (this.synchronousMode()) {
            throw new LDAPException(ResultCode.NOT_SUPPORTED, LDAPMessages.ERR_ASYNC_NOT_SUPPORTED_IN_SYNCHRONOUS_MODE.get());
        }
        AsyncResultListener listener;
        if (resultListener == null) {
            listener = DiscardAsyncListener.getInstance();
        }
        else {
            listener = resultListener;
        }
        return modifyRequest.processAsync(this, listener);
    }
    
    public AsyncRequestID asyncModify(final ReadOnlyModifyRequest modifyRequest, final AsyncResultListener resultListener) throws LDAPException {
        if (this.synchronousMode()) {
            throw new LDAPException(ResultCode.NOT_SUPPORTED, LDAPMessages.ERR_ASYNC_NOT_SUPPORTED_IN_SYNCHRONOUS_MODE.get());
        }
        return this.asyncModify((ModifyRequest)modifyRequest, resultListener);
    }
    
    @Override
    public LDAPResult modifyDN(final String dn, final String newRDN, final boolean deleteOldRDN) throws LDAPException {
        Validator.ensureNotNull(dn, newRDN);
        return this.modifyDN(new ModifyDNRequest(dn, newRDN, deleteOldRDN));
    }
    
    @Override
    public LDAPResult modifyDN(final String dn, final String newRDN, final boolean deleteOldRDN, final String newSuperiorDN) throws LDAPException {
        Validator.ensureNotNull(dn, newRDN);
        return this.modifyDN(new ModifyDNRequest(dn, newRDN, deleteOldRDN, newSuperiorDN));
    }
    
    @Override
    public LDAPResult modifyDN(final ModifyDNRequest modifyDNRequest) throws LDAPException {
        Validator.ensureNotNull(modifyDNRequest);
        final LDAPResult ldapResult = modifyDNRequest.process(this, 1);
        switch (ldapResult.getResultCode().intValue()) {
            case 0:
            case 16654: {
                return ldapResult;
            }
            default: {
                throw new LDAPException(ldapResult);
            }
        }
    }
    
    @Override
    public LDAPResult modifyDN(final ReadOnlyModifyDNRequest modifyDNRequest) throws LDAPException {
        return this.modifyDN((ModifyDNRequest)modifyDNRequest);
    }
    
    public AsyncRequestID asyncModifyDN(final ModifyDNRequest modifyDNRequest, final AsyncResultListener resultListener) throws LDAPException {
        Validator.ensureNotNull(modifyDNRequest);
        if (this.synchronousMode()) {
            throw new LDAPException(ResultCode.NOT_SUPPORTED, LDAPMessages.ERR_ASYNC_NOT_SUPPORTED_IN_SYNCHRONOUS_MODE.get());
        }
        AsyncResultListener listener;
        if (resultListener == null) {
            listener = DiscardAsyncListener.getInstance();
        }
        else {
            listener = resultListener;
        }
        return modifyDNRequest.processAsync(this, listener);
    }
    
    public AsyncRequestID asyncModifyDN(final ReadOnlyModifyDNRequest modifyDNRequest, final AsyncResultListener resultListener) throws LDAPException {
        if (this.synchronousMode()) {
            throw new LDAPException(ResultCode.NOT_SUPPORTED, LDAPMessages.ERR_ASYNC_NOT_SUPPORTED_IN_SYNCHRONOUS_MODE.get());
        }
        return this.asyncModifyDN((ModifyDNRequest)modifyDNRequest, resultListener);
    }
    
    @Override
    public SearchResult search(final String baseDN, final SearchScope scope, final String filter, final String... attributes) throws LDAPSearchException {
        Validator.ensureNotNull(baseDN, filter);
        try {
            return this.search(new SearchRequest(baseDN, scope, filter, attributes));
        }
        catch (final LDAPSearchException lse) {
            Debug.debugException(lse);
            throw lse;
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw new LDAPSearchException(le);
        }
    }
    
    @Override
    public SearchResult search(final String baseDN, final SearchScope scope, final Filter filter, final String... attributes) throws LDAPSearchException {
        Validator.ensureNotNull(baseDN, filter);
        return this.search(new SearchRequest(baseDN, scope, filter, attributes));
    }
    
    @Override
    public SearchResult search(final SearchResultListener searchResultListener, final String baseDN, final SearchScope scope, final String filter, final String... attributes) throws LDAPSearchException {
        Validator.ensureNotNull(baseDN, filter);
        try {
            return this.search(new SearchRequest(searchResultListener, baseDN, scope, filter, attributes));
        }
        catch (final LDAPSearchException lse) {
            Debug.debugException(lse);
            throw lse;
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw new LDAPSearchException(le);
        }
    }
    
    @Override
    public SearchResult search(final SearchResultListener searchResultListener, final String baseDN, final SearchScope scope, final Filter filter, final String... attributes) throws LDAPSearchException {
        Validator.ensureNotNull(baseDN, filter);
        try {
            return this.search(new SearchRequest(searchResultListener, baseDN, scope, filter, attributes));
        }
        catch (final LDAPSearchException lse) {
            Debug.debugException(lse);
            throw lse;
        }
    }
    
    @Override
    public SearchResult search(final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int sizeLimit, final int timeLimit, final boolean typesOnly, final String filter, final String... attributes) throws LDAPSearchException {
        Validator.ensureNotNull(baseDN, filter);
        try {
            return this.search(new SearchRequest(baseDN, scope, derefPolicy, sizeLimit, timeLimit, typesOnly, filter, attributes));
        }
        catch (final LDAPSearchException lse) {
            Debug.debugException(lse);
            throw lse;
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw new LDAPSearchException(le);
        }
    }
    
    @Override
    public SearchResult search(final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int sizeLimit, final int timeLimit, final boolean typesOnly, final Filter filter, final String... attributes) throws LDAPSearchException {
        Validator.ensureNotNull(baseDN, filter);
        return this.search(new SearchRequest(baseDN, scope, derefPolicy, sizeLimit, timeLimit, typesOnly, filter, attributes));
    }
    
    @Override
    public SearchResult search(final SearchResultListener searchResultListener, final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int sizeLimit, final int timeLimit, final boolean typesOnly, final String filter, final String... attributes) throws LDAPSearchException {
        Validator.ensureNotNull(baseDN, filter);
        try {
            return this.search(new SearchRequest(searchResultListener, baseDN, scope, derefPolicy, sizeLimit, timeLimit, typesOnly, filter, attributes));
        }
        catch (final LDAPSearchException lse) {
            Debug.debugException(lse);
            throw lse;
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw new LDAPSearchException(le);
        }
    }
    
    @Override
    public SearchResult search(final SearchResultListener searchResultListener, final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int sizeLimit, final int timeLimit, final boolean typesOnly, final Filter filter, final String... attributes) throws LDAPSearchException {
        Validator.ensureNotNull(baseDN, filter);
        return this.search(new SearchRequest(searchResultListener, baseDN, scope, derefPolicy, sizeLimit, timeLimit, typesOnly, filter, attributes));
    }
    
    @Override
    public SearchResult search(final SearchRequest searchRequest) throws LDAPSearchException {
        Validator.ensureNotNull(searchRequest);
        SearchResult searchResult;
        try {
            searchResult = searchRequest.process(this, 1);
        }
        catch (final LDAPSearchException lse) {
            Debug.debugException(lse);
            throw lse;
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw new LDAPSearchException(le);
        }
        if (!searchResult.getResultCode().equals(ResultCode.SUCCESS)) {
            throw new LDAPSearchException(searchResult);
        }
        return searchResult;
    }
    
    @Override
    public SearchResult search(final ReadOnlySearchRequest searchRequest) throws LDAPSearchException {
        return this.search((SearchRequest)searchRequest);
    }
    
    @Override
    public SearchResultEntry searchForEntry(final String baseDN, final SearchScope scope, final String filter, final String... attributes) throws LDAPSearchException {
        SearchRequest r;
        try {
            r = new SearchRequest(baseDN, scope, DereferencePolicy.NEVER, 1, 0, false, filter, attributes);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw new LDAPSearchException(le);
        }
        return this.searchForEntry(r);
    }
    
    @Override
    public SearchResultEntry searchForEntry(final String baseDN, final SearchScope scope, final Filter filter, final String... attributes) throws LDAPSearchException {
        return this.searchForEntry(new SearchRequest(baseDN, scope, DereferencePolicy.NEVER, 1, 0, false, filter, attributes));
    }
    
    @Override
    public SearchResultEntry searchForEntry(final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int timeLimit, final boolean typesOnly, final String filter, final String... attributes) throws LDAPSearchException {
        SearchRequest r;
        try {
            r = new SearchRequest(baseDN, scope, derefPolicy, 1, timeLimit, typesOnly, filter, attributes);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw new LDAPSearchException(le);
        }
        return this.searchForEntry(r);
    }
    
    @Override
    public SearchResultEntry searchForEntry(final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int timeLimit, final boolean typesOnly, final Filter filter, final String... attributes) throws LDAPSearchException {
        return this.searchForEntry(new SearchRequest(baseDN, scope, derefPolicy, 1, timeLimit, typesOnly, filter, attributes));
    }
    
    @Override
    public SearchResultEntry searchForEntry(final SearchRequest searchRequest) throws LDAPSearchException {
        SearchRequest r;
        if (searchRequest.getSearchResultListener() != null || searchRequest.getSizeLimit() != 1) {
            r = new SearchRequest(searchRequest.getBaseDN(), searchRequest.getScope(), searchRequest.getDereferencePolicy(), 1, searchRequest.getTimeLimitSeconds(), searchRequest.typesOnly(), searchRequest.getFilter(), searchRequest.getAttributes());
            r.setFollowReferrals(searchRequest.followReferralsInternal());
            r.setReferralConnector(searchRequest.getReferralConnectorInternal());
            r.setResponseTimeoutMillis(searchRequest.getResponseTimeoutMillis(null));
            if (searchRequest.hasControl()) {
                r.setControlsInternal(searchRequest.getControls());
            }
        }
        else {
            r = searchRequest;
        }
        SearchResult result;
        try {
            result = this.search(r);
        }
        catch (final LDAPSearchException lse) {
            Debug.debugException(lse);
            if (lse.getResultCode() == ResultCode.NO_SUCH_OBJECT) {
                return null;
            }
            throw lse;
        }
        if (result.getEntryCount() == 0) {
            return null;
        }
        return result.getSearchEntries().get(0);
    }
    
    @Override
    public SearchResultEntry searchForEntry(final ReadOnlySearchRequest searchRequest) throws LDAPSearchException {
        return this.searchForEntry((SearchRequest)searchRequest);
    }
    
    public AsyncRequestID asyncSearch(final SearchRequest searchRequest) throws LDAPException {
        Validator.ensureNotNull(searchRequest);
        final SearchResultListener searchListener = searchRequest.getSearchResultListener();
        if (searchListener == null) {
            final LDAPException le = new LDAPException(ResultCode.PARAM_ERROR, LDAPMessages.ERR_ASYNC_SEARCH_NO_LISTENER.get());
            Debug.debugCodingError(le);
            throw le;
        }
        if (!(searchListener instanceof AsyncSearchResultListener)) {
            final LDAPException le = new LDAPException(ResultCode.PARAM_ERROR, LDAPMessages.ERR_ASYNC_SEARCH_INVALID_LISTENER.get());
            Debug.debugCodingError(le);
            throw le;
        }
        if (this.synchronousMode()) {
            throw new LDAPException(ResultCode.NOT_SUPPORTED, LDAPMessages.ERR_ASYNC_NOT_SUPPORTED_IN_SYNCHRONOUS_MODE.get());
        }
        return searchRequest.processAsync(this, (AsyncSearchResultListener)searchListener);
    }
    
    public AsyncRequestID asyncSearch(final ReadOnlySearchRequest searchRequest) throws LDAPException {
        if (this.synchronousMode()) {
            throw new LDAPException(ResultCode.NOT_SUPPORTED, LDAPMessages.ERR_ASYNC_NOT_SUPPORTED_IN_SYNCHRONOUS_MODE.get());
        }
        return this.asyncSearch((SearchRequest)searchRequest);
    }
    
    public LDAPResult processOperation(final LDAPRequest request) throws LDAPException {
        if (request instanceof BindRequest) {
            return this.processBindOperation((BindRequest)request);
        }
        return request.process(this, 1);
    }
    
    private BindResult processBindOperation(final BindRequest bindRequest) throws LDAPException {
        boolean hasRetainIdentityControl = false;
        for (final Control c : bindRequest.getControls()) {
            if (c.getOID().equals("1.3.6.1.4.1.30221.2.5.3")) {
                hasRetainIdentityControl = true;
                break;
            }
        }
        if (!hasRetainIdentityControl) {
            this.lastBindRequest = null;
        }
        final BindResult bindResult = bindRequest.process(this, 1);
        if (bindResult.getResultCode().equals(ResultCode.SUCCESS) && !hasRetainIdentityControl) {
            this.lastBindRequest = bindRequest;
            if (this.connectionOptions.useSchema()) {
                try {
                    this.cachedSchema = getCachedSchema(this);
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                }
            }
        }
        return bindResult;
    }
    
    public ReferralConnector getReferralConnector() {
        if (this.referralConnector == null) {
            return this;
        }
        return this.referralConnector;
    }
    
    public void setReferralConnector(final ReferralConnector referralConnector) {
        if (referralConnector == null) {
            this.referralConnector = this;
        }
        else {
            this.referralConnector = referralConnector;
        }
    }
    
    void sendMessage(final LDAPMessage message, final long sendTimeoutMillis) throws LDAPException {
        if (this.needsReconnect.compareAndSet(true, false)) {
            this.reconnect();
        }
        final LDAPConnectionInternals internals = this.connectionInternals;
        if (internals == null) {
            throw new LDAPException(ResultCode.SERVER_DOWN, LDAPMessages.ERR_CONN_NOT_ESTABLISHED.get());
        }
        final boolean autoReconnect = this.connectionOptions.autoReconnect();
        internals.sendMessage(message, sendTimeoutMillis, autoReconnect);
        this.lastCommunicationTime = System.currentTimeMillis();
    }
    
    int nextMessageID() {
        final LDAPConnectionInternals internals = this.connectionInternals;
        if (internals == null) {
            return -1;
        }
        return internals.nextMessageID();
    }
    
    DisconnectInfo getDisconnectInfo() {
        return this.disconnectInfo.get();
    }
    
    public void setDisconnectInfo(final DisconnectType type, final String message, final Throwable cause) {
        this.disconnectInfo.compareAndSet(null, new DisconnectInfo(this, type, message, cause));
    }
    
    DisconnectInfo setDisconnectInfo(final DisconnectInfo info) {
        this.disconnectInfo.compareAndSet(null, info);
        return this.disconnectInfo.get();
    }
    
    public DisconnectType getDisconnectType() {
        final DisconnectInfo di = this.disconnectInfo.get();
        if (di == null) {
            return null;
        }
        return di.getType();
    }
    
    public String getDisconnectMessage() {
        final DisconnectInfo di = this.disconnectInfo.get();
        if (di == null) {
            return null;
        }
        return di.getMessage();
    }
    
    public Throwable getDisconnectCause() {
        final DisconnectInfo di = this.disconnectInfo.get();
        if (di == null) {
            return null;
        }
        return di.getCause();
    }
    
    void setClosed() {
        this.needsReconnect.set(false);
        if (this.disconnectInfo.get() == null) {
            try {
                final StackTraceElement[] stackElements = Thread.currentThread().getStackTrace();
                final StackTraceElement[] parentStackElements = new StackTraceElement[stackElements.length - 1];
                System.arraycopy(stackElements, 1, parentStackElements, 0, parentStackElements.length);
                this.setDisconnectInfo(DisconnectType.OTHER, LDAPMessages.ERR_CONN_CLOSED_BY_UNEXPECTED_CALL_PATH.get(StaticUtils.getStackTrace(parentStackElements)), null);
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
        }
        this.connectionStatistics.incrementNumDisconnects();
        final LDAPConnectionInternals internals = this.connectionInternals;
        if (internals != null) {
            internals.close();
            this.connectionInternals = null;
        }
        this.cachedSchema = null;
        this.lastCommunicationTime = -1L;
        synchronized (this) {
            final Timer t = this.timer;
            this.timer = null;
            if (t != null) {
                t.cancel();
            }
        }
    }
    
    void registerResponseAcceptor(final int messageID, final ResponseAcceptor responseAcceptor) throws LDAPException {
        if (this.needsReconnect.compareAndSet(true, false)) {
            this.reconnect();
        }
        final LDAPConnectionInternals internals = this.connectionInternals;
        if (internals == null) {
            throw new LDAPException(ResultCode.SERVER_DOWN, LDAPMessages.ERR_CONN_NOT_ESTABLISHED.get());
        }
        internals.registerResponseAcceptor(messageID, responseAcceptor);
    }
    
    void deregisterResponseAcceptor(final int messageID) {
        final LDAPConnectionInternals internals = this.connectionInternals;
        if (internals != null) {
            internals.deregisterResponseAcceptor(messageID);
        }
    }
    
    synchronized Timer getTimer() {
        if (this.timer == null) {
            this.timer = new Timer("Timer thread for " + this.toString(), true);
        }
        return this.timer;
    }
    
    @Override
    public LDAPConnection getReferralConnection(final LDAPURL referralURL, final LDAPConnection connection) throws LDAPException {
        final String host = referralURL.getHost();
        final int port = referralURL.getPort();
        BindRequest bindRequest = null;
        if (connection.lastBindRequest != null) {
            bindRequest = connection.lastBindRequest.getRebindRequest(host, port);
            if (bindRequest == null) {
                throw new LDAPException(ResultCode.REFERRAL, LDAPMessages.ERR_CONN_CANNOT_AUTHENTICATE_FOR_REFERRAL.get(host, port));
            }
        }
        final ExtendedRequest connStartTLSRequest = connection.startTLSRequest;
        final LDAPConnection conn = new LDAPConnection(connection.socketFactory, connection.connectionOptions, host, port);
        if (connStartTLSRequest != null) {
            try {
                final ExtendedResult startTLSResult = conn.processExtendedOperation(connStartTLSRequest);
                if (startTLSResult.getResultCode() != ResultCode.SUCCESS) {
                    throw new LDAPException(startTLSResult);
                }
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                conn.setDisconnectInfo(DisconnectType.SECURITY_PROBLEM, null, le);
                conn.close();
                throw le;
            }
        }
        if (bindRequest != null) {
            try {
                conn.bind(bindRequest);
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                conn.setDisconnectInfo(DisconnectType.BIND_FAILED, null, le);
                conn.close();
                throw le;
            }
        }
        return conn;
    }
    
    public BindRequest getLastBindRequest() {
        return this.lastBindRequest;
    }
    
    public ExtendedRequest getStartTLSRequest() {
        return this.startTLSRequest;
    }
    
    LDAPConnectionInternals getConnectionInternals(final boolean throwIfDisconnected) throws LDAPException {
        final LDAPConnectionInternals internals = this.connectionInternals;
        if (internals == null && throwIfDisconnected) {
            throw new LDAPException(ResultCode.SERVER_DOWN, LDAPMessages.ERR_CONN_NOT_ESTABLISHED.get());
        }
        return internals;
    }
    
    Schema getCachedSchema() {
        return this.cachedSchema;
    }
    
    void setCachedSchema(final Schema cachedSchema) {
        this.cachedSchema = cachedSchema;
    }
    
    public boolean synchronousMode() {
        final LDAPConnectionInternals internals = this.connectionInternals;
        return internals != null && internals.synchronousMode();
    }
    
    LDAPResponse readResponse(final int messageID) throws LDAPException {
        final LDAPConnectionInternals internals = this.connectionInternals;
        if (internals != null) {
            final LDAPResponse response = internals.getConnectionReader().readResponse(messageID);
            Debug.debugLDAPResult(response, this);
            return response;
        }
        final DisconnectInfo di = this.disconnectInfo.get();
        if (di == null) {
            return new ConnectionClosedResponse(ResultCode.CONNECT_ERROR, LDAPMessages.ERR_CONN_READ_RESPONSE_NOT_ESTABLISHED.get());
        }
        return new ConnectionClosedResponse(di.getType().getResultCode(), di.getMessage());
    }
    
    public long getConnectTime() {
        final LDAPConnectionInternals internals = this.connectionInternals;
        if (internals != null) {
            return internals.getConnectTime();
        }
        return -1L;
    }
    
    public long getLastCommunicationTime() {
        if (this.lastCommunicationTime > 0L) {
            return this.lastCommunicationTime;
        }
        return this.getConnectTime();
    }
    
    void setLastCommunicationTime() {
        this.lastCommunicationTime = System.currentTimeMillis();
    }
    
    public LDAPConnectionStatistics getConnectionStatistics() {
        return this.connectionStatistics;
    }
    
    public int getActiveOperationCount() {
        final LDAPConnectionInternals internals = this.connectionInternals;
        if (internals == null) {
            return -1;
        }
        if (internals.synchronousMode()) {
            return -1;
        }
        return internals.getConnectionReader().getActiveOperationCount();
    }
    
    private static Schema getCachedSchema(final LDAPConnection c) throws LDAPException {
        final Schema s = c.getSchema();
        synchronized (LDAPConnection.SCHEMA_SET) {
            return LDAPConnection.SCHEMA_SET.addAndGet(s);
        }
    }
    
    synchronized Object getAttachment(final String name) {
        if (this.attachments == null) {
            return null;
        }
        return this.attachments.get(name);
    }
    
    synchronized void setAttachment(final String name, final Object value) {
        if (this.attachments == null) {
            this.attachments = new HashMap<String, Object>(StaticUtils.computeMapCapacity(10));
        }
        if (value == null) {
            this.attachments.remove(name);
        }
        else {
            this.attachments.put(name, value);
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.setDisconnectInfo(DisconnectType.CLOSED_BY_FINALIZER, null, null);
        this.setClosed();
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("LDAPConnection(");
        final String name = this.connectionName;
        final String poolName = this.connectionPoolName;
        if (name != null) {
            buffer.append("name='");
            buffer.append(name);
            buffer.append("', ");
        }
        else if (poolName != null) {
            buffer.append("poolName='");
            buffer.append(poolName);
            buffer.append("', ");
        }
        final LDAPConnectionInternals internals = this.connectionInternals;
        if (internals != null && internals.isConnected()) {
            buffer.append("connected to ");
            buffer.append(internals.getHost());
            buffer.append(':');
            buffer.append(internals.getPort());
        }
        else {
            buffer.append("not connected");
        }
        buffer.append(')');
    }
    
    static {
        NEXT_CONNECTION_ID = new AtomicLong(0L);
        DEFAULT_SOCKET_FACTORY = SocketFactory.getDefault();
        SCHEMA_SET = new WeakHashSet<Schema>();
    }
}
