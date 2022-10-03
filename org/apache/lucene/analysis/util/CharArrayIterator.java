package org.apache.lucene.analysis.util;

import java.text.BreakIterator;
import java.util.Locale;
import java.text.CharacterIterator;

public abstract class CharArrayIterator implements CharacterIterator
{
    private char[] array;
    private int start;
    private int index;
    private int length;
    private int limit;
    public static final boolean HAS_BUGGY_BREAKITERATORS;
    
    public char[] getText() {
        return this.array;
    }
    
    public int getStart() {
        return this.start;
    }
    
    public int getLength() {
        return this.length;
    }
    
    public void setText(final char[] array, final int start, final int length) {
        this.array = array;
        this.start = start;
        this.index = start;
        this.length = length;
        this.limit = start + length;
    }
    
    @Override
    public char current() {
        return (this.index == this.limit) ? '\uffff' : this.jreBugWorkaround(this.array[this.index]);
    }
    
    protected abstract char jreBugWorkaround(final char p0);
    
    @Override
    public char first() {
        this.index = this.start;
        return this.current();
    }
    
    @Override
    public int getBeginIndex() {
        return 0;
    }
    
    @Override
    public int getEndIndex() {
        return this.length;
    }
    
    @Override
    public int getIndex() {
        return this.index - this.start;
    }
    
    @Override
    public char last() {
        this.index = ((this.limit == this.start) ? this.limit : (this.limit - 1));
        return this.current();
    }
    
    @Override
    public char next() {
        if (++this.index >= this.limit) {
            this.index = this.limit;
            return '\uffff';
        }
        return this.current();
    }
    
    @Override
    public char previous() {
        final int index = this.index - 1;
        this.index = index;
        if (index < this.start) {
            this.index = this.start;
            return '\uffff';
        }
        return this.current();
    }
    
    @Override
    public char setIndex(final int position) {
        if (position < this.getBeginIndex() || position > this.getEndIndex()) {
            throw new IllegalArgumentException("Illegal Position: " + position);
        }
        this.index = this.start + position;
        return this.current();
    }
    
    @Override
    public CharArrayIterator clone() {
        try {
            return (CharArrayIterator)super.clone();
        }
        catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static CharArrayIterator newSentenceInstance() {
        if (CharArrayIterator.HAS_BUGGY_BREAKITERATORS) {
            return new CharArrayIterator() {
                @Override
                protected char jreBugWorkaround(final char ch) {
                    return (ch >= '\ud800' && ch <= '\udfff') ? ',' : ch;
                }
            };
        }
        return new CharArrayIterator() {
            @Override
            protected char jreBugWorkaround(final char ch) {
                return ch;
            }
        };
    }
    
    public static CharArrayIterator newWordInstance() {
        if (CharArrayIterator.HAS_BUGGY_BREAKITERATORS) {
            return new CharArrayIterator() {
                @Override
                protected char jreBugWorkaround(final char ch) {
                    return (ch >= '\ud800' && ch <= '\udfff') ? 'A' : ch;
                }
            };
        }
        return new CharArrayIterator() {
            @Override
            protected char jreBugWorkaround(final char ch) {
                return ch;
            }
        };
    }
    
    static {
        boolean v;
        try {
            final BreakIterator bi = BreakIterator.getSentenceInstance(Locale.US);
            bi.setText("\udb40\udc53");
            bi.next();
            v = false;
        }
        catch (final Exception e) {
            v = true;
        }
        HAS_BUGGY_BREAKITERATORS = v;
    }
}
