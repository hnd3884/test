package org.apache.poi.xdgf.geom;

import org.apache.poi.util.Removal;
import org.apache.poi.util.Dimension2DDouble;

@Deprecated
@Removal(version = "5.0.0")
public class Dimension2dDouble extends Dimension2DDouble
{
    public Dimension2dDouble() {
    }
    
    public Dimension2dDouble(final double width, final double height) {
        super(width, height);
    }
}
