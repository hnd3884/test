package sun.nio.cs.ext;

import java.nio.charset.CharsetEncoder;
import sun.nio.cs.SingleByte;
import java.nio.charset.CharsetDecoder;
import sun.nio.cs.HistoricallyNamedCharset;
import java.nio.charset.Charset;

public class IBM1006 extends Charset implements HistoricallyNamedCharset
{
    private static final String b2cTable = "\u0080\u0081\u0082\u0083\u0084\u0085\u0086\u0087\u0088\u0089\u008a\u008b\u008c\u008d\u008e\u008f\u0090\u0091\u0092\u0093\u0094\u0095\u0096\u0097\u0098\u0099\u009a\u009b\u009c\u009d\u009e\u009f \u06f0\u06f1\u06f2\u06f3\u06f4\u06f5\u06f6\u06f7\u06f8\u06f9\u060c\u061b\u00ad\u061f\ufe81\ufe8d\ufe8e\uf8fb\ufe8f\ufe91\ufb56\ufb58\ufe93\ufe95\ufe97\ufb66\ufb68\ufe99\ufe9b\ufe9d\ufe9f\ufb7a\ufb7c\ufea1\ufea3\ufea5\ufea7\ufea9\ufb88\ufeab\ufead\ufb8c\ufeaf\ufb8a\ufeb1\ufeb3\ufeb5\ufeb7\ufeb9\ufebb\ufebd\ufebf\ufec3\ufec7\ufec9\ufeca\ufecb\ufecc\ufecd\ufece\ufecf\ufed0\ufed1\ufed3\ufed5\ufed7\ufb8e\ufedb\ufb92\ufb94\ufedd\ufedf\ufee0\ufee1\ufee3\ufb9e\ufee5\ufee7\ufe85\ufeed\ufba6\ufba8\ufba9\ufbaa\ufe80\ufe89\ufe8a\ufe8b\ufbfc\ufbfd\ufbfe\ufbb0\ufbae\ufe7c\ufe7d\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f";
    private static final char[] b2c;
    private static final char[] c2b;
    private static final char[] c2bIndex;
    
    public IBM1006() {
        super("x-IBM1006", ExtendedCharsets.aliasesFor("x-IBM1006"));
    }
    
    @Override
    public String historicalName() {
        return "Cp1006";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset instanceof IBM1006;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new SingleByte.Decoder(this, IBM1006.b2c);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new SingleByte.Encoder(this, IBM1006.c2b, IBM1006.c2bIndex);
    }
    
    static {
        b2c = "\u0080\u0081\u0082\u0083\u0084\u0085\u0086\u0087\u0088\u0089\u008a\u008b\u008c\u008d\u008e\u008f\u0090\u0091\u0092\u0093\u0094\u0095\u0096\u0097\u0098\u0099\u009a\u009b\u009c\u009d\u009e\u009f \u06f0\u06f1\u06f2\u06f3\u06f4\u06f5\u06f6\u06f7\u06f8\u06f9\u060c\u061b\u00ad\u061f\ufe81\ufe8d\ufe8e\uf8fb\ufe8f\ufe91\ufb56\ufb58\ufe93\ufe95\ufe97\ufb66\ufb68\ufe99\ufe9b\ufe9d\ufe9f\ufb7a\ufb7c\ufea1\ufea3\ufea5\ufea7\ufea9\ufb88\ufeab\ufead\ufb8c\ufeaf\ufb8a\ufeb1\ufeb3\ufeb5\ufeb7\ufeb9\ufebb\ufebd\ufebf\ufec3\ufec7\ufec9\ufeca\ufecb\ufecc\ufecd\ufece\ufecf\ufed0\ufed1\ufed3\ufed5\ufed7\ufb8e\ufedb\ufb92\ufb94\ufedd\ufedf\ufee0\ufee1\ufee3\ufb9e\ufee5\ufee7\ufe85\ufeed\ufba6\ufba8\ufba9\ufbaa\ufe80\ufe89\ufe8a\ufe8b\ufbfc\ufbfd\ufbfe\ufbb0\ufbae\ufe7c\ufe7d\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f".toCharArray();
        c2b = new char[1280];
        c2bIndex = new char[256];
        SingleByte.initC2B(IBM1006.b2c, null, IBM1006.c2b, IBM1006.c2bIndex);
    }
}
