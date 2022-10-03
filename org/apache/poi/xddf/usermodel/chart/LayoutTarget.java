package org.apache.poi.xddf.usermodel.chart;

import java.util.HashMap;
import org.openxmlformats.schemas.drawingml.x2006.chart.STLayoutTarget;

public enum LayoutTarget
{
    INNER(STLayoutTarget.INNER), 
    OUTER(STLayoutTarget.OUTER);
    
    final STLayoutTarget.Enum underlying;
    private static final HashMap<STLayoutTarget.Enum, LayoutTarget> reverse;
    
    private LayoutTarget(final STLayoutTarget.Enum layoutTarget) {
        this.underlying = layoutTarget;
    }
    
    static LayoutTarget valueOf(final STLayoutTarget.Enum layoutTarget) {
        return LayoutTarget.reverse.get(layoutTarget);
    }
    
    static {
        reverse = new HashMap<STLayoutTarget.Enum, LayoutTarget>();
        for (final LayoutTarget value : values()) {
            LayoutTarget.reverse.put(value.underlying, value);
        }
    }
}
