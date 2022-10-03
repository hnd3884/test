package sun.nio.cs.ext;

import java.nio.charset.CharsetEncoder;
import sun.nio.cs.SingleByte;
import java.nio.charset.CharsetDecoder;
import sun.nio.cs.HistoricallyNamedCharset;
import java.nio.charset.Charset;

public class IBM1112 extends Charset implements HistoricallyNamedCharset
{
    private static final String b2cTable = "\u00d8abcdefghi«»\u0101\u017c\u0144±°jklmnopqr\u0156\u0157\u00e6\u0137\u00c6¤µ~stuvwxyz\u201d\u017a\u0100\u017b\u0143®^£\u012b·©§¶¼½¾[]\u0179\u0136\u013c\u00d7{ABCDEFGHI\u00ad\u014d\u00f6\u0146\u00f3\u00f5}JKLMNOPQR¹\u0107\u00fc\u0142\u015b\u2019\\\u00f7STUVWXYZ²\u014c\u00d6\u0145\u00d3\u00d50123456789³\u0106\u00dc\u0141\u015a\u009f\u0000\u0001\u0002\u0003\u009c\t\u0086\u007f\u0097\u008d\u008e\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u009d\n\b\u0087\u0018\u0019\u0092\u008f\u001c\u001d\u001e\u001f\u0080\u0081\u0082\u0083\u0084\n\u0017\u001b\u0088\u0089\u008a\u008b\u008c\u0005\u0006\u0007\u0090\u0091\u0016\u0093\u0094\u0095\u0096\u0004\u0098\u0099\u009a\u009b\u0014\u0015\u009e\u001a  \u0161\u00e4\u0105\u012f\u016b\u00e5\u0113\u017e¢.<(+|&\u00e9\u0119\u0117\u010d\u0173\u201e\u201c\u0123\u00df!$*);¬-/\u0160\u00c4\u0104\u012e\u016a\u00c5\u0112\u017d¦,%_>?\u00f8\u00c9\u0118\u0116\u010c\u0172\u012a\u013b\u0122`:#@'=\"";
    private static final char[] b2c;
    private static final char[] c2b;
    private static final char[] c2bIndex;
    
    public IBM1112() {
        super("x-IBM1112", ExtendedCharsets.aliasesFor("x-IBM1112"));
    }
    
    @Override
    public String historicalName() {
        return "Cp1112";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset instanceof IBM1112;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new SingleByte.Decoder(this, IBM1112.b2c);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new SingleByte.Encoder(this, IBM1112.c2b, IBM1112.c2bIndex);
    }
    
    static {
        b2c = "\u00d8abcdefghi«»\u0101\u017c\u0144±°jklmnopqr\u0156\u0157\u00e6\u0137\u00c6¤µ~stuvwxyz\u201d\u017a\u0100\u017b\u0143®^£\u012b·©§¶¼½¾[]\u0179\u0136\u013c\u00d7{ABCDEFGHI\u00ad\u014d\u00f6\u0146\u00f3\u00f5}JKLMNOPQR¹\u0107\u00fc\u0142\u015b\u2019\\\u00f7STUVWXYZ²\u014c\u00d6\u0145\u00d3\u00d50123456789³\u0106\u00dc\u0141\u015a\u009f\u0000\u0001\u0002\u0003\u009c\t\u0086\u007f\u0097\u008d\u008e\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u009d\n\b\u0087\u0018\u0019\u0092\u008f\u001c\u001d\u001e\u001f\u0080\u0081\u0082\u0083\u0084\n\u0017\u001b\u0088\u0089\u008a\u008b\u008c\u0005\u0006\u0007\u0090\u0091\u0016\u0093\u0094\u0095\u0096\u0004\u0098\u0099\u009a\u009b\u0014\u0015\u009e\u001a  \u0161\u00e4\u0105\u012f\u016b\u00e5\u0113\u017e¢.<(+|&\u00e9\u0119\u0117\u010d\u0173\u201e\u201c\u0123\u00df!$*);¬-/\u0160\u00c4\u0104\u012e\u016a\u00c5\u0112\u017d¦,%_>?\u00f8\u00c9\u0118\u0116\u010c\u0172\u012a\u013b\u0122`:#@'=\"".toCharArray();
        c2b = new char[768];
        c2bIndex = new char[256];
        final char[] b2c2 = IBM1112.b2c;
        final char[] charArray = "\u00d8abcdefghi«»\u0101\u017c\u0144±°jklmnopqr\u0156\u0157\u00e6\u0137\u00c6¤µ~stuvwxyz\u201d\u017a\u0100\u017b\u0143®^£\u012b·©§¶¼½¾[]\u0179\u0136\u013c\u00d7{ABCDEFGHI\u00ad\u014d\u00f6\u0146\u00f3\u00f5}JKLMNOPQR¹\u0107\u00fc\u0142\u015b\u2019\\\u00f7STUVWXYZ²\u014c\u00d6\u0145\u00d3\u00d50123456789³\u0106\u00dc\u0141\u015a\u009f\u0000\u0001\u0002\u0003\u009c\t\u0086\u007f\u0097\u008d\u008e\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u009d\n\b\u0087\u0018\u0019\u0092\u008f\u001c\u001d\u001e\u001f\u0080\u0081\u0082\u0083\u0084\n\u0017\u001b\u0088\u0089\u008a\u008b\u008c\u0005\u0006\u0007\u0090\u0091\u0016\u0093\u0094\u0095\u0096\u0004\u0098\u0099\u009a\u009b\u0014\u0015\u009e\u001a  \u0161\u00e4\u0105\u012f\u016b\u00e5\u0113\u017e¢.<(+|&\u00e9\u0119\u0117\u010d\u0173\u201e\u201c\u0123\u00df!$*);¬-/\u0160\u00c4\u0104\u012e\u016a\u00c5\u0112\u017d¦,%_>?\u00f8\u00c9\u0118\u0116\u010c\u0172\u012a\u013b\u0122`:#@'=\"".toCharArray();
        charArray[165] = '\ufffd';
        SingleByte.initC2B(charArray, new char[] { '\u0015', '\u0085' }, IBM1112.c2b, IBM1112.c2bIndex);
    }
}
