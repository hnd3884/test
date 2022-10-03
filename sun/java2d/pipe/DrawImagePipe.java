package sun.java2d.pipe;

import java.awt.image.BufferedImageOp;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;
import java.awt.Color;
import java.awt.Image;
import sun.java2d.SunGraphics2D;

public interface DrawImagePipe
{
    boolean copyImage(final SunGraphics2D p0, final Image p1, final int p2, final int p3, final Color p4, final ImageObserver p5);
    
    boolean copyImage(final SunGraphics2D p0, final Image p1, final int p2, final int p3, final int p4, final int p5, final int p6, final int p7, final Color p8, final ImageObserver p9);
    
    boolean scaleImage(final SunGraphics2D p0, final Image p1, final int p2, final int p3, final int p4, final int p5, final Color p6, final ImageObserver p7);
    
    boolean scaleImage(final SunGraphics2D p0, final Image p1, final int p2, final int p3, final int p4, final int p5, final int p6, final int p7, final int p8, final int p9, final Color p10, final ImageObserver p11);
    
    boolean transformImage(final SunGraphics2D p0, final Image p1, final AffineTransform p2, final ImageObserver p3);
    
    void transformImage(final SunGraphics2D p0, final BufferedImage p1, final BufferedImageOp p2, final int p3, final int p4);
}
