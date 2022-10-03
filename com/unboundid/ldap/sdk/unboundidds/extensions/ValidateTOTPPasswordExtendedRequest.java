package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.ldap.sdk.LDAPRequest;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Validator;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedRequest;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ValidateTOTPPasswordExtendedRequest extends ExtendedRequest
{
    public static final String VALIDATE_TOTP_PASSWORD_REQUEST_OID = "1.3.6.1.4.1.30221.2.6.15";
    private static final byte TYPE_USER_DN = Byte.MIN_VALUE;
    private static final byte TYPE_TOTP_PASSWORD = -127;
    private static final long serialVersionUID = -4610279612454559569L;
    private final String userDN;
    private final String totpPassword;
    
    public ValidateTOTPPasswordExtendedRequest(final String userDN, final String totpPassword, final Control... controls) {
        super("1.3.6.1.4.1.30221.2.6.15", encodeValue(userDN, totpPassword), controls);
        Validator.ensureNotNull(userDN);
        Validator.ensureNotNull(totpPassword);
        this.userDN = userDN;
        this.totpPassword = totpPassword;
    }
    
    public ValidateTOTPPasswordExtendedRequest(final ExtendedRequest extendedRequest) throws LDAPException {
        super(extendedRequest);
        final ASN1OctetString value = extendedRequest.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_VALIDATE_TOTP_REQUEST_MISSING_VALUE.get());
        }
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(value.getValue()).elements();
            this.userDN = ASN1OctetString.decodeAsOctetString(elements[0]).stringValue();
            this.totpPassword = ASN1OctetString.decodeAsOctetString(elements[1]).stringValue();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_VALIDATE_TOTP_REQUEST_MALFORMED_VALUE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    private static ASN1OctetString encodeValue(final String userDN, final String totpPassword) {
        return new ASN1OctetString(new ASN1Sequence(new ASN1Element[] { new ASN1OctetString((byte)(-128), userDN), new ASN1OctetString((byte)(-127), totpPassword) }).encode());
    }
    
    public String getUserDN() {
        return this.userDN;
    }
    
    public String getTOTPPassword() {
        return this.totpPassword;
    }
    
    @Override
    public ValidateTOTPPasswordExtendedRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public ValidateTOTPPasswordExtendedRequest duplicate(final Control[] controls) {
        final ValidateTOTPPasswordExtendedRequest r = new ValidateTOTPPasswordExtendedRequest(this.userDN, this.totpPassword, controls);
        r.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return r;
    }
    
    @Override
    public String getExtendedRequestName() {
        return ExtOpMessages.INFO_EXTENDED_REQUEST_NAME_VALIDATE_TOTP.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("ValidateTOTPPasswordExtendedRequest(userDN='");
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
