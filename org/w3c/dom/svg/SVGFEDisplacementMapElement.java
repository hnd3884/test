package org.w3c.dom.svg;

public interface SVGFEDisplacementMapElement extends SVGElement, SVGFilterPrimitiveStandardAttributes
{
    public static final short SVG_CHANNEL_UNKNOWN = 0;
    public static final short SVG_CHANNEL_R = 1;
    public static final short SVG_CHANNEL_G = 2;
    public static final short SVG_CHANNEL_B = 3;
    public static final short SVG_CHANNEL_A = 4;
    
    SVGAnimatedString getIn1();
    
    SVGAnimatedString getIn2();
    
    SVGAnimatedNumber getScale();
    
    SVGAnimatedEnumeration getXChannelSelector();
    
    SVGAnimatedEnumeration getYChannelSelector();
}
