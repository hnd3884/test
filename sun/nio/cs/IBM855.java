package sun.nio.cs;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.Charset;

public class IBM855 extends Charset implements HistoricallyNamedCharset
{
    private static final String b2cTable = "\u0452\u0402\u0453\u0403\u0451\u0401\u0454\u0404\u0455\u0405\u0456\u0406\u0457\u0407\u0458\u0408\u0459\u0409\u045a\u040a\u045b\u040b\u045c\u040c\u045e\u040e\u045f\u040f\u044e\u042e\u044a\u042a\u0430\u0410\u0431\u0411\u0446\u0426\u0434\u0414\u0435\u0415\u0444\u0424\u0433\u0413«»\u2591\u2592\u2593\u2502\u2524\u0445\u0425\u0438\u0418\u2563\u2551\u2557\u255d\u0439\u0419\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u043a\u041a\u255a\u2554\u2569\u2566\u2560\u2550\u256c¤\u043b\u041b\u043c\u041c\u043d\u041d\u043e\u041e\u043f\u2518\u250c\u2588\u2584\u041f\u044f\u2580\u042f\u0440\u0420\u0441\u0421\u0442\u0422\u0443\u0423\u0436\u0416\u0432\u0412\u044c\u042c\u2116\u00ad\u044b\u042b\u0437\u0417\u0448\u0428\u044d\u042d\u0449\u0429\u0447\u0427§\u25a0 \u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f";
    private static final char[] b2c;
    private static final char[] c2b;
    private static final char[] c2bIndex;
    
    public IBM855() {
        super("IBM855", StandardCharsets.aliases_IBM855);
    }
    
    @Override
    public String historicalName() {
        return "Cp855";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset instanceof IBM855;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new SingleByte.Decoder(this, IBM855.b2c);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new SingleByte.Encoder(this, IBM855.c2b, IBM855.c2bIndex);
    }
    
    static {
        b2c = "\u0452\u0402\u0453\u0403\u0451\u0401\u0454\u0404\u0455\u0405\u0456\u0406\u0457\u0407\u0458\u0408\u0459\u0409\u045a\u040a\u045b\u040b\u045c\u040c\u045e\u040e\u045f\u040f\u044e\u042e\u044a\u042a\u0430\u0410\u0431\u0411\u0446\u0426\u0434\u0414\u0435\u0415\u0444\u0424\u0433\u0413«»\u2591\u2592\u2593\u2502\u2524\u0445\u0425\u0438\u0418\u2563\u2551\u2557\u255d\u0439\u0419\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u043a\u041a\u255a\u2554\u2569\u2566\u2560\u2550\u256c¤\u043b\u041b\u043c\u041c\u043d\u041d\u043e\u041e\u043f\u2518\u250c\u2588\u2584\u041f\u044f\u2580\u042f\u0440\u0420\u0441\u0421\u0442\u0422\u0443\u0423\u0436\u0416\u0432\u0412\u044c\u042c\u2116\u00ad\u044b\u042b\u0437\u0417\u0448\u0428\u044d\u042d\u0449\u0429\u0447\u0427§\u25a0 \u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f".toCharArray();
        c2b = new char[1024];
        c2bIndex = new char[256];
        SingleByte.initC2B(IBM855.b2c, null, IBM855.c2b, IBM855.c2bIndex);
    }
}
