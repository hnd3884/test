package com.sun.xml.internal.org.jvnet.mimepull;

import java.nio.ByteBuffer;

final class FileData implements Data
{
    private final DataFile file;
    private final long pointer;
    private final int length;
    
    FileData(final DataFile file, final ByteBuffer buf) {
        this(file, file.writeTo(buf.array(), 0, buf.limit()), buf.limit());
    }
    
    FileData(final DataFile file, final long pointer, final int length) {
        this.file = file;
        this.pointer = pointer;
        this.length = length;
    }
    
    @Override
    public byte[] read() {
        final byte[] buf = new byte[this.length];
        this.file.read(this.pointer, buf, 0, this.length);
        return buf;
    }
    
    @Override
    public long writeTo(final DataFile file) {
        throw new IllegalStateException();
    }
    
    @Override
    public int size() {
        return this.length;
    }
    
    @Override
    public Data createNext(final DataHead dataHead, final ByteBuffer buf) {
        return new FileData(this.file, buf);
    }
}
