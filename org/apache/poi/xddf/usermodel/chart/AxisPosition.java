package org.apache.poi.xddf.usermodel.chart;

import java.util.HashMap;
import org.openxmlformats.schemas.drawingml.x2006.chart.STAxPos;

public enum AxisPosition
{
    BOTTOM(STAxPos.B), 
    LEFT(STAxPos.L), 
    RIGHT(STAxPos.R), 
    TOP(STAxPos.T);
    
    final STAxPos.Enum underlying;
    private static final HashMap<STAxPos.Enum, AxisPosition> reverse;
    
    private AxisPosition(final STAxPos.Enum position) {
        this.underlying = position;
    }
    
    static AxisPosition valueOf(final STAxPos.Enum position) {
        return AxisPosition.reverse.get(position);
    }
    
    static {
        reverse = new HashMap<STAxPos.Enum, AxisPosition>();
        for (final AxisPosition value : values()) {
            AxisPosition.reverse.put(value.underlying, value);
        }
    }
}
