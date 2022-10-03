package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.io.IOException;
import java.io.OutputStream;

class AppOutputStream extends OutputStream
{
    private SSLSocketImpl c;
    OutputRecord r;
    private final byte[] oneByte;
    
    AppOutputStream(final SSLSocketImpl conn) {
        this.oneByte = new byte[1];
        this.r = new OutputRecord((byte)23);
        this.c = conn;
    }
    
    @Override
    public synchronized void write(final byte[] b, int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        }
        if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return;
        }
        this.c.checkWrite();
        boolean isFirstRecordOfThePayload = true;
        try {
            do {
                boolean holdRecord = false;
                int howmuch;
                if (isFirstRecordOfThePayload && this.c.needToSplitPayload()) {
                    howmuch = Math.min(1, this.r.availableDataBytes());
                    if (len != 1 && howmuch == 1) {
                        holdRecord = true;
                    }
                }
                else {
                    howmuch = Math.min(len, this.r.availableDataBytes());
                }
                if (isFirstRecordOfThePayload && howmuch != 0) {
                    isFirstRecordOfThePayload = false;
                }
                if (howmuch > 0) {
                    this.r.write(b, off, howmuch);
                    off += howmuch;
                    len -= howmuch;
                }
                this.c.writeRecord(this.r, holdRecord);
                this.c.checkWrite();
            } while (len > 0);
        }
        catch (final Exception e) {
            this.c.handleException(e);
        }
    }
    
    @Override
    public synchronized void write(final int i) throws IOException {
        this.oneByte[0] = (byte)i;
        this.write(this.oneByte, 0, 1);
    }
    
    @Override
    public void close() throws IOException {
        this.c.close();
    }
}
