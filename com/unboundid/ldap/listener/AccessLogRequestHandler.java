package com.unboundid.ldap.listener;

import com.unboundid.util.ObjectPair;
import com.unboundid.ldap.protocol.SearchResultEntryProtocolOp;
import java.util.Date;
import com.unboundid.ldap.protocol.UnbindRequestProtocolOp;
import com.unboundid.ldap.protocol.SearchResultDoneProtocolOp;
import java.util.Iterator;
import com.unboundid.ldap.protocol.SearchRequestProtocolOp;
import com.unboundid.ldap.protocol.ModifyDNResponseProtocolOp;
import com.unboundid.ldap.protocol.ModifyDNRequestProtocolOp;
import com.unboundid.ldap.protocol.ModifyResponseProtocolOp;
import com.unboundid.ldap.protocol.ModifyRequestProtocolOp;
import com.unboundid.ldap.protocol.ExtendedResponseProtocolOp;
import com.unboundid.ldap.protocol.ExtendedRequestProtocolOp;
import com.unboundid.ldap.protocol.DeleteResponseProtocolOp;
import com.unboundid.ldap.protocol.DeleteRequestProtocolOp;
import com.unboundid.ldap.protocol.CompareResponseProtocolOp;
import com.unboundid.ldap.protocol.CompareRequestProtocolOp;
import com.unboundid.ldap.protocol.BindResponseProtocolOp;
import com.unboundid.ldap.protocol.BindRequestProtocolOp;
import com.unboundid.ldap.protocol.AddResponseProtocolOp;
import com.unboundid.ldap.protocol.LDAPMessage;
import com.unboundid.ldap.protocol.AddRequestProtocolOp;
import com.unboundid.ldap.sdk.Control;
import java.util.List;
import com.unboundid.ldap.protocol.AbandonRequestProtocolOp;
import com.unboundid.ldap.sdk.LDAPException;
import java.net.Socket;
import java.util.logging.LogRecord;
import java.util.logging.Level;
import com.unboundid.util.Validator;
import com.unboundid.util.StaticUtils;
import java.util.logging.Handler;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class AccessLogRequestHandler extends LDAPListenerRequestHandler implements SearchEntryTransformer
{
    private static final ThreadLocal<DecimalFormat> DECIMAL_FORMATTERS;
    private static final ThreadLocal<SimpleDateFormat> DATE_FORMATTERS;
    private static final ThreadLocal<StringBuilder> BUFFERS;
    private final AtomicLong nextOperationID;
    private final ConcurrentHashMap<Integer, AtomicLong> entryCounts;
    private final Handler logHandler;
    private final LDAPListenerClientConnection clientConnection;
    private final LDAPListenerRequestHandler requestHandler;
    
    public AccessLogRequestHandler(final Handler logHandler, final LDAPListenerRequestHandler requestHandler) {
        this.entryCounts = new ConcurrentHashMap<Integer, AtomicLong>(StaticUtils.computeMapCapacity(50));
        Validator.ensureNotNull(logHandler, requestHandler);
        this.logHandler = logHandler;
        this.requestHandler = requestHandler;
        this.nextOperationID = null;
        this.clientConnection = null;
    }
    
    private AccessLogRequestHandler(final Handler logHandler, final LDAPListenerRequestHandler requestHandler, final LDAPListenerClientConnection clientConnection) {
        this.entryCounts = new ConcurrentHashMap<Integer, AtomicLong>(StaticUtils.computeMapCapacity(50));
        this.logHandler = logHandler;
        this.requestHandler = requestHandler;
        this.clientConnection = clientConnection;
        this.nextOperationID = new AtomicLong(0L);
    }
    
    @Override
    public AccessLogRequestHandler newInstance(final LDAPListenerClientConnection connection) throws LDAPException {
        final AccessLogRequestHandler h = new AccessLogRequestHandler(this.logHandler, this.requestHandler.newInstance(connection), connection);
        connection.addSearchEntryTransformer(h);
        final StringBuilder b = h.getConnectionHeader("CONNECT");
        final Socket s = connection.getSocket();
        b.append(" from=\"");
        b.append(s.getInetAddress().getHostAddress());
        b.append(':');
        b.append(s.getPort());
        b.append("\" to=\"");
        b.append(s.getLocalAddress().getHostAddress());
        b.append(':');
        b.append(s.getLocalPort());
        b.append('\"');
        this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
        this.logHandler.flush();
        return h;
    }
    
    @Override
    public void closeInstance() {
        final StringBuilder b = this.getConnectionHeader("DISCONNECT");
        this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
        this.logHandler.flush();
        this.requestHandler.closeInstance();
    }
    
    @Override
    public void processAbandonRequest(final int messageID, final AbandonRequestProtocolOp request, final List<Control> controls) {
        final StringBuilder b = this.getRequestHeader("ABANDON", this.nextOperationID.getAndIncrement(), messageID);
        b.append(" idToAbandon=");
        b.append(request.getIDToAbandon());
        this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
        this.logHandler.flush();
        this.requestHandler.processAbandonRequest(messageID, request, controls);
    }
    
    @Override
    public LDAPMessage processAddRequest(final int messageID, final AddRequestProtocolOp request, final List<Control> controls) {
        final long opID = this.nextOperationID.getAndIncrement();
        final StringBuilder b = this.getRequestHeader("ADD", opID, messageID);
        b.append(" dn=\"");
        b.append(request.getDN());
        b.append('\"');
        this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
        this.logHandler.flush();
        final long startTimeNanos = System.nanoTime();
        final LDAPMessage responseMessage = this.requestHandler.processAddRequest(messageID, request, controls);
        final long eTimeNanos = System.nanoTime() - startTimeNanos;
        final AddResponseProtocolOp protocolOp = responseMessage.getAddResponseProtocolOp();
        this.generateResponse(b, "ADD", opID, messageID, protocolOp.getResultCode(), protocolOp.getDiagnosticMessage(), protocolOp.getMatchedDN(), protocolOp.getReferralURLs(), eTimeNanos);
        this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
        this.logHandler.flush();
        return responseMessage;
    }
    
    @Override
    public LDAPMessage processBindRequest(final int messageID, final BindRequestProtocolOp request, final List<Control> controls) {
        final long opID = this.nextOperationID.getAndIncrement();
        final StringBuilder b = this.getRequestHeader("BIND", opID, messageID);
        b.append(" version=");
        b.append(request.getVersion());
        b.append(" dn=\"");
        b.append(request.getBindDN());
        b.append("\" authType=\"");
        switch (request.getCredentialsType()) {
            case Byte.MIN_VALUE: {
                b.append("SIMPLE");
                break;
            }
            case -93: {
                b.append("SASL ");
                b.append(request.getSASLMechanism());
                break;
            }
        }
        b.append('\"');
        this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
        this.logHandler.flush();
        final long startTimeNanos = System.nanoTime();
        final LDAPMessage responseMessage = this.requestHandler.processBindRequest(messageID, request, controls);
        final long eTimeNanos = System.nanoTime() - startTimeNanos;
        final BindResponseProtocolOp protocolOp = responseMessage.getBindResponseProtocolOp();
        this.generateResponse(b, "BIND", opID, messageID, protocolOp.getResultCode(), protocolOp.getDiagnosticMessage(), protocolOp.getMatchedDN(), protocolOp.getReferralURLs(), eTimeNanos);
        this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
        this.logHandler.flush();
        return responseMessage;
    }
    
    @Override
    public LDAPMessage processCompareRequest(final int messageID, final CompareRequestProtocolOp request, final List<Control> controls) {
        final long opID = this.nextOperationID.getAndIncrement();
        final StringBuilder b = this.getRequestHeader("COMPARE", opID, messageID);
        b.append(" dn=\"");
        b.append(request.getDN());
        b.append("\" attr=\"");
        b.append(request.getAttributeName());
        b.append('\"');
        this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
        this.logHandler.flush();
        final long startTimeNanos = System.nanoTime();
        final LDAPMessage responseMessage = this.requestHandler.processCompareRequest(messageID, request, controls);
        final long eTimeNanos = System.nanoTime() - startTimeNanos;
        final CompareResponseProtocolOp protocolOp = responseMessage.getCompareResponseProtocolOp();
        this.generateResponse(b, "COMPARE", opID, messageID, protocolOp.getResultCode(), protocolOp.getDiagnosticMessage(), protocolOp.getMatchedDN(), protocolOp.getReferralURLs(), eTimeNanos);
        this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
        this.logHandler.flush();
        return responseMessage;
    }
    
    @Override
    public LDAPMessage processDeleteRequest(final int messageID, final DeleteRequestProtocolOp request, final List<Control> controls) {
        final long opID = this.nextOperationID.getAndIncrement();
        final StringBuilder b = this.getRequestHeader("DELETE", opID, messageID);
        b.append(" dn=\"");
        b.append(request.getDN());
        b.append('\"');
        this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
        this.logHandler.flush();
        final long startTimeNanos = System.nanoTime();
        final LDAPMessage responseMessage = this.requestHandler.processDeleteRequest(messageID, request, controls);
        final long eTimeNanos = System.nanoTime() - startTimeNanos;
        final DeleteResponseProtocolOp protocolOp = responseMessage.getDeleteResponseProtocolOp();
        this.generateResponse(b, "DELETE", opID, messageID, protocolOp.getResultCode(), protocolOp.getDiagnosticMessage(), protocolOp.getMatchedDN(), protocolOp.getReferralURLs(), eTimeNanos);
        this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
        this.logHandler.flush();
        return responseMessage;
    }
    
    @Override
    public LDAPMessage processExtendedRequest(final int messageID, final ExtendedRequestProtocolOp request, final List<Control> controls) {
        final long opID = this.nextOperationID.getAndIncrement();
        final StringBuilder b = this.getRequestHeader("EXTENDED", opID, messageID);
        b.append(" requestOID=\"");
        b.append(request.getOID());
        b.append('\"');
        this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
        this.logHandler.flush();
        final long startTimeNanos = System.nanoTime();
        final LDAPMessage responseMessage = this.requestHandler.processExtendedRequest(messageID, request, controls);
        final long eTimeNanos = System.nanoTime() - startTimeNanos;
        final ExtendedResponseProtocolOp protocolOp = responseMessage.getExtendedResponseProtocolOp();
        this.generateResponse(b, "EXTENDED", opID, messageID, protocolOp.getResultCode(), protocolOp.getDiagnosticMessage(), protocolOp.getMatchedDN(), protocolOp.getReferralURLs(), eTimeNanos);
        final String responseOID = protocolOp.getResponseOID();
        if (responseOID != null) {
            b.append(" responseOID=\"");
            b.append(responseOID);
            b.append('\"');
        }
        this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
        this.logHandler.flush();
        return responseMessage;
    }
    
    @Override
    public LDAPMessage processModifyRequest(final int messageID, final ModifyRequestProtocolOp request, final List<Control> controls) {
        final long opID = this.nextOperationID.getAndIncrement();
        final StringBuilder b = this.getRequestHeader("MODIFY", opID, messageID);
        b.append(" dn=\"");
        b.append(request.getDN());
        b.append('\"');
        this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
        this.logHandler.flush();
        final long startTimeNanos = System.nanoTime();
        final LDAPMessage responseMessage = this.requestHandler.processModifyRequest(messageID, request, controls);
        final long eTimeNanos = System.nanoTime() - startTimeNanos;
        final ModifyResponseProtocolOp protocolOp = responseMessage.getModifyResponseProtocolOp();
        this.generateResponse(b, "MODIFY", opID, messageID, protocolOp.getResultCode(), protocolOp.getDiagnosticMessage(), protocolOp.getMatchedDN(), protocolOp.getReferralURLs(), eTimeNanos);
        this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
        this.logHandler.flush();
        return responseMessage;
    }
    
    @Override
    public LDAPMessage processModifyDNRequest(final int messageID, final ModifyDNRequestProtocolOp request, final List<Control> controls) {
        final long opID = this.nextOperationID.getAndIncrement();
        final StringBuilder b = this.getRequestHeader("MODDN", opID, messageID);
        b.append(" dn=\"");
        b.append(request.getDN());
        b.append("\" newRDN=\"");
        b.append(request.getNewRDN());
        b.append("\" deleteOldRDN=");
        b.append(request.deleteOldRDN());
        final String newSuperior = request.getNewSuperiorDN();
        if (newSuperior != null) {
            b.append(" newSuperior=\"");
            b.append(newSuperior);
            b.append('\"');
        }
        this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
        this.logHandler.flush();
        final long startTimeNanos = System.nanoTime();
        final LDAPMessage responseMessage = this.requestHandler.processModifyDNRequest(messageID, request, controls);
        final long eTimeNanos = System.nanoTime() - startTimeNanos;
        final ModifyDNResponseProtocolOp protocolOp = responseMessage.getModifyDNResponseProtocolOp();
        this.generateResponse(b, "MODDN", opID, messageID, protocolOp.getResultCode(), protocolOp.getDiagnosticMessage(), protocolOp.getMatchedDN(), protocolOp.getReferralURLs(), eTimeNanos);
        this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
        this.logHandler.flush();
        return responseMessage;
    }
    
    @Override
    public LDAPMessage processSearchRequest(final int messageID, final SearchRequestProtocolOp request, final List<Control> controls) {
        final long opID = this.nextOperationID.getAndIncrement();
        final StringBuilder b = this.getRequestHeader("SEARCH", opID, messageID);
        b.append(" base=\"");
        b.append(request.getBaseDN());
        b.append("\" scope=");
        b.append(request.getScope().intValue());
        b.append(" filter=\"");
        request.getFilter().toString(b);
        b.append("\" attrs=\"");
        final List<String> attrList = request.getAttributes();
        if (attrList.isEmpty()) {
            b.append("ALL");
        }
        else {
            final Iterator<String> iterator = attrList.iterator();
            while (iterator.hasNext()) {
                b.append(iterator.next());
                if (iterator.hasNext()) {
                    b.append(',');
                }
            }
        }
        b.append('\"');
        this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
        this.logHandler.flush();
        final AtomicLong l = new AtomicLong(0L);
        this.entryCounts.put(messageID, l);
        try {
            final long startTimeNanos = System.nanoTime();
            final LDAPMessage responseMessage = this.requestHandler.processSearchRequest(messageID, request, controls);
            final long eTimeNanos = System.nanoTime() - startTimeNanos;
            final SearchResultDoneProtocolOp protocolOp = responseMessage.getSearchResultDoneProtocolOp();
            this.generateResponse(b, "SEARCH", opID, messageID, protocolOp.getResultCode(), protocolOp.getDiagnosticMessage(), protocolOp.getMatchedDN(), protocolOp.getReferralURLs(), eTimeNanos);
            b.append(" entriesReturned=");
            b.append(l.get());
            this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
            this.logHandler.flush();
            return responseMessage;
        }
        finally {
            this.entryCounts.remove(messageID);
        }
    }
    
    @Override
    public void processUnbindRequest(final int messageID, final UnbindRequestProtocolOp request, final List<Control> controls) {
        final StringBuilder b = this.getRequestHeader("UNBIND", this.nextOperationID.getAndIncrement(), messageID);
        this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
        this.logHandler.flush();
        this.requestHandler.processUnbindRequest(messageID, request, controls);
    }
    
    private static StringBuilder getBuffer() {
        StringBuilder b = AccessLogRequestHandler.BUFFERS.get();
        if (b == null) {
            b = new StringBuilder();
            AccessLogRequestHandler.BUFFERS.set(b);
        }
        else {
            b.setLength(0);
        }
        return b;
    }
    
    private static void addTimestamp(final StringBuilder buffer) {
        SimpleDateFormat dateFormat = AccessLogRequestHandler.DATE_FORMATTERS.get();
        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat("'['dd/MMM/yyyy:HH:mm:ss Z']'");
            AccessLogRequestHandler.DATE_FORMATTERS.set(dateFormat);
        }
        buffer.append(dateFormat.format(new Date()));
    }
    
    private StringBuilder getConnectionHeader(final String messageType) {
        final StringBuilder b = getBuffer();
        addTimestamp(b);
        b.append(' ');
        b.append(messageType);
        b.append(" conn=");
        b.append(this.clientConnection.getConnectionID());
        return b;
    }
    
    private StringBuilder getRequestHeader(final String opType, final long opID, final int msgID) {
        final StringBuilder b = getBuffer();
        addTimestamp(b);
        b.append(' ');
        b.append(opType);
        b.append(" REQUEST conn=");
        b.append(this.clientConnection.getConnectionID());
        b.append(" op=");
        b.append(opID);
        b.append(" msgID=");
        b.append(msgID);
        return b;
    }
    
    private void generateResponse(final StringBuilder b, final String opType, final long opID, final int msgID, final int resultCode, final String diagnosticMessage, final String matchedDN, final List<String> referralURLs, final long eTimeNanos) {
        b.setLength(0);
        addTimestamp(b);
        b.append(' ');
        b.append(opType);
        b.append(" RESULT conn=");
        b.append(this.clientConnection.getConnectionID());
        b.append(" op=");
        b.append(opID);
        b.append(" msgID=");
        b.append(msgID);
        b.append(" resultCode=");
        b.append(resultCode);
        if (diagnosticMessage != null) {
            b.append(" diagnosticMessage=\"");
            b.append(diagnosticMessage);
            b.append('\"');
        }
        if (matchedDN != null) {
            b.append(" matchedDN=\"");
            b.append(matchedDN);
            b.append('\"');
        }
        if (!referralURLs.isEmpty()) {
            b.append(" referralURLs=\"");
            final Iterator<String> iterator = referralURLs.iterator();
            while (iterator.hasNext()) {
                b.append(iterator.next());
                if (iterator.hasNext()) {
                    b.append(',');
                }
            }
            b.append('\"');
        }
        DecimalFormat f = AccessLogRequestHandler.DECIMAL_FORMATTERS.get();
        if (f == null) {
            f = new DecimalFormat("0.000");
            AccessLogRequestHandler.DECIMAL_FORMATTERS.set(f);
        }
        b.append(" etime=");
        b.append(f.format(eTimeNanos / 1000000.0));
    }
    
    @Override
    public ObjectPair<SearchResultEntryProtocolOp, Control[]> transformEntry(final int messageID, final SearchResultEntryProtocolOp entry, final Control[] controls) {
        final AtomicLong l = this.entryCounts.get(messageID);
        if (l != null) {
            l.incrementAndGet();
        }
        return new ObjectPair<SearchResultEntryProtocolOp, Control[]>(entry, controls);
    }
    
    static {
        DECIMAL_FORMATTERS = new ThreadLocal<DecimalFormat>();
        DATE_FORMATTERS = new ThreadLocal<SimpleDateFormat>();
        BUFFERS = new ThreadLocal<StringBuilder>();
    }
}
