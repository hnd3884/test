package java.nio;

class HeapByteBufferR extends HeapByteBuffer
{
    HeapByteBufferR(final int n, final int n2) {
        super(n, n2);
        this.isReadOnly = true;
    }
    
    HeapByteBufferR(final byte[] array, final int n, final int n2) {
        super(array, n, n2);
        this.isReadOnly = true;
    }
    
    protected HeapByteBufferR(final byte[] array, final int n, final int n2, final int n3, final int n4, final int n5) {
        super(array, n, n2, n3, n4, n5);
        this.isReadOnly = true;
    }
    
    @Override
    public ByteBuffer slice() {
        final int position = this.position();
        final int limit = this.limit();
        final int n = (position <= limit) ? (limit - position) : 0;
        return new HeapByteBufferR(this.hb, -1, 0, n, n, position + this.offset);
    }
    
    @Override
    public ByteBuffer duplicate() {
        return new HeapByteBufferR(this.hb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
    }
    
    @Override
    public ByteBuffer asReadOnlyBuffer() {
        return this.duplicate();
    }
    
    @Override
    public boolean isReadOnly() {
        return true;
    }
    
    @Override
    public ByteBuffer put(final byte b) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ByteBuffer put(final int n, final byte b) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ByteBuffer put(final byte[] array, final int n, final int n2) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ByteBuffer put(final ByteBuffer byteBuffer) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ByteBuffer compact() {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    byte _get(final int n) {
        return this.hb[n];
    }
    
    @Override
    void _put(final int n, final byte b) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ByteBuffer putChar(final char c) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ByteBuffer putChar(final int n, final char c) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public CharBuffer asCharBuffer() {
        final int position = this.position();
        final int n = this.limit() - position >> 1;
        final int n2 = this.offset + position;
        return this.bigEndian ? new ByteBufferAsCharBufferRB(this, -1, 0, n, n, n2) : new ByteBufferAsCharBufferRL(this, -1, 0, n, n, n2);
    }
    
    @Override
    public ByteBuffer putShort(final short n) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ByteBuffer putShort(final int n, final short n2) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ShortBuffer asShortBuffer() {
        final int position = this.position();
        final int n = this.limit() - position >> 1;
        final int n2 = this.offset + position;
        return this.bigEndian ? new ByteBufferAsShortBufferRB(this, -1, 0, n, n, n2) : new ByteBufferAsShortBufferRL(this, -1, 0, n, n, n2);
    }
    
    @Override
    public ByteBuffer putInt(final int n) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ByteBuffer putInt(final int n, final int n2) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public IntBuffer asIntBuffer() {
        final int position = this.position();
        final int n = this.limit() - position >> 2;
        final int n2 = this.offset + position;
        return this.bigEndian ? new ByteBufferAsIntBufferRB(this, -1, 0, n, n, n2) : new ByteBufferAsIntBufferRL(this, -1, 0, n, n, n2);
    }
    
    @Override
    public ByteBuffer putLong(final long n) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ByteBuffer putLong(final int n, final long n2) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public LongBuffer asLongBuffer() {
        final int position = this.position();
        final int n = this.limit() - position >> 3;
        final int n2 = this.offset + position;
        return this.bigEndian ? new ByteBufferAsLongBufferRB(this, -1, 0, n, n, n2) : new ByteBufferAsLongBufferRL(this, -1, 0, n, n, n2);
    }
    
    @Override
    public ByteBuffer putFloat(final float n) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ByteBuffer putFloat(final int n, final float n2) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public FloatBuffer asFloatBuffer() {
        final int position = this.position();
        final int n = this.limit() - position >> 2;
        final int n2 = this.offset + position;
        return this.bigEndian ? new ByteBufferAsFloatBufferRB(this, -1, 0, n, n, n2) : new ByteBufferAsFloatBufferRL(this, -1, 0, n, n, n2);
    }
    
    @Override
    public ByteBuffer putDouble(final double n) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ByteBuffer putDouble(final int n, final double n2) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public DoubleBuffer asDoubleBuffer() {
        final int position = this.position();
        final int n = this.limit() - position >> 3;
        final int n2 = this.offset + position;
        return this.bigEndian ? new ByteBufferAsDoubleBufferRB(this, -1, 0, n, n, n2) : new ByteBufferAsDoubleBufferRL(this, -1, 0, n, n, n2);
    }
}
