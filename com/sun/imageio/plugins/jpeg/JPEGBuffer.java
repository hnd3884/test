package com.sun.imageio.plugins.jpeg;

import java.io.IOException;
import javax.imageio.IIOException;
import javax.imageio.stream.ImageInputStream;

class JPEGBuffer
{
    private boolean debug;
    final int BUFFER_SIZE = 4096;
    byte[] buf;
    int bufAvail;
    int bufPtr;
    ImageInputStream iis;
    
    JPEGBuffer(final ImageInputStream iis) {
        this.debug = false;
        this.buf = new byte[4096];
        this.bufAvail = 0;
        this.bufPtr = 0;
        this.iis = iis;
    }
    
    void loadBuf(final int n) throws IOException {
        if (this.debug) {
            System.out.print("loadbuf called with ");
            System.out.print("count " + n + ", ");
            System.out.println("bufAvail " + this.bufAvail + ", ");
        }
        if (n != 0) {
            if (this.bufAvail >= n) {
                return;
            }
        }
        else if (this.bufAvail == 4096) {
            return;
        }
        if (this.bufAvail > 0 && this.bufAvail < 4096) {
            System.arraycopy(this.buf, this.bufPtr, this.buf, 0, this.bufAvail);
        }
        final int read = this.iis.read(this.buf, this.bufAvail, this.buf.length - this.bufAvail);
        if (this.debug) {
            System.out.println("iis.read returned " + read);
        }
        if (read != -1) {
            this.bufAvail += read;
        }
        this.bufPtr = 0;
        if (this.bufAvail < Math.min(4096, n)) {
            throw new IIOException("Image Format Error");
        }
    }
    
    void readData(final byte[] array) throws IOException {
        int length = array.length;
        if (this.bufAvail >= length) {
            System.arraycopy(this.buf, this.bufPtr, array, 0, length);
            this.bufAvail -= length;
            this.bufPtr += length;
            return;
        }
        int bufAvail = 0;
        if (this.bufAvail > 0) {
            System.arraycopy(this.buf, this.bufPtr, array, 0, this.bufAvail);
            bufAvail = this.bufAvail;
            length -= this.bufAvail;
            this.bufAvail = 0;
            this.bufPtr = 0;
        }
        if (this.iis.read(array, bufAvail, length) != length) {
            throw new IIOException("Image format Error");
        }
    }
    
    void skipData(int n) throws IOException {
        if (this.bufAvail >= n) {
            this.bufAvail -= n;
            this.bufPtr += n;
            return;
        }
        if (this.bufAvail > 0) {
            n -= this.bufAvail;
            this.bufAvail = 0;
            this.bufPtr = 0;
        }
        if (this.iis.skipBytes(n) != n) {
            throw new IIOException("Image format Error");
        }
    }
    
    void pushBack() throws IOException {
        this.iis.seek(this.iis.getStreamPosition() - this.bufAvail);
        this.bufAvail = 0;
        this.bufPtr = 0;
    }
    
    long getStreamPosition() throws IOException {
        return this.iis.getStreamPosition() - this.bufAvail;
    }
    
    boolean scanForFF(final JPEGImageReader jpegImageReader) throws IOException {
        boolean b = false;
        for (int i = 0; i == 0; i = 1) {
            while (this.bufAvail > 0) {
                if ((this.buf[this.bufPtr++] & 0xFF) == 0xFF) {
                    --this.bufAvail;
                    i = 1;
                    break;
                }
                --this.bufAvail;
            }
            this.loadBuf(0);
            if (i == 1) {
                while (this.bufAvail > 0 && (this.buf[this.bufPtr] & 0xFF) == 0xFF) {
                    ++this.bufPtr;
                    --this.bufAvail;
                }
            }
            if (this.bufAvail == 0) {
                b = true;
                this.buf[0] = -39;
                this.bufAvail = 1;
                this.bufPtr = 0;
            }
        }
        return b;
    }
    
    void print(int i) {
        System.out.print("buffer has ");
        System.out.print(this.bufAvail);
        System.out.println(" bytes available");
        if (this.bufAvail < i) {
            i = this.bufAvail;
        }
        int bufPtr = this.bufPtr;
        while (i > 0) {
            System.out.print(" " + Integer.toHexString(this.buf[bufPtr++] & 0xFF));
            --i;
        }
        System.out.println();
    }
}
