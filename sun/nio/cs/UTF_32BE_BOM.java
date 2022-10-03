package sun.nio.cs;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class UTF_32BE_BOM extends Unicode
{
    public UTF_32BE_BOM() {
        super("X-UTF-32BE-BOM", StandardCharsets.aliases_UTF_32BE_BOM);
    }
    
    @Override
    public String historicalName() {
        return "X-UTF-32BE-BOM";
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new UTF_32Coder.Decoder(this, 1);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new UTF_32Coder.Encoder(this, 1, true);
    }
}
