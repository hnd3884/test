package org.w3c.dom.svg;

import org.w3c.dom.css.RGBColor;
import org.w3c.dom.css.CSSValue;

public interface SVGColor extends CSSValue
{
    public static final short SVG_COLORTYPE_UNKNOWN = 0;
    public static final short SVG_COLORTYPE_RGBCOLOR = 1;
    public static final short SVG_COLORTYPE_RGBCOLOR_ICCCOLOR = 2;
    public static final short SVG_COLORTYPE_CURRENTCOLOR = 3;
    
    short getColorType();
    
    RGBColor getRGBColor();
    
    SVGICCColor getICCColor();
    
    void setRGBColor(final String p0) throws SVGException;
    
    void setRGBColorICCColor(final String p0, final String p1) throws SVGException;
    
    void setColor(final short p0, final String p1, final String p2) throws SVGException;
}
