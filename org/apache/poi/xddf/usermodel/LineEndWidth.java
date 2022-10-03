package org.apache.poi.xddf.usermodel;

import java.util.HashMap;
import org.openxmlformats.schemas.drawingml.x2006.main.STLineEndWidth;

public enum LineEndWidth
{
    LARGE(STLineEndWidth.LG), 
    MEDIUM(STLineEndWidth.MED), 
    SMALL(STLineEndWidth.SM);
    
    final STLineEndWidth.Enum underlying;
    private static final HashMap<STLineEndWidth.Enum, LineEndWidth> reverse;
    
    private LineEndWidth(final STLineEndWidth.Enum lineEnd) {
        this.underlying = lineEnd;
    }
    
    static LineEndWidth valueOf(final STLineEndWidth.Enum LineEndWidth) {
        return LineEndWidth.reverse.get(LineEndWidth);
    }
    
    static {
        reverse = new HashMap<STLineEndWidth.Enum, LineEndWidth>();
        for (final LineEndWidth value : values()) {
            LineEndWidth.reverse.put(value.underlying, value);
        }
    }
}
