package java.nio;

import sun.nio.ch.DirectBuffer;

class DirectLongBufferRU extends DirectLongBufferU implements DirectBuffer
{
    DirectLongBufferRU(final DirectBuffer directBuffer, final int n, final int n2, final int n3, final int n4, final int n5) {
        super(directBuffer, n, n2, n3, n4, n5);
    }
    
    @Override
    public LongBuffer slice() {
        final int position = this.position();
        final int limit = this.limit();
        final int n = (position <= limit) ? (limit - position) : 0;
        final int n2 = position << 3;
        assert n2 >= 0;
        return new DirectLongBufferRU(this, -1, 0, n, n, n2);
    }
    
    @Override
    public LongBuffer duplicate() {
        return new DirectLongBufferRU(this, this.markValue(), this.position(), this.limit(), this.capacity(), 0);
    }
    
    @Override
    public LongBuffer asReadOnlyBuffer() {
        return this.duplicate();
    }
    
    @Override
    public LongBuffer put(final long n) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public LongBuffer put(final int n, final long n2) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public LongBuffer put(final LongBuffer longBuffer) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public LongBuffer put(final long[] array, final int n, final int n2) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public LongBuffer compact() {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public boolean isDirect() {
        return true;
    }
    
    @Override
    public boolean isReadOnly() {
        return true;
    }
    
    @Override
    public ByteOrder order() {
        return (ByteOrder.nativeOrder() != ByteOrder.BIG_ENDIAN) ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
    }
}
