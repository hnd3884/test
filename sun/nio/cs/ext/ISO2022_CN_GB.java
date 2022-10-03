package sun.nio.cs.ext;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.Charset;
import sun.nio.cs.HistoricallyNamedCharset;

public class ISO2022_CN_GB extends ISO2022 implements HistoricallyNamedCharset
{
    public ISO2022_CN_GB() {
        super("x-ISO-2022-CN-GB", ExtendedCharsets.aliasesFor("x-ISO-2022-CN-GB"));
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset instanceof EUC_CN || charset.name().equals("US-ASCII") || charset instanceof ISO2022_CN_GB;
    }
    
    @Override
    public String historicalName() {
        return "ISO2022CN_GB";
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new ISO2022_CN.Decoder(this);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new Encoder(this);
    }
    
    private static class Encoder extends ISO2022.Encoder
    {
        public Encoder(final Charset charset) {
            super(charset);
            this.SODesig = "$)A";
            try {
                this.ISOEncoder = Charset.forName("EUC_CN").newEncoder();
            }
            catch (final Exception ex) {}
        }
        
        @Override
        public boolean isLegalReplacement(final byte[] array) {
            return array.length == 1 && array[0] == 63;
        }
    }
}
