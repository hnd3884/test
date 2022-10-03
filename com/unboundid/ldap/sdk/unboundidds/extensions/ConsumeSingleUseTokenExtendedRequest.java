package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.ldap.sdk.LDAPRequest;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.util.Validator;
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
public final class ConsumeSingleUseTokenExtendedRequest extends ExtendedRequest
{
    public static final String CONSUME_SINGLE_USE_TOKEN_REQUEST_OID = "1.3.6.1.4.1.30221.2.6.51";
    private static final long serialVersionUID = -3162206445662323272L;
    private final String tokenID;
    private final String tokenValue;
    private final String userDN;
    
    public ConsumeSingleUseTokenExtendedRequest(final String userDN, final String tokenID, final String tokenValue, final Control... controls) {
        super("1.3.6.1.4.1.30221.2.6.51", encodeValue(userDN, tokenID, tokenValue), controls);
        this.userDN = userDN;
        this.tokenID = tokenID;
        this.tokenValue = tokenValue;
    }
    
    public ConsumeSingleUseTokenExtendedRequest(final ExtendedRequest request) throws LDAPException {
        super(request);
        final ASN1OctetString value = request.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_CONSUME_SINGLE_USE_TOKEN_REQUEST_NO_VALUE.get());
        }
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(value.getValue()).elements();
            this.userDN = ASN1OctetString.decodeAsOctetString(elements[0]).stringValue();
            this.tokenID = ASN1OctetString.decodeAsOctetString(elements[1]).stringValue();
            this.tokenValue = ASN1OctetString.decodeAsOctetString(elements[2]).stringValue();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_CONSUME_SINGLE_USE_TOKEN_REQUEST_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    private static ASN1OctetString encodeValue(final String userDN, final String tokenID, final String tokenValue) {
        Validator.ensureNotNull(userDN);
        Validator.ensureNotNull(tokenID);
        Validator.ensureNotNull(tokenValue);
        final ASN1Sequence valueSequence = new ASN1Sequence(new ASN1Element[] { new ASN1OctetString(userDN), new ASN1OctetString(tokenID), new ASN1OctetString(tokenValue) });
        return new ASN1OctetString(valueSequence.encode());
    }
    
    public String getUserDN() {
        return this.userDN;
    }
    
    public String getTokenID() {
        return this.tokenID;
    }
    
    public String getTokenValue() {
        return this.tokenValue;
    }
    
    @Override
    public ConsumeSingleUseTokenExtendedRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public ConsumeSingleUseTokenExtendedRequest duplicate(final Control[] controls) {
        final ConsumeSingleUseTokenExtendedRequest r = new ConsumeSingleUseTokenExtendedRequest(this.userDN, this.tokenID, this.tokenValue, controls);
        r.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return r;
    }
    
    @Override
    public String getExtendedRequestName() {
        return ExtOpMessages.INFO_EXTENDED_REQUEST_NAME_CONSUME_SINGLE_USE_TOKEN.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("ConsumeSingleUseTokenExtendedRequest(userDN='");
        buffer.append(this.userDN);
        buffer.append("', tokenID='");
        buffer.append(this.tokenID);
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
