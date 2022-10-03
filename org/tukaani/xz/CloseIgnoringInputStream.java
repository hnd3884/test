package org.tukaani.xz;

import java.io.InputStream;
import java.io.FilterInputStream;

public class CloseIgnoringInputStream extends FilterInputStream
{
    public CloseIgnoringInputStream(final InputStream inputStream) {
        super(inputStream);
    }
    
    @Override
    public void close() {
    }
}
