package org.apache.poi.sl.draw;

import org.apache.poi.util.Internal;
import org.apache.poi.common.usermodel.fonts.FontInfo;

@Internal
class DrawFontInfo implements FontInfo
{
    private final String typeface;
    
    DrawFontInfo(final String typeface) {
        this.typeface = typeface;
    }
    
    @Override
    public String getTypeface() {
        return this.typeface;
    }
}
