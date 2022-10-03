package sun.nio.cs.ext;

import java.nio.charset.CharsetEncoder;
import sun.nio.cs.SingleByte;
import java.nio.charset.CharsetDecoder;
import sun.nio.cs.HistoricallyNamedCharset;
import java.nio.charset.Charset;

public class MacCentralEurope extends Charset implements HistoricallyNamedCharset
{
    private static final String b2cTable = "\u00c4\u0100\u0101\u00c9\u0104\u00d6\u00dc\u00e1\u0105\u010c\u00e4\u010d\u0106\u0107\u00e9\u0179\u017a\u010e\u00ed\u010f\u0112\u0113\u0116\u00f3\u0117\u00f4\u00f6\u00f5\u00fa\u011a\u011b\u00fc\u2020°\u0118£§\u2022¶\u00df®©\u2122\u0119¨\u2260\u0123\u012e\u012f\u012a\u2264\u2265\u012b\u0136\u2202\u2211\u0142\u013b\u013c\u013d\u013e\u0139\u013a\u0145\u0146\u0143¬\u221a\u0144\u0147\u2206«»\u2026 \u0148\u0150\u00d5\u0151\u014c\u2013\u2014\u201c\u201d\u2018\u2019\u00f7\u25ca\u014d\u0154\u0155\u0158\u2039\u203a\u0159\u0156\u0157\u0160\u201a\u201e\u0161\u015a\u015b\u00c1\u0164\u0165\u00cd\u017d\u017e\u016a\u00d3\u00d4\u016b\u016e\u00da\u016f\u0170\u0171\u0172\u0173\u00dd\u00fd\u0137\u017b\u0141\u017c\u0122\u02c7\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f";
    private static final char[] b2c;
    private static final char[] c2b;
    private static final char[] c2bIndex;
    
    public MacCentralEurope() {
        super("x-MacCentralEurope", ExtendedCharsets.aliasesFor("x-MacCentralEurope"));
    }
    
    @Override
    public String historicalName() {
        return "MacCentralEurope";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset instanceof MacCentralEurope;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new SingleByte.Decoder(this, MacCentralEurope.b2c);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new SingleByte.Encoder(this, MacCentralEurope.c2b, MacCentralEurope.c2bIndex);
    }
    
    static {
        b2c = "\u00c4\u0100\u0101\u00c9\u0104\u00d6\u00dc\u00e1\u0105\u010c\u00e4\u010d\u0106\u0107\u00e9\u0179\u017a\u010e\u00ed\u010f\u0112\u0113\u0116\u00f3\u0117\u00f4\u00f6\u00f5\u00fa\u011a\u011b\u00fc\u2020°\u0118£§\u2022¶\u00df®©\u2122\u0119¨\u2260\u0123\u012e\u012f\u012a\u2264\u2265\u012b\u0136\u2202\u2211\u0142\u013b\u013c\u013d\u013e\u0139\u013a\u0145\u0146\u0143¬\u221a\u0144\u0147\u2206«»\u2026 \u0148\u0150\u00d5\u0151\u014c\u2013\u2014\u201c\u201d\u2018\u2019\u00f7\u25ca\u014d\u0154\u0155\u0158\u2039\u203a\u0159\u0156\u0157\u0160\u201a\u201e\u0161\u015a\u015b\u00c1\u0164\u0165\u00cd\u017d\u017e\u016a\u00d3\u00d4\u016b\u016e\u00da\u016f\u0170\u0171\u0172\u0173\u00dd\u00fd\u0137\u017b\u0141\u017c\u0122\u02c7\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f".toCharArray();
        c2b = new char[1792];
        c2bIndex = new char[256];
        SingleByte.initC2B(MacCentralEurope.b2c, null, MacCentralEurope.c2b, MacCentralEurope.c2bIndex);
    }
}
