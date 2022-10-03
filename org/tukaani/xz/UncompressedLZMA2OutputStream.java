package org.tukaani.xz;

import java.io.OutputStream;
import java.io.IOException;
import java.io.DataOutputStream;

class UncompressedLZMA2OutputStream extends FinishableOutputStream
{
    private final ArrayCache arrayCache;
    private FinishableOutputStream out;
    private final DataOutputStream outData;
    private final byte[] uncompBuf;
    private int uncompPos;
    private boolean dictResetNeeded;
    private boolean finished;
    private IOException exception;
    private final byte[] tempBuf;
    
    static int getMemoryUsage() {
        return 70;
    }
    
    UncompressedLZMA2OutputStream(final FinishableOutputStream out, final ArrayCache arrayCache) {
        this.uncompPos = 0;
        this.dictResetNeeded = true;
        this.finished = false;
        this.exception = null;
        this.tempBuf = new byte[1];
        if (out == null) {
            throw new NullPointerException();
        }
        this.out = out;
        this.outData = new DataOutputStream(out);
        this.arrayCache = arrayCache;
        this.uncompBuf = arrayCache.getByteArray(65536, false);
    }
    
    @Override
    public void write(final int n) throws IOException {
        this.tempBuf[0] = (byte)n;
        this.write(this.tempBuf, 0, 1);
    }
    
    @Override
    public void write(final byte[] array, final int n, int i) throws IOException {
        if (n < 0 || i < 0 || n + i < 0 || n + i > array.length) {
            throw new IndexOutOfBoundsException();
        }
        if (this.exception != null) {
            throw this.exception;
        }
        if (this.finished) {
            throw new XZIOException("Stream finished or closed");
        }
        try {
            while (i > 0) {
                final int min = Math.min(65536 - this.uncompPos, i);
                System.arraycopy(array, n, this.uncompBuf, this.uncompPos, min);
                i -= min;
                this.uncompPos += min;
                if (this.uncompPos == 65536) {
                    this.writeChunk();
                }
            }
        }
        catch (final IOException exception) {
            throw this.exception = exception;
        }
    }
    
    private void writeChunk() throws IOException {
        this.outData.writeByte(this.dictResetNeeded ? 1 : 2);
        this.outData.writeShort(this.uncompPos - 1);
        this.outData.write(this.uncompBuf, 0, this.uncompPos);
        this.uncompPos = 0;
        this.dictResetNeeded = false;
    }
    
    private void writeEndMarker() throws IOException {
        if (this.exception != null) {
            throw this.exception;
        }
        if (this.finished) {
            throw new XZIOException("Stream finished or closed");
        }
        try {
            if (this.uncompPos > 0) {
                this.writeChunk();
            }
            this.out.write(0);
        }
        catch (final IOException exception) {
            throw this.exception = exception;
        }
        this.finished = true;
        this.arrayCache.putArray(this.uncompBuf);
    }
    
    @Override
    public void flush() throws IOException {
        if (this.exception != null) {
            throw this.exception;
        }
        if (this.finished) {
            throw new XZIOException("Stream finished or closed");
        }
        try {
            if (this.uncompPos > 0) {
                this.writeChunk();
            }
            this.out.flush();
        }
        catch (final IOException exception) {
            throw this.exception = exception;
        }
    }
    
    @Override
    public void finish() throws IOException {
        if (!this.finished) {
            this.writeEndMarker();
            try {
                this.out.finish();
            }
            catch (final IOException exception) {
                throw this.exception = exception;
            }
        }
    }
    
    @Override
    public void close() throws IOException {
        if (this.out != null) {
            if (!this.finished) {
                try {
                    this.writeEndMarker();
                }
                catch (final IOException ex) {}
            }
            try {
                this.out.close();
            }
            catch (final IOException exception) {
                if (this.exception == null) {
                    this.exception = exception;
                }
            }
            this.out = null;
        }
        if (this.exception != null) {
            throw this.exception;
        }
    }
}
