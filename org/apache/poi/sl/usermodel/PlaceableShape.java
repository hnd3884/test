package org.apache.poi.sl.usermodel;

import java.awt.geom.Rectangle2D;

public interface PlaceableShape<S extends Shape<S, P>, P extends TextParagraph<S, P, ? extends TextRun>>
{
    ShapeContainer<S, P> getParent();
    
    Sheet<S, P> getSheet();
    
    Rectangle2D getAnchor();
    
    void setAnchor(final Rectangle2D p0);
    
    double getRotation();
    
    void setRotation(final double p0);
    
    void setFlipHorizontal(final boolean p0);
    
    void setFlipVertical(final boolean p0);
    
    boolean getFlipHorizontal();
    
    boolean getFlipVertical();
}
