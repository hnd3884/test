package com.unboundid.ldap.sdk.unboundidds.controls;

import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Long;
import com.unboundid.asn1.ASN1Boolean;
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
public final class GeneratePasswordResponseControl extends Control implements DecodeableControl
{
    public static final String GENERATE_PASSWORD_RESPONSE_OID = "1.3.6.1.4.1.30221.2.5.59";
    private static final byte TYPE_SECONDS_UNTIL_EXPIRATION = Byte.MIN_VALUE;
    private static final long serialVersionUID = 7542512192838228238L;
    private final ASN1OctetString generatedPassword;
    private final boolean mustChangePassword;
    private final Long secondsUntilExpiration;
    
    GeneratePasswordResponseControl() {
        this.generatedPassword = null;
        this.mustChangePassword = false;
        this.secondsUntilExpiration = null;
    }
    
    public GeneratePasswordResponseControl(final String generatedPassword, final boolean mustChangePassword, final Long secondsUntilExpiration) {
        this(new ASN1OctetString(generatedPassword), mustChangePassword, secondsUntilExpiration);
    }
    
    public GeneratePasswordResponseControl(final byte[] generatedPassword, final boolean mustChangePassword, final Long secondsUntilExpiration) {
        this(new ASN1OctetString(generatedPassword), mustChangePassword, secondsUntilExpiration);
    }
    
    private GeneratePasswordResponseControl(final ASN1OctetString generatedPassword, final boolean mustChangePassword, final Long secondsUntilExpiration) {
        super("1.3.6.1.4.1.30221.2.5.59", false, encodeValue(generatedPassword, mustChangePassword, secondsUntilExpiration));
        this.generatedPassword = generatedPassword;
        this.mustChangePassword = mustChangePassword;
        this.secondsUntilExpiration = secondsUntilExpiration;
    }
    
    public GeneratePasswordResponseControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        super(oid, isCritical, value);
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_GENERATE_PASSWORD_RESPONSE_NO_VALUE.get());
        }
        try {
            final ASN1Element valElement = ASN1Element.decode(value.getValue());
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(valElement).elements();
            this.generatedPassword = ASN1OctetString.decodeAsOctetString(elements[0]);
            this.mustChangePassword = ASN1Boolean.decodeAsBoolean(elements[1]).booleanValue();
            Long secsUntilExp = null;
            for (int i = 2; i < elements.length; ++i) {
                final ASN1Element e = elements[i];
                switch (e.getType()) {
                    case Byte.MIN_VALUE: {
                        secsUntilExp = ASN1Long.decodeAsLong(e).longValue();
                        break;
                    }
                }
            }
            this.secondsUntilExpiration = secsUntilExp;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_GENERATE_PASSWORD_RESPONSE_CANNOT_DECODE_VALUE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    @Override
    public GeneratePasswordResponseControl decodeControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        return new GeneratePasswordResponseControl(oid, isCritical, value);
    }
    
    public static GeneratePasswordResponseControl get(final LDAPResult result) throws LDAPException {
        final Control c = result.getResponseControl("1.3.6.1.4.1.30221.2.5.59");
        if (c == null) {
            return null;
        }
        if (c instanceof GeneratePasswordResponseControl) {
            return (GeneratePasswordResponseControl)c;
        }
        return new GeneratePasswordResponseControl(c.getOID(), c.isCritical(), c.getValue());
    }
    
    private static ASN1OctetString encodeValue(final ASN1OctetString generatedPassword, final boolean mustChangePassword, final Long secondsUntilExpiration) {
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(3);
        elements.add(generatedPassword);
        elements.add(mustChangePassword ? ASN1Boolean.UNIVERSAL_BOOLEAN_TRUE_ELEMENT : ASN1Boolean.UNIVERSAL_BOOLEAN_FALSE_ELEMENT);
        if (secondsUntilExpiration != null) {
            elements.add(new ASN1Long((byte)(-128), secondsUntilExpiration));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public ASN1OctetString getGeneratedPassword() {
        return this.generatedPassword;
    }
    
    public String getGeneratedPasswordString() {
        return this.generatedPassword.stringValue();
    }
    
    public byte[] getGeneratedPasswordBytes() {
        return this.generatedPassword.getValue();
    }
    
    public boolean mustChangePassword() {
        return this.mustChangePassword;
    }
    
    public Long getSecondsUntilExpiration() {
        return this.secondsUntilExpiration;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_GENERATE_PASSWORD_RESPONSE.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("GeneratePasswordResponseControl(mustChangePassword=");
        buffer.append(this.mustChangePassword);
        if (this.secondsUntilExpiration != null) {
            buffer.append(", secondsUntilExpiration=");
            buffer.append(this.secondsUntilExpiration);
        }
        buffer.append(')');
    }
}
