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

public class DecryptedPOP extends ASN1Object
{
    private final BodyPartID bodyPartID;
    private final AlgorithmIdentifier thePOPAlgID;
    private final byte[] thePOP;
    
    public DecryptedPOP(final BodyPartID bodyPartID, final AlgorithmIdentifier thePOPAlgID, final byte[] array) {
        this.bodyPartID = bodyPartID;
        this.thePOPAlgID = thePOPAlgID;
        this.thePOP = Arrays.clone(array);
    }
    
    private DecryptedPOP(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 3) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.bodyPartID = BodyPartID.getInstance(asn1Sequence.getObjectAt(0));
        this.thePOPAlgID = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(1));
        this.thePOP = Arrays.clone(ASN1OctetString.getInstance(asn1Sequence.getObjectAt(2)).getOctets());
    }
    
    public static DecryptedPOP getInstance(final Object o) {
        if (o instanceof DecryptedPOP) {
            return (DecryptedPOP)o;
        }
        if (o != null) {
            return new DecryptedPOP(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public BodyPartID getBodyPartID() {
        return this.bodyPartID;
    }
    
    public AlgorithmIdentifier getThePOPAlgID() {
        return this.thePOPAlgID;
    }
    
    public byte[] getThePOP() {
        return Arrays.clone(this.thePOP);
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.bodyPartID);
        asn1EncodableVector.add(this.thePOPAlgID);
        asn1EncodableVector.add(new DEROctetString(this.thePOP));
        return new DERSequence(asn1EncodableVector);
    }
}
