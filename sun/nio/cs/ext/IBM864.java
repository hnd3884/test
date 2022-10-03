package sun.nio.cs.ext;

import java.nio.charset.CharsetEncoder;
import sun.nio.cs.SingleByte;
import java.nio.charset.CharsetDecoder;
import sun.nio.cs.HistoricallyNamedCharset;
import java.nio.charset.Charset;

public class IBM864 extends Charset implements HistoricallyNamedCharset
{
    private static final String b2cTable = "°·\u2219\u221a\u2592\u2500\u2502\u253c\u2524\u252c\u251c\u2534\u2510\u250c\u2514\u2518\u03b2\u221e\u03c6±½¼\u2248«»\ufef7\ufef8\ufffd\ufffd\ufefb\ufefc\ufffd \u00ad\ufe82£¤\ufe84\ufffd\ufffd\ufe8e\ufe8f\ufe95\ufe99\u060c\ufe9d\ufea1\ufea5\u0660\u0661\u0662\u0663\u0664\u0665\u0666\u0667\u0668\u0669\ufed1\u061b\ufeb1\ufeb5\ufeb9\u061f¢\ufe80\ufe81\ufe83\ufe85\ufeca\ufe8b\ufe8d\ufe91\ufe93\ufe97\ufe9b\ufe9f\ufea3\ufea7\ufea9\ufeab\ufead\ufeaf\ufeb3\ufeb7\ufebb\ufebf\ufec1\ufec5\ufecb\ufecf¦¬\u00f7\u00d7\ufec9\u0640\ufed3\ufed7\ufedb\ufedf\ufee3\ufee7\ufeeb\ufeed\ufeef\ufef3\ufebd\ufecc\ufece\ufecd\ufee1\ufe7d\u0651\ufee5\ufee9\ufeec\ufef0\ufef2\ufed0\ufed5\ufef5\ufef6\ufedd\ufed9\ufef1\u25a0\ufffd\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$\u066a&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f";
    private static final char[] b2c;
    private static final char[] c2b;
    private static final char[] c2bIndex;
    
    public IBM864() {
        super("IBM864", ExtendedCharsets.aliasesFor("IBM864"));
    }
    
    @Override
    public String historicalName() {
        return "Cp864";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset instanceof IBM864;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new SingleByte.Decoder(this, IBM864.b2c);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new SingleByte.Encoder(this, IBM864.c2b, IBM864.c2bIndex);
    }
    
    static {
        b2c = "°·\u2219\u221a\u2592\u2500\u2502\u253c\u2524\u252c\u251c\u2534\u2510\u250c\u2514\u2518\u03b2\u221e\u03c6±½¼\u2248«»\ufef7\ufef8\ufffd\ufffd\ufefb\ufefc\ufffd \u00ad\ufe82£¤\ufe84\ufffd\ufffd\ufe8e\ufe8f\ufe95\ufe99\u060c\ufe9d\ufea1\ufea5\u0660\u0661\u0662\u0663\u0664\u0665\u0666\u0667\u0668\u0669\ufed1\u061b\ufeb1\ufeb5\ufeb9\u061f¢\ufe80\ufe81\ufe83\ufe85\ufeca\ufe8b\ufe8d\ufe91\ufe93\ufe97\ufe9b\ufe9f\ufea3\ufea7\ufea9\ufeab\ufead\ufeaf\ufeb3\ufeb7\ufebb\ufebf\ufec1\ufec5\ufecb\ufecf¦¬\u00f7\u00d7\ufec9\u0640\ufed3\ufed7\ufedb\ufedf\ufee3\ufee7\ufeeb\ufeed\ufeef\ufef3\ufebd\ufecc\ufece\ufecd\ufee1\ufe7d\u0651\ufee5\ufee9\ufeec\ufef0\ufef2\ufed0\ufed5\ufef5\ufef6\ufedd\ufed9\ufef1\u25a0\ufffd\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$\u066a&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f".toCharArray();
        c2b = new char[1792];
        c2bIndex = new char[256];
        SingleByte.initC2B(IBM864.b2c, null, IBM864.c2b, IBM864.c2bIndex);
    }
}
