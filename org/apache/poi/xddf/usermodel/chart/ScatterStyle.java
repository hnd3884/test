package org.apache.poi.xddf.usermodel.chart;

import java.util.HashMap;
import org.openxmlformats.schemas.drawingml.x2006.chart.STScatterStyle;

public enum ScatterStyle
{
    LINE(STScatterStyle.LINE), 
    LINE_MARKER(STScatterStyle.LINE_MARKER), 
    MARKER(STScatterStyle.MARKER), 
    NONE(STScatterStyle.NONE), 
    SMOOTH(STScatterStyle.SMOOTH), 
    SMOOTH_MARKER(STScatterStyle.SMOOTH_MARKER);
    
    final STScatterStyle.Enum underlying;
    private static final HashMap<STScatterStyle.Enum, ScatterStyle> reverse;
    
    private ScatterStyle(final STScatterStyle.Enum style) {
        this.underlying = style;
    }
    
    static ScatterStyle valueOf(final STScatterStyle.Enum style) {
        return ScatterStyle.reverse.get(style);
    }
    
    static {
        reverse = new HashMap<STScatterStyle.Enum, ScatterStyle>();
        for (final ScatterStyle value : values()) {
            ScatterStyle.reverse.put(value.underlying, value);
        }
    }
}
