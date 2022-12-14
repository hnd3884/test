package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Object;

public class PrivateKeyUsagePeriod extends ASN1Object
{
    private ASN1GeneralizedTime _notBefore;
    private ASN1GeneralizedTime _notAfter;
    
    public static PrivateKeyUsagePeriod getInstance(final Object o) {
        if (o instanceof PrivateKeyUsagePeriod) {
            return (PrivateKeyUsagePeriod)o;
        }
        if (o != null) {
            return new PrivateKeyUsagePeriod(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private PrivateKeyUsagePeriod(final ASN1Sequence asn1Sequence) {
        final Enumeration objects = asn1Sequence.getObjects();
        while (objects.hasMoreElements()) {
            final ASN1TaggedObject asn1TaggedObject = objects.nextElement();
            if (asn1TaggedObject.getTagNo() == 0) {
                this._notBefore = ASN1GeneralizedTime.getInstance(asn1TaggedObject, false);
            }
            else {
                if (asn1TaggedObject.getTagNo() != 1) {
                    continue;
                }
                this._notAfter = ASN1GeneralizedTime.getInstance(asn1TaggedObject, false);
            }
        }
    }
    
    public ASN1GeneralizedTime getNotBefore() {
        return this._notBefore;
    }
    
    public ASN1GeneralizedTime getNotAfter() {
        return this._notAfter;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        if (this._notBefore != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 0, this._notBefore));
        }
        if (this._notAfter != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 1, this._notAfter));
        }
        return new DERSequence(asn1EncodableVector);
    }
}
