package org.apache.poi.xddf.usermodel.chart;

import java.util.HashMap;
import org.openxmlformats.schemas.drawingml.x2006.chart.STLegendPos;

public enum LegendPosition
{
    BOTTOM(STLegendPos.B), 
    LEFT(STLegendPos.L), 
    RIGHT(STLegendPos.R), 
    TOP(STLegendPos.T), 
    TOP_RIGHT(STLegendPos.TR);
    
    final STLegendPos.Enum underlying;
    private static final HashMap<STLegendPos.Enum, LegendPosition> reverse;
    
    private LegendPosition(final STLegendPos.Enum position) {
        this.underlying = position;
    }
    
    static LegendPosition valueOf(final STLegendPos.Enum position) {
        return LegendPosition.reverse.get(position);
    }
    
    static {
        reverse = new HashMap<STLegendPos.Enum, LegendPosition>();
        for (final LegendPosition value : values()) {
            LegendPosition.reverse.put(value.underlying, value);
        }
    }
}
