package org.apache.poi.xddf.usermodel.chart;

import java.util.HashMap;
import org.openxmlformats.schemas.drawingml.x2006.chart.STBarDir;

public enum BarDirection
{
    BAR(STBarDir.BAR), 
    COL(STBarDir.COL);
    
    final STBarDir.Enum underlying;
    private static final HashMap<STBarDir.Enum, BarDirection> reverse;
    
    private BarDirection(final STBarDir.Enum direction) {
        this.underlying = direction;
    }
    
    static BarDirection valueOf(final STBarDir.Enum direction) {
        return BarDirection.reverse.get(direction);
    }
    
    static {
        reverse = new HashMap<STBarDir.Enum, BarDirection>();
        for (final BarDirection value : values()) {
            BarDirection.reverse.put(value.underlying, value);
        }
    }
}
