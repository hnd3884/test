package com.sun.java.util.jar.pack;

import java.io.OutputStream;
import java.util.HashMap;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

class Coding implements Comparable<Coding>, CodingMethod, Histogram.BitMetric
{
    public static final int B_MAX = 5;
    public static final int H_MAX = 256;
    public static final int S_MAX = 2;
    private final int B;
    private final int H;
    private final int L;
    private final int S;
    private final int del;
    private final int min;
    private final int max;
    private final int umin;
    private final int umax;
    private final int[] byteMin;
    private final int[] byteMax;
    private static Map<Coding, Coding> codeMap;
    private static final byte[] byteBitWidths;
    static boolean verboseStringForDebug;
    
    private static int saturate32(final long n) {
        if (n > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        if (n < -2147483648L) {
            return Integer.MIN_VALUE;
        }
        return (int)n;
    }
    
    private static long codeRangeLong(final int n, final int n2) {
        return codeRangeLong(n, n2, n);
    }
    
    private static long codeRangeLong(final int n, final int n2, final int n3) {
        assert n3 >= 0 && n3 <= n;
        assert n >= 1 && n <= 5;
        assert n2 >= 1 && n2 <= 256;
        if (n3 == 0) {
            return 0L;
        }
        if (n == 1) {
            return n2;
        }
        final int n4 = 256 - n2;
        long n5 = 0L;
        long n6 = 1L;
        for (int i = 1; i <= n3; ++i) {
            n5 += n6;
            n6 *= n2;
        }
        long n7 = n5 * n4;
        if (n3 == n) {
            n7 += n6;
        }
        return n7;
    }
    
    public static int codeMax(final int n, final int n2, final int n3, final int n4) {
        final long codeRangeLong = codeRangeLong(n, n2, n4);
        if (codeRangeLong == 0L) {
            return -1;
        }
        if (n3 == 0 || codeRangeLong >= 4294967296L) {
            return saturate32(codeRangeLong - 1L);
        }
        long n5;
        for (n5 = codeRangeLong - 1L; isNegativeCode(n5, n3); --n5) {}
        if (n5 < 0L) {
            return -1;
        }
        final int decodeSign32 = decodeSign32(n5, n3);
        if (decodeSign32 < 0) {
            return Integer.MAX_VALUE;
        }
        return decodeSign32;
    }
    
    public static int codeMin(final int n, final int n2, final int n3, final int n4) {
        final long codeRangeLong = codeRangeLong(n, n2, n4);
        if (codeRangeLong >= 4294967296L && n4 == n) {
            return Integer.MIN_VALUE;
        }
        if (n3 == 0) {
            return 0;
        }
        long n5;
        for (n5 = codeRangeLong - 1L; !isNegativeCode(n5, n3); --n5) {}
        if (n5 < 0L) {
            return 0;
        }
        return decodeSign32(n5, n3);
    }
    
    private static long toUnsigned32(final int n) {
        return (long)n << 32 >>> 32;
    }
    
    private static boolean isNegativeCode(final long n, final int n2) {
        assert n2 > 0;
        assert n >= -1L;
        return ((int)n + 1 & (1 << n2) - 1) == 0x0;
    }
    
    private static boolean hasNegativeCode(final int n, final int n2) {
        assert n2 > 0;
        return 0 > n && n >= ~(-1 >>> n2);
    }
    
    private static int decodeSign32(final long n, final int n2) {
        assert n == toUnsigned32((int)n) : Long.toHexString(n);
        if (n2 == 0) {
            return (int)n;
        }
        int n3;
        if (isNegativeCode(n, n2)) {
            n3 = ~((int)n >>> n2);
        }
        else {
            n3 = (int)n - ((int)n >>> n2);
        }
        assert n3 == ((int)n >>> 1 ^ -((int)n & 0x1));
        return n3;
    }
    
    private static long encodeSign32(final int n, final int n2) {
        if (n2 == 0) {
            return toUnsigned32(n);
        }
        final int n3 = (1 << n2) - 1;
        long n4;
        if (!hasNegativeCode(n, n2)) {
            n4 = n + toUnsigned32(n) / n3;
        }
        else {
            n4 = (-n << n2) - 1;
        }
        final long unsigned32 = toUnsigned32((int)n4);
        assert n == decodeSign32(unsigned32, n2) : Long.toHexString(unsigned32) + " -> " + Integer.toHexString(n) + " != " + Integer.toHexString(decodeSign32(unsigned32, n2));
        return unsigned32;
    }
    
    public static void writeInt(final byte[] array, final int[] array2, final int n, final int n2, final int n3, final int n4) {
        final long encodeSign32 = encodeSign32(n, n4);
        assert encodeSign32 == toUnsigned32((int)encodeSign32);
        assert encodeSign32 < codeRangeLong(n2, n3) : Long.toHexString(encodeSign32);
        final int n5 = 256 - n3;
        long n6 = encodeSign32;
        int n7 = array2[0];
        long n9;
        int n10;
        for (int n8 = 0; n8 < n2 - 1 && n6 >= n5; n6 = n9 / n3, array[n7++] = (byte)n10, ++n8) {
            n9 = n6 - n5;
            n10 = (int)(n5 + n9 % n3);
        }
        array[n7++] = (byte)n6;
        array2[0] = n7;
    }
    
    public static int readInt(final byte[] array, final int[] array2, final int n, final int n2, final int n3) {
        final int n4 = 256 - n2;
        long n5 = 0L;
        long n6 = 1L;
        int n7 = array2[0];
        for (int i = 0; i < n; ++i) {
            final int n8 = array[n7++] & 0xFF;
            n5 += n8 * n6;
            n6 *= n2;
            if (n8 < n4) {
                break;
            }
        }
        array2[0] = n7;
        return decodeSign32(n5, n3);
    }
    
    public static int readIntFrom(final InputStream inputStream, final int n, final int n2, final int n3) throws IOException {
        final int n4 = 256 - n2;
        long n5 = 0L;
        long n6 = 1L;
        for (int i = 0; i < n; ++i) {
            final int read = inputStream.read();
            if (read < 0) {
                throw new RuntimeException("unexpected EOF");
            }
            n5 += read * n6;
            n6 *= n2;
            if (read < n4) {
                break;
            }
        }
        assert n5 >= 0L && n5 < codeRangeLong(n, n2);
        return decodeSign32(n5, n3);
    }
    
    private Coding(final int n, final int n2, final int n3) {
        this(n, n2, n3, 0);
    }
    
    private Coding(final int b, final int h, final int s, final int del) {
        this.B = b;
        this.H = h;
        this.L = 256 - h;
        this.S = s;
        this.del = del;
        this.min = codeMin(b, h, s, b);
        this.max = codeMax(b, h, s, b);
        this.umin = codeMin(b, h, 0, b);
        this.umax = codeMax(b, h, 0, b);
        this.byteMin = new int[b];
        this.byteMax = new int[b];
        for (int i = 1; i <= b; ++i) {
            this.byteMin[i - 1] = codeMin(b, h, s, i);
            this.byteMax[i - 1] = codeMax(b, h, s, i);
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Coding)) {
            return false;
        }
        final Coding coding = (Coding)o;
        return this.B == coding.B && this.H == coding.H && this.S == coding.S && this.del == coding.del;
    }
    
    @Override
    public int hashCode() {
        return (this.del << 14) + (this.S << 11) + (this.B << 8) + (this.H << 0);
    }
    
    private static synchronized Coding of(final int n, final int n2, final int n3, final int n4) {
        if (Coding.codeMap == null) {
            Coding.codeMap = new HashMap<Coding, Coding>();
        }
        final Coding coding = new Coding(n, n2, n3, n4);
        Coding coding2 = Coding.codeMap.get(coding);
        if (coding2 == null) {
            Coding.codeMap.put(coding, coding2 = coding);
        }
        return coding2;
    }
    
    public static Coding of(final int n, final int n2) {
        return of(n, n2, 0, 0);
    }
    
    public static Coding of(final int n, final int n2, final int n3) {
        return of(n, n2, n3, 0);
    }
    
    public boolean canRepresentValue(final int n) {
        if (this.isSubrange()) {
            return this.canRepresentUnsigned(n);
        }
        return this.canRepresentSigned(n);
    }
    
    public boolean canRepresentSigned(final int n) {
        return n >= this.min && n <= this.max;
    }
    
    public boolean canRepresentUnsigned(final int n) {
        return n >= this.umin && n <= this.umax;
    }
    
    public int readFrom(final byte[] array, final int[] array2) {
        return readInt(array, array2, this.B, this.H, this.S);
    }
    
    public void writeTo(final byte[] array, final int[] array2, final int n) {
        writeInt(array, array2, n, this.B, this.H, this.S);
    }
    
    public int readFrom(final InputStream inputStream) throws IOException {
        return readIntFrom(inputStream, this.B, this.H, this.S);
    }
    
    public void writeTo(final OutputStream outputStream, final int n) throws IOException {
        final byte[] array = new byte[this.B];
        final int[] array2 = { 0 };
        writeInt(array, array2, n, this.B, this.H, this.S);
        outputStream.write(array, 0, array2[0]);
    }
    
    @Override
    public void readArrayFrom(final InputStream inputStream, final int[] array, final int n, final int n2) throws IOException {
        for (int i = n; i < n2; ++i) {
            array[i] = this.readFrom(inputStream);
        }
        for (int j = 0; j < this.del; ++j) {
            long n3 = 0L;
            for (int k = n; k < n2; ++k) {
                n3 += array[k];
                if (this.isSubrange()) {
                    n3 = this.reduceToUnsignedRange(n3);
                }
                array[k] = (int)n3;
            }
        }
    }
    
    @Override
    public void writeArrayTo(final OutputStream outputStream, int[] array, int n, int length) throws IOException {
        if (length <= n) {
            return;
        }
        for (int i = 0; i < this.del; ++i) {
            int[] array2;
            if (!this.isSubrange()) {
                array2 = makeDeltas(array, n, length, 0, 0);
            }
            else {
                array2 = makeDeltas(array, n, length, this.min, this.max);
            }
            array = array2;
            n = 0;
            length = array2.length;
        }
        final byte[] array3 = new byte[256];
        final int n2 = array3.length - this.B;
        final int[] array4 = { 0 };
        int j = n;
        while (j < length) {
            while (array4[0] <= n2) {
                this.writeTo(array3, array4, array[j++]);
                if (j >= length) {
                    break;
                }
            }
            outputStream.write(array3, 0, array4[0]);
            array4[0] = 0;
        }
    }
    
    boolean isSubrange() {
        return this.max < Integer.MAX_VALUE && this.max - (long)this.min + 1L <= 2147483647L;
    }
    
    boolean isFullRange() {
        return this.max == Integer.MAX_VALUE && this.min == Integer.MIN_VALUE;
    }
    
    int getRange() {
        assert this.isSubrange();
        return this.max - this.min + 1;
    }
    
    Coding setB(final int n) {
        return of(n, this.H, this.S, this.del);
    }
    
    Coding setH(final int n) {
        return of(this.B, n, this.S, this.del);
    }
    
    Coding setS(final int n) {
        return of(this.B, this.H, n, this.del);
    }
    
    Coding setL(final int n) {
        return this.setH(256 - n);
    }
    
    Coding setD(final int n) {
        return of(this.B, this.H, this.S, n);
    }
    
    Coding getDeltaCoding() {
        return this.setD(this.del + 1);
    }
    
    Coding getValueCoding() {
        if (this.isDelta()) {
            return of(this.B, this.H, 0, this.del - 1);
        }
        return this;
    }
    
    int reduceToUnsignedRange(long n) {
        if (n == (int)n && this.canRepresentUnsigned((int)n)) {
            return (int)n;
        }
        final int range = this.getRange();
        assert range > 0;
        n %= range;
        if (n < 0L) {
            n += range;
        }
        assert this.canRepresentUnsigned((int)n);
        return (int)n;
    }
    
    int reduceToSignedRange(final int n) {
        if (this.canRepresentSigned(n)) {
            return n;
        }
        return reduceToSignedRange(n, this.min, this.max);
    }
    
    static int reduceToSignedRange(int n, final int n2, final int n3) {
        final int n4 = n3 - n2 + 1;
        assert n4 > 0;
        final int n5 = n;
        n -= n2;
        if (n < 0 && n5 >= 0) {
            n -= n4;
            assert n >= 0;
        }
        n %= n4;
        if (n < 0) {
            n += n4;
        }
        n += n2;
        assert n2 <= n && n <= n3;
        return n;
    }
    
    boolean isSigned() {
        return this.min < 0;
    }
    
    boolean isDelta() {
        return this.del != 0;
    }
    
    public int B() {
        return this.B;
    }
    
    public int H() {
        return this.H;
    }
    
    public int L() {
        return this.L;
    }
    
    public int S() {
        return this.S;
    }
    
    public int del() {
        return this.del;
    }
    
    public int min() {
        return this.min;
    }
    
    public int max() {
        return this.max;
    }
    
    public int umin() {
        return this.umin;
    }
    
    public int umax() {
        return this.umax;
    }
    
    public int byteMin(final int n) {
        return this.byteMin[n - 1];
    }
    
    public int byteMax(final int n) {
        return this.byteMax[n - 1];
    }
    
    @Override
    public int compareTo(final Coding coding) {
        int n = this.del - coding.del;
        if (n == 0) {
            n = this.B - coding.B;
        }
        if (n == 0) {
            n = this.H - coding.H;
        }
        if (n == 0) {
            n = this.S - coding.S;
        }
        return n;
    }
    
    public int distanceFrom(final Coding coding) {
        int n = this.del - coding.del;
        if (n < 0) {
            n = -n;
        }
        int n2 = this.S - coding.S;
        if (n2 < 0) {
            n2 = -n2;
        }
        int n3 = this.B - coding.B;
        if (n3 < 0) {
            n3 = -n3;
        }
        int n4;
        if (this.H == coding.H) {
            n4 = 0;
        }
        else {
            final int hl = this.getHL();
            final int hl2 = coding.getHL();
            final int n5 = hl * hl;
            final int n6 = hl2 * hl2;
            if (n5 > n6) {
                n4 = ceil_lg2(1 + (n5 - 1) / n6);
            }
            else {
                n4 = ceil_lg2(1 + (n6 - 1) / n5);
            }
        }
        final int n7 = 5 * (n + n2 + n3) + n4;
        assert this.compareTo(coding) == 0;
        return n7;
    }
    
    private int getHL() {
        if (this.H <= 128) {
            return this.H;
        }
        if (this.L >= 1) {
            return 16384 / this.L;
        }
        return 32768;
    }
    
    static int ceil_lg2(int i) {
        assert i - 1 >= 0;
        --i;
        int n = 0;
        while (i != 0) {
            ++n;
            i >>= 1;
        }
        return n;
    }
    
    static int bitWidth(int n) {
        if (n < 0) {
            n ^= -1;
        }
        byte b = 0;
        int n2 = n;
        if (n2 < Coding.byteBitWidths.length) {
            return Coding.byteBitWidths[n2];
        }
        final int n3 = n2 >>> 16;
        if (n3 != 0) {
            n2 = n3;
            b += 16;
        }
        final int n4 = n2 >>> 8;
        if (n4 != 0) {
            n2 = n4;
            b += 8;
        }
        return b + Coding.byteBitWidths[n2];
    }
    
    static int[] makeDeltas(final int[] array, final int n, final int n2, final int n3, final int n4) {
        assert n4 >= n3;
        final int n5 = n2 - n;
        final int[] array2 = new int[n5];
        int n6 = 0;
        if (n3 == n4) {
            for (int i = 0; i < n5; ++i) {
                final int n7 = array[n + i];
                array2[i] = n7 - n6;
                n6 = n7;
            }
        }
        else {
            for (int j = 0; j < n5; ++j) {
                final int n8 = array[n + j];
                assert n8 >= 0 && n8 + n3 <= n4;
                final int n9 = n8 - n6;
                assert n9 == n8 - (long)n6;
                n6 = n8;
                array2[j] = reduceToSignedRange(n9, n3, n4);
            }
        }
        return array2;
    }
    
    boolean canRepresent(final int n, final int n2) {
        assert n <= n2;
        if (this.del <= 0) {
            return this.canRepresentSigned(n2) && this.canRepresentSigned(n);
        }
        if (this.isSubrange()) {
            return this.canRepresentUnsigned(n2) && this.canRepresentUnsigned(n);
        }
        return this.isFullRange();
    }
    
    boolean canRepresent(final int[] array, final int n, final int n2) {
        final int n3 = n2 - n;
        if (n3 == 0) {
            return true;
        }
        if (this.isFullRange()) {
            return true;
        }
        int n5;
        int n4 = n5 = array[n];
        for (int i = 1; i < n3; ++i) {
            final int n6 = array[n + i];
            if (n4 < n6) {
                n4 = n6;
            }
            if (n5 > n6) {
                n5 = n6;
            }
        }
        return this.canRepresent(n5, n4);
    }
    
    @Override
    public double getBitLength(final int n) {
        return this.getLength(n) * 8.0;
    }
    
    public int getLength(int reduceToSignedRange) {
        if (this.isDelta() && this.isSubrange()) {
            if (!this.canRepresentUnsigned(reduceToSignedRange)) {
                return Integer.MAX_VALUE;
            }
            reduceToSignedRange = this.reduceToSignedRange(reduceToSignedRange);
        }
        if (reduceToSignedRange >= 0) {
            for (int i = 0; i < this.B; ++i) {
                if (reduceToSignedRange <= this.byteMax[i]) {
                    return i + 1;
                }
            }
        }
        else {
            for (int j = 0; j < this.B; ++j) {
                if (reduceToSignedRange >= this.byteMin[j]) {
                    return j + 1;
                }
            }
        }
        return Integer.MAX_VALUE;
    }
    
    public int getLength(int[] array, int n, final int n2) {
        final int n3 = n2 - n;
        if (this.B == 1) {
            return n3;
        }
        if (this.L == 0) {
            return n3 * this.B;
        }
        if (this.isDelta()) {
            int[] array2;
            if (!this.isSubrange()) {
                array2 = makeDeltas(array, n, n2, 0, 0);
            }
            else {
                array2 = makeDeltas(array, n, n2, this.min, this.max);
            }
            array = array2;
            n = 0;
        }
        int n4 = n3;
        for (int i = 1; i <= this.B; ++i) {
            final int n5 = this.byteMax[i - 1];
            final int n6 = this.byteMin[i - 1];
            int n7 = 0;
            for (int j = 0; j < n3; ++j) {
                final int n8 = array[n + j];
                if (n8 >= 0) {
                    if (n8 > n5) {
                        ++n7;
                    }
                }
                else if (n8 < n6) {
                    ++n7;
                }
            }
            if (n7 == 0) {
                break;
            }
            if (i == this.B) {
                return Integer.MAX_VALUE;
            }
            n4 += n7;
        }
        return n4;
    }
    
    @Override
    public byte[] getMetaCoding(final Coding coding) {
        if (coding == this) {
            return new byte[] { 0 };
        }
        final int index = BandStructure.indexOf(this);
        if (index > 0) {
            return new byte[] { (byte)index };
        }
        return new byte[] { 116, (byte)(this.del + 2 * this.S + 8 * (this.B - 1)), (byte)(this.H - 1) };
    }
    
    public static int parseMetaCoding(final byte[] array, int n, final Coding coding, final CodingMethod[] array2) {
        final int n2 = array[n++] & 0xFF;
        if (1 <= n2 && n2 <= 115) {
            final Coding codingForIndex = BandStructure.codingForIndex(n2);
            assert codingForIndex != null;
            array2[0] = codingForIndex;
            return n;
        }
        else {
            if (n2 != 116) {
                return n - 1;
            }
            final int n3 = array[n++] & 0xFF;
            final int n4 = array[n++] & 0xFF;
            final int n5 = n3 % 2;
            final int n6 = n3 / 2 % 4;
            final int n7 = n3 / 8 + 1;
            final int n8 = n4 + 1;
            if (1 > n7 || n7 > 5 || 0 > n6 || n6 > 2 || 1 > n8 || n8 > 256 || 0 > n5 || n5 > 1 || (n7 == 1 && n8 != 256) || (n7 == 5 && n8 == 256)) {
                throw new RuntimeException("Bad arb. coding: (" + n7 + "," + n8 + "," + n6 + "," + n5);
            }
            array2[0] = of(n7, n8, n6, n5);
            return n;
        }
    }
    
    public String keyString() {
        return "(" + this.B + "," + this.H + "," + this.S + "," + this.del + ")";
    }
    
    @Override
    public String toString() {
        return "Coding" + this.keyString();
    }
    
    String stringForDebug() {
        String s = this.keyString() + " L=" + this.L + " r=[" + ((this.min == Integer.MIN_VALUE) ? "min" : ("" + this.min)) + "," + ((this.max == Integer.MAX_VALUE) ? "max" : ("" + this.max)) + "]";
        if (this.isSubrange()) {
            s += " subrange";
        }
        else if (!this.isFullRange()) {
            s += " MIDRANGE";
        }
        if (Coding.verboseStringForDebug) {
            String s2 = s + " {";
            int n = 0;
            for (int i = 1; i <= this.B; ++i) {
                final int saturate32 = saturate32(this.byteMax[i - 1] - (long)this.byteMin[i - 1] + 1L);
                assert saturate32 == saturate32(codeRangeLong(this.B, this.H, i));
                final int n2 = saturate32 - n;
                s2 = s2 + " #" + i + "=" + (((n = n2) == Integer.MAX_VALUE) ? "max" : ("" + n2));
            }
            s = s2 + " }";
        }
        return s;
    }
    
    static {
        byteBitWidths = new byte[256];
        for (int i = 0; i < Coding.byteBitWidths.length; ++i) {
            Coding.byteBitWidths[i] = (byte)ceil_lg2(i + 1);
        }
        for (int j = 10; j >= 0; j = (j << 1) - (j >> 3)) {
            assert bitWidth(j) == ceil_lg2(j + 1);
        }
        Coding.verboseStringForDebug = false;
    }
}
