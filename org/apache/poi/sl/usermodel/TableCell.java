package org.apache.poi.sl.usermodel;

import java.awt.Color;

public interface TableCell<S extends Shape<S, P>, P extends TextParagraph<S, P, ? extends TextRun>> extends TextShape<S, P>
{
    StrokeStyle getBorderStyle(final BorderEdge p0);
    
    void setBorderStyle(final BorderEdge p0, final StrokeStyle p1);
    
    void setBorderWidth(final BorderEdge p0, final double p1);
    
    void setBorderColor(final BorderEdge p0, final Color p1);
    
    void setBorderCompound(final BorderEdge p0, final StrokeStyle.LineCompound p1);
    
    void setBorderDash(final BorderEdge p0, final StrokeStyle.LineDash p1);
    
    void removeBorder(final BorderEdge p0);
    
    int getGridSpan();
    
    int getRowSpan();
    
    boolean isMerged();
    
    public enum BorderEdge
    {
        bottom, 
        left, 
        top, 
        right;
    }
}
