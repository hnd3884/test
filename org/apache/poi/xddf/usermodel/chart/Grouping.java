package org.apache.poi.xddf.usermodel.chart;

import java.util.HashMap;
import org.openxmlformats.schemas.drawingml.x2006.chart.STGrouping;

public enum Grouping
{
    STANDARD(STGrouping.STANDARD), 
    STACKED(STGrouping.STACKED), 
    PERCENT_STACKED(STGrouping.PERCENT_STACKED);
    
    final STGrouping.Enum underlying;
    private static final HashMap<STGrouping.Enum, Grouping> reverse;
    
    private Grouping(final STGrouping.Enum grouping) {
        this.underlying = grouping;
    }
    
    static Grouping valueOf(final STGrouping.Enum grouping) {
        return Grouping.reverse.get(grouping);
    }
    
    static {
        reverse = new HashMap<STGrouping.Enum, Grouping>();
        for (final Grouping value : values()) {
            Grouping.reverse.put(value.underlying, value);
        }
    }
}
