package sun.java2d.pipe;

import java.awt.AlphaComposite;
import java.awt.Composite;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.SurfaceType;
import sun.java2d.loops.Blit;
import sun.java2d.loops.MaskBlit;

public abstract class BufferedMaskBlit extends MaskBlit
{
    private static final int ST_INT_ARGB = 0;
    private static final int ST_INT_ARGB_PRE = 1;
    private static final int ST_INT_RGB = 2;
    private static final int ST_INT_BGR = 3;
    private final RenderQueue rq;
    private final int srcTypeVal;
    private Blit blitop;
    
    protected BufferedMaskBlit(final RenderQueue rq, final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        super(surfaceType, compositeType, surfaceType2);
        this.rq = rq;
        if (surfaceType == SurfaceType.IntArgb) {
            this.srcTypeVal = 0;
        }
        else if (surfaceType == SurfaceType.IntArgbPre) {
            this.srcTypeVal = 1;
        }
        else if (surfaceType == SurfaceType.IntRgb) {
            this.srcTypeVal = 2;
        }
        else {
            if (surfaceType != SurfaceType.IntBgr) {
                throw new InternalError("unrecognized source surface type");
            }
            this.srcTypeVal = 3;
        }
    }
    
    @Override
    public void MaskBlit(final SurfaceData surfaceData, final SurfaceData surfaceData2, Composite srcOver, final Region region, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final byte[] array, final int n7, final int n8) {
        if (n5 <= 0 || n6 <= 0) {
            return;
        }
        if (array == null) {
            if (this.blitop == null) {
                this.blitop = Blit.getFromCache(surfaceData.getSurfaceType(), CompositeType.AnyAlpha, this.getDestType());
            }
            this.blitop.Blit(surfaceData, surfaceData2, srcOver, region, n, n2, n3, n4, n5, n6);
            return;
        }
        if (((AlphaComposite)srcOver).getRule() != 3) {
            srcOver = AlphaComposite.SrcOver;
        }
        this.rq.lock();
        try {
            this.validateContext(surfaceData2, srcOver, region);
            final RenderBuffer buffer = this.rq.getBuffer();
            this.rq.ensureCapacity(20 + n5 * n6 * 4);
            buffer.position(this.enqueueTile(buffer.getAddress(), buffer.position(), surfaceData, surfaceData.getNativeOps(), this.srcTypeVal, array, array.length, n7, n8, n, n2, n3, n4, n5, n6));
        }
        finally {
            this.rq.unlock();
        }
    }
    
    private native int enqueueTile(final long p0, final int p1, final SurfaceData p2, final long p3, final int p4, final byte[] p5, final int p6, final int p7, final int p8, final int p9, final int p10, final int p11, final int p12, final int p13, final int p14);
    
    protected abstract void validateContext(final SurfaceData p0, final Composite p1, final Region p2);
}
