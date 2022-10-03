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
public final class StartTransactionExtendedRequest extends ExtendedRequest
{
    public static final String START_TRANSACTION_REQUEST_OID = "1.3.6.1.1.21.1";
    private static final long serialVersionUID = 7382735226826929629L;
    
    public StartTransactionExtendedRequest() {
        super("1.3.6.1.1.21.1");
    }
    
    public StartTransactionExtendedRequest(final Control[] controls) {
        super("1.3.6.1.1.21.1", controls);
    }
    
    public StartTransactionExtendedRequest(final ExtendedRequest extendedRequest) throws LDAPException {
        super(extendedRequest);
        if (extendedRequest.hasValue()) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_START_TXN_REQUEST_HAS_VALUE.get());
        }
    }
    
    public StartTransactionExtendedResult process(final LDAPConnection connection, final int depth) throws LDAPException {
        final ExtendedResult extendedResponse = super.process(connection, depth);
        return new StartTransactionExtendedResult(extendedResponse);
    }
    
    @Override
    public StartTransactionExtendedRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public StartTransactionExtendedRequest duplicate(final Control[] controls) {
        final StartTransactionExtendedRequest r = new StartTransactionExtendedRequest(controls);
        r.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return r;
    }
    
    @Override
    public String getExtendedRequestName() {
        return ExtOpMessages.INFO_EXTENDED_REQUEST_NAME_START_TXN.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("StartTransactionExtendedRequest(");
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
