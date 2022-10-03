package sun.nio.cs;

import java.nio.charset.CoderResult;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.Charset;

public class US_ASCII extends Charset implements HistoricallyNamedCharset
{
    public US_ASCII() {
        super("US-ASCII", StandardCharsets.aliases_US_ASCII);
    }
    
    @Override
    public String historicalName() {
        return "ASCII";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset instanceof US_ASCII;
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
        private char repl;
        
        private Decoder(final Charset charset) {
            super(charset, 1.0f, 1.0f);
            this.repl = '\ufffd';
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
                    if (b < 0) {
                        return CoderResult.malformedForLength(1);
                    }
                    if (n5 >= n4) {
                        return CoderResult.OVERFLOW;
                    }
                    array2[n5++] = (char)b;
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
                    if (value < 0) {
                        return CoderResult.malformedForLength(1);
                    }
                    if (!charBuffer.hasRemaining()) {
                        return CoderResult.OVERFLOW;
                    }
                    charBuffer.put((char)value);
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
        protected void implReplaceWith(final String s) {
            this.repl = s.charAt(0);
        }
        
        @Override
        public int decode(final byte[] array, int n, int min, final char[] array2) {
            int i = 0;
            min = Math.min(min, array2.length);
            while (i < min) {
                final byte b = array[n++];
                if (b >= 0) {
                    array2[i++] = (char)b;
                }
                else {
                    array2[i++] = this.repl;
                }
            }
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
            return c < '\u0080';
        }
        
        @Override
        public boolean isLegalReplacement(final byte[] array) {
            return (array.length == 1 && array[0] >= 0) || super.isLegalReplacement(array);
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
            try {
                while (i < n2) {
                    final char c = array[i];
                    if (c < '\u0080') {
                        if (n5 >= n4) {
                            return CoderResult.OVERFLOW;
                        }
                        array2[n5] = (byte)c;
                        ++i;
                        ++n5;
                    }
                    else {
                        if (this.sgp.parse(c, array, i, n2) < 0) {
                            return this.sgp.error();
                        }
                        return this.sgp.unmappableResult();
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
            int position = charBuffer.position();
            try {
                while (charBuffer.hasRemaining()) {
                    final char value = charBuffer.get();
                    if (value < '\u0080') {
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
            int n3 = i + Math.min(n, array2.length);
            while (i < n3) {
                final char c = array[i++];
                if (c < '\u0080') {
                    array2[n2++] = (byte)c;
                }
                else {
                    if (Character.isHighSurrogate(c) && i < n3 && Character.isLowSurrogate(array[i])) {
                        if (n > array2.length) {
                            ++n3;
                            --n;
                        }
                        ++i;
                    }
                    array2[n2++] = this.repl;
                }
            }
            return n2;
        }
    }
}
