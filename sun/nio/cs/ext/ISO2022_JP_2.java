package sun.nio.cs.ext;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.Charset;

public class ISO2022_JP_2 extends ISO2022_JP
{
    public ISO2022_JP_2() {
        super("ISO-2022-JP-2", ExtendedCharsets.aliasesFor("ISO-2022-JP-2"));
    }
    
    @Override
    public String historicalName() {
        return "ISO2022JP2";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return super.contains(charset) || charset instanceof JIS_X_0212 || charset instanceof ISO2022_JP_2;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new Decoder(this, Decoder.DEC0208, CoderHolder.DEC0212);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new Encoder(this, Encoder.ENC0208, CoderHolder.ENC0212, true);
    }
    
    private static class CoderHolder
    {
        static final DoubleByte.Decoder DEC0212;
        static final DoubleByte.Encoder ENC0212;
        
        static {
            DEC0212 = (DoubleByte.Decoder)new JIS_X_0212().newDecoder();
            ENC0212 = (DoubleByte.Encoder)new JIS_X_0212().newEncoder();
        }
    }
}
