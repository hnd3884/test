package org.apache.poi.sl.draw.geom;

import java.awt.geom.Path2D;

public class ClosePathCommand implements PathCommand
{
    ClosePathCommand() {
    }
    
    @Override
    public void execute(final Path2D.Double path, final Context ctx) {
        path.closePath();
    }
}
