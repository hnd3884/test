package sun.nio.cs;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

class UTF_16 extends Unicode
{
    public UTF_16() {
        super("UTF-16", StandardCharsets.aliases_UTF_16);
    }
    
    @Override
    public String historicalName() {
        return "UTF-16";
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
            super(charset, 0);
        }
    }
    
    private static class Encoder extends UnicodeEncoder
    {
        public Encoder(final Charset charset) {
            super(charset, 0, true);
        }
    }
}
