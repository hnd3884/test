package sun.java2d.opengl;

import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.pipe.Region;
import java.awt.Composite;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.SurfaceType;
import java.lang.ref.WeakReference;
import sun.java2d.loops.Blit;

class OGLGeneralBlit extends Blit
{
    private final Blit performop;
    private WeakReference srcTmp;
    
    OGLGeneralBlit(final SurfaceType surfaceType, final CompositeType compositeType, final Blit performop) {
        super(SurfaceType.Any, compositeType, surfaceType);
        this.performop = performop;
    }
    
    @Override
    public synchronized void Blit(SurfaceData convert, final SurfaceData surfaceData, final Composite composite, final Region region, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        final Blit fromCache = Blit.getFromCache(convert.getSurfaceType(), CompositeType.SrcNoEa, SurfaceType.IntArgbPre);
        SurfaceData surfaceData2 = null;
        if (this.srcTmp != null) {
            surfaceData2 = (SurfaceData)this.srcTmp.get();
        }
        convert = GraphicsPrimitive.convertFrom(fromCache, convert, n, n2, n5, n6, surfaceData2, 3);
        this.performop.Blit(convert, surfaceData, composite, region, 0, 0, n3, n4, n5, n6);
        if (convert != surfaceData2) {
            this.srcTmp = new WeakReference((T)convert);
        }
    }
}
