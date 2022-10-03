package sun.nio.cs.ext;

import java.util.Arrays;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import sun.nio.cs.HistoricallyNamedCharset;
import java.nio.charset.Charset;

public class Big5_Solaris extends Charset implements HistoricallyNamedCharset
{
    static char[][] b2c;
    static char[] b2cSB;
    private static volatile boolean b2cInitialized;
    static char[] c2b;
    static char[] c2bIndex;
    private static volatile boolean c2bInitialized;
    
    public Big5_Solaris() {
        super("x-Big5-Solaris", ExtendedCharsets.aliasesFor("x-Big5-Solaris"));
    }
    
    @Override
    public String historicalName() {
        return "Big5_Solaris";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset.name().equals("US-ASCII") || charset instanceof Big5 || charset instanceof Big5_Solaris;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        initb2c();
        return new DoubleByte.Decoder(this, Big5_Solaris.b2c, Big5_Solaris.b2cSB, 64, 254);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        initc2b();
        return new DoubleByte.Encoder(this, Big5_Solaris.c2b, Big5_Solaris.c2bIndex);
    }
    
    static void initb2c() {
        if (Big5_Solaris.b2cInitialized) {
            return;
        }
        synchronized (Big5_Solaris.class) {
            if (Big5_Solaris.b2cInitialized) {
                return;
            }
            Big5.initb2c();
            Big5_Solaris.b2c = Big5.b2c.clone();
            final int[] array = { 63958, 30849, 63959, 37561, 63960, 35023, 63961, 22715, 63962, 24658, 63963, 31911, 63964, 23290 };
            if (Big5_Solaris.b2c[249] == DoubleByte.B2C_UNMAPPABLE) {
                Arrays.fill(Big5_Solaris.b2c[249] = new char[191], '\ufffd');
            }
            for (int i = 0; i < array.length; Big5_Solaris.b2c[249][array[i++] & 0xBF] = (char)array[i++]) {}
            Big5_Solaris.b2cSB = Big5.b2cSB;
            Big5_Solaris.b2cInitialized = true;
        }
    }
    
    static void initc2b() {
        if (Big5_Solaris.c2bInitialized) {
            return;
        }
        synchronized (Big5_Solaris.class) {
            if (Big5_Solaris.c2bInitialized) {
                return;
            }
            Big5.initc2b();
            Big5_Solaris.c2b = Big5.c2b.clone();
            Big5_Solaris.c2bIndex = Big5.c2bIndex.clone();
            final int[] array = { 30849, 63958, 37561, 63959, 35023, 63960, 22715, 63961, 24658, 63962, 31911, 63963, 23290, 63964 };
            int n;
            for (int i = 0; i < array.length; n = array[i++], Big5_Solaris.c2b[Big5_Solaris.c2bIndex[n >> 8] + (n & 0xFF)] = (char)array[i++]) {}
            Big5_Solaris.c2bInitialized = true;
        }
    }
    
    static {
        Big5_Solaris.b2cInitialized = false;
        Big5_Solaris.c2bInitialized = false;
    }
}
