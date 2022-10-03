package java.nio;

class HeapFloatBuffer extends FloatBuffer
{
    HeapFloatBuffer(final int n, final int n2) {
        super(-1, 0, n2, n, new float[n], 0);
    }
    
    HeapFloatBuffer(final float[] array, final int n, final int n2) {
        super(-1, n, n + n2, array.length, array, 0);
    }
    
    protected HeapFloatBuffer(final float[] array, final int n, final int n2, final int n3, final int n4, final int n5) {
        super(n, n2, n3, n4, array, n5);
    }
    
    @Override
    public FloatBuffer slice() {
        final int position = this.position();
        final int limit = this.limit();
        final int n = (position <= limit) ? (limit - position) : 0;
        return new HeapFloatBuffer(this.hb, -1, 0, n, n, position + this.offset);
    }
    
    @Override
    public FloatBuffer duplicate() {
        return new HeapFloatBuffer(this.hb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
    }
    
    @Override
    public FloatBuffer asReadOnlyBuffer() {
        return new HeapFloatBufferR(this.hb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
    }
    
    protected int ix(final int n) {
        return n + this.offset;
    }
    
    @Override
    public float get() {
        return this.hb[this.ix(this.nextGetIndex())];
    }
    
    @Override
    public float get(final int n) {
        return this.hb[this.ix(this.checkIndex(n))];
    }
    
    @Override
    public FloatBuffer get(final float[] array, final int n, final int n2) {
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
    public FloatBuffer put(final float n) {
        this.hb[this.ix(this.nextPutIndex())] = n;
        return this;
    }
    
    @Override
    public FloatBuffer put(final int n, final float n2) {
        this.hb[this.ix(this.checkIndex(n))] = n2;
        return this;
    }
    
    @Override
    public FloatBuffer put(final float[] array, final int n, final int n2) {
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
    public FloatBuffer put(final FloatBuffer floatBuffer) {
        if (floatBuffer instanceof HeapFloatBuffer) {
            if (floatBuffer == this) {
                throw new IllegalArgumentException();
            }
            final HeapFloatBuffer heapFloatBuffer = (HeapFloatBuffer)floatBuffer;
            final int position = this.position();
            final int position2 = heapFloatBuffer.position();
            final int n = heapFloatBuffer.limit() - position2;
            if (n > this.limit() - position) {
                throw new BufferOverflowException();
            }
            System.arraycopy(heapFloatBuffer.hb, heapFloatBuffer.ix(position2), this.hb, this.ix(position), n);
            heapFloatBuffer.position(position2 + n);
            this.position(position + n);
        }
        else if (floatBuffer.isDirect()) {
            final int remaining = floatBuffer.remaining();
            final int position3 = this.position();
            if (remaining > this.limit() - position3) {
                throw new BufferOverflowException();
            }
            floatBuffer.get(this.hb, this.ix(position3), remaining);
            this.position(position3 + remaining);
        }
        else {
            super.put(floatBuffer);
        }
        return this;
    }
    
    @Override
    public FloatBuffer compact() {
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
