package sun.nio.cs.ext;

import java.nio.charset.CharsetEncoder;
import sun.nio.cs.SingleByte;
import java.nio.charset.CharsetDecoder;
import sun.nio.cs.HistoricallyNamedCharset;
import java.nio.charset.Charset;

public class IBM868 extends Charset implements HistoricallyNamedCharset
{
    private static final String b2cTable = "\u06f0\u06f1\u06f2\u06f3\u06f4\u06f5\u06f6\u06f7\u06f8\u06f9\u060c\u061b\u061f\ufe81\ufe8d\ufe8e\uf8fb\ufe8f\ufe91\ufb56\ufb58\ufe93\ufe95\ufe97\ufb66\ufb68\ufe99\ufe9b\ufe9d\ufe9f\ufb7a\ufb7c\ufea1\ufea3\ufea5\ufea7\ufea9\ufb88\ufeab\ufead\ufb8c\ufeaf\ufb8a\ufeb1\ufeb3\ufeb5«»\u2591\u2592\u2593\u2502\u2524\ufeb7\ufeb9\ufebb\ufebd\u2563\u2551\u2557\u255d\ufebf\ufec3\u2510\u2514\u2534\u252c\u251c\u2500\u253c\ufec7\ufec9\u255a\u2554\u2569\u2566\u2560\u2550\u256c\ufeca\ufecb\ufecc\ufecd\ufece\ufecf\ufed0\ufed1\ufed3\ufed5\u2518\u250c\u2588\u2584\ufed7\ufb8e\u2580\ufedb\ufb92\ufb94\ufedd\ufedf\ufee0\ufee1\ufee3\ufb9e\ufee5\ufee7\ufe85\ufeed\ufba6\ufba8\ufba9\u00ad\ufbaa\ufe80\ufe89\ufe8a\ufe8b\ufbfc\ufbfd\ufbfe\ufbb0\ufbae\ufe7c\ufe7d\ufffd\u25a0 \u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f";
    private static final char[] b2c;
    private static final char[] c2b;
    private static final char[] c2bIndex;
    
    public IBM868() {
        super("IBM868", ExtendedCharsets.aliasesFor("IBM868"));
    }
    
    @Override
    public String historicalName() {
        return "Cp868";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset instanceof IBM868;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new SingleByte.Decoder(this, IBM868.b2c);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new SingleByte.Encoder(this, IBM868.c2b, IBM868.c2bIndex);
    }
    
    static {
        b2c = "\u06f0\u06f1\u06f2\u06f3\u06f4\u06f5\u06f6\u06f7\u06f8\u06f9\u060c\u061b\u061f\ufe81\ufe8d\ufe8e\uf8fb\ufe8f\ufe91\ufb56\ufb58\ufe93\ufe95\ufe97\ufb66\ufb68\ufe99\ufe9b\ufe9d\ufe9f\ufb7a\ufb7c\ufea1\ufea3\ufea5\ufea7\ufea9\ufb88\ufeab\ufead\ufb8c\ufeaf\ufb8a\ufeb1\ufeb3\ufeb5«»\u2591\u2592\u2593\u2502\u2524\ufeb7\ufeb9\ufebb\ufebd\u2563\u2551\u2557\u255d\ufebf\ufec3\u2510\u2514\u2534\u252c\u251c\u2500\u253c\ufec7\ufec9\u255a\u2554\u2569\u2566\u2560\u2550\u256c\ufeca\ufecb\ufecc\ufecd\ufece\ufecf\ufed0\ufed1\ufed3\ufed5\u2518\u250c\u2588\u2584\ufed7\ufb8e\u2580\ufedb\ufb92\ufb94\ufedd\ufedf\ufee0\ufee1\ufee3\ufb9e\ufee5\ufee7\ufe85\ufeed\ufba6\ufba8\ufba9\u00ad\ufbaa\ufe80\ufe89\ufe8a\ufe8b\ufbfc\ufbfd\ufbfe\ufbb0\ufbae\ufe7c\ufe7d\ufffd\u25a0 \u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f".toCharArray();
        c2b = new char[1792];
        c2bIndex = new char[256];
        SingleByte.initC2B(IBM868.b2c, null, IBM868.c2b, IBM868.c2bIndex);
    }
}
