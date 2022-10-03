package com.sun.imageio.stream;

import java.io.IOException;
import java.io.Closeable;
import sun.java2d.DisposerRecord;

public class CloseableDisposerRecord implements DisposerRecord
{
    private Closeable closeable;
    
    public CloseableDisposerRecord(final Closeable closeable) {
        this.closeable = closeable;
    }
    
    @Override
    public synchronized void dispose() {
        if (this.closeable != null) {
            try {
                this.closeable.close();
            }
            catch (final IOException ex) {}
            finally {
                this.closeable = null;
            }
        }
    }
}
