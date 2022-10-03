package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.ldap.sdk.LDAPRequest;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedRequest;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class GetConnectionIDExtendedRequest extends ExtendedRequest
{
    public static final String GET_CONNECTION_ID_REQUEST_OID = "1.3.6.1.4.1.30221.1.6.2";
    private static final long serialVersionUID = 4787797927715098127L;
    
    public GetConnectionIDExtendedRequest() {
        this((Control[])null);
    }
    
    public GetConnectionIDExtendedRequest(final Control[] controls) {
        super("1.3.6.1.4.1.30221.1.6.2", null, controls);
    }
    
    public GetConnectionIDExtendedRequest(final ExtendedRequest extendedRequest) throws LDAPException {
        super(extendedRequest);
        if (extendedRequest.hasValue()) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GET_CONN_ID_REQUEST_HAS_VALUE.get());
        }
    }
    
    public GetConnectionIDExtendedResult process(final LDAPConnection connection, final int depth) throws LDAPException {
        final ExtendedResult extendedResponse = super.process(connection, depth);
        return new GetConnectionIDExtendedResult(extendedResponse);
    }
    
    @Override
    public GetConnectionIDExtendedRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public GetConnectionIDExtendedRequest duplicate(final Control[] controls) {
        final GetConnectionIDExtendedRequest r = new GetConnectionIDExtendedRequest(controls);
        r.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return r;
    }
    
    @Override
    public String getExtendedRequestName() {
        return ExtOpMessages.INFO_EXTENDED_REQUEST_NAME_GET_CONNECTION_ID.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("GetConnectionIDExtendedRequest(");
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
