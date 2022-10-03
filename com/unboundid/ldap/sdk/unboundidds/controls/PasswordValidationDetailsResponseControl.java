package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.ldap.sdk.LDAPResult;
import java.util.Iterator;
import com.unboundid.asn1.ASN1Null;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Integer;
import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1OctetString;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.DecodeableControl;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class PasswordValidationDetailsResponseControl extends Control implements DecodeableControl
{
    public static final String PASSWORD_VALIDATION_DETAILS_RESPONSE_OID = "1.3.6.1.4.1.30221.2.5.41";
    private static final byte TYPE_MISSING_CURRENT_PASSWORD = -125;
    private static final byte TYPE_MUST_CHANGE_PW = -124;
    private static final byte TYPE_SECONDS_UNTIL_EXPIRATION = -123;
    private static final long serialVersionUID = -2205640814914704074L;
    private final boolean missingCurrentPassword;
    private final boolean mustChangePassword;
    private final Integer secondsUntilExpiration;
    private final List<PasswordQualityRequirementValidationResult> validationResults;
    private final PasswordValidationDetailsResponseType responseType;
    
    PasswordValidationDetailsResponseControl() {
        this.responseType = null;
        this.validationResults = null;
        this.missingCurrentPassword = true;
        this.mustChangePassword = true;
        this.secondsUntilExpiration = null;
    }
    
    public PasswordValidationDetailsResponseControl(final PasswordValidationDetailsResponseType responseType, final Collection<PasswordQualityRequirementValidationResult> validationResults, final boolean missingCurrentPassword, final boolean mustChangePassword, final Integer secondsUntilExpiration) {
        super("1.3.6.1.4.1.30221.2.5.41", false, encodeValue(responseType, validationResults, missingCurrentPassword, mustChangePassword, secondsUntilExpiration));
        this.responseType = responseType;
        this.missingCurrentPassword = missingCurrentPassword;
        this.mustChangePassword = mustChangePassword;
        this.secondsUntilExpiration = secondsUntilExpiration;
        if (validationResults == null) {
            this.validationResults = Collections.emptyList();
        }
        else {
            this.validationResults = Collections.unmodifiableList((List<? extends PasswordQualityRequirementValidationResult>)new ArrayList<PasswordQualityRequirementValidationResult>(validationResults));
        }
    }
    
    public PasswordValidationDetailsResponseControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        super(oid, isCritical, value);
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_PW_VALIDATION_RESPONSE_NO_VALUE.get());
        }
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(value.getValue()).elements();
            this.responseType = PasswordValidationDetailsResponseType.forBERType(elements[0].getType());
            if (this.responseType == null) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_PW_VALIDATION_RESPONSE_INVALID_RESPONSE_TYPE.get(StaticUtils.toHex(elements[0].getType())));
            }
            if (this.responseType == PasswordValidationDetailsResponseType.VALIDATION_DETAILS) {
                final ASN1Element[] resultElements = ASN1Sequence.decodeAsSequence(elements[0]).elements();
                final ArrayList<PasswordQualityRequirementValidationResult> resultList = new ArrayList<PasswordQualityRequirementValidationResult>(resultElements.length);
                for (final ASN1Element e : resultElements) {
                    resultList.add(PasswordQualityRequirementValidationResult.decode(e));
                }
                this.validationResults = Collections.unmodifiableList((List<? extends PasswordQualityRequirementValidationResult>)resultList);
            }
            else {
                this.validationResults = Collections.emptyList();
            }
            boolean missingCurrent = false;
            boolean mustChange = false;
            Integer secondsRemaining = null;
            for (int i = 1; i < elements.length; ++i) {
                switch (elements[i].getType()) {
                    case -125: {
                        missingCurrent = ASN1Boolean.decodeAsBoolean(elements[i]).booleanValue();
                        break;
                    }
                    case -124: {
                        mustChange = ASN1Boolean.decodeAsBoolean(elements[i]).booleanValue();
                        break;
                    }
                    case -123: {
                        secondsRemaining = ASN1Integer.decodeAsInteger(elements[i]).intValue();
                        break;
                    }
                }
            }
            this.missingCurrentPassword = missingCurrent;
            this.mustChangePassword = mustChange;
            this.secondsUntilExpiration = secondsRemaining;
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_PW_VALIDATION_RESPONSE_ERROR_PARSING_VALUE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    private static ASN1OctetString encodeValue(final PasswordValidationDetailsResponseType responseType, final Collection<PasswordQualityRequirementValidationResult> validationResults, final boolean missingCurrentPassword, final boolean mustChangePassword, final Integer secondsUntilExpiration) {
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(4);
        switch (responseType) {
            case VALIDATION_DETAILS: {
                if (validationResults == null) {
                    elements.add(new ASN1Sequence(responseType.getBERType()));
                    break;
                }
                final ArrayList<ASN1Element> resultElements = new ArrayList<ASN1Element>(validationResults.size());
                for (final PasswordQualityRequirementValidationResult r : validationResults) {
                    resultElements.add(r.encode());
                }
                elements.add(new ASN1Sequence(responseType.getBERType(), resultElements));
                break;
            }
            case NO_PASSWORD_PROVIDED:
            case MULTIPLE_PASSWORDS_PROVIDED:
            case NO_VALIDATION_ATTEMPTED: {
                elements.add(new ASN1Null(responseType.getBERType()));
                break;
            }
        }
        if (missingCurrentPassword) {
            elements.add(new ASN1Boolean((byte)(-125), missingCurrentPassword));
        }
        if (mustChangePassword) {
            elements.add(new ASN1Boolean((byte)(-124), mustChangePassword));
        }
        if (secondsUntilExpiration != null) {
            elements.add(new ASN1Integer((byte)(-123), secondsUntilExpiration));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public PasswordValidationDetailsResponseType getResponseType() {
        return this.responseType;
    }
    
    public List<PasswordQualityRequirementValidationResult> getValidationResults() {
        return this.validationResults;
    }
    
    public boolean missingCurrentPassword() {
        return this.missingCurrentPassword;
    }
    
    public boolean mustChangePassword() {
        return this.mustChangePassword;
    }
    
    public Integer getSecondsUntilExpiration() {
        return this.secondsUntilExpiration;
    }
    
    @Override
    public PasswordValidationDetailsResponseControl decodeControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        return new PasswordValidationDetailsResponseControl(oid, isCritical, value);
    }
    
    public static PasswordValidationDetailsResponseControl get(final LDAPResult result) throws LDAPException {
        final Control c = result.getResponseControl("1.3.6.1.4.1.30221.2.5.41");
        if (c == null) {
            return null;
        }
        if (c instanceof PasswordValidationDetailsResponseControl) {
            return (PasswordValidationDetailsResponseControl)c;
        }
        return new PasswordValidationDetailsResponseControl(c.getOID(), c.isCritical(), c.getValue());
    }
    
    public static PasswordValidationDetailsResponseControl get(final LDAPException exception) throws LDAPException {
        return get(exception.toLDAPResult());
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_PW_VALIDATION_RESPONSE.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("PasswordValidationDetailsResponseControl(responseType='");
        buffer.append(this.responseType.name());
        buffer.append('\'');
        if (this.responseType == PasswordValidationDetailsResponseType.VALIDATION_DETAILS) {
            buffer.append(", validationDetails={");
            final Iterator<PasswordQualityRequirementValidationResult> iterator = this.validationResults.iterator();
            while (iterator.hasNext()) {
                iterator.next().toString(buffer);
                if (iterator.hasNext()) {
                    buffer.append(',');
                }
            }
            buffer.append('}');
        }
        buffer.append(", missingCurrentPassword=");
        buffer.append(this.missingCurrentPassword);
        buffer.append(", mustChangePassword=");
        buffer.append(this.mustChangePassword);
        if (this.secondsUntilExpiration != null) {
            buffer.append(", secondsUntilExpiration=");
            buffer.append(this.secondsUntilExpiration);
        }
        buffer.append("})");
    }
}
