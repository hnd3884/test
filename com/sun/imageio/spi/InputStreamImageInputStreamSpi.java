package com.sun.imageio.spi;

import java.io.IOException;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.imageio.stream.FileCacheImageInputStream;
import java.io.InputStream;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.util.Locale;
import javax.imageio.spi.ImageInputStreamSpi;

public class InputStreamImageInputStreamSpi extends ImageInputStreamSpi
{
    private static final String vendorName = "Oracle Corporation";
    private static final String version = "1.0";
    private static final Class inputClass;
    
    public InputStreamImageInputStreamSpi() {
        super("Oracle Corporation", "1.0", InputStreamImageInputStreamSpi.inputClass);
    }
    
    @Override
    public String getDescription(final Locale locale) {
        return "Service provider that instantiates a FileCacheImageInputStream or MemoryCacheImageInputStream from an InputStream";
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
    public ImageInputStream createInputStreamInstance(final Object o, final boolean b, final File file) throws IOException {
        if (!(o instanceof InputStream)) {
            throw new IllegalArgumentException();
        }
        final InputStream inputStream = (InputStream)o;
        if (b) {
            return new FileCacheImageInputStream(inputStream, file);
        }
        return new MemoryCacheImageInputStream(inputStream);
    }
    
    static {
        inputClass = InputStream.class;
    }
}
