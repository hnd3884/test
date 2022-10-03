package sun.java2d.cmm;

import java.awt.image.WritableRaster;
import java.awt.image.Raster;
import java.awt.image.BufferedImage;

public interface ColorTransform
{
    public static final int Any = -1;
    public static final int In = 1;
    public static final int Out = 2;
    public static final int Gamut = 3;
    public static final int Simulation = 4;
    
    int getNumInComponents();
    
    int getNumOutComponents();
    
    void colorConvert(final BufferedImage p0, final BufferedImage p1);
    
    void colorConvert(final Raster p0, final WritableRaster p1, final float[] p2, final float[] p3, final float[] p4, final float[] p5);
    
    void colorConvert(final Raster p0, final WritableRaster p1);
    
    short[] colorConvert(final short[] p0, final short[] p1);
    
    byte[] colorConvert(final byte[] p0, final byte[] p1);
}
