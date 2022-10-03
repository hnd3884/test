package sun.security.ssl;

import javax.net.ssl.KeyStoreBuilderParameters;
import java.util.Collections;
import java.security.InvalidAlgorithmParameterException;
import javax.net.ssl.ManagerFactoryParameters;
import java.security.UnrecoverableKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.KeyStoreException;
import java.security.KeyStore;
import javax.net.ssl.KeyManager;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.KeyManagerFactorySpi;

abstract class KeyManagerFactoryImpl extends KeyManagerFactorySpi
{
    X509ExtendedKeyManager keyManager;
    boolean isInitialized;
    
    @Override
    protected KeyManager[] engineGetKeyManagers() {
        if (!this.isInitialized) {
            throw new IllegalStateException("KeyManagerFactoryImpl is not initialized");
        }
        return new KeyManager[] { this.keyManager };
    }
    
    public static final class SunX509 extends KeyManagerFactoryImpl
    {
        @Override
        protected void engineInit(final KeyStore keyStore, final char[] array) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
            if (keyStore != null && SunJSSE.isFIPS() && keyStore.getProvider() != SunJSSE.cryptoProvider) {
                throw new KeyStoreException("FIPS mode: KeyStore must be from provider " + SunJSSE.cryptoProvider.getName());
            }
            this.keyManager = new SunX509KeyManagerImpl(keyStore, array);
            this.isInitialized = true;
        }
        
        @Override
        protected void engineInit(final ManagerFactoryParameters managerFactoryParameters) throws InvalidAlgorithmParameterException {
            throw new InvalidAlgorithmParameterException("SunX509KeyManager does not use ManagerFactoryParameters");
        }
    }
    
    public static final class X509 extends KeyManagerFactoryImpl
    {
        @Override
        protected void engineInit(final KeyStore keyStore, final char[] array) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
            if (keyStore == null) {
                this.keyManager = new X509KeyManagerImpl(Collections.emptyList());
            }
            else {
                if (SunJSSE.isFIPS() && keyStore.getProvider() != SunJSSE.cryptoProvider) {
                    throw new KeyStoreException("FIPS mode: KeyStore must be from provider " + SunJSSE.cryptoProvider.getName());
                }
                try {
                    this.keyManager = new X509KeyManagerImpl(KeyStore.Builder.newInstance(keyStore, new KeyStore.PasswordProtection(array)));
                }
                catch (final RuntimeException ex) {
                    throw new KeyStoreException("initialization failed", ex);
                }
            }
            this.isInitialized = true;
        }
        
        @Override
        protected void engineInit(final ManagerFactoryParameters managerFactoryParameters) throws InvalidAlgorithmParameterException {
            if (!(managerFactoryParameters instanceof KeyStoreBuilderParameters)) {
                throw new InvalidAlgorithmParameterException("Parameters must be instance of KeyStoreBuilderParameters");
            }
            if (SunJSSE.isFIPS()) {
                throw new InvalidAlgorithmParameterException("FIPS mode: KeyStoreBuilderParameters not supported");
            }
            this.keyManager = new X509KeyManagerImpl(((KeyStoreBuilderParameters)managerFactoryParameters).getParameters());
            this.isInitialized = true;
        }
    }
}
