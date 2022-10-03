package com.sun.imageio.plugins.common;

import java.io.IOException;
import javax.imageio.stream.ImageInputStream;
import java.io.InputStream;

public class InputStreamAdapter extends InputStream
{
    ImageInputStream stream;
    
    public InputStreamAdapter(final ImageInputStream stream) {
        this.stream = stream;
    }
    
    @Override
    public int read() throws IOException {
        return this.stream.read();
    }
    
    @Override
    public int read(final byte[] array, final int n, final int n2) throws IOException {
        return this.stream.read(array, n, n2);
    }
}
