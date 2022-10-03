package org.apache.taglibs.standard.util;

import java.io.IOException;
import java.io.Writer;

public class UnclosableWriter extends Writer
{
    private Writer w;
    
    public UnclosableWriter(final Writer w) {
        this.w = w;
    }
    
    @Override
    public void close() {
    }
    
    @Override
    public void flush() {
    }
    
    @Override
    public void write(final char[] cbuf, final int off, final int len) throws IOException {
        this.w.write(cbuf, off, len);
    }
}
