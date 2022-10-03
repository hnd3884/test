package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.ldap.sdk.LDAPRequest;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedRequest;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class GetSupportedOTPDeliveryMechanismsExtendedRequest extends ExtendedRequest
{
    public static final String GET_SUPPORTED_OTP_DELIVERY_MECHANISMS_REQUEST_OID = "1.3.6.1.4.1.30221.2.6.47";
    private static final byte TYPE_USER_DN = Byte.MIN_VALUE;
    private static final long serialVersionUID = -1670631089524097883L;
    private final String userDN;
    
    public GetSupportedOTPDeliveryMechanismsExtendedRequest(final String userDN, final Control... controls) {
        super("1.3.6.1.4.1.30221.2.6.47", encodeValue(userDN), controls);
        this.userDN = userDN;
    }
    
    public GetSupportedOTPDeliveryMechanismsExtendedRequest(final ExtendedRequest request) throws LDAPException {
        super(request);
        final ASN1OctetString value = request.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GET_SUPPORTED_OTP_MECH_REQUEST_NO_VALUE.get());
        }
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(value.getValue()).elements();
            this.userDN = ASN1OctetString.decodeAsOctetString(elements[0]).stringValue();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GET_SUPPORTED_OTP_MECH_REQUEST_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    private static ASN1OctetString encodeValue(final String userDN) {
        return new ASN1OctetString(new ASN1Sequence(new ASN1Element[] { new ASN1OctetString((byte)(-128), userDN) }).encode());
    }
    
    public String getUserDN() {
        return this.userDN;
    }
    
    public GetSupportedOTPDeliveryMechanismsExtendedResult process(final LDAPConnection connection, final int depth) throws LDAPException {
        final ExtendedResult extendedResponse = super.process(connection, depth);
        return new GetSupportedOTPDeliveryMechanismsExtendedResult(extendedResponse);
    }
    
    @Override
    public GetSupportedOTPDeliveryMechanismsExtendedRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public GetSupportedOTPDeliveryMechanismsExtendedRequest duplicate(final Control[] controls) {
        final GetSupportedOTPDeliveryMechanismsExtendedRequest r = new GetSupportedOTPDeliveryMechanismsExtendedRequest(this.userDN, controls);
        r.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return r;
    }
    
    @Override
    public String getExtendedRequestName() {
        return ExtOpMessages.INFO_GET_SUPPORTED_OTP_MECH_REQ_NAME.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("GetSupportedOTPDeliveryMechanismsExtendedRequest(userDN='");
        buffer.append(this.userDN);
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
}
