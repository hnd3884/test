package org.bouncycastle.pqc.asn1;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;

public class SPHINCS256KeyParams extends ASN1Object
{
    private final ASN1Integer version;
    private final AlgorithmIdentifier treeDigest;
    
    public SPHINCS256KeyParams(final AlgorithmIdentifier treeDigest) {
        this.version = new ASN1Integer(0L);
        this.treeDigest = treeDigest;
    }
    
    private SPHINCS256KeyParams(final ASN1Sequence asn1Sequence) {
        this.version = ASN1Integer.getInstance(asn1Sequence.getObjectAt(0));
        this.treeDigest = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(1));
    }
    
    public static final SPHINCS256KeyParams getInstance(final Object o) {
        if (o instanceof SPHINCS256KeyParams) {
            return (SPHINCS256KeyParams)o;
        }
        if (o != null) {
            return new SPHINCS256KeyParams(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public AlgorithmIdentifier getTreeDigest() {
        return this.treeDigest;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.version);
        asn1EncodableVector.add(this.treeDigest);
        return new DERSequence(asn1EncodableVector);
    }
}
