package sun.nio.cs.ext;

import java.nio.charset.CharsetEncoder;
import sun.nio.cs.SingleByte;
import java.nio.charset.CharsetDecoder;
import sun.nio.cs.HistoricallyNamedCharset;
import java.nio.charset.Charset;

public class IBM1122 extends Charset implements HistoricallyNamedCharset
{
    private static final String b2cTable = "\u00d8abcdefghi«»\u0161\u00fd\u017e±°jklmnopqrªº\u00e6¸\u00c6]µ\u00fcstuvwxyz¡¿\u0160\u00dd\u017d®¢£¥·©[¶¼½¾¬|\u203e¨´\u00d7\u00e4ABCDEFGHI\u00ad\u00f4¦\u00f2\u00f3\u00f5\u00e5JKLMNOPQR¹\u00fb~\u00f9\u00fa\u00ff\u00c9\u00f7STUVWXYZ²\u00d4@\u00d2\u00d3\u00d50123456789³\u00db\u00dc\u00d9\u00da\u009f\u0000\u0001\u0002\u0003\u009c\t\u0086\u007f\u0097\u008d\u008e\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u009d\n\b\u0087\u0018\u0019\u0092\u008f\u001c\u001d\u001e\u001f\u0080\u0081\u0082\u0083\u0084\n\u0017\u001b\u0088\u0089\u008a\u008b\u008c\u0005\u0006\u0007\u0090\u0091\u0016\u0093\u0094\u0095\u0096\u0004\u0098\u0099\u009a\u009b\u0014\u0015\u009e\u001a  \u00e2{\u00e0\u00e1\u00e3}\u00e7\u00f1§.<(+!&`\u00ea\u00eb\u00e8\u00ed\u00ee\u00ef\u00ec\u00df¤\u00c5*);^-/\u00c2#\u00c0\u00c1\u00c3$\u00c7\u00d1\u00f6,%_>?\u00f8\\\u00ca\u00cb\u00c8\u00cd\u00ce\u00cf\u00cc\u00e9:\u00c4\u00d6'=\"";
    private static final char[] b2c;
    private static final char[] c2b;
    private static final char[] c2bIndex;
    
    public IBM1122() {
        super("x-IBM1122", ExtendedCharsets.aliasesFor("x-IBM1122"));
    }
    
    @Override
    public String historicalName() {
        return "Cp1122";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset instanceof IBM1122;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new SingleByte.Decoder(this, IBM1122.b2c);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new SingleByte.Encoder(this, IBM1122.c2b, IBM1122.c2bIndex);
    }
    
    static {
        b2c = "\u00d8abcdefghi«»\u0161\u00fd\u017e±°jklmnopqrªº\u00e6¸\u00c6]µ\u00fcstuvwxyz¡¿\u0160\u00dd\u017d®¢£¥·©[¶¼½¾¬|\u203e¨´\u00d7\u00e4ABCDEFGHI\u00ad\u00f4¦\u00f2\u00f3\u00f5\u00e5JKLMNOPQR¹\u00fb~\u00f9\u00fa\u00ff\u00c9\u00f7STUVWXYZ²\u00d4@\u00d2\u00d3\u00d50123456789³\u00db\u00dc\u00d9\u00da\u009f\u0000\u0001\u0002\u0003\u009c\t\u0086\u007f\u0097\u008d\u008e\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u009d\n\b\u0087\u0018\u0019\u0092\u008f\u001c\u001d\u001e\u001f\u0080\u0081\u0082\u0083\u0084\n\u0017\u001b\u0088\u0089\u008a\u008b\u008c\u0005\u0006\u0007\u0090\u0091\u0016\u0093\u0094\u0095\u0096\u0004\u0098\u0099\u009a\u009b\u0014\u0015\u009e\u001a  \u00e2{\u00e0\u00e1\u00e3}\u00e7\u00f1§.<(+!&`\u00ea\u00eb\u00e8\u00ed\u00ee\u00ef\u00ec\u00df¤\u00c5*);^-/\u00c2#\u00c0\u00c1\u00c3$\u00c7\u00d1\u00f6,%_>?\u00f8\\\u00ca\u00cb\u00c8\u00cd\u00ce\u00cf\u00cc\u00e9:\u00c4\u00d6'=\"".toCharArray();
        c2b = new char[768];
        c2bIndex = new char[256];
        final char[] b2c2 = IBM1122.b2c;
        final char[] charArray = "\u00d8abcdefghi«»\u0161\u00fd\u017e±°jklmnopqrªº\u00e6¸\u00c6]µ\u00fcstuvwxyz¡¿\u0160\u00dd\u017d®¢£¥·©[¶¼½¾¬|\u203e¨´\u00d7\u00e4ABCDEFGHI\u00ad\u00f4¦\u00f2\u00f3\u00f5\u00e5JKLMNOPQR¹\u00fb~\u00f9\u00fa\u00ff\u00c9\u00f7STUVWXYZ²\u00d4@\u00d2\u00d3\u00d50123456789³\u00db\u00dc\u00d9\u00da\u009f\u0000\u0001\u0002\u0003\u009c\t\u0086\u007f\u0097\u008d\u008e\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u009d\n\b\u0087\u0018\u0019\u0092\u008f\u001c\u001d\u001e\u001f\u0080\u0081\u0082\u0083\u0084\n\u0017\u001b\u0088\u0089\u008a\u008b\u008c\u0005\u0006\u0007\u0090\u0091\u0016\u0093\u0094\u0095\u0096\u0004\u0098\u0099\u009a\u009b\u0014\u0015\u009e\u001a  \u00e2{\u00e0\u00e1\u00e3}\u00e7\u00f1§.<(+!&`\u00ea\u00eb\u00e8\u00ed\u00ee\u00ef\u00ec\u00df¤\u00c5*);^-/\u00c2#\u00c0\u00c1\u00c3$\u00c7\u00d1\u00f6,%_>?\u00f8\\\u00ca\u00cb\u00c8\u00cd\u00ce\u00cf\u00cc\u00e9:\u00c4\u00d6'=\"".toCharArray();
        charArray[165] = '\ufffd';
        SingleByte.initC2B(charArray, new char[] { '\u0015', '\u0085' }, IBM1122.c2b, IBM1122.c2bIndex);
    }
}
