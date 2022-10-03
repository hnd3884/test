package org.antlr.v4.runtime;

import java.util.Arrays;
import org.antlr.v4.runtime.misc.Interval;

public class UnbufferedTokenStream<T extends Token> implements TokenStream
{
    protected TokenSource tokenSource;
    protected Token[] tokens;
    protected int n;
    protected int p;
    protected int numMarkers;
    protected Token lastToken;
    protected Token lastTokenBufferStart;
    protected int currentTokenIndex;
    
    public UnbufferedTokenStream(final TokenSource tokenSource) {
        this(tokenSource, 256);
    }
    
    public UnbufferedTokenStream(final TokenSource tokenSource, final int bufferSize) {
        this.p = 0;
        this.numMarkers = 0;
        this.currentTokenIndex = 0;
        this.tokenSource = tokenSource;
        this.tokens = new Token[bufferSize];
        this.n = 0;
        this.fill(1);
    }
    
    @Override
    public Token get(final int i) {
        final int bufferStartIndex = this.getBufferStartIndex();
        if (i < bufferStartIndex || i >= bufferStartIndex + this.n) {
            throw new IndexOutOfBoundsException("get(" + i + ") outside buffer: " + bufferStartIndex + ".." + (bufferStartIndex + this.n));
        }
        return this.tokens[i - bufferStartIndex];
    }
    
    @Override
    public Token LT(final int i) {
        if (i == -1) {
            return this.lastToken;
        }
        this.sync(i);
        final int index = this.p + i - 1;
        if (index < 0) {
            throw new IndexOutOfBoundsException("LT(" + i + ") gives negative index");
        }
        if (index < this.n) {
            return this.tokens[index];
        }
        assert this.n > 0 && this.tokens[this.n - 1].getType() == -1;
        return this.tokens[this.n - 1];
    }
    
    @Override
    public int LA(final int i) {
        return this.LT(i).getType();
    }
    
    @Override
    public TokenSource getTokenSource() {
        return this.tokenSource;
    }
    
    @Override
    public String getText() {
        return "";
    }
    
    @Override
    public String getText(final RuleContext ctx) {
        return this.getText(ctx.getSourceInterval());
    }
    
    @Override
    public String getText(final Token start, final Token stop) {
        return this.getText(Interval.of(start.getTokenIndex(), stop.getTokenIndex()));
    }
    
    @Override
    public void consume() {
        if (this.LA(1) == -1) {
            throw new IllegalStateException("cannot consume EOF");
        }
        this.lastToken = this.tokens[this.p];
        if (this.p == this.n - 1 && this.numMarkers == 0) {
            this.n = 0;
            this.p = -1;
            this.lastTokenBufferStart = this.lastToken;
        }
        ++this.p;
        ++this.currentTokenIndex;
        this.sync(1);
    }
    
    protected void sync(final int want) {
        final int need = this.p + want - 1 - this.n + 1;
        if (need > 0) {
            this.fill(need);
        }
    }
    
    protected int fill(final int n) {
        for (int i = 0; i < n; ++i) {
            if (this.n > 0 && this.tokens[this.n - 1].getType() == -1) {
                return i;
            }
            final Token t = this.tokenSource.nextToken();
            this.add(t);
        }
        return n;
    }
    
    protected void add(final Token t) {
        if (this.n >= this.tokens.length) {
            this.tokens = Arrays.copyOf(this.tokens, this.tokens.length * 2);
        }
        if (t instanceof WritableToken) {
            ((WritableToken)t).setTokenIndex(this.getBufferStartIndex() + this.n);
        }
        this.tokens[this.n++] = t;
    }
    
    @Override
    public int mark() {
        if (this.numMarkers == 0) {
            this.lastTokenBufferStart = this.lastToken;
        }
        final int mark = -this.numMarkers - 1;
        ++this.numMarkers;
        return mark;
    }
    
    @Override
    public void release(final int marker) {
        final int expectedMark = -this.numMarkers;
        if (marker != expectedMark) {
            throw new IllegalStateException("release() called with an invalid marker.");
        }
        --this.numMarkers;
        if (this.numMarkers == 0) {
            if (this.p > 0) {
                System.arraycopy(this.tokens, this.p, this.tokens, 0, this.n - this.p);
                this.n -= this.p;
                this.p = 0;
            }
            this.lastTokenBufferStart = this.lastToken;
        }
    }
    
    @Override
    public int index() {
        return this.currentTokenIndex;
    }
    
    @Override
    public void seek(int index) {
        if (index == this.currentTokenIndex) {
            return;
        }
        if (index > this.currentTokenIndex) {
            this.sync(index - this.currentTokenIndex);
            index = Math.min(index, this.getBufferStartIndex() + this.n - 1);
        }
        final int bufferStartIndex = this.getBufferStartIndex();
        final int i = index - bufferStartIndex;
        if (i < 0) {
            throw new IllegalArgumentException("cannot seek to negative index " + index);
        }
        if (i >= this.n) {
            throw new UnsupportedOperationException("seek to index outside buffer: " + index + " not in " + bufferStartIndex + ".." + (bufferStartIndex + this.n));
        }
        this.p = i;
        this.currentTokenIndex = index;
        if (this.p == 0) {
            this.lastToken = this.lastTokenBufferStart;
        }
        else {
            this.lastToken = this.tokens[this.p - 1];
        }
    }
    
    @Override
    public int size() {
        throw new UnsupportedOperationException("Unbuffered stream cannot know its size");
    }
    
    @Override
    public String getSourceName() {
        return this.tokenSource.getSourceName();
    }
    
    @Override
    public String getText(final Interval interval) {
        final int bufferStartIndex = this.getBufferStartIndex();
        final int bufferStopIndex = bufferStartIndex + this.tokens.length - 1;
        final int start = interval.a;
        final int stop = interval.b;
        if (start < bufferStartIndex || stop > bufferStopIndex) {
            throw new UnsupportedOperationException("interval " + interval + " not in token buffer window: " + bufferStartIndex + ".." + bufferStopIndex);
        }
        final int a = start - bufferStartIndex;
        final int b = stop - bufferStartIndex;
        final StringBuilder buf = new StringBuilder();
        for (int i = a; i <= b; ++i) {
            final Token t = this.tokens[i];
            buf.append(t.getText());
        }
        return buf.toString();
    }
    
    protected final int getBufferStartIndex() {
        return this.currentTokenIndex - this.p;
    }
}
