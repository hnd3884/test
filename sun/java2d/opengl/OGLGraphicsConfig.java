package sun.java2d.opengl;

import sun.java2d.pipe.BufferedContext;
import sun.java2d.SurfaceData;
import sun.awt.image.SurfaceManager;
import sun.java2d.pipe.hw.AccelGraphicsConfig;

interface OGLGraphicsConfig extends AccelGraphicsConfig, SurfaceManager.ProxiedGraphicsConfig
{
    OGLContext getContext();
    
    long getNativeConfigInfo();
    
    boolean isCapPresent(final int p0);
    
    SurfaceData createManagedSurface(final int p0, final int p1, final int p2);
}
