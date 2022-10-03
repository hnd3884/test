package sun.nio.cs;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.Charset;

public class MS1257 extends Charset implements HistoricallyNamedCharset
{
    private static final String b2cTable = "\u20ac\ufffd\u201a\ufffd\u201e\u2026\u2020\u2021\ufffd\u2030\ufffd\u2039\ufffd¨\u02c7¸\ufffd\u2018\u2019\u201c\u201d\u2022\u2013\u2014\ufffd\u2122\ufffd\u203a\ufffd¯\u02db\ufffd \ufffd¢£¤\ufffd¦§\u00d8©\u0156«¬\u00ad®\u00c6°±²³´µ¶·\u00f8¹\u0157»¼½¾\u00e6\u0104\u012e\u0100\u0106\u00c4\u00c5\u0118\u0112\u010c\u00c9\u0179\u0116\u0122\u0136\u012a\u013b\u0160\u0143\u0145\u00d3\u014c\u00d5\u00d6\u00d7\u0172\u0141\u015a\u016a\u00dc\u017b\u017d\u00df\u0105\u012f\u0101\u0107\u00e4\u00e5\u0119\u0113\u010d\u00e9\u017a\u0117\u0123\u0137\u012b\u013c\u0161\u0144\u0146\u00f3\u014d\u00f5\u00f6\u00f7\u0173\u0142\u015b\u016b\u00fc\u017c\u017e\u02d9\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f";
    private static final char[] b2c;
    private static final char[] c2b;
    private static final char[] c2bIndex;
    
    public MS1257() {
        super("windows-1257", StandardCharsets.aliases_MS1257);
    }
    
    @Override
    public String historicalName() {
        return "Cp1257";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset.name().equals("US-ASCII") || charset instanceof MS1257;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new SingleByte.Decoder(this, MS1257.b2c);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new SingleByte.Encoder(this, MS1257.c2b, MS1257.c2bIndex);
    }
    
    static {
        b2c = "\u20ac\ufffd\u201a\ufffd\u201e\u2026\u2020\u2021\ufffd\u2030\ufffd\u2039\ufffd¨\u02c7¸\ufffd\u2018\u2019\u201c\u201d\u2022\u2013\u2014\ufffd\u2122\ufffd\u203a\ufffd¯\u02db\ufffd \ufffd¢£¤\ufffd¦§\u00d8©\u0156«¬\u00ad®\u00c6°±²³´µ¶·\u00f8¹\u0157»¼½¾\u00e6\u0104\u012e\u0100\u0106\u00c4\u00c5\u0118\u0112\u010c\u00c9\u0179\u0116\u0122\u0136\u012a\u013b\u0160\u0143\u0145\u00d3\u014c\u00d5\u00d6\u00d7\u0172\u0141\u015a\u016a\u00dc\u017b\u017d\u00df\u0105\u012f\u0101\u0107\u00e4\u00e5\u0119\u0113\u010d\u00e9\u017a\u0117\u0123\u0137\u012b\u013c\u0161\u0144\u0146\u00f3\u014d\u00f5\u00f6\u00f7\u0173\u0142\u015b\u016b\u00fc\u017c\u017e\u02d9\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f".toCharArray();
        c2b = new char[1536];
        c2bIndex = new char[256];
        SingleByte.initC2B(MS1257.b2c, null, MS1257.c2b, MS1257.c2bIndex);
    }
}
