package sun.nio.cs;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

class UTF_16LE_BOM extends Unicode
{
    public UTF_16LE_BOM() {
        super("x-UTF-16LE-BOM", StandardCharsets.aliases_UTF_16LE_BOM);
    }
    
    @Override
    public String historicalName() {
        return "UnicodeLittle";
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new Decoder(this);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new Encoder(this);
    }
    
    private static class Decoder extends UnicodeDecoder
    {
        public Decoder(final Charset charset) {
            super(charset, 0, 2);
        }
    }
    
    private static class Encoder extends UnicodeEncoder
    {
        public Encoder(final Charset charset) {
            super(charset, 1, true);
        }
    }
}
