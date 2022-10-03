package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.Collection;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Boolean;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Validator;
import com.unboundid.ldap.sdk.unboundidds.extensions.PasswordQualityRequirement;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class PasswordQualityRequirementValidationResult implements Serializable
{
    private static final byte TYPE_ADDITIONAL_INFO = Byte.MIN_VALUE;
    private static final long serialVersionUID = -8048878239770726375L;
    private final boolean requirementSatisfied;
    private final PasswordQualityRequirement passwordRequirement;
    private final String additionalInfo;
    
    public PasswordQualityRequirementValidationResult(final PasswordQualityRequirement passwordRequirement, final boolean requirementSatisfied, final String additionalInfo) {
        Validator.ensureNotNull(passwordRequirement);
        this.passwordRequirement = passwordRequirement;
        this.requirementSatisfied = requirementSatisfied;
        this.additionalInfo = additionalInfo;
    }
    
    public PasswordQualityRequirement getPasswordRequirement() {
        return this.passwordRequirement;
    }
    
    public boolean requirementSatisfied() {
        return this.requirementSatisfied;
    }
    
    public String getAdditionalInfo() {
        return this.additionalInfo;
    }
    
    public ASN1Element encode() {
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(3);
        elements.add(this.passwordRequirement.encode());
        elements.add(new ASN1Boolean(this.requirementSatisfied));
        if (this.additionalInfo != null) {
            elements.add(new ASN1OctetString((byte)(-128), this.additionalInfo));
        }
        return new ASN1Sequence(elements);
    }
    
    public static PasswordQualityRequirementValidationResult decode(final ASN1Element element) throws LDAPException {
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(element).elements();
            final PasswordQualityRequirement passwordRequirement = PasswordQualityRequirement.decode(elements[0]);
            final boolean requirementSatisfied = ASN1Boolean.decodeAsBoolean(elements[1]).booleanValue();
            String additionalInfo = null;
            int i = 2;
            while (i < elements.length) {
                switch (elements[i].getType()) {
                    case Byte.MIN_VALUE: {
                        additionalInfo = ASN1OctetString.decodeAsOctetString(elements[i]).stringValue();
                        ++i;
                        continue;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_PW_REQ_VALIDATION_RESULT_INVALID_ELEMENT_TYPE.get(StaticUtils.toHex(elements[i].getType())));
                    }
                }
            }
            return new PasswordQualityRequirementValidationResult(passwordRequirement, requirementSatisfied, additionalInfo);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_PW_REQ_VALIDATION_RESULT_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("PasswordQualityRequirementValidationResult(requirement=");
        this.passwordRequirement.toString(buffer);
        buffer.append(", requirementSatisfied=");
        buffer.append(this.requirementSatisfied);
        if (this.additionalInfo != null) {
            buffer.append(", additionalInfo='");
            buffer.append(this.additionalInfo);
            buffer.append('\'');
        }
        buffer.append(')');
    }
}
