package javax.imageio.event;

import java.awt.image.BufferedImage;
import javax.imageio.ImageReader;
import java.util.EventListener;

public interface IIOReadUpdateListener extends EventListener
{
    void passStarted(final ImageReader p0, final BufferedImage p1, final int p2, final int p3, final int p4, final int p5, final int p6, final int p7, final int p8, final int[] p9);
    
    void imageUpdate(final ImageReader p0, final BufferedImage p1, final int p2, final int p3, final int p4, final int p5, final int p6, final int p7, final int[] p8);
    
    void passComplete(final ImageReader p0, final BufferedImage p1);
    
    void thumbnailPassStarted(final ImageReader p0, final BufferedImage p1, final int p2, final int p3, final int p4, final int p5, final int p6, final int p7, final int p8, final int[] p9);
    
    void thumbnailUpdate(final ImageReader p0, final BufferedImage p1, final int p2, final int p3, final int p4, final int p5, final int p6, final int p7, final int[] p8);
    
    void thumbnailPassComplete(final ImageReader p0, final BufferedImage p1);
}
