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
public final class NoOpRequestControl extends Control
{
    public static final String NO_OP_REQUEST_OID = "1.3.6.1.4.1.4203.1.10.2";
    private static final long serialVersionUID = -7435407787971958294L;
    
    public NoOpRequestControl() {
        super("1.3.6.1.4.1.4203.1.10.2", true, null);
    }
    
    public NoOpRequestControl(final Control control) throws LDAPException {
        super(control);
        if (control.hasValue()) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_NOOP_REQUEST_HAS_VALUE.get());
        }
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_NOOP_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("NoOpRequestControl(isCritical=");
        buffer.append(this.isCritical());
        buffer.append(')');
    }
}
