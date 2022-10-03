package javax.swing.text;

import java.text.CharacterIterator;

public class Segment implements Cloneable, CharacterIterator, CharSequence
{
    public char[] array;
    public int offset;
    public int count;
    private boolean partialReturn;
    private int pos;
    
    public Segment() {
        this(null, 0, 0);
    }
    
    public Segment(final char[] array, final int offset, final int count) {
        this.array = array;
        this.offset = offset;
        this.count = count;
        this.partialReturn = false;
    }
    
    public void setPartialReturn(final boolean partialReturn) {
        this.partialReturn = partialReturn;
    }
    
    public boolean isPartialReturn() {
        return this.partialReturn;
    }
    
    @Override
    public String toString() {
        if (this.array != null) {
            return new String(this.array, this.offset, this.count);
        }
        return "";
    }
    
    @Override
    public char first() {
        this.pos = this.offset;
        if (this.count != 0) {
            return this.array[this.pos];
        }
        return '\uffff';
    }
    
    @Override
    public char last() {
        this.pos = this.offset + this.count;
        if (this.count != 0) {
            --this.pos;
            return this.array[this.pos];
        }
        return '\uffff';
    }
    
    @Override
    public char current() {
        if (this.count != 0 && this.pos < this.offset + this.count) {
            return this.array[this.pos];
        }
        return '\uffff';
    }
    
    @Override
    public char next() {
        ++this.pos;
        final int pos = this.offset + this.count;
        if (this.pos >= pos) {
            this.pos = pos;
            return '\uffff';
        }
        return this.current();
    }
    
    @Override
    public char previous() {
        if (this.pos == this.offset) {
            return '\uffff';
        }
        --this.pos;
        return this.current();
    }
    
    @Override
    public char setIndex(final int pos) {
        final int n = this.offset + this.count;
        if (pos < this.offset || pos > n) {
            throw new IllegalArgumentException("bad position: " + pos);
        }
        this.pos = pos;
        if (this.pos != n && this.count != 0) {
            return this.array[this.pos];
        }
        return '\uffff';
    }
    
    @Override
    public int getBeginIndex() {
        return this.offset;
    }
    
    @Override
    public int getEndIndex() {
        return this.offset + this.count;
    }
    
    @Override
    public int getIndex() {
        return this.pos;
    }
    
    @Override
    public char charAt(final int n) {
        if (n < 0 || n >= this.count) {
            throw new StringIndexOutOfBoundsException(n);
        }
        return this.array[this.offset + n];
    }
    
    @Override
    public int length() {
        return this.count;
    }
    
    @Override
    public CharSequence subSequence(final int n, final int n2) {
        if (n < 0) {
            throw new StringIndexOutOfBoundsException(n);
        }
        if (n2 > this.count) {
            throw new StringIndexOutOfBoundsException(n2);
        }
        if (n > n2) {
            throw new StringIndexOutOfBoundsException(n2 - n);
        }
        final Segment segment = new Segment();
        segment.array = this.array;
        segment.offset = this.offset + n;
        segment.count = n2 - n;
        return segment;
    }
    
    @Override
    public Object clone() {
        Object clone;
        try {
            clone = super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            clone = null;
        }
        return clone;
    }
}
