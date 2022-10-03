package org.apache.poi.sl.usermodel;

import java.awt.geom.Rectangle2D;

public interface GroupShape<S extends Shape<S, P>, P extends TextParagraph<S, P, ? extends TextRun>> extends Shape<S, P>, ShapeContainer<S, P>, PlaceableShape<S, P>
{
    Rectangle2D getInteriorAnchor();
    
    void setInteriorAnchor(final Rectangle2D p0);
}
