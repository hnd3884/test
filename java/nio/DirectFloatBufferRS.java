package java.nio;

import sun.nio.ch.DirectBuffer;

class DirectFloatBufferRS extends DirectFloatBufferS implements DirectBuffer
{
    DirectFloatBufferRS(final DirectBuffer directBuffer, final int n, final int n2, final int n3, final int n4, final int n5) {
        super(directBuffer, n, n2, n3, n4, n5);
    }
    
    @Override
    public FloatBuffer slice() {
        final int position = this.position();
        final int limit = this.limit();
        final int n = (position <= limit) ? (limit - position) : 0;
        final int n2 = position << 2;
        assert n2 >= 0;
        return new DirectFloatBufferRS(this, -1, 0, n, n, n2);
    }
    
    @Override
    public FloatBuffer duplicate() {
        return new DirectFloatBufferRS(this, this.markValue(), this.position(), this.limit(), this.capacity(), 0);
    }
    
    @Override
    public FloatBuffer asReadOnlyBuffer() {
        return this.duplicate();
    }
    
    @Override
    public FloatBuffer put(final float n) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public FloatBuffer put(final int n, final float n2) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public FloatBuffer put(final FloatBuffer floatBuffer) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public FloatBuffer put(final float[] array, final int n, final int n2) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public FloatBuffer compact() {
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
