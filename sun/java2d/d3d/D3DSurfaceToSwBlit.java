package sun.java2d.d3d;

import sun.java2d.pipe.RenderBuffer;
import sun.java2d.pipe.Region;
import java.awt.Composite;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.SurfaceType;
import sun.java2d.loops.Blit;

class D3DSurfaceToSwBlit extends Blit
{
    private int typeval;
    
    D3DSurfaceToSwBlit(final SurfaceType surfaceType, final int typeval) {
        super(D3DSurfaceData.D3DSurface, CompositeType.SrcNoEa, surfaceType);
        this.typeval = typeval;
    }
    
    @Override
    public void Blit(final SurfaceData surfaceData, final SurfaceData surfaceData2, final Composite composite, final Region region, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        final D3DRenderQueue instance = D3DRenderQueue.getInstance();
        instance.lock();
        try {
            instance.addReference(surfaceData2);
            final RenderBuffer buffer = instance.getBuffer();
            D3DContext.setScratchSurface(((D3DSurfaceData)surfaceData).getContext());
            instance.ensureCapacityAndAlignment(48, 32);
            buffer.putInt(34);
            buffer.putInt(n).putInt(n2);
            buffer.putInt(n3).putInt(n4);
            buffer.putInt(n5).putInt(n6);
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
