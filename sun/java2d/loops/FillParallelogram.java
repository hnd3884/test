package sun.java2d.loops;

import sun.java2d.SurfaceData;
import sun.java2d.SunGraphics2D;

public class FillParallelogram extends GraphicsPrimitive
{
    public static final String methodSignature;
    public static final int primTypeID;
    
    public static FillParallelogram locate(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        return (FillParallelogram)GraphicsPrimitiveMgr.locate(FillParallelogram.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    protected FillParallelogram(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        super(FillParallelogram.methodSignature, FillParallelogram.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    public FillParallelogram(final long n, final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        super(n, FillParallelogram.methodSignature, FillParallelogram.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    public native void FillParallelogram(final SunGraphics2D p0, final SurfaceData p1, final double p2, final double p3, final double p4, final double p5, final double p6, final double p7);
    
    @Override
    public GraphicsPrimitive makePrimitive(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        throw new InternalError("FillParallelogram not implemented for " + surfaceType + " with " + compositeType);
    }
    
    @Override
    public GraphicsPrimitive traceWrap() {
        return new TraceFillParallelogram(this);
    }
    
    static {
        methodSignature = "FillParallelogram(...)".toString();
        primTypeID = GraphicsPrimitive.makePrimTypeID();
    }
    
    private static class TraceFillParallelogram extends FillParallelogram
    {
        FillParallelogram target;
        
        public TraceFillParallelogram(final FillParallelogram target) {
            super(target.getSourceType(), target.getCompositeType(), target.getDestType());
            this.target = target;
        }
        
        @Override
        public GraphicsPrimitive traceWrap() {
            return this;
        }
        
        @Override
        public void FillParallelogram(final SunGraphics2D sunGraphics2D, final SurfaceData surfaceData, final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
            GraphicsPrimitive.tracePrimitive(this.target);
            this.target.FillParallelogram(sunGraphics2D, surfaceData, n, n2, n3, n4, n5, n6);
        }
    }
}
