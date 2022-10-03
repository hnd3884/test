package sun.nio.cs;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

class UTF_16LE extends Unicode
{
    public UTF_16LE() {
        super("UTF-16LE", StandardCharsets.aliases_UTF_16LE);
    }
    
    @Override
    public String historicalName() {
        return "UnicodeLittleUnmarked";
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
            super(charset, 2);
        }
    }
    
    private static class Encoder extends UnicodeEncoder
    {
        public Encoder(final Charset charset) {
            super(charset, 1, false);
        }
    }
}
