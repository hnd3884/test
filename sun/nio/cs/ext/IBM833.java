package sun.nio.cs.ext;

import java.nio.charset.CharsetEncoder;
import sun.nio.cs.SingleByte;
import java.nio.charset.CharsetDecoder;
import sun.nio.cs.HistoricallyNamedCharset;
import java.nio.charset.Charset;

public class IBM833 extends Charset implements HistoricallyNamedCharset
{
    private static final String b2cTable = "]abcdefghi\uffc2\uffc3\uffc4\uffc5\uffc6\uffc7\ufffdjklmnopqr\uffca\uffcb\uffcc\uffcd\uffce\uffcf\u203e~stuvwxyz\uffd2\uffd3\uffd4\uffd5\uffd6\uffd7^\ufffd\\\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\uffda\uffdb\uffdc\ufffd\ufffd\ufffd{ABCDEFGHI\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd}JKLMNOPQR\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u20a9\ufffdSTUVWXYZ\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd0123456789\ufffd\ufffd\ufffd\ufffd\ufffd\u009f\u0000\u0001\u0002\u0003\u009c\t\u0086\u007f\u0097\u008d\u008e\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u009d\u0085\b\u0087\u0018\u0019\u0092\u008f\u001c\u001d\u001e\u001f\u0080\u0081\u0082\u0083\u0084\n\u0017\u001b\u0088\u0089\u008a\u008b\u008c\u0005\u0006\u0007\u0090\u0091\u0016\u0093\u0094\u0095\u0096\u0004\u0098\u0099\u009a\u009b\u0014\u0015\u009e\u001a \ufffd\uffa0\uffa1\uffa2\uffa3\uffa4\uffa5\uffa6\uffa7¢.<(+|&\ufffd\uffa8\uffa9\uffaa\uffab\uffac\uffad\uffae\uffaf!$*);¬-/\uffb0\uffb1\uffb2\uffb3\uffb4\uffb5\uffb6\uffb7¦,%_>?[\ufffd\uffb8\uffb9\uffba\uffbb\uffbc\uffbd\uffbe`:#@'=\"";
    private static final char[] b2c;
    private static final char[] c2b;
    private static final char[] c2bIndex;
    
    public IBM833() {
        super("x-IBM833", ExtendedCharsets.aliasesFor("x-IBM833"));
    }
    
    @Override
    public String historicalName() {
        return "Cp833";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset instanceof IBM833;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new SingleByte.Decoder(this, IBM833.b2c);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new SingleByte.Encoder(this, IBM833.c2b, IBM833.c2bIndex);
    }
    
    static {
        b2c = "]abcdefghi\uffc2\uffc3\uffc4\uffc5\uffc6\uffc7\ufffdjklmnopqr\uffca\uffcb\uffcc\uffcd\uffce\uffcf\u203e~stuvwxyz\uffd2\uffd3\uffd4\uffd5\uffd6\uffd7^\ufffd\\\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\uffda\uffdb\uffdc\ufffd\ufffd\ufffd{ABCDEFGHI\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd}JKLMNOPQR\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u20a9\ufffdSTUVWXYZ\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd0123456789\ufffd\ufffd\ufffd\ufffd\ufffd\u009f\u0000\u0001\u0002\u0003\u009c\t\u0086\u007f\u0097\u008d\u008e\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u009d\u0085\b\u0087\u0018\u0019\u0092\u008f\u001c\u001d\u001e\u001f\u0080\u0081\u0082\u0083\u0084\n\u0017\u001b\u0088\u0089\u008a\u008b\u008c\u0005\u0006\u0007\u0090\u0091\u0016\u0093\u0094\u0095\u0096\u0004\u0098\u0099\u009a\u009b\u0014\u0015\u009e\u001a \ufffd\uffa0\uffa1\uffa2\uffa3\uffa4\uffa5\uffa6\uffa7¢.<(+|&\ufffd\uffa8\uffa9\uffaa\uffab\uffac\uffad\uffae\uffaf!$*);¬-/\uffb0\uffb1\uffb2\uffb3\uffb4\uffb5\uffb6\uffb7¦,%_>?[\ufffd\uffb8\uffb9\uffba\uffbb\uffbc\uffbd\uffbe`:#@'=\"".toCharArray();
        c2b = new char[768];
        c2bIndex = new char[256];
        SingleByte.initC2B(IBM833.b2c, new char[] { 'Z', '\uff01', '\u007f', '\uff02', '{', '\uff03', '[', '\uff04', 'l', '\uff05', 'P', '\uff06', '}', '\uff07', 'M', '\uff08', ']', '\uff09', '\\', '\uff0a', 'N', '\uff0b', 'k', '\uff0c', '`', '\uff0d', 'K', '\uff0e', 'a', '\uff0f', '\u00f0', '\uff10', '\u00f1', '\uff11', '\u00f2', '\uff12', '\u00f3', '\uff13', '\u00f4', '\uff14', '\u00f5', '\uff15', '\u00f6', '\uff16', '\u00f7', '\uff17', '\u00f8', '\uff18', '\u00f9', '\uff19', 'z', '\uff1a', '^', '\uff1b', 'L', '\uff1c', '~', '\uff1d', 'n', '\uff1e', 'o', '\uff1f', '|', '\uff20', '\u00c1', '\uff21', '\u00c2', '\uff22', '\u00c3', '\uff23', '\u00c4', '\uff24', '\u00c5', '\uff25', '\u00c6', '\uff26', '\u00c7', '\uff27', '\u00c8', '\uff28', '\u00c9', '\uff29', '\u00d1', '\uff2a', '\u00d2', '\uff2b', '\u00d3', '\uff2c', '\u00d4', '\uff2d', '\u00d5', '\uff2e', '\u00d6', '\uff2f', '\u00d7', '\uff30', '\u00d8', '\uff31', '\u00d9', '\uff32', '\u00e2', '\uff33', '\u00e3', '\uff34', '\u00e4', '\uff35', '\u00e5', '\uff36', '\u00e6', '\uff37', '\u00e7', '\uff38', '\u00e8', '\uff39', '\u00e9', '\uff3a', 'p', '\uff3b', '²', '\uff3c', '\u0080', '\uff3d', '°', '\uff3e', 'm', '\uff3f', 'y', '\uff40', '\u0081', '\uff41', '\u0082', '\uff42', '\u0083', '\uff43', '\u0084', '\uff44', '\u0085', '\uff45', '\u0086', '\uff46', '\u0087', '\uff47', '\u0088', '\uff48', '\u0089', '\uff49', '\u0091', '\uff4a', '\u0092', '\uff4b', '\u0093', '\uff4c', '\u0094', '\uff4d', '\u0095', '\uff4e', '\u0096', '\uff4f', '\u0097', '\uff50', '\u0098', '\uff51', '\u0099', '\uff52', '¢', '\uff53', '£', '\uff54', '¤', '\uff55', '¥', '\uff56', '¦', '\uff57', '§', '\uff58', '¨', '\uff59', '©', '\uff5a', '\u00c0', '\uff5b', 'O', '\uff5c', '\u00d0', '\uff5d', '¡', '\uff5e' }, IBM833.c2b, IBM833.c2bIndex);
    }
}
