package java.awt.image;

import java.util.Hashtable;

public interface ImageConsumer
{
    public static final int RANDOMPIXELORDER = 1;
    public static final int TOPDOWNLEFTRIGHT = 2;
    public static final int COMPLETESCANLINES = 4;
    public static final int SINGLEPASS = 8;
    public static final int SINGLEFRAME = 16;
    public static final int IMAGEERROR = 1;
    public static final int SINGLEFRAMEDONE = 2;
    public static final int STATICIMAGEDONE = 3;
    public static final int IMAGEABORTED = 4;
    
    void setDimensions(final int p0, final int p1);
    
    void setProperties(final Hashtable<?, ?> p0);
    
    void setColorModel(final ColorModel p0);
    
    void setHints(final int p0);
    
    void setPixels(final int p0, final int p1, final int p2, final int p3, final ColorModel p4, final byte[] p5, final int p6, final int p7);
    
    void setPixels(final int p0, final int p1, final int p2, final int p3, final ColorModel p4, final int[] p5, final int p6, final int p7);
    
    void imageComplete(final int p0);
}
