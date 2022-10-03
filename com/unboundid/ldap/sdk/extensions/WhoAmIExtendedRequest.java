package com.unboundid.ldap.sdk.extensions;

import com.unboundid.ldap.sdk.LDAPRequest;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedRequest;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class WhoAmIExtendedRequest extends ExtendedRequest
{
    public static final String WHO_AM_I_REQUEST_OID = "1.3.6.1.4.1.4203.1.11.3";
    private static final long serialVersionUID = -2936513698220673318L;
    
    public WhoAmIExtendedRequest() {
        super("1.3.6.1.4.1.4203.1.11.3");
    }
    
    public WhoAmIExtendedRequest(final Control[] controls) {
        super("1.3.6.1.4.1.4203.1.11.3", controls);
    }
    
    public WhoAmIExtendedRequest(final ExtendedRequest extendedRequest) throws LDAPException {
        super(extendedRequest);
        if (extendedRequest.hasValue()) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_WHO_AM_I_REQUEST_HAS_VALUE.get());
        }
    }
    
    public WhoAmIExtendedResult process(final LDAPConnection connection, final int depth) throws LDAPException {
        final ExtendedResult extendedResponse = super.process(connection, depth);
        return new WhoAmIExtendedResult(extendedResponse);
    }
    
    @Override
    public WhoAmIExtendedRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public WhoAmIExtendedRequest duplicate(final Control[] controls) {
        final WhoAmIExtendedRequest r = new WhoAmIExtendedRequest(controls);
        r.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return r;
    }
    
    @Override
    public String getExtendedRequestName() {
        return ExtOpMessages.INFO_EXTENDED_REQUEST_NAME_WHO_AM_I.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("WhoAmIExtendedRequest(");
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
