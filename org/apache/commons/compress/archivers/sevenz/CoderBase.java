package org.apache.commons.compress.archivers.sevenz;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import org.apache.commons.compress.utils.ByteUtils;

abstract class CoderBase
{
    private final Class<?>[] acceptableOptions;
    
    protected CoderBase(final Class<?>... acceptableOptions) {
        this.acceptableOptions = acceptableOptions;
    }
    
    boolean canAcceptOptions(final Object opts) {
        for (final Class<?> c : this.acceptableOptions) {
            if (c.isInstance(opts)) {
                return true;
            }
        }
        return false;
    }
    
    byte[] getOptionsAsProperties(final Object options) throws IOException {
        return ByteUtils.EMPTY_BYTE_ARRAY;
    }
    
    Object getOptionsFromCoder(final Coder coder, final InputStream in) throws IOException {
        return null;
    }
    
    abstract InputStream decode(final String p0, final InputStream p1, final long p2, final Coder p3, final byte[] p4, final int p5) throws IOException;
    
    OutputStream encode(final OutputStream out, final Object options) throws IOException {
        throw new UnsupportedOperationException("Method doesn't support writing");
    }
    
    protected static int numberOptionOrDefault(final Object options, final int defaultValue) {
        return (options instanceof Number) ? ((Number)options).intValue() : defaultValue;
    }
}
