package org.apache.lucene.analysis.util;

import java.io.Reader;
import java.text.CharacterIterator;
import java.io.IOException;
import org.apache.lucene.util.AttributeFactory;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import java.text.BreakIterator;
import org.apache.lucene.analysis.Tokenizer;

public abstract class SegmentingTokenizerBase extends Tokenizer
{
    protected static final int BUFFERMAX = 1024;
    protected final char[] buffer;
    private int length;
    private int usableLength;
    protected int offset;
    private final BreakIterator iterator;
    private final CharArrayIterator wrapper;
    private final OffsetAttribute offsetAtt;
    
    public SegmentingTokenizerBase(final BreakIterator iterator) {
        this(SegmentingTokenizerBase.DEFAULT_TOKEN_ATTRIBUTE_FACTORY, iterator);
    }
    
    public SegmentingTokenizerBase(final AttributeFactory factory, final BreakIterator iterator) {
        super(factory);
        this.buffer = new char[1024];
        this.length = 0;
        this.usableLength = 0;
        this.offset = 0;
        this.wrapper = CharArrayIterator.newSentenceInstance();
        this.offsetAtt = (OffsetAttribute)this.addAttribute((Class)OffsetAttribute.class);
        this.iterator = iterator;
    }
    
    public final boolean incrementToken() throws IOException {
        if (this.length == 0 || !this.incrementWord()) {
            while (!this.incrementSentence()) {
                this.refill();
                if (this.length <= 0) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public void reset() throws IOException {
        super.reset();
        this.wrapper.setText(this.buffer, 0, 0);
        this.iterator.setText(this.wrapper);
        final int length = 0;
        this.offset = length;
        this.usableLength = length;
        this.length = length;
    }
    
    public final void end() throws IOException {
        super.end();
        final int finalOffset = this.correctOffset((this.length < 0) ? this.offset : (this.offset + this.length));
        this.offsetAtt.setOffset(finalOffset, finalOffset);
    }
    
    private int findSafeEnd() {
        for (int i = this.length - 1; i >= 0; --i) {
            if (this.isSafeEnd(this.buffer[i])) {
                return i + 1;
            }
        }
        return -1;
    }
    
    protected boolean isSafeEnd(final char ch) {
        switch (ch) {
            case '\n':
            case '\r':
            case '\u0085':
            case '\u2028':
            case '\u2029': {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private void refill() throws IOException {
        this.offset += this.usableLength;
        final int leftover = this.length - this.usableLength;
        System.arraycopy(this.buffer, this.usableLength, this.buffer, 0, leftover);
        final int requested = this.buffer.length - leftover;
        final int returned = read(this.input, this.buffer, leftover, requested);
        this.length = ((returned < 0) ? leftover : (returned + leftover));
        if (returned < requested) {
            this.usableLength = this.length;
        }
        else {
            this.usableLength = this.findSafeEnd();
            if (this.usableLength < 0) {
                this.usableLength = this.length;
            }
        }
        this.wrapper.setText(this.buffer, 0, Math.max(0, this.usableLength));
        this.iterator.setText(this.wrapper);
    }
    
    private static int read(final Reader input, final char[] buffer, final int offset, final int length) throws IOException {
        assert length >= 0 : "length must not be negative: " + length;
        int remaining;
        int count;
        for (remaining = length; remaining > 0; remaining -= count) {
            final int location = length - remaining;
            count = input.read(buffer, offset + location, remaining);
            if (-1 == count) {
                break;
            }
        }
        return length - remaining;
    }
    
    private boolean incrementSentence() throws IOException {
        if (this.length == 0) {
            return false;
        }
        while (true) {
            final int start = this.iterator.current();
            if (start == -1) {
                return false;
            }
            final int end = this.iterator.next();
            if (end == -1) {
                return false;
            }
            this.setNextSentence(start, end);
            if (this.incrementWord()) {
                return true;
            }
        }
    }
    
    protected abstract void setNextSentence(final int p0, final int p1);
    
    protected abstract boolean incrementWord();
}
