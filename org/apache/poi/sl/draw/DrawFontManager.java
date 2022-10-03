package org.apache.poi.sl.draw;

import java.awt.Font;
import org.apache.poi.common.usermodel.fonts.FontInfo;
import java.awt.Graphics2D;

public interface DrawFontManager
{
    FontInfo getMappedFont(final Graphics2D p0, final FontInfo p1);
    
    FontInfo getFallbackFont(final Graphics2D p0, final FontInfo p1);
    
    String mapFontCharset(final Graphics2D p0, final FontInfo p1, final String p2);
    
    Font createAWTFont(final Graphics2D p0, final FontInfo p1, final double p2, final boolean p3, final boolean p4);
}
