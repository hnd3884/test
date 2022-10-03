package io.netty.handler.ssl;

import javax.net.ssl.X509KeyManager;
import io.netty.util.internal.ObjectUtil;
import javax.net.ssl.KeyManager;
import java.security.InvalidAlgorithmParameterException;
import javax.net.ssl.ManagerFactoryParameters;
import java.security.UnrecoverableKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.KeyStoreException;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactorySpi;
import javax.net.ssl.KeyManagerFactory;

public final class OpenSslCachingX509KeyManagerFactory extends KeyManagerFactory
{
    private final int maxCachedEntries;
    
    public OpenSslCachingX509KeyManagerFactory(final KeyManagerFactory factory) {
        this(factory, 1024);
    }
    
    public OpenSslCachingX509KeyManagerFactory(final KeyManagerFactory factory, final int maxCachedEntries) {
        super(new KeyManagerFactorySpi() {
            @Override
            protected void engineInit(final KeyStore keyStore, final char[] chars) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
                factory.init(keyStore, chars);
            }
            
            @Override
            protected void engineInit(final ManagerFactoryParameters managerFactoryParameters) throws InvalidAlgorithmParameterException {
                factory.init(managerFactoryParameters);
            }
            
            @Override
            protected KeyManager[] engineGetKeyManagers() {
                return factory.getKeyManagers();
            }
        }, factory.getProvider(), factory.getAlgorithm());
        this.maxCachedEntries = ObjectUtil.checkPositive(maxCachedEntries, "maxCachedEntries");
    }
    
    OpenSslKeyMaterialProvider newProvider(final String password) {
        final X509KeyManager keyManager = ReferenceCountedOpenSslContext.chooseX509KeyManager(this.getKeyManagers());
        if ("sun.security.ssl.X509KeyManagerImpl".equals(keyManager.getClass().getName())) {
            return new OpenSslKeyMaterialProvider(keyManager, password);
        }
        return new OpenSslCachingKeyMaterialProvider(ReferenceCountedOpenSslContext.chooseX509KeyManager(this.getKeyManagers()), password, this.maxCachedEntries);
    }
}
