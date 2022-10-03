package org.apache.commons.net.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FilterOutputStream;

public final class ToNetASCIIOutputStream extends FilterOutputStream
{
    private boolean __lastWasCR;
    
    public ToNetASCIIOutputStream(final OutputStream output) {
        super(output);
        this.__lastWasCR = false;
    }
    
    @Override
    public synchronized void write(final int ch) throws IOException {
        switch (ch) {
            case 13: {
                this.__lastWasCR = true;
                this.out.write(13);
                return;
            }
            case 10: {
                if (!this.__lastWasCR) {
                    this.out.write(13);
                    break;
                }
                break;
            }
        }
        this.__lastWasCR = false;
        this.out.write(ch);
    }
    
    @Override
    public synchronized void write(final byte[] buffer) throws IOException {
        this.write(buffer, 0, buffer.length);
    }
    
    @Override
    public synchronized void write(final byte[] buffer, int offset, int length) throws IOException {
        while (length-- > 0) {
            this.write(buffer[offset++]);
        }
    }
}
