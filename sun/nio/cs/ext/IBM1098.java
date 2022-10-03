package sun.nio.cs.ext;

import java.nio.charset.CharsetEncoder;
import sun.nio.cs.SingleByte;
import java.nio.charset.CharsetDecoder;
import sun.nio.cs.HistoricallyNamedCharset;
import java.nio.charset.Charset;

public class IBM1098 extends Charset implements HistoricallyNamedCharset
{
    private static final String b2cTable = "\ufffd\ufffd\u060c\u061b\u061f\u064b\ufe81\ufe82\uf8fa\ufe8d\ufe8e\uf8fb\ufe80\ufe83\ufe84\uf8f9\ufe85\ufe8b\ufe8f\ufe91\ufb56\ufb58\ufe95\ufe97\ufe99\ufe9b\ufe9d\ufe9f\ufb7a\ufb7c\u00d7\ufea1\ufea3\ufea5\ufea7\ufea9\ufeab\ufead\ufeaf\ufb8a\ufeb1\ufeb3\ufeb5\ufeb7\ufeb9\ufebb«»\u2591\u2592\u2593\u2502\u2524\ufebd\ufebf\ufec1\ufec3\u2563\u2551\u2557\u255d¤\ufec5\u2510\u2514\u2534\u252c\u251c\u2500\u253c\ufec7\ufec9\u255a\u2554\u2569\u2566\u2560\u2550\u256c\ufffd\ufeca\ufecb\ufecc\ufecd\ufece\ufecf\ufed0\ufed1\ufed3\u2518\u250c\u2588\u2584\ufed5\ufed7\u2580\ufb8e\ufedb\ufb92\ufb94\ufedd\ufedf\ufee1\ufee3\ufee5\ufee7\ufeed\ufee9\ufeeb\ufeec\ufba4\ufbfc\u00ad\ufbfd\ufbfe\u0640\u06f0\u06f1\u06f2\u06f3\u06f4\u06f5\u06f6\u06f7\u06f8\u06f9\u25a0 \u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f";
    private static final char[] b2c;
    private static final char[] c2b;
    private static final char[] c2bIndex;
    
    public IBM1098() {
        super("x-IBM1098", ExtendedCharsets.aliasesFor("x-IBM1098"));
    }
    
    @Override
    public String historicalName() {
        return "Cp1098";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset instanceof IBM1098;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new SingleByte.Decoder(this, IBM1098.b2c);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new SingleByte.Encoder(this, IBM1098.c2b, IBM1098.c2bIndex);
    }
    
    static {
        b2c = "\ufffd\ufffd\u060c\u061b\u061f\u064b\ufe81\ufe82\uf8fa\ufe8d\ufe8e\uf8fb\ufe80\ufe83\ufe84\uf8f9\ufe85\ufe8b\ufe8f\ufe91\ufb56\ufb58\ufe95\ufe97\ufe99\ufe9b\ufe9d\ufe9f\ufb7a\ufb7c\u00d7\ufea1\ufea3\ufea5\ufea7\ufea9\ufeab\ufead\ufeaf\ufb8a\ufeb1\ufeb3\ufeb5\ufeb7\ufeb9\ufebb«»\u2591\u2592\u2593\u2502\u2524\ufebd\ufebf\ufec1\ufec3\u2563\u2551\u2557\u255d¤\ufec5\u2510\u2514\u2534\u252c\u251c\u2500\u253c\ufec7\ufec9\u255a\u2554\u2569\u2566\u2560\u2550\u256c\ufffd\ufeca\ufecb\ufecc\ufecd\ufece\ufecf\ufed0\ufed1\ufed3\u2518\u250c\u2588\u2584\ufed5\ufed7\u2580\ufb8e\ufedb\ufb92\ufb94\ufedd\ufedf\ufee1\ufee3\ufee5\ufee7\ufeed\ufee9\ufeeb\ufeec\ufba4\ufbfc\u00ad\ufbfd\ufbfe\u0640\u06f0\u06f1\u06f2\u06f3\u06f4\u06f5\u06f6\u06f7\u06f8\u06f9\u25a0 \u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f".toCharArray();
        c2b = new char[1792];
        c2bIndex = new char[256];
        SingleByte.initC2B(IBM1098.b2c, null, IBM1098.c2b, IBM1098.c2bIndex);
    }
}
