package java.awt.image;

import java.awt.geom.Point2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Composite;
import java.awt.AlphaComposite;
import sun.awt.image.ImagingLib;
import java.util.Hashtable;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

public class AffineTransformOp implements BufferedImageOp, RasterOp
{
    private AffineTransform xform;
    RenderingHints hints;
    public static final int TYPE_NEAREST_NEIGHBOR = 1;
    public static final int TYPE_BILINEAR = 2;
    public static final int TYPE_BICUBIC = 3;
    int interpolationType;
    
    public AffineTransformOp(final AffineTransform affineTransform, final RenderingHints hints) {
        this.interpolationType = 1;
        this.validateTransform(affineTransform);
        this.xform = (AffineTransform)affineTransform.clone();
        this.hints = hints;
        if (hints != null) {
            final Object value = hints.get(RenderingHints.KEY_INTERPOLATION);
            if (value == null) {
                final Object value2 = hints.get(RenderingHints.KEY_RENDERING);
                if (value2 == RenderingHints.VALUE_RENDER_SPEED) {
                    this.interpolationType = 1;
                }
                else if (value2 == RenderingHints.VALUE_RENDER_QUALITY) {
                    this.interpolationType = 2;
                }
            }
            else if (value == RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR) {
                this.interpolationType = 1;
            }
            else if (value == RenderingHints.VALUE_INTERPOLATION_BILINEAR) {
                this.interpolationType = 2;
            }
            else if (value == RenderingHints.VALUE_INTERPOLATION_BICUBIC) {
                this.interpolationType = 3;
            }
        }
        else {
            this.interpolationType = 1;
        }
    }
    
    public AffineTransformOp(final AffineTransform affineTransform, final int interpolationType) {
        this.interpolationType = 1;
        this.validateTransform(affineTransform);
        this.xform = (AffineTransform)affineTransform.clone();
        switch (interpolationType) {
            case 1:
            case 2:
            case 3: {
                this.interpolationType = interpolationType;
                return;
            }
            default: {
                throw new IllegalArgumentException("Unknown interpolation type: " + interpolationType);
            }
        }
    }
    
    public final int getInterpolationType() {
        return this.interpolationType;
    }
    
    @Override
    public final BufferedImage filter(BufferedImage filter, BufferedImage compatibleDestImage) {
        if (filter == null) {
            throw new NullPointerException("src image is null");
        }
        if (filter == compatibleDestImage) {
            throw new IllegalArgumentException("src image cannot be the same as the dst image");
        }
        boolean b = false;
        final ColorModel colorModel = filter.getColorModel();
        BufferedImage compatibleDestImage2;
        if ((compatibleDestImage2 = compatibleDestImage) == null) {
            compatibleDestImage = (compatibleDestImage2 = this.createCompatibleDestImage(filter, null));
        }
        else {
            final ColorModel colorModel2 = compatibleDestImage.getColorModel();
            if (colorModel.getColorSpace().getType() != colorModel2.getColorSpace().getType()) {
                final int type;
                final int n = type = this.xform.getType();
                final AffineTransform xform = this.xform;
                final int n2 = 24;
                final AffineTransform xform2 = this.xform;
                boolean b2 = (type & (n2 | 0x20)) != 0x0;
                if (!b2) {
                    final int n3 = n;
                    final AffineTransform xform3 = this.xform;
                    if (n3 != 1) {
                        final int n4 = n;
                        final AffineTransform xform4 = this.xform;
                        if (n4 != 0) {
                            final double[] array = new double[4];
                            this.xform.getMatrix(array);
                            b2 = (array[0] != (int)array[0] || array[3] != (int)array[3]);
                        }
                    }
                }
                if (b2 && colorModel.getTransparency() == 1) {
                    final ColorConvertOp colorConvertOp = new ColorConvertOp(this.hints);
                    final int width = filter.getWidth();
                    final int height = filter.getHeight();
                    BufferedImage bufferedImage;
                    if (colorModel2.getTransparency() == 1) {
                        bufferedImage = new BufferedImage(width, height, 2);
                    }
                    else {
                        bufferedImage = new BufferedImage(colorModel2, colorModel2.createCompatibleWritableRaster(width, height), colorModel2.isAlphaPremultiplied(), null);
                    }
                    filter = colorConvertOp.filter(filter, bufferedImage);
                }
                else {
                    b = true;
                    compatibleDestImage = this.createCompatibleDestImage(filter, null);
                }
            }
        }
        if (this.interpolationType != 1 && compatibleDestImage.getColorModel() instanceof IndexColorModel) {
            compatibleDestImage = new BufferedImage(compatibleDestImage.getWidth(), compatibleDestImage.getHeight(), 2);
        }
        if (ImagingLib.filter(this, filter, compatibleDestImage) == null) {
            throw new ImagingOpException("Unable to transform src image");
        }
        if (b) {
            new ColorConvertOp(this.hints).filter(compatibleDestImage, compatibleDestImage2);
        }
        else if (compatibleDestImage2 != compatibleDestImage) {
            final Graphics2D graphics = compatibleDestImage2.createGraphics();
            try {
                graphics.setComposite(AlphaComposite.Src);
                graphics.drawImage(compatibleDestImage, 0, 0, null);
            }
            finally {
                graphics.dispose();
            }
        }
        return compatibleDestImage2;
    }
    
    @Override
    public final WritableRaster filter(final Raster raster, WritableRaster compatibleDestRaster) {
        if (raster == null) {
            throw new NullPointerException("src image is null");
        }
        if (compatibleDestRaster == null) {
            compatibleDestRaster = this.createCompatibleDestRaster(raster);
        }
        if (raster == compatibleDestRaster) {
            throw new IllegalArgumentException("src image cannot be the same as the dst image");
        }
        if (raster.getNumBands() != compatibleDestRaster.getNumBands()) {
            throw new IllegalArgumentException("Number of src bands (" + raster.getNumBands() + ") does not match number of  dst bands (" + compatibleDestRaster.getNumBands() + ")");
        }
        if (ImagingLib.filter(this, raster, compatibleDestRaster) == null) {
            throw new ImagingOpException("Unable to transform src image");
        }
        return compatibleDestRaster;
    }
    
    @Override
    public final Rectangle2D getBounds2D(final BufferedImage bufferedImage) {
        return this.getBounds2D(bufferedImage.getRaster());
    }
    
    @Override
    public final Rectangle2D getBounds2D(final Raster raster) {
        final int width = raster.getWidth();
        final int height = raster.getHeight();
        final float[] array = { 0.0f, 0.0f, (float)width, 0.0f, (float)width, (float)height, 0.0f, (float)height };
        this.xform.transform(array, 0, array, 0, 4);
        float n = array[0];
        float n2 = array[1];
        float n3 = array[0];
        float n4 = array[1];
        for (int i = 2; i < 8; i += 2) {
            if (array[i] > n) {
                n = array[i];
            }
            else if (array[i] < n3) {
                n3 = array[i];
            }
            if (array[i + 1] > n2) {
                n2 = array[i + 1];
            }
            else if (array[i + 1] < n4) {
                n4 = array[i + 1];
            }
        }
        return new Rectangle2D.Float(n3, n4, n - n3, n2 - n4);
    }
    
    @Override
    public BufferedImage createCompatibleDestImage(final BufferedImage bufferedImage, final ColorModel colorModel) {
        final Rectangle bounds = this.getBounds2D(bufferedImage).getBounds();
        final int n = bounds.x + bounds.width;
        final int n2 = bounds.y + bounds.height;
        if (n <= 0) {
            throw new RasterFormatException("Transformed width (" + n + ") is less than or equal to 0.");
        }
        if (n2 <= 0) {
            throw new RasterFormatException("Transformed height (" + n2 + ") is less than or equal to 0.");
        }
        BufferedImage bufferedImage2;
        if (colorModel == null) {
            final ColorModel colorModel2 = bufferedImage.getColorModel();
            if (this.interpolationType != 1 && (colorModel2 instanceof IndexColorModel || colorModel2.getTransparency() == 1)) {
                bufferedImage2 = new BufferedImage(n, n2, 2);
            }
            else {
                bufferedImage2 = new BufferedImage(colorModel2, bufferedImage.getRaster().createCompatibleWritableRaster(n, n2), colorModel2.isAlphaPremultiplied(), null);
            }
        }
        else {
            bufferedImage2 = new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(n, n2), colorModel.isAlphaPremultiplied(), null);
        }
        return bufferedImage2;
    }
    
    @Override
    public WritableRaster createCompatibleDestRaster(final Raster raster) {
        final Rectangle2D bounds2D = this.getBounds2D(raster);
        return raster.createCompatibleWritableRaster((int)bounds2D.getX(), (int)bounds2D.getY(), (int)bounds2D.getWidth(), (int)bounds2D.getHeight());
    }
    
    @Override
    public final Point2D getPoint2D(final Point2D point2D, final Point2D point2D2) {
        return this.xform.transform(point2D, point2D2);
    }
    
    public final AffineTransform getTransform() {
        return (AffineTransform)this.xform.clone();
    }
    
    @Override
    public final RenderingHints getRenderingHints() {
        if (this.hints == null) {
            Object o = null;
            switch (this.interpolationType) {
                case 1: {
                    o = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
                    break;
                }
                case 2: {
                    o = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
                    break;
                }
                case 3: {
                    o = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
                    break;
                }
                default: {
                    throw new InternalError("Unknown interpolation type " + this.interpolationType);
                }
            }
            this.hints = new RenderingHints(RenderingHints.KEY_INTERPOLATION, o);
        }
        return this.hints;
    }
    
    void validateTransform(final AffineTransform affineTransform) {
        if (Math.abs(affineTransform.getDeterminant()) <= Double.MIN_VALUE) {
            throw new ImagingOpException("Unable to invert transform " + affineTransform);
        }
    }
}
