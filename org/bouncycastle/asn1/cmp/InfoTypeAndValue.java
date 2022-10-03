package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class InfoTypeAndValue extends ASN1Object
{
    private ASN1ObjectIdentifier infoType;
    private ASN1Encodable infoValue;
    
    private InfoTypeAndValue(final ASN1Sequence asn1Sequence) {
        this.infoType = ASN1ObjectIdentifier.getInstance(asn1Sequence.getObjectAt(0));
        if (asn1Sequence.size() > 1) {
            this.infoValue = asn1Sequence.getObjectAt(1);
        }
    }
    
    public static InfoTypeAndValue getInstance(final Object o) {
        if (o instanceof InfoTypeAndValue) {
            return (InfoTypeAndValue)o;
        }
        if (o != null) {
            return new InfoTypeAndValue(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public InfoTypeAndValue(final ASN1ObjectIdentifier infoType) {
        this.infoType = infoType;
        this.infoValue = null;
    }
    
    public InfoTypeAndValue(final ASN1ObjectIdentifier infoType, final ASN1Encodable infoValue) {
        this.infoType = infoType;
        this.infoValue = infoValue;
    }
    
    public ASN1ObjectIdentifier getInfoType() {
        return this.infoType;
    }
    
    public ASN1Encodable getInfoValue() {
        return this.infoValue;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.infoType);
        if (this.infoValue != null) {
            asn1EncodableVector.add(this.infoValue);
        }
        return new DERSequence(asn1EncodableVector);
    }
}
