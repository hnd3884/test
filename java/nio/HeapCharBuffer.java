package java.nio;

class HeapCharBuffer extends CharBuffer
{
    HeapCharBuffer(final int n, final int n2) {
        super(-1, 0, n2, n, new char[n], 0);
    }
    
    HeapCharBuffer(final char[] array, final int n, final int n2) {
        super(-1, n, n + n2, array.length, array, 0);
    }
    
    protected HeapCharBuffer(final char[] array, final int n, final int n2, final int n3, final int n4, final int n5) {
        super(n, n2, n3, n4, array, n5);
    }
    
    @Override
    public CharBuffer slice() {
        final int position = this.position();
        final int limit = this.limit();
        final int n = (position <= limit) ? (limit - position) : 0;
        return new HeapCharBuffer(this.hb, -1, 0, n, n, position + this.offset);
    }
    
    @Override
    public CharBuffer duplicate() {
        return new HeapCharBuffer(this.hb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
    }
    
    @Override
    public CharBuffer asReadOnlyBuffer() {
        return new HeapCharBufferR(this.hb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
    }
    
    protected int ix(final int n) {
        return n + this.offset;
    }
    
    @Override
    public char get() {
        return this.hb[this.ix(this.nextGetIndex())];
    }
    
    @Override
    public char get(final int n) {
        return this.hb[this.ix(this.checkIndex(n))];
    }
    
    @Override
    char getUnchecked(final int n) {
        return this.hb[this.ix(n)];
    }
    
    @Override
    public CharBuffer get(final char[] array, final int n, final int n2) {
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
    public CharBuffer put(final char c) {
        this.hb[this.ix(this.nextPutIndex())] = c;
        return this;
    }
    
    @Override
    public CharBuffer put(final int n, final char c) {
        this.hb[this.ix(this.checkIndex(n))] = c;
        return this;
    }
    
    @Override
    public CharBuffer put(final char[] array, final int n, final int n2) {
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
    public CharBuffer put(final CharBuffer charBuffer) {
        if (charBuffer instanceof HeapCharBuffer) {
            if (charBuffer == this) {
                throw new IllegalArgumentException();
            }
            final HeapCharBuffer heapCharBuffer = (HeapCharBuffer)charBuffer;
            final int position = this.position();
            final int position2 = heapCharBuffer.position();
            final int n = heapCharBuffer.limit() - position2;
            if (n > this.limit() - position) {
                throw new BufferOverflowException();
            }
            System.arraycopy(heapCharBuffer.hb, heapCharBuffer.ix(position2), this.hb, this.ix(position), n);
            heapCharBuffer.position(position2 + n);
            this.position(position + n);
        }
        else if (charBuffer.isDirect()) {
            final int remaining = charBuffer.remaining();
            final int position3 = this.position();
            if (remaining > this.limit() - position3) {
                throw new BufferOverflowException();
            }
            charBuffer.get(this.hb, this.ix(position3), remaining);
            this.position(position3 + remaining);
        }
        else {
            super.put(charBuffer);
        }
        return this;
    }
    
    @Override
    public CharBuffer compact() {
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
        return new HeapCharBuffer(this.hb, -1, position + n, position + n2, this.capacity(), this.offset);
    }
    
    @Override
    public ByteOrder order() {
        return ByteOrder.nativeOrder();
    }
}
