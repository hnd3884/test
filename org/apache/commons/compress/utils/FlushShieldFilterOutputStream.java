package org.apache.commons.compress.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FilterOutputStream;

public class FlushShieldFilterOutputStream extends FilterOutputStream
{
    public FlushShieldFilterOutputStream(final OutputStream out) {
        super(out);
    }
    
    @Override
    public void flush() throws IOException {
    }
}
