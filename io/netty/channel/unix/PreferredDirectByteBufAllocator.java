package io.netty.channel.unix;

import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public final class PreferredDirectByteBufAllocator implements ByteBufAllocator
{
    private ByteBufAllocator allocator;
    
    public void updateAllocator(final ByteBufAllocator allocator) {
        this.allocator = allocator;
    }
    
    @Override
    public ByteBuf buffer() {
        return this.allocator.directBuffer();
    }
    
    @Override
    public ByteBuf buffer(final int initialCapacity) {
        return this.allocator.directBuffer(initialCapacity);
    }
    
    @Override
    public ByteBuf buffer(final int initialCapacity, final int maxCapacity) {
        return this.allocator.directBuffer(initialCapacity, maxCapacity);
    }
    
    @Override
    public ByteBuf ioBuffer() {
        return this.allocator.directBuffer();
    }
    
    @Override
    public ByteBuf ioBuffer(final int initialCapacity) {
        return this.allocator.directBuffer(initialCapacity);
    }
    
    @Override
    public ByteBuf ioBuffer(final int initialCapacity, final int maxCapacity) {
        return this.allocator.directBuffer(initialCapacity, maxCapacity);
    }
    
    @Override
    public ByteBuf heapBuffer() {
        return this.allocator.heapBuffer();
    }
    
    @Override
    public ByteBuf heapBuffer(final int initialCapacity) {
        return this.allocator.heapBuffer(initialCapacity);
    }
    
    @Override
    public ByteBuf heapBuffer(final int initialCapacity, final int maxCapacity) {
        return this.allocator.heapBuffer(initialCapacity, maxCapacity);
    }
    
    @Override
    public ByteBuf directBuffer() {
        return this.allocator.directBuffer();
    }
    
    @Override
    public ByteBuf directBuffer(final int initialCapacity) {
        return this.allocator.directBuffer(initialCapacity);
    }
    
    @Override
    public ByteBuf directBuffer(final int initialCapacity, final int maxCapacity) {
        return this.allocator.directBuffer(initialCapacity, maxCapacity);
    }
    
    @Override
    public CompositeByteBuf compositeBuffer() {
        return this.allocator.compositeDirectBuffer();
    }
    
    @Override
    public CompositeByteBuf compositeBuffer(final int maxNumComponents) {
        return this.allocator.compositeDirectBuffer(maxNumComponents);
    }
    
    @Override
    public CompositeByteBuf compositeHeapBuffer() {
        return this.allocator.compositeHeapBuffer();
    }
    
    @Override
    public CompositeByteBuf compositeHeapBuffer(final int maxNumComponents) {
        return this.allocator.compositeHeapBuffer(maxNumComponents);
    }
    
    @Override
    public CompositeByteBuf compositeDirectBuffer() {
        return this.allocator.compositeDirectBuffer();
    }
    
    @Override
    public CompositeByteBuf compositeDirectBuffer(final int maxNumComponents) {
        return this.allocator.compositeDirectBuffer(maxNumComponents);
    }
    
    @Override
    public boolean isDirectBufferPooled() {
        return this.allocator.isDirectBufferPooled();
    }
    
    @Override
    public int calculateNewCapacity(final int minNewCapacity, final int maxCapacity) {
        return this.allocator.calculateNewCapacity(minNewCapacity, maxCapacity);
    }
}
