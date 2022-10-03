package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.File;

final class DataFile
{
    private WeakDataFile weak;
    private long writePointer;
    
    DataFile(final File file) {
        this.writePointer = 0L;
        this.weak = new WeakDataFile(this, file);
    }
    
    void close() {
        this.weak.close();
    }
    
    synchronized void read(final long pointer, final byte[] buf, final int offset, final int length) {
        this.weak.read(pointer, buf, offset, length);
    }
    
    void renameTo(final File f) {
        this.weak.renameTo(f);
    }
    
    synchronized long writeTo(final byte[] data, final int offset, final int length) {
        final long temp = this.writePointer;
        this.writePointer = this.weak.writeTo(this.writePointer, data, offset, length);
        return temp;
    }
}
