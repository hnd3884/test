package com.unboundid.ldap.sdk.controls;

import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.DecodeableControl;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class AuthorizationIdentityResponseControl extends Control implements DecodeableControl
{
    public static final String AUTHORIZATION_IDENTITY_RESPONSE_OID = "2.16.840.1.113730.3.4.15";
    private static final long serialVersionUID = -6315724175438820336L;
    private final String authorizationID;
    
    AuthorizationIdentityResponseControl() {
        this.authorizationID = null;
    }
    
    public AuthorizationIdentityResponseControl(final String authorizationID) {
        super("2.16.840.1.113730.3.4.15", false, new ASN1OctetString(authorizationID));
        Validator.ensureNotNull(authorizationID);
        this.authorizationID = authorizationID;
    }
    
    public AuthorizationIdentityResponseControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        super(oid, isCritical, value);
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_AUTHZID_RESPONSE_NO_VALUE.get());
        }
        this.authorizationID = value.stringValue();
    }
    
    @Override
    public AuthorizationIdentityResponseControl decodeControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        return new AuthorizationIdentityResponseControl(oid, isCritical, value);
    }
    
    public static AuthorizationIdentityResponseControl get(final BindResult result) throws LDAPException {
        final Control c = result.getResponseControl("2.16.840.1.113730.3.4.15");
        if (c == null) {
            return null;
        }
        if (c instanceof AuthorizationIdentityResponseControl) {
            return (AuthorizationIdentityResponseControl)c;
        }
        return new AuthorizationIdentityResponseControl(c.getOID(), c.isCritical(), c.getValue());
    }
    
    public String getAuthorizationID() {
        return this.authorizationID;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_AUTHZID_RESPONSE.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("AuthorizationIdentityResponseControl(authorizationID='");
        buffer.append(this.authorizationID);
        buffer.append("', isCritical=");
        buffer.append(this.isCritical());
        buffer.append(')');
    }
}
