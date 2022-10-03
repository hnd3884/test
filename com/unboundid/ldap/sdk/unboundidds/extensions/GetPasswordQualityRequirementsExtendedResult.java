package com.unboundid.ldap.sdk.unboundidds.extensions;

import java.util.Iterator;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Integer;
import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.asn1.ASN1Sequence;
import java.util.ArrayList;
import java.util.Collections;
import com.unboundid.ldap.sdk.Control;
import java.util.Collection;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedResult;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class GetPasswordQualityRequirementsExtendedResult extends ExtendedResult
{
    public static final String OID_GET_PASSWORD_QUALITY_REQUIREMENTS_RESULT = "1.3.6.1.4.1.30221.2.6.44";
    private static final byte TYPE_CURRENT_PW_REQUIRED = Byte.MIN_VALUE;
    private static final byte TYPE_MUST_CHANGE_PW = -127;
    private static final byte TYPE_SECONDS_UNTIL_EXPIRATION = -126;
    private static final long serialVersionUID = -4990045432443188148L;
    private final Boolean currentPasswordRequired;
    private final Boolean mustChangePassword;
    private final Integer secondsUntilExpiration;
    private final List<PasswordQualityRequirement> passwordRequirements;
    
    public GetPasswordQualityRequirementsExtendedResult(final int messageID, final ResultCode resultCode, final String diagnosticMessage, final String matchedDN, final String[] referralURLs, final Collection<PasswordQualityRequirement> passwordRequirements, final Boolean currentPasswordRequired, final Boolean mustChangePassword, final Integer secondsUntilExpiration, final Control... controls) {
        super(messageID, resultCode, diagnosticMessage, matchedDN, referralURLs, (resultCode == ResultCode.SUCCESS) ? "1.3.6.1.4.1.30221.2.6.44" : null, encodeValue(resultCode, passwordRequirements, currentPasswordRequired, mustChangePassword, secondsUntilExpiration), controls);
        if (passwordRequirements == null || passwordRequirements.isEmpty()) {
            this.passwordRequirements = Collections.emptyList();
        }
        else {
            this.passwordRequirements = Collections.unmodifiableList((List<? extends PasswordQualityRequirement>)new ArrayList<PasswordQualityRequirement>(passwordRequirements));
        }
        this.currentPasswordRequired = currentPasswordRequired;
        this.mustChangePassword = mustChangePassword;
        this.secondsUntilExpiration = secondsUntilExpiration;
    }
    
    public GetPasswordQualityRequirementsExtendedResult(final ExtendedResult r) throws LDAPException {
        super(r);
        final ASN1OctetString value = r.getValue();
        if (value == null) {
            this.passwordRequirements = Collections.emptyList();
            this.currentPasswordRequired = null;
            this.mustChangePassword = null;
            this.secondsUntilExpiration = null;
            return;
        }
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(value.getValue()).elements();
            final ASN1Element[] requirementElements = ASN1Sequence.decodeAsSequence(elements[0]).elements();
            final ArrayList<PasswordQualityRequirement> requirementList = new ArrayList<PasswordQualityRequirement>(requirementElements.length);
            for (final ASN1Element e : requirementElements) {
                requirementList.add(PasswordQualityRequirement.decode(e));
            }
            this.passwordRequirements = Collections.unmodifiableList((List<? extends PasswordQualityRequirement>)requirementList);
            Boolean cpr = null;
            Boolean mcp = null;
            Integer sue = null;
            for (int i = 1; i < elements.length; ++i) {
                switch (elements[i].getType()) {
                    case Byte.MIN_VALUE: {
                        cpr = ASN1Boolean.decodeAsBoolean(elements[i]).booleanValue();
                        break;
                    }
                    case -127: {
                        mcp = ASN1Boolean.decodeAsBoolean(elements[i]).booleanValue();
                        break;
                    }
                    case -126: {
                        sue = ASN1Integer.decodeAsInteger(elements[i]).intValue();
                        break;
                    }
                }
            }
            this.currentPasswordRequired = cpr;
            this.mustChangePassword = mcp;
            this.secondsUntilExpiration = sue;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GET_PW_QUALITY_REQS_RESULT_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    private static ASN1OctetString encodeValue(final ResultCode resultCode, final Collection<PasswordQualityRequirement> passwordRequirements, final Boolean currentPasswordRequired, final Boolean mustChangePassword, final Integer secondsUntilExpiration) {
        if (resultCode != ResultCode.SUCCESS) {
            Validator.ensureTrue(passwordRequirements == null || passwordRequirements.isEmpty());
            Validator.ensureTrue(currentPasswordRequired == null);
            Validator.ensureTrue(mustChangePassword == null);
            Validator.ensureTrue(secondsUntilExpiration == null);
            return null;
        }
        final ArrayList<ASN1Element> valueSequence = new ArrayList<ASN1Element>(4);
        if (passwordRequirements == null) {
            valueSequence.add(new ASN1Sequence());
        }
        else {
            final ArrayList<ASN1Element> requirementElements = new ArrayList<ASN1Element>(passwordRequirements.size());
            for (final PasswordQualityRequirement r : passwordRequirements) {
                requirementElements.add(r.encode());
            }
            valueSequence.add(new ASN1Sequence(requirementElements));
        }
        if (currentPasswordRequired != null) {
            valueSequence.add(new ASN1Boolean((byte)(-128), currentPasswordRequired));
        }
        if (mustChangePassword != null) {
            valueSequence.add(new ASN1Boolean((byte)(-127), mustChangePassword));
        }
        if (secondsUntilExpiration != null) {
            valueSequence.add(new ASN1Integer((byte)(-126), secondsUntilExpiration));
        }
        return new ASN1OctetString(new ASN1Sequence(valueSequence).encode());
    }
    
    public List<PasswordQualityRequirement> getPasswordRequirements() {
        return this.passwordRequirements;
    }
    
    public Boolean getCurrentPasswordRequired() {
        return this.currentPasswordRequired;
    }
    
    public Boolean getMustChangePassword() {
        return this.mustChangePassword;
    }
    
    public Integer getSecondsUntilExpiration() {
        return this.secondsUntilExpiration;
    }
    
    @Override
    public String getExtendedResultName() {
        return ExtOpMessages.INFO_EXTENDED_RESULT_NAME_GET_PW_QUALITY_REQS.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("GetPasswordQualityRequirementsExtendedResult(resultCode=");
        buffer.append(this.getResultCode());
        final int messageID = this.getMessageID();
        if (messageID >= 0) {
            buffer.append(", messageID=");
            buffer.append(messageID);
        }
        buffer.append(", requirements{");
        final Iterator<PasswordQualityRequirement> requirementsIterator = this.passwordRequirements.iterator();
        while (requirementsIterator.hasNext()) {
            requirementsIterator.next().toString(buffer);
            if (requirementsIterator.hasNext()) {
                buffer.append(',');
            }
        }
        buffer.append('}');
        if (this.currentPasswordRequired != null) {
            buffer.append(", currentPasswordRequired=");
            buffer.append(this.currentPasswordRequired);
        }
        if (this.mustChangePassword != null) {
            buffer.append(", mustChangePassword=");
            buffer.append(this.mustChangePassword);
        }
        if (this.secondsUntilExpiration != null) {
            buffer.append(", secondsUntilExpiration=");
            buffer.append(this.secondsUntilExpiration);
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
