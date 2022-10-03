package sun.nio.cs;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.Charset;

public class MS1253 extends Charset implements HistoricallyNamedCharset
{
    private static final String b2cTable = "\u20ac\ufffd\u201a\u0192\u201e\u2026\u2020\u2021\ufffd\u2030\ufffd\u2039\ufffd\ufffd\ufffd\ufffd\ufffd\u2018\u2019\u201c\u201d\u2022\u2013\u2014\ufffd\u2122\ufffd\u203a\ufffd\ufffd\ufffd\ufffd \u0385\u0386£¤¥¦§¨©\ufffd«¬\u00ad®\u2015°±²³\u0384µ¶·\u0388\u0389\u038a»\u038c½\u038e\u038f\u0390\u0391\u0392\u0393\u0394\u0395\u0396\u0397\u0398\u0399\u039a\u039b\u039c\u039d\u039e\u039f\u03a0\u03a1\ufffd\u03a3\u03a4\u03a5\u03a6\u03a7\u03a8\u03a9\u03aa\u03ab\u03ac\u03ad\u03ae\u03af\u03b0\u03b1\u03b2\u03b3\u03b4\u03b5\u03b6\u03b7\u03b8\u03b9\u03ba\u03bb\u03bc\u03bd\u03be\u03bf\u03c0\u03c1\u03c2\u03c3\u03c4\u03c5\u03c6\u03c7\u03c8\u03c9\u03ca\u03cb\u03cc\u03cd\u03ce\ufffd\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f";
    private static final char[] b2c;
    private static final char[] c2b;
    private static final char[] c2bIndex;
    
    public MS1253() {
        super("windows-1253", StandardCharsets.aliases_MS1253);
    }
    
    @Override
    public String historicalName() {
        return "Cp1253";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset.name().equals("US-ASCII") || charset instanceof MS1253;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new SingleByte.Decoder(this, MS1253.b2c);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new SingleByte.Encoder(this, MS1253.c2b, MS1253.c2bIndex);
    }
    
    static {
        b2c = "\u20ac\ufffd\u201a\u0192\u201e\u2026\u2020\u2021\ufffd\u2030\ufffd\u2039\ufffd\ufffd\ufffd\ufffd\ufffd\u2018\u2019\u201c\u201d\u2022\u2013\u2014\ufffd\u2122\ufffd\u203a\ufffd\ufffd\ufffd\ufffd \u0385\u0386£¤¥¦§¨©\ufffd«¬\u00ad®\u2015°±²³\u0384µ¶·\u0388\u0389\u038a»\u038c½\u038e\u038f\u0390\u0391\u0392\u0393\u0394\u0395\u0396\u0397\u0398\u0399\u039a\u039b\u039c\u039d\u039e\u039f\u03a0\u03a1\ufffd\u03a3\u03a4\u03a5\u03a6\u03a7\u03a8\u03a9\u03aa\u03ab\u03ac\u03ad\u03ae\u03af\u03b0\u03b1\u03b2\u03b3\u03b4\u03b5\u03b6\u03b7\u03b8\u03b9\u03ba\u03bb\u03bc\u03bd\u03be\u03bf\u03c0\u03c1\u03c2\u03c3\u03c4\u03c5\u03c6\u03c7\u03c8\u03c9\u03ca\u03cb\u03cc\u03cd\u03ce\ufffd\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f".toCharArray();
        c2b = new char[1536];
        c2bIndex = new char[256];
        SingleByte.initC2B(MS1253.b2c, null, MS1253.c2b, MS1253.c2bIndex);
    }
}
