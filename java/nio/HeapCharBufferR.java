package java.nio;

class HeapCharBufferR extends HeapCharBuffer
{
    HeapCharBufferR(final int n, final int n2) {
        super(n, n2);
        this.isReadOnly = true;
    }
    
    HeapCharBufferR(final char[] array, final int n, final int n2) {
        super(array, n, n2);
        this.isReadOnly = true;
    }
    
    protected HeapCharBufferR(final char[] array, final int n, final int n2, final int n3, final int n4, final int n5) {
        super(array, n, n2, n3, n4, n5);
        this.isReadOnly = true;
    }
    
    @Override
    public CharBuffer slice() {
        final int position = this.position();
        final int limit = this.limit();
        final int n = (position <= limit) ? (limit - position) : 0;
        return new HeapCharBufferR(this.hb, -1, 0, n, n, position + this.offset);
    }
    
    @Override
    public CharBuffer duplicate() {
        return new HeapCharBufferR(this.hb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
    }
    
    @Override
    public CharBuffer asReadOnlyBuffer() {
        return this.duplicate();
    }
    
    @Override
    public boolean isReadOnly() {
        return true;
    }
    
    @Override
    public CharBuffer put(final char c) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public CharBuffer put(final int n, final char c) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public CharBuffer put(final char[] array, final int n, final int n2) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public CharBuffer put(final CharBuffer charBuffer) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public CharBuffer compact() {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    String toString(final int n, final int n2) {
        try {
            return new String(this.hb, n + this.offset, n2 - n);
        }
        catch (final StringIndexOutOfBoundsException ex) {
            throw new IndexOutOfBoundsException();
        }
    }
    
    @Override
    public CharBuffer subSequence(final int n, final int n2) {
        if (n < 0 || n2 > this.length() || n > n2) {
            throw new IndexOutOfBoundsException();
        }
        final int position = this.position();
        return new HeapCharBufferR(this.hb, -1, position + n, position + n2, this.capacity(), this.offset);
    }
    
    @Override
    public ByteOrder order() {
        return ByteOrder.nativeOrder();
    }
}
