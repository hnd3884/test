package com.unboundid.ldap.sdk.extensions;

import com.unboundid.ldap.sdk.LDAPRequest;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Integer;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.AsyncRequestID;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedRequest;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class CancelExtendedRequest extends ExtendedRequest
{
    public static final String CANCEL_REQUEST_OID = "1.3.6.1.1.8";
    private static final long serialVersionUID = -7170687636394194183L;
    private final int targetMessageID;
    
    public CancelExtendedRequest(final AsyncRequestID requestID) {
        this(requestID.getMessageID(), null);
    }
    
    public CancelExtendedRequest(final int targetMessageID) {
        this(targetMessageID, null);
    }
    
    public CancelExtendedRequest(final AsyncRequestID requestID, final Control[] controls) {
        this(requestID.getMessageID(), controls);
    }
    
    public CancelExtendedRequest(final int targetMessageID, final Control[] controls) {
        super("1.3.6.1.1.8", encodeValue(targetMessageID), controls);
        this.targetMessageID = targetMessageID;
    }
    
    public CancelExtendedRequest(final ExtendedRequest extendedRequest) throws LDAPException {
        super(extendedRequest);
        final ASN1OctetString value = extendedRequest.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_CANCEL_REQUEST_NO_VALUE.get());
        }
        try {
            final ASN1Element valueElement = ASN1Element.decode(value.getValue());
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(valueElement).elements();
            this.targetMessageID = ASN1Integer.decodeAsInteger(elements[0]).intValue();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_CANCEL_REQUEST_CANNOT_DECODE.get(e), e);
        }
    }
    
    private static ASN1OctetString encodeValue(final int targetMessageID) {
        final ASN1Element[] sequenceValues = { new ASN1Integer(targetMessageID) };
        return new ASN1OctetString(new ASN1Sequence(sequenceValues).encode());
    }
    
    @Override
    protected ExtendedResult process(final LDAPConnection connection, final int depth) throws LDAPException {
        if (connection.synchronousMode()) {
            throw new LDAPException(ResultCode.NOT_SUPPORTED, ExtOpMessages.ERR_CANCEL_NOT_SUPPORTED_IN_SYNCHRONOUS_MODE.get());
        }
        return super.process(connection, depth);
    }
    
    public int getTargetMessageID() {
        return this.targetMessageID;
    }
    
    @Override
    public CancelExtendedRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public CancelExtendedRequest duplicate(final Control[] controls) {
        final CancelExtendedRequest cancelRequest = new CancelExtendedRequest(this.targetMessageID, controls);
        cancelRequest.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return cancelRequest;
    }
    
    @Override
    public String getExtendedRequestName() {
        return ExtOpMessages.INFO_EXTENDED_REQUEST_NAME_CANCEL.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("CancelExtendedRequest(targetMessageID=");
        buffer.append(this.targetMessageID);
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
}
