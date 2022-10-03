package org.apache.lucene.analysis;

import java.io.IOException;
import org.apache.lucene.util.AttributeFactory;
import java.io.Reader;

public abstract class Tokenizer extends TokenStream
{
    protected Reader input;
    private Reader inputPending;
    private static final Reader ILLEGAL_STATE_READER;
    
    protected Tokenizer() {
        this.input = Tokenizer.ILLEGAL_STATE_READER;
        this.inputPending = Tokenizer.ILLEGAL_STATE_READER;
    }
    
    protected Tokenizer(final AttributeFactory factory) {
        super(factory);
        this.input = Tokenizer.ILLEGAL_STATE_READER;
        this.inputPending = Tokenizer.ILLEGAL_STATE_READER;
    }
    
    @Override
    public void close() throws IOException {
        this.input.close();
        final Reader illegal_STATE_READER = Tokenizer.ILLEGAL_STATE_READER;
        this.input = illegal_STATE_READER;
        this.inputPending = illegal_STATE_READER;
    }
    
    protected final int correctOffset(final int currentOff) {
        return (this.input instanceof CharFilter) ? ((CharFilter)this.input).correctOffset(currentOff) : currentOff;
    }
    
    public final void setReader(final Reader input) {
        if (input == null) {
            throw new NullPointerException("input must not be null");
        }
        if (this.input != Tokenizer.ILLEGAL_STATE_READER) {
            throw new IllegalStateException("TokenStream contract violation: close() call missing");
        }
        this.inputPending = input;
        this.setReaderTestPoint();
    }
    
    @Override
    public void reset() throws IOException {
        super.reset();
        this.input = this.inputPending;
        this.inputPending = Tokenizer.ILLEGAL_STATE_READER;
    }
    
    void setReaderTestPoint() {
    }
    
    static {
        ILLEGAL_STATE_READER = new Reader() {
            @Override
            public int read(final char[] cbuf, final int off, final int len) {
                throw new IllegalStateException("TokenStream contract violation: reset()/close() call missing, reset() called multiple times, or subclass does not call super.reset(). Please see Javadocs of TokenStream class for more information about the correct consuming workflow.");
            }
            
            @Override
            public void close() {
            }
        };
    }
}
