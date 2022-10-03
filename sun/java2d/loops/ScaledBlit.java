package sun.java2d.loops;

import sun.java2d.pipe.Region;
import java.awt.Composite;
import sun.java2d.SurfaceData;

public class ScaledBlit extends GraphicsPrimitive
{
    public static final String methodSignature;
    public static final int primTypeID;
    private static RenderCache blitcache;
    
    public static ScaledBlit locate(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        return (ScaledBlit)GraphicsPrimitiveMgr.locate(ScaledBlit.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    public static ScaledBlit getFromCache(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        final Object value = ScaledBlit.blitcache.get(surfaceType, compositeType, surfaceType2);
        if (value != null) {
            return (ScaledBlit)value;
        }
        final ScaledBlit locate = locate(surfaceType, compositeType, surfaceType2);
        if (locate != null) {
            ScaledBlit.blitcache.put(surfaceType, compositeType, surfaceType2, locate);
        }
        return locate;
    }
    
    protected ScaledBlit(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        super(ScaledBlit.methodSignature, ScaledBlit.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    public ScaledBlit(final long n, final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        super(n, ScaledBlit.methodSignature, ScaledBlit.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    public native void Scale(final SurfaceData p0, final SurfaceData p1, final Composite p2, final Region p3, final int p4, final int p5, final int p6, final int p7, final double p8, final double p9, final double p10, final double p11);
    
    @Override
    public GraphicsPrimitive makePrimitive(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        return null;
    }
    
    @Override
    public GraphicsPrimitive traceWrap() {
        return new TraceScaledBlit(this);
    }
    
    static {
        methodSignature = "ScaledBlit(...)".toString();
        primTypeID = GraphicsPrimitive.makePrimTypeID();
        ScaledBlit.blitcache = new RenderCache(20);
        GraphicsPrimitiveMgr.registerGeneral(new ScaledBlit(null, null, null));
    }
    
    private static class TraceScaledBlit extends ScaledBlit
    {
        ScaledBlit target;
        
        public TraceScaledBlit(final ScaledBlit target) {
            super(target.getSourceType(), target.getCompositeType(), target.getDestType());
            this.target = target;
        }
        
        @Override
        public GraphicsPrimitive traceWrap() {
            return this;
        }
        
        @Override
        public void Scale(final SurfaceData surfaceData, final SurfaceData surfaceData2, final Composite composite, final Region region, final int n, final int n2, final int n3, final int n4, final double n5, final double n6, final double n7, final double n8) {
            GraphicsPrimitive.tracePrimitive(this.target);
            this.target.Scale(surfaceData, surfaceData2, composite, region, n, n2, n3, n4, n5, n6, n7, n8);
        }
    }
}
