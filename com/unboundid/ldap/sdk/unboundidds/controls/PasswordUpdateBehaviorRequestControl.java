package com.unboundid.ldap.sdk.unboundidds.controls;

import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class PasswordUpdateBehaviorRequestControl extends Control
{
    public static final String PASSWORD_UPDATE_BEHAVIOR_REQUEST_OID = "1.3.6.1.4.1.30221.2.5.51";
    private static final byte TYPE_IS_SELF_CHANGE = Byte.MIN_VALUE;
    private static final byte TYPE_ALLOW_PRE_ENCODED_PASSWORD = -127;
    private static final byte TYPE_SKIP_PASSWORD_VALIDATION = -126;
    private static final byte TYPE_IGNORE_PASSWORD_HISTORY = -125;
    private static final byte TYPE_IGNORE_MINIMUM_PASSWORD_AGE = -124;
    private static final byte TYPE_PASSWORD_STORAGE_SCHEME = -123;
    private static final byte TYPE_MUST_CHANGE_PASSWORD = -122;
    private static final long serialVersionUID = -1915608505128236450L;
    private final Boolean allowPreEncodedPassword;
    private final Boolean ignoreMinimumPasswordAge;
    private final Boolean ignorePasswordHistory;
    private final Boolean isSelfChange;
    private final Boolean mustChangePassword;
    private final Boolean skipPasswordValidation;
    private final String passwordStorageScheme;
    
    public PasswordUpdateBehaviorRequestControl(final PasswordUpdateBehaviorRequestControlProperties properties, final boolean isCritical) {
        super("1.3.6.1.4.1.30221.2.5.51", isCritical, encodeValue(properties));
        this.isSelfChange = properties.getIsSelfChange();
        this.allowPreEncodedPassword = properties.getAllowPreEncodedPassword();
        this.skipPasswordValidation = properties.getSkipPasswordValidation();
        this.ignorePasswordHistory = properties.getIgnorePasswordHistory();
        this.ignoreMinimumPasswordAge = properties.getIgnoreMinimumPasswordAge();
        this.passwordStorageScheme = properties.getPasswordStorageScheme();
        this.mustChangePassword = properties.getMustChangePassword();
    }
    
    public PasswordUpdateBehaviorRequestControl(final Control control) throws LDAPException {
        super(control);
        final ASN1OctetString value = control.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_PW_UPDATE_BEHAVIOR_REQ_DECODE_NO_VALUE.get());
        }
        try {
            Boolean allowPreEncoded = null;
            Boolean ignoreAge = null;
            Boolean ignoreHistory = null;
            Boolean mustChange = null;
            Boolean selfChange = null;
            Boolean skipValidation = null;
            String scheme = null;
            for (final ASN1Element e : ASN1Sequence.decodeAsSequence(value.getValue()).elements()) {
                switch (e.getType()) {
                    case Byte.MIN_VALUE: {
                        selfChange = ASN1Boolean.decodeAsBoolean(e).booleanValue();
                        break;
                    }
                    case -127: {
                        allowPreEncoded = ASN1Boolean.decodeAsBoolean(e).booleanValue();
                        break;
                    }
                    case -126: {
                        skipValidation = ASN1Boolean.decodeAsBoolean(e).booleanValue();
                        break;
                    }
                    case -125: {
                        ignoreHistory = ASN1Boolean.decodeAsBoolean(e).booleanValue();
                        break;
                    }
                    case -124: {
                        ignoreAge = ASN1Boolean.decodeAsBoolean(e).booleanValue();
                        break;
                    }
                    case -123: {
                        scheme = ASN1OctetString.decodeAsOctetString(e).stringValue();
                        break;
                    }
                    case -122: {
                        mustChange = ASN1Boolean.decodeAsBoolean(e).booleanValue();
                        break;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_PW_UPDATE_BEHAVIOR_REQ_DECODE_UNRECOGNIZED_ELEMENT_TYPE.get(StaticUtils.toHex(e.getType())));
                    }
                }
            }
            this.isSelfChange = selfChange;
            this.allowPreEncodedPassword = allowPreEncoded;
            this.skipPasswordValidation = skipValidation;
            this.ignorePasswordHistory = ignoreHistory;
            this.ignoreMinimumPasswordAge = ignoreAge;
            this.passwordStorageScheme = scheme;
            this.mustChangePassword = mustChange;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_PW_UPDATE_BEHAVIOR_REQ_DECODE_ERROR.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    private static ASN1OctetString encodeValue(final PasswordUpdateBehaviorRequestControlProperties properties) {
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(6);
        if (properties.getIsSelfChange() != null) {
            elements.add(new ASN1Boolean((byte)(-128), properties.getIsSelfChange()));
        }
        if (properties.getAllowPreEncodedPassword() != null) {
            elements.add(new ASN1Boolean((byte)(-127), properties.getAllowPreEncodedPassword()));
        }
        if (properties.getSkipPasswordValidation() != null) {
            elements.add(new ASN1Boolean((byte)(-126), properties.getSkipPasswordValidation()));
        }
        if (properties.getIgnorePasswordHistory() != null) {
            elements.add(new ASN1Boolean((byte)(-125), properties.getIgnorePasswordHistory()));
        }
        if (properties.getIgnoreMinimumPasswordAge() != null) {
            elements.add(new ASN1Boolean((byte)(-124), properties.getIgnoreMinimumPasswordAge()));
        }
        if (properties.getPasswordStorageScheme() != null) {
            elements.add(new ASN1OctetString((byte)(-123), properties.getPasswordStorageScheme()));
        }
        if (properties.getMustChangePassword() != null) {
            elements.add(new ASN1Boolean((byte)(-122), properties.getMustChangePassword()));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public Boolean getIsSelfChange() {
        return this.isSelfChange;
    }
    
    public Boolean getAllowPreEncodedPassword() {
        return this.allowPreEncodedPassword;
    }
    
    public Boolean getSkipPasswordValidation() {
        return this.skipPasswordValidation;
    }
    
    public Boolean getIgnorePasswordHistory() {
        return this.ignorePasswordHistory;
    }
    
    public Boolean getIgnoreMinimumPasswordAge() {
        return this.ignoreMinimumPasswordAge;
    }
    
    public String getPasswordStorageScheme() {
        return this.passwordStorageScheme;
    }
    
    public Boolean getMustChangePassword() {
        return this.mustChangePassword;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_PW_UPDATE_BEHAVIOR_REQ_CONTROL_NAME.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("PasswordUpdateBehaviorRequestControl(oid='");
        buffer.append("1.3.6.1.4.1.30221.2.5.51");
        buffer.append("', isCritical=");
        buffer.append(this.isCritical());
        buffer.append(", properties=");
        new PasswordUpdateBehaviorRequestControlProperties(this).toString(buffer);
        buffer.append(')');
    }
}
