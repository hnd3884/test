package org.bouncycastle.asn1.pkcs;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class CRLBag extends ASN1Object
{
    private ASN1ObjectIdentifier crlId;
    private ASN1Encodable crlValue;
    
    private CRLBag(final ASN1Sequence asn1Sequence) {
        this.crlId = (ASN1ObjectIdentifier)asn1Sequence.getObjectAt(0);
        this.crlValue = ((ASN1TaggedObject)asn1Sequence.getObjectAt(1)).getObject();
    }
    
    public static CRLBag getInstance(final Object o) {
        if (o instanceof CRLBag) {
            return (CRLBag)o;
        }
        if (o != null) {
            return new CRLBag(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public CRLBag(final ASN1ObjectIdentifier crlId, final ASN1Encodable crlValue) {
        this.crlId = crlId;
        this.crlValue = crlValue;
    }
    
    public ASN1ObjectIdentifier getCrlId() {
        return this.crlId;
    }
    
    public ASN1Encodable getCrlValue() {
        return this.crlValue;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.crlId);
        asn1EncodableVector.add(new DERTaggedObject(0, this.crlValue));
        return new DERSequence(asn1EncodableVector);
    }
}
