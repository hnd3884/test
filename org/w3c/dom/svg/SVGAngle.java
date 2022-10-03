package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGAngle
{
    public static final short SVG_ANGLETYPE_UNKNOWN = 0;
    public static final short SVG_ANGLETYPE_UNSPECIFIED = 1;
    public static final short SVG_ANGLETYPE_DEG = 2;
    public static final short SVG_ANGLETYPE_RAD = 3;
    public static final short SVG_ANGLETYPE_GRAD = 4;
    
    short getUnitType();
    
    float getValue();
    
    void setValue(final float p0) throws DOMException;
    
    float getValueInSpecifiedUnits();
    
    void setValueInSpecifiedUnits(final float p0) throws DOMException;
    
    String getValueAsString();
    
    void setValueAsString(final String p0) throws DOMException;
    
    void newValueSpecifiedUnits(final short p0, final float p1);
    
    void convertToSpecifiedUnits(final short p0);
}
