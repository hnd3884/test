package java.awt;

import java.awt.image.WritableRaster;
import java.awt.image.Raster;

public interface CompositeContext
{
    void dispose();
    
    void compose(final Raster p0, final Raster p1, final WritableRaster p2);
}
