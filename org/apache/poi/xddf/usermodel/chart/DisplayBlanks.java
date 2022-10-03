package org.apache.poi.xddf.usermodel.chart;

import java.util.HashMap;
import org.openxmlformats.schemas.drawingml.x2006.chart.STDispBlanksAs;

public enum DisplayBlanks
{
    GAP(STDispBlanksAs.GAP), 
    SPAN(STDispBlanksAs.SPAN), 
    ZERO(STDispBlanksAs.ZERO);
    
    final STDispBlanksAs.Enum underlying;
    private static final HashMap<STDispBlanksAs.Enum, DisplayBlanks> reverse;
    
    private DisplayBlanks(final STDispBlanksAs.Enum mode) {
        this.underlying = mode;
    }
    
    static DisplayBlanks valueOf(final STDispBlanksAs.Enum mode) {
        return DisplayBlanks.reverse.get(mode);
    }
    
    static {
        reverse = new HashMap<STDispBlanksAs.Enum, DisplayBlanks>();
        for (final DisplayBlanks value : values()) {
            DisplayBlanks.reverse.put(value.underlying, value);
        }
    }
}
