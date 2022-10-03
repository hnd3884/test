package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Integer;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Object;

public class GeneralSubtree extends ASN1Object
{
    private static final BigInteger ZERO;
    private GeneralName base;
    private ASN1Integer minimum;
    private ASN1Integer maximum;
    
    private GeneralSubtree(final ASN1Sequence asn1Sequence) {
        this.base = GeneralName.getInstance(asn1Sequence.getObjectAt(0));
        Label_0290: {
            switch (asn1Sequence.size()) {
                case 1: {
                    break;
                }
                case 2: {
                    final ASN1TaggedObject instance = ASN1TaggedObject.getInstance(asn1Sequence.getObjectAt(1));
                    switch (instance.getTagNo()) {
                        case 0: {
                            this.minimum = ASN1Integer.getInstance(instance, false);
                            break Label_0290;
                        }
                        case 1: {
                            this.maximum = ASN1Integer.getInstance(instance, false);
                            break Label_0290;
                        }
                        default: {
                            throw new IllegalArgumentException("Bad tag number: " + instance.getTagNo());
                        }
                    }
                    break;
                }
                case 3: {
                    final ASN1TaggedObject instance2 = ASN1TaggedObject.getInstance(asn1Sequence.getObjectAt(1));
                    if (instance2.getTagNo() != 0) {
                        throw new IllegalArgumentException("Bad tag number for 'minimum': " + instance2.getTagNo());
                    }
                    this.minimum = ASN1Integer.getInstance(instance2, false);
                    final ASN1TaggedObject instance3 = ASN1TaggedObject.getInstance(asn1Sequence.getObjectAt(2));
                    if (instance3.getTagNo() != 1) {
                        throw new IllegalArgumentException("Bad tag number for 'maximum': " + instance3.getTagNo());
                    }
                    this.maximum = ASN1Integer.getInstance(instance3, false);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
                }
            }
        }
    }
    
    public GeneralSubtree(final GeneralName base, final BigInteger bigInteger, final BigInteger bigInteger2) {
        this.base = base;
        if (bigInteger2 != null) {
            this.maximum = new ASN1Integer(bigInteger2);
        }
        if (bigInteger == null) {
            this.minimum = null;
        }
        else {
            this.minimum = new ASN1Integer(bigInteger);
        }
    }
    
    public GeneralSubtree(final GeneralName generalName) {
        this(generalName, null, null);
    }
    
    public static GeneralSubtree getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return new GeneralSubtree(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static GeneralSubtree getInstance(final Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof GeneralSubtree) {
            return (GeneralSubtree)o;
        }
        return new GeneralSubtree(ASN1Sequence.getInstance(o));
    }
    
    public GeneralName getBase() {
        return this.base;
    }
    
    public BigInteger getMinimum() {
        if (this.minimum == null) {
            return GeneralSubtree.ZERO;
        }
        return this.minimum.getValue();
    }
    
    public BigInteger getMaximum() {
        if (this.maximum == null) {
            return null;
        }
        return this.maximum.getValue();
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.base);
        if (this.minimum != null && !this.minimum.getValue().equals(GeneralSubtree.ZERO)) {
            asn1EncodableVector.add(new DERTaggedObject(false, 0, this.minimum));
        }
        if (this.maximum != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 1, this.maximum));
        }
        return new DERSequence(asn1EncodableVector);
    }
    
    static {
        ZERO = BigInteger.valueOf(0L);
    }
}
