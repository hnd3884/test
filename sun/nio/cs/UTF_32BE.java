package sun.nio.cs;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class UTF_32BE extends Unicode
{
    public UTF_32BE() {
        super("UTF-32BE", StandardCharsets.aliases_UTF_32BE);
    }
    
    @Override
    public String historicalName() {
        return "UTF-32BE";
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new UTF_32Coder.Decoder(this, 1);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new UTF_32Coder.Encoder(this, 1, false);
    }
}
