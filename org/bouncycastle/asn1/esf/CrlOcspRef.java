package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class CrlOcspRef extends ASN1Object
{
    private CrlListID crlids;
    private OcspListID ocspids;
    private OtherRevRefs otherRev;
    
    public static CrlOcspRef getInstance(final Object o) {
        if (o instanceof CrlOcspRef) {
            return (CrlOcspRef)o;
        }
        if (o != null) {
            return new CrlOcspRef(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private CrlOcspRef(final ASN1Sequence asn1Sequence) {
        final Enumeration objects = asn1Sequence.getObjects();
        while (objects.hasMoreElements()) {
            final ASN1TaggedObject asn1TaggedObject = objects.nextElement();
            switch (asn1TaggedObject.getTagNo()) {
                case 0: {
                    this.crlids = CrlListID.getInstance(asn1TaggedObject.getObject());
                    continue;
                }
                case 1: {
                    this.ocspids = OcspListID.getInstance(asn1TaggedObject.getObject());
                    continue;
                }
                case 2: {
                    this.otherRev = OtherRevRefs.getInstance(asn1TaggedObject.getObject());
                    continue;
                }
                default: {
                    throw new IllegalArgumentException("illegal tag");
                }
            }
        }
    }
    
    public CrlOcspRef(final CrlListID crlids, final OcspListID ocspids, final OtherRevRefs otherRev) {
        this.crlids = crlids;
        this.ocspids = ocspids;
        this.otherRev = otherRev;
    }
    
    public CrlListID getCrlids() {
        return this.crlids;
    }
    
    public OcspListID getOcspids() {
        return this.ocspids;
    }
    
    public OtherRevRefs getOtherRev() {
        return this.otherRev;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        if (null != this.crlids) {
            asn1EncodableVector.add(new DERTaggedObject(true, 0, this.crlids.toASN1Primitive()));
        }
        if (null != this.ocspids) {
            asn1EncodableVector.add(new DERTaggedObject(true, 1, this.ocspids.toASN1Primitive()));
        }
        if (null != this.otherRev) {
            asn1EncodableVector.add(new DERTaggedObject(true, 2, this.otherRev.toASN1Primitive()));
        }
        return new DERSequence(asn1EncodableVector);
    }
}
