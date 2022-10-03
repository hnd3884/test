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
public final class GeneratePasswordRequestControl extends Control
{
    public static final String GENERATE_PASSWORD_REQUEST_OID = "1.3.6.1.4.1.30221.2.5.58";
    private static final long serialVersionUID = 5302210626500743525L;
    
    public GeneratePasswordRequestControl() {
        this(true);
    }
    
    public GeneratePasswordRequestControl(final boolean isCritical) {
        super("1.3.6.1.4.1.30221.2.5.58", isCritical, null);
    }
    
    public GeneratePasswordRequestControl(final Control control) throws LDAPException {
        super(control);
        if (control.hasValue()) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_GENERATE_PASSWORD_REQUEST_HAS_VALUE.get());
        }
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_GENERATE_PASSWORD_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("GeneratePasswordRequestControl(isCritical=");
        buffer.append(this.isCritical());
        buffer.append(')');
    }
}
