package sun.nio.cs.ext;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.Charset;
import sun.nio.cs.HistoricallyNamedCharset;

public class ISO2022_CN_CNS extends ISO2022 implements HistoricallyNamedCharset
{
    public ISO2022_CN_CNS() {
        super("x-ISO-2022-CN-CNS", ExtendedCharsets.aliasesFor("x-ISO-2022-CN-CNS"));
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset instanceof EUC_TW || charset.name().equals("US-ASCII") || charset instanceof ISO2022_CN_CNS;
    }
    
    @Override
    public String historicalName() {
        return "ISO2022CN_CNS";
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
        private byte[] bb;
        
        public Encoder(final Charset charset) {
            super(charset);
            this.bb = new byte[4];
            this.SODesig = "$)G";
            this.SS2Desig = "$*H";
            this.SS3Desig = "$+I";
            try {
                this.ISOEncoder = Charset.forName("EUC_TW").newEncoder();
            }
            catch (final Exception ex) {}
        }
        
        @Override
        public boolean canEncode(final char c) {
            final int euc;
            return c <= '\u007f' || (euc = ((EUC_TW.Encoder)this.ISOEncoder).toEUC(c, this.bb)) == 2 || (euc == 4 && this.bb[0] == -114 && (this.bb[1] == -94 || this.bb[1] == -93));
        }
        
        @Override
        public boolean isLegalReplacement(final byte[] array) {
            return array.length == 1 && array[0] == 63;
        }
    }
}
