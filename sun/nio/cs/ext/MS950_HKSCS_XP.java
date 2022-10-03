package sun.nio.cs.ext;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.Charset;

public class MS950_HKSCS_XP extends Charset
{
    public MS950_HKSCS_XP() {
        super("x-MS950-HKSCS-XP", ExtendedCharsets.aliasesFor("x-MS950-HKSCS-XP"));
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset.name().equals("US-ASCII") || charset instanceof MS950 || charset instanceof MS950_HKSCS_XP;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new Decoder((Charset)this);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new Encoder((Charset)this);
    }
    
    static class Decoder extends HKSCS.Decoder
    {
        private static DoubleByte.Decoder ms950;
        private static char[][] b2cBmp;
        
        @Override
        public char decodeDoubleEx(final int n, final int n2) {
            return '\ufffd';
        }
        
        private Decoder(final Charset charset) {
            super(charset, Decoder.ms950, Decoder.b2cBmp, null);
        }
        
        static {
            Decoder.ms950 = (DoubleByte.Decoder)new MS950().newDecoder();
            HKSCS.Decoder.initb2c(Decoder.b2cBmp = new char[256][], HKSCS_XPMapping.b2cBmpStr);
        }
    }
    
    private static class Encoder extends HKSCS.Encoder
    {
        private static DoubleByte.Encoder ms950;
        static char[][] c2bBmp;
        
        @Override
        public int encodeSupp(final int n) {
            return 65533;
        }
        
        private Encoder(final Charset charset) {
            super(charset, Encoder.ms950, Encoder.c2bBmp, null);
        }
        
        static {
            Encoder.ms950 = (DoubleByte.Encoder)new MS950().newEncoder();
            HKSCS.Encoder.initc2b(Encoder.c2bBmp = new char[256][], HKSCS_XPMapping.b2cBmpStr, null);
        }
    }
}
