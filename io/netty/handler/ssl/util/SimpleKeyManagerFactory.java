package io.netty.handler.ssl.util;

import io.netty.util.internal.SuppressJava6Requirement;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509KeyManager;
import io.netty.util.internal.PlatformDependent;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStoreException;
import javax.net.ssl.KeyManager;
import javax.net.ssl.ManagerFactoryParameters;
import java.security.KeyStore;
import io.netty.util.internal.ObjectUtil;
import javax.net.ssl.KeyManagerFactorySpi;
import io.netty.util.concurrent.FastThreadLocal;
import java.security.Provider;
import javax.net.ssl.KeyManagerFactory;

public abstract class SimpleKeyManagerFactory extends KeyManagerFactory
{
    private static final Provider PROVIDER;
    private static final FastThreadLocal<SimpleKeyManagerFactorySpi> CURRENT_SPI;
    
    protected SimpleKeyManagerFactory() {
        this("");
    }
    
    protected SimpleKeyManagerFactory(final String name) {
        super(SimpleKeyManagerFactory.CURRENT_SPI.get(), SimpleKeyManagerFactory.PROVIDER, ObjectUtil.checkNotNull(name, "name"));
        SimpleKeyManagerFactory.CURRENT_SPI.get().init(this);
        SimpleKeyManagerFactory.CURRENT_SPI.remove();
    }
    
    protected abstract void engineInit(final KeyStore p0, final char[] p1) throws Exception;
    
    protected abstract void engineInit(final ManagerFactoryParameters p0) throws Exception;
    
    protected abstract KeyManager[] engineGetKeyManagers();
    
    static {
        PROVIDER = new Provider("", 0.0, "") {
            private static final long serialVersionUID = -2680540247105807895L;
        };
        CURRENT_SPI = new FastThreadLocal<SimpleKeyManagerFactorySpi>() {
            @Override
            protected SimpleKeyManagerFactorySpi initialValue() {
                return new SimpleKeyManagerFactorySpi();
            }
        };
    }
    
    private static final class SimpleKeyManagerFactorySpi extends KeyManagerFactorySpi
    {
        private SimpleKeyManagerFactory parent;
        private volatile KeyManager[] keyManagers;
        
        void init(final SimpleKeyManagerFactory parent) {
            this.parent = parent;
        }
        
        @Override
        protected void engineInit(final KeyStore keyStore, final char[] pwd) throws KeyStoreException {
            try {
                this.parent.engineInit(keyStore, pwd);
            }
            catch (final KeyStoreException e) {
                throw e;
            }
            catch (final Exception e2) {
                throw new KeyStoreException(e2);
            }
        }
        
        @Override
        protected void engineInit(final ManagerFactoryParameters managerFactoryParameters) throws InvalidAlgorithmParameterException {
            try {
                this.parent.engineInit(managerFactoryParameters);
            }
            catch (final InvalidAlgorithmParameterException e) {
                throw e;
            }
            catch (final Exception e2) {
                throw new InvalidAlgorithmParameterException(e2);
            }
        }
        
        @Override
        protected KeyManager[] engineGetKeyManagers() {
            KeyManager[] keyManagers = this.keyManagers;
            if (keyManagers == null) {
                keyManagers = this.parent.engineGetKeyManagers();
                if (PlatformDependent.javaVersion() >= 7) {
                    wrapIfNeeded(keyManagers);
                }
                this.keyManagers = keyManagers;
            }
            return keyManagers.clone();
        }
        
        @SuppressJava6Requirement(reason = "Usage guarded by java version check")
        private static void wrapIfNeeded(final KeyManager[] keyManagers) {
            for (int i = 0; i < keyManagers.length; ++i) {
                final KeyManager tm = keyManagers[i];
                if (tm instanceof X509KeyManager && !(tm instanceof X509ExtendedKeyManager)) {
                    keyManagers[i] = new X509KeyManagerWrapper((X509KeyManager)tm);
                }
            }
        }
    }
}
