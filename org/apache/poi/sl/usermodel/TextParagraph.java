package org.apache.poi.sl.usermodel;

import java.awt.Color;
import java.util.List;

public interface TextParagraph<S extends Shape<S, P>, P extends TextParagraph<S, P, T>, T extends TextRun> extends Iterable<T>
{
    Double getSpaceBefore();
    
    void setSpaceBefore(final Double p0);
    
    Double getSpaceAfter();
    
    void setSpaceAfter(final Double p0);
    
    Double getLeftMargin();
    
    void setLeftMargin(final Double p0);
    
    Double getRightMargin();
    
    void setRightMargin(final Double p0);
    
    Double getIndent();
    
    void setIndent(final Double p0);
    
    int getIndentLevel();
    
    void setIndentLevel(final int p0);
    
    Double getLineSpacing();
    
    void setLineSpacing(final Double p0);
    
    String getDefaultFontFamily();
    
    Double getDefaultFontSize();
    
    TextAlign getTextAlign();
    
    void setTextAlign(final TextAlign p0);
    
    FontAlign getFontAlign();
    
    BulletStyle getBulletStyle();
    
    void setBulletStyle(final Object... p0);
    
    Double getDefaultTabSize();
    
    TextShape<S, P> getParentShape();
    
    List<T> getTextRuns();
    
    boolean isHeaderOrFooter();
    
    List<? extends TabStop> getTabStops();
    
    void addTabStops(final double p0, final TabStop.TabStopType p1);
    
    void clearTabStops();
    
    public enum TextAlign
    {
        LEFT, 
        CENTER, 
        RIGHT, 
        JUSTIFY, 
        JUSTIFY_LOW, 
        DIST, 
        THAI_DIST;
    }
    
    public enum FontAlign
    {
        AUTO, 
        TOP, 
        CENTER, 
        BASELINE, 
        BOTTOM;
    }
    
    public interface BulletStyle
    {
        String getBulletCharacter();
        
        String getBulletFont();
        
        Double getBulletFontSize();
        
        void setBulletFontColor(final Color p0);
        
        void setBulletFontColor(final PaintStyle p0);
        
        PaintStyle getBulletFontColor();
        
        AutoNumberingScheme getAutoNumberingScheme();
        
        Integer getAutoNumberingStartAt();
    }
}
