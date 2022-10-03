package sun.java2d.loops;

import java.awt.AlphaComposite;
import sun.java2d.SunGraphics2D;
import java.awt.Color;
import sun.awt.image.BufImgSurfaceData;
import java.util.Hashtable;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.Font;
import sun.java2d.pipe.Region;
import java.awt.Composite;
import sun.java2d.SurfaceData;

public class BlitBg extends GraphicsPrimitive
{
    public static final String methodSignature;
    public static final int primTypeID;
    private static RenderCache blitcache;
    
    public static BlitBg locate(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        return (BlitBg)GraphicsPrimitiveMgr.locate(BlitBg.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    public static BlitBg getFromCache(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        final Object value = BlitBg.blitcache.get(surfaceType, compositeType, surfaceType2);
        if (value != null) {
            return (BlitBg)value;
        }
        final BlitBg locate = locate(surfaceType, compositeType, surfaceType2);
        if (locate == null) {
            System.out.println("blitbg loop not found for:");
            System.out.println("src:  " + surfaceType);
            System.out.println("comp: " + compositeType);
            System.out.println("dst:  " + surfaceType2);
        }
        else {
            BlitBg.blitcache.put(surfaceType, compositeType, surfaceType2, locate);
        }
        return locate;
    }
    
    protected BlitBg(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        super(BlitBg.methodSignature, BlitBg.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    public BlitBg(final long n, final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        super(n, BlitBg.methodSignature, BlitBg.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    public native void BlitBg(final SurfaceData p0, final SurfaceData p1, final Composite p2, final Region p3, final int p4, final int p5, final int p6, final int p7, final int p8, final int p9, final int p10);
    
    @Override
    public GraphicsPrimitive makePrimitive(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        return new General(surfaceType, compositeType, surfaceType2);
    }
    
    @Override
    public GraphicsPrimitive traceWrap() {
        return new TraceBlitBg(this);
    }
    
    static {
        methodSignature = "BlitBg(...)".toString();
        primTypeID = GraphicsPrimitive.makePrimTypeID();
        BlitBg.blitcache = new RenderCache(20);
        GraphicsPrimitiveMgr.registerGeneral(new BlitBg(null, null, null));
    }
    
    private static class General extends BlitBg
    {
        CompositeType compositeType;
        private static Font defaultFont;
        
        public General(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
            super(surfaceType, compositeType, surfaceType2);
            this.compositeType = compositeType;
        }
        
        @Override
        public void BlitBg(final SurfaceData surfaceData, final SurfaceData surfaceData2, final Composite composite, final Region region, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7) {
            ColorModel colorModel = surfaceData2.getColorModel();
            final boolean b = n >>> 24 != 255;
            if (!colorModel.hasAlpha() && b) {
                colorModel = ColorModel.getRGBdefault();
            }
            final SurfaceData data = BufImgSurfaceData.createData(new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(n6, n7), colorModel.isAlphaPremultiplied(), null));
            final Color color = new Color(n, b);
            final SunGraphics2D sunGraphics2D = new SunGraphics2D(data, color, color, General.defaultFont);
            final FillRect locate = FillRect.locate(SurfaceType.AnyColor, CompositeType.SrcNoEa, data.getSurfaceType());
            final Blit fromCache = Blit.getFromCache(surfaceData.getSurfaceType(), CompositeType.SrcOverNoEa, data.getSurfaceType());
            final Blit fromCache2 = Blit.getFromCache(data.getSurfaceType(), this.compositeType, surfaceData2.getSurfaceType());
            locate.FillRect(sunGraphics2D, data, 0, 0, n6, n7);
            fromCache.Blit(surfaceData, data, AlphaComposite.SrcOver, null, n2, n3, 0, 0, n6, n7);
            fromCache2.Blit(data, surfaceData2, composite, region, 0, 0, n4, n5, n6, n7);
        }
        
        static {
            General.defaultFont = new Font("Dialog", 0, 12);
        }
    }
    
    private static class TraceBlitBg extends BlitBg
    {
        BlitBg target;
        
        public TraceBlitBg(final BlitBg target) {
            super(target.getSourceType(), target.getCompositeType(), target.getDestType());
            this.target = target;
        }
        
        @Override
        public GraphicsPrimitive traceWrap() {
            return this;
        }
        
        @Override
        public void BlitBg(final SurfaceData surfaceData, final SurfaceData surfaceData2, final Composite composite, final Region region, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7) {
            GraphicsPrimitive.tracePrimitive(this.target);
            this.target.BlitBg(surfaceData, surfaceData2, composite, region, n, n2, n3, n4, n5, n6, n7);
        }
    }
}
