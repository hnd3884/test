package org.apache.poi.sl.usermodel;

import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import java.util.List;

public interface TextShape<S extends Shape<S, P>, P extends TextParagraph<S, P, ? extends TextRun>> extends SimpleShape<S, P>, Iterable<P>
{
    String getText();
    
    TextRun setText(final String p0);
    
    TextRun appendText(final String p0, final boolean p1);
    
    List<P> getTextParagraphs();
    
    Insets2D getInsets();
    
    void setInsets(final Insets2D p0);
    
    double getTextHeight();
    
    double getTextHeight(final Graphics2D p0);
    
    VerticalAlignment getVerticalAlignment();
    
    void setVerticalAlignment(final VerticalAlignment p0);
    
    boolean isHorizontalCentered();
    
    void setHorizontalCentered(final Boolean p0);
    
    boolean getWordWrap();
    
    void setWordWrap(final boolean p0);
    
    TextDirection getTextDirection();
    
    void setTextDirection(final TextDirection p0);
    
    Double getTextRotation();
    
    void setTextRotation(final Double p0);
    
    void setTextPlaceholder(final TextPlaceholder p0);
    
    TextPlaceholder getTextPlaceholder();
    
    Rectangle2D resizeToFitText();
    
    Rectangle2D resizeToFitText(final Graphics2D p0);
    
    public enum TextDirection
    {
        HORIZONTAL, 
        VERTICAL, 
        VERTICAL_270, 
        STACKED;
    }
    
    public enum TextAutofit
    {
        NONE, 
        NORMAL, 
        SHAPE;
    }
    
    public enum TextPlaceholder
    {
        TITLE(0), 
        BODY(1), 
        CENTER_TITLE(6), 
        CENTER_BODY(5), 
        HALF_BODY(7), 
        QUARTER_BODY(8), 
        NOTES(2), 
        OTHER(4);
        
        public final int nativeId;
        
        private TextPlaceholder(final int nativeId) {
            this.nativeId = nativeId;
        }
        
        public static TextPlaceholder fromNativeId(final int nativeId) {
            for (final TextPlaceholder ld : values()) {
                if (ld.nativeId == nativeId) {
                    return ld;
                }
            }
            return null;
        }
        
        public static boolean isTitle(final int nativeId) {
            return nativeId == TextPlaceholder.TITLE.nativeId || nativeId == TextPlaceholder.CENTER_TITLE.nativeId;
        }
    }
}
