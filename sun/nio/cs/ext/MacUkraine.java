package sun.nio.cs.ext;

import java.nio.charset.CharsetEncoder;
import sun.nio.cs.SingleByte;
import java.nio.charset.CharsetDecoder;
import sun.nio.cs.HistoricallyNamedCharset;
import java.nio.charset.Charset;

public class MacUkraine extends Charset implements HistoricallyNamedCharset
{
    private static final String b2cTable = "\u0410\u0411\u0412\u0413\u0414\u0415\u0416\u0417\u0418\u0419\u041a\u041b\u041c\u041d\u041e\u041f\u0420\u0421\u0422\u0423\u0424\u0425\u0426\u0427\u0428\u0429\u042a\u042b\u042c\u042d\u042e\u042f\u2020°\u0490£§\u2022¶\u0406®©\u2122\u0402\u0452\u2260\u0403\u0453\u221e±\u2264\u2265\u0456µ\u0491\u0408\u0404\u0454\u0407\u0457\u0409\u0459\u040a\u045a\u0458\u0405¬\u221a\u0192\u2248\u2206«»\u2026 \u040b\u045b\u040c\u045c\u0455\u2013\u2014\u201c\u201d\u2018\u2019\u00f7\u201e\u040e\u045e\u040f\u045f\u2116\u0401\u0451\u044f\u0430\u0431\u0432\u0433\u0434\u0435\u0436\u0437\u0438\u0439\u043a\u043b\u043c\u043d\u043e\u043f\u0440\u0441\u0442\u0443\u0444\u0445\u0446\u0447\u0448\u0449\u044a\u044b\u044c\u044d\u044e¤\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f";
    private static final char[] b2c;
    private static final char[] c2b;
    private static final char[] c2bIndex;
    
    public MacUkraine() {
        super("x-MacUkraine", ExtendedCharsets.aliasesFor("x-MacUkraine"));
    }
    
    @Override
    public String historicalName() {
        return "MacUkraine";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset instanceof MacUkraine;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new SingleByte.Decoder(this, MacUkraine.b2c);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new SingleByte.Encoder(this, MacUkraine.c2b, MacUkraine.c2bIndex);
    }
    
    static {
        b2c = "\u0410\u0411\u0412\u0413\u0414\u0415\u0416\u0417\u0418\u0419\u041a\u041b\u041c\u041d\u041e\u041f\u0420\u0421\u0422\u0423\u0424\u0425\u0426\u0427\u0428\u0429\u042a\u042b\u042c\u042d\u042e\u042f\u2020°\u0490£§\u2022¶\u0406®©\u2122\u0402\u0452\u2260\u0403\u0453\u221e±\u2264\u2265\u0456µ\u0491\u0408\u0404\u0454\u0407\u0457\u0409\u0459\u040a\u045a\u0458\u0405¬\u221a\u0192\u2248\u2206«»\u2026 \u040b\u045b\u040c\u045c\u0455\u2013\u2014\u201c\u201d\u2018\u2019\u00f7\u201e\u040e\u045e\u040f\u045f\u2116\u0401\u0451\u044f\u0430\u0431\u0432\u0433\u0434\u0435\u0436\u0437\u0438\u0439\u043a\u043b\u043c\u043d\u043e\u043f\u0440\u0441\u0442\u0443\u0444\u0445\u0446\u0447\u0448\u0449\u044a\u044b\u044c\u044d\u044e¤\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f".toCharArray();
        c2b = new char[1536];
        c2bIndex = new char[256];
        SingleByte.initC2B(MacUkraine.b2c, null, MacUkraine.c2b, MacUkraine.c2bIndex);
    }
}
