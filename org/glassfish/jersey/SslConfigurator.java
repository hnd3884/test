package org.glassfish.jersey;

import java.util.Arrays;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.SecureRandom;
import javax.net.ssl.TrustManager;
import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManagerFactory;
import java.security.UnrecoverableKeyException;
import javax.net.ssl.KeyManagerFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.io.FileNotFoundException;
import java.security.cert.CertificateException;
import java.security.KeyStoreException;
import org.glassfish.jersey.internal.LocalizationMessages;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.security.AccessController;
import org.glassfish.jersey.internal.util.PropertiesHelper;
import java.util.Properties;
import javax.net.ssl.SSLContext;
import java.security.KeyStore;
import java.util.logging.Logger;

public final class SslConfigurator
{
    public static final String TRUST_STORE_PROVIDER = "javax.net.ssl.trustStoreProvider";
    public static final String KEY_STORE_PROVIDER = "javax.net.ssl.keyStoreProvider";
    public static final String TRUST_STORE_FILE = "javax.net.ssl.trustStore";
    public static final String KEY_STORE_FILE = "javax.net.ssl.keyStore";
    public static final String TRUST_STORE_PASSWORD = "javax.net.ssl.trustStorePassword";
    public static final String KEY_STORE_PASSWORD = "javax.net.ssl.keyStorePassword";
    public static final String TRUST_STORE_TYPE = "javax.net.ssl.trustStoreType";
    public static final String KEY_STORE_TYPE = "javax.net.ssl.keyStoreType";
    public static final String KEY_MANAGER_FACTORY_ALGORITHM = "ssl.keyManagerFactory.algorithm";
    public static final String KEY_MANAGER_FACTORY_PROVIDER = "ssl.keyManagerFactory.provider";
    public static final String TRUST_MANAGER_FACTORY_ALGORITHM = "ssl.trustManagerFactory.algorithm";
    public static final String TRUST_MANAGER_FACTORY_PROVIDER = "ssl.trustManagerFactory.provider";
    private static final SslConfigurator DEFAULT_CONFIG_NO_PROPS;
    private static final Logger LOGGER;
    private KeyStore keyStore;
    private KeyStore trustStore;
    private String trustStoreProvider;
    private String keyStoreProvider;
    private String trustStoreType;
    private String keyStoreType;
    private char[] trustStorePass;
    private char[] keyStorePass;
    private char[] keyPass;
    private String trustStoreFile;
    private String keyStoreFile;
    private byte[] trustStoreBytes;
    private byte[] keyStoreBytes;
    private String trustManagerFactoryAlgorithm;
    private String keyManagerFactoryAlgorithm;
    private String trustManagerFactoryProvider;
    private String keyManagerFactoryProvider;
    private String securityProtocol;
    
    public static SSLContext getDefaultContext() {
        return getDefaultContext(true);
    }
    
    public static SSLContext getDefaultContext(final boolean readSystemProperties) {
        if (readSystemProperties) {
            return new SslConfigurator(true).createSSLContext();
        }
        return SslConfigurator.DEFAULT_CONFIG_NO_PROPS.createSSLContext();
    }
    
    public static SslConfigurator newInstance() {
        return new SslConfigurator(false);
    }
    
    public static SslConfigurator newInstance(final boolean readSystemProperties) {
        return new SslConfigurator(readSystemProperties);
    }
    
    private SslConfigurator(final boolean readSystemProperties) {
        this.securityProtocol = "TLS";
        if (readSystemProperties) {
            this.retrieve(AccessController.doPrivileged(PropertiesHelper.getSystemProperties()));
        }
    }
    
    private SslConfigurator(final SslConfigurator that) {
        this.securityProtocol = "TLS";
        this.keyStore = that.keyStore;
        this.trustStore = that.trustStore;
        this.trustStoreProvider = that.trustStoreProvider;
        this.keyStoreProvider = that.keyStoreProvider;
        this.trustStoreType = that.trustStoreType;
        this.keyStoreType = that.keyStoreType;
        this.trustStorePass = that.trustStorePass;
        this.keyStorePass = that.keyStorePass;
        this.keyPass = that.keyPass;
        this.trustStoreFile = that.trustStoreFile;
        this.keyStoreFile = that.keyStoreFile;
        this.trustStoreBytes = that.trustStoreBytes;
        this.keyStoreBytes = that.keyStoreBytes;
        this.trustManagerFactoryAlgorithm = that.trustManagerFactoryAlgorithm;
        this.keyManagerFactoryAlgorithm = that.keyManagerFactoryAlgorithm;
        this.trustManagerFactoryProvider = that.trustManagerFactoryProvider;
        this.keyManagerFactoryProvider = that.keyManagerFactoryProvider;
        this.securityProtocol = that.securityProtocol;
    }
    
    public SslConfigurator copy() {
        return new SslConfigurator(this);
    }
    
    public SslConfigurator trustStoreProvider(final String trustStoreProvider) {
        this.trustStoreProvider = trustStoreProvider;
        return this;
    }
    
    public SslConfigurator keyStoreProvider(final String keyStoreProvider) {
        this.keyStoreProvider = keyStoreProvider;
        return this;
    }
    
    public SslConfigurator trustStoreType(final String trustStoreType) {
        this.trustStoreType = trustStoreType;
        return this;
    }
    
    public SslConfigurator keyStoreType(final String keyStoreType) {
        this.keyStoreType = keyStoreType;
        return this;
    }
    
    public SslConfigurator trustStorePassword(final String password) {
        this.trustStorePass = password.toCharArray();
        return this;
    }
    
    public SslConfigurator keyStorePassword(final String password) {
        this.keyStorePass = password.toCharArray();
        return this;
    }
    
    public SslConfigurator keyStorePassword(final char[] password) {
        this.keyStorePass = password.clone();
        return this;
    }
    
    public SslConfigurator keyPassword(final String password) {
        this.keyPass = password.toCharArray();
        return this;
    }
    
    public SslConfigurator keyPassword(final char[] password) {
        this.keyPass = password.clone();
        return this;
    }
    
    public SslConfigurator trustStoreFile(final String fileName) {
        this.trustStoreFile = fileName;
        this.trustStoreBytes = null;
        this.trustStore = null;
        return this;
    }
    
    public SslConfigurator trustStoreBytes(final byte[] payload) {
        this.trustStoreBytes = payload.clone();
        this.trustStoreFile = null;
        this.trustStore = null;
        return this;
    }
    
    public SslConfigurator keyStoreFile(final String fileName) {
        this.keyStoreFile = fileName;
        this.keyStoreBytes = null;
        this.keyStore = null;
        return this;
    }
    
    public SslConfigurator keyStoreBytes(final byte[] payload) {
        this.keyStoreBytes = payload.clone();
        this.keyStoreFile = null;
        this.keyStore = null;
        return this;
    }
    
    public SslConfigurator trustManagerFactoryAlgorithm(final String algorithm) {
        this.trustManagerFactoryAlgorithm = algorithm;
        return this;
    }
    
    public SslConfigurator keyManagerFactoryAlgorithm(final String algorithm) {
        this.keyManagerFactoryAlgorithm = algorithm;
        return this;
    }
    
    public SslConfigurator trustManagerFactoryProvider(final String provider) {
        this.trustManagerFactoryProvider = provider;
        return this;
    }
    
    public SslConfigurator keyManagerFactoryProvider(final String provider) {
        this.keyManagerFactoryProvider = provider;
        return this;
    }
    
    public SslConfigurator securityProtocol(final String protocol) {
        this.securityProtocol = protocol;
        return this;
    }
    
    KeyStore getKeyStore() {
        return this.keyStore;
    }
    
    public SslConfigurator keyStore(final KeyStore keyStore) {
        this.keyStore = keyStore;
        this.keyStoreFile = null;
        this.keyStoreBytes = null;
        return this;
    }
    
    KeyStore getTrustStore() {
        return this.trustStore;
    }
    
    public SslConfigurator trustStore(final KeyStore trustStore) {
        this.trustStore = trustStore;
        this.trustStoreFile = null;
        this.trustStoreBytes = null;
        return this;
    }
    
    public SSLContext createSSLContext() {
        TrustManagerFactory trustManagerFactory = null;
        KeyManagerFactory keyManagerFactory = null;
        KeyStore _keyStore = this.keyStore;
        Label_0283: {
            if (_keyStore == null) {
                if (this.keyStoreBytes == null) {
                    if (this.keyStoreFile == null) {
                        break Label_0283;
                    }
                }
                try {
                    if (this.keyStoreProvider != null) {
                        _keyStore = KeyStore.getInstance((this.keyStoreType != null) ? this.keyStoreType : KeyStore.getDefaultType(), this.keyStoreProvider);
                    }
                    else {
                        _keyStore = KeyStore.getInstance((this.keyStoreType != null) ? this.keyStoreType : KeyStore.getDefaultType());
                    }
                    InputStream keyStoreInputStream = null;
                    try {
                        if (this.keyStoreBytes != null) {
                            keyStoreInputStream = new ByteArrayInputStream(this.keyStoreBytes);
                        }
                        else if (!this.keyStoreFile.equals("NONE")) {
                            keyStoreInputStream = new FileInputStream(this.keyStoreFile);
                        }
                        _keyStore.load(keyStoreInputStream, this.keyStorePass);
                    }
                    finally {
                        try {
                            if (keyStoreInputStream != null) {
                                keyStoreInputStream.close();
                            }
                        }
                        catch (final IOException ex) {}
                    }
                }
                catch (final KeyStoreException e) {
                    throw new IllegalStateException(LocalizationMessages.SSL_KS_IMPL_NOT_FOUND(), e);
                }
                catch (final CertificateException e2) {
                    throw new IllegalStateException(LocalizationMessages.SSL_KS_CERT_LOAD_ERROR(), e2);
                }
                catch (final FileNotFoundException e3) {
                    throw new IllegalStateException(LocalizationMessages.SSL_KS_FILE_NOT_FOUND(this.keyStoreFile), e3);
                }
                catch (final IOException e4) {
                    throw new IllegalStateException(LocalizationMessages.SSL_KS_LOAD_ERROR(this.keyStoreFile), e4);
                }
                catch (final NoSuchProviderException e5) {
                    throw new IllegalStateException(LocalizationMessages.SSL_KS_PROVIDERS_NOT_REGISTERED(), e5);
                }
                catch (final NoSuchAlgorithmException e6) {
                    throw new IllegalStateException(LocalizationMessages.SSL_KS_INTEGRITY_ALGORITHM_NOT_FOUND(), e6);
                }
            }
        }
        if (_keyStore != null) {
            String kmfAlgorithm = this.keyManagerFactoryAlgorithm;
            if (kmfAlgorithm == null) {
                kmfAlgorithm = AccessController.doPrivileged(PropertiesHelper.getSystemProperty("ssl.keyManagerFactory.algorithm", KeyManagerFactory.getDefaultAlgorithm()));
            }
            try {
                if (this.keyManagerFactoryProvider != null) {
                    keyManagerFactory = KeyManagerFactory.getInstance(kmfAlgorithm, this.keyManagerFactoryProvider);
                }
                else {
                    keyManagerFactory = KeyManagerFactory.getInstance(kmfAlgorithm);
                }
                final char[] password = (this.keyPass != null) ? this.keyPass : this.keyStorePass;
                if (password != null) {
                    keyManagerFactory.init(_keyStore, password);
                }
                else {
                    final String ksName = (this.keyStoreProvider != null) ? LocalizationMessages.SSL_KMF_NO_PASSWORD_FOR_PROVIDER_BASED_KS() : ((this.keyStoreBytes != null) ? LocalizationMessages.SSL_KMF_NO_PASSWORD_FOR_BYTE_BASED_KS() : this.keyStoreFile);
                    SslConfigurator.LOGGER.config(LocalizationMessages.SSL_KMF_NO_PASSWORD_SET(ksName));
                    keyManagerFactory = null;
                }
            }
            catch (final KeyStoreException e7) {
                throw new IllegalStateException(LocalizationMessages.SSL_KMF_INIT_FAILED(), e7);
            }
            catch (final UnrecoverableKeyException e8) {
                throw new IllegalStateException(LocalizationMessages.SSL_KMF_UNRECOVERABLE_KEY(), e8);
            }
            catch (final NoSuchAlgorithmException e9) {
                throw new IllegalStateException(LocalizationMessages.SSL_KMF_ALGORITHM_NOT_SUPPORTED(), e9);
            }
            catch (final NoSuchProviderException e10) {
                throw new IllegalStateException(LocalizationMessages.SSL_KMF_PROVIDER_NOT_REGISTERED(), e10);
            }
        }
        KeyStore _trustStore = this.trustStore;
        Label_0767: {
            if (_trustStore == null) {
                if (this.trustStoreBytes == null) {
                    if (this.trustStoreFile == null) {
                        break Label_0767;
                    }
                }
                try {
                    if (this.trustStoreProvider != null) {
                        _trustStore = KeyStore.getInstance((this.trustStoreType != null) ? this.trustStoreType : KeyStore.getDefaultType(), this.trustStoreProvider);
                    }
                    else {
                        _trustStore = KeyStore.getInstance((this.trustStoreType != null) ? this.trustStoreType : KeyStore.getDefaultType());
                    }
                    InputStream trustStoreInputStream = null;
                    try {
                        if (this.trustStoreBytes != null) {
                            trustStoreInputStream = new ByteArrayInputStream(this.trustStoreBytes);
                        }
                        else if (!this.trustStoreFile.equals("NONE")) {
                            trustStoreInputStream = new FileInputStream(this.trustStoreFile);
                        }
                        _trustStore.load(trustStoreInputStream, this.trustStorePass);
                    }
                    finally {
                        try {
                            if (trustStoreInputStream != null) {
                                trustStoreInputStream.close();
                            }
                        }
                        catch (final IOException ex2) {}
                    }
                }
                catch (final KeyStoreException e7) {
                    throw new IllegalStateException(LocalizationMessages.SSL_TS_IMPL_NOT_FOUND(), e7);
                }
                catch (final CertificateException e11) {
                    throw new IllegalStateException(LocalizationMessages.SSL_TS_CERT_LOAD_ERROR(), e11);
                }
                catch (final FileNotFoundException e12) {
                    throw new IllegalStateException(LocalizationMessages.SSL_TS_FILE_NOT_FOUND(this.trustStoreFile), e12);
                }
                catch (final IOException e13) {
                    throw new IllegalStateException(LocalizationMessages.SSL_TS_LOAD_ERROR(this.trustStoreFile), e13);
                }
                catch (final NoSuchProviderException e10) {
                    throw new IllegalStateException(LocalizationMessages.SSL_TS_PROVIDERS_NOT_REGISTERED(), e10);
                }
                catch (final NoSuchAlgorithmException e9) {
                    throw new IllegalStateException(LocalizationMessages.SSL_TS_INTEGRITY_ALGORITHM_NOT_FOUND(), e9);
                }
            }
        }
        if (_trustStore != null) {
            String tmfAlgorithm = this.trustManagerFactoryAlgorithm;
            if (tmfAlgorithm == null) {
                tmfAlgorithm = AccessController.doPrivileged(PropertiesHelper.getSystemProperty("ssl.trustManagerFactory.algorithm", TrustManagerFactory.getDefaultAlgorithm()));
            }
            try {
                if (this.trustManagerFactoryProvider != null) {
                    trustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm, this.trustManagerFactoryProvider);
                }
                else {
                    trustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm);
                }
                trustManagerFactory.init(_trustStore);
            }
            catch (final KeyStoreException e14) {
                throw new IllegalStateException(LocalizationMessages.SSL_TMF_INIT_FAILED(), e14);
            }
            catch (final NoSuchAlgorithmException e15) {
                throw new IllegalStateException(LocalizationMessages.SSL_TMF_ALGORITHM_NOT_SUPPORTED(), e15);
            }
            catch (final NoSuchProviderException e16) {
                throw new IllegalStateException(LocalizationMessages.SSL_TMF_PROVIDER_NOT_REGISTERED(), e16);
            }
        }
        try {
            String secProtocol = "TLS";
            if (this.securityProtocol != null) {
                secProtocol = this.securityProtocol;
            }
            final SSLContext sslContext = SSLContext.getInstance(secProtocol);
            sslContext.init((KeyManager[])((keyManagerFactory != null) ? keyManagerFactory.getKeyManagers() : null), (TrustManager[])((trustManagerFactory != null) ? trustManagerFactory.getTrustManagers() : null), null);
            return sslContext;
        }
        catch (final KeyManagementException e17) {
            throw new IllegalStateException(LocalizationMessages.SSL_CTX_INIT_FAILED(), e17);
        }
        catch (final NoSuchAlgorithmException e9) {
            throw new IllegalStateException(LocalizationMessages.SSL_CTX_ALGORITHM_NOT_SUPPORTED(), e9);
        }
    }
    
    public SslConfigurator retrieve(final Properties props) {
        this.trustStoreProvider = props.getProperty("javax.net.ssl.trustStoreProvider");
        this.keyStoreProvider = props.getProperty("javax.net.ssl.keyStoreProvider");
        this.trustManagerFactoryProvider = props.getProperty("ssl.trustManagerFactory.provider");
        this.keyManagerFactoryProvider = props.getProperty("ssl.keyManagerFactory.provider");
        this.trustStoreType = props.getProperty("javax.net.ssl.trustStoreType");
        this.keyStoreType = props.getProperty("javax.net.ssl.keyStoreType");
        if (props.getProperty("javax.net.ssl.trustStorePassword") != null) {
            this.trustStorePass = props.getProperty("javax.net.ssl.trustStorePassword").toCharArray();
        }
        else {
            this.trustStorePass = null;
        }
        if (props.getProperty("javax.net.ssl.keyStorePassword") != null) {
            this.keyStorePass = props.getProperty("javax.net.ssl.keyStorePassword").toCharArray();
        }
        else {
            this.keyStorePass = null;
        }
        this.trustStoreFile = props.getProperty("javax.net.ssl.trustStore");
        this.keyStoreFile = props.getProperty("javax.net.ssl.keyStore");
        this.trustStoreBytes = null;
        this.keyStoreBytes = null;
        this.trustStore = null;
        this.keyStore = null;
        this.securityProtocol = "TLS";
        return this;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final SslConfigurator that = (SslConfigurator)o;
        Label_0062: {
            if (this.keyManagerFactoryAlgorithm != null) {
                if (this.keyManagerFactoryAlgorithm.equals(that.keyManagerFactoryAlgorithm)) {
                    break Label_0062;
                }
            }
            else if (that.keyManagerFactoryAlgorithm == null) {
                break Label_0062;
            }
            return false;
        }
        Label_0095: {
            if (this.keyManagerFactoryProvider != null) {
                if (this.keyManagerFactoryProvider.equals(that.keyManagerFactoryProvider)) {
                    break Label_0095;
                }
            }
            else if (that.keyManagerFactoryProvider == null) {
                break Label_0095;
            }
            return false;
        }
        if (!Arrays.equals(this.keyPass, that.keyPass)) {
            return false;
        }
        Label_0144: {
            if (this.keyStore != null) {
                if (this.keyStore.equals(that.keyStore)) {
                    break Label_0144;
                }
            }
            else if (that.keyStore == null) {
                break Label_0144;
            }
            return false;
        }
        if (!Arrays.equals(this.keyStoreBytes, that.keyStoreBytes)) {
            return false;
        }
        Label_0193: {
            if (this.keyStoreFile != null) {
                if (this.keyStoreFile.equals(that.keyStoreFile)) {
                    break Label_0193;
                }
            }
            else if (that.keyStoreFile == null) {
                break Label_0193;
            }
            return false;
        }
        if (!Arrays.equals(this.keyStorePass, that.keyStorePass)) {
            return false;
        }
        Label_0242: {
            if (this.keyStoreProvider != null) {
                if (this.keyStoreProvider.equals(that.keyStoreProvider)) {
                    break Label_0242;
                }
            }
            else if (that.keyStoreProvider == null) {
                break Label_0242;
            }
            return false;
        }
        Label_0275: {
            if (this.keyStoreType != null) {
                if (this.keyStoreType.equals(that.keyStoreType)) {
                    break Label_0275;
                }
            }
            else if (that.keyStoreType == null) {
                break Label_0275;
            }
            return false;
        }
        Label_0308: {
            if (this.securityProtocol != null) {
                if (this.securityProtocol.equals(that.securityProtocol)) {
                    break Label_0308;
                }
            }
            else if (that.securityProtocol == null) {
                break Label_0308;
            }
            return false;
        }
        Label_0341: {
            if (this.trustManagerFactoryAlgorithm != null) {
                if (this.trustManagerFactoryAlgorithm.equals(that.trustManagerFactoryAlgorithm)) {
                    break Label_0341;
                }
            }
            else if (that.trustManagerFactoryAlgorithm == null) {
                break Label_0341;
            }
            return false;
        }
        Label_0374: {
            if (this.trustManagerFactoryProvider != null) {
                if (this.trustManagerFactoryProvider.equals(that.trustManagerFactoryProvider)) {
                    break Label_0374;
                }
            }
            else if (that.trustManagerFactoryProvider == null) {
                break Label_0374;
            }
            return false;
        }
        Label_0407: {
            if (this.trustStore != null) {
                if (this.trustStore.equals(that.trustStore)) {
                    break Label_0407;
                }
            }
            else if (that.trustStore == null) {
                break Label_0407;
            }
            return false;
        }
        if (!Arrays.equals(this.trustStoreBytes, that.trustStoreBytes)) {
            return false;
        }
        Label_0456: {
            if (this.trustStoreFile != null) {
                if (this.trustStoreFile.equals(that.trustStoreFile)) {
                    break Label_0456;
                }
            }
            else if (that.trustStoreFile == null) {
                break Label_0456;
            }
            return false;
        }
        if (!Arrays.equals(this.trustStorePass, that.trustStorePass)) {
            return false;
        }
        Label_0505: {
            if (this.trustStoreProvider != null) {
                if (this.trustStoreProvider.equals(that.trustStoreProvider)) {
                    break Label_0505;
                }
            }
            else if (that.trustStoreProvider == null) {
                break Label_0505;
            }
            return false;
        }
        if (this.trustStoreType != null) {
            if (this.trustStoreType.equals(that.trustStoreType)) {
                return true;
            }
        }
        else if (that.trustStoreType == null) {
            return true;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int result = (this.keyStore != null) ? this.keyStore.hashCode() : 0;
        result = 31 * result + ((this.trustStore != null) ? this.trustStore.hashCode() : 0);
        result = 31 * result + ((this.trustStoreProvider != null) ? this.trustStoreProvider.hashCode() : 0);
        result = 31 * result + ((this.keyStoreProvider != null) ? this.keyStoreProvider.hashCode() : 0);
        result = 31 * result + ((this.trustStoreType != null) ? this.trustStoreType.hashCode() : 0);
        result = 31 * result + ((this.keyStoreType != null) ? this.keyStoreType.hashCode() : 0);
        result = 31 * result + ((this.trustStorePass != null) ? Arrays.hashCode(this.trustStorePass) : 0);
        result = 31 * result + ((this.keyStorePass != null) ? Arrays.hashCode(this.keyStorePass) : 0);
        result = 31 * result + ((this.keyPass != null) ? Arrays.hashCode(this.keyPass) : 0);
        result = 31 * result + ((this.trustStoreFile != null) ? this.trustStoreFile.hashCode() : 0);
        result = 31 * result + ((this.keyStoreFile != null) ? this.keyStoreFile.hashCode() : 0);
        result = 31 * result + ((this.trustStoreBytes != null) ? Arrays.hashCode(this.trustStoreBytes) : 0);
        result = 31 * result + ((this.keyStoreBytes != null) ? Arrays.hashCode(this.keyStoreBytes) : 0);
        result = 31 * result + ((this.trustManagerFactoryAlgorithm != null) ? this.trustManagerFactoryAlgorithm.hashCode() : 0);
        result = 31 * result + ((this.keyManagerFactoryAlgorithm != null) ? this.keyManagerFactoryAlgorithm.hashCode() : 0);
        result = 31 * result + ((this.trustManagerFactoryProvider != null) ? this.trustManagerFactoryProvider.hashCode() : 0);
        result = 31 * result + ((this.keyManagerFactoryProvider != null) ? this.keyManagerFactoryProvider.hashCode() : 0);
        result = 31 * result + ((this.securityProtocol != null) ? this.securityProtocol.hashCode() : 0);
        return result;
    }
    
    static {
        DEFAULT_CONFIG_NO_PROPS = new SslConfigurator(false);
        LOGGER = Logger.getLogger(SslConfigurator.class.getName());
    }
}
