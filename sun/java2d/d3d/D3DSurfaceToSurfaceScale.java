package sun.java2d.d3d;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImageOp;
import java.awt.image.BufferedImage;
import sun.java2d.pipe.Region;
import java.awt.Composite;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.ScaledBlit;

class D3DSurfaceToSurfaceScale extends ScaledBlit
{
    D3DSurfaceToSurfaceScale() {
        super(D3DSurfaceData.D3DSurface, CompositeType.AnyAlpha, D3DSurfaceData.D3DSurface);
    }
    
    @Override
    public void Scale(final SurfaceData surfaceData, final SurfaceData surfaceData2, final Composite composite, final Region region, final int n, final int n2, final int n3, final int n4, final double n5, final double n6, final double n7, final double n8) {
        D3DBlitLoops.IsoBlit(surfaceData, surfaceData2, null, null, composite, region, null, 1, n, n2, n3, n4, n5, n6, n7, n8, false);
    }
}
