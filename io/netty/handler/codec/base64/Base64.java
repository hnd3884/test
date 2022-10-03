package io.netty.handler.codec.base64;

import io.netty.util.internal.PlatformDependent;
import io.netty.util.ByteProcessor;
import java.nio.ByteOrder;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBuf;
import io.netty.util.internal.ObjectUtil;

public final class Base64
{
    private static final int MAX_LINE_LENGTH = 76;
    private static final byte EQUALS_SIGN = 61;
    private static final byte NEW_LINE = 10;
    private static final byte WHITE_SPACE_ENC = -5;
    private static final byte EQUALS_SIGN_ENC = -1;
    
    private static byte[] alphabet(final Base64Dialect dialect) {
        return ObjectUtil.checkNotNull(dialect, "dialect").alphabet;
    }
    
    private static byte[] decodabet(final Base64Dialect dialect) {
        return ObjectUtil.checkNotNull(dialect, "dialect").decodabet;
    }
    
    private static boolean breakLines(final Base64Dialect dialect) {
        return ObjectUtil.checkNotNull(dialect, "dialect").breakLinesByDefault;
    }
    
    public static ByteBuf encode(final ByteBuf src) {
        return encode(src, Base64Dialect.STANDARD);
    }
    
    public static ByteBuf encode(final ByteBuf src, final Base64Dialect dialect) {
        return encode(src, breakLines(dialect), dialect);
    }
    
    public static ByteBuf encode(final ByteBuf src, final boolean breakLines) {
        return encode(src, breakLines, Base64Dialect.STANDARD);
    }
    
    public static ByteBuf encode(final ByteBuf src, final boolean breakLines, final Base64Dialect dialect) {
        ObjectUtil.checkNotNull(src, "src");
        final ByteBuf dest = encode(src, src.readerIndex(), src.readableBytes(), breakLines, dialect);
        src.readerIndex(src.writerIndex());
        return dest;
    }
    
    public static ByteBuf encode(final ByteBuf src, final int off, final int len) {
        return encode(src, off, len, Base64Dialect.STANDARD);
    }
    
    public static ByteBuf encode(final ByteBuf src, final int off, final int len, final Base64Dialect dialect) {
        return encode(src, off, len, breakLines(dialect), dialect);
    }
    
    public static ByteBuf encode(final ByteBuf src, final int off, final int len, final boolean breakLines) {
        return encode(src, off, len, breakLines, Base64Dialect.STANDARD);
    }
    
    public static ByteBuf encode(final ByteBuf src, final int off, final int len, final boolean breakLines, final Base64Dialect dialect) {
        return encode(src, off, len, breakLines, dialect, src.alloc());
    }
    
    public static ByteBuf encode(final ByteBuf src, final int off, final int len, final boolean breakLines, final Base64Dialect dialect, final ByteBufAllocator allocator) {
        ObjectUtil.checkNotNull(src, "src");
        ObjectUtil.checkNotNull(dialect, "dialect");
        final ByteBuf dest = allocator.buffer(encodedBufferSize(len, breakLines)).order(src.order());
        final byte[] alphabet = alphabet(dialect);
        int d = 0;
        int e = 0;
        final int len2 = len - 2;
        int lineLength = 0;
        while (d < len2) {
            encode3to4(src, d + off, 3, dest, e, alphabet);
            lineLength += 4;
            if (breakLines && lineLength == 76) {
                dest.setByte(e + 4, 10);
                ++e;
                lineLength = 0;
            }
            d += 3;
            e += 4;
        }
        if (d < len) {
            encode3to4(src, d + off, len - d, dest, e, alphabet);
            e += 4;
        }
        if (e > 1 && dest.getByte(e - 1) == 10) {
            --e;
        }
        return dest.slice(0, e);
    }
    
    private static void encode3to4(final ByteBuf src, final int srcOffset, final int numSigBytes, final ByteBuf dest, final int destOffset, final byte[] alphabet) {
        if (src.order() == ByteOrder.BIG_ENDIAN) {
            int inBuff = 0;
            switch (numSigBytes) {
                case 1: {
                    inBuff = toInt(src.getByte(srcOffset));
                    break;
                }
                case 2: {
                    inBuff = toIntBE(src.getShort(srcOffset));
                    break;
                }
                default: {
                    inBuff = ((numSigBytes <= 0) ? 0 : toIntBE(src.getMedium(srcOffset)));
                    break;
                }
            }
            encode3to4BigEndian(inBuff, numSigBytes, dest, destOffset, alphabet);
        }
        else {
            int inBuff = 0;
            switch (numSigBytes) {
                case 1: {
                    inBuff = toInt(src.getByte(srcOffset));
                    break;
                }
                case 2: {
                    inBuff = toIntLE(src.getShort(srcOffset));
                    break;
                }
                default: {
                    inBuff = ((numSigBytes <= 0) ? 0 : toIntLE(src.getMedium(srcOffset)));
                    break;
                }
            }
            encode3to4LittleEndian(inBuff, numSigBytes, dest, destOffset, alphabet);
        }
    }
    
    static int encodedBufferSize(final int len, final boolean breakLines) {
        final long len2 = ((long)len << 2) / 3L;
        long ret = len2 + 3L & 0xFFFFFFFFFFFFFFFCL;
        if (breakLines) {
            ret += len2 / 76L;
        }
        return (ret < 2147483647L) ? ((int)ret) : Integer.MAX_VALUE;
    }
    
    private static int toInt(final byte value) {
        return (value & 0xFF) << 16;
    }
    
    private static int toIntBE(final short value) {
        return (value & 0xFF00) << 8 | (value & 0xFF) << 8;
    }
    
    private static int toIntLE(final short value) {
        return (value & 0xFF) << 16 | (value & 0xFF00);
    }
    
    private static int toIntBE(final int mediumValue) {
        return (mediumValue & 0xFF0000) | (mediumValue & 0xFF00) | (mediumValue & 0xFF);
    }
    
    private static int toIntLE(final int mediumValue) {
        return (mediumValue & 0xFF) << 16 | (mediumValue & 0xFF00) | (mediumValue & 0xFF0000) >>> 16;
    }
    
    private static void encode3to4BigEndian(final int inBuff, final int numSigBytes, final ByteBuf dest, final int destOffset, final byte[] alphabet) {
        switch (numSigBytes) {
            case 3: {
                dest.setInt(destOffset, alphabet[inBuff >>> 18] << 24 | alphabet[inBuff >>> 12 & 0x3F] << 16 | alphabet[inBuff >>> 6 & 0x3F] << 8 | alphabet[inBuff & 0x3F]);
                break;
            }
            case 2: {
                dest.setInt(destOffset, alphabet[inBuff >>> 18] << 24 | alphabet[inBuff >>> 12 & 0x3F] << 16 | alphabet[inBuff >>> 6 & 0x3F] << 8 | 0x3D);
                break;
            }
            case 1: {
                dest.setInt(destOffset, alphabet[inBuff >>> 18] << 24 | alphabet[inBuff >>> 12 & 0x3F] << 16 | 0x3D00 | 0x3D);
                break;
            }
        }
    }
    
    private static void encode3to4LittleEndian(final int inBuff, final int numSigBytes, final ByteBuf dest, final int destOffset, final byte[] alphabet) {
        switch (numSigBytes) {
            case 3: {
                dest.setInt(destOffset, alphabet[inBuff >>> 18] | alphabet[inBuff >>> 12 & 0x3F] << 8 | alphabet[inBuff >>> 6 & 0x3F] << 16 | alphabet[inBuff & 0x3F] << 24);
                break;
            }
            case 2: {
                dest.setInt(destOffset, alphabet[inBuff >>> 18] | alphabet[inBuff >>> 12 & 0x3F] << 8 | alphabet[inBuff >>> 6 & 0x3F] << 16 | 0x3D000000);
                break;
            }
            case 1: {
                dest.setInt(destOffset, alphabet[inBuff >>> 18] | alphabet[inBuff >>> 12 & 0x3F] << 8 | 0x3D0000 | 0x3D000000);
                break;
            }
        }
    }
    
    public static ByteBuf decode(final ByteBuf src) {
        return decode(src, Base64Dialect.STANDARD);
    }
    
    public static ByteBuf decode(final ByteBuf src, final Base64Dialect dialect) {
        ObjectUtil.checkNotNull(src, "src");
        final ByteBuf dest = decode(src, src.readerIndex(), src.readableBytes(), dialect);
        src.readerIndex(src.writerIndex());
        return dest;
    }
    
    public static ByteBuf decode(final ByteBuf src, final int off, final int len) {
        return decode(src, off, len, Base64Dialect.STANDARD);
    }
    
    public static ByteBuf decode(final ByteBuf src, final int off, final int len, final Base64Dialect dialect) {
        return decode(src, off, len, dialect, src.alloc());
    }
    
    public static ByteBuf decode(final ByteBuf src, final int off, final int len, final Base64Dialect dialect, final ByteBufAllocator allocator) {
        ObjectUtil.checkNotNull(src, "src");
        ObjectUtil.checkNotNull(dialect, "dialect");
        return new Decoder().decode(src, off, len, allocator, dialect);
    }
    
    static int decodedBufferSize(final int len) {
        return len - (len >>> 2);
    }
    
    private Base64() {
    }
    
    private static final class Decoder implements ByteProcessor
    {
        private final byte[] b4;
        private int b4Posn;
        private byte[] decodabet;
        private int outBuffPosn;
        private ByteBuf dest;
        
        private Decoder() {
            this.b4 = new byte[4];
        }
        
        ByteBuf decode(final ByteBuf src, final int off, final int len, final ByteBufAllocator allocator, final Base64Dialect dialect) {
            this.dest = allocator.buffer(Base64.decodedBufferSize(len)).order(src.order());
            this.decodabet = decodabet(dialect);
            try {
                src.forEachByte(off, len, this);
                return this.dest.slice(0, this.outBuffPosn);
            }
            catch (final Throwable cause) {
                this.dest.release();
                PlatformDependent.throwException(cause);
                return null;
            }
        }
        
        @Override
        public boolean process(final byte value) throws Exception {
            if (value > 0) {
                final byte sbiDecode = this.decodabet[value];
                if (sbiDecode >= -5) {
                    if (sbiDecode >= -1) {
                        this.b4[this.b4Posn++] = value;
                        if (this.b4Posn > 3) {
                            this.outBuffPosn += decode4to3(this.b4, this.dest, this.outBuffPosn, this.decodabet);
                            this.b4Posn = 0;
                            return value != 61;
                        }
                    }
                    return true;
                }
            }
            throw new IllegalArgumentException("invalid Base64 input character: " + (short)(value & 0xFF) + " (decimal)");
        }
        
        private static int decode4to3(final byte[] src, final ByteBuf dest, final int destOffset, final byte[] decodabet) {
            final byte src2 = src[0];
            final byte src3 = src[1];
            final byte src4 = src[2];
            if (src4 == 61) {
                int decodedValue;
                try {
                    decodedValue = ((decodabet[src2] & 0xFF) << 2 | (decodabet[src3] & 0xFF) >>> 4);
                }
                catch (final IndexOutOfBoundsException ignored) {
                    throw new IllegalArgumentException("not encoded in Base64");
                }
                dest.setByte(destOffset, decodedValue);
                return 1;
            }
            final byte src5 = src[3];
            if (src5 == 61) {
                final byte b1 = decodabet[src3];
                int decodedValue;
                try {
                    if (dest.order() == ByteOrder.BIG_ENDIAN) {
                        decodedValue = (((decodabet[src2] & 0x3F) << 2 | (b1 & 0xF0) >> 4) << 8 | (b1 & 0xF) << 4 | (decodabet[src4] & 0xFC) >>> 2);
                    }
                    else {
                        decodedValue = ((decodabet[src2] & 0x3F) << 2 | (b1 & 0xF0) >> 4 | ((b1 & 0xF) << 4 | (decodabet[src4] & 0xFC) >>> 2) << 8);
                    }
                }
                catch (final IndexOutOfBoundsException ignored2) {
                    throw new IllegalArgumentException("not encoded in Base64");
                }
                dest.setShort(destOffset, decodedValue);
                return 2;
            }
            int decodedValue;
            try {
                if (dest.order() == ByteOrder.BIG_ENDIAN) {
                    decodedValue = ((decodabet[src2] & 0x3F) << 18 | (decodabet[src3] & 0xFF) << 12 | (decodabet[src4] & 0xFF) << 6 | (decodabet[src5] & 0xFF));
                }
                else {
                    final byte b1 = decodabet[src3];
                    final byte b2 = decodabet[src4];
                    decodedValue = ((decodabet[src2] & 0x3F) << 2 | (b1 & 0xF) << 12 | (b1 & 0xF0) >>> 4 | (b2 & 0x3) << 22 | (b2 & 0xFC) << 6 | (decodabet[src5] & 0xFF) << 16);
                }
            }
            catch (final IndexOutOfBoundsException ignored3) {
                throw new IllegalArgumentException("not encoded in Base64");
            }
            dest.setMedium(destOffset, decodedValue);
            return 3;
        }
    }
}
