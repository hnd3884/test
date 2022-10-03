package sun.awt.windows;

import java.awt.Font;
import java.util.Hashtable;
import java.awt.FontMetrics;

final class WFontMetrics extends FontMetrics
{
    int[] widths;
    int ascent;
    int descent;
    int leading;
    int height;
    int maxAscent;
    int maxDescent;
    int maxHeight;
    int maxAdvance;
    static Hashtable table;
    
    public WFontMetrics(final Font font) {
        super(font);
        this.init();
    }
    
    @Override
    public int getLeading() {
        return this.leading;
    }
    
    @Override
    public int getAscent() {
        return this.ascent;
    }
    
    @Override
    public int getDescent() {
        return this.descent;
    }
    
    @Override
    public int getHeight() {
        return this.height;
    }
    
    @Override
    public int getMaxAscent() {
        return this.maxAscent;
    }
    
    @Override
    public int getMaxDescent() {
        return this.maxDescent;
    }
    
    @Override
    public int getMaxAdvance() {
        return this.maxAdvance;
    }
    
    @Override
    public native int stringWidth(final String p0);
    
    @Override
    public native int charsWidth(final char[] p0, final int p1, final int p2);
    
    @Override
    public native int bytesWidth(final byte[] p0, final int p1, final int p2);
    
    @Override
    public int[] getWidths() {
        return this.widths;
    }
    
    native void init();
    
    static FontMetrics getFontMetrics(final Font font) {
        FontMetrics fontMetrics = WFontMetrics.table.get(font);
        if (fontMetrics == null) {
            WFontMetrics.table.put(font, fontMetrics = new WFontMetrics(font));
        }
        return fontMetrics;
    }
    
    private static native void initIDs();
    
    static {
        initIDs();
        WFontMetrics.table = new Hashtable();
    }
}
