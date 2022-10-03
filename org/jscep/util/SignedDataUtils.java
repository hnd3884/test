package org.jscep.util;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import org.bouncycastle.util.Store;
import java.security.GeneralSecurityException;
import java.security.cert.CertStoreParameters;
import java.util.Collection;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.CRLException;
import org.bouncycastle.cert.X509CRLHolder;
import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import org.bouncycastle.cert.X509CertificateHolder;
import java.util.ArrayList;
import org.bouncycastle.util.Selector;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertStore;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.SignatureAlgorithmIdentifierFinder;
import org.bouncycastle.cms.CMSSignatureAlgorithmNameGenerator;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.cms.DefaultCMSSignatureAlgorithmNameGenerator;
import org.bouncycastle.cms.SignerId;
import org.bouncycastle.cms.jcajce.JcaSignerId;
import java.security.cert.X509Certificate;
import org.bouncycastle.cms.CMSSignedData;
import org.slf4j.Logger;

public final class SignedDataUtils
{
    private static final Logger LOGGER;
    
    private SignedDataUtils() {
    }
    
    public static boolean isSignedBy(final CMSSignedData sd, final X509Certificate signer) {
        final SignerInformationStore store = sd.getSignerInfos();
        final SignerInformation signerInfo = store.get((SignerId)new JcaSignerId(signer));
        if (signerInfo == null) {
            return false;
        }
        final CMSSignatureAlgorithmNameGenerator sigNameGenerator = (CMSSignatureAlgorithmNameGenerator)new DefaultCMSSignatureAlgorithmNameGenerator();
        final SignatureAlgorithmIdentifierFinder sigAlgorithmFinder = (SignatureAlgorithmIdentifierFinder)new DefaultSignatureAlgorithmIdentifierFinder();
        ContentVerifierProvider verifierProvider;
        try {
            verifierProvider = new JcaContentVerifierProviderBuilder().build(signer);
        }
        catch (final OperatorCreationException e) {
            throw new RuntimeException((Throwable)e);
        }
        DigestCalculatorProvider digestProvider;
        try {
            digestProvider = new JcaDigestCalculatorProviderBuilder().build();
        }
        catch (final OperatorCreationException e2) {
            throw new RuntimeException((Throwable)e2);
        }
        final SignerInformationVerifier verifier = new SignerInformationVerifier(sigNameGenerator, sigAlgorithmFinder, verifierProvider, digestProvider);
        try {
            return signerInfo.verify(verifier);
        }
        catch (final CMSException e3) {
            return false;
        }
    }
    
    public static CertStore fromSignedData(final CMSSignedData signedData) {
        CertificateFactory factory;
        try {
            factory = CertificateFactory.getInstance("X509");
        }
        catch (final CertificateException e) {
            throw new RuntimeException(e);
        }
        final Store certStore = signedData.getCertificates();
        final Store crlStore = signedData.getCRLs();
        final Collection<X509CertificateHolder> certs = certStore.getMatches((Selector)null);
        final Collection<X509CRLHolder> crls = crlStore.getMatches((Selector)null);
        final Collection<Object> certsAndCrls = new ArrayList<Object>();
        for (final X509CertificateHolder cert : certs) {
            ByteArrayInputStream byteIn;
            try {
                byteIn = new ByteArrayInputStream(cert.getEncoded());
            }
            catch (final IOException e2) {
                SignedDataUtils.LOGGER.error("Error encoding certificate", (Throwable)e2);
                continue;
            }
            try {
                certsAndCrls.add(factory.generateCertificate(byteIn));
            }
            catch (final CertificateException e3) {
                SignedDataUtils.LOGGER.error("Error generating certificate", (Throwable)e3);
            }
        }
        for (final X509CRLHolder crl : crls) {
            ByteArrayInputStream byteIn;
            try {
                byteIn = new ByteArrayInputStream(crl.getEncoded());
            }
            catch (final IOException e2) {
                SignedDataUtils.LOGGER.error("Error encoding crl", (Throwable)e2);
                continue;
            }
            try {
                certsAndCrls.add(factory.generateCRL(byteIn));
            }
            catch (final CRLException e4) {
                SignedDataUtils.LOGGER.error("Error generating certificate", (Throwable)e4);
            }
        }
        try {
            return CertStore.getInstance("Collection", new CollectionCertStoreParameters(certsAndCrls));
        }
        catch (final GeneralSecurityException e5) {
            throw new RuntimeException(e5);
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)SignedDataUtils.class);
    }
}
