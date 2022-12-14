package io.netty.handler.ssl.util;

import javax.net.ssl.ManagerFactoryParameters;
import java.security.KeyStore;
import io.netty.util.internal.ObjectUtil;
import javax.net.ssl.TrustManager;

public final class TrustManagerFactoryWrapper extends SimpleTrustManagerFactory
{
    private final TrustManager tm;
    
    public TrustManagerFactoryWrapper(final TrustManager tm) {
        this.tm = ObjectUtil.checkNotNull(tm, "tm");
    }
    
    @Override
    protected void engineInit(final KeyStore keyStore) throws Exception {
    }
    
    @Override
    protected void engineInit(final ManagerFactoryParameters managerFactoryParameters) throws Exception {
    }
    
    @Override
    protected TrustManager[] engineGetTrustManagers() {
        return new TrustManager[] { this.tm };
    }
}
