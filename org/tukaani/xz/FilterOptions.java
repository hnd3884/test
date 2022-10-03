package org.tukaani.xz;

import java.io.IOException;
import java.io.InputStream;

public abstract class FilterOptions implements Cloneable
{
    public static int getEncoderMemoryUsage(final FilterOptions[] array) {
        int n = 0;
        for (int i = 0; i < array.length; ++i) {
            n += array[i].getEncoderMemoryUsage();
        }
        return n;
    }
    
    public static int getDecoderMemoryUsage(final FilterOptions[] array) {
        int n = 0;
        for (int i = 0; i < array.length; ++i) {
            n += array[i].getDecoderMemoryUsage();
        }
        return n;
    }
    
    public abstract int getEncoderMemoryUsage();
    
    public FinishableOutputStream getOutputStream(final FinishableOutputStream finishableOutputStream) {
        return this.getOutputStream(finishableOutputStream, ArrayCache.getDefaultCache());
    }
    
    public abstract FinishableOutputStream getOutputStream(final FinishableOutputStream p0, final ArrayCache p1);
    
    public abstract int getDecoderMemoryUsage();
    
    public InputStream getInputStream(final InputStream inputStream) throws IOException {
        return this.getInputStream(inputStream, ArrayCache.getDefaultCache());
    }
    
    public abstract InputStream getInputStream(final InputStream p0, final ArrayCache p1) throws IOException;
    
    abstract FilterEncoder getFilterEncoder();
    
    FilterOptions() {
    }
}
