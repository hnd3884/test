package sun.nio.cs.ext;

import java.nio.charset.CharsetEncoder;
import sun.nio.cs.SingleByte;
import java.nio.charset.CharsetDecoder;
import sun.nio.cs.HistoricallyNamedCharset;
import java.nio.charset.Charset;

public class MS1258 extends Charset implements HistoricallyNamedCharset
{
    private static final String b2cTable = "\u20ac\ufffd\u201a\u0192\u201e\u2026\u2020\u2021\u02c6\u2030\ufffd\u2039\u0152\ufffd\ufffd\ufffd\ufffd\u2018\u2019\u201c\u201d\u2022\u2013\u2014\u02dc\u2122\ufffd\u203a\u0153\ufffd\ufffd\u0178 ¡¢£¤¥¦§¨©ª«¬\u00ad®¯°±²³´µ¶·¸¹º»¼½¾¿\u00c0\u00c1\u00c2\u0102\u00c4\u00c5\u00c6\u00c7\u00c8\u00c9\u00ca\u00cb\u0300\u00cd\u00ce\u00cf\u0110\u00d1\u0309\u00d3\u00d4\u01a0\u00d6\u00d7\u00d8\u00d9\u00da\u00db\u00dc\u01af\u0303\u00df\u00e0\u00e1\u00e2\u0103\u00e4\u00e5\u00e6\u00e7\u00e8\u00e9\u00ea\u00eb\u0301\u00ed\u00ee\u00ef\u0111\u00f1\u0323\u00f3\u00f4\u01a1\u00f6\u00f7\u00f8\u00f9\u00fa\u00fb\u00fc\u01b0\u20ab\u00ff\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f";
    private static final char[] b2c;
    private static final char[] c2b;
    private static final char[] c2bIndex;
    
    public MS1258() {
        super("windows-1258", ExtendedCharsets.aliasesFor("windows-1258"));
    }
    
    @Override
    public String historicalName() {
        return "Cp1258";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset.name().equals("US-ASCII") || charset instanceof MS1258;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new SingleByte.Decoder(this, MS1258.b2c);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new SingleByte.Encoder(this, MS1258.c2b, MS1258.c2bIndex);
    }
    
    static {
        b2c = "\u20ac\ufffd\u201a\u0192\u201e\u2026\u2020\u2021\u02c6\u2030\ufffd\u2039\u0152\ufffd\ufffd\ufffd\ufffd\u2018\u2019\u201c\u201d\u2022\u2013\u2014\u02dc\u2122\ufffd\u203a\u0153\ufffd\ufffd\u0178 ¡¢£¤¥¦§¨©ª«¬\u00ad®¯°±²³´µ¶·¸¹º»¼½¾¿\u00c0\u00c1\u00c2\u0102\u00c4\u00c5\u00c6\u00c7\u00c8\u00c9\u00ca\u00cb\u0300\u00cd\u00ce\u00cf\u0110\u00d1\u0309\u00d3\u00d4\u01a0\u00d6\u00d7\u00d8\u00d9\u00da\u00db\u00dc\u01af\u0303\u00df\u00e0\u00e1\u00e2\u0103\u00e4\u00e5\u00e6\u00e7\u00e8\u00e9\u00ea\u00eb\u0301\u00ed\u00ee\u00ef\u0111\u00f1\u0323\u00f3\u00f4\u01a1\u00f6\u00f7\u00f8\u00f9\u00fa\u00fb\u00fc\u01b0\u20ab\u00ff\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f".toCharArray();
        c2b = new char[1792];
        c2bIndex = new char[256];
        SingleByte.initC2B(MS1258.b2c, null, MS1258.c2b, MS1258.c2bIndex);
    }
}
