package com.sun.imageio.plugins.wbmp;

import javax.imageio.IIOException;
import javax.imageio.ImageReader;
import java.io.IOException;
import com.sun.imageio.plugins.common.ReaderUtil;
import java.util.Locale;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.spi.ImageReaderSpi;

public class WBMPImageReaderSpi extends ImageReaderSpi
{
    private static final int MAX_WBMP_WIDTH = 1024;
    private static final int MAX_WBMP_HEIGHT = 768;
    private static String[] writerSpiNames;
    private static String[] formatNames;
    private static String[] entensions;
    private static String[] mimeType;
    private boolean registered;
    
    public WBMPImageReaderSpi() {
        super("Oracle Corporation", "1.0", WBMPImageReaderSpi.formatNames, WBMPImageReaderSpi.entensions, WBMPImageReaderSpi.mimeType, "com.sun.imageio.plugins.wbmp.WBMPImageReader", new Class[] { ImageInputStream.class }, WBMPImageReaderSpi.writerSpiNames, true, null, null, null, null, true, "javax_imageio_wbmp_1.0", "com.sun.imageio.plugins.wbmp.WBMPMetadataFormat", null, null);
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
        return "Standard WBMP Image Reader";
    }
    
    @Override
    public boolean canDecodeInput(final Object o) throws IOException {
        if (!(o instanceof ImageInputStream)) {
            return false;
        }
        final ImageInputStream imageInputStream = (ImageInputStream)o;
        imageInputStream.mark();
        try {
            final byte byte1 = imageInputStream.readByte();
            final byte byte2 = imageInputStream.readByte();
            if (byte1 != 0 || byte2 != 0) {
                return false;
            }
            final int multiByteInteger = ReaderUtil.readMultiByteInteger(imageInputStream);
            final int multiByteInteger2 = ReaderUtil.readMultiByteInteger(imageInputStream);
            if (multiByteInteger <= 0 || multiByteInteger2 <= 0) {
                return false;
            }
            final long length = imageInputStream.length();
            if (length == -1L) {
                return multiByteInteger < 1024 && multiByteInteger2 < 768;
            }
            return length - imageInputStream.getStreamPosition() == (multiByteInteger / 8 + ((multiByteInteger % 8 != 0) ? 1 : 0)) * (long)multiByteInteger2;
        }
        finally {
            imageInputStream.reset();
        }
    }
    
    @Override
    public ImageReader createReaderInstance(final Object o) throws IIOException {
        return new WBMPImageReader(this);
    }
    
    static {
        WBMPImageReaderSpi.writerSpiNames = new String[] { "com.sun.imageio.plugins.wbmp.WBMPImageWriterSpi" };
        WBMPImageReaderSpi.formatNames = new String[] { "wbmp", "WBMP" };
        WBMPImageReaderSpi.entensions = new String[] { "wbmp" };
        WBMPImageReaderSpi.mimeType = new String[] { "image/vnd.wap.wbmp" };
    }
}
