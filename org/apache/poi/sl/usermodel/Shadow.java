package org.apache.poi.sl.usermodel;

public interface Shadow<S extends Shape<S, P>, P extends TextParagraph<S, P, ? extends TextRun>>
{
    SimpleShape<S, P> getShadowParent();
    
    double getDistance();
    
    double getAngle();
    
    double getBlur();
    
    PaintStyle.SolidPaint getFillStyle();
}
