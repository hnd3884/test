package org.apache.poi.sl.draw;

import org.apache.poi.sl.usermodel.AutoShape;
import org.apache.poi.sl.usermodel.FreeformShape;

public class DrawFreeformShape extends DrawAutoShape
{
    public DrawFreeformShape(final FreeformShape<?, ?> shape) {
        super(shape);
    }
}
