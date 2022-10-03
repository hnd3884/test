package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Object;

public class KEKIdentifier extends ASN1Object
{
    private ASN1OctetString keyIdentifier;
    private ASN1GeneralizedTime date;
    private OtherKeyAttribute other;
    
    public KEKIdentifier(final byte[] array, final ASN1GeneralizedTime date, final OtherKeyAttribute other) {
        this.keyIdentifier = new DEROctetString(array);
        this.date = date;
        this.other = other;
    }
    
    private KEKIdentifier(final ASN1Sequence asn1Sequence) {
        this.keyIdentifier = (ASN1OctetString)asn1Sequence.getObjectAt(0);
        switch (asn1Sequence.size()) {
            case 1: {
                break;
            }
            case 2: {
                if (asn1Sequence.getObjectAt(1) instanceof ASN1GeneralizedTime) {
                    this.date = (ASN1GeneralizedTime)asn1Sequence.getObjectAt(1);
                    break;
                }
                this.other = OtherKeyAttribute.getInstance(asn1Sequence.getObjectAt(1));
                break;
            }
            case 3: {
                this.date = (ASN1GeneralizedTime)asn1Sequence.getObjectAt(1);
                this.other = OtherKeyAttribute.getInstance(asn1Sequence.getObjectAt(2));
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid KEKIdentifier");
            }
        }
    }
    
    public static KEKIdentifier getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static KEKIdentifier getInstance(final Object o) {
        if (o == null || o instanceof KEKIdentifier) {
            return (KEKIdentifier)o;
        }
        if (o instanceof ASN1Sequence) {
            return new KEKIdentifier((ASN1Sequence)o);
        }
        throw new IllegalArgumentException("Invalid KEKIdentifier: " + o.getClass().getName());
    }
    
    public ASN1OctetString getKeyIdentifier() {
        return this.keyIdentifier;
    }
    
    public ASN1GeneralizedTime getDate() {
        return this.date;
    }
    
    public OtherKeyAttribute getOther() {
        return this.other;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.keyIdentifier);
        if (this.date != null) {
            asn1EncodableVector.add(this.date);
        }
        if (this.other != null) {
            asn1EncodableVector.add(this.other);
        }
        return new DERSequence(asn1EncodableVector);
    }
}
