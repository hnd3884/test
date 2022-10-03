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
public final class AuthorizationIdentityRequestControl extends Control
{
    public static final String AUTHORIZATION_IDENTITY_REQUEST_OID = "2.16.840.1.113730.3.4.16";
    private static final long serialVersionUID = -4059607155175828138L;
    
    public AuthorizationIdentityRequestControl() {
        super("2.16.840.1.113730.3.4.16", false, null);
    }
    
    public AuthorizationIdentityRequestControl(final boolean isCritical) {
        super("2.16.840.1.113730.3.4.16", isCritical, null);
    }
    
    public AuthorizationIdentityRequestControl(final Control control) throws LDAPException {
        super(control);
        if (control.hasValue()) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_AUTHZID_REQUEST_HAS_VALUE.get());
        }
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_AUTHZID_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("AuthorizationIdentityRequestControl(isCritical=");
        buffer.append(this.isCritical());
        buffer.append(')');
    }
}
