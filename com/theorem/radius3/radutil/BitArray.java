package com.theorem.radius3.radutil;

public class BitArray
{
    public static final int INT_SIZE = 32;
    public static final int BYTE_SIZE = 8;
    private int[] a;
    private int b;
    private static final char[] c;
    private static final char[] d;
    
    public BitArray(final int b) {
        this.b = 0;
        this.b = b;
        this.a = new int[(this.b + 32 - 1) / 32];
    }
    
    public BitArray(final byte[] array) {
        this.b = 0;
        this.a(array, array.length);
    }
    
    public BitArray(final byte[] array, final int n) {
        this.b = 0;
        this.a(array, n);
    }
    
    private final void a(final byte[] array, final int n) {
        this.b = 8 * n;
        this.a = new int[(this.b + 32 - 1) / 32];
        final int length = this.a.length;
        final byte[] array2 = new byte[this.a.length * 4];
        System.arraycopy(array, 0, array2, 0, array.length);
        int n2 = 0;
        for (int i = 0; i < length; ++i) {
            this.a[i] = (array2[n2++] << 24 | (array2[n2++] & 0xFF) << 16 | (array2[n2++] & 0xFF) << 8 | (array2[n2++] & 0xFF));
        }
    }
    
    public final void set(final int n) {
        if (n >= this.b) {
            return;
        }
        final int n2 = 32 - n % 32 - 1;
        final int[] a = this.a;
        final int n3 = n / 32;
        a[n3] |= 1 << n2;
    }
    
    public final void set(final int n, final boolean b) {
        if (b) {
            this.set(n);
        }
        else {
            this.clear(n);
        }
    }
    
    public final boolean get(final int n) {
        return n < this.b && (this.a[n / 32] & 1 << 32 - n % 32 - 1) != 0x0;
    }
    
    public final int cardinality() {
        int n = 0;
        for (int i = 0; i < this.b; ++i) {
            if (this.get(i)) {
                ++n;
            }
        }
        return n;
    }
    
    public final boolean xor() {
        if (this.b > 1) {
            boolean value = this.get(0);
            for (int i = 1; i < this.b; ++i) {
                value ^= this.get(i);
            }
            return value;
        }
        return false;
    }
    
    public final int extractInt(final int n, int n2) {
        int b = n + n2;
        if (b > this.b) {
            b = this.b;
        }
        if (n2 > 32) {
            n2 = 32;
        }
        final BitArray bitArray = new BitArray(32);
        int n3 = 32 - n2;
        for (int i = n; i < b; ++i) {
            bitArray.set(n3++, this.get(i));
        }
        return concoctInt(bitArray.toByteArray(), 0);
    }
    
    public final long extractLong(final int n, int n2) {
        int b = n + n2;
        if (b > this.b) {
            b = this.b;
        }
        if (n2 > 64) {
            n2 = 64;
        }
        final BitArray bitArray = new BitArray(64);
        int n3 = 64 - n2;
        for (int i = n; i < b; ++i) {
            bitArray.set(n3++, this.get(i));
        }
        return b(bitArray.toByteArray());
    }
    
    public final byte[] extractBytes(final int n, int n2) {
        int b = n + n2;
        if (b > this.b) {
            b = this.b;
        }
        if (n2 > b - n) {
            n2 = b - n;
        }
        final BitArray bitArray = new BitArray(n2);
        int n3 = this.b - n2;
        for (int i = n; i < b; ++i) {
            bitArray.set(n3++, this.get(i));
        }
        return bitArray.toByteArray();
    }
    
    public final void clear(final int n) {
        if (n >= this.b) {
            return;
        }
        final int n2 = 32 - n % 32 - 1;
        final int[] a = this.a;
        final int n3 = n / 32;
        a[n3] &= ~(1 << n2);
    }
    
    public final void clear(final int n, int n2) {
        if (n > this.b) {
            return;
        }
        if (n > n2) {
            return;
        }
        if (n2 > this.b) {
            n2 = this.b + 1;
        }
        for (int i = n; i < n2; ++i) {
            this.clear(i);
        }
    }
    
    public final boolean isEmpty() {
        return this.cardinality() == 0;
    }
    
    public final void clear() {
        for (int length = this.a.length, i = 0; i < length; ++i) {
            this.a[i] = 0;
        }
    }
    
    public final void reset() {
        for (int i = 0; i < this.a.length; ++i) {
            this.a[0] = 0;
        }
    }
    
    public final byte[] toByteArray() {
        final byte[] array = new byte[this.a.length * 4];
        int n = 0;
        for (int i = 0; i < this.a.length; ++i) {
            final int n2 = this.a[i];
            array[n++] = (byte)(n2 >>> 24);
            array[n++] = (byte)(n2 >>> 16);
            array[n++] = (byte)(n2 >>> 8);
            array[n++] = (byte)n2;
        }
        final byte[] array2 = new byte[this.b / 8];
        System.arraycopy(array, 0, array2, 0, array2.length);
        return array2;
    }
    
    public final String toHexString() {
        return this.a(this.toByteArray());
    }
    
    private final String a(final byte[] array) {
        final StringBuffer sb = new StringBuffer();
        for (int length = array.length, i = 0; i < length; ++i) {
            sb.append(BitArray.c[array[i] >>> 4 & 0xF]).append(BitArray.c[array[i] & 0xF]);
        }
        return sb.toString();
    }
    
    public final String toString() {
        final StringBuffer sb = new StringBuffer("BitArray: ");
        final byte[] byteArray = this.toByteArray();
        for (int i = 0; i < byteArray.length; ++i) {
            sb.append(BitArray.d[byteArray[i] >>> 7 & 0x1]);
            sb.append(BitArray.d[byteArray[i] >>> 6 & 0x1]);
            sb.append(BitArray.d[byteArray[i] >>> 5 & 0x1]);
            sb.append(BitArray.d[byteArray[i] >>> 4 & 0x1]);
            sb.append(BitArray.d[byteArray[i] >>> 3 & 0x1]);
            sb.append(BitArray.d[byteArray[i] >>> 2 & 0x1]);
            sb.append(BitArray.d[byteArray[i] >>> 1 & 0x1]);
            sb.append(BitArray.c[byteArray[i] & 0x1]);
        }
        return sb.toString();
    }
    
    public final int getMaximumBits() {
        return this.b;
    }
    
    public static void main(final String[] array) {
        final BitArray bitArray = new BitArray(new byte[5]);
        for (int i = 0; i <= 41; ++i) {
            a(bitArray, i);
        }
        final BitArray bitArray2 = new BitArray(new byte[2], 4);
        for (int j = 0; j <= 41; ++j) {
            a(bitArray2, j);
        }
    }
    
    private static void a(final BitArray bitArray, final int n) {
        bitArray.set(n);
        System.out.println(n + " " + bitArray);
        System.out.println(n + " " + bitArray.get(n));
        bitArray.clear(n);
        System.out.println(n + " " + bitArray);
        System.out.println(n + " " + bitArray.get(0));
        System.out.println();
    }
    
    public static int concoctInt(final byte[] array, final int n) {
        return array[n] << 24 | (array[n + 1] & 0xFF) << 16 | (array[n + 2] & 0xFF) << 8 | (array[n + 3] & 0xFF);
    }
    
    private static long b(final byte[] array) {
        return ((long)concoctInt(array, 0) & 0xFFFFFFFFL) << 32 | ((long)concoctInt(array, 4) & 0xFFFFFFFFL);
    }
    
    public final int nextClearBit(final int n) {
        int n2;
        for (n2 = n; n2 < this.b && this.get(n2); ++n2) {}
        return n2;
    }
    
    public final int nextSetBit(final int n) {
        int n2;
        for (n2 = n; n2 < this.b && !this.get(n2); ++n2) {}
        return n2;
    }
    
    public final int length() {
        return this.b;
    }
    
    static {
        c = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        d = new char[] { '0', '1' };
    }
}
