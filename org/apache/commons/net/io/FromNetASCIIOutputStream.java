package org.apache.commons.net.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FilterOutputStream;

public final class FromNetASCIIOutputStream extends FilterOutputStream
{
    private boolean __lastWasCR;
    
    public FromNetASCIIOutputStream(final OutputStream output) {
        super(output);
        this.__lastWasCR = false;
    }
    
    private void __write(final int ch) throws IOException {
        switch (ch) {
            case 13: {
                this.__lastWasCR = true;
                break;
            }
            case 10: {
                if (this.__lastWasCR) {
                    this.out.write(FromNetASCIIInputStream._lineSeparatorBytes);
                    this.__lastWasCR = false;
                    break;
                }
                this.__lastWasCR = false;
                this.out.write(10);
                break;
            }
            default: {
                if (this.__lastWasCR) {
                    this.out.write(13);
                    this.__lastWasCR = false;
                }
                this.out.write(ch);
                break;
            }
        }
    }
    
    @Override
    public synchronized void write(final int ch) throws IOException {
        if (FromNetASCIIInputStream._noConversionRequired) {
            this.out.write(ch);
            return;
        }
        this.__write(ch);
    }
    
    @Override
    public synchronized void write(final byte[] buffer) throws IOException {
        this.write(buffer, 0, buffer.length);
    }
    
    @Override
    public synchronized void write(final byte[] buffer, int offset, int length) throws IOException {
        if (FromNetASCIIInputStream._noConversionRequired) {
            this.out.write(buffer, offset, length);
            return;
        }
        while (length-- > 0) {
            this.__write(buffer[offset++]);
        }
    }
    
    @Override
    public synchronized void close() throws IOException {
        if (FromNetASCIIInputStream._noConversionRequired) {
            super.close();
            return;
        }
        if (this.__lastWasCR) {
            this.out.write(13);
        }
        super.close();
    }
}
