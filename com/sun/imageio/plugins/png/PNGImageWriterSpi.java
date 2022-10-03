package com.sun.imageio.plugins.png;

import javax.imageio.ImageWriter;
import java.util.Locale;
import java.awt.image.ColorModel;
import java.awt.image.SampleModel;
import java.awt.image.IndexColorModel;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.spi.ImageWriterSpi;

public class PNGImageWriterSpi extends ImageWriterSpi
{
    private static final String vendorName = "Oracle Corporation";
    private static final String version = "1.0";
    private static final String[] names;
    private static final String[] suffixes;
    private static final String[] MIMETypes;
    private static final String writerClassName = "com.sun.imageio.plugins.png.PNGImageWriter";
    private static final String[] readerSpiNames;
    
    public PNGImageWriterSpi() {
        super("Oracle Corporation", "1.0", PNGImageWriterSpi.names, PNGImageWriterSpi.suffixes, PNGImageWriterSpi.MIMETypes, "com.sun.imageio.plugins.png.PNGImageWriter", new Class[] { ImageOutputStream.class }, PNGImageWriterSpi.readerSpiNames, false, null, null, null, null, true, "javax_imageio_png_1.0", "com.sun.imageio.plugins.png.PNGMetadataFormat", null, null);
    }
    
    @Override
    public boolean canEncodeImage(final ImageTypeSpecifier imageTypeSpecifier) {
        final SampleModel sampleModel = imageTypeSpecifier.getSampleModel();
        final ColorModel colorModel = imageTypeSpecifier.getColorModel();
        final int[] sampleSize = sampleModel.getSampleSize();
        int n = sampleSize[0];
        for (int i = 1; i < sampleSize.length; ++i) {
            if (sampleSize[i] > n) {
                n = sampleSize[i];
            }
        }
        if (n < 1 || n > 16) {
            return false;
        }
        final int numBands = sampleModel.getNumBands();
        if (numBands < 1 || numBands > 4) {
            return false;
        }
        final boolean hasAlpha = colorModel.hasAlpha();
        return colorModel instanceof IndexColorModel || (((numBands != 1 && numBands != 3) || !hasAlpha) && ((numBands != 2 && numBands != 4) || hasAlpha));
    }
    
    @Override
    public String getDescription(final Locale locale) {
        return "Standard PNG image writer";
    }
    
    @Override
    public ImageWriter createWriterInstance(final Object o) {
        return new PNGImageWriter(this);
    }
    
    static {
        names = new String[] { "png", "PNG" };
        suffixes = new String[] { "png" };
        MIMETypes = new String[] { "image/png", "image/x-png" };
        readerSpiNames = new String[] { "com.sun.imageio.plugins.png.PNGImageReaderSpi" };
    }
}
