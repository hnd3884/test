package java.nio;

class StringCharBuffer extends CharBuffer
{
    CharSequence str;
    
    StringCharBuffer(final CharSequence str, final int n, final int n2) {
        super(-1, n, n2, str.length());
        final int length = str.length();
        if (n < 0 || n > length || n2 < n || n2 > length) {
            throw new IndexOutOfBoundsException();
        }
        this.str = str;
    }
    
    @Override
    public CharBuffer slice() {
        final int position = this.position();
        final int limit = this.limit();
        final int n = (position <= limit) ? (limit - position) : 0;
        return new StringCharBuffer(this.str, -1, 0, n, n, this.offset + position);
    }
    
    private StringCharBuffer(final CharSequence str, final int n, final int n2, final int n3, final int n4, final int n5) {
        super(n, n2, n3, n4, null, n5);
        this.str = str;
    }
    
    @Override
    public CharBuffer duplicate() {
        return new StringCharBuffer(this.str, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
    }
    
    @Override
    public CharBuffer asReadOnlyBuffer() {
        return this.duplicate();
    }
    
    @Override
    public final char get() {
        return this.str.charAt(this.nextGetIndex() + this.offset);
    }
    
    @Override
    public final char get(final int n) {
        return this.str.charAt(this.checkIndex(n) + this.offset);
    }
    
    @Override
    char getUnchecked(final int n) {
        return this.str.charAt(n + this.offset);
    }
    
    @Override
    public final CharBuffer put(final char c) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public final CharBuffer put(final int n, final char c) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public final CharBuffer compact() {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public final boolean isReadOnly() {
        return true;
    }
    
    @Override
    final String toString(final int n, final int n2) {
        return this.str.toString().substring(n + this.offset, n2 + this.offset);
    }
    
    @Override
    public final CharBuffer subSequence(final int n, final int n2) {
        try {
            final int position = this.position();
            return new StringCharBuffer(this.str, -1, position + this.checkIndex(n, position), position + this.checkIndex(n2, position), this.capacity(), this.offset);
        }
        catch (final IllegalArgumentException ex) {
            throw new IndexOutOfBoundsException();
        }
    }
    
    @Override
    public boolean isDirect() {
        return false;
    }
    
    @Override
    public ByteOrder order() {
        return ByteOrder.nativeOrder();
    }
}
