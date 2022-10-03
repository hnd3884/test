package sun.nio.cs;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class UTF_32LE extends Unicode
{
    public UTF_32LE() {
        super("UTF-32LE", StandardCharsets.aliases_UTF_32LE);
    }
    
    @Override
    public String historicalName() {
        return "UTF-32LE";
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new UTF_32Coder.Decoder(this, 2);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new UTF_32Coder.Encoder(this, 2, false);
    }
}
