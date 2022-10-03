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
public final class DontUseCopyRequestControl extends Control
{
    public static final String DONT_USE_COPY_REQUEST_OID = "1.3.6.1.1.22";
    private static final long serialVersionUID = -5352797941017941217L;
    
    public DontUseCopyRequestControl() {
        super("1.3.6.1.1.22", true, null);
    }
    
    public DontUseCopyRequestControl(final Control control) throws LDAPException {
        super(control);
        if (control.hasValue()) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_DONT_USE_COPY_HAS_VALUE.get());
        }
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_DONT_USE_COPY.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("DontUseCopyRequestControl(isCritical=");
        buffer.append(this.isCritical());
        buffer.append(')');
    }
}
