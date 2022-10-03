package com.sun.xml.internal.ws.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.FilterInputStream;

public class NoCloseInputStream extends FilterInputStream
{
    public NoCloseInputStream(final InputStream is) {
        super(is);
    }
    
    @Override
    public void close() throws IOException {
    }
    
    public void doClose() throws IOException {
        super.close();
    }
}
