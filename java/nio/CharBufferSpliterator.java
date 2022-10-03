package java.nio;

import java.util.function.IntConsumer;
import java.util.Spliterator;

class CharBufferSpliterator implements Spliterator.OfInt
{
    private final CharBuffer buffer;
    private int index;
    private final int limit;
    
    CharBufferSpliterator(final CharBuffer charBuffer) {
        this(charBuffer, charBuffer.position(), charBuffer.limit());
    }
    
    CharBufferSpliterator(final CharBuffer buffer, final int n, final int limit) {
        assert n <= limit;
        this.buffer = buffer;
        this.index = ((n <= limit) ? n : limit);
        this.limit = limit;
    }
    
    @Override
    public Spliterator.OfInt trySplit() {
        final int index = this.index;
        final int index2 = index + this.limit >>> 1;
        return (index >= index2) ? null : new CharBufferSpliterator(this.buffer, index, this.index = index2);
    }
    
    @Override
    public void forEachRemaining(final IntConsumer intConsumer) {
        if (intConsumer == null) {
            throw new NullPointerException();
        }
        final CharBuffer buffer = this.buffer;
        int i = this.index;
        final int limit = this.limit;
        this.index = limit;
        while (i < limit) {
            intConsumer.accept(buffer.getUnchecked(i++));
        }
    }
    
    @Override
    public boolean tryAdvance(final IntConsumer intConsumer) {
        if (intConsumer == null) {
            throw new NullPointerException();
        }
        if (this.index >= 0 && this.index < this.limit) {
            intConsumer.accept(this.buffer.getUnchecked(this.index++));
            return true;
        }
        return false;
    }
    
    @Override
    public long estimateSize() {
        return this.limit - this.index;
    }
    
    @Override
    public int characteristics() {
        return 16464;
    }
}
