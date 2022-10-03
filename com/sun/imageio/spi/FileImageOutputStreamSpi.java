package com.sun.imageio.spi;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.io.File;
import java.util.Locale;
import javax.imageio.spi.ImageOutputStreamSpi;

public class FileImageOutputStreamSpi extends ImageOutputStreamSpi
{
    private static final String vendorName = "Oracle Corporation";
    private static final String version = "1.0";
    private static final Class outputClass;
    
    public FileImageOutputStreamSpi() {
        super("Oracle Corporation", "1.0", FileImageOutputStreamSpi.outputClass);
    }
    
    @Override
    public String getDescription(final Locale locale) {
        return "Service provider that instantiates a FileImageOutputStream from a File";
    }
    
    @Override
    public ImageOutputStream createOutputStreamInstance(final Object o, final boolean b, final File file) {
        if (o instanceof File) {
            try {
                return new FileImageOutputStream((File)o);
            }
            catch (final Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
        throw new IllegalArgumentException();
    }
    
    static {
        outputClass = File.class;
    }
}
