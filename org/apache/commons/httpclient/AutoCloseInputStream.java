package org.apache.commons.httpclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.FilterInputStream;

class AutoCloseInputStream extends FilterInputStream
{
    private boolean streamOpen;
    private boolean selfClosed;
    private ResponseConsumedWatcher watcher;
    
    public AutoCloseInputStream(final InputStream in, final ResponseConsumedWatcher watcher) {
        super(in);
        this.streamOpen = true;
        this.selfClosed = false;
        this.watcher = null;
        this.watcher = watcher;
    }
    
    public int read() throws IOException {
        int l = -1;
        if (this.isReadAllowed()) {
            l = super.read();
            this.checkClose(l);
        }
        return l;
    }
    
    public int read(final byte[] b, final int off, final int len) throws IOException {
        int l = -1;
        if (this.isReadAllowed()) {
            l = super.read(b, off, len);
            this.checkClose(l);
        }
        return l;
    }
    
    public int read(final byte[] b) throws IOException {
        int l = -1;
        if (this.isReadAllowed()) {
            l = super.read(b);
            this.checkClose(l);
        }
        return l;
    }
    
    public int available() throws IOException {
        int a = 0;
        if (this.isReadAllowed()) {
            a = super.available();
        }
        return a;
    }
    
    public void close() throws IOException {
        if (!this.selfClosed) {
            this.selfClosed = true;
            this.notifyWatcher();
        }
    }
    
    private void checkClose(final int readResult) throws IOException {
        if (readResult == -1) {
            this.notifyWatcher();
        }
    }
    
    private boolean isReadAllowed() throws IOException {
        if (!this.streamOpen && this.selfClosed) {
            throw new IOException("Attempted read on closed stream.");
        }
        return this.streamOpen;
    }
    
    private void notifyWatcher() throws IOException {
        if (this.streamOpen) {
            super.close();
            this.streamOpen = false;
            if (this.watcher != null) {
                this.watcher.responseConsumed();
            }
        }
    }
}
