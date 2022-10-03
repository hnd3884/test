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
public final class PasswordExpiredControl extends Control implements DecodeableControl
{
    public static final String PASSWORD_EXPIRED_OID = "2.16.840.1.113730.3.4.4";
    private static final long serialVersionUID = -2731704592689892224L;
    
    public PasswordExpiredControl() {
        super("2.16.840.1.113730.3.4.4", false, new ASN1OctetString("0"));
    }
    
    public PasswordExpiredControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        super(oid, isCritical, value);
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_PW_EXPIRED_NO_VALUE.get());
        }
        try {
            Integer.parseInt(value.stringValue());
        }
        catch (final NumberFormatException nfe) {
            Debug.debugException(nfe);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_PW_EXPIRED_VALUE_NOT_INTEGER.get(), nfe);
        }
    }
    
    @Override
    public PasswordExpiredControl decodeControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        return new PasswordExpiredControl(oid, isCritical, value);
    }
    
    public static PasswordExpiredControl get(final LDAPResult result) throws LDAPException {
        final Control c = result.getResponseControl("2.16.840.1.113730.3.4.4");
        if (c == null) {
            return null;
        }
        if (c instanceof PasswordExpiredControl) {
            return (PasswordExpiredControl)c;
        }
        return new PasswordExpiredControl(c.getOID(), c.isCritical(), c.getValue());
    }
    
    public static PasswordExpiredControl get(final LDAPException exception) throws LDAPException {
        return get(exception.toLDAPResult());
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_PW_EXPIRED.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("PasswordExpiredControl(isCritical=");
        buffer.append(this.isCritical());
        buffer.append(')');
    }
}
