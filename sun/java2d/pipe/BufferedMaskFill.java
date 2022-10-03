package sun.java2d.pipe;

import java.awt.AlphaComposite;
import java.awt.Composite;
import sun.java2d.SurfaceData;
import sun.java2d.SunGraphics2D;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.SurfaceType;
import sun.java2d.loops.MaskFill;

public abstract class BufferedMaskFill extends MaskFill
{
    protected final RenderQueue rq;
    
    protected BufferedMaskFill(final RenderQueue rq, final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        super(surfaceType, compositeType, surfaceType2);
        this.rq = rq;
    }
    
    @Override
    public void MaskFill(final SunGraphics2D sunGraphics2D, final SurfaceData surfaceData, Composite srcOver, final int n, final int n2, final int n3, final int n4, final byte[] array, final int n5, final int n6) {
        if (((AlphaComposite)srcOver).getRule() != 3) {
            srcOver = AlphaComposite.SrcOver;
        }
        this.rq.lock();
        try {
            this.validateContext(sunGraphics2D, srcOver, 2);
            int n7;
            if (array != null) {
                n7 = (array.length + 3 & 0xFFFFFFFC);
            }
            else {
                n7 = 0;
            }
            final int n8 = 32 + n7;
            final RenderBuffer buffer = this.rq.getBuffer();
            if (n8 <= buffer.capacity()) {
                if (n8 > buffer.remaining()) {
                    this.rq.flushNow();
                }
                buffer.putInt(32);
                buffer.putInt(n).putInt(n2).putInt(n3).putInt(n4);
                buffer.putInt(n5);
                buffer.putInt(n6);
                buffer.putInt(n7);
                if (array != null) {
                    final int n9 = n7 - array.length;
                    buffer.put(array);
                    if (n9 != 0) {
                        buffer.position(buffer.position() + n9);
                    }
                }
            }
            else {
                this.rq.flushAndInvokeNow(new Runnable() {
                    @Override
                    public void run() {
                        BufferedMaskFill.this.maskFill(n, n2, n3, n4, n5, n6, array.length, array);
                    }
                });
            }
        }
        finally {
            this.rq.unlock();
        }
    }
    
    protected abstract void maskFill(final int p0, final int p1, final int p2, final int p3, final int p4, final int p5, final int p6, final byte[] p7);
    
    protected abstract void validateContext(final SunGraphics2D p0, final Composite p1, final int p2);
}
