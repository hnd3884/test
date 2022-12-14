package org.w3c.dom.svg;

public interface SVGFEBlendElement extends SVGElement, SVGFilterPrimitiveStandardAttributes
{
    public static final short SVG_FEBLEND_MODE_UNKNOWN = 0;
    public static final short SVG_FEBLEND_MODE_NORMAL = 1;
    public static final short SVG_FEBLEND_MODE_MULTIPLY = 2;
    public static final short SVG_FEBLEND_MODE_SCREEN = 3;
    public static final short SVG_FEBLEND_MODE_DARKEN = 4;
    public static final short SVG_FEBLEND_MODE_LIGHTEN = 5;
    
    SVGAnimatedString getIn1();
    
    SVGAnimatedString getIn2();
    
    SVGAnimatedEnumeration getMode();
}
