package com.unboundid.ldap.sdk;

import com.unboundid.util.InternalUseOnly;
import com.unboundid.util.StaticUtils;
import java.util.Timer;
import java.util.logging.Level;
import java.util.TimerTask;
import com.unboundid.ldap.protocol.LDAPMessage;
import com.unboundid.util.Debug;
import java.util.concurrent.TimeUnit;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Element;
import java.util.Iterator;
import com.unboundid.asn1.ASN1BufferSequence;
import com.unboundid.asn1.ASN1Buffer;
import java.util.Collections;
import com.unboundid.ldif.LDIFChangeRecord;
import com.unboundid.ldif.LDIFException;
import com.unboundid.ldif.LDIFModifyChangeRecord;
import com.unboundid.ldif.LDIFReader;
import java.util.List;
import java.util.Collection;
import java.util.Arrays;
import com.unboundid.util.Validator;
import java.util.ArrayList;
import com.unboundid.ldap.protocol.LDAPResponse;
import java.util.concurrent.LinkedBlockingQueue;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;
import com.unboundid.ldap.protocol.ProtocolOp;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class ModifyRequest extends UpdatableLDAPRequest implements ReadOnlyModifyRequest, ResponseAcceptor, ProtocolOp
{
    private static final long serialVersionUID = -4747622844001634758L;
    private final LinkedBlockingQueue<LDAPResponse> responseQueue;
    private final ArrayList<Modification> modifications;
    private int messageID;
    private String dn;
    
    public ModifyRequest(final String dn, final Modification mod) {
        super(null);
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        this.messageID = -1;
        Validator.ensureNotNull(dn, mod);
        this.dn = dn;
        (this.modifications = new ArrayList<Modification>(1)).add(mod);
    }
    
    public ModifyRequest(final String dn, final Modification... mods) {
        super(null);
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        this.messageID = -1;
        Validator.ensureNotNull(dn, mods);
        Validator.ensureFalse(mods.length == 0, "ModifyRequest.mods must not be empty.");
        this.dn = dn;
        (this.modifications = new ArrayList<Modification>(mods.length)).addAll(Arrays.asList(mods));
    }
    
    public ModifyRequest(final String dn, final List<Modification> mods) {
        super(null);
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        this.messageID = -1;
        Validator.ensureNotNull(dn, mods);
        Validator.ensureFalse(mods.isEmpty(), "ModifyRequest.mods must not be empty.");
        this.dn = dn;
        this.modifications = new ArrayList<Modification>(mods);
    }
    
    public ModifyRequest(final DN dn, final Modification mod) {
        super(null);
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        this.messageID = -1;
        Validator.ensureNotNull(dn, mod);
        this.dn = dn.toString();
        (this.modifications = new ArrayList<Modification>(1)).add(mod);
    }
    
    public ModifyRequest(final DN dn, final Modification... mods) {
        super(null);
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        this.messageID = -1;
        Validator.ensureNotNull(dn, mods);
        Validator.ensureFalse(mods.length == 0, "ModifyRequest.mods must not be empty.");
        this.dn = dn.toString();
        (this.modifications = new ArrayList<Modification>(mods.length)).addAll(Arrays.asList(mods));
    }
    
    public ModifyRequest(final DN dn, final List<Modification> mods) {
        super(null);
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        this.messageID = -1;
        Validator.ensureNotNull(dn, mods);
        Validator.ensureFalse(mods.isEmpty(), "ModifyRequest.mods must not be empty.");
        this.dn = dn.toString();
        this.modifications = new ArrayList<Modification>(mods);
    }
    
    public ModifyRequest(final String dn, final Modification mod, final Control[] controls) {
        super(controls);
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        this.messageID = -1;
        Validator.ensureNotNull(dn, mod);
        this.dn = dn;
        (this.modifications = new ArrayList<Modification>(1)).add(mod);
    }
    
    public ModifyRequest(final String dn, final Modification[] mods, final Control[] controls) {
        super(controls);
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        this.messageID = -1;
        Validator.ensureNotNull(dn, mods);
        Validator.ensureFalse(mods.length == 0, "ModifyRequest.mods must not be empty.");
        this.dn = dn;
        (this.modifications = new ArrayList<Modification>(mods.length)).addAll(Arrays.asList(mods));
    }
    
    public ModifyRequest(final String dn, final List<Modification> mods, final Control[] controls) {
        super(controls);
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        this.messageID = -1;
        Validator.ensureNotNull(dn, mods);
        Validator.ensureFalse(mods.isEmpty(), "ModifyRequest.mods must not be empty.");
        this.dn = dn;
        this.modifications = new ArrayList<Modification>(mods);
    }
    
    public ModifyRequest(final DN dn, final Modification mod, final Control[] controls) {
        super(controls);
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        this.messageID = -1;
        Validator.ensureNotNull(dn, mod);
        this.dn = dn.toString();
        (this.modifications = new ArrayList<Modification>(1)).add(mod);
    }
    
    public ModifyRequest(final DN dn, final Modification[] mods, final Control[] controls) {
        super(controls);
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        this.messageID = -1;
        Validator.ensureNotNull(dn, mods);
        Validator.ensureFalse(mods.length == 0, "ModifyRequest.mods must not be empty.");
        this.dn = dn.toString();
        (this.modifications = new ArrayList<Modification>(mods.length)).addAll(Arrays.asList(mods));
    }
    
    public ModifyRequest(final DN dn, final List<Modification> mods, final Control[] controls) {
        super(controls);
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        this.messageID = -1;
        Validator.ensureNotNull(dn, mods);
        Validator.ensureFalse(mods.isEmpty(), "ModifyRequest.mods must not be empty.");
        this.dn = dn.toString();
        this.modifications = new ArrayList<Modification>(mods);
    }
    
    public ModifyRequest(final String... ldifModificationLines) throws LDIFException {
        super(null);
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        this.messageID = -1;
        final LDIFChangeRecord changeRecord = LDIFReader.decodeChangeRecord(ldifModificationLines);
        if (!(changeRecord instanceof LDIFModifyChangeRecord)) {
            throw new LDIFException(LDAPMessages.ERR_MODIFY_INVALID_LDIF.get(), 0L, false, ldifModificationLines, null);
        }
        final LDIFModifyChangeRecord modifyRecord = (LDIFModifyChangeRecord)changeRecord;
        final ModifyRequest r = modifyRecord.toModifyRequest();
        this.dn = r.dn;
        this.modifications = r.modifications;
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
    public List<Modification> getModifications() {
        return Collections.unmodifiableList((List<? extends Modification>)this.modifications);
    }
    
    public void addModification(final Modification mod) {
        Validator.ensureNotNull(mod);
        this.modifications.add(mod);
    }
    
    public boolean removeModification(final Modification mod) {
        Validator.ensureNotNull(mod);
        return this.modifications.remove(mod);
    }
    
    public void setModifications(final Modification mod) {
        Validator.ensureNotNull(mod);
        this.modifications.clear();
        this.modifications.add(mod);
    }
    
    public void setModifications(final Modification[] mods) {
        Validator.ensureNotNull(mods);
        Validator.ensureFalse(mods.length == 0, "ModifyRequest.setModifications.mods must not be empty.");
        this.modifications.clear();
        this.modifications.addAll(Arrays.asList(mods));
    }
    
    public void setModifications(final List<Modification> mods) {
        Validator.ensureNotNull(mods);
        Validator.ensureFalse(mods.isEmpty(), "ModifyRequest.setModifications.mods must not be empty.");
        this.modifications.clear();
        this.modifications.addAll(mods);
    }
    
    @Override
    public byte getProtocolOpType() {
        return 102;
    }
    
    @Override
    public void writeTo(final ASN1Buffer writer) {
        final ASN1BufferSequence requestSequence = writer.beginSequence((byte)102);
        writer.addOctetString(this.dn);
        final ASN1BufferSequence modSequence = writer.beginSequence();
        for (final Modification m : this.modifications) {
            m.writeTo(writer);
        }
        modSequence.end();
        requestSequence.end();
    }
    
    @Override
    public ASN1Element encodeProtocolOp() {
        final ASN1Element[] modElements = new ASN1Element[this.modifications.size()];
        for (int i = 0; i < modElements.length; ++i) {
            modElements[i] = this.modifications.get(i).encode();
        }
        final ASN1Element[] protocolOpElements = { new ASN1OctetString(this.dn), new ASN1Sequence(modElements) };
        return new ASN1Sequence((byte)102, protocolOpElements);
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
                throw new LDAPException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_MODIFY_INTERRUPTED.get(connection.getHostPort()), ie);
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
            final AsyncHelper helper = new AsyncHelper(connection, OperationType.MODIFY, this.messageID, resultListener, this.getIntermediateResponseListener());
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
            connection.getConnectionStatistics().incrementNumModifyRequests();
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
        connection.getConnectionStatistics().incrementNumModifyRequests();
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
            throw new LDAPException(ResultCode.TIMEOUT, LDAPMessages.ERR_MODIFY_CLIENT_TIMEOUT.get(waitTime, this.messageID, this.dn, connection.getHostPort()));
        }
        connection.getConnectionStatistics().incrementNumModifyResponses(System.nanoTime() - requestTime);
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
                throw new LDAPException(ccr.getResultCode(), LDAPMessages.ERR_CONN_CLOSED_WAITING_FOR_MODIFY_RESPONSE.get(connection.getHostPort(), this.toString()));
            }
            throw new LDAPException(ccr.getResultCode(), LDAPMessages.ERR_CONN_CLOSED_WAITING_FOR_MODIFY_RESPONSE_WITH_MESSAGE.get(connection.getHostPort(), this.toString(), message));
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
                    ModifyRequest modifyRequest;
                    if (referralURL.baseDNProvided()) {
                        modifyRequest = new ModifyRequest(referralURL.getBaseDN(), this.modifications, this.getControls());
                    }
                    else {
                        modifyRequest = this;
                    }
                    final LDAPConnection referralConn = this.getReferralConnector(connection).getReferralConnection(referralURL, connection);
                    try {
                        return modifyRequest.process(referralConn, depth + 1);
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
        return OperationType.MODIFY;
    }
    
    @Override
    public ModifyRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public ModifyRequest duplicate(final Control[] controls) {
        final ModifyRequest r = new ModifyRequest(this.dn, new ArrayList<Modification>(this.modifications), controls);
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
    public LDIFModifyChangeRecord toLDIFChangeRecord() {
        return new LDIFModifyChangeRecord(this);
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
        buffer.append("ModifyRequest(dn='");
        buffer.append(this.dn);
        buffer.append("', mods={");
        for (int i = 0; i < this.modifications.size(); ++i) {
            final Modification m = this.modifications.get(i);
            if (i > 0) {
                buffer.append(", ");
            }
            switch (m.getModificationType().intValue()) {
                case 0: {
                    buffer.append("ADD ");
                    break;
                }
                case 1: {
                    buffer.append("DELETE ");
                    break;
                }
                case 2: {
                    buffer.append("REPLACE ");
                    break;
                }
                case 3: {
                    buffer.append("INCREMENT ");
                    break;
                }
            }
            buffer.append(m.getAttributeName());
        }
        buffer.append('}');
        final Control[] controls = this.getControls();
        if (controls.length > 0) {
            buffer.append(", controls={");
            for (int j = 0; j < controls.length; ++j) {
                if (j > 0) {
                    buffer.append(", ");
                }
                buffer.append(controls[j]);
            }
            buffer.append('}');
        }
        buffer.append(')');
    }
    
    @Override
    public void toCode(final List<String> lineList, final String requestID, final int indentSpaces, final boolean includeProcessing) {
        final ArrayList<ToCodeArgHelper> constructorArgs = new ArrayList<ToCodeArgHelper>(this.modifications.size() + 1);
        constructorArgs.add(ToCodeArgHelper.createString(this.dn, "Entry DN"));
        boolean firstMod = true;
        for (final Modification m : this.modifications) {
            String comment;
            if (firstMod) {
                firstMod = false;
                comment = "Modifications";
            }
            else {
                comment = null;
            }
            constructorArgs.add(ToCodeArgHelper.createModification(m, comment));
        }
        ToCodeHelper.generateMethodCall(lineList, indentSpaces, "ModifyRequest", requestID + "Request", "new ModifyRequest", constructorArgs);
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
            lineList.add(indent + "  LDAPResult " + requestID + "Result = connection.modify(" + requestID + "Request);");
            lineList.add(indent + "  // The modify was processed successfully.");
            lineList.add(indent + '}');
            lineList.add(indent + "catch (LDAPException e)");
            lineList.add(indent + '{');
            lineList.add(indent + "  // The modify failed.  Maybe the following " + "will help explain why.");
            lineList.add(indent + "  ResultCode resultCode = e.getResultCode();");
            lineList.add(indent + "  String message = e.getMessage();");
            lineList.add(indent + "  String matchedDN = e.getMatchedDN();");
            lineList.add(indent + "  String[] referralURLs = e.getReferralURLs();");
            lineList.add(indent + "  Control[] responseControls = " + "e.getResponseControls();");
            lineList.add(indent + '}');
        }
    }
}
