package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.DecodeableControl;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class IntermediateClientResponseControl extends Control implements DecodeableControl
{
    public static final String INTERMEDIATE_CLIENT_RESPONSE_OID = "1.3.6.1.4.1.30221.2.5.2";
    private static final long serialVersionUID = 7476073413872875835L;
    private final IntermediateClientResponseValue value;
    
    IntermediateClientResponseControl() {
        this.value = null;
    }
    
    public IntermediateClientResponseControl(final IntermediateClientResponseValue upstreamResponse, final String upstreamServerAddress, final Boolean upstreamServerSecure, final String serverName, final String serverSessionID, final String serverResponseID) {
        this(false, new IntermediateClientResponseValue(upstreamResponse, upstreamServerAddress, upstreamServerSecure, serverName, serverSessionID, serverResponseID));
    }
    
    public IntermediateClientResponseControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        super(oid, isCritical, value);
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ICRESP_CONTROL_NO_VALUE.get());
        }
        ASN1Sequence valueSequence;
        try {
            final ASN1Element valueElement = ASN1Element.decode(value.getValue());
            valueSequence = ASN1Sequence.decodeAsSequence(valueElement);
        }
        catch (final Exception e) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ICRESP_CONTROL_VALUE_NOT_SEQUENCE.get(StaticUtils.getExceptionMessage(e)), e);
        }
        this.value = IntermediateClientResponseValue.decode(valueSequence);
    }
    
    public IntermediateClientResponseControl(final IntermediateClientResponseValue value) {
        this(false, value);
    }
    
    public IntermediateClientResponseControl(final boolean isCritical, final IntermediateClientResponseValue value) {
        super("1.3.6.1.4.1.30221.2.5.2", isCritical, new ASN1OctetString(value.encode().encode()));
        this.value = value;
    }
    
    @Override
    public IntermediateClientResponseControl decodeControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        return new IntermediateClientResponseControl(oid, isCritical, value);
    }
    
    public static IntermediateClientResponseControl get(final LDAPResult result) throws LDAPException {
        final Control c = result.getResponseControl("1.3.6.1.4.1.30221.2.5.2");
        if (c == null) {
            return null;
        }
        if (c instanceof IntermediateClientResponseControl) {
            return (IntermediateClientResponseControl)c;
        }
        return new IntermediateClientResponseControl(c.getOID(), c.isCritical(), c.getValue());
    }
    
    public IntermediateClientResponseValue getResponseValue() {
        return this.value;
    }
    
    public IntermediateClientResponseValue getUpstreamResponse() {
        return this.value.getUpstreamResponse();
    }
    
    public String getUpstreamServerAddress() {
        return this.value.getUpstreamServerAddress();
    }
    
    public Boolean upstreamServerSecure() {
        return this.value.upstreamServerSecure();
    }
    
    public String getServerName() {
        return this.value.getServerName();
    }
    
    public String getServerSessionID() {
        return this.value.getServerSessionID();
    }
    
    public String getServerResponseID() {
        return this.value.getServerResponseID();
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_INTERMEDIATE_CLIENT_RESPONSE.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("IntermediateClientResponseControl(isCritical=");
        buffer.append(this.isCritical());
        buffer.append(", value=");
        this.value.toString(buffer);
        buffer.append(')');
    }
}
