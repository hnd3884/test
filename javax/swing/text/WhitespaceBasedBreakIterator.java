package javax.swing.text;

import java.util.Arrays;
import java.text.StringCharacterIterator;
import java.text.CharacterIterator;
import java.text.BreakIterator;

class WhitespaceBasedBreakIterator extends BreakIterator
{
    private char[] text;
    private int[] breaks;
    private int pos;
    
    WhitespaceBasedBreakIterator() {
        this.text = new char[0];
        this.breaks = new int[] { 0 };
        this.pos = 0;
    }
    
    @Override
    public void setText(final CharacterIterator characterIterator) {
        final int beginIndex = characterIterator.getBeginIndex();
        this.text = new char[characterIterator.getEndIndex() - beginIndex];
        final int[] array = new int[this.text.length + 1];
        int n = 0;
        array[n++] = beginIndex;
        int n2 = 0;
        boolean b = false;
        for (char c = characterIterator.first(); c != '\uffff'; c = characterIterator.next()) {
            this.text[n2] = c;
            final boolean whitespace = Character.isWhitespace(c);
            if (b && !whitespace) {
                array[n++] = n2 + beginIndex;
            }
            b = whitespace;
            ++n2;
        }
        if (this.text.length > 0) {
            array[n++] = this.text.length + beginIndex;
        }
        System.arraycopy(array, 0, this.breaks = new int[n], 0, n);
    }
    
    @Override
    public CharacterIterator getText() {
        return new StringCharacterIterator(new String(this.text));
    }
    
    @Override
    public int first() {
        final int[] breaks = this.breaks;
        final int pos = 0;
        this.pos = pos;
        return breaks[pos];
    }
    
    @Override
    public int last() {
        final int[] breaks = this.breaks;
        final int pos = this.breaks.length - 1;
        this.pos = pos;
        return breaks[pos];
    }
    
    @Override
    public int current() {
        return this.breaks[this.pos];
    }
    
    @Override
    public int next() {
        return (this.pos == this.breaks.length - 1) ? -1 : this.breaks[++this.pos];
    }
    
    @Override
    public int previous() {
        int n;
        if (this.pos == 0) {
            n = -1;
        }
        else {
            final int[] breaks = this.breaks;
            final int pos = this.pos - 1;
            this.pos = pos;
            n = breaks[pos];
        }
        return n;
    }
    
    @Override
    public int next(final int n) {
        return this.checkhit(this.pos + n);
    }
    
    @Override
    public int following(final int n) {
        return this.adjacent(n, 1);
    }
    
    @Override
    public int preceding(final int n) {
        return this.adjacent(n, -1);
    }
    
    private int checkhit(final int pos) {
        if (pos < 0 || pos >= this.breaks.length) {
            return -1;
        }
        final int[] breaks = this.breaks;
        this.pos = pos;
        return breaks[pos];
    }
    
    private int adjacent(final int n, final int n2) {
        final int binarySearch = Arrays.binarySearch(this.breaks, n);
        return this.checkhit(Math.abs(binarySearch) + n2 + ((binarySearch < 0) ? ((n2 < 0) ? -1 : -2) : 0));
    }
}
