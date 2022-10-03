package sun.nio.cs.ext;

import java.nio.charset.CharsetEncoder;
import sun.nio.cs.SingleByte;
import java.nio.charset.CharsetDecoder;
import sun.nio.cs.HistoricallyNamedCharset;
import java.nio.charset.Charset;

public class IBM869 extends Charset implements HistoricallyNamedCharset
{
    private static final String b2cTable = "\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u0386\ufffd·¬¦\u2018\u2019\u0388\u2015\u0389\u038a\u03aa\u038c\ufffd\ufffd\u038e\u03ab©\u038f²³\u03ac£\u03ad\u03ae\u03af\u03ca\u0390\u03cc\u03cd\u0391\u0392\u0393\u0394\u0395\u0396\u0397½\u0398\u0399«»\u2591\u2592\u2593\u2502\u2524\u039a\u039b\u039c\u039d\u2563\u2551\u2557\u255d\u039e\u039f\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u03a0\u03a1\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u03a3\u03a4\u03a5\u03a6\u03a7\u03a8\u03a9\u03b1\u03b2\u03b3\u2518\u250c\u2588\u2584\u03b4\u03b5\u2580\u03b6\u03b7\u03b8\u03b9\u03ba\u03bb\u03bc\u03bd\u03be\u03bf\u03c0\u03c1\u03c3\u03c2\u03c4\u0384\u00ad±\u03c5\u03c6\u03c7§\u03c8\u0385°¨\u03c9\u03cb\u03b0\u03ce\u25a0 \u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f";
    private static final char[] b2c;
    private static final char[] c2b;
    private static final char[] c2bIndex;
    
    public IBM869() {
        super("IBM869", ExtendedCharsets.aliasesFor("IBM869"));
    }
    
    @Override
    public String historicalName() {
        return "Cp869";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset instanceof IBM869;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new SingleByte.Decoder(this, IBM869.b2c);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new SingleByte.Encoder(this, IBM869.c2b, IBM869.c2bIndex);
    }
    
    static {
        b2c = "\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u0386\ufffd·¬¦\u2018\u2019\u0388\u2015\u0389\u038a\u03aa\u038c\ufffd\ufffd\u038e\u03ab©\u038f²³\u03ac£\u03ad\u03ae\u03af\u03ca\u0390\u03cc\u03cd\u0391\u0392\u0393\u0394\u0395\u0396\u0397½\u0398\u0399«»\u2591\u2592\u2593\u2502\u2524\u039a\u039b\u039c\u039d\u2563\u2551\u2557\u255d\u039e\u039f\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u03a0\u03a1\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u03a3\u03a4\u03a5\u03a6\u03a7\u03a8\u03a9\u03b1\u03b2\u03b3\u2518\u250c\u2588\u2584\u03b4\u03b5\u2580\u03b6\u03b7\u03b8\u03b9\u03ba\u03bb\u03bc\u03bd\u03be\u03bf\u03c0\u03c1\u03c3\u03c2\u03c4\u0384\u00ad±\u03c5\u03c6\u03c7§\u03c8\u0385°¨\u03c9\u03cb\u03b0\u03ce\u25a0 \u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f".toCharArray();
        c2b = new char[1280];
        c2bIndex = new char[256];
        SingleByte.initC2B(IBM869.b2c, null, IBM869.c2b, IBM869.c2bIndex);
    }
}
