package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.ASN1Object;

public class EncryptedPOP extends ASN1Object
{
    private final TaggedRequest request;
    private final ContentInfo cms;
    private final AlgorithmIdentifier thePOPAlgID;
    private final AlgorithmIdentifier witnessAlgID;
    private final byte[] witness;
    
    private EncryptedPOP(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 5) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.request = TaggedRequest.getInstance(asn1Sequence.getObjectAt(0));
        this.cms = ContentInfo.getInstance(asn1Sequence.getObjectAt(1));
        this.thePOPAlgID = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(2));
        this.witnessAlgID = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(3));
        this.witness = Arrays.clone(ASN1OctetString.getInstance(asn1Sequence.getObjectAt(4)).getOctets());
    }
    
    public EncryptedPOP(final TaggedRequest request, final ContentInfo cms, final AlgorithmIdentifier thePOPAlgID, final AlgorithmIdentifier witnessAlgID, final byte[] array) {
        this.request = request;
        this.cms = cms;
        this.thePOPAlgID = thePOPAlgID;
        this.witnessAlgID = witnessAlgID;
        this.witness = Arrays.clone(array);
    }
    
    public static EncryptedPOP getInstance(final Object o) {
        if (o instanceof EncryptedPOP) {
            return (EncryptedPOP)o;
        }
        if (o != null) {
            return new EncryptedPOP(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public TaggedRequest getRequest() {
        return this.request;
    }
    
    public ContentInfo getCms() {
        return this.cms;
    }
    
    public AlgorithmIdentifier getThePOPAlgID() {
        return this.thePOPAlgID;
    }
    
    public AlgorithmIdentifier getWitnessAlgID() {
        return this.witnessAlgID;
    }
    
    public byte[] getWitness() {
        return Arrays.clone(this.witness);
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.request);
        asn1EncodableVector.add(this.cms);
        asn1EncodableVector.add(this.thePOPAlgID);
        asn1EncodableVector.add(this.witnessAlgID);
        asn1EncodableVector.add(new DEROctetString(this.witness));
        return new DERSequence(asn1EncodableVector);
    }
}
