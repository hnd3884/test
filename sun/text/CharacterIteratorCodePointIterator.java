package sun.text;

import java.text.CharacterIterator;

final class CharacterIteratorCodePointIterator extends CodePointIterator
{
    private CharacterIterator iter;
    
    public CharacterIteratorCodePointIterator(final CharacterIterator iter) {
        this.iter = iter;
    }
    
    @Override
    public void setToStart() {
        this.iter.setIndex(this.iter.getBeginIndex());
    }
    
    @Override
    public void setToLimit() {
        this.iter.setIndex(this.iter.getEndIndex());
    }
    
    @Override
    public int next() {
        final char current = this.iter.current();
        if (current == '\uffff') {
            return -1;
        }
        final char next = this.iter.next();
        if (Character.isHighSurrogate(current) && next != '\uffff' && Character.isLowSurrogate(next)) {
            this.iter.next();
            return Character.toCodePoint(current, next);
        }
        return current;
    }
    
    @Override
    public int prev() {
        final char previous = this.iter.previous();
        if (previous != '\uffff') {
            if (Character.isLowSurrogate(previous)) {
                final char previous2 = this.iter.previous();
                if (Character.isHighSurrogate(previous2)) {
                    return Character.toCodePoint(previous2, previous);
                }
                this.iter.next();
            }
            return previous;
        }
        return -1;
    }
    
    @Override
    public int charIndex() {
        return this.iter.getIndex();
    }
}
