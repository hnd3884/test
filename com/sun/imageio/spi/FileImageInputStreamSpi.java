package com.sun.imageio.spi;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.util.Locale;
import javax.imageio.spi.ImageInputStreamSpi;

public class FileImageInputStreamSpi extends ImageInputStreamSpi
{
    private static final String vendorName = "Oracle Corporation";
    private static final String version = "1.0";
    private static final Class inputClass;
    
    public FileImageInputStreamSpi() {
        super("Oracle Corporation", "1.0", FileImageInputStreamSpi.inputClass);
    }
    
    @Override
    public String getDescription(final Locale locale) {
        return "Service provider that instantiates a FileImageInputStream from a File";
    }
    
    @Override
    public ImageInputStream createInputStreamInstance(final Object o, final boolean b, final File file) {
        if (o instanceof File) {
            try {
                return new FileImageInputStream((File)o);
            }
            catch (final Exception ex) {
                return null;
            }
        }
        throw new IllegalArgumentException();
    }
    
    static {
        inputClass = File.class;
    }
}
