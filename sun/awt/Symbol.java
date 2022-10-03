package sun.awt;

import java.nio.charset.CoderResult;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.Charset;

public class Symbol extends Charset
{
    public Symbol() {
        super("Symbol", null);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new Encoder(this);
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        throw new Error("Decoder is not implemented for Symbol Charset");
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset instanceof Symbol;
    }
    
    private static class Encoder extends CharsetEncoder
    {
        private static byte[] table_math;
        private static byte[] table_greek;
        
        public Encoder(final Charset charset) {
            super(charset, 1.0f, 1.0f);
        }
        
        @Override
        public boolean canEncode(final char c) {
            if (c >= '\u2200' && c <= '\u22ef') {
                if (Encoder.table_math[c - '\u2200'] != 0) {
                    return true;
                }
            }
            else if (c >= '\u0391' && c <= '\u03d6' && Encoder.table_greek[c - '\u0391'] != 0) {
                return true;
            }
            return false;
        }
        
        @Override
        protected CoderResult encodeLoop(final CharBuffer charBuffer, final ByteBuffer byteBuffer) {
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
                    if (n4 - n5 < 1) {
                        return CoderResult.OVERFLOW;
                    }
                    if (!this.canEncode(c)) {
                        return CoderResult.unmappableForLength(1);
                    }
                    ++i;
                    if (c >= '\u2200' && c <= '\u22ef') {
                        array2[n5++] = Encoder.table_math[c - '\u2200'];
                    }
                    else {
                        if (c < '\u0391' || c > '\u03d6') {
                            continue;
                        }
                        array2[n5++] = Encoder.table_greek[c - '\u0391'];
                    }
                }
                return CoderResult.UNDERFLOW;
            }
            finally {
                charBuffer.position(i - charBuffer.arrayOffset());
                byteBuffer.position(n5 - byteBuffer.arrayOffset());
            }
        }
        
        @Override
        public boolean isLegalReplacement(final byte[] array) {
            return true;
        }
        
        static {
            Encoder.table_math = new byte[] { 34, 0, 100, 36, 0, -58, 68, -47, -50, -49, 0, 0, 0, 39, 0, 80, 0, -27, 45, 0, 0, -92, 0, 42, -80, -73, -42, 0, 0, -75, -91, 0, 0, 0, 0, -67, 0, 0, 0, -39, -38, -57, -56, -14, 0, 0, 0, 0, 0, 0, 0, 0, 92, 0, 0, 0, 0, 0, 0, 0, 126, 0, 0, 0, 0, 0, 0, 0, 0, 64, 0, 0, -69, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -71, -70, 0, 0, -93, -77, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -52, -55, -53, 0, -51, -54, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -59, 0, -60, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 94, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -32, -41, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -68 };
            Encoder.table_greek = new byte[] { 65, 66, 71, 68, 69, 90, 72, 81, 73, 75, 76, 77, 78, 88, 79, 80, 82, 0, 83, 84, 85, 70, 67, 89, 87, 0, 0, 0, 0, 0, 0, 0, 97, 98, 103, 100, 101, 122, 104, 113, 105, 107, 108, 109, 110, 120, 111, 112, 114, 86, 115, 116, 117, 102, 99, 121, 119, 0, 0, 0, 0, 0, 0, 0, 74, -95, 0, 0, 106, 118 };
        }
    }
}
