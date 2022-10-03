package com.sun.imageio.plugins.jpeg;

import javax.imageio.IIOException;
import javax.imageio.ImageWriter;
import java.awt.image.SampleModel;
import javax.imageio.ImageTypeSpecifier;
import java.util.Locale;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.spi.ImageWriterSpi;

public class JPEGImageWriterSpi extends ImageWriterSpi
{
    private static String[] readerSpiNames;
    
    public JPEGImageWriterSpi() {
        super("Oracle Corporation", "0.5", JPEG.names, JPEG.suffixes, JPEG.MIMETypes, "com.sun.imageio.plugins.jpeg.JPEGImageWriter", new Class[] { ImageOutputStream.class }, JPEGImageWriterSpi.readerSpiNames, true, "javax_imageio_jpeg_stream_1.0", "com.sun.imageio.plugins.jpeg.JPEGStreamMetadataFormat", null, null, true, "javax_imageio_jpeg_image_1.0", "com.sun.imageio.plugins.jpeg.JPEGImageMetadataFormat", null, null);
    }
    
    @Override
    public String getDescription(final Locale locale) {
        return "Standard JPEG Image Writer";
    }
    
    @Override
    public boolean isFormatLossless() {
        return false;
    }
    
    @Override
    public boolean canEncodeImage(final ImageTypeSpecifier imageTypeSpecifier) {
        final SampleModel sampleModel = imageTypeSpecifier.getSampleModel();
        if (imageTypeSpecifier.getColorModel().hasAlpha()) {
            return false;
        }
        final int[] sampleSize = sampleModel.getSampleSize();
        int n = sampleSize[0];
        for (int i = 1; i < sampleSize.length; ++i) {
            if (sampleSize[i] > n) {
                n = sampleSize[i];
            }
        }
        return n >= 1 && n <= 8;
    }
    
    @Override
    public ImageWriter createWriterInstance(final Object o) throws IIOException {
        return new JPEGImageWriter(this);
    }
    
    static {
        JPEGImageWriterSpi.readerSpiNames = new String[] { "com.sun.imageio.plugins.jpeg.JPEGImageReaderSpi" };
    }
}
