package java.awt.font;

import java.text.CharacterIterator;

class CharArrayIterator implements CharacterIterator
{
    private char[] chars;
    private int pos;
    private int begin;
    
    CharArrayIterator(final char[] array) {
        this.reset(array, 0);
    }
    
    CharArrayIterator(final char[] array, final int n) {
        this.reset(array, n);
    }
    
    @Override
    public char first() {
        this.pos = 0;
        return this.current();
    }
    
    @Override
    public char last() {
        if (this.chars.length > 0) {
            this.pos = this.chars.length - 1;
        }
        else {
            this.pos = 0;
        }
        return this.current();
    }
    
    @Override
    public char current() {
        if (this.pos >= 0 && this.pos < this.chars.length) {
            return this.chars[this.pos];
        }
        return '\uffff';
    }
    
    @Override
    public char next() {
        if (this.pos < this.chars.length - 1) {
            ++this.pos;
            return this.chars[this.pos];
        }
        this.pos = this.chars.length;
        return '\uffff';
    }
    
    @Override
    public char previous() {
        if (this.pos > 0) {
            --this.pos;
            return this.chars[this.pos];
        }
        this.pos = 0;
        return '\uffff';
    }
    
    @Override
    public char setIndex(int pos) {
        pos -= this.begin;
        if (pos < 0 || pos > this.chars.length) {
            throw new IllegalArgumentException("Invalid index");
        }
        this.pos = pos;
        return this.current();
    }
    
    @Override
    public int getBeginIndex() {
        return this.begin;
    }
    
    @Override
    public int getEndIndex() {
        return this.begin + this.chars.length;
    }
    
    @Override
    public int getIndex() {
        return this.begin + this.pos;
    }
    
    @Override
    public Object clone() {
        final CharArrayIterator charArrayIterator = new CharArrayIterator(this.chars, this.begin);
        charArrayIterator.pos = this.pos;
        return charArrayIterator;
    }
    
    void reset(final char[] array) {
        this.reset(array, 0);
    }
    
    void reset(final char[] chars, final int begin) {
        this.chars = chars;
        this.begin = begin;
        this.pos = 0;
    }
}
