package org.apache.poi.common.usermodel.fonts;

import org.apache.poi.util.POILogger;
import java.nio.charset.UnsupportedCharsetException;
import org.apache.poi.util.POILogFactory;
import java.nio.charset.Charset;

public enum FontCharset
{
    ANSI(0, "Cp1252"), 
    DEFAULT(1, "Cp1252"), 
    SYMBOL(2, ""), 
    MAC(77, "MacRoman"), 
    SHIFTJIS(128, "Shift_JIS"), 
    HANGUL(129, "cp949"), 
    JOHAB(130, "x-Johab"), 
    GB2312(134, "GB2312"), 
    CHINESEBIG5(136, "Big5"), 
    GREEK(161, "Cp1253"), 
    TURKISH(162, "Cp1254"), 
    VIETNAMESE(163, "Cp1258"), 
    HEBREW(177, "Cp1255"), 
    ARABIC(178, "Cp1256"), 
    BALTIC(186, "Cp1257"), 
    RUSSIAN(204, "Cp1251"), 
    THAI(222, "x-windows-874"), 
    EASTEUROPE(238, "Cp1250"), 
    OEM(255, "Cp1252");
    
    private static FontCharset[] _table;
    private int nativeId;
    private Charset charset;
    
    private FontCharset(final int flag, final String javaCharsetName) {
        this.nativeId = flag;
        if (javaCharsetName.length() > 0) {
            try {
                this.charset = Charset.forName(javaCharsetName);
                return;
            }
            catch (final UnsupportedCharsetException e) {
                final POILogger logger = POILogFactory.getLogger(FontCharset.class);
                logger.log(5, "Unsupported charset: " + javaCharsetName);
            }
        }
        this.charset = null;
    }
    
    public Charset getCharset() {
        return this.charset;
    }
    
    public int getNativeId() {
        return this.nativeId;
    }
    
    public static FontCharset valueOf(final int value) {
        return (value < 0 || value >= FontCharset._table.length) ? null : FontCharset._table[value];
    }
    
    static {
        FontCharset._table = new FontCharset[256];
        for (final FontCharset c : values()) {
            FontCharset._table[c.getNativeId()] = c;
        }
    }
}
