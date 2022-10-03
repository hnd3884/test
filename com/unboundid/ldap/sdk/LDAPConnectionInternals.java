package com.unboundid.ldap.sdk;

import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.protocol.LDAPMessage;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedOutputStream;
import com.unboundid.util.DebugType;
import java.util.logging.Level;
import java.io.IOException;
import com.unboundid.util.Debug;
import javax.net.ssl.SSLSocket;
import javax.net.SocketFactory;
import java.net.Socket;
import javax.security.sasl.SaslClient;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicInteger;
import com.unboundid.asn1.ASN1Buffer;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicLong;
import com.unboundid.util.InternalUseOnly;

@InternalUseOnly
final class LDAPConnectionInternals
{
    private static final AtomicLong ACTIVE_CONNECTION_COUNT;
    private static final AtomicReference<ThreadLocal<ASN1Buffer>> ASN1_BUFFERS;
    private final AtomicInteger nextMessageID;
    private final boolean synchronousMode;
    private final InetAddress inetAddress;
    private final int port;
    private final long connectTime;
    private final LDAPConnection connection;
    private final LDAPConnectionReader connectionReader;
    private volatile OutputStream outputStream;
    private volatile SaslClient saslClient;
    private volatile Socket socket;
    private final String host;
    private final WriteTimeoutHandler writeTimeoutHandler;
    
    LDAPConnectionInternals(final LDAPConnection connection, final LDAPConnectionOptions options, final SocketFactory socketFactory, final String host, final InetAddress inetAddress, final int port, final int timeout) throws IOException {
        this.connection = connection;
        this.host = host;
        this.inetAddress = inetAddress;
        this.port = port;
        if (options.captureConnectStackTrace()) {
            connection.setConnectStackTrace(Thread.currentThread().getStackTrace());
        }
        this.connectTime = System.currentTimeMillis();
        this.nextMessageID = new AtomicInteger(0);
        this.synchronousMode = options.useSynchronousMode();
        this.saslClient = null;
        this.socket = null;
        this.writeTimeoutHandler = new WriteTimeoutHandler(connection);
        try {
            final ConnectThread connectThread = new ConnectThread(socketFactory, inetAddress, port, timeout);
            connectThread.start();
            this.socket = connectThread.getConnectedSocket();
            if (this.socket instanceof SSLSocket) {
                final SSLSocket sslSocket = (SSLSocket)this.socket;
                options.getSSLSocketVerifier().verifySSLSocket(host, port, sslSocket);
            }
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            if (this.socket != null) {
                this.socket.close();
            }
            this.writeTimeoutHandler.destroy();
            throw new IOException(le);
        }
        try {
            Debug.debugConnect(host, port, connection);
            if (options.getReceiveBufferSize() > 0) {
                this.socket.setReceiveBufferSize(options.getReceiveBufferSize());
            }
            if (options.getSendBufferSize() > 0) {
                this.socket.setSendBufferSize(options.getSendBufferSize());
            }
            this.socket.setKeepAlive(options.useKeepAlive());
            this.socket.setReuseAddress(options.useReuseAddress());
            this.socket.setSoLinger(options.useLinger(), options.getLingerTimeoutSeconds());
            this.socket.setTcpNoDelay(options.useTCPNoDelay());
            final int soTimeout = Math.max(0, (int)options.getResponseTimeoutMillis());
            Debug.debug(Level.INFO, DebugType.CONNECT, "Setting the SO_TIMEOUT value for connection " + connection + " to " + soTimeout + "ms.");
            this.socket.setSoTimeout(soTimeout);
            this.outputStream = new BufferedOutputStream(this.socket.getOutputStream());
            this.connectionReader = new LDAPConnectionReader(connection, this);
        }
        catch (final IOException ioe) {
            Debug.debugException(ioe);
            try {
                this.socket.close();
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
            this.writeTimeoutHandler.destroy();
            throw ioe;
        }
        LDAPConnectionInternals.ACTIVE_CONNECTION_COUNT.incrementAndGet();
    }
    
    void startConnectionReader() {
        if (!this.synchronousMode) {
            this.connectionReader.start();
        }
    }
    
    LDAPConnection getConnection() {
        return this.connection;
    }
    
    LDAPConnectionReader getConnectionReader() {
        return this.connectionReader;
    }
    
    InetAddress getInetAddress() {
        return this.inetAddress;
    }
    
    String getHost() {
        return this.host;
    }
    
    int getPort() {
        return this.port;
    }
    
    Socket getSocket() {
        return this.socket;
    }
    
    void setSocket(final Socket socket) {
        this.socket = socket;
    }
    
    OutputStream getOutputStream() {
        return this.outputStream;
    }
    
    boolean isConnected() {
        return this.socket.isConnected();
    }
    
    boolean synchronousMode() {
        return this.synchronousMode;
    }
    
    void convertToTLS(final SSLSocketFactory sslSocketFactory) throws LDAPException {
        this.outputStream = this.connectionReader.doStartTLS(sslSocketFactory);
    }
    
    void applySASLQoP(final SaslClient saslClient) throws LDAPException {
        this.saslClient = saslClient;
        this.connectionReader.applySASLQoP(saslClient);
    }
    
    int nextMessageID() {
        int msgID = this.nextMessageID.incrementAndGet();
        if (msgID > 0) {
            return msgID;
        }
        while (!this.nextMessageID.compareAndSet(msgID, 1)) {
            msgID = this.nextMessageID.incrementAndGet();
            if (msgID > 0) {
                return msgID;
            }
        }
        return 1;
    }
    
    void registerResponseAcceptor(final int messageID, final ResponseAcceptor responseAcceptor) throws LDAPException {
        if (!this.isConnected()) {
            final LDAPConnectionOptions connectionOptions = this.connection.getConnectionOptions();
            final boolean autoReconnect = connectionOptions.autoReconnect();
            final boolean closeRequested = this.connection.closeRequested();
            if (!autoReconnect || closeRequested) {
                throw new LDAPException(ResultCode.SERVER_DOWN, LDAPMessages.ERR_CONN_NOT_ESTABLISHED.get());
            }
            this.connection.reconnect();
            this.connection.registerResponseAcceptor(messageID, responseAcceptor);
        }
        this.connectionReader.registerResponseAcceptor(messageID, responseAcceptor);
    }
    
    void deregisterResponseAcceptor(final int messageID) {
        this.connectionReader.deregisterResponseAcceptor(messageID);
    }
    
    void sendMessage(final LDAPMessage message, final long sendTimeoutMillis, final boolean allowRetry) throws LDAPException {
        if (!this.isConnected()) {
            throw new LDAPException(ResultCode.SERVER_DOWN, LDAPMessages.ERR_CONN_NOT_ESTABLISHED.get());
        }
        ASN1Buffer buffer = LDAPConnectionInternals.ASN1_BUFFERS.get().get();
        if (buffer == null) {
            buffer = new ASN1Buffer();
            LDAPConnectionInternals.ASN1_BUFFERS.get().set(buffer);
        }
        buffer.clear();
        try {
            message.writeTo(buffer);
        }
        catch (final LDAPRuntimeException lre) {
            Debug.debugException(lre);
            lre.throwLDAPException();
        }
        try {
            final int soTimeout = Math.max(0, (int)sendTimeoutMillis);
            if (Debug.debugEnabled()) {
                Debug.debug(Level.INFO, DebugType.CONNECT, "Setting the SO_TIMEOUT value for connection " + this.connection + " to " + soTimeout + "ms.");
            }
            this.socket.setSoTimeout(soTimeout);
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
        Label_0199: {
            if (sendTimeoutMillis > 0L) {
                final Long writeID = this.writeTimeoutHandler.beginWrite(sendTimeoutMillis);
                break Label_0199;
            }
            final Long writeID = null;
            try {
                final OutputStream os = this.outputStream;
                if (this.saslClient == null) {
                    buffer.writeTo(os);
                }
                else {
                    final byte[] clearBytes = buffer.toByteArray();
                    final byte[] saslBytes = this.saslClient.wrap(clearBytes, 0, clearBytes.length);
                    final byte[] lengthBytes = { (byte)(saslBytes.length >> 24 & 0xFF), (byte)(saslBytes.length >> 16 & 0xFF), (byte)(saslBytes.length >> 8 & 0xFF), (byte)(saslBytes.length & 0xFF) };
                    os.write(lengthBytes);
                    os.write(saslBytes);
                }
                os.flush();
            }
            catch (final IOException ioe) {
                Debug.debugException(ioe);
                if (message.getProtocolOpType() == 66) {
                    return;
                }
                final boolean closeRequested = this.connection.closeRequested();
                if (allowRetry && !closeRequested && !this.connection.synchronousMode()) {
                    this.connection.reconnect();
                    try {
                        this.sendMessage(message, sendTimeoutMillis, false);
                        return;
                    }
                    catch (final Exception e2) {
                        Debug.debugException(e2);
                    }
                }
                throw new LDAPException(ResultCode.SERVER_DOWN, LDAPMessages.ERR_CONN_SEND_ERROR.get(this.host + ':' + this.port, StaticUtils.getExceptionMessage(ioe)), ioe);
            }
            catch (final Exception e3) {
                Debug.debugException(e3);
                throw new LDAPException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_CONN_ENCODE_ERROR.get(this.host + ':' + this.port, StaticUtils.getExceptionMessage(e3)), e3);
            }
            finally {
                if (writeID != null) {
                    this.writeTimeoutHandler.writeCompleted(writeID);
                }
                if (buffer.zeroBufferOnClear()) {
                    buffer.clear();
                }
            }
        }
    }
    
    void close() {
        DisconnectInfo disconnectInfo = this.connection.getDisconnectInfo();
        if (disconnectInfo == null) {
            disconnectInfo = this.connection.setDisconnectInfo(new DisconnectInfo(this.connection, DisconnectType.UNKNOWN, null, null));
        }
        final boolean closedByFinalizer = disconnectInfo.getType() == DisconnectType.CLOSED_BY_FINALIZER && this.socket.isConnected();
        this.writeTimeoutHandler.destroy();
        try {
            this.socket.close();
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
        try {
            this.connectionReader.close(false);
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
        try {
            this.outputStream.close();
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
        if (this.saslClient != null) {
            try {
                this.saslClient.dispose();
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
            finally {
                this.saslClient = null;
            }
        }
        Debug.debugDisconnect(this.host, this.port, this.connection, disconnectInfo.getType(), disconnectInfo.getMessage(), disconnectInfo.getCause());
        if (closedByFinalizer && Debug.debugEnabled(DebugType.LDAP)) {
            Debug.debug(Level.WARNING, DebugType.LDAP, "Connection closed by LDAP SDK finalizer:  " + this.toString());
        }
        disconnectInfo.notifyDisconnectHandler();
        this.connection.setServerSet(null);
        final long remainingActiveConnections = LDAPConnectionInternals.ACTIVE_CONNECTION_COUNT.decrementAndGet();
        if (remainingActiveConnections <= 0L) {
            LDAPConnectionInternals.ASN1_BUFFERS.set(new ThreadLocal<ASN1Buffer>());
            if (remainingActiveConnections < 0L) {
                LDAPConnectionInternals.ACTIVE_CONNECTION_COUNT.compareAndSet(remainingActiveConnections, 0L);
            }
        }
    }
    
    public long getConnectTime() {
        if (this.isConnected()) {
            return this.connectTime;
        }
        return -1L;
    }
    
    static long getActiveConnectionCount() {
        return LDAPConnectionInternals.ACTIVE_CONNECTION_COUNT.get();
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("LDAPConnectionInternals(host='");
        buffer.append(this.host);
        buffer.append("', port=");
        buffer.append(this.port);
        buffer.append(", connected=");
        buffer.append(this.socket.isConnected());
        buffer.append(", nextMessageID=");
        buffer.append(this.nextMessageID.get());
        buffer.append(')');
    }
    
    static {
        ACTIVE_CONNECTION_COUNT = new AtomicLong(0L);
        ASN1_BUFFERS = new AtomicReference<ThreadLocal<ASN1Buffer>>(new ThreadLocal<ASN1Buffer>());
    }
}
