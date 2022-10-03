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
import com.unboundid.asn1.ASN1BufferSequence;
import com.unboundid.asn1.ASN1Buffer;
import com.unboundid.ldap.matchingrules.MatchingRule;
import java.util.Iterator;
import java.util.Collections;
import com.unboundid.ldif.LDIFChangeRecord;
import java.util.List;
import com.unboundid.ldif.LDIFException;
import com.unboundid.ldif.LDIFAddChangeRecord;
import com.unboundid.ldif.LDIFReader;
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
public final class AddRequest extends UpdatableLDAPRequest implements ReadOnlyAddRequest, ResponseAcceptor, ProtocolOp
{
    private static final long serialVersionUID = 1320730292848237219L;
    private final LinkedBlockingQueue<LDAPResponse> responseQueue;
    private ArrayList<Attribute> attributes;
    private int messageID;
    private String dn;
    
    public AddRequest(final String dn, final Attribute... attributes) {
        super(null);
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        this.messageID = -1;
        Validator.ensureNotNull(dn, attributes);
        this.dn = dn;
        (this.attributes = new ArrayList<Attribute>(attributes.length)).addAll(Arrays.asList(attributes));
    }
    
    public AddRequest(final String dn, final Attribute[] attributes, final Control[] controls) {
        super(controls);
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        this.messageID = -1;
        Validator.ensureNotNull(dn, attributes);
        this.dn = dn;
        (this.attributes = new ArrayList<Attribute>(attributes.length)).addAll(Arrays.asList(attributes));
    }
    
    public AddRequest(final String dn, final Collection<Attribute> attributes) {
        super(null);
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        this.messageID = -1;
        Validator.ensureNotNull(dn, attributes);
        this.dn = dn;
        this.attributes = new ArrayList<Attribute>(attributes);
    }
    
    public AddRequest(final String dn, final Collection<Attribute> attributes, final Control[] controls) {
        super(controls);
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        this.messageID = -1;
        Validator.ensureNotNull(dn, attributes);
        this.dn = dn;
        this.attributes = new ArrayList<Attribute>(attributes);
    }
    
    public AddRequest(final DN dn, final Attribute... attributes) {
        super(null);
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        this.messageID = -1;
        Validator.ensureNotNull(dn, attributes);
        this.dn = dn.toString();
        (this.attributes = new ArrayList<Attribute>(attributes.length)).addAll(Arrays.asList(attributes));
    }
    
    public AddRequest(final DN dn, final Attribute[] attributes, final Control[] controls) {
        super(controls);
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        this.messageID = -1;
        Validator.ensureNotNull(dn, attributes);
        this.dn = dn.toString();
        (this.attributes = new ArrayList<Attribute>(attributes.length)).addAll(Arrays.asList(attributes));
    }
    
    public AddRequest(final DN dn, final Collection<Attribute> attributes) {
        super(null);
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        this.messageID = -1;
        Validator.ensureNotNull(dn, attributes);
        this.dn = dn.toString();
        this.attributes = new ArrayList<Attribute>(attributes);
    }
    
    public AddRequest(final DN dn, final Collection<Attribute> attributes, final Control[] controls) {
        super(controls);
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        this.messageID = -1;
        Validator.ensureNotNull(dn, attributes);
        this.dn = dn.toString();
        this.attributes = new ArrayList<Attribute>(attributes);
    }
    
    public AddRequest(final Entry entry) {
        super(null);
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        this.messageID = -1;
        Validator.ensureNotNull(entry);
        this.dn = entry.getDN();
        this.attributes = new ArrayList<Attribute>(entry.getAttributes());
    }
    
    public AddRequest(final Entry entry, final Control[] controls) {
        super(controls);
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        this.messageID = -1;
        Validator.ensureNotNull(entry);
        this.dn = entry.getDN();
        this.attributes = new ArrayList<Attribute>(entry.getAttributes());
    }
    
    public AddRequest(final String... ldifLines) throws LDIFException {
        super(null);
        this.responseQueue = new LinkedBlockingQueue<LDAPResponse>();
        this.messageID = -1;
        final LDIFChangeRecord changeRecord = LDIFReader.decodeChangeRecord(true, ldifLines);
        if (changeRecord instanceof LDIFAddChangeRecord) {
            this.dn = changeRecord.getDN();
            this.attributes = new ArrayList<Attribute>(Arrays.asList(((LDIFAddChangeRecord)changeRecord).getAttributes()));
            this.setControls(changeRecord.getControls());
            return;
        }
        throw new LDIFException(LDAPMessages.ERR_ADD_INAPPROPRIATE_CHANGE_TYPE.get(changeRecord.getChangeType().name()), 0L, true, Arrays.asList(ldifLines), null);
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
    public List<Attribute> getAttributes() {
        return Collections.unmodifiableList((List<? extends Attribute>)this.attributes);
    }
    
    @Override
    public Attribute getAttribute(final String attributeName) {
        Validator.ensureNotNull(attributeName);
        for (final Attribute a : this.attributes) {
            if (a.getName().equalsIgnoreCase(attributeName)) {
                return a;
            }
        }
        return null;
    }
    
    @Override
    public boolean hasAttribute(final String attributeName) {
        return this.getAttribute(attributeName) != null;
    }
    
    @Override
    public boolean hasAttribute(final Attribute attribute) {
        Validator.ensureNotNull(attribute);
        final Attribute a = this.getAttribute(attribute.getName());
        return a != null && attribute.equals(a);
    }
    
    @Override
    public boolean hasAttributeValue(final String attributeName, final String attributeValue) {
        Validator.ensureNotNull(attributeName, attributeValue);
        final Attribute a = this.getAttribute(attributeName);
        return a != null && a.hasValue(attributeValue);
    }
    
    @Override
    public boolean hasAttributeValue(final String attributeName, final String attributeValue, final MatchingRule matchingRule) {
        Validator.ensureNotNull(attributeName, attributeValue);
        final Attribute a = this.getAttribute(attributeName);
        return a != null && a.hasValue(attributeValue, matchingRule);
    }
    
    @Override
    public boolean hasAttributeValue(final String attributeName, final byte[] attributeValue) {
        Validator.ensureNotNull(attributeName, attributeValue);
        final Attribute a = this.getAttribute(attributeName);
        return a != null && a.hasValue(attributeValue);
    }
    
    @Override
    public boolean hasAttributeValue(final String attributeName, final byte[] attributeValue, final MatchingRule matchingRule) {
        Validator.ensureNotNull(attributeName, attributeValue);
        final Attribute a = this.getAttribute(attributeName);
        return a != null && a.hasValue(attributeValue, matchingRule);
    }
    
    @Override
    public boolean hasObjectClass(final String objectClassName) {
        return this.hasAttributeValue("objectClass", objectClassName);
    }
    
    @Override
    public Entry toEntry() {
        return new Entry(this.dn, this.attributes);
    }
    
    public void setAttributes(final Attribute[] attributes) {
        Validator.ensureNotNull(attributes);
        this.attributes.clear();
        this.attributes.addAll(Arrays.asList(attributes));
    }
    
    public void setAttributes(final Collection<Attribute> attributes) {
        Validator.ensureNotNull(attributes);
        this.attributes.clear();
        this.attributes.addAll(attributes);
    }
    
    public void addAttribute(final Attribute attribute) {
        Validator.ensureNotNull(attribute);
        for (int i = 0; i < this.attributes.size(); ++i) {
            final Attribute a = this.attributes.get(i);
            if (a.getName().equalsIgnoreCase(attribute.getName())) {
                this.attributes.set(i, Attribute.mergeAttributes(a, attribute));
                return;
            }
        }
        this.attributes.add(attribute);
    }
    
    public void addAttribute(final String name, final String value) {
        Validator.ensureNotNull(name, value);
        this.addAttribute(new Attribute(name, value));
    }
    
    public void addAttribute(final String name, final byte[] value) {
        Validator.ensureNotNull(name, value);
        this.addAttribute(new Attribute(name, value));
    }
    
    public void addAttribute(final String name, final String... values) {
        Validator.ensureNotNull(name, values);
        this.addAttribute(new Attribute(name, values));
    }
    
    public void addAttribute(final String name, final byte[]... values) {
        Validator.ensureNotNull(name, values);
        this.addAttribute(new Attribute(name, values));
    }
    
    public boolean removeAttribute(final String attributeName) {
        Validator.ensureNotNull(attributeName);
        final Iterator<Attribute> iterator = this.attributes.iterator();
        while (iterator.hasNext()) {
            final Attribute a = iterator.next();
            if (a.getName().equalsIgnoreCase(attributeName)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }
    
    public boolean removeAttributeValue(final String name, final String value) {
        Validator.ensureNotNull(name, value);
        int pos = -1;
        for (int i = 0; i < this.attributes.size(); ++i) {
            final Attribute a = this.attributes.get(i);
            if (a.getName().equalsIgnoreCase(name)) {
                pos = i;
                break;
            }
        }
        if (pos < 0) {
            return false;
        }
        final Attribute a2 = this.attributes.get(pos);
        final Attribute newAttr = Attribute.removeValues(a2, new Attribute(name, value));
        if (a2.getRawValues().length == newAttr.getRawValues().length) {
            return false;
        }
        if (newAttr.getRawValues().length == 0) {
            this.attributes.remove(pos);
        }
        else {
            this.attributes.set(pos, newAttr);
        }
        return true;
    }
    
    public boolean removeAttribute(final String name, final byte[] value) {
        Validator.ensureNotNull(name, value);
        int pos = -1;
        for (int i = 0; i < this.attributes.size(); ++i) {
            final Attribute a = this.attributes.get(i);
            if (a.getName().equalsIgnoreCase(name)) {
                pos = i;
                break;
            }
        }
        if (pos < 0) {
            return false;
        }
        final Attribute a2 = this.attributes.get(pos);
        final Attribute newAttr = Attribute.removeValues(a2, new Attribute(name, value));
        if (a2.getRawValues().length == newAttr.getRawValues().length) {
            return false;
        }
        if (newAttr.getRawValues().length == 0) {
            this.attributes.remove(pos);
        }
        else {
            this.attributes.set(pos, newAttr);
        }
        return true;
    }
    
    public void replaceAttribute(final Attribute attribute) {
        Validator.ensureNotNull(attribute);
        for (int i = 0; i < this.attributes.size(); ++i) {
            if (this.attributes.get(i).getName().equalsIgnoreCase(attribute.getName())) {
                this.attributes.set(i, attribute);
                return;
            }
        }
        this.attributes.add(attribute);
    }
    
    public void replaceAttribute(final String name, final String value) {
        Validator.ensureNotNull(name, value);
        for (int i = 0; i < this.attributes.size(); ++i) {
            if (this.attributes.get(i).getName().equalsIgnoreCase(name)) {
                this.attributes.set(i, new Attribute(name, value));
                return;
            }
        }
        this.attributes.add(new Attribute(name, value));
    }
    
    public void replaceAttribute(final String name, final byte[] value) {
        Validator.ensureNotNull(name, value);
        for (int i = 0; i < this.attributes.size(); ++i) {
            if (this.attributes.get(i).getName().equalsIgnoreCase(name)) {
                this.attributes.set(i, new Attribute(name, value));
                return;
            }
        }
        this.attributes.add(new Attribute(name, value));
    }
    
    public void replaceAttribute(final String name, final String... values) {
        Validator.ensureNotNull(name, values);
        for (int i = 0; i < this.attributes.size(); ++i) {
            if (this.attributes.get(i).getName().equalsIgnoreCase(name)) {
                this.attributes.set(i, new Attribute(name, values));
                return;
            }
        }
        this.attributes.add(new Attribute(name, values));
    }
    
    public void replaceAttribute(final String name, final byte[]... values) {
        Validator.ensureNotNull(name, values);
        for (int i = 0; i < this.attributes.size(); ++i) {
            if (this.attributes.get(i).getName().equalsIgnoreCase(name)) {
                this.attributes.set(i, new Attribute(name, values));
                return;
            }
        }
        this.attributes.add(new Attribute(name, values));
    }
    
    @Override
    public byte getProtocolOpType() {
        return 104;
    }
    
    @Override
    public void writeTo(final ASN1Buffer buffer) {
        final ASN1BufferSequence requestSequence = buffer.beginSequence((byte)104);
        buffer.addOctetString(this.dn);
        final ASN1BufferSequence attrSequence = buffer.beginSequence();
        for (final Attribute a : this.attributes) {
            a.writeTo(buffer);
        }
        attrSequence.end();
        requestSequence.end();
    }
    
    @Override
    public ASN1Element encodeProtocolOp() {
        final ASN1Element[] attrElements = new ASN1Element[this.attributes.size()];
        for (int i = 0; i < attrElements.length; ++i) {
            attrElements[i] = this.attributes.get(i).encode();
        }
        final ASN1Element[] addRequestElements = { new ASN1OctetString(this.dn), new ASN1Sequence(attrElements) };
        return new ASN1Sequence((byte)104, addRequestElements);
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
                throw new LDAPException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_ADD_INTERRUPTED.get(connection.getHostPort()), ie);
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
            final AsyncHelper helper = new AsyncHelper(connection, OperationType.ADD, this.messageID, resultListener, this.getIntermediateResponseListener());
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
            connection.getConnectionStatistics().incrementNumAddRequests();
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
        connection.getConnectionStatistics().incrementNumAddRequests();
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
            throw new LDAPException(ResultCode.TIMEOUT, LDAPMessages.ERR_ADD_CLIENT_TIMEOUT.get(waitTime, this.messageID, this.dn, connection.getHostPort()));
        }
        connection.getConnectionStatistics().incrementNumAddResponses(System.nanoTime() - requestTime);
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
                throw new LDAPException(ccr.getResultCode(), LDAPMessages.ERR_CONN_CLOSED_WAITING_FOR_ADD_RESPONSE.get(connection.getHostPort(), this.toString()));
            }
            throw new LDAPException(ccr.getResultCode(), LDAPMessages.ERR_CONN_CLOSED_WAITING_FOR_ADD_RESPONSE_WITH_MESSAGE.get(connection.getHostPort(), this.toString(), message));
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
                    AddRequest addRequest;
                    if (referralURL.baseDNProvided()) {
                        addRequest = new AddRequest(referralURL.getBaseDN(), this.attributes, this.getControls());
                    }
                    else {
                        addRequest = this;
                    }
                    final LDAPConnection referralConn = this.getReferralConnector(connection).getReferralConnection(referralURL, connection);
                    try {
                        return addRequest.process(referralConn, depth + 1);
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
    
    @Override
    public int getLastMessageID() {
        return this.messageID;
    }
    
    @Override
    public OperationType getOperationType() {
        return OperationType.ADD;
    }
    
    @Override
    public AddRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public AddRequest duplicate(final Control[] controls) {
        final ArrayList<Attribute> attrs = new ArrayList<Attribute>(this.attributes);
        final AddRequest r = new AddRequest(this.dn, attrs, controls);
        if (this.followReferralsInternal() != null) {
            r.setFollowReferrals(this.followReferralsInternal());
        }
        if (this.getReferralConnectorInternal() != null) {
            r.setReferralConnector(this.getReferralConnectorInternal());
        }
        r.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return r;
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
    public LDIFAddChangeRecord toLDIFChangeRecord() {
        return new LDIFAddChangeRecord(this);
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
        buffer.append("AddRequest(dn='");
        buffer.append(this.dn);
        buffer.append("', attrs={");
        for (int i = 0; i < this.attributes.size(); ++i) {
            if (i > 0) {
                buffer.append(", ");
            }
            buffer.append(this.attributes.get(i));
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
        final ArrayList<ToCodeArgHelper> constructorArgs = new ArrayList<ToCodeArgHelper>(this.attributes.size() + 1);
        constructorArgs.add(ToCodeArgHelper.createString(this.dn, "Entry DN"));
        boolean firstAttribute = true;
        for (final Attribute a : this.attributes) {
            String comment;
            if (firstAttribute) {
                firstAttribute = false;
                comment = "Entry Attributes";
            }
            else {
                comment = null;
            }
            constructorArgs.add(ToCodeArgHelper.createAttribute(a, comment));
        }
        ToCodeHelper.generateMethodCall(lineList, indentSpaces, "AddRequest", requestID + "Request", "new AddRequest", constructorArgs);
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
            lineList.add(indent + "  LDAPResult " + requestID + "Result = connection.add(" + requestID + "Request);");
            lineList.add(indent + "  // The add was processed successfully.");
            lineList.add(indent + '}');
            lineList.add(indent + "catch (LDAPException e)");
            lineList.add(indent + '{');
            lineList.add(indent + "  // The add failed.  Maybe the following will " + "help explain why.");
            lineList.add(indent + "  ResultCode resultCode = e.getResultCode();");
            lineList.add(indent + "  String message = e.getMessage();");
            lineList.add(indent + "  String matchedDN = e.getMatchedDN();");
            lineList.add(indent + "  String[] referralURLs = e.getReferralURLs();");
            lineList.add(indent + "  Control[] responseControls = " + "e.getResponseControls();");
            lineList.add(indent + '}');
        }
    }
}
