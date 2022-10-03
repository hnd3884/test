package sun.security.ssl;

import java.security.cert.CertPathParameters;
import java.security.cert.PKIXBuilderParameters;
import javax.net.ssl.CertPathTrustManagerParameters;
import java.security.AccessController;
import java.io.FileNotFoundException;
import java.security.PrivilegedExceptionAction;
import java.io.FileInputStream;
import java.io.File;
import javax.net.ssl.TrustManager;
import java.security.InvalidAlgorithmParameterException;
import javax.net.ssl.ManagerFactoryParameters;
import sun.security.validator.TrustStoreUtil;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.security.KeyStore;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.TrustManagerFactorySpi;

abstract class TrustManagerFactoryImpl extends TrustManagerFactorySpi
{
    private X509TrustManager trustManager;
    private boolean isInitialized;
    
    TrustManagerFactoryImpl() {
        this.trustManager = null;
        this.isInitialized = false;
    }
    
    @Override
    protected void engineInit(final KeyStore keyStore) throws KeyStoreException {
        Label_0160: {
            if (keyStore == null) {
                try {
                    this.trustManager = this.getInstance(TrustStoreManager.getTrustedCerts());
                    break Label_0160;
                }
                catch (final SecurityException ex) {
                    if (SSLLogger.isOn && SSLLogger.isOn("trustmanager")) {
                        SSLLogger.fine("SunX509: skip default keystore", ex);
                    }
                    break Label_0160;
                }
                catch (final Error error) {
                    if (SSLLogger.isOn && SSLLogger.isOn("trustmanager")) {
                        SSLLogger.fine("SunX509: skip default keystore", error);
                    }
                    throw error;
                }
                catch (final RuntimeException ex2) {
                    if (SSLLogger.isOn && SSLLogger.isOn("trustmanager")) {
                        SSLLogger.fine("SunX509: skip default keystor", ex2);
                    }
                    throw ex2;
                }
                catch (final Exception ex3) {
                    if (SSLLogger.isOn && SSLLogger.isOn("trustmanager")) {
                        SSLLogger.fine("SunX509: skip default keystore", ex3);
                    }
                    throw new KeyStoreException("problem accessing trust store", ex3);
                }
            }
            this.trustManager = this.getInstance(TrustStoreUtil.getTrustedCerts(keyStore));
        }
        this.isInitialized = true;
    }
    
    abstract X509TrustManager getInstance(final Collection<X509Certificate> p0);
    
    abstract X509TrustManager getInstance(final ManagerFactoryParameters p0) throws InvalidAlgorithmParameterException;
    
    @Override
    protected void engineInit(final ManagerFactoryParameters managerFactoryParameters) throws InvalidAlgorithmParameterException {
        this.trustManager = this.getInstance(managerFactoryParameters);
        this.isInitialized = true;
    }
    
    @Override
    protected TrustManager[] engineGetTrustManagers() {
        if (!this.isInitialized) {
            throw new IllegalStateException("TrustManagerFactoryImpl is not initialized");
        }
        return new TrustManager[] { this.trustManager };
    }
    
    private static FileInputStream getFileInputStream(final File file) throws Exception {
        return AccessController.doPrivileged((PrivilegedExceptionAction<FileInputStream>)new PrivilegedExceptionAction<FileInputStream>() {
            @Override
            public FileInputStream run() throws Exception {
                try {
                    if (file.exists()) {
                        return new FileInputStream(file);
                    }
                    return null;
                }
                catch (final FileNotFoundException ex) {
                    return null;
                }
            }
        });
    }
    
    public static final class SimpleFactory extends TrustManagerFactoryImpl
    {
        @Override
        X509TrustManager getInstance(final Collection<X509Certificate> collection) {
            return new X509TrustManagerImpl("Simple", collection);
        }
        
        @Override
        X509TrustManager getInstance(final ManagerFactoryParameters managerFactoryParameters) throws InvalidAlgorithmParameterException {
            throw new InvalidAlgorithmParameterException("SunX509 TrustManagerFactory does not use ManagerFactoryParameters");
        }
    }
    
    public static final class PKIXFactory extends TrustManagerFactoryImpl
    {
        @Override
        X509TrustManager getInstance(final Collection<X509Certificate> collection) {
            return new X509TrustManagerImpl("PKIX", collection);
        }
        
        @Override
        X509TrustManager getInstance(final ManagerFactoryParameters managerFactoryParameters) throws InvalidAlgorithmParameterException {
            if (!(managerFactoryParameters instanceof CertPathTrustManagerParameters)) {
                throw new InvalidAlgorithmParameterException("Parameters must be CertPathTrustManagerParameters");
            }
            final CertPathParameters parameters = ((CertPathTrustManagerParameters)managerFactoryParameters).getParameters();
            if (!(parameters instanceof PKIXBuilderParameters)) {
                throw new InvalidAlgorithmParameterException("Encapsulated parameters must be PKIXBuilderParameters");
            }
            return new X509TrustManagerImpl("PKIX", (PKIXBuilderParameters)parameters);
        }
    }
}
