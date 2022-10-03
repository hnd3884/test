package com.unboundid.ldap.sdk;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import com.unboundid.util.InternalUseOnly;
import com.unboundid.util.StaticUtils;
import java.util.concurrent.TimeUnit;
import com.unboundid.util.Debug;
import java.util.logging.Level;
import com.unboundid.ldap.protocol.ProtocolOp;
import com.unboundid.ldap.protocol.LDAPMessage;
import com.unboundid.ldap.protocol.BindRequestProtocolOp;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.protocol.LDAPResponse;
import java.util.concurrent.LinkedBlockingQueue;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public abstract class SASLBindRequest extends BindRequest implements ResponseAcceptor
{
    protected static final byte CRED_TYPE_SASL = -93;
    private static final long serialVersionUID = -5842126553864908312L;
    private int messageID;
    private final LinkedBlockingQueue<LDAPResponse> responseQueue;
    
    protected SASLBindRequest(final Control[] controls) {
        super(controls);
        this.messageID = -1;
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
    }
    
    @Override
    public String getBindType() {
        return this.getSASLMechanismName();
    }
    
    public abstract String getSASLMechanismName();
    
    @Override
    public int getLastMessageID() {
        return this.messageID;
    }
    
    protected final BindResult sendBindRequest(final LDAPConnection connection, final String bindDN, final ASN1OctetString saslCredentials, final Control[] controls, final long timeoutMillis) throws LDAPException {
        this.messageID = connection.nextMessageID();
        final BindRequestProtocolOp protocolOp = new BindRequestProtocolOp(bindDN, this.getSASLMechanismName(), saslCredentials);
        final LDAPMessage requestMessage = new LDAPMessage(this.messageID, protocolOp, controls);
        return this.sendMessage(connection, requestMessage, timeoutMillis);
    }
    
    protected final BindResult sendMessage(final LDAPConnection connection, final LDAPMessage requestMessage, final long timeoutMillis) throws LDAPException {
        if (connection.synchronousMode()) {
            return this.sendMessageSync(connection, requestMessage, timeoutMillis);
        }
        final int msgID = requestMessage.getMessageID();
        connection.registerResponseAcceptor(msgID, this);
        try {
            Debug.debugLDAPRequest(Level.INFO, this, msgID, connection);
            final long requestTime = System.nanoTime();
            connection.getConnectionStatistics().incrementNumBindRequests();
            connection.sendMessage(requestMessage, timeoutMillis);
            LDAPResponse response;
            try {
                if (timeoutMillis > 0L) {
                    response = this.responseQueue.poll(timeoutMillis, TimeUnit.MILLISECONDS);
                }
                else {
                    response = this.responseQueue.take();
                }
            }
            catch (final InterruptedException ie) {
                Debug.debugException(ie);
                Thread.currentThread().interrupt();
                throw new LDAPException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_BIND_INTERRUPTED.get(connection.getHostPort()), ie);
            }
            return this.handleResponse(connection, response, requestTime);
        }
        finally {
            connection.deregisterResponseAcceptor(msgID);
        }
    }
    
    private BindResult sendMessageSync(final LDAPConnection connection, final LDAPMessage requestMessage, final long timeoutMillis) throws LDAPException {
        final int msgID = requestMessage.getMessageID();
        Debug.debugLDAPRequest(Level.INFO, this, msgID, connection);
        final long requestTime = System.nanoTime();
        connection.getConnectionStatistics().incrementNumBindRequests();
        connection.sendMessage(requestMessage, timeoutMillis);
        LDAPResponse response;
        while (true) {
            response = connection.readResponse(this.messageID);
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
    
    private BindResult handleResponse(final LDAPConnection connection, final LDAPResponse response, final long requestTime) throws LDAPException {
        if (response == null) {
            final long waitTime = StaticUtils.nanosToMillis(System.nanoTime() - requestTime);
            throw new LDAPException(ResultCode.TIMEOUT, LDAPMessages.ERR_SASL_BIND_CLIENT_TIMEOUT.get(waitTime, this.getSASLMechanismName(), this.messageID, connection.getHostPort()));
        }
        if (!(response instanceof ConnectionClosedResponse)) {
            connection.getConnectionStatistics().incrementNumBindResponses(System.nanoTime() - requestTime);
            return (BindResult)response;
        }
        final ConnectionClosedResponse ccr = (ConnectionClosedResponse)response;
        final String message = ccr.getMessage();
        if (message == null) {
            throw new LDAPException(ccr.getResultCode(), LDAPMessages.ERR_CONN_CLOSED_WAITING_FOR_BIND_RESPONSE.get(connection.getHostPort(), this.toString()));
        }
        throw new LDAPException(ccr.getResultCode(), LDAPMessages.ERR_CONN_CLOSED_WAITING_FOR_BIND_RESPONSE_WITH_MESSAGE.get(connection.getHostPort(), this.toString(), message));
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
    public void toCode(final List<String> lineList, final String requestID, final int indentSpaces, final boolean includeProcessing) {
        final ArrayList<ToCodeArgHelper> constructorArgs = new ArrayList<ToCodeArgHelper>(4);
        constructorArgs.add(ToCodeArgHelper.createString(null, "Bind DN"));
        constructorArgs.add(ToCodeArgHelper.createString(this.getSASLMechanismName(), "SASL Mechanism Name"));
        constructorArgs.add(ToCodeArgHelper.createByteArray("---redacted-SASL-credentials".getBytes(StandardCharsets.UTF_8), true, "SASL Credentials"));
        final Control[] controls = this.getControls();
        if (controls.length > 0) {
            constructorArgs.add(ToCodeArgHelper.createControlArray(controls, "Bind Controls"));
        }
        ToCodeHelper.generateMethodCall(lineList, indentSpaces, "GenericSASLBindRequest", requestID + "Request", "new GenericSASLBindRequest", constructorArgs);
        if (includeProcessing) {
            final StringBuilder buffer = new StringBuilder();
            for (int i = 0; i < indentSpaces; ++i) {
                buffer.append(' ');
            }
            final String indent = buffer.toString();
            lineList.add("");
            lineList.add(indent + '{');
            lineList.add(indent + "  BindResult " + requestID + "Result = connection.bind(" + requestID + "Request);");
            lineList.add(indent + "  // The bind was processed successfully.");
            lineList.add(indent + '}');
            lineList.add(indent + "catch (SASLBindInProgressException e)");
            lineList.add(indent + '{');
            lineList.add(indent + "  // The SASL bind requires multiple stages.  " + "Continue it here.");
            lineList.add(indent + "  // Do not attempt to use the connection for " + "any other purpose until bind processing has completed.");
            lineList.add(indent + '}');
            lineList.add(indent + "catch (LDAPException e)");
            lineList.add(indent + '{');
            lineList.add(indent + "  // The bind failed.  Maybe the following will " + "help explain why.");
            lineList.add(indent + "  // Note that the connection is now likely in " + "an unauthenticated state.");
            lineList.add(indent + "  ResultCode resultCode = e.getResultCode();");
            lineList.add(indent + "  String message = e.getMessage();");
            lineList.add(indent + "  String matchedDN = e.getMatchedDN();");
            lineList.add(indent + "  String[] referralURLs = e.getReferralURLs();");
            lineList.add(indent + "  Control[] responseControls = " + "e.getResponseControls();");
            lineList.add(indent + '}');
        }
    }
}
