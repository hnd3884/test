package sun.awt.image;

import java.awt.image.WritableRaster;
import java.awt.image.Raster;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.RasterFormatException;
import java.awt.image.DataBufferByte;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.image.SampleModel;

public class ByteInterleavedRaster extends ByteComponentRaster
{
    boolean inOrder;
    int dbOffset;
    int dbOffsetPacked;
    boolean packed;
    int[] bitMasks;
    int[] bitOffsets;
    private int maxX;
    private int maxY;
    
    public ByteInterleavedRaster(final SampleModel sampleModel, final Point point) {
        this(sampleModel, sampleModel.createDataBuffer(), new Rectangle(point.x, point.y, sampleModel.getWidth(), sampleModel.getHeight()), point, null);
    }
    
    public ByteInterleavedRaster(final SampleModel sampleModel, final DataBuffer dataBuffer, final Point point) {
        this(sampleModel, dataBuffer, new Rectangle(point.x, point.y, sampleModel.getWidth(), sampleModel.getHeight()), point, null);
    }
    
    private boolean isInterleaved(final ComponentSampleModel componentSampleModel) {
        final int numBands = this.sampleModel.getNumBands();
        if (numBands == 1) {
            return true;
        }
        final int[] bankIndices = componentSampleModel.getBankIndices();
        for (int i = 0; i < numBands; ++i) {
            if (bankIndices[i] != 0) {
                return false;
            }
        }
        final int[] bandOffsets = componentSampleModel.getBandOffsets();
        int n2;
        int n = n2 = bandOffsets[0];
        for (int j = 1; j < numBands; ++j) {
            final int n3 = bandOffsets[j];
            if (n3 < n) {
                n = n3;
            }
            if (n3 > n2) {
                n2 = n3;
            }
        }
        return n2 - n < componentSampleModel.getPixelStride();
    }
    
    public ByteInterleavedRaster(final SampleModel sampleModel, final DataBuffer dataBuffer, final Rectangle rectangle, final Point point, final ByteInterleavedRaster byteInterleavedRaster) {
        super(sampleModel, dataBuffer, rectangle, point, byteInterleavedRaster);
        this.packed = false;
        this.maxX = this.minX + this.width;
        this.maxY = this.minY + this.height;
        if (!(dataBuffer instanceof DataBufferByte)) {
            throw new RasterFormatException("ByteInterleavedRasters must have byte DataBuffers");
        }
        final DataBufferByte dataBufferByte = (DataBufferByte)dataBuffer;
        this.data = SunWritableRaster.stealData(dataBufferByte, 0);
        final int n = rectangle.x - point.x;
        final int n2 = rectangle.y - point.y;
        if (sampleModel instanceof PixelInterleavedSampleModel || (sampleModel instanceof ComponentSampleModel && this.isInterleaved((ComponentSampleModel)sampleModel))) {
            final ComponentSampleModel componentSampleModel = (ComponentSampleModel)sampleModel;
            this.scanlineStride = componentSampleModel.getScanlineStride();
            this.pixelStride = componentSampleModel.getPixelStride();
            this.dataOffsets = componentSampleModel.getBandOffsets();
            for (int i = 0; i < this.getNumDataElements(); ++i) {
                final int[] dataOffsets = this.dataOffsets;
                final int n3 = i;
                dataOffsets[n3] += n * this.pixelStride + n2 * this.scanlineStride;
            }
        }
        else {
            if (!(sampleModel instanceof SinglePixelPackedSampleModel)) {
                throw new RasterFormatException("ByteInterleavedRasters must have PixelInterleavedSampleModel, SinglePixelPackedSampleModel or interleaved ComponentSampleModel.  Sample model is " + sampleModel);
            }
            final SinglePixelPackedSampleModel singlePixelPackedSampleModel = (SinglePixelPackedSampleModel)sampleModel;
            this.packed = true;
            this.bitMasks = singlePixelPackedSampleModel.getBitMasks();
            this.bitOffsets = singlePixelPackedSampleModel.getBitOffsets();
            this.scanlineStride = singlePixelPackedSampleModel.getScanlineStride();
            this.pixelStride = 1;
            (this.dataOffsets = new int[1])[0] = dataBufferByte.getOffset();
            final int[] dataOffsets2 = this.dataOffsets;
            final int n4 = 0;
            dataOffsets2[n4] += n * this.pixelStride + n2 * this.scanlineStride;
        }
        this.bandOffset = this.dataOffsets[0];
        this.dbOffsetPacked = dataBuffer.getOffset() - this.sampleModelTranslateY * this.scanlineStride - this.sampleModelTranslateX * this.pixelStride;
        this.dbOffset = this.dbOffsetPacked - (n * this.pixelStride + n2 * this.scanlineStride);
        this.inOrder = false;
        if (this.numDataElements == this.pixelStride) {
            this.inOrder = true;
            for (int j = 1; j < this.numDataElements; ++j) {
                if (this.dataOffsets[j] - this.dataOffsets[0] != j) {
                    this.inOrder = false;
                    break;
                }
            }
        }
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
    public byte[] getDataStorage() {
        return this.data;
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
        final int n3 = (n2 - this.minY) * this.scanlineStride + (n - this.minX) * this.pixelStride;
        for (int i = 0; i < this.numDataElements; ++i) {
            array[i] = this.data[this.dataOffsets[i] + n3];
        }
        return array;
    }
    
    @Override
    public Object getDataElements(final int n, final int n2, final int n3, final int n4, final Object o) {
        return this.getByteData(n, n2, n3, n4, (byte[])o);
    }
    
    @Override
    public byte[] getByteData(final int n, final int n2, final int n3, final int n4, final int n5, byte[] array) {
        if (n < this.minX || n2 < this.minY || n + n3 > this.maxX || n2 + n4 > this.maxY) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        if (array == null) {
            array = new byte[n3 * n4];
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
    public byte[] getByteData(final int n, final int n2, final int n3, final int n4, byte[] array) {
        if (n < this.minX || n2 < this.minY || n + n3 > this.maxX || n2 + n4 > this.maxY) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        if (array == null) {
            array = new byte[this.numDataElements * n3 * n4];
        }
        int n5 = (n2 - this.minY) * this.scanlineStride + (n - this.minX) * this.pixelStride;
        int n6 = 0;
        if (this.inOrder) {
            int n7 = n5 + this.dataOffsets[0];
            final int n8 = n3 * this.pixelStride;
            if (this.scanlineStride == n8) {
                System.arraycopy(this.data, n7, array, n6, n8 * n4);
            }
            else {
                for (int i = 0; i < n4; ++i, n7 += this.scanlineStride) {
                    System.arraycopy(this.data, n7, array, n6, n8);
                    n6 += n8;
                }
            }
        }
        else if (this.numDataElements == 1) {
            for (int n9 = n5 + this.dataOffsets[0], j = 0; j < n4; ++j, n9 += this.scanlineStride) {
                for (int n10 = n9, k = 0; k < n3; ++k, n10 += this.pixelStride) {
                    array[n6++] = this.data[n10];
                }
            }
        }
        else if (this.numDataElements == 2) {
            int n11 = n5 + this.dataOffsets[0];
            final int n12 = this.dataOffsets[1] - this.dataOffsets[0];
            for (int l = 0; l < n4; ++l, n11 += this.scanlineStride) {
                for (int n13 = n11, n14 = 0; n14 < n3; ++n14, n13 += this.pixelStride) {
                    array[n6++] = this.data[n13];
                    array[n6++] = this.data[n13 + n12];
                }
            }
        }
        else if (this.numDataElements == 3) {
            int n15 = n5 + this.dataOffsets[0];
            final int n16 = this.dataOffsets[1] - this.dataOffsets[0];
            final int n17 = this.dataOffsets[2] - this.dataOffsets[0];
            for (int n18 = 0; n18 < n4; ++n18, n15 += this.scanlineStride) {
                for (int n19 = n15, n20 = 0; n20 < n3; ++n20, n19 += this.pixelStride) {
                    array[n6++] = this.data[n19];
                    array[n6++] = this.data[n19 + n16];
                    array[n6++] = this.data[n19 + n17];
                }
            }
        }
        else if (this.numDataElements == 4) {
            int n21 = n5 + this.dataOffsets[0];
            final int n22 = this.dataOffsets[1] - this.dataOffsets[0];
            final int n23 = this.dataOffsets[2] - this.dataOffsets[0];
            final int n24 = this.dataOffsets[3] - this.dataOffsets[0];
            for (int n25 = 0; n25 < n4; ++n25, n21 += this.scanlineStride) {
                for (int n26 = n21, n27 = 0; n27 < n3; ++n27, n26 += this.pixelStride) {
                    array[n6++] = this.data[n26];
                    array[n6++] = this.data[n26 + n22];
                    array[n6++] = this.data[n26 + n23];
                    array[n6++] = this.data[n26 + n24];
                }
            }
        }
        else {
            for (int n28 = 0; n28 < n4; ++n28, n5 += this.scanlineStride) {
                for (int n29 = n5, n30 = 0; n30 < n3; ++n30, n29 += this.pixelStride) {
                    for (int n31 = 0; n31 < this.numDataElements; ++n31) {
                        array[n6++] = this.data[this.dataOffsets[n31] + n29];
                    }
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
        final int n3 = (n2 - this.minY) * this.scanlineStride + (n - this.minX) * this.pixelStride;
        for (int i = 0; i < this.numDataElements; ++i) {
            this.data[this.dataOffsets[i] + n3] = array[i];
        }
        this.markDirty();
    }
    
    @Override
    public void setDataElements(final int n, final int n2, final Raster raster) {
        final int minX = raster.getMinX();
        final int minY = raster.getMinY();
        final int n3 = n + minX;
        final int n4 = n2 + minY;
        final int width = raster.getWidth();
        final int height = raster.getHeight();
        if (n3 < this.minX || n4 < this.minY || n3 + width > this.maxX || n4 + height > this.maxY) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        this.setDataElements(n3, n4, minX, minY, width, height, raster);
    }
    
    private void setDataElements(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final Raster raster) {
        if (n5 <= 0 || n6 <= 0) {
            return;
        }
        final int minX = raster.getMinX();
        final int minY = raster.getMinY();
        Object dataElements = null;
        if (raster instanceof ByteInterleavedRaster) {
            final ByteInterleavedRaster byteInterleavedRaster = (ByteInterleavedRaster)raster;
            final byte[] dataStorage = byteInterleavedRaster.getDataStorage();
            if (this.inOrder && byteInterleavedRaster.inOrder && this.pixelStride == byteInterleavedRaster.pixelStride) {
                final int dataOffset = byteInterleavedRaster.getDataOffset(0);
                final int scanlineStride = byteInterleavedRaster.getScanlineStride();
                int n7 = dataOffset + (n4 - minY) * scanlineStride + (n3 - minX) * byteInterleavedRaster.getPixelStride();
                int n8 = this.dataOffsets[0] + (n2 - this.minY) * this.scanlineStride + (n - this.minX) * this.pixelStride;
                final int n9 = n5 * this.pixelStride;
                for (int i = 0; i < n6; ++i) {
                    System.arraycopy(dataStorage, n7, this.data, n8, n9);
                    n7 += scanlineStride;
                    n8 += this.scanlineStride;
                }
                this.markDirty();
                return;
            }
        }
        for (int j = 0; j < n6; ++j) {
            dataElements = raster.getDataElements(minX, minY + j, n5, 1, dataElements);
            this.setDataElements(n, n2 + j, n5, 1, dataElements);
        }
    }
    
    @Override
    public void setDataElements(final int n, final int n2, final int n3, final int n4, final Object o) {
        this.putByteData(n, n2, n3, n4, (byte[])o);
    }
    
    @Override
    public void putByteData(final int n, final int n2, final int n3, final int n4, final int n5, final byte[] array) {
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
    public void putByteData(final int n, final int n2, final int n3, final int n4, final byte[] array) {
        if (n < this.minX || n2 < this.minY || n + n3 > this.maxX || n2 + n4 > this.maxY) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        int n5 = (n2 - this.minY) * this.scanlineStride + (n - this.minX) * this.pixelStride;
        int n6 = 0;
        if (this.inOrder) {
            int n7 = n5 + this.dataOffsets[0];
            final int n8 = n3 * this.pixelStride;
            if (n8 == this.scanlineStride) {
                System.arraycopy(array, 0, this.data, n7, n8 * n4);
            }
            else {
                for (int i = 0; i < n4; ++i, n7 += this.scanlineStride) {
                    System.arraycopy(array, n6, this.data, n7, n8);
                    n6 += n8;
                }
            }
        }
        else if (this.numDataElements == 1) {
            for (int n9 = n5 + this.dataOffsets[0], j = 0; j < n4; ++j, n9 += this.scanlineStride) {
                for (int n10 = n9, k = 0; k < n3; ++k, n10 += this.pixelStride) {
                    this.data[n10] = array[n6++];
                }
            }
        }
        else if (this.numDataElements == 2) {
            int n11 = n5 + this.dataOffsets[0];
            final int n12 = this.dataOffsets[1] - this.dataOffsets[0];
            for (int l = 0; l < n4; ++l, n11 += this.scanlineStride) {
                for (int n13 = n11, n14 = 0; n14 < n3; ++n14, n13 += this.pixelStride) {
                    this.data[n13] = array[n6++];
                    this.data[n13 + n12] = array[n6++];
                }
            }
        }
        else if (this.numDataElements == 3) {
            int n15 = n5 + this.dataOffsets[0];
            final int n16 = this.dataOffsets[1] - this.dataOffsets[0];
            final int n17 = this.dataOffsets[2] - this.dataOffsets[0];
            for (int n18 = 0; n18 < n4; ++n18, n15 += this.scanlineStride) {
                for (int n19 = n15, n20 = 0; n20 < n3; ++n20, n19 += this.pixelStride) {
                    this.data[n19] = array[n6++];
                    this.data[n19 + n16] = array[n6++];
                    this.data[n19 + n17] = array[n6++];
                }
            }
        }
        else if (this.numDataElements == 4) {
            int n21 = n5 + this.dataOffsets[0];
            final int n22 = this.dataOffsets[1] - this.dataOffsets[0];
            final int n23 = this.dataOffsets[2] - this.dataOffsets[0];
            final int n24 = this.dataOffsets[3] - this.dataOffsets[0];
            for (int n25 = 0; n25 < n4; ++n25, n21 += this.scanlineStride) {
                for (int n26 = n21, n27 = 0; n27 < n3; ++n27, n26 += this.pixelStride) {
                    this.data[n26] = array[n6++];
                    this.data[n26 + n22] = array[n6++];
                    this.data[n26 + n23] = array[n6++];
                    this.data[n26 + n24] = array[n6++];
                }
            }
        }
        else {
            for (int n28 = 0; n28 < n4; ++n28, n5 += this.scanlineStride) {
                for (int n29 = n5, n30 = 0; n30 < n3; ++n30, n29 += this.pixelStride) {
                    for (int n31 = 0; n31 < this.numDataElements; ++n31) {
                        this.data[this.dataOffsets[n31] + n29] = array[n6++];
                    }
                }
            }
        }
        this.markDirty();
    }
    
    @Override
    public int getSample(final int n, final int n2, final int n3) {
        if (n < this.minX || n2 < this.minY || n >= this.maxX || n2 >= this.maxY) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        if (this.packed) {
            return (this.data[n2 * this.scanlineStride + n + this.dbOffsetPacked] & this.bitMasks[n3]) >>> this.bitOffsets[n3];
        }
        return this.data[n2 * this.scanlineStride + n * this.pixelStride + this.dbOffset + this.dataOffsets[n3]] & 0xFF;
    }
    
    @Override
    public void setSample(final int n, final int n2, final int n3, final int n4) {
        if (n < this.minX || n2 < this.minY || n >= this.maxX || n2 >= this.maxY) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        if (this.packed) {
            final int n5 = n2 * this.scanlineStride + n + this.dbOffsetPacked;
            final int n6 = this.bitMasks[n3];
            this.data[n5] = (byte)((byte)(this.data[n5] & ~n6) | (n4 << this.bitOffsets[n3] & n6));
        }
        else {
            this.data[n2 * this.scanlineStride + n * this.pixelStride + this.dbOffset + this.dataOffsets[n3]] = (byte)n4;
        }
        this.markDirty();
    }
    
    @Override
    public int[] getSamples(final int n, final int n2, final int n3, final int n4, final int n5, final int[] array) {
        if (n < this.minX || n2 < this.minY || n + n3 > this.maxX || n2 + n4 > this.maxY) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        int[] array2;
        if (array != null) {
            array2 = array;
        }
        else {
            array2 = new int[n3 * n4];
        }
        final int n6 = n2 * this.scanlineStride + n * this.pixelStride;
        int n7 = 0;
        if (this.packed) {
            int n8 = n6 + this.dbOffsetPacked;
            final int n9 = this.bitMasks[n5];
            final int n10 = this.bitOffsets[n5];
            for (int i = 0; i < n4; ++i) {
                int n11 = n8;
                for (int j = 0; j < n3; ++j) {
                    array2[n7++] = (this.data[n11++] & n9) >>> n10;
                }
                n8 += this.scanlineStride;
            }
        }
        else {
            int n12 = n6 + (this.dbOffset + this.dataOffsets[n5]);
            for (int k = 0; k < n4; ++k) {
                int n13 = n12;
                for (int l = 0; l < n3; ++l) {
                    array2[n7++] = (this.data[n13] & 0xFF);
                    n13 += this.pixelStride;
                }
                n12 += this.scanlineStride;
            }
        }
        return array2;
    }
    
    @Override
    public void setSamples(final int n, final int n2, final int n3, final int n4, final int n5, final int[] array) {
        if (n < this.minX || n2 < this.minY || n + n3 > this.maxX || n2 + n4 > this.maxY) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        final int n6 = n2 * this.scanlineStride + n * this.pixelStride;
        int n7 = 0;
        if (this.packed) {
            int n8 = n6 + this.dbOffsetPacked;
            final int n9 = this.bitMasks[n5];
            for (int i = 0; i < n4; ++i) {
                int n10 = n8;
                for (int j = 0; j < n3; ++j) {
                    this.data[n10++] = (byte)((byte)(this.data[n10] & ~n9) | (array[n7++] << this.bitOffsets[n5] & n9));
                }
                n8 += this.scanlineStride;
            }
        }
        else {
            int n11 = n6 + (this.dbOffset + this.dataOffsets[n5]);
            for (int k = 0; k < n4; ++k) {
                int n12 = n11;
                for (int l = 0; l < n3; ++l) {
                    this.data[n12] = (byte)array[n7++];
                    n12 += this.pixelStride;
                }
                n11 += this.scanlineStride;
            }
        }
        this.markDirty();
    }
    
    @Override
    public int[] getPixels(final int n, final int n2, final int n3, final int n4, final int[] array) {
        if (n < this.minX || n2 < this.minY || n + n3 > this.maxX || n2 + n4 > this.maxY) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        int[] array2;
        if (array != null) {
            array2 = array;
        }
        else {
            array2 = new int[n3 * n4 * this.numBands];
        }
        final int n5 = n2 * this.scanlineStride + n * this.pixelStride;
        int n6 = 0;
        if (this.packed) {
            int n7 = n5 + this.dbOffsetPacked;
            for (int i = 0; i < n4; ++i) {
                for (int j = 0; j < n3; ++j) {
                    final byte b = this.data[n7 + j];
                    for (int k = 0; k < this.numBands; ++k) {
                        array2[n6++] = (b & this.bitMasks[k]) >>> this.bitOffsets[k];
                    }
                }
                n7 += this.scanlineStride;
            }
        }
        else {
            int n8 = n5 + this.dbOffset;
            final int n9 = this.dataOffsets[0];
            if (this.numBands == 1) {
                for (int l = 0; l < n4; ++l) {
                    int n10 = n8 + n9;
                    for (int n11 = 0; n11 < n3; ++n11) {
                        array2[n6++] = (this.data[n10] & 0xFF);
                        n10 += this.pixelStride;
                    }
                    n8 += this.scanlineStride;
                }
            }
            else if (this.numBands == 2) {
                final int n12 = this.dataOffsets[1] - n9;
                for (int n13 = 0; n13 < n4; ++n13) {
                    int n14 = n8 + n9;
                    for (int n15 = 0; n15 < n3; ++n15) {
                        array2[n6++] = (this.data[n14] & 0xFF);
                        array2[n6++] = (this.data[n14 + n12] & 0xFF);
                        n14 += this.pixelStride;
                    }
                    n8 += this.scanlineStride;
                }
            }
            else if (this.numBands == 3) {
                final int n16 = this.dataOffsets[1] - n9;
                final int n17 = this.dataOffsets[2] - n9;
                for (int n18 = 0; n18 < n4; ++n18) {
                    int n19 = n8 + n9;
                    for (int n20 = 0; n20 < n3; ++n20) {
                        array2[n6++] = (this.data[n19] & 0xFF);
                        array2[n6++] = (this.data[n19 + n16] & 0xFF);
                        array2[n6++] = (this.data[n19 + n17] & 0xFF);
                        n19 += this.pixelStride;
                    }
                    n8 += this.scanlineStride;
                }
            }
            else if (this.numBands == 4) {
                final int n21 = this.dataOffsets[1] - n9;
                final int n22 = this.dataOffsets[2] - n9;
                final int n23 = this.dataOffsets[3] - n9;
                for (int n24 = 0; n24 < n4; ++n24) {
                    int n25 = n8 + n9;
                    for (int n26 = 0; n26 < n3; ++n26) {
                        array2[n6++] = (this.data[n25] & 0xFF);
                        array2[n6++] = (this.data[n25 + n21] & 0xFF);
                        array2[n6++] = (this.data[n25 + n22] & 0xFF);
                        array2[n6++] = (this.data[n25 + n23] & 0xFF);
                        n25 += this.pixelStride;
                    }
                    n8 += this.scanlineStride;
                }
            }
            else {
                for (int n27 = 0; n27 < n4; ++n27) {
                    int n28 = n8;
                    for (int n29 = 0; n29 < n3; ++n29) {
                        for (int n30 = 0; n30 < this.numBands; ++n30) {
                            array2[n6++] = (this.data[n28 + this.dataOffsets[n30]] & 0xFF);
                        }
                        n28 += this.pixelStride;
                    }
                    n8 += this.scanlineStride;
                }
            }
        }
        return array2;
    }
    
    @Override
    public void setPixels(final int n, final int n2, final int n3, final int n4, final int[] array) {
        if (n < this.minX || n2 < this.minY || n + n3 > this.maxX || n2 + n4 > this.maxY) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        final int n5 = n2 * this.scanlineStride + n * this.pixelStride;
        int n6 = 0;
        if (this.packed) {
            int n7 = n5 + this.dbOffsetPacked;
            for (int i = 0; i < n4; ++i) {
                for (int j = 0; j < n3; ++j) {
                    int n8 = 0;
                    for (int k = 0; k < this.numBands; ++k) {
                        n8 |= (array[n6++] << this.bitOffsets[k] & this.bitMasks[k]);
                    }
                    this.data[n7 + j] = (byte)n8;
                }
                n7 += this.scanlineStride;
            }
        }
        else {
            int n9 = n5 + this.dbOffset;
            final int n10 = this.dataOffsets[0];
            if (this.numBands == 1) {
                for (int l = 0; l < n4; ++l) {
                    int n11 = n9 + n10;
                    for (int n12 = 0; n12 < n3; ++n12) {
                        this.data[n11] = (byte)array[n6++];
                        n11 += this.pixelStride;
                    }
                    n9 += this.scanlineStride;
                }
            }
            else if (this.numBands == 2) {
                final int n13 = this.dataOffsets[1] - n10;
                for (int n14 = 0; n14 < n4; ++n14) {
                    int n15 = n9 + n10;
                    for (int n16 = 0; n16 < n3; ++n16) {
                        this.data[n15] = (byte)array[n6++];
                        this.data[n15 + n13] = (byte)array[n6++];
                        n15 += this.pixelStride;
                    }
                    n9 += this.scanlineStride;
                }
            }
            else if (this.numBands == 3) {
                final int n17 = this.dataOffsets[1] - n10;
                final int n18 = this.dataOffsets[2] - n10;
                for (int n19 = 0; n19 < n4; ++n19) {
                    int n20 = n9 + n10;
                    for (int n21 = 0; n21 < n3; ++n21) {
                        this.data[n20] = (byte)array[n6++];
                        this.data[n20 + n17] = (byte)array[n6++];
                        this.data[n20 + n18] = (byte)array[n6++];
                        n20 += this.pixelStride;
                    }
                    n9 += this.scanlineStride;
                }
            }
            else if (this.numBands == 4) {
                final int n22 = this.dataOffsets[1] - n10;
                final int n23 = this.dataOffsets[2] - n10;
                final int n24 = this.dataOffsets[3] - n10;
                for (int n25 = 0; n25 < n4; ++n25) {
                    int n26 = n9 + n10;
                    for (int n27 = 0; n27 < n3; ++n27) {
                        this.data[n26] = (byte)array[n6++];
                        this.data[n26 + n22] = (byte)array[n6++];
                        this.data[n26 + n23] = (byte)array[n6++];
                        this.data[n26 + n24] = (byte)array[n6++];
                        n26 += this.pixelStride;
                    }
                    n9 += this.scanlineStride;
                }
            }
            else {
                for (int n28 = 0; n28 < n4; ++n28) {
                    int n29 = n9;
                    for (int n30 = 0; n30 < n3; ++n30) {
                        for (int n31 = 0; n31 < this.numBands; ++n31) {
                            this.data[n29 + this.dataOffsets[n31]] = (byte)array[n6++];
                        }
                        n29 += this.pixelStride;
                    }
                    n9 += this.scanlineStride;
                }
            }
        }
        this.markDirty();
    }
    
    @Override
    public void setRect(final int n, final int n2, final Raster raster) {
        if (!(raster instanceof ByteInterleavedRaster)) {
            super.setRect(n, n2, raster);
            return;
        }
        int width = raster.getWidth();
        int height = raster.getHeight();
        int minX = raster.getMinX();
        int minY = raster.getMinY();
        int minX2 = n + minX;
        int minY2 = n2 + minY;
        if (minX2 < this.minX) {
            final int n3 = this.minX - minX2;
            width -= n3;
            minX += n3;
            minX2 = this.minX;
        }
        if (minY2 < this.minY) {
            final int n4 = this.minY - minY2;
            height -= n4;
            minY += n4;
            minY2 = this.minY;
        }
        if (minX2 + width > this.maxX) {
            width = this.maxX - minX2;
        }
        if (minY2 + height > this.maxY) {
            height = this.maxY - minY2;
        }
        this.setDataElements(minX2, minY2, minX, minY, width, height, raster);
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
        return new ByteInterleavedRaster(sampleModel, this.dataBuffer, new Rectangle(n5, n6, n3, n4), new Point(this.sampleModelTranslateX + (n5 - n), this.sampleModelTranslateY + (n6 - n2)), this);
    }
    
    @Override
    public WritableRaster createCompatibleWritableRaster(final int n, final int n2) {
        if (n <= 0 || n2 <= 0) {
            throw new RasterFormatException("negative " + ((n <= 0) ? "width" : "height"));
        }
        return new ByteInterleavedRaster(this.sampleModel.createCompatibleSampleModel(n, n2), new Point(0, 0));
    }
    
    @Override
    public WritableRaster createCompatibleWritableRaster() {
        return this.createCompatibleWritableRaster(this.width, this.height);
    }
    
    @Override
    public String toString() {
        return new String("ByteInterleavedRaster: width = " + this.width + " height = " + this.height + " #numDataElements " + this.numDataElements + " dataOff[0] = " + this.dataOffsets[0]);
    }
}
