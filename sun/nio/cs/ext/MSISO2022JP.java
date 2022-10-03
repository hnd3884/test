package sun.nio.cs.ext;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.Charset;

public class MSISO2022JP extends ISO2022_JP
{
    public MSISO2022JP() {
        super("x-windows-iso2022jp", ExtendedCharsets.aliasesFor("x-windows-iso2022jp"));
    }
    
    @Override
    public String historicalName() {
        return "windows-iso2022jp";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return super.contains(charset) || charset instanceof MSISO2022JP;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new Decoder(this, CoderHolder.DEC0208, null);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new Encoder(this, CoderHolder.ENC0208, null, true);
    }
    
    private static class CoderHolder
    {
        static final DoubleByte.Decoder DEC0208;
        static final DoubleByte.Encoder ENC0208;
        
        static {
            DEC0208 = (DoubleByte.Decoder)new JIS_X_0208_MS932().newDecoder();
            ENC0208 = (DoubleByte.Encoder)new JIS_X_0208_MS932().newEncoder();
        }
    }
}
