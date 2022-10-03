package sun.java2d.opengl;

import java.awt.Color;
import sun.java2d.loops.CompositeType;
import sun.java2d.SurfaceData;
import sun.java2d.SurfaceDataProxy;

public class OGLSurfaceDataProxy extends SurfaceDataProxy
{
    OGLGraphicsConfig oglgc;
    int transparency;
    
    public static SurfaceDataProxy createProxy(final SurfaceData surfaceData, final OGLGraphicsConfig oglGraphicsConfig) {
        if (surfaceData instanceof OGLSurfaceData) {
            return OGLSurfaceDataProxy.UNCACHED;
        }
        return new OGLSurfaceDataProxy(oglGraphicsConfig, surfaceData.getTransparency());
    }
    
    public OGLSurfaceDataProxy(final OGLGraphicsConfig oglgc, final int transparency) {
        this.oglgc = oglgc;
        this.transparency = transparency;
    }
    
    @Override
    public SurfaceData validateSurfaceData(final SurfaceData surfaceData, SurfaceData managedSurface, final int n, final int n2) {
        if (managedSurface == null) {
            try {
                managedSurface = this.oglgc.createManagedSurface(n, n2, this.transparency);
            }
            catch (final OutOfMemoryError outOfMemoryError) {
                return null;
            }
        }
        return managedSurface;
    }
    
    @Override
    public boolean isSupportedOperation(final SurfaceData surfaceData, final int n, final CompositeType compositeType, final Color color) {
        return compositeType.isDerivedFrom(CompositeType.AnyAlpha) && (color == null || this.transparency == 1);
    }
}
