package java.nio;

import sun.nio.ch.DirectBuffer;

class DirectCharBufferRS extends DirectCharBufferS implements DirectBuffer
{
    DirectCharBufferRS(final DirectBuffer directBuffer, final int n, final int n2, final int n3, final int n4, final int n5) {
        super(directBuffer, n, n2, n3, n4, n5);
    }
    
    @Override
    public CharBuffer slice() {
        final int position = this.position();
        final int limit = this.limit();
        final int n = (position <= limit) ? (limit - position) : 0;
        final int n2 = position << 1;
        assert n2 >= 0;
        return new DirectCharBufferRS(this, -1, 0, n, n, n2);
    }
    
    @Override
    public CharBuffer duplicate() {
        return new DirectCharBufferRS(this, this.markValue(), this.position(), this.limit(), this.capacity(), 0);
    }
    
    @Override
    public CharBuffer asReadOnlyBuffer() {
        return this.duplicate();
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
    public CharBuffer put(final CharBuffer charBuffer) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public CharBuffer put(final char[] array, final int n, final int n2) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public CharBuffer compact() {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public boolean isDirect() {
        return true;
    }
    
    @Override
    public boolean isReadOnly() {
        return true;
    }
    
    @Override
    public String toString(final int n, final int n2) {
        if (n2 > this.limit() || n > n2) {
            throw new IndexOutOfBoundsException();
        }
        try {
            final char[] array = new char[n2 - n];
            final CharBuffer wrap = CharBuffer.wrap(array);
            final CharBuffer duplicate = this.duplicate();
            duplicate.position(n);
            duplicate.limit(n2);
            wrap.put(duplicate);
            return new String(array);
        }
        catch (final StringIndexOutOfBoundsException ex) {
            throw new IndexOutOfBoundsException();
        }
    }
    
    @Override
    public CharBuffer subSequence(final int n, final int n2) {
        final int position = this.position();
        final int limit = this.limit();
        assert position <= limit;
        final int n3 = (position <= limit) ? position : limit;
        final int n4 = limit - n3;
        if (n < 0 || n2 > n4 || n > n2) {
            throw new IndexOutOfBoundsException();
        }
        return new DirectCharBufferRS(this, -1, n3 + n, n3 + n2, this.capacity(), this.offset);
    }
    
    @Override
    public ByteOrder order() {
        return (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
    }
}
