package sun.awt.windows;

import java.nio.charset.CoderResult;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.Charset;

public final class WingDings extends Charset
{
    public WingDings() {
        super("WingDings", null);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new Encoder(this);
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        throw new Error("Decoder isn't implemented for WingDings Charset");
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset instanceof WingDings;
    }
    
    private static class Encoder extends CharsetEncoder
    {
        private static byte[] table;
        
        public Encoder(final Charset charset) {
            super(charset, 1.0f, 1.0f);
        }
        
        @Override
        public boolean canEncode(final char c) {
            return c >= '\u2701' && c <= '\u27be' && Encoder.table[c - '\u2700'] != 0;
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
                    array2[n5++] = Encoder.table[c - '\u2700'];
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
            Encoder.table = new byte[] { 0, 35, 34, 0, 0, 0, 41, 62, 81, 42, 0, 0, 65, 63, 0, 0, 0, 0, 0, -4, 0, 0, 0, -5, 0, 0, 0, 0, 0, 0, 86, 0, 88, 89, 0, 0, 0, 0, 0, 0, 0, 0, -75, 0, 0, 0, 0, 0, -74, 0, 0, 0, -83, -81, -84, 0, 0, 0, 0, 0, 0, 0, 0, 124, 123, 0, 0, 0, 84, 0, 0, 0, 0, 0, 0, 0, 0, -90, 0, 0, 0, 113, 114, 0, 0, 0, 117, 0, 0, 0, 0, 0, 0, 125, 126, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -116, -115, -114, -113, -112, -111, -110, -109, -108, -107, -127, -126, -125, -124, -123, -122, -121, -120, -119, -118, -116, -115, -114, -113, -112, -111, -110, -109, -108, -107, -24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -24, -40, 0, 0, -60, -58, 0, 0, -16, 0, 0, 0, 0, 0, 0, 0, 0, 0, -36, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        }
    }
}
