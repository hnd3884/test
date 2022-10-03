package sun.java2d;

import sun.awt.image.VolatileSurfaceManager;
import sun.awt.image.SunVolatileImage;

public abstract class SurfaceManagerFactory
{
    private static SurfaceManagerFactory instance;
    
    public static synchronized SurfaceManagerFactory getInstance() {
        if (SurfaceManagerFactory.instance == null) {
            throw new IllegalStateException("No SurfaceManagerFactory set.");
        }
        return SurfaceManagerFactory.instance;
    }
    
    public static synchronized void setInstance(final SurfaceManagerFactory instance) {
        if (instance == null) {
            throw new IllegalArgumentException("factory must be non-null");
        }
        if (SurfaceManagerFactory.instance != null) {
            throw new IllegalStateException("The surface manager factory is already initialized");
        }
        SurfaceManagerFactory.instance = instance;
    }
    
    public abstract VolatileSurfaceManager createVolatileManager(final SunVolatileImage p0, final Object p1);
}
