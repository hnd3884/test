package org.msgpack.io;

abstract class AbstractInput implements Input
{
    private int readByteCount;
    
    AbstractInput() {
        this.readByteCount = 0;
    }
    
    @Override
    public int getReadByteCount() {
        return this.readByteCount;
    }
    
    @Override
    public void resetReadByteCount() {
        this.readByteCount = 0;
    }
    
    protected final void incrReadByteCount(final int size) {
        this.readByteCount += size;
    }
    
    protected final void incrReadOneByteCount() {
        ++this.readByteCount;
    }
}
