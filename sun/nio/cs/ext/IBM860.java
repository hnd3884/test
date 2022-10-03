package sun.nio.cs.ext;

import java.nio.charset.CharsetEncoder;
import sun.nio.cs.SingleByte;
import java.nio.charset.CharsetDecoder;
import sun.nio.cs.HistoricallyNamedCharset;
import java.nio.charset.Charset;

public class IBM860 extends Charset implements HistoricallyNamedCharset
{
    private static final String b2cTable = "\u00c7\u00fc\u00e9\u00e2\u00e3\u00e0\u00c1\u00e7\u00ea\u00ca\u00e8\u00cd\u00d4\u00ec\u00c3\u00c2\u00c9\u00c0\u00c8\u00f4\u00f5\u00f2\u00da\u00f9\u00cc\u00d5\u00dc¢£\u00d9\u20a7\u00d3\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1ªº¿\u00d2¬½¼¡«»\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u00df\u0393\u03c0\u03a3\u03c3µ\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u03c6\u03b5\u2229\u2261±\u2265\u2264\u2320\u2321\u00f7\u2248°\u2219·\u221a\u207f²\u25a0 \u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f";
    private static final char[] b2c;
    private static final char[] c2b;
    private static final char[] c2bIndex;
    
    public IBM860() {
        super("IBM860", ExtendedCharsets.aliasesFor("IBM860"));
    }
    
    @Override
    public String historicalName() {
        return "Cp860";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset instanceof IBM860;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new SingleByte.Decoder(this, IBM860.b2c);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new SingleByte.Encoder(this, IBM860.c2b, IBM860.c2bIndex);
    }
    
    static {
        b2c = "\u00c7\u00fc\u00e9\u00e2\u00e3\u00e0\u00c1\u00e7\u00ea\u00ca\u00e8\u00cd\u00d4\u00ec\u00c3\u00c2\u00c9\u00c0\u00c8\u00f4\u00f5\u00f2\u00da\u00f9\u00cc\u00d5\u00dc¢£\u00d9\u20a7\u00d3\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1ªº¿\u00d2¬½¼¡«»\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u00df\u0393\u03c0\u03a3\u03c3µ\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u03c6\u03b5\u2229\u2261±\u2265\u2264\u2320\u2321\u00f7\u2248°\u2219·\u221a\u207f²\u25a0 \u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f".toCharArray();
        c2b = new char[1536];
        c2bIndex = new char[256];
        SingleByte.initC2B(IBM860.b2c, null, IBM860.c2b, IBM860.c2bIndex);
    }
}
