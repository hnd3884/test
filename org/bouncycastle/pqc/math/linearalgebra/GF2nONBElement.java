package org.bouncycastle.pqc.math.linearalgebra;

import java.math.BigInteger;
import java.security.SecureRandom;

public class GF2nONBElement extends GF2nElement
{
    private static final long[] mBitmask;
    private static final long[] mMaxmask;
    private static final int[] mIBY64;
    private static final int MAXLONG = 64;
    private int mLength;
    private int mBit;
    private long[] mPol;
    
    public GF2nONBElement(final GF2nONBField mField, final SecureRandom secureRandom) {
        this.mField = mField;
        this.mDegree = this.mField.getDegree();
        this.mLength = mField.getONBLength();
        this.mBit = mField.getONBBit();
        this.mPol = new long[this.mLength];
        if (this.mLength > 1) {
            for (int i = 0; i < this.mLength - 1; ++i) {
                this.mPol[i] = secureRandom.nextLong();
            }
            this.mPol[this.mLength - 1] = secureRandom.nextLong() >>> 64 - this.mBit;
        }
        else {
            this.mPol[0] = secureRandom.nextLong();
            this.mPol[0] >>>= 64 - this.mBit;
        }
    }
    
    public GF2nONBElement(final GF2nONBField mField, final byte[] array) {
        this.mField = mField;
        this.mDegree = this.mField.getDegree();
        this.mLength = mField.getONBLength();
        this.mBit = mField.getONBBit();
        this.mPol = new long[this.mLength];
        this.assign(array);
    }
    
    public GF2nONBElement(final GF2nONBField mField, final BigInteger bigInteger) {
        this.mField = mField;
        this.mDegree = this.mField.getDegree();
        this.mLength = mField.getONBLength();
        this.mBit = mField.getONBBit();
        this.mPol = new long[this.mLength];
        this.assign(bigInteger);
    }
    
    private GF2nONBElement(final GF2nONBField mField, final long[] mPol) {
        this.mField = mField;
        this.mDegree = this.mField.getDegree();
        this.mLength = mField.getONBLength();
        this.mBit = mField.getONBBit();
        this.mPol = mPol;
    }
    
    public GF2nONBElement(final GF2nONBElement gf2nONBElement) {
        this.mField = gf2nONBElement.mField;
        this.mDegree = this.mField.getDegree();
        this.mLength = ((GF2nONBField)this.mField).getONBLength();
        this.mBit = ((GF2nONBField)this.mField).getONBBit();
        this.mPol = new long[this.mLength];
        this.assign(gf2nONBElement.getElement());
    }
    
    @Override
    public Object clone() {
        return new GF2nONBElement(this);
    }
    
    public static GF2nONBElement ZERO(final GF2nONBField gf2nONBField) {
        return new GF2nONBElement(gf2nONBField, new long[gf2nONBField.getONBLength()]);
    }
    
    public static GF2nONBElement ONE(final GF2nONBField gf2nONBField) {
        final int onbLength = gf2nONBField.getONBLength();
        final long[] array = new long[onbLength];
        for (int i = 0; i < onbLength - 1; ++i) {
            array[i] = -1L;
        }
        array[onbLength - 1] = GF2nONBElement.mMaxmask[gf2nONBField.getONBBit() - 1];
        return new GF2nONBElement(gf2nONBField, array);
    }
    
    @Override
    void assignZero() {
        this.mPol = new long[this.mLength];
    }
    
    @Override
    void assignOne() {
        for (int i = 0; i < this.mLength - 1; ++i) {
            this.mPol[i] = -1L;
        }
        this.mPol[this.mLength - 1] = GF2nONBElement.mMaxmask[this.mBit - 1];
    }
    
    private void assign(final BigInteger bigInteger) {
        this.assign(bigInteger.toByteArray());
    }
    
    private void assign(final long[] array) {
        System.arraycopy(array, 0, this.mPol, 0, this.mLength);
    }
    
    private void assign(final byte[] array) {
        this.mPol = new long[this.mLength];
        for (int i = 0; i < array.length; ++i) {
            final long[] mPol = this.mPol;
            final int n = i >>> 3;
            mPol[n] |= ((long)array[array.length - 1 - i] & 0xFFL) << ((i & 0x7) << 3);
        }
    }
    
    public boolean isZero() {
        boolean b = true;
        for (int n = 0; n < this.mLength && b; b = (b && (this.mPol[n] & -1L) == 0x0L), ++n) {}
        return b;
    }
    
    public boolean isOne() {
        boolean b = true;
        for (int n = 0; n < this.mLength - 1 && b; b = (b && (this.mPol[n] & -1L) == -1L), ++n) {}
        if (b) {
            b = (b && (this.mPol[this.mLength - 1] & GF2nONBElement.mMaxmask[this.mBit - 1]) == GF2nONBElement.mMaxmask[this.mBit - 1]);
        }
        return b;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof GF2nONBElement)) {
            return false;
        }
        final GF2nONBElement gf2nONBElement = (GF2nONBElement)o;
        for (int i = 0; i < this.mLength; ++i) {
            if (this.mPol[i] != gf2nONBElement.mPol[i]) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        return this.mPol.hashCode();
    }
    
    @Override
    public boolean testRightmostBit() {
        return (this.mPol[this.mLength - 1] & GF2nONBElement.mBitmask[this.mBit - 1]) != 0x0L;
    }
    
    @Override
    boolean testBit(final int n) {
        return n >= 0 && n <= this.mDegree && (this.mPol[n >>> 6] & GF2nONBElement.mBitmask[n & 0x3F]) != 0x0L;
    }
    
    private long[] getElement() {
        final long[] array = new long[this.mPol.length];
        System.arraycopy(this.mPol, 0, array, 0, this.mPol.length);
        return array;
    }
    
    private long[] getElementReverseOrder() {
        final long[] array = new long[this.mPol.length];
        for (int i = 0; i < this.mDegree; ++i) {
            if (this.testBit(this.mDegree - i - 1)) {
                final long[] array2 = array;
                final int n = i >>> 6;
                array2[n] |= GF2nONBElement.mBitmask[i & 0x3F];
            }
        }
        return array;
    }
    
    void reverseOrder() {
        this.mPol = this.getElementReverseOrder();
    }
    
    public GFElement add(final GFElement gfElement) throws RuntimeException {
        final GF2nONBElement gf2nONBElement = new GF2nONBElement(this);
        gf2nONBElement.addToThis(gfElement);
        return gf2nONBElement;
    }
    
    public void addToThis(final GFElement gfElement) throws RuntimeException {
        if (!(gfElement instanceof GF2nONBElement)) {
            throw new RuntimeException();
        }
        if (!this.mField.equals(((GF2nONBElement)gfElement).mField)) {
            throw new RuntimeException();
        }
        for (int i = 0; i < this.mLength; ++i) {
            final long[] mPol = this.mPol;
            final int n = i;
            mPol[n] ^= ((GF2nONBElement)gfElement).mPol[i];
        }
    }
    
    @Override
    public GF2nElement increase() {
        final GF2nONBElement gf2nONBElement = new GF2nONBElement(this);
        gf2nONBElement.increaseThis();
        return gf2nONBElement;
    }
    
    @Override
    public void increaseThis() {
        this.addToThis(ONE((GF2nONBField)this.mField));
    }
    
    public GFElement multiply(final GFElement gfElement) throws RuntimeException {
        final GF2nONBElement gf2nONBElement = new GF2nONBElement(this);
        gf2nONBElement.multiplyThisBy(gfElement);
        return gf2nONBElement;
    }
    
    public void multiplyThisBy(final GFElement gfElement) throws RuntimeException {
        if (!(gfElement instanceof GF2nONBElement)) {
            throw new RuntimeException("The elements have different representation: not yet implemented");
        }
        if (!this.mField.equals(((GF2nONBElement)gfElement).mField)) {
            throw new RuntimeException();
        }
        if (this.equals(gfElement)) {
            this.squareThis();
        }
        else {
            final long[] mPol = this.mPol;
            final long[] mPol2 = ((GF2nONBElement)gfElement).mPol;
            final long[] array = new long[this.mLength];
            final int[][] mMult = ((GF2nONBField)this.mField).mMult;
            final int n = this.mLength - 1;
            final int n2 = this.mBit - 1;
            final long n3 = GF2nONBElement.mBitmask[63];
            final long n4 = GF2nONBElement.mBitmask[n2];
            for (int i = 0; i < this.mDegree; ++i) {
                int n5 = 0;
                for (int j = 0; j < this.mDegree; ++j) {
                    final int n6 = GF2nONBElement.mIBY64[j];
                    final int n7 = j & 0x3F;
                    final int n8 = GF2nONBElement.mIBY64[mMult[j][0]];
                    final int n9 = mMult[j][0] & 0x3F;
                    if ((mPol[n6] & GF2nONBElement.mBitmask[n7]) != 0x0L) {
                        if ((mPol2[n8] & GF2nONBElement.mBitmask[n9]) != 0x0L) {
                            n5 ^= 0x1;
                        }
                        if (mMult[j][1] != -1 && (mPol2[GF2nONBElement.mIBY64[mMult[j][1]]] & GF2nONBElement.mBitmask[mMult[j][1] & 0x3F]) != 0x0L) {
                            n5 ^= 0x1;
                        }
                    }
                }
                final int n10 = GF2nONBElement.mIBY64[i];
                final int n11 = i & 0x3F;
                if (n5 != 0) {
                    final long[] array2 = array;
                    final int n12 = n10;
                    array2[n12] ^= GF2nONBElement.mBitmask[n11];
                }
                if (this.mLength > 1) {
                    int n13 = ((mPol[n] & 0x1L) == 0x1L) ? 1 : 0;
                    for (int k = n - 1; k >= 0; --k) {
                        final boolean b = (mPol[k] & 0x1L) != 0x0L;
                        mPol[k] >>>= 1;
                        if (n13 != 0) {
                            final long[] array3 = mPol;
                            final int n14 = k;
                            array3[n14] ^= n3;
                        }
                        n13 = (b ? 1 : 0);
                    }
                    mPol[n] >>>= 1;
                    if (n13 != 0) {
                        final long[] array4 = mPol;
                        final int n15 = n;
                        array4[n15] ^= n4;
                    }
                    int n16 = ((mPol2[n] & 0x1L) == 0x1L) ? 1 : 0;
                    for (int l = n - 1; l >= 0; --l) {
                        final boolean b2 = (mPol2[l] & 0x1L) != 0x0L;
                        mPol2[l] >>>= 1;
                        if (n16 != 0) {
                            final long[] array5 = mPol2;
                            final int n17 = l;
                            array5[n17] ^= n3;
                        }
                        n16 = (b2 ? 1 : 0);
                    }
                    mPol2[n] >>>= 1;
                    if (n16 != 0) {
                        final long[] array6 = mPol2;
                        final int n18 = n;
                        array6[n18] ^= n4;
                    }
                }
                else {
                    final boolean b3 = (mPol[0] & 0x1L) == 0x1L;
                    mPol[0] >>>= 1;
                    if (b3) {
                        final long[] array7 = mPol;
                        final int n19 = 0;
                        array7[n19] ^= n4;
                    }
                    final boolean b4 = (mPol2[0] & 0x1L) == 0x1L;
                    mPol2[0] >>>= 1;
                    if (b4) {
                        final long[] array8 = mPol2;
                        final int n20 = 0;
                        array8[n20] ^= n4;
                    }
                }
            }
            this.assign(array);
        }
    }
    
    @Override
    public GF2nElement square() {
        final GF2nONBElement gf2nONBElement = new GF2nONBElement(this);
        gf2nONBElement.squareThis();
        return gf2nONBElement;
    }
    
    @Override
    public void squareThis() {
        final long[] element = this.getElement();
        final int n = this.mLength - 1;
        final int n2 = this.mBit - 1;
        final long n3 = GF2nONBElement.mBitmask[63];
        int n4 = ((element[n] & GF2nONBElement.mBitmask[n2]) != 0x0L) ? 1 : 0;
        for (int i = 0; i < n; ++i) {
            final boolean b = (element[i] & n3) != 0x0L;
            element[i] <<= 1;
            if (n4 != 0) {
                final long[] array = element;
                final int n5 = i;
                array[n5] ^= 0x1L;
            }
            n4 = (b ? 1 : 0);
        }
        final boolean b2 = (element[n] & GF2nONBElement.mBitmask[n2]) != 0x0L;
        element[n] <<= 1;
        if (n4 != 0) {
            final long[] array2 = element;
            final int n6 = n;
            array2[n6] ^= 0x1L;
        }
        if (b2) {
            final long[] array3 = element;
            final int n7 = n;
            array3[n7] ^= GF2nONBElement.mBitmask[n2 + 1];
        }
        this.assign(element);
    }
    
    public GFElement invert() throws ArithmeticException {
        final GF2nONBElement gf2nONBElement = new GF2nONBElement(this);
        gf2nONBElement.invertThis();
        return gf2nONBElement;
    }
    
    public void invertThis() throws ArithmeticException {
        if (this.isZero()) {
            throw new ArithmeticException();
        }
        int n = 31;
        for (int n2 = 0; n2 == 0 && n >= 0; --n) {
            if (((long)(this.mDegree - 1) & GF2nONBElement.mBitmask[n]) != 0x0L) {
                n2 = 1;
            }
        }
        ++n;
        ZERO((GF2nONBField)this.mField);
        final GF2nONBElement gf2nONBElement = new GF2nONBElement(this);
        int n3 = 1;
        for (int i = n - 1; i >= 0; --i) {
            final GF2nElement gf2nElement = (GF2nElement)gf2nONBElement.clone();
            for (int j = 1; j <= n3; ++j) {
                gf2nElement.squareThis();
            }
            gf2nONBElement.multiplyThisBy(gf2nElement);
            n3 <<= 1;
            if (((long)(this.mDegree - 1) & GF2nONBElement.mBitmask[i]) != 0x0L) {
                gf2nONBElement.squareThis();
                gf2nONBElement.multiplyThisBy(this);
                ++n3;
            }
        }
        gf2nONBElement.squareThis();
    }
    
    @Override
    public GF2nElement squareRoot() {
        final GF2nONBElement gf2nONBElement = new GF2nONBElement(this);
        gf2nONBElement.squareRootThis();
        return gf2nONBElement;
    }
    
    @Override
    public void squareRootThis() {
        final long[] element = this.getElement();
        final int n = this.mLength - 1;
        final int n2 = this.mBit - 1;
        final long n3 = GF2nONBElement.mBitmask[63];
        int n4 = ((element[0] & 0x1L) != 0x0L) ? 1 : 0;
        for (int i = n; i >= 0; --i) {
            final boolean b = (element[i] & 0x1L) != 0x0L;
            element[i] >>>= 1;
            if (n4 != 0) {
                if (i == n) {
                    final long[] array = element;
                    final int n5 = i;
                    array[n5] ^= GF2nONBElement.mBitmask[n2];
                }
                else {
                    final long[] array2 = element;
                    final int n6 = i;
                    array2[n6] ^= n3;
                }
            }
            n4 = (b ? 1 : 0);
        }
        this.assign(element);
    }
    
    @Override
    public int trace() {
        int n = 0;
        final int n2 = this.mLength - 1;
        for (int i = 0; i < n2; ++i) {
            for (int j = 0; j < 64; ++j) {
                if ((this.mPol[i] & GF2nONBElement.mBitmask[j]) != 0x0L) {
                    n ^= 0x1;
                }
            }
        }
        for (int mBit = this.mBit, k = 0; k < mBit; ++k) {
            if ((this.mPol[n2] & GF2nONBElement.mBitmask[k]) != 0x0L) {
                n ^= 0x1;
            }
        }
        return n;
    }
    
    @Override
    public GF2nElement solveQuadraticEquation() throws RuntimeException {
        if (this.trace() == 1) {
            throw new RuntimeException();
        }
        final long n = GF2nONBElement.mBitmask[63];
        final long n2 = 0L;
        final long n3 = 1L;
        final long[] array = new long[this.mLength];
        long n4 = 0L;
        for (int i = 0; i < this.mLength - 1; ++i) {
            for (int j = 1; j < 64; ++j) {
                if (((GF2nONBElement.mBitmask[j] & this.mPol[i]) == n2 || (n4 & GF2nONBElement.mBitmask[j - 1]) == n2) && ((this.mPol[i] & GF2nONBElement.mBitmask[j]) != n2 || (n4 & GF2nONBElement.mBitmask[j - 1]) != n2)) {
                    n4 ^= GF2nONBElement.mBitmask[j];
                }
            }
            array[i] = n4;
            if (((n & n4) != n2 && (n3 & this.mPol[i + 1]) == n3) || ((n & n4) == n2 && (n3 & this.mPol[i + 1]) == n2)) {
                n4 = n2;
            }
            else {
                n4 = n3;
            }
        }
        final int n5 = this.mDegree & 0x3F;
        final long n6 = this.mPol[this.mLength - 1];
        for (int k = 1; k < n5; ++k) {
            if (((GF2nONBElement.mBitmask[k] & n6) == n2 || (GF2nONBElement.mBitmask[k - 1] & n4) == n2) && ((GF2nONBElement.mBitmask[k] & n6) != n2 || (GF2nONBElement.mBitmask[k - 1] & n4) != n2)) {
                n4 ^= GF2nONBElement.mBitmask[k];
            }
        }
        array[this.mLength - 1] = n4;
        return new GF2nONBElement((GF2nONBField)this.mField, array);
    }
    
    @Override
    public String toString() {
        return this.toString(16);
    }
    
    public String toString(final int n) {
        String s = "";
        final long[] element = this.getElement();
        final int mBit = this.mBit;
        if (n == 2) {
            for (int i = mBit - 1; i >= 0; --i) {
                if ((element[element.length - 1] & 1L << i) == 0x0L) {
                    s += "0";
                }
                else {
                    s += "1";
                }
            }
            for (int j = element.length - 2; j >= 0; --j) {
                for (int k = 63; k >= 0; --k) {
                    if ((element[j] & GF2nONBElement.mBitmask[k]) == 0x0L) {
                        s += "0";
                    }
                    else {
                        s += "1";
                    }
                }
            }
        }
        else if (n == 16) {
            final char[] array = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
            for (int l = element.length - 1; l >= 0; --l) {
                s = s + array[(int)(element[l] >>> 60) & 0xF] + array[(int)(element[l] >>> 56) & 0xF] + array[(int)(element[l] >>> 52) & 0xF] + array[(int)(element[l] >>> 48) & 0xF] + array[(int)(element[l] >>> 44) & 0xF] + array[(int)(element[l] >>> 40) & 0xF] + array[(int)(element[l] >>> 36) & 0xF] + array[(int)(element[l] >>> 32) & 0xF] + array[(int)(element[l] >>> 28) & 0xF] + array[(int)(element[l] >>> 24) & 0xF] + array[(int)(element[l] >>> 20) & 0xF] + array[(int)(element[l] >>> 16) & 0xF] + array[(int)(element[l] >>> 12) & 0xF] + array[(int)(element[l] >>> 8) & 0xF] + array[(int)(element[l] >>> 4) & 0xF] + array[(int)element[l] & 0xF] + " ";
            }
        }
        return s;
    }
    
    public BigInteger toFlexiBigInt() {
        return new BigInteger(1, this.toByteArray());
    }
    
    public byte[] toByteArray() {
        final int n = (this.mDegree - 1 >> 3) + 1;
        final byte[] array = new byte[n];
        for (int i = 0; i < n; ++i) {
            array[n - i - 1] = (byte)((this.mPol[i >>> 3] & 255L << ((i & 0x7) << 3)) >>> ((i & 0x7) << 3));
        }
        return array;
    }
    
    static {
        mBitmask = new long[] { 1L, 2L, 4L, 8L, 16L, 32L, 64L, 128L, 256L, 512L, 1024L, 2048L, 4096L, 8192L, 16384L, 32768L, 65536L, 131072L, 262144L, 524288L, 1048576L, 2097152L, 4194304L, 8388608L, 16777216L, 33554432L, 67108864L, 134217728L, 268435456L, 536870912L, 1073741824L, 2147483648L, 4294967296L, 8589934592L, 17179869184L, 34359738368L, 68719476736L, 137438953472L, 274877906944L, 549755813888L, 1099511627776L, 2199023255552L, 4398046511104L, 8796093022208L, 17592186044416L, 35184372088832L, 70368744177664L, 140737488355328L, 281474976710656L, 562949953421312L, 1125899906842624L, 2251799813685248L, 4503599627370496L, 9007199254740992L, 18014398509481984L, 36028797018963968L, 72057594037927936L, 144115188075855872L, 288230376151711744L, 576460752303423488L, 1152921504606846976L, 2305843009213693952L, 4611686018427387904L, Long.MIN_VALUE };
        mMaxmask = new long[] { 1L, 3L, 7L, 15L, 31L, 63L, 127L, 255L, 511L, 1023L, 2047L, 4095L, 8191L, 16383L, 32767L, 65535L, 131071L, 262143L, 524287L, 1048575L, 2097151L, 4194303L, 8388607L, 16777215L, 33554431L, 67108863L, 134217727L, 268435455L, 536870911L, 1073741823L, 2147483647L, 4294967295L, 8589934591L, 17179869183L, 34359738367L, 68719476735L, 137438953471L, 274877906943L, 549755813887L, 1099511627775L, 2199023255551L, 4398046511103L, 8796093022207L, 17592186044415L, 35184372088831L, 70368744177663L, 140737488355327L, 281474976710655L, 562949953421311L, 1125899906842623L, 2251799813685247L, 4503599627370495L, 9007199254740991L, 18014398509481983L, 36028797018963967L, 72057594037927935L, 144115188075855871L, 288230376151711743L, 576460752303423487L, 1152921504606846975L, 2305843009213693951L, 4611686018427387903L, Long.MAX_VALUE, -1L };
        mIBY64 = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5 };
    }
}
