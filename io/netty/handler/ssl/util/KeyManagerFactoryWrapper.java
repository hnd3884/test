package io.netty.handler.ssl.util;

import javax.net.ssl.ManagerFactoryParameters;
import java.security.KeyStore;
import io.netty.util.internal.ObjectUtil;
import javax.net.ssl.KeyManager;

public final class KeyManagerFactoryWrapper extends SimpleKeyManagerFactory
{
    private final KeyManager km;
    
    public KeyManagerFactoryWrapper(final KeyManager km) {
        this.km = ObjectUtil.checkNotNull(km, "km");
    }
    
    @Override
    protected void engineInit(final KeyStore keyStore, final char[] var2) throws Exception {
    }
    
    @Override
    protected void engineInit(final ManagerFactoryParameters managerFactoryParameters) throws Exception {
    }
    
    @Override
    protected KeyManager[] engineGetKeyManagers() {
        return new KeyManager[] { this.km };
    }
}
