package sun.java2d.pipe.hw;

import java.awt.Graphics2D;
import java.awt.ImageCapabilities;
import java.awt.Component;
import java.awt.GraphicsConfiguration;
import sun.awt.image.SunVolatileImage;

public class AccelTypedVolatileImage extends SunVolatileImage
{
    public AccelTypedVolatileImage(final GraphicsConfiguration graphicsConfiguration, final int n, final int n2, final int n3, final int n4) {
        super(null, graphicsConfiguration, n, n2, null, n3, null, n4);
    }
    
    @Override
    public Graphics2D createGraphics() {
        if (this.getForcedAccelSurfaceType() == 3) {
            throw new UnsupportedOperationException("Can't render to a non-RT Texture");
        }
        return super.createGraphics();
    }
}
