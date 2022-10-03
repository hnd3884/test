package java.nio;

class HeapFloatBufferR extends HeapFloatBuffer
{
    HeapFloatBufferR(final int n, final int n2) {
        super(n, n2);
        this.isReadOnly = true;
    }
    
    HeapFloatBufferR(final float[] array, final int n, final int n2) {
        super(array, n, n2);
        this.isReadOnly = true;
    }
    
    protected HeapFloatBufferR(final float[] array, final int n, final int n2, final int n3, final int n4, final int n5) {
        super(array, n, n2, n3, n4, n5);
        this.isReadOnly = true;
    }
    
    @Override
    public FloatBuffer slice() {
        final int position = this.position();
        final int limit = this.limit();
        final int n = (position <= limit) ? (limit - position) : 0;
        return new HeapFloatBufferR(this.hb, -1, 0, n, n, position + this.offset);
    }
    
    @Override
    public FloatBuffer duplicate() {
        return new HeapFloatBufferR(this.hb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
    }
    
    @Override
    public FloatBuffer asReadOnlyBuffer() {
        return this.duplicate();
    }
    
    @Override
    public boolean isReadOnly() {
        return true;
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
    public FloatBuffer put(final float[] array, final int n, final int n2) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public FloatBuffer put(final FloatBuffer floatBuffer) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public FloatBuffer compact() {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ByteOrder order() {
        return ByteOrder.nativeOrder();
    }
}
