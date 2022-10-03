package org.apache.poi.xddf.usermodel.chart;

import java.util.HashMap;
import org.openxmlformats.schemas.drawingml.x2006.chart.STOrientation;

public enum AxisOrientation
{
    MIN_MAX(STOrientation.MIN_MAX), 
    MAX_MIN(STOrientation.MAX_MIN);
    
    final STOrientation.Enum underlying;
    private static final HashMap<STOrientation.Enum, AxisOrientation> reverse;
    
    private AxisOrientation(final STOrientation.Enum orientation) {
        this.underlying = orientation;
    }
    
    static AxisOrientation valueOf(final STOrientation.Enum orientation) {
        return AxisOrientation.reverse.get(orientation);
    }
    
    static {
        reverse = new HashMap<STOrientation.Enum, AxisOrientation>();
        for (final AxisOrientation value : values()) {
            AxisOrientation.reverse.put(value.underlying, value);
        }
    }
}
