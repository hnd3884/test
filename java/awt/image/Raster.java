package java.awt.image;

import java.awt.Rectangle;
import sun.awt.image.BytePackedRaster;
import sun.awt.image.IntegerInterleavedRaster;
import sun.awt.image.SunWritableRaster;
import sun.awt.image.ShortBandedRaster;
import sun.awt.image.ByteBandedRaster;
import sun.awt.image.ShortInterleavedRaster;
import sun.awt.image.ByteInterleavedRaster;
import java.awt.Point;

public class Raster
{
    protected SampleModel sampleModel;
    protected DataBuffer dataBuffer;
    protected int minX;
    protected int minY;
    protected int width;
    protected int height;
    protected int sampleModelTranslateX;
    protected int sampleModelTranslateY;
    protected int numBands;
    protected int numDataElements;
    protected Raster parent;
    
    private static native void initIDs();
    
    public static WritableRaster createInterleavedRaster(final int n, final int n2, final int n3, final int n4, final Point point) {
        final int[] array = new int[n4];
        for (int i = 0; i < n4; ++i) {
            array[i] = i;
        }
        return createInterleavedRaster(n, n2, n3, n2 * n4, n4, array, point);
    }
    
    public static WritableRaster createInterleavedRaster(final int n, final int n2, final int n3, final int n4, final int n5, final int[] array, final Point point) {
        final int n6 = n4 * (n3 - 1) + n5 * n2;
        DataBuffer dataBuffer = null;
        switch (n) {
            case 0: {
                dataBuffer = new DataBufferByte(n6);
                break;
            }
            case 1: {
                dataBuffer = new DataBufferUShort(n6);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported data type " + n);
            }
        }
        return createInterleavedRaster(dataBuffer, n2, n3, n4, n5, array, point);
    }
    
    public static WritableRaster createBandedRaster(final int n, final int n2, final int n3, final int n4, final Point point) {
        if (n4 < 1) {
            throw new ArrayIndexOutOfBoundsException("Number of bands (" + n4 + ") must be greater than 0");
        }
        final int[] array = new int[n4];
        final int[] array2 = new int[n4];
        for (int i = 0; i < n4; ++i) {
            array2[array[i] = i] = 0;
        }
        return createBandedRaster(n, n2, n3, n2, array, array2, point);
    }
    
    public static WritableRaster createBandedRaster(final int n, final int n2, final int n3, final int n4, final int[] array, final int[] array2, final Point point) {
        final int length = array2.length;
        if (array == null) {
            throw new ArrayIndexOutOfBoundsException("Bank indices array is null");
        }
        if (array2 == null) {
            throw new ArrayIndexOutOfBoundsException("Band offsets array is null");
        }
        int n5 = array[0];
        int n6 = array2[0];
        for (int i = 1; i < length; ++i) {
            if (array[i] > n5) {
                n5 = array[i];
            }
            if (array2[i] > n6) {
                n6 = array2[i];
            }
        }
        final int n7 = n5 + 1;
        final int n8 = n6 + n4 * (n3 - 1) + n2;
        DataBuffer dataBuffer = null;
        switch (n) {
            case 0: {
                dataBuffer = new DataBufferByte(n8, n7);
                break;
            }
            case 1: {
                dataBuffer = new DataBufferUShort(n8, n7);
                break;
            }
            case 3: {
                dataBuffer = new DataBufferInt(n8, n7);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported data type " + n);
            }
        }
        return createBandedRaster(dataBuffer, n2, n3, n4, array, array2, point);
    }
    
    public static WritableRaster createPackedRaster(final int n, final int n2, final int n3, final int[] array, final Point point) {
        DataBuffer dataBuffer = null;
        switch (n) {
            case 0: {
                dataBuffer = new DataBufferByte(n2 * n3);
                break;
            }
            case 1: {
                dataBuffer = new DataBufferUShort(n2 * n3);
                break;
            }
            case 3: {
                dataBuffer = new DataBufferInt(n2 * n3);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported data type " + n);
            }
        }
        return createPackedRaster(dataBuffer, n2, n3, n2, array, point);
    }
    
    public static WritableRaster createPackedRaster(final int n, final int n2, final int n3, final int n4, final int n5, final Point point) {
        if (n4 <= 0) {
            throw new IllegalArgumentException("Number of bands (" + n4 + ") must be greater than 0");
        }
        if (n5 <= 0) {
            throw new IllegalArgumentException("Bits per band (" + n5 + ") must be greater than 0");
        }
        if (n4 == 1) {
            final double n6 = n2;
            DataBuffer dataBuffer = null;
            switch (n) {
                case 0: {
                    dataBuffer = new DataBufferByte((int)Math.ceil(n6 / (8 / n5)) * n3);
                    break;
                }
                case 1: {
                    dataBuffer = new DataBufferUShort((int)Math.ceil(n6 / (16 / n5)) * n3);
                    break;
                }
                case 3: {
                    dataBuffer = new DataBufferInt((int)Math.ceil(n6 / (32 / n5)) * n3);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Unsupported data type " + n);
                }
            }
            return createPackedRaster(dataBuffer, n2, n3, n5, point);
        }
        final int[] array = new int[n4];
        final int n7 = (1 << n5) - 1;
        int n8 = (n4 - 1) * n5;
        if (n8 + n5 > DataBuffer.getDataTypeSize(n)) {
            throw new IllegalArgumentException("bitsPerBand(" + n5 + ") * bands is  greater than data type size.");
        }
        switch (n) {
            case 0:
            case 1:
            case 3: {
                for (int i = 0; i < n4; ++i) {
                    array[i] = n7 << n8;
                    n8 -= n5;
                }
                return createPackedRaster(n, n2, n3, array, point);
            }
            default: {
                throw new IllegalArgumentException("Unsupported data type " + n);
            }
        }
    }
    
    public static WritableRaster createInterleavedRaster(final DataBuffer dataBuffer, final int n, final int n2, final int n3, final int n4, final int[] array, Point point) {
        if (dataBuffer == null) {
            throw new NullPointerException("DataBuffer cannot be null");
        }
        if (point == null) {
            point = new Point(0, 0);
        }
        final int dataType = dataBuffer.getDataType();
        final PixelInterleavedSampleModel pixelInterleavedSampleModel = new PixelInterleavedSampleModel(dataType, n, n2, n4, n3, array);
        switch (dataType) {
            case 0: {
                return new ByteInterleavedRaster(pixelInterleavedSampleModel, dataBuffer, point);
            }
            case 1: {
                return new ShortInterleavedRaster(pixelInterleavedSampleModel, dataBuffer, point);
            }
            default: {
                throw new IllegalArgumentException("Unsupported data type " + dataType);
            }
        }
    }
    
    public static WritableRaster createBandedRaster(final DataBuffer dataBuffer, final int n, final int n2, final int n3, final int[] array, final int[] array2, Point point) {
        if (dataBuffer == null) {
            throw new NullPointerException("DataBuffer cannot be null");
        }
        if (point == null) {
            point = new Point(0, 0);
        }
        final int dataType = dataBuffer.getDataType();
        if (array2.length != array.length) {
            throw new IllegalArgumentException("bankIndices.length != bandOffsets.length");
        }
        final BandedSampleModel bandedSampleModel = new BandedSampleModel(dataType, n, n2, n3, array, array2);
        switch (dataType) {
            case 0: {
                return new ByteBandedRaster(bandedSampleModel, dataBuffer, point);
            }
            case 1: {
                return new ShortBandedRaster(bandedSampleModel, dataBuffer, point);
            }
            case 3: {
                return new SunWritableRaster(bandedSampleModel, dataBuffer, point);
            }
            default: {
                throw new IllegalArgumentException("Unsupported data type " + dataType);
            }
        }
    }
    
    public static WritableRaster createPackedRaster(final DataBuffer dataBuffer, final int n, final int n2, final int n3, final int[] array, Point point) {
        if (dataBuffer == null) {
            throw new NullPointerException("DataBuffer cannot be null");
        }
        if (point == null) {
            point = new Point(0, 0);
        }
        final int dataType = dataBuffer.getDataType();
        final SinglePixelPackedSampleModel singlePixelPackedSampleModel = new SinglePixelPackedSampleModel(dataType, n, n2, n3, array);
        switch (dataType) {
            case 0: {
                return new ByteInterleavedRaster(singlePixelPackedSampleModel, dataBuffer, point);
            }
            case 1: {
                return new ShortInterleavedRaster(singlePixelPackedSampleModel, dataBuffer, point);
            }
            case 3: {
                return new IntegerInterleavedRaster(singlePixelPackedSampleModel, dataBuffer, point);
            }
            default: {
                throw new IllegalArgumentException("Unsupported data type " + dataType);
            }
        }
    }
    
    public static WritableRaster createPackedRaster(final DataBuffer dataBuffer, final int n, final int n2, final int n3, Point point) {
        if (dataBuffer == null) {
            throw new NullPointerException("DataBuffer cannot be null");
        }
        if (point == null) {
            point = new Point(0, 0);
        }
        final int dataType = dataBuffer.getDataType();
        if (dataType != 0 && dataType != 1 && dataType != 3) {
            throw new IllegalArgumentException("Unsupported data type " + dataType);
        }
        if (dataBuffer.getNumBanks() != 1) {
            throw new RasterFormatException("DataBuffer for packed Rasters must only have 1 bank.");
        }
        final MultiPixelPackedSampleModel multiPixelPackedSampleModel = new MultiPixelPackedSampleModel(dataType, n, n2, n3);
        if (dataType == 0 && (n3 == 1 || n3 == 2 || n3 == 4)) {
            return new BytePackedRaster(multiPixelPackedSampleModel, dataBuffer, point);
        }
        return new SunWritableRaster(multiPixelPackedSampleModel, dataBuffer, point);
    }
    
    public static Raster createRaster(final SampleModel sampleModel, final DataBuffer dataBuffer, Point point) {
        if (sampleModel == null || dataBuffer == null) {
            throw new NullPointerException("SampleModel and DataBuffer cannot be null");
        }
        if (point == null) {
            point = new Point(0, 0);
        }
        final int dataType = sampleModel.getDataType();
        if (sampleModel instanceof PixelInterleavedSampleModel) {
            switch (dataType) {
                case 0: {
                    return new ByteInterleavedRaster(sampleModel, dataBuffer, point);
                }
                case 1: {
                    return new ShortInterleavedRaster(sampleModel, dataBuffer, point);
                }
            }
        }
        else if (sampleModel instanceof SinglePixelPackedSampleModel) {
            switch (dataType) {
                case 0: {
                    return new ByteInterleavedRaster(sampleModel, dataBuffer, point);
                }
                case 1: {
                    return new ShortInterleavedRaster(sampleModel, dataBuffer, point);
                }
                case 3: {
                    return new IntegerInterleavedRaster(sampleModel, dataBuffer, point);
                }
            }
        }
        else if (sampleModel instanceof MultiPixelPackedSampleModel && dataType == 0 && sampleModel.getSampleSize(0) < 8) {
            return new BytePackedRaster(sampleModel, dataBuffer, point);
        }
        return new Raster(sampleModel, dataBuffer, point);
    }
    
    public static WritableRaster createWritableRaster(final SampleModel sampleModel, Point point) {
        if (point == null) {
            point = new Point(0, 0);
        }
        return createWritableRaster(sampleModel, sampleModel.createDataBuffer(), point);
    }
    
    public static WritableRaster createWritableRaster(final SampleModel sampleModel, final DataBuffer dataBuffer, Point point) {
        if (sampleModel == null || dataBuffer == null) {
            throw new NullPointerException("SampleModel and DataBuffer cannot be null");
        }
        if (point == null) {
            point = new Point(0, 0);
        }
        final int dataType = sampleModel.getDataType();
        if (sampleModel instanceof PixelInterleavedSampleModel) {
            switch (dataType) {
                case 0: {
                    return new ByteInterleavedRaster(sampleModel, dataBuffer, point);
                }
                case 1: {
                    return new ShortInterleavedRaster(sampleModel, dataBuffer, point);
                }
            }
        }
        else if (sampleModel instanceof SinglePixelPackedSampleModel) {
            switch (dataType) {
                case 0: {
                    return new ByteInterleavedRaster(sampleModel, dataBuffer, point);
                }
                case 1: {
                    return new ShortInterleavedRaster(sampleModel, dataBuffer, point);
                }
                case 3: {
                    return new IntegerInterleavedRaster(sampleModel, dataBuffer, point);
                }
            }
        }
        else if (sampleModel instanceof MultiPixelPackedSampleModel && dataType == 0 && sampleModel.getSampleSize(0) < 8) {
            return new BytePackedRaster(sampleModel, dataBuffer, point);
        }
        return new SunWritableRaster(sampleModel, dataBuffer, point);
    }
    
    protected Raster(final SampleModel sampleModel, final Point point) {
        this(sampleModel, sampleModel.createDataBuffer(), new Rectangle(point.x, point.y, sampleModel.getWidth(), sampleModel.getHeight()), point, null);
    }
    
    protected Raster(final SampleModel sampleModel, final DataBuffer dataBuffer, final Point point) {
        this(sampleModel, dataBuffer, new Rectangle(point.x, point.y, sampleModel.getWidth(), sampleModel.getHeight()), point, null);
    }
    
    protected Raster(final SampleModel sampleModel, final DataBuffer dataBuffer, final Rectangle rectangle, final Point point, final Raster parent) {
        if (sampleModel == null || dataBuffer == null || rectangle == null || point == null) {
            throw new NullPointerException("SampleModel, dataBuffer, aRegion and sampleModelTranslate cannot be null");
        }
        this.sampleModel = sampleModel;
        this.dataBuffer = dataBuffer;
        this.minX = rectangle.x;
        this.minY = rectangle.y;
        this.width = rectangle.width;
        this.height = rectangle.height;
        if (this.width <= 0 || this.height <= 0) {
            throw new RasterFormatException("negative or zero " + ((this.width <= 0) ? "width" : "height"));
        }
        if (this.minX + this.width < this.minX) {
            throw new RasterFormatException("overflow condition for X coordinates of Raster");
        }
        if (this.minY + this.height < this.minY) {
            throw new RasterFormatException("overflow condition for Y coordinates of Raster");
        }
        this.sampleModelTranslateX = point.x;
        this.sampleModelTranslateY = point.y;
        this.numBands = sampleModel.getNumBands();
        this.numDataElements = sampleModel.getNumDataElements();
        this.parent = parent;
    }
    
    public Raster getParent() {
        return this.parent;
    }
    
    public final int getSampleModelTranslateX() {
        return this.sampleModelTranslateX;
    }
    
    public final int getSampleModelTranslateY() {
        return this.sampleModelTranslateY;
    }
    
    public WritableRaster createCompatibleWritableRaster() {
        return new SunWritableRaster(this.sampleModel, new Point(0, 0));
    }
    
    public WritableRaster createCompatibleWritableRaster(final int n, final int n2) {
        if (n <= 0 || n2 <= 0) {
            throw new RasterFormatException("negative " + ((n <= 0) ? "width" : "height"));
        }
        return new SunWritableRaster(this.sampleModel.createCompatibleSampleModel(n, n2), new Point(0, 0));
    }
    
    public WritableRaster createCompatibleWritableRaster(final Rectangle rectangle) {
        if (rectangle == null) {
            throw new NullPointerException("Rect cannot be null");
        }
        return this.createCompatibleWritableRaster(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }
    
    public WritableRaster createCompatibleWritableRaster(final int n, final int n2, final int n3, final int n4) {
        return this.createCompatibleWritableRaster(n3, n4).createWritableChild(0, 0, n3, n4, n, n2, null);
    }
    
    public Raster createTranslatedChild(final int n, final int n2) {
        return this.createChild(this.minX, this.minY, this.width, this.height, n, n2, null);
    }
    
    public Raster createChild(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int[] array) {
        if (n < this.minX) {
            throw new RasterFormatException("parentX lies outside raster");
        }
        if (n2 < this.minY) {
            throw new RasterFormatException("parentY lies outside raster");
        }
        if (n + n3 < n || n + n3 > this.width + this.minX) {
            throw new RasterFormatException("(parentX + width) is outside raster");
        }
        if (n2 + n4 < n2 || n2 + n4 > this.height + this.minY) {
            throw new RasterFormatException("(parentY + height) is outside raster");
        }
        SampleModel sampleModel;
        if (array == null) {
            sampleModel = this.sampleModel;
        }
        else {
            sampleModel = this.sampleModel.createSubsetSampleModel(array);
        }
        return new Raster(sampleModel, this.getDataBuffer(), new Rectangle(n5, n6, n3, n4), new Point(this.sampleModelTranslateX + (n5 - n), this.sampleModelTranslateY + (n6 - n2)), this);
    }
    
    public Rectangle getBounds() {
        return new Rectangle(this.minX, this.minY, this.width, this.height);
    }
    
    public final int getMinX() {
        return this.minX;
    }
    
    public final int getMinY() {
        return this.minY;
    }
    
    public final int getWidth() {
        return this.width;
    }
    
    public final int getHeight() {
        return this.height;
    }
    
    public final int getNumBands() {
        return this.numBands;
    }
    
    public final int getNumDataElements() {
        return this.sampleModel.getNumDataElements();
    }
    
    public final int getTransferType() {
        return this.sampleModel.getTransferType();
    }
    
    public DataBuffer getDataBuffer() {
        return this.dataBuffer;
    }
    
    public SampleModel getSampleModel() {
        return this.sampleModel;
    }
    
    public Object getDataElements(final int n, final int n2, final Object o) {
        return this.sampleModel.getDataElements(n - this.sampleModelTranslateX, n2 - this.sampleModelTranslateY, o, this.dataBuffer);
    }
    
    public Object getDataElements(final int n, final int n2, final int n3, final int n4, final Object o) {
        return this.sampleModel.getDataElements(n - this.sampleModelTranslateX, n2 - this.sampleModelTranslateY, n3, n4, o, this.dataBuffer);
    }
    
    public int[] getPixel(final int n, final int n2, final int[] array) {
        return this.sampleModel.getPixel(n - this.sampleModelTranslateX, n2 - this.sampleModelTranslateY, array, this.dataBuffer);
    }
    
    public float[] getPixel(final int n, final int n2, final float[] array) {
        return this.sampleModel.getPixel(n - this.sampleModelTranslateX, n2 - this.sampleModelTranslateY, array, this.dataBuffer);
    }
    
    public double[] getPixel(final int n, final int n2, final double[] array) {
        return this.sampleModel.getPixel(n - this.sampleModelTranslateX, n2 - this.sampleModelTranslateY, array, this.dataBuffer);
    }
    
    public int[] getPixels(final int n, final int n2, final int n3, final int n4, final int[] array) {
        return this.sampleModel.getPixels(n - this.sampleModelTranslateX, n2 - this.sampleModelTranslateY, n3, n4, array, this.dataBuffer);
    }
    
    public float[] getPixels(final int n, final int n2, final int n3, final int n4, final float[] array) {
        return this.sampleModel.getPixels(n - this.sampleModelTranslateX, n2 - this.sampleModelTranslateY, n3, n4, array, this.dataBuffer);
    }
    
    public double[] getPixels(final int n, final int n2, final int n3, final int n4, final double[] array) {
        return this.sampleModel.getPixels(n - this.sampleModelTranslateX, n2 - this.sampleModelTranslateY, n3, n4, array, this.dataBuffer);
    }
    
    public int getSample(final int n, final int n2, final int n3) {
        return this.sampleModel.getSample(n - this.sampleModelTranslateX, n2 - this.sampleModelTranslateY, n3, this.dataBuffer);
    }
    
    public float getSampleFloat(final int n, final int n2, final int n3) {
        return this.sampleModel.getSampleFloat(n - this.sampleModelTranslateX, n2 - this.sampleModelTranslateY, n3, this.dataBuffer);
    }
    
    public double getSampleDouble(final int n, final int n2, final int n3) {
        return this.sampleModel.getSampleDouble(n - this.sampleModelTranslateX, n2 - this.sampleModelTranslateY, n3, this.dataBuffer);
    }
    
    public int[] getSamples(final int n, final int n2, final int n3, final int n4, final int n5, final int[] array) {
        return this.sampleModel.getSamples(n - this.sampleModelTranslateX, n2 - this.sampleModelTranslateY, n3, n4, n5, array, this.dataBuffer);
    }
    
    public float[] getSamples(final int n, final int n2, final int n3, final int n4, final int n5, final float[] array) {
        return this.sampleModel.getSamples(n - this.sampleModelTranslateX, n2 - this.sampleModelTranslateY, n3, n4, n5, array, this.dataBuffer);
    }
    
    public double[] getSamples(final int n, final int n2, final int n3, final int n4, final int n5, final double[] array) {
        return this.sampleModel.getSamples(n - this.sampleModelTranslateX, n2 - this.sampleModelTranslateY, n3, n4, n5, array, this.dataBuffer);
    }
    
    static {
        ColorModel.loadLibraries();
        initIDs();
    }
}
