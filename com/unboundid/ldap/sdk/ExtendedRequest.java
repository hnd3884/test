package com.unboundid.ldap.sdk;

import java.util.ArrayList;
import java.util.List;
import com.unboundid.util.InternalUseOnly;
import com.unboundid.util.StaticUtils;
import java.util.concurrent.TimeUnit;
import com.unboundid.ldap.sdk.extensions.StartTLSExtendedRequest;
import com.unboundid.util.Debug;
import java.util.logging.Level;
import com.unboundid.ldap.protocol.LDAPMessage;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1BufferSequence;
import com.unboundid.asn1.ASN1Buffer;
import com.unboundid.util.Validator;
import com.unboundid.ldap.protocol.LDAPResponse;
import java.util.concurrent.LinkedBlockingQueue;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.Extensible;
import com.unboundid.ldap.protocol.ProtocolOp;

@Extensible
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public class ExtendedRequest extends LDAPRequest implements ResponseAcceptor, ProtocolOp
{
    protected static final byte TYPE_EXTENDED_REQUEST_OID = Byte.MIN_VALUE;
    protected static final byte TYPE_EXTENDED_REQUEST_VALUE = -127;
    private static final long serialVersionUID = 5572410770060685796L;
    private final ASN1OctetString value;
    private int messageID;
    private final LinkedBlockingQueue<LDAPResponse> responseQueue;
    private final String oid;
    
    public ExtendedRequest(final String oid) {
        super(null);
        this.messageID = -1;
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        Validator.ensureNotNull(oid);
        this.oid = oid;
        this.value = null;
    }
    
    public ExtendedRequest(final String oid, final Control[] controls) {
        super(controls);
        this.messageID = -1;
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        Validator.ensureNotNull(oid);
        this.oid = oid;
        this.value = null;
    }
    
    public ExtendedRequest(final String oid, final ASN1OctetString value) {
        super(null);
        this.messageID = -1;
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        Validator.ensureNotNull(oid);
        this.oid = oid;
        this.value = value;
    }
    
    public ExtendedRequest(final String oid, final ASN1OctetString value, final Control[] controls) {
        super(controls);
        this.messageID = -1;
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        Validator.ensureNotNull(oid);
        this.oid = oid;
        this.value = value;
    }
    
    protected ExtendedRequest(final ExtendedRequest extendedRequest) {
        super(extendedRequest.getControls());
        this.messageID = -1;
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        this.messageID = extendedRequest.messageID;
        this.oid = extendedRequest.oid;
        this.value = extendedRequest.value;
    }
    
    public final String getOID() {
        return this.oid;
    }
    
    public final boolean hasValue() {
        return this.value != null;
    }
    
    public final ASN1OctetString getValue() {
        return this.value;
    }
    
    @Override
    public final byte getProtocolOpType() {
        return 119;
    }
    
    @Override
    public final void writeTo(final ASN1Buffer writer) {
        final ASN1BufferSequence requestSequence = writer.beginSequence((byte)119);
        writer.addOctetString((byte)(-128), this.oid);
        if (this.value != null) {
            writer.addOctetString((byte)(-127), this.value.getValue());
        }
        requestSequence.end();
    }
    
    @Override
    public ASN1Element encodeProtocolOp() {
        ASN1Element[] protocolOpElements;
        if (this.value == null) {
            protocolOpElements = new ASN1Element[] { new ASN1OctetString((byte)(-128), this.oid) };
        }
        else {
            protocolOpElements = new ASN1Element[] { new ASN1OctetString((byte)(-128), this.oid), new ASN1OctetString((byte)(-127), this.value.getValue()) };
        }
        return new ASN1Sequence((byte)119, protocolOpElements);
    }
    
    @Override
    protected ExtendedResult process(final LDAPConnection connection, final int depth) throws LDAPException {
        if (connection.synchronousMode()) {
            return this.processSync(connection);
        }
        this.messageID = connection.nextMessageID();
        final LDAPMessage message = new LDAPMessage(this.messageID, this, this.getControls());
        connection.registerResponseAcceptor(this.messageID, this);
        try {
            final long responseTimeout = this.getResponseTimeoutMillis(connection);
            Debug.debugLDAPRequest(Level.INFO, this, this.messageID, connection);
            final long requestTime = System.nanoTime();
            connection.getConnectionStatistics().incrementNumExtendedRequests();
            if (this instanceof StartTLSExtendedRequest) {
                connection.sendMessage(message, 50L);
            }
            else {
                connection.sendMessage(message, responseTimeout);
            }
            LDAPResponse response;
            try {
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
                throw new LDAPException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_EXTOP_INTERRUPTED.get(connection.getHostPort()), ie);
            }
            return this.handleResponse(connection, response, requestTime);
        }
        finally {
            connection.deregisterResponseAcceptor(this.messageID);
        }
    }
    
    private ExtendedResult processSync(final LDAPConnection connection) throws LDAPException {
        this.messageID = connection.nextMessageID();
        final LDAPMessage message = new LDAPMessage(this.messageID, this, this.getControls());
        final long requestTime = System.nanoTime();
        Debug.debugLDAPRequest(Level.INFO, this, this.messageID, connection);
        connection.getConnectionStatistics().incrementNumExtendedRequests();
        connection.sendMessage(message, this.getResponseTimeoutMillis(connection));
        LDAPResponse response;
        while (true) {
            try {
                response = connection.readResponse(this.messageID);
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                if (le.getResultCode() == ResultCode.TIMEOUT && connection.getConnectionOptions().abandonOnTimeout()) {
                    connection.abandon(this.messageID, new Control[0]);
                }
                throw le;
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
        return this.handleResponse(connection, response, requestTime);
    }
    
    private ExtendedResult handleResponse(final LDAPConnection connection, final LDAPResponse response, final long requestTime) throws LDAPException {
        if (response == null) {
            final long waitTime = StaticUtils.nanosToMillis(System.nanoTime() - requestTime);
            if (connection.getConnectionOptions().abandonOnTimeout()) {
                connection.abandon(this.messageID, new Control[0]);
            }
            throw new LDAPException(ResultCode.TIMEOUT, LDAPMessages.ERR_EXTENDED_CLIENT_TIMEOUT.get(waitTime, this.messageID, this.oid, connection.getHostPort()));
        }
        if (!(response instanceof ConnectionClosedResponse)) {
            connection.getConnectionStatistics().incrementNumExtendedResponses(System.nanoTime() - requestTime);
            return (ExtendedResult)response;
        }
        final ConnectionClosedResponse ccr = (ConnectionClosedResponse)response;
        final String msg = ccr.getMessage();
        if (msg == null) {
            throw new LDAPException(ccr.getResultCode(), LDAPMessages.ERR_CONN_CLOSED_WAITING_FOR_EXTENDED_RESPONSE.get(connection.getHostPort(), this.toString()));
        }
        throw new LDAPException(ccr.getResultCode(), LDAPMessages.ERR_CONN_CLOSED_WAITING_FOR_EXTENDED_RESPONSE_WITH_MESSAGE.get(connection.getHostPort(), this.toString(), msg));
    }
    
    @InternalUseOnly
    @Override
    public final void responseReceived(final LDAPResponse response) throws LDAPException {
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
    public final int getLastMessageID() {
        return this.messageID;
    }
    
    @Override
    public final OperationType getOperationType() {
        return OperationType.EXTENDED;
    }
    
    @Override
    public ExtendedRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public ExtendedRequest duplicate(final Control[] controls) {
        final ExtendedRequest r = new ExtendedRequest(this.oid, this.value, controls);
        r.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return r;
    }
    
    public String getExtendedRequestName() {
        return this.oid;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("ExtendedRequest(oid='");
        buffer.append(this.oid);
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
        constructorArgs.add(ToCodeArgHelper.createString(this.oid, "Request OID"));
        constructorArgs.add(ToCodeArgHelper.createASN1OctetString(this.value, "Request Value"));
        final Control[] controls = this.getControls();
        if (controls.length > 0) {
            constructorArgs.add(ToCodeArgHelper.createControlArray(controls, "Request Controls"));
        }
        ToCodeHelper.generateMethodCall(lineList, indentSpaces, "ExtendedRequest", requestID + "Request", "new ExtendedRequest", constructorArgs);
        if (includeProcessing) {
            final StringBuilder buffer = new StringBuilder();
            for (int i = 0; i < indentSpaces; ++i) {
                buffer.append(' ');
            }
            final String indent = buffer.toString();
            lineList.add("");
            lineList.add(indent + "try");
            lineList.add(indent + '{');
            lineList.add(indent + "  ExtendedResult " + requestID + "Result = connection.processExtendedOperation(" + requestID + "Request);");
            lineList.add(indent + "  // The extended operation was processed and " + "we have a result.");
            lineList.add(indent + "  // This does not necessarily mean that the " + "operation was successful.");
            lineList.add(indent + "  // Examine the result details for more " + "information.");
            lineList.add(indent + "  ResultCode resultCode = " + requestID + "Result.getResultCode();");
            lineList.add(indent + "  String message = " + requestID + "Result.getMessage();");
            lineList.add(indent + "  String matchedDN = " + requestID + "Result.getMatchedDN();");
            lineList.add(indent + "  String[] referralURLs = " + requestID + "Result.getReferralURLs();");
            lineList.add(indent + "  String responseOID = " + requestID + "Result.getOID();");
            lineList.add(indent + "  ASN1OctetString responseValue = " + requestID + "Result.getValue();");
            lineList.add(indent + "  Control[] responseControls = " + requestID + "Result.getResponseControls();");
            lineList.add(indent + '}');
            lineList.add(indent + "catch (LDAPException e)");
            lineList.add(indent + '{');
            lineList.add(indent + "  // A problem was encountered while attempting " + "to process the extended operation.");
            lineList.add(indent + "  // Maybe the following will help explain why.");
            lineList.add(indent + "  ResultCode resultCode = e.getResultCode();");
            lineList.add(indent + "  String message = e.getMessage();");
            lineList.add(indent + "  String matchedDN = e.getMatchedDN();");
            lineList.add(indent + "  String[] referralURLs = e.getReferralURLs();");
            lineList.add(indent + "  Control[] responseControls = " + "e.getResponseControls();");
            lineList.add(indent + '}');
        }
    }
}
