package org.apache.poi.xddf.usermodel.chart;

import java.util.HashMap;
import org.openxmlformats.schemas.drawingml.x2006.chart.STTickMark;

public enum AxisTickMark
{
    CROSS(STTickMark.CROSS), 
    IN(STTickMark.IN), 
    NONE(STTickMark.NONE), 
    OUT(STTickMark.OUT);
    
    final STTickMark.Enum underlying;
    private static final HashMap<STTickMark.Enum, AxisTickMark> reverse;
    
    private AxisTickMark(final STTickMark.Enum tickMark) {
        this.underlying = tickMark;
    }
    
    static AxisTickMark valueOf(final STTickMark.Enum tickMark) {
        return AxisTickMark.reverse.get(tickMark);
    }
    
    static {
        reverse = new HashMap<STTickMark.Enum, AxisTickMark>();
        for (final AxisTickMark value : values()) {
            AxisTickMark.reverse.put(value.underlying, value);
        }
    }
}
