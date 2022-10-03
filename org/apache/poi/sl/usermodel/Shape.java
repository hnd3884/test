package org.apache.poi.sl.usermodel;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public interface Shape<S extends Shape<S, P>, P extends TextParagraph<S, P, ? extends TextRun>>
{
    ShapeContainer<S, P> getParent();
    
    Sheet<S, P> getSheet();
    
    Rectangle2D getAnchor();
    
    String getShapeName();
    
    void draw(final Graphics2D p0, final Rectangle2D p1);
    
    int getShapeId();
}
