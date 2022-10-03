package sun.java2d.loops;

import java.awt.geom.AffineTransform;
import sun.java2d.pipe.Region;
import java.awt.Composite;
import sun.java2d.SurfaceData;

public class TransformBlit extends GraphicsPrimitive
{
    public static final String methodSignature;
    public static final int primTypeID;
    private static RenderCache blitcache;
    
    public static TransformBlit locate(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        return (TransformBlit)GraphicsPrimitiveMgr.locate(TransformBlit.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    public static TransformBlit getFromCache(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        final Object value = TransformBlit.blitcache.get(surfaceType, compositeType, surfaceType2);
        if (value != null) {
            return (TransformBlit)value;
        }
        final TransformBlit locate = locate(surfaceType, compositeType, surfaceType2);
        if (locate != null) {
            TransformBlit.blitcache.put(surfaceType, compositeType, surfaceType2, locate);
        }
        return locate;
    }
    
    protected TransformBlit(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        super(TransformBlit.methodSignature, TransformBlit.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    public TransformBlit(final long n, final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        super(n, TransformBlit.methodSignature, TransformBlit.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    public native void Transform(final SurfaceData p0, final SurfaceData p1, final Composite p2, final Region p3, final AffineTransform p4, final int p5, final int p6, final int p7, final int p8, final int p9, final int p10, final int p11);
    
    @Override
    public GraphicsPrimitive makePrimitive(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        return null;
    }
    
    @Override
    public GraphicsPrimitive traceWrap() {
        return new TraceTransformBlit(this);
    }
    
    static {
        methodSignature = "TransformBlit(...)".toString();
        primTypeID = GraphicsPrimitive.makePrimTypeID();
        TransformBlit.blitcache = new RenderCache(10);
        GraphicsPrimitiveMgr.registerGeneral(new TransformBlit(null, null, null));
    }
    
    private static class TraceTransformBlit extends TransformBlit
    {
        TransformBlit target;
        
        public TraceTransformBlit(final TransformBlit target) {
            super(target.getSourceType(), target.getCompositeType(), target.getDestType());
            this.target = target;
        }
        
        @Override
        public GraphicsPrimitive traceWrap() {
            return this;
        }
        
        @Override
        public void Transform(final SurfaceData surfaceData, final SurfaceData surfaceData2, final Composite composite, final Region region, final AffineTransform affineTransform, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7) {
            GraphicsPrimitive.tracePrimitive(this.target);
            this.target.Transform(surfaceData, surfaceData2, composite, region, affineTransform, n, n2, n3, n4, n5, n6, n7);
        }
    }
}
