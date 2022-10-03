package sun.nio.cs;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

class UTF_16BE extends Unicode
{
    public UTF_16BE() {
        super("UTF-16BE", StandardCharsets.aliases_UTF_16BE);
    }
    
    @Override
    public String historicalName() {
        return "UnicodeBigUnmarked";
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
            super(charset, 1);
        }
    }
    
    private static class Encoder extends UnicodeEncoder
    {
        public Encoder(final Charset charset) {
            super(charset, 0, false);
        }
    }
}
