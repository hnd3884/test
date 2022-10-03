package jdk.jfr.internal;

import java.nio.ByteOrder;
import sun.misc.Unsafe;

final class Bits
{
    private static final Unsafe unsafe;
    private static final boolean unalignedAccess = false;
    private static final boolean bigEndian;
    
    private Bits() {
    }
    
    private static short swap(final short n) {
        return Short.reverseBytes(n);
    }
    
    private static char swap(final char c) {
        return Character.reverseBytes(c);
    }
    
    private static int swap(final int n) {
        return Integer.reverseBytes(n);
    }
    
    private static long swap(final long n) {
        return Long.reverseBytes(n);
    }
    
    private static float swap(final float n) {
        return Float.intBitsToFloat(swap(Float.floatToIntBits(n)));
    }
    
    private static double swap(final double n) {
        return Double.longBitsToDouble(swap(Double.doubleToLongBits(n)));
    }
    
    private static boolean isAddressAligned(final long n, final int n2) {
        return (n & (long)(n2 - 1)) == 0x0L;
    }
    
    private static byte char1(final char c) {
        return (byte)(c >> 8);
    }
    
    private static byte char0(final char c) {
        return (byte)c;
    }
    
    private static byte short1(final short n) {
        return (byte)(n >> 8);
    }
    
    private static byte short0(final short n) {
        return (byte)n;
    }
    
    private static byte int3(final int n) {
        return (byte)(n >> 24);
    }
    
    private static byte int2(final int n) {
        return (byte)(n >> 16);
    }
    
    private static byte int1(final int n) {
        return (byte)(n >> 8);
    }
    
    private static byte int0(final int n) {
        return (byte)n;
    }
    
    private static byte long7(final long n) {
        return (byte)(n >> 56);
    }
    
    private static byte long6(final long n) {
        return (byte)(n >> 48);
    }
    
    private static byte long5(final long n) {
        return (byte)(n >> 40);
    }
    
    private static byte long4(final long n) {
        return (byte)(n >> 32);
    }
    
    private static byte long3(final long n) {
        return (byte)(n >> 24);
    }
    
    private static byte long2(final long n) {
        return (byte)(n >> 16);
    }
    
    private static byte long1(final long n) {
        return (byte)(n >> 8);
    }
    
    private static byte long0(final long n) {
        return (byte)n;
    }
    
    private static void putCharBigEndianUnaligned(final long n, final char c) {
        putByte_(n, char1(c));
        putByte_(n + 1L, char0(c));
    }
    
    private static void putShortBigEndianUnaligned(final long n, final short n2) {
        putByte_(n, short1(n2));
        putByte_(n + 1L, short0(n2));
    }
    
    private static void putIntBigEndianUnaligned(final long n, final int n2) {
        putByte_(n, int3(n2));
        putByte_(n + 1L, int2(n2));
        putByte_(n + 2L, int1(n2));
        putByte_(n + 3L, int0(n2));
    }
    
    private static void putLongBigEndianUnaligned(final long n, final long n2) {
        putByte_(n, long7(n2));
        putByte_(n + 1L, long6(n2));
        putByte_(n + 2L, long5(n2));
        putByte_(n + 3L, long4(n2));
        putByte_(n + 4L, long3(n2));
        putByte_(n + 5L, long2(n2));
        putByte_(n + 6L, long1(n2));
        putByte_(n + 7L, long0(n2));
    }
    
    private static void putFloatBigEndianUnaligned(final long n, final float n2) {
        putIntBigEndianUnaligned(n, Float.floatToRawIntBits(n2));
    }
    
    private static void putDoubleBigEndianUnaligned(final long n, final double n2) {
        putLongBigEndianUnaligned(n, Double.doubleToRawLongBits(n2));
    }
    
    private static void putByte_(final long n, final byte b) {
        Bits.unsafe.putByte(n, b);
    }
    
    private static void putBoolean_(final long n, final boolean b) {
        Bits.unsafe.putBoolean(null, n, b);
    }
    
    private static void putChar_(final long n, final char c) {
        Bits.unsafe.putChar(n, Bits.bigEndian ? c : swap(c));
    }
    
    private static void putShort_(final long n, final short n2) {
        Bits.unsafe.putShort(n, Bits.bigEndian ? n2 : swap(n2));
    }
    
    private static void putInt_(final long n, final int n2) {
        Bits.unsafe.putInt(n, Bits.bigEndian ? n2 : swap(n2));
    }
    
    private static void putLong_(final long n, final long n2) {
        Bits.unsafe.putLong(n, Bits.bigEndian ? n2 : swap(n2));
    }
    
    private static void putFloat_(final long n, final float n2) {
        Bits.unsafe.putFloat(n, Bits.bigEndian ? n2 : swap(n2));
    }
    
    private static void putDouble_(final long n, final double n2) {
        Bits.unsafe.putDouble(n, Bits.bigEndian ? n2 : swap(n2));
    }
    
    static int putByte(final long n, final byte b) {
        putByte_(n, b);
        return 1;
    }
    
    static int putBoolean(final long n, final boolean b) {
        putBoolean_(n, b);
        return 1;
    }
    
    static int putChar(final long n, final char c) {
        if (isAddressAligned(n, 2)) {
            putChar_(n, c);
            return 2;
        }
        putCharBigEndianUnaligned(n, c);
        return 2;
    }
    
    static int putShort(final long n, final short n2) {
        if (isAddressAligned(n, 2)) {
            putShort_(n, n2);
            return 2;
        }
        putShortBigEndianUnaligned(n, n2);
        return 2;
    }
    
    static int putInt(final long n, final int n2) {
        if (isAddressAligned(n, 4)) {
            putInt_(n, n2);
            return 4;
        }
        putIntBigEndianUnaligned(n, n2);
        return 4;
    }
    
    static int putLong(final long n, final long n2) {
        if (isAddressAligned(n, 8)) {
            putLong_(n, n2);
            return 8;
        }
        putLongBigEndianUnaligned(n, n2);
        return 8;
    }
    
    static int putFloat(final long n, final float n2) {
        if (isAddressAligned(n, 4)) {
            putFloat_(n, n2);
            return 4;
        }
        putFloatBigEndianUnaligned(n, n2);
        return 4;
    }
    
    static int putDouble(final long n, final double n2) {
        if (isAddressAligned(n, 8)) {
            putDouble_(n, n2);
            return 8;
        }
        putDoubleBigEndianUnaligned(n, n2);
        return 8;
    }
    
    static {
        unsafe = Unsafe.getUnsafe();
        bigEndian = (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN);
    }
}
