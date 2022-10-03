package com.sun.imageio.plugins.bmp;

import javax.imageio.IIOException;
import javax.imageio.ImageReader;
import java.io.IOException;
import java.util.Locale;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.spi.ImageReaderSpi;

public class BMPImageReaderSpi extends ImageReaderSpi
{
    private static String[] writerSpiNames;
    private static String[] formatNames;
    private static String[] entensions;
    private static String[] mimeType;
    private boolean registered;
    
    public BMPImageReaderSpi() {
        super("Oracle Corporation", "1.0", BMPImageReaderSpi.formatNames, BMPImageReaderSpi.entensions, BMPImageReaderSpi.mimeType, "com.sun.imageio.plugins.bmp.BMPImageReader", new Class[] { ImageInputStream.class }, BMPImageReaderSpi.writerSpiNames, false, null, null, null, null, true, "javax_imageio_bmp_1.0", "com.sun.imageio.plugins.bmp.BMPMetadataFormat", null, null);
        this.registered = false;
    }
    
    @Override
    public void onRegistration(final ServiceRegistry serviceRegistry, final Class<?> clazz) {
        if (this.registered) {
            return;
        }
        this.registered = true;
    }
    
    @Override
    public String getDescription(final Locale locale) {
        return "Standard BMP Image Reader";
    }
    
    @Override
    public boolean canDecodeInput(final Object o) throws IOException {
        if (!(o instanceof ImageInputStream)) {
            return false;
        }
        final ImageInputStream imageInputStream = (ImageInputStream)o;
        final byte[] array = new byte[2];
        imageInputStream.mark();
        imageInputStream.readFully(array);
        imageInputStream.reset();
        return array[0] == 66 && array[1] == 77;
    }
    
    @Override
    public ImageReader createReaderInstance(final Object o) throws IIOException {
        return new BMPImageReader(this);
    }
    
    static {
        BMPImageReaderSpi.writerSpiNames = new String[] { "com.sun.imageio.plugins.bmp.BMPImageWriterSpi" };
        BMPImageReaderSpi.formatNames = new String[] { "bmp", "BMP" };
        BMPImageReaderSpi.entensions = new String[] { "bmp" };
        BMPImageReaderSpi.mimeType = new String[] { "image/bmp" };
    }
}
