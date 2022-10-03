package java.nio;

class HeapByteBuffer extends ByteBuffer
{
    HeapByteBuffer(final int n, final int n2) {
        super(-1, 0, n2, n, new byte[n], 0);
    }
    
    HeapByteBuffer(final byte[] array, final int n, final int n2) {
        super(-1, n, n + n2, array.length, array, 0);
    }
    
    protected HeapByteBuffer(final byte[] array, final int n, final int n2, final int n3, final int n4, final int n5) {
        super(n, n2, n3, n4, array, n5);
    }
    
    @Override
    public ByteBuffer slice() {
        final int position = this.position();
        final int limit = this.limit();
        final int n = (position <= limit) ? (limit - position) : 0;
        return new HeapByteBuffer(this.hb, -1, 0, n, n, position + this.offset);
    }
    
    @Override
    public ByteBuffer duplicate() {
        return new HeapByteBuffer(this.hb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
    }
    
    @Override
    public ByteBuffer asReadOnlyBuffer() {
        return new HeapByteBufferR(this.hb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
    }
    
    protected int ix(final int n) {
        return n + this.offset;
    }
    
    @Override
    public byte get() {
        return this.hb[this.ix(this.nextGetIndex())];
    }
    
    @Override
    public byte get(final int n) {
        return this.hb[this.ix(this.checkIndex(n))];
    }
    
    @Override
    public ByteBuffer get(final byte[] array, final int n, final int n2) {
        Buffer.checkBounds(n, n2, array.length);
        final int position = this.position();
        if (n2 > this.limit() - position) {
            throw new BufferUnderflowException();
        }
        System.arraycopy(this.hb, this.ix(position), array, n, n2);
        this.position(position + n2);
        return this;
    }
    
    @Override
    public boolean isDirect() {
        return false;
    }
    
    @Override
    public boolean isReadOnly() {
        return false;
    }
    
    @Override
    public ByteBuffer put(final byte b) {
        this.hb[this.ix(this.nextPutIndex())] = b;
        return this;
    }
    
    @Override
    public ByteBuffer put(final int n, final byte b) {
        this.hb[this.ix(this.checkIndex(n))] = b;
        return this;
    }
    
    @Override
    public ByteBuffer put(final byte[] array, final int n, final int n2) {
        Buffer.checkBounds(n, n2, array.length);
        final int position = this.position();
        if (n2 > this.limit() - position) {
            throw new BufferOverflowException();
        }
        System.arraycopy(array, n, this.hb, this.ix(position), n2);
        this.position(position + n2);
        return this;
    }
    
    @Override
    public ByteBuffer put(final ByteBuffer byteBuffer) {
        if (byteBuffer instanceof HeapByteBuffer) {
            if (byteBuffer == this) {
                throw new IllegalArgumentException();
            }
            final HeapByteBuffer heapByteBuffer = (HeapByteBuffer)byteBuffer;
            final int position = this.position();
            final int position2 = heapByteBuffer.position();
            final int n = heapByteBuffer.limit() - position2;
            if (n > this.limit() - position) {
                throw new BufferOverflowException();
            }
            System.arraycopy(heapByteBuffer.hb, heapByteBuffer.ix(position2), this.hb, this.ix(position), n);
            heapByteBuffer.position(position2 + n);
            this.position(position + n);
        }
        else if (byteBuffer.isDirect()) {
            final int remaining = byteBuffer.remaining();
            final int position3 = this.position();
            if (remaining > this.limit() - position3) {
                throw new BufferOverflowException();
            }
            byteBuffer.get(this.hb, this.ix(position3), remaining);
            this.position(position3 + remaining);
        }
        else {
            super.put(byteBuffer);
        }
        return this;
    }
    
    @Override
    public ByteBuffer compact() {
        final int position = this.position();
        final int limit = this.limit();
        assert position <= limit;
        final int n = (position <= limit) ? (limit - position) : 0;
        System.arraycopy(this.hb, this.ix(position), this.hb, this.ix(0), n);
        this.position(n);
        this.limit(this.capacity());
        this.discardMark();
        return this;
    }
    
    @Override
    byte _get(final int n) {
        return this.hb[n];
    }
    
    @Override
    void _put(final int n, final byte b) {
        this.hb[n] = b;
    }
    
    @Override
    public char getChar() {
        return Bits.getChar(this, this.ix(this.nextGetIndex(2)), this.bigEndian);
    }
    
    @Override
    public char getChar(final int n) {
        return Bits.getChar(this, this.ix(this.checkIndex(n, 2)), this.bigEndian);
    }
    
    @Override
    public ByteBuffer putChar(final char c) {
        Bits.putChar(this, this.ix(this.nextPutIndex(2)), c, this.bigEndian);
        return this;
    }
    
    @Override
    public ByteBuffer putChar(final int n, final char c) {
        Bits.putChar(this, this.ix(this.checkIndex(n, 2)), c, this.bigEndian);
        return this;
    }
    
    @Override
    public CharBuffer asCharBuffer() {
        final int position = this.position();
        final int n = this.limit() - position >> 1;
        final int n2 = this.offset + position;
        return this.bigEndian ? new ByteBufferAsCharBufferB(this, -1, 0, n, n, n2) : new ByteBufferAsCharBufferL(this, -1, 0, n, n, n2);
    }
    
    @Override
    public short getShort() {
        return Bits.getShort(this, this.ix(this.nextGetIndex(2)), this.bigEndian);
    }
    
    @Override
    public short getShort(final int n) {
        return Bits.getShort(this, this.ix(this.checkIndex(n, 2)), this.bigEndian);
    }
    
    @Override
    public ByteBuffer putShort(final short n) {
        Bits.putShort(this, this.ix(this.nextPutIndex(2)), n, this.bigEndian);
        return this;
    }
    
    @Override
    public ByteBuffer putShort(final int n, final short n2) {
        Bits.putShort(this, this.ix(this.checkIndex(n, 2)), n2, this.bigEndian);
        return this;
    }
    
    @Override
    public ShortBuffer asShortBuffer() {
        final int position = this.position();
        final int n = this.limit() - position >> 1;
        final int n2 = this.offset + position;
        return this.bigEndian ? new ByteBufferAsShortBufferB(this, -1, 0, n, n, n2) : new ByteBufferAsShortBufferL(this, -1, 0, n, n, n2);
    }
    
    @Override
    public int getInt() {
        return Bits.getInt(this, this.ix(this.nextGetIndex(4)), this.bigEndian);
    }
    
    @Override
    public int getInt(final int n) {
        return Bits.getInt(this, this.ix(this.checkIndex(n, 4)), this.bigEndian);
    }
    
    @Override
    public ByteBuffer putInt(final int n) {
        Bits.putInt(this, this.ix(this.nextPutIndex(4)), n, this.bigEndian);
        return this;
    }
    
    @Override
    public ByteBuffer putInt(final int n, final int n2) {
        Bits.putInt(this, this.ix(this.checkIndex(n, 4)), n2, this.bigEndian);
        return this;
    }
    
    @Override
    public IntBuffer asIntBuffer() {
        final int position = this.position();
        final int n = this.limit() - position >> 2;
        final int n2 = this.offset + position;
        return this.bigEndian ? new ByteBufferAsIntBufferB(this, -1, 0, n, n, n2) : new ByteBufferAsIntBufferL(this, -1, 0, n, n, n2);
    }
    
    @Override
    public long getLong() {
        return Bits.getLong(this, this.ix(this.nextGetIndex(8)), this.bigEndian);
    }
    
    @Override
    public long getLong(final int n) {
        return Bits.getLong(this, this.ix(this.checkIndex(n, 8)), this.bigEndian);
    }
    
    @Override
    public ByteBuffer putLong(final long n) {
        Bits.putLong(this, this.ix(this.nextPutIndex(8)), n, this.bigEndian);
        return this;
    }
    
    @Override
    public ByteBuffer putLong(final int n, final long n2) {
        Bits.putLong(this, this.ix(this.checkIndex(n, 8)), n2, this.bigEndian);
        return this;
    }
    
    @Override
    public LongBuffer asLongBuffer() {
        final int position = this.position();
        final int n = this.limit() - position >> 3;
        final int n2 = this.offset + position;
        return this.bigEndian ? new ByteBufferAsLongBufferB(this, -1, 0, n, n, n2) : new ByteBufferAsLongBufferL(this, -1, 0, n, n, n2);
    }
    
    @Override
    public float getFloat() {
        return Bits.getFloat(this, this.ix(this.nextGetIndex(4)), this.bigEndian);
    }
    
    @Override
    public float getFloat(final int n) {
        return Bits.getFloat(this, this.ix(this.checkIndex(n, 4)), this.bigEndian);
    }
    
    @Override
    public ByteBuffer putFloat(final float n) {
        Bits.putFloat(this, this.ix(this.nextPutIndex(4)), n, this.bigEndian);
        return this;
    }
    
    @Override
    public ByteBuffer putFloat(final int n, final float n2) {
        Bits.putFloat(this, this.ix(this.checkIndex(n, 4)), n2, this.bigEndian);
        return this;
    }
    
    @Override
    public FloatBuffer asFloatBuffer() {
        final int position = this.position();
        final int n = this.limit() - position >> 2;
        final int n2 = this.offset + position;
        return this.bigEndian ? new ByteBufferAsFloatBufferB(this, -1, 0, n, n, n2) : new ByteBufferAsFloatBufferL(this, -1, 0, n, n, n2);
    }
    
    @Override
    public double getDouble() {
        return Bits.getDouble(this, this.ix(this.nextGetIndex(8)), this.bigEndian);
    }
    
    @Override
    public double getDouble(final int n) {
        return Bits.getDouble(this, this.ix(this.checkIndex(n, 8)), this.bigEndian);
    }
    
    @Override
    public ByteBuffer putDouble(final double n) {
        Bits.putDouble(this, this.ix(this.nextPutIndex(8)), n, this.bigEndian);
        return this;
    }
    
    @Override
    public ByteBuffer putDouble(final int n, final double n2) {
        Bits.putDouble(this, this.ix(this.checkIndex(n, 8)), n2, this.bigEndian);
        return this;
    }
    
    @Override
    public DoubleBuffer asDoubleBuffer() {
        final int position = this.position();
        final int n = this.limit() - position >> 3;
        final int n2 = this.offset + position;
        return this.bigEndian ? new ByteBufferAsDoubleBufferB(this, -1, 0, n, n, n2) : new ByteBufferAsDoubleBufferL(this, -1, 0, n, n, n2);
    }
}
