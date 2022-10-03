package org.w3c.dom.svg;

public interface SVGTextPathElement extends SVGTextContentElement, SVGURIReference
{
    public static final short TEXTPATH_METHODTYPE_UNKNOWN = 0;
    public static final short TEXTPATH_METHODTYPE_ALIGN = 1;
    public static final short TEXTPATH_METHODTYPE_STRETCH = 2;
    public static final short TEXTPATH_SPACINGTYPE_UNKNOWN = 0;
    public static final short TEXTPATH_SPACINGTYPE_AUTO = 1;
    public static final short TEXTPATH_SPACINGTYPE_EXACT = 2;
    
    SVGAnimatedLength getStartOffset();
    
    SVGAnimatedEnumeration getMethod();
    
    SVGAnimatedEnumeration getSpacing();
}
