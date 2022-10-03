package sun.nio.cs;

import java.nio.charset.CharsetEncoder;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.Buffer;
import java.nio.charset.CoderResult;

public class SingleByte
{
    private static final CoderResult withResult(final CoderResult coderResult, final Buffer buffer, final int n, final Buffer buffer2, final int n2) {
        buffer.position(n - buffer.arrayOffset());
        buffer2.position(n2 - buffer2.arrayOffset());
        return coderResult;
    }
    
    public static void initC2B(final char[] array, final char[] array2, final char[] array3, final char[] array4) {
        for (int i = 0; i < array4.length; ++i) {
            array4[i] = '\ufffd';
        }
        for (int j = 0; j < array3.length; ++j) {
            array3[j] = '\ufffd';
        }
        int n = 0;
        for (int k = 0; k < array.length; ++k) {
            final char c = array[k];
            if (c != '\ufffd') {
                final int n2 = c >> 8;
                if (array4[n2] == '\ufffd') {
                    array4[n2] = (char)n;
                    n += 256;
                }
                array3[array4[n2] + (c & '\u00ff')] = (char)((k >= 128) ? (k - 128) : (k + 128));
            }
        }
        if (array2 != null) {
            int l = 0;
            while (l < array2.length) {
                final char c2 = array2[l++];
                final char c3 = array2[l++];
                final int n3 = c3 >> 8;
                if (array4[n3] == '\ufffd') {
                    array4[n3] = (char)n;
                    n += 256;
                }
                array3[array4[n3] + (c3 & '\u00ff')] = c2;
            }
        }
    }
    
    public static final class Decoder extends CharsetDecoder implements ArrayDecoder
    {
        private final char[] b2c;
        private char repl;
        
        public Decoder(final Charset charset, final char[] b2c) {
            super(charset, 1.0f, 1.0f);
            this.repl = '\ufffd';
            this.b2c = b2c;
        }
        
        private CoderResult decodeArrayLoop(final ByteBuffer byteBuffer, final CharBuffer charBuffer) {
            final byte[] array = byteBuffer.array();
            int i = byteBuffer.arrayOffset() + byteBuffer.position();
            int n = byteBuffer.arrayOffset() + byteBuffer.limit();
            final char[] array2 = charBuffer.array();
            int n2 = charBuffer.arrayOffset() + charBuffer.position();
            final int n3 = charBuffer.arrayOffset() + charBuffer.limit();
            CoderResult coderResult = CoderResult.UNDERFLOW;
            if (n3 - n2 < n - i) {
                n = i + (n3 - n2);
                coderResult = CoderResult.OVERFLOW;
            }
            while (i < n) {
                final char decode = this.decode(array[i]);
                if (decode == '\ufffd') {
                    return withResult(CoderResult.unmappableForLength(1), byteBuffer, i, charBuffer, n2);
                }
                array2[n2++] = decode;
                ++i;
            }
            return withResult(coderResult, byteBuffer, i, charBuffer, n2);
        }
        
        private CoderResult decodeBufferLoop(final ByteBuffer byteBuffer, final CharBuffer charBuffer) {
            int position = byteBuffer.position();
            try {
                while (byteBuffer.hasRemaining()) {
                    final char decode = this.decode(byteBuffer.get());
                    if (decode == '\ufffd') {
                        return CoderResult.unmappableForLength(1);
                    }
                    if (!charBuffer.hasRemaining()) {
                        return CoderResult.OVERFLOW;
                    }
                    charBuffer.put(decode);
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
        
        public final char decode(final int n) {
            return this.b2c[n + 128];
        }
        
        @Override
        protected void implReplaceWith(final String s) {
            this.repl = s.charAt(0);
        }
        
        @Override
        public int decode(final byte[] array, int n, int length, final char[] array2) {
            if (length > array2.length) {
                length = array2.length;
            }
            int i;
            for (i = 0; i < length; ++i) {
                array2[i] = this.decode(array[n++]);
                if (array2[i] == '\ufffd') {
                    array2[i] = this.repl;
                }
            }
            return i;
        }
    }
    
    public static final class Encoder extends CharsetEncoder implements ArrayEncoder
    {
        private Surrogate.Parser sgp;
        private final char[] c2b;
        private final char[] c2bIndex;
        private byte repl;
        
        public Encoder(final Charset charset, final char[] c2b, final char[] c2bIndex) {
            super(charset, 1.0f, 1.0f);
            this.repl = 63;
            this.c2b = c2b;
            this.c2bIndex = c2bIndex;
        }
        
        @Override
        public boolean canEncode(final char c) {
            return this.encode(c) != 65533;
        }
        
        @Override
        public boolean isLegalReplacement(final byte[] array) {
            return (array.length == 1 && array[0] == 63) || super.isLegalReplacement(array);
        }
        
        private CoderResult encodeArrayLoop(final CharBuffer charBuffer, final ByteBuffer byteBuffer) {
            final char[] array = charBuffer.array();
            int i = charBuffer.arrayOffset() + charBuffer.position();
            int n = charBuffer.arrayOffset() + charBuffer.limit();
            final byte[] array2 = byteBuffer.array();
            int n2 = byteBuffer.arrayOffset() + byteBuffer.position();
            final int n3 = byteBuffer.arrayOffset() + byteBuffer.limit();
            CoderResult coderResult = CoderResult.UNDERFLOW;
            if (n3 - n2 < n - i) {
                n = i + (n3 - n2);
                coderResult = CoderResult.OVERFLOW;
            }
            while (i < n) {
                final char c = array[i];
                final int encode = this.encode(c);
                if (encode == 65533) {
                    if (!Character.isSurrogate(c)) {
                        return withResult(CoderResult.unmappableForLength(1), charBuffer, i, byteBuffer, n2);
                    }
                    if (this.sgp == null) {
                        this.sgp = new Surrogate.Parser();
                    }
                    if (this.sgp.parse(c, array, i, n) < 0) {
                        return withResult(this.sgp.error(), charBuffer, i, byteBuffer, n2);
                    }
                    return withResult(this.sgp.unmappableResult(), charBuffer, i, byteBuffer, n2);
                }
                else {
                    array2[n2++] = (byte)encode;
                    ++i;
                }
            }
            return withResult(coderResult, charBuffer, i, byteBuffer, n2);
        }
        
        private CoderResult encodeBufferLoop(final CharBuffer charBuffer, final ByteBuffer byteBuffer) {
            int position = charBuffer.position();
            try {
                while (charBuffer.hasRemaining()) {
                    final char value = charBuffer.get();
                    final int encode = this.encode(value);
                    if (encode == 65533) {
                        if (!Character.isSurrogate(value)) {
                            return CoderResult.unmappableForLength(1);
                        }
                        if (this.sgp == null) {
                            this.sgp = new Surrogate.Parser();
                        }
                        if (this.sgp.parse(value, charBuffer) < 0) {
                            return this.sgp.error();
                        }
                        return this.sgp.unmappableResult();
                    }
                    else {
                        if (!byteBuffer.hasRemaining()) {
                            return CoderResult.OVERFLOW;
                        }
                        byteBuffer.put((byte)encode);
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
        
        public final int encode(final char c) {
            final char c2 = this.c2bIndex[c >> 8];
            if (c2 == '\ufffd') {
                return 65533;
            }
            return this.c2b[c2 + (c & '\u00ff')];
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
                final int encode = this.encode(c);
                if (encode != 65533) {
                    array2[n2++] = (byte)encode;
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
