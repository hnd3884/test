package com.unboundid.ldap.sdk;

import com.unboundid.ldap.sdk.extensions.StartTLSExtendedRequest;
import com.unboundid.util.Validator;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLContext;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class StartTLSPostConnectProcessor implements PostConnectProcessor
{
    private final SSLContext sslContext;
    private final SSLSocketFactory sslSocketFactory;
    
    public StartTLSPostConnectProcessor(final SSLContext sslContext) {
        Validator.ensureNotNull(sslContext);
        this.sslContext = sslContext;
        this.sslSocketFactory = null;
    }
    
    public StartTLSPostConnectProcessor(final SSLSocketFactory sslSocketFactory) {
        Validator.ensureNotNull(sslSocketFactory);
        this.sslSocketFactory = sslSocketFactory;
        this.sslContext = null;
    }
    
    @Override
    public void processPreAuthenticatedConnection(final LDAPConnection connection) throws LDAPException {
        StartTLSExtendedRequest startTLSRequest;
        if (this.sslContext == null) {
            startTLSRequest = new StartTLSExtendedRequest(this.sslSocketFactory);
        }
        else {
            startTLSRequest = new StartTLSExtendedRequest(this.sslContext);
        }
        final LDAPConnectionOptions opts = connection.getConnectionOptions();
        startTLSRequest.setResponseTimeoutMillis(opts.getConnectTimeoutMillis());
        final ExtendedResult r = connection.processExtendedOperation(startTLSRequest);
        if (!r.getResultCode().equals(ResultCode.SUCCESS)) {
            throw new LDAPExtendedOperationException(r);
        }
    }
    
    @Override
    public void processPostAuthenticatedConnection(final LDAPConnection connection) throws LDAPException {
    }
}
