package sun.nio.cs.ext;

import java.util.Arrays;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import sun.nio.cs.HistoricallyNamedCharset;
import java.nio.charset.Charset;

public class IBM942C extends Charset implements HistoricallyNamedCharset
{
    static final char[] b2cSB;
    static final char[] c2b;
    static final char[] c2bIndex;
    
    public IBM942C() {
        super("x-IBM942C", ExtendedCharsets.aliasesFor("x-IBM942C"));
    }
    
    @Override
    public String historicalName() {
        return "Cp942C";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset.name().equals("US-ASCII") || charset instanceof IBM942C;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new DoubleByte.Decoder(this, IBM942.b2c, IBM942C.b2cSB, 64, 252);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new DoubleByte.Encoder(this, IBM942C.c2b, IBM942C.c2bIndex);
    }
    
    static {
        IBM942.initb2c();
        (b2cSB = Arrays.copyOf(IBM942.b2cSB, IBM942.b2cSB.length))[26] = '\u001a';
        IBM942C.b2cSB[28] = '\u001c';
        IBM942C.b2cSB[92] = '\\';
        IBM942C.b2cSB[126] = '~';
        IBM942C.b2cSB[127] = '\u007f';
        IBM942.initc2b();
        c2b = Arrays.copyOf(IBM942.c2b, IBM942.c2b.length);
        c2bIndex = Arrays.copyOf(IBM942.c2bIndex, IBM942.c2bIndex.length);
        IBM942C.c2b[IBM942C.c2bIndex[0] + '\u001a'] = '\u001a';
        IBM942C.c2b[IBM942C.c2bIndex[0] + '\u001c'] = '\u001c';
        IBM942C.c2b[IBM942C.c2bIndex[0] + '\\'] = '\\';
        IBM942C.c2b[IBM942C.c2bIndex[0] + '~'] = '~';
        IBM942C.c2b[IBM942C.c2bIndex[0] + '\u007f'] = '\u007f';
    }
}
