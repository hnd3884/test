package java.awt.image;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import sun.awt.image.ImagingLib;
import java.util.Arrays;
import java.awt.RenderingHints;

public class BandCombineOp implements RasterOp
{
    float[][] matrix;
    int nrows;
    int ncols;
    RenderingHints hints;
    
    public BandCombineOp(final float[][] array, final RenderingHints hints) {
        this.nrows = 0;
        this.ncols = 0;
        this.nrows = array.length;
        this.ncols = array[0].length;
        this.matrix = new float[this.nrows][];
        for (int i = 0; i < this.nrows; ++i) {
            if (this.ncols > array[i].length) {
                throw new IndexOutOfBoundsException("row " + i + " too short");
            }
            this.matrix[i] = Arrays.copyOf(array[i], this.ncols);
        }
        this.hints = hints;
    }
    
    public final float[][] getMatrix() {
        final float[][] array = new float[this.nrows][];
        for (int i = 0; i < this.nrows; ++i) {
            array[i] = Arrays.copyOf(this.matrix[i], this.ncols);
        }
        return array;
    }
    
    @Override
    public WritableRaster filter(final Raster raster, WritableRaster compatibleDestRaster) {
        final int numBands = raster.getNumBands();
        if (this.ncols != numBands && this.ncols != numBands + 1) {
            throw new IllegalArgumentException("Number of columns in the matrix (" + this.ncols + ") must be equal to the number of bands ([+1]) in src (" + numBands + ").");
        }
        if (compatibleDestRaster == null) {
            compatibleDestRaster = this.createCompatibleDestRaster(raster);
        }
        else if (this.nrows != compatibleDestRaster.getNumBands()) {
            throw new IllegalArgumentException("Number of rows in the matrix (" + this.nrows + ") must be equal to the number of bands ([+1]) in dst (" + numBands + ").");
        }
        if (ImagingLib.filter(this, raster, compatibleDestRaster) != null) {
            return compatibleDestRaster;
        }
        int[] array = null;
        final int[] array2 = new int[compatibleDestRaster.getNumBands()];
        final int minX = raster.getMinX();
        int minY = raster.getMinY();
        final int minX2 = compatibleDestRaster.getMinX();
        int minY2 = compatibleDestRaster.getMinY();
        if (this.ncols == numBands) {
            for (int i = 0; i < raster.getHeight(); ++i, ++minY, ++minY2) {
                for (int n = minX2, n2 = minX, j = 0; j < raster.getWidth(); ++j, ++n2, ++n) {
                    array = raster.getPixel(n2, minY, array);
                    for (int k = 0; k < this.nrows; ++k) {
                        float n3 = 0.0f;
                        for (int l = 0; l < this.ncols; ++l) {
                            n3 += this.matrix[k][l] * array[l];
                        }
                        array2[k] = (int)n3;
                    }
                    compatibleDestRaster.setPixel(n, minY2, array2);
                }
            }
        }
        else {
            for (int n4 = 0; n4 < raster.getHeight(); ++n4, ++minY, ++minY2) {
                for (int n5 = minX2, n6 = minX, n7 = 0; n7 < raster.getWidth(); ++n7, ++n6, ++n5) {
                    array = raster.getPixel(n6, minY, array);
                    for (int n8 = 0; n8 < this.nrows; ++n8) {
                        float n9 = 0.0f;
                        for (int n10 = 0; n10 < numBands; ++n10) {
                            n9 += this.matrix[n8][n10] * array[n10];
                        }
                        array2[n8] = (int)(n9 + this.matrix[n8][numBands]);
                    }
                    compatibleDestRaster.setPixel(n5, minY2, array2);
                }
            }
        }
        return compatibleDestRaster;
    }
    
    @Override
    public final Rectangle2D getBounds2D(final Raster raster) {
        return raster.getBounds();
    }
    
    @Override
    public WritableRaster createCompatibleDestRaster(final Raster raster) {
        final int numBands = raster.getNumBands();
        if (this.ncols != numBands && this.ncols != numBands + 1) {
            throw new IllegalArgumentException("Number of columns in the matrix (" + this.ncols + ") must be equal to the number of bands ([+1]) in src (" + numBands + ").");
        }
        if (raster.getNumBands() == this.nrows) {
            return raster.createCompatibleWritableRaster();
        }
        throw new IllegalArgumentException("Don't know how to create a  compatible Raster with " + this.nrows + " bands.");
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
