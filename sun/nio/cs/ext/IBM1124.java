package sun.nio.cs.ext;

import java.nio.charset.CharsetEncoder;
import sun.nio.cs.SingleByte;
import java.nio.charset.CharsetDecoder;
import sun.nio.cs.HistoricallyNamedCharset;
import java.nio.charset.Charset;

public class IBM1124 extends Charset implements HistoricallyNamedCharset
{
    private static final String b2cTable = "\u0080\u0081\u0082\u0083\u0084\u0085\u0086\u0087\u0088\u0089\u008a\u008b\u008c\u008d\u008e\u008f\u0090\u0091\u0092\u0093\u0094\u0095\u0096\u0097\u0098\u0099\u009a\u009b\u009c\u009d\u009e\u009f \u0401\u0402\u0490\u0404\u0405\u0406\u0407\u0408\u0409\u040a\u040b\u040c\u00ad\u040e\u040f\u0410\u0411\u0412\u0413\u0414\u0415\u0416\u0417\u0418\u0419\u041a\u041b\u041c\u041d\u041e\u041f\u0420\u0421\u0422\u0423\u0424\u0425\u0426\u0427\u0428\u0429\u042a\u042b\u042c\u042d\u042e\u042f\u0430\u0431\u0432\u0433\u0434\u0435\u0436\u0437\u0438\u0439\u043a\u043b\u043c\u043d\u043e\u043f\u0440\u0441\u0442\u0443\u0444\u0445\u0446\u0447\u0448\u0449\u044a\u044b\u044c\u044d\u044e\u044f\u2116\u0451\u0452\u0491\u0454\u0455\u0456\u0457\u0458\u0459\u045a\u045b\u045c§\u045e\u045f\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f";
    private static final char[] b2c;
    private static final char[] c2b;
    private static final char[] c2bIndex;
    
    public IBM1124() {
        super("x-IBM1124", ExtendedCharsets.aliasesFor("x-IBM1124"));
    }
    
    @Override
    public String historicalName() {
        return "Cp1124";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset instanceof IBM1124;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new SingleByte.Decoder(this, IBM1124.b2c);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new SingleByte.Encoder(this, IBM1124.c2b, IBM1124.c2bIndex);
    }
    
    static {
        b2c = "\u0080\u0081\u0082\u0083\u0084\u0085\u0086\u0087\u0088\u0089\u008a\u008b\u008c\u008d\u008e\u008f\u0090\u0091\u0092\u0093\u0094\u0095\u0096\u0097\u0098\u0099\u009a\u009b\u009c\u009d\u009e\u009f \u0401\u0402\u0490\u0404\u0405\u0406\u0407\u0408\u0409\u040a\u040b\u040c\u00ad\u040e\u040f\u0410\u0411\u0412\u0413\u0414\u0415\u0416\u0417\u0418\u0419\u041a\u041b\u041c\u041d\u041e\u041f\u0420\u0421\u0422\u0423\u0424\u0425\u0426\u0427\u0428\u0429\u042a\u042b\u042c\u042d\u042e\u042f\u0430\u0431\u0432\u0433\u0434\u0435\u0436\u0437\u0438\u0439\u043a\u043b\u043c\u043d\u043e\u043f\u0440\u0441\u0442\u0443\u0444\u0445\u0446\u0447\u0448\u0449\u044a\u044b\u044c\u044d\u044e\u044f\u2116\u0451\u0452\u0491\u0454\u0455\u0456\u0457\u0458\u0459\u045a\u045b\u045c§\u045e\u045f\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f".toCharArray();
        c2b = new char[768];
        c2bIndex = new char[256];
        SingleByte.initC2B(IBM1124.b2c, null, IBM1124.c2b, IBM1124.c2bIndex);
    }
}
