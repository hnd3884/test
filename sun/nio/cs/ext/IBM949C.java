package sun.nio.cs.ext;

import java.util.Arrays;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import sun.nio.cs.HistoricallyNamedCharset;
import java.nio.charset.Charset;

public class IBM949C extends Charset implements HistoricallyNamedCharset
{
    static final char[] b2cSB;
    static final char[] c2b;
    static final char[] c2bIndex;
    
    public IBM949C() {
        super("x-IBM949C", ExtendedCharsets.aliasesFor("x-IBM949C"));
    }
    
    @Override
    public String historicalName() {
        return "Cp949C";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset.name().equals("US-ASCII") || charset instanceof IBM949C;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new DoubleByte.Decoder(this, IBM949.b2c, IBM949C.b2cSB, 161, 254);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new DoubleByte.Encoder(this, IBM949C.c2b, IBM949C.c2bIndex);
    }
    
    static {
        IBM949.initb2c();
        b2cSB = new char[256];
        for (int i = 0; i < 128; ++i) {
            IBM949C.b2cSB[i] = (char)i;
        }
        for (int j = 128; j < 256; ++j) {
            IBM949C.b2cSB[j] = IBM949.b2cSB[j];
        }
        IBM949.initc2b();
        c2b = Arrays.copyOf(IBM949.c2b, IBM949.c2b.length);
        c2bIndex = Arrays.copyOf(IBM949.c2bIndex, IBM949.c2bIndex.length);
        for (char c = '\0'; c < '\u0080'; ++c) {
            IBM949C.c2b[IBM949C.c2bIndex[c >> 8] + (c & '\u00ff')] = c;
        }
    }
}
