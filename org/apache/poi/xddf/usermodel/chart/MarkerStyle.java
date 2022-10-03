package org.apache.poi.xddf.usermodel.chart;

import java.util.HashMap;
import org.openxmlformats.schemas.drawingml.x2006.chart.STMarkerStyle;

public enum MarkerStyle
{
    CIRCLE(STMarkerStyle.CIRCLE), 
    DASH(STMarkerStyle.DASH), 
    DIAMOND(STMarkerStyle.DIAMOND), 
    DOT(STMarkerStyle.DOT), 
    NONE(STMarkerStyle.NONE), 
    PICTURE(STMarkerStyle.PICTURE), 
    PLUS(STMarkerStyle.PLUS), 
    SQUARE(STMarkerStyle.SQUARE), 
    STAR(STMarkerStyle.STAR), 
    TRIANGLE(STMarkerStyle.TRIANGLE), 
    X(STMarkerStyle.X);
    
    final STMarkerStyle.Enum underlying;
    private static final HashMap<STMarkerStyle.Enum, MarkerStyle> reverse;
    
    private MarkerStyle(final STMarkerStyle.Enum style) {
        this.underlying = style;
    }
    
    static MarkerStyle valueOf(final STMarkerStyle.Enum style) {
        return MarkerStyle.reverse.get(style);
    }
    
    static {
        reverse = new HashMap<STMarkerStyle.Enum, MarkerStyle>();
        for (final MarkerStyle value : values()) {
            MarkerStyle.reverse.put(value.underlying, value);
        }
    }
}
