package org.apache.poi.sl.usermodel;

import java.util.List;
import java.awt.geom.Point2D;
import java.awt.geom.Dimension2D;
import java.io.InputStream;

public interface PaintStyle
{
    public enum PaintModifier
    {
        NONE, 
        NORM, 
        LIGHTEN, 
        LIGHTEN_LESS, 
        DARKEN, 
        DARKEN_LESS;
    }
    
    public enum FlipMode
    {
        NONE, 
        X, 
        Y, 
        XY;
    }
    
    public enum TextureAlignment
    {
        BOTTOM("b"), 
        BOTTOM_LEFT("bl"), 
        BOTTOM_RIGHT("br"), 
        CENTER("ctr"), 
        LEFT("l"), 
        RIGHT("r"), 
        TOP("t"), 
        TOP_LEFT("tl"), 
        TOP_RIGHT("tr");
        
        private final String ooxmlId;
        
        private TextureAlignment(final String ooxmlId) {
            this.ooxmlId = ooxmlId;
        }
        
        public static TextureAlignment fromOoxmlId(final String ooxmlId) {
            for (final TextureAlignment ta : values()) {
                if (ta.ooxmlId.equals(ooxmlId)) {
                    return ta;
                }
            }
            return null;
        }
    }
    
    public interface GradientPaint extends PaintStyle
    {
        double getGradientAngle();
        
        ColorStyle[] getGradientColors();
        
        float[] getGradientFractions();
        
        boolean isRotatedWithShape();
        
        GradientType getGradientType();
        
        default Insets2D getFillToInsets() {
            return null;
        }
        
        public enum GradientType
        {
            linear, 
            circular, 
            rectangular, 
            shape;
        }
    }
    
    public interface TexturePaint extends PaintStyle
    {
        InputStream getImageData();
        
        String getContentType();
        
        int getAlpha();
        
        default boolean isRotatedWithShape() {
            return true;
        }
        
        default Dimension2D getScale() {
            return null;
        }
        
        default Point2D getOffset() {
            return null;
        }
        
        default FlipMode getFlipMode() {
            return FlipMode.NONE;
        }
        
        default TextureAlignment getAlignment() {
            return null;
        }
        
        default Insets2D getInsets() {
            return null;
        }
        
        default Insets2D getStretch() {
            return null;
        }
        
        default List<ColorStyle> getDuoTone() {
            return null;
        }
    }
    
    public interface SolidPaint extends PaintStyle
    {
        ColorStyle getSolidColor();
    }
}
