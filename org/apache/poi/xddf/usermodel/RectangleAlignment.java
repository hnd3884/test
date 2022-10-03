package org.apache.poi.xddf.usermodel;

import java.util.HashMap;
import org.openxmlformats.schemas.drawingml.x2006.main.STRectAlignment;

public enum RectangleAlignment
{
    BOTTOM(STRectAlignment.B), 
    BOTTOM_LEFT(STRectAlignment.BL), 
    BOTTOM_RIGHT(STRectAlignment.BR), 
    CENTER(STRectAlignment.CTR), 
    LEFT(STRectAlignment.L), 
    RIGHT(STRectAlignment.R), 
    TOP(STRectAlignment.T), 
    TOP_LEFT(STRectAlignment.TL), 
    TOP_RIGHT(STRectAlignment.TR);
    
    final STRectAlignment.Enum underlying;
    private static final HashMap<STRectAlignment.Enum, RectangleAlignment> reverse;
    
    private RectangleAlignment(final STRectAlignment.Enum alignment) {
        this.underlying = alignment;
    }
    
    static RectangleAlignment valueOf(final STRectAlignment.Enum alignment) {
        return RectangleAlignment.reverse.get(alignment);
    }
    
    static {
        reverse = new HashMap<STRectAlignment.Enum, RectangleAlignment>();
        for (final RectangleAlignment value : values()) {
            RectangleAlignment.reverse.put(value.underlying, value);
        }
    }
}
