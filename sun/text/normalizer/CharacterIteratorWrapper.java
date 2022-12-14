package sun.text.normalizer;

import java.text.CharacterIterator;

public class CharacterIteratorWrapper extends UCharacterIterator
{
    private CharacterIterator iterator;
    
    public CharacterIteratorWrapper(final CharacterIterator iterator) {
        if (iterator == null) {
            throw new IllegalArgumentException();
        }
        this.iterator = iterator;
    }
    
    @Override
    public int current() {
        final char current = this.iterator.current();
        if (current == '\uffff') {
            return -1;
        }
        return current;
    }
    
    @Override
    public int getLength() {
        return this.iterator.getEndIndex() - this.iterator.getBeginIndex();
    }
    
    @Override
    public int getIndex() {
        return this.iterator.getIndex();
    }
    
    @Override
    public int next() {
        final char current = this.iterator.current();
        this.iterator.next();
        if (current == '\uffff') {
            return -1;
        }
        return current;
    }
    
    @Override
    public int previous() {
        final char previous = this.iterator.previous();
        if (previous == '\uffff') {
            return -1;
        }
        return previous;
    }
    
    @Override
    public void setIndex(final int index) {
        this.iterator.setIndex(index);
    }
    
    @Override
    public int getText(final char[] array, int n) {
        final int n2 = this.iterator.getEndIndex() - this.iterator.getBeginIndex();
        final int index = this.iterator.getIndex();
        if (n < 0 || n + n2 > array.length) {
            throw new IndexOutOfBoundsException(Integer.toString(n2));
        }
        for (char c = this.iterator.first(); c != '\uffff'; c = this.iterator.next()) {
            array[n++] = c;
        }
        this.iterator.setIndex(index);
        return n2;
    }
    
    @Override
    public Object clone() {
        try {
            final CharacterIteratorWrapper characterIteratorWrapper = (CharacterIteratorWrapper)super.clone();
            characterIteratorWrapper.iterator = (CharacterIterator)this.iterator.clone();
            return characterIteratorWrapper;
        }
        catch (final CloneNotSupportedException ex) {
            return null;
        }
    }
}
