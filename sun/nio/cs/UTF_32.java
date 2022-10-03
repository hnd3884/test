package sun.nio.cs;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class UTF_32 extends Unicode
{
    public UTF_32() {
        super("UTF-32", StandardCharsets.aliases_UTF_32);
    }
    
    @Override
    public String historicalName() {
        return "UTF-32";
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new UTF_32Coder.Decoder(this, 0);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new UTF_32Coder.Encoder(this, 1, false);
    }
}
