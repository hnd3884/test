package org.apache.poi.sl.draw.geom;

public interface Formula
{
    public static final double OOXML_DEGREE = 60000.0;
    
    double evaluate(final Context p0);
}
