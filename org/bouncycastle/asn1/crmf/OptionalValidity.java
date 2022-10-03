package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.asn1.ASN1Object;

public class OptionalValidity extends ASN1Object
{
    private Time notBefore;
    private Time notAfter;
    
    private OptionalValidity(final ASN1Sequence asn1Sequence) {
        final Enumeration objects = asn1Sequence.getObjects();
        while (objects.hasMoreElements()) {
            final ASN1TaggedObject asn1TaggedObject = objects.nextElement();
            if (asn1TaggedObject.getTagNo() == 0) {
                this.notBefore = Time.getInstance(asn1TaggedObject, true);
            }
            else {
                this.notAfter = Time.getInstance(asn1TaggedObject, true);
            }
        }
    }
    
    public static OptionalValidity getInstance(final Object o) {
        if (o instanceof OptionalValidity) {
            return (OptionalValidity)o;
        }
        if (o != null) {
            return new OptionalValidity(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public OptionalValidity(final Time notBefore, final Time notAfter) {
        if (notBefore == null && notAfter == null) {
            throw new IllegalArgumentException("at least one of notBefore/notAfter must not be null.");
        }
        this.notBefore = notBefore;
        this.notAfter = notAfter;
    }
    
    public Time getNotBefore() {
        return this.notBefore;
    }
    
    public Time getNotAfter() {
        return this.notAfter;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        if (this.notBefore != null) {
            asn1EncodableVector.add(new DERTaggedObject(true, 0, this.notBefore));
        }
        if (this.notAfter != null) {
            asn1EncodableVector.add(new DERTaggedObject(true, 1, this.notAfter));
        }
        return new DERSequence(asn1EncodableVector);
    }
}
