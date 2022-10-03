package com.sun.imageio.plugins.wbmp;

import javax.imageio.IIOException;
import javax.imageio.ImageWriter;
import java.awt.image.SampleModel;
import java.awt.image.MultiPixelPackedSampleModel;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.spi.ServiceRegistry;
import java.util.Locale;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.spi.ImageWriterSpi;

public class WBMPImageWriterSpi extends ImageWriterSpi
{
    private static String[] readerSpiNames;
    private static String[] formatNames;
    private static String[] entensions;
    private static String[] mimeType;
    private boolean registered;
    
    public WBMPImageWriterSpi() {
        super("Oracle Corporation", "1.0", WBMPImageWriterSpi.formatNames, WBMPImageWriterSpi.entensions, WBMPImageWriterSpi.mimeType, "com.sun.imageio.plugins.wbmp.WBMPImageWriter", new Class[] { ImageOutputStream.class }, WBMPImageWriterSpi.readerSpiNames, true, null, null, null, null, true, null, null, null, null);
        this.registered = false;
    }
    
    @Override
    public String getDescription(final Locale locale) {
        return "Standard WBMP Image Writer";
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
        final SampleModel sampleModel = imageTypeSpecifier.getSampleModel();
        return sampleModel instanceof MultiPixelPackedSampleModel && sampleModel.getSampleSize(0) == 1;
    }
    
    @Override
    public ImageWriter createWriterInstance(final Object o) throws IIOException {
        return new WBMPImageWriter(this);
    }
    
    static {
        WBMPImageWriterSpi.readerSpiNames = new String[] { "com.sun.imageio.plugins.wbmp.WBMPImageReaderSpi" };
        WBMPImageWriterSpi.formatNames = new String[] { "wbmp", "WBMP" };
        WBMPImageWriterSpi.entensions = new String[] { "wbmp" };
        WBMPImageWriterSpi.mimeType = new String[] { "image/vnd.wap.wbmp" };
    }
}
