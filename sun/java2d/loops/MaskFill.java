package sun.java2d.loops;

import sun.java2d.pipe.Region;
import sun.awt.image.BufImgSurfaceData;
import java.awt.image.BufferedImage;
import java.awt.Composite;
import sun.java2d.SurfaceData;
import sun.java2d.SunGraphics2D;

public class MaskFill extends GraphicsPrimitive
{
    public static final String methodSignature;
    public static final String fillPgramSignature;
    public static final String drawPgramSignature;
    public static final int primTypeID;
    private static RenderCache fillcache;
    
    public static MaskFill locate(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        return (MaskFill)GraphicsPrimitiveMgr.locate(MaskFill.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    public static MaskFill locatePrim(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        return (MaskFill)GraphicsPrimitiveMgr.locatePrim(MaskFill.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    public static MaskFill getFromCache(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        final Object value = MaskFill.fillcache.get(surfaceType, compositeType, surfaceType2);
        if (value != null) {
            return (MaskFill)value;
        }
        final MaskFill locatePrim = locatePrim(surfaceType, compositeType, surfaceType2);
        if (locatePrim != null) {
            MaskFill.fillcache.put(surfaceType, compositeType, surfaceType2, locatePrim);
        }
        return locatePrim;
    }
    
    protected MaskFill(final String s, final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        super(s, MaskFill.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    protected MaskFill(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        super(MaskFill.methodSignature, MaskFill.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    public MaskFill(final long n, final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        super(n, MaskFill.methodSignature, MaskFill.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    public native void MaskFill(final SunGraphics2D p0, final SurfaceData p1, final Composite p2, final int p3, final int p4, final int p5, final int p6, final byte[] p7, final int p8, final int p9);
    
    public native void FillAAPgram(final SunGraphics2D p0, final SurfaceData p1, final Composite p2, final double p3, final double p4, final double p5, final double p6, final double p7, final double p8);
    
    public native void DrawAAPgram(final SunGraphics2D p0, final SurfaceData p1, final Composite p2, final double p3, final double p4, final double p5, final double p6, final double p7, final double p8, final double p9, final double p10);
    
    public boolean canDoParallelograms() {
        return this.getNativePrim() != 0L;
    }
    
    @Override
    public GraphicsPrimitive makePrimitive(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        if (!SurfaceType.OpaqueColor.equals(surfaceType) && !SurfaceType.AnyColor.equals(surfaceType)) {
            throw new InternalError("MaskFill can only fill with colors");
        }
        if (CompositeType.Xor.equals(compositeType)) {
            throw new InternalError("Cannot construct MaskFill for XOR mode");
        }
        return new General(surfaceType, compositeType, surfaceType2);
    }
    
    @Override
    public GraphicsPrimitive traceWrap() {
        return new TraceMaskFill(this);
    }
    
    static {
        methodSignature = "MaskFill(...)".toString();
        fillPgramSignature = "FillAAPgram(...)".toString();
        drawPgramSignature = "DrawAAPgram(...)".toString();
        primTypeID = GraphicsPrimitive.makePrimTypeID();
        MaskFill.fillcache = new RenderCache(10);
        GraphicsPrimitiveMgr.registerGeneral(new MaskFill(null, null, null));
    }
    
    private static class General extends MaskFill
    {
        FillRect fillop;
        MaskBlit maskop;
        
        public General(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
            super(surfaceType, compositeType, surfaceType2);
            this.fillop = FillRect.locate(surfaceType, CompositeType.SrcNoEa, SurfaceType.IntArgb);
            this.maskop = MaskBlit.locate(SurfaceType.IntArgb, compositeType, surfaceType2);
        }
        
        @Override
        public void MaskFill(final SunGraphics2D sunGraphics2D, final SurfaceData surfaceData, final Composite composite, final int n, final int n2, final int n3, final int n4, final byte[] array, final int n5, final int n6) {
            final SurfaceData data = BufImgSurfaceData.createData(new BufferedImage(n3, n4, 2));
            final Region clipRegion = sunGraphics2D.clipRegion;
            sunGraphics2D.clipRegion = null;
            final int pixel = sunGraphics2D.pixel;
            sunGraphics2D.pixel = data.pixelFor(sunGraphics2D.getColor());
            this.fillop.FillRect(sunGraphics2D, data, 0, 0, n3, n4);
            sunGraphics2D.pixel = pixel;
            sunGraphics2D.clipRegion = clipRegion;
            this.maskop.MaskBlit(data, surfaceData, composite, null, 0, 0, n, n2, n3, n4, array, n5, n6);
        }
    }
    
    private static class TraceMaskFill extends MaskFill
    {
        MaskFill target;
        MaskFill fillPgramTarget;
        MaskFill drawPgramTarget;
        
        public TraceMaskFill(final MaskFill target) {
            super(target.getSourceType(), target.getCompositeType(), target.getDestType());
            this.target = target;
            this.fillPgramTarget = new MaskFill(TraceMaskFill.fillPgramSignature, target.getSourceType(), target.getCompositeType(), target.getDestType());
            this.drawPgramTarget = new MaskFill(TraceMaskFill.drawPgramSignature, target.getSourceType(), target.getCompositeType(), target.getDestType());
        }
        
        @Override
        public GraphicsPrimitive traceWrap() {
            return this;
        }
        
        @Override
        public void MaskFill(final SunGraphics2D sunGraphics2D, final SurfaceData surfaceData, final Composite composite, final int n, final int n2, final int n3, final int n4, final byte[] array, final int n5, final int n6) {
            GraphicsPrimitive.tracePrimitive(this.target);
            this.target.MaskFill(sunGraphics2D, surfaceData, composite, n, n2, n3, n4, array, n5, n6);
        }
        
        @Override
        public void FillAAPgram(final SunGraphics2D sunGraphics2D, final SurfaceData surfaceData, final Composite composite, final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
            GraphicsPrimitive.tracePrimitive(this.fillPgramTarget);
            this.target.FillAAPgram(sunGraphics2D, surfaceData, composite, n, n2, n3, n4, n5, n6);
        }
        
        @Override
        public void DrawAAPgram(final SunGraphics2D sunGraphics2D, final SurfaceData surfaceData, final Composite composite, final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8) {
            GraphicsPrimitive.tracePrimitive(this.drawPgramTarget);
            this.target.DrawAAPgram(sunGraphics2D, surfaceData, composite, n, n2, n3, n4, n5, n6, n7, n8);
        }
        
        @Override
        public boolean canDoParallelograms() {
            return this.target.canDoParallelograms();
        }
    }
}
