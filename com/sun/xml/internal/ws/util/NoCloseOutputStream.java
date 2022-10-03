package com.sun.xml.internal.ws.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FilterOutputStream;

public class NoCloseOutputStream extends FilterOutputStream
{
    public NoCloseOutputStream(final OutputStream out) {
        super(out);
    }
    
    @Override
    public void close() throws IOException {
    }
    
    public void doClose() throws IOException {
        super.close();
    }
}
