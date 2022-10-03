package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.ldap.sdk.LDAPRequest;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedRequest;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class EndAdministrativeSessionExtendedRequest extends ExtendedRequest
{
    public static final String END_ADMIN_SESSION_REQUEST_OID = "1.3.6.1.4.1.30221.2.6.14";
    private static final long serialVersionUID = 1860335278876749499L;
    
    public EndAdministrativeSessionExtendedRequest(final Control... controls) {
        super("1.3.6.1.4.1.30221.2.6.14", controls);
    }
    
    public EndAdministrativeSessionExtendedRequest(final ExtendedRequest extendedRequest) throws LDAPException {
        super(extendedRequest);
        if (extendedRequest.getValue() != null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_END_ADMIN_SESSION_REQUEST_HAS_VALUE.get());
        }
    }
    
    @Override
    public EndAdministrativeSessionExtendedRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public EndAdministrativeSessionExtendedRequest duplicate(final Control[] controls) {
        return new EndAdministrativeSessionExtendedRequest(controls);
    }
    
    @Override
    public String getExtendedRequestName() {
        return ExtOpMessages.INFO_EXTENDED_REQUEST_NAME_END_ADMIN_SESSION.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("EndAdministrativeSessionExtendedRequest(");
        final Control[] controls = this.getControls();
        if (controls.length > 0) {
            buffer.append("controls={");
            for (int i = 0; i < controls.length; ++i) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append(controls[i]);
            }
            buffer.append('}');
        }
        buffer.append(')');
    }
}
