package sun.nio.cs.ext;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import sun.nio.cs.HistoricallyNamedCharset;
import java.nio.charset.Charset;

public class EUC_JP_LINUX extends Charset implements HistoricallyNamedCharset
{
    public EUC_JP_LINUX() {
        super("x-euc-jp-linux", ExtendedCharsets.aliasesFor("x-euc-jp-linux"));
    }
    
    @Override
    public String historicalName() {
        return "EUC_JP_LINUX";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset instanceof JIS_X_0201 || charset.name().equals("US-ASCII") || charset instanceof EUC_JP_LINUX;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new Decoder((Charset)this);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new Encoder((Charset)this);
    }
    
    private static class Decoder extends EUC_JP.Decoder
    {
        private Decoder(final Charset charset) {
            super(charset, 1.0f, 1.0f, Decoder.DEC0201, Decoder.DEC0208, null);
        }
    }
    
    private static class Encoder extends EUC_JP.Encoder
    {
        private Encoder(final Charset charset) {
            super(charset, 2.0f, 2.0f, Encoder.ENC0201, Encoder.ENC0208, null);
        }
    }
}
