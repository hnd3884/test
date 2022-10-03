package org.msgpack.packer;

import org.msgpack.MessageTypeException;

public final class PackerStack
{
    private int top;
    private byte[] types;
    private int[] counts;
    public static final int MAX_STACK_SIZE = 128;
    private static final byte TYPE_INVALID = 0;
    private static final byte TYPE_ARRAY = 1;
    private static final byte TYPE_MAP = 2;
    
    public PackerStack() {
        this.top = 0;
        this.types = new byte[128];
        this.counts = new int[128];
        this.types[0] = 0;
    }
    
    public void pushArray(final int size) {
        ++this.top;
        this.types[this.top] = 1;
        this.counts[this.top] = size;
    }
    
    public void pushMap(final int size) {
        ++this.top;
        this.types[this.top] = 2;
        this.counts[this.top] = size * 2;
    }
    
    public void checkCount() {
        if (this.counts[this.top] > 0) {
            return;
        }
        if (this.types[this.top] == 1) {
            throw new MessageTypeException("Array is end but writeArrayEnd() is not called");
        }
        if (this.types[this.top] == 2) {
            throw new MessageTypeException("Map is end but writeMapEnd() is not called");
        }
    }
    
    public void reduceCount() {
        final int[] counts = this.counts;
        final int top = this.top;
        --counts[top];
    }
    
    public void pop() {
        --this.top;
    }
    
    public int getDepth() {
        return this.top;
    }
    
    public int getTopCount() {
        return this.counts[this.top];
    }
    
    public boolean topIsArray() {
        return this.types[this.top] == 1;
    }
    
    public boolean topIsMap() {
        return this.types[this.top] == 2;
    }
    
    public void clear() {
        this.top = 0;
    }
}
