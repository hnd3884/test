package com.zoho.framework.io;

import java.io.IOException;
import java.io.PipedOutputStream;
import java.io.BufferedInputStream;
import java.io.PipedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

public class DataBufferStream
{
    private AtomicBoolean readFlag;
    private AtomicBoolean writeFlag;
    private AtomicBoolean isCompleted;
    private OutputStream outputStream;
    private InputStream inputStream;
    
    public DataBufferStream(final int sizeOfBufferInMB) throws IOException {
        this.readFlag = new AtomicBoolean(false);
        this.writeFlag = new AtomicBoolean(false);
        this.isCompleted = new AtomicBoolean(false);
        try {
            final InputStream pin = new PipedInputStream(sizeOfBufferInMB * 1048576);
            this.setInputStream(new BufferedInputStream(pin, sizeOfBufferInMB * 1048576));
            this.setOutputStream(new PipedOutputStream((PipedInputStream)pin));
        }
        catch (final IOException ioe) {
            ioe.printStackTrace();
            throw new IOException("Error while creating DataBufferStream");
        }
    }
    
    public OutputStream getOutputStream() {
        return this.outputStream;
    }
    
    private void setOutputStream(final OutputStream outputStream) {
        this.outputStream = outputStream;
    }
    
    public InputStream getInputStream() {
        return this.inputStream;
    }
    
    private void setInputStream(final InputStream inputStream) {
        this.inputStream = inputStream;
    }
    
    public AtomicBoolean getReadFlag() {
        return this.readFlag;
    }
    
    public AtomicBoolean getWriteFlag() {
        return this.writeFlag;
    }
    
    public AtomicBoolean isCompleted() {
        return this.isCompleted;
    }
    
    public void setIsCompleted(final boolean isCompleted) {
        this.isCompleted.set(isCompleted);
    }
}
