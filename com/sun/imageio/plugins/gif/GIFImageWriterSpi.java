package com.sun.imageio.plugins.gif;

import javax.imageio.ImageWriter;
import java.util.Locale;
import java.awt.image.ColorModel;
import java.awt.image.SampleModel;
import com.sun.imageio.plugins.common.PaletteBuilder;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.spi.ImageWriterSpi;

public class GIFImageWriterSpi extends ImageWriterSpi
{
    private static final String vendorName = "Oracle Corporation";
    private static final String version = "1.0";
    private static final String[] names;
    private static final String[] suffixes;
    private static final String[] MIMETypes;
    private static final String writerClassName = "com.sun.imageio.plugins.gif.GIFImageWriter";
    private static final String[] readerSpiNames;
    
    public GIFImageWriterSpi() {
        super("Oracle Corporation", "1.0", GIFImageWriterSpi.names, GIFImageWriterSpi.suffixes, GIFImageWriterSpi.MIMETypes, "com.sun.imageio.plugins.gif.GIFImageWriter", new Class[] { ImageOutputStream.class }, GIFImageWriterSpi.readerSpiNames, true, "javax_imageio_gif_stream_1.0", "com.sun.imageio.plugins.gif.GIFStreamMetadataFormat", null, null, true, "javax_imageio_gif_image_1.0", "com.sun.imageio.plugins.gif.GIFImageMetadataFormat", null, null);
    }
    
    @Override
    public boolean canEncodeImage(final ImageTypeSpecifier imageTypeSpecifier) {
        if (imageTypeSpecifier == null) {
            throw new IllegalArgumentException("type == null!");
        }
        final SampleModel sampleModel = imageTypeSpecifier.getSampleModel();
        final ColorModel colorModel = imageTypeSpecifier.getColorModel();
        return (sampleModel.getNumBands() == 1 && sampleModel.getSampleSize(0) <= 8 && sampleModel.getWidth() <= 65535 && sampleModel.getHeight() <= 65535 && (colorModel == null || colorModel.getComponentSize()[0] <= 8)) || PaletteBuilder.canCreatePalette(imageTypeSpecifier);
    }
    
    @Override
    public String getDescription(final Locale locale) {
        return "Standard GIF image writer";
    }
    
    @Override
    public ImageWriter createWriterInstance(final Object o) {
        return new GIFImageWriter(this);
    }
    
    static {
        names = new String[] { "gif", "GIF" };
        suffixes = new String[] { "gif" };
        MIMETypes = new String[] { "image/gif" };
        readerSpiNames = new String[] { "com.sun.imageio.plugins.gif.GIFImageReaderSpi" };
    }
}
