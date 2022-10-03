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
public final class AccountUsableRequestControl extends Control
{
    public static final String ACCOUNT_USABLE_REQUEST_OID = "1.3.6.1.4.1.42.2.27.9.5.8";
    private static final long serialVersionUID = 2776055961624360982L;
    
    public AccountUsableRequestControl() {
        this(false);
    }
    
    public AccountUsableRequestControl(final boolean isCritical) {
        super("1.3.6.1.4.1.42.2.27.9.5.8", isCritical, null);
    }
    
    public AccountUsableRequestControl(final Control control) throws LDAPException {
        super(control);
        if (control.hasValue()) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ACCOUNT_USABLE_REQUEST_HAS_VALUE.get());
        }
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_ACCOUNT_USABLE_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("AccountUsableRequestControl(isCritical=");
        buffer.append(this.isCritical());
        buffer.append(')');
    }
}
