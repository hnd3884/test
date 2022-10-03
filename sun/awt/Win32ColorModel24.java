package sun.awt;

import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.SampleModel;
import java.awt.Point;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.color.ColorSpace;
import java.awt.image.ComponentColorModel;

public class Win32ColorModel24 extends ComponentColorModel
{
    public Win32ColorModel24() {
        super(ColorSpace.getInstance(1000), new int[] { 8, 8, 8 }, false, false, 1, 0);
    }
    
    @Override
    public WritableRaster createCompatibleWritableRaster(final int n, final int n2) {
        return Raster.createInterleavedRaster(0, n, n2, n * 3, 3, new int[] { 2, 1, 0 }, null);
    }
    
    @Override
    public SampleModel createCompatibleSampleModel(final int n, final int n2) {
        return new PixelInterleavedSampleModel(0, n, n2, 3, n * 3, new int[] { 2, 1, 0 });
    }
}
