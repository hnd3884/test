package com.unboundid.ldap.sdk;

import java.util.List;
import com.unboundid.ldif.LDIFDeleteChangeRecord;
import com.unboundid.util.InternalUseOnly;
import com.unboundid.util.StaticUtils;
import java.util.Timer;
import java.util.logging.Level;
import java.util.TimerTask;
import com.unboundid.ldap.protocol.LDAPMessage;
import com.unboundid.util.Debug;
import java.util.concurrent.TimeUnit;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1Buffer;
import com.unboundid.util.Validator;
import com.unboundid.ldap.protocol.LDAPResponse;
import java.util.concurrent.LinkedBlockingQueue;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;
import com.unboundid.ldap.protocol.ProtocolOp;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class DeleteRequest extends UpdatableLDAPRequest implements ReadOnlyDeleteRequest, ResponseAcceptor, ProtocolOp
{
    private static final long serialVersionUID = -6126029442850884239L;
    private int messageID;
    private final LinkedBlockingQueue<LDAPResponse> responseQueue;
    private String dn;
    
    public DeleteRequest(final String dn) {
        super(null);
        this.messageID = -1;
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        Validator.ensureNotNull(dn);
        this.dn = dn;
    }
    
    public DeleteRequest(final String dn, final Control[] controls) {
        super(controls);
        this.messageID = -1;
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        Validator.ensureNotNull(dn);
        this.dn = dn;
    }
    
    public DeleteRequest(final DN dn) {
        super(null);
        this.messageID = -1;
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        Validator.ensureNotNull(dn);
        this.dn = dn.toString();
    }
    
    public DeleteRequest(final DN dn, final Control[] controls) {
        super(controls);
        this.messageID = -1;
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        Validator.ensureNotNull(dn);
        this.dn = dn.toString();
    }
    
    @Override
    public String getDN() {
        return this.dn;
    }
    
    public void setDN(final String dn) {
        Validator.ensureNotNull(dn);
        this.dn = dn;
    }
    
    public void setDN(final DN dn) {
        Validator.ensureNotNull(dn);
        this.dn = dn.toString();
    }
    
    @Override
    public byte getProtocolOpType() {
        return 74;
    }
    
    @Override
    public void writeTo(final ASN1Buffer buffer) {
        buffer.addOctetString((byte)74, this.dn);
    }
    
    @Override
    public ASN1Element encodeProtocolOp() {
        return new ASN1OctetString((byte)74, this.dn);
    }
    
    @Override
    protected LDAPResult process(final LDAPConnection connection, final int depth) throws LDAPException {
        if (connection.synchronousMode()) {
            final boolean autoReconnect = connection.getConnectionOptions().autoReconnect();
            return this.processSync(connection, depth, autoReconnect);
        }
        final long requestTime = System.nanoTime();
        this.processAsync(connection, null);
        try {
            LDAPResponse response;
            try {
                final long responseTimeout = this.getResponseTimeoutMillis(connection);
                if (responseTimeout > 0L) {
                    response = this.responseQueue.poll(responseTimeout, TimeUnit.MILLISECONDS);
                }
                else {
                    response = this.responseQueue.take();
                }
            }
            catch (final InterruptedException ie) {
                Debug.debugException(ie);
                Thread.currentThread().interrupt();
                throw new LDAPException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_DELETE_INTERRUPTED.get(connection.getHostPort()), ie);
            }
            return this.handleResponse(connection, response, requestTime, depth, false);
        }
        finally {
            connection.deregisterResponseAcceptor(this.messageID);
        }
    }
    
    AsyncRequestID processAsync(final LDAPConnection connection, final AsyncResultListener resultListener) throws LDAPException {
        this.messageID = connection.nextMessageID();
        final LDAPMessage message = new LDAPMessage(this.messageID, this, this.getControls());
        final long timeout = this.getResponseTimeoutMillis(connection);
        AsyncRequestID asyncRequestID;
        if (resultListener == null) {
            asyncRequestID = null;
            connection.registerResponseAcceptor(this.messageID, this);
        }
        else {
            final AsyncHelper helper = new AsyncHelper(connection, OperationType.DELETE, this.messageID, resultListener, this.getIntermediateResponseListener());
            connection.registerResponseAcceptor(this.messageID, helper);
            asyncRequestID = helper.getAsyncRequestID();
            if (timeout > 0L) {
                final Timer timer = connection.getTimer();
                final AsyncTimeoutTimerTask timerTask = new AsyncTimeoutTimerTask(helper);
                timer.schedule(timerTask, timeout);
                asyncRequestID.setTimerTask(timerTask);
            }
        }
        try {
            Debug.debugLDAPRequest(Level.INFO, this, this.messageID, connection);
            connection.getConnectionStatistics().incrementNumDeleteRequests();
            connection.sendMessage(message, timeout);
            return asyncRequestID;
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            connection.deregisterResponseAcceptor(this.messageID);
            throw le;
        }
    }
    
    private LDAPResult processSync(final LDAPConnection connection, final int depth, final boolean allowRetry) throws LDAPException {
        this.messageID = connection.nextMessageID();
        final LDAPMessage message = new LDAPMessage(this.messageID, this, this.getControls());
        final long requestTime = System.nanoTime();
        Debug.debugLDAPRequest(Level.INFO, this, this.messageID, connection);
        connection.getConnectionStatistics().incrementNumDeleteRequests();
        try {
            connection.sendMessage(message, this.getResponseTimeoutMillis(connection));
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            if (allowRetry) {
                final LDAPResult retryResult = this.reconnectAndRetry(connection, depth, le.getResultCode());
                if (retryResult != null) {
                    return retryResult;
                }
            }
            throw le;
        }
        LDAPResponse response;
        while (true) {
            try {
                response = connection.readResponse(this.messageID);
            }
            catch (final LDAPException le2) {
                Debug.debugException(le2);
                if (le2.getResultCode() == ResultCode.TIMEOUT && connection.getConnectionOptions().abandonOnTimeout()) {
                    connection.abandon(this.messageID, new Control[0]);
                }
                if (allowRetry) {
                    final LDAPResult retryResult2 = this.reconnectAndRetry(connection, depth, le2.getResultCode());
                    if (retryResult2 != null) {
                        return retryResult2;
                    }
                }
                throw le2;
            }
            if (!(response instanceof IntermediateResponse)) {
                break;
            }
            final IntermediateResponseListener listener = this.getIntermediateResponseListener();
            if (listener == null) {
                continue;
            }
            listener.intermediateResponseReturned((IntermediateResponse)response);
        }
        return this.handleResponse(connection, response, requestTime, depth, allowRetry);
    }
    
    private LDAPResult handleResponse(final LDAPConnection connection, final LDAPResponse response, final long requestTime, final int depth, final boolean allowRetry) throws LDAPException {
        if (response == null) {
            final long waitTime = StaticUtils.nanosToMillis(System.nanoTime() - requestTime);
            if (connection.getConnectionOptions().abandonOnTimeout()) {
                connection.abandon(this.messageID, new Control[0]);
            }
            throw new LDAPException(ResultCode.TIMEOUT, LDAPMessages.ERR_DELETE_CLIENT_TIMEOUT.get(waitTime, this.messageID, this.dn, connection.getHostPort()));
        }
        connection.getConnectionStatistics().incrementNumDeleteResponses(System.nanoTime() - requestTime);
        if (response instanceof ConnectionClosedResponse) {
            if (allowRetry) {
                final LDAPResult retryResult = this.reconnectAndRetry(connection, depth, ResultCode.SERVER_DOWN);
                if (retryResult != null) {
                    return retryResult;
                }
            }
            final ConnectionClosedResponse ccr = (ConnectionClosedResponse)response;
            final String message = ccr.getMessage();
            if (message == null) {
                throw new LDAPException(ccr.getResultCode(), LDAPMessages.ERR_CONN_CLOSED_WAITING_FOR_DELETE_RESPONSE.get(connection.getHostPort(), this.toString()));
            }
            throw new LDAPException(ccr.getResultCode(), LDAPMessages.ERR_CONN_CLOSED_WAITING_FOR_DELETE_RESPONSE_WITH_MESSAGE.get(connection.getHostPort(), this.toString(), message));
        }
        else {
            final LDAPResult result = (LDAPResult)response;
            if (!result.getResultCode().equals(ResultCode.REFERRAL) || !this.followReferrals(connection)) {
                if (allowRetry) {
                    final LDAPResult retryResult2 = this.reconnectAndRetry(connection, depth, result.getResultCode());
                    if (retryResult2 != null) {
                        return retryResult2;
                    }
                }
                return result;
            }
            if (depth >= connection.getConnectionOptions().getReferralHopLimit()) {
                return new LDAPResult(this.messageID, ResultCode.REFERRAL_LIMIT_EXCEEDED, LDAPMessages.ERR_TOO_MANY_REFERRALS.get(), result.getMatchedDN(), result.getReferralURLs(), result.getResponseControls());
            }
            return this.followReferral(result, connection, depth);
        }
    }
    
    private LDAPResult reconnectAndRetry(final LDAPConnection connection, final int depth, final ResultCode resultCode) {
        try {
            switch (resultCode.intValue()) {
                case 81:
                case 84:
                case 91: {
                    connection.reconnect();
                    return this.processSync(connection, depth, false);
                }
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
        return null;
    }
    
    private LDAPResult followReferral(final LDAPResult referralResult, final LDAPConnection connection, final int depth) throws LDAPException {
        for (final String urlString : referralResult.getReferralURLs()) {
            try {
                final LDAPURL referralURL = new LDAPURL(urlString);
                final String host = referralURL.getHost();
                if (host != null) {
                    DeleteRequest deleteRequest;
                    if (referralURL.baseDNProvided()) {
                        deleteRequest = new DeleteRequest(referralURL.getBaseDN(), this.getControls());
                    }
                    else {
                        deleteRequest = this;
                    }
                    final LDAPConnection referralConn = this.getReferralConnector(connection).getReferralConnection(referralURL, connection);
                    try {
                        return deleteRequest.process(referralConn, depth + 1);
                    }
                    finally {
                        referralConn.setDisconnectInfo(DisconnectType.REFERRAL, null, null);
                        referralConn.close();
                    }
                }
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
            }
        }
        return referralResult;
    }
    
    @InternalUseOnly
    @Override
    public void responseReceived(final LDAPResponse response) throws LDAPException {
        try {
            this.responseQueue.put(response);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new LDAPException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_EXCEPTION_HANDLING_RESPONSE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    @Override
    public int getLastMessageID() {
        return this.messageID;
    }
    
    @Override
    public OperationType getOperationType() {
        return OperationType.DELETE;
    }
    
    @Override
    public DeleteRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public DeleteRequest duplicate(final Control[] controls) {
        final DeleteRequest r = new DeleteRequest(this.dn, controls);
        if (this.followReferralsInternal() != null) {
            r.setFollowReferrals(this.followReferralsInternal());
        }
        if (this.getReferralConnectorInternal() != null) {
            r.setReferralConnector(this.getReferralConnectorInternal());
        }
        r.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return r;
    }
    
    @Override
    public LDIFDeleteChangeRecord toLDIFChangeRecord() {
        return new LDIFDeleteChangeRecord(this);
    }
    
    @Override
    public String[] toLDIF() {
        return this.toLDIFChangeRecord().toLDIF();
    }
    
    @Override
    public String toLDIFString() {
        return this.toLDIFChangeRecord().toLDIFString();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("DeleteRequest(dn='");
        buffer.append(this.dn);
        buffer.append('\'');
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
        ToCodeHelper.generateMethodCall(lineList, indentSpaces, "DeleteRequest", requestID + "Request", "new DeleteRequest", ToCodeArgHelper.createString(this.dn, "Entry DN"));
        for (final Control c : this.getControls()) {
            ToCodeHelper.generateMethodCall(lineList, indentSpaces, null, null, requestID + "Request.addControl", ToCodeArgHelper.createControl(c, null));
        }
        if (includeProcessing) {
            final StringBuilder buffer = new StringBuilder();
            for (int i = 0; i < indentSpaces; ++i) {
                buffer.append(' ');
            }
            final String indent = buffer.toString();
            lineList.add("");
            lineList.add(indent + "try");
            lineList.add(indent + '{');
            lineList.add(indent + "  LDAPResult " + requestID + "Result = connection.delete(" + requestID + "Request);");
            lineList.add(indent + "  // The delete was processed successfully.");
            lineList.add(indent + '}');
            lineList.add(indent + "catch (LDAPException e)");
            lineList.add(indent + '{');
            lineList.add(indent + "  // The delete failed.  Maybe the following " + "will help explain why.");
            lineList.add(indent + "  ResultCode resultCode = e.getResultCode();");
            lineList.add(indent + "  String message = e.getMessage();");
            lineList.add(indent + "  String matchedDN = e.getMatchedDN();");
            lineList.add(indent + "  String[] referralURLs = e.getReferralURLs();");
            lineList.add(indent + "  Control[] responseControls = " + "e.getResponseControls();");
            lineList.add(indent + '}');
        }
    }
}
