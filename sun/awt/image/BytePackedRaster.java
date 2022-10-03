package sun.awt.image;

import java.awt.image.Raster;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.RasterFormatException;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.awt.image.DataBuffer;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.image.SampleModel;

public class BytePackedRaster extends SunWritableRaster
{
    int dataBitOffset;
    int scanlineStride;
    int pixelBitStride;
    int bitMask;
    byte[] data;
    int shiftOffset;
    int type;
    private int maxX;
    private int maxY;
    
    private static native void initIDs();
    
    public BytePackedRaster(final SampleModel sampleModel, final Point point) {
        this(sampleModel, sampleModel.createDataBuffer(), new Rectangle(point.x, point.y, sampleModel.getWidth(), sampleModel.getHeight()), point, null);
    }
    
    public BytePackedRaster(final SampleModel sampleModel, final DataBuffer dataBuffer, final Point point) {
        this(sampleModel, dataBuffer, new Rectangle(point.x, point.y, sampleModel.getWidth(), sampleModel.getHeight()), point, null);
    }
    
    public BytePackedRaster(final SampleModel sampleModel, final DataBuffer dataBuffer, final Rectangle rectangle, final Point point, final BytePackedRaster bytePackedRaster) {
        super(sampleModel, dataBuffer, rectangle, point, bytePackedRaster);
        this.maxX = this.minX + this.width;
        this.maxY = this.minY + this.height;
        if (!(dataBuffer instanceof DataBufferByte)) {
            throw new RasterFormatException("BytePackedRasters must havebyte DataBuffers");
        }
        final DataBufferByte dataBufferByte = (DataBufferByte)dataBuffer;
        this.data = SunWritableRaster.stealData(dataBufferByte, 0);
        if (dataBufferByte.getNumBanks() != 1) {
            throw new RasterFormatException("DataBuffer for BytePackedRasters must only have 1 bank.");
        }
        final int offset = dataBufferByte.getOffset();
        if (!(sampleModel instanceof MultiPixelPackedSampleModel)) {
            throw new RasterFormatException("BytePackedRasters must haveMultiPixelPackedSampleModel");
        }
        final MultiPixelPackedSampleModel multiPixelPackedSampleModel = (MultiPixelPackedSampleModel)sampleModel;
        this.type = 11;
        this.pixelBitStride = multiPixelPackedSampleModel.getPixelBitStride();
        if (this.pixelBitStride != 1 && this.pixelBitStride != 2 && this.pixelBitStride != 4) {
            throw new RasterFormatException("BytePackedRasters must have a bit depth of 1, 2, or 4");
        }
        this.scanlineStride = multiPixelPackedSampleModel.getScanlineStride();
        this.dataBitOffset = multiPixelPackedSampleModel.getDataBitOffset() + offset * 8;
        this.dataBitOffset += (rectangle.x - point.x) * this.pixelBitStride + (rectangle.y - point.y) * this.scanlineStride * 8;
        this.bitMask = (1 << this.pixelBitStride) - 1;
        this.shiftOffset = 8 - this.pixelBitStride;
        this.verify(false);
    }
    
    public int getDataBitOffset() {
        return this.dataBitOffset;
    }
    
    public int getScanlineStride() {
        return this.scanlineStride;
    }
    
    public int getPixelBitStride() {
        return this.pixelBitStride;
    }
    
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
        final int n3 = this.dataBitOffset + (n - this.minX) * this.pixelBitStride;
        array[0] = (byte)((this.data[(n2 - this.minY) * this.scanlineStride + (n3 >> 3)] & 0xFF) >> this.shiftOffset - (n3 & 0x7) & this.bitMask);
        return array;
    }
    
    @Override
    public Object getDataElements(final int n, final int n2, final int n3, final int n4, final Object o) {
        return this.getByteData(n, n2, n3, n4, (byte[])o);
    }
    
    public Object getPixelData(final int n, final int n2, final int n3, final int n4, final Object o) {
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
        final int pixelBitStride = this.pixelBitStride;
        final int n5 = this.dataBitOffset + (n - this.minX) * pixelBitStride;
        int n6 = (n2 - this.minY) * this.scanlineStride;
        int n7 = 0;
        final byte[] data = this.data;
        for (int i = 0; i < n4; ++i) {
            int n8 = n5;
            for (int j = 0; j < n3; ++j) {
                array[n7++] = (byte)(this.bitMask & data[n6 + (n8 >> 3)] >> this.shiftOffset - (n8 & 0x7));
                n8 += pixelBitStride;
            }
            n6 += this.scanlineStride;
        }
        return array;
    }
    
    public byte[] getByteData(final int n, final int n2, final int n3, final int n4, final int n5, final byte[] array) {
        return this.getByteData(n, n2, n3, n4, array);
    }
    
    public byte[] getByteData(final int n, final int n2, final int n3, final int n4, byte[] array) {
        if (n < this.minX || n2 < this.minY || n + n3 > this.maxX || n2 + n4 > this.maxY) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        if (array == null) {
            array = new byte[n3 * n4];
        }
        final int pixelBitStride = this.pixelBitStride;
        final int n5 = this.dataBitOffset + (n - this.minX) * pixelBitStride;
        int n6 = (n2 - this.minY) * this.scanlineStride;
        int n7 = 0;
        final byte[] data = this.data;
        for (int i = 0; i < n4; ++i) {
            int n8;
            int j;
            for (n8 = n5, j = 0; j < n3 && (n8 & 0x7) != 0x0; n8 += pixelBitStride, ++j) {
                array[n7++] = (byte)(this.bitMask & data[n6 + (n8 >> 3)] >> this.shiftOffset - (n8 & 0x7));
            }
            int n9 = n6 + (n8 >> 3);
            switch (pixelBitStride) {
                case 1: {
                    while (j < n3 - 7) {
                        final byte b = data[n9++];
                        array[n7++] = (byte)(b >> 7 & 0x1);
                        array[n7++] = (byte)(b >> 6 & 0x1);
                        array[n7++] = (byte)(b >> 5 & 0x1);
                        array[n7++] = (byte)(b >> 4 & 0x1);
                        array[n7++] = (byte)(b >> 3 & 0x1);
                        array[n7++] = (byte)(b >> 2 & 0x1);
                        array[n7++] = (byte)(b >> 1 & 0x1);
                        array[n7++] = (byte)(b & 0x1);
                        n8 += 8;
                        j += 8;
                    }
                    break;
                }
                case 2: {
                    while (j < n3 - 7) {
                        final byte b2 = data[n9++];
                        array[n7++] = (byte)(b2 >> 6 & 0x3);
                        array[n7++] = (byte)(b2 >> 4 & 0x3);
                        array[n7++] = (byte)(b2 >> 2 & 0x3);
                        array[n7++] = (byte)(b2 & 0x3);
                        final byte b3 = data[n9++];
                        array[n7++] = (byte)(b3 >> 6 & 0x3);
                        array[n7++] = (byte)(b3 >> 4 & 0x3);
                        array[n7++] = (byte)(b3 >> 2 & 0x3);
                        array[n7++] = (byte)(b3 & 0x3);
                        n8 += 16;
                        j += 8;
                    }
                    break;
                }
                case 4: {
                    while (j < n3 - 7) {
                        final byte b4 = data[n9++];
                        array[n7++] = (byte)(b4 >> 4 & 0xF);
                        array[n7++] = (byte)(b4 & 0xF);
                        final byte b5 = data[n9++];
                        array[n7++] = (byte)(b5 >> 4 & 0xF);
                        array[n7++] = (byte)(b5 & 0xF);
                        final byte b6 = data[n9++];
                        array[n7++] = (byte)(b6 >> 4 & 0xF);
                        array[n7++] = (byte)(b6 & 0xF);
                        final byte b7 = data[n9++];
                        array[n7++] = (byte)(b7 >> 4 & 0xF);
                        array[n7++] = (byte)(b7 & 0xF);
                        n8 += 32;
                        j += 8;
                    }
                    break;
                }
            }
            while (j < n3) {
                array[n7++] = (byte)(this.bitMask & data[n6 + (n8 >> 3)] >> this.shiftOffset - (n8 & 0x7));
                n8 += pixelBitStride;
                ++j;
            }
            n6 += this.scanlineStride;
        }
        return array;
    }
    
    @Override
    public void setDataElements(final int n, final int n2, final Object o) {
        if (n < this.minX || n2 < this.minY || n >= this.maxX || n2 >= this.maxY) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        final byte[] array = (byte[])o;
        final int n3 = this.dataBitOffset + (n - this.minX) * this.pixelBitStride;
        final int n4 = (n2 - this.minY) * this.scanlineStride + (n3 >> 3);
        final int n5 = this.shiftOffset - (n3 & 0x7);
        this.data[n4] = (byte)((byte)(this.data[n4] & ~(this.bitMask << n5)) | (array[0] & this.bitMask) << n5);
        this.markDirty();
    }
    
    @Override
    public void setDataElements(final int n, final int n2, final Raster raster) {
        if (!(raster instanceof BytePackedRaster) || ((BytePackedRaster)raster).pixelBitStride != this.pixelBitStride) {
            super.setDataElements(n, n2, raster);
            return;
        }
        final int minX = raster.getMinX();
        final int minY = raster.getMinY();
        final int n3 = minX + n;
        final int n4 = minY + n2;
        final int width = raster.getWidth();
        final int height = raster.getHeight();
        if (n3 < this.minX || n4 < this.minY || n3 + width > this.maxX || n4 + height > this.maxY) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        this.setDataElements(n3, n4, minX, minY, width, height, (BytePackedRaster)raster);
    }
    
    private void setDataElements(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final BytePackedRaster bytePackedRaster) {
        if (n5 <= 0 || n6 <= 0) {
            return;
        }
        final byte[] data = bytePackedRaster.data;
        final byte[] data2 = this.data;
        final int scanlineStride = bytePackedRaster.scanlineStride;
        final int scanlineStride2 = this.scanlineStride;
        int n7 = bytePackedRaster.dataBitOffset + 8 * (n4 - bytePackedRaster.minY) * scanlineStride + (n3 - bytePackedRaster.minX) * bytePackedRaster.pixelBitStride;
        int n8 = this.dataBitOffset + 8 * (n2 - this.minY) * scanlineStride2 + (n - this.minX) * this.pixelBitStride;
        int n9 = n5 * this.pixelBitStride;
        if ((n7 & 0x7) == (n8 & 0x7)) {
            final int n10 = n8 & 0x7;
            if (n10 != 0) {
                int n11 = 8 - n10;
                int n12 = n7 >> 3;
                int n13 = n8 >> 3;
                int n14 = 255 >> n10;
                if (n9 < n11) {
                    n14 &= 255 << n11 - n9;
                    n11 = n9;
                }
                for (int i = 0; i < n6; ++i) {
                    data2[n13] = (byte)((data2[n13] & ~n14) | (data[n12] & n14));
                    n12 += scanlineStride;
                    n13 += scanlineStride2;
                }
                n7 += n11;
                n8 += n11;
                n9 -= n11;
            }
            if (n9 >= 8) {
                int n15 = n7 >> 3;
                int n16 = n8 >> 3;
                final int n17 = n9 >> 3;
                if (n17 == scanlineStride && scanlineStride == scanlineStride2) {
                    System.arraycopy(data, n15, data2, n16, scanlineStride * n6);
                }
                else {
                    for (int j = 0; j < n6; ++j) {
                        System.arraycopy(data, n15, data2, n16, n17);
                        n15 += scanlineStride;
                        n16 += scanlineStride2;
                    }
                }
                final int n18 = n17 * 8;
                n7 += n18;
                n8 += n18;
                n9 -= n18;
            }
            if (n9 > 0) {
                int n19 = n7 >> 3;
                int n20 = n8 >> 3;
                final int n21 = 65280 >> n9 & 0xFF;
                for (int k = 0; k < n6; ++k) {
                    data2[n20] = (byte)((data2[n20] & ~n21) | (data[n19] & n21));
                    n19 += scanlineStride;
                    n20 += scanlineStride2;
                }
            }
        }
        else {
            final int n22 = n8 & 0x7;
            if (n22 != 0 || n9 < 8) {
                int n23 = 8 - n22;
                int n24 = n7 >> 3;
                int n25 = n8 >> 3;
                final int n26 = n7 & 0x7;
                final int n27 = 8 - n26;
                int n28 = 255 >> n22;
                if (n9 < n23) {
                    n28 &= 255 << n23 - n9;
                    n23 = n9;
                }
                final int n29 = data.length - 1;
                for (int l = 0; l < n6; ++l) {
                    final byte b = data[n24];
                    byte b2 = 0;
                    if (n24 < n29) {
                        b2 = data[n24 + 1];
                    }
                    data2[n25] = (byte)((data2[n25] & ~n28) | ((b << n26 | (b2 & 0xFF) >> n27) >> n22 & n28));
                    n24 += scanlineStride;
                    n25 += scanlineStride2;
                }
                n7 += n23;
                n8 += n23;
                n9 -= n23;
            }
            if (n9 >= 8) {
                final int n30 = n7 >> 3;
                final int n31 = n8 >> 3;
                final int n32 = n9 >> 3;
                final int n33 = n7 & 0x7;
                final int n34 = 8 - n33;
                for (int n35 = 0; n35 < n6; ++n35) {
                    int n36 = n30 + n35 * scanlineStride;
                    int n37 = n31 + n35 * scanlineStride2;
                    byte b3 = data[n36];
                    for (int n38 = 0; n38 < n32; ++n38) {
                        final byte b4 = data[n36 + 1];
                        data2[n37] = (byte)(b3 << n33 | (b4 & 0xFF) >> n34);
                        b3 = b4;
                        ++n36;
                        ++n37;
                    }
                }
                final int n39 = n32 * 8;
                n7 += n39;
                n8 += n39;
                n9 -= n39;
            }
            if (n9 > 0) {
                int n40 = n7 >> 3;
                int n41 = n8 >> 3;
                final int n42 = 65280 >> n9 & 0xFF;
                final int n43 = n7 & 0x7;
                final int n44 = 8 - n43;
                final int n45 = data.length - 1;
                for (int n46 = 0; n46 < n6; ++n46) {
                    final byte b5 = data[n40];
                    byte b6 = 0;
                    if (n40 < n45) {
                        b6 = data[n40 + 1];
                    }
                    data2[n41] = (byte)((data2[n41] & ~n42) | ((b5 << n43 | (b6 & 0xFF) >> n44) & n42));
                    n40 += scanlineStride;
                    n41 += scanlineStride2;
                }
            }
        }
        this.markDirty();
    }
    
    @Override
    public void setRect(final int n, final int n2, final Raster raster) {
        if (!(raster instanceof BytePackedRaster) || ((BytePackedRaster)raster).pixelBitStride != this.pixelBitStride) {
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
        this.setDataElements(minX2, minY2, minX, minY, width, height, (BytePackedRaster)raster);
    }
    
    @Override
    public void setDataElements(final int n, final int n2, final int n3, final int n4, final Object o) {
        this.putByteData(n, n2, n3, n4, (byte[])o);
    }
    
    public void putByteData(final int n, final int n2, final int n3, final int n4, final int n5, final byte[] array) {
        this.putByteData(n, n2, n3, n4, array);
    }
    
    public void putByteData(final int n, final int n2, final int n3, final int n4, final byte[] array) {
        if (n < this.minX || n2 < this.minY || n + n3 > this.maxX || n2 + n4 > this.maxY) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        if (n3 == 0 || n4 == 0) {
            return;
        }
        final int pixelBitStride = this.pixelBitStride;
        final int n5 = this.dataBitOffset + (n - this.minX) * pixelBitStride;
        int n6 = (n2 - this.minY) * this.scanlineStride;
        int n7 = 0;
        final byte[] data = this.data;
        for (int i = 0; i < n4; ++i) {
            int n8;
            int j;
            for (n8 = n5, j = 0; j < n3 && (n8 & 0x7) != 0x0; n8 += pixelBitStride, ++j) {
                final int n9 = this.shiftOffset - (n8 & 0x7);
                data[n6 + (n8 >> 3)] = (byte)((data[n6 + (n8 >> 3)] & ~(this.bitMask << n9)) | (array[n7++] & this.bitMask) << n9);
            }
            int n10 = n6 + (n8 >> 3);
            switch (pixelBitStride) {
                case 1: {
                    while (j < n3 - 7) {
                        data[n10++] = (byte)((array[n7++] & 0x1) << 7 | (array[n7++] & 0x1) << 6 | (array[n7++] & 0x1) << 5 | (array[n7++] & 0x1) << 4 | (array[n7++] & 0x1) << 3 | (array[n7++] & 0x1) << 2 | (array[n7++] & 0x1) << 1 | (array[n7++] & 0x1));
                        n8 += 8;
                        j += 8;
                    }
                    break;
                }
                case 2: {
                    while (j < n3 - 7) {
                        data[n10++] = (byte)((array[n7++] & 0x3) << 6 | (array[n7++] & 0x3) << 4 | (array[n7++] & 0x3) << 2 | (array[n7++] & 0x3));
                        data[n10++] = (byte)((array[n7++] & 0x3) << 6 | (array[n7++] & 0x3) << 4 | (array[n7++] & 0x3) << 2 | (array[n7++] & 0x3));
                        n8 += 16;
                        j += 8;
                    }
                    break;
                }
                case 4: {
                    while (j < n3 - 7) {
                        data[n10++] = (byte)((array[n7++] & 0xF) << 4 | (array[n7++] & 0xF));
                        data[n10++] = (byte)((array[n7++] & 0xF) << 4 | (array[n7++] & 0xF));
                        data[n10++] = (byte)((array[n7++] & 0xF) << 4 | (array[n7++] & 0xF));
                        data[n10++] = (byte)((array[n7++] & 0xF) << 4 | (array[n7++] & 0xF));
                        n8 += 32;
                        j += 8;
                    }
                    break;
                }
            }
            while (j < n3) {
                final int n11 = this.shiftOffset - (n8 & 0x7);
                data[n6 + (n8 >> 3)] = (byte)((data[n6 + (n8 >> 3)] & ~(this.bitMask << n11)) | (array[n7++] & this.bitMask) << n11);
                n8 += pixelBitStride;
                ++j;
            }
            n6 += this.scanlineStride;
        }
        this.markDirty();
    }
    
    @Override
    public int[] getPixels(final int n, final int n2, final int n3, final int n4, int[] array) {
        if (n < this.minX || n2 < this.minY || n + n3 > this.maxX || n2 + n4 > this.maxY) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        if (array == null) {
            array = new int[n3 * n4];
        }
        final int pixelBitStride = this.pixelBitStride;
        final int n5 = this.dataBitOffset + (n - this.minX) * pixelBitStride;
        int n6 = (n2 - this.minY) * this.scanlineStride;
        int n7 = 0;
        final byte[] data = this.data;
        for (int i = 0; i < n4; ++i) {
            int n8;
            int j;
            for (n8 = n5, j = 0; j < n3 && (n8 & 0x7) != 0x0; n8 += pixelBitStride, ++j) {
                array[n7++] = (this.bitMask & data[n6 + (n8 >> 3)] >> this.shiftOffset - (n8 & 0x7));
            }
            int n9 = n6 + (n8 >> 3);
            switch (pixelBitStride) {
                case 1: {
                    while (j < n3 - 7) {
                        final byte b = data[n9++];
                        array[n7++] = (b >> 7 & 0x1);
                        array[n7++] = (b >> 6 & 0x1);
                        array[n7++] = (b >> 5 & 0x1);
                        array[n7++] = (b >> 4 & 0x1);
                        array[n7++] = (b >> 3 & 0x1);
                        array[n7++] = (b >> 2 & 0x1);
                        array[n7++] = (b >> 1 & 0x1);
                        array[n7++] = (b & 0x1);
                        n8 += 8;
                        j += 8;
                    }
                    break;
                }
                case 2: {
                    while (j < n3 - 7) {
                        final byte b2 = data[n9++];
                        array[n7++] = (b2 >> 6 & 0x3);
                        array[n7++] = (b2 >> 4 & 0x3);
                        array[n7++] = (b2 >> 2 & 0x3);
                        array[n7++] = (b2 & 0x3);
                        final byte b3 = data[n9++];
                        array[n7++] = (b3 >> 6 & 0x3);
                        array[n7++] = (b3 >> 4 & 0x3);
                        array[n7++] = (b3 >> 2 & 0x3);
                        array[n7++] = (b3 & 0x3);
                        n8 += 16;
                        j += 8;
                    }
                    break;
                }
                case 4: {
                    while (j < n3 - 7) {
                        final byte b4 = data[n9++];
                        array[n7++] = (b4 >> 4 & 0xF);
                        array[n7++] = (b4 & 0xF);
                        final byte b5 = data[n9++];
                        array[n7++] = (b5 >> 4 & 0xF);
                        array[n7++] = (b5 & 0xF);
                        final byte b6 = data[n9++];
                        array[n7++] = (b6 >> 4 & 0xF);
                        array[n7++] = (b6 & 0xF);
                        final byte b7 = data[n9++];
                        array[n7++] = (b7 >> 4 & 0xF);
                        array[n7++] = (b7 & 0xF);
                        n8 += 32;
                        j += 8;
                    }
                    break;
                }
            }
            while (j < n3) {
                array[n7++] = (this.bitMask & data[n6 + (n8 >> 3)] >> this.shiftOffset - (n8 & 0x7));
                n8 += pixelBitStride;
                ++j;
            }
            n6 += this.scanlineStride;
        }
        return array;
    }
    
    @Override
    public void setPixels(final int n, final int n2, final int n3, final int n4, final int[] array) {
        if (n < this.minX || n2 < this.minY || n + n3 > this.maxX || n2 + n4 > this.maxY) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        final int pixelBitStride = this.pixelBitStride;
        final int n5 = this.dataBitOffset + (n - this.minX) * pixelBitStride;
        int n6 = (n2 - this.minY) * this.scanlineStride;
        int n7 = 0;
        final byte[] data = this.data;
        for (int i = 0; i < n4; ++i) {
            int n8;
            int j;
            for (n8 = n5, j = 0; j < n3 && (n8 & 0x7) != 0x0; n8 += pixelBitStride, ++j) {
                final int n9 = this.shiftOffset - (n8 & 0x7);
                data[n6 + (n8 >> 3)] = (byte)((data[n6 + (n8 >> 3)] & ~(this.bitMask << n9)) | (array[n7++] & this.bitMask) << n9);
            }
            int n10 = n6 + (n8 >> 3);
            switch (pixelBitStride) {
                case 1: {
                    while (j < n3 - 7) {
                        data[n10++] = (byte)((array[n7++] & 0x1) << 7 | (array[n7++] & 0x1) << 6 | (array[n7++] & 0x1) << 5 | (array[n7++] & 0x1) << 4 | (array[n7++] & 0x1) << 3 | (array[n7++] & 0x1) << 2 | (array[n7++] & 0x1) << 1 | (array[n7++] & 0x1));
                        n8 += 8;
                        j += 8;
                    }
                    break;
                }
                case 2: {
                    while (j < n3 - 7) {
                        data[n10++] = (byte)((array[n7++] & 0x3) << 6 | (array[n7++] & 0x3) << 4 | (array[n7++] & 0x3) << 2 | (array[n7++] & 0x3));
                        data[n10++] = (byte)((array[n7++] & 0x3) << 6 | (array[n7++] & 0x3) << 4 | (array[n7++] & 0x3) << 2 | (array[n7++] & 0x3));
                        n8 += 16;
                        j += 8;
                    }
                    break;
                }
                case 4: {
                    while (j < n3 - 7) {
                        data[n10++] = (byte)((array[n7++] & 0xF) << 4 | (array[n7++] & 0xF));
                        data[n10++] = (byte)((array[n7++] & 0xF) << 4 | (array[n7++] & 0xF));
                        data[n10++] = (byte)((array[n7++] & 0xF) << 4 | (array[n7++] & 0xF));
                        data[n10++] = (byte)((array[n7++] & 0xF) << 4 | (array[n7++] & 0xF));
                        n8 += 32;
                        j += 8;
                    }
                    break;
                }
            }
            while (j < n3) {
                final int n11 = this.shiftOffset - (n8 & 0x7);
                data[n6 + (n8 >> 3)] = (byte)((data[n6 + (n8 >> 3)] & ~(this.bitMask << n11)) | (array[n7++] & this.bitMask) << n11);
                n8 += pixelBitStride;
                ++j;
            }
            n6 += this.scanlineStride;
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
        return new BytePackedRaster(sampleModel, this.dataBuffer, new Rectangle(n5, n6, n3, n4), new Point(this.sampleModelTranslateX + (n5 - n), this.sampleModelTranslateY + (n6 - n2)), this);
    }
    
    @Override
    public WritableRaster createCompatibleWritableRaster(final int n, final int n2) {
        if (n <= 0 || n2 <= 0) {
            throw new RasterFormatException("negative " + ((n <= 0) ? "width" : "height"));
        }
        return new BytePackedRaster(this.sampleModel.createCompatibleSampleModel(n, n2), new Point(0, 0));
    }
    
    @Override
    public WritableRaster createCompatibleWritableRaster() {
        return this.createCompatibleWritableRaster(this.width, this.height);
    }
    
    private void verify(final boolean b) {
        if (this.dataBitOffset < 0) {
            throw new RasterFormatException("Data offsets must be >= 0");
        }
        if (this.width <= 0 || this.height <= 0 || this.height > Integer.MAX_VALUE / this.width) {
            throw new RasterFormatException("Invalid raster dimension");
        }
        if (this.width - 1 > Integer.MAX_VALUE / this.pixelBitStride) {
            throw new RasterFormatException("Invalid raster dimension");
        }
        if (this.minX - (long)this.sampleModelTranslateX < 0L || this.minY - (long)this.sampleModelTranslateY < 0L) {
            throw new RasterFormatException("Incorrect origin/translate: (" + this.minX + ", " + this.minY + ") / (" + this.sampleModelTranslateX + ", " + this.sampleModelTranslateY + ")");
        }
        if (this.scanlineStride < 0 || this.scanlineStride > Integer.MAX_VALUE / this.height) {
            throw new RasterFormatException("Invalid scanline stride");
        }
        if ((this.height > 1 || this.minY - this.sampleModelTranslateY > 0) && this.scanlineStride > this.data.length) {
            throw new RasterFormatException("Incorrect scanline stride: " + this.scanlineStride);
        }
        final long n = this.dataBitOffset + (this.height - 1) * (long)this.scanlineStride * 8L + (this.width - 1) * (long)this.pixelBitStride + this.pixelBitStride - 1L;
        if (n < 0L || n / 8L >= this.data.length) {
            throw new RasterFormatException("raster dimensions overflow array bounds");
        }
        if (b && this.height > 1 && (this.width * this.pixelBitStride - 1) / 8L >= this.scanlineStride) {
            throw new RasterFormatException("data for adjacent scanlines overlaps");
        }
    }
    
    @Override
    public String toString() {
        return new String("BytePackedRaster: width = " + this.width + " height = " + this.height + " #channels " + this.numBands + " xOff = " + this.sampleModelTranslateX + " yOff = " + this.sampleModelTranslateY);
    }
    
    static {
        NativeLibLoader.loadLibraries();
        initIDs();
    }
}
