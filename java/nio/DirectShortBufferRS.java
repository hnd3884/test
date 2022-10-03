package java.nio;

import sun.nio.ch.DirectBuffer;

class DirectShortBufferRS extends DirectShortBufferS implements DirectBuffer
{
    DirectShortBufferRS(final DirectBuffer directBuffer, final int n, final int n2, final int n3, final int n4, final int n5) {
        super(directBuffer, n, n2, n3, n4, n5);
    }
    
    @Override
    public ShortBuffer slice() {
        final int position = this.position();
        final int limit = this.limit();
        final int n = (position <= limit) ? (limit - position) : 0;
        final int n2 = position << 1;
        assert n2 >= 0;
        return new DirectShortBufferRS(this, -1, 0, n, n, n2);
    }
    
    @Override
    public ShortBuffer duplicate() {
        return new DirectShortBufferRS(this, this.markValue(), this.position(), this.limit(), this.capacity(), 0);
    }
    
    @Override
    public ShortBuffer asReadOnlyBuffer() {
        return this.duplicate();
    }
    
    @Override
    public ShortBuffer put(final short n) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ShortBuffer put(final int n, final short n2) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ShortBuffer put(final ShortBuffer shortBuffer) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ShortBuffer put(final short[] array, final int n, final int n2) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ShortBuffer compact() {
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
        return (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
    }
}
