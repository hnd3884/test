package java.awt.image;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;
import java.awt.Graphics2D;
import java.awt.Image;
import sun.awt.image.ImagingLib;
import java.awt.RenderingHints;

public class ConvolveOp implements BufferedImageOp, RasterOp
{
    Kernel kernel;
    int edgeHint;
    RenderingHints hints;
    public static final int EDGE_ZERO_FILL = 0;
    public static final int EDGE_NO_OP = 1;
    
    public ConvolveOp(final Kernel kernel, final int edgeHint, final RenderingHints hints) {
        this.kernel = kernel;
        this.edgeHint = edgeHint;
        this.hints = hints;
    }
    
    public ConvolveOp(final Kernel kernel) {
        this.kernel = kernel;
        this.edgeHint = 0;
    }
    
    public int getEdgeCondition() {
        return this.edgeHint;
    }
    
    public final Kernel getKernel() {
        return (Kernel)this.kernel.clone();
    }
    
    @Override
    public final BufferedImage filter(BufferedImage convertToIntDiscrete, BufferedImage bufferedImage) {
        if (convertToIntDiscrete == null) {
            throw new NullPointerException("src image is null");
        }
        if (convertToIntDiscrete == bufferedImage) {
            throw new IllegalArgumentException("src image cannot be the same as the dst image");
        }
        boolean b = false;
        ColorModel colorModel = convertToIntDiscrete.getColorModel();
        BufferedImage compatibleDestImage = bufferedImage;
        if (colorModel instanceof IndexColorModel) {
            convertToIntDiscrete = ((IndexColorModel)colorModel).convertToIntDiscrete(convertToIntDiscrete.getRaster(), false);
            colorModel = convertToIntDiscrete.getColorModel();
        }
        if (bufferedImage == null) {
            bufferedImage = (compatibleDestImage = this.createCompatibleDestImage(convertToIntDiscrete, null));
        }
        else {
            final ColorModel colorModel2 = bufferedImage.getColorModel();
            if (colorModel.getColorSpace().getType() != colorModel2.getColorSpace().getType()) {
                b = true;
                bufferedImage = this.createCompatibleDestImage(convertToIntDiscrete, null);
                bufferedImage.getColorModel();
            }
            else if (colorModel2 instanceof IndexColorModel) {
                bufferedImage = this.createCompatibleDestImage(convertToIntDiscrete, null);
                bufferedImage.getColorModel();
            }
        }
        if (ImagingLib.filter(this, convertToIntDiscrete, bufferedImage) == null) {
            throw new ImagingOpException("Unable to convolve src image");
        }
        if (b) {
            new ColorConvertOp(this.hints).filter(bufferedImage, compatibleDestImage);
        }
        else if (compatibleDestImage != bufferedImage) {
            final Graphics2D graphics = compatibleDestImage.createGraphics();
            try {
                graphics.drawImage(bufferedImage, 0, 0, null);
            }
            finally {
                graphics.dispose();
            }
        }
        return compatibleDestImage;
    }
    
    @Override
    public final WritableRaster filter(final Raster raster, WritableRaster compatibleDestRaster) {
        if (compatibleDestRaster == null) {
            compatibleDestRaster = this.createCompatibleDestRaster(raster);
        }
        else {
            if (raster == compatibleDestRaster) {
                throw new IllegalArgumentException("src image cannot be the same as the dst image");
            }
            if (raster.getNumBands() != compatibleDestRaster.getNumBands()) {
                throw new ImagingOpException("Different number of bands in src  and dst Rasters");
            }
        }
        if (ImagingLib.filter(this, raster, compatibleDestRaster) == null) {
            throw new ImagingOpException("Unable to convolve src image");
        }
        return compatibleDestRaster;
    }
    
    @Override
    public BufferedImage createCompatibleDestImage(final BufferedImage bufferedImage, ColorModel colorModel) {
        final int width = bufferedImage.getWidth();
        final int height = bufferedImage.getHeight();
        WritableRaster writableRaster = null;
        if (colorModel == null) {
            colorModel = bufferedImage.getColorModel();
            if (colorModel instanceof IndexColorModel) {
                colorModel = ColorModel.getRGBdefault();
            }
            else {
                writableRaster = bufferedImage.getData().createCompatibleWritableRaster(width, height);
            }
        }
        if (writableRaster == null) {
            writableRaster = colorModel.createCompatibleWritableRaster(width, height);
        }
        return new BufferedImage(colorModel, writableRaster, colorModel.isAlphaPremultiplied(), null);
    }
    
    @Override
    public WritableRaster createCompatibleDestRaster(final Raster raster) {
        return raster.createCompatibleWritableRaster();
    }
    
    @Override
    public final Rectangle2D getBounds2D(final BufferedImage bufferedImage) {
        return this.getBounds2D(bufferedImage.getRaster());
    }
    
    @Override
    public final Rectangle2D getBounds2D(final Raster raster) {
        return raster.getBounds();
    }
    
    @Override
    public final Point2D getPoint2D(final Point2D point2D, Point2D point2D2) {
        if (point2D2 == null) {
            point2D2 = new Point2D.Float();
        }
        point2D2.setLocation(point2D.getX(), point2D.getY());
        return point2D2;
    }
    
    @Override
    public final RenderingHints getRenderingHints() {
        return this.hints;
    }
}
