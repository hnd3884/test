package sun.nio.cs.ext;

import java.nio.charset.CharsetEncoder;
import sun.nio.cs.SingleByte;
import java.nio.charset.CharsetDecoder;
import sun.nio.cs.HistoricallyNamedCharset;
import java.nio.charset.Charset;

public class IBM1097 extends Charset implements HistoricallyNamedCharset
{
    private static final String b2cTable = "\ufb8aabcdefghi«»\ufeb1\ufeb3\ufeb5\ufeb7\ufeb9jklmnopqr\ufebb\ufebd\ufebf\ufec1\ufec3\ufec5\ufec7~stuvwxyz\ufec9\ufeca\ufecb\ufecc\ufecd\ufece\ufecf\ufed0\ufed1\ufed3\ufed5\ufed7\ufb8e\ufedb\ufb92\ufb94[]\ufedd\ufedf\ufee1\u00d7{ABCDEFGHI\u00ad\ufee3\ufee5\ufee7\ufeed\ufee9}JKLMNOPQR\ufeeb\ufeec\ufba4\ufbfc\ufbfd\ufbfe\\\u061fSTUVWXYZ\u0640\u06f0\u06f1\u06f2\u06f3\u06f40123456789\u06f5\u06f6\u06f7\u06f8\u06f9\u009f\u0000\u0001\u0002\u0003\u009c\t\u0086\u007f\u0097\u008d\u008e\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u009d\u0085\b\u0087\u0018\u0019\u0092\u008f\u001c\u001d\u001e\u001f\u0080\u0081\u0082\u0083\u0084\n\u0017\u001b\u0088\u0089\u008a\u008b\u008c\u0005\u0006\u0007\u0090\u0091\u0016\u0093\u0094\u0095\u0096\u0004\u0098\u0099\u009a\u009b\u0014\u0015\u009e\u001a  \u060c\u064b\ufe81\ufe82\uf8fa\ufe8d\ufe8e\uf8fb¤.<(+|&\ufe80\ufe83\ufe84\uf8f9\ufe85\ufe8b\ufe8f\ufe91\ufb56!$*);¬-/\ufb58\ufe95\ufe97\ufe99\ufe9b\ufe9d\ufe9f\ufb7a\u061b,%_>?\ufb7c\ufea1\ufea3\ufea5\ufea7\ufea9\ufeab\ufead\ufeaf`:#@'=\"";
    private static final char[] b2c;
    private static final char[] c2b;
    private static final char[] c2bIndex;
    
    public IBM1097() {
        super("x-IBM1097", ExtendedCharsets.aliasesFor("x-IBM1097"));
    }
    
    @Override
    public String historicalName() {
        return "Cp1097";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset instanceof IBM1097;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new SingleByte.Decoder(this, IBM1097.b2c);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new SingleByte.Encoder(this, IBM1097.c2b, IBM1097.c2bIndex);
    }
    
    static {
        b2c = "\ufb8aabcdefghi«»\ufeb1\ufeb3\ufeb5\ufeb7\ufeb9jklmnopqr\ufebb\ufebd\ufebf\ufec1\ufec3\ufec5\ufec7~stuvwxyz\ufec9\ufeca\ufecb\ufecc\ufecd\ufece\ufecf\ufed0\ufed1\ufed3\ufed5\ufed7\ufb8e\ufedb\ufb92\ufb94[]\ufedd\ufedf\ufee1\u00d7{ABCDEFGHI\u00ad\ufee3\ufee5\ufee7\ufeed\ufee9}JKLMNOPQR\ufeeb\ufeec\ufba4\ufbfc\ufbfd\ufbfe\\\u061fSTUVWXYZ\u0640\u06f0\u06f1\u06f2\u06f3\u06f40123456789\u06f5\u06f6\u06f7\u06f8\u06f9\u009f\u0000\u0001\u0002\u0003\u009c\t\u0086\u007f\u0097\u008d\u008e\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u009d\u0085\b\u0087\u0018\u0019\u0092\u008f\u001c\u001d\u001e\u001f\u0080\u0081\u0082\u0083\u0084\n\u0017\u001b\u0088\u0089\u008a\u008b\u008c\u0005\u0006\u0007\u0090\u0091\u0016\u0093\u0094\u0095\u0096\u0004\u0098\u0099\u009a\u009b\u0014\u0015\u009e\u001a  \u060c\u064b\ufe81\ufe82\uf8fa\ufe8d\ufe8e\uf8fb¤.<(+|&\ufe80\ufe83\ufe84\uf8f9\ufe85\ufe8b\ufe8f\ufe91\ufb56!$*);¬-/\ufb58\ufe95\ufe97\ufe99\ufe9b\ufe9d\ufe9f\ufb7a\u061b,%_>?\ufb7c\ufea1\ufea3\ufea5\ufea7\ufea9\ufeab\ufead\ufeaf`:#@'=\"".toCharArray();
        c2b = new char[1280];
        c2bIndex = new char[256];
        SingleByte.initC2B(IBM1097.b2c, null, IBM1097.c2b, IBM1097.c2bIndex);
    }
}
