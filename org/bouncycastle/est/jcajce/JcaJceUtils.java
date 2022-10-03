package org.bouncycastle.est.jcajce;

import java.security.NoSuchProviderException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import javax.net.ssl.KeyManagerFactory;
import java.security.KeyStore;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.X509CertificateHolder;
import java.util.Iterator;
import java.security.GeneralSecurityException;
import java.security.cert.CertPathParameters;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.CertSelector;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.X509CertSelector;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertStoreParameters;
import java.security.cert.CertStore;
import java.util.Collection;
import java.security.cert.CollectionCertStoreParameters;
import java.util.Arrays;
import java.security.cert.CRL;
import java.security.cert.TrustAnchor;
import java.util.Set;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;

public class JcaJceUtils
{
    public static X509TrustManager getTrustAllTrustManager() {
        return new X509TrustManager() {
            public void checkClientTrusted(final X509Certificate[] array, final String s) throws CertificateException {
            }
            
            public void checkServerTrusted(final X509Certificate[] array, final String s) throws CertificateException {
            }
            
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }
    
    public static X509TrustManager[] getCertPathTrustManager(final Set<TrustAnchor> set, final CRL[] array) {
        final X509Certificate[] array2 = new X509Certificate[set.size()];
        int n = 0;
        final Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            array2[n++] = ((TrustAnchor)iterator.next()).getTrustedCert();
        }
        return new X509TrustManager[] { new X509TrustManager() {
                public void checkClientTrusted(final X509Certificate[] array, final String s) throws CertificateException {
                }
                
                public void checkServerTrusted(final X509Certificate[] array, final String s) throws CertificateException {
                    try {
                        final CertStore instance = CertStore.getInstance("Collection", new CollectionCertStoreParameters(Arrays.asList(array)), "BC");
                        final CertPathBuilder instance2 = CertPathBuilder.getInstance("PKIX", "BC");
                        final X509CertSelector x509CertSelector = new X509CertSelector();
                        x509CertSelector.setCertificate(array[0]);
                        final PKIXBuilderParameters pkixBuilderParameters = new PKIXBuilderParameters(set, x509CertSelector);
                        pkixBuilderParameters.addCertStore(instance);
                        if (array != null) {
                            pkixBuilderParameters.setRevocationEnabled(true);
                            pkixBuilderParameters.addCertStore(CertStore.getInstance("Collection", new CollectionCertStoreParameters(Arrays.asList(array))));
                        }
                        else {
                            pkixBuilderParameters.setRevocationEnabled(false);
                        }
                        final PKIXCertPathValidatorResult pkixCertPathValidatorResult = (PKIXCertPathValidatorResult)instance2.build(pkixBuilderParameters);
                        JcaJceUtils.validateServerCertUsage(array[0]);
                    }
                    catch (final CertificateException ex) {
                        throw ex;
                    }
                    catch (final GeneralSecurityException ex2) {
                        throw new CertificateException("unable to process certificates: " + ex2.getMessage(), ex2);
                    }
                }
                
                public X509Certificate[] getAcceptedIssuers() {
                    final X509Certificate[] array = new X509Certificate[array2.length];
                    System.arraycopy(array2, 0, array, 0, array.length);
                    return array;
                }
            } };
    }
    
    public static void validateServerCertUsage(final X509Certificate x509Certificate) throws CertificateException {
        try {
            final X509CertificateHolder x509CertificateHolder = new X509CertificateHolder(x509Certificate.getEncoded());
            final KeyUsage fromExtensions = KeyUsage.fromExtensions(x509CertificateHolder.getExtensions());
            if (fromExtensions != null) {
                if (fromExtensions.hasUsages(4)) {
                    throw new CertificateException("Key usage must not contain keyCertSign");
                }
                if (!fromExtensions.hasUsages(128) && !fromExtensions.hasUsages(32)) {
                    throw new CertificateException("Key usage must be none, digitalSignature or keyEncipherment");
                }
            }
            final ExtendedKeyUsage fromExtensions2 = ExtendedKeyUsage.fromExtensions(x509CertificateHolder.getExtensions());
            if (fromExtensions2 != null && !fromExtensions2.hasKeyPurposeId(KeyPurposeId.id_kp_serverAuth) && !fromExtensions2.hasKeyPurposeId(KeyPurposeId.id_kp_msSGC) && !fromExtensions2.hasKeyPurposeId(KeyPurposeId.id_kp_nsSGC)) {
                throw new CertificateException("Certificate extended key usage must include serverAuth, msSGC or nsSGC");
            }
        }
        catch (final CertificateException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            throw new CertificateException(ex2.getMessage(), ex2);
        }
    }
    
    public static KeyManagerFactory createKeyManagerFactory(final String s, final String s2, final KeyStore keyStore, final char[] array) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        KeyManagerFactory keyManagerFactory;
        if (s == null && s2 == null) {
            keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        }
        else if (s2 == null) {
            keyManagerFactory = KeyManagerFactory.getInstance(s);
        }
        else {
            keyManagerFactory = KeyManagerFactory.getInstance(s, s2);
        }
        keyManagerFactory.init(keyStore, array);
        return keyManagerFactory;
    }
}
