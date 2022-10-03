package com.sun.imageio.plugins.wbmp;

import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.image.DataBufferByte;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.Rectangle;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.ImageReadParam;
import java.awt.image.RenderedImage;
import javax.imageio.ImageTypeSpecifier;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import com.sun.imageio.plugins.common.ReaderUtil;
import javax.imageio.IIOException;
import java.io.IOException;
import com.sun.imageio.plugins.common.I18N;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.ImageReader;

public class WBMPImageReader extends ImageReader
{
    private ImageInputStream iis;
    private boolean gotHeader;
    private int width;
    private int height;
    private int wbmpType;
    private WBMPMetadata metadata;
    
    public WBMPImageReader(final ImageReaderSpi imageReaderSpi) {
        super(imageReaderSpi);
        this.iis = null;
        this.gotHeader = false;
    }
    
    @Override
    public void setInput(final Object o, final boolean b, final boolean b2) {
        super.setInput(o, b, b2);
        this.iis = (ImageInputStream)o;
        this.gotHeader = false;
    }
    
    @Override
    public int getNumImages(final boolean b) throws IOException {
        if (this.iis == null) {
            throw new IllegalStateException(I18N.getString("GetNumImages0"));
        }
        if (this.seekForwardOnly && b) {
            throw new IllegalStateException(I18N.getString("GetNumImages1"));
        }
        return 1;
    }
    
    @Override
    public int getWidth(final int n) throws IOException {
        this.checkIndex(n);
        this.readHeader();
        return this.width;
    }
    
    @Override
    public int getHeight(final int n) throws IOException {
        this.checkIndex(n);
        this.readHeader();
        return this.height;
    }
    
    @Override
    public boolean isRandomAccessEasy(final int n) throws IOException {
        this.checkIndex(n);
        return true;
    }
    
    private void checkIndex(final int n) {
        if (n != 0) {
            throw new IndexOutOfBoundsException(I18N.getString("WBMPImageReader0"));
        }
    }
    
    public void readHeader() throws IOException {
        if (this.gotHeader) {
            return;
        }
        if (this.iis == null) {
            throw new IllegalStateException("Input source not set!");
        }
        this.metadata = new WBMPMetadata();
        this.wbmpType = this.iis.readByte();
        if (this.iis.readByte() != 0 || !this.isValidWbmpType(this.wbmpType)) {
            throw new IIOException(I18N.getString("WBMPImageReader2"));
        }
        this.metadata.wbmpType = this.wbmpType;
        this.width = ReaderUtil.readMultiByteInteger(this.iis);
        this.metadata.width = this.width;
        this.height = ReaderUtil.readMultiByteInteger(this.iis);
        this.metadata.height = this.height;
        this.gotHeader = true;
    }
    
    @Override
    public Iterator getImageTypes(final int n) throws IOException {
        this.checkIndex(n);
        this.readHeader();
        final BufferedImage bufferedImage = new BufferedImage(1, 1, 12);
        final ArrayList list = new ArrayList(1);
        list.add(new ImageTypeSpecifier(bufferedImage));
        return list.iterator();
    }
    
    @Override
    public ImageReadParam getDefaultReadParam() {
        return new ImageReadParam();
    }
    
    @Override
    public IIOMetadata getImageMetadata(final int n) throws IOException {
        this.checkIndex(n);
        if (this.metadata == null) {
            this.readHeader();
        }
        return this.metadata;
    }
    
    @Override
    public IIOMetadata getStreamMetadata() throws IOException {
        return null;
    }
    
    @Override
    public BufferedImage read(final int n, ImageReadParam defaultReadParam) throws IOException {
        if (this.iis == null) {
            throw new IllegalStateException(I18N.getString("WBMPImageReader1"));
        }
        this.checkIndex(n);
        this.clearAbortRequest();
        this.processImageStarted(n);
        if (defaultReadParam == null) {
            defaultReadParam = this.getDefaultReadParam();
        }
        this.readHeader();
        final Rectangle rectangle = new Rectangle(0, 0, 0, 0);
        final Rectangle rectangle2 = new Rectangle(0, 0, 0, 0);
        ImageReader.computeRegions(defaultReadParam, this.width, this.height, defaultReadParam.getDestination(), rectangle, rectangle2);
        final int sourceXSubsampling = defaultReadParam.getSourceXSubsampling();
        final int sourceYSubsampling = defaultReadParam.getSourceYSubsampling();
        defaultReadParam.getSubsamplingXOffset();
        defaultReadParam.getSubsamplingYOffset();
        BufferedImage destination = defaultReadParam.getDestination();
        if (destination == null) {
            destination = new BufferedImage(rectangle2.x + rectangle2.width, rectangle2.y + rectangle2.height, 12);
        }
        final boolean b = rectangle2.equals(new Rectangle(0, 0, this.width, this.height)) && rectangle2.equals(new Rectangle(0, 0, destination.getWidth(), destination.getHeight()));
        final WritableRaster writableTile = destination.getWritableTile(0, 0);
        final MultiPixelPackedSampleModel multiPixelPackedSampleModel = (MultiPixelPackedSampleModel)destination.getSampleModel();
        if (b) {
            if (this.abortRequested()) {
                this.processReadAborted();
                return destination;
            }
            this.iis.read(((DataBufferByte)writableTile.getDataBuffer()).getData(), 0, this.height * multiPixelPackedSampleModel.getScanlineStride());
            this.processImageUpdate(destination, 0, 0, this.width, this.height, 1, 1, new int[] { 0 });
            this.processImageProgress(100.0f);
        }
        else {
            final int n2 = (this.width + 7) / 8;
            final byte[] array = new byte[n2];
            final byte[] data = ((DataBufferByte)writableTile.getDataBuffer()).getData();
            final int scanlineStride = multiPixelPackedSampleModel.getScanlineStride();
            this.iis.skipBytes(n2 * rectangle.y);
            final int n3 = n2 * (sourceYSubsampling - 1);
            final int[] array2 = new int[rectangle2.width];
            final int[] array3 = new int[rectangle2.width];
            final int[] array4 = new int[rectangle2.width];
            final int[] array5 = new int[rectangle2.width];
            for (int i = rectangle2.x, x = rectangle.x, n4 = 0; i < rectangle2.x + rectangle2.width; ++i, ++n4, x += sourceXSubsampling) {
                array4[n4] = x >> 3;
                array2[n4] = 7 - (x & 0x7);
                array5[n4] = i >> 3;
                array3[n4] = 7 - (i & 0x7);
            }
            int j = 0;
            int y = rectangle.y;
            int n5 = rectangle2.y * scanlineStride;
            while (j < rectangle2.height) {
                if (this.abortRequested()) {
                    break;
                }
                this.iis.read(array, 0, n2);
                for (int k = 0; k < rectangle2.width; ++k) {
                    final int n6 = array[array4[k]] >> array2[k] & 0x1;
                    final byte[] array6 = data;
                    final int n7 = n5 + array5[k];
                    array6[n7] |= (byte)(n6 << array3[k]);
                }
                n5 += scanlineStride;
                this.iis.skipBytes(n3);
                this.processImageUpdate(destination, 0, j, rectangle2.width, 1, 1, 1, new int[] { 0 });
                this.processImageProgress(100.0f * j / rectangle2.height);
                ++j;
                y += sourceYSubsampling;
            }
        }
        if (this.abortRequested()) {
            this.processReadAborted();
        }
        else {
            this.processImageComplete();
        }
        return destination;
    }
    
    @Override
    public boolean canReadRaster() {
        return true;
    }
    
    @Override
    public Raster readRaster(final int n, final ImageReadParam imageReadParam) throws IOException {
        return this.read(n, imageReadParam).getData();
    }
    
    @Override
    public void reset() {
        super.reset();
        this.iis = null;
        this.gotHeader = false;
    }
    
    boolean isValidWbmpType(final int n) {
        return n == 0;
    }
}
