package sun.nio.cs.ext;

import java.nio.charset.CharsetEncoder;
import sun.nio.cs.SingleByte;
import java.nio.charset.CharsetDecoder;
import sun.nio.cs.HistoricallyNamedCharset;
import java.nio.charset.Charset;

public class IBM870 extends Charset implements HistoricallyNamedCharset
{
    private static final String b2cTable = "\u02d8abcdefghi\u015b\u0148\u0111\u00fd\u0159\u015f�jklmnopqr\u0142\u0144\u0161�\u02db�\u0105~stuvwxyz\u015a\u0147\u0110\u00dd\u0158\u015e\u02d9\u0104\u017c\u0162\u017b�\u017e\u017a\u017d\u0179\u0141\u0143\u0160��\u00d7{ABCDEFGHI\u00ad\u00f4\u00f6\u0155\u00f3\u0151}JKLMNOPQR\u011a\u0171\u00fc\u0165\u00fa\u011b\\\u00f7STUVWXYZ\u010f\u00d4\u00d6\u0154\u00d3\u01500123456789\u010e\u0170\u00dc\u0164\u00da\u009f\u0000\u0001\u0002\u0003\u009c\t\u0086\u007f\u0097\u008d\u008e\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u009d\n\b\u0087\u0018\u0019\u0092\u008f\u001c\u001d\u001e\u001f\u0080\u0081\u0082\u0083\u0084\n\u0017\u001b\u0088\u0089\u008a\u008b\u008c\u0005\u0006\u0007\u0090\u0091\u0016\u0093\u0094\u0095\u0096\u0004\u0098\u0099\u009a\u009b\u0014\u0015\u009e\u001a �\u00e2\u00e4\u0163\u00e1\u0103\u010d\u00e7\u0107[.<(+!&\u00e9\u0119\u00eb\u016f\u00ed\u00ee\u013e\u013a\u00df]$*);^-/\u00c2\u00c4\u02dd\u00c1\u0102\u010c\u00c7\u0106|,%_>?\u02c7\u00c9\u0118\u00cb\u016e\u00cd\u00ce\u013d\u0139`:#@'=\"";
    private static final char[] b2c;
    private static final char[] c2b;
    private static final char[] c2bIndex;
    
    public IBM870() {
        super("IBM870", ExtendedCharsets.aliasesFor("IBM870"));
    }
    
    @Override
    public String historicalName() {
        return "Cp870";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset instanceof IBM870;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new SingleByte.Decoder(this, IBM870.b2c);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new SingleByte.Encoder(this, IBM870.c2b, IBM870.c2bIndex);
    }
    
    static {
        b2c = "\u02d8abcdefghi\u015b\u0148\u0111\u00fd\u0159\u015f�jklmnopqr\u0142\u0144\u0161�\u02db�\u0105~stuvwxyz\u015a\u0147\u0110\u00dd\u0158\u015e\u02d9\u0104\u017c\u0162\u017b�\u017e\u017a\u017d\u0179\u0141\u0143\u0160��\u00d7{ABCDEFGHI\u00ad\u00f4\u00f6\u0155\u00f3\u0151}JKLMNOPQR\u011a\u0171\u00fc\u0165\u00fa\u011b\\\u00f7STUVWXYZ\u010f\u00d4\u00d6\u0154\u00d3\u01500123456789\u010e\u0170\u00dc\u0164\u00da\u009f\u0000\u0001\u0002\u0003\u009c\t\u0086\u007f\u0097\u008d\u008e\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u009d\n\b\u0087\u0018\u0019\u0092\u008f\u001c\u001d\u001e\u001f\u0080\u0081\u0082\u0083\u0084\n\u0017\u001b\u0088\u0089\u008a\u008b\u008c\u0005\u0006\u0007\u0090\u0091\u0016\u0093\u0094\u0095\u0096\u0004\u0098\u0099\u009a\u009b\u0014\u0015\u009e\u001a �\u00e2\u00e4\u0163\u00e1\u0103\u010d\u00e7\u0107[.<(+!&\u00e9\u0119\u00eb\u016f\u00ed\u00ee\u013e\u013a\u00df]$*);^-/\u00c2\u00c4\u02dd\u00c1\u0102\u010c\u00c7\u0106|,%_>?\u02c7\u00c9\u0118\u00cb\u016e\u00cd\u00ce\u013d\u0139`:#@'=\"".toCharArray();
        c2b = new char[768];
        c2bIndex = new char[256];
        final char[] b2c2 = IBM870.b2c;
        final char[] charArray = "\u02d8abcdefghi\u015b\u0148\u0111\u00fd\u0159\u015f�jklmnopqr\u0142\u0144\u0161�\u02db�\u0105~stuvwxyz\u015a\u0147\u0110\u00dd\u0158\u015e\u02d9\u0104\u017c\u0162\u017b�\u017e\u017a\u017d\u0179\u0141\u0143\u0160��\u00d7{ABCDEFGHI\u00ad\u00f4\u00f6\u0155\u00f3\u0151}JKLMNOPQR\u011a\u0171\u00fc\u0165\u00fa\u011b\\\u00f7STUVWXYZ\u010f\u00d4\u00d6\u0154\u00d3\u01500123456789\u010e\u0170\u00dc\u0164\u00da\u009f\u0000\u0001\u0002\u0003\u009c\t\u0086\u007f\u0097\u008d\u008e\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u009d\n\b\u0087\u0018\u0019\u0092\u008f\u001c\u001d\u001e\u001f\u0080\u0081\u0082\u0083\u0084\n\u0017\u001b\u0088\u0089\u008a\u008b\u008c\u0005\u0006\u0007\u0090\u0091\u0016\u0093\u0094\u0095\u0096\u0004\u0098\u0099\u009a\u009b\u0014\u0015\u009e\u001a �\u00e2\u00e4\u0163\u00e1\u0103\u010d\u00e7\u0107[.<(+!&\u00e9\u0119\u00eb\u016f\u00ed\u00ee\u013e\u013a\u00df]$*);^-/\u00c2\u00c4\u02dd\u00c1\u0102\u010c\u00c7\u0106|,%_>?\u02c7\u00c9\u0118\u00cb\u016e\u00cd\u00ce\u013d\u0139`:#@'=\"".toCharArray();
        charArray[165] = '\ufffd';
        SingleByte.initC2B(charArray, new char[] { '\u0015', '\u0085' }, IBM870.c2b, IBM870.c2bIndex);
    }
}
