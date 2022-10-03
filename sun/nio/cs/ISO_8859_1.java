package sun.nio.cs;

import java.nio.charset.CoderResult;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.Charset;

class ISO_8859_1 extends Charset implements HistoricallyNamedCharset
{
    public ISO_8859_1() {
        super("ISO-8859-1", StandardCharsets.aliases_ISO_8859_1);
    }
    
    @Override
    public String historicalName() {
        return "ISO8859_1";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset instanceof US_ASCII || charset instanceof ISO_8859_1;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new Decoder((Charset)this);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new Encoder((Charset)this);
    }
    
    private static class Decoder extends CharsetDecoder implements ArrayDecoder
    {
        private Decoder(final Charset charset) {
            super(charset, 1.0f, 1.0f);
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
                    final byte b = array[i];
                    if (n5 >= n4) {
                        return CoderResult.OVERFLOW;
                    }
                    array2[n5++] = (char)(b & 0xFF);
                    ++i;
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
                    final byte value = byteBuffer.get();
                    if (!charBuffer.hasRemaining()) {
                        return CoderResult.OVERFLOW;
                    }
                    charBuffer.put((char)(value & 0xFF));
                    ++position;
                }
                return CoderResult.UNDERFLOW;
            }
            finally {
                byteBuffer.position(position);
            }
        }
        
        @Override
        protected CoderResult decodeLoop(final ByteBuffer byteBuffer, final CharBuffer charBuffer) {
            if (byteBuffer.hasArray() && charBuffer.hasArray()) {
                return this.decodeArrayLoop(byteBuffer, charBuffer);
            }
            return this.decodeBufferLoop(byteBuffer, charBuffer);
        }
        
        @Override
        public int decode(final byte[] array, int n, int length, final char[] array2) {
            if (length > array2.length) {
                length = array2.length;
            }
            int i;
            for (i = 0; i < length; array2[i++] = (char)(array[n++] & 0xFF)) {}
            return i;
        }
    }
    
    private static class Encoder extends CharsetEncoder implements ArrayEncoder
    {
        private final Surrogate.Parser sgp;
        private byte repl;
        
        private Encoder(final Charset charset) {
            super(charset, 1.0f, 1.0f);
            this.sgp = new Surrogate.Parser();
            this.repl = 63;
        }
        
        @Override
        public boolean canEncode(final char c) {
            return c <= '\u00ff';
        }
        
        @Override
        public boolean isLegalReplacement(final byte[] array) {
            return true;
        }
        
        private static int encodeISOArray(final char[] array, int n, final byte[] array2, int n2, final int n3) {
            int i;
            for (i = 0; i < n3; ++i) {
                final char c = array[n++];
                if (c > '\u00ff') {
                    break;
                }
                array2[n2++] = (byte)c;
            }
            return i;
        }
        
        private CoderResult encodeArrayLoop(final CharBuffer charBuffer, final ByteBuffer byteBuffer) {
            final char[] array = charBuffer.array();
            final int arrayOffset = charBuffer.arrayOffset();
            final int n = arrayOffset + charBuffer.position();
            final int n2 = arrayOffset + charBuffer.limit();
            assert n <= n2;
            int n3 = (n <= n2) ? n : n2;
            final byte[] array2 = byteBuffer.array();
            final int arrayOffset2 = byteBuffer.arrayOffset();
            final int n4 = arrayOffset2 + byteBuffer.position();
            final int n5 = arrayOffset2 + byteBuffer.limit();
            assert n4 <= n5;
            int n6 = (n4 <= n5) ? n4 : n5;
            final int n7 = n5 - n6;
            final int n8 = n2 - n3;
            final int n9 = (n7 < n8) ? n7 : n8;
            try {
                final int n10 = (n9 <= 0) ? 0 : encodeISOArray(array, n3, array2, n6, n9);
                n3 += n10;
                n6 += n10;
                if (n10 != n9) {
                    if (this.sgp.parse(array[n3], array, n3, n2) < 0) {
                        return this.sgp.error();
                    }
                    return this.sgp.unmappableResult();
                }
                else {
                    if (n9 < n8) {
                        return CoderResult.OVERFLOW;
                    }
                    return CoderResult.UNDERFLOW;
                }
            }
            finally {
                charBuffer.position(n3 - arrayOffset);
                byteBuffer.position(n6 - arrayOffset2);
            }
        }
        
        private CoderResult encodeBufferLoop(final CharBuffer charBuffer, final ByteBuffer byteBuffer) {
            int position = charBuffer.position();
            try {
                while (charBuffer.hasRemaining()) {
                    final char value = charBuffer.get();
                    if (value <= '\u00ff') {
                        if (!byteBuffer.hasRemaining()) {
                            return CoderResult.OVERFLOW;
                        }
                        byteBuffer.put((byte)value);
                        ++position;
                    }
                    else {
                        if (this.sgp.parse(value, charBuffer) < 0) {
                            return this.sgp.error();
                        }
                        return this.sgp.unmappableResult();
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
        
        @Override
        protected void implReplaceWith(final byte[] array) {
            this.repl = array[0];
        }
        
        @Override
        public int encode(final char[] array, int i, int n, final byte[] array2) {
            int n2 = 0;
            int n3 = Math.min(n, array2.length);
            int n4 = i + n3;
            while (i < n4) {
                final int n5 = (n3 <= 0) ? 0 : encodeISOArray(array, i, array2, n2, n3);
                i += n5;
                n2 += n5;
                if (n5 != n3) {
                    if (Character.isHighSurrogate(array[i++]) && i < n4 && Character.isLowSurrogate(array[i])) {
                        if (n > array2.length) {
                            ++n4;
                            --n;
                        }
                        ++i;
                    }
                    array2[n2++] = this.repl;
                    n3 = Math.min(n4 - i, array2.length - n2);
                }
            }
            return n2;
        }
    }
}
