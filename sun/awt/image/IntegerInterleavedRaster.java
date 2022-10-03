package sun.awt.image;

import java.awt.image.WritableRaster;
import java.awt.image.Raster;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.RasterFormatException;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBuffer;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.image.SampleModel;

public class IntegerInterleavedRaster extends IntegerComponentRaster
{
    private int maxX;
    private int maxY;
    
    public IntegerInterleavedRaster(final SampleModel sampleModel, final Point point) {
        this(sampleModel, sampleModel.createDataBuffer(), new Rectangle(point.x, point.y, sampleModel.getWidth(), sampleModel.getHeight()), point, null);
    }
    
    public IntegerInterleavedRaster(final SampleModel sampleModel, final DataBuffer dataBuffer, final Point point) {
        this(sampleModel, dataBuffer, new Rectangle(point.x, point.y, sampleModel.getWidth(), sampleModel.getHeight()), point, null);
    }
    
    public IntegerInterleavedRaster(final SampleModel sampleModel, final DataBuffer dataBuffer, final Rectangle rectangle, final Point point, final IntegerInterleavedRaster integerInterleavedRaster) {
        super(sampleModel, dataBuffer, rectangle, point, integerInterleavedRaster);
        this.maxX = this.minX + this.width;
        this.maxY = this.minY + this.height;
        if (!(dataBuffer instanceof DataBufferInt)) {
            throw new RasterFormatException("IntegerInterleavedRasters must haveinteger DataBuffers");
        }
        final DataBufferInt dataBufferInt = (DataBufferInt)dataBuffer;
        this.data = SunWritableRaster.stealData(dataBufferInt, 0);
        if (sampleModel instanceof SinglePixelPackedSampleModel) {
            final SinglePixelPackedSampleModel singlePixelPackedSampleModel = (SinglePixelPackedSampleModel)sampleModel;
            this.scanlineStride = singlePixelPackedSampleModel.getScanlineStride();
            this.pixelStride = 1;
            (this.dataOffsets = new int[1])[0] = dataBufferInt.getOffset();
            this.bandOffset = this.dataOffsets[0];
            final int n = rectangle.x - point.x;
            final int n2 = rectangle.y - point.y;
            final int[] dataOffsets = this.dataOffsets;
            final int n3 = 0;
            dataOffsets[n3] += n + n2 * this.scanlineStride;
            this.numDataElems = singlePixelPackedSampleModel.getNumDataElements();
            this.verify();
            return;
        }
        throw new RasterFormatException("IntegerInterleavedRasters must have SinglePixelPackedSampleModel");
    }
    
    @Override
    public int[] getDataOffsets() {
        return this.dataOffsets.clone();
    }
    
    @Override
    public int getDataOffset(final int n) {
        return this.dataOffsets[n];
    }
    
    @Override
    public int getScanlineStride() {
        return this.scanlineStride;
    }
    
    @Override
    public int getPixelStride() {
        return this.pixelStride;
    }
    
    @Override
    public int[] getDataStorage() {
        return this.data;
    }
    
    @Override
    public Object getDataElements(final int n, final int n2, final Object o) {
        if (n < this.minX || n2 < this.minY || n >= this.maxX || n2 >= this.maxY) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        int[] array;
        if (o == null) {
            array = new int[] { 0 };
        }
        else {
            array = (int[])o;
        }
        array[0] = this.data[(n2 - this.minY) * this.scanlineStride + (n - this.minX) + this.dataOffsets[0]];
        return array;
    }
    
    @Override
    public Object getDataElements(final int n, final int n2, final int n3, final int n4, final Object o) {
        if (n < this.minX || n2 < this.minY || n + n3 > this.maxX || n2 + n4 > this.maxY) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        int[] array;
        if (o instanceof int[]) {
            array = (int[])o;
        }
        else {
            array = new int[n3 * n4];
        }
        int n5 = (n2 - this.minY) * this.scanlineStride + (n - this.minX) + this.dataOffsets[0];
        int n6 = 0;
        for (int i = 0; i < n4; ++i) {
            System.arraycopy(this.data, n5, array, n6, n3);
            n6 += n3;
            n5 += this.scanlineStride;
        }
        return array;
    }
    
    @Override
    public void setDataElements(final int n, final int n2, final Object o) {
        if (n < this.minX || n2 < this.minY || n >= this.maxX || n2 >= this.maxY) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        this.data[(n2 - this.minY) * this.scanlineStride + (n - this.minX) + this.dataOffsets[0]] = ((int[])o)[0];
        this.markDirty();
    }
    
    @Override
    public void setDataElements(final int n, final int n2, final Raster raster) {
        final int n3 = n + raster.getMinX();
        final int n4 = n2 + raster.getMinY();
        final int width = raster.getWidth();
        final int height = raster.getHeight();
        if (n3 < this.minX || n4 < this.minY || n3 + width > this.maxX || n4 + height > this.maxY) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        this.setDataElements(n3, n4, width, height, raster);
    }
    
    private void setDataElements(final int n, final int n2, final int n3, final int n4, final Raster raster) {
        if (n3 <= 0 || n4 <= 0) {
            return;
        }
        final int minX = raster.getMinX();
        final int minY = raster.getMinY();
        if (raster instanceof IntegerInterleavedRaster) {
            final IntegerInterleavedRaster integerInterleavedRaster = (IntegerInterleavedRaster)raster;
            final int[] dataStorage = integerInterleavedRaster.getDataStorage();
            final int scanlineStride = integerInterleavedRaster.getScanlineStride();
            int dataOffset = integerInterleavedRaster.getDataOffset(0);
            int n5 = this.dataOffsets[0] + (n2 - this.minY) * this.scanlineStride + (n - this.minX);
            for (int i = 0; i < n4; ++i) {
                System.arraycopy(dataStorage, dataOffset, this.data, n5, n3);
                dataOffset += scanlineStride;
                n5 += this.scanlineStride;
            }
            this.markDirty();
            return;
        }
        Object dataElements = null;
        for (int j = 0; j < n4; ++j) {
            dataElements = raster.getDataElements(minX, minY + j, n3, 1, dataElements);
            this.setDataElements(n, n2 + j, n3, 1, dataElements);
        }
    }
    
    @Override
    public void setDataElements(final int n, final int n2, final int n3, final int n4, final Object o) {
        if (n < this.minX || n2 < this.minY || n + n3 > this.maxX || n2 + n4 > this.maxY) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        final int[] array = (int[])o;
        int n5 = (n2 - this.minY) * this.scanlineStride + (n - this.minX) + this.dataOffsets[0];
        int n6 = 0;
        for (int i = 0; i < n4; ++i) {
            System.arraycopy(array, n6, this.data, n5, n3);
            n6 += n3;
            n5 += this.scanlineStride;
        }
        this.markDirty();
    }
    
    @Override
    public WritableRaster createWritableChild(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int[] array) {
        if (n < this.minX) {
            throw new RasterFormatException("x lies outside raster");
        }
        if (n2 < this.minY) {
            throw new RasterFormatException("y lies outside raster");
        }
        if (n + n3 < n || n + n3 > this.minX + this.width) {
            throw new RasterFormatException("(x + width) is outside raster");
        }
        if (n2 + n4 < n2 || n2 + n4 > this.minY + this.height) {
            throw new RasterFormatException("(y + height) is outside raster");
        }
        SampleModel sampleModel;
        if (array != null) {
            sampleModel = this.sampleModel.createSubsetSampleModel(array);
        }
        else {
            sampleModel = this.sampleModel;
        }
        return new IntegerInterleavedRaster(sampleModel, this.dataBuffer, new Rectangle(n5, n6, n3, n4), new Point(this.sampleModelTranslateX + (n5 - n), this.sampleModelTranslateY + (n6 - n2)), this);
    }
    
    @Override
    public Raster createChild(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int[] array) {
        return this.createWritableChild(n, n2, n3, n4, n5, n6, array);
    }
    
    @Override
    public WritableRaster createCompatibleWritableRaster(final int n, final int n2) {
        if (n <= 0 || n2 <= 0) {
            throw new RasterFormatException("negative " + ((n <= 0) ? "width" : "height"));
        }
        return new IntegerInterleavedRaster(this.sampleModel.createCompatibleSampleModel(n, n2), new Point(0, 0));
    }
    
    @Override
    public WritableRaster createCompatibleWritableRaster() {
        return this.createCompatibleWritableRaster(this.width, this.height);
    }
    
    @Override
    public String toString() {
        return new String("IntegerInterleavedRaster: width = " + this.width + " height = " + this.height + " #Bands = " + this.numBands + " xOff = " + this.sampleModelTranslateX + " yOff = " + this.sampleModelTranslateY + " dataOffset[0] " + this.dataOffsets[0]);
    }
}
