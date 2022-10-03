package org.apache.poi.sl.draw.geom;

import java.awt.geom.Path2D;

public interface PathCommand
{
    void execute(final Path2D.Double p0, final Context p1);
}
