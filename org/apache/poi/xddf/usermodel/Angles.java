package org.apache.poi.xddf.usermodel;

public class Angles
{
    public static final int OOXML_DEGREE = 60000;
    
    public static final int degreesToAttribute(final double angle) {
        return Math.toIntExact(Math.round(60000.0 * angle));
    }
    
    public static final double attributeToDegrees(final int angle) {
        return angle / 60000.0;
    }
}
