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
public final class PasswordPolicyRequestControl extends Control
{
    public static final String PASSWORD_POLICY_REQUEST_OID = "1.3.6.1.4.1.42.2.27.8.5.1";
    private static final long serialVersionUID = 6495056761590890150L;
    
    public PasswordPolicyRequestControl() {
        super("1.3.6.1.4.1.42.2.27.8.5.1", false, null);
    }
    
    public PasswordPolicyRequestControl(final boolean isCritical) {
        super("1.3.6.1.4.1.42.2.27.8.5.1", isCritical, null);
    }
    
    public PasswordPolicyRequestControl(final Control control) throws LDAPException {
        super(control);
        if (control.hasValue()) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_PWP_REQUEST_HAS_VALUE.get());
        }
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_PW_POLICY_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("PasswordPolicyRequestControl(isCritical=");
        buffer.append(this.isCritical());
        buffer.append(')');
    }
}
