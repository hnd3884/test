package org.apache.poi.util;

import java.io.InputStream;
import java.io.FilterInputStream;

public class CloseIgnoringInputStream extends FilterInputStream
{
    public CloseIgnoringInputStream(final InputStream in) {
        super(in);
    }
    
    @Override
    public void close() {
    }
}
