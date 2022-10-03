package org.apache.poi.xddf.usermodel;

import java.util.HashMap;
import org.openxmlformats.schemas.drawingml.x2006.main.STLineEndLength;

public enum LineEndLength
{
    LARGE(STLineEndLength.LG), 
    MEDIUM(STLineEndLength.MED), 
    SMALL(STLineEndLength.SM);
    
    final STLineEndLength.Enum underlying;
    private static final HashMap<STLineEndLength.Enum, LineEndLength> reverse;
    
    private LineEndLength(final STLineEndLength.Enum lineEnd) {
        this.underlying = lineEnd;
    }
    
    static LineEndLength valueOf(final STLineEndLength.Enum LineEndWidth) {
        return LineEndLength.reverse.get(LineEndWidth);
    }
    
    static {
        reverse = new HashMap<STLineEndLength.Enum, LineEndLength>();
        for (final LineEndLength value : values()) {
            LineEndLength.reverse.put(value.underlying, value);
        }
    }
}
