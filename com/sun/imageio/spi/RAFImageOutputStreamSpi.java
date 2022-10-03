package com.sun.imageio.spi;

import javax.imageio.stream.FileImageOutputStream;
import java.io.RandomAccessFile;
import javax.imageio.stream.ImageOutputStream;
import java.io.File;
import java.util.Locale;
import javax.imageio.spi.ImageOutputStreamSpi;

public class RAFImageOutputStreamSpi extends ImageOutputStreamSpi
{
    private static final String vendorName = "Oracle Corporation";
    private static final String version = "1.0";
    private static final Class outputClass;
    
    public RAFImageOutputStreamSpi() {
        super("Oracle Corporation", "1.0", RAFImageOutputStreamSpi.outputClass);
    }
    
    @Override
    public String getDescription(final Locale locale) {
        return "Service provider that instantiates a FileImageOutputStream from a RandomAccessFile";
    }
    
    @Override
    public ImageOutputStream createOutputStreamInstance(final Object o, final boolean b, final File file) {
        if (o instanceof RandomAccessFile) {
            try {
                return new FileImageOutputStream((RandomAccessFile)o);
            }
            catch (final Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
        throw new IllegalArgumentException("input not a RandomAccessFile!");
    }
    
    static {
        outputClass = RandomAccessFile.class;
    }
}
