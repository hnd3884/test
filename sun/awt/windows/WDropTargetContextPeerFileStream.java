package sun.awt.windows;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileInputStream;

final class WDropTargetContextPeerFileStream extends FileInputStream
{
    private long stgmedium;
    
    WDropTargetContextPeerFileStream(final String s, final long stgmedium) throws FileNotFoundException {
        super(s);
        this.stgmedium = stgmedium;
    }
    
    @Override
    public void close() throws IOException {
        if (this.stgmedium != 0L) {
            super.close();
            this.freeStgMedium(this.stgmedium);
            this.stgmedium = 0L;
        }
    }
    
    private native void freeStgMedium(final long p0);
}
