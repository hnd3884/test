package com.sun.imageio.plugins.bmp;

import javax.imageio.IIOException;
import javax.imageio.ImageWriter;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.spi.ServiceRegistry;
import java.util.Locale;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.spi.ImageWriterSpi;

public class BMPImageWriterSpi extends ImageWriterSpi
{
    private static String[] readerSpiNames;
    private static String[] formatNames;
    private static String[] entensions;
    private static String[] mimeType;
    private boolean registered;
    
    public BMPImageWriterSpi() {
        super("Oracle Corporation", "1.0", BMPImageWriterSpi.formatNames, BMPImageWriterSpi.entensions, BMPImageWriterSpi.mimeType, "com.sun.imageio.plugins.bmp.BMPImageWriter", new Class[] { ImageOutputStream.class }, BMPImageWriterSpi.readerSpiNames, false, null, null, null, null, true, "javax_imageio_bmp_1.0", "com.sun.imageio.plugins.bmp.BMPMetadataFormat", null, null);
        this.registered = false;
    }
    
    @Override
    public String getDescription(final Locale locale) {
        return "Standard BMP Image Writer";
    }
    
    @Override
    public void onRegistration(final ServiceRegistry serviceRegistry, final Class<?> clazz) {
        if (this.registered) {
            return;
        }
        this.registered = true;
    }
    
    @Override
    public boolean canEncodeImage(final ImageTypeSpecifier imageTypeSpecifier) {
        final int dataType = imageTypeSpecifier.getSampleModel().getDataType();
        if (dataType < 0 || dataType > 3) {
            return false;
        }
        final SampleModel sampleModel = imageTypeSpecifier.getSampleModel();
        final int numBands = sampleModel.getNumBands();
        return (numBands == 1 || numBands == 3) && (numBands != 1 || dataType == 0) && (dataType <= 0 || sampleModel instanceof SinglePixelPackedSampleModel);
    }
    
    @Override
    public ImageWriter createWriterInstance(final Object o) throws IIOException {
        return new BMPImageWriter(this);
    }
    
    static {
        BMPImageWriterSpi.readerSpiNames = new String[] { "com.sun.imageio.plugins.bmp.BMPImageReaderSpi" };
        BMPImageWriterSpi.formatNames = new String[] { "bmp", "BMP" };
        BMPImageWriterSpi.entensions = new String[] { "bmp" };
        BMPImageWriterSpi.mimeType = new String[] { "image/bmp" };
    }
}
