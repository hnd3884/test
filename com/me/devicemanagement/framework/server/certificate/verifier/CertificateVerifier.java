package com.me.devicemanagement.framework.server.certificate.verifier;

import java.security.GeneralSecurityException;
import java.security.cert.CertPathParameters;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertStoreParameters;
import java.security.cert.CertStore;
import java.util.Collection;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.CertSelector;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.PublicKey;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.security.cert.CertPathBuilderException;
import java.util.Iterator;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.security.cert.PKIXCertPathBuilderResult;
import java.util.Set;
import java.security.cert.Certificate;

public class CertificateVerifier
{
    public static PKIXCertPathBuilderResult verifyCertificate(final Certificate cert, final Set<Certificate> additionalCerts) throws CertificateVerificationException {
        X509Certificate x509Cert = null;
        final Set<X509Certificate> x509AdditionalCerts = new HashSet<X509Certificate>();
        if (cert instanceof X509Certificate) {
            x509Cert = (X509Certificate)cert;
            for (final Certificate additionalCert : additionalCerts) {
                if (!(additionalCert instanceof X509Certificate)) {
                    throw new CertificateVerificationException("Error in converting from Certificate to X509Certificate");
                }
                final X509Certificate x509AdditionalCert = (X509Certificate)additionalCert;
                x509AdditionalCerts.add(x509AdditionalCert);
            }
            return verifyCertificate(x509Cert, x509AdditionalCerts);
        }
        throw new CertificateVerificationException("Error in converting from Certificate to X509Certificate");
    }
    
    public static PKIXCertPathBuilderResult verifyCertificate(final X509Certificate cert, final Set<X509Certificate> additionalCerts) throws CertificateVerificationException {
        try {
            if (isSelfSigned(cert)) {
                throw new CertificateVerificationException("The certificate is self-signed.");
            }
            final Set<X509Certificate> trustedRootCerts = new HashSet<X509Certificate>();
            final Set<X509Certificate> intermediateCerts = new HashSet<X509Certificate>();
            for (final X509Certificate additionalCert : additionalCerts) {
                if (isSelfSigned(additionalCert)) {
                    trustedRootCerts.add(additionalCert);
                }
                else {
                    intermediateCerts.add(additionalCert);
                }
            }
            final PKIXCertPathBuilderResult verifiedCertChain = verifyCertificate(cert, trustedRootCerts, intermediateCerts);
            return verifiedCertChain;
        }
        catch (final CertPathBuilderException certPathEx) {
            throw new CertificateVerificationException("Error building certification path: " + cert.getSubjectX500Principal(), certPathEx);
        }
        catch (final CertificateVerificationException cvex) {
            throw cvex;
        }
        catch (final Exception ex) {
            throw new CertificateVerificationException("Error verifying the certificate: " + cert.getSubjectX500Principal(), ex);
        }
    }
    
    public static boolean isSelfSigned(final X509Certificate cert) throws CertificateException, NoSuchAlgorithmException, NoSuchProviderException {
        try {
            final PublicKey key = cert.getPublicKey();
            cert.verify(key);
            return true;
        }
        catch (final SignatureException sigEx) {
            return false;
        }
        catch (final InvalidKeyException keyEx) {
            return false;
        }
    }
    
    private static PKIXCertPathBuilderResult verifyCertificate(final X509Certificate cert, final Set<X509Certificate> trustedRootCerts, final Set<X509Certificate> intermediateCerts) throws GeneralSecurityException {
        final X509CertSelector selector = new X509CertSelector();
        selector.setCertificate(cert);
        final Set<TrustAnchor> trustAnchors = new HashSet<TrustAnchor>();
        for (final X509Certificate trustedRootCert : trustedRootCerts) {
            trustAnchors.add(new TrustAnchor(trustedRootCert, null));
        }
        final PKIXBuilderParameters pkixParams = new PKIXBuilderParameters(trustAnchors, selector);
        pkixParams.setRevocationEnabled(false);
        final CertStore intermediateCertStore = CertStore.getInstance("Collection", new CollectionCertStoreParameters(intermediateCerts));
        pkixParams.addCertStore(intermediateCertStore);
        final CertPathBuilder builder = CertPathBuilder.getInstance("PKIX");
        final PKIXCertPathBuilderResult result = (PKIXCertPathBuilderResult)builder.build(pkixParams);
        return result;
    }
}
