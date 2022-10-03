package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.LDAPException;
import java.util.Iterator;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.Validator;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.Control;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedResult;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class GeneratePasswordExtendedResult extends ExtendedResult
{
    public static final String GENERATE_PASSWORD_RESULT_OID = "1.3.6.1.4.1.30221.2.6.63";
    private static final long serialVersionUID = -6840636721723079194L;
    private final List<GeneratedPassword> generatedPasswords;
    private final String passwordPolicyDN;
    
    public GeneratePasswordExtendedResult(final int messageID, final String passwordPolicyDN, final List<GeneratedPassword> generatedPasswords, final Control... controls) {
        this(messageID, ResultCode.SUCCESS, null, null, null, passwordPolicyDN, generatedPasswords, controls);
    }
    
    public GeneratePasswordExtendedResult(final int messageID, final ResultCode resultCode, final String diagnosticMessage, final String matchedDN, final String[] referralURLs, final String passwordPolicyDN, final List<GeneratedPassword> generatedPasswords, final Control... controls) {
        super(messageID, resultCode, diagnosticMessage, matchedDN, referralURLs, (resultCode == ResultCode.SUCCESS) ? "1.3.6.1.4.1.30221.2.6.63" : null, (resultCode == ResultCode.SUCCESS) ? encodeValue(passwordPolicyDN, generatedPasswords) : null, controls);
        this.passwordPolicyDN = passwordPolicyDN;
        if (resultCode == ResultCode.SUCCESS) {
            this.generatedPasswords = Collections.unmodifiableList((List<? extends GeneratedPassword>)new ArrayList<GeneratedPassword>(generatedPasswords));
        }
        else {
            Validator.ensureTrue(passwordPolicyDN == null, "GeneratePasswordExtendedResult.passwordPolicyDN must be null for a non-success result.");
            Validator.ensureTrue(generatedPasswords == null || generatedPasswords.isEmpty(), "GeneratePasswordExtendedResult.generatedPasswords must be null or empty for a non-success result.");
            this.generatedPasswords = Collections.emptyList();
        }
    }
    
    private static ASN1OctetString encodeValue(final String passwordPolicyDN, final List<GeneratedPassword> generatedPasswords) {
        Validator.ensureNotNullOrEmpty(passwordPolicyDN, "GeneratePasswordExtendedResult.passwordPolicyDN must not be null or empty in a success result.");
        Validator.ensureNotNullOrEmpty(generatedPasswords, "GeneratePasswordExtendedResult.generatedPasswords must not be null or empty in a success result.");
        final List<ASN1Element> passwordElements = new ArrayList<ASN1Element>(generatedPasswords.size());
        for (final GeneratedPassword p : generatedPasswords) {
            passwordElements.add(p.encode());
        }
        final ASN1Sequence valueSequence = new ASN1Sequence(new ASN1Element[] { new ASN1OctetString(passwordPolicyDN), new ASN1Sequence(passwordElements) });
        return new ASN1OctetString(valueSequence.encode());
    }
    
    public GeneratePasswordExtendedResult(final ExtendedResult extendedResult) throws LDAPException {
        super(extendedResult);
        final ASN1OctetString value = extendedResult.getValue();
        if (value == null) {
            if (extendedResult.getResultCode() == ResultCode.SUCCESS) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GENERATE_PASSWORD_RESULT_SUCCESS_MISSING_VALUE.get());
            }
            this.passwordPolicyDN = null;
            this.generatedPasswords = Collections.emptyList();
        }
        else {
            if (extendedResult.getResultCode() != ResultCode.SUCCESS) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GENERATE_PASSWORD_RESULT_NON_SUCCESS_WITH_VALUE.get(String.valueOf(extendedResult.getResultCode())));
            }
            try {
                final ASN1Element[] valueElements = ASN1Sequence.decodeAsSequence(value.getValue()).elements();
                this.passwordPolicyDN = ASN1OctetString.decodeAsOctetString(valueElements[0]).stringValue();
                final ASN1Element[] pwElements = ASN1Sequence.decodeAsSequence(valueElements[1]).elements();
                final List<GeneratedPassword> pwList = new ArrayList<GeneratedPassword>(pwElements.length);
                for (final ASN1Element e : pwElements) {
                    pwList.add(GeneratedPassword.decode(e));
                }
                if (pwList.isEmpty()) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GENERATE_PASSWORD_RESULT_DECODE_NO_PASSWORDS.get());
                }
                this.generatedPasswords = Collections.unmodifiableList((List<? extends GeneratedPassword>)pwList);
            }
            catch (final LDAPException e2) {
                Debug.debugException(e2);
                throw e2;
            }
            catch (final Exception e3) {
                Debug.debugException(e3);
                throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GENERATE_PASSWORD_RESULT_DECODING_ERROR.get(StaticUtils.getExceptionMessage(e3)), e3);
            }
        }
    }
    
    public String getPasswordPolicyDN() {
        return this.passwordPolicyDN;
    }
    
    public List<GeneratedPassword> getGeneratedPasswords() {
        return this.generatedPasswords;
    }
    
    @Override
    public String getExtendedResultName() {
        return ExtOpMessages.INFO_GENERATE_PASSWORD_RESULT_NAME.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("GeneratePasswordExtendedResult(resultCode=");
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
        if (this.passwordPolicyDN != null) {
            buffer.append(", passwordPolicyDN='");
            buffer.append(this.passwordPolicyDN);
            buffer.append('\'');
        }
        if (!this.generatedPasswords.isEmpty()) {
            buffer.append(", generatedPasswords={ ");
            final Iterator<GeneratedPassword> iterator = this.generatedPasswords.iterator();
            while (iterator.hasNext()) {
                iterator.next().toString(buffer);
                if (iterator.hasNext()) {
                    buffer.append(", ");
                }
            }
            buffer.append(" }");
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
