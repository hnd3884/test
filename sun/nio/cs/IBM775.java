package sun.nio.cs;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.Charset;

public class IBM775 extends Charset implements HistoricallyNamedCharset
{
    private static final String b2cTable = "\u0106\u00fc\u00e9\u0101\u00e4\u0123\u00e5\u0107\u0142\u0113\u0156\u0157\u012b\u0179\u00c4\u00c5\u00c9\u00e6\u00c6\u014d\u00f6\u0122¢\u015a\u015b\u00d6\u00dc\u00f8£\u00d8\u00d7¤\u0100\u012a\u00f3\u017b\u017c\u017a\u201d¦©®¬½¼\u0141«»\u2591\u2592\u2593\u2502\u2524\u0104\u010c\u0118\u0116\u2563\u2551\u2557\u255d\u012e\u0160\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u0172\u016a\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u017d\u0105\u010d\u0119\u0117\u012f\u0161\u0173\u016b\u017e\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u00d3\u00df\u014c\u0143\u00f5\u00d5µ\u0144\u0136\u0137\u013b\u013c\u0146\u0112\u0145\u2019\u00ad±\u201c¾¶§\u00f7\u201e°\u2219·¹³²\u25a0 \u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f";
    private static final char[] b2c;
    private static final char[] c2b;
    private static final char[] c2bIndex;
    
    public IBM775() {
        super("IBM775", StandardCharsets.aliases_IBM775);
    }
    
    @Override
    public String historicalName() {
        return "Cp775";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset instanceof IBM775;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new SingleByte.Decoder(this, IBM775.b2c);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new SingleByte.Encoder(this, IBM775.c2b, IBM775.c2bIndex);
    }
    
    static {
        b2c = "\u0106\u00fc\u00e9\u0101\u00e4\u0123\u00e5\u0107\u0142\u0113\u0156\u0157\u012b\u0179\u00c4\u00c5\u00c9\u00e6\u00c6\u014d\u00f6\u0122¢\u015a\u015b\u00d6\u00dc\u00f8£\u00d8\u00d7¤\u0100\u012a\u00f3\u017b\u017c\u017a\u201d¦©®¬½¼\u0141«»\u2591\u2592\u2593\u2502\u2524\u0104\u010c\u0118\u0116\u2563\u2551\u2557\u255d\u012e\u0160\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u0172\u016a\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u017d\u0105\u010d\u0119\u0117\u012f\u0161\u0173\u016b\u017e\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u00d3\u00df\u014c\u0143\u00f5\u00d5µ\u0144\u0136\u0137\u013b\u013c\u0146\u0112\u0145\u2019\u00ad±\u201c¾¶§\u00f7\u201e°\u2219·¹³²\u25a0 \u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f".toCharArray();
        c2b = new char[1280];
        c2bIndex = new char[256];
        SingleByte.initC2B(IBM775.b2c, null, IBM775.c2b, IBM775.c2bIndex);
    }
}
