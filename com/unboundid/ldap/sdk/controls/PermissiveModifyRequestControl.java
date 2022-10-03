package com.unboundid.ldap.sdk.controls;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class PermissiveModifyRequestControl extends Control
{
    public static final String PERMISSIVE_MODIFY_REQUEST_OID = "1.2.840.113556.1.4.1413";
    private static final long serialVersionUID = -2599039772002106760L;
    
    public PermissiveModifyRequestControl() {
        super("1.2.840.113556.1.4.1413", false, null);
    }
    
    public PermissiveModifyRequestControl(final boolean isCritical) {
        super("1.2.840.113556.1.4.1413", isCritical, null);
    }
    
    public PermissiveModifyRequestControl(final Control control) throws LDAPException {
        super(control);
        if (control.hasValue()) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_PERMISSIVE_MODIFY_HAS_VALUE.get());
        }
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_PERMISSIVE_MODIFY_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("PermissiveModifyRequestControl(isCritical=");
        buffer.append(this.isCritical());
        buffer.append(')');
    }
}
