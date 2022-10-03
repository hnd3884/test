package java.nio;

class HeapIntBuffer extends IntBuffer
{
    HeapIntBuffer(final int n, final int n2) {
        super(-1, 0, n2, n, new int[n], 0);
    }
    
    HeapIntBuffer(final int[] array, final int n, final int n2) {
        super(-1, n, n + n2, array.length, array, 0);
    }
    
    protected HeapIntBuffer(final int[] array, final int n, final int n2, final int n3, final int n4, final int n5) {
        super(n, n2, n3, n4, array, n5);
    }
    
    @Override
    public IntBuffer slice() {
        final int position = this.position();
        final int limit = this.limit();
        final int n = (position <= limit) ? (limit - position) : 0;
        return new HeapIntBuffer(this.hb, -1, 0, n, n, position + this.offset);
    }
    
    @Override
    public IntBuffer duplicate() {
        return new HeapIntBuffer(this.hb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
    }
    
    @Override
    public IntBuffer asReadOnlyBuffer() {
        return new HeapIntBufferR(this.hb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
    }
    
    protected int ix(final int n) {
        return n + this.offset;
    }
    
    @Override
    public int get() {
        return this.hb[this.ix(this.nextGetIndex())];
    }
    
    @Override
    public int get(final int n) {
        return this.hb[this.ix(this.checkIndex(n))];
    }
    
    @Override
    public IntBuffer get(final int[] array, final int n, final int n2) {
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
    public IntBuffer put(final int n) {
        this.hb[this.ix(this.nextPutIndex())] = n;
        return this;
    }
    
    @Override
    public IntBuffer put(final int n, final int n2) {
        this.hb[this.ix(this.checkIndex(n))] = n2;
        return this;
    }
    
    @Override
    public IntBuffer put(final int[] array, final int n, final int n2) {
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
    public IntBuffer put(final IntBuffer intBuffer) {
        if (intBuffer instanceof HeapIntBuffer) {
            if (intBuffer == this) {
                throw new IllegalArgumentException();
            }
            final HeapIntBuffer heapIntBuffer = (HeapIntBuffer)intBuffer;
            final int position = this.position();
            final int position2 = heapIntBuffer.position();
            final int n = heapIntBuffer.limit() - position2;
            if (n > this.limit() - position) {
                throw new BufferOverflowException();
            }
            System.arraycopy(heapIntBuffer.hb, heapIntBuffer.ix(position2), this.hb, this.ix(position), n);
            heapIntBuffer.position(position2 + n);
            this.position(position + n);
        }
        else if (intBuffer.isDirect()) {
            final int remaining = intBuffer.remaining();
            final int position3 = this.position();
            if (remaining > this.limit() - position3) {
                throw new BufferOverflowException();
            }
            intBuffer.get(this.hb, this.ix(position3), remaining);
            this.position(position3 + remaining);
        }
        else {
            super.put(intBuffer);
        }
        return this;
    }
    
    @Override
    public IntBuffer compact() {
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
