package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.asn1.ASN1Element;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.util.Validator;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedResult;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class GenerateTOTPSharedSecretExtendedResult extends ExtendedResult
{
    public static final String GENERATE_TOTP_SHARED_SECRET_RESULT_OID = "1.3.6.1.4.1.30221.2.6.57";
    private static final byte TYPE_TOTP_SHARED_SECRET = Byte.MIN_VALUE;
    private static final long serialVersionUID = 8505040895542971346L;
    private final String totpSharedSecret;
    
    public GenerateTOTPSharedSecretExtendedResult(final int messageID, final String totpSharedSecret, final Control... responseControls) {
        this(messageID, ResultCode.SUCCESS, null, null, null, totpSharedSecret, responseControls);
    }
    
    public GenerateTOTPSharedSecretExtendedResult(final int messageID, final ResultCode resultCode, final String diagnosticMessage, final String matchedDN, final String[] referralURLs, final String totpSharedSecret, final Control... responseControls) {
        super(messageID, resultCode, diagnosticMessage, matchedDN, referralURLs, (totpSharedSecret == null) ? null : "1.3.6.1.4.1.30221.2.6.57", encodeValue(totpSharedSecret), responseControls);
        this.totpSharedSecret = totpSharedSecret;
        if (totpSharedSecret == null) {
            Validator.ensureTrue(resultCode != ResultCode.SUCCESS, "If the result code is SUCCESS, the TOTP shared secret must be non-null");
        }
    }
    
    public GenerateTOTPSharedSecretExtendedResult(final ExtendedResult extendedResult) throws LDAPException {
        super(extendedResult);
        final ASN1OctetString value = extendedResult.getValue();
        if (value == null) {
            this.totpSharedSecret = null;
        }
        else {
            try {
                final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(value.getValue()).elements();
                this.totpSharedSecret = ASN1OctetString.decodeAsOctetString(elements[0]).stringValue();
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GEN_TOTP_SECRET_RESULT_ERROR_DECODING_VALUE.get(StaticUtils.getExceptionMessage(e)));
            }
        }
    }
    
    private static ASN1OctetString encodeValue(final String totpSharedSecret) {
        if (totpSharedSecret == null) {
            return null;
        }
        return new ASN1OctetString(new ASN1Sequence(new ASN1Element[] { new ASN1OctetString((byte)(-128), totpSharedSecret) }).encode());
    }
    
    public String getTOTPSharedSecret() {
        return this.totpSharedSecret;
    }
    
    @Override
    public String getExtendedResultName() {
        return ExtOpMessages.INFO_GEN_TOTP_SECRET_RESULT_NAME.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("GenerateTOTPSharedSecretExtendedResult(resultCode=");
        buffer.append(this.getResultCode());
        final int messageID = this.getMessageID();
        if (messageID >= 0) {
            buffer.append(", messageID=");
            buffer.append(messageID);
        }
        final String diagnosticMessage = this.getDiagnosticMessage();
        if (diagnosticMessage != null) {
            buffer.append(", diagnosticMessage='");
            buffer.append(diagnosticMessage);
            buffer.append('\'');
        }
        final String matchedDN = this.getMatchedDN();
        if (matchedDN != null) {
            buffer.append(", matchedDN='");
            buffer.append(matchedDN);
            buffer.append('\'');
        }
        final String[] referralURLs = this.getReferralURLs();
        if (referralURLs.length > 0) {
            buffer.append(", referralURLs={");
            for (int i = 0; i < referralURLs.length; ++i) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append('\'');
                buffer.append(referralURLs[i]);
                buffer.append('\'');
            }
            buffer.append('}');
        }
        final Control[] responseControls = this.getResponseControls();
        if (responseControls.length > 0) {
            buffer.append(", responseControls={");
            for (int j = 0; j < responseControls.length; ++j) {
                if (j > 0) {
                    buffer.append(", ");
                }
                buffer.append(responseControls[j]);
            }
            buffer.append('}');
        }
        buffer.append(')');
    }
}
