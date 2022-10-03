package com.unboundid.ldap.sdk;

import java.util.ArrayList;
import java.util.List;
import com.unboundid.util.InternalUseOnly;
import com.unboundid.util.StaticUtils;
import java.util.Timer;
import java.util.logging.Level;
import java.util.TimerTask;
import com.unboundid.ldap.protocol.LDAPMessage;
import com.unboundid.util.Debug;
import java.util.concurrent.TimeUnit;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1BufferSequence;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1Buffer;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.protocol.LDAPResponse;
import java.util.concurrent.LinkedBlockingQueue;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;
import com.unboundid.ldap.protocol.ProtocolOp;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class CompareRequest extends UpdatableLDAPRequest implements ReadOnlyCompareRequest, ResponseAcceptor, ProtocolOp
{
    private static final long serialVersionUID = 6343453776330347024L;
    private final LinkedBlockingQueue<LDAPResponse> responseQueue;
    private ASN1OctetString assertionValue;
    private int messageID;
    private String attributeName;
    private String dn;
    
    public CompareRequest(final String dn, final String attributeName, final String assertionValue) {
        super(null);
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        this.messageID = -1;
        Validator.ensureNotNull(dn, attributeName, assertionValue);
        this.dn = dn;
        this.attributeName = attributeName;
        this.assertionValue = new ASN1OctetString(assertionValue);
    }
    
    public CompareRequest(final String dn, final String attributeName, final byte[] assertionValue) {
        super(null);
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        this.messageID = -1;
        Validator.ensureNotNull(dn, attributeName, assertionValue);
        this.dn = dn;
        this.attributeName = attributeName;
        this.assertionValue = new ASN1OctetString(assertionValue);
    }
    
    public CompareRequest(final DN dn, final String attributeName, final String assertionValue) {
        super(null);
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        this.messageID = -1;
        Validator.ensureNotNull(dn, attributeName, assertionValue);
        this.dn = dn.toString();
        this.attributeName = attributeName;
        this.assertionValue = new ASN1OctetString(assertionValue);
    }
    
    public CompareRequest(final DN dn, final String attributeName, final byte[] assertionValue) {
        super(null);
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        this.messageID = -1;
        Validator.ensureNotNull(dn, attributeName, assertionValue);
        this.dn = dn.toString();
        this.attributeName = attributeName;
        this.assertionValue = new ASN1OctetString(assertionValue);
    }
    
    public CompareRequest(final String dn, final String attributeName, final String assertionValue, final Control[] controls) {
        super(controls);
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        this.messageID = -1;
        Validator.ensureNotNull(dn, attributeName, assertionValue);
        this.dn = dn;
        this.attributeName = attributeName;
        this.assertionValue = new ASN1OctetString(assertionValue);
    }
    
    public CompareRequest(final String dn, final String attributeName, final byte[] assertionValue, final Control[] controls) {
        super(controls);
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        this.messageID = -1;
        Validator.ensureNotNull(dn, attributeName, assertionValue);
        this.dn = dn;
        this.attributeName = attributeName;
        this.assertionValue = new ASN1OctetString(assertionValue);
    }
    
    public CompareRequest(final DN dn, final String attributeName, final String assertionValue, final Control[] controls) {
        super(controls);
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        this.messageID = -1;
        Validator.ensureNotNull(dn, attributeName, assertionValue);
        this.dn = dn.toString();
        this.attributeName = attributeName;
        this.assertionValue = new ASN1OctetString(assertionValue);
    }
    
    public CompareRequest(final DN dn, final String attributeName, final ASN1OctetString assertionValue, final Control[] controls) {
        super(controls);
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        this.messageID = -1;
        Validator.ensureNotNull(dn, attributeName, assertionValue);
        this.dn = dn.toString();
        this.attributeName = attributeName;
        this.assertionValue = assertionValue;
    }
    
    public CompareRequest(final DN dn, final String attributeName, final byte[] assertionValue, final Control[] controls) {
        super(controls);
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        this.messageID = -1;
        Validator.ensureNotNull(dn, attributeName, assertionValue);
        this.dn = dn.toString();
        this.attributeName = attributeName;
        this.assertionValue = new ASN1OctetString(assertionValue);
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
    public String getAttributeName() {
        return this.attributeName;
    }
    
    public void setAttributeName(final String attributeName) {
        Validator.ensureNotNull(attributeName);
        this.attributeName = attributeName;
    }
    
    @Override
    public String getAssertionValue() {
        return this.assertionValue.stringValue();
    }
    
    @Override
    public byte[] getAssertionValueBytes() {
        return this.assertionValue.getValue();
    }
    
    @Override
    public ASN1OctetString getRawAssertionValue() {
        return this.assertionValue;
    }
    
    public void setAssertionValue(final String assertionValue) {
        Validator.ensureNotNull(assertionValue);
        this.assertionValue = new ASN1OctetString(assertionValue);
    }
    
    public void setAssertionValue(final byte[] assertionValue) {
        Validator.ensureNotNull(assertionValue);
        this.assertionValue = new ASN1OctetString(assertionValue);
    }
    
    public void setAssertionValue(final ASN1OctetString assertionValue) {
        this.assertionValue = assertionValue;
    }
    
    @Override
    public byte getProtocolOpType() {
        return 110;
    }
    
    @Override
    public void writeTo(final ASN1Buffer buffer) {
        final ASN1BufferSequence requestSequence = buffer.beginSequence((byte)110);
        buffer.addOctetString(this.dn);
        final ASN1BufferSequence avaSequence = buffer.beginSequence();
        buffer.addOctetString(this.attributeName);
        buffer.addElement(this.assertionValue);
        avaSequence.end();
        requestSequence.end();
    }
    
    @Override
    public ASN1Element encodeProtocolOp() {
        final ASN1Element[] avaElements = { new ASN1OctetString(this.attributeName), this.assertionValue };
        final ASN1Element[] protocolOpElements = { new ASN1OctetString(this.dn), new ASN1Sequence(avaElements) };
        return new ASN1Sequence((byte)110, protocolOpElements);
    }
    
    @Override
    protected CompareResult process(final LDAPConnection connection, final int depth) throws LDAPException {
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
                throw new LDAPException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_COMPARE_INTERRUPTED.get(connection.getHostPort()), ie);
            }
            return this.handleResponse(connection, response, requestTime, depth, false);
        }
        finally {
            connection.deregisterResponseAcceptor(this.messageID);
        }
    }
    
    AsyncRequestID processAsync(final LDAPConnection connection, final AsyncCompareResultListener resultListener) throws LDAPException {
        this.messageID = connection.nextMessageID();
        final LDAPMessage message = new LDAPMessage(this.messageID, this, this.getControls());
        final long timeout = this.getResponseTimeoutMillis(connection);
        AsyncRequestID asyncRequestID;
        if (resultListener == null) {
            asyncRequestID = null;
            connection.registerResponseAcceptor(this.messageID, this);
        }
        else {
            final AsyncCompareHelper compareHelper = new AsyncCompareHelper(connection, this.messageID, resultListener, this.getIntermediateResponseListener());
            connection.registerResponseAcceptor(this.messageID, compareHelper);
            asyncRequestID = compareHelper.getAsyncRequestID();
            if (timeout > 0L) {
                final Timer timer = connection.getTimer();
                final AsyncTimeoutTimerTask timerTask = new AsyncTimeoutTimerTask(compareHelper);
                timer.schedule(timerTask, timeout);
                asyncRequestID.setTimerTask(timerTask);
            }
        }
        try {
            Debug.debugLDAPRequest(Level.INFO, this, this.messageID, connection);
            connection.getConnectionStatistics().incrementNumCompareRequests();
            connection.sendMessage(message, timeout);
            return asyncRequestID;
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            connection.deregisterResponseAcceptor(this.messageID);
            throw le;
        }
    }
    
    private CompareResult processSync(final LDAPConnection connection, final int depth, final boolean allowRetry) throws LDAPException {
        this.messageID = connection.nextMessageID();
        final LDAPMessage message = new LDAPMessage(this.messageID, this, this.getControls());
        final long requestTime = System.nanoTime();
        Debug.debugLDAPRequest(Level.INFO, this, this.messageID, connection);
        connection.getConnectionStatistics().incrementNumCompareRequests();
        try {
            connection.sendMessage(message, this.getResponseTimeoutMillis(connection));
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            if (allowRetry) {
                final CompareResult retryResult = this.reconnectAndRetry(connection, depth, le.getResultCode());
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
                    final CompareResult retryResult2 = this.reconnectAndRetry(connection, depth, le2.getResultCode());
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
    
    private CompareResult handleResponse(final LDAPConnection connection, final LDAPResponse response, final long requestTime, final int depth, final boolean allowRetry) throws LDAPException {
        if (response == null) {
            final long waitTime = StaticUtils.nanosToMillis(System.nanoTime() - requestTime);
            if (connection.getConnectionOptions().abandonOnTimeout()) {
                connection.abandon(this.messageID, new Control[0]);
            }
            throw new LDAPException(ResultCode.TIMEOUT, LDAPMessages.ERR_COMPARE_CLIENT_TIMEOUT.get(waitTime, this.messageID, this.dn, connection.getHostPort()));
        }
        connection.getConnectionStatistics().incrementNumCompareResponses(System.nanoTime() - requestTime);
        if (response instanceof ConnectionClosedResponse) {
            if (allowRetry) {
                final CompareResult retryResult = this.reconnectAndRetry(connection, depth, ResultCode.SERVER_DOWN);
                if (retryResult != null) {
                    return retryResult;
                }
            }
            final ConnectionClosedResponse ccr = (ConnectionClosedResponse)response;
            final String message = ccr.getMessage();
            if (message == null) {
                throw new LDAPException(ccr.getResultCode(), LDAPMessages.ERR_CONN_CLOSED_WAITING_FOR_COMPARE_RESPONSE.get(connection.getHostPort(), this.toString()));
            }
            throw new LDAPException(ccr.getResultCode(), LDAPMessages.ERR_CONN_CLOSED_WAITING_FOR_COMPARE_RESPONSE_WITH_MESSAGE.get(connection.getHostPort(), this.toString(), message));
        }
        else {
            CompareResult result;
            if (response instanceof CompareResult) {
                result = (CompareResult)response;
            }
            else {
                result = new CompareResult((LDAPResult)response);
            }
            if (!result.getResultCode().equals(ResultCode.REFERRAL) || !this.followReferrals(connection)) {
                if (allowRetry) {
                    final CompareResult retryResult2 = this.reconnectAndRetry(connection, depth, result.getResultCode());
                    if (retryResult2 != null) {
                        return retryResult2;
                    }
                }
                return result;
            }
            if (depth >= connection.getConnectionOptions().getReferralHopLimit()) {
                return new CompareResult(this.messageID, ResultCode.REFERRAL_LIMIT_EXCEEDED, LDAPMessages.ERR_TOO_MANY_REFERRALS.get(), result.getMatchedDN(), result.getReferralURLs(), result.getResponseControls());
            }
            return this.followReferral(result, connection, depth);
        }
    }
    
    private CompareResult reconnectAndRetry(final LDAPConnection connection, final int depth, final ResultCode resultCode) {
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
    
    private CompareResult followReferral(final CompareResult referralResult, final LDAPConnection connection, final int depth) throws LDAPException {
        for (final String urlString : referralResult.getReferralURLs()) {
            try {
                final LDAPURL referralURL = new LDAPURL(urlString);
                final String host = referralURL.getHost();
                if (host != null) {
                    CompareRequest compareRequest;
                    if (referralURL.baseDNProvided()) {
                        compareRequest = new CompareRequest(referralURL.getBaseDN(), this.attributeName, this.assertionValue, this.getControls());
                    }
                    else {
                        compareRequest = this;
                    }
                    final LDAPConnection referralConn = this.getReferralConnector(connection).getReferralConnection(referralURL, connection);
                    try {
                        return compareRequest.process(referralConn, depth + 1);
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
        return OperationType.COMPARE;
    }
    
    @Override
    public CompareRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public CompareRequest duplicate(final Control[] controls) {
        final CompareRequest r = new CompareRequest(this.dn, this.attributeName, this.assertionValue.getValue(), controls);
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
    public void toString(final StringBuilder buffer) {
        buffer.append("CompareRequest(dn='");
        buffer.append(this.dn);
        buffer.append("', attr='");
        buffer.append(this.attributeName);
        buffer.append("', value='");
        buffer.append(this.assertionValue.stringValue());
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
        final ArrayList<ToCodeArgHelper> constructorArgs = new ArrayList<ToCodeArgHelper>(3);
        constructorArgs.add(ToCodeArgHelper.createString(this.dn, "Entry DN"));
        constructorArgs.add(ToCodeArgHelper.createString(this.attributeName, "Attribute Name"));
        if (StaticUtils.isSensitiveToCodeAttribute(this.attributeName)) {
            constructorArgs.add(ToCodeArgHelper.createString("---redacted-value", "Assertion Value (Redacted because " + this.attributeName + " is " + "configured as a sensitive attribute)"));
        }
        else if (StaticUtils.isPrintableString(this.assertionValue.getValue())) {
            constructorArgs.add(ToCodeArgHelper.createString(this.assertionValue.stringValue(), "Assertion Value"));
        }
        else {
            constructorArgs.add(ToCodeArgHelper.createByteArray(this.assertionValue.getValue(), true, "Assertion Value"));
        }
        ToCodeHelper.generateMethodCall(lineList, indentSpaces, "CompareRequest", requestID + "Request", "new CompareRequest", constructorArgs);
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
            lineList.add(indent + "  CompareResult " + requestID + "Result = connection.compare(" + requestID + "Request);");
            lineList.add(indent + "  // The compare was processed successfully.");
            lineList.add(indent + "  boolean compareMatched = " + requestID + "Result.compareMatched();");
            lineList.add(indent + '}');
            lineList.add(indent + "catch (LDAPException e)");
            lineList.add(indent + '{');
            lineList.add(indent + "  // The compare failed.  Maybe the following " + "will help explain why.");
            lineList.add(indent + "  ResultCode resultCode = e.getResultCode();");
            lineList.add(indent + "  String message = e.getMessage();");
            lineList.add(indent + "  String matchedDN = e.getMatchedDN();");
            lineList.add(indent + "  String[] referralURLs = e.getReferralURLs();");
            lineList.add(indent + "  Control[] responseControls = " + "e.getResponseControls();");
            lineList.add(indent + '}');
        }
    }
}
