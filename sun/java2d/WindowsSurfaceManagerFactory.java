package sun.java2d;

import java.awt.GraphicsConfiguration;
import sun.awt.image.BufImgVolatileSurfaceManager;
import sun.java2d.opengl.WGLVolatileSurfaceManager;
import sun.java2d.opengl.WGLGraphicsConfig;
import sun.java2d.d3d.D3DVolatileSurfaceManager;
import sun.java2d.d3d.D3DGraphicsConfig;
import sun.awt.image.VolatileSurfaceManager;
import sun.awt.image.SunVolatileImage;

public class WindowsSurfaceManagerFactory extends SurfaceManagerFactory
{
    @Override
    public VolatileSurfaceManager createVolatileManager(final SunVolatileImage sunVolatileImage, final Object o) {
        final GraphicsConfiguration graphicsConfig = sunVolatileImage.getGraphicsConfig();
        if (graphicsConfig instanceof D3DGraphicsConfig) {
            return new D3DVolatileSurfaceManager(sunVolatileImage, o);
        }
        if (graphicsConfig instanceof WGLGraphicsConfig) {
            return new WGLVolatileSurfaceManager(sunVolatileImage, o);
        }
        return new BufImgVolatileSurfaceManager(sunVolatileImage, o);
    }
}
