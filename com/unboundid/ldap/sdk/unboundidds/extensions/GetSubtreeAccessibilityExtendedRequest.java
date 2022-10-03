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
public final class GetSubtreeAccessibilityExtendedRequest extends ExtendedRequest
{
    public static final String GET_SUBTREE_ACCESSIBILITY_REQUEST_OID = "1.3.6.1.4.1.30221.1.6.20";
    private static final long serialVersionUID = 6519976409372387402L;
    
    public GetSubtreeAccessibilityExtendedRequest(final Control... controls) {
        super("1.3.6.1.4.1.30221.1.6.20", null, controls);
    }
    
    public GetSubtreeAccessibilityExtendedRequest(final ExtendedRequest extendedRequest) throws LDAPException {
        super(extendedRequest);
        if (extendedRequest.hasValue()) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GET_SUBTREE_ACCESSIBILITY_REQUEST_HAS_VALUE.get());
        }
    }
    
    public GetSubtreeAccessibilityExtendedResult process(final LDAPConnection connection, final int depth) throws LDAPException {
        final ExtendedResult extendedResponse = super.process(connection, depth);
        return new GetSubtreeAccessibilityExtendedResult(extendedResponse);
    }
    
    @Override
    public GetSubtreeAccessibilityExtendedRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public GetSubtreeAccessibilityExtendedRequest duplicate(final Control[] controls) {
        final GetSubtreeAccessibilityExtendedRequest r = new GetSubtreeAccessibilityExtendedRequest(controls);
        r.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return r;
    }
    
    @Override
    public String getExtendedRequestName() {
        return ExtOpMessages.INFO_EXTENDED_REQUEST_NAME_GET_SUBTREE_ACCESSIBILITY.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("GetSubtreeAccessibilityExtendedRequest(");
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
