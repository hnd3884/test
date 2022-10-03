package java.nio;

import sun.misc.JavaNioAccess;
import sun.misc.JavaLangRefAccess;
import sun.misc.SharedSecrets;
import sun.misc.VM;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.util.concurrent.atomic.AtomicLong;
import sun.misc.Unsafe;

class Bits
{
    private static final Unsafe unsafe;
    private static final ByteOrder byteOrder;
    private static int pageSize;
    private static boolean unaligned;
    private static boolean unalignedKnown;
    private static volatile long maxMemory;
    private static final AtomicLong reservedMemory;
    private static final AtomicLong totalCapacity;
    private static final AtomicLong count;
    private static volatile boolean memoryLimitSet;
    private static final int MAX_SLEEPS = 9;
    static final int JNI_COPY_TO_ARRAY_THRESHOLD = 6;
    static final int JNI_COPY_FROM_ARRAY_THRESHOLD = 6;
    static final long UNSAFE_COPY_THRESHOLD = 1048576L;
    
    private Bits() {
    }
    
    static short swap(final short n) {
        return Short.reverseBytes(n);
    }
    
    static char swap(final char c) {
        return Character.reverseBytes(c);
    }
    
    static int swap(final int n) {
        return Integer.reverseBytes(n);
    }
    
    static long swap(final long n) {
        return Long.reverseBytes(n);
    }
    
    private static char makeChar(final byte b, final byte b2) {
        return (char)(b << 8 | (b2 & 0xFF));
    }
    
    static char getCharL(final ByteBuffer byteBuffer, final int n) {
        return makeChar(byteBuffer._get(n + 1), byteBuffer._get(n));
    }
    
    static char getCharL(final long n) {
        return makeChar(_get(n + 1L), _get(n));
    }
    
    static char getCharB(final ByteBuffer byteBuffer, final int n) {
        return makeChar(byteBuffer._get(n), byteBuffer._get(n + 1));
    }
    
    static char getCharB(final long n) {
        return makeChar(_get(n), _get(n + 1L));
    }
    
    static char getChar(final ByteBuffer byteBuffer, final int n, final boolean b) {
        return b ? getCharB(byteBuffer, n) : getCharL(byteBuffer, n);
    }
    
    static char getChar(final long n, final boolean b) {
        return b ? getCharB(n) : getCharL(n);
    }
    
    private static byte char1(final char c) {
        return (byte)(c >> 8);
    }
    
    private static byte char0(final char c) {
        return (byte)c;
    }
    
    static void putCharL(final ByteBuffer byteBuffer, final int n, final char c) {
        byteBuffer._put(n, char0(c));
        byteBuffer._put(n + 1, char1(c));
    }
    
    static void putCharL(final long n, final char c) {
        _put(n, char0(c));
        _put(n + 1L, char1(c));
    }
    
    static void putCharB(final ByteBuffer byteBuffer, final int n, final char c) {
        byteBuffer._put(n, char1(c));
        byteBuffer._put(n + 1, char0(c));
    }
    
    static void putCharB(final long n, final char c) {
        _put(n, char1(c));
        _put(n + 1L, char0(c));
    }
    
    static void putChar(final ByteBuffer byteBuffer, final int n, final char c, final boolean b) {
        if (b) {
            putCharB(byteBuffer, n, c);
        }
        else {
            putCharL(byteBuffer, n, c);
        }
    }
    
    static void putChar(final long n, final char c, final boolean b) {
        if (b) {
            putCharB(n, c);
        }
        else {
            putCharL(n, c);
        }
    }
    
    private static short makeShort(final byte b, final byte b2) {
        return (short)(b << 8 | (b2 & 0xFF));
    }
    
    static short getShortL(final ByteBuffer byteBuffer, final int n) {
        return makeShort(byteBuffer._get(n + 1), byteBuffer._get(n));
    }
    
    static short getShortL(final long n) {
        return makeShort(_get(n + 1L), _get(n));
    }
    
    static short getShortB(final ByteBuffer byteBuffer, final int n) {
        return makeShort(byteBuffer._get(n), byteBuffer._get(n + 1));
    }
    
    static short getShortB(final long n) {
        return makeShort(_get(n), _get(n + 1L));
    }
    
    static short getShort(final ByteBuffer byteBuffer, final int n, final boolean b) {
        return b ? getShortB(byteBuffer, n) : getShortL(byteBuffer, n);
    }
    
    static short getShort(final long n, final boolean b) {
        return b ? getShortB(n) : getShortL(n);
    }
    
    private static byte short1(final short n) {
        return (byte)(n >> 8);
    }
    
    private static byte short0(final short n) {
        return (byte)n;
    }
    
    static void putShortL(final ByteBuffer byteBuffer, final int n, final short n2) {
        byteBuffer._put(n, short0(n2));
        byteBuffer._put(n + 1, short1(n2));
    }
    
    static void putShortL(final long n, final short n2) {
        _put(n, short0(n2));
        _put(n + 1L, short1(n2));
    }
    
    static void putShortB(final ByteBuffer byteBuffer, final int n, final short n2) {
        byteBuffer._put(n, short1(n2));
        byteBuffer._put(n + 1, short0(n2));
    }
    
    static void putShortB(final long n, final short n2) {
        _put(n, short1(n2));
        _put(n + 1L, short0(n2));
    }
    
    static void putShort(final ByteBuffer byteBuffer, final int n, final short n2, final boolean b) {
        if (b) {
            putShortB(byteBuffer, n, n2);
        }
        else {
            putShortL(byteBuffer, n, n2);
        }
    }
    
    static void putShort(final long n, final short n2, final boolean b) {
        if (b) {
            putShortB(n, n2);
        }
        else {
            putShortL(n, n2);
        }
    }
    
    private static int makeInt(final byte b, final byte b2, final byte b3, final byte b4) {
        return b << 24 | (b2 & 0xFF) << 16 | (b3 & 0xFF) << 8 | (b4 & 0xFF);
    }
    
    static int getIntL(final ByteBuffer byteBuffer, final int n) {
        return makeInt(byteBuffer._get(n + 3), byteBuffer._get(n + 2), byteBuffer._get(n + 1), byteBuffer._get(n));
    }
    
    static int getIntL(final long n) {
        return makeInt(_get(n + 3L), _get(n + 2L), _get(n + 1L), _get(n));
    }
    
    static int getIntB(final ByteBuffer byteBuffer, final int n) {
        return makeInt(byteBuffer._get(n), byteBuffer._get(n + 1), byteBuffer._get(n + 2), byteBuffer._get(n + 3));
    }
    
    static int getIntB(final long n) {
        return makeInt(_get(n), _get(n + 1L), _get(n + 2L), _get(n + 3L));
    }
    
    static int getInt(final ByteBuffer byteBuffer, final int n, final boolean b) {
        return b ? getIntB(byteBuffer, n) : getIntL(byteBuffer, n);
    }
    
    static int getInt(final long n, final boolean b) {
        return b ? getIntB(n) : getIntL(n);
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
    
    static void putIntL(final ByteBuffer byteBuffer, final int n, final int n2) {
        byteBuffer._put(n + 3, int3(n2));
        byteBuffer._put(n + 2, int2(n2));
        byteBuffer._put(n + 1, int1(n2));
        byteBuffer._put(n, int0(n2));
    }
    
    static void putIntL(final long n, final int n2) {
        _put(n + 3L, int3(n2));
        _put(n + 2L, int2(n2));
        _put(n + 1L, int1(n2));
        _put(n, int0(n2));
    }
    
    static void putIntB(final ByteBuffer byteBuffer, final int n, final int n2) {
        byteBuffer._put(n, int3(n2));
        byteBuffer._put(n + 1, int2(n2));
        byteBuffer._put(n + 2, int1(n2));
        byteBuffer._put(n + 3, int0(n2));
    }
    
    static void putIntB(final long n, final int n2) {
        _put(n, int3(n2));
        _put(n + 1L, int2(n2));
        _put(n + 2L, int1(n2));
        _put(n + 3L, int0(n2));
    }
    
    static void putInt(final ByteBuffer byteBuffer, final int n, final int n2, final boolean b) {
        if (b) {
            putIntB(byteBuffer, n, n2);
        }
        else {
            putIntL(byteBuffer, n, n2);
        }
    }
    
    static void putInt(final long n, final int n2, final boolean b) {
        if (b) {
            putIntB(n, n2);
        }
        else {
            putIntL(n, n2);
        }
    }
    
    private static long makeLong(final byte b, final byte b2, final byte b3, final byte b4, final byte b5, final byte b6, final byte b7, final byte b8) {
        return (long)b << 56 | ((long)b2 & 0xFFL) << 48 | ((long)b3 & 0xFFL) << 40 | ((long)b4 & 0xFFL) << 32 | ((long)b5 & 0xFFL) << 24 | ((long)b6 & 0xFFL) << 16 | ((long)b7 & 0xFFL) << 8 | ((long)b8 & 0xFFL);
    }
    
    static long getLongL(final ByteBuffer byteBuffer, final int n) {
        return makeLong(byteBuffer._get(n + 7), byteBuffer._get(n + 6), byteBuffer._get(n + 5), byteBuffer._get(n + 4), byteBuffer._get(n + 3), byteBuffer._get(n + 2), byteBuffer._get(n + 1), byteBuffer._get(n));
    }
    
    static long getLongL(final long n) {
        return makeLong(_get(n + 7L), _get(n + 6L), _get(n + 5L), _get(n + 4L), _get(n + 3L), _get(n + 2L), _get(n + 1L), _get(n));
    }
    
    static long getLongB(final ByteBuffer byteBuffer, final int n) {
        return makeLong(byteBuffer._get(n), byteBuffer._get(n + 1), byteBuffer._get(n + 2), byteBuffer._get(n + 3), byteBuffer._get(n + 4), byteBuffer._get(n + 5), byteBuffer._get(n + 6), byteBuffer._get(n + 7));
    }
    
    static long getLongB(final long n) {
        return makeLong(_get(n), _get(n + 1L), _get(n + 2L), _get(n + 3L), _get(n + 4L), _get(n + 5L), _get(n + 6L), _get(n + 7L));
    }
    
    static long getLong(final ByteBuffer byteBuffer, final int n, final boolean b) {
        return b ? getLongB(byteBuffer, n) : getLongL(byteBuffer, n);
    }
    
    static long getLong(final long n, final boolean b) {
        return b ? getLongB(n) : getLongL(n);
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
    
    static void putLongL(final ByteBuffer byteBuffer, final int n, final long n2) {
        byteBuffer._put(n + 7, long7(n2));
        byteBuffer._put(n + 6, long6(n2));
        byteBuffer._put(n + 5, long5(n2));
        byteBuffer._put(n + 4, long4(n2));
        byteBuffer._put(n + 3, long3(n2));
        byteBuffer._put(n + 2, long2(n2));
        byteBuffer._put(n + 1, long1(n2));
        byteBuffer._put(n, long0(n2));
    }
    
    static void putLongL(final long n, final long n2) {
        _put(n + 7L, long7(n2));
        _put(n + 6L, long6(n2));
        _put(n + 5L, long5(n2));
        _put(n + 4L, long4(n2));
        _put(n + 3L, long3(n2));
        _put(n + 2L, long2(n2));
        _put(n + 1L, long1(n2));
        _put(n, long0(n2));
    }
    
    static void putLongB(final ByteBuffer byteBuffer, final int n, final long n2) {
        byteBuffer._put(n, long7(n2));
        byteBuffer._put(n + 1, long6(n2));
        byteBuffer._put(n + 2, long5(n2));
        byteBuffer._put(n + 3, long4(n2));
        byteBuffer._put(n + 4, long3(n2));
        byteBuffer._put(n + 5, long2(n2));
        byteBuffer._put(n + 6, long1(n2));
        byteBuffer._put(n + 7, long0(n2));
    }
    
    static void putLongB(final long n, final long n2) {
        _put(n, long7(n2));
        _put(n + 1L, long6(n2));
        _put(n + 2L, long5(n2));
        _put(n + 3L, long4(n2));
        _put(n + 4L, long3(n2));
        _put(n + 5L, long2(n2));
        _put(n + 6L, long1(n2));
        _put(n + 7L, long0(n2));
    }
    
    static void putLong(final ByteBuffer byteBuffer, final int n, final long n2, final boolean b) {
        if (b) {
            putLongB(byteBuffer, n, n2);
        }
        else {
            putLongL(byteBuffer, n, n2);
        }
    }
    
    static void putLong(final long n, final long n2, final boolean b) {
        if (b) {
            putLongB(n, n2);
        }
        else {
            putLongL(n, n2);
        }
    }
    
    static float getFloatL(final ByteBuffer byteBuffer, final int n) {
        return Float.intBitsToFloat(getIntL(byteBuffer, n));
    }
    
    static float getFloatL(final long n) {
        return Float.intBitsToFloat(getIntL(n));
    }
    
    static float getFloatB(final ByteBuffer byteBuffer, final int n) {
        return Float.intBitsToFloat(getIntB(byteBuffer, n));
    }
    
    static float getFloatB(final long n) {
        return Float.intBitsToFloat(getIntB(n));
    }
    
    static float getFloat(final ByteBuffer byteBuffer, final int n, final boolean b) {
        return b ? getFloatB(byteBuffer, n) : getFloatL(byteBuffer, n);
    }
    
    static float getFloat(final long n, final boolean b) {
        return b ? getFloatB(n) : getFloatL(n);
    }
    
    static void putFloatL(final ByteBuffer byteBuffer, final int n, final float n2) {
        putIntL(byteBuffer, n, Float.floatToRawIntBits(n2));
    }
    
    static void putFloatL(final long n, final float n2) {
        putIntL(n, Float.floatToRawIntBits(n2));
    }
    
    static void putFloatB(final ByteBuffer byteBuffer, final int n, final float n2) {
        putIntB(byteBuffer, n, Float.floatToRawIntBits(n2));
    }
    
    static void putFloatB(final long n, final float n2) {
        putIntB(n, Float.floatToRawIntBits(n2));
    }
    
    static void putFloat(final ByteBuffer byteBuffer, final int n, final float n2, final boolean b) {
        if (b) {
            putFloatB(byteBuffer, n, n2);
        }
        else {
            putFloatL(byteBuffer, n, n2);
        }
    }
    
    static void putFloat(final long n, final float n2, final boolean b) {
        if (b) {
            putFloatB(n, n2);
        }
        else {
            putFloatL(n, n2);
        }
    }
    
    static double getDoubleL(final ByteBuffer byteBuffer, final int n) {
        return Double.longBitsToDouble(getLongL(byteBuffer, n));
    }
    
    static double getDoubleL(final long n) {
        return Double.longBitsToDouble(getLongL(n));
    }
    
    static double getDoubleB(final ByteBuffer byteBuffer, final int n) {
        return Double.longBitsToDouble(getLongB(byteBuffer, n));
    }
    
    static double getDoubleB(final long n) {
        return Double.longBitsToDouble(getLongB(n));
    }
    
    static double getDouble(final ByteBuffer byteBuffer, final int n, final boolean b) {
        return b ? getDoubleB(byteBuffer, n) : getDoubleL(byteBuffer, n);
    }
    
    static double getDouble(final long n, final boolean b) {
        return b ? getDoubleB(n) : getDoubleL(n);
    }
    
    static void putDoubleL(final ByteBuffer byteBuffer, final int n, final double n2) {
        putLongL(byteBuffer, n, Double.doubleToRawLongBits(n2));
    }
    
    static void putDoubleL(final long n, final double n2) {
        putLongL(n, Double.doubleToRawLongBits(n2));
    }
    
    static void putDoubleB(final ByteBuffer byteBuffer, final int n, final double n2) {
        putLongB(byteBuffer, n, Double.doubleToRawLongBits(n2));
    }
    
    static void putDoubleB(final long n, final double n2) {
        putLongB(n, Double.doubleToRawLongBits(n2));
    }
    
    static void putDouble(final ByteBuffer byteBuffer, final int n, final double n2, final boolean b) {
        if (b) {
            putDoubleB(byteBuffer, n, n2);
        }
        else {
            putDoubleL(byteBuffer, n, n2);
        }
    }
    
    static void putDouble(final long n, final double n2, final boolean b) {
        if (b) {
            putDoubleB(n, n2);
        }
        else {
            putDoubleL(n, n2);
        }
    }
    
    private static byte _get(final long n) {
        return Bits.unsafe.getByte(n);
    }
    
    private static void _put(final long n, final byte b) {
        Bits.unsafe.putByte(n, b);
    }
    
    static Unsafe unsafe() {
        return Bits.unsafe;
    }
    
    static ByteOrder byteOrder() {
        if (Bits.byteOrder == null) {
            throw new Error("Unknown byte order");
        }
        return Bits.byteOrder;
    }
    
    static int pageSize() {
        if (Bits.pageSize == -1) {
            Bits.pageSize = unsafe().pageSize();
        }
        return Bits.pageSize;
    }
    
    static int pageCount(final long n) {
        return (int)(n + pageSize() - 1L) / pageSize();
    }
    
    static boolean unaligned() {
        if (Bits.unalignedKnown) {
            return Bits.unaligned;
        }
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("os.arch"));
        Bits.unaligned = (s.equals("i386") || s.equals("x86") || s.equals("amd64") || s.equals("x86_64") || s.equals("ppc64") || s.equals("ppc64le"));
        Bits.unalignedKnown = true;
        return Bits.unaligned;
    }
    
    static void reserveMemory(final long n, final int n2) {
        if (!Bits.memoryLimitSet && VM.isBooted()) {
            Bits.maxMemory = VM.maxDirectMemory();
            Bits.memoryLimitSet = true;
        }
        if (tryReserveMemory(n, n2)) {
            return;
        }
        final JavaLangRefAccess javaLangRefAccess = SharedSecrets.getJavaLangRefAccess();
        while (javaLangRefAccess.tryHandlePendingReference()) {
            if (tryReserveMemory(n, n2)) {
                return;
            }
        }
        System.gc();
        boolean b = false;
        try {
            long n3 = 1L;
            int n4 = 0;
            while (!tryReserveMemory(n, n2)) {
                if (n4 >= 9) {
                    throw new OutOfMemoryError("Direct buffer memory");
                }
                if (javaLangRefAccess.tryHandlePendingReference()) {
                    continue;
                }
                try {
                    Thread.sleep(n3);
                    n3 <<= 1;
                    ++n4;
                }
                catch (final InterruptedException ex) {
                    b = true;
                }
            }
        }
        finally {
            if (b) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    private static boolean tryReserveMemory(final long n, final int n2) {
        long value;
        while (n2 <= Bits.maxMemory - (value = Bits.totalCapacity.get())) {
            if (Bits.totalCapacity.compareAndSet(value, value + n2)) {
                Bits.reservedMemory.addAndGet(n);
                Bits.count.incrementAndGet();
                return true;
            }
        }
        return false;
    }
    
    static void unreserveMemory(final long n, final int n2) {
        final long decrementAndGet = Bits.count.decrementAndGet();
        final long addAndGet = Bits.reservedMemory.addAndGet(-n);
        final long addAndGet2 = Bits.totalCapacity.addAndGet(-n2);
        assert decrementAndGet >= 0L && addAndGet >= 0L && addAndGet2 >= 0L;
    }
    
    static void copyFromArray(final Object o, final long n, final long n2, long n3, long n4) {
        long n6;
        for (long n5 = n + n2; n4 > 0L; n4 -= n6, n5 += n6, n3 += n6) {
            n6 = ((n4 > 1048576L) ? 1048576L : n4);
            Bits.unsafe.copyMemory(o, n5, null, n3, n6);
        }
    }
    
    static void copyToArray(long n, final Object o, final long n2, final long n3, long n4) {
        long n6;
        for (long n5 = n2 + n3; n4 > 0L; n4 -= n6, n += n6, n5 += n6) {
            n6 = ((n4 > 1048576L) ? 1048576L : n4);
            Bits.unsafe.copyMemory(null, n, o, n5, n6);
        }
    }
    
    static void copyFromCharArray(final Object o, final long n, final long n2, final long n3) {
        copySwapMemory(o, Bits.unsafe.arrayBaseOffset(o.getClass()) + n, null, n2, n3, 2L);
    }
    
    static void copyToCharArray(final long n, final Object o, final long n2, final long n3) {
        copySwapMemory(null, n, o, Bits.unsafe.arrayBaseOffset(o.getClass()) + n2, n3, 2L);
    }
    
    static void copyFromShortArray(final Object o, final long n, final long n2, final long n3) {
        copySwapMemory(o, Bits.unsafe.arrayBaseOffset(o.getClass()) + n, null, n2, n3, 2L);
    }
    
    static void copyToShortArray(final long n, final Object o, final long n2, final long n3) {
        copySwapMemory(null, n, o, Bits.unsafe.arrayBaseOffset(o.getClass()) + n2, n3, 2L);
    }
    
    static void copyFromIntArray(final Object o, final long n, final long n2, final long n3) {
        copySwapMemory(o, Bits.unsafe.arrayBaseOffset(o.getClass()) + n, null, n2, n3, 4L);
    }
    
    static void copyToIntArray(final long n, final Object o, final long n2, final long n3) {
        copySwapMemory(null, n, o, Bits.unsafe.arrayBaseOffset(o.getClass()) + n2, n3, 4L);
    }
    
    static void copyFromLongArray(final Object o, final long n, final long n2, final long n3) {
        copySwapMemory(o, Bits.unsafe.arrayBaseOffset(o.getClass()) + n, null, n2, n3, 8L);
    }
    
    static void copyToLongArray(final long n, final Object o, final long n2, final long n3) {
        copySwapMemory(null, n, o, Bits.unsafe.arrayBaseOffset(o.getClass()) + n2, n3, 8L);
    }
    
    private static boolean isPrimitiveArray(final Class<?> clazz) {
        final Class<?> componentType = clazz.getComponentType();
        return componentType != null && componentType.isPrimitive();
    }
    
    private static native void copySwapMemory0(final Object p0, final long p1, final Object p2, final long p3, final long p4, final long p5);
    
    private static void copySwapMemory(final Object o, final long n, final Object o2, final long n2, final long n3, final long n4) {
        if (n3 < 0L) {
            throw new IllegalArgumentException();
        }
        if (n4 != 2L && n4 != 4L && n4 != 8L) {
            throw new IllegalArgumentException();
        }
        if (n3 % n4 != 0L) {
            throw new IllegalArgumentException();
        }
        if ((o == null && n == 0L) || (o2 == null && n2 == 0L)) {
            throw new NullPointerException();
        }
        if (o != null && (n < 0L || !isPrimitiveArray(o.getClass()))) {
            throw new IllegalArgumentException();
        }
        if (o2 != null && (n2 < 0L || !isPrimitiveArray(o2.getClass()))) {
            throw new IllegalArgumentException();
        }
        if (Bits.unsafe.addressSize() == 4 && (n3 >>> 32 != 0L || n >>> 32 != 0L || n2 >>> 32 != 0L)) {
            throw new IllegalArgumentException();
        }
        if (n3 == 0L) {
            return;
        }
        copySwapMemory0(o, n, o2, n2, n3, n4);
    }
    
    static {
        unsafe = Unsafe.getUnsafe();
        final long allocateMemory = Bits.unsafe.allocateMemory(8L);
        try {
            Bits.unsafe.putLong(allocateMemory, 72623859790382856L);
            switch (Bits.unsafe.getByte(allocateMemory)) {
                case 1: {
                    byteOrder = ByteOrder.BIG_ENDIAN;
                    break;
                }
                case 8: {
                    byteOrder = ByteOrder.LITTLE_ENDIAN;
                    break;
                }
                default: {
                    assert false;
                    byteOrder = null;
                    break;
                }
            }
        }
        finally {
            Bits.unsafe.freeMemory(allocateMemory);
        }
        Bits.pageSize = -1;
        Bits.unalignedKnown = false;
        Bits.maxMemory = VM.maxDirectMemory();
        reservedMemory = new AtomicLong();
        totalCapacity = new AtomicLong();
        count = new AtomicLong();
        Bits.memoryLimitSet = false;
        SharedSecrets.setJavaNioAccess(new JavaNioAccess() {
            @Override
            public BufferPool getDirectBufferPool() {
                return new BufferPool() {
                    @Override
                    public String getName() {
                        return "direct";
                    }
                    
                    @Override
                    public long getCount() {
                        return Bits.count.get();
                    }
                    
                    @Override
                    public long getTotalCapacity() {
                        return Bits.totalCapacity.get();
                    }
                    
                    @Override
                    public long getMemoryUsed() {
                        return Bits.reservedMemory.get();
                    }
                };
            }
            
            @Override
            public ByteBuffer newDirectByteBuffer(final long n, final int n2, final Object o) {
                return new DirectByteBuffer(n, n2, o);
            }
            
            @Override
            public void truncate(final Buffer buffer) {
                buffer.truncate();
            }
        });
    }
}
