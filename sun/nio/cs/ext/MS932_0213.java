package sun.nio.cs.ext;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.Charset;

public class MS932_0213 extends Charset
{
    public MS932_0213() {
        super("x-MS932_0213", ExtendedCharsets.aliasesFor("MS932_0213"));
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset.name().equals("US-ASCII") || charset instanceof MS932 || charset instanceof MS932_0213;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new Decoder(this);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new Encoder(this);
    }
    
    protected static class Decoder extends SJIS_0213.Decoder
    {
        static DoubleByte.Decoder decMS932;
        
        protected Decoder(final Charset charset) {
            super(charset);
        }
        
        @Override
        protected char decodeDouble(final int n, final int n2) {
            final char decodeDouble = Decoder.decMS932.decodeDouble(n, n2);
            if (decodeDouble == '\ufffd') {
                return super.decodeDouble(n, n2);
            }
            return decodeDouble;
        }
        
        static {
            Decoder.decMS932 = (DoubleByte.Decoder)new MS932().newDecoder();
        }
    }
    
    protected static class Encoder extends SJIS_0213.Encoder
    {
        static DoubleByte.Encoder encMS932;
        
        protected Encoder(final Charset charset) {
            super(charset);
        }
        
        @Override
        protected int encodeChar(final char c) {
            final int encodeChar = Encoder.encMS932.encodeChar(c);
            if (encodeChar == 65533) {
                return super.encodeChar(c);
            }
            return encodeChar;
        }
        
        static {
            Encoder.encMS932 = (DoubleByte.Encoder)new MS932().newEncoder();
        }
    }
}
