package java.awt.image;

import java.awt.geom.Point2D;
import java.util.Hashtable;
import java.awt.geom.Rectangle2D;
import sun.awt.image.ImagingLib;
import java.awt.RenderingHints;

public class LookupOp implements BufferedImageOp, RasterOp
{
    private LookupTable ltable;
    private int numComponents;
    RenderingHints hints;
    
    public LookupOp(final LookupTable ltable, final RenderingHints hints) {
        this.ltable = ltable;
        this.hints = hints;
        this.numComponents = this.ltable.getNumComponents();
    }
    
    public final LookupTable getTable() {
        return this.ltable;
    }
    
    @Override
    public final BufferedImage filter(final BufferedImage bufferedImage, BufferedImage bufferedImage2) {
        final ColorModel colorModel = bufferedImage.getColorModel();
        final int numColorComponents = colorModel.getNumColorComponents();
        if (colorModel instanceof IndexColorModel) {
            throw new IllegalArgumentException("LookupOp cannot be performed on an indexed image");
        }
        final int numComponents = this.ltable.getNumComponents();
        if (numComponents != 1 && numComponents != colorModel.getNumComponents() && numComponents != colorModel.getNumColorComponents()) {
            throw new IllegalArgumentException("Number of arrays in the  lookup table (" + numComponents + " is not compatible with the  src image: " + bufferedImage);
        }
        boolean b = false;
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
            if (colorModel.hasAlpha() && (numColorComponents - 1 == numComponents || numComponents == 1)) {
                final int minX = writableRaster.getMinX();
                final int minY = writableRaster.getMinY();
                final int[] array = new int[numColorComponents - 1];
                for (int i = 0; i < numColorComponents - 1; ++i) {
                    array[i] = i;
                }
                writableRaster = writableRaster.createWritableChild(minX, minY, writableRaster.getWidth(), writableRaster.getHeight(), minX, minY, array);
            }
            if (colorModel2.hasAlpha() && (writableRaster2.getNumBands() - 1 == numComponents || numComponents == 1)) {
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
        compatibleDestRaster.getNumBands();
        final int height = raster.getHeight();
        final int width = raster.getWidth();
        final int[] array = new int[numBands];
        if (compatibleDestRaster == null) {
            compatibleDestRaster = this.createCompatibleDestRaster(raster);
        }
        else if (height != compatibleDestRaster.getHeight() || width != compatibleDestRaster.getWidth()) {
            throw new IllegalArgumentException("Width or height of Rasters do not match");
        }
        final int numBands2 = compatibleDestRaster.getNumBands();
        if (numBands != numBands2) {
            throw new IllegalArgumentException("Number of channels in the src (" + numBands + ") does not match number of channels in the destination (" + numBands2 + ")");
        }
        final int numComponents = this.ltable.getNumComponents();
        if (numComponents != 1 && numComponents != raster.getNumBands()) {
            throw new IllegalArgumentException("Number of arrays in the  lookup table (" + numComponents + " is not compatible with the  src Raster: " + raster);
        }
        if (ImagingLib.filter(this, raster, compatibleDestRaster) != null) {
            return compatibleDestRaster;
        }
        if (this.ltable instanceof ByteLookupTable) {
            this.byteFilter((ByteLookupTable)this.ltable, raster, compatibleDestRaster, width, height, numBands);
        }
        else if (this.ltable instanceof ShortLookupTable) {
            this.shortFilter((ShortLookupTable)this.ltable, raster, compatibleDestRaster, width, height, numBands);
        }
        else {
            final int minX = raster.getMinX();
            int minY = raster.getMinY();
            final int minX2 = compatibleDestRaster.getMinX();
            for (int minY2 = compatibleDestRaster.getMinY(), i = 0; i < height; ++i, ++minY, ++minY2) {
                for (int n = minX, n2 = minX2, j = 0; j < width; ++j, ++n, ++n2) {
                    raster.getPixel(n, minY, array);
                    this.ltable.lookupPixel(array, array);
                    compatibleDestRaster.setPixel(n2, minY2, array);
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
        final int width = bufferedImage.getWidth();
        final int height = bufferedImage.getHeight();
        int n = 0;
        BufferedImage bufferedImage2;
        if (colorModel == null) {
            ColorModel colorModel2 = bufferedImage.getColorModel();
            final WritableRaster raster = bufferedImage.getRaster();
            if (colorModel2 instanceof ComponentColorModel) {
                final DataBuffer dataBuffer = raster.getDataBuffer();
                final boolean hasAlpha = colorModel2.hasAlpha();
                final boolean alphaPremultiplied = colorModel2.isAlphaPremultiplied();
                final int transparency = colorModel2.getTransparency();
                int[] array = null;
                if (this.ltable instanceof ByteLookupTable) {
                    if (dataBuffer.getDataType() == 1) {
                        if (hasAlpha) {
                            array = new int[2];
                            if (transparency == 2) {
                                array[1] = 1;
                            }
                            else {
                                array[1] = 8;
                            }
                        }
                        else {
                            array = new int[] { 0 };
                        }
                        array[0] = 8;
                    }
                }
                else if (this.ltable instanceof ShortLookupTable) {
                    n = 1;
                    if (dataBuffer.getDataType() == 0) {
                        if (hasAlpha) {
                            array = new int[2];
                            if (transparency == 2) {
                                array[1] = 1;
                            }
                            else {
                                array[1] = 16;
                            }
                        }
                        else {
                            array = new int[] { 0 };
                        }
                        array[0] = 16;
                    }
                }
                if (array != null) {
                    colorModel2 = new ComponentColorModel(colorModel2.getColorSpace(), array, hasAlpha, alphaPremultiplied, transparency, n);
                }
            }
            bufferedImage2 = new BufferedImage(colorModel2, colorModel2.createCompatibleWritableRaster(width, height), colorModel2.isAlphaPremultiplied(), null);
        }
        else {
            bufferedImage2 = new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(width, height), colorModel.isAlphaPremultiplied(), null);
        }
        return bufferedImage2;
    }
    
    @Override
    public WritableRaster createCompatibleDestRaster(final Raster raster) {
        return raster.createCompatibleWritableRaster();
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
    
    private final void byteFilter(final ByteLookupTable byteLookupTable, final Raster raster, final WritableRaster writableRaster, final int n, final int n2, final int n3) {
        int[] samples = null;
        final byte[][] table = byteLookupTable.getTable();
        final int offset = byteLookupTable.getOffset();
        int n4 = 1;
        if (table.length == 1) {
            n4 = 0;
        }
        final int length = table[0].length;
        for (int i = 0; i < n2; ++i) {
            for (int n5 = 0, j = 0; j < n3; ++j, n5 += n4) {
                samples = raster.getSamples(0, i, n, 1, j, samples);
                for (int k = 0; k < n; ++k) {
                    final int n6 = samples[k] - offset;
                    if (n6 < 0 || n6 > length) {
                        throw new IllegalArgumentException("index (" + n6 + "(out of range:  srcPix[" + k + "]=" + samples[k] + " offset=" + offset);
                    }
                    samples[k] = table[n5][n6];
                }
                writableRaster.setSamples(0, i, n, 1, j, samples);
            }
        }
    }
    
    private final void shortFilter(final ShortLookupTable shortLookupTable, final Raster raster, final WritableRaster writableRaster, final int n, final int n2, final int n3) {
        int[] samples = null;
        final short[][] table = shortLookupTable.getTable();
        final int offset = shortLookupTable.getOffset();
        int n4 = 1;
        if (table.length == 1) {
            n4 = 0;
        }
        final int n5 = 65535;
        for (int i = 0; i < n2; ++i) {
            for (int n6 = 0, j = 0; j < n3; ++j, n6 += n4) {
                samples = raster.getSamples(0, i, n, 1, j, samples);
                for (int k = 0; k < n; ++k) {
                    final int n7 = samples[k] - offset;
                    if (n7 < 0 || n7 > n5) {
                        throw new IllegalArgumentException("index out of range " + n7 + " x is " + k + "srcPix[x]=" + samples[k] + " offset=" + offset);
                    }
                    samples[k] = table[n6][n7];
                }
                writableRaster.setSamples(0, i, n, 1, j, samples);
            }
        }
    }
}
