package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1Object;

public class OriginatorInfo extends ASN1Object
{
    private ASN1Set certs;
    private ASN1Set crls;
    
    public OriginatorInfo(final ASN1Set certs, final ASN1Set crls) {
        this.certs = certs;
        this.crls = crls;
    }
    
    private OriginatorInfo(final ASN1Sequence asn1Sequence) {
        Label_0179: {
            switch (asn1Sequence.size()) {
                case 0: {
                    break;
                }
                case 1: {
                    final ASN1TaggedObject asn1TaggedObject = (ASN1TaggedObject)asn1Sequence.getObjectAt(0);
                    switch (asn1TaggedObject.getTagNo()) {
                        case 0: {
                            this.certs = ASN1Set.getInstance(asn1TaggedObject, false);
                            break Label_0179;
                        }
                        case 1: {
                            this.crls = ASN1Set.getInstance(asn1TaggedObject, false);
                            break Label_0179;
                        }
                        default: {
                            throw new IllegalArgumentException("Bad tag in OriginatorInfo: " + asn1TaggedObject.getTagNo());
                        }
                    }
                    break;
                }
                case 2: {
                    this.certs = ASN1Set.getInstance((ASN1TaggedObject)asn1Sequence.getObjectAt(0), false);
                    this.crls = ASN1Set.getInstance((ASN1TaggedObject)asn1Sequence.getObjectAt(1), false);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("OriginatorInfo too big");
                }
            }
        }
    }
    
    public static OriginatorInfo getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static OriginatorInfo getInstance(final Object o) {
        if (o instanceof OriginatorInfo) {
            return (OriginatorInfo)o;
        }
        if (o != null) {
            return new OriginatorInfo(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public ASN1Set getCertificates() {
        return this.certs;
    }
    
    public ASN1Set getCRLs() {
        return this.crls;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        if (this.certs != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 0, this.certs));
        }
        if (this.crls != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 1, this.crls));
        }
        return new DERSequence(asn1EncodableVector);
    }
}
