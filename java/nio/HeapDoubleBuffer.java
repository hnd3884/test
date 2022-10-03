package java.nio;

class HeapDoubleBuffer extends DoubleBuffer
{
    HeapDoubleBuffer(final int n, final int n2) {
        super(-1, 0, n2, n, new double[n], 0);
    }
    
    HeapDoubleBuffer(final double[] array, final int n, final int n2) {
        super(-1, n, n + n2, array.length, array, 0);
    }
    
    protected HeapDoubleBuffer(final double[] array, final int n, final int n2, final int n3, final int n4, final int n5) {
        super(n, n2, n3, n4, array, n5);
    }
    
    @Override
    public DoubleBuffer slice() {
        final int position = this.position();
        final int limit = this.limit();
        final int n = (position <= limit) ? (limit - position) : 0;
        return new HeapDoubleBuffer(this.hb, -1, 0, n, n, position + this.offset);
    }
    
    @Override
    public DoubleBuffer duplicate() {
        return new HeapDoubleBuffer(this.hb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
    }
    
    @Override
    public DoubleBuffer asReadOnlyBuffer() {
        return new HeapDoubleBufferR(this.hb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
    }
    
    protected int ix(final int n) {
        return n + this.offset;
    }
    
    @Override
    public double get() {
        return this.hb[this.ix(this.nextGetIndex())];
    }
    
    @Override
    public double get(final int n) {
        return this.hb[this.ix(this.checkIndex(n))];
    }
    
    @Override
    public DoubleBuffer get(final double[] array, final int n, final int n2) {
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
    public DoubleBuffer put(final double n) {
        this.hb[this.ix(this.nextPutIndex())] = n;
        return this;
    }
    
    @Override
    public DoubleBuffer put(final int n, final double n2) {
        this.hb[this.ix(this.checkIndex(n))] = n2;
        return this;
    }
    
    @Override
    public DoubleBuffer put(final double[] array, final int n, final int n2) {
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
    public DoubleBuffer put(final DoubleBuffer doubleBuffer) {
        if (doubleBuffer instanceof HeapDoubleBuffer) {
            if (doubleBuffer == this) {
                throw new IllegalArgumentException();
            }
            final HeapDoubleBuffer heapDoubleBuffer = (HeapDoubleBuffer)doubleBuffer;
            final int position = this.position();
            final int position2 = heapDoubleBuffer.position();
            final int n = heapDoubleBuffer.limit() - position2;
            if (n > this.limit() - position) {
                throw new BufferOverflowException();
            }
            System.arraycopy(heapDoubleBuffer.hb, heapDoubleBuffer.ix(position2), this.hb, this.ix(position), n);
            heapDoubleBuffer.position(position2 + n);
            this.position(position + n);
        }
        else if (doubleBuffer.isDirect()) {
            final int remaining = doubleBuffer.remaining();
            final int position3 = this.position();
            if (remaining > this.limit() - position3) {
                throw new BufferOverflowException();
            }
            doubleBuffer.get(this.hb, this.ix(position3), remaining);
            this.position(position3 + remaining);
        }
        else {
            super.put(doubleBuffer);
        }
        return this;
    }
    
    @Override
    public DoubleBuffer compact() {
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
