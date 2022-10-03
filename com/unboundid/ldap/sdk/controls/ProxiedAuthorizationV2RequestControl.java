package com.unboundid.ldap.sdk.controls;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ProxiedAuthorizationV2RequestControl extends Control
{
    public static final String PROXIED_AUTHORIZATION_V2_REQUEST_OID = "2.16.840.1.113730.3.4.18";
    private static final long serialVersionUID = 1054244283964851067L;
    private final String authorizationID;
    
    public ProxiedAuthorizationV2RequestControl(final String authorizationID) {
        super("2.16.840.1.113730.3.4.18", true, new ASN1OctetString(authorizationID));
        Validator.ensureNotNull(authorizationID);
        this.authorizationID = authorizationID;
    }
    
    public ProxiedAuthorizationV2RequestControl(final Control control) throws LDAPException {
        super(control);
        final ASN1OctetString value = control.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_PROXY_V2_NO_VALUE.get());
        }
        this.authorizationID = value.stringValue();
    }
    
    public String getAuthorizationID() {
        return this.authorizationID;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_PROXIED_AUTHZ_V2_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("ProxiedAuthorizationV2RequestControl(authorizationID='");
        buffer.append(this.authorizationID);
        buffer.append("')");
    }
}
