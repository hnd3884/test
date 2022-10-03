package com.sun.imageio.stream;

import java.io.IOException;
import javax.imageio.stream.ImageInputStream;

public class StreamFinalizer
{
    private ImageInputStream stream;
    
    public StreamFinalizer(final ImageInputStream stream) {
        this.stream = stream;
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            this.stream.close();
        }
        catch (final IOException ex) {}
        finally {
            this.stream = null;
            super.finalize();
        }
    }
}
