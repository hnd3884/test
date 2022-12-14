package org.bouncycastle.asn1.pkcs;

import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.BERTaggedObject;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class EncryptedData extends ASN1Object
{
    ASN1Sequence data;
    ASN1ObjectIdentifier bagId;
    ASN1Primitive bagValue;
    
    public static EncryptedData getInstance(final Object o) {
        if (o instanceof EncryptedData) {
            return (EncryptedData)o;
        }
        if (o != null) {
            return new EncryptedData(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private EncryptedData(final ASN1Sequence asn1Sequence) {
        if (((ASN1Integer)asn1Sequence.getObjectAt(0)).getValue().intValue() != 0) {
            throw new IllegalArgumentException("sequence not version 0");
        }
        this.data = ASN1Sequence.getInstance(asn1Sequence.getObjectAt(1));
    }
    
    public EncryptedData(final ASN1ObjectIdentifier asn1ObjectIdentifier, final AlgorithmIdentifier algorithmIdentifier, final ASN1Encodable asn1Encodable) {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(asn1ObjectIdentifier);
        asn1EncodableVector.add(algorithmIdentifier.toASN1Primitive());
        asn1EncodableVector.add(new BERTaggedObject(false, 0, asn1Encodable));
        this.data = new BERSequence(asn1EncodableVector);
    }
    
    public ASN1ObjectIdentifier getContentType() {
        return ASN1ObjectIdentifier.getInstance(this.data.getObjectAt(0));
    }
    
    public AlgorithmIdentifier getEncryptionAlgorithm() {
        return AlgorithmIdentifier.getInstance(this.data.getObjectAt(1));
    }
    
    public ASN1OctetString getContent() {
        if (this.data.size() == 3) {
            return ASN1OctetString.getInstance(ASN1TaggedObject.getInstance(this.data.getObjectAt(2)), false);
        }
        return null;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(new ASN1Integer(0L));
        asn1EncodableVector.add(this.data);
        return new BERSequence(asn1EncodableVector);
    }
}
