package java.nio;

import sun.nio.ch.DirectBuffer;

class DirectDoubleBufferRS extends DirectDoubleBufferS implements DirectBuffer
{
    DirectDoubleBufferRS(final DirectBuffer directBuffer, final int n, final int n2, final int n3, final int n4, final int n5) {
        super(directBuffer, n, n2, n3, n4, n5);
    }
    
    @Override
    public DoubleBuffer slice() {
        final int position = this.position();
        final int limit = this.limit();
        final int n = (position <= limit) ? (limit - position) : 0;
        final int n2 = position << 3;
        assert n2 >= 0;
        return new DirectDoubleBufferRS(this, -1, 0, n, n, n2);
    }
    
    @Override
    public DoubleBuffer duplicate() {
        return new DirectDoubleBufferRS(this, this.markValue(), this.position(), this.limit(), this.capacity(), 0);
    }
    
    @Override
    public DoubleBuffer asReadOnlyBuffer() {
        return this.duplicate();
    }
    
    @Override
    public DoubleBuffer put(final double n) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public DoubleBuffer put(final int n, final double n2) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public DoubleBuffer put(final DoubleBuffer doubleBuffer) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public DoubleBuffer put(final double[] array, final int n, final int n2) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public DoubleBuffer compact() {
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
