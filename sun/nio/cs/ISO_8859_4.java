package sun.nio.cs;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.Charset;

public class ISO_8859_4 extends Charset implements HistoricallyNamedCharset
{
    private static final String b2cTable = "\u0080\u0081\u0082\u0083\u0084\u0085\u0086\u0087\u0088\u0089\u008a\u008b\u008c\u008d\u008e\u008f\u0090\u0091\u0092\u0093\u0094\u0095\u0096\u0097\u0098\u0099\u009a\u009b\u009c\u009d\u009e\u009f \u0104\u0138\u0156¤\u0128\u013b§¨\u0160\u0112\u0122\u0166\u00ad\u017d¯°\u0105\u02db\u0157´\u0129\u013c\u02c7¸\u0161\u0113\u0123\u0167\u014a\u017e\u014b\u0100\u00c1\u00c2\u00c3\u00c4\u00c5\u00c6\u012e\u010c\u00c9\u0118\u00cb\u0116\u00cd\u00ce\u012a\u0110\u0145\u014c\u0136\u00d4\u00d5\u00d6\u00d7\u00d8\u0172\u00da\u00db\u00dc\u0168\u016a\u00df\u0101\u00e1\u00e2\u00e3\u00e4\u00e5\u00e6\u012f\u010d\u00e9\u0119\u00eb\u0117\u00ed\u00ee\u012b\u0111\u0146\u014d\u0137\u00f4\u00f5\u00f6\u00f7\u00f8\u0173\u00fa\u00fb\u00fc\u0169\u016b\u02d9\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f";
    private static final char[] b2c;
    private static final char[] c2b;
    private static final char[] c2bIndex;
    
    public ISO_8859_4() {
        super("ISO-8859-4", StandardCharsets.aliases_ISO_8859_4);
    }
    
    @Override
    public String historicalName() {
        return "ISO8859_4";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset.name().equals("US-ASCII") || charset instanceof ISO_8859_4;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new SingleByte.Decoder(this, ISO_8859_4.b2c);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new SingleByte.Encoder(this, ISO_8859_4.c2b, ISO_8859_4.c2bIndex);
    }
    
    static {
        b2c = "\u0080\u0081\u0082\u0083\u0084\u0085\u0086\u0087\u0088\u0089\u008a\u008b\u008c\u008d\u008e\u008f\u0090\u0091\u0092\u0093\u0094\u0095\u0096\u0097\u0098\u0099\u009a\u009b\u009c\u009d\u009e\u009f \u0104\u0138\u0156¤\u0128\u013b§¨\u0160\u0112\u0122\u0166\u00ad\u017d¯°\u0105\u02db\u0157´\u0129\u013c\u02c7¸\u0161\u0113\u0123\u0167\u014a\u017e\u014b\u0100\u00c1\u00c2\u00c3\u00c4\u00c5\u00c6\u012e\u010c\u00c9\u0118\u00cb\u0116\u00cd\u00ce\u012a\u0110\u0145\u014c\u0136\u00d4\u00d5\u00d6\u00d7\u00d8\u0172\u00da\u00db\u00dc\u0168\u016a\u00df\u0101\u00e1\u00e2\u00e3\u00e4\u00e5\u00e6\u012f\u010d\u00e9\u0119\u00eb\u0117\u00ed\u00ee\u012b\u0111\u0146\u014d\u0137\u00f4\u00f5\u00f6\u00f7\u00f8\u0173\u00fa\u00fb\u00fc\u0169\u016b\u02d9\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f".toCharArray();
        c2b = new char[768];
        c2bIndex = new char[256];
        SingleByte.initC2B(ISO_8859_4.b2c, null, ISO_8859_4.c2b, ISO_8859_4.c2bIndex);
    }
}
