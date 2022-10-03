package org.apache.poi.xddf.usermodel.chart;

import java.util.HashMap;
import org.openxmlformats.schemas.drawingml.x2006.chart.STLayoutMode;

public enum LayoutMode
{
    EDGE(STLayoutMode.EDGE), 
    FACTOR(STLayoutMode.FACTOR);
    
    final STLayoutMode.Enum underlying;
    private static final HashMap<STLayoutMode.Enum, LayoutMode> reverse;
    
    private LayoutMode(final STLayoutMode.Enum layoutMode) {
        this.underlying = layoutMode;
    }
    
    static LayoutMode valueOf(final STLayoutMode.Enum layoutMode) {
        return LayoutMode.reverse.get(layoutMode);
    }
    
    static {
        reverse = new HashMap<STLayoutMode.Enum, LayoutMode>();
        for (final LayoutMode value : values()) {
            LayoutMode.reverse.put(value.underlying, value);
        }
    }
}
