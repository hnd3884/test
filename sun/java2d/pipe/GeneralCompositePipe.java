package sun.java2d.pipe;

import sun.java2d.SurfaceData;
import java.awt.CompositeContext;
import sun.java2d.loops.MaskBlit;
import java.awt.Composite;
import java.awt.AlphaComposite;
import sun.java2d.loops.Blit;
import sun.java2d.loops.CompositeType;
import sun.awt.image.BufImgSurfaceData;
import java.util.Hashtable;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.PaintContext;
import java.awt.image.ColorModel;
import java.awt.RenderingHints;
import java.awt.Rectangle;
import java.awt.Shape;
import sun.java2d.SunGraphics2D;

public class GeneralCompositePipe implements CompositePipe
{
    @Override
    public Object startSequence(final SunGraphics2D sunGraphics2D, final Shape shape, final Rectangle rectangle, final int[] array) {
        final RenderingHints renderingHints = sunGraphics2D.getRenderingHints();
        final ColorModel deviceColorModel = sunGraphics2D.getDeviceColorModel();
        final PaintContext context = sunGraphics2D.paint.createContext(deviceColorModel, rectangle, shape.getBounds2D(), sunGraphics2D.cloneTransform(), renderingHints);
        return new TileContext(sunGraphics2D, context, sunGraphics2D.composite.createContext(context.getColorModel(), deviceColorModel, renderingHints), deviceColorModel);
    }
    
    @Override
    public boolean needTile(final Object o, final int n, final int n2, final int n3, final int n4) {
        return true;
    }
    
    @Override
    public void renderPathTile(final Object o, final byte[] array, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        final TileContext tileContext = (TileContext)o;
        final PaintContext paintCtxt = tileContext.paintCtxt;
        final CompositeContext compCtxt = tileContext.compCtxt;
        final SunGraphics2D sunG2D = tileContext.sunG2D;
        final Raster raster = paintCtxt.getRaster(n3, n4, n5, n6);
        paintCtxt.getColorModel();
        final SurfaceData surfaceData = sunG2D.getSurfaceData();
        final Raster raster2 = surfaceData.getRaster(n3, n4, n5, n6);
        Raster raster3;
        WritableRaster compatibleWritableRaster;
        if (raster2 instanceof WritableRaster && array == null) {
            compatibleWritableRaster = (WritableRaster)(raster3 = ((WritableRaster)raster2).createWritableChild(n3, n4, n5, n6, 0, 0, null));
        }
        else {
            raster3 = raster2.createChild(n3, n4, n5, n6, 0, 0, null);
            compatibleWritableRaster = raster3.createCompatibleWritableRaster();
        }
        compCtxt.compose(raster, raster3, compatibleWritableRaster);
        if (raster2 != compatibleWritableRaster && compatibleWritableRaster.getParent() != raster2) {
            if (raster2 instanceof WritableRaster && array == null) {
                ((WritableRaster)raster2).setDataElements(n3, n4, compatibleWritableRaster);
            }
            else {
                final ColorModel deviceColorModel = sunG2D.getDeviceColorModel();
                final SurfaceData data = BufImgSurfaceData.createData(new BufferedImage(deviceColorModel, compatibleWritableRaster, deviceColorModel.isAlphaPremultiplied(), null));
                if (array == null) {
                    Blit.getFromCache(data.getSurfaceType(), CompositeType.SrcNoEa, surfaceData.getSurfaceType()).Blit(data, surfaceData, AlphaComposite.Src, null, 0, 0, n3, n4, n5, n6);
                }
                else {
                    MaskBlit.getFromCache(data.getSurfaceType(), CompositeType.SrcNoEa, surfaceData.getSurfaceType()).MaskBlit(data, surfaceData, AlphaComposite.Src, null, 0, 0, n3, n4, n5, n6, array, n, n2);
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
        if (tileContext.compCtxt != null) {
            tileContext.compCtxt.dispose();
        }
    }
    
    class TileContext
    {
        SunGraphics2D sunG2D;
        PaintContext paintCtxt;
        CompositeContext compCtxt;
        ColorModel compModel;
        Object pipeState;
        
        public TileContext(final SunGraphics2D sunG2D, final PaintContext paintCtxt, final CompositeContext compCtxt, final ColorModel compModel) {
            this.sunG2D = sunG2D;
            this.paintCtxt = paintCtxt;
            this.compCtxt = compCtxt;
            this.compModel = compModel;
        }
    }
}
