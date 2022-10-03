package sun.awt.image;

import java.awt.image.Raster;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.RasterFormatException;
import java.awt.image.DataBufferUShort;
import java.awt.image.WritableRaster;
import java.awt.image.DataBuffer;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.image.SampleModel;

public class ShortComponentRaster extends SunWritableRaster
{
    protected int bandOffset;
    protected int[] dataOffsets;
    protected int scanlineStride;
    protected int pixelStride;
    protected short[] data;
    int type;
    private int maxX;
    private int maxY;
    
    private static native void initIDs();
    
    public ShortComponentRaster(final SampleModel sampleModel, final Point point) {
        this(sampleModel, sampleModel.createDataBuffer(), new Rectangle(point.x, point.y, sampleModel.getWidth(), sampleModel.getHeight()), point, null);
    }
    
    public ShortComponentRaster(final SampleModel sampleModel, final DataBuffer dataBuffer, final Point point) {
        this(sampleModel, dataBuffer, new Rectangle(point.x, point.y, sampleModel.getWidth(), sampleModel.getHeight()), point, null);
    }
    
    public ShortComponentRaster(final SampleModel sampleModel, final DataBuffer dataBuffer, final Rectangle rectangle, final Point point, final ShortComponentRaster shortComponentRaster) {
        super(sampleModel, dataBuffer, rectangle, point, shortComponentRaster);
        this.maxX = this.minX + this.width;
        this.maxY = this.minY + this.height;
        if (!(dataBuffer instanceof DataBufferUShort)) {
            throw new RasterFormatException("ShortComponentRasters must have short DataBuffers");
        }
        final DataBufferUShort dataBufferUShort = (DataBufferUShort)dataBuffer;
        this.data = SunWritableRaster.stealData(dataBufferUShort, 0);
        if (dataBufferUShort.getNumBanks() != 1) {
            throw new RasterFormatException("DataBuffer for ShortComponentRasters must only have 1 bank.");
        }
        final int offset = dataBufferUShort.getOffset();
        if (sampleModel instanceof ComponentSampleModel) {
            final ComponentSampleModel componentSampleModel = (ComponentSampleModel)sampleModel;
            this.type = 2;
            this.scanlineStride = componentSampleModel.getScanlineStride();
            this.pixelStride = componentSampleModel.getPixelStride();
            this.dataOffsets = componentSampleModel.getBandOffsets();
            final int n = rectangle.x - point.x;
            final int n2 = rectangle.y - point.y;
            for (int i = 0; i < this.getNumDataElements(); ++i) {
                final int[] dataOffsets = this.dataOffsets;
                final int n3 = i;
                dataOffsets[n3] += offset + n * this.pixelStride + n2 * this.scanlineStride;
            }
        }
        else {
            if (!(sampleModel instanceof SinglePixelPackedSampleModel)) {
                throw new RasterFormatException("ShortComponentRasters must haveComponentSampleModel or SinglePixelPackedSampleModel");
            }
            final SinglePixelPackedSampleModel singlePixelPackedSampleModel = (SinglePixelPackedSampleModel)sampleModel;
            this.type = 8;
            this.scanlineStride = singlePixelPackedSampleModel.getScanlineStride();
            this.pixelStride = 1;
            (this.dataOffsets = new int[1])[0] = offset;
            final int n4 = rectangle.x - point.x;
            final int n5 = rectangle.y - point.y;
            final int[] dataOffsets2 = this.dataOffsets;
            final int n6 = 0;
            dataOffsets2[n6] += n4 + n5 * this.scanlineStride;
        }
        this.bandOffset = this.dataOffsets[0];
        this.verify();
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
        return this.pixelStride;
    }
    
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
        return new ShortComponentRaster(sampleModel, this.dataBuffer, new Rectangle(n5, n6, n3, n4), new Point(this.sampleModelTranslateX + (n5 - n), this.sampleModelTranslateY + (n6 - n2)), this);
    }
    
    @Override
    public WritableRaster createCompatibleWritableRaster(final int n, final int n2) {
        if (n <= 0 || n2 <= 0) {
            throw new RasterFormatException("negative " + ((n <= 0) ? "width" : "height"));
        }
        return new ShortComponentRaster(this.sampleModel.createCompatibleSampleModel(n, n2), new Point(0, 0));
    }
    
    @Override
    public WritableRaster createCompatibleWritableRaster() {
        return this.createCompatibleWritableRaster(this.width, this.height);
    }
    
    protected final void verify() {
        if (this.width <= 0 || this.height <= 0 || this.height > Integer.MAX_VALUE / this.width) {
            throw new RasterFormatException("Invalid raster dimension");
        }
        for (int i = 0; i < this.dataOffsets.length; ++i) {
            if (this.dataOffsets[i] < 0) {
                throw new RasterFormatException("Data offsets for band " + i + "(" + this.dataOffsets[i] + ") must be >= 0");
            }
        }
        if (this.minX - (long)this.sampleModelTranslateX < 0L || this.minY - (long)this.sampleModelTranslateY < 0L) {
            throw new RasterFormatException("Incorrect origin/translate: (" + this.minX + ", " + this.minY + ") / (" + this.sampleModelTranslateX + ", " + this.sampleModelTranslateY + ")");
        }
        if (this.scanlineStride < 0 || this.scanlineStride > Integer.MAX_VALUE / this.height) {
            throw new RasterFormatException("Incorrect scanline stride: " + this.scanlineStride);
        }
        if ((this.height > 1 || this.minY - this.sampleModelTranslateY > 0) && this.scanlineStride > this.data.length) {
            throw new RasterFormatException("Incorrect scanline stride: " + this.scanlineStride);
        }
        final int n = (this.height - 1) * this.scanlineStride;
        if (this.pixelStride < 0 || this.pixelStride > Integer.MAX_VALUE / this.width || this.pixelStride > this.data.length) {
            throw new RasterFormatException("Incorrect pixel stride: " + this.pixelStride);
        }
        final int n2 = (this.width - 1) * this.pixelStride;
        if (n2 > Integer.MAX_VALUE - n) {
            throw new RasterFormatException("Incorrect raster attributes");
        }
        final int n3 = n2 + n;
        int n4 = 0;
        for (int j = 0; j < this.numDataElements; ++j) {
            if (this.dataOffsets[j] > Integer.MAX_VALUE - n3) {
                throw new RasterFormatException("Incorrect band offset: " + this.dataOffsets[j]);
            }
            final int n5 = n3 + this.dataOffsets[j];
            if (n5 > n4) {
                n4 = n5;
            }
        }
        if (this.data.length <= n4) {
            throw new RasterFormatException("Data array too small (should be > " + n4 + " )");
        }
    }
    
    @Override
    public String toString() {
        return new String("ShortComponentRaster: width = " + this.width + " height = " + this.height + " #numDataElements " + this.numDataElements);
    }
    
    static {
        NativeLibLoader.loadLibraries();
        initIDs();
    }
}
