package org.bouncycastle.pqc.asn1;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Object;

public class ParSet extends ASN1Object
{
    private static final BigInteger ZERO;
    private int t;
    private int[] h;
    private int[] w;
    private int[] k;
    
    private static int checkBigIntegerInIntRangeAndPositive(final BigInteger bigInteger) {
        if (bigInteger.compareTo(BigInteger.valueOf(2147483647L)) > 0 || bigInteger.compareTo(ParSet.ZERO) <= 0) {
            throw new IllegalArgumentException("BigInteger not in Range: " + bigInteger.toString());
        }
        return bigInteger.intValue();
    }
    
    private ParSet(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 4) {
            throw new IllegalArgumentException("sie of seqOfParams = " + asn1Sequence.size());
        }
        this.t = checkBigIntegerInIntRangeAndPositive(((ASN1Integer)asn1Sequence.getObjectAt(0)).getValue());
        final ASN1Sequence asn1Sequence2 = (ASN1Sequence)asn1Sequence.getObjectAt(1);
        final ASN1Sequence asn1Sequence3 = (ASN1Sequence)asn1Sequence.getObjectAt(2);
        final ASN1Sequence asn1Sequence4 = (ASN1Sequence)asn1Sequence.getObjectAt(3);
        if (asn1Sequence2.size() != this.t || asn1Sequence3.size() != this.t || asn1Sequence4.size() != this.t) {
            throw new IllegalArgumentException("invalid size of sequences");
        }
        this.h = new int[asn1Sequence2.size()];
        this.w = new int[asn1Sequence3.size()];
        this.k = new int[asn1Sequence4.size()];
        for (int i = 0; i < this.t; ++i) {
            this.h[i] = checkBigIntegerInIntRangeAndPositive(((ASN1Integer)asn1Sequence2.getObjectAt(i)).getValue());
            this.w[i] = checkBigIntegerInIntRangeAndPositive(((ASN1Integer)asn1Sequence3.getObjectAt(i)).getValue());
            this.k[i] = checkBigIntegerInIntRangeAndPositive(((ASN1Integer)asn1Sequence4.getObjectAt(i)).getValue());
        }
    }
    
    public ParSet(final int t, final int[] h, final int[] w, final int[] k) {
        this.t = t;
        this.h = h;
        this.w = w;
        this.k = k;
    }
    
    public static ParSet getInstance(final Object o) {
        if (o instanceof ParSet) {
            return (ParSet)o;
        }
        if (o != null) {
            return new ParSet(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public int getT() {
        return this.t;
    }
    
    public int[] getH() {
        return Arrays.clone(this.h);
    }
    
    public int[] getW() {
        return Arrays.clone(this.w);
    }
    
    public int[] getK() {
        return Arrays.clone(this.k);
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        final ASN1EncodableVector asn1EncodableVector2 = new ASN1EncodableVector();
        final ASN1EncodableVector asn1EncodableVector3 = new ASN1EncodableVector();
        for (int i = 0; i < this.h.length; ++i) {
            asn1EncodableVector.add(new ASN1Integer(this.h[i]));
            asn1EncodableVector2.add(new ASN1Integer(this.w[i]));
            asn1EncodableVector3.add(new ASN1Integer(this.k[i]));
        }
        final ASN1EncodableVector asn1EncodableVector4 = new ASN1EncodableVector();
        asn1EncodableVector4.add(new ASN1Integer(this.t));
        asn1EncodableVector4.add(new DERSequence(asn1EncodableVector));
        asn1EncodableVector4.add(new DERSequence(asn1EncodableVector2));
        asn1EncodableVector4.add(new DERSequence(asn1EncodableVector3));
        return new DERSequence(asn1EncodableVector4);
    }
    
    static {
        ZERO = BigInteger.valueOf(0L);
    }
}
