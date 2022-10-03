package org.bouncycastle.cms;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.asn1.cms.SignerIdentifier;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DigestCalculatorProvider;

public class SignerInfoGeneratorBuilder
{
    private DigestCalculatorProvider digestProvider;
    private boolean directSignature;
    private CMSAttributeTableGenerator signedGen;
    private CMSAttributeTableGenerator unsignedGen;
    private CMSSignatureEncryptionAlgorithmFinder sigEncAlgFinder;
    
    public SignerInfoGeneratorBuilder(final DigestCalculatorProvider digestCalculatorProvider) {
        this(digestCalculatorProvider, new DefaultCMSSignatureEncryptionAlgorithmFinder());
    }
    
    public SignerInfoGeneratorBuilder(final DigestCalculatorProvider digestProvider, final CMSSignatureEncryptionAlgorithmFinder sigEncAlgFinder) {
        this.digestProvider = digestProvider;
        this.sigEncAlgFinder = sigEncAlgFinder;
    }
    
    public SignerInfoGeneratorBuilder setDirectSignature(final boolean directSignature) {
        this.directSignature = directSignature;
        return this;
    }
    
    public SignerInfoGeneratorBuilder setSignedAttributeGenerator(final CMSAttributeTableGenerator signedGen) {
        this.signedGen = signedGen;
        return this;
    }
    
    public SignerInfoGeneratorBuilder setUnsignedAttributeGenerator(final CMSAttributeTableGenerator unsignedGen) {
        this.unsignedGen = unsignedGen;
        return this;
    }
    
    public SignerInfoGenerator build(final ContentSigner contentSigner, final X509CertificateHolder associatedCertificate) throws OperatorCreationException {
        final SignerInfoGenerator generator = this.createGenerator(contentSigner, new SignerIdentifier(new IssuerAndSerialNumber(associatedCertificate.toASN1Structure())));
        generator.setAssociatedCertificate(associatedCertificate);
        return generator;
    }
    
    public SignerInfoGenerator build(final ContentSigner contentSigner, final byte[] array) throws OperatorCreationException {
        return this.createGenerator(contentSigner, new SignerIdentifier((ASN1OctetString)new DEROctetString(array)));
    }
    
    private SignerInfoGenerator createGenerator(final ContentSigner contentSigner, final SignerIdentifier signerIdentifier) throws OperatorCreationException {
        if (this.directSignature) {
            return new SignerInfoGenerator(signerIdentifier, contentSigner, this.digestProvider, this.sigEncAlgFinder, true);
        }
        if (this.signedGen != null || this.unsignedGen != null) {
            if (this.signedGen == null) {
                this.signedGen = new DefaultSignedAttributeTableGenerator();
            }
            return new SignerInfoGenerator(signerIdentifier, contentSigner, this.digestProvider, this.sigEncAlgFinder, this.signedGen, this.unsignedGen);
        }
        return new SignerInfoGenerator(signerIdentifier, contentSigner, this.digestProvider, this.sigEncAlgFinder);
    }
}
