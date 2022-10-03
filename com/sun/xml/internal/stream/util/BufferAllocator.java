package com.sun.xml.internal.stream.util;

public class BufferAllocator
{
    public static int SMALL_SIZE_LIMIT;
    public static int MEDIUM_SIZE_LIMIT;
    public static int LARGE_SIZE_LIMIT;
    char[] smallCharBuffer;
    char[] mediumCharBuffer;
    char[] largeCharBuffer;
    byte[] smallByteBuffer;
    byte[] mediumByteBuffer;
    byte[] largeByteBuffer;
    
    public char[] getCharBuffer(final int size) {
        if (size <= BufferAllocator.SMALL_SIZE_LIMIT) {
            final char[] buffer = this.smallCharBuffer;
            this.smallCharBuffer = null;
            return buffer;
        }
        if (size <= BufferAllocator.MEDIUM_SIZE_LIMIT) {
            final char[] buffer = this.mediumCharBuffer;
            this.mediumCharBuffer = null;
            return buffer;
        }
        if (size <= BufferAllocator.LARGE_SIZE_LIMIT) {
            final char[] buffer = this.largeCharBuffer;
            this.largeCharBuffer = null;
            return buffer;
        }
        return null;
    }
    
    public void returnCharBuffer(final char[] c) {
        if (c == null) {
            return;
        }
        if (c.length <= BufferAllocator.SMALL_SIZE_LIMIT) {
            this.smallCharBuffer = c;
        }
        else if (c.length <= BufferAllocator.MEDIUM_SIZE_LIMIT) {
            this.mediumCharBuffer = c;
        }
        else if (c.length <= BufferAllocator.LARGE_SIZE_LIMIT) {
            this.largeCharBuffer = c;
        }
    }
    
    public byte[] getByteBuffer(final int size) {
        if (size <= BufferAllocator.SMALL_SIZE_LIMIT) {
            final byte[] buffer = this.smallByteBuffer;
            this.smallByteBuffer = null;
            return buffer;
        }
        if (size <= BufferAllocator.MEDIUM_SIZE_LIMIT) {
            final byte[] buffer = this.mediumByteBuffer;
            this.mediumByteBuffer = null;
            return buffer;
        }
        if (size <= BufferAllocator.LARGE_SIZE_LIMIT) {
            final byte[] buffer = this.largeByteBuffer;
            this.largeByteBuffer = null;
            return buffer;
        }
        return null;
    }
    
    public void returnByteBuffer(final byte[] b) {
        if (b == null) {
            return;
        }
        if (b.length <= BufferAllocator.SMALL_SIZE_LIMIT) {
            this.smallByteBuffer = b;
        }
        else if (b.length <= BufferAllocator.MEDIUM_SIZE_LIMIT) {
            this.mediumByteBuffer = b;
        }
        else if (b.length <= BufferAllocator.LARGE_SIZE_LIMIT) {
            this.largeByteBuffer = b;
        }
    }
    
    static {
        BufferAllocator.SMALL_SIZE_LIMIT = 128;
        BufferAllocator.MEDIUM_SIZE_LIMIT = 2048;
        BufferAllocator.LARGE_SIZE_LIMIT = 8192;
    }
}
