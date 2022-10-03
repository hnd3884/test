package org.apache.lucene.util;

import java.util.Iterator;
import java.util.Properties;
import java.io.DataInputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.util.Arrays;
import java.math.BigInteger;

public abstract class StringHelper
{
    public static final int GOOD_FAST_HASH_SEED;
    private static BigInteger nextId;
    private static final BigInteger mask128;
    private static final Object idLock;
    public static final int ID_LENGTH = 16;
    
    public static int bytesDifference(final BytesRef left, final BytesRef right) {
        final int len = (left.length < right.length) ? left.length : right.length;
        final byte[] bytesLeft = left.bytes;
        final int offLeft = left.offset;
        final byte[] bytesRight = right.bytes;
        final int offRight = right.offset;
        for (int i = 0; i < len; ++i) {
            if (bytesLeft[i + offLeft] != bytesRight[i + offRight]) {
                return i;
            }
        }
        return len;
    }
    
    public static int sortKeyLength(final BytesRef priorTerm, final BytesRef currentTerm) {
        final int currentTermOffset = currentTerm.offset;
        final int priorTermOffset = priorTerm.offset;
        for (int limit = Math.min(priorTerm.length, currentTerm.length), i = 0; i < limit; ++i) {
            if (priorTerm.bytes[priorTermOffset + i] != currentTerm.bytes[currentTermOffset + i]) {
                return i + 1;
            }
        }
        return Math.min(1 + priorTerm.length, currentTerm.length);
    }
    
    private StringHelper() {
    }
    
    public static boolean startsWith(final byte[] ref, final BytesRef prefix) {
        if (ref.length < prefix.length) {
            return false;
        }
        for (int i = 0; i < prefix.length; ++i) {
            if (ref[i] != prefix.bytes[prefix.offset + i]) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean startsWith(final BytesRef ref, final BytesRef prefix) {
        return sliceEquals(ref, prefix, 0);
    }
    
    public static boolean endsWith(final BytesRef ref, final BytesRef suffix) {
        return sliceEquals(ref, suffix, ref.length - suffix.length);
    }
    
    private static boolean sliceEquals(final BytesRef sliceToTest, final BytesRef other, final int pos) {
        if (pos < 0 || sliceToTest.length - pos < other.length) {
            return false;
        }
        int i = sliceToTest.offset + pos;
        int j = other.offset;
        final int k = other.offset + other.length;
        while (j < k) {
            if (sliceToTest.bytes[i++] != other.bytes[j++]) {
                return false;
            }
        }
        return true;
    }
    
    public static int murmurhash3_x86_32(final byte[] data, final int offset, final int len, final int seed) {
        final int c1 = -862048943;
        final int c2 = 461845907;
        int h1 = seed;
        final int roundedEnd = offset + (len & 0xFFFFFFFC);
        for (int i = offset; i < roundedEnd; i += 4) {
            int k1 = (data[i] & 0xFF) | (data[i + 1] & 0xFF) << 8 | (data[i + 2] & 0xFF) << 16 | data[i + 3] << 24;
            k1 *= -862048943;
            k1 = Integer.rotateLeft(k1, 15);
            k1 *= 461845907;
            h1 ^= k1;
            h1 = Integer.rotateLeft(h1, 13);
            h1 = h1 * 5 - 430675100;
        }
        int k2 = 0;
        switch (len & 0x3) {
            case 3: {
                k2 = (data[roundedEnd + 2] & 0xFF) << 16;
            }
            case 2: {
                k2 |= (data[roundedEnd + 1] & 0xFF) << 8;
            }
            case 1: {
                k2 |= (data[roundedEnd] & 0xFF);
                k2 *= -862048943;
                k2 = Integer.rotateLeft(k2, 15);
                k2 *= 461845907;
                h1 ^= k2;
                break;
            }
        }
        h1 ^= len;
        h1 ^= h1 >>> 16;
        h1 *= -2048144789;
        h1 ^= h1 >>> 13;
        h1 *= -1028477387;
        h1 ^= h1 >>> 16;
        return h1;
    }
    
    public static int murmurhash3_x86_32(final BytesRef bytes, final int seed) {
        return murmurhash3_x86_32(bytes.bytes, bytes.offset, bytes.length, seed);
    }
    
    public static byte[] randomId() {
        final byte[] bits;
        synchronized (StringHelper.idLock) {
            bits = StringHelper.nextId.toByteArray();
            StringHelper.nextId = StringHelper.nextId.add(BigInteger.ONE).and(StringHelper.mask128);
        }
        if (bits.length <= 16) {
            final byte[] result = new byte[16];
            System.arraycopy(bits, 0, result, result.length - bits.length, bits.length);
            return result;
        }
        assert bits.length == 17;
        assert bits[0] == 0;
        return Arrays.copyOfRange(bits, 1, bits.length);
    }
    
    public static String idToString(final byte[] id) {
        if (id == null) {
            return "(null)";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(new BigInteger(1, id).toString(36));
        if (id.length != 16) {
            sb.append(" (INVALID FORMAT)");
        }
        return sb.toString();
    }
    
    public static BytesRef intsRefToBytesRef(final IntsRef ints) {
        final byte[] bytes = new byte[ints.length];
        for (int i = 0; i < ints.length; ++i) {
            final int x = ints.ints[ints.offset + i];
            if (x < 0 || x > 255) {
                throw new IllegalArgumentException("int at pos=" + i + " with value=" + x + " is out-of-bounds for byte");
            }
            bytes[i] = (byte)x;
        }
        return new BytesRef(bytes);
    }
    
    static {
        final String prop = System.getProperty("tests.seed");
        if (prop != null) {
            GOOD_FAST_HASH_SEED = prop.hashCode();
        }
        else {
            GOOD_FAST_HASH_SEED = (int)System.currentTimeMillis();
        }
        idLock = new Object();
        final byte[] maskBytes128 = new byte[16];
        Arrays.fill(maskBytes128, (byte)(-1));
        mask128 = new BigInteger(1, maskBytes128);
        String prop2 = System.getProperty("tests.seed");
        long x2;
        long x0;
        if (prop2 != null) {
            if (prop2.length() > 8) {
                prop2 = prop2.substring(prop2.length() - 8);
            }
            x0 = (x2 = Long.parseLong(prop2, 16));
        }
        else {
            try (final DataInputStream is = new DataInputStream(Files.newInputStream(Paths.get("/dev/urandom", new String[0]), new OpenOption[0]))) {
                x0 = is.readLong();
                x2 = is.readLong();
            }
            catch (final Exception unavailable) {
                x0 = System.nanoTime();
                x2 = StringHelper.class.hashCode() << 32;
                final StringBuilder sb = new StringBuilder();
                try {
                    final Properties p = System.getProperties();
                    for (final String s : p.stringPropertyNames()) {
                        sb.append(s);
                        sb.append(p.getProperty(s));
                    }
                    x2 |= sb.toString().hashCode();
                }
                catch (final SecurityException notallowed) {
                    x2 |= StringBuffer.class.hashCode();
                }
            }
        }
        for (int i = 0; i < 10; ++i) {
            long s2 = x0;
            final long s3 = x0 = x2;
            s2 ^= s2 << 23;
            x2 = (s2 ^ s3 ^ s2 >>> 17 ^ s3 >>> 26);
        }
        final byte[] maskBytes129 = new byte[8];
        Arrays.fill(maskBytes129, (byte)(-1));
        final BigInteger mask129 = new BigInteger(1, maskBytes129);
        final BigInteger unsignedX0 = BigInteger.valueOf(x0).and(mask129);
        final BigInteger unsignedX2 = BigInteger.valueOf(x2).and(mask129);
        StringHelper.nextId = unsignedX0.shiftLeft(64).or(unsignedX2);
    }
}
