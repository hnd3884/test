package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.LDAPExtendedOperationException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.ExtendedRequest;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import java.io.Serializable;
import com.unboundid.ldap.sdk.PostConnectProcessor;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class StartAdministrativeSessionPostConnectProcessor implements PostConnectProcessor, Serializable
{
    private static final long serialVersionUID = 3327980552475726214L;
    private final StartAdministrativeSessionExtendedRequest request;
    
    public StartAdministrativeSessionPostConnectProcessor(final StartAdministrativeSessionExtendedRequest request) {
        this.request = request;
    }
    
    @Override
    public void processPreAuthenticatedConnection(final LDAPConnection connection) throws LDAPException {
        final ExtendedResult result = connection.processExtendedOperation(this.request.duplicate());
        if (result.getResultCode() != ResultCode.SUCCESS) {
            throw new LDAPExtendedOperationException(result);
        }
    }
    
    @Override
    public void processPostAuthenticatedConnection(final LDAPConnection connection) throws LDAPException {
    }
}
