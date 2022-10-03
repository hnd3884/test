package org.bouncycastle.cms;

import java.util.HashMap;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.ASN1Set;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;
import java.util.Map;
import java.util.Collections;
import org.bouncycastle.asn1.cms.SignerInfo;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.util.io.TeeOutputStream;
import java.io.OutputStream;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.asn1.cms.SignerIdentifier;

public class SignerInfoGenerator
{
    private final SignerIdentifier signerIdentifier;
    private final CMSAttributeTableGenerator sAttrGen;
    private final CMSAttributeTableGenerator unsAttrGen;
    private final ContentSigner signer;
    private final DigestCalculator digester;
    private final DigestAlgorithmIdentifierFinder digAlgFinder;
    private final CMSSignatureEncryptionAlgorithmFinder sigEncAlgFinder;
    private byte[] calculatedDigest;
    private X509CertificateHolder certHolder;
    
    SignerInfoGenerator(final SignerIdentifier signerIdentifier, final ContentSigner contentSigner, final DigestCalculatorProvider digestCalculatorProvider, final CMSSignatureEncryptionAlgorithmFinder cmsSignatureEncryptionAlgorithmFinder) throws OperatorCreationException {
        this(signerIdentifier, contentSigner, digestCalculatorProvider, cmsSignatureEncryptionAlgorithmFinder, false);
    }
    
    SignerInfoGenerator(final SignerIdentifier signerIdentifier, final ContentSigner signer, final DigestCalculatorProvider digestCalculatorProvider, final CMSSignatureEncryptionAlgorithmFinder sigEncAlgFinder, final boolean b) throws OperatorCreationException {
        this.digAlgFinder = new DefaultDigestAlgorithmIdentifierFinder();
        this.calculatedDigest = null;
        this.signerIdentifier = signerIdentifier;
        this.signer = signer;
        if (digestCalculatorProvider != null) {
            this.digester = digestCalculatorProvider.get(this.digAlgFinder.find(signer.getAlgorithmIdentifier()));
        }
        else {
            this.digester = null;
        }
        if (b) {
            this.sAttrGen = null;
            this.unsAttrGen = null;
        }
        else {
            this.sAttrGen = new DefaultSignedAttributeTableGenerator();
            this.unsAttrGen = null;
        }
        this.sigEncAlgFinder = sigEncAlgFinder;
    }
    
    public SignerInfoGenerator(final SignerInfoGenerator signerInfoGenerator, final CMSAttributeTableGenerator sAttrGen, final CMSAttributeTableGenerator unsAttrGen) {
        this.digAlgFinder = new DefaultDigestAlgorithmIdentifierFinder();
        this.calculatedDigest = null;
        this.signerIdentifier = signerInfoGenerator.signerIdentifier;
        this.signer = signerInfoGenerator.signer;
        this.digester = signerInfoGenerator.digester;
        this.sigEncAlgFinder = signerInfoGenerator.sigEncAlgFinder;
        this.sAttrGen = sAttrGen;
        this.unsAttrGen = unsAttrGen;
    }
    
    SignerInfoGenerator(final SignerIdentifier signerIdentifier, final ContentSigner signer, final DigestCalculatorProvider digestCalculatorProvider, final CMSSignatureEncryptionAlgorithmFinder sigEncAlgFinder, final CMSAttributeTableGenerator sAttrGen, final CMSAttributeTableGenerator unsAttrGen) throws OperatorCreationException {
        this.digAlgFinder = new DefaultDigestAlgorithmIdentifierFinder();
        this.calculatedDigest = null;
        this.signerIdentifier = signerIdentifier;
        this.signer = signer;
        if (digestCalculatorProvider != null) {
            this.digester = digestCalculatorProvider.get(this.digAlgFinder.find(signer.getAlgorithmIdentifier()));
        }
        else {
            this.digester = null;
        }
        this.sAttrGen = sAttrGen;
        this.unsAttrGen = unsAttrGen;
        this.sigEncAlgFinder = sigEncAlgFinder;
    }
    
    public SignerIdentifier getSID() {
        return this.signerIdentifier;
    }
    
    public int getGeneratedVersion() {
        return this.signerIdentifier.isTagged() ? 3 : 1;
    }
    
    public boolean hasAssociatedCertificate() {
        return this.certHolder != null;
    }
    
    public X509CertificateHolder getAssociatedCertificate() {
        return this.certHolder;
    }
    
    public AlgorithmIdentifier getDigestAlgorithm() {
        if (this.digester != null) {
            return this.digester.getAlgorithmIdentifier();
        }
        return this.digAlgFinder.find(this.signer.getAlgorithmIdentifier());
    }
    
    public OutputStream getCalculatingOutputStream() {
        if (this.digester == null) {
            return this.signer.getOutputStream();
        }
        if (this.sAttrGen == null) {
            return (OutputStream)new TeeOutputStream(this.digester.getOutputStream(), this.signer.getOutputStream());
        }
        return this.digester.getOutputStream();
    }
    
    public SignerInfo generate(final ASN1ObjectIdentifier asn1ObjectIdentifier) throws CMSException {
        try {
            ASN1Set attributeSet = null;
            final AlgorithmIdentifier encryptionAlgorithm = this.sigEncAlgFinder.findEncryptionAlgorithm(this.signer.getAlgorithmIdentifier());
            AlgorithmIdentifier algorithmIdentifier;
            if (this.sAttrGen != null) {
                algorithmIdentifier = this.digester.getAlgorithmIdentifier();
                this.calculatedDigest = this.digester.getDigest();
                attributeSet = this.getAttributeSet(this.sAttrGen.getAttributes(Collections.unmodifiableMap((Map<?, ?>)this.getBaseParameters(asn1ObjectIdentifier, this.digester.getAlgorithmIdentifier(), encryptionAlgorithm, this.calculatedDigest))));
                final OutputStream outputStream = this.signer.getOutputStream();
                outputStream.write(attributeSet.getEncoded("DER"));
                outputStream.close();
            }
            else if (this.digester != null) {
                algorithmIdentifier = this.digester.getAlgorithmIdentifier();
                this.calculatedDigest = this.digester.getDigest();
            }
            else {
                algorithmIdentifier = this.digAlgFinder.find(this.signer.getAlgorithmIdentifier());
                this.calculatedDigest = null;
            }
            final byte[] signature = this.signer.getSignature();
            ASN1Set attributeSet2 = null;
            if (this.unsAttrGen != null) {
                final Map baseParameters = this.getBaseParameters(asn1ObjectIdentifier, algorithmIdentifier, encryptionAlgorithm, this.calculatedDigest);
                baseParameters.put("encryptedDigest", Arrays.clone(signature));
                attributeSet2 = this.getAttributeSet(this.unsAttrGen.getAttributes(Collections.unmodifiableMap((Map<?, ?>)baseParameters)));
            }
            return new SignerInfo(this.signerIdentifier, algorithmIdentifier, attributeSet, encryptionAlgorithm, (ASN1OctetString)new DEROctetString(signature), attributeSet2);
        }
        catch (final IOException ex) {
            throw new CMSException("encoding error.", ex);
        }
    }
    
    void setAssociatedCertificate(final X509CertificateHolder certHolder) {
        this.certHolder = certHolder;
    }
    
    private ASN1Set getAttributeSet(final AttributeTable attributeTable) {
        if (attributeTable != null) {
            return (ASN1Set)new DERSet(attributeTable.toASN1EncodableVector());
        }
        return null;
    }
    
    private Map getBaseParameters(final ASN1ObjectIdentifier asn1ObjectIdentifier, final AlgorithmIdentifier algorithmIdentifier, final AlgorithmIdentifier algorithmIdentifier2, final byte[] array) {
        final HashMap hashMap = new HashMap();
        if (asn1ObjectIdentifier != null) {
            hashMap.put("contentType", asn1ObjectIdentifier);
        }
        hashMap.put("digestAlgID", algorithmIdentifier);
        hashMap.put("signatureAlgID", algorithmIdentifier2);
        hashMap.put("digest", Arrays.clone(array));
        return hashMap;
    }
    
    public byte[] getCalculatedDigest() {
        if (this.calculatedDigest != null) {
            return Arrays.clone(this.calculatedDigest);
        }
        return null;
    }
    
    public CMSAttributeTableGenerator getSignedAttributeTableGenerator() {
        return this.sAttrGen;
    }
    
    public CMSAttributeTableGenerator getUnsignedAttributeTableGenerator() {
        return this.unsAttrGen;
    }
}
