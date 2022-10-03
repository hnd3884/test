package com.sun.java.util.jar.pack;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

class AdaptiveCoding implements CodingMethod
{
    CodingMethod headCoding;
    int headLength;
    CodingMethod tailCoding;
    public static final int KX_MIN = 0;
    public static final int KX_MAX = 3;
    public static final int KX_LG2BASE = 4;
    public static final int KX_BASE = 16;
    public static final int KB_MIN = 0;
    public static final int KB_MAX = 255;
    public static final int KB_OFFSET = 1;
    public static final int KB_DEFAULT = 3;
    
    public AdaptiveCoding(final int headLength, final CodingMethod headCoding, final CodingMethod tailCoding) {
        assert isCodableLength(headLength);
        this.headLength = headLength;
        this.headCoding = headCoding;
        this.tailCoding = tailCoding;
    }
    
    public void setHeadCoding(final CodingMethod headCoding) {
        this.headCoding = headCoding;
    }
    
    public void setHeadLength(final int headLength) {
        assert isCodableLength(headLength);
        this.headLength = headLength;
    }
    
    public void setTailCoding(final CodingMethod tailCoding) {
        this.tailCoding = tailCoding;
    }
    
    public boolean isTrivial() {
        return this.headCoding == this.tailCoding;
    }
    
    @Override
    public void writeArrayTo(final OutputStream outputStream, final int[] array, final int n, final int n2) throws IOException {
        writeArray(this, outputStream, array, n, n2);
    }
    
    private static void writeArray(AdaptiveCoding adaptiveCoding, final OutputStream outputStream, final int[] array, int n, final int n2) throws IOException {
        while (true) {
            final int n3 = n + adaptiveCoding.headLength;
            assert n3 <= n2;
            adaptiveCoding.headCoding.writeArrayTo(outputStream, array, n, n3);
            n = n3;
            if (!(adaptiveCoding.tailCoding instanceof AdaptiveCoding)) {
                adaptiveCoding.tailCoding.writeArrayTo(outputStream, array, n, n2);
                return;
            }
            adaptiveCoding = (AdaptiveCoding)adaptiveCoding.tailCoding;
        }
    }
    
    @Override
    public void readArrayFrom(final InputStream inputStream, final int[] array, final int n, final int n2) throws IOException {
        readArray(this, inputStream, array, n, n2);
    }
    
    private static void readArray(AdaptiveCoding adaptiveCoding, final InputStream inputStream, final int[] array, int n, final int n2) throws IOException {
        while (true) {
            final int n3 = n + adaptiveCoding.headLength;
            assert n3 <= n2;
            adaptiveCoding.headCoding.readArrayFrom(inputStream, array, n, n3);
            n = n3;
            if (!(adaptiveCoding.tailCoding instanceof AdaptiveCoding)) {
                adaptiveCoding.tailCoding.readArrayFrom(inputStream, array, n, n2);
                return;
            }
            adaptiveCoding = (AdaptiveCoding)adaptiveCoding.tailCoding;
        }
    }
    
    static int getKXOf(int n) {
        for (int i = 0; i <= 3; ++i) {
            if ((n - 1 & 0xFFFFFF00) == 0x0) {
                return i;
            }
            n >>>= 4;
        }
        return -1;
    }
    
    static int getKBOf(int n) {
        final int kxOf = getKXOf(n);
        if (kxOf < 0) {
            return -1;
        }
        n >>>= kxOf * 4;
        return n - 1;
    }
    
    static int decodeK(final int n, final int n2) {
        assert 0 <= n && n <= 3;
        assert 0 <= n2 && n2 <= 255;
        return n2 + 1 << n * 4;
    }
    
    static int getNextK(final int n) {
        if (n <= 0) {
            return 1;
        }
        int kxOf = getKXOf(n);
        if (kxOf < 0) {
            return Integer.MAX_VALUE;
        }
        final int n2 = 1 << kxOf * 4;
        final int n3 = 255 << kxOf * 4;
        final int n4 = n + n2 & ~(n2 - 1);
        if ((n4 - n2 & ~n3) == 0x0) {
            assert getKXOf(n4) == kxOf;
            return n4;
        }
        else {
            if (kxOf == 3) {
                return Integer.MAX_VALUE;
            }
            ++kxOf;
            final int n5 = (n4 | (n3 & ~(255 << kxOf * 4))) + n2;
            assert getKXOf(n5) == kxOf;
            return n5;
        }
    }
    
    public static boolean isCodableLength(final int n) {
        final int kxOf = getKXOf(n);
        return kxOf >= 0 && (n - (1 << kxOf * 4) & ~(255 << kxOf * 4)) == 0x0;
    }
    
    @Override
    public byte[] getMetaCoding(final Coding coding) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(10);
        try {
            makeMetaCoding(this, coding, byteArrayOutputStream);
        }
        catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
        return byteArrayOutputStream.toByteArray();
    }
    
    private static void makeMetaCoding(AdaptiveCoding adaptiveCoding, final Coding coding, final ByteArrayOutputStream byteArrayOutputStream) throws IOException {
        while (true) {
            final CodingMethod headCoding = adaptiveCoding.headCoding;
            final int headLength = adaptiveCoding.headLength;
            final CodingMethod tailCoding = adaptiveCoding.tailCoding;
            final int n = headLength;
            assert isCodableLength(n);
            final int n2 = (headCoding == coding) ? 1 : 0;
            int n3 = (tailCoding == coding) ? 1 : 0;
            if (n2 + n3 > 1) {
                n3 = 0;
            }
            final int n4 = 1 * n2 + 2 * n3;
            assert n4 < 3;
            final int kxOf = getKXOf(n);
            final int kbOf = getKBOf(n);
            assert decodeK(kxOf, kbOf) == n;
            final int n5 = (kbOf != 3) ? 1 : 0;
            byteArrayOutputStream.write(117 + kxOf + 4 * n5 + 8 * n4);
            if (n5 != 0) {
                byteArrayOutputStream.write(kbOf);
            }
            if (n2 == 0) {
                byteArrayOutputStream.write(headCoding.getMetaCoding(coding));
            }
            if (!(tailCoding instanceof AdaptiveCoding)) {
                if (n3 == 0) {
                    byteArrayOutputStream.write(tailCoding.getMetaCoding(coding));
                }
                return;
            }
            adaptiveCoding = (AdaptiveCoding)tailCoding;
        }
    }
    
    public static int parseMetaCoding(final byte[] array, int n, final Coding coding, final CodingMethod[] array2) {
        int n2 = array[n++] & 0xFF;
        if (n2 < 117 || n2 >= 141) {
            return n - 1;
        }
        AdaptiveCoding adaptiveCoding = null;
        int i = 1;
        while (i != 0) {
            i = 0;
            assert n2 >= 117;
            n2 -= 117;
            final int n3 = n2 % 4;
            final int n4 = n2 / 4 % 2;
            final int n5 = n2 / 8;
            assert n5 < 3;
            final int n6 = n5 & 0x1;
            final int n7 = n5 & 0x2;
            final CodingMethod[] array3 = { coding };
            final CodingMethod[] array4 = { coding };
            int n8 = 3;
            if (n4 != 0) {
                n8 = (array[n++] & 0xFF);
            }
            if (n6 == 0) {
                n = BandStructure.parseMetaCoding(array, n, coding, array3);
            }
            if (n7 == 0 && (n2 = (array[n] & 0xFF)) >= 117 && n2 < 141) {
                ++n;
                i = 1;
            }
            else if (n7 == 0) {
                n = BandStructure.parseMetaCoding(array, n, coding, array4);
            }
            final AdaptiveCoding tailCoding = new AdaptiveCoding(decodeK(n3, n8), array3[0], array4[0]);
            if (adaptiveCoding == null) {
                array2[0] = tailCoding;
            }
            else {
                adaptiveCoding.tailCoding = tailCoding;
            }
            adaptiveCoding = tailCoding;
        }
        return n;
    }
    
    private String keyString(final CodingMethod codingMethod) {
        if (codingMethod instanceof Coding) {
            return ((Coding)codingMethod).keyString();
        }
        return codingMethod.toString();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(20);
        AdaptiveCoding adaptiveCoding = this;
        sb.append("run(");
        while (true) {
            sb.append(adaptiveCoding.headLength).append("*");
            sb.append(this.keyString(adaptiveCoding.headCoding));
            if (!(adaptiveCoding.tailCoding instanceof AdaptiveCoding)) {
                break;
            }
            adaptiveCoding = (AdaptiveCoding)adaptiveCoding.tailCoding;
            sb.append(" ");
        }
        sb.append(" **").append(this.keyString(adaptiveCoding.tailCoding));
        sb.append(")");
        return sb.toString();
    }
}
