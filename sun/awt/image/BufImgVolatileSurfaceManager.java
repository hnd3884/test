package sun.awt.image;

import sun.java2d.SurfaceData;

public class BufImgVolatileSurfaceManager extends VolatileSurfaceManager
{
    public BufImgVolatileSurfaceManager(final SunVolatileImage sunVolatileImage, final Object o) {
        super(sunVolatileImage, o);
    }
    
    @Override
    protected boolean isAccelerationEnabled() {
        return false;
    }
    
    @Override
    protected SurfaceData initAcceleratedSurface() {
        return null;
    }
}
