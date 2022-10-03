package sun.nio.cs;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.Charset;

public class IBM857 extends Charset implements HistoricallyNamedCharset
{
    private static final String b2cTable = "\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u0131\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u0130\u00d6\u00dc\u00f8£\u00d8\u015e\u015f\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u011e\u011f¿®¬½¼¡«»\u2591\u2592\u2593\u2502\u2524\u00c1\u00c2\u00c0©\u2563\u2551\u2557\u255d¢¥\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u00e3\u00c3\u255a\u2554\u2569\u2566\u2560\u2550\u256c¤ºª\u00ca\u00cb\u00c8\ufffd\u00cd\u00ce\u00cf\u2518\u250c\u2588\u2584¦\u00cc\u2580\u00d3\u00df\u00d4\u00d2\u00f5\u00d5µ\ufffd\u00d7\u00da\u00db\u00d9\u00ec\u00ff¯´\u00ad±\ufffd¾¶§\u00f7¸°¨·¹³²\u25a0 \u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f";
    private static final char[] b2c;
    private static final char[] c2b;
    private static final char[] c2bIndex;
    
    public IBM857() {
        super("IBM857", StandardCharsets.aliases_IBM857);
    }
    
    @Override
    public String historicalName() {
        return "Cp857";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset instanceof IBM857;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new SingleByte.Decoder(this, IBM857.b2c);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new SingleByte.Encoder(this, IBM857.c2b, IBM857.c2bIndex);
    }
    
    static {
        b2c = "\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u0131\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u0130\u00d6\u00dc\u00f8£\u00d8\u015e\u015f\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u011e\u011f¿®¬½¼¡«»\u2591\u2592\u2593\u2502\u2524\u00c1\u00c2\u00c0©\u2563\u2551\u2557\u255d¢¥\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u00e3\u00c3\u255a\u2554\u2569\u2566\u2560\u2550\u256c¤ºª\u00ca\u00cb\u00c8\ufffd\u00cd\u00ce\u00cf\u2518\u250c\u2588\u2584¦\u00cc\u2580\u00d3\u00df\u00d4\u00d2\u00f5\u00d5µ\ufffd\u00d7\u00da\u00db\u00d9\u00ec\u00ff¯´\u00ad±\ufffd¾¶§\u00f7¸°¨·¹³²\u25a0 \u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f".toCharArray();
        c2b = new char[1024];
        c2bIndex = new char[256];
        SingleByte.initC2B(IBM857.b2c, null, IBM857.c2b, IBM857.c2bIndex);
    }
}
