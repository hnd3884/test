package org.bouncycastle.cms.jcajce;

import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.cms.SignerInfoGeneratorBuilder;
import java.security.cert.CertificateEncodingException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cms.SignerInfoGenerator;
import java.security.cert.X509Certificate;
import java.security.PrivateKey;
import org.bouncycastle.cms.DefaultSignedAttributeTableGenerator;
import org.bouncycastle.asn1.cms.AttributeTable;
import java.security.Provider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.cms.CMSAttributeTableGenerator;

public class JcaSimpleSignerInfoGeneratorBuilder
{
    private Helper helper;
    private boolean hasNoSignedAttributes;
    private CMSAttributeTableGenerator signedGen;
    private CMSAttributeTableGenerator unsignedGen;
    
    public JcaSimpleSignerInfoGeneratorBuilder() throws OperatorCreationException {
        this.helper = new Helper();
    }
    
    public JcaSimpleSignerInfoGeneratorBuilder setProvider(final String s) throws OperatorCreationException {
        this.helper = new NamedHelper(s);
        return this;
    }
    
    public JcaSimpleSignerInfoGeneratorBuilder setProvider(final Provider provider) throws OperatorCreationException {
        this.helper = new ProviderHelper(provider);
        return this;
    }
    
    public JcaSimpleSignerInfoGeneratorBuilder setDirectSignature(final boolean hasNoSignedAttributes) {
        this.hasNoSignedAttributes = hasNoSignedAttributes;
        return this;
    }
    
    public JcaSimpleSignerInfoGeneratorBuilder setSignedAttributeGenerator(final CMSAttributeTableGenerator signedGen) {
        this.signedGen = signedGen;
        return this;
    }
    
    public JcaSimpleSignerInfoGeneratorBuilder setSignedAttributeGenerator(final AttributeTable attributeTable) {
        this.signedGen = new DefaultSignedAttributeTableGenerator(attributeTable);
        return this;
    }
    
    public JcaSimpleSignerInfoGeneratorBuilder setUnsignedAttributeGenerator(final CMSAttributeTableGenerator unsignedGen) {
        this.unsignedGen = unsignedGen;
        return this;
    }
    
    public SignerInfoGenerator build(final String s, final PrivateKey privateKey, final X509Certificate x509Certificate) throws OperatorCreationException, CertificateEncodingException {
        return this.configureAndBuild().build(this.helper.createContentSigner(s, privateKey), new JcaX509CertificateHolder(x509Certificate));
    }
    
    public SignerInfoGenerator build(final String s, final PrivateKey privateKey, final byte[] array) throws OperatorCreationException, CertificateEncodingException {
        return this.configureAndBuild().build(this.helper.createContentSigner(s, privateKey), array);
    }
    
    private SignerInfoGeneratorBuilder configureAndBuild() throws OperatorCreationException {
        final SignerInfoGeneratorBuilder signerInfoGeneratorBuilder = new SignerInfoGeneratorBuilder(this.helper.createDigestCalculatorProvider());
        signerInfoGeneratorBuilder.setDirectSignature(this.hasNoSignedAttributes);
        signerInfoGeneratorBuilder.setSignedAttributeGenerator(this.signedGen);
        signerInfoGeneratorBuilder.setUnsignedAttributeGenerator(this.unsignedGen);
        return signerInfoGeneratorBuilder;
    }
    
    private class Helper
    {
        ContentSigner createContentSigner(final String s, final PrivateKey privateKey) throws OperatorCreationException {
            return new JcaContentSignerBuilder(s).build(privateKey);
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
        ContentSigner createContentSigner(final String s, final PrivateKey privateKey) throws OperatorCreationException {
            return new JcaContentSignerBuilder(s).setProvider(this.providerName).build(privateKey);
        }
        
        @Override
        DigestCalculatorProvider createDigestCalculatorProvider() throws OperatorCreationException {
            return new JcaDigestCalculatorProviderBuilder().setProvider(this.providerName).build();
        }
    }
    
    private class ProviderHelper extends Helper
    {
        private final Provider provider;
        
        public ProviderHelper(final Provider provider) {
            this.provider = provider;
        }
        
        @Override
        ContentSigner createContentSigner(final String s, final PrivateKey privateKey) throws OperatorCreationException {
            return new JcaContentSignerBuilder(s).setProvider(this.provider).build(privateKey);
        }
        
        @Override
        DigestCalculatorProvider createDigestCalculatorProvider() throws OperatorCreationException {
            return new JcaDigestCalculatorProviderBuilder().setProvider(this.provider).build();
        }
    }
}
