package com.sun.imageio.spi;

import java.io.IOException;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.imageio.stream.FileCacheImageOutputStream;
import java.io.OutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.io.File;
import java.util.Locale;
import javax.imageio.spi.ImageOutputStreamSpi;

public class OutputStreamImageOutputStreamSpi extends ImageOutputStreamSpi
{
    private static final String vendorName = "Oracle Corporation";
    private static final String version = "1.0";
    private static final Class outputClass;
    
    public OutputStreamImageOutputStreamSpi() {
        super("Oracle Corporation", "1.0", OutputStreamImageOutputStreamSpi.outputClass);
    }
    
    @Override
    public String getDescription(final Locale locale) {
        return "Service provider that instantiates an OutputStreamImageOutputStream from an OutputStream";
    }
    
    @Override
    public boolean canUseCacheFile() {
        return true;
    }
    
    @Override
    public boolean needsCacheFile() {
        return false;
    }
    
    @Override
    public ImageOutputStream createOutputStreamInstance(final Object o, final boolean b, final File file) throws IOException {
        if (!(o instanceof OutputStream)) {
            throw new IllegalArgumentException();
        }
        final OutputStream outputStream = (OutputStream)o;
        if (b) {
            return new FileCacheImageOutputStream(outputStream, file);
        }
        return new MemoryCacheImageOutputStream(outputStream);
    }
    
    static {
        outputClass = OutputStream.class;
    }
}
