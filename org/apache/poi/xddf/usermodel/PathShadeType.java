package org.apache.poi.xddf.usermodel;

import java.util.HashMap;
import org.openxmlformats.schemas.drawingml.x2006.main.STPathShadeType;

public enum PathShadeType
{
    CIRCLE(STPathShadeType.CIRCLE), 
    RECTANGLE(STPathShadeType.RECT), 
    SHAPE(STPathShadeType.SHAPE);
    
    final STPathShadeType.Enum underlying;
    private static final HashMap<STPathShadeType.Enum, PathShadeType> reverse;
    
    private PathShadeType(final STPathShadeType.Enum pathShadeType) {
        this.underlying = pathShadeType;
    }
    
    static PathShadeType valueOf(final STPathShadeType.Enum pathShadeType) {
        return PathShadeType.reverse.get(pathShadeType);
    }
    
    static {
        reverse = new HashMap<STPathShadeType.Enum, PathShadeType>();
        for (final PathShadeType value : values()) {
            PathShadeType.reverse.put(value.underlying, value);
        }
    }
}
