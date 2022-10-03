package sun.awt.image;

import java.awt.image.Raster;
import java.awt.image.BandedSampleModel;
import java.awt.image.RasterFormatException;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.awt.image.DataBuffer;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.image.SampleModel;

public class ByteBandedRaster extends SunWritableRaster
{
    int[] dataOffsets;
    int scanlineStride;
    byte[][] data;
    private int maxX;
    private int maxY;
    
    public ByteBandedRaster(final SampleModel sampleModel, final Point point) {
        this(sampleModel, sampleModel.createDataBuffer(), new Rectangle(point.x, point.y, sampleModel.getWidth(), sampleModel.getHeight()), point, null);
    }
    
    public ByteBandedRaster(final SampleModel sampleModel, final DataBuffer dataBuffer, final Point point) {
        this(sampleModel, dataBuffer, new Rectangle(point.x, point.y, sampleModel.getWidth(), sampleModel.getHeight()), point, null);
    }
    
    public ByteBandedRaster(final SampleModel sampleModel, final DataBuffer dataBuffer, final Rectangle rectangle, final Point point, final ByteBandedRaster byteBandedRaster) {
        super(sampleModel, dataBuffer, rectangle, point, byteBandedRaster);
        this.maxX = this.minX + this.width;
        this.maxY = this.minY + this.height;
        if (!(dataBuffer instanceof DataBufferByte)) {
            throw new RasterFormatException("ByteBandedRaster must havebyte DataBuffers");
        }
        final DataBufferByte dataBufferByte = (DataBufferByte)dataBuffer;
        if (sampleModel instanceof BandedSampleModel) {
            final BandedSampleModel bandedSampleModel = (BandedSampleModel)sampleModel;
            this.scanlineStride = bandedSampleModel.getScanlineStride();
            final int[] bankIndices = bandedSampleModel.getBankIndices();
            final int[] bandOffsets = bandedSampleModel.getBandOffsets();
            final int[] offsets = dataBufferByte.getOffsets();
            this.dataOffsets = new int[bankIndices.length];
            this.data = new byte[bankIndices.length][];
            final int n = rectangle.x - point.x;
            final int n2 = rectangle.y - point.y;
            for (int i = 0; i < bankIndices.length; ++i) {
                this.data[i] = SunWritableRaster.stealData(dataBufferByte, bankIndices[i]);
                this.dataOffsets[i] = offsets[bankIndices[i]] + n + n2 * this.scanlineStride + bandOffsets[i];
            }
            this.verify();
            return;
        }
        throw new RasterFormatException("ByteBandedRasters must haveBandedSampleModels");
    }
    
    public int[] getDataOffsets() {
        return this.dataOffsets.clone();
    }
    
    public int getDataOffset(final int n) {
        return this.dataOffsets[n];
    }
    
    public int getScanlineStride() {
        return this.scanlineStride;
    }
    
    public int getPixelStride() {
        return 1;
    }
    
    public byte[][] getDataStorage() {
        return this.data;
    }
    
    public byte[] getDataStorage(final int n) {
        return this.data[n];
    }
    
    @Override
    public Object getDataElements(final int n, final int n2, final Object o) {
        if (n < this.minX || n2 < this.minY || n >= this.maxX || n2 >= this.maxY) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        byte[] array;
        if (o == null) {
            array = new byte[this.numDataElements];
        }
        else {
            array = (byte[])o;
        }
        final int n3 = (n2 - this.minY) * this.scanlineStride + (n - this.minX);
        for (int i = 0; i < this.numDataElements; ++i) {
            array[i] = this.data[i][this.dataOffsets[i] + n3];
        }
        return array;
    }
    
    @Override
    public Object getDataElements(final int n, final int n2, final int n3, final int n4, final Object o) {
        if (n < this.minX || n2 < this.minY || n + n3 > this.maxX || n2 + n4 > this.maxY) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        byte[] array;
        if (o == null) {
            array = new byte[this.numDataElements * n3 * n4];
        }
        else {
            array = (byte[])o;
        }
        final int n5 = (n2 - this.minY) * this.scanlineStride + (n - this.minX);
        for (int i = 0; i < this.numDataElements; ++i) {
            int n6 = i;
            final byte[] array2 = this.data[i];
            final int n7 = this.dataOffsets[i];
            for (int n8 = n5, j = 0; j < n4; ++j, n8 += this.scanlineStride) {
                int n9 = n7 + n8;
                for (int k = 0; k < n3; ++k) {
                    array[n6] = array2[n9++];
                    n6 += this.numDataElements;
                }
            }
        }
        return array;
    }
    
    public byte[] getByteData(final int n, final int n2, final int n3, final int n4, final int n5, byte[] array) {
        if (n < this.minX || n2 < this.minY || n + n3 > this.maxX || n2 + n4 > this.maxY) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        if (array == null) {
            array = new byte[this.scanlineStride * n4];
        }
        int n6 = (n2 - this.minY) * this.scanlineStride + (n - this.minX) + this.dataOffsets[n5];
        if (this.scanlineStride == n3) {
            System.arraycopy(this.data[n5], n6, array, 0, n3 * n4);
        }
        else {
            int n7 = 0;
            for (int i = 0; i < n4; ++i, n6 += this.scanlineStride) {
                System.arraycopy(this.data[n5], n6, array, n7, n3);
                n7 += n3;
            }
        }
        return array;
    }
    
    public byte[] getByteData(final int n, final int n2, final int n3, final int n4, byte[] array) {
        if (n < this.minX || n2 < this.minY || n + n3 > this.maxX || n2 + n4 > this.maxY) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        if (array == null) {
            array = new byte[this.numDataElements * this.scanlineStride * n4];
        }
        final int n5 = (n2 - this.minY) * this.scanlineStride + (n - this.minX);
        for (int i = 0; i < this.numDataElements; ++i) {
            int n6 = i;
            final byte[] array2 = this.data[i];
            final int n7 = this.dataOffsets[i];
            for (int n8 = n5, j = 0; j < n4; ++j, n8 += this.scanlineStride) {
                int n9 = n7 + n8;
                for (int k = 0; k < n3; ++k) {
                    array[n6] = array2[n9++];
                    n6 += this.numDataElements;
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
        final byte[] array = (byte[])o;
        final int n3 = (n2 - this.minY) * this.scanlineStride + (n - this.minX);
        for (int i = 0; i < this.numDataElements; ++i) {
            this.data[i][this.dataOffsets[i] + n3] = array[i];
        }
        this.markDirty();
    }
    
    @Override
    public void setDataElements(final int n, final int n2, final Raster raster) {
        final int n3 = raster.getMinX() + n;
        final int n4 = raster.getMinY() + n2;
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
        final byte[] array = (byte[])o;
        final int n5 = (n2 - this.minY) * this.scanlineStride + (n - this.minX);
        for (int i = 0; i < this.numDataElements; ++i) {
            int n6 = i;
            final byte[] array2 = this.data[i];
            final int n7 = this.dataOffsets[i];
            for (int n8 = n5, j = 0; j < n4; ++j, n8 += this.scanlineStride) {
                int n9 = n7 + n8;
                for (int k = 0; k < n3; ++k) {
                    array2[n9++] = array[n6];
                    n6 += this.numDataElements;
                }
            }
        }
        this.markDirty();
    }
    
    public void putByteData(final int n, final int n2, final int n3, final int n4, final int n5, final byte[] array) {
        if (n < this.minX || n2 < this.minY || n + n3 > this.maxX || n2 + n4 > this.maxY) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        int n6 = (n2 - this.minY) * this.scanlineStride + (n - this.minX) + this.dataOffsets[n5];
        int n7 = 0;
        if (this.scanlineStride == n3) {
            System.arraycopy(array, 0, this.data[n5], n6, n3 * n4);
        }
        else {
            for (int i = 0; i < n4; ++i, n6 += this.scanlineStride) {
                System.arraycopy(array, n7, this.data[n5], n6, n3);
                n7 += n3;
            }
        }
        this.markDirty();
    }
    
    public void putByteData(final int n, final int n2, final int n3, final int n4, final byte[] array) {
        if (n < this.minX || n2 < this.minY || n + n3 > this.maxX || n2 + n4 > this.maxY) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        final int n5 = (n2 - this.minY) * this.scanlineStride + (n - this.minX);
        for (int i = 0; i < this.numDataElements; ++i) {
            int n6 = i;
            final byte[] array2 = this.data[i];
            final int n7 = this.dataOffsets[i];
            for (int n8 = n5, j = 0; j < n4; ++j, n8 += this.scanlineStride) {
                int n9 = n7 + n8;
                for (int k = 0; k < n3; ++k) {
                    array2[n9++] = array[n6];
                    n6 += this.numDataElements;
                }
            }
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
        if (n + n3 < n || n + n3 > this.width + this.minX) {
            throw new RasterFormatException("(x + width) is outside raster");
        }
        if (n2 + n4 < n2 || n2 + n4 > this.height + this.minY) {
            throw new RasterFormatException("(y + height) is outside raster");
        }
        SampleModel sampleModel;
        if (array != null) {
            sampleModel = this.sampleModel.createSubsetSampleModel(array);
        }
        else {
            sampleModel = this.sampleModel;
        }
        return new ByteBandedRaster(sampleModel, this.dataBuffer, new Rectangle(n5, n6, n3, n4), new Point(this.sampleModelTranslateX + (n5 - n), this.sampleModelTranslateY + (n6 - n2)), this);
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
        return new ByteBandedRaster(this.sampleModel.createCompatibleSampleModel(n, n2), new Point(0, 0));
    }
    
    @Override
    public WritableRaster createCompatibleWritableRaster() {
        return this.createCompatibleWritableRaster(this.width, this.height);
    }
    
    private void verify() {
        if (this.width <= 0 || this.height <= 0 || this.height > Integer.MAX_VALUE / this.width) {
            throw new RasterFormatException("Invalid raster dimension");
        }
        if (this.scanlineStride < 0 || this.scanlineStride > Integer.MAX_VALUE / this.height) {
            throw new RasterFormatException("Incorrect scanline stride: " + this.scanlineStride);
        }
        if (this.minX - (long)this.sampleModelTranslateX < 0L || this.minY - (long)this.sampleModelTranslateY < 0L) {
            throw new RasterFormatException("Incorrect origin/translate: (" + this.minX + ", " + this.minY + ") / (" + this.sampleModelTranslateX + ", " + this.sampleModelTranslateY + ")");
        }
        if (this.height > 1 || this.minY - this.sampleModelTranslateY > 0) {
            for (int i = 0; i < this.data.length; ++i) {
                if (this.scanlineStride > this.data[i].length) {
                    throw new RasterFormatException("Incorrect scanline stride: " + this.scanlineStride);
                }
            }
        }
        for (int j = 0; j < this.dataOffsets.length; ++j) {
            if (this.dataOffsets[j] < 0) {
                throw new RasterFormatException("Data offsets for band " + j + "(" + this.dataOffsets[j] + ") must be >= 0");
            }
        }
        final int n = (this.height - 1) * this.scanlineStride;
        if (this.width - 1 > Integer.MAX_VALUE - n) {
            throw new RasterFormatException("Invalid raster dimension");
        }
        final int n2 = n + (this.width - 1);
        int n3 = 0;
        for (int k = 0; k < this.numDataElements; ++k) {
            if (this.dataOffsets[k] > Integer.MAX_VALUE - n2) {
                throw new RasterFormatException("Invalid raster dimension");
            }
            final int n4 = n2 + this.dataOffsets[k];
            if (n4 > n3) {
                n3 = n4;
            }
        }
        if (this.data.length == 1) {
            if (this.data[0].length <= n3 * this.numDataElements) {
                throw new RasterFormatException("Data array too small (it is " + this.data[0].length + " and should be > " + n3 * this.numDataElements + " )");
            }
        }
        else {
            for (int l = 0; l < this.numDataElements; ++l) {
                if (this.data[l].length <= n3) {
                    throw new RasterFormatException("Data array too small (it is " + this.data[l].length + " and should be > " + n3 + " )");
                }
            }
        }
    }
    
    @Override
    public String toString() {
        return new String("ByteBandedRaster: width = " + this.width + " height = " + this.height + " #bands " + this.numDataElements + " minX = " + this.minX + " minY = " + this.minY);
    }
}
