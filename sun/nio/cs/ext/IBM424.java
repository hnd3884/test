package sun.nio.cs.ext;

import java.nio.charset.CharsetEncoder;
import sun.nio.cs.SingleByte;
import java.nio.charset.CharsetDecoder;
import sun.nio.cs.HistoricallyNamedCharset;
import java.nio.charset.Charset;

public class IBM424 extends Charset implements HistoricallyNamedCharset
{
    private static final String b2cTable = "\ufffdabcdefghi«»\ufffd\ufffd\ufffd±°jklmnopqr\ufffd\ufffd\ufffd¸\ufffd¤µ~stuvwxyz\ufffd\ufffd\ufffd\ufffd\ufffd®^£¥\u2022©§¶¼½¾[]\u203e¨´\u00d7{ABCDEFGHI\u00ad\ufffd\ufffd\ufffd\ufffd\ufffd}JKLMNOPQR¹\ufffd\ufffd\ufffd\ufffd\ufffd\\\u00f7STUVWXYZ²\ufffd\ufffd\ufffd\ufffd\ufffd0123456789³\ufffd\ufffd\ufffd\ufffd\u009f\u0000\u0001\u0002\u0003\u009c\t\u0086\u007f\u0097\u008d\u008e\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u009d\n\b\u0087\u0018\u0019\u0092\u008f\u001c\u001d\u001e\u001f\u0080\u0081\u0082\u0083\u0084\n\u0017\u001b\u0088\u0089\u008a\u008b\u008c\u0005\u0006\u0007\u0090\u0091\u0016\u0093\u0094\u0095\u0096\u0004\u0098\u0099\u009a\u009b\u0014\u0015\u009e\u001a \u05d0\u05d1\u05d2\u05d3\u05d4\u05d5\u05d6\u05d7\u05d8¢.<(+|&\u05d9\u05da\u05db\u05dc\u05dd\u05de\u05df\u05e0\u05e1!$*);¬-/\u05e2\u05e3\u05e4\u05e5\u05e6\u05e7\u05e8\u05e9¦,%_>?\ufffd\u05ea\ufffd\ufffd \ufffd\ufffd\ufffd\u2017`:#@'=\"";
    private static final char[] b2c;
    private static final char[] c2b;
    private static final char[] c2bIndex;
    
    public IBM424() {
        super("IBM424", ExtendedCharsets.aliasesFor("IBM424"));
    }
    
    @Override
    public String historicalName() {
        return "Cp424";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset instanceof IBM424;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new SingleByte.Decoder(this, IBM424.b2c);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new SingleByte.Encoder(this, IBM424.c2b, IBM424.c2bIndex);
    }
    
    static {
        b2c = "\ufffdabcdefghi«»\ufffd\ufffd\ufffd±°jklmnopqr\ufffd\ufffd\ufffd¸\ufffd¤µ~stuvwxyz\ufffd\ufffd\ufffd\ufffd\ufffd®^£¥\u2022©§¶¼½¾[]\u203e¨´\u00d7{ABCDEFGHI\u00ad\ufffd\ufffd\ufffd\ufffd\ufffd}JKLMNOPQR¹\ufffd\ufffd\ufffd\ufffd\ufffd\\\u00f7STUVWXYZ²\ufffd\ufffd\ufffd\ufffd\ufffd0123456789³\ufffd\ufffd\ufffd\ufffd\u009f\u0000\u0001\u0002\u0003\u009c\t\u0086\u007f\u0097\u008d\u008e\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u009d\n\b\u0087\u0018\u0019\u0092\u008f\u001c\u001d\u001e\u001f\u0080\u0081\u0082\u0083\u0084\n\u0017\u001b\u0088\u0089\u008a\u008b\u008c\u0005\u0006\u0007\u0090\u0091\u0016\u0093\u0094\u0095\u0096\u0004\u0098\u0099\u009a\u009b\u0014\u0015\u009e\u001a \u05d0\u05d1\u05d2\u05d3\u05d4\u05d5\u05d6\u05d7\u05d8¢.<(+|&\u05d9\u05da\u05db\u05dc\u05dd\u05de\u05df\u05e0\u05e1!$*);¬-/\u05e2\u05e3\u05e4\u05e5\u05e6\u05e7\u05e8\u05e9¦,%_>?\ufffd\u05ea\ufffd\ufffd \ufffd\ufffd\ufffd\u2017`:#@'=\"".toCharArray();
        c2b = new char[1024];
        c2bIndex = new char[256];
        final char[] b2c2 = IBM424.b2c;
        final char[] charArray = "\ufffdabcdefghi«»\ufffd\ufffd\ufffd±°jklmnopqr\ufffd\ufffd\ufffd¸\ufffd¤µ~stuvwxyz\ufffd\ufffd\ufffd\ufffd\ufffd®^£¥\u2022©§¶¼½¾[]\u203e¨´\u00d7{ABCDEFGHI\u00ad\ufffd\ufffd\ufffd\ufffd\ufffd}JKLMNOPQR¹\ufffd\ufffd\ufffd\ufffd\ufffd\\\u00f7STUVWXYZ²\ufffd\ufffd\ufffd\ufffd\ufffd0123456789³\ufffd\ufffd\ufffd\ufffd\u009f\u0000\u0001\u0002\u0003\u009c\t\u0086\u007f\u0097\u008d\u008e\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u009d\n\b\u0087\u0018\u0019\u0092\u008f\u001c\u001d\u001e\u001f\u0080\u0081\u0082\u0083\u0084\n\u0017\u001b\u0088\u0089\u008a\u008b\u008c\u0005\u0006\u0007\u0090\u0091\u0016\u0093\u0094\u0095\u0096\u0004\u0098\u0099\u009a\u009b\u0014\u0015\u009e\u001a \u05d0\u05d1\u05d2\u05d3\u05d4\u05d5\u05d6\u05d7\u05d8¢.<(+|&\u05d9\u05da\u05db\u05dc\u05dd\u05de\u05df\u05e0\u05e1!$*);¬-/\u05e2\u05e3\u05e4\u05e5\u05e6\u05e7\u05e8\u05e9¦,%_>?\ufffd\u05ea\ufffd\ufffd \ufffd\ufffd\ufffd\u2017`:#@'=\"".toCharArray();
        charArray[165] = '\ufffd';
        SingleByte.initC2B(charArray, new char[] { '\u0015', '\u0085' }, IBM424.c2b, IBM424.c2bIndex);
    }
}
