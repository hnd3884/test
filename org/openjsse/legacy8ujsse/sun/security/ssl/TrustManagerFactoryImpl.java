package org.openjsse.legacy8ujsse.sun.security.ssl;

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
import org.openjsse.legacy8ujsse.sun.security.validator.TrustStoreUtil;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.security.KeyStore;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.TrustManagerFactorySpi;

abstract class TrustManagerFactoryImpl extends TrustManagerFactorySpi
{
    private static final Debug debug;
    private X509TrustManager trustManager;
    private boolean isInitialized;
    
    TrustManagerFactoryImpl() {
        this.trustManager = null;
        this.isInitialized = false;
    }
    
    @Override
    protected void engineInit(final KeyStore ks) throws KeyStoreException {
        Label_0208: {
            if (ks == null) {
                try {
                    this.trustManager = this.getInstance(TrustStoreManager.getTrustedCerts());
                    break Label_0208;
                }
                catch (final SecurityException se) {
                    if (TrustManagerFactoryImpl.debug != null && Debug.isOn("trustmanager")) {
                        System.out.println("SunX509: skip default keystore: " + se);
                    }
                    break Label_0208;
                }
                catch (final Error err) {
                    if (TrustManagerFactoryImpl.debug != null && Debug.isOn("trustmanager")) {
                        System.out.println("SunX509: skip default keystore: " + err);
                    }
                    throw err;
                }
                catch (final RuntimeException re) {
                    if (TrustManagerFactoryImpl.debug != null && Debug.isOn("trustmanager")) {
                        System.out.println("SunX509: skip default keystore: " + re);
                    }
                    throw re;
                }
                catch (final Exception e) {
                    if (TrustManagerFactoryImpl.debug != null && Debug.isOn("trustmanager")) {
                        System.out.println("SunX509: skip default keystore: " + e);
                    }
                    throw new KeyStoreException("problem accessing trust store", e);
                }
            }
            this.trustManager = this.getInstance(TrustStoreUtil.getTrustedCerts(ks));
        }
        this.isInitialized = true;
    }
    
    abstract X509TrustManager getInstance(final Collection<X509Certificate> p0);
    
    abstract X509TrustManager getInstance(final ManagerFactoryParameters p0) throws InvalidAlgorithmParameterException;
    
    @Override
    protected void engineInit(final ManagerFactoryParameters spec) throws InvalidAlgorithmParameterException {
        this.trustManager = this.getInstance(spec);
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
                catch (final FileNotFoundException e) {
                    return null;
                }
            }
        });
    }
    
    static {
        debug = Debug.getInstance("ssl");
    }
    
    public static final class SimpleFactory extends TrustManagerFactoryImpl
    {
        @Override
        X509TrustManager getInstance(final Collection<X509Certificate> trustedCerts) {
            return new X509TrustManagerImpl("Simple", trustedCerts);
        }
        
        @Override
        X509TrustManager getInstance(final ManagerFactoryParameters spec) throws InvalidAlgorithmParameterException {
            throw new InvalidAlgorithmParameterException("SunX509 TrustManagerFactory does not use ManagerFactoryParameters");
        }
    }
    
    public static final class PKIXFactory extends TrustManagerFactoryImpl
    {
        @Override
        X509TrustManager getInstance(final Collection<X509Certificate> trustedCerts) {
            return new X509TrustManagerImpl("PKIX", trustedCerts);
        }
        
        @Override
        X509TrustManager getInstance(final ManagerFactoryParameters spec) throws InvalidAlgorithmParameterException {
            if (!(spec instanceof CertPathTrustManagerParameters)) {
                throw new InvalidAlgorithmParameterException("Parameters must be CertPathTrustManagerParameters");
            }
            final CertPathParameters params = ((CertPathTrustManagerParameters)spec).getParameters();
            if (!(params instanceof PKIXBuilderParameters)) {
                throw new InvalidAlgorithmParameterException("Encapsulated parameters must be PKIXBuilderParameters");
            }
            final PKIXBuilderParameters pkixParams = (PKIXBuilderParameters)params;
            return new X509TrustManagerImpl("PKIX", pkixParams);
        }
    }
}
