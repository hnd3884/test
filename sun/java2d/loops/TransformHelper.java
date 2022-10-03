package sun.java2d.loops;

import java.awt.geom.AffineTransform;
import sun.java2d.pipe.Region;
import java.awt.Composite;
import sun.java2d.SurfaceData;

public class TransformHelper extends GraphicsPrimitive
{
    public static final String methodSignature;
    public static final int primTypeID;
    private static RenderCache helpercache;
    
    public static TransformHelper locate(final SurfaceType surfaceType) {
        return (TransformHelper)GraphicsPrimitiveMgr.locate(TransformHelper.primTypeID, surfaceType, CompositeType.SrcNoEa, SurfaceType.IntArgbPre);
    }
    
    public static synchronized TransformHelper getFromCache(final SurfaceType surfaceType) {
        final Object value = TransformHelper.helpercache.get(surfaceType, null, null);
        if (value != null) {
            return (TransformHelper)value;
        }
        final TransformHelper locate = locate(surfaceType);
        if (locate != null) {
            TransformHelper.helpercache.put(surfaceType, null, null, locate);
        }
        return locate;
    }
    
    protected TransformHelper(final SurfaceType surfaceType) {
        super(TransformHelper.methodSignature, TransformHelper.primTypeID, surfaceType, CompositeType.SrcNoEa, SurfaceType.IntArgbPre);
    }
    
    public TransformHelper(final long n, final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        super(n, TransformHelper.methodSignature, TransformHelper.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    public native void Transform(final MaskBlit p0, final SurfaceData p1, final SurfaceData p2, final Composite p3, final Region p4, final AffineTransform p5, final int p6, final int p7, final int p8, final int p9, final int p10, final int p11, final int p12, final int p13, final int p14, final int[] p15, final int p16, final int p17);
    
    @Override
    public GraphicsPrimitive makePrimitive(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        return null;
    }
    
    @Override
    public GraphicsPrimitive traceWrap() {
        return new TraceTransformHelper(this);
    }
    
    static {
        methodSignature = "TransformHelper(...)".toString();
        primTypeID = GraphicsPrimitive.makePrimTypeID();
        TransformHelper.helpercache = new RenderCache(10);
    }
    
    private static class TraceTransformHelper extends TransformHelper
    {
        TransformHelper target;
        
        public TraceTransformHelper(final TransformHelper target) {
            super(target.getSourceType());
            this.target = target;
        }
        
        @Override
        public GraphicsPrimitive traceWrap() {
            return this;
        }
        
        @Override
        public void Transform(final MaskBlit maskBlit, final SurfaceData surfaceData, final SurfaceData surfaceData2, final Composite composite, final Region region, final AffineTransform affineTransform, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final int n9, final int[] array, final int n10, final int n11) {
            GraphicsPrimitive.tracePrimitive(this.target);
            this.target.Transform(maskBlit, surfaceData, surfaceData2, composite, region, affineTransform, n, n2, n3, n4, n5, n6, n7, n8, n9, array, n10, n11);
        }
    }
}
