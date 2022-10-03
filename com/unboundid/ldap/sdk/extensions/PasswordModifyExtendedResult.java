package com.unboundid.ldap.sdk.extensions;

import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedResult;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class PasswordModifyExtendedResult extends ExtendedResult
{
    private static final long serialVersionUID = -160274020063799410L;
    private final ASN1OctetString generatedPassword;
    
    public PasswordModifyExtendedResult(final ExtendedResult extendedResult) throws LDAPException {
        super(extendedResult);
        final ASN1OctetString value = extendedResult.getValue();
        if (value == null) {
            this.generatedPassword = null;
            return;
        }
        ASN1Element[] elements;
        try {
            final ASN1Element valueElement = ASN1Element.decode(value.getValue());
            elements = ASN1Sequence.decodeAsSequence(valueElement).elements();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_PW_MODIFY_RESPONSE_VALUE_NOT_SEQUENCE.get(e), e);
        }
        if (elements.length == 0) {
            this.generatedPassword = null;
            return;
        }
        if (elements.length != 1) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_PW_MODIFY_RESPONSE_MULTIPLE_ELEMENTS.get());
        }
        this.generatedPassword = ASN1OctetString.decodeAsOctetString(elements[0]);
    }
    
    public PasswordModifyExtendedResult(final int messageID, final ResultCode resultCode, final String diagnosticMessage, final String matchedDN, final String[] referralURLs, final ASN1OctetString generatedPassword, final Control[] responseControls) {
        super(messageID, resultCode, diagnosticMessage, matchedDN, referralURLs, null, encodeValue(generatedPassword), responseControls);
        this.generatedPassword = generatedPassword;
    }
    
    private static ASN1OctetString encodeValue(final ASN1OctetString generatedPassword) {
        if (generatedPassword == null) {
            return null;
        }
        final ASN1Element[] elements = { new ASN1OctetString((byte)(-128), generatedPassword.getValue()) };
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public String getGeneratedPassword() {
        if (this.generatedPassword == null) {
            return null;
        }
        return this.generatedPassword.stringValue();
    }
    
    public byte[] getGeneratedPasswordBytes() {
        if (this.generatedPassword == null) {
            return null;
        }
        return this.generatedPassword.getValue();
    }
    
    public ASN1OctetString getRawGeneratedPassword() {
        return this.generatedPassword;
    }
    
    @Override
    public String getExtendedResultName() {
        return ExtOpMessages.INFO_EXTENDED_RESULT_NAME_PASSWORD_MODIFY.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("PasswordModifyExtendedResult(resultCode=");
        buffer.append(this.getResultCode());
        final int messageID = this.getMessageID();
        if (messageID >= 0) {
            buffer.append(", messageID=");
            buffer.append(messageID);
        }
        if (this.generatedPassword != null) {
            buffer.append(", generatedPassword='");
            buffer.append(this.generatedPassword.stringValue());
            buffer.append('\'');
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
