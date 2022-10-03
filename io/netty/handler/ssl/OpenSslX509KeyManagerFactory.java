package io.netty.handler.ssl;

import java.io.OutputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.security.cert.Certificate;
import io.netty.internal.tcnative.SSL;
import java.security.Key;
import java.util.Date;
import java.security.KeyStoreSpi;
import io.netty.util.ReferenceCountUtil;
import java.util.Iterator;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.KeyManager;
import java.security.InvalidAlgorithmParameterException;
import javax.net.ssl.ManagerFactoryParameters;
import java.util.Collections;
import java.security.KeyStore;
import java.io.InputStream;
import io.netty.util.internal.ObjectUtil;
import java.security.cert.X509Certificate;
import java.security.UnrecoverableKeyException;
import java.security.KeyStoreException;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.io.File;
import javax.net.ssl.KeyManagerFactorySpi;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import javax.net.ssl.KeyManagerFactory;

public final class OpenSslX509KeyManagerFactory extends KeyManagerFactory
{
    private final OpenSslKeyManagerFactorySpi spi;
    
    public OpenSslX509KeyManagerFactory() {
        this(newOpenSslKeyManagerFactorySpi(null));
    }
    
    public OpenSslX509KeyManagerFactory(final Provider provider) {
        this(newOpenSslKeyManagerFactorySpi(provider));
    }
    
    public OpenSslX509KeyManagerFactory(final String algorithm, final Provider provider) throws NoSuchAlgorithmException {
        this(newOpenSslKeyManagerFactorySpi(algorithm, provider));
    }
    
    private OpenSslX509KeyManagerFactory(final OpenSslKeyManagerFactorySpi spi) {
        super(spi, spi.kmf.getProvider(), spi.kmf.getAlgorithm());
        this.spi = spi;
    }
    
    private static OpenSslKeyManagerFactorySpi newOpenSslKeyManagerFactorySpi(final Provider provider) {
        try {
            return newOpenSslKeyManagerFactorySpi(null, provider);
        }
        catch (final NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
    
    private static OpenSslKeyManagerFactorySpi newOpenSslKeyManagerFactorySpi(String algorithm, final Provider provider) throws NoSuchAlgorithmException {
        if (algorithm == null) {
            algorithm = KeyManagerFactory.getDefaultAlgorithm();
        }
        return new OpenSslKeyManagerFactorySpi((provider == null) ? KeyManagerFactory.getInstance(algorithm) : KeyManagerFactory.getInstance(algorithm, provider));
    }
    
    OpenSslKeyMaterialProvider newProvider() {
        return this.spi.newProvider();
    }
    
    public static OpenSslX509KeyManagerFactory newEngineBased(final File certificateChain, final String password) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        return newEngineBased(SslContext.toX509Certificates(certificateChain), password);
    }
    
    public static OpenSslX509KeyManagerFactory newEngineBased(final X509Certificate[] certificateChain, final String password) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        ObjectUtil.checkNotNull(certificateChain, "certificateChain");
        final KeyStore store = new OpenSslKeyStore((X509Certificate[])certificateChain.clone(), false);
        store.load(null, null);
        final OpenSslX509KeyManagerFactory factory = new OpenSslX509KeyManagerFactory();
        factory.init(store, (char[])((password == null) ? null : password.toCharArray()));
        return factory;
    }
    
    public static OpenSslX509KeyManagerFactory newKeyless(final File chain) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        return newKeyless(SslContext.toX509Certificates(chain));
    }
    
    public static OpenSslX509KeyManagerFactory newKeyless(final InputStream chain) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        return newKeyless(SslContext.toX509Certificates(chain));
    }
    
    public static OpenSslX509KeyManagerFactory newKeyless(final X509Certificate... certificateChain) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        ObjectUtil.checkNotNull(certificateChain, "certificateChain");
        final KeyStore store = new OpenSslKeyStore((X509Certificate[])certificateChain.clone(), true);
        store.load(null, null);
        final OpenSslX509KeyManagerFactory factory = new OpenSslX509KeyManagerFactory();
        factory.init(store, null);
        return factory;
    }
    
    private static final class OpenSslKeyManagerFactorySpi extends KeyManagerFactorySpi
    {
        final KeyManagerFactory kmf;
        private volatile ProviderFactory providerFactory;
        
        OpenSslKeyManagerFactorySpi(final KeyManagerFactory kmf) {
            this.kmf = ObjectUtil.checkNotNull(kmf, "kmf");
        }
        
        @Override
        protected synchronized void engineInit(final KeyStore keyStore, final char[] chars) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
            if (this.providerFactory != null) {
                throw new KeyStoreException("Already initialized");
            }
            if (!keyStore.aliases().hasMoreElements()) {
                throw new KeyStoreException("No aliases found");
            }
            this.kmf.init(keyStore, chars);
            this.providerFactory = new ProviderFactory(ReferenceCountedOpenSslContext.chooseX509KeyManager(this.kmf.getKeyManagers()), password(chars), Collections.list(keyStore.aliases()));
        }
        
        private static String password(final char[] password) {
            if (password == null || password.length == 0) {
                return null;
            }
            return new String(password);
        }
        
        @Override
        protected void engineInit(final ManagerFactoryParameters managerFactoryParameters) throws InvalidAlgorithmParameterException {
            throw new InvalidAlgorithmParameterException("Not supported");
        }
        
        @Override
        protected KeyManager[] engineGetKeyManagers() {
            final ProviderFactory providerFactory = this.providerFactory;
            if (providerFactory == null) {
                throw new IllegalStateException("engineInit(...) not called yet");
            }
            return new KeyManager[] { providerFactory.keyManager };
        }
        
        OpenSslKeyMaterialProvider newProvider() {
            final ProviderFactory providerFactory = this.providerFactory;
            if (providerFactory == null) {
                throw new IllegalStateException("engineInit(...) not called yet");
            }
            return providerFactory.newProvider();
        }
        
        private static final class ProviderFactory
        {
            private final X509KeyManager keyManager;
            private final String password;
            private final Iterable<String> aliases;
            
            ProviderFactory(final X509KeyManager keyManager, final String password, final Iterable<String> aliases) {
                this.keyManager = keyManager;
                this.password = password;
                this.aliases = aliases;
            }
            
            OpenSslKeyMaterialProvider newProvider() {
                return new OpenSslPopulatedKeyMaterialProvider(this.keyManager, this.password, this.aliases);
            }
            
            private static final class OpenSslPopulatedKeyMaterialProvider extends OpenSslKeyMaterialProvider
            {
                private final Map<String, Object> materialMap;
                
                OpenSslPopulatedKeyMaterialProvider(final X509KeyManager keyManager, final String password, final Iterable<String> aliases) {
                    super(keyManager, password);
                    this.materialMap = new HashMap<String, Object>();
                    boolean initComplete = false;
                    try {
                        for (final String alias : aliases) {
                            if (alias != null && !this.materialMap.containsKey(alias)) {
                                try {
                                    this.materialMap.put(alias, super.chooseKeyMaterial(UnpooledByteBufAllocator.DEFAULT, alias));
                                }
                                catch (final Exception e) {
                                    this.materialMap.put(alias, e);
                                }
                            }
                        }
                        initComplete = true;
                    }
                    finally {
                        if (!initComplete) {
                            this.destroy();
                        }
                    }
                    ObjectUtil.checkNonEmpty(this.materialMap, "materialMap");
                }
                
                @Override
                OpenSslKeyMaterial chooseKeyMaterial(final ByteBufAllocator allocator, final String alias) throws Exception {
                    final Object value = this.materialMap.get(alias);
                    if (value == null) {
                        return null;
                    }
                    if (value instanceof OpenSslKeyMaterial) {
                        return ((OpenSslKeyMaterial)value).retain();
                    }
                    throw (Exception)value;
                }
                
                @Override
                void destroy() {
                    for (final Object material : this.materialMap.values()) {
                        ReferenceCountUtil.release(material);
                    }
                    this.materialMap.clear();
                }
            }
        }
    }
    
    private static final class OpenSslKeyStore extends KeyStore
    {
        private OpenSslKeyStore(final X509Certificate[] certificateChain, final boolean keyless) {
            super(new KeyStoreSpi() {
                private final Date creationDate = new Date();
                
                @Override
                public Key engineGetKey(final String alias, final char[] password) throws UnrecoverableKeyException {
                    if (this.engineContainsAlias(alias)) {
                        long privateKeyAddress;
                        if (keyless) {
                            privateKeyAddress = 0L;
                        }
                        else {
                            try {
                                privateKeyAddress = SSL.loadPrivateKeyFromEngine(alias, (password == null) ? null : new String(password));
                            }
                            catch (final Exception e) {
                                final UnrecoverableKeyException keyException = new UnrecoverableKeyException("Unable to load key from engine");
                                keyException.initCause(e);
                                throw keyException;
                            }
                        }
                        return new OpenSslPrivateKey(privateKeyAddress);
                    }
                    return null;
                }
                
                @Override
                public Certificate[] engineGetCertificateChain(final String alias) {
                    return (Certificate[])(this.engineContainsAlias(alias) ? ((X509Certificate[])certificateChain.clone()) : null);
                }
                
                @Override
                public Certificate engineGetCertificate(final String alias) {
                    return this.engineContainsAlias(alias) ? certificateChain[0] : null;
                }
                
                @Override
                public Date engineGetCreationDate(final String alias) {
                    return this.engineContainsAlias(alias) ? this.creationDate : null;
                }
                
                @Override
                public void engineSetKeyEntry(final String alias, final Key key, final char[] password, final Certificate[] chain) throws KeyStoreException {
                    throw new KeyStoreException("Not supported");
                }
                
                @Override
                public void engineSetKeyEntry(final String alias, final byte[] key, final Certificate[] chain) throws KeyStoreException {
                    throw new KeyStoreException("Not supported");
                }
                
                @Override
                public void engineSetCertificateEntry(final String alias, final Certificate cert) throws KeyStoreException {
                    throw new KeyStoreException("Not supported");
                }
                
                @Override
                public void engineDeleteEntry(final String alias) throws KeyStoreException {
                    throw new KeyStoreException("Not supported");
                }
                
                @Override
                public Enumeration<String> engineAliases() {
                    return Collections.enumeration(Collections.singleton("key"));
                }
                
                @Override
                public boolean engineContainsAlias(final String alias) {
                    return "key".equals(alias);
                }
                
                @Override
                public int engineSize() {
                    return 1;
                }
                
                @Override
                public boolean engineIsKeyEntry(final String alias) {
                    return this.engineContainsAlias(alias);
                }
                
                @Override
                public boolean engineIsCertificateEntry(final String alias) {
                    return this.engineContainsAlias(alias);
                }
                
                @Override
                public String engineGetCertificateAlias(final Certificate cert) {
                    if (cert instanceof X509Certificate) {
                        for (final X509Certificate x509Certificate : certificateChain) {
                            if (x509Certificate.equals(cert)) {
                                return "key";
                            }
                        }
                    }
                    return null;
                }
                
                @Override
                public void engineStore(final OutputStream stream, final char[] password) {
                    throw new UnsupportedOperationException();
                }
                
                @Override
                public void engineLoad(final InputStream stream, final char[] password) {
                    if (stream != null && password != null) {
                        throw new UnsupportedOperationException();
                    }
                }
            }, null, "native");
            OpenSsl.ensureAvailability();
        }
    }
}
