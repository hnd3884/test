package org.apache.lucene.analysis.core;

import java.io.IOException;
import org.apache.lucene.util.AttributeFactory;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.Tokenizer;

public final class KeywordTokenizer extends Tokenizer
{
    public static final int DEFAULT_BUFFER_SIZE = 256;
    private boolean done;
    private int finalOffset;
    private final CharTermAttribute termAtt;
    private OffsetAttribute offsetAtt;
    
    public KeywordTokenizer() {
        this(256);
    }
    
    public KeywordTokenizer(final int bufferSize) {
        this.done = false;
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.offsetAtt = (OffsetAttribute)this.addAttribute((Class)OffsetAttribute.class);
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("bufferSize must be > 0");
        }
        this.termAtt.resizeBuffer(bufferSize);
    }
    
    public KeywordTokenizer(final AttributeFactory factory, final int bufferSize) {
        super(factory);
        this.done = false;
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.offsetAtt = (OffsetAttribute)this.addAttribute((Class)OffsetAttribute.class);
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("bufferSize must be > 0");
        }
        this.termAtt.resizeBuffer(bufferSize);
    }
    
    public final boolean incrementToken() throws IOException {
        if (!this.done) {
            this.clearAttributes();
            this.done = true;
            int upto = 0;
            char[] buffer = this.termAtt.buffer();
            while (true) {
                final int length = this.input.read(buffer, upto, buffer.length - upto);
                if (length == -1) {
                    break;
                }
                upto += length;
                if (upto != buffer.length) {
                    continue;
                }
                buffer = this.termAtt.resizeBuffer(1 + buffer.length);
            }
            this.termAtt.setLength(upto);
            this.finalOffset = this.correctOffset(upto);
            this.offsetAtt.setOffset(this.correctOffset(0), this.finalOffset);
            return true;
        }
        return false;
    }
    
    public final void end() throws IOException {
        super.end();
        this.offsetAtt.setOffset(this.finalOffset, this.finalOffset);
    }
    
    public void reset() throws IOException {
        super.reset();
        this.done = false;
    }
}
