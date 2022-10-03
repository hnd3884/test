package org.apache.poi.xddf.usermodel.text;

import java.util.HashMap;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextFontAlignType;

public enum FontAlignment
{
    AUTOMATIC(STTextFontAlignType.AUTO), 
    BOTTOM(STTextFontAlignType.B), 
    BASELINE(STTextFontAlignType.BASE), 
    CENTER(STTextFontAlignType.CTR), 
    TOP(STTextFontAlignType.T);
    
    final STTextFontAlignType.Enum underlying;
    private static final HashMap<STTextFontAlignType.Enum, FontAlignment> reverse;
    
    private FontAlignment(final STTextFontAlignType.Enum align) {
        this.underlying = align;
    }
    
    static FontAlignment valueOf(final STTextFontAlignType.Enum align) {
        return FontAlignment.reverse.get(align);
    }
    
    static {
        reverse = new HashMap<STTextFontAlignType.Enum, FontAlignment>();
        for (final FontAlignment value : values()) {
            FontAlignment.reverse.put(value.underlying, value);
        }
    }
}
