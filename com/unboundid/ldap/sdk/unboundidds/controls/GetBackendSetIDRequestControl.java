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
public final class GetBackendSetIDRequestControl extends Control
{
    public static final String GET_BACKEND_SET_ID_REQUEST_OID = "1.3.6.1.4.1.30221.2.5.33";
    private static final long serialVersionUID = 7874405591825684773L;
    
    public GetBackendSetIDRequestControl() {
        this(false);
    }
    
    public GetBackendSetIDRequestControl(final boolean isCritical) {
        super("1.3.6.1.4.1.30221.2.5.33", isCritical, null);
    }
    
    public GetBackendSetIDRequestControl(final Control control) throws LDAPException {
        super(control);
        if (control.hasValue()) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_GET_BACKEND_SET_ID_REQUEST_HAS_VALUE.get());
        }
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_GET_BACKEND_SET_ID_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("GetBackendSetIDRequestControl(isCritical=");
        buffer.append(this.isCritical());
        buffer.append(')');
    }
}
