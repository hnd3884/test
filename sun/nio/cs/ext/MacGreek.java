package sun.nio.cs.ext;

import java.nio.charset.CharsetEncoder;
import sun.nio.cs.SingleByte;
import java.nio.charset.CharsetDecoder;
import sun.nio.cs.HistoricallyNamedCharset;
import java.nio.charset.Charset;

public class MacGreek extends Charset implements HistoricallyNamedCharset
{
    private static final String b2cTable = "\u00c4¹²\u00c9³\u00d6\u00dc\u0385\u00e0\u00e2\u00e4\u0384¨\u00e7\u00e9\u00e8\u00ea\u00eb£\u2122\u00ee\u00ef\u2022½\u2030\u00f4\u00f6¦\u00ad\u00f9\u00fb\u00fc\u2020\u0393\u0394\u0398\u039b\u039e\u03a0\u00df®©\u03a3\u03aa§\u2260°\u0387\u0391±\u2264\u2265¥\u0392\u0395\u0396\u0397\u0399\u039a\u039c\u03a6\u03ab\u03a8\u03a9\u03ac\u039d¬\u039f\u03a1\u2248\u03a4«»\u2026 \u03a5\u03a7\u0386\u0388\u0153\u2013\u2015\u201c\u201d\u2018\u2019\u00f7\u0389\u038a\u038c\u038e\u03ad\u03ae\u03af\u03cc\u038f\u03cd\u03b1\u03b2\u03c8\u03b4\u03b5\u03c6\u03b3\u03b7\u03b9\u03be\u03ba\u03bb\u03bc\u03bd\u03bf\u03c0\u03ce\u03c1\u03c3\u03c4\u03b8\u03c9\u03c2\u03c7\u03c5\u03b6\u03ca\u03cb\u0390\u03b0\ufffd\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f";
    private static final char[] b2c;
    private static final char[] c2b;
    private static final char[] c2bIndex;
    
    public MacGreek() {
        super("x-MacGreek", ExtendedCharsets.aliasesFor("x-MacGreek"));
    }
    
    @Override
    public String historicalName() {
        return "MacGreek";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset instanceof MacGreek;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new SingleByte.Decoder(this, MacGreek.b2c);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new SingleByte.Encoder(this, MacGreek.c2b, MacGreek.c2bIndex);
    }
    
    static {
        b2c = "\u00c4¹²\u00c9³\u00d6\u00dc\u0385\u00e0\u00e2\u00e4\u0384¨\u00e7\u00e9\u00e8\u00ea\u00eb£\u2122\u00ee\u00ef\u2022½\u2030\u00f4\u00f6¦\u00ad\u00f9\u00fb\u00fc\u2020\u0393\u0394\u0398\u039b\u039e\u03a0\u00df®©\u03a3\u03aa§\u2260°\u0387\u0391±\u2264\u2265¥\u0392\u0395\u0396\u0397\u0399\u039a\u039c\u03a6\u03ab\u03a8\u03a9\u03ac\u039d¬\u039f\u03a1\u2248\u03a4«»\u2026 \u03a5\u03a7\u0386\u0388\u0153\u2013\u2015\u201c\u201d\u2018\u2019\u00f7\u0389\u038a\u038c\u038e\u03ad\u03ae\u03af\u03cc\u038f\u03cd\u03b1\u03b2\u03c8\u03b4\u03b5\u03c6\u03b3\u03b7\u03b9\u03be\u03ba\u03bb\u03bc\u03bd\u03bf\u03c0\u03ce\u03c1\u03c3\u03c4\u03b8\u03c9\u03c2\u03c7\u03c5\u03b6\u03ca\u03cb\u0390\u03b0\ufffd\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f".toCharArray();
        c2b = new char[1792];
        c2bIndex = new char[256];
        SingleByte.initC2B(MacGreek.b2c, null, MacGreek.c2b, MacGreek.c2bIndex);
    }
}
