package org.apache.poi.xddf.usermodel.chart;

import java.util.HashMap;
import org.openxmlformats.schemas.drawingml.x2006.chart.STLblAlgn;

public enum AxisLabelAlignment
{
    CENTER(STLblAlgn.CTR), 
    LEFT(STLblAlgn.L), 
    RIGHT(STLblAlgn.R);
    
    final STLblAlgn.Enum underlying;
    private static final HashMap<STLblAlgn.Enum, AxisLabelAlignment> reverse;
    
    private AxisLabelAlignment(final STLblAlgn.Enum alignment) {
        this.underlying = alignment;
    }
    
    static AxisLabelAlignment valueOf(final STLblAlgn.Enum alignment) {
        return AxisLabelAlignment.reverse.get(alignment);
    }
    
    static {
        reverse = new HashMap<STLblAlgn.Enum, AxisLabelAlignment>();
        for (final AxisLabelAlignment value : values()) {
            AxisLabelAlignment.reverse.put(value.underlying, value);
        }
    }
}
