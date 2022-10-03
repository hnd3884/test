package sun.nio.cs.ext;

import java.nio.charset.CoderResult;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import sun.nio.cs.US_ASCII;
import sun.nio.cs.HistoricallyNamedCharset;
import java.nio.charset.Charset;

public class ISO2022_CN extends Charset implements HistoricallyNamedCharset
{
    private static final byte ISO_ESC = 27;
    private static final byte ISO_SI = 15;
    private static final byte ISO_SO = 14;
    private static final byte ISO_SS2_7 = 78;
    private static final byte ISO_SS3_7 = 79;
    private static final byte MSB = Byte.MIN_VALUE;
    private static final char REPLACE_CHAR = '\ufffd';
    private static final byte SODesigGB = 0;
    private static final byte SODesigCNS = 1;
    
    public ISO2022_CN() {
        super("ISO-2022-CN", ExtendedCharsets.aliasesFor("ISO-2022-CN"));
    }
    
    @Override
    public String historicalName() {
        return "ISO2022CN";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset instanceof EUC_CN || charset instanceof US_ASCII || charset instanceof EUC_TW || charset instanceof ISO2022_CN;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new Decoder(this);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean canEncode() {
        return false;
    }
    
    static class Decoder extends CharsetDecoder
    {
        private boolean shiftOut;
        private byte currentSODesig;
        private static final Charset gb2312;
        private static final Charset cns;
        private final DoubleByte.Decoder gb2312Decoder;
        private final EUC_TW.Decoder cnsDecoder;
        
        Decoder(final Charset charset) {
            super(charset, 1.0f, 1.0f);
            this.shiftOut = false;
            this.currentSODesig = 0;
            this.gb2312Decoder = (DoubleByte.Decoder)Decoder.gb2312.newDecoder();
            this.cnsDecoder = (EUC_TW.Decoder)Decoder.cns.newDecoder();
        }
        
        @Override
        protected void implReset() {
            this.shiftOut = false;
            this.currentSODesig = 0;
        }
        
        private char cnsDecode(final byte b, final byte b2, final byte b3) {
            final byte b4 = (byte)(b | 0xFFFFFF80);
            final byte b5 = (byte)(b2 | 0xFFFFFF80);
            int n;
            if (b3 == 78) {
                n = 1;
            }
            else {
                if (b3 != 79) {
                    return '\ufffd';
                }
                n = 2;
            }
            final char[] unicode = this.cnsDecoder.toUnicode(b4 & 0xFF, b5 & 0xFF, n);
            if (unicode == null || unicode.length == 2) {
                return '\ufffd';
            }
            return unicode[0];
        }
        
        private char SODecode(final byte b, final byte b2, final byte b3) {
            final byte b4 = (byte)(b | 0xFFFFFF80);
            final byte b5 = (byte)(b2 | 0xFFFFFF80);
            if (b3 == 0) {
                return this.gb2312Decoder.decodeDouble(b4 & 0xFF, b5 & 0xFF);
            }
            final char[] unicode = this.cnsDecoder.toUnicode(b4 & 0xFF, b5 & 0xFF, 0);
            if (unicode == null) {
                return '\ufffd';
            }
            return unicode[0];
        }
        
        private CoderResult decodeBufferLoop(final ByteBuffer byteBuffer, final CharBuffer charBuffer) {
            int position = byteBuffer.position();
            try {
                while (byteBuffer.hasRemaining()) {
                    byte b = byteBuffer.get();
                    int n = 1;
                    while (b == 27 || b == 14 || b == 15) {
                        if (b == 27) {
                            this.currentSODesig = 0;
                            if (byteBuffer.remaining() < 1) {
                                return CoderResult.UNDERFLOW;
                            }
                            final byte value = byteBuffer.get();
                            ++n;
                            if ((value & 0xFFFFFF80) != 0x0) {
                                return CoderResult.malformedForLength(n);
                            }
                            if (value == 36) {
                                if (byteBuffer.remaining() < 1) {
                                    return CoderResult.UNDERFLOW;
                                }
                                final byte value2 = byteBuffer.get();
                                ++n;
                                if ((value2 & 0xFFFFFF80) != 0x0) {
                                    return CoderResult.malformedForLength(n);
                                }
                                if (value2 == 65) {
                                    this.currentSODesig = 0;
                                }
                                else if (value2 == 41) {
                                    if (byteBuffer.remaining() < 1) {
                                        return CoderResult.UNDERFLOW;
                                    }
                                    final byte value3 = byteBuffer.get();
                                    ++n;
                                    if (value3 == 65) {
                                        this.currentSODesig = 0;
                                    }
                                    else {
                                        if (value3 != 71) {
                                            return CoderResult.malformedForLength(n);
                                        }
                                        this.currentSODesig = 1;
                                    }
                                }
                                else if (value2 == 42) {
                                    if (byteBuffer.remaining() < 1) {
                                        return CoderResult.UNDERFLOW;
                                    }
                                    final byte value4 = byteBuffer.get();
                                    ++n;
                                    if (value4 != 72) {
                                        return CoderResult.malformedForLength(n);
                                    }
                                }
                                else {
                                    if (value2 != 43) {
                                        return CoderResult.malformedForLength(n);
                                    }
                                    if (byteBuffer.remaining() < 1) {
                                        return CoderResult.UNDERFLOW;
                                    }
                                    final byte value5 = byteBuffer.get();
                                    ++n;
                                    if (value5 != 73) {
                                        return CoderResult.malformedForLength(n);
                                    }
                                }
                            }
                            else {
                                if (value != 78 && value != 79) {
                                    return CoderResult.malformedForLength(n);
                                }
                                if (byteBuffer.remaining() < 2) {
                                    return CoderResult.UNDERFLOW;
                                }
                                final byte value6 = byteBuffer.get();
                                final byte value7 = byteBuffer.get();
                                n += 2;
                                if (charBuffer.remaining() < 1) {
                                    return CoderResult.OVERFLOW;
                                }
                                final char cnsDecode = this.cnsDecode(value6, value7, value);
                                if (cnsDecode == '\ufffd') {
                                    return CoderResult.unmappableForLength(n);
                                }
                                charBuffer.put(cnsDecode);
                            }
                        }
                        else if (b == 14) {
                            this.shiftOut = true;
                        }
                        else if (b == 15) {
                            this.shiftOut = false;
                        }
                        position += n;
                        if (byteBuffer.remaining() < 1) {
                            return CoderResult.UNDERFLOW;
                        }
                        b = byteBuffer.get();
                        n = 1;
                    }
                    if (charBuffer.remaining() < 1) {
                        return CoderResult.OVERFLOW;
                    }
                    if (!this.shiftOut) {
                        charBuffer.put((char)(b & 0xFF));
                        position += n;
                    }
                    else {
                        if (byteBuffer.remaining() < 1) {
                            return CoderResult.UNDERFLOW;
                        }
                        final byte value8 = byteBuffer.get();
                        ++n;
                        final char soDecode = this.SODecode(b, value8, this.currentSODesig);
                        if (soDecode == '\ufffd') {
                            return CoderResult.unmappableForLength(n);
                        }
                        charBuffer.put(soDecode);
                        position += n;
                    }
                }
                return CoderResult.UNDERFLOW;
            }
            finally {
                byteBuffer.position(position);
            }
        }
        
        private CoderResult decodeArrayLoop(final ByteBuffer byteBuffer, final CharBuffer charBuffer) {
            final byte[] array = byteBuffer.array();
            final int n = byteBuffer.arrayOffset() + byteBuffer.position();
            final int n2 = byteBuffer.arrayOffset() + byteBuffer.limit();
            assert n <= n2;
            int i = (n <= n2) ? n : n2;
            final char[] array2 = charBuffer.array();
            final int n3 = charBuffer.arrayOffset() + charBuffer.position();
            final int n4 = charBuffer.arrayOffset() + charBuffer.limit();
            assert n3 <= n4;
            int n5 = (n3 <= n4) ? n3 : n4;
            try {
                while (i < n2) {
                    byte b = array[i];
                    int n6 = 1;
                    while (b == 27 || b == 14 || b == 15) {
                        if (b == 27) {
                            this.currentSODesig = 0;
                            if (i + 2 > n2) {
                                return CoderResult.UNDERFLOW;
                            }
                            final byte b2 = array[i + 1];
                            ++n6;
                            if ((b2 & 0xFFFFFF80) != 0x0) {
                                return CoderResult.malformedForLength(n6);
                            }
                            if (b2 == 36) {
                                if (i + 3 > n2) {
                                    return CoderResult.UNDERFLOW;
                                }
                                final byte b3 = array[i + 2];
                                ++n6;
                                if ((b3 & 0xFFFFFF80) != 0x0) {
                                    return CoderResult.malformedForLength(n6);
                                }
                                if (b3 == 65) {
                                    this.currentSODesig = 0;
                                }
                                else if (b3 == 41) {
                                    if (i + 4 > n2) {
                                        return CoderResult.UNDERFLOW;
                                    }
                                    final byte b4 = array[i + 3];
                                    ++n6;
                                    if (b4 == 65) {
                                        this.currentSODesig = 0;
                                    }
                                    else {
                                        if (b4 != 71) {
                                            return CoderResult.malformedForLength(n6);
                                        }
                                        this.currentSODesig = 1;
                                    }
                                }
                                else if (b3 == 42) {
                                    if (i + 4 > n2) {
                                        return CoderResult.UNDERFLOW;
                                    }
                                    final byte b5 = array[i + 3];
                                    ++n6;
                                    if (b5 != 72) {
                                        return CoderResult.malformedForLength(n6);
                                    }
                                }
                                else {
                                    if (b3 != 43) {
                                        return CoderResult.malformedForLength(n6);
                                    }
                                    if (i + 4 > n2) {
                                        return CoderResult.UNDERFLOW;
                                    }
                                    final byte b6 = array[i + 3];
                                    ++n6;
                                    if (b6 != 73) {
                                        return CoderResult.malformedForLength(n6);
                                    }
                                }
                            }
                            else {
                                if (b2 != 78 && b2 != 79) {
                                    return CoderResult.malformedForLength(n6);
                                }
                                if (i + 4 > n2) {
                                    return CoderResult.UNDERFLOW;
                                }
                                final byte b7 = array[i + 2];
                                final byte b8 = array[i + 3];
                                if (n4 - n5 < 1) {
                                    return CoderResult.OVERFLOW;
                                }
                                n6 += 2;
                                final char cnsDecode = this.cnsDecode(b7, b8, b2);
                                if (cnsDecode == '\ufffd') {
                                    return CoderResult.unmappableForLength(n6);
                                }
                                array2[n5++] = cnsDecode;
                            }
                        }
                        else if (b == 14) {
                            this.shiftOut = true;
                        }
                        else if (b == 15) {
                            this.shiftOut = false;
                        }
                        i += n6;
                        if (i + 1 > n2) {
                            return CoderResult.UNDERFLOW;
                        }
                        b = array[i];
                        n6 = 1;
                    }
                    if (n4 - n5 < 1) {
                        return CoderResult.OVERFLOW;
                    }
                    if (!this.shiftOut) {
                        array2[n5++] = (char)(b & 0xFF);
                    }
                    else {
                        if (i + 2 > n2) {
                            return CoderResult.UNDERFLOW;
                        }
                        final byte b9 = array[i + 1];
                        ++n6;
                        final char soDecode = this.SODecode(b, b9, this.currentSODesig);
                        if (soDecode == '\ufffd') {
                            return CoderResult.unmappableForLength(n6);
                        }
                        array2[n5++] = soDecode;
                    }
                    i += n6;
                }
                return CoderResult.UNDERFLOW;
            }
            finally {
                byteBuffer.position(i - byteBuffer.arrayOffset());
                charBuffer.position(n5 - charBuffer.arrayOffset());
            }
        }
        
        @Override
        protected CoderResult decodeLoop(final ByteBuffer byteBuffer, final CharBuffer charBuffer) {
            if (byteBuffer.hasArray() && charBuffer.hasArray()) {
                return this.decodeArrayLoop(byteBuffer, charBuffer);
            }
            return this.decodeBufferLoop(byteBuffer, charBuffer);
        }
        
        static {
            gb2312 = new EUC_CN();
            cns = new EUC_TW();
        }
    }
}
