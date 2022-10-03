package org.apache.axiom.blob;

final class MemoryBlobChunk
{
    final byte[] buffer;
    int size;
    MemoryBlobChunk nextChunk;
    
    MemoryBlobChunk(final int capacity) {
        this.buffer = new byte[capacity];
    }
    
    MemoryBlobChunk allocateNextChunk() {
        return this.nextChunk = new MemoryBlobChunk(this.buffer.length * 2);
    }
}
