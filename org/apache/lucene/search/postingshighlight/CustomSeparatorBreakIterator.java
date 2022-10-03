package org.apache.lucene.search.postingshighlight;

import java.text.CharacterIterator;
import java.text.BreakIterator;

public final class CustomSeparatorBreakIterator extends BreakIterator
{
    private final char separator;
    private CharacterIterator text;
    private int current;
    
    public CustomSeparatorBreakIterator(final char separator) {
        this.separator = separator;
    }
    
    @Override
    public int current() {
        return this.current;
    }
    
    @Override
    public int first() {
        this.text.setIndex(this.text.getBeginIndex());
        return this.current = this.text.getIndex();
    }
    
    @Override
    public int last() {
        this.text.setIndex(this.text.getEndIndex());
        return this.current = this.text.getIndex();
    }
    
    @Override
    public int next() {
        if (this.text.getIndex() == this.text.getEndIndex()) {
            return -1;
        }
        return this.advanceForward();
    }
    
    private int advanceForward() {
        char c;
        while ((c = this.text.next()) != '\uffff') {
            if (c == this.separator) {
                return this.current = this.text.getIndex() + 1;
            }
        }
        assert this.text.getIndex() == this.text.getEndIndex();
        return this.current = this.text.getIndex();
    }
    
    @Override
    public int following(final int pos) {
        if (pos < this.text.getBeginIndex() || pos > this.text.getEndIndex()) {
            throw new IllegalArgumentException("offset out of bounds");
        }
        if (pos == this.text.getEndIndex()) {
            this.text.setIndex(this.text.getEndIndex());
            this.current = this.text.getIndex();
            return -1;
        }
        this.text.setIndex(pos);
        this.current = this.text.getIndex();
        return this.advanceForward();
    }
    
    @Override
    public int previous() {
        if (this.text.getIndex() == this.text.getBeginIndex()) {
            return -1;
        }
        return this.advanceBackward();
    }
    
    private int advanceBackward() {
        char c;
        while ((c = this.text.previous()) != '\uffff') {
            if (c == this.separator) {
                return this.current = this.text.getIndex() + 1;
            }
        }
        assert this.text.getIndex() == this.text.getBeginIndex();
        return this.current = this.text.getIndex();
    }
    
    @Override
    public int preceding(final int pos) {
        if (pos < this.text.getBeginIndex() || pos > this.text.getEndIndex()) {
            throw new IllegalArgumentException("offset out of bounds");
        }
        if (pos == this.text.getBeginIndex()) {
            this.text.setIndex(this.text.getBeginIndex());
            this.current = this.text.getIndex();
            return -1;
        }
        this.text.setIndex(pos);
        this.current = this.text.getIndex();
        return this.advanceBackward();
    }
    
    @Override
    public int next(final int n) {
        if (n < 0) {
            for (int i = 0; i < -n; ++i) {
                this.previous();
            }
        }
        else {
            for (int i = 0; i < n; ++i) {
                this.next();
            }
        }
        return this.current();
    }
    
    @Override
    public CharacterIterator getText() {
        return this.text;
    }
    
    @Override
    public void setText(final CharacterIterator newText) {
        this.text = newText;
        this.current = this.text.getBeginIndex();
    }
}
