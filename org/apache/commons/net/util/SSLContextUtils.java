package org.apache.commons.net.util;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.io.IOException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.KeyManager;

public class SSLContextUtils
{
    private SSLContextUtils() {
    }
    
    public static SSLContext createSSLContext(final String protocol, final KeyManager keyManager, final TrustManager trustManager) throws IOException {
        return createSSLContext(protocol, (KeyManager[])((keyManager == null) ? null : new KeyManager[] { keyManager }), (TrustManager[])((trustManager == null) ? null : new TrustManager[] { trustManager }));
    }
    
    public static SSLContext createSSLContext(final String protocol, final KeyManager[] keyManagers, final TrustManager[] trustManagers) throws IOException {
        SSLContext ctx;
        try {
            ctx = SSLContext.getInstance(protocol);
            ctx.init(keyManagers, trustManagers, null);
        }
        catch (final GeneralSecurityException e) {
            final IOException ioe = new IOException("Could not initialize SSL context");
            ioe.initCause(e);
            throw ioe;
        }
        return ctx;
    }
}
