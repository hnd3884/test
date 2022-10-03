package com.unboundid.util.ssl;

import com.unboundid.ldap.sdk.LDAPException;
import javax.net.ssl.SSLSocket;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class TrustAllSSLSocketVerifier extends SSLSocketVerifier
{
    private static final TrustAllSSLSocketVerifier INSTANCE;
    
    private TrustAllSSLSocketVerifier() {
    }
    
    public static TrustAllSSLSocketVerifier getInstance() {
        return TrustAllSSLSocketVerifier.INSTANCE;
    }
    
    @Override
    public void verifySSLSocket(final String host, final int port, final SSLSocket sslSocket) throws LDAPException {
    }
    
    static {
        INSTANCE = new TrustAllSSLSocketVerifier();
    }
}
