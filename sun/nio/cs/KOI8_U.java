package sun.nio.cs;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.Charset;

public class KOI8_U extends Charset implements HistoricallyNamedCharset
{
    private static final String b2cTable = "\u2500\u2502\u250c\u2510\u2514\u2518\u251c\u2524\u252c\u2534\u253c\u2580\u2584\u2588\u258c\u2590\u2591\u2592\u2593\u2320\u25a0\u2219\u221a\u2248\u2264\u2265 \u2321°²·\u00f7\u2550\u2551\u2552\u0451\u0454\u2554\u0456\u0457\u2557\u2558\u2559\u255a\u255b\u0491\u255d\u255e\u255f\u2560\u2561\u0401\u0404\u2563\u0406\u0407\u2566\u2567\u2568\u2569\u256a\u0490\u256c©\u044e\u0430\u0431\u0446\u0434\u0435\u0444\u0433\u0445\u0438\u0439\u043a\u043b\u043c\u043d\u043e\u043f\u044f\u0440\u0441\u0442\u0443\u0436\u0432\u044c\u044b\u0437\u0448\u044d\u0449\u0447\u044a\u042e\u0410\u0411\u0426\u0414\u0415\u0424\u0413\u0425\u0418\u0419\u041a\u041b\u041c\u041d\u041e\u041f\u042f\u0420\u0421\u0422\u0423\u0416\u0412\u042c\u042b\u0417\u0428\u042d\u0429\u0427\u042a\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f";
    private static final char[] b2c;
    private static final char[] c2b;
    private static final char[] c2bIndex;
    
    public KOI8_U() {
        super("KOI8-U", StandardCharsets.aliases_KOI8_U);
    }
    
    @Override
    public String historicalName() {
        return "KOI8_U";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset.name().equals("US-ASCII") || charset instanceof KOI8_U;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new SingleByte.Decoder(this, KOI8_U.b2c);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new SingleByte.Encoder(this, KOI8_U.c2b, KOI8_U.c2bIndex);
    }
    
    static {
        b2c = "\u2500\u2502\u250c\u2510\u2514\u2518\u251c\u2524\u252c\u2534\u253c\u2580\u2584\u2588\u258c\u2590\u2591\u2592\u2593\u2320\u25a0\u2219\u221a\u2248\u2264\u2265 \u2321°²·\u00f7\u2550\u2551\u2552\u0451\u0454\u2554\u0456\u0457\u2557\u2558\u2559\u255a\u255b\u0491\u255d\u255e\u255f\u2560\u2561\u0401\u0404\u2563\u0406\u0407\u2566\u2567\u2568\u2569\u256a\u0490\u256c©\u044e\u0430\u0431\u0446\u0434\u0435\u0444\u0433\u0445\u0438\u0439\u043a\u043b\u043c\u043d\u043e\u043f\u044f\u0440\u0441\u0442\u0443\u0436\u0432\u044c\u044b\u0437\u0448\u044d\u0449\u0447\u044a\u042e\u0410\u0411\u0426\u0414\u0415\u0424\u0413\u0425\u0418\u0419\u041a\u041b\u041c\u041d\u041e\u041f\u042f\u0420\u0421\u0422\u0423\u0416\u0412\u042c\u042b\u0417\u0428\u042d\u0429\u0427\u042a\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f".toCharArray();
        c2b = new char[1280];
        c2bIndex = new char[256];
        SingleByte.initC2B(KOI8_U.b2c, null, KOI8_U.c2b, KOI8_U.c2bIndex);
    }
}
