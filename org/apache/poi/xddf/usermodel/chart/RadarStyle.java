package org.apache.poi.xddf.usermodel.chart;

import java.util.HashMap;
import org.openxmlformats.schemas.drawingml.x2006.chart.STRadarStyle;

public enum RadarStyle
{
    FILLED(STRadarStyle.FILLED), 
    MARKER(STRadarStyle.MARKER), 
    STANDARD(STRadarStyle.STANDARD);
    
    final STRadarStyle.Enum underlying;
    private static final HashMap<STRadarStyle.Enum, RadarStyle> reverse;
    
    private RadarStyle(final STRadarStyle.Enum style) {
        this.underlying = style;
    }
    
    static RadarStyle valueOf(final STRadarStyle.Enum style) {
        return RadarStyle.reverse.get(style);
    }
    
    static {
        reverse = new HashMap<STRadarStyle.Enum, RadarStyle>();
        for (final RadarStyle value : values()) {
            RadarStyle.reverse.put(value.underlying, value);
        }
    }
}
