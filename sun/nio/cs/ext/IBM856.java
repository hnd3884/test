package sun.nio.cs.ext;

import java.nio.charset.CharsetEncoder;
import sun.nio.cs.SingleByte;
import java.nio.charset.CharsetDecoder;
import sun.nio.cs.HistoricallyNamedCharset;
import java.nio.charset.Charset;

public class IBM856 extends Charset implements HistoricallyNamedCharset
{
    private static final String b2cTable = "\u05d0\u05d1\u05d2\u05d3\u05d4\u05d5\u05d6\u05d7\u05d8\u05d9\u05da\u05db\u05dc\u05dd\u05de\u05df\u05e0\u05e1\u05e2\u05e3\u05e4\u05e5\u05e6\u05e7\u05e8\u05e9\u05ea\ufffd£\ufffd\u00d7\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd®¬½¼\ufffd«»\u2591\u2592\u2593\u2502\u2524\ufffd\ufffd\ufffd©\u2563\u2551\u2557\u255d¢¥\u2510\u2514\u2534\u252c\u251c\u2500\u253c\ufffd\ufffd\u255a\u2554\u2569\u2566\u2560\u2550\u256c¤\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u2518\u250c\u2588\u2584¦\ufffd\u2580\ufffd\ufffd\ufffd\ufffd\ufffd\ufffdµ\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u203e´\u00ad±\u2017¾¶§\u00f7¸°¨\u2022¹³²\u25a0 \u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f";
    private static final char[] b2c;
    private static final char[] c2b;
    private static final char[] c2bIndex;
    
    public IBM856() {
        super("x-IBM856", ExtendedCharsets.aliasesFor("x-IBM856"));
    }
    
    @Override
    public String historicalName() {
        return "Cp856";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset instanceof IBM856;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new SingleByte.Decoder(this, IBM856.b2c);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new SingleByte.Encoder(this, IBM856.c2b, IBM856.c2bIndex);
    }
    
    static {
        b2c = "\u05d0\u05d1\u05d2\u05d3\u05d4\u05d5\u05d6\u05d7\u05d8\u05d9\u05da\u05db\u05dc\u05dd\u05de\u05df\u05e0\u05e1\u05e2\u05e3\u05e4\u05e5\u05e6\u05e7\u05e8\u05e9\u05ea\ufffd£\ufffd\u00d7\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd®¬½¼\ufffd«»\u2591\u2592\u2593\u2502\u2524\ufffd\ufffd\ufffd©\u2563\u2551\u2557\u255d¢¥\u2510\u2514\u2534\u252c\u251c\u2500\u253c\ufffd\ufffd\u255a\u2554\u2569\u2566\u2560\u2550\u256c¤\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u2518\u250c\u2588\u2584¦\ufffd\u2580\ufffd\ufffd\ufffd\ufffd\ufffd\ufffdµ\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u203e´\u00ad±\u2017¾¶§\u00f7¸°¨\u2022¹³²\u25a0 \u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f".toCharArray();
        c2b = new char[1280];
        c2bIndex = new char[256];
        SingleByte.initC2B(IBM856.b2c, null, IBM856.c2b, IBM856.c2bIndex);
    }
}
