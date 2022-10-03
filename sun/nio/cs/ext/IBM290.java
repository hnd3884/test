package sun.nio.cs.ext;

import java.nio.charset.CharsetEncoder;
import sun.nio.cs.SingleByte;
import java.nio.charset.CharsetDecoder;
import sun.nio.cs.HistoricallyNamedCharset;
import java.nio.charset.Charset;

public class IBM290 extends Charset implements HistoricallyNamedCharset
{
    private static final String b2cTable = "]\uff71\uff72\uff73\uff74\uff75\uff76\uff77\uff78\uff79\uff7aq\uff7b\uff7c\uff7d\uff7e\uff7f\uff80\uff81\uff82\uff83\uff84\uff85\uff86\uff87\uff88\uff89r\ufffd\uff8a\uff8b\uff8c~\u203e\uff8d\uff8e\uff8f\uff90\uff91\uff92\uff93\uff94\uff95s\uff96\uff97\uff98\uff99^¢\\tuvwxyz\uff9a\uff9b\uff9c\uff9d\uff9e\uff9f{ABCDEFGHI\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd}JKLMNOPQR\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd$\ufffdSTUVWXYZ\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd0123456789\ufffd\ufffd\ufffd\ufffd\ufffd\u009f\u0000\u0001\u0002\u0003\u009c\t\u0086\u007f\u0097\u008d\u008e\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u009d\u0085\b\u0087\u0018\u0019\u0092\u008f\u001c\u001d\u001e\u001f\u0080\u0081\u0082\u0083\u0084\n\u0017\u001b\u0088\u0089\u008a\u008b\u008c\u0005\u0006\u0007\u0090\u0091\u0016\u0093\u0094\u0095\u0096\u0004\u0098\u0099\u009a\u009b\u0014\u0015\u009e\u001a \uff61\uff62\uff63\uff64\uff65\uff66\uff67\uff68\uff69£.<(+|&\uff6a\uff6b\uff6c\uff6d\uff6e\uff6f\ufffd\uff70\ufffd!¥*);¬-/abcdefgh\ufffd,%_>?[ijklmnop`:#@'=\"";
    private static final char[] b2c;
    private static final char[] c2b;
    private static final char[] c2bIndex;
    
    public IBM290() {
        super("IBM290", ExtendedCharsets.aliasesFor("IBM290"));
    }
    
    @Override
    public String historicalName() {
        return "Cp290";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset instanceof IBM290;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new SingleByte.Decoder(this, IBM290.b2c);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new SingleByte.Encoder(this, IBM290.c2b, IBM290.c2bIndex);
    }
    
    static {
        b2c = "]\uff71\uff72\uff73\uff74\uff75\uff76\uff77\uff78\uff79\uff7aq\uff7b\uff7c\uff7d\uff7e\uff7f\uff80\uff81\uff82\uff83\uff84\uff85\uff86\uff87\uff88\uff89r\ufffd\uff8a\uff8b\uff8c~\u203e\uff8d\uff8e\uff8f\uff90\uff91\uff92\uff93\uff94\uff95s\uff96\uff97\uff98\uff99^¢\\tuvwxyz\uff9a\uff9b\uff9c\uff9d\uff9e\uff9f{ABCDEFGHI\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd}JKLMNOPQR\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd$\ufffdSTUVWXYZ\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd0123456789\ufffd\ufffd\ufffd\ufffd\ufffd\u009f\u0000\u0001\u0002\u0003\u009c\t\u0086\u007f\u0097\u008d\u008e\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u009d\u0085\b\u0087\u0018\u0019\u0092\u008f\u001c\u001d\u001e\u001f\u0080\u0081\u0082\u0083\u0084\n\u0017\u001b\u0088\u0089\u008a\u008b\u008c\u0005\u0006\u0007\u0090\u0091\u0016\u0093\u0094\u0095\u0096\u0004\u0098\u0099\u009a\u009b\u0014\u0015\u009e\u001a \uff61\uff62\uff63\uff64\uff65\uff66\uff67\uff68\uff69£.<(+|&\uff6a\uff6b\uff6c\uff6d\uff6e\uff6f\ufffd\uff70\ufffd!¥*);¬-/abcdefgh\ufffd,%_>?[ijklmnop`:#@'=\"".toCharArray();
        c2b = new char[768];
        c2bIndex = new char[256];
        SingleByte.initC2B(IBM290.b2c, new char[] { 'K', '\uff0e', 'L', '\uff1c', 'M', '\uff08', 'N', '\uff0b', 'O', '\uff5c', 'P', '\uff06', 'Z', '\uff01', '\\', '\uff0a', ']', '\uff09', '^', '\uff1b', '`', '\uff0d', 'a', '\uff0f', 'b', '\uff41', 'c', '\uff42', 'd', '\uff43', 'e', '\uff44', 'f', '\uff45', 'g', '\uff46', 'h', '\uff47', 'i', '\uff48', 'k', '\uff0c', 'l', '\uff05', 'm', '\uff3f', 'n', '\uff1e', 'o', '\uff1f', 'p', '\uff3b', 'q', '\uff49', 'r', '\uff4a', 's', '\uff4b', 't', '\uff4c', 'u', '\uff4d', 'v', '\uff4e', 'w', '\uff4f', 'x', '\uff50', 'y', '\uff40', 'z', '\uff1a', '{', '\uff03', '|', '\uff20', '}', '\uff07', '~', '\uff1d', '\u007f', '\uff02', '\u0080', '\uff3d', '\u008b', '\uff51', '\u009b', '\uff52', ' ', '\uff5e', '«', '\uff53', '°', '\uff3e', '²', '\uff3c', '³', '\uff54', '´', '\uff55', 'µ', '\uff56', '¶', '\uff57', '·', '\uff58', '¸', '\uff59', '¹', '\uff5a', '\u00c0', '\uff5b', '\u00c1', '\uff21', '\u00c2', '\uff22', '\u00c3', '\uff23', '\u00c4', '\uff24', '\u00c5', '\uff25', '\u00c6', '\uff26', '\u00c7', '\uff27', '\u00c8', '\uff28', '\u00c9', '\uff29', '\u00d0', '\uff5d', '\u00d1', '\uff2a', '\u00d2', '\uff2b', '\u00d3', '\uff2c', '\u00d4', '\uff2d', '\u00d5', '\uff2e', '\u00d6', '\uff2f', '\u00d7', '\uff30', '\u00d8', '\uff31', '\u00d9', '\uff32', '\u00e0', '\uff04', '\u00e2', '\uff33', '\u00e3', '\uff34', '\u00e4', '\uff35', '\u00e5', '\uff36', '\u00e6', '\uff37', '\u00e7', '\uff38', '\u00e8', '\uff39', '\u00e9', '\uff3a', '\u00f0', '\uff10', '\u00f1', '\uff11', '\u00f2', '\uff12', '\u00f3', '\uff13', '\u00f4', '\uff14', '\u00f5', '\uff15', '\u00f6', '\uff16', '\u00f7', '\uff17', '\u00f8', '\uff18', '\u00f9', '\uff19' }, IBM290.c2b, IBM290.c2bIndex);
    }
}
