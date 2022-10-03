package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class CMSAlgorithmProtection extends ASN1Object
{
    public static final int SIGNATURE = 1;
    public static final int MAC = 2;
    private final AlgorithmIdentifier digestAlgorithm;
    private final AlgorithmIdentifier signatureAlgorithm;
    private final AlgorithmIdentifier macAlgorithm;
    
    public CMSAlgorithmProtection(final AlgorithmIdentifier digestAlgorithm, final int n, final AlgorithmIdentifier algorithmIdentifier) {
        if (digestAlgorithm == null || algorithmIdentifier == null) {
            throw new NullPointerException("AlgorithmIdentifiers cannot be null");
        }
        this.digestAlgorithm = digestAlgorithm;
        if (n == 1) {
            this.signatureAlgorithm = algorithmIdentifier;
            this.macAlgorithm = null;
        }
        else {
            if (n != 2) {
                throw new IllegalArgumentException("Unknown type: " + n);
            }
            this.signatureAlgorithm = null;
            this.macAlgorithm = algorithmIdentifier;
        }
    }
    
    private CMSAlgorithmProtection(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 2) {
            throw new IllegalArgumentException("Sequence wrong size: One of signatureAlgorithm or macAlgorithm must be present");
        }
        this.digestAlgorithm = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(0));
        final ASN1TaggedObject instance = ASN1TaggedObject.getInstance(asn1Sequence.getObjectAt(1));
        if (instance.getTagNo() == 1) {
            this.signatureAlgorithm = AlgorithmIdentifier.getInstance(instance, false);
            this.macAlgorithm = null;
        }
        else {
            if (instance.getTagNo() != 2) {
                throw new IllegalArgumentException("Unknown tag found: " + instance.getTagNo());
            }
            this.signatureAlgorithm = null;
            this.macAlgorithm = AlgorithmIdentifier.getInstance(instance, false);
        }
    }
    
    public static CMSAlgorithmProtection getInstance(final Object o) {
        if (o instanceof CMSAlgorithmProtection) {
            return (CMSAlgorithmProtection)o;
        }
        if (o != null) {
            return new CMSAlgorithmProtection(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public AlgorithmIdentifier getDigestAlgorithm() {
        return this.digestAlgorithm;
    }
    
    public AlgorithmIdentifier getMacAlgorithm() {
        return this.macAlgorithm;
    }
    
    public AlgorithmIdentifier getSignatureAlgorithm() {
        return this.signatureAlgorithm;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.digestAlgorithm);
        if (this.signatureAlgorithm != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 1, this.signatureAlgorithm));
        }
        if (this.macAlgorithm != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 2, this.macAlgorithm));
        }
        return new DERSequence(asn1EncodableVector);
    }
}
