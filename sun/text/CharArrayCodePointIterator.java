package sun.text;

final class CharArrayCodePointIterator extends CodePointIterator
{
    private char[] text;
    private int start;
    private int limit;
    private int index;
    
    public CharArrayCodePointIterator(final char[] text) {
        this.text = text;
        this.limit = text.length;
    }
    
    public CharArrayCodePointIterator(final char[] text, final int n, final int limit) {
        if (n < 0 || limit < n || limit > text.length) {
            throw new IllegalArgumentException();
        }
        this.text = text;
        this.index = n;
        this.start = n;
        this.limit = limit;
    }
    
    @Override
    public void setToStart() {
        this.index = this.start;
    }
    
    @Override
    public void setToLimit() {
        this.index = this.limit;
    }
    
    @Override
    public int next() {
        if (this.index < this.limit) {
            final char c = this.text[this.index++];
            if (Character.isHighSurrogate(c) && this.index < this.limit) {
                final char c2 = this.text[this.index];
                if (Character.isLowSurrogate(c2)) {
                    ++this.index;
                    return Character.toCodePoint(c, c2);
                }
            }
            return c;
        }
        return -1;
    }
    
    @Override
    public int prev() {
        if (this.index > this.start) {
            final char[] text = this.text;
            final int index = this.index - 1;
            this.index = index;
            final char c = text[index];
            if (Character.isLowSurrogate(c) && this.index > this.start) {
                final char c2 = this.text[this.index - 1];
                if (Character.isHighSurrogate(c2)) {
                    --this.index;
                    return Character.toCodePoint(c2, c);
                }
            }
            return c;
        }
        return -1;
    }
    
    @Override
    public int charIndex() {
        return this.index;
    }
}
