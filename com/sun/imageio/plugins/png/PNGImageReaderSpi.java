package com.sun.imageio.plugins.png;

import javax.imageio.ImageReader;
import java.io.IOException;
import java.util.Locale;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.spi.ImageReaderSpi;

public class PNGImageReaderSpi extends ImageReaderSpi
{
    private static final String vendorName = "Oracle Corporation";
    private static final String version = "1.0";
    private static final String[] names;
    private static final String[] suffixes;
    private static final String[] MIMETypes;
    private static final String readerClassName = "com.sun.imageio.plugins.png.PNGImageReader";
    private static final String[] writerSpiNames;
    
    public PNGImageReaderSpi() {
        super("Oracle Corporation", "1.0", PNGImageReaderSpi.names, PNGImageReaderSpi.suffixes, PNGImageReaderSpi.MIMETypes, "com.sun.imageio.plugins.png.PNGImageReader", new Class[] { ImageInputStream.class }, PNGImageReaderSpi.writerSpiNames, false, null, null, null, null, true, "javax_imageio_png_1.0", "com.sun.imageio.plugins.png.PNGMetadataFormat", null, null);
    }
    
    @Override
    public String getDescription(final Locale locale) {
        return "Standard PNG image reader";
    }
    
    @Override
    public boolean canDecodeInput(final Object o) throws IOException {
        if (!(o instanceof ImageInputStream)) {
            return false;
        }
        final ImageInputStream imageInputStream = (ImageInputStream)o;
        final byte[] array = new byte[8];
        imageInputStream.mark();
        imageInputStream.readFully(array);
        imageInputStream.reset();
        return array[0] == -119 && array[1] == 80 && array[2] == 78 && array[3] == 71 && array[4] == 13 && array[5] == 10 && array[6] == 26 && array[7] == 10;
    }
    
    @Override
    public ImageReader createReaderInstance(final Object o) {
        return new PNGImageReader(this);
    }
    
    static {
        names = new String[] { "png", "PNG" };
        suffixes = new String[] { "png" };
        MIMETypes = new String[] { "image/png", "image/x-png" };
        writerSpiNames = new String[] { "com.sun.imageio.plugins.png.PNGImageWriterSpi" };
    }
}
