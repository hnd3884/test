package org.apache.poi.sl.draw;

import java.awt.RenderingHints;
import java.util.Map;
import java.awt.Font;
import org.apache.poi.common.usermodel.fonts.FontInfo;
import java.awt.Graphics2D;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.Set;

public class DrawFontManagerDefault implements DrawFontManager
{
    protected final Set<String> knownSymbolFonts;
    
    public DrawFontManagerDefault() {
        (this.knownSymbolFonts = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER)).add("Wingdings");
        this.knownSymbolFonts.add("Symbol");
    }
    
    @Override
    public FontInfo getMappedFont(final Graphics2D graphics, final FontInfo fontInfo) {
        return this.getFontWithFallback(graphics, Drawable.FONT_MAP, fontInfo);
    }
    
    @Override
    public FontInfo getFallbackFont(final Graphics2D graphics, final FontInfo fontInfo) {
        FontInfo fi = this.getFontWithFallback(graphics, Drawable.FONT_FALLBACK, fontInfo);
        if (fi == null) {
            fi = new DrawFontInfo("SansSerif");
        }
        return fi;
    }
    
    @Override
    public String mapFontCharset(final Graphics2D graphics, final FontInfo fontInfo, final String text) {
        return (fontInfo != null && this.knownSymbolFonts.contains(fontInfo.getTypeface())) ? mapSymbolChars(text) : text;
    }
    
    public static String mapSymbolChars(final String text) {
        boolean changed = false;
        final char[] chrs = text.toCharArray();
        for (int i = 0; i < chrs.length; ++i) {
            if ((' ' <= chrs[i] && chrs[i] <= '\u007f') || (' ' <= chrs[i] && chrs[i] <= '\u00ff')) {
                final char[] array = chrs;
                final int n = i;
                array[n] |= '\uf000';
                changed = true;
            }
        }
        return changed ? new String(chrs) : text;
    }
    
    @Override
    public Font createAWTFont(final Graphics2D graphics, final FontInfo fontInfo, final double fontSize, final boolean bold, final boolean italic) {
        final int style = (bold ? 1 : 0) | (italic ? 2 : 0);
        Font font = new Font(fontInfo.getTypeface(), style, 12);
        if ("Dialog".equals(font.getFamily())) {
            font = new Font("SansSerif", style, 12);
        }
        return font.deriveFont((float)fontSize);
    }
    
    private FontInfo getFontWithFallback(final Graphics2D graphics, final Drawable.DrawableHint hint, final FontInfo fontInfo) {
        final Map<String, String> fontMap = (Map<String, String>)graphics.getRenderingHint(hint);
        if (fontMap == null) {
            return fontInfo;
        }
        final String f = (fontInfo != null) ? fontInfo.getTypeface() : null;
        String mappedTypeface = null;
        if (fontMap.containsKey(f)) {
            mappedTypeface = fontMap.get(f);
        }
        else if (fontMap.containsKey("*")) {
            mappedTypeface = fontMap.get("*");
        }
        return (mappedTypeface != null) ? new DrawFontInfo(mappedTypeface) : fontInfo;
    }
}
