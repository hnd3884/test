package sun.nio.cs.ext;

import java.nio.charset.CharsetEncoder;
import sun.nio.cs.SingleByte;
import java.nio.charset.CharsetDecoder;
import sun.nio.cs.HistoricallyNamedCharset;
import java.nio.charset.Charset;

public class IBM875 extends Charset implements HistoricallyNamedCharset
{
    private static final String b2cTable = "\u0385abcdefghi\u03b1\u03b2\u03b3\u03b4\u03b5\u03b6°jklmnopqr\u03b7\u03b8\u03b9\u03ba\u03bb\u03bc´~stuvwxyz\u03bd\u03be\u03bf\u03c0\u03c1\u03c3£\u03ac\u03ad\u03ae\u03ca\u03af\u03cc\u03cd\u03cb\u03ce\u03c2\u03c4\u03c5\u03c6\u03c7\u03c8{ABCDEFGHI\u00ad\u03c9\u0390\u03b0\u2018\u2015}JKLMNOPQR±½\ufffd\u0387\u2019¦\\\ufffdSTUVWXYZ²§\ufffd\ufffd«¬0123456789³©\ufffd\ufffd»\u009f\u0000\u0001\u0002\u0003\u009c\t\u0086\u007f\u0097\u008d\u008e\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u009d\n\b\u0087\u0018\u0019\u0092\u008f\u001c\u001d\u001e\u001f\u0080\u0081\u0082\u0083\u0084\n\u0017\u001b\u0088\u0089\u008a\u008b\u008c\u0005\u0006\u0007\u0090\u0091\u0016\u0093\u0094\u0095\u0096\u0004\u0098\u0099\u009a\u009b\u0014\u0015\u009e\u001a \u0391\u0392\u0393\u0394\u0395\u0396\u0397\u0398\u0399[.<(+!&\u039a\u039b\u039c\u039d\u039e\u039f\u03a0\u03a1\u03a3]$*);^-/\u03a4\u03a5\u03a6\u03a7\u03a8\u03a9\u03aa\u03ab|,%_>?¨\u0386\u0388\u0389 \u038a\u038c\u038e\u038f`:#@'=\"";
    private static final char[] b2c;
    private static final char[] c2b;
    private static final char[] c2bIndex;
    
    public IBM875() {
        super("x-IBM875", ExtendedCharsets.aliasesFor("x-IBM875"));
    }
    
    @Override
    public String historicalName() {
        return "Cp875";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset instanceof IBM875;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new SingleByte.Decoder(this, IBM875.b2c);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new SingleByte.Encoder(this, IBM875.c2b, IBM875.c2bIndex);
    }
    
    static {
        b2c = "\u0385abcdefghi\u03b1\u03b2\u03b3\u03b4\u03b5\u03b6°jklmnopqr\u03b7\u03b8\u03b9\u03ba\u03bb\u03bc´~stuvwxyz\u03bd\u03be\u03bf\u03c0\u03c1\u03c3£\u03ac\u03ad\u03ae\u03ca\u03af\u03cc\u03cd\u03cb\u03ce\u03c2\u03c4\u03c5\u03c6\u03c7\u03c8{ABCDEFGHI\u00ad\u03c9\u0390\u03b0\u2018\u2015}JKLMNOPQR±½\ufffd\u0387\u2019¦\\\ufffdSTUVWXYZ²§\ufffd\ufffd«¬0123456789³©\ufffd\ufffd»\u009f\u0000\u0001\u0002\u0003\u009c\t\u0086\u007f\u0097\u008d\u008e\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u009d\n\b\u0087\u0018\u0019\u0092\u008f\u001c\u001d\u001e\u001f\u0080\u0081\u0082\u0083\u0084\n\u0017\u001b\u0088\u0089\u008a\u008b\u008c\u0005\u0006\u0007\u0090\u0091\u0016\u0093\u0094\u0095\u0096\u0004\u0098\u0099\u009a\u009b\u0014\u0015\u009e\u001a \u0391\u0392\u0393\u0394\u0395\u0396\u0397\u0398\u0399[.<(+!&\u039a\u039b\u039c\u039d\u039e\u039f\u03a0\u03a1\u03a3]$*);^-/\u03a4\u03a5\u03a6\u03a7\u03a8\u03a9\u03aa\u03ab|,%_>?¨\u0386\u0388\u0389 \u038a\u038c\u038e\u038f`:#@'=\"".toCharArray();
        c2b = new char[1024];
        c2bIndex = new char[256];
        final char[] b2c2 = IBM875.b2c;
        final char[] charArray = "\u0385abcdefghi\u03b1\u03b2\u03b3\u03b4\u03b5\u03b6°jklmnopqr\u03b7\u03b8\u03b9\u03ba\u03bb\u03bc´~stuvwxyz\u03bd\u03be\u03bf\u03c0\u03c1\u03c3£\u03ac\u03ad\u03ae\u03ca\u03af\u03cc\u03cd\u03cb\u03ce\u03c2\u03c4\u03c5\u03c6\u03c7\u03c8{ABCDEFGHI\u00ad\u03c9\u0390\u03b0\u2018\u2015}JKLMNOPQR±½\ufffd\u0387\u2019¦\\\ufffdSTUVWXYZ²§\ufffd\ufffd«¬0123456789³©\ufffd\ufffd»\u009f\u0000\u0001\u0002\u0003\u009c\t\u0086\u007f\u0097\u008d\u008e\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u009d\n\b\u0087\u0018\u0019\u0092\u008f\u001c\u001d\u001e\u001f\u0080\u0081\u0082\u0083\u0084\n\u0017\u001b\u0088\u0089\u008a\u008b\u008c\u0005\u0006\u0007\u0090\u0091\u0016\u0093\u0094\u0095\u0096\u0004\u0098\u0099\u009a\u009b\u0014\u0015\u009e\u001a \u0391\u0392\u0393\u0394\u0395\u0396\u0397\u0398\u0399[.<(+!&\u039a\u039b\u039c\u039d\u039e\u039f\u03a0\u03a1\u03a3]$*);^-/\u03a4\u03a5\u03a6\u03a7\u03a8\u03a9\u03aa\u03ab|,%_>?¨\u0386\u0388\u0389 \u038a\u038c\u038e\u038f`:#@'=\"".toCharArray();
        charArray[165] = '\ufffd';
        SingleByte.initC2B(charArray, new char[] { '\u0015', '\u0085' }, IBM875.c2b, IBM875.c2bIndex);
    }
}
