package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.ldap.sdk.LDAPRequest;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.List;
import java.util.Collection;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Integer;
import com.unboundid.asn1.ASN1Null;
import com.unboundid.asn1.ASN1Element;
import java.util.ArrayList;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedRequest;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class GeneratePasswordExtendedRequest extends ExtendedRequest
{
    public static final String GENERATE_PASSWORD_REQUEST_OID = "1.3.6.1.4.1.30221.2.6.62";
    private static final byte TYPE_NUMBER_OF_PASSWORDS = -125;
    private static final int DEFAULT_NUMBER_OF_PASSWORDS = 1;
    private static final byte TYPE_VALIDATION_ATTEMPTS = -124;
    private static final int DEFAULT_VALIDATION_ATTEMPTS = 5;
    private static final long serialVersionUID = -4264500486902843854L;
    private final int numberOfPasswords;
    private final int numberOfValidationAttempts;
    private final GeneratePasswordPolicySelectionType passwordPolicySelectionType;
    private final String passwordPolicyDN;
    private final String targetEntryDN;
    
    public GeneratePasswordExtendedRequest(final Control... controls) {
        this(GeneratePasswordPolicySelectionType.DEFAULT_POLICY, null, null, 1, 5, controls);
    }
    
    private GeneratePasswordExtendedRequest(final GeneratePasswordPolicySelectionType passwordPolicySelectionType, final String passwordPolicyDN, final String targetEntryDN, final int numberOfPasswords, final int numberOfValidationAttempts, final Control... controls) {
        super("1.3.6.1.4.1.30221.2.6.62", encodeValue(passwordPolicySelectionType, passwordPolicyDN, targetEntryDN, numberOfPasswords, numberOfValidationAttempts), controls);
        this.passwordPolicySelectionType = passwordPolicySelectionType;
        this.passwordPolicyDN = passwordPolicyDN;
        this.targetEntryDN = targetEntryDN;
        this.numberOfPasswords = numberOfPasswords;
        this.numberOfValidationAttempts = numberOfValidationAttempts;
    }
    
    private static ASN1OctetString encodeValue(final GeneratePasswordPolicySelectionType passwordPolicySelectionType, final String passwordPolicyDN, final String targetEntryDN, final int numberOfPasswords, final int numberOfValidationAttempts) {
        Validator.ensureNotNullWithMessage(passwordPolicySelectionType, "GeneratePasswordExtendedRequest.passwordPolicySelectionType must not be null.");
        final List<ASN1Element> elements = new ArrayList<ASN1Element>(3);
        switch (passwordPolicySelectionType) {
            case DEFAULT_POLICY: {
                Validator.ensureTrue(passwordPolicyDN == null, "GeneratePasswordExtendedRequest.passwordPolicyDN must be null when using a password policy selection type of " + passwordPolicySelectionType + '.');
                Validator.ensureTrue(targetEntryDN == null, "GeneratePasswordExtendedRequest.targetEntryDN must be null when using a password policy selection type of " + passwordPolicySelectionType + '.');
                if (numberOfPasswords == 1 && numberOfValidationAttempts == 5) {
                    return null;
                }
                elements.add(new ASN1Null(passwordPolicySelectionType.getBERType()));
                break;
            }
            case PASSWORD_POLICY_DN: {
                Validator.ensureNotNullWithMessage(passwordPolicyDN, "GeneratePasswordExtendedRequest.passwordPolicyDN must not be null when using a password policy selection type of " + passwordPolicySelectionType + '.');
                Validator.ensureTrue(targetEntryDN == null, "GeneratePasswordExtendedRequest.targetEntryDN must be null when using a password policy selection type of " + passwordPolicySelectionType + '.');
                elements.add(new ASN1OctetString(passwordPolicySelectionType.getBERType(), passwordPolicyDN));
                break;
            }
            case TARGET_ENTRY_DN: {
                Validator.ensureTrue(passwordPolicyDN == null, "GeneratePasswordExtendedRequest.passwordPolicyDN must be null when using a password policy selection type of " + passwordPolicySelectionType + '.');
                Validator.ensureNotNullWithMessage(targetEntryDN, "GeneratePasswordExtendedRequest.targetEntryDN must not be null when using a password policy selection type of " + passwordPolicySelectionType + '.');
                elements.add(new ASN1OctetString(passwordPolicySelectionType.getBERType(), targetEntryDN));
                break;
            }
        }
        if (numberOfPasswords != 1) {
            Validator.ensureTrue(numberOfPasswords >= 1, "GeneratePasswordExtendedRequest.numberOfPasswords must be greater than or equal to one.");
            elements.add(new ASN1Integer((byte)(-125), numberOfPasswords));
        }
        if (numberOfValidationAttempts != 5) {
            Validator.ensureTrue(numberOfValidationAttempts >= 0, "GeneratePasswordExtendedRequest.validationAttempts must be greater than or equal to zero.");
            elements.add(new ASN1Integer((byte)(-124), numberOfValidationAttempts));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public GeneratePasswordExtendedRequest(final ExtendedRequest request) throws LDAPException {
        super(request);
        final ASN1OctetString value = request.getValue();
        if (value == null) {
            this.passwordPolicySelectionType = GeneratePasswordPolicySelectionType.DEFAULT_POLICY;
            this.passwordPolicyDN = null;
            this.targetEntryDN = null;
            this.numberOfPasswords = 1;
            this.numberOfValidationAttempts = 5;
            return;
        }
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(value.getValue()).elements();
            this.passwordPolicySelectionType = GeneratePasswordPolicySelectionType.forType(elements[0].getType());
            if (this.passwordPolicySelectionType == null) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GENERATE_PASSWORD_REQUEST_UNSUPPORTED_SELECTION_TYPE.get(StaticUtils.toHex(elements[0].getType())));
            }
            switch (this.passwordPolicySelectionType) {
                case PASSWORD_POLICY_DN: {
                    this.passwordPolicyDN = elements[0].decodeAsOctetString().stringValue();
                    this.targetEntryDN = null;
                    break;
                }
                case TARGET_ENTRY_DN: {
                    this.targetEntryDN = elements[0].decodeAsOctetString().stringValue();
                    this.passwordPolicyDN = null;
                    break;
                }
                default: {
                    this.passwordPolicyDN = null;
                    this.targetEntryDN = null;
                    break;
                }
            }
            int numPasswords = 1;
            int numAttempts = 5;
            for (int i = 1; i < elements.length; ++i) {
                switch (elements[i].getType()) {
                    case -125: {
                        numPasswords = ASN1Integer.decodeAsInteger(elements[i]).intValue();
                        if (numPasswords < 1) {
                            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GENERATE_PASSWORD_REQUEST_INVALID_NUM_PASSWORDS.get(numPasswords));
                        }
                        break;
                    }
                    case -124: {
                        numAttempts = ASN1Integer.decodeAsInteger(elements[i]).intValue();
                        if (numAttempts < 0) {
                            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GENERATE_PASSWORD_REQUEST_INVALID_NUM_ATTEMPTS.get(numAttempts));
                        }
                        break;
                    }
                }
            }
            this.numberOfPasswords = numPasswords;
            this.numberOfValidationAttempts = numAttempts;
        }
        catch (final LDAPException e) {
            Debug.debugException(e);
            throw e;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GENERATE_PASSWORD_REQUEST_DECODING_ERROR.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    public static GeneratePasswordExtendedRequest createDefaultPolicyRequest(final int numberOfPasswords, final int numberOfValidationAttempts, final Control... controls) {
        return new GeneratePasswordExtendedRequest(GeneratePasswordPolicySelectionType.DEFAULT_POLICY, null, null, numberOfPasswords, numberOfValidationAttempts, controls);
    }
    
    public static GeneratePasswordExtendedRequest createPasswordPolicyDNRequest(final String passwordPolicyDN, final int numberOfPasswords, final int numberOfValidationAttempts, final Control... controls) {
        return new GeneratePasswordExtendedRequest(GeneratePasswordPolicySelectionType.PASSWORD_POLICY_DN, passwordPolicyDN, null, numberOfPasswords, numberOfValidationAttempts, controls);
    }
    
    public static GeneratePasswordExtendedRequest createTargetEntryDNRequest(final String targetEntryDN, final int numberOfPasswords, final int numberOfValidationAttempts, final Control... controls) {
        return new GeneratePasswordExtendedRequest(GeneratePasswordPolicySelectionType.TARGET_ENTRY_DN, null, targetEntryDN, numberOfPasswords, numberOfValidationAttempts, controls);
    }
    
    public GeneratePasswordPolicySelectionType getPasswordPolicySelectionType() {
        return this.passwordPolicySelectionType;
    }
    
    public String getPasswordPolicyDN() {
        return this.passwordPolicyDN;
    }
    
    public String getTargetEntryDN() {
        return this.targetEntryDN;
    }
    
    public int getNumberOfPasswords() {
        return this.numberOfPasswords;
    }
    
    public int getNumberOfValidationAttempts() {
        return this.numberOfValidationAttempts;
    }
    
    @Override
    protected GeneratePasswordExtendedResult process(final LDAPConnection connection, final int depth) throws LDAPException {
        return new GeneratePasswordExtendedResult(super.process(connection, depth));
    }
    
    @Override
    public GeneratePasswordExtendedRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public GeneratePasswordExtendedRequest duplicate(final Control[] controls) {
        final GeneratePasswordExtendedRequest r = new GeneratePasswordExtendedRequest(this.passwordPolicySelectionType, this.passwordPolicyDN, this.targetEntryDN, this.numberOfPasswords, this.numberOfValidationAttempts, controls);
        r.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return r;
    }
    
    @Override
    public String getExtendedRequestName() {
        return ExtOpMessages.INFO_GENERATE_PASSWORD_REQUEST_NAME.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("GeneratePasswordExtendedRequest(passwordPolicySelectionType='");
        buffer.append(this.passwordPolicySelectionType.name());
        buffer.append('\'');
        switch (this.passwordPolicySelectionType) {
            case PASSWORD_POLICY_DN: {
                buffer.append(", passwordPolicyDN='");
                buffer.append(this.passwordPolicyDN);
                buffer.append('\'');
                break;
            }
            case TARGET_ENTRY_DN: {
                buffer.append(", targetEntryDN='");
                buffer.append(this.targetEntryDN);
                buffer.append('\'');
                break;
            }
        }
        buffer.append(", numberOfPasswords=");
        buffer.append(this.numberOfPasswords);
        buffer.append(", numberOfValidationAttempts=");
        buffer.append(this.numberOfValidationAttempts);
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
