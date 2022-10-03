package sun.awt.windows;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.Charset;
import sun.awt.AWTCharset;

final class WDefaultFontCharset extends AWTCharset
{
    private String fontName;
    
    WDefaultFontCharset(final String fontName) {
        super("WDefaultFontCharset", Charset.forName("windows-1252"));
        this.fontName = fontName;
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new Encoder();
    }
    
    private synchronized native boolean canConvert(final char p0);
    
    private static native void initIDs();
    
    static {
        initIDs();
    }
    
    private class Encoder extends AWTCharset.Encoder
    {
        @Override
        public boolean canEncode(final char c) {
            return WDefaultFontCharset.this.canConvert(c);
        }
    }
}
