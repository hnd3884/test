package java.awt.font;

import java.text.CharacterIterator;
import java.text.AttributedCharacterIterator;
import java.text.BreakIterator;

public final class LineBreakMeasurer
{
    private BreakIterator breakIter;
    private int start;
    private int pos;
    private int limit;
    private TextMeasurer measurer;
    private CharArrayIterator charIter;
    
    public LineBreakMeasurer(final AttributedCharacterIterator attributedCharacterIterator, final FontRenderContext fontRenderContext) {
        this(attributedCharacterIterator, BreakIterator.getLineInstance(), fontRenderContext);
    }
    
    public LineBreakMeasurer(final AttributedCharacterIterator attributedCharacterIterator, final BreakIterator breakIter, final FontRenderContext fontRenderContext) {
        if (attributedCharacterIterator.getEndIndex() - attributedCharacterIterator.getBeginIndex() < 1) {
            throw new IllegalArgumentException("Text must contain at least one character.");
        }
        this.breakIter = breakIter;
        this.measurer = new TextMeasurer(attributedCharacterIterator, fontRenderContext);
        this.limit = attributedCharacterIterator.getEndIndex();
        final int beginIndex = attributedCharacterIterator.getBeginIndex();
        this.start = beginIndex;
        this.pos = beginIndex;
        this.charIter = new CharArrayIterator(this.measurer.getChars(), this.start);
        this.breakIter.setText(this.charIter);
    }
    
    public int nextOffset(final float n) {
        return this.nextOffset(n, this.limit, false);
    }
    
    public int nextOffset(final float n, final int n2, final boolean b) {
        int n3 = this.pos;
        if (this.pos < this.limit) {
            if (n2 <= this.pos) {
                throw new IllegalArgumentException("offsetLimit must be after current position");
            }
            final int lineBreakIndex = this.measurer.getLineBreakIndex(this.pos, n);
            if (lineBreakIndex == this.limit) {
                n3 = this.limit;
            }
            else if (Character.isWhitespace(this.measurer.getChars()[lineBreakIndex - this.start])) {
                n3 = this.breakIter.following(lineBreakIndex);
            }
            else {
                final int n4 = lineBreakIndex + 1;
                if (n4 == this.limit) {
                    this.breakIter.last();
                    n3 = this.breakIter.previous();
                }
                else {
                    n3 = this.breakIter.preceding(n4);
                }
                if (n3 <= this.pos) {
                    if (b) {
                        n3 = this.pos;
                    }
                    else {
                        n3 = Math.max(this.pos + 1, lineBreakIndex);
                    }
                }
            }
        }
        if (n3 > n2) {
            n3 = n2;
        }
        return n3;
    }
    
    public TextLayout nextLayout(final float n) {
        return this.nextLayout(n, this.limit, false);
    }
    
    public TextLayout nextLayout(final float n, final int n2, final boolean b) {
        if (this.pos >= this.limit) {
            return null;
        }
        final int nextOffset = this.nextOffset(n, n2, b);
        if (nextOffset == this.pos) {
            return null;
        }
        final TextLayout layout = this.measurer.getLayout(this.pos, nextOffset);
        this.pos = nextOffset;
        return layout;
    }
    
    public int getPosition() {
        return this.pos;
    }
    
    public void setPosition(final int pos) {
        if (pos < this.start || pos > this.limit) {
            throw new IllegalArgumentException("position is out of range");
        }
        this.pos = pos;
    }
    
    public void insertChar(final AttributedCharacterIterator attributedCharacterIterator, final int n) {
        this.measurer.insertChar(attributedCharacterIterator, n);
        this.limit = attributedCharacterIterator.getEndIndex();
        final int beginIndex = attributedCharacterIterator.getBeginIndex();
        this.start = beginIndex;
        this.pos = beginIndex;
        this.charIter.reset(this.measurer.getChars(), attributedCharacterIterator.getBeginIndex());
        this.breakIter.setText(this.charIter);
    }
    
    public void deleteChar(final AttributedCharacterIterator attributedCharacterIterator, final int n) {
        this.measurer.deleteChar(attributedCharacterIterator, n);
        this.limit = attributedCharacterIterator.getEndIndex();
        final int beginIndex = attributedCharacterIterator.getBeginIndex();
        this.start = beginIndex;
        this.pos = beginIndex;
        this.charIter.reset(this.measurer.getChars(), this.start);
        this.breakIter.setText(this.charIter);
    }
}
