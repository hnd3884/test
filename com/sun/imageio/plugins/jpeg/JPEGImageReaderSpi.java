package com.sun.imageio.plugins.jpeg;

import javax.imageio.IIOException;
import javax.imageio.ImageReader;
import java.io.IOException;
import java.util.Locale;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.spi.ImageReaderSpi;

public class JPEGImageReaderSpi extends ImageReaderSpi
{
    private static String[] writerSpiNames;
    
    public JPEGImageReaderSpi() {
        super("Oracle Corporation", "0.5", JPEG.names, JPEG.suffixes, JPEG.MIMETypes, "com.sun.imageio.plugins.jpeg.JPEGImageReader", new Class[] { ImageInputStream.class }, JPEGImageReaderSpi.writerSpiNames, true, "javax_imageio_jpeg_stream_1.0", "com.sun.imageio.plugins.jpeg.JPEGStreamMetadataFormat", null, null, true, "javax_imageio_jpeg_image_1.0", "com.sun.imageio.plugins.jpeg.JPEGImageMetadataFormat", null, null);
    }
    
    @Override
    public String getDescription(final Locale locale) {
        return "Standard JPEG Image Reader";
    }
    
    @Override
    public boolean canDecodeInput(final Object o) throws IOException {
        if (!(o instanceof ImageInputStream)) {
            return false;
        }
        final ImageInputStream imageInputStream = (ImageInputStream)o;
        imageInputStream.mark();
        final int read = imageInputStream.read();
        final int read2 = imageInputStream.read();
        imageInputStream.reset();
        return read == 255 && read2 == 216;
    }
    
    @Override
    public ImageReader createReaderInstance(final Object o) throws IIOException {
        return new JPEGImageReader(this);
    }
    
    static {
        JPEGImageReaderSpi.writerSpiNames = new String[] { "com.sun.imageio.plugins.jpeg.JPEGImageWriterSpi" };
    }
}
