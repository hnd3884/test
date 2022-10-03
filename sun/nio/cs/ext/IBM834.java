package sun.nio.cs.ext;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.Charset;

public class IBM834 extends Charset
{
    public IBM834() {
        super("x-IBM834", ExtendedCharsets.aliasesFor("x-IBM834"));
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset instanceof IBM834;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        IBM933.initb2c();
        return new DoubleByte.Decoder_DBCSONLY(this, IBM933.b2c, null, 64, 254);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        IBM933.initc2b();
        return new Encoder(this);
    }
    
    protected static class Encoder extends DoubleByte.Encoder_DBCSONLY
    {
        public Encoder(final Charset charset) {
            super(charset, new byte[] { -2, -2 }, IBM933.c2b, IBM933.c2bIndex);
        }
        
        @Override
        public int encodeChar(final char c) {
            final int encodeChar = super.encodeChar(c);
            if (encodeChar == 65533) {
                if (c == '·') {
                    return 16707;
                }
                if (c == '\u00ad') {
                    return 16712;
                }
                if (c == '\u2015') {
                    return 16713;
                }
                if (c == '\u223c') {
                    return 17057;
                }
                if (c == '\uff5e') {
                    return 18772;
                }
                if (c == '\u2299') {
                    return 18799;
                }
            }
            return encodeChar;
        }
        
        @Override
        public boolean isLegalReplacement(final byte[] array) {
            return (array.length == 2 && array[0] == -2 && array[1] == -2) || super.isLegalReplacement(array);
        }
    }
}
