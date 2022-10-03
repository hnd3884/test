package java.awt;

import java.awt.image.Raster;
import java.awt.image.ColorModel;

public interface PaintContext
{
    void dispose();
    
    ColorModel getColorModel();
    
    Raster getRaster(final int p0, final int p1, final int p2, final int p3);
}
