package jcifs.util;

import java.io.IOException;
import java.util.Date;

public class Encdec
{
    public static final long MILLISECONDS_BETWEEN_1970_AND_1601 = 11644473600000L;
    public static final long SEC_BETWEEEN_1904_AND_1970 = 2082844800L;
    public static final int TIME_1970_SEC_32BE = 1;
    public static final int TIME_1970_SEC_32LE = 2;
    public static final int TIME_1904_SEC_32BE = 3;
    public static final int TIME_1904_SEC_32LE = 4;
    public static final int TIME_1601_NANOS_64LE = 5;
    public static final int TIME_1601_NANOS_64BE = 6;
    public static final int TIME_1970_MILLIS_64BE = 7;
    public static final int TIME_1970_MILLIS_64LE = 8;
    
    public static int enc_uint16be(final short s, final byte[] dst, int di) {
        dst[di++] = (byte)(s >> 8 & 0xFF);
        dst[di] = (byte)(s & 0xFF);
        return 2;
    }
    
    public static int enc_uint32be(final int i, final byte[] dst, int di) {
        dst[di++] = (byte)(i >> 24 & 0xFF);
        dst[di++] = (byte)(i >> 16 & 0xFF);
        dst[di++] = (byte)(i >> 8 & 0xFF);
        dst[di] = (byte)(i & 0xFF);
        return 4;
    }
    
    public static int enc_uint16le(final short s, final byte[] dst, int di) {
        dst[di++] = (byte)(s & 0xFF);
        dst[di] = (byte)(s >> 8 & 0xFF);
        return 2;
    }
    
    public static int enc_uint32le(final int i, final byte[] dst, int di) {
        dst[di++] = (byte)(i & 0xFF);
        dst[di++] = (byte)(i >> 8 & 0xFF);
        dst[di++] = (byte)(i >> 16 & 0xFF);
        dst[di] = (byte)(i >> 24 & 0xFF);
        return 4;
    }
    
    public static short dec_uint16be(final byte[] src, final int si) {
        return (short)((src[si] & 0xFF) << 8 | (src[si + 1] & 0xFF));
    }
    
    public static int dec_uint32be(final byte[] src, final int si) {
        return (src[si] & 0xFF) << 24 | (src[si + 1] & 0xFF) << 16 | (src[si + 2] & 0xFF) << 8 | (src[si + 3] & 0xFF);
    }
    
    public static short dec_uint16le(final byte[] src, final int si) {
        return (short)((src[si] & 0xFF) | (src[si + 1] & 0xFF) << 8);
    }
    
    public static int dec_uint32le(final byte[] src, final int si) {
        return (src[si] & 0xFF) | (src[si + 1] & 0xFF) << 8 | (src[si + 2] & 0xFF) << 16 | (src[si + 3] & 0xFF) << 24;
    }
    
    public static int enc_uint64be(final long l, final byte[] dst, final int di) {
        enc_uint32be((int)(l & 0xFFFFFFFFL), dst, di + 4);
        enc_uint32be((int)(l >> 32 & 0xFFFFFFFFL), dst, di);
        return 8;
    }
    
    public static int enc_uint64le(final long l, final byte[] dst, final int di) {
        enc_uint32le((int)(l & 0xFFFFFFFFL), dst, di);
        enc_uint32le((int)(l >> 32 & 0xFFFFFFFFL), dst, di + 4);
        return 8;
    }
    
    public static long dec_uint64be(final byte[] src, final int si) {
        long l = (long)dec_uint32be(src, si) & 0xFFFFFFFFL;
        l <<= 32;
        l |= ((long)dec_uint32be(src, si + 4) & 0xFFFFFFFFL);
        return l;
    }
    
    public static long dec_uint64le(final byte[] src, final int si) {
        long l = (long)dec_uint32le(src, si + 4) & 0xFFFFFFFFL;
        l <<= 32;
        l |= ((long)dec_uint32le(src, si) & 0xFFFFFFFFL);
        return l;
    }
    
    public static int enc_floatle(final float f, final byte[] dst, final int di) {
        return enc_uint32le(Float.floatToIntBits(f), dst, di);
    }
    
    public static int enc_floatbe(final float f, final byte[] dst, final int di) {
        return enc_uint32be(Float.floatToIntBits(f), dst, di);
    }
    
    public static float dec_floatle(final byte[] src, final int si) {
        return Float.intBitsToFloat(dec_uint32le(src, si));
    }
    
    public static float dec_floatbe(final byte[] src, final int si) {
        return Float.intBitsToFloat(dec_uint32be(src, si));
    }
    
    public static int enc_doublele(final double d, final byte[] dst, final int di) {
        return enc_uint64le(Double.doubleToLongBits(d), dst, di);
    }
    
    public static int enc_doublebe(final double d, final byte[] dst, final int di) {
        return enc_uint64be(Double.doubleToLongBits(d), dst, di);
    }
    
    public static double dec_doublele(final byte[] src, final int si) {
        return Double.longBitsToDouble(dec_uint64le(src, si));
    }
    
    public static double dec_doublebe(final byte[] src, final int si) {
        return Double.longBitsToDouble(dec_uint64be(src, si));
    }
    
    public static int enc_time(final Date date, final byte[] dst, final int di, final int enc) {
        switch (enc) {
            case 1: {
                return enc_uint32be((int)(date.getTime() / 1000L), dst, di);
            }
            case 2: {
                return enc_uint32le((int)(date.getTime() / 1000L), dst, di);
            }
            case 3: {
                return enc_uint32be((int)(date.getTime() / 1000L + 2082844800L & -1L), dst, di);
            }
            case 4: {
                return enc_uint32le((int)(date.getTime() / 1000L + 2082844800L & -1L), dst, di);
            }
            case 6: {
                final long t = (date.getTime() + 11644473600000L) * 10000L;
                return enc_uint64be(t, dst, di);
            }
            case 5: {
                final long t = (date.getTime() + 11644473600000L) * 10000L;
                return enc_uint64le(t, dst, di);
            }
            case 7: {
                return enc_uint64be(date.getTime(), dst, di);
            }
            case 8: {
                return enc_uint64le(date.getTime(), dst, di);
            }
            default: {
                throw new IllegalArgumentException("Unsupported time encoding");
            }
        }
    }
    
    public static Date dec_time(final byte[] src, final int si, final int enc) {
        switch (enc) {
            case 1: {
                return new Date(dec_uint32be(src, si) * 1000L);
            }
            case 2: {
                return new Date(dec_uint32le(src, si) * 1000L);
            }
            case 3: {
                return new Date((((long)dec_uint32be(src, si) & 0xFFFFFFFFL) - 2082844800L) * 1000L);
            }
            case 4: {
                return new Date((((long)dec_uint32le(src, si) & 0xFFFFFFFFL) - 2082844800L) * 1000L);
            }
            case 6: {
                final long t = dec_uint64be(src, si);
                return new Date(t / 10000L - 11644473600000L);
            }
            case 5: {
                final long t = dec_uint64le(src, si);
                return new Date(t / 10000L - 11644473600000L);
            }
            case 7: {
                return new Date(dec_uint64be(src, si));
            }
            case 8: {
                return new Date(dec_uint64le(src, si));
            }
            default: {
                throw new IllegalArgumentException("Unsupported time encoding");
            }
        }
    }
    
    public static int enc_utf8(final String str, final byte[] dst, int di, final int dlim) throws IOException {
        final int start = di;
        for (int strlen = str.length(), i = 0; di < dlim && i < strlen; ++i) {
            final int ch = str.charAt(i);
            if (ch >= 1 && ch <= 127) {
                dst[di++] = (byte)ch;
            }
            else if (ch > 2047) {
                if (dlim - di < 3) {
                    break;
                }
                dst[di++] = (byte)(0xE0 | (ch >> 12 & 0xF));
                dst[di++] = (byte)(0x80 | (ch >> 6 & 0x3F));
                dst[di++] = (byte)(0x80 | (ch >> 0 & 0x3F));
            }
            else {
                if (dlim - di < 2) {
                    break;
                }
                dst[di++] = (byte)(0xC0 | (ch >> 6 & 0x1F));
                dst[di++] = (byte)(0x80 | (ch >> 0 & 0x3F));
            }
        }
        return di - start;
    }
    
    public static String dec_utf8(final byte[] src, int si, final int slim) throws IOException {
        final char[] uni = new char[slim - si];
        int ui = 0;
        int ch;
        while (si < slim && (ch = (src[si++] & 0xFF)) != 0) {
            if (ch < 128) {
                uni[ui] = (char)ch;
            }
            else if ((ch & 0xE0) == 0xC0) {
                if (slim - si < 2) {
                    break;
                }
                uni[ui] = (char)((ch & 0x1F) << 6);
                ch = (src[si++] & 0xFF);
                final char[] array = uni;
                final int n = ui;
                array[n] |= (char)(ch & 0x3F);
                if ((ch & 0xC0) != 0x80 || uni[ui] < '\u0080') {
                    throw new IOException("Invalid UTF-8 sequence");
                }
            }
            else {
                if ((ch & 0xF0) != 0xE0) {
                    throw new IOException("Unsupported UTF-8 sequence");
                }
                if (slim - si < 3) {
                    break;
                }
                uni[ui] = (char)((ch & 0xF) << 12);
                ch = (src[si++] & 0xFF);
                if ((ch & 0xC0) != 0x80) {
                    throw new IOException("Invalid UTF-8 sequence");
                }
                final char[] array2 = uni;
                final int n2 = ui;
                array2[n2] |= (char)((ch & 0x3F) << 6);
                ch = (src[si++] & 0xFF);
                final char[] array3 = uni;
                final int n3 = ui;
                array3[n3] |= (char)(ch & 0x3F);
                if ((ch & 0xC0) != 0x80 || uni[ui] < '\u0800') {
                    throw new IOException("Invalid UTF-8 sequence");
                }
            }
            ++ui;
        }
        return new String(uni, 0, ui);
    }
    
    public static String dec_ucs2le(final byte[] src, int si, final int slim, final char[] buf) throws IOException {
        int bi = 0;
        while (si + 1 < slim) {
            buf[bi] = (char)dec_uint16le(src, si);
            if (buf[bi] == '\0') {
                break;
            }
            ++bi;
            si += 2;
        }
        return new String(buf, 0, bi);
    }
}
