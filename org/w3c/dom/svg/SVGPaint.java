package org.w3c.dom.svg;

public interface SVGPaint extends SVGColor
{
    public static final short SVG_PAINTTYPE_UNKNOWN = 0;
    public static final short SVG_PAINTTYPE_RGBCOLOR = 1;
    public static final short SVG_PAINTTYPE_RGBCOLOR_ICCCOLOR = 2;
    public static final short SVG_PAINTTYPE_NONE = 101;
    public static final short SVG_PAINTTYPE_CURRENTCOLOR = 102;
    public static final short SVG_PAINTTYPE_URI_NONE = 103;
    public static final short SVG_PAINTTYPE_URI_CURRENTCOLOR = 104;
    public static final short SVG_PAINTTYPE_URI_RGBCOLOR = 105;
    public static final short SVG_PAINTTYPE_URI_RGBCOLOR_ICCCOLOR = 106;
    public static final short SVG_PAINTTYPE_URI = 107;
    
    short getPaintType();
    
    String getUri();
    
    void setUri(final String p0);
    
    void setPaint(final short p0, final String p1, final String p2, final String p3) throws SVGException;
}
