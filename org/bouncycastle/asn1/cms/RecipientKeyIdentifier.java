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

public class RecipientKeyIdentifier extends ASN1Object
{
    private ASN1OctetString subjectKeyIdentifier;
    private ASN1GeneralizedTime date;
    private OtherKeyAttribute other;
    
    public RecipientKeyIdentifier(final ASN1OctetString subjectKeyIdentifier, final ASN1GeneralizedTime date, final OtherKeyAttribute other) {
        this.subjectKeyIdentifier = subjectKeyIdentifier;
        this.date = date;
        this.other = other;
    }
    
    public RecipientKeyIdentifier(final byte[] array, final ASN1GeneralizedTime date, final OtherKeyAttribute other) {
        this.subjectKeyIdentifier = new DEROctetString(array);
        this.date = date;
        this.other = other;
    }
    
    public RecipientKeyIdentifier(final byte[] array) {
        this(array, null, null);
    }
    
    @Deprecated
    public RecipientKeyIdentifier(final ASN1Sequence asn1Sequence) {
        this.subjectKeyIdentifier = ASN1OctetString.getInstance(asn1Sequence.getObjectAt(0));
        switch (asn1Sequence.size()) {
            case 1: {
                break;
            }
            case 2: {
                if (asn1Sequence.getObjectAt(1) instanceof ASN1GeneralizedTime) {
                    this.date = ASN1GeneralizedTime.getInstance(asn1Sequence.getObjectAt(1));
                    break;
                }
                this.other = OtherKeyAttribute.getInstance(asn1Sequence.getObjectAt(2));
                break;
            }
            case 3: {
                this.date = ASN1GeneralizedTime.getInstance(asn1Sequence.getObjectAt(1));
                this.other = OtherKeyAttribute.getInstance(asn1Sequence.getObjectAt(2));
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid RecipientKeyIdentifier");
            }
        }
    }
    
    public static RecipientKeyIdentifier getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static RecipientKeyIdentifier getInstance(final Object o) {
        if (o instanceof RecipientKeyIdentifier) {
            return (RecipientKeyIdentifier)o;
        }
        if (o != null) {
            return new RecipientKeyIdentifier(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public ASN1OctetString getSubjectKeyIdentifier() {
        return this.subjectKeyIdentifier;
    }
    
    public ASN1GeneralizedTime getDate() {
        return this.date;
    }
    
    public OtherKeyAttribute getOtherKeyAttribute() {
        return this.other;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.subjectKeyIdentifier);
        if (this.date != null) {
            asn1EncodableVector.add(this.date);
        }
        if (this.other != null) {
            asn1EncodableVector.add(this.other);
        }
        return new DERSequence(asn1EncodableVector);
    }
}
