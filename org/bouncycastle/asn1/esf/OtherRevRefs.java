package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1EncodableVector;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class OtherRevRefs extends ASN1Object
{
    private ASN1ObjectIdentifier otherRevRefType;
    private ASN1Encodable otherRevRefs;
    
    public static OtherRevRefs getInstance(final Object o) {
        if (o instanceof OtherRevRefs) {
            return (OtherRevRefs)o;
        }
        if (o != null) {
            return new OtherRevRefs(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private OtherRevRefs(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 2) {
            throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
        }
        this.otherRevRefType = new ASN1ObjectIdentifier(((ASN1ObjectIdentifier)asn1Sequence.getObjectAt(0)).getId());
        try {
            this.otherRevRefs = ASN1Primitive.fromByteArray(asn1Sequence.getObjectAt(1).toASN1Primitive().getEncoded("DER"));
        }
        catch (final IOException ex) {
            throw new IllegalStateException();
        }
    }
    
    public OtherRevRefs(final ASN1ObjectIdentifier otherRevRefType, final ASN1Encodable otherRevRefs) {
        this.otherRevRefType = otherRevRefType;
        this.otherRevRefs = otherRevRefs;
    }
    
    public ASN1ObjectIdentifier getOtherRevRefType() {
        return this.otherRevRefType;
    }
    
    public ASN1Encodable getOtherRevRefs() {
        return this.otherRevRefs;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.otherRevRefType);
        asn1EncodableVector.add(this.otherRevRefs);
        return new DERSequence(asn1EncodableVector);
    }
}
