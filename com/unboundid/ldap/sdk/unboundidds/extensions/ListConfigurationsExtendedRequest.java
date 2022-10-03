package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.ldap.sdk.LDAPRequest;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.ldap.sdk.ExtendedRequest;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ListConfigurationsExtendedRequest extends ExtendedRequest
{
    public static final String LIST_CONFIGS_REQUEST_OID = "1.3.6.1.4.1.30221.2.6.26";
    private static final long serialVersionUID = -5511054471842622735L;
    
    public ListConfigurationsExtendedRequest(final Control... controls) {
        super("1.3.6.1.4.1.30221.2.6.26", controls);
    }
    
    public ListConfigurationsExtendedRequest(final ExtendedRequest r) throws LDAPException {
        super(r);
        if (r.hasValue()) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_LIST_CONFIGS_REQUEST_HAS_VALUE.get());
        }
    }
    
    public ListConfigurationsExtendedResult process(final LDAPConnection connection, final int depth) throws LDAPException {
        final ExtendedResult extendedResponse = super.process(connection, depth);
        return new ListConfigurationsExtendedResult(extendedResponse);
    }
    
    @Override
    public ListConfigurationsExtendedRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public ListConfigurationsExtendedRequest duplicate(final Control[] controls) {
        final ListConfigurationsExtendedRequest r = new ListConfigurationsExtendedRequest(controls);
        r.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return r;
    }
    
    @Override
    public String getExtendedRequestName() {
        return ExtOpMessages.INFO_EXTENDED_REQUEST_NAME_LIST_CONFIGS.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("ListConfigurationsExtendedRequest(");
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
