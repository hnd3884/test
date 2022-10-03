package org.openjsse.com.sun.net.ssl;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.security.KeyManagementException;
import java.security.SecureRandom;

@Deprecated
public abstract class SSLContextSpi
{
    protected abstract void engineInit(final KeyManager[] p0, final TrustManager[] p1, final SecureRandom p2) throws KeyManagementException;
    
    protected abstract SSLSocketFactory engineGetSocketFactory();
    
    protected abstract SSLServerSocketFactory engineGetServerSocketFactory();
}
