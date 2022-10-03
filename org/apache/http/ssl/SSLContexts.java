package org.apache.http.ssl;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.net.ssl.TrustManager;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;

public class SSLContexts
{
    public static SSLContext createDefault() throws SSLInitializationException {
        try {
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, null, null);
            return sslContext;
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new SSLInitializationException(ex.getMessage(), ex);
        }
        catch (final KeyManagementException ex2) {
            throw new SSLInitializationException(ex2.getMessage(), ex2);
        }
    }
    
    public static SSLContext createSystemDefault() throws SSLInitializationException {
        try {
            return SSLContext.getDefault();
        }
        catch (final NoSuchAlgorithmException ex) {
            return createDefault();
        }
    }
    
    public static SSLContextBuilder custom() {
        return SSLContextBuilder.create();
    }
}
