package org.bouncycastle.cms.jcajce;

import java.security.cert.CertificateEncodingException;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import java.security.cert.X509Certificate;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.cms.CMSAttributeTableGenerator;
import org.bouncycastle.cms.CMSSignatureEncryptionAlgorithmFinder;
import org.bouncycastle.cms.DefaultCMSSignatureEncryptionAlgorithmFinder;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.cms.SignerInfoGeneratorBuilder;

public class JcaSignerInfoGeneratorBuilder
{
    private SignerInfoGeneratorBuilder builder;
    
    public JcaSignerInfoGeneratorBuilder(final DigestCalculatorProvider digestCalculatorProvider) {
        this(digestCalculatorProvider, new DefaultCMSSignatureEncryptionAlgorithmFinder());
    }
    
    public JcaSignerInfoGeneratorBuilder(final DigestCalculatorProvider digestCalculatorProvider, final CMSSignatureEncryptionAlgorithmFinder cmsSignatureEncryptionAlgorithmFinder) {
        this.builder = new SignerInfoGeneratorBuilder(digestCalculatorProvider, cmsSignatureEncryptionAlgorithmFinder);
    }
    
    public JcaSignerInfoGeneratorBuilder setDirectSignature(final boolean directSignature) {
        this.builder.setDirectSignature(directSignature);
        return this;
    }
    
    public JcaSignerInfoGeneratorBuilder setSignedAttributeGenerator(final CMSAttributeTableGenerator signedAttributeGenerator) {
        this.builder.setSignedAttributeGenerator(signedAttributeGenerator);
        return this;
    }
    
    public JcaSignerInfoGeneratorBuilder setUnsignedAttributeGenerator(final CMSAttributeTableGenerator unsignedAttributeGenerator) {
        this.builder.setUnsignedAttributeGenerator(unsignedAttributeGenerator);
        return this;
    }
    
    public SignerInfoGenerator build(final ContentSigner contentSigner, final X509CertificateHolder x509CertificateHolder) throws OperatorCreationException {
        return this.builder.build(contentSigner, x509CertificateHolder);
    }
    
    public SignerInfoGenerator build(final ContentSigner contentSigner, final byte[] array) throws OperatorCreationException {
        return this.builder.build(contentSigner, array);
    }
    
    public SignerInfoGenerator build(final ContentSigner contentSigner, final X509Certificate x509Certificate) throws OperatorCreationException, CertificateEncodingException {
        return this.build(contentSigner, new JcaX509CertificateHolder(x509Certificate));
    }
}
