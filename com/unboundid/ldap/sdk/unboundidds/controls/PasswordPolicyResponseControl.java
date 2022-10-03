package com.unboundid.ldap.sdk.unboundidds.controls;

import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.asn1.ASN1Enumerated;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1Integer;
import com.unboundid.asn1.ASN1Exception;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.DecodeableControl;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class PasswordPolicyResponseControl extends Control implements DecodeableControl
{
    public static final String PASSWORD_POLICY_RESPONSE_OID = "1.3.6.1.4.1.42.2.27.8.5.1";
    private static final byte TYPE_WARNING = -96;
    private static final byte TYPE_ERROR = -127;
    private static final byte TYPE_TIME_BEFORE_EXPIRATION = Byte.MIN_VALUE;
    private static final byte TYPE_GRACE_LOGINS_REMAINING = -127;
    private static final long serialVersionUID = 1835830253434331833L;
    private final int warningValue;
    private final PasswordPolicyErrorType errorType;
    private final PasswordPolicyWarningType warningType;
    
    PasswordPolicyResponseControl() {
        this.warningType = null;
        this.errorType = null;
        this.warningValue = -1;
    }
    
    public PasswordPolicyResponseControl(final PasswordPolicyWarningType warningType, final int warningValue, final PasswordPolicyErrorType errorType) {
        this(warningType, warningValue, errorType, false);
    }
    
    public PasswordPolicyResponseControl(final PasswordPolicyWarningType warningType, final int warningValue, final PasswordPolicyErrorType errorType, final boolean isCritical) {
        super("1.3.6.1.4.1.42.2.27.8.5.1", isCritical, encodeValue(warningType, warningValue, errorType));
        this.warningType = warningType;
        this.errorType = errorType;
        if (warningType == null) {
            this.warningValue = -1;
        }
        else {
            this.warningValue = warningValue;
        }
    }
    
    public PasswordPolicyResponseControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        super(oid, isCritical, value);
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_PWP_RESPONSE_NO_VALUE.get());
        }
        ASN1Sequence valueSequence;
        try {
            final ASN1Element valueElement = ASN1Element.decode(value.getValue());
            valueSequence = ASN1Sequence.decodeAsSequence(valueElement);
        }
        catch (final ASN1Exception ae) {
            Debug.debugException(ae);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_PWP_RESPONSE_VALUE_NOT_SEQUENCE.get(ae), ae);
        }
        final ASN1Element[] valueElements = valueSequence.elements();
        if (valueElements.length > 2) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_PWP_RESPONSE_INVALID_ELEMENT_COUNT.get(valueElements.length));
        }
        int wv = -1;
        PasswordPolicyErrorType et = null;
        PasswordPolicyWarningType wt = null;
        for (final ASN1Element e : valueElements) {
            switch (e.getType()) {
                case -96: {
                    if (wt == null) {
                        try {
                            final ASN1Element warningElement = ASN1Element.decode(e.getValue());
                            wv = ASN1Integer.decodeAsInteger(warningElement).intValue();
                            switch (warningElement.getType()) {
                                case Byte.MIN_VALUE: {
                                    wt = PasswordPolicyWarningType.TIME_BEFORE_EXPIRATION;
                                    break;
                                }
                                case -127: {
                                    wt = PasswordPolicyWarningType.GRACE_LOGINS_REMAINING;
                                    break;
                                }
                                default: {
                                    throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_PWP_RESPONSE_INVALID_WARNING_TYPE.get(StaticUtils.toHex(warningElement.getType())));
                                }
                            }
                            break;
                        }
                        catch (final ASN1Exception ae2) {
                            Debug.debugException(ae2);
                            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_PWP_RESPONSE_CANNOT_DECODE_WARNING.get(ae2), ae2);
                        }
                    }
                    throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_PWP_RESPONSE_MULTIPLE_WARNING.get());
                }
                case -127: {
                    if (et == null) {
                        try {
                            final ASN1Enumerated errorElement = ASN1Enumerated.decodeAsEnumerated(e);
                            et = PasswordPolicyErrorType.valueOf(errorElement.intValue());
                            if (et == null) {
                                throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_PWP_RESPONSE_INVALID_ERROR_TYPE.get(errorElement.intValue()));
                            }
                            break;
                        }
                        catch (final ASN1Exception ae2) {
                            Debug.debugException(ae2);
                            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_PWP_RESPONSE_CANNOT_DECODE_ERROR.get(ae2), ae2);
                        }
                    }
                    throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_PWP_RESPONSE_MULTIPLE_ERROR.get());
                }
                default: {
                    throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_PWP_RESPONSE_INVALID_TYPE.get(StaticUtils.toHex(e.getType())));
                }
            }
        }
        this.warningType = wt;
        this.warningValue = wv;
        this.errorType = et;
    }
    
    @Override
    public PasswordPolicyResponseControl decodeControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        return new PasswordPolicyResponseControl(oid, isCritical, value);
    }
    
    public static PasswordPolicyResponseControl get(final LDAPResult result) throws LDAPException {
        final Control c = result.getResponseControl("1.3.6.1.4.1.42.2.27.8.5.1");
        if (c == null) {
            return null;
        }
        if (c instanceof PasswordPolicyResponseControl) {
            return (PasswordPolicyResponseControl)c;
        }
        return new PasswordPolicyResponseControl(c.getOID(), c.isCritical(), c.getValue());
    }
    
    private static ASN1OctetString encodeValue(final PasswordPolicyWarningType warningType, final int warningValue, final PasswordPolicyErrorType errorType) {
        final ArrayList<ASN1Element> valueElements = new ArrayList<ASN1Element>(2);
        if (warningType != null) {
            switch (warningType) {
                case TIME_BEFORE_EXPIRATION: {
                    valueElements.add(new ASN1Element((byte)(-96), new ASN1Integer((byte)(-128), warningValue).encode()));
                    break;
                }
                case GRACE_LOGINS_REMAINING: {
                    valueElements.add(new ASN1Element((byte)(-96), new ASN1Integer((byte)(-127), warningValue).encode()));
                    break;
                }
            }
        }
        if (errorType != null) {
            valueElements.add(new ASN1Enumerated((byte)(-127), errorType.intValue()));
        }
        return new ASN1OctetString(new ASN1Sequence(valueElements).encode());
    }
    
    public PasswordPolicyWarningType getWarningType() {
        return this.warningType;
    }
    
    public int getWarningValue() {
        return this.warningValue;
    }
    
    public PasswordPolicyErrorType getErrorType() {
        return this.errorType;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_PW_POLICY_RESPONSE.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("PasswordPolicyResponseControl(");
        boolean elementAdded = false;
        if (this.warningType != null) {
            buffer.append("warningType='");
            buffer.append(this.warningType.getName());
            buffer.append("', warningValue=");
            buffer.append(this.warningValue);
            elementAdded = true;
        }
        if (this.errorType != null) {
            if (elementAdded) {
                buffer.append(", ");
            }
            buffer.append("errorType='");
            buffer.append(this.errorType.getName());
            buffer.append('\'');
            elementAdded = true;
        }
        if (elementAdded) {
            buffer.append(", ");
        }
        buffer.append("isCritical=");
        buffer.append(this.isCritical());
        buffer.append(')');
    }
}
