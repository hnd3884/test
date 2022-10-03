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
public final class ManageDsaITRequestControl extends Control
{
    public static final String MANAGE_DSA_IT_REQUEST_OID = "2.16.840.1.113730.3.4.2";
    private static final long serialVersionUID = -4540943247829123783L;
    
    public ManageDsaITRequestControl() {
        super("2.16.840.1.113730.3.4.2", false, null);
    }
    
    public ManageDsaITRequestControl(final boolean isCritical) {
        super("2.16.840.1.113730.3.4.2", isCritical, null);
    }
    
    public ManageDsaITRequestControl(final Control control) throws LDAPException {
        super(control);
        if (control.hasValue()) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_MANAGE_DSA_IT_HAS_VALUE.get());
        }
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_MANAGE_DSAIT_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("ManageDsaITRequestControl(isCritical=");
        buffer.append(this.isCritical());
        buffer.append(')');
    }
}
