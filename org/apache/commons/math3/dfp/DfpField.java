package org.apache.commons.math3.dfp;

import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.Field;

public class DfpField implements Field<Dfp>
{
    public static final int FLAG_INVALID = 1;
    public static final int FLAG_DIV_ZERO = 2;
    public static final int FLAG_OVERFLOW = 4;
    public static final int FLAG_UNDERFLOW = 8;
    public static final int FLAG_INEXACT = 16;
    private static String sqr2String;
    private static String sqr2ReciprocalString;
    private static String sqr3String;
    private static String sqr3ReciprocalString;
    private static String piString;
    private static String eString;
    private static String ln2String;
    private static String ln5String;
    private static String ln10String;
    private final int radixDigits;
    private final Dfp zero;
    private final Dfp one;
    private final Dfp two;
    private final Dfp sqr2;
    private final Dfp[] sqr2Split;
    private final Dfp sqr2Reciprocal;
    private final Dfp sqr3;
    private final Dfp sqr3Reciprocal;
    private final Dfp pi;
    private final Dfp[] piSplit;
    private final Dfp e;
    private final Dfp[] eSplit;
    private final Dfp ln2;
    private final Dfp[] ln2Split;
    private final Dfp ln5;
    private final Dfp[] ln5Split;
    private final Dfp ln10;
    private RoundingMode rMode;
    private int ieeeFlags;
    
    public DfpField(final int decimalDigits) {
        this(decimalDigits, true);
    }
    
    private DfpField(final int decimalDigits, final boolean computeConstants) {
        this.radixDigits = ((decimalDigits < 13) ? 4 : ((decimalDigits + 3) / 4));
        this.rMode = RoundingMode.ROUND_HALF_EVEN;
        this.ieeeFlags = 0;
        this.zero = new Dfp(this, 0);
        this.one = new Dfp(this, 1);
        this.two = new Dfp(this, 2);
        if (computeConstants) {
            synchronized (DfpField.class) {
                computeStringConstants((decimalDigits < 67) ? 200 : (3 * decimalDigits));
                this.sqr2 = new Dfp(this, DfpField.sqr2String);
                this.sqr2Split = this.split(DfpField.sqr2String);
                this.sqr2Reciprocal = new Dfp(this, DfpField.sqr2ReciprocalString);
                this.sqr3 = new Dfp(this, DfpField.sqr3String);
                this.sqr3Reciprocal = new Dfp(this, DfpField.sqr3ReciprocalString);
                this.pi = new Dfp(this, DfpField.piString);
                this.piSplit = this.split(DfpField.piString);
                this.e = new Dfp(this, DfpField.eString);
                this.eSplit = this.split(DfpField.eString);
                this.ln2 = new Dfp(this, DfpField.ln2String);
                this.ln2Split = this.split(DfpField.ln2String);
                this.ln5 = new Dfp(this, DfpField.ln5String);
                this.ln5Split = this.split(DfpField.ln5String);
                this.ln10 = new Dfp(this, DfpField.ln10String);
            }
        }
        else {
            this.sqr2 = null;
            this.sqr2Split = null;
            this.sqr2Reciprocal = null;
            this.sqr3 = null;
            this.sqr3Reciprocal = null;
            this.pi = null;
            this.piSplit = null;
            this.e = null;
            this.eSplit = null;
            this.ln2 = null;
            this.ln2Split = null;
            this.ln5 = null;
            this.ln5Split = null;
            this.ln10 = null;
        }
    }
    
    public int getRadixDigits() {
        return this.radixDigits;
    }
    
    public void setRoundingMode(final RoundingMode mode) {
        this.rMode = mode;
    }
    
    public RoundingMode getRoundingMode() {
        return this.rMode;
    }
    
    public int getIEEEFlags() {
        return this.ieeeFlags;
    }
    
    public void clearIEEEFlags() {
        this.ieeeFlags = 0;
    }
    
    public void setIEEEFlags(final int flags) {
        this.ieeeFlags = (flags & 0x1F);
    }
    
    public void setIEEEFlagsBits(final int bits) {
        this.ieeeFlags |= (bits & 0x1F);
    }
    
    public Dfp newDfp() {
        return new Dfp(this);
    }
    
    public Dfp newDfp(final byte x) {
        return new Dfp(this, x);
    }
    
    public Dfp newDfp(final int x) {
        return new Dfp(this, x);
    }
    
    public Dfp newDfp(final long x) {
        return new Dfp(this, x);
    }
    
    public Dfp newDfp(final double x) {
        return new Dfp(this, x);
    }
    
    public Dfp newDfp(final Dfp d) {
        return new Dfp(d);
    }
    
    public Dfp newDfp(final String s) {
        return new Dfp(this, s);
    }
    
    public Dfp newDfp(final byte sign, final byte nans) {
        return new Dfp(this, sign, nans);
    }
    
    public Dfp getZero() {
        return this.zero;
    }
    
    public Dfp getOne() {
        return this.one;
    }
    
    public Class<? extends FieldElement<Dfp>> getRuntimeClass() {
        return Dfp.class;
    }
    
    public Dfp getTwo() {
        return this.two;
    }
    
    public Dfp getSqr2() {
        return this.sqr2;
    }
    
    public Dfp[] getSqr2Split() {
        return this.sqr2Split.clone();
    }
    
    public Dfp getSqr2Reciprocal() {
        return this.sqr2Reciprocal;
    }
    
    public Dfp getSqr3() {
        return this.sqr3;
    }
    
    public Dfp getSqr3Reciprocal() {
        return this.sqr3Reciprocal;
    }
    
    public Dfp getPi() {
        return this.pi;
    }
    
    public Dfp[] getPiSplit() {
        return this.piSplit.clone();
    }
    
    public Dfp getE() {
        return this.e;
    }
    
    public Dfp[] getESplit() {
        return this.eSplit.clone();
    }
    
    public Dfp getLn2() {
        return this.ln2;
    }
    
    public Dfp[] getLn2Split() {
        return this.ln2Split.clone();
    }
    
    public Dfp getLn5() {
        return this.ln5;
    }
    
    public Dfp[] getLn5Split() {
        return this.ln5Split.clone();
    }
    
    public Dfp getLn10() {
        return this.ln10;
    }
    
    private Dfp[] split(final String a) {
        final Dfp[] result = new Dfp[2];
        boolean leading = true;
        int sp = 0;
        int sig = 0;
        final char[] buf = new char[a.length()];
        for (int i = 0; i < buf.length; ++i) {
            buf[i] = a.charAt(i);
            if (buf[i] >= '1' && buf[i] <= '9') {
                leading = false;
            }
            if (buf[i] == '.') {
                sig += (400 - sig) % 4;
                leading = false;
            }
            if (sig == this.radixDigits / 2 * 4) {
                sp = i;
                break;
            }
            if (buf[i] >= '0' && buf[i] <= '9' && !leading) {
                ++sig;
            }
        }
        result[0] = new Dfp(this, new String(buf, 0, sp));
        for (int i = 0; i < buf.length; ++i) {
            buf[i] = a.charAt(i);
            if (buf[i] >= '0' && buf[i] <= '9' && i < sp) {
                buf[i] = '0';
            }
        }
        result[1] = new Dfp(this, new String(buf));
        return result;
    }
    
    private static void computeStringConstants(final int highPrecisionDecimalDigits) {
        if (DfpField.sqr2String == null || DfpField.sqr2String.length() < highPrecisionDecimalDigits - 3) {
            final DfpField highPrecisionField = new DfpField(highPrecisionDecimalDigits, false);
            final Dfp highPrecisionOne = new Dfp(highPrecisionField, 1);
            final Dfp highPrecisionTwo = new Dfp(highPrecisionField, 2);
            final Dfp highPrecisionThree = new Dfp(highPrecisionField, 3);
            final Dfp highPrecisionSqr2 = highPrecisionTwo.sqrt();
            DfpField.sqr2String = highPrecisionSqr2.toString();
            DfpField.sqr2ReciprocalString = highPrecisionOne.divide(highPrecisionSqr2).toString();
            final Dfp highPrecisionSqr3 = highPrecisionThree.sqrt();
            DfpField.sqr3String = highPrecisionSqr3.toString();
            DfpField.sqr3ReciprocalString = highPrecisionOne.divide(highPrecisionSqr3).toString();
            DfpField.piString = computePi(highPrecisionOne, highPrecisionTwo, highPrecisionThree).toString();
            DfpField.eString = computeExp(highPrecisionOne, highPrecisionOne).toString();
            DfpField.ln2String = computeLn(highPrecisionTwo, highPrecisionOne, highPrecisionTwo).toString();
            DfpField.ln5String = computeLn(new Dfp(highPrecisionField, 5), highPrecisionOne, highPrecisionTwo).toString();
            DfpField.ln10String = computeLn(new Dfp(highPrecisionField, 10), highPrecisionOne, highPrecisionTwo).toString();
        }
    }
    
    private static Dfp computePi(final Dfp one, final Dfp two, final Dfp three) {
        final Dfp sqrt2 = two.sqrt();
        Dfp yk = sqrt2.subtract(one);
        final Dfp four = two.add(two);
        Dfp two2kp3 = two;
        Dfp ak = two.multiply(three.subtract(two.multiply(sqrt2)));
        for (int i = 1; i < 20; ++i) {
            final Dfp ykM1 = yk;
            final Dfp y2 = yk.multiply(yk);
            final Dfp oneMinusY4 = one.subtract(y2.multiply(y2));
            final Dfp s = oneMinusY4.sqrt().sqrt();
            yk = one.subtract(s).divide(one.add(s));
            two2kp3 = two2kp3.multiply(four);
            final Dfp p = one.add(yk);
            final Dfp p2 = p.multiply(p);
            ak = ak.multiply(p2.multiply(p2)).subtract(two2kp3.multiply(yk).multiply(one.add(yk).add(yk.multiply(yk))));
            if (yk.equals(ykM1)) {
                break;
            }
        }
        return one.divide(ak);
    }
    
    public static Dfp computeExp(final Dfp a, final Dfp one) {
        Dfp y = new Dfp(one);
        Dfp py = new Dfp(one);
        Dfp f = new Dfp(one);
        Dfp fi = new Dfp(one);
        Dfp x = new Dfp(one);
        for (int i = 0; i < 10000; ++i) {
            x = x.multiply(a);
            y = y.add(x.divide(f));
            fi = fi.add(one);
            f = f.multiply(fi);
            if (y.equals(py)) {
                break;
            }
            py = new Dfp(y);
        }
        return y;
    }
    
    public static Dfp computeLn(final Dfp a, final Dfp one, final Dfp two) {
        int den = 1;
        final Dfp x = a.add(new Dfp(a.getField(), -1)).divide(a.add(one));
        Dfp y = new Dfp(x);
        Dfp num = new Dfp(x);
        Dfp py = new Dfp(y);
        for (int i = 0; i < 10000; ++i) {
            num = num.multiply(x);
            num = num.multiply(x);
            den += 2;
            final Dfp t = num.divide(den);
            y = y.add(t);
            if (y.equals(py)) {
                break;
            }
            py = new Dfp(y);
        }
        return y.multiply(two);
    }
    
    public enum RoundingMode
    {
        ROUND_DOWN, 
        ROUND_UP, 
        ROUND_HALF_UP, 
        ROUND_HALF_DOWN, 
        ROUND_HALF_EVEN, 
        ROUND_HALF_ODD, 
        ROUND_CEIL, 
        ROUND_FLOOR;
    }
}
