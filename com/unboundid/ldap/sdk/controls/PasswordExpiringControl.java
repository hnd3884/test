package com.unboundid.ldap.sdk.controls;

import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.util.Debug;
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
public final class PasswordExpiringControl extends Control implements DecodeableControl
{
    public static final String PASSWORD_EXPIRING_OID = "2.16.840.1.113730.3.4.5";
    private static final long serialVersionUID = 1250220480854441338L;
    private final int secondsUntilExpiration;
    
    PasswordExpiringControl() {
        this.secondsUntilExpiration = -1;
    }
    
    public PasswordExpiringControl(final int secondsUntilExpiration) {
        super("2.16.840.1.113730.3.4.5", false, new ASN1OctetString(String.valueOf(secondsUntilExpiration)));
        this.secondsUntilExpiration = secondsUntilExpiration;
    }
    
    public PasswordExpiringControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        super(oid, isCritical, value);
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_PW_EXPIRING_NO_VALUE.get());
        }
        try {
            this.secondsUntilExpiration = Integer.parseInt(value.stringValue());
        }
        catch (final NumberFormatException nfe) {
            Debug.debugException(nfe);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_PW_EXPIRING_VALUE_NOT_INTEGER.get(), nfe);
        }
    }
    
    @Override
    public PasswordExpiringControl decodeControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        return new PasswordExpiringControl(oid, isCritical, value);
    }
    
    public static PasswordExpiringControl get(final LDAPResult result) throws LDAPException {
        final Control c = result.getResponseControl("2.16.840.1.113730.3.4.5");
        if (c == null) {
            return null;
        }
        if (c instanceof PasswordExpiringControl) {
            return (PasswordExpiringControl)c;
        }
        return new PasswordExpiringControl(c.getOID(), c.isCritical(), c.getValue());
    }
    
    public int getSecondsUntilExpiration() {
        return this.secondsUntilExpiration;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_PW_EXPIRING.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("PasswordExpiringControl(secondsUntilExpiration=");
        buffer.append(this.secondsUntilExpiration);
        buffer.append(", isCritical=");
        buffer.append(this.isCritical());
        buffer.append(')');
    }
}
