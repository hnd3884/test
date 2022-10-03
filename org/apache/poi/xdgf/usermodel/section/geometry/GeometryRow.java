package org.apache.poi.xdgf.usermodel.section.geometry;

import org.apache.poi.xdgf.usermodel.XDGFShape;
import java.awt.geom.Path2D;

public interface GeometryRow
{
    void setupMaster(final GeometryRow p0);
    
    void addToPath(final Path2D.Double p0, final XDGFShape p1);
}
