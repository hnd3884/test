package sun.java2d.loops;

import sun.java2d.SurfaceData;
import sun.java2d.SunGraphics2D;

public class FillRect extends GraphicsPrimitive
{
    public static final String methodSignature;
    public static final int primTypeID;
    
    public static FillRect locate(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        return (FillRect)GraphicsPrimitiveMgr.locate(FillRect.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    protected FillRect(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        super(FillRect.methodSignature, FillRect.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    public FillRect(final long n, final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        super(n, FillRect.methodSignature, FillRect.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    public native void FillRect(final SunGraphics2D p0, final SurfaceData p1, final int p2, final int p3, final int p4, final int p5);
    
    @Override
    public GraphicsPrimitive makePrimitive(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        return new General(surfaceType, compositeType, surfaceType2);
    }
    
    @Override
    public GraphicsPrimitive traceWrap() {
        return new TraceFillRect(this);
    }
    
    static {
        methodSignature = "FillRect(...)".toString();
        primTypeID = GraphicsPrimitive.makePrimTypeID();
        GraphicsPrimitiveMgr.registerGeneral(new FillRect(null, null, null));
    }
    
    public static class General extends FillRect
    {
        public MaskFill fillop;
        
        public General(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
            super(surfaceType, compositeType, surfaceType2);
            this.fillop = MaskFill.locate(surfaceType, compositeType, surfaceType2);
        }
        
        @Override
        public void FillRect(final SunGraphics2D sunGraphics2D, final SurfaceData surfaceData, final int n, final int n2, final int n3, final int n4) {
            this.fillop.MaskFill(sunGraphics2D, surfaceData, sunGraphics2D.composite, n, n2, n3, n4, null, 0, 0);
        }
    }
    
    private static class TraceFillRect extends FillRect
    {
        FillRect target;
        
        public TraceFillRect(final FillRect target) {
            super(target.getSourceType(), target.getCompositeType(), target.getDestType());
            this.target = target;
        }
        
        @Override
        public GraphicsPrimitive traceWrap() {
            return this;
        }
        
        @Override
        public void FillRect(final SunGraphics2D sunGraphics2D, final SurfaceData surfaceData, final int n, final int n2, final int n3, final int n4) {
            GraphicsPrimitive.tracePrimitive(this.target);
            this.target.FillRect(sunGraphics2D, surfaceData, n, n2, n3, n4);
        }
    }
}
