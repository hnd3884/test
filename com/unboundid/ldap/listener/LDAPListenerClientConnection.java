package com.unboundid.ldap.listener;

import javax.net.ssl.SSLSocket;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import javax.net.ssl.SSLSocketFactory;
import com.unboundid.ldap.protocol.IntermediateResponseProtocolOp;
import com.unboundid.ldap.protocol.SearchResultReferenceProtocolOp;
import java.util.Collection;
import com.unboundid.ldap.sdk.Attribute;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.util.ObjectPair;
import java.util.Iterator;
import com.unboundid.ldap.protocol.SearchResultEntryProtocolOp;
import com.unboundid.ldap.sdk.LDAPRuntimeException;
import com.unboundid.util.InternalUseOnly;
import com.unboundid.ldap.protocol.SearchResultDoneProtocolOp;
import com.unboundid.ldap.protocol.ModifyDNResponseProtocolOp;
import com.unboundid.ldap.protocol.ModifyResponseProtocolOp;
import com.unboundid.ldap.protocol.ExtendedResponseProtocolOp;
import com.unboundid.ldap.protocol.DeleteResponseProtocolOp;
import com.unboundid.ldap.protocol.CompareResponseProtocolOp;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.protocol.BindResponseProtocolOp;
import com.unboundid.ldap.protocol.ProtocolOp;
import java.util.List;
import com.unboundid.ldap.protocol.AddResponseProtocolOp;
import com.unboundid.ldap.protocol.LDAPMessage;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.extensions.NoticeOfDisconnectionExtendedResult;
import java.io.IOException;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import com.unboundid.util.Validator;
import java.net.Socket;
import java.io.OutputStream;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import com.unboundid.asn1.ASN1StreamReader;
import com.unboundid.asn1.ASN1Buffer;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import java.io.Closeable;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class LDAPListenerClientConnection extends Thread implements Closeable
{
    private static final Control[] EMPTY_CONTROL_ARRAY;
    private final ASN1Buffer asn1Buffer;
    private volatile ASN1StreamReader asn1Reader;
    private final AtomicBoolean suppressNextResponse;
    private final CopyOnWriteArrayList<IntermediateResponseTransformer> intermediateResponseTransformers;
    private final CopyOnWriteArrayList<SearchEntryTransformer> searchEntryTransformers;
    private final CopyOnWriteArrayList<SearchReferenceTransformer> searchReferenceTransformers;
    private final LDAPListener listener;
    private final LDAPListenerExceptionHandler exceptionHandler;
    private final LDAPListenerRequestHandler requestHandler;
    private final long connectionID;
    private volatile OutputStream outputStream;
    private volatile Socket socket;
    
    public LDAPListenerClientConnection(final LDAPListener listener, final Socket socket, final LDAPListenerRequestHandler requestHandler, final LDAPListenerExceptionHandler exceptionHandler) throws LDAPException {
        Validator.ensureNotNull(socket, requestHandler);
        this.setName("LDAPListener client connection reader for connection from " + socket.getInetAddress().getHostAddress() + ':' + socket.getPort() + " to " + socket.getLocalAddress().getHostAddress() + ':' + socket.getLocalPort());
        this.listener = listener;
        this.socket = socket;
        this.exceptionHandler = exceptionHandler;
        this.asn1Buffer = new ASN1Buffer();
        this.suppressNextResponse = new AtomicBoolean(false);
        this.intermediateResponseTransformers = new CopyOnWriteArrayList<IntermediateResponseTransformer>();
        this.searchEntryTransformers = new CopyOnWriteArrayList<SearchEntryTransformer>();
        this.searchReferenceTransformers = new CopyOnWriteArrayList<SearchReferenceTransformer>();
        if (listener == null) {
            this.connectionID = -1L;
        }
        else {
            this.connectionID = listener.nextConnectionID();
        }
        try {
            LDAPListenerConfig config;
            if (listener == null) {
                config = new LDAPListenerConfig(0, requestHandler);
            }
            else {
                config = listener.getConfig();
            }
            socket.setKeepAlive(config.useKeepAlive());
            socket.setReuseAddress(config.useReuseAddress());
            socket.setSoLinger(config.useLinger(), config.getLingerTimeoutSeconds());
            socket.setTcpNoDelay(config.useTCPNoDelay());
            final int sendBufferSize = config.getSendBufferSize();
            if (sendBufferSize > 0) {
                socket.setSendBufferSize(sendBufferSize);
            }
            this.asn1Reader = new ASN1StreamReader(socket.getInputStream());
        }
        catch (final IOException ioe) {
            Debug.debugException(ioe);
            try {
                socket.close();
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
            throw new LDAPException(ResultCode.CONNECT_ERROR, ListenerMessages.ERR_CONN_CREATE_IO_EXCEPTION.get(StaticUtils.getExceptionMessage(ioe)), ioe);
        }
        try {
            this.outputStream = socket.getOutputStream();
        }
        catch (final IOException ioe) {
            Debug.debugException(ioe);
            try {
                this.asn1Reader.close();
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
            try {
                socket.close();
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
            throw new LDAPException(ResultCode.CONNECT_ERROR, ListenerMessages.ERR_CONN_CREATE_IO_EXCEPTION.get(StaticUtils.getExceptionMessage(ioe)), ioe);
        }
        try {
            this.requestHandler = requestHandler.newInstance(this);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            try {
                this.asn1Reader.close();
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
            try {
                socket.close();
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
            throw le;
        }
    }
    
    @Override
    public synchronized void close() throws IOException {
        try {
            this.requestHandler.closeInstance();
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
        try {
            this.asn1Reader.close();
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
        this.socket.close();
    }
    
    void close(final LDAPException le) {
        if (this.exceptionHandler == null) {
            Debug.debugException(le);
        }
        else {
            try {
                this.exceptionHandler.connectionTerminated(this, le);
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
        }
        try {
            this.sendUnsolicitedNotification(new NoticeOfDisconnectionExtendedResult(le));
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
        try {
            this.close();
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
    }
    
    @InternalUseOnly
    @Override
    public void run() {
        try {
            while (true) {
                LDAPMessage requestMessage;
                try {
                    requestMessage = LDAPMessage.readFrom(this.asn1Reader, false);
                    if (requestMessage == null) {
                        try {
                            this.close();
                        }
                        catch (final IOException ioe) {
                            Debug.debugException(ioe);
                        }
                        return;
                    }
                }
                catch (final LDAPException le) {
                    Debug.debugException(le);
                    this.close(le);
                    return;
                }
                try {
                    final int messageID = requestMessage.getMessageID();
                    final List<Control> controls = requestMessage.getControls();
                    LDAPMessage responseMessage = null;
                    switch (requestMessage.getProtocolOpType()) {
                        case 80: {
                            this.requestHandler.processAbandonRequest(messageID, requestMessage.getAbandonRequestProtocolOp(), controls);
                            responseMessage = null;
                            break;
                        }
                        case 104: {
                            try {
                                responseMessage = this.requestHandler.processAddRequest(messageID, requestMessage.getAddRequestProtocolOp(), controls);
                            }
                            catch (final Exception e) {
                                Debug.debugException(e);
                                responseMessage = new LDAPMessage(messageID, new AddResponseProtocolOp(80, null, ListenerMessages.ERR_CONN_REQUEST_HANDLER_FAILURE.get(StaticUtils.getExceptionMessage(e)), null), new Control[0]);
                            }
                            break;
                        }
                        case 96: {
                            try {
                                responseMessage = this.requestHandler.processBindRequest(messageID, requestMessage.getBindRequestProtocolOp(), controls);
                            }
                            catch (final Exception e) {
                                Debug.debugException(e);
                                responseMessage = new LDAPMessage(messageID, new BindResponseProtocolOp(80, null, ListenerMessages.ERR_CONN_REQUEST_HANDLER_FAILURE.get(StaticUtils.getExceptionMessage(e)), null, null), new Control[0]);
                            }
                            break;
                        }
                        case 110: {
                            try {
                                responseMessage = this.requestHandler.processCompareRequest(messageID, requestMessage.getCompareRequestProtocolOp(), controls);
                            }
                            catch (final Exception e) {
                                Debug.debugException(e);
                                responseMessage = new LDAPMessage(messageID, new CompareResponseProtocolOp(80, null, ListenerMessages.ERR_CONN_REQUEST_HANDLER_FAILURE.get(StaticUtils.getExceptionMessage(e)), null), new Control[0]);
                            }
                            break;
                        }
                        case 74: {
                            try {
                                responseMessage = this.requestHandler.processDeleteRequest(messageID, requestMessage.getDeleteRequestProtocolOp(), controls);
                            }
                            catch (final Exception e) {
                                Debug.debugException(e);
                                responseMessage = new LDAPMessage(messageID, new DeleteResponseProtocolOp(80, null, ListenerMessages.ERR_CONN_REQUEST_HANDLER_FAILURE.get(StaticUtils.getExceptionMessage(e)), null), new Control[0]);
                            }
                            break;
                        }
                        case 119: {
                            try {
                                responseMessage = this.requestHandler.processExtendedRequest(messageID, requestMessage.getExtendedRequestProtocolOp(), controls);
                            }
                            catch (final Exception e) {
                                Debug.debugException(e);
                                responseMessage = new LDAPMessage(messageID, new ExtendedResponseProtocolOp(80, null, ListenerMessages.ERR_CONN_REQUEST_HANDLER_FAILURE.get(StaticUtils.getExceptionMessage(e)), null, null, null), new Control[0]);
                            }
                            break;
                        }
                        case 102: {
                            try {
                                responseMessage = this.requestHandler.processModifyRequest(messageID, requestMessage.getModifyRequestProtocolOp(), controls);
                            }
                            catch (final Exception e) {
                                Debug.debugException(e);
                                responseMessage = new LDAPMessage(messageID, new ModifyResponseProtocolOp(80, null, ListenerMessages.ERR_CONN_REQUEST_HANDLER_FAILURE.get(StaticUtils.getExceptionMessage(e)), null), new Control[0]);
                            }
                            break;
                        }
                        case 108: {
                            try {
                                responseMessage = this.requestHandler.processModifyDNRequest(messageID, requestMessage.getModifyDNRequestProtocolOp(), controls);
                            }
                            catch (final Exception e) {
                                Debug.debugException(e);
                                responseMessage = new LDAPMessage(messageID, new ModifyDNResponseProtocolOp(80, null, ListenerMessages.ERR_CONN_REQUEST_HANDLER_FAILURE.get(StaticUtils.getExceptionMessage(e)), null), new Control[0]);
                            }
                            break;
                        }
                        case 99: {
                            try {
                                responseMessage = this.requestHandler.processSearchRequest(messageID, requestMessage.getSearchRequestProtocolOp(), controls);
                            }
                            catch (final Exception e) {
                                Debug.debugException(e);
                                responseMessage = new LDAPMessage(messageID, new SearchResultDoneProtocolOp(80, null, ListenerMessages.ERR_CONN_REQUEST_HANDLER_FAILURE.get(StaticUtils.getExceptionMessage(e)), null), new Control[0]);
                            }
                            break;
                        }
                        case 66: {
                            this.requestHandler.processUnbindRequest(messageID, requestMessage.getUnbindRequestProtocolOp(), controls);
                            this.close();
                            return;
                        }
                        default: {
                            this.close(new LDAPException(ResultCode.PROTOCOL_ERROR, ListenerMessages.ERR_CONN_INVALID_PROTOCOL_OP_TYPE.get(StaticUtils.toHex(requestMessage.getProtocolOpType()))));
                            return;
                        }
                    }
                    if (responseMessage == null) {
                        continue;
                    }
                    try {
                        this.sendMessage(responseMessage);
                    }
                    catch (final LDAPException le2) {
                        Debug.debugException(le2);
                        this.close(le2);
                    }
                }
                catch (final Throwable t) {
                    this.close(new LDAPException(ResultCode.LOCAL_ERROR, ListenerMessages.ERR_CONN_EXCEPTION_IN_REQUEST_HANDLER.get(String.valueOf(requestMessage), StaticUtils.getExceptionMessage(t))));
                    StaticUtils.throwErrorOrRuntimeException(t);
                }
            }
        }
        finally {
            if (this.listener != null) {
                this.listener.connectionClosed(this);
            }
        }
    }
    
    private synchronized void sendMessage(final LDAPMessage message) throws LDAPException {
        if (this.suppressNextResponse.compareAndSet(true, false)) {
            return;
        }
        this.asn1Buffer.clear();
        try {
            message.writeTo(this.asn1Buffer);
        }
        catch (final LDAPRuntimeException lre) {
            Debug.debugException(lre);
            lre.throwLDAPException();
        }
        try {
            this.asn1Buffer.writeTo(this.outputStream);
        }
        catch (final IOException ioe) {
            Debug.debugException(ioe);
            throw new LDAPException(ResultCode.LOCAL_ERROR, ListenerMessages.ERR_CONN_SEND_MESSAGE_EXCEPTION.get(StaticUtils.getExceptionMessage(ioe)), ioe);
        }
        finally {
            if (this.asn1Buffer.zeroBufferOnClear()) {
                this.asn1Buffer.clear();
            }
        }
    }
    
    public void sendSearchResultEntry(final int messageID, final SearchResultEntryProtocolOp protocolOp, final Control... controls) throws LDAPException {
        if (this.searchEntryTransformers.isEmpty()) {
            this.sendMessage(new LDAPMessage(messageID, protocolOp, controls));
        }
        else {
            SearchResultEntryProtocolOp op = protocolOp;
            Control[] c;
            if (controls == null) {
                c = LDAPListenerClientConnection.EMPTY_CONTROL_ARRAY;
            }
            else {
                c = controls;
            }
            for (final SearchEntryTransformer t : this.searchEntryTransformers) {
                try {
                    final ObjectPair<SearchResultEntryProtocolOp, Control[]> p = t.transformEntry(messageID, op, c);
                    if (p == null) {
                        return;
                    }
                    op = p.getFirst();
                    c = p.getSecond();
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    this.sendMessage(new LDAPMessage(messageID, protocolOp, c));
                    throw new LDAPException(ResultCode.LOCAL_ERROR, ListenerMessages.ERR_CONN_SEARCH_ENTRY_TRANSFORMER_EXCEPTION.get(t.getClass().getName(), String.valueOf(op), StaticUtils.getExceptionMessage(e)), e);
                }
            }
            this.sendMessage(new LDAPMessage(messageID, op, c));
        }
    }
    
    public void sendSearchResultEntry(final int messageID, final Entry entry, final Control... controls) throws LDAPException {
        this.sendSearchResultEntry(messageID, new SearchResultEntryProtocolOp(entry.getDN(), new ArrayList<Attribute>(entry.getAttributes())), controls);
    }
    
    public void sendSearchResultReference(final int messageID, final SearchResultReferenceProtocolOp protocolOp, final Control... controls) throws LDAPException {
        if (this.searchReferenceTransformers.isEmpty()) {
            this.sendMessage(new LDAPMessage(messageID, protocolOp, controls));
        }
        else {
            SearchResultReferenceProtocolOp op = protocolOp;
            Control[] c;
            if (controls == null) {
                c = LDAPListenerClientConnection.EMPTY_CONTROL_ARRAY;
            }
            else {
                c = controls;
            }
            for (final SearchReferenceTransformer t : this.searchReferenceTransformers) {
                try {
                    final ObjectPair<SearchResultReferenceProtocolOp, Control[]> p = t.transformReference(messageID, op, c);
                    if (p == null) {
                        return;
                    }
                    op = p.getFirst();
                    c = p.getSecond();
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    this.sendMessage(new LDAPMessage(messageID, protocolOp, c));
                    throw new LDAPException(ResultCode.LOCAL_ERROR, ListenerMessages.ERR_CONN_SEARCH_REFERENCE_TRANSFORMER_EXCEPTION.get(t.getClass().getName(), String.valueOf(op), StaticUtils.getExceptionMessage(e)), e);
                }
            }
            this.sendMessage(new LDAPMessage(messageID, op, c));
        }
    }
    
    public void sendIntermediateResponse(final int messageID, final IntermediateResponseProtocolOp protocolOp, final Control... controls) throws LDAPException {
        if (this.intermediateResponseTransformers.isEmpty()) {
            this.sendMessage(new LDAPMessage(messageID, protocolOp, controls));
        }
        else {
            IntermediateResponseProtocolOp op = protocolOp;
            Control[] c;
            if (controls == null) {
                c = LDAPListenerClientConnection.EMPTY_CONTROL_ARRAY;
            }
            else {
                c = controls;
            }
            for (final IntermediateResponseTransformer t : this.intermediateResponseTransformers) {
                try {
                    final ObjectPair<IntermediateResponseProtocolOp, Control[]> p = t.transformIntermediateResponse(messageID, op, c);
                    if (p == null) {
                        return;
                    }
                    op = p.getFirst();
                    c = p.getSecond();
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    this.sendMessage(new LDAPMessage(messageID, protocolOp, c));
                    throw new LDAPException(ResultCode.LOCAL_ERROR, ListenerMessages.ERR_CONN_INTERMEDIATE_RESPONSE_TRANSFORMER_EXCEPTION.get(t.getClass().getName(), String.valueOf(op), StaticUtils.getExceptionMessage(e)), e);
                }
            }
            this.sendMessage(new LDAPMessage(messageID, op, c));
        }
    }
    
    public void sendUnsolicitedNotification(final ExtendedResult result) throws LDAPException {
        this.sendUnsolicitedNotification(new ExtendedResponseProtocolOp(result.getResultCode().intValue(), result.getMatchedDN(), result.getDiagnosticMessage(), StaticUtils.toList(result.getReferralURLs()), result.getOID(), result.getValue()), result.getResponseControls());
    }
    
    public void sendUnsolicitedNotification(final ExtendedResponseProtocolOp extendedResponse, final Control... controls) throws LDAPException {
        this.sendMessage(new LDAPMessage(0, extendedResponse, controls));
    }
    
    public synchronized Socket getSocket() {
        return this.socket;
    }
    
    public synchronized OutputStream convertToTLS(final SSLSocketFactory f) throws LDAPException {
        final OutputStream clearOutputStream = this.outputStream;
        final Socket origSocket = this.socket;
        final String hostname = LDAPConnectionOptions.DEFAULT_NAME_RESOLVER.getHostName(origSocket.getInetAddress());
        final int port = origSocket.getPort();
        try {
            synchronized (f) {
                this.socket = f.createSocket(this.socket, hostname, port, true);
            }
            ((SSLSocket)this.socket).setUseClientMode(false);
            this.outputStream = this.socket.getOutputStream();
            this.asn1Reader = new ASN1StreamReader(this.socket.getInputStream());
            this.suppressNextResponse.set(true);
            return clearOutputStream;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            final LDAPException le = new LDAPException(ResultCode.LOCAL_ERROR, ListenerMessages.ERR_CONN_CONVERT_TO_TLS_FAILURE.get(StaticUtils.getExceptionMessage(e)), e);
            this.close(le);
            throw le;
        }
    }
    
    public long getConnectionID() {
        return this.connectionID;
    }
    
    public void addSearchEntryTransformer(final SearchEntryTransformer t) {
        this.searchEntryTransformers.add(t);
    }
    
    public void removeSearchEntryTransformer(final SearchEntryTransformer t) {
        this.searchEntryTransformers.remove(t);
    }
    
    public void addSearchReferenceTransformer(final SearchReferenceTransformer t) {
        this.searchReferenceTransformers.add(t);
    }
    
    public void removeSearchReferenceTransformer(final SearchReferenceTransformer t) {
        this.searchReferenceTransformers.remove(t);
    }
    
    public void addIntermediateResponseTransformer(final IntermediateResponseTransformer t) {
        this.intermediateResponseTransformers.add(t);
    }
    
    public void removeIntermediateResponseTransformer(final IntermediateResponseTransformer t) {
        this.intermediateResponseTransformers.remove(t);
    }
    
    static {
        EMPTY_CONTROL_ARRAY = new Control[0];
    }
}
