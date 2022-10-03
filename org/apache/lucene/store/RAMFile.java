package org.apache.lucene.store;

import java.util.Iterator;
import java.util.Arrays;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.lucene.util.Accountable;

public class RAMFile implements Accountable
{
    protected final ArrayList<byte[]> buffers;
    long length;
    RAMDirectory directory;
    protected long sizeInBytes;
    
    public RAMFile() {
        this.buffers = new ArrayList<byte[]>();
    }
    
    RAMFile(final RAMDirectory directory) {
        this.buffers = new ArrayList<byte[]>();
        this.directory = directory;
    }
    
    public synchronized long getLength() {
        return this.length;
    }
    
    protected synchronized void setLength(final long length) {
        this.length = length;
    }
    
    protected final byte[] addBuffer(final int size) {
        final byte[] buffer = this.newBuffer(size);
        synchronized (this) {
            this.buffers.add(buffer);
            this.sizeInBytes += size;
        }
        if (this.directory != null) {
            this.directory.sizeInBytes.getAndAdd(size);
        }
        return buffer;
    }
    
    protected final synchronized byte[] getBuffer(final int index) {
        return this.buffers.get(index);
    }
    
    protected final synchronized int numBuffers() {
        return this.buffers.size();
    }
    
    protected byte[] newBuffer(final int size) {
        return new byte[size];
    }
    
    @Override
    public synchronized long ramBytesUsed() {
        return this.sizeInBytes;
    }
    
    @Override
    public Collection<Accountable> getChildResources() {
        return (Collection<Accountable>)Collections.emptyList();
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(length=" + this.length + ")";
    }
    
    @Override
    public int hashCode() {
        int h = (int)(this.length ^ this.length >>> 32);
        for (final byte[] block : this.buffers) {
            h = 31 * h + Arrays.hashCode(block);
        }
        return h;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final RAMFile other = (RAMFile)obj;
        if (this.length != other.length) {
            return false;
        }
        if (this.buffers.size() != other.buffers.size()) {
            return false;
        }
        for (int i = 0; i < this.buffers.size(); ++i) {
            if (!Arrays.equals(this.buffers.get(i), other.buffers.get(i))) {
                return false;
            }
        }
        return true;
    }
}
