package sun.awt.image;

import java.awt.image.WritableRaster;
import java.awt.image.Raster;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.RasterFormatException;
import java.awt.image.DataBufferUShort;
import java.awt.image.DataBuffer;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.image.SampleModel;

public class ShortInterleavedRaster extends ShortComponentRaster
{
    private int maxX;
    private int maxY;
    
    public ShortInterleavedRaster(final SampleModel sampleModel, final Point point) {
        this(sampleModel, sampleModel.createDataBuffer(), new Rectangle(point.x, point.y, sampleModel.getWidth(), sampleModel.getHeight()), point, null);
    }
    
    public ShortInterleavedRaster(final SampleModel sampleModel, final DataBuffer dataBuffer, final Point point) {
        this(sampleModel, dataBuffer, new Rectangle(point.x, point.y, sampleModel.getWidth(), sampleModel.getHeight()), point, null);
    }
    
    public ShortInterleavedRaster(final SampleModel sampleModel, final DataBuffer dataBuffer, final Rectangle rectangle, final Point point, final ShortInterleavedRaster shortInterleavedRaster) {
        super(sampleModel, dataBuffer, rectangle, point, shortInterleavedRaster);
        this.maxX = this.minX + this.width;
        this.maxY = this.minY + this.height;
        if (!(dataBuffer instanceof DataBufferUShort)) {
            throw new RasterFormatException("ShortInterleavedRasters must have ushort DataBuffers");
        }
        final DataBufferUShort dataBufferUShort = (DataBufferUShort)dataBuffer;
        this.data = SunWritableRaster.stealData(dataBufferUShort, 0);
        if (sampleModel instanceof PixelInterleavedSampleModel || (sampleModel instanceof ComponentSampleModel && sampleModel.getNumBands() == 1)) {
            final ComponentSampleModel componentSampleModel = (ComponentSampleModel)sampleModel;
            this.scanlineStride = componentSampleModel.getScanlineStride();
            this.pixelStride = componentSampleModel.getPixelStride();
            this.dataOffsets = componentSampleModel.getBandOffsets();
            final int n = rectangle.x - point.x;
            final int n2 = rectangle.y - point.y;
            for (int i = 0; i < this.getNumDataElements(); ++i) {
                final int[] dataOffsets = this.dataOffsets;
                final int n3 = i;
                dataOffsets[n3] += n * this.pixelStride + n2 * this.scanlineStride;
            }
        }
        else {
            if (!(sampleModel instanceof SinglePixelPackedSampleModel)) {
                throw new RasterFormatException("ShortInterleavedRasters must have PixelInterleavedSampleModel, SinglePixelPackedSampleModel or 1 band ComponentSampleModel.  Sample model is " + sampleModel);
            }
            this.scanlineStride = ((SinglePixelPackedSampleModel)sampleModel).getScanlineStride();
            this.pixelStride = 1;
            (this.dataOffsets = new int[1])[0] = dataBufferUShort.getOffset();
            final int n4 = rectangle.x - point.x;
            final int n5 = rectangle.y - point.y;
            final int[] dataOffsets2 = this.dataOffsets;
            final int n6 = 0;
            dataOffsets2[n6] += n4 + n5 * this.scanlineStride;
        }
        this.bandOffset = this.dataOffsets[0];
        this.verify();
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
    public short[] getDataStorage() {
        return this.data;
    }
    
    @Override
    public Object getDataElements(final int n, final int n2, final Object o) {
        if (n < this.minX || n2 < this.minY || n >= this.maxX || n2 >= this.maxY) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        short[] array;
        if (o == null) {
            array = new short[this.numDataElements];
        }
        else {
            array = (short[])o;
        }
        final int n3 = (n2 - this.minY) * this.scanlineStride + (n - this.minX) * this.pixelStride;
        for (int i = 0; i < this.numDataElements; ++i) {
            array[i] = this.data[this.dataOffsets[i] + n3];
        }
        return array;
    }
    
    @Override
    public Object getDataElements(final int n, final int n2, final int n3, final int n4, final Object o) {
        if (n < this.minX || n2 < this.minY || n + n3 > this.maxX || n2 + n4 > this.maxY) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        short[] array;
        if (o == null) {
            array = new short[n3 * n4 * this.numDataElements];
        }
        else {
            array = (short[])o;
        }
        int n5 = (n2 - this.minY) * this.scanlineStride + (n - this.minX) * this.pixelStride;
        int n6 = 0;
        for (int i = 0; i < n4; ++i, n5 += this.scanlineStride) {
            for (int n7 = n5, j = 0; j < n3; ++j, n7 += this.pixelStride) {
                for (int k = 0; k < this.numDataElements; ++k) {
                    array[n6++] = this.data[this.dataOffsets[k] + n7];
                }
            }
        }
        return array;
    }
    
    @Override
    public short[] getShortData(final int n, final int n2, final int n3, final int n4, final int n5, short[] array) {
        if (n < this.minX || n2 < this.minY || n + n3 > this.maxX || n2 + n4 > this.maxY) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        if (array == null) {
            array = new short[this.numDataElements * n3 * n4];
        }
        int n6 = (n2 - this.minY) * this.scanlineStride + (n - this.minX) * this.pixelStride + this.dataOffsets[n5];
        int n7 = 0;
        if (this.pixelStride == 1) {
            if (this.scanlineStride == n3) {
                System.arraycopy(this.data, n6, array, 0, n3 * n4);
            }
            else {
                for (int i = 0; i < n4; ++i, n6 += this.scanlineStride) {
                    System.arraycopy(this.data, n6, array, n7, n3);
                    n7 += n3;
                }
            }
        }
        else {
            for (int j = 0; j < n4; ++j, n6 += this.scanlineStride) {
                for (int n8 = n6, k = 0; k < n3; ++k, n8 += this.pixelStride) {
                    array[n7++] = this.data[n8];
                }
            }
        }
        return array;
    }
    
    @Override
    public short[] getShortData(final int n, final int n2, final int n3, final int n4, short[] array) {
        if (n < this.minX || n2 < this.minY || n + n3 > this.maxX || n2 + n4 > this.maxY) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        if (array == null) {
            array = new short[this.numDataElements * n3 * n4];
        }
        int n5 = (n2 - this.minY) * this.scanlineStride + (n - this.minX) * this.pixelStride;
        int n6 = 0;
        for (int i = 0; i < n4; ++i, n5 += this.scanlineStride) {
            for (int n7 = n5, j = 0; j < n3; ++j, n7 += this.pixelStride) {
                for (int k = 0; k < this.numDataElements; ++k) {
                    array[n6++] = this.data[this.dataOffsets[k] + n7];
                }
            }
        }
        return array;
    }
    
    @Override
    public void setDataElements(final int n, final int n2, final Object o) {
        if (n < this.minX || n2 < this.minY || n >= this.maxX || n2 >= this.maxY) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        final short[] array = (short[])o;
        final int n3 = (n2 - this.minY) * this.scanlineStride + (n - this.minX) * this.pixelStride;
        for (int i = 0; i < this.numDataElements; ++i) {
            this.data[this.dataOffsets[i] + n3] = array[i];
        }
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
        Object dataElements = null;
        for (int i = 0; i < n4; ++i) {
            dataElements = raster.getDataElements(minX, minY + i, n3, 1, dataElements);
            this.setDataElements(n, n2 + i, n3, 1, dataElements);
        }
    }
    
    @Override
    public void setDataElements(final int n, final int n2, final int n3, final int n4, final Object o) {
        if (n < this.minX || n2 < this.minY || n + n3 > this.maxX || n2 + n4 > this.maxY) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        final short[] array = (short[])o;
        int n5 = (n2 - this.minY) * this.scanlineStride + (n - this.minX) * this.pixelStride;
        int n6 = 0;
        for (int i = 0; i < n4; ++i, n5 += this.scanlineStride) {
            for (int n7 = n5, j = 0; j < n3; ++j, n7 += this.pixelStride) {
                for (int k = 0; k < this.numDataElements; ++k) {
                    this.data[this.dataOffsets[k] + n7] = array[n6++];
                }
            }
        }
        this.markDirty();
    }
    
    @Override
    public void putShortData(final int n, final int n2, final int n3, final int n4, final int n5, final short[] array) {
        if (n < this.minX || n2 < this.minY || n + n3 > this.maxX || n2 + n4 > this.maxY) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        int n6 = (n2 - this.minY) * this.scanlineStride + (n - this.minX) * this.pixelStride + this.dataOffsets[n5];
        int n7 = 0;
        if (this.pixelStride == 1) {
            if (this.scanlineStride == n3) {
                System.arraycopy(array, 0, this.data, n6, n3 * n4);
            }
            else {
                for (int i = 0; i < n4; ++i, n6 += this.scanlineStride) {
                    System.arraycopy(array, n7, this.data, n6, n3);
                    n7 += n3;
                }
            }
        }
        else {
            for (int j = 0; j < n4; ++j, n6 += this.scanlineStride) {
                for (int n8 = n6, k = 0; k < n3; ++k, n8 += this.pixelStride) {
                    this.data[n8] = array[n7++];
                }
            }
        }
        this.markDirty();
    }
    
    @Override
    public void putShortData(final int n, final int n2, final int n3, final int n4, final short[] array) {
        if (n < this.minX || n2 < this.minY || n + n3 > this.maxX || n2 + n4 > this.maxY) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        int n5 = (n2 - this.minY) * this.scanlineStride + (n - this.minX) * this.pixelStride;
        int n6 = 0;
        for (int i = 0; i < n4; ++i, n5 += this.scanlineStride) {
            for (int n7 = n5, j = 0; j < n3; ++j, n7 += this.pixelStride) {
                for (int k = 0; k < this.numDataElements; ++k) {
                    this.data[this.dataOffsets[k] + n7] = array[n6++];
                }
            }
        }
        this.markDirty();
    }
    
    @Override
    public Raster createChild(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int[] array) {
        return this.createWritableChild(n, n2, n3, n4, n5, n6, array);
    }
    
    @Override
    public WritableRaster createWritableChild(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int[] array) {
        if (n < this.minX) {
            throw new RasterFormatException("x lies outside the raster");
        }
        if (n2 < this.minY) {
            throw new RasterFormatException("y lies outside the raster");
        }
        if (n + n3 < n || n + n3 > this.minX + this.width) {
            throw new RasterFormatException("(x + width) is outside of Raster");
        }
        if (n2 + n4 < n2 || n2 + n4 > this.minY + this.height) {
            throw new RasterFormatException("(y + height) is outside of Raster");
        }
        SampleModel sampleModel;
        if (array != null) {
            sampleModel = this.sampleModel.createSubsetSampleModel(array);
        }
        else {
            sampleModel = this.sampleModel;
        }
        return new ShortInterleavedRaster(sampleModel, this.dataBuffer, new Rectangle(n5, n6, n3, n4), new Point(this.sampleModelTranslateX + (n5 - n), this.sampleModelTranslateY + (n6 - n2)), this);
    }
    
    @Override
    public WritableRaster createCompatibleWritableRaster(final int n, final int n2) {
        if (n <= 0 || n2 <= 0) {
            throw new RasterFormatException("negative " + ((n <= 0) ? "width" : "height"));
        }
        return new ShortInterleavedRaster(this.sampleModel.createCompatibleSampleModel(n, n2), new Point(0, 0));
    }
    
    @Override
    public WritableRaster createCompatibleWritableRaster() {
        return this.createCompatibleWritableRaster(this.width, this.height);
    }
    
    @Override
    public String toString() {
        return new String("ShortInterleavedRaster: width = " + this.width + " height = " + this.height + " #numDataElements " + this.numDataElements);
    }
}
