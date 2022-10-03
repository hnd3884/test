package sun.security.provider;

import java.nio.ByteOrder;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import sun.misc.Unsafe;

final class ByteArrayAccess
{
    private static final Unsafe unsafe;
    private static final boolean littleEndianUnaligned;
    private static final boolean bigEndian;
    private static final int byteArrayOfs;
    
    private ByteArrayAccess() {
    }
    
    private static boolean unaligned() {
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("os.arch", ""));
        return s.equals("i386") || s.equals("x86") || s.equals("amd64") || s.equals("x86_64") || s.equals("ppc64") || s.equals("ppc64le");
    }
    
    static void b2iLittle(final byte[] array, int i, final int[] array2, int n, int n2) {
        if (i < 0 || array.length - i < n2 || n < 0 || array2.length - n < n2 / 4) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (ByteArrayAccess.littleEndianUnaligned) {
            for (i += ByteArrayAccess.byteArrayOfs, n2 += i; i < n2; i += 4) {
                array2[n++] = ByteArrayAccess.unsafe.getInt(array, (long)i);
            }
        }
        else if (ByteArrayAccess.bigEndian && (i & 0x3) == 0x0) {
            for (i += ByteArrayAccess.byteArrayOfs, n2 += i; i < n2; i += 4) {
                array2[n++] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(array, (long)i));
            }
        }
        else {
            for (n2 += i; i < n2; i += 4) {
                array2[n++] = ((array[i] & 0xFF) | (array[i + 1] & 0xFF) << 8 | (array[i + 2] & 0xFF) << 16 | array[i + 3] << 24);
            }
        }
    }
    
    static void b2iLittle64(final byte[] array, int n, final int[] array2) {
        if (n < 0 || array.length - n < 64 || array2.length < 16) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (ByteArrayAccess.littleEndianUnaligned) {
            n += ByteArrayAccess.byteArrayOfs;
            array2[0] = ByteArrayAccess.unsafe.getInt(array, (long)n);
            array2[1] = ByteArrayAccess.unsafe.getInt(array, (long)(n + 4));
            array2[2] = ByteArrayAccess.unsafe.getInt(array, (long)(n + 8));
            array2[3] = ByteArrayAccess.unsafe.getInt(array, (long)(n + 12));
            array2[4] = ByteArrayAccess.unsafe.getInt(array, (long)(n + 16));
            array2[5] = ByteArrayAccess.unsafe.getInt(array, (long)(n + 20));
            array2[6] = ByteArrayAccess.unsafe.getInt(array, (long)(n + 24));
            array2[7] = ByteArrayAccess.unsafe.getInt(array, (long)(n + 28));
            array2[8] = ByteArrayAccess.unsafe.getInt(array, (long)(n + 32));
            array2[9] = ByteArrayAccess.unsafe.getInt(array, (long)(n + 36));
            array2[10] = ByteArrayAccess.unsafe.getInt(array, (long)(n + 40));
            array2[11] = ByteArrayAccess.unsafe.getInt(array, (long)(n + 44));
            array2[12] = ByteArrayAccess.unsafe.getInt(array, (long)(n + 48));
            array2[13] = ByteArrayAccess.unsafe.getInt(array, (long)(n + 52));
            array2[14] = ByteArrayAccess.unsafe.getInt(array, (long)(n + 56));
            array2[15] = ByteArrayAccess.unsafe.getInt(array, (long)(n + 60));
        }
        else if (ByteArrayAccess.bigEndian && (n & 0x3) == 0x0) {
            n += ByteArrayAccess.byteArrayOfs;
            array2[0] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(array, (long)n));
            array2[1] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(array, (long)(n + 4)));
            array2[2] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(array, (long)(n + 8)));
            array2[3] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(array, (long)(n + 12)));
            array2[4] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(array, (long)(n + 16)));
            array2[5] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(array, (long)(n + 20)));
            array2[6] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(array, (long)(n + 24)));
            array2[7] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(array, (long)(n + 28)));
            array2[8] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(array, (long)(n + 32)));
            array2[9] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(array, (long)(n + 36)));
            array2[10] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(array, (long)(n + 40)));
            array2[11] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(array, (long)(n + 44)));
            array2[12] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(array, (long)(n + 48)));
            array2[13] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(array, (long)(n + 52)));
            array2[14] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(array, (long)(n + 56)));
            array2[15] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(array, (long)(n + 60)));
        }
        else {
            b2iLittle(array, n, array2, 0, 64);
        }
    }
    
    static void i2bLittle(final int[] array, int n, final byte[] array2, int i, int n2) {
        if (n < 0 || array.length - n < n2 / 4 || i < 0 || array2.length - i < n2) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (ByteArrayAccess.littleEndianUnaligned) {
            for (i += ByteArrayAccess.byteArrayOfs, n2 += i; i < n2; i += 4) {
                ByteArrayAccess.unsafe.putInt(array2, (long)i, array[n++]);
            }
        }
        else if (ByteArrayAccess.bigEndian && (i & 0x3) == 0x0) {
            for (i += ByteArrayAccess.byteArrayOfs, n2 += i; i < n2; i += 4) {
                ByteArrayAccess.unsafe.putInt(array2, (long)i, Integer.reverseBytes(array[n++]));
            }
        }
        else {
            int n3;
            for (n2 += i; i < n2; array2[i++] = (byte)n3, array2[i++] = (byte)(n3 >> 8), array2[i++] = (byte)(n3 >> 16), array2[i++] = (byte)(n3 >> 24)) {
                n3 = array[n++];
            }
        }
    }
    
    static void i2bLittle4(final int n, final byte[] array, final int n2) {
        if (n2 < 0 || array.length - n2 < 4) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (ByteArrayAccess.littleEndianUnaligned) {
            ByteArrayAccess.unsafe.putInt(array, (long)(ByteArrayAccess.byteArrayOfs + n2), n);
        }
        else if (ByteArrayAccess.bigEndian && (n2 & 0x3) == 0x0) {
            ByteArrayAccess.unsafe.putInt(array, (long)(ByteArrayAccess.byteArrayOfs + n2), Integer.reverseBytes(n));
        }
        else {
            array[n2] = (byte)n;
            array[n2 + 1] = (byte)(n >> 8);
            array[n2 + 2] = (byte)(n >> 16);
            array[n2 + 3] = (byte)(n >> 24);
        }
    }
    
    static void b2iBig(final byte[] array, int i, final int[] array2, int n, int n2) {
        if (i < 0 || array.length - i < n2 || n < 0 || array2.length - n < n2 / 4) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (ByteArrayAccess.littleEndianUnaligned) {
            for (i += ByteArrayAccess.byteArrayOfs, n2 += i; i < n2; i += 4) {
                array2[n++] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(array, (long)i));
            }
        }
        else if (ByteArrayAccess.bigEndian && (i & 0x3) == 0x0) {
            for (i += ByteArrayAccess.byteArrayOfs, n2 += i; i < n2; i += 4) {
                array2[n++] = ByteArrayAccess.unsafe.getInt(array, (long)i);
            }
        }
        else {
            for (n2 += i; i < n2; i += 4) {
                array2[n++] = ((array[i + 3] & 0xFF) | (array[i + 2] & 0xFF) << 8 | (array[i + 1] & 0xFF) << 16 | array[i] << 24);
            }
        }
    }
    
    static void b2iBig64(final byte[] array, int n, final int[] array2) {
        if (n < 0 || array.length - n < 64 || array2.length < 16) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (ByteArrayAccess.littleEndianUnaligned) {
            n += ByteArrayAccess.byteArrayOfs;
            array2[0] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(array, (long)n));
            array2[1] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(array, (long)(n + 4)));
            array2[2] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(array, (long)(n + 8)));
            array2[3] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(array, (long)(n + 12)));
            array2[4] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(array, (long)(n + 16)));
            array2[5] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(array, (long)(n + 20)));
            array2[6] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(array, (long)(n + 24)));
            array2[7] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(array, (long)(n + 28)));
            array2[8] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(array, (long)(n + 32)));
            array2[9] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(array, (long)(n + 36)));
            array2[10] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(array, (long)(n + 40)));
            array2[11] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(array, (long)(n + 44)));
            array2[12] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(array, (long)(n + 48)));
            array2[13] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(array, (long)(n + 52)));
            array2[14] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(array, (long)(n + 56)));
            array2[15] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(array, (long)(n + 60)));
        }
        else if (ByteArrayAccess.bigEndian && (n & 0x3) == 0x0) {
            n += ByteArrayAccess.byteArrayOfs;
            array2[0] = ByteArrayAccess.unsafe.getInt(array, (long)n);
            array2[1] = ByteArrayAccess.unsafe.getInt(array, (long)(n + 4));
            array2[2] = ByteArrayAccess.unsafe.getInt(array, (long)(n + 8));
            array2[3] = ByteArrayAccess.unsafe.getInt(array, (long)(n + 12));
            array2[4] = ByteArrayAccess.unsafe.getInt(array, (long)(n + 16));
            array2[5] = ByteArrayAccess.unsafe.getInt(array, (long)(n + 20));
            array2[6] = ByteArrayAccess.unsafe.getInt(array, (long)(n + 24));
            array2[7] = ByteArrayAccess.unsafe.getInt(array, (long)(n + 28));
            array2[8] = ByteArrayAccess.unsafe.getInt(array, (long)(n + 32));
            array2[9] = ByteArrayAccess.unsafe.getInt(array, (long)(n + 36));
            array2[10] = ByteArrayAccess.unsafe.getInt(array, (long)(n + 40));
            array2[11] = ByteArrayAccess.unsafe.getInt(array, (long)(n + 44));
            array2[12] = ByteArrayAccess.unsafe.getInt(array, (long)(n + 48));
            array2[13] = ByteArrayAccess.unsafe.getInt(array, (long)(n + 52));
            array2[14] = ByteArrayAccess.unsafe.getInt(array, (long)(n + 56));
            array2[15] = ByteArrayAccess.unsafe.getInt(array, (long)(n + 60));
        }
        else {
            b2iBig(array, n, array2, 0, 64);
        }
    }
    
    static void i2bBig(final int[] array, int n, final byte[] array2, int i, int n2) {
        if (n < 0 || array.length - n < n2 / 4 || i < 0 || array2.length - i < n2) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (ByteArrayAccess.littleEndianUnaligned) {
            for (i += ByteArrayAccess.byteArrayOfs, n2 += i; i < n2; i += 4) {
                ByteArrayAccess.unsafe.putInt(array2, (long)i, Integer.reverseBytes(array[n++]));
            }
        }
        else if (ByteArrayAccess.bigEndian && (i & 0x3) == 0x0) {
            for (i += ByteArrayAccess.byteArrayOfs, n2 += i; i < n2; i += 4) {
                ByteArrayAccess.unsafe.putInt(array2, (long)i, array[n++]);
            }
        }
        else {
            int n3;
            for (n2 += i; i < n2; array2[i++] = (byte)(n3 >> 24), array2[i++] = (byte)(n3 >> 16), array2[i++] = (byte)(n3 >> 8), array2[i++] = (byte)n3) {
                n3 = array[n++];
            }
        }
    }
    
    static void i2bBig4(final int n, final byte[] array, final int n2) {
        if (n2 < 0 || array.length - n2 < 4) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (ByteArrayAccess.littleEndianUnaligned) {
            ByteArrayAccess.unsafe.putInt(array, (long)(ByteArrayAccess.byteArrayOfs + n2), Integer.reverseBytes(n));
        }
        else if (ByteArrayAccess.bigEndian && (n2 & 0x3) == 0x0) {
            ByteArrayAccess.unsafe.putInt(array, (long)(ByteArrayAccess.byteArrayOfs + n2), n);
        }
        else {
            array[n2] = (byte)(n >> 24);
            array[n2 + 1] = (byte)(n >> 16);
            array[n2 + 2] = (byte)(n >> 8);
            array[n2 + 3] = (byte)n;
        }
    }
    
    static void b2lBig(final byte[] array, int i, final long[] array2, int n, int n2) {
        if (i < 0 || array.length - i < n2 || n < 0 || array2.length - n < n2 / 8) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (ByteArrayAccess.littleEndianUnaligned) {
            for (i += ByteArrayAccess.byteArrayOfs, n2 += i; i < n2; i += 8) {
                array2[n++] = Long.reverseBytes(ByteArrayAccess.unsafe.getLong(array, (long)i));
            }
        }
        else if (ByteArrayAccess.bigEndian && (i & 0x3) == 0x0) {
            for (i += ByteArrayAccess.byteArrayOfs, n2 += i; i < n2; i += 8) {
                array2[n++] = ((long)ByteArrayAccess.unsafe.getInt(array, (long)i) << 32 | ((long)ByteArrayAccess.unsafe.getInt(array, (long)(i + 4)) & 0xFFFFFFFFL));
            }
        }
        else {
            int n3;
            for (n2 += i; i < n2; i += 4, array2[n++] = ((long)n3 << 32 | ((long)((array[i + 3] & 0xFF) | (array[i + 2] & 0xFF) << 8 | (array[i + 1] & 0xFF) << 16 | array[i] << 24) & 0xFFFFFFFFL)), i += 4) {
                n3 = ((array[i + 3] & 0xFF) | (array[i + 2] & 0xFF) << 8 | (array[i + 1] & 0xFF) << 16 | array[i] << 24);
            }
        }
    }
    
    static void b2lBig128(final byte[] array, int n, final long[] array2) {
        if (n < 0 || array.length - n < 128 || array2.length < 16) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (ByteArrayAccess.littleEndianUnaligned) {
            n += ByteArrayAccess.byteArrayOfs;
            array2[0] = Long.reverseBytes(ByteArrayAccess.unsafe.getLong(array, (long)n));
            array2[1] = Long.reverseBytes(ByteArrayAccess.unsafe.getLong(array, (long)(n + 8)));
            array2[2] = Long.reverseBytes(ByteArrayAccess.unsafe.getLong(array, (long)(n + 16)));
            array2[3] = Long.reverseBytes(ByteArrayAccess.unsafe.getLong(array, (long)(n + 24)));
            array2[4] = Long.reverseBytes(ByteArrayAccess.unsafe.getLong(array, (long)(n + 32)));
            array2[5] = Long.reverseBytes(ByteArrayAccess.unsafe.getLong(array, (long)(n + 40)));
            array2[6] = Long.reverseBytes(ByteArrayAccess.unsafe.getLong(array, (long)(n + 48)));
            array2[7] = Long.reverseBytes(ByteArrayAccess.unsafe.getLong(array, (long)(n + 56)));
            array2[8] = Long.reverseBytes(ByteArrayAccess.unsafe.getLong(array, (long)(n + 64)));
            array2[9] = Long.reverseBytes(ByteArrayAccess.unsafe.getLong(array, (long)(n + 72)));
            array2[10] = Long.reverseBytes(ByteArrayAccess.unsafe.getLong(array, (long)(n + 80)));
            array2[11] = Long.reverseBytes(ByteArrayAccess.unsafe.getLong(array, (long)(n + 88)));
            array2[12] = Long.reverseBytes(ByteArrayAccess.unsafe.getLong(array, (long)(n + 96)));
            array2[13] = Long.reverseBytes(ByteArrayAccess.unsafe.getLong(array, (long)(n + 104)));
            array2[14] = Long.reverseBytes(ByteArrayAccess.unsafe.getLong(array, (long)(n + 112)));
            array2[15] = Long.reverseBytes(ByteArrayAccess.unsafe.getLong(array, (long)(n + 120)));
        }
        else {
            b2lBig(array, n, array2, 0, 128);
        }
    }
    
    static void l2bBig(final long[] array, int n, final byte[] array2, int i, int n2) {
        if (n < 0 || array.length - n < n2 / 8 || i < 0 || array2.length - i < n2) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (ByteArrayAccess.littleEndianUnaligned) {
            for (i += ByteArrayAccess.byteArrayOfs, n2 += i; i < n2; i += 8) {
                ByteArrayAccess.unsafe.putLong(array2, (long)i, Long.reverseBytes(array[n++]));
            }
        }
        else {
            long n3;
            for (n2 += i; i < n2; array2[i++] = (byte)(n3 >> 56), array2[i++] = (byte)(n3 >> 48), array2[i++] = (byte)(n3 >> 40), array2[i++] = (byte)(n3 >> 32), array2[i++] = (byte)(n3 >> 24), array2[i++] = (byte)(n3 >> 16), array2[i++] = (byte)(n3 >> 8), array2[i++] = (byte)n3) {
                n3 = array[n++];
            }
        }
    }
    
    static void b2lLittle(final byte[] array, int i, final long[] array2, int n, int n2) {
        if (i < 0 || array.length - i < n2 || n < 0 || array2.length - n < n2 / 8) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (ByteArrayAccess.littleEndianUnaligned) {
            for (i += ByteArrayAccess.byteArrayOfs, n2 += i; i < n2; i += 8) {
                array2[n++] = ByteArrayAccess.unsafe.getLong(array, (long)i);
            }
        }
        else {
            for (n2 += i; i < n2; i += 8) {
                array2[n++] = (((long)array[i] & 0xFFL) | ((long)array[i + 1] & 0xFFL) << 8 | ((long)array[i + 2] & 0xFFL) << 16 | ((long)array[i + 3] & 0xFFL) << 24 | ((long)array[i + 4] & 0xFFL) << 32 | ((long)array[i + 5] & 0xFFL) << 40 | ((long)array[i + 6] & 0xFFL) << 48 | ((long)array[i + 7] & 0xFFL) << 56);
            }
        }
    }
    
    static void l2bLittle(final long[] array, int n, final byte[] array2, int i, int n2) {
        if (n < 0 || array.length - n < n2 / 8 || i < 0 || array2.length - i < n2) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (ByteArrayAccess.littleEndianUnaligned) {
            for (i += ByteArrayAccess.byteArrayOfs, n2 += i; i < n2; i += 8) {
                ByteArrayAccess.unsafe.putLong(array2, (long)i, array[n++]);
            }
        }
        else {
            long n3;
            for (n2 += i; i < n2; array2[i++] = (byte)n3, array2[i++] = (byte)(n3 >> 8), array2[i++] = (byte)(n3 >> 16), array2[i++] = (byte)(n3 >> 24), array2[i++] = (byte)(n3 >> 32), array2[i++] = (byte)(n3 >> 40), array2[i++] = (byte)(n3 >> 48), array2[i++] = (byte)(n3 >> 56)) {
                n3 = array[n++];
            }
        }
    }
    
    static {
        unsafe = Unsafe.getUnsafe();
        byteArrayOfs = ByteArrayAccess.unsafe.arrayBaseOffset(byte[].class);
        final boolean b = ByteArrayAccess.unsafe.arrayIndexScale(byte[].class) == 1 && ByteArrayAccess.unsafe.arrayIndexScale(int[].class) == 4 && ByteArrayAccess.unsafe.arrayIndexScale(long[].class) == 8 && (ByteArrayAccess.byteArrayOfs & 0x3) == 0x0;
        final ByteOrder nativeOrder = ByteOrder.nativeOrder();
        littleEndianUnaligned = (b && unaligned() && nativeOrder == ByteOrder.LITTLE_ENDIAN);
        bigEndian = (b && nativeOrder == ByteOrder.BIG_ENDIAN);
    }
}
