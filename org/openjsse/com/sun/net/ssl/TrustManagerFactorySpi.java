package org.openjsse.com.sun.net.ssl;

import java.security.KeyStoreException;
import java.security.KeyStore;

@Deprecated
public abstract class TrustManagerFactorySpi
{
    protected abstract void engineInit(final KeyStore p0) throws KeyStoreException;
    
    protected abstract TrustManager[] engineGetTrustManagers();
}
