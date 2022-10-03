package org.apache.poi.xddf.usermodel.chart;

import java.util.HashMap;
import org.openxmlformats.schemas.drawingml.x2006.chart.STTickLblPos;

public enum AxisTickLabelPosition
{
    HIGH(STTickLblPos.HIGH), 
    LOW(STTickLblPos.LOW), 
    NEXT_TO(STTickLblPos.NEXT_TO), 
    NONE(STTickLblPos.NONE);
    
    final STTickLblPos.Enum underlying;
    private static final HashMap<STTickLblPos.Enum, AxisTickLabelPosition> reverse;
    
    private AxisTickLabelPosition(final STTickLblPos.Enum position) {
        this.underlying = position;
    }
    
    static AxisTickLabelPosition valueOf(final STTickLblPos.Enum position) {
        return AxisTickLabelPosition.reverse.get(position);
    }
    
    static {
        reverse = new HashMap<STTickLblPos.Enum, AxisTickLabelPosition>();
        for (final AxisTickLabelPosition value : values()) {
            AxisTickLabelPosition.reverse.put(value.underlying, value);
        }
    }
}
