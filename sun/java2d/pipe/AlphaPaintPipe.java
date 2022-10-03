package sun.java2d.pipe;

import java.awt.image.ColorModel;
import java.awt.PaintContext;
import sun.java2d.loops.MaskBlit;
import sun.java2d.loops.Blit;
import sun.java2d.loops.CompositeType;
import sun.awt.image.BufImgSurfaceData;
import java.util.Hashtable;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.image.Raster;
import sun.java2d.SurfaceData;
import java.awt.Rectangle;
import java.awt.Shape;
import sun.java2d.SunGraphics2D;
import java.lang.ref.WeakReference;

public class AlphaPaintPipe implements CompositePipe
{
    static WeakReference cachedLastRaster;
    static WeakReference cachedLastColorModel;
    static WeakReference cachedLastData;
    private static final int TILE_SIZE = 32;
    
    @Override
    public Object startSequence(final SunGraphics2D sunGraphics2D, final Shape shape, final Rectangle rectangle, final int[] array) {
        return new TileContext(sunGraphics2D, sunGraphics2D.paint.createContext(sunGraphics2D.getDeviceColorModel(), rectangle, shape.getBounds2D(), sunGraphics2D.cloneTransform(), sunGraphics2D.getRenderingHints()));
    }
    
    @Override
    public boolean needTile(final Object o, final int n, final int n2, final int n3, final int n4) {
        return true;
    }
    
    @Override
    public void renderPathTile(final Object o, final byte[] array, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        final TileContext tileContext = (TileContext)o;
        final PaintContext paintCtxt = tileContext.paintCtxt;
        final SunGraphics2D sunG2D = tileContext.sunG2D;
        final SurfaceData dstData = tileContext.dstData;
        SurfaceData data = null;
        Raster raster = null;
        if (tileContext.lastData != null && tileContext.lastRaster != null) {
            data = (SurfaceData)tileContext.lastData.get();
            raster = (Raster)tileContext.lastRaster.get();
            if (data == null || raster == null) {
                data = null;
                raster = null;
            }
        }
        final ColorModel paintModel = tileContext.paintModel;
        for (int i = 0; i < n6; i += 32) {
            final int n7 = n4 + i;
            final int min = Math.min(n6 - i, 32);
            for (int j = 0; j < n5; j += 32) {
                final int n8 = n3 + j;
                final int min2 = Math.min(n5 - j, 32);
                Raster raster2 = paintCtxt.getRaster(n8, n7, min2, min);
                if (raster2.getMinX() != 0 || raster2.getMinY() != 0) {
                    raster2 = raster2.createTranslatedChild(0, 0);
                }
                if (raster != raster2) {
                    raster = raster2;
                    tileContext.lastRaster = new WeakReference((T)raster);
                    data = BufImgSurfaceData.createData(new BufferedImage(paintModel, (WritableRaster)raster2, paintModel.isAlphaPremultiplied(), null));
                    tileContext.lastData = new WeakReference(data);
                    tileContext.lastMask = null;
                    tileContext.lastBlit = null;
                }
                if (array == null) {
                    if (tileContext.lastBlit == null) {
                        CompositeType compositeType = sunG2D.imageComp;
                        if (CompositeType.SrcOverNoEa.equals(compositeType) && paintModel.getTransparency() == 1) {
                            compositeType = CompositeType.SrcNoEa;
                        }
                        tileContext.lastBlit = Blit.getFromCache(data.getSurfaceType(), compositeType, dstData.getSurfaceType());
                    }
                    tileContext.lastBlit.Blit(data, dstData, sunG2D.composite, null, 0, 0, n8, n7, min2, min);
                }
                else {
                    if (tileContext.lastMask == null) {
                        CompositeType compositeType2 = sunG2D.imageComp;
                        if (CompositeType.SrcOverNoEa.equals(compositeType2) && paintModel.getTransparency() == 1) {
                            compositeType2 = CompositeType.SrcNoEa;
                        }
                        tileContext.lastMask = MaskBlit.getFromCache(data.getSurfaceType(), compositeType2, dstData.getSurfaceType());
                    }
                    tileContext.lastMask.MaskBlit(data, dstData, sunG2D.composite, null, 0, 0, n8, n7, min2, min, array, n + i * n2 + j, n2);
                }
            }
        }
    }
    
    @Override
    public void skipTile(final Object o, final int n, final int n2) {
    }
    
    @Override
    public void endSequence(final Object o) {
        final TileContext tileContext = (TileContext)o;
        if (tileContext.paintCtxt != null) {
            tileContext.paintCtxt.dispose();
        }
        synchronized (AlphaPaintPipe.class) {
            if (tileContext.lastData != null) {
                AlphaPaintPipe.cachedLastRaster = tileContext.lastRaster;
                if (AlphaPaintPipe.cachedLastColorModel == null || AlphaPaintPipe.cachedLastColorModel.get() != tileContext.paintModel) {
                    AlphaPaintPipe.cachedLastColorModel = new WeakReference(tileContext.paintModel);
                }
                AlphaPaintPipe.cachedLastData = tileContext.lastData;
            }
        }
    }
    
    static class TileContext
    {
        SunGraphics2D sunG2D;
        PaintContext paintCtxt;
        ColorModel paintModel;
        WeakReference lastRaster;
        WeakReference lastData;
        MaskBlit lastMask;
        Blit lastBlit;
        SurfaceData dstData;
        
        public TileContext(final SunGraphics2D sunG2D, final PaintContext paintCtxt) {
            this.sunG2D = sunG2D;
            this.paintCtxt = paintCtxt;
            this.paintModel = paintCtxt.getColorModel();
            this.dstData = sunG2D.getSurfaceData();
            synchronized (AlphaPaintPipe.class) {
                if (AlphaPaintPipe.cachedLastColorModel != null && AlphaPaintPipe.cachedLastColorModel.get() == this.paintModel) {
                    this.lastRaster = AlphaPaintPipe.cachedLastRaster;
                    this.lastData = AlphaPaintPipe.cachedLastData;
                }
            }
        }
    }
}
