package javax.swing;

import java.awt.Color;
import java.awt.image.RGBImageFilter;

class DebugGraphicsFilter extends RGBImageFilter
{
    Color color;
    
    DebugGraphicsFilter(final Color color) {
        this.canFilterIndexColorModel = true;
        this.color = color;
    }
    
    @Override
    public int filterRGB(final int n, final int n2, final int n3) {
        return this.color.getRGB() | (n3 & 0xFF000000);
    }
}
