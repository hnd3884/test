package sun.java2d.opengl;

import sun.java2d.pipe.RenderBuffer;
import sun.java2d.pipe.hw.AccelSurface;
import sun.java2d.pipe.BufferedContext;
import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.pipe.Region;
import java.awt.Composite;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.SurfaceType;
import sun.java2d.SurfaceData;
import java.lang.ref.WeakReference;
import sun.java2d.loops.Blit;

final class OGLSurfaceToSwBlit extends Blit
{
    private final int typeval;
    private WeakReference<SurfaceData> srcTmp;
    
    OGLSurfaceToSwBlit(final SurfaceType surfaceType, final int typeval) {
        super(OGLSurfaceData.OpenGLSurface, CompositeType.SrcNoEa, surfaceType);
        this.typeval = typeval;
    }
    
    private synchronized void complexClipBlit(SurfaceData convert, final SurfaceData surfaceData, final Composite composite, final Region region, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        SurfaceData surfaceData2 = null;
        if (this.srcTmp != null) {
            surfaceData2 = this.srcTmp.get();
        }
        convert = GraphicsPrimitive.convertFrom(this, convert, n, n2, n5, n6, surfaceData2, (this.typeval == 1) ? 3 : 2);
        Blit.getFromCache(convert.getSurfaceType(), CompositeType.SrcNoEa, surfaceData.getSurfaceType()).Blit(convert, surfaceData, composite, region, 0, 0, n3, n4, n5, n6);
        if (convert != surfaceData2) {
            this.srcTmp = new WeakReference<SurfaceData>(convert);
        }
    }
    
    @Override
    public void Blit(final SurfaceData surfaceData, final SurfaceData surfaceData2, final Composite composite, Region intersectionXYWH, int n, int n2, int loX, int loY, int width, int height) {
        if (intersectionXYWH != null) {
            intersectionXYWH = intersectionXYWH.getIntersectionXYWH(loX, loY, width, height);
            if (intersectionXYWH.isEmpty()) {
                return;
            }
            n += intersectionXYWH.getLoX() - loX;
            n2 += intersectionXYWH.getLoY() - loY;
            loX = intersectionXYWH.getLoX();
            loY = intersectionXYWH.getLoY();
            width = intersectionXYWH.getWidth();
            height = intersectionXYWH.getHeight();
            if (!intersectionXYWH.isRectangular()) {
                this.complexClipBlit(surfaceData, surfaceData2, composite, intersectionXYWH, n, n2, loX, loY, width, height);
                return;
            }
        }
        final OGLRenderQueue instance = OGLRenderQueue.getInstance();
        instance.lock();
        try {
            instance.addReference(surfaceData2);
            final RenderBuffer buffer = instance.getBuffer();
            BufferedContext.validateContext((AccelSurface)surfaceData);
            instance.ensureCapacityAndAlignment(48, 32);
            buffer.putInt(34);
            buffer.putInt(n).putInt(n2);
            buffer.putInt(loX).putInt(loY);
            buffer.putInt(width).putInt(height);
            buffer.putInt(this.typeval);
            buffer.putLong(surfaceData.getNativeOps());
            buffer.putLong(surfaceData2.getNativeOps());
            instance.flushNow();
        }
        finally {
            instance.unlock();
        }
    }
}
