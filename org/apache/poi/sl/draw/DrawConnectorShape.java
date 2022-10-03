package org.apache.poi.sl.draw;

import org.apache.poi.sl.usermodel.SimpleShape;
import org.apache.poi.sl.usermodel.ConnectorShape;

public class DrawConnectorShape extends DrawSimpleShape
{
    public DrawConnectorShape(final ConnectorShape<?, ?> shape) {
        super(shape);
    }
}
