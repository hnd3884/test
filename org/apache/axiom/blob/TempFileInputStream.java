package org.apache.axiom.blob;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileInputStream;

final class TempFileInputStream extends FileInputStream
{
    private long markPosition;
    
    TempFileInputStream(final File file) throws FileNotFoundException {
        super(file);
    }
    
    @Override
    public boolean markSupported() {
        return true;
    }
    
    @Override
    public synchronized void mark(final int readlimit) {
        try {
            this.markPosition = this.getChannel().position();
        }
        catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    @Override
    public synchronized void reset() throws IOException {
        this.getChannel().position(this.markPosition);
    }
}
