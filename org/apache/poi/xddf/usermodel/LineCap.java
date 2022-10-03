package org.apache.poi.xddf.usermodel;

import java.util.HashMap;
import org.openxmlformats.schemas.drawingml.x2006.main.STLineCap;

public enum LineCap
{
    FLAT(STLineCap.FLAT), 
    ROUND(STLineCap.RND), 
    SQUARE(STLineCap.SQ);
    
    final STLineCap.Enum underlying;
    private static final HashMap<STLineCap.Enum, LineCap> reverse;
    
    private LineCap(final STLineCap.Enum line) {
        this.underlying = line;
    }
    
    static LineCap valueOf(final STLineCap.Enum LineEndWidth) {
        return LineCap.reverse.get(LineEndWidth);
    }
    
    static {
        reverse = new HashMap<STLineCap.Enum, LineCap>();
        for (final LineCap value : values()) {
            LineCap.reverse.put(value.underlying, value);
        }
    }
}
