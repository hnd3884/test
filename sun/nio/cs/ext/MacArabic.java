package sun.nio.cs.ext;

import java.nio.charset.CharsetEncoder;
import sun.nio.cs.SingleByte;
import java.nio.charset.CharsetDecoder;
import sun.nio.cs.HistoricallyNamedCharset;
import java.nio.charset.Charset;

public class MacArabic extends Charset implements HistoricallyNamedCharset
{
    private static final String b2cTable = "\u00c4 \u00c7\u00c9\u00d1\u00d6\u00dc\u00e1\u00e0\u00e2\u00e4\u06ba«\u00e7\u00e9\u00e8\u00ea\u00eb\u00ed\u2026\u00ee\u00ef\u00f1\u00f3»\u00f4\u00f6\u00f7\u00fa\u00f9\u00fb\u00fc\ufffd\ufffd\ufffd\ufffd\ufffd\u066a\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u060c\ufffd\ufffd\ufffd\u0660\u0661\u0662\u0663\u0664\u0665\u0666\u0667\u0668\u0669\ufffd\u061b\ufffd\ufffd\ufffd\u061f\u066d\u0621\u0622\u0623\u0624\u0625\u0626\u0627\u0628\u0629\u062a\u062b\u062c\u062d\u062e\u062f\u0630\u0631\u0632\u0633\u0634\u0635\u0636\u0637\u0638\u0639\u063a\ufffd\ufffd\ufffd\ufffd\ufffd\u0640\u0641\u0642\u0643\u0644\u0645\u0646\u0647\u0648\u0649\u064a\u064b\u064c\u064d\u064e\u064f\u0650\u0651\u0652\u067e\u0679\u0686\u06d5\u06a4\u06af\u0688\u0691\ufffd\ufffd\ufffd\u0698\u06d2\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f";
    private static final char[] b2c;
    private static final char[] c2b;
    private static final char[] c2bIndex;
    
    public MacArabic() {
        super("x-MacArabic", ExtendedCharsets.aliasesFor("x-MacArabic"));
    }
    
    @Override
    public String historicalName() {
        return "MacArabic";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset instanceof MacArabic;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new SingleByte.Decoder(this, MacArabic.b2c);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new SingleByte.Encoder(this, MacArabic.c2b, MacArabic.c2bIndex);
    }
    
    static {
        b2c = "\u00c4 \u00c7\u00c9\u00d1\u00d6\u00dc\u00e1\u00e0\u00e2\u00e4\u06ba«\u00e7\u00e9\u00e8\u00ea\u00eb\u00ed\u2026\u00ee\u00ef\u00f1\u00f3»\u00f4\u00f6\u00f7\u00fa\u00f9\u00fb\u00fc\ufffd\ufffd\ufffd\ufffd\ufffd\u066a\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u060c\ufffd\ufffd\ufffd\u0660\u0661\u0662\u0663\u0664\u0665\u0666\u0667\u0668\u0669\ufffd\u061b\ufffd\ufffd\ufffd\u061f\u066d\u0621\u0622\u0623\u0624\u0625\u0626\u0627\u0628\u0629\u062a\u062b\u062c\u062d\u062e\u062f\u0630\u0631\u0632\u0633\u0634\u0635\u0636\u0637\u0638\u0639\u063a\ufffd\ufffd\ufffd\ufffd\ufffd\u0640\u0641\u0642\u0643\u0644\u0645\u0646\u0647\u0648\u0649\u064a\u064b\u064c\u064d\u064e\u064f\u0650\u0651\u0652\u067e\u0679\u0686\u06d5\u06a4\u06af\u0688\u0691\ufffd\ufffd\ufffd\u0698\u06d2\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f".toCharArray();
        c2b = new char[1024];
        c2bIndex = new char[256];
        SingleByte.initC2B(MacArabic.b2c, null, MacArabic.c2b, MacArabic.c2bIndex);
    }
}
