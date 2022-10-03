package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class CertificatePair extends ASN1Object
{
    private Certificate forward;
    private Certificate reverse;
    
    public static CertificatePair getInstance(final Object o) {
        if (o == null || o instanceof CertificatePair) {
            return (CertificatePair)o;
        }
        if (o instanceof ASN1Sequence) {
            return new CertificatePair((ASN1Sequence)o);
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    private CertificatePair(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 1 && asn1Sequence.size() != 2) {
            throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
        }
        final Enumeration objects = asn1Sequence.getObjects();
        while (objects.hasMoreElements()) {
            final ASN1TaggedObject instance = ASN1TaggedObject.getInstance(objects.nextElement());
            if (instance.getTagNo() == 0) {
                this.forward = Certificate.getInstance(instance, true);
            }
            else {
                if (instance.getTagNo() != 1) {
                    throw new IllegalArgumentException("Bad tag number: " + instance.getTagNo());
                }
                this.reverse = Certificate.getInstance(instance, true);
            }
        }
    }
    
    public CertificatePair(final Certificate forward, final Certificate reverse) {
        this.forward = forward;
        this.reverse = reverse;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        if (this.forward != null) {
            asn1EncodableVector.add(new DERTaggedObject(0, this.forward));
        }
        if (this.reverse != null) {
            asn1EncodableVector.add(new DERTaggedObject(1, this.reverse));
        }
        return new DERSequence(asn1EncodableVector);
    }
    
    public Certificate getForward() {
        return this.forward;
    }
    
    public Certificate getReverse() {
        return this.reverse;
    }
}
