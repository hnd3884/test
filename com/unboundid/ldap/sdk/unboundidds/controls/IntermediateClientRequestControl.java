package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class IntermediateClientRequestControl extends Control
{
    public static final String INTERMEDIATE_CLIENT_REQUEST_OID = "1.3.6.1.4.1.30221.2.5.2";
    private static final long serialVersionUID = 4883725840393001578L;
    private final IntermediateClientRequestValue value;
    
    public IntermediateClientRequestControl(final IntermediateClientRequestValue downstreamRequest, final String downstreamClientAddress, final Boolean downstreamClientSecure, final String clientIdentity, final String clientName, final String clientSessionID, final String clientRequestID) {
        this(true, new IntermediateClientRequestValue(downstreamRequest, downstreamClientAddress, downstreamClientSecure, clientIdentity, clientName, clientSessionID, clientRequestID));
    }
    
    public IntermediateClientRequestControl(final IntermediateClientRequestValue value) {
        this(true, value);
    }
    
    public IntermediateClientRequestControl(final boolean isCritical, final IntermediateClientRequestValue value) {
        super("1.3.6.1.4.1.30221.2.5.2", isCritical, new ASN1OctetString(value.encode().encode()));
        this.value = value;
    }
    
    public IntermediateClientRequestControl(final Control control) throws LDAPException {
        super(control);
        final ASN1OctetString controlValue = control.getValue();
        if (controlValue == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ICREQ_CONTROL_NO_VALUE.get());
        }
        ASN1Sequence valueSequence;
        try {
            final ASN1Element valueElement = ASN1Element.decode(controlValue.getValue());
            valueSequence = ASN1Sequence.decodeAsSequence(valueElement);
        }
        catch (final Exception e) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ICREQ_CONTROL_VALUE_NOT_SEQUENCE.get(e), e);
        }
        this.value = IntermediateClientRequestValue.decode(valueSequence);
    }
    
    public IntermediateClientRequestValue getRequestValue() {
        return this.value;
    }
    
    public IntermediateClientRequestValue getDownstreamRequest() {
        return this.value.getDownstreamRequest();
    }
    
    public String getClientIdentity() {
        return this.value.getClientIdentity();
    }
    
    public String getDownstreamClientAddress() {
        return this.value.getDownstreamClientAddress();
    }
    
    public Boolean downstreamClientSecure() {
        return this.value.downstreamClientSecure();
    }
    
    public String getClientName() {
        return this.value.getClientName();
    }
    
    public String getClientSessionID() {
        return this.value.getClientSessionID();
    }
    
    public String getClientRequestID() {
        return this.value.getClientRequestID();
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_INTERMEDIATE_CLIENT_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("IntermediateClientRequestControl(isCritical=");
        buffer.append(this.isCritical());
        buffer.append(", value=");
        this.value.toString(buffer);
        buffer.append(')');
    }
}
