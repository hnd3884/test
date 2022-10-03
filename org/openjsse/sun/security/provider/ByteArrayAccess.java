package org.openjsse.sun.security.provider;

import java.lang.reflect.Field;
import java.nio.ByteOrder;
import sun.misc.VM;
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
        final String arch = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("os.arch", ""));
        return arch.equals("i386") || arch.equals("x86") || arch.equals("amd64") || arch.equals("x86_64") || arch.equals("ppc64") || arch.equals("ppc64le");
    }
    
    static void b2iLittle(final byte[] in, int inOfs, final int[] out, int outOfs, int len) {
        if (inOfs < 0 || in.length - inOfs < len || outOfs < 0 || out.length - outOfs < len / 4) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (ByteArrayAccess.littleEndianUnaligned) {
            for (inOfs += ByteArrayAccess.byteArrayOfs, len += inOfs; inOfs < len; inOfs += 4) {
                out[outOfs++] = ByteArrayAccess.unsafe.getInt(in, (long)inOfs);
            }
        }
        else if (ByteArrayAccess.bigEndian && (inOfs & 0x3) == 0x0) {
            for (inOfs += ByteArrayAccess.byteArrayOfs, len += inOfs; inOfs < len; inOfs += 4) {
                out[outOfs++] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(in, (long)inOfs));
            }
        }
        else {
            for (len += inOfs; inOfs < len; inOfs += 4) {
                out[outOfs++] = ((in[inOfs] & 0xFF) | (in[inOfs + 1] & 0xFF) << 8 | (in[inOfs + 2] & 0xFF) << 16 | in[inOfs + 3] << 24);
            }
        }
    }
    
    static void b2iLittle64(final byte[] in, int inOfs, final int[] out) {
        if (inOfs < 0 || in.length - inOfs < 64 || out.length < 16) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (ByteArrayAccess.littleEndianUnaligned) {
            inOfs += ByteArrayAccess.byteArrayOfs;
            out[0] = ByteArrayAccess.unsafe.getInt(in, (long)inOfs);
            out[1] = ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 4));
            out[2] = ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 8));
            out[3] = ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 12));
            out[4] = ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 16));
            out[5] = ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 20));
            out[6] = ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 24));
            out[7] = ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 28));
            out[8] = ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 32));
            out[9] = ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 36));
            out[10] = ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 40));
            out[11] = ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 44));
            out[12] = ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 48));
            out[13] = ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 52));
            out[14] = ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 56));
            out[15] = ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 60));
        }
        else if (ByteArrayAccess.bigEndian && (inOfs & 0x3) == 0x0) {
            inOfs += ByteArrayAccess.byteArrayOfs;
            out[0] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(in, (long)inOfs));
            out[1] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 4)));
            out[2] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 8)));
            out[3] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 12)));
            out[4] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 16)));
            out[5] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 20)));
            out[6] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 24)));
            out[7] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 28)));
            out[8] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 32)));
            out[9] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 36)));
            out[10] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 40)));
            out[11] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 44)));
            out[12] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 48)));
            out[13] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 52)));
            out[14] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 56)));
            out[15] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 60)));
        }
        else {
            b2iLittle(in, inOfs, out, 0, 64);
        }
    }
    
    static void i2bLittle(final int[] in, int inOfs, final byte[] out, int outOfs, int len) {
        if (inOfs < 0 || in.length - inOfs < len / 4 || outOfs < 0 || out.length - outOfs < len) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (ByteArrayAccess.littleEndianUnaligned) {
            for (outOfs += ByteArrayAccess.byteArrayOfs, len += outOfs; outOfs < len; outOfs += 4) {
                ByteArrayAccess.unsafe.putInt(out, (long)outOfs, in[inOfs++]);
            }
        }
        else if (ByteArrayAccess.bigEndian && (outOfs & 0x3) == 0x0) {
            for (outOfs += ByteArrayAccess.byteArrayOfs, len += outOfs; outOfs < len; outOfs += 4) {
                ByteArrayAccess.unsafe.putInt(out, (long)outOfs, Integer.reverseBytes(in[inOfs++]));
            }
        }
        else {
            int i;
            for (len += outOfs; outOfs < len; out[outOfs++] = (byte)i, out[outOfs++] = (byte)(i >> 8), out[outOfs++] = (byte)(i >> 16), out[outOfs++] = (byte)(i >> 24)) {
                i = in[inOfs++];
            }
        }
    }
    
    static void i2bLittle4(final int val, final byte[] out, final int outOfs) {
        if (outOfs < 0 || out.length - outOfs < 4) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (ByteArrayAccess.littleEndianUnaligned) {
            ByteArrayAccess.unsafe.putInt(out, (long)(ByteArrayAccess.byteArrayOfs + outOfs), val);
        }
        else if (ByteArrayAccess.bigEndian && (outOfs & 0x3) == 0x0) {
            ByteArrayAccess.unsafe.putInt(out, (long)(ByteArrayAccess.byteArrayOfs + outOfs), Integer.reverseBytes(val));
        }
        else {
            out[outOfs] = (byte)val;
            out[outOfs + 1] = (byte)(val >> 8);
            out[outOfs + 2] = (byte)(val >> 16);
            out[outOfs + 3] = (byte)(val >> 24);
        }
    }
    
    static void b2iBig(final byte[] in, int inOfs, final int[] out, int outOfs, int len) {
        if (inOfs < 0 || in.length - inOfs < len || outOfs < 0 || out.length - outOfs < len / 4) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (ByteArrayAccess.littleEndianUnaligned) {
            for (inOfs += ByteArrayAccess.byteArrayOfs, len += inOfs; inOfs < len; inOfs += 4) {
                out[outOfs++] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(in, (long)inOfs));
            }
        }
        else if (ByteArrayAccess.bigEndian && (inOfs & 0x3) == 0x0) {
            for (inOfs += ByteArrayAccess.byteArrayOfs, len += inOfs; inOfs < len; inOfs += 4) {
                out[outOfs++] = ByteArrayAccess.unsafe.getInt(in, (long)inOfs);
            }
        }
        else {
            for (len += inOfs; inOfs < len; inOfs += 4) {
                out[outOfs++] = ((in[inOfs + 3] & 0xFF) | (in[inOfs + 2] & 0xFF) << 8 | (in[inOfs + 1] & 0xFF) << 16 | in[inOfs] << 24);
            }
        }
    }
    
    static void b2iBig64(final byte[] in, int inOfs, final int[] out) {
        if (inOfs < 0 || in.length - inOfs < 64 || out.length < 16) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (ByteArrayAccess.littleEndianUnaligned) {
            inOfs += ByteArrayAccess.byteArrayOfs;
            out[0] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(in, (long)inOfs));
            out[1] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 4)));
            out[2] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 8)));
            out[3] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 12)));
            out[4] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 16)));
            out[5] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 20)));
            out[6] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 24)));
            out[7] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 28)));
            out[8] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 32)));
            out[9] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 36)));
            out[10] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 40)));
            out[11] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 44)));
            out[12] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 48)));
            out[13] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 52)));
            out[14] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 56)));
            out[15] = Integer.reverseBytes(ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 60)));
        }
        else if (ByteArrayAccess.bigEndian && (inOfs & 0x3) == 0x0) {
            inOfs += ByteArrayAccess.byteArrayOfs;
            out[0] = ByteArrayAccess.unsafe.getInt(in, (long)inOfs);
            out[1] = ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 4));
            out[2] = ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 8));
            out[3] = ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 12));
            out[4] = ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 16));
            out[5] = ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 20));
            out[6] = ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 24));
            out[7] = ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 28));
            out[8] = ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 32));
            out[9] = ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 36));
            out[10] = ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 40));
            out[11] = ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 44));
            out[12] = ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 48));
            out[13] = ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 52));
            out[14] = ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 56));
            out[15] = ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 60));
        }
        else {
            b2iBig(in, inOfs, out, 0, 64);
        }
    }
    
    static void i2bBig(final int[] in, int inOfs, final byte[] out, int outOfs, int len) {
        if (inOfs < 0 || in.length - inOfs < len / 4 || outOfs < 0 || out.length - outOfs < len) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (ByteArrayAccess.littleEndianUnaligned) {
            for (outOfs += ByteArrayAccess.byteArrayOfs, len += outOfs; outOfs < len; outOfs += 4) {
                ByteArrayAccess.unsafe.putInt(out, (long)outOfs, Integer.reverseBytes(in[inOfs++]));
            }
        }
        else if (ByteArrayAccess.bigEndian && (outOfs & 0x3) == 0x0) {
            for (outOfs += ByteArrayAccess.byteArrayOfs, len += outOfs; outOfs < len; outOfs += 4) {
                ByteArrayAccess.unsafe.putInt(out, (long)outOfs, in[inOfs++]);
            }
        }
        else {
            int i;
            for (len += outOfs; outOfs < len; out[outOfs++] = (byte)(i >> 24), out[outOfs++] = (byte)(i >> 16), out[outOfs++] = (byte)(i >> 8), out[outOfs++] = (byte)i) {
                i = in[inOfs++];
            }
        }
    }
    
    static void i2bBig4(final int val, final byte[] out, final int outOfs) {
        if (outOfs < 0 || out.length - outOfs < 4) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (ByteArrayAccess.littleEndianUnaligned) {
            ByteArrayAccess.unsafe.putInt(out, (long)(ByteArrayAccess.byteArrayOfs + outOfs), Integer.reverseBytes(val));
        }
        else if (ByteArrayAccess.bigEndian && (outOfs & 0x3) == 0x0) {
            ByteArrayAccess.unsafe.putInt(out, (long)(ByteArrayAccess.byteArrayOfs + outOfs), val);
        }
        else {
            out[outOfs] = (byte)(val >> 24);
            out[outOfs + 1] = (byte)(val >> 16);
            out[outOfs + 2] = (byte)(val >> 8);
            out[outOfs + 3] = (byte)val;
        }
    }
    
    static void b2lBig(final byte[] in, int inOfs, final long[] out, int outOfs, int len) {
        if (inOfs < 0 || in.length - inOfs < len || outOfs < 0 || out.length - outOfs < len / 8) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (ByteArrayAccess.littleEndianUnaligned) {
            for (inOfs += ByteArrayAccess.byteArrayOfs, len += inOfs; inOfs < len; inOfs += 8) {
                out[outOfs++] = Long.reverseBytes(ByteArrayAccess.unsafe.getLong(in, (long)inOfs));
            }
        }
        else if (ByteArrayAccess.bigEndian && (inOfs & 0x3) == 0x0) {
            for (inOfs += ByteArrayAccess.byteArrayOfs, len += inOfs; inOfs < len; inOfs += 8) {
                out[outOfs++] = ((long)ByteArrayAccess.unsafe.getInt(in, (long)inOfs) << 32 | ((long)ByteArrayAccess.unsafe.getInt(in, (long)(inOfs + 4)) & 0xFFFFFFFFL));
            }
        }
        else {
            int i1;
            int i2;
            for (len += inOfs; inOfs < len; inOfs += 4, i2 = ((in[inOfs + 3] & 0xFF) | (in[inOfs + 2] & 0xFF) << 8 | (in[inOfs + 1] & 0xFF) << 16 | in[inOfs] << 24), out[outOfs++] = ((long)i1 << 32 | ((long)i2 & 0xFFFFFFFFL)), inOfs += 4) {
                i1 = ((in[inOfs + 3] & 0xFF) | (in[inOfs + 2] & 0xFF) << 8 | (in[inOfs + 1] & 0xFF) << 16 | in[inOfs] << 24);
            }
        }
    }
    
    static void b2lBig128(final byte[] in, int inOfs, final long[] out) {
        if (inOfs < 0 || in.length - inOfs < 128 || out.length < 16) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (ByteArrayAccess.littleEndianUnaligned) {
            inOfs += ByteArrayAccess.byteArrayOfs;
            out[0] = Long.reverseBytes(ByteArrayAccess.unsafe.getLong(in, (long)inOfs));
            out[1] = Long.reverseBytes(ByteArrayAccess.unsafe.getLong(in, (long)(inOfs + 8)));
            out[2] = Long.reverseBytes(ByteArrayAccess.unsafe.getLong(in, (long)(inOfs + 16)));
            out[3] = Long.reverseBytes(ByteArrayAccess.unsafe.getLong(in, (long)(inOfs + 24)));
            out[4] = Long.reverseBytes(ByteArrayAccess.unsafe.getLong(in, (long)(inOfs + 32)));
            out[5] = Long.reverseBytes(ByteArrayAccess.unsafe.getLong(in, (long)(inOfs + 40)));
            out[6] = Long.reverseBytes(ByteArrayAccess.unsafe.getLong(in, (long)(inOfs + 48)));
            out[7] = Long.reverseBytes(ByteArrayAccess.unsafe.getLong(in, (long)(inOfs + 56)));
            out[8] = Long.reverseBytes(ByteArrayAccess.unsafe.getLong(in, (long)(inOfs + 64)));
            out[9] = Long.reverseBytes(ByteArrayAccess.unsafe.getLong(in, (long)(inOfs + 72)));
            out[10] = Long.reverseBytes(ByteArrayAccess.unsafe.getLong(in, (long)(inOfs + 80)));
            out[11] = Long.reverseBytes(ByteArrayAccess.unsafe.getLong(in, (long)(inOfs + 88)));
            out[12] = Long.reverseBytes(ByteArrayAccess.unsafe.getLong(in, (long)(inOfs + 96)));
            out[13] = Long.reverseBytes(ByteArrayAccess.unsafe.getLong(in, (long)(inOfs + 104)));
            out[14] = Long.reverseBytes(ByteArrayAccess.unsafe.getLong(in, (long)(inOfs + 112)));
            out[15] = Long.reverseBytes(ByteArrayAccess.unsafe.getLong(in, (long)(inOfs + 120)));
        }
        else {
            b2lBig(in, inOfs, out, 0, 128);
        }
    }
    
    static void l2bBig(final long[] in, int inOfs, final byte[] out, int outOfs, int len) {
        if (inOfs < 0 || in.length - inOfs < len / 8 || outOfs < 0 || out.length - outOfs < len) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (ByteArrayAccess.littleEndianUnaligned) {
            for (outOfs += ByteArrayAccess.byteArrayOfs, len += outOfs; outOfs < len; outOfs += 8) {
                ByteArrayAccess.unsafe.putLong(out, (long)outOfs, Long.reverseBytes(in[inOfs++]));
            }
        }
        else {
            long i;
            for (len += outOfs; outOfs < len; out[outOfs++] = (byte)(i >> 56), out[outOfs++] = (byte)(i >> 48), out[outOfs++] = (byte)(i >> 40), out[outOfs++] = (byte)(i >> 32), out[outOfs++] = (byte)(i >> 24), out[outOfs++] = (byte)(i >> 16), out[outOfs++] = (byte)(i >> 8), out[outOfs++] = (byte)i) {
                i = in[inOfs++];
            }
        }
    }
    
    static void b2lLittle(final byte[] in, int inOfs, final long[] out, int outOfs, int len) {
        if (inOfs < 0 || in.length - inOfs < len || outOfs < 0 || out.length - outOfs < len / 8) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (ByteArrayAccess.littleEndianUnaligned) {
            for (inOfs += ByteArrayAccess.byteArrayOfs, len += inOfs; inOfs < len; inOfs += 8) {
                out[outOfs++] = ByteArrayAccess.unsafe.getLong(in, (long)inOfs);
            }
        }
        else {
            for (len += inOfs; inOfs < len; inOfs += 8) {
                out[outOfs++] = (((long)in[inOfs] & 0xFFL) | ((long)in[inOfs + 1] & 0xFFL) << 8 | ((long)in[inOfs + 2] & 0xFFL) << 16 | ((long)in[inOfs + 3] & 0xFFL) << 24 | ((long)in[inOfs + 4] & 0xFFL) << 32 | ((long)in[inOfs + 5] & 0xFFL) << 40 | ((long)in[inOfs + 6] & 0xFFL) << 48 | ((long)in[inOfs + 7] & 0xFFL) << 56);
            }
        }
    }
    
    static void l2bLittle(final long[] in, int inOfs, final byte[] out, int outOfs, int len) {
        if (inOfs < 0 || in.length - inOfs < len / 8 || outOfs < 0 || out.length - outOfs < len) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (ByteArrayAccess.littleEndianUnaligned) {
            for (outOfs += ByteArrayAccess.byteArrayOfs, len += outOfs; outOfs < len; outOfs += 8) {
                ByteArrayAccess.unsafe.putLong(out, (long)outOfs, in[inOfs++]);
            }
        }
        else {
            long i;
            for (len += outOfs; outOfs < len; out[outOfs++] = (byte)i, out[outOfs++] = (byte)(i >> 8), out[outOfs++] = (byte)(i >> 16), out[outOfs++] = (byte)(i >> 24), out[outOfs++] = (byte)(i >> 32), out[outOfs++] = (byte)(i >> 40), out[outOfs++] = (byte)(i >> 48), out[outOfs++] = (byte)(i >> 56)) {
                i = in[inOfs++];
            }
        }
    }
    
    static {
        Object unsafeObj = null;
        try {
            final ClassLoader cLoader = ByteArrayAccess.class.getClassLoader();
            if (!VM.isSystemDomainLoader(cLoader) && !cLoader.getClass().getName().startsWith("sun.misc.Launcher$ExtClassLoader")) {
                throw new SecurityException("Provider must be loaded by ExtClassLoader");
            }
            final Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafeObj = f.get(null);
        }
        catch (final NoSuchFieldException | IllegalAccessException ex) {}
        unsafe = (Unsafe)unsafeObj;
        byteArrayOfs = ByteArrayAccess.unsafe.arrayBaseOffset(byte[].class);
        final boolean scaleOK = ByteArrayAccess.unsafe.arrayIndexScale(byte[].class) == 1 && ByteArrayAccess.unsafe.arrayIndexScale(int[].class) == 4 && ByteArrayAccess.unsafe.arrayIndexScale(long[].class) == 8 && (ByteArrayAccess.byteArrayOfs & 0x3) == 0x0;
        final ByteOrder byteOrder = ByteOrder.nativeOrder();
        littleEndianUnaligned = (scaleOK && unaligned() && byteOrder == ByteOrder.LITTLE_ENDIAN);
        bigEndian = (scaleOK && byteOrder == ByteOrder.BIG_ENDIAN);
    }
}
