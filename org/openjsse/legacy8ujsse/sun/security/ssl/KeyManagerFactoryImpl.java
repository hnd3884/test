package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.util.List;
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
        protected void engineInit(final KeyStore ks, final char[] password) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
            if (ks != null && Legacy8uJSSE.isFIPS() && ks.getProvider() != Legacy8uJSSE.cryptoProvider) {
                throw new KeyStoreException("FIPS mode: KeyStore must be from provider " + Legacy8uJSSE.cryptoProvider.getName());
            }
            this.keyManager = new SunX509KeyManagerImpl(ks, password);
            this.isInitialized = true;
        }
        
        @Override
        protected void engineInit(final ManagerFactoryParameters spec) throws InvalidAlgorithmParameterException {
            throw new InvalidAlgorithmParameterException("SunX509KeyManager does not use ManagerFactoryParameters");
        }
    }
    
    public static final class X509 extends KeyManagerFactoryImpl
    {
        @Override
        protected void engineInit(final KeyStore ks, final char[] password) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
            if (ks == null) {
                this.keyManager = new X509KeyManagerImpl(Collections.emptyList());
            }
            else {
                if (Legacy8uJSSE.isFIPS() && ks.getProvider() != Legacy8uJSSE.cryptoProvider) {
                    throw new KeyStoreException("FIPS mode: KeyStore must be from provider " + Legacy8uJSSE.cryptoProvider.getName());
                }
                try {
                    final KeyStore.Builder builder = KeyStore.Builder.newInstance(ks, new KeyStore.PasswordProtection(password));
                    this.keyManager = new X509KeyManagerImpl(builder);
                }
                catch (final RuntimeException e) {
                    throw new KeyStoreException("initialization failed", e);
                }
            }
            this.isInitialized = true;
        }
        
        @Override
        protected void engineInit(final ManagerFactoryParameters params) throws InvalidAlgorithmParameterException {
            if (!(params instanceof KeyStoreBuilderParameters)) {
                throw new InvalidAlgorithmParameterException("Parameters must be instance of KeyStoreBuilderParameters");
            }
            if (Legacy8uJSSE.isFIPS()) {
                throw new InvalidAlgorithmParameterException("FIPS mode: KeyStoreBuilderParameters not supported");
            }
            final List<KeyStore.Builder> builders = ((KeyStoreBuilderParameters)params).getParameters();
            this.keyManager = new X509KeyManagerImpl(builders);
            this.isInitialized = true;
        }
    }
}
