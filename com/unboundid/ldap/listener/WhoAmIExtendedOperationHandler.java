package com.unboundid.ldap.listener;

import com.unboundid.ldap.sdk.extensions.WhoAmIExtendedResult;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.ExtendedRequest;
import java.util.Collections;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class WhoAmIExtendedOperationHandler extends InMemoryExtendedOperationHandler
{
    @Override
    public String getExtendedOperationHandlerName() {
        return "Who Am I?";
    }
    
    @Override
    public List<String> getSupportedExtendedRequestOIDs() {
        return Collections.singletonList("1.3.6.1.4.1.4203.1.11.3");
    }
    
    @Override
    public ExtendedResult processExtendedOperation(final InMemoryRequestHandler handler, final int messageID, final ExtendedRequest request) {
        for (final Control c : request.getControls()) {
            if (c.isCritical()) {
                return new ExtendedResult(messageID, ResultCode.UNAVAILABLE_CRITICAL_EXTENSION, ListenerMessages.ERR_WHO_AM_I_EXTOP_UNSUPPORTED_CONTROL.get(c.getOID()), null, null, null, null, null);
            }
        }
        final String authorizationID = "dn:" + handler.getAuthenticatedDN().toString();
        return new WhoAmIExtendedResult(messageID, ResultCode.SUCCESS, null, null, null, authorizationID, null);
    }
}
