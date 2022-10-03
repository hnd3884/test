package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1EncodableVector;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class OtherRevVals extends ASN1Object
{
    private ASN1ObjectIdentifier otherRevValType;
    private ASN1Encodable otherRevVals;
    
    public static OtherRevVals getInstance(final Object o) {
        if (o instanceof OtherRevVals) {
            return (OtherRevVals)o;
        }
        if (o != null) {
            return new OtherRevVals(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private OtherRevVals(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 2) {
            throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
        }
        this.otherRevValType = (ASN1ObjectIdentifier)asn1Sequence.getObjectAt(0);
        try {
            this.otherRevVals = ASN1Primitive.fromByteArray(asn1Sequence.getObjectAt(1).toASN1Primitive().getEncoded("DER"));
        }
        catch (final IOException ex) {
            throw new IllegalStateException();
        }
    }
    
    public OtherRevVals(final ASN1ObjectIdentifier otherRevValType, final ASN1Encodable otherRevVals) {
        this.otherRevValType = otherRevValType;
        this.otherRevVals = otherRevVals;
    }
    
    public ASN1ObjectIdentifier getOtherRevValType() {
        return this.otherRevValType;
    }
    
    public ASN1Encodable getOtherRevVals() {
        return this.otherRevVals;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.otherRevValType);
        asn1EncodableVector.add(this.otherRevVals);
        return new DERSequence(asn1EncodableVector);
    }
}
