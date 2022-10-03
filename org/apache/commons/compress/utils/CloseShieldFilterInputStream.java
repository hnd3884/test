package org.apache.commons.compress.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.FilterInputStream;

public class CloseShieldFilterInputStream extends FilterInputStream
{
    public CloseShieldFilterInputStream(final InputStream in) {
        super(in);
    }
    
    @Override
    public void close() throws IOException {
    }
}
