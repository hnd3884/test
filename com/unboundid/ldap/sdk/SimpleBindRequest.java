package com.unboundid.ldap.sdk;

import java.util.ArrayList;
import java.util.List;
import com.unboundid.util.InternalUseOnly;
import com.unboundid.util.StaticUtils;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import com.unboundid.ldap.protocol.LDAPMessage;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Integer;
import com.unboundid.util.LDAPSDKUsageException;
import com.unboundid.asn1.ASN1BufferSequence;
import java.util.Arrays;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1Buffer;
import com.unboundid.ldap.protocol.LDAPResponse;
import java.util.concurrent.LinkedBlockingQueue;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.protocol.ProtocolOp;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class SimpleBindRequest extends BindRequest implements ResponseAcceptor, ProtocolOp
{
    private static final byte CRED_TYPE_SIMPLE = Byte.MIN_VALUE;
    private static final ASN1OctetString NO_BIND_DN;
    private static final ASN1OctetString NO_PASSWORD;
    private static final long serialVersionUID = 4725871243149974407L;
    private int messageID;
    private final ASN1OctetString bindDN;
    private final ASN1OctetString password;
    private final LinkedBlockingQueue<LDAPResponse> responseQueue;
    private final PasswordProvider passwordProvider;
    
    public SimpleBindRequest() {
        this(SimpleBindRequest.NO_BIND_DN, SimpleBindRequest.NO_PASSWORD, null, SimpleBindRequest.NO_CONTROLS);
    }
    
    public SimpleBindRequest(final String bindDN, final String password) {
        this(bindDN, password, SimpleBindRequest.NO_CONTROLS);
    }
    
    public SimpleBindRequest(final String bindDN, final byte[] password) {
        this(bindDN, password, SimpleBindRequest.NO_CONTROLS);
    }
    
    public SimpleBindRequest(final DN bindDN, final String password) {
        this(bindDN, password, SimpleBindRequest.NO_CONTROLS);
    }
    
    public SimpleBindRequest(final DN bindDN, final byte[] password) {
        this(bindDN, password, SimpleBindRequest.NO_CONTROLS);
    }
    
    public SimpleBindRequest(final String bindDN, final String password, final Control... controls) {
        super(controls);
        this.messageID = -1;
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        if (bindDN == null) {
            this.bindDN = SimpleBindRequest.NO_BIND_DN;
        }
        else {
            this.bindDN = new ASN1OctetString(bindDN);
        }
        if (password == null) {
            this.password = SimpleBindRequest.NO_PASSWORD;
        }
        else {
            this.password = new ASN1OctetString((byte)(-128), password);
        }
        this.passwordProvider = null;
    }
    
    public SimpleBindRequest(final String bindDN, final byte[] password, final Control... controls) {
        super(controls);
        this.messageID = -1;
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        if (bindDN == null) {
            this.bindDN = SimpleBindRequest.NO_BIND_DN;
        }
        else {
            this.bindDN = new ASN1OctetString(bindDN);
        }
        if (password == null) {
            this.password = SimpleBindRequest.NO_PASSWORD;
        }
        else {
            this.password = new ASN1OctetString((byte)(-128), password);
        }
        this.passwordProvider = null;
    }
    
    public SimpleBindRequest(final DN bindDN, final String password, final Control... controls) {
        super(controls);
        this.messageID = -1;
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        if (bindDN == null) {
            this.bindDN = SimpleBindRequest.NO_BIND_DN;
        }
        else {
            this.bindDN = new ASN1OctetString(bindDN.toString());
        }
        if (password == null) {
            this.password = SimpleBindRequest.NO_PASSWORD;
        }
        else {
            this.password = new ASN1OctetString((byte)(-128), password);
        }
        this.passwordProvider = null;
    }
    
    public SimpleBindRequest(final DN bindDN, final byte[] password, final Control... controls) {
        super(controls);
        this.messageID = -1;
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        if (bindDN == null) {
            this.bindDN = SimpleBindRequest.NO_BIND_DN;
        }
        else {
            this.bindDN = new ASN1OctetString(bindDN.toString());
        }
        if (password == null) {
            this.password = SimpleBindRequest.NO_PASSWORD;
        }
        else {
            this.password = new ASN1OctetString((byte)(-128), password);
        }
        this.passwordProvider = null;
    }
    
    public SimpleBindRequest(final String bindDN, final PasswordProvider passwordProvider, final Control... controls) {
        super(controls);
        this.messageID = -1;
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        this.bindDN = new ASN1OctetString(bindDN);
        this.passwordProvider = passwordProvider;
        this.password = null;
    }
    
    public SimpleBindRequest(final DN bindDN, final PasswordProvider passwordProvider, final Control... controls) {
        super(controls);
        this.messageID = -1;
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        this.bindDN = new ASN1OctetString(bindDN.toString());
        this.passwordProvider = passwordProvider;
        this.password = null;
    }
    
    private SimpleBindRequest(final ASN1OctetString bindDN, final ASN1OctetString password, final PasswordProvider passwordProvider, final Control... controls) {
        super(controls);
        this.messageID = -1;
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        this.bindDN = bindDN;
        this.password = password;
        this.passwordProvider = passwordProvider;
    }
    
    public String getBindDN() {
        return this.bindDN.stringValue();
    }
    
    public ASN1OctetString getPassword() {
        return this.password;
    }
    
    public PasswordProvider getPasswordProvider() {
        return this.passwordProvider;
    }
    
    @Override
    public byte getProtocolOpType() {
        return 96;
    }
    
    @Override
    public void writeTo(final ASN1Buffer buffer) {
        final ASN1BufferSequence requestSequence = buffer.beginSequence((byte)96);
        buffer.addElement(SimpleBindRequest.VERSION_ELEMENT);
        buffer.addElement(this.bindDN);
        if (this.passwordProvider == null) {
            buffer.addElement(this.password);
        }
        else {
            byte[] pwBytes;
            try {
                pwBytes = this.passwordProvider.getPasswordBytes();
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                throw new LDAPRuntimeException(le);
            }
            final ASN1OctetString pw = new ASN1OctetString((byte)(-128), pwBytes);
            buffer.addElement(pw);
            buffer.setZeroBufferOnClear();
            Arrays.fill(pwBytes, (byte)0);
        }
        requestSequence.end();
    }
    
    @Override
    public ASN1Element encodeProtocolOp() throws LDAPSDKUsageException {
        if (this.password == null) {
            throw new LDAPSDKUsageException(LDAPMessages.ERR_SIMPLE_BIND_ENCODE_PROTOCOL_OP_WITH_PROVIDER.get());
        }
        return new ASN1Sequence((byte)96, new ASN1Element[] { new ASN1Integer(3), this.bindDN, this.password });
    }
    
    @Override
    protected BindResult process(final LDAPConnection connection, final int depth) throws LDAPException {
        if (this.password != null && this.bindDN.getValue().length > 0 && this.password.getValue().length == 0 && connection.getConnectionOptions().bindWithDNRequiresPassword()) {
            final LDAPException le = new LDAPException(ResultCode.PARAM_ERROR, LDAPMessages.ERR_SIMPLE_BIND_DN_WITHOUT_PASSWORD.get());
            Debug.debugCodingError(le);
            throw le;
        }
        if (connection.synchronousMode()) {
            final boolean autoReconnect = connection.getConnectionOptions().autoReconnect();
            return this.processSync(connection, autoReconnect);
        }
        this.messageID = connection.nextMessageID();
        final LDAPMessage message = new LDAPMessage(this.messageID, this, this.getControls());
        connection.registerResponseAcceptor(this.messageID, this);
        try {
            final long responseTimeout = this.getResponseTimeoutMillis(connection);
            Debug.debugLDAPRequest(Level.INFO, this, this.messageID, connection);
            final long requestTime = System.nanoTime();
            connection.getConnectionStatistics().incrementNumBindRequests();
            connection.sendMessage(message, responseTimeout);
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
                throw new LDAPException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_BIND_INTERRUPTED.get(connection.getHostPort()), ie);
            }
            return this.handleResponse(connection, response, requestTime, false);
        }
        finally {
            connection.deregisterResponseAcceptor(this.messageID);
        }
    }
    
    private BindResult processSync(final LDAPConnection connection, final boolean allowRetry) throws LDAPException {
        this.messageID = connection.nextMessageID();
        final LDAPMessage message = new LDAPMessage(this.messageID, this, this.getControls());
        final long requestTime = System.nanoTime();
        Debug.debugLDAPRequest(Level.INFO, this, this.messageID, connection);
        connection.getConnectionStatistics().incrementNumBindRequests();
        try {
            connection.sendMessage(message, this.getResponseTimeoutMillis(connection));
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            if (allowRetry) {
                final BindResult bindResult = this.reconnectAndRetry(connection, le.getResultCode());
                if (bindResult != null) {
                    return bindResult;
                }
            }
            throw le;
        }
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
        return this.handleResponse(connection, response, requestTime, allowRetry);
    }
    
    private BindResult handleResponse(final LDAPConnection connection, final LDAPResponse response, final long requestTime, final boolean allowRetry) throws LDAPException {
        if (response == null) {
            final long waitTime = StaticUtils.nanosToMillis(System.nanoTime() - requestTime);
            throw new LDAPException(ResultCode.TIMEOUT, LDAPMessages.ERR_SIMPLE_BIND_CLIENT_TIMEOUT.get(waitTime, this.messageID, this.bindDN.stringValue(), connection.getHostPort()));
        }
        connection.getConnectionStatistics().incrementNumBindResponses(System.nanoTime() - requestTime);
        if (!(response instanceof ConnectionClosedResponse)) {
            final BindResult bindResult = (BindResult)response;
            if (allowRetry) {
                final BindResult retryResult = this.reconnectAndRetry(connection, bindResult.getResultCode());
                if (retryResult != null) {
                    return retryResult;
                }
            }
            return bindResult;
        }
        if (allowRetry) {
            final BindResult retryResult2 = this.reconnectAndRetry(connection, ResultCode.SERVER_DOWN);
            if (retryResult2 != null) {
                return retryResult2;
            }
        }
        final ConnectionClosedResponse ccr = (ConnectionClosedResponse)response;
        final String message = ccr.getMessage();
        if (message == null) {
            throw new LDAPException(ccr.getResultCode(), LDAPMessages.ERR_CONN_CLOSED_WAITING_FOR_BIND_RESPONSE.get(connection.getHostPort(), this.toString()));
        }
        throw new LDAPException(ccr.getResultCode(), LDAPMessages.ERR_CONN_CLOSED_WAITING_FOR_BIND_RESPONSE_WITH_MESSAGE.get(connection.getHostPort(), this.toString(), message));
    }
    
    private BindResult reconnectAndRetry(final LDAPConnection connection, final ResultCode resultCode) {
        try {
            switch (resultCode.intValue()) {
                case 81:
                case 84:
                case 91: {
                    connection.reconnect();
                    return this.processSync(connection, false);
                }
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
        return null;
    }
    
    @Override
    public SimpleBindRequest getRebindRequest(final String host, final int port) {
        return new SimpleBindRequest(this.bindDN, this.password, this.passwordProvider, this.getControls());
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
    public String getBindType() {
        return "SIMPLE";
    }
    
    @Override
    public int getLastMessageID() {
        return this.messageID;
    }
    
    @Override
    public SimpleBindRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public SimpleBindRequest duplicate(final Control[] controls) {
        final SimpleBindRequest bindRequest = new SimpleBindRequest(this.bindDN, this.password, this.passwordProvider, controls);
        bindRequest.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return bindRequest;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("SimpleBindRequest(dn='");
        buffer.append(this.bindDN);
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
        constructorArgs.add(ToCodeArgHelper.createString(this.bindDN.stringValue(), "Bind DN"));
        constructorArgs.add(ToCodeArgHelper.createString("---redacted-password---", "Bind Password"));
        final Control[] controls = this.getControls();
        if (controls.length > 0) {
            constructorArgs.add(ToCodeArgHelper.createControlArray(controls, "Bind Controls"));
        }
        ToCodeHelper.generateMethodCall(lineList, indentSpaces, "SimpleBindRequest", requestID + "Request", "new SimpleBindRequest", constructorArgs);
        if (includeProcessing) {
            final StringBuilder buffer = new StringBuilder();
            for (int i = 0; i < indentSpaces; ++i) {
                buffer.append(' ');
            }
            final String indent = buffer.toString();
            lineList.add("");
            lineList.add(indent + "try");
            lineList.add(indent + '{');
            lineList.add(indent + "  BindResult " + requestID + "Result = connection.bind(" + requestID + "Request);");
            lineList.add(indent + "  // The bind was processed successfully.");
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
    
    static {
        NO_BIND_DN = new ASN1OctetString();
        NO_PASSWORD = new ASN1OctetString((byte)(-128));
    }
}
