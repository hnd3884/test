package java.awt.image;

import java.awt.geom.Point2D;
import java.util.Hashtable;
import java.awt.geom.Rectangle2D;
import sun.awt.image.ImagingLib;
import java.awt.RenderingHints;

public class RescaleOp implements BufferedImageOp, RasterOp
{
    float[] scaleFactors;
    float[] offsets;
    int length;
    RenderingHints hints;
    private int srcNbits;
    private int dstNbits;
    
    public RescaleOp(final float[] array, final float[] array2, final RenderingHints hints) {
        this.length = 0;
        this.length = array.length;
        if (this.length > array2.length) {
            this.length = array2.length;
        }
        this.scaleFactors = new float[this.length];
        this.offsets = new float[this.length];
        for (int i = 0; i < this.length; ++i) {
            this.scaleFactors[i] = array[i];
            this.offsets[i] = array2[i];
        }
        this.hints = hints;
    }
    
    public RescaleOp(final float n, final float n2, final RenderingHints hints) {
        this.length = 0;
        this.length = 1;
        this.scaleFactors = new float[1];
        this.offsets = new float[1];
        this.scaleFactors[0] = n;
        this.offsets[0] = n2;
        this.hints = hints;
    }
    
    public final float[] getScaleFactors(final float[] array) {
        if (array == null) {
            return this.scaleFactors.clone();
        }
        System.arraycopy(this.scaleFactors, 0, array, 0, Math.min(this.scaleFactors.length, array.length));
        return array;
    }
    
    public final float[] getOffsets(final float[] array) {
        if (array == null) {
            return this.offsets.clone();
        }
        System.arraycopy(this.offsets, 0, array, 0, Math.min(this.offsets.length, array.length));
        return array;
    }
    
    public final int getNumFactors() {
        return this.length;
    }
    
    private ByteLookupTable createByteLut(final float[] array, final float[] array2, final int n, final int n2) {
        final byte[][] array3 = new byte[array.length][n2];
        for (int i = 0; i < array.length; ++i) {
            final float n3 = array[i];
            final float n4 = array2[i];
            final byte[] array4 = array3[i];
            for (int j = 0; j < n2; ++j) {
                int n5 = (int)(j * n3 + n4);
                if ((n5 & 0xFFFFFF00) != 0x0) {
                    if (n5 < 0) {
                        n5 = 0;
                    }
                    else {
                        n5 = 255;
                    }
                }
                array4[j] = (byte)n5;
            }
        }
        return new ByteLookupTable(0, array3);
    }
    
    private ShortLookupTable createShortLut(final float[] array, final float[] array2, final int n, final int n2) {
        final short[][] array3 = new short[array.length][n2];
        for (int i = 0; i < array.length; ++i) {
            final float n3 = array[i];
            final float n4 = array2[i];
            final short[] array4 = array3[i];
            for (int j = 0; j < n2; ++j) {
                int n5 = (int)(j * n3 + n4);
                if ((n5 & 0xFFFF0000) != 0x0) {
                    if (n5 < 0) {
                        n5 = 0;
                    }
                    else {
                        n5 = 65535;
                    }
                }
                array4[j] = (short)n5;
            }
        }
        return new ShortLookupTable(0, array3);
    }
    
    private boolean canUseLookup(final Raster raster, final Raster raster2) {
        final int dataType = raster.getDataBuffer().getDataType();
        if (dataType != 0 && dataType != 1) {
            return false;
        }
        final SampleModel sampleModel = raster2.getSampleModel();
        this.dstNbits = sampleModel.getSampleSize(0);
        if (this.dstNbits != 8 && this.dstNbits != 16) {
            return false;
        }
        for (int i = 1; i < raster.getNumBands(); ++i) {
            if (sampleModel.getSampleSize(i) != this.dstNbits) {
                return false;
            }
        }
        final SampleModel sampleModel2 = raster.getSampleModel();
        this.srcNbits = sampleModel2.getSampleSize(0);
        if (this.srcNbits > 16) {
            return false;
        }
        for (int j = 1; j < raster.getNumBands(); ++j) {
            if (sampleModel2.getSampleSize(j) != this.srcNbits) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public final BufferedImage filter(final BufferedImage bufferedImage, BufferedImage bufferedImage2) {
        final ColorModel colorModel = bufferedImage.getColorModel();
        final int numColorComponents = colorModel.getNumColorComponents();
        if (colorModel instanceof IndexColorModel) {
            throw new IllegalArgumentException("Rescaling cannot be performed on an indexed image");
        }
        if (this.length != 1 && this.length != numColorComponents && this.length != colorModel.getNumComponents()) {
            throw new IllegalArgumentException("Number of scaling constants does not equal the number of of color or color/alpha  components");
        }
        boolean b = false;
        if (this.length > numColorComponents && colorModel.hasAlpha()) {
            this.length = numColorComponents + 1;
        }
        final int width = bufferedImage.getWidth();
        final int height = bufferedImage.getHeight();
        ColorModel colorModel2;
        if (bufferedImage2 == null) {
            bufferedImage2 = this.createCompatibleDestImage(bufferedImage, null);
            colorModel2 = colorModel;
        }
        else {
            if (width != bufferedImage2.getWidth()) {
                throw new IllegalArgumentException("Src width (" + width + ") not equal to dst width (" + bufferedImage2.getWidth() + ")");
            }
            if (height != bufferedImage2.getHeight()) {
                throw new IllegalArgumentException("Src height (" + height + ") not equal to dst height (" + bufferedImage2.getHeight() + ")");
            }
            colorModel2 = bufferedImage2.getColorModel();
            if (colorModel.getColorSpace().getType() != colorModel2.getColorSpace().getType()) {
                b = true;
                bufferedImage2 = this.createCompatibleDestImage(bufferedImage, null);
            }
        }
        final BufferedImage bufferedImage3 = bufferedImage2;
        if (ImagingLib.filter(this, bufferedImage, bufferedImage2) == null) {
            WritableRaster writableRaster = bufferedImage.getRaster();
            WritableRaster writableRaster2 = bufferedImage2.getRaster();
            if (colorModel.hasAlpha() && (numColorComponents - 1 == this.length || this.length == 1)) {
                final int minX = writableRaster.getMinX();
                final int minY = writableRaster.getMinY();
                final int[] array = new int[numColorComponents - 1];
                for (int i = 0; i < numColorComponents - 1; ++i) {
                    array[i] = i;
                }
                writableRaster = writableRaster.createWritableChild(minX, minY, writableRaster.getWidth(), writableRaster.getHeight(), minX, minY, array);
            }
            if (colorModel2.hasAlpha() && (writableRaster2.getNumBands() - 1 == this.length || this.length == 1)) {
                final int minX2 = writableRaster2.getMinX();
                final int minY2 = writableRaster2.getMinY();
                final int[] array2 = new int[numColorComponents - 1];
                for (int j = 0; j < numColorComponents - 1; ++j) {
                    array2[j] = j;
                }
                writableRaster2 = writableRaster2.createWritableChild(minX2, minY2, writableRaster2.getWidth(), writableRaster2.getHeight(), minX2, minY2, array2);
            }
            this.filter(writableRaster, writableRaster2);
        }
        if (b) {
            new ColorConvertOp(this.hints).filter(bufferedImage2, bufferedImage3);
        }
        return bufferedImage3;
    }
    
    @Override
    public final WritableRaster filter(final Raster raster, WritableRaster compatibleDestRaster) {
        final int numBands = raster.getNumBands();
        final int width = raster.getWidth();
        final int height = raster.getHeight();
        int[] pixel = null;
        int n = 0;
        if (compatibleDestRaster == null) {
            compatibleDestRaster = this.createCompatibleDestRaster(raster);
        }
        else {
            if (height != compatibleDestRaster.getHeight() || width != compatibleDestRaster.getWidth()) {
                throw new IllegalArgumentException("Width or height of Rasters do not match");
            }
            if (numBands != compatibleDestRaster.getNumBands()) {
                throw new IllegalArgumentException("Number of bands in src " + numBands + " does not equal number of bands in dest " + compatibleDestRaster.getNumBands());
            }
        }
        if (this.length != 1 && this.length != raster.getNumBands()) {
            throw new IllegalArgumentException("Number of scaling constants does not equal the number of of bands in the src raster");
        }
        if (ImagingLib.filter(this, raster, compatibleDestRaster) != null) {
            return compatibleDestRaster;
        }
        if (this.canUseLookup(raster, compatibleDestRaster)) {
            final int n2 = 1 << this.srcNbits;
            if (1 << this.dstNbits == 256) {
                new LookupOp(this.createByteLut(this.scaleFactors, this.offsets, numBands, n2), this.hints).filter(raster, compatibleDestRaster);
            }
            else {
                new LookupOp(this.createShortLut(this.scaleFactors, this.offsets, numBands, n2), this.hints).filter(raster, compatibleDestRaster);
            }
        }
        else {
            if (this.length > 1) {
                n = 1;
            }
            final int minX = raster.getMinX();
            int minY = raster.getMinY();
            final int minX2 = compatibleDestRaster.getMinX();
            int minY2 = compatibleDestRaster.getMinY();
            final int[] array = new int[numBands];
            final int[] array2 = new int[numBands];
            final SampleModel sampleModel = compatibleDestRaster.getSampleModel();
            for (int i = 0; i < numBands; ++i) {
                array[i] = (1 << sampleModel.getSampleSize(i)) - 1;
                array2[i] = ~array[i];
            }
            for (int j = 0; j < height; ++j, ++minY, ++minY2) {
                for (int n3 = minX2, n4 = minX, k = 0; k < width; ++k, ++n4, ++n3) {
                    pixel = raster.getPixel(n4, minY, pixel);
                    for (int n5 = 0, l = 0; l < numBands; ++l, n5 += n) {
                        int n6 = (int)(pixel[l] * this.scaleFactors[n5] + this.offsets[n5]);
                        if ((n6 & array2[l]) != 0x0) {
                            if (n6 < 0) {
                                n6 = 0;
                            }
                            else {
                                n6 = array[l];
                            }
                        }
                        pixel[l] = n6;
                    }
                    compatibleDestRaster.setPixel(n3, minY2, pixel);
                }
            }
        }
        return compatibleDestRaster;
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
    public BufferedImage createCompatibleDestImage(final BufferedImage bufferedImage, final ColorModel colorModel) {
        BufferedImage bufferedImage2;
        if (colorModel == null) {
            final ColorModel colorModel2 = bufferedImage.getColorModel();
            bufferedImage2 = new BufferedImage(colorModel2, bufferedImage.getRaster().createCompatibleWritableRaster(), colorModel2.isAlphaPremultiplied(), null);
        }
        else {
            bufferedImage2 = new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(bufferedImage.getWidth(), bufferedImage.getHeight()), colorModel.isAlphaPremultiplied(), null);
        }
        return bufferedImage2;
    }
    
    @Override
    public WritableRaster createCompatibleDestRaster(final Raster raster) {
        return raster.createCompatibleWritableRaster(raster.getWidth(), raster.getHeight());
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
