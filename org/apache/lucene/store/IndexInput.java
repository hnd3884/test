package org.apache.lucene.store;

import java.io.IOException;
import java.io.Closeable;

public abstract class IndexInput extends DataInput implements Cloneable, Closeable
{
    private final String resourceDescription;
    
    protected IndexInput(final String resourceDescription) {
        if (resourceDescription == null) {
            throw new IllegalArgumentException("resourceDescription must not be null");
        }
        this.resourceDescription = resourceDescription;
    }
    
    @Override
    public abstract void close() throws IOException;
    
    public abstract long getFilePointer();
    
    public abstract void seek(final long p0) throws IOException;
    
    public abstract long length();
    
    @Override
    public String toString() {
        return this.resourceDescription;
    }
    
    @Override
    public IndexInput clone() {
        return (IndexInput)super.clone();
    }
    
    public abstract IndexInput slice(final String p0, final long p1, final long p2) throws IOException;
    
    protected String getFullSliceDescription(final String sliceDescription) {
        if (sliceDescription == null) {
            return this.toString();
        }
        return this.toString() + " [slice=" + sliceDescription + "]";
    }
    
    public RandomAccessInput randomAccessSlice(final long offset, final long length) throws IOException {
        final IndexInput slice = this.slice("randomaccess", offset, length);
        if (slice instanceof RandomAccessInput) {
            return (RandomAccessInput)slice;
        }
        return new RandomAccessInput() {
            @Override
            public byte readByte(final long pos) throws IOException {
                slice.seek(pos);
                return slice.readByte();
            }
            
            @Override
            public short readShort(final long pos) throws IOException {
                slice.seek(pos);
                return slice.readShort();
            }
            
            @Override
            public int readInt(final long pos) throws IOException {
                slice.seek(pos);
                return slice.readInt();
            }
            
            @Override
            public long readLong(final long pos) throws IOException {
                slice.seek(pos);
                return slice.readLong();
            }
        };
    }
}
