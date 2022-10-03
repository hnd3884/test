package org.apache.poi.xddf.usermodel.chart;

import java.util.HashMap;
import org.openxmlformats.schemas.drawingml.x2006.chart.STCrossBetween;

public enum AxisCrossBetween
{
    BETWEEN(STCrossBetween.BETWEEN), 
    MIDPOINT_CATEGORY(STCrossBetween.MID_CAT);
    
    final STCrossBetween.Enum underlying;
    private static final HashMap<STCrossBetween.Enum, AxisCrossBetween> reverse;
    
    private AxisCrossBetween(final STCrossBetween.Enum crossBetween) {
        this.underlying = crossBetween;
    }
    
    static AxisCrossBetween valueOf(final STCrossBetween.Enum crossBetween) {
        return AxisCrossBetween.reverse.get(crossBetween);
    }
    
    static {
        reverse = new HashMap<STCrossBetween.Enum, AxisCrossBetween>();
        for (final AxisCrossBetween value : values()) {
            AxisCrossBetween.reverse.put(value.underlying, value);
        }
    }
}
