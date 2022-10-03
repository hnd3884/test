package sun.awt.image;

import java.awt.image.Raster;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.RasterFormatException;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.awt.image.DataBuffer;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.image.SampleModel;

public class IntegerComponentRaster extends SunWritableRaster
{
    static final int TYPE_CUSTOM = 0;
    static final int TYPE_BYTE_SAMPLES = 1;
    static final int TYPE_USHORT_SAMPLES = 2;
    static final int TYPE_INT_SAMPLES = 3;
    static final int TYPE_BYTE_BANDED_SAMPLES = 4;
    static final int TYPE_USHORT_BANDED_SAMPLES = 5;
    static final int TYPE_INT_BANDED_SAMPLES = 6;
    static final int TYPE_BYTE_PACKED_SAMPLES = 7;
    static final int TYPE_USHORT_PACKED_SAMPLES = 8;
    static final int TYPE_INT_PACKED_SAMPLES = 9;
    static final int TYPE_INT_8BIT_SAMPLES = 10;
    static final int TYPE_BYTE_BINARY_SAMPLES = 11;
    protected int bandOffset;
    protected int[] dataOffsets;
    protected int scanlineStride;
    protected int pixelStride;
    protected int[] data;
    protected int numDataElems;
    int type;
    private int maxX;
    private int maxY;
    
    private static native void initIDs();
    
    public IntegerComponentRaster(final SampleModel sampleModel, final Point point) {
        this(sampleModel, sampleModel.createDataBuffer(), new Rectangle(point.x, point.y, sampleModel.getWidth(), sampleModel.getHeight()), point, null);
    }
    
    public IntegerComponentRaster(final SampleModel sampleModel, final DataBuffer dataBuffer, final Point point) {
        this(sampleModel, dataBuffer, new Rectangle(point.x, point.y, sampleModel.getWidth(), sampleModel.getHeight()), point, null);
    }
    
    public IntegerComponentRaster(final SampleModel sampleModel, final DataBuffer dataBuffer, final Rectangle rectangle, final Point point, final IntegerComponentRaster integerComponentRaster) {
        super(sampleModel, dataBuffer, rectangle, point, integerComponentRaster);
        this.maxX = this.minX + this.width;
        this.maxY = this.minY + this.height;
        if (!(dataBuffer instanceof DataBufferInt)) {
            throw new RasterFormatException("IntegerComponentRasters must haveinteger DataBuffers");
        }
        final DataBufferInt dataBufferInt = (DataBufferInt)dataBuffer;
        if (dataBufferInt.getNumBanks() != 1) {
            throw new RasterFormatException("DataBuffer for IntegerComponentRasters must only have 1 bank.");
        }
        this.data = SunWritableRaster.stealData(dataBufferInt, 0);
        if (sampleModel instanceof SinglePixelPackedSampleModel) {
            final SinglePixelPackedSampleModel singlePixelPackedSampleModel = (SinglePixelPackedSampleModel)sampleModel;
            final int[] bitOffsets = singlePixelPackedSampleModel.getBitOffsets();
            boolean b = false;
            for (int i = 1; i < bitOffsets.length; ++i) {
                if (bitOffsets[i] % 8 != 0) {
                    b = true;
                }
            }
            this.type = (b ? 9 : 10);
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
        throw new RasterFormatException("IntegerComponentRasters must have SinglePixelPackedSampleModel");
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
            array = new int[this.numDataElements];
        }
        else {
            array = (int[])o;
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
        int[] array;
        if (o instanceof int[]) {
            array = (int[])o;
        }
        else {
            array = new int[this.numDataElements * n3 * n4];
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
        final int[] array = (int[])o;
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
    
    private void setDataElements(final int n, final int n2, int n3, final int n4, final Raster raster) {
        if (n3 <= 0 || n4 <= 0) {
            return;
        }
        final int minX = raster.getMinX();
        final int minY = raster.getMinY();
        if (raster instanceof IntegerComponentRaster && this.pixelStride == 1 && this.numDataElements == 1) {
            final IntegerComponentRaster integerComponentRaster = (IntegerComponentRaster)raster;
            if (integerComponentRaster.getNumDataElements() != 1) {
                throw new ArrayIndexOutOfBoundsException("Number of bands does not match");
            }
            final int[] dataStorage = integerComponentRaster.getDataStorage();
            final int scanlineStride = integerComponentRaster.getScanlineStride();
            int dataOffset = integerComponentRaster.getDataOffset(0);
            int n5 = this.dataOffsets[0] + (n2 - this.minY) * this.scanlineStride + (n - this.minX);
            if (integerComponentRaster.getPixelStride() == this.pixelStride) {
                n3 *= this.pixelStride;
                for (int i = 0; i < n4; ++i) {
                    System.arraycopy(dataStorage, dataOffset, this.data, n5, n3);
                    dataOffset += scanlineStride;
                    n5 += this.scanlineStride;
                }
                this.markDirty();
                return;
            }
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
        return new IntegerComponentRaster(sampleModel, this.dataBuffer, new Rectangle(n5, n6, n3, n4), new Point(this.sampleModelTranslateX + (n5 - n), this.sampleModelTranslateY + (n6 - n2)), this);
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
        return new IntegerComponentRaster(this.sampleModel.createCompatibleSampleModel(n, n2), new Point(0, 0));
    }
    
    @Override
    public WritableRaster createCompatibleWritableRaster() {
        return this.createCompatibleWritableRaster(this.width, this.height);
    }
    
    protected final void verify() {
        if (this.width <= 0 || this.height <= 0 || this.height > Integer.MAX_VALUE / this.width) {
            throw new RasterFormatException("Invalid raster dimension");
        }
        if (this.dataOffsets[0] < 0) {
            throw new RasterFormatException("Data offset (" + this.dataOffsets[0] + ") must be >= 0");
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
        for (int i = 0; i < this.numDataElements; ++i) {
            if (this.dataOffsets[i] > Integer.MAX_VALUE - n3) {
                throw new RasterFormatException("Incorrect band offset: " + this.dataOffsets[i]);
            }
            final int n5 = n3 + this.dataOffsets[i];
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
        return new String("IntegerComponentRaster: width = " + this.width + " height = " + this.height + " #Bands = " + this.numBands + " #DataElements " + this.numDataElements + " xOff = " + this.sampleModelTranslateX + " yOff = " + this.sampleModelTranslateY + " dataOffset[0] " + this.dataOffsets[0]);
    }
    
    static {
        NativeLibLoader.loadLibraries();
        initIDs();
    }
}
