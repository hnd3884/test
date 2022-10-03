package sun.nio.cs.ext;

import java.nio.charset.CharsetEncoder;
import sun.nio.cs.SingleByte;
import java.nio.charset.CharsetDecoder;
import sun.nio.cs.HistoricallyNamedCharset;
import java.nio.charset.Charset;

public class IBM1046 extends Charset implements HistoricallyNamedCharset
{
    private static final String b2cTable = "\ufe88\u00d7\u00f7\uf8f6\uf8f5\uf8f4\uf8f7\ufe71\u0088\u25a0\u2502\u2500\u2510\u250c\u2514\u2518\ufe79\ufe7b\ufe7d\ufe7f\ufe77\ufe8a\ufef0\ufef3\ufef2\ufece\ufecf\ufed0\ufef6\ufef8\ufefa\ufefc \uf8fa\uf8f9\uf8f8¤\uf8fb\ufe8b\ufe91\ufe97\ufe9b\ufe9f\ufea3\u060c\u00ad\ufea7\ufeb3\u0660\u0661\u0662\u0663\u0664\u0665\u0666\u0667\u0668\u0669\ufeb7\u061b\ufebb\ufebf\ufeca\u061f\ufecb\ufe80\ufe81\ufe83\ufe85\ufe87\ufe89\ufe8d\ufe8f\ufe93\ufe95\ufe99\ufe9d\ufea1\ufea5\ufea9\ufeab\ufead\ufeaf\ufeb1\ufeb5\ufeb9\ufebd\ufec3\ufec7\ufec9\ufecd\ufecc\ufe82\ufe84\ufe8e\ufed3\u0640\ufed1\ufed5\ufed9\ufedd\ufee1\ufee5\ufeeb\ufeed\ufeef\ufef1\ufe70\ufe72\ufe74\ufe76\ufe78\ufe7a\ufe7c\ufe7e\ufed7\ufedb\ufedf\uf8fc\ufef5\ufef7\ufef9\ufefb\ufee3\ufee7\ufeec\ufee9\ufffd\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f";
    private static final char[] b2c;
    private static final char[] c2b;
    private static final char[] c2bIndex;
    
    public IBM1046() {
        super("x-IBM1046", ExtendedCharsets.aliasesFor("x-IBM1046"));
    }
    
    @Override
    public String historicalName() {
        return "Cp1046";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset instanceof IBM1046;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new SingleByte.Decoder(this, IBM1046.b2c);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new SingleByte.Encoder(this, IBM1046.c2b, IBM1046.c2bIndex);
    }
    
    static {
        b2c = "\ufe88\u00d7\u00f7\uf8f6\uf8f5\uf8f4\uf8f7\ufe71\u0088\u25a0\u2502\u2500\u2510\u250c\u2514\u2518\ufe79\ufe7b\ufe7d\ufe7f\ufe77\ufe8a\ufef0\ufef3\ufef2\ufece\ufecf\ufed0\ufef6\ufef8\ufefa\ufefc \uf8fa\uf8f9\uf8f8¤\uf8fb\ufe8b\ufe91\ufe97\ufe9b\ufe9f\ufea3\u060c\u00ad\ufea7\ufeb3\u0660\u0661\u0662\u0663\u0664\u0665\u0666\u0667\u0668\u0669\ufeb7\u061b\ufebb\ufebf\ufeca\u061f\ufecb\ufe80\ufe81\ufe83\ufe85\ufe87\ufe89\ufe8d\ufe8f\ufe93\ufe95\ufe99\ufe9d\ufea1\ufea5\ufea9\ufeab\ufead\ufeaf\ufeb1\ufeb5\ufeb9\ufebd\ufec3\ufec7\ufec9\ufecd\ufecc\ufe82\ufe84\ufe8e\ufed3\u0640\ufed1\ufed5\ufed9\ufedd\ufee1\ufee5\ufeeb\ufeed\ufeef\ufef1\ufe70\ufe72\ufe74\ufe76\ufe78\ufe7a\ufe7c\ufe7e\ufed7\ufedb\ufedf\uf8fc\ufef5\ufef7\ufef9\ufefb\ufee3\ufee7\ufeec\ufee9\ufffd\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f".toCharArray();
        c2b = new char[1536];
        c2bIndex = new char[256];
        SingleByte.initC2B(IBM1046.b2c, null, IBM1046.c2b, IBM1046.c2bIndex);
    }
}
