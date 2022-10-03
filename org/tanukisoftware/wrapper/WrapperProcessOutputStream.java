package org.tanukisoftware.wrapper;

import java.io.IOException;
import java.io.OutputStream;

public class WrapperProcessOutputStream extends OutputStream
{
    private long m_ptr;
    private boolean m_closed;
    
    private WrapperProcessOutputStream() {
    }
    
    private native void nativeWrite(final int p0);
    
    private native void nativeClose();
    
    public void write(final int b) throws IOException {
        synchronized (this) {
            if (this.m_closed || !WrapperManager.isNativeLibraryOk()) {
                throw new IOException(WrapperManager.getRes().getString("Stream is closed."));
            }
            this.nativeWrite(b);
        }
    }
    
    public void close() throws IOException {
        synchronized (this) {
            if (!this.m_closed) {
                if (WrapperManager.isNativeLibraryOk()) {
                    this.nativeClose();
                }
                this.m_closed = true;
            }
        }
    }
}
