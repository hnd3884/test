package sun.nio.cs.ext;

import java.util.Arrays;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import sun.nio.cs.HistoricallyNamedCharset;
import java.nio.charset.Charset;

public class IBM943C extends Charset implements HistoricallyNamedCharset
{
    static final char[] b2cSB;
    static final char[] c2b;
    static final char[] c2bIndex;
    
    public IBM943C() {
        super("x-IBM943C", ExtendedCharsets.aliasesFor("x-IBM943C"));
    }
    
    @Override
    public String historicalName() {
        return "Cp943C";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset.name().equals("US-ASCII") || charset instanceof IBM943C;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new DoubleByte.Decoder(this, IBM943.b2c, IBM943C.b2cSB, 64, 252);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new DoubleByte.Encoder(this, IBM943C.c2b, IBM943C.c2bIndex);
    }
    
    static {
        IBM943.initb2c();
        b2cSB = new char[256];
        for (int i = 0; i < 128; ++i) {
            IBM943C.b2cSB[i] = (char)i;
        }
        for (int j = 128; j < 256; ++j) {
            IBM943C.b2cSB[j] = IBM943.b2cSB[j];
        }
        IBM943.initc2b();
        c2b = Arrays.copyOf(IBM943.c2b, IBM943.c2b.length);
        c2bIndex = Arrays.copyOf(IBM943.c2bIndex, IBM943.c2bIndex.length);
        for (char c = '\0'; c < '\u0080'; ++c) {
            IBM943C.c2b[IBM943C.c2bIndex[c >> 8] + (c & '\u00ff')] = c;
        }
    }
}
