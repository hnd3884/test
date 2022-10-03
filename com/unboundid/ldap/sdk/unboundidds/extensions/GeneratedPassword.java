package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import java.util.Iterator;
import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1Sequence;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import com.unboundid.util.Validator;
import java.util.List;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class GeneratedPassword implements Serializable
{
    private static final byte TYPE_VALIDATION_ERRORS = -96;
    private static final long serialVersionUID = -240847847799966594L;
    private final ASN1OctetString password;
    private final boolean validationAttempted;
    private final List<String> validationErrors;
    
    public GeneratedPassword(final String password, final boolean validationAttempted, final List<String> validationErrors) {
        this(new ASN1OctetString(password), validationAttempted, validationErrors);
    }
    
    public GeneratedPassword(final byte[] password, final boolean validationAttempted, final List<String> validationErrors) {
        this(new ASN1OctetString(password), validationAttempted, validationErrors);
    }
    
    private GeneratedPassword(final ASN1OctetString password, final boolean validationAttempted, final List<String> validationErrors) {
        Validator.ensureTrue(password != null && password.getValueLength() > 0, "GeneratedPassword.password must not be null or empty.");
        this.password = password;
        this.validationAttempted = validationAttempted;
        if (validationErrors == null) {
            this.validationErrors = Collections.emptyList();
        }
        else {
            this.validationErrors = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(validationErrors));
        }
    }
    
    public String getPasswordString() {
        return this.password.stringValue();
    }
    
    public byte[] getPasswordBytes() {
        return this.password.getValue();
    }
    
    public boolean validationAttempted() {
        return this.validationAttempted;
    }
    
    public List<String> getValidationErrors() {
        return this.validationErrors;
    }
    
    public ASN1Sequence encode() {
        final List<ASN1Element> elements = new ArrayList<ASN1Element>(3);
        elements.add(this.password);
        elements.add(new ASN1Boolean(this.validationAttempted));
        if (!this.validationErrors.isEmpty()) {
            final List<ASN1Element> validationErrorElements = new ArrayList<ASN1Element>(this.validationErrors.size());
            for (final String error : this.validationErrors) {
                validationErrorElements.add(new ASN1OctetString(error));
            }
            elements.add(new ASN1Sequence((byte)(-96), validationErrorElements));
        }
        return new ASN1Sequence(elements);
    }
    
    public static GeneratedPassword decode(final ASN1Element element) throws LDAPException {
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(element).elements();
            final ASN1OctetString password = elements[0].decodeAsOctetString();
            final boolean validationAttempted = elements[1].decodeAsBoolean().booleanValue();
            final List<String> validationErrors = new ArrayList<String>(5);
            for (int i = 2; i < elements.length; ++i) {
                if (elements[i].getType() == -96) {
                    for (final ASN1Element errorElement : elements[i].decodeAsSequence().elements()) {
                        validationErrors.add(errorElement.decodeAsOctetString().stringValue());
                    }
                }
            }
            return new GeneratedPassword(password, validationAttempted, validationErrors);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GENERATED_PASSWORD_DECODING_ERROR.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("GeneratedPassword(passwordLength=");
        buffer.append(this.password.getValueLength());
        buffer.append(", validationAttempted=");
        buffer.append(this.validationAttempted);
        if (!this.validationErrors.isEmpty()) {
            buffer.append(", validationErrors={");
            buffer.append('}');
        }
        buffer.append(')');
    }
}
