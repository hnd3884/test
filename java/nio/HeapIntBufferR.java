package java.nio;

class HeapIntBufferR extends HeapIntBuffer
{
    HeapIntBufferR(final int n, final int n2) {
        super(n, n2);
        this.isReadOnly = true;
    }
    
    HeapIntBufferR(final int[] array, final int n, final int n2) {
        super(array, n, n2);
        this.isReadOnly = true;
    }
    
    protected HeapIntBufferR(final int[] array, final int n, final int n2, final int n3, final int n4, final int n5) {
        super(array, n, n2, n3, n4, n5);
        this.isReadOnly = true;
    }
    
    @Override
    public IntBuffer slice() {
        final int position = this.position();
        final int limit = this.limit();
        final int n = (position <= limit) ? (limit - position) : 0;
        return new HeapIntBufferR(this.hb, -1, 0, n, n, position + this.offset);
    }
    
    @Override
    public IntBuffer duplicate() {
        return new HeapIntBufferR(this.hb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
    }
    
    @Override
    public IntBuffer asReadOnlyBuffer() {
        return this.duplicate();
    }
    
    @Override
    public boolean isReadOnly() {
        return true;
    }
    
    @Override
    public IntBuffer put(final int n) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public IntBuffer put(final int n, final int n2) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public IntBuffer put(final int[] array, final int n, final int n2) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public IntBuffer put(final IntBuffer intBuffer) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public IntBuffer compact() {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ByteOrder order() {
        return ByteOrder.nativeOrder();
    }
}
