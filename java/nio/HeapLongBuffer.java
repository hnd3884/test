package java.nio;

class HeapLongBuffer extends LongBuffer
{
    HeapLongBuffer(final int n, final int n2) {
        super(-1, 0, n2, n, new long[n], 0);
    }
    
    HeapLongBuffer(final long[] array, final int n, final int n2) {
        super(-1, n, n + n2, array.length, array, 0);
    }
    
    protected HeapLongBuffer(final long[] array, final int n, final int n2, final int n3, final int n4, final int n5) {
        super(n, n2, n3, n4, array, n5);
    }
    
    @Override
    public LongBuffer slice() {
        final int position = this.position();
        final int limit = this.limit();
        final int n = (position <= limit) ? (limit - position) : 0;
        return new HeapLongBuffer(this.hb, -1, 0, n, n, position + this.offset);
    }
    
    @Override
    public LongBuffer duplicate() {
        return new HeapLongBuffer(this.hb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
    }
    
    @Override
    public LongBuffer asReadOnlyBuffer() {
        return new HeapLongBufferR(this.hb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
    }
    
    protected int ix(final int n) {
        return n + this.offset;
    }
    
    @Override
    public long get() {
        return this.hb[this.ix(this.nextGetIndex())];
    }
    
    @Override
    public long get(final int n) {
        return this.hb[this.ix(this.checkIndex(n))];
    }
    
    @Override
    public LongBuffer get(final long[] array, final int n, final int n2) {
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
    public LongBuffer put(final long n) {
        this.hb[this.ix(this.nextPutIndex())] = n;
        return this;
    }
    
    @Override
    public LongBuffer put(final int n, final long n2) {
        this.hb[this.ix(this.checkIndex(n))] = n2;
        return this;
    }
    
    @Override
    public LongBuffer put(final long[] array, final int n, final int n2) {
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
    public LongBuffer put(final LongBuffer longBuffer) {
        if (longBuffer instanceof HeapLongBuffer) {
            if (longBuffer == this) {
                throw new IllegalArgumentException();
            }
            final HeapLongBuffer heapLongBuffer = (HeapLongBuffer)longBuffer;
            final int position = this.position();
            final int position2 = heapLongBuffer.position();
            final int n = heapLongBuffer.limit() - position2;
            if (n > this.limit() - position) {
                throw new BufferOverflowException();
            }
            System.arraycopy(heapLongBuffer.hb, heapLongBuffer.ix(position2), this.hb, this.ix(position), n);
            heapLongBuffer.position(position2 + n);
            this.position(position + n);
        }
        else if (longBuffer.isDirect()) {
            final int remaining = longBuffer.remaining();
            final int position3 = this.position();
            if (remaining > this.limit() - position3) {
                throw new BufferOverflowException();
            }
            longBuffer.get(this.hb, this.ix(position3), remaining);
            this.position(position3 + remaining);
        }
        else {
            super.put(longBuffer);
        }
        return this;
    }
    
    @Override
    public LongBuffer compact() {
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
