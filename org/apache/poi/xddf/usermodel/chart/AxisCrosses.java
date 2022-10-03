package org.apache.poi.xddf.usermodel.chart;

import java.util.HashMap;
import org.openxmlformats.schemas.drawingml.x2006.chart.STCrosses;

public enum AxisCrosses
{
    AUTO_ZERO(STCrosses.AUTO_ZERO), 
    MAX(STCrosses.MAX), 
    MIN(STCrosses.MIN);
    
    final STCrosses.Enum underlying;
    private static final HashMap<STCrosses.Enum, AxisCrosses> reverse;
    
    private AxisCrosses(final STCrosses.Enum crosses) {
        this.underlying = crosses;
    }
    
    static AxisCrosses valueOf(final STCrosses.Enum crosses) {
        return AxisCrosses.reverse.get(crosses);
    }
    
    static {
        reverse = new HashMap<STCrosses.Enum, AxisCrosses>();
        for (final AxisCrosses value : values()) {
            AxisCrosses.reverse.put(value.underlying, value);
        }
    }
}
