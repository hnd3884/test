package org.apache.poi.sl.usermodel;

import java.awt.geom.Path2D;

public interface FreeformShape<S extends Shape<S, P>, P extends TextParagraph<S, P, ? extends TextRun>> extends AutoShape<S, P>
{
    Path2D getPath();
    
    int setPath(final Path2D p0);
}
