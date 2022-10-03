package com.sun.imageio.plugins.gif;

import javax.imageio.ImageReader;
import java.io.IOException;
import java.util.Locale;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.spi.ImageReaderSpi;

public class GIFImageReaderSpi extends ImageReaderSpi
{
    private static final String vendorName = "Oracle Corporation";
    private static final String version = "1.0";
    private static final String[] names;
    private static final String[] suffixes;
    private static final String[] MIMETypes;
    private static final String readerClassName = "com.sun.imageio.plugins.gif.GIFImageReader";
    private static final String[] writerSpiNames;
    
    public GIFImageReaderSpi() {
        super("Oracle Corporation", "1.0", GIFImageReaderSpi.names, GIFImageReaderSpi.suffixes, GIFImageReaderSpi.MIMETypes, "com.sun.imageio.plugins.gif.GIFImageReader", new Class[] { ImageInputStream.class }, GIFImageReaderSpi.writerSpiNames, true, "javax_imageio_gif_stream_1.0", "com.sun.imageio.plugins.gif.GIFStreamMetadataFormat", null, null, true, "javax_imageio_gif_image_1.0", "com.sun.imageio.plugins.gif.GIFImageMetadataFormat", null, null);
    }
    
    @Override
    public String getDescription(final Locale locale) {
        return "Standard GIF image reader";
    }
    
    @Override
    public boolean canDecodeInput(final Object o) throws IOException {
        if (!(o instanceof ImageInputStream)) {
            return false;
        }
        final ImageInputStream imageInputStream = (ImageInputStream)o;
        final byte[] array = new byte[6];
        imageInputStream.mark();
        imageInputStream.readFully(array);
        imageInputStream.reset();
        return array[0] == 71 && array[1] == 73 && array[2] == 70 && array[3] == 56 && (array[4] == 55 || array[4] == 57) && array[5] == 97;
    }
    
    @Override
    public ImageReader createReaderInstance(final Object o) {
        return new GIFImageReader(this);
    }
    
    static {
        names = new String[] { "gif", "GIF" };
        suffixes = new String[] { "gif" };
        MIMETypes = new String[] { "image/gif" };
        writerSpiNames = new String[] { "com.sun.imageio.plugins.gif.GIFImageWriterSpi" };
    }
}
