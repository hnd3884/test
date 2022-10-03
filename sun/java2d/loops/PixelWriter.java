package sun.java2d.loops;

import java.awt.image.WritableRaster;

abstract class PixelWriter
{
    protected WritableRaster dstRast;
    
    public void setRaster(final WritableRaster dstRast) {
        this.dstRast = dstRast;
    }
    
    public abstract void writePixel(final int p0, final int p1);
}
