package org.bouncycastle.asn1.pkcs;

import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.DLTaggedObject;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class SafeBag extends ASN1Object
{
    private ASN1ObjectIdentifier bagId;
    private ASN1Encodable bagValue;
    private ASN1Set bagAttributes;
    
    public SafeBag(final ASN1ObjectIdentifier bagId, final ASN1Encodable bagValue) {
        this.bagId = bagId;
        this.bagValue = bagValue;
        this.bagAttributes = null;
    }
    
    public SafeBag(final ASN1ObjectIdentifier bagId, final ASN1Encodable bagValue, final ASN1Set bagAttributes) {
        this.bagId = bagId;
        this.bagValue = bagValue;
        this.bagAttributes = bagAttributes;
    }
    
    public static SafeBag getInstance(final Object o) {
        if (o instanceof SafeBag) {
            return (SafeBag)o;
        }
        if (o != null) {
            return new SafeBag(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private SafeBag(final ASN1Sequence asn1Sequence) {
        this.bagId = (ASN1ObjectIdentifier)asn1Sequence.getObjectAt(0);
        this.bagValue = ((ASN1TaggedObject)asn1Sequence.getObjectAt(1)).getObject();
        if (asn1Sequence.size() == 3) {
            this.bagAttributes = (ASN1Set)asn1Sequence.getObjectAt(2);
        }
    }
    
    public ASN1ObjectIdentifier getBagId() {
        return this.bagId;
    }
    
    public ASN1Encodable getBagValue() {
        return this.bagValue;
    }
    
    public ASN1Set getBagAttributes() {
        return this.bagAttributes;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.bagId);
        asn1EncodableVector.add(new DLTaggedObject(true, 0, this.bagValue));
        if (this.bagAttributes != null) {
            asn1EncodableVector.add(this.bagAttributes);
        }
        return new DLSequence(asn1EncodableVector);
    }
}
