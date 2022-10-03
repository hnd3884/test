package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class IdentityProofV2 extends ASN1Object
{
    private final AlgorithmIdentifier proofAlgID;
    private final AlgorithmIdentifier macAlgId;
    private final byte[] witness;
    
    public IdentityProofV2(final AlgorithmIdentifier proofAlgID, final AlgorithmIdentifier macAlgId, final byte[] array) {
        this.proofAlgID = proofAlgID;
        this.macAlgId = macAlgId;
        this.witness = Arrays.clone(array);
    }
    
    private IdentityProofV2(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 3) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.proofAlgID = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(0));
        this.macAlgId = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(1));
        this.witness = Arrays.clone(ASN1OctetString.getInstance(asn1Sequence.getObjectAt(2)).getOctets());
    }
    
    public static IdentityProofV2 getInstance(final Object o) {
        if (o instanceof IdentityProofV2) {
            return (IdentityProofV2)o;
        }
        if (o != null) {
            return new IdentityProofV2(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public AlgorithmIdentifier getProofAlgID() {
        return this.proofAlgID;
    }
    
    public AlgorithmIdentifier getMacAlgId() {
        return this.macAlgId;
    }
    
    public byte[] getWitness() {
        return Arrays.clone(this.witness);
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.proofAlgID);
        asn1EncodableVector.add(this.macAlgId);
        asn1EncodableVector.add(new DEROctetString(this.getWitness()));
        return new DERSequence(asn1EncodableVector);
    }
}
