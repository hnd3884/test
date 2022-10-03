package com.unboundid.ldap.sdk;

import java.util.Iterator;
import com.unboundid.asn1.InternalASN1Helper;
import javax.security.sasl.SaslClient;
import com.unboundid.ldap.protocol.LDAPResponse;
import com.unboundid.ldap.sdk.unboundidds.extensions.InteractiveTransactionAbortedExtendedResult;
import com.unboundid.ldap.sdk.extensions.NoticeOfDisconnectionExtendedResult;
import com.unboundid.util.DebugType;
import com.unboundid.asn1.ASN1Exception;
import java.io.InterruptedIOException;
import javax.net.ssl.SSLSocket;
import com.unboundid.util.Debug;
import java.util.logging.Level;
import java.net.SocketTimeoutException;
import com.unboundid.ldap.protocol.LDAPMessage;
import java.io.IOException;
import com.unboundid.util.StaticUtils;
import java.io.BufferedInputStream;
import com.unboundid.util.WakeableSleeper;
import javax.net.ssl.SSLSocketFactory;
import java.net.Socket;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;
import com.unboundid.asn1.ASN1StreamReader;
import com.unboundid.util.InternalUseOnly;

@InternalUseOnly
final class LDAPConnectionReader extends Thread
{
    private static final int DEFAULT_INPUT_BUFFER_SIZE = 4096;
    private volatile ASN1StreamReader asn1StreamReader;
    private volatile boolean closeRequested;
    private final ConcurrentHashMap<Integer, ResponseAcceptor> acceptorMap;
    private volatile Exception startTLSException;
    private volatile InputStream inputStream;
    private volatile OutputStream startTLSOutputStream;
    private final LDAPConnection connection;
    private volatile Socket socket;
    private volatile SSLSocketFactory sslSocketFactory;
    private volatile Thread thread;
    private final WakeableSleeper startTLSSleeper;
    
    LDAPConnectionReader(final LDAPConnection connection, final LDAPConnectionInternals connectionInternals) throws IOException {
        this.connection = connection;
        this.setName(this.constructThreadName(connectionInternals));
        this.setDaemon(true);
        this.socket = connectionInternals.getSocket();
        this.inputStream = new BufferedInputStream(this.socket.getInputStream(), 4096);
        this.asn1StreamReader = new ASN1StreamReader(this.inputStream, connection.getConnectionOptions().getMaxMessageSize());
        this.acceptorMap = new ConcurrentHashMap<Integer, ResponseAcceptor>(StaticUtils.computeMapCapacity(10));
        this.closeRequested = false;
        this.sslSocketFactory = null;
        this.startTLSException = null;
        this.startTLSOutputStream = null;
        this.startTLSSleeper = new WakeableSleeper();
    }
    
    void registerResponseAcceptor(final int messageID, final ResponseAcceptor acceptor) throws LDAPException {
        final ResponseAcceptor existingAcceptor = this.acceptorMap.putIfAbsent(messageID, acceptor);
        if (existingAcceptor != null) {
            throw new LDAPException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_CONNREADER_MSGID_IN_USE.get(String.valueOf(acceptor), messageID, String.valueOf(this.connection), String.valueOf(existingAcceptor)));
        }
    }
    
    void deregisterResponseAcceptor(final int messageID) {
        this.acceptorMap.remove(messageID);
    }
    
    int getActiveOperationCount() {
        return this.acceptorMap.size();
    }
    
    @Override
    public void run() {
        boolean reconnect = false;
        this.thread = Thread.currentThread();
        while (!this.closeRequested) {
            try {
                LDAPResponse response;
                try {
                    response = LDAPMessage.readLDAPResponseFrom(this.asn1StreamReader, true, this.connection.getCachedSchema());
                }
                catch (final LDAPException le) {
                    final Throwable t = le.getCause();
                    if (t != null && t instanceof SocketTimeoutException) {
                        final SocketTimeoutException ste = (SocketTimeoutException)t;
                        Debug.debugException(Level.FINEST, ste);
                        if (this.sslSocketFactory == null) {
                            continue;
                        }
                        final LDAPConnectionOptions connectionOptions = this.connection.getConnectionOptions();
                        try {
                            final int responseTimeoutMillis = (int)connectionOptions.getResponseTimeoutMillis();
                            if (responseTimeoutMillis > 0) {
                                InternalSDKHelper.setSoTimeout(this.connection, responseTimeoutMillis);
                            }
                            else {
                                InternalSDKHelper.setSoTimeout(this.connection, 0);
                            }
                            final SSLSocket sslSocket;
                            synchronized (this.sslSocketFactory) {
                                sslSocket = (SSLSocket)this.sslSocketFactory.createSocket(this.socket, this.connection.getConnectedAddress(), this.socket.getPort(), true);
                                sslSocket.startHandshake();
                            }
                            connectionOptions.getSSLSocketVerifier().verifySSLSocket(this.connection.getConnectedAddress(), this.socket.getPort(), sslSocket);
                            this.inputStream = new BufferedInputStream(sslSocket.getInputStream(), 4096);
                            this.asn1StreamReader = new ASN1StreamReader(this.inputStream, connectionOptions.getMaxMessageSize());
                            this.startTLSOutputStream = sslSocket.getOutputStream();
                            this.socket = sslSocket;
                            this.connection.getConnectionInternals(true).setSocket(sslSocket);
                            this.startTLSSleeper.wakeup();
                        }
                        catch (final Exception e) {
                            Debug.debugException(e);
                            this.connection.setDisconnectInfo(DisconnectType.SECURITY_PROBLEM, StaticUtils.getExceptionMessage(e), e);
                            this.startTLSException = e;
                            this.closeRequested = true;
                            if (this.thread != null) {
                                this.thread.setName(this.thread.getName() + " (closed)");
                                this.thread = null;
                            }
                            this.closeInternal(true, StaticUtils.getExceptionMessage(e));
                            this.startTLSSleeper.wakeup();
                            return;
                        }
                        this.sslSocketFactory = null;
                        continue;
                    }
                    if (this.closeRequested || this.connection.closeRequested() || this.connection.getDisconnectType() != null) {
                        this.closeRequested = true;
                        Debug.debugException(Level.FINEST, le);
                    }
                    else {
                        Debug.debugException(le);
                    }
                    Level debugLevel = Level.SEVERE;
                    String message;
                    if (t == null) {
                        this.connection.setDisconnectInfo(DisconnectType.DECODE_ERROR, le.getMessage(), t);
                        message = le.getMessage();
                        debugLevel = Level.WARNING;
                    }
                    else if (t instanceof InterruptedIOException && this.socket.isClosed()) {
                        this.connection.setDisconnectInfo(DisconnectType.SERVER_CLOSED_WITHOUT_NOTICE, le.getMessage(), t);
                        message = LDAPMessages.ERR_READER_CLOSING_DUE_TO_INTERRUPTED_IO.get(this.connection.getHostPort());
                        debugLevel = Level.WARNING;
                    }
                    else if (t instanceof IOException) {
                        this.connection.setDisconnectInfo(DisconnectType.IO_ERROR, le.getMessage(), t);
                        message = LDAPMessages.ERR_READER_CLOSING_DUE_TO_IO_EXCEPTION.get(this.connection.getHostPort(), StaticUtils.getExceptionMessage(t));
                        debugLevel = Level.WARNING;
                    }
                    else if (t instanceof ASN1Exception) {
                        this.connection.setDisconnectInfo(DisconnectType.DECODE_ERROR, le.getMessage(), t);
                        message = LDAPMessages.ERR_READER_CLOSING_DUE_TO_ASN1_EXCEPTION.get(this.connection.getHostPort(), StaticUtils.getExceptionMessage(t));
                    }
                    else {
                        this.connection.setDisconnectInfo(DisconnectType.LOCAL_ERROR, le.getMessage(), t);
                        message = LDAPMessages.ERR_READER_CLOSING_DUE_TO_EXCEPTION.get(this.connection.getHostPort(), StaticUtils.getExceptionMessage(t));
                    }
                    Debug.debug(debugLevel, DebugType.LDAP, message, t);
                    final boolean autoReconnect = this.connection.getConnectionOptions().autoReconnect();
                    if (!this.closeRequested && autoReconnect) {
                        reconnect = true;
                        break;
                    }
                    this.closeRequested = true;
                    if (this.thread != null) {
                        this.thread.setName(this.thread.getName() + " (closed)");
                        this.thread = null;
                    }
                    this.closeInternal(true, message);
                    return;
                }
                if (response == null) {
                    this.connection.setDisconnectInfo(DisconnectType.SERVER_CLOSED_WITHOUT_NOTICE, null, null);
                    final boolean autoReconnect2 = this.connection.getConnectionOptions().autoReconnect();
                    if (this.closeRequested || this.connection.unbindRequestSent() || !autoReconnect2) {
                        this.closeRequested = true;
                        if (this.thread != null) {
                            this.thread.setName(this.thread.getName() + " (closed)");
                            this.thread = null;
                        }
                        this.closeInternal(true, null);
                        return;
                    }
                    reconnect = true;
                }
                else {
                    Debug.debugLDAPResult(response, this.connection);
                    this.connection.setLastCommunicationTime();
                    ResponseAcceptor responseAcceptor;
                    if (response instanceof SearchResultEntry || response instanceof SearchResultReference) {
                        responseAcceptor = this.acceptorMap.get(response.getMessageID());
                    }
                    else {
                        if (response instanceof IntermediateResponse) {
                            final IntermediateResponse ir = (IntermediateResponse)response;
                            responseAcceptor = this.acceptorMap.get(response.getMessageID());
                            IntermediateResponseListener l = null;
                            if (responseAcceptor instanceof LDAPRequest) {
                                final LDAPRequest r = (LDAPRequest)responseAcceptor;
                                l = r.getIntermediateResponseListener();
                            }
                            else if (responseAcceptor instanceof IntermediateResponseListener) {
                                l = (IntermediateResponseListener)responseAcceptor;
                            }
                            if (l == null) {
                                Debug.debug(Level.WARNING, DebugType.LDAP, LDAPMessages.WARN_INTERMEDIATE_RESPONSE_WITH_NO_LISTENER.get(String.valueOf(ir)));
                            }
                            else {
                                try {
                                    l.intermediateResponseReturned(ir);
                                }
                                catch (final Exception e2) {
                                    Debug.debugException(e2);
                                }
                            }
                            continue;
                        }
                        responseAcceptor = this.acceptorMap.remove(response.getMessageID());
                    }
                    if (responseAcceptor != null) {
                        try {
                            responseAcceptor.responseReceived(response);
                        }
                        catch (final LDAPException le2) {
                            Debug.debugException(le2);
                            Debug.debug(Level.WARNING, DebugType.LDAP, LDAPMessages.ERR_READER_ACCEPTOR_ERROR.get(String.valueOf(response), this.connection.getHostPort(), StaticUtils.getExceptionMessage(le2)), le2);
                        }
                        continue;
                    }
                    if (response instanceof ExtendedResult && response.getMessageID() == 0) {
                        ExtendedResult extendedResult = (ExtendedResult)response;
                        final String oid = extendedResult.getOID();
                        if ("1.3.6.1.4.1.1466.20036".equals(oid)) {
                            extendedResult = new NoticeOfDisconnectionExtendedResult(extendedResult);
                            this.connection.setDisconnectInfo(DisconnectType.SERVER_CLOSED_WITH_NOTICE, extendedResult.getDiagnosticMessage(), null);
                        }
                        else if ("1.3.6.1.4.1.30221.2.6.5".equals(oid)) {
                            extendedResult = new InteractiveTransactionAbortedExtendedResult(extendedResult);
                        }
                        final UnsolicitedNotificationHandler handler = this.connection.getConnectionOptions().getUnsolicitedNotificationHandler();
                        if (handler == null) {
                            if (!Debug.debugEnabled(DebugType.LDAP)) {
                                continue;
                            }
                            Debug.debug(Level.WARNING, DebugType.LDAP, LDAPMessages.WARN_READER_UNHANDLED_UNSOLICITED_NOTIFICATION.get(response));
                        }
                        else {
                            handler.handleUnsolicitedNotification(this.connection, extendedResult);
                        }
                        continue;
                    }
                    if (!Debug.debugEnabled(DebugType.LDAP)) {
                        continue;
                    }
                    Debug.debug(Level.WARNING, DebugType.LDAP, LDAPMessages.WARN_READER_NO_ACCEPTOR.get(response));
                    continue;
                }
            }
            catch (final Exception e3) {
                Debug.debugException(e3);
                Level debugLevel2 = Level.SEVERE;
                String message2;
                if (e3 instanceof IOException) {
                    this.connection.setDisconnectInfo(DisconnectType.IO_ERROR, null, e3);
                    message2 = LDAPMessages.ERR_READER_CLOSING_DUE_TO_IO_EXCEPTION.get(this.connection.getHostPort(), StaticUtils.getExceptionMessage(e3));
                    debugLevel2 = Level.WARNING;
                }
                else if (e3 instanceof ASN1Exception) {
                    this.connection.setDisconnectInfo(DisconnectType.DECODE_ERROR, null, e3);
                    message2 = LDAPMessages.ERR_READER_CLOSING_DUE_TO_ASN1_EXCEPTION.get(this.connection.getHostPort(), StaticUtils.getExceptionMessage(e3));
                }
                else {
                    this.connection.setDisconnectInfo(DisconnectType.LOCAL_ERROR, null, e3);
                    message2 = LDAPMessages.ERR_READER_CLOSING_DUE_TO_EXCEPTION.get(this.connection.getHostPort(), StaticUtils.getExceptionMessage(e3));
                }
                Debug.debug(debugLevel2, DebugType.LDAP, message2, e3);
                final boolean autoReconnect3 = this.connection.getConnectionOptions().autoReconnect();
                if (!autoReconnect3) {
                    this.closeRequested = true;
                    if (this.thread != null) {
                        this.thread.setName(this.thread.getName() + " (closed)");
                        this.thread = null;
                    }
                    this.closeInternal(true, message2);
                    return;
                }
                reconnect = true;
            }
            break;
        }
        if (this.thread != null) {
            this.thread.setName(this.constructThreadName(null));
            this.thread = null;
        }
        if (reconnect && !this.connection.closeRequested()) {
            try {
                this.connection.setNeedsReconnect();
            }
            catch (final Exception e3) {
                Debug.debugException(e3);
            }
        }
        else {
            this.closeInternal(true, null);
        }
    }
    
    LDAPResponse readResponse(final int messageID) throws LDAPException {
        try {
            while (true) {
                final LDAPResponse response = LDAPMessage.readLDAPResponseFrom(this.asn1StreamReader, false, this.connection.getCachedSchema());
                if (response == null) {
                    return new ConnectionClosedResponse(ResultCode.SERVER_DOWN, null);
                }
                this.connection.setLastCommunicationTime();
                if (response.getMessageID() == messageID) {
                    return response;
                }
                if (response instanceof ExtendedResult && response.getMessageID() == 0) {
                    ExtendedResult extendedResult = (ExtendedResult)response;
                    final String oid = extendedResult.getOID();
                    if ("1.3.6.1.4.1.1466.20036".equals(oid)) {
                        extendedResult = new NoticeOfDisconnectionExtendedResult(extendedResult);
                        this.connection.setDisconnectInfo(DisconnectType.SERVER_CLOSED_WITH_NOTICE, extendedResult.getDiagnosticMessage(), null);
                    }
                    else if ("1.3.6.1.4.1.30221.2.6.5".equals(oid)) {
                        extendedResult = new InteractiveTransactionAbortedExtendedResult(extendedResult);
                    }
                    final UnsolicitedNotificationHandler handler = this.connection.getConnectionOptions().getUnsolicitedNotificationHandler();
                    if (handler == null) {
                        if (!Debug.debugEnabled(DebugType.LDAP)) {
                            continue;
                        }
                        Debug.debug(Level.WARNING, DebugType.LDAP, LDAPMessages.WARN_READER_UNHANDLED_UNSOLICITED_NOTIFICATION.get(response));
                    }
                    else {
                        handler.handleUnsolicitedNotification(this.connection, extendedResult);
                    }
                }
                else {
                    if (!Debug.debugEnabled(DebugType.LDAP)) {
                        continue;
                    }
                    Debug.debug(Level.WARNING, DebugType.LDAP, LDAPMessages.WARN_READER_DISCARDING_UNEXPECTED_RESPONSE.get(response, messageID));
                }
            }
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            final Throwable t = le.getCause();
            if (t != null && t instanceof SocketTimeoutException) {
                throw new LDAPException(ResultCode.TIMEOUT, le.getMessage(), le);
            }
            Level debugLevel = Level.SEVERE;
            String message;
            if (t == null) {
                this.connection.setDisconnectInfo(DisconnectType.DECODE_ERROR, le.getMessage(), t);
                message = le.getMessage();
                debugLevel = Level.WARNING;
            }
            else if (t instanceof IOException) {
                this.connection.setDisconnectInfo(DisconnectType.IO_ERROR, le.getMessage(), t);
                message = LDAPMessages.ERR_READER_CLOSING_DUE_TO_IO_EXCEPTION.get(this.connection.getHostPort(), StaticUtils.getExceptionMessage(t));
                debugLevel = Level.WARNING;
            }
            else if (t instanceof ASN1Exception) {
                this.connection.setDisconnectInfo(DisconnectType.DECODE_ERROR, le.getMessage(), t);
                message = LDAPMessages.ERR_READER_CLOSING_DUE_TO_ASN1_EXCEPTION.get(this.connection.getHostPort(), StaticUtils.getExceptionMessage(t));
            }
            else {
                this.connection.setDisconnectInfo(DisconnectType.LOCAL_ERROR, le.getMessage(), t);
                message = LDAPMessages.ERR_READER_CLOSING_DUE_TO_EXCEPTION.get(this.connection.getHostPort(), StaticUtils.getExceptionMessage(t));
            }
            Debug.debug(debugLevel, DebugType.LDAP, message, t);
            final boolean autoReconnect = this.connection.getConnectionOptions().autoReconnect();
            if (!autoReconnect) {
                this.closeRequested = true;
            }
            this.closeInternal(true, message);
            throw le;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            Level debugLevel2 = Level.SEVERE;
            String message2;
            if (e instanceof IOException) {
                this.connection.setDisconnectInfo(DisconnectType.IO_ERROR, null, e);
                message2 = LDAPMessages.ERR_READER_CLOSING_DUE_TO_IO_EXCEPTION.get(this.connection.getHostPort(), StaticUtils.getExceptionMessage(e));
                debugLevel2 = Level.WARNING;
            }
            else if (e instanceof ASN1Exception) {
                this.connection.setDisconnectInfo(DisconnectType.DECODE_ERROR, null, e);
                message2 = LDAPMessages.ERR_READER_CLOSING_DUE_TO_ASN1_EXCEPTION.get(this.connection.getHostPort(), StaticUtils.getExceptionMessage(e));
            }
            else {
                this.connection.setDisconnectInfo(DisconnectType.LOCAL_ERROR, null, e);
                message2 = LDAPMessages.ERR_READER_CLOSING_DUE_TO_EXCEPTION.get(this.connection.getHostPort(), StaticUtils.getExceptionMessage(e));
            }
            Debug.debug(debugLevel2, DebugType.LDAP, message2, e);
            final boolean autoReconnect2 = this.connection.getConnectionOptions().autoReconnect();
            if (!autoReconnect2) {
                this.closeRequested = true;
            }
            this.closeInternal(true, message2);
            throw new LDAPException(ResultCode.SERVER_DOWN, message2, e);
        }
    }
    
    OutputStream doStartTLS(final SSLSocketFactory sslSocketFactory) throws LDAPException {
        final LDAPConnectionOptions connectionOptions = this.connection.getConnectionOptions();
        if (this.connection.synchronousMode()) {
            try {
                final int connectTimeout = connectionOptions.getConnectTimeoutMillis();
                if (connectTimeout > 0) {
                    InternalSDKHelper.setSoTimeout(this.connection, connectTimeout);
                }
                else {
                    InternalSDKHelper.setSoTimeout(this.connection, 0);
                }
                final SSLSocket sslSocket;
                synchronized (sslSocketFactory) {
                    sslSocket = (SSLSocket)sslSocketFactory.createSocket(this.socket, this.connection.getConnectedAddress(), this.socket.getPort(), true);
                    sslSocket.startHandshake();
                }
                connectionOptions.getSSLSocketVerifier().verifySSLSocket(this.connection.getConnectedAddress(), this.socket.getPort(), sslSocket);
                this.inputStream = new BufferedInputStream(sslSocket.getInputStream(), 4096);
                this.asn1StreamReader = new ASN1StreamReader(this.inputStream, connectionOptions.getMaxMessageSize());
                this.startTLSOutputStream = sslSocket.getOutputStream();
                this.socket = sslSocket;
                this.connection.getConnectionInternals(true).setSocket(sslSocket);
                final OutputStream outputStream = this.startTLSOutputStream;
                this.startTLSOutputStream = null;
                return outputStream;
            }
            catch (final Exception e) {
                Debug.debugException(e);
                this.connection.setDisconnectInfo(DisconnectType.SECURITY_PROBLEM, StaticUtils.getExceptionMessage(e), e);
                this.startTLSException = e;
                this.closeInternal(this.closeRequested = true, StaticUtils.getExceptionMessage(e));
                throw new LDAPException(ResultCode.SERVER_DOWN, LDAPMessages.ERR_CONNREADER_STARTTLS_FAILED.get(StaticUtils.getExceptionMessage(e)), e);
            }
        }
        this.sslSocketFactory = sslSocketFactory;
        final int originalSOTimeout = InternalSDKHelper.getSoTimeout(this.connection);
        try {
            InternalSDKHelper.setSoTimeout(this.connection, 50);
            while (this.startTLSOutputStream == null) {
                if (this.thread == null) {
                    if (this.startTLSException == null) {
                        throw new LDAPException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_CONNREADER_STARTTLS_FAILED_NO_EXCEPTION.get());
                    }
                    final Exception e2 = this.startTLSException;
                    this.startTLSException = null;
                    throw new LDAPException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_CONNREADER_STARTTLS_FAILED.get(StaticUtils.getExceptionMessage(e2)), e2);
                }
                else {
                    this.startTLSSleeper.sleep(10L);
                }
            }
            final OutputStream outputStream2 = this.startTLSOutputStream;
            this.startTLSOutputStream = null;
            return outputStream2;
        }
        finally {
            InternalSDKHelper.setSoTimeout(this.connection, originalSOTimeout);
        }
    }
    
    void applySASLQoP(final SaslClient saslClient) {
        InternalASN1Helper.setSASLClient(this.asn1StreamReader, saslClient);
    }
    
    void close(final boolean notifyConnection) {
        this.closeRequested = true;
        for (int i = 0; i < 5; ++i) {
            try {
                final Thread t = this.thread;
                if (t == null || t == Thread.currentThread() || !t.isAlive()) {
                    break;
                }
                t.interrupt();
                t.join(100L);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                if (e instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        this.closeInternal(notifyConnection, null);
    }
    
    private void closeInternal(final boolean notifyConnection, final String message) {
        final InputStream is = this.inputStream;
        this.inputStream = null;
        try {
            if (is != null) {
                is.close();
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
        if (notifyConnection) {
            this.connection.setClosed();
        }
        final Iterator<Integer> iterator = this.acceptorMap.keySet().iterator();
        while (iterator.hasNext()) {
            final int messageID = iterator.next();
            final ResponseAcceptor acceptor = this.acceptorMap.get(messageID);
            try {
                if (message == null) {
                    final DisconnectType disconnectType = this.connection.getDisconnectType();
                    if (disconnectType == null) {
                        acceptor.responseReceived(new ConnectionClosedResponse(ResultCode.SERVER_DOWN, null));
                    }
                    else {
                        acceptor.responseReceived(new ConnectionClosedResponse(disconnectType.getResultCode(), this.connection.getDisconnectMessage()));
                    }
                }
                else {
                    acceptor.responseReceived(new ConnectionClosedResponse(ResultCode.SERVER_DOWN, message));
                }
            }
            catch (final Exception e2) {
                Debug.debugException(e2);
            }
            iterator.remove();
        }
    }
    
    Thread getReaderThread() {
        return this.thread;
    }
    
    void updateThreadName() {
        final Thread t = this.thread;
        if (t != null) {
            try {
                t.setName(this.constructThreadName(this.connection.getConnectionInternals(true)));
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
        }
    }
    
    private String constructThreadName(final LDAPConnectionInternals connectionInternals) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("Connection reader for connection ");
        buffer.append(this.connection.getConnectionID());
        buffer.append(' ');
        String name = this.connection.getConnectionName();
        if (name != null) {
            buffer.append('\'');
            buffer.append(name);
            buffer.append("' ");
        }
        name = this.connection.getConnectionPoolName();
        if (name != null) {
            buffer.append("in pool '");
            buffer.append(name);
            buffer.append("' ");
        }
        if (connectionInternals == null) {
            buffer.append("(not connected)");
        }
        else {
            buffer.append("to ");
            buffer.append(connectionInternals.getHost());
            buffer.append(':');
            buffer.append(connectionInternals.getPort());
        }
        return buffer.toString();
    }
}
