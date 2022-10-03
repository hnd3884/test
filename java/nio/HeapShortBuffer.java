package java.nio;

class HeapShortBuffer extends ShortBuffer
{
    HeapShortBuffer(final int n, final int n2) {
        super(-1, 0, n2, n, new short[n], 0);
    }
    
    HeapShortBuffer(final short[] array, final int n, final int n2) {
        super(-1, n, n + n2, array.length, array, 0);
    }
    
    protected HeapShortBuffer(final short[] array, final int n, final int n2, final int n3, final int n4, final int n5) {
        super(n, n2, n3, n4, array, n5);
    }
    
    @Override
    public ShortBuffer slice() {
        final int position = this.position();
        final int limit = this.limit();
        final int n = (position <= limit) ? (limit - position) : 0;
        return new HeapShortBuffer(this.hb, -1, 0, n, n, position + this.offset);
    }
    
    @Override
    public ShortBuffer duplicate() {
        return new HeapShortBuffer(this.hb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
    }
    
    @Override
    public ShortBuffer asReadOnlyBuffer() {
        return new HeapShortBufferR(this.hb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
    }
    
    protected int ix(final int n) {
        return n + this.offset;
    }
    
    @Override
    public short get() {
        return this.hb[this.ix(this.nextGetIndex())];
    }
    
    @Override
    public short get(final int n) {
        return this.hb[this.ix(this.checkIndex(n))];
    }
    
    @Override
    public ShortBuffer get(final short[] array, final int n, final int n2) {
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
    public ShortBuffer put(final short n) {
        this.hb[this.ix(this.nextPutIndex())] = n;
        return this;
    }
    
    @Override
    public ShortBuffer put(final int n, final short n2) {
        this.hb[this.ix(this.checkIndex(n))] = n2;
        return this;
    }
    
    @Override
    public ShortBuffer put(final short[] array, final int n, final int n2) {
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
    public ShortBuffer put(final ShortBuffer shortBuffer) {
        if (shortBuffer instanceof HeapShortBuffer) {
            if (shortBuffer == this) {
                throw new IllegalArgumentException();
            }
            final HeapShortBuffer heapShortBuffer = (HeapShortBuffer)shortBuffer;
            final int position = this.position();
            final int position2 = heapShortBuffer.position();
            final int n = heapShortBuffer.limit() - position2;
            if (n > this.limit() - position) {
                throw new BufferOverflowException();
            }
            System.arraycopy(heapShortBuffer.hb, heapShortBuffer.ix(position2), this.hb, this.ix(position), n);
            heapShortBuffer.position(position2 + n);
            this.position(position + n);
        }
        else if (shortBuffer.isDirect()) {
            final int remaining = shortBuffer.remaining();
            final int position3 = this.position();
            if (remaining > this.limit() - position3) {
                throw new BufferOverflowException();
            }
            shortBuffer.get(this.hb, this.ix(position3), remaining);
            this.position(position3 + remaining);
        }
        else {
            super.put(shortBuffer);
        }
        return this;
    }
    
    @Override
    public ShortBuffer compact() {
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
    public ByteOrder order() {
        return ByteOrder.nativeOrder();
    }
}
