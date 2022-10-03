package sun.text;

final class CharSequenceCodePointIterator extends CodePointIterator
{
    private CharSequence text;
    private int index;
    
    public CharSequenceCodePointIterator(final CharSequence text) {
        this.text = text;
    }
    
    @Override
    public void setToStart() {
        this.index = 0;
    }
    
    @Override
    public void setToLimit() {
        this.index = this.text.length();
    }
    
    @Override
    public int next() {
        if (this.index < this.text.length()) {
            final char char1 = this.text.charAt(this.index++);
            if (Character.isHighSurrogate(char1) && this.index < this.text.length()) {
                final char char2 = this.text.charAt(this.index + 1);
                if (Character.isLowSurrogate(char2)) {
                    ++this.index;
                    return Character.toCodePoint(char1, char2);
                }
            }
            return char1;
        }
        return -1;
    }
    
    @Override
    public int prev() {
        if (this.index > 0) {
            final CharSequence text = this.text;
            final int index = this.index - 1;
            this.index = index;
            final char char1 = text.charAt(index);
            if (Character.isLowSurrogate(char1) && this.index > 0) {
                final char char2 = this.text.charAt(this.index - 1);
                if (Character.isHighSurrogate(char2)) {
                    --this.index;
                    return Character.toCodePoint(char2, char1);
                }
            }
            return char1;
        }
        return -1;
    }
    
    @Override
    public int charIndex() {
        return this.index;
    }
}
