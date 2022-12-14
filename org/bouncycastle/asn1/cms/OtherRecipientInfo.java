package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class OtherRecipientInfo extends ASN1Object
{
    private ASN1ObjectIdentifier oriType;
    private ASN1Encodable oriValue;
    
    public OtherRecipientInfo(final ASN1ObjectIdentifier oriType, final ASN1Encodable oriValue) {
        this.oriType = oriType;
        this.oriValue = oriValue;
    }
    
    @Deprecated
    public OtherRecipientInfo(final ASN1Sequence asn1Sequence) {
        this.oriType = ASN1ObjectIdentifier.getInstance(asn1Sequence.getObjectAt(0));
        this.oriValue = asn1Sequence.getObjectAt(1);
    }
    
    public static OtherRecipientInfo getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static OtherRecipientInfo getInstance(final Object o) {
        if (o instanceof OtherRecipientInfo) {
            return (OtherRecipientInfo)o;
        }
        if (o != null) {
            return new OtherRecipientInfo(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public ASN1ObjectIdentifier getType() {
        return this.oriType;
    }
    
    public ASN1Encodable getValue() {
        return this.oriValue;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.oriType);
        asn1EncodableVector.add(this.oriValue);
        return new DERSequence(asn1EncodableVector);
    }
}
