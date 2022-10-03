package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.ASN1Object;

public class CrlID extends ASN1Object
{
    private DERIA5String crlUrl;
    private ASN1Integer crlNum;
    private ASN1GeneralizedTime crlTime;
    
    private CrlID(final ASN1Sequence asn1Sequence) {
        final Enumeration objects = asn1Sequence.getObjects();
        while (objects.hasMoreElements()) {
            final ASN1TaggedObject asn1TaggedObject = objects.nextElement();
            switch (asn1TaggedObject.getTagNo()) {
                case 0: {
                    this.crlUrl = DERIA5String.getInstance(asn1TaggedObject, true);
                    continue;
                }
                case 1: {
                    this.crlNum = ASN1Integer.getInstance(asn1TaggedObject, true);
                    continue;
                }
                case 2: {
                    this.crlTime = ASN1GeneralizedTime.getInstance(asn1TaggedObject, true);
                    continue;
                }
                default: {
                    throw new IllegalArgumentException("unknown tag number: " + asn1TaggedObject.getTagNo());
                }
            }
        }
    }
    
    public static CrlID getInstance(final Object o) {
        if (o instanceof CrlID) {
            return (CrlID)o;
        }
        if (o != null) {
            return new CrlID(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public DERIA5String getCrlUrl() {
        return this.crlUrl;
    }
    
    public ASN1Integer getCrlNum() {
        return this.crlNum;
    }
    
    public ASN1GeneralizedTime getCrlTime() {
        return this.crlTime;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        if (this.crlUrl != null) {
            asn1EncodableVector.add(new DERTaggedObject(true, 0, this.crlUrl));
        }
        if (this.crlNum != null) {
            asn1EncodableVector.add(new DERTaggedObject(true, 1, this.crlNum));
        }
        if (this.crlTime != null) {
            asn1EncodableVector.add(new DERTaggedObject(true, 2, this.crlTime));
        }
        return new DERSequence(asn1EncodableVector);
    }
}
