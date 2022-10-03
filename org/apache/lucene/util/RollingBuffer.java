package org.apache.lucene.util;

public abstract class RollingBuffer<T extends Resettable>
{
    private T[] buffer;
    private int nextWrite;
    private int nextPos;
    private int count;
    
    public RollingBuffer() {
        this.buffer = (T[])new Resettable[8];
        for (int idx = 0; idx < this.buffer.length; ++idx) {
            this.buffer[idx] = this.newInstance();
        }
    }
    
    protected abstract T newInstance();
    
    public void reset() {
        --this.nextWrite;
        while (this.count > 0) {
            if (this.nextWrite == -1) {
                this.nextWrite = this.buffer.length - 1;
            }
            this.buffer[this.nextWrite--].reset();
            --this.count;
        }
        this.nextWrite = 0;
        this.nextPos = 0;
        this.count = 0;
    }
    
    private boolean inBounds(final int pos) {
        return pos < this.nextPos && pos >= this.nextPos - this.count;
    }
    
    private int getIndex(final int pos) {
        int index = this.nextWrite - (this.nextPos - pos);
        if (index < 0) {
            index += this.buffer.length;
        }
        return index;
    }
    
    public T get(final int pos) {
        while (pos >= this.nextPos) {
            if (this.count == this.buffer.length) {
                final T[] newBuffer = (T[])new Resettable[ArrayUtil.oversize(1 + this.count, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
                System.arraycopy(this.buffer, this.nextWrite, newBuffer, 0, this.buffer.length - this.nextWrite);
                System.arraycopy(this.buffer, 0, newBuffer, this.buffer.length - this.nextWrite, this.nextWrite);
                for (int i = this.buffer.length; i < newBuffer.length; ++i) {
                    newBuffer[i] = this.newInstance();
                }
                this.nextWrite = this.buffer.length;
                this.buffer = newBuffer;
            }
            if (this.nextWrite == this.buffer.length) {
                this.nextWrite = 0;
            }
            ++this.nextWrite;
            ++this.nextPos;
            ++this.count;
        }
        assert this.inBounds(pos);
        final int index = this.getIndex(pos);
        return this.buffer[index];
    }
    
    public int getMaxPos() {
        return this.nextPos - 1;
    }
    
    public void freeBefore(final int pos) {
        final int toFree = this.count - (this.nextPos - pos);
        assert toFree >= 0;
        assert toFree <= this.count : "toFree=" + toFree + " count=" + this.count;
        int index = this.nextWrite - this.count;
        if (index < 0) {
            index += this.buffer.length;
        }
        for (int i = 0; i < toFree; ++i) {
            if (index == this.buffer.length) {
                index = 0;
            }
            this.buffer[index].reset();
            ++index;
        }
        this.count -= toFree;
    }
    
    public interface Resettable
    {
        void reset();
    }
}
