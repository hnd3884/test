package org.w3c.dom.svg;

public interface SVGFETurbulenceElement extends SVGElement, SVGFilterPrimitiveStandardAttributes
{
    public static final short SVG_TURBULENCE_TYPE_UNKNOWN = 0;
    public static final short SVG_TURBULENCE_TYPE_FRACTALNOISE = 1;
    public static final short SVG_TURBULENCE_TYPE_TURBULENCE = 2;
    public static final short SVG_STITCHTYPE_UNKNOWN = 0;
    public static final short SVG_STITCHTYPE_STITCH = 1;
    public static final short SVG_STITCHTYPE_NOSTITCH = 2;
    
    SVGAnimatedNumber getBaseFrequencyX();
    
    SVGAnimatedNumber getBaseFrequencyY();
    
    SVGAnimatedInteger getNumOctaves();
    
    SVGAnimatedNumber getSeed();
    
    SVGAnimatedEnumeration getStitchTiles();
    
    SVGAnimatedEnumeration getType();
}
