package sun.nio.cs.ext;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.Charset;
import sun.nio.cs.HistoricallyNamedCharset;

public class ISO2022_KR extends ISO2022 implements HistoricallyNamedCharset
{
    private static Charset ksc5601_cs;
    
    public ISO2022_KR() {
        super("ISO-2022-KR", ExtendedCharsets.aliasesFor("ISO-2022-KR"));
        ISO2022_KR.ksc5601_cs = new EUC_KR();
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset instanceof EUC_KR || charset.name().equals("US-ASCII") || charset instanceof ISO2022_KR;
    }
    
    @Override
    public String historicalName() {
        return "ISO2022KR";
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new Decoder(this);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new Encoder(this);
    }
    
    private static class Decoder extends ISO2022.Decoder
    {
        public Decoder(final Charset charset) {
            super(charset);
            this.SODesig = new byte[][] { { 36, 41, 67 } };
            this.SODecoder = new CharsetDecoder[1];
            try {
                this.SODecoder[0] = ISO2022_KR.ksc5601_cs.newDecoder();
            }
            catch (final Exception ex) {}
        }
    }
    
    private static class Encoder extends ISO2022.Encoder
    {
        public Encoder(final Charset charset) {
            super(charset);
            this.SODesig = "$)C";
            try {
                this.ISOEncoder = ISO2022_KR.ksc5601_cs.newEncoder();
            }
            catch (final Exception ex) {}
        }
        
        @Override
        public boolean canEncode(final char c) {
            return this.ISOEncoder.canEncode(c);
        }
    }
}
