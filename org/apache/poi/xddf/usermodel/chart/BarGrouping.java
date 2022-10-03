package org.apache.poi.xddf.usermodel.chart;

import java.util.HashMap;
import org.openxmlformats.schemas.drawingml.x2006.chart.STBarGrouping;

public enum BarGrouping
{
    STANDARD(STBarGrouping.STANDARD), 
    CLUSTERED(STBarGrouping.CLUSTERED), 
    STACKED(STBarGrouping.STACKED), 
    PERCENT_STACKED(STBarGrouping.PERCENT_STACKED);
    
    final STBarGrouping.Enum underlying;
    private static final HashMap<STBarGrouping.Enum, BarGrouping> reverse;
    
    private BarGrouping(final STBarGrouping.Enum grouping) {
        this.underlying = grouping;
    }
    
    static BarGrouping valueOf(final STBarGrouping.Enum grouping) {
        return BarGrouping.reverse.get(grouping);
    }
    
    static {
        reverse = new HashMap<STBarGrouping.Enum, BarGrouping>();
        for (final BarGrouping value : values()) {
            BarGrouping.reverse.put(value.underlying, value);
        }
    }
}
