package sun.java2d.d3d;

import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.loops.Blit;
import java.awt.geom.AffineTransform;
import sun.java2d.pipe.Region;
import java.awt.Composite;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.SurfaceType;
import sun.java2d.SurfaceData;
import java.lang.ref.WeakReference;
import sun.java2d.loops.TransformBlit;

final class D3DGeneralTransformedBlit extends TransformBlit
{
    private final TransformBlit performop;
    private WeakReference<SurfaceData> srcTmp;
    
    D3DGeneralTransformedBlit(final TransformBlit performop) {
        super(SurfaceType.Any, CompositeType.AnyAlpha, D3DSurfaceData.D3DSurface);
        this.performop = performop;
    }
    
    @Override
    public synchronized void Transform(SurfaceData convert, final SurfaceData surfaceData, final Composite composite, final Region region, final AffineTransform affineTransform, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7) {
        final Blit fromCache = Blit.getFromCache(convert.getSurfaceType(), CompositeType.SrcNoEa, SurfaceType.IntArgbPre);
        final SurfaceData surfaceData2 = (this.srcTmp != null) ? this.srcTmp.get() : null;
        convert = GraphicsPrimitive.convertFrom(fromCache, convert, n2, n3, n6, n7, surfaceData2, 3);
        this.performop.Transform(convert, surfaceData, composite, region, affineTransform, n, 0, 0, n4, n5, n6, n7);
        if (convert != surfaceData2) {
            this.srcTmp = new WeakReference<SurfaceData>(convert);
        }
    }
}
