package com.sun.corba.se.impl.encoding;

import java.util.NoSuchElementException;
import java.util.LinkedList;

public class BufferQueue
{
    private LinkedList list;
    
    public BufferQueue() {
        this.list = new LinkedList();
    }
    
    public void enqueue(final ByteBufferWithInfo byteBufferWithInfo) {
        this.list.addLast(byteBufferWithInfo);
    }
    
    public ByteBufferWithInfo dequeue() throws NoSuchElementException {
        return this.list.removeFirst();
    }
    
    public int size() {
        return this.list.size();
    }
    
    public void push(final ByteBufferWithInfo byteBufferWithInfo) {
        this.list.addFirst(byteBufferWithInfo);
    }
}
