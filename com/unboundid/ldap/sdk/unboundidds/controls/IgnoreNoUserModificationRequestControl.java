package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class IgnoreNoUserModificationRequestControl extends Control
{
    public static final String IGNORE_NO_USER_MODIFICATION_REQUEST_OID = "1.3.6.1.4.1.30221.2.5.5";
    private static final long serialVersionUID = 2622432059171073555L;
    
    public IgnoreNoUserModificationRequestControl() {
        this(true);
    }
    
    public IgnoreNoUserModificationRequestControl(final boolean isCritical) {
        super("1.3.6.1.4.1.30221.2.5.5", isCritical, null);
    }
    
    public IgnoreNoUserModificationRequestControl(final Control control) throws LDAPException {
        super(control);
        if (control.hasValue()) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_IGNORENUM_REQUEST_HAS_VALUE.get());
        }
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_IGNORE_NO_USER_MODIFICATION_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("IgnoreNoUserModificationRequestControl(isCritical=");
        buffer.append(this.isCritical());
        buffer.append(')');
    }
}
