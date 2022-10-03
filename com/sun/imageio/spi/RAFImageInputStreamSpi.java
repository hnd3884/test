package com.sun.imageio.spi;

import javax.imageio.stream.FileImageInputStream;
import java.io.RandomAccessFile;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.util.Locale;
import javax.imageio.spi.ImageInputStreamSpi;

public class RAFImageInputStreamSpi extends ImageInputStreamSpi
{
    private static final String vendorName = "Oracle Corporation";
    private static final String version = "1.0";
    private static final Class inputClass;
    
    public RAFImageInputStreamSpi() {
        super("Oracle Corporation", "1.0", RAFImageInputStreamSpi.inputClass);
    }
    
    @Override
    public String getDescription(final Locale locale) {
        return "Service provider that instantiates a FileImageInputStream from a RandomAccessFile";
    }
    
    @Override
    public ImageInputStream createInputStreamInstance(final Object o, final boolean b, final File file) {
        if (o instanceof RandomAccessFile) {
            try {
                return new FileImageInputStream((RandomAccessFile)o);
            }
            catch (final Exception ex) {
                return null;
            }
        }
        throw new IllegalArgumentException("input not a RandomAccessFile!");
    }
    
    static {
        inputClass = RandomAccessFile.class;
    }
}
