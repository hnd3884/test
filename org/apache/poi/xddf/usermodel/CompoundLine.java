package org.apache.poi.xddf.usermodel;

import java.util.HashMap;
import org.openxmlformats.schemas.drawingml.x2006.main.STCompoundLine;

public enum CompoundLine
{
    DOUBLE(STCompoundLine.DBL), 
    SINGLE(STCompoundLine.SNG), 
    THICK_THIN(STCompoundLine.THICK_THIN), 
    THIN_THICK(STCompoundLine.THIN_THICK), 
    TRIPLE(STCompoundLine.TRI);
    
    final STCompoundLine.Enum underlying;
    private static final HashMap<STCompoundLine.Enum, CompoundLine> reverse;
    
    private CompoundLine(final STCompoundLine.Enum line) {
        this.underlying = line;
    }
    
    static CompoundLine valueOf(final STCompoundLine.Enum LineEndWidth) {
        return CompoundLine.reverse.get(LineEndWidth);
    }
    
    static {
        reverse = new HashMap<STCompoundLine.Enum, CompoundLine>();
        for (final CompoundLine value : values()) {
            CompoundLine.reverse.put(value.underlying, value);
        }
    }
}
