package sun.java2d.d3d;

import java.awt.Color;
import sun.java2d.loops.CompositeType;
import sun.java2d.InvalidPipeException;
import sun.java2d.SurfaceData;
import sun.java2d.SurfaceDataProxy;

public class D3DSurfaceDataProxy extends SurfaceDataProxy
{
    D3DGraphicsConfig d3dgc;
    int transparency;
    
    public static SurfaceDataProxy createProxy(final SurfaceData surfaceData, final D3DGraphicsConfig d3DGraphicsConfig) {
        if (surfaceData instanceof D3DSurfaceData) {
            return D3DSurfaceDataProxy.UNCACHED;
        }
        return new D3DSurfaceDataProxy(d3DGraphicsConfig, surfaceData.getTransparency());
    }
    
    public D3DSurfaceDataProxy(final D3DGraphicsConfig d3dgc, final int transparency) {
        this.d3dgc = d3dgc;
        this.transparency = transparency;
        this.activateDisplayListener();
    }
    
    @Override
    public SurfaceData validateSurfaceData(final SurfaceData surfaceData, SurfaceData managedSurface, final int n, final int n2) {
        if (managedSurface != null) {
            if (!managedSurface.isSurfaceLost()) {
                return managedSurface;
            }
        }
        try {
            managedSurface = this.d3dgc.createManagedSurface(n, n2, this.transparency);
        }
        catch (final InvalidPipeException ex) {
            this.d3dgc.getD3DDevice();
            if (!D3DGraphicsDevice.isD3DAvailable()) {
                this.invalidate();
                this.flush();
                return null;
            }
        }
        return managedSurface;
    }
    
    @Override
    public boolean isSupportedOperation(final SurfaceData surfaceData, final int n, final CompositeType compositeType, final Color color) {
        return color == null || this.transparency == 1;
    }
}
