package sun.java2d.loops;

import sun.java2d.pipe.Region;
import sun.font.GlyphList;
import sun.java2d.SurfaceData;
import sun.java2d.SunGraphics2D;

public class DrawGlyphListAA extends GraphicsPrimitive
{
    public static final String methodSignature;
    public static final int primTypeID;
    
    public static DrawGlyphListAA locate(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        return (DrawGlyphListAA)GraphicsPrimitiveMgr.locate(DrawGlyphListAA.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    protected DrawGlyphListAA(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        super(DrawGlyphListAA.methodSignature, DrawGlyphListAA.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    public DrawGlyphListAA(final long n, final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        super(n, DrawGlyphListAA.methodSignature, DrawGlyphListAA.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    public native void DrawGlyphListAA(final SunGraphics2D p0, final SurfaceData p1, final GlyphList p2);
    
    @Override
    public GraphicsPrimitive makePrimitive(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        return new General(surfaceType, compositeType, surfaceType2);
    }
    
    @Override
    public GraphicsPrimitive traceWrap() {
        return new TraceDrawGlyphListAA(this);
    }
    
    static {
        methodSignature = "DrawGlyphListAA(...)".toString();
        primTypeID = GraphicsPrimitive.makePrimTypeID();
        GraphicsPrimitiveMgr.registerGeneral(new DrawGlyphListAA(null, null, null));
    }
    
    public static class General extends DrawGlyphListAA
    {
        MaskFill maskop;
        
        public General(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
            super(surfaceType, compositeType, surfaceType2);
            this.maskop = MaskFill.locate(surfaceType, compositeType, surfaceType2);
        }
        
        @Override
        public void DrawGlyphListAA(final SunGraphics2D sunGraphics2D, final SurfaceData surfaceData, final GlyphList list) {
            list.getBounds();
            final int numGlyphs = list.getNumGlyphs();
            final Region compClip = sunGraphics2D.getCompClip();
            final int loX = compClip.getLoX();
            final int loY = compClip.getLoY();
            final int hiX = compClip.getHiX();
            final int hiY = compClip.getHiY();
            for (int i = 0; i < numGlyphs; ++i) {
                list.setGlyphIndex(i);
                final int[] metrics = list.getMetrics();
                int n = metrics[0];
                int n2 = metrics[1];
                final int n3 = metrics[2];
                int n4 = n + n3;
                int n5 = n2 + metrics[3];
                int n6 = 0;
                if (n < loX) {
                    n6 = loX - n;
                    n = loX;
                }
                if (n2 < loY) {
                    n6 += (loY - n2) * n3;
                    n2 = loY;
                }
                if (n4 > hiX) {
                    n4 = hiX;
                }
                if (n5 > hiY) {
                    n5 = hiY;
                }
                if (n4 > n && n5 > n2) {
                    this.maskop.MaskFill(sunGraphics2D, surfaceData, sunGraphics2D.composite, n, n2, n4 - n, n5 - n2, list.getGrayBits(), n6, n3);
                }
            }
        }
    }
    
    private static class TraceDrawGlyphListAA extends DrawGlyphListAA
    {
        DrawGlyphListAA target;
        
        public TraceDrawGlyphListAA(final DrawGlyphListAA target) {
            super(target.getSourceType(), target.getCompositeType(), target.getDestType());
            this.target = target;
        }
        
        @Override
        public GraphicsPrimitive traceWrap() {
            return this;
        }
        
        @Override
        public void DrawGlyphListAA(final SunGraphics2D sunGraphics2D, final SurfaceData surfaceData, final GlyphList list) {
            GraphicsPrimitive.tracePrimitive(this.target);
            this.target.DrawGlyphListAA(sunGraphics2D, surfaceData, list);
        }
    }
}
