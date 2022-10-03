package sun.nio.cs.ext;

import java.nio.charset.CharsetEncoder;
import sun.nio.cs.SingleByte;
import java.nio.charset.CharsetDecoder;
import sun.nio.cs.HistoricallyNamedCharset;
import java.nio.charset.Charset;

public class MacCroatian extends Charset implements HistoricallyNamedCharset
{
    private static final String b2cTable = "\u00c4\u00c5\u00c7\u00c9\u00d1\u00d6\u00dc\u00e1\u00e0\u00e2\u00e4\u00e3\u00e5\u00e7\u00e9\u00e8\u00ea\u00eb\u00ed\u00ec\u00ee\u00ef\u00f1\u00f3\u00f2\u00f4\u00f6\u00f5\u00fa\u00f9\u00fb\u00fc\u2020°¢£§\u2022¶\u00df®\u0160\u2122´¨\u2260\u017d\u00d8\u221e±\u2264\u2265\u2206µ\u2202\u2211\u220f\u0161\u222bªº\u2126\u017e\u00f8¿¡¬\u221a\u0192\u2248\u0106«\u010c\u2026 \u00c0\u00c3\u00d5\u0152\u0153\u0110\u2014\u201c\u201d\u2018\u2019\u00f7\u25ca\uf8ff©\u2044¤\u2039\u203a\u00c6»\u2013·\u201a\u201e\u2030\u00c2\u0107\u00c1\u010d\u00c8\u00cd\u00ce\u00cf\u00cc\u00d3\u00d4\u0111\u00d2\u00da\u00db\u00d9\u0131\u02c6\u02dc¯\u03c0\u00cb\u02da¸\u00ca\u00e6\u02c7\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f";
    private static final char[] b2c;
    private static final char[] c2b;
    private static final char[] c2bIndex;
    
    public MacCroatian() {
        super("x-MacCroatian", ExtendedCharsets.aliasesFor("x-MacCroatian"));
    }
    
    @Override
    public String historicalName() {
        return "MacCroatian";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset instanceof MacCroatian;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new SingleByte.Decoder(this, MacCroatian.b2c);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new SingleByte.Encoder(this, MacCroatian.c2b, MacCroatian.c2bIndex);
    }
    
    static {
        b2c = "\u00c4\u00c5\u00c7\u00c9\u00d1\u00d6\u00dc\u00e1\u00e0\u00e2\u00e4\u00e3\u00e5\u00e7\u00e9\u00e8\u00ea\u00eb\u00ed\u00ec\u00ee\u00ef\u00f1\u00f3\u00f2\u00f4\u00f6\u00f5\u00fa\u00f9\u00fb\u00fc\u2020°¢£§\u2022¶\u00df®\u0160\u2122´¨\u2260\u017d\u00d8\u221e±\u2264\u2265\u2206µ\u2202\u2211\u220f\u0161\u222bªº\u2126\u017e\u00f8¿¡¬\u221a\u0192\u2248\u0106«\u010c\u2026 \u00c0\u00c3\u00d5\u0152\u0153\u0110\u2014\u201c\u201d\u2018\u2019\u00f7\u25ca\uf8ff©\u2044¤\u2039\u203a\u00c6»\u2013·\u201a\u201e\u2030\u00c2\u0107\u00c1\u010d\u00c8\u00cd\u00ce\u00cf\u00cc\u00d3\u00d4\u0111\u00d2\u00da\u00db\u00d9\u0131\u02c6\u02dc¯\u03c0\u00cb\u02da¸\u00ca\u00e6\u02c7\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f".toCharArray();
        c2b = new char[2304];
        c2bIndex = new char[256];
        SingleByte.initC2B(MacCroatian.b2c, null, MacCroatian.c2b, MacCroatian.c2bIndex);
    }
}
