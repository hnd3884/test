package sun.nio.cs.ext;

import sun.nio.cs.Surrogate;
import java.nio.charset.CoderResult;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import sun.nio.cs.SingleByte;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import sun.nio.cs.HistoricallyNamedCharset;
import java.nio.charset.Charset;

public class EUC_JP extends Charset implements HistoricallyNamedCharset
{
    public EUC_JP() {
        super("EUC-JP", ExtendedCharsets.aliasesFor("EUC-JP"));
    }
    
    @Override
    public String historicalName() {
        return "EUC_JP";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset.name().equals("US-ASCII") || charset instanceof JIS_X_0201 || charset instanceof JIS_X_0208 || charset instanceof JIS_X_0212 || charset instanceof EUC_JP;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new Decoder(this);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new Encoder(this);
    }
    
    static class Decoder extends CharsetDecoder implements DelegatableDecoder
    {
        static final SingleByte.Decoder DEC0201;
        static final DoubleByte.Decoder DEC0208;
        static final DoubleByte.Decoder DEC0212;
        private final SingleByte.Decoder dec0201;
        private final DoubleByte.Decoder dec0208;
        private final DoubleByte.Decoder dec0212;
        
        protected Decoder(final Charset charset) {
            this(charset, 0.5f, 1.0f, Decoder.DEC0201, Decoder.DEC0208, Decoder.DEC0212);
        }
        
        protected Decoder(final Charset charset, final float n, final float n2, final SingleByte.Decoder dec0201, final DoubleByte.Decoder dec202, final DoubleByte.Decoder dec203) {
            super(charset, n, n2);
            this.dec0201 = dec0201;
            this.dec0208 = dec202;
            this.dec0212 = dec203;
        }
        
        protected char decodeDouble(final int n, final int n2) {
            if (n != 142) {
                return this.dec0208.decodeDouble(n - 128, n2 - 128);
            }
            if (n2 < 128) {
                return '\ufffd';
            }
            return this.dec0201.decode((byte)n2);
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
                    final int n6 = array[i] & 0xFF;
                    int n7 = 1;
                    char c;
                    if ((n6 & 0x80) == 0x0) {
                        c = (char)n6;
                    }
                    else if (n6 == 143) {
                        if (i + 3 > n2) {
                            return CoderResult.UNDERFLOW;
                        }
                        final int n8 = array[i + 1] & 0xFF;
                        final int n9 = array[i + 2] & 0xFF;
                        n7 += 2;
                        if (this.dec0212 == null) {
                            return CoderResult.unmappableForLength(n7);
                        }
                        c = this.dec0212.decodeDouble(n8 - 128, n9 - 128);
                    }
                    else {
                        if (i + 2 > n2) {
                            return CoderResult.UNDERFLOW;
                        }
                        final int n10 = array[i + 1] & 0xFF;
                        ++n7;
                        c = this.decodeDouble(n6, n10);
                    }
                    if (c == '\ufffd') {
                        return CoderResult.unmappableForLength(n7);
                    }
                    if (n5 + 1 > n4) {
                        return CoderResult.OVERFLOW;
                    }
                    array2[n5++] = c;
                    i += n7;
                }
                return CoderResult.UNDERFLOW;
            }
            finally {
                byteBuffer.position(i - byteBuffer.arrayOffset());
                charBuffer.position(n5 - charBuffer.arrayOffset());
            }
        }
        
        private CoderResult decodeBufferLoop(final ByteBuffer byteBuffer, final CharBuffer charBuffer) {
            int position = byteBuffer.position();
            try {
                while (byteBuffer.hasRemaining()) {
                    final int n = byteBuffer.get() & 0xFF;
                    int n2 = 1;
                    char c;
                    if ((n & 0x80) == 0x0) {
                        c = (char)n;
                    }
                    else if (n == 143) {
                        if (byteBuffer.remaining() < 2) {
                            return CoderResult.UNDERFLOW;
                        }
                        final int n3 = byteBuffer.get() & 0xFF;
                        final int n4 = byteBuffer.get() & 0xFF;
                        n2 += 2;
                        if (this.dec0212 == null) {
                            return CoderResult.unmappableForLength(n2);
                        }
                        c = this.dec0212.decodeDouble(n3 - 128, n4 - 128);
                    }
                    else {
                        if (byteBuffer.remaining() < 1) {
                            return CoderResult.UNDERFLOW;
                        }
                        final int n5 = byteBuffer.get() & 0xFF;
                        ++n2;
                        c = this.decodeDouble(n, n5);
                    }
                    if (c == '\ufffd') {
                        return CoderResult.unmappableForLength(n2);
                    }
                    if (charBuffer.remaining() < 1) {
                        return CoderResult.OVERFLOW;
                    }
                    charBuffer.put(c);
                    position += n2;
                }
                return CoderResult.UNDERFLOW;
            }
            finally {
                byteBuffer.position(position);
            }
        }
        
        @Override
        public CoderResult decodeLoop(final ByteBuffer byteBuffer, final CharBuffer charBuffer) {
            if (byteBuffer.hasArray() && charBuffer.hasArray()) {
                return this.decodeArrayLoop(byteBuffer, charBuffer);
            }
            return this.decodeBufferLoop(byteBuffer, charBuffer);
        }
        
        @Override
        public void implReset() {
            super.implReset();
        }
        
        @Override
        public CoderResult implFlush(final CharBuffer charBuffer) {
            return super.implFlush(charBuffer);
        }
        
        static {
            DEC0201 = (SingleByte.Decoder)new JIS_X_0201().newDecoder();
            DEC0208 = (DoubleByte.Decoder)new JIS_X_0208().newDecoder();
            DEC0212 = (DoubleByte.Decoder)new JIS_X_0212().newDecoder();
        }
    }
    
    static class Encoder extends CharsetEncoder
    {
        static final SingleByte.Encoder ENC0201;
        static final DoubleByte.Encoder ENC0208;
        static final DoubleByte.Encoder ENC0212;
        private final Surrogate.Parser sgp;
        private final SingleByte.Encoder enc0201;
        private final DoubleByte.Encoder enc0208;
        private final DoubleByte.Encoder enc0212;
        
        protected Encoder(final Charset charset) {
            this(charset, 3.0f, 3.0f, Encoder.ENC0201, Encoder.ENC0208, Encoder.ENC0212);
        }
        
        protected Encoder(final Charset charset, final float n, final float n2, final SingleByte.Encoder enc0201, final DoubleByte.Encoder enc202, final DoubleByte.Encoder enc203) {
            super(charset, n, n2);
            this.sgp = new Surrogate.Parser();
            this.enc0201 = enc0201;
            this.enc0208 = enc202;
            this.enc0212 = enc203;
        }
        
        @Override
        public boolean canEncode(final char c) {
            return this.encodeSingle(c, new byte[3]) != 0 || this.encodeDouble(c) != 65533;
        }
        
        protected int encodeSingle(final char c, final byte[] array) {
            final int encode = this.enc0201.encode(c);
            if (encode == 65533) {
                return 0;
            }
            if (encode >= 0 && encode < 128) {
                array[0] = (byte)encode;
                return 1;
            }
            array[0] = -114;
            array[1] = (byte)encode;
            return 2;
        }
        
        protected int encodeDouble(final char c) {
            int n = this.enc0208.encodeChar(c);
            if (n != 65533) {
                return n + 32896;
            }
            if (this.enc0212 != null) {
                n = this.enc0212.encodeChar(c);
                if (n != 65533) {
                    n += 9404544;
                }
            }
            return n;
        }
        
        private CoderResult encodeArrayLoop(final CharBuffer charBuffer, final ByteBuffer byteBuffer) {
            final char[] array = charBuffer.array();
            final int n = charBuffer.arrayOffset() + charBuffer.position();
            final int n2 = charBuffer.arrayOffset() + charBuffer.limit();
            assert n <= n2;
            int i = (n <= n2) ? n : n2;
            final byte[] array2 = byteBuffer.array();
            final int n3 = byteBuffer.arrayOffset() + byteBuffer.position();
            final int n4 = byteBuffer.arrayOffset() + byteBuffer.limit();
            assert n3 <= n4;
            int n5 = (n3 <= n4) ? n3 : n4;
            final byte[] array3 = new byte[3];
            try {
                while (i < n2) {
                    final byte[] array4 = array3;
                    final char c = array[i];
                    if (Character.isSurrogate(c)) {
                        if (this.sgp.parse(c, array, i, n2) < 0) {
                            return this.sgp.error();
                        }
                        return this.sgp.unmappableResult();
                    }
                    else {
                        int encodeSingle = this.encodeSingle(c, array4);
                        if (encodeSingle == 0) {
                            final int encodeDouble = this.encodeDouble(c);
                            if (encodeDouble == 65533) {
                                return CoderResult.unmappableForLength(1);
                            }
                            if ((encodeDouble & 0xFF0000) == 0x0) {
                                array4[0] = (byte)((encodeDouble & 0xFF00) >> 8);
                                array4[1] = (byte)(encodeDouble & 0xFF);
                                encodeSingle = 2;
                            }
                            else {
                                array4[0] = -113;
                                array4[1] = (byte)((encodeDouble & 0xFF00) >> 8);
                                array4[2] = (byte)(encodeDouble & 0xFF);
                                encodeSingle = 3;
                            }
                        }
                        if (n4 - n5 < encodeSingle) {
                            return CoderResult.OVERFLOW;
                        }
                        for (int j = 0; j < encodeSingle; ++j) {
                            array2[n5++] = array4[j];
                        }
                        ++i;
                    }
                }
                return CoderResult.UNDERFLOW;
            }
            finally {
                charBuffer.position(i - charBuffer.arrayOffset());
                byteBuffer.position(n5 - byteBuffer.arrayOffset());
            }
        }
        
        private CoderResult encodeBufferLoop(final CharBuffer charBuffer, final ByteBuffer byteBuffer) {
            final byte[] array = new byte[3];
            int position = charBuffer.position();
            try {
                while (charBuffer.hasRemaining()) {
                    final byte[] array2 = array;
                    final char value = charBuffer.get();
                    if (Character.isSurrogate(value)) {
                        if (this.sgp.parse(value, charBuffer) < 0) {
                            return this.sgp.error();
                        }
                        return this.sgp.unmappableResult();
                    }
                    else {
                        int encodeSingle = this.encodeSingle(value, array2);
                        if (encodeSingle == 0) {
                            final int encodeDouble = this.encodeDouble(value);
                            if (encodeDouble == 65533) {
                                return CoderResult.unmappableForLength(1);
                            }
                            if ((encodeDouble & 0xFF0000) == 0x0) {
                                array2[0] = (byte)((encodeDouble & 0xFF00) >> 8);
                                array2[1] = (byte)(encodeDouble & 0xFF);
                                encodeSingle = 2;
                            }
                            else {
                                array2[0] = -113;
                                array2[1] = (byte)((encodeDouble & 0xFF00) >> 8);
                                array2[2] = (byte)(encodeDouble & 0xFF);
                                encodeSingle = 3;
                            }
                        }
                        if (byteBuffer.remaining() < encodeSingle) {
                            return CoderResult.OVERFLOW;
                        }
                        for (int i = 0; i < encodeSingle; ++i) {
                            byteBuffer.put(array2[i]);
                        }
                        ++position;
                    }
                }
                return CoderResult.UNDERFLOW;
            }
            finally {
                charBuffer.position(position);
            }
        }
        
        @Override
        protected CoderResult encodeLoop(final CharBuffer charBuffer, final ByteBuffer byteBuffer) {
            if (charBuffer.hasArray() && byteBuffer.hasArray()) {
                return this.encodeArrayLoop(charBuffer, byteBuffer);
            }
            return this.encodeBufferLoop(charBuffer, byteBuffer);
        }
        
        static {
            ENC0201 = (SingleByte.Encoder)new JIS_X_0201().newEncoder();
            ENC0208 = (DoubleByte.Encoder)new JIS_X_0208().newEncoder();
            ENC0212 = (DoubleByte.Encoder)new JIS_X_0212().newEncoder();
        }
    }
}
