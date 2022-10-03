package sun.java2d.loops;

import sun.java2d.pipe.SpanIterator;
import sun.java2d.SurfaceData;
import sun.java2d.SunGraphics2D;

public class FillSpans extends GraphicsPrimitive
{
    public static final String methodSignature;
    public static final int primTypeID;
    
    public static FillSpans locate(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        return (FillSpans)GraphicsPrimitiveMgr.locate(FillSpans.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    protected FillSpans(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        super(FillSpans.methodSignature, FillSpans.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    public FillSpans(final long n, final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        super(n, FillSpans.methodSignature, FillSpans.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    private native void FillSpans(final SunGraphics2D p0, final SurfaceData p1, final int p2, final long p3, final SpanIterator p4);
    
    public void FillSpans(final SunGraphics2D sunGraphics2D, final SurfaceData surfaceData, final SpanIterator spanIterator) {
        this.FillSpans(sunGraphics2D, surfaceData, sunGraphics2D.pixel, spanIterator.getNativeIterator(), spanIterator);
    }
    
    @Override
    public GraphicsPrimitive makePrimitive(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        throw new InternalError("FillSpans not implemented for " + surfaceType + " with " + compositeType);
    }
    
    @Override
    public GraphicsPrimitive traceWrap() {
        return new TraceFillSpans(this);
    }
    
    static {
        methodSignature = "FillSpans(...)".toString();
        primTypeID = GraphicsPrimitive.makePrimTypeID();
    }
    
    private static class TraceFillSpans extends FillSpans
    {
        FillSpans target;
        
        public TraceFillSpans(final FillSpans target) {
            super(target.getSourceType(), target.getCompositeType(), target.getDestType());
            this.target = target;
        }
        
        @Override
        public GraphicsPrimitive traceWrap() {
            return this;
        }
        
        @Override
        public void FillSpans(final SunGraphics2D sunGraphics2D, final SurfaceData surfaceData, final SpanIterator spanIterator) {
            GraphicsPrimitive.tracePrimitive(this.target);
            this.target.FillSpans(sunGraphics2D, surfaceData, spanIterator);
        }
    }
}
