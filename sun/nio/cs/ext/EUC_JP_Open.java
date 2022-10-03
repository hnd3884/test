package sun.nio.cs.ext;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import sun.nio.cs.HistoricallyNamedCharset;
import java.nio.charset.Charset;

public class EUC_JP_Open extends Charset implements HistoricallyNamedCharset
{
    public EUC_JP_Open() {
        super("x-eucJP-Open", ExtendedCharsets.aliasesFor("x-eucJP-Open"));
    }
    
    @Override
    public String historicalName() {
        return "EUC_JP_Solaris";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset.name().equals("US-ASCII") || charset instanceof JIS_X_0201 || charset instanceof EUC_JP;
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
        private static DoubleByte.Decoder DEC0208_Solaris;
        private static DoubleByte.Decoder DEC0212_Solaris;
        
        private Decoder(final Charset charset) {
            super(charset, 0.5f, 1.0f, Decoder.DEC0201, Decoder.DEC0208, Decoder.DEC0212_Solaris);
        }
        
        @Override
        protected char decodeDouble(final int n, final int n2) {
            final char decodeDouble = super.decodeDouble(n, n2);
            if (decodeDouble == '\ufffd') {
                return Decoder.DEC0208_Solaris.decodeDouble(n - 128, n2 - 128);
            }
            return decodeDouble;
        }
        
        static {
            Decoder.DEC0208_Solaris = (DoubleByte.Decoder)new JIS_X_0208_Solaris().newDecoder();
            Decoder.DEC0212_Solaris = (DoubleByte.Decoder)new JIS_X_0212_Solaris().newDecoder();
        }
    }
    
    private static class Encoder extends EUC_JP.Encoder
    {
        private static DoubleByte.Encoder ENC0208_Solaris;
        private static DoubleByte.Encoder ENC0212_Solaris;
        
        private Encoder(final Charset charset) {
            super(charset);
        }
        
        @Override
        protected int encodeDouble(final char c) {
            final int encodeDouble = super.encodeDouble(c);
            if (encodeDouble != 65533) {
                return encodeDouble;
            }
            final int encodeChar = Encoder.ENC0208_Solaris.encodeChar(c);
            if (encodeChar != 65533 && encodeChar > 29952) {
                return 9404544 + Encoder.ENC0212_Solaris.encodeChar(c);
            }
            return (encodeChar == 65533) ? encodeChar : (encodeChar + 32896);
        }
        
        static {
            Encoder.ENC0208_Solaris = (DoubleByte.Encoder)new JIS_X_0208_Solaris().newEncoder();
            Encoder.ENC0212_Solaris = (DoubleByte.Encoder)new JIS_X_0212_Solaris().newEncoder();
        }
    }
}
