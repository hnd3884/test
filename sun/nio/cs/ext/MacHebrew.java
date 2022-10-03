package sun.nio.cs.ext;

import java.nio.charset.CharsetEncoder;
import sun.nio.cs.SingleByte;
import java.nio.charset.CharsetDecoder;
import sun.nio.cs.HistoricallyNamedCharset;
import java.nio.charset.Charset;

public class MacHebrew extends Charset implements HistoricallyNamedCharset
{
    private static final String b2cTable = "\u00c4\ufb1f\u00c7\u00c9\u00d1\u00d6\u00dc\u00e1\u00e0\u00e2\u00e4\u00e3\u00e5\u00e7\u00e9\u00e8\u00ea\u00eb\u00ed\u00ec\u00ee\u00ef\u00f1\u00f3\u00f2\u00f4\u00f6\u00f5\u00fa\u00f9\u00fb\u00fc\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u20aa\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u201e\ufffd\ufffd\ufffd\ufffd\u05bc\ufb4b\ufb35\u2026 \u05b8\u05b7\u05b5\u05b6\u05b4\u2013\u2014\u201c\u201d\u2018\u2019\ufb2a\ufb2b\u05bf\u05b0\u05b2\u05b1\u05bb\u05b9\ufffd\u05b3\u05d0\u05d1\u05d2\u05d3\u05d4\u05d5\u05d6\u05d7\u05d8\u05d9\u05da\u05db\u05dc\u05dd\u05de\u05df\u05e0\u05e1\u05e2\u05e3\u05e4\u05e5\u05e6\u05e7\u05e8\u05e9\u05ea\ufffd\ufffd\ufffd\ufffd\ufffd\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f";
    private static final char[] b2c;
    private static final char[] c2b;
    private static final char[] c2bIndex;
    
    public MacHebrew() {
        super("x-MacHebrew", ExtendedCharsets.aliasesFor("x-MacHebrew"));
    }
    
    @Override
    public String historicalName() {
        return "MacHebrew";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset instanceof MacHebrew;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new SingleByte.Decoder(this, MacHebrew.b2c);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new SingleByte.Encoder(this, MacHebrew.c2b, MacHebrew.c2bIndex);
    }
    
    static {
        b2c = "\u00c4\ufb1f\u00c7\u00c9\u00d1\u00d6\u00dc\u00e1\u00e0\u00e2\u00e4\u00e3\u00e5\u00e7\u00e9\u00e8\u00ea\u00eb\u00ed\u00ec\u00ee\u00ef\u00f1\u00f3\u00f2\u00f4\u00f6\u00f5\u00fa\u00f9\u00fb\u00fc\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u20aa\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u201e\ufffd\ufffd\ufffd\ufffd\u05bc\ufb4b\ufb35\u2026 \u05b8\u05b7\u05b5\u05b6\u05b4\u2013\u2014\u201c\u201d\u2018\u2019\ufb2a\ufb2b\u05bf\u05b0\u05b2\u05b1\u05bb\u05b9\ufffd\u05b3\u05d0\u05d1\u05d2\u05d3\u05d4\u05d5\u05d6\u05d7\u05d8\u05d9\u05da\u05db\u05dc\u05dd\u05de\u05df\u05e0\u05e1\u05e2\u05e3\u05e4\u05e5\u05e6\u05e7\u05e8\u05e9\u05ea\ufffd\ufffd\ufffd\ufffd\ufffd\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f".toCharArray();
        c2b = new char[1280];
        c2bIndex = new char[256];
        SingleByte.initC2B(MacHebrew.b2c, null, MacHebrew.c2b, MacHebrew.c2bIndex);
    }
}
