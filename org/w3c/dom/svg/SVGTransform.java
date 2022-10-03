package org.w3c.dom.svg;

public interface SVGTransform
{
    public static final short SVG_TRANSFORM_UNKNOWN = 0;
    public static final short SVG_TRANSFORM_MATRIX = 1;
    public static final short SVG_TRANSFORM_TRANSLATE = 2;
    public static final short SVG_TRANSFORM_SCALE = 3;
    public static final short SVG_TRANSFORM_ROTATE = 4;
    public static final short SVG_TRANSFORM_SKEWX = 5;
    public static final short SVG_TRANSFORM_SKEWY = 6;
    
    short getType();
    
    SVGMatrix getMatrix();
    
    float getAngle();
    
    void setMatrix(final SVGMatrix p0);
    
    void setTranslate(final float p0, final float p1);
    
    void setScale(final float p0, final float p1);
    
    void setRotate(final float p0, final float p1, final float p2);
    
    void setSkewX(final float p0);
    
    void setSkewY(final float p0);
}
