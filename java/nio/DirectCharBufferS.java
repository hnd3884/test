package java.nio;

import sun.misc.Cleaner;
import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;

class DirectCharBufferS extends CharBuffer implements DirectBuffer
{
    protected static final Unsafe unsafe;
    private static final long arrayBaseOffset;
    protected static final boolean unaligned;
    private final Object att;
    
    @Override
    public Object attachment() {
        return this.att;
    }
    
    @Override
    public Cleaner cleaner() {
        return null;
    }
    
    DirectCharBufferS(final DirectBuffer att, final int n, final int n2, final int n3, final int n4, final int n5) {
        super(n, n2, n3, n4);
        this.address = att.address() + n5;
        this.att = att;
    }
    
    @Override
    public CharBuffer slice() {
        final int position = this.position();
        final int limit = this.limit();
        final int n = (position <= limit) ? (limit - position) : 0;
        final int n2 = position << 1;
        assert n2 >= 0;
        return new DirectCharBufferS(this, -1, 0, n, n, n2);
    }
    
    @Override
    public CharBuffer duplicate() {
        return new DirectCharBufferS(this, this.markValue(), this.position(), this.limit(), this.capacity(), 0);
    }
    
    @Override
    public CharBuffer asReadOnlyBuffer() {
        return new DirectCharBufferRS(this, this.markValue(), this.position(), this.limit(), this.capacity(), 0);
    }
    
    @Override
    public long address() {
        return this.address;
    }
    
    private long ix(final int n) {
        return this.address + ((long)n << 1);
    }
    
    @Override
    public char get() {
        return Bits.swap(DirectCharBufferS.unsafe.getChar(this.ix(this.nextGetIndex())));
    }
    
    @Override
    public char get(final int n) {
        return Bits.swap(DirectCharBufferS.unsafe.getChar(this.ix(this.checkIndex(n))));
    }
    
    @Override
    char getUnchecked(final int n) {
        return Bits.swap(DirectCharBufferS.unsafe.getChar(this.ix(n)));
    }
    
    @Override
    public CharBuffer get(final char[] array, final int n, final int n2) {
        if ((long)n2 << 1 > 6L) {
            Buffer.checkBounds(n, n2, array.length);
            final int position = this.position();
            final int limit = this.limit();
            assert position <= limit;
            if (n2 > ((position <= limit) ? (limit - position) : 0)) {
                throw new BufferUnderflowException();
            }
            if (this.order() != ByteOrder.nativeOrder()) {
                Bits.copyToCharArray(this.ix(position), array, (long)n << 1, (long)n2 << 1);
            }
            else {
                Bits.copyToArray(this.ix(position), array, DirectCharBufferS.arrayBaseOffset, (long)n << 1, (long)n2 << 1);
            }
            this.position(position + n2);
        }
        else {
            super.get(array, n, n2);
        }
        return this;
    }
    
    @Override
    public CharBuffer put(final char c) {
        DirectCharBufferS.unsafe.putChar(this.ix(this.nextPutIndex()), Bits.swap(c));
        return this;
    }
    
    @Override
    public CharBuffer put(final int n, final char c) {
        DirectCharBufferS.unsafe.putChar(this.ix(this.checkIndex(n)), Bits.swap(c));
        return this;
    }
    
    @Override
    public CharBuffer put(final CharBuffer charBuffer) {
        if (charBuffer instanceof DirectCharBufferS) {
            if (charBuffer == this) {
                throw new IllegalArgumentException();
            }
            final DirectCharBufferS directCharBufferS = (DirectCharBufferS)charBuffer;
            final int position = directCharBufferS.position();
            final int limit = directCharBufferS.limit();
            assert position <= limit;
            final int n = (position <= limit) ? (limit - position) : 0;
            final int position2 = this.position();
            final int limit2 = this.limit();
            assert position2 <= limit2;
            if (n > ((position2 <= limit2) ? (limit2 - position2) : 0)) {
                throw new BufferOverflowException();
            }
            DirectCharBufferS.unsafe.copyMemory(directCharBufferS.ix(position), this.ix(position2), (long)n << 1);
            directCharBufferS.position(position + n);
            this.position(position2 + n);
        }
        else if (charBuffer.hb != null) {
            final int position3 = charBuffer.position();
            final int limit3 = charBuffer.limit();
            assert position3 <= limit3;
            final int n2 = (position3 <= limit3) ? (limit3 - position3) : 0;
            this.put(charBuffer.hb, charBuffer.offset + position3, n2);
            charBuffer.position(position3 + n2);
        }
        else {
            super.put(charBuffer);
        }
        return this;
    }
    
    @Override
    public CharBuffer put(final char[] array, final int n, final int n2) {
        if ((long)n2 << 1 > 6L) {
            Buffer.checkBounds(n, n2, array.length);
            final int position = this.position();
            final int limit = this.limit();
            assert position <= limit;
            if (n2 > ((position <= limit) ? (limit - position) : 0)) {
                throw new BufferOverflowException();
            }
            if (this.order() != ByteOrder.nativeOrder()) {
                Bits.copyFromCharArray(array, (long)n << 1, this.ix(position), (long)n2 << 1);
            }
            else {
                Bits.copyFromArray(array, DirectCharBufferS.arrayBaseOffset, (long)n << 1, this.ix(position), (long)n2 << 1);
            }
            this.position(position + n2);
        }
        else {
            super.put(array, n, n2);
        }
        return this;
    }
    
    @Override
    public CharBuffer compact() {
        final int position = this.position();
        final int limit = this.limit();
        assert position <= limit;
        final int n = (position <= limit) ? (limit - position) : 0;
        DirectCharBufferS.unsafe.copyMemory(this.ix(position), this.ix(0), (long)n << 1);
        this.position(n);
        this.limit(this.capacity());
        this.discardMark();
        return this;
    }
    
    @Override
    public boolean isDirect() {
        return true;
    }
    
    @Override
    public boolean isReadOnly() {
        return false;
    }
    
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
        return new DirectCharBufferS(this, -1, n3 + n, n3 + n2, this.capacity(), this.offset);
    }
    
    @Override
    public ByteOrder order() {
        return (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
    }
    
    static {
        unsafe = Bits.unsafe();
        arrayBaseOffset = DirectCharBufferS.unsafe.arrayBaseOffset(char[].class);
        unaligned = Bits.unaligned();
    }
}
