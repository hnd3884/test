package org.bouncycastle.cms.jcajce;

import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.operator.ContentVerifierProvider;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateException;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.cert.X509CertificateHolder;
import java.security.Provider;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.cms.DefaultCMSSignatureAlgorithmNameGenerator;
import org.bouncycastle.operator.SignatureAlgorithmIdentifierFinder;
import org.bouncycastle.cms.CMSSignatureAlgorithmNameGenerator;
import org.bouncycastle.operator.DigestCalculatorProvider;

public class JcaSignerInfoVerifierBuilder
{
    private Helper helper;
    private DigestCalculatorProvider digestProvider;
    private CMSSignatureAlgorithmNameGenerator sigAlgNameGen;
    private SignatureAlgorithmIdentifierFinder sigAlgIDFinder;
    
    public JcaSignerInfoVerifierBuilder(final DigestCalculatorProvider digestProvider) {
        this.helper = new Helper();
        this.sigAlgNameGen = new DefaultCMSSignatureAlgorithmNameGenerator();
        this.sigAlgIDFinder = new DefaultSignatureAlgorithmIdentifierFinder();
        this.digestProvider = digestProvider;
    }
    
    public JcaSignerInfoVerifierBuilder setProvider(final Provider provider) {
        this.helper = new ProviderHelper(provider);
        return this;
    }
    
    public JcaSignerInfoVerifierBuilder setProvider(final String s) {
        this.helper = new NamedHelper(s);
        return this;
    }
    
    public JcaSignerInfoVerifierBuilder setSignatureAlgorithmNameGenerator(final CMSSignatureAlgorithmNameGenerator sigAlgNameGen) {
        this.sigAlgNameGen = sigAlgNameGen;
        return this;
    }
    
    public JcaSignerInfoVerifierBuilder setSignatureAlgorithmFinder(final SignatureAlgorithmIdentifierFinder sigAlgIDFinder) {
        this.sigAlgIDFinder = sigAlgIDFinder;
        return this;
    }
    
    public SignerInformationVerifier build(final X509CertificateHolder x509CertificateHolder) throws OperatorCreationException, CertificateException {
        return new SignerInformationVerifier(this.sigAlgNameGen, this.sigAlgIDFinder, this.helper.createContentVerifierProvider(x509CertificateHolder), this.digestProvider);
    }
    
    public SignerInformationVerifier build(final X509Certificate x509Certificate) throws OperatorCreationException {
        return new SignerInformationVerifier(this.sigAlgNameGen, this.sigAlgIDFinder, this.helper.createContentVerifierProvider(x509Certificate), this.digestProvider);
    }
    
    public SignerInformationVerifier build(final PublicKey publicKey) throws OperatorCreationException {
        return new SignerInformationVerifier(this.sigAlgNameGen, this.sigAlgIDFinder, this.helper.createContentVerifierProvider(publicKey), this.digestProvider);
    }
    
    private class Helper
    {
        ContentVerifierProvider createContentVerifierProvider(final PublicKey publicKey) throws OperatorCreationException {
            return new JcaContentVerifierProviderBuilder().build(publicKey);
        }
        
        ContentVerifierProvider createContentVerifierProvider(final X509Certificate x509Certificate) throws OperatorCreationException {
            return new JcaContentVerifierProviderBuilder().build(x509Certificate);
        }
        
        ContentVerifierProvider createContentVerifierProvider(final X509CertificateHolder x509CertificateHolder) throws OperatorCreationException, CertificateException {
            return new JcaContentVerifierProviderBuilder().build(x509CertificateHolder);
        }
        
        DigestCalculatorProvider createDigestCalculatorProvider() throws OperatorCreationException {
            return new JcaDigestCalculatorProviderBuilder().build();
        }
    }
    
    private class NamedHelper extends Helper
    {
        private final String providerName;
        
        public NamedHelper(final String providerName) {
            this.providerName = providerName;
        }
        
        @Override
        ContentVerifierProvider createContentVerifierProvider(final PublicKey publicKey) throws OperatorCreationException {
            return new JcaContentVerifierProviderBuilder().setProvider(this.providerName).build(publicKey);
        }
        
        @Override
        ContentVerifierProvider createContentVerifierProvider(final X509Certificate x509Certificate) throws OperatorCreationException {
            return new JcaContentVerifierProviderBuilder().setProvider(this.providerName).build(x509Certificate);
        }
        
        @Override
        DigestCalculatorProvider createDigestCalculatorProvider() throws OperatorCreationException {
            return new JcaDigestCalculatorProviderBuilder().setProvider(this.providerName).build();
        }
        
        @Override
        ContentVerifierProvider createContentVerifierProvider(final X509CertificateHolder x509CertificateHolder) throws OperatorCreationException, CertificateException {
            return new JcaContentVerifierProviderBuilder().setProvider(this.providerName).build(x509CertificateHolder);
        }
    }
    
    private class ProviderHelper extends Helper
    {
        private final Provider provider;
        
        public ProviderHelper(final Provider provider) {
            this.provider = provider;
        }
        
        @Override
        ContentVerifierProvider createContentVerifierProvider(final PublicKey publicKey) throws OperatorCreationException {
            return new JcaContentVerifierProviderBuilder().setProvider(this.provider).build(publicKey);
        }
        
        @Override
        ContentVerifierProvider createContentVerifierProvider(final X509Certificate x509Certificate) throws OperatorCreationException {
            return new JcaContentVerifierProviderBuilder().setProvider(this.provider).build(x509Certificate);
        }
        
        @Override
        DigestCalculatorProvider createDigestCalculatorProvider() throws OperatorCreationException {
            return new JcaDigestCalculatorProviderBuilder().setProvider(this.provider).build();
        }
        
        @Override
        ContentVerifierProvider createContentVerifierProvider(final X509CertificateHolder x509CertificateHolder) throws OperatorCreationException, CertificateException {
            return new JcaContentVerifierProviderBuilder().setProvider(this.provider).build(x509CertificateHolder);
        }
    }
}
