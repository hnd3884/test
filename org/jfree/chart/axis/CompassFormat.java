package org.jfree.chart.axis;

import java.text.ParsePosition;
import java.text.FieldPosition;
import java.text.NumberFormat;

public class CompassFormat extends NumberFormat
{
    private static final String N = "N";
    private static final String E = "E";
    private static final String S = "S";
    private static final String W = "W";
    public static final String[] DIRECTIONS;
    
    public String getDirectionCode(double direction) {
        direction %= 360.0;
        if (direction < 0.0) {
            direction += 360.0;
        }
        final int index = ((int)Math.floor(direction / 11.25) + 1) / 2;
        return CompassFormat.DIRECTIONS[index];
    }
    
    public StringBuffer format(final double number, final StringBuffer toAppendTo, final FieldPosition pos) {
        return toAppendTo.append(this.getDirectionCode(number));
    }
    
    public StringBuffer format(final long number, final StringBuffer toAppendTo, final FieldPosition pos) {
        return toAppendTo.append(this.getDirectionCode((double)number));
    }
    
    public Number parse(final String source, final ParsePosition parsePosition) {
        return null;
    }
    
    static {
        DIRECTIONS = new String[] { "N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW", "N" };
    }
}
