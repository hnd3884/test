package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class GetPasswordPolicyStateIssuesRequestControl extends Control
{
    public static final String GET_PASSWORD_POLICY_STATE_ISSUES_REQUEST_OID = "1.3.6.1.4.1.30221.2.5.46";
    private static final long serialVersionUID = 5423754545363349200L;
    
    public GetPasswordPolicyStateIssuesRequestControl() {
        this(false);
    }
    
    public GetPasswordPolicyStateIssuesRequestControl(final boolean isCritical) {
        super("1.3.6.1.4.1.30221.2.5.46", isCritical);
    }
    
    public GetPasswordPolicyStateIssuesRequestControl(final Control control) throws LDAPException {
        super(control);
        if (control.hasValue()) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_GET_PWP_STATE_ISSUES_REQUEST_HAS_VALUE.get());
        }
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_GET_PWP_STATE_ISSUES_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("GetPasswordPolicyStateIssuesRequestControl(isCritical=");
        buffer.append(this.isCritical());
        buffer.append(')');
    }
}
