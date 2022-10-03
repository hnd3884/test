package org.apache.poi.sl.draw;

import org.apache.poi.sl.usermodel.TextShape;
import org.apache.poi.sl.usermodel.AutoShape;

public class DrawAutoShape extends DrawTextShape
{
    public DrawAutoShape(final AutoShape<?, ?> shape) {
        super(shape);
    }
}
