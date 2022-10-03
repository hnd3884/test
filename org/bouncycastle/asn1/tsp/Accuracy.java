package org.bouncycastle.asn1.tsp;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;

public class Accuracy extends ASN1Object
{
    ASN1Integer seconds;
    ASN1Integer millis;
    ASN1Integer micros;
    protected static final int MIN_MILLIS = 1;
    protected static final int MAX_MILLIS = 999;
    protected static final int MIN_MICROS = 1;
    protected static final int MAX_MICROS = 999;
    
    protected Accuracy() {
    }
    
    public Accuracy(final ASN1Integer seconds, final ASN1Integer millis, final ASN1Integer micros) {
        this.seconds = seconds;
        if (millis != null && (millis.getValue().intValue() < 1 || millis.getValue().intValue() > 999)) {
            throw new IllegalArgumentException("Invalid millis field : not in (1..999)");
        }
        this.millis = millis;
        if (micros != null && (micros.getValue().intValue() < 1 || micros.getValue().intValue() > 999)) {
            throw new IllegalArgumentException("Invalid micros field : not in (1..999)");
        }
        this.micros = micros;
    }
    
    private Accuracy(final ASN1Sequence asn1Sequence) {
        this.seconds = null;
        this.millis = null;
        this.micros = null;
        for (int i = 0; i < asn1Sequence.size(); ++i) {
            if (asn1Sequence.getObjectAt(i) instanceof ASN1Integer) {
                this.seconds = (ASN1Integer)asn1Sequence.getObjectAt(i);
            }
            else if (asn1Sequence.getObjectAt(i) instanceof ASN1TaggedObject) {
                final ASN1TaggedObject asn1TaggedObject = (ASN1TaggedObject)asn1Sequence.getObjectAt(i);
                switch (asn1TaggedObject.getTagNo()) {
                    case 0: {
                        this.millis = ASN1Integer.getInstance(asn1TaggedObject, false);
                        if (this.millis.getValue().intValue() < 1 || this.millis.getValue().intValue() > 999) {
                            throw new IllegalArgumentException("Invalid millis field : not in (1..999).");
                        }
                        break;
                    }
                    case 1: {
                        this.micros = ASN1Integer.getInstance(asn1TaggedObject, false);
                        if (this.micros.getValue().intValue() < 1 || this.micros.getValue().intValue() > 999) {
                            throw new IllegalArgumentException("Invalid micros field : not in (1..999).");
                        }
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("Invalig tag number");
                    }
                }
            }
        }
    }
    
    public static Accuracy getInstance(final Object o) {
        if (o instanceof Accuracy) {
            return (Accuracy)o;
        }
        if (o != null) {
            return new Accuracy(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public ASN1Integer getSeconds() {
        return this.seconds;
    }
    
    public ASN1Integer getMillis() {
        return this.millis;
    }
    
    public ASN1Integer getMicros() {
        return this.micros;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        if (this.seconds != null) {
            asn1EncodableVector.add(this.seconds);
        }
        if (this.millis != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 0, this.millis));
        }
        if (this.micros != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 1, this.micros));
        }
        return new DERSequence(asn1EncodableVector);
    }
}
