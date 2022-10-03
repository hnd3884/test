package sun.java2d.opengl;

import java.awt.AlphaComposite;
import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.pipe.Region;
import java.awt.Composite;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.SurfaceType;
import sun.java2d.SurfaceData;
import java.lang.ref.WeakReference;
import sun.java2d.loops.Blit;

final class OGLAnyCompositeBlit extends Blit
{
    private WeakReference<SurfaceData> dstTmp;
    private WeakReference<SurfaceData> srcTmp;
    private final Blit convertsrc;
    private final Blit convertdst;
    private final Blit convertresult;
    
    OGLAnyCompositeBlit(final SurfaceType surfaceType, final Blit convertsrc, final Blit convertdst, final Blit convertresult) {
        super(surfaceType, CompositeType.Any, OGLSurfaceData.OpenGLSurface);
        this.convertsrc = convertsrc;
        this.convertdst = convertdst;
        this.convertresult = convertresult;
    }
    
    @Override
    public synchronized void Blit(SurfaceData convert, final SurfaceData surfaceData, final Composite composite, final Region region, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        if (this.convertsrc != null) {
            SurfaceData surfaceData2 = null;
            if (this.srcTmp != null) {
                surfaceData2 = this.srcTmp.get();
            }
            convert = GraphicsPrimitive.convertFrom(this.convertsrc, convert, n, n2, n5, n6, surfaceData2, 3);
            if (convert != surfaceData2) {
                this.srcTmp = new WeakReference<SurfaceData>(convert);
            }
        }
        SurfaceData surfaceData3 = null;
        if (this.dstTmp != null) {
            surfaceData3 = this.dstTmp.get();
        }
        final SurfaceData convert2 = GraphicsPrimitive.convertFrom(this.convertdst, surfaceData, n3, n4, n5, n6, surfaceData3, 3);
        Blit.getFromCache(convert.getSurfaceType(), CompositeType.Any, convert2.getSurfaceType()).Blit(convert, convert2, composite, (region == null) ? null : region.getTranslatedRegion(-n3, -n4), n, n2, 0, 0, n5, n6);
        if (convert2 != surfaceData3) {
            this.dstTmp = new WeakReference<SurfaceData>(convert2);
        }
        this.convertresult.Blit(convert2, surfaceData, AlphaComposite.Src, region, 0, 0, n3, n4, n5, n6);
    }
}
