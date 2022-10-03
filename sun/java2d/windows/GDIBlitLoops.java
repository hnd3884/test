package sun.java2d.windows;

import java.awt.Composite;
import sun.java2d.pipe.Region;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.GraphicsPrimitiveMgr;
import sun.java2d.loops.SurfaceType;
import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.loops.Blit;

public class GDIBlitLoops extends Blit
{
    int rmask;
    int gmask;
    int bmask;
    boolean indexed;
    
    public static void register() {
        GraphicsPrimitiveMgr.register(new GraphicsPrimitive[] { new GDIBlitLoops(SurfaceType.IntRgb, GDIWindowSurfaceData.AnyGdi), new GDIBlitLoops(SurfaceType.Ushort555Rgb, GDIWindowSurfaceData.AnyGdi, 31744, 992, 31), new GDIBlitLoops(SurfaceType.Ushort565Rgb, GDIWindowSurfaceData.AnyGdi, 63488, 2016, 31), new GDIBlitLoops(SurfaceType.ThreeByteBgr, GDIWindowSurfaceData.AnyGdi), new GDIBlitLoops(SurfaceType.ByteIndexedOpaque, GDIWindowSurfaceData.AnyGdi, true), new GDIBlitLoops(SurfaceType.Index8Gray, GDIWindowSurfaceData.AnyGdi, true), new GDIBlitLoops(SurfaceType.ByteGray, GDIWindowSurfaceData.AnyGdi) });
    }
    
    public GDIBlitLoops(final SurfaceType surfaceType, final SurfaceType surfaceType2) {
        this(surfaceType, surfaceType2, 0, 0, 0);
    }
    
    public GDIBlitLoops(final SurfaceType surfaceType, final SurfaceType surfaceType2, final boolean indexed) {
        this(surfaceType, surfaceType2, 0, 0, 0);
        this.indexed = indexed;
    }
    
    public GDIBlitLoops(final SurfaceType surfaceType, final SurfaceType surfaceType2, final int rmask, final int gmask, final int bmask) {
        super(surfaceType, CompositeType.SrcNoEa, surfaceType2);
        this.indexed = false;
        this.rmask = rmask;
        this.gmask = gmask;
        this.bmask = bmask;
    }
    
    public native void nativeBlit(final SurfaceData p0, final SurfaceData p1, final Region p2, final int p3, final int p4, final int p5, final int p6, final int p7, final int p8, final int p9, final int p10, final int p11, final boolean p12);
    
    @Override
    public void Blit(final SurfaceData surfaceData, final SurfaceData surfaceData2, final Composite composite, final Region region, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.nativeBlit(surfaceData, surfaceData2, region, n, n2, n3, n4, n5, n6, this.rmask, this.gmask, this.bmask, this.indexed);
    }
}
