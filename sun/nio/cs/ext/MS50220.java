package sun.nio.cs.ext;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.Charset;

public class MS50220 extends ISO2022_JP
{
    private static final DoubleByte.Decoder DEC0208;
    private static final DoubleByte.Decoder DEC0212;
    private static final DoubleByte.Encoder ENC0208;
    private static final DoubleByte.Encoder ENC0212;
    
    public MS50220() {
        super("x-windows-50220", ExtendedCharsets.aliasesFor("x-windows-50220"));
    }
    
    protected MS50220(final String s, final String[] array) {
        super(s, array);
    }
    
    @Override
    public String historicalName() {
        return "MS50220";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return super.contains(charset) || charset instanceof JIS_X_0212 || charset instanceof MS50220;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new Decoder(this, MS50220.DEC0208, MS50220.DEC0212);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new Encoder(this, MS50220.ENC0208, MS50220.ENC0212, this.doSBKANA());
    }
    
    @Override
    protected boolean doSBKANA() {
        return false;
    }
    
    static {
        DEC0208 = (DoubleByte.Decoder)new JIS_X_0208_MS5022X().newDecoder();
        DEC0212 = (DoubleByte.Decoder)new JIS_X_0212_MS5022X().newDecoder();
        ENC0208 = (DoubleByte.Encoder)new JIS_X_0208_MS5022X().newEncoder();
        ENC0212 = (DoubleByte.Encoder)new JIS_X_0212_MS5022X().newEncoder();
    }
}
