package org.apache.lucene.analysis.ngram;

import java.io.IOException;
import org.apache.lucene.util.AttributeFactory;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharacterUtils;
import org.apache.lucene.analysis.Tokenizer;

public class NGramTokenizer extends Tokenizer
{
    public static final int DEFAULT_MIN_NGRAM_SIZE = 1;
    public static final int DEFAULT_MAX_NGRAM_SIZE = 2;
    private CharacterUtils charUtils;
    private CharacterUtils.CharacterBuffer charBuffer;
    private int[] buffer;
    private int bufferStart;
    private int bufferEnd;
    private int offset;
    private int gramSize;
    private int minGram;
    private int maxGram;
    private boolean exhausted;
    private int lastCheckedChar;
    private int lastNonTokenChar;
    private boolean edgesOnly;
    private final CharTermAttribute termAtt;
    private final PositionIncrementAttribute posIncAtt;
    private final PositionLengthAttribute posLenAtt;
    private final OffsetAttribute offsetAtt;
    
    NGramTokenizer(final int minGram, final int maxGram, final boolean edgesOnly) {
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.posIncAtt = (PositionIncrementAttribute)this.addAttribute((Class)PositionIncrementAttribute.class);
        this.posLenAtt = (PositionLengthAttribute)this.addAttribute((Class)PositionLengthAttribute.class);
        this.offsetAtt = (OffsetAttribute)this.addAttribute((Class)OffsetAttribute.class);
        this.init(minGram, maxGram, edgesOnly);
    }
    
    public NGramTokenizer(final int minGram, final int maxGram) {
        this(minGram, maxGram, false);
    }
    
    NGramTokenizer(final AttributeFactory factory, final int minGram, final int maxGram, final boolean edgesOnly) {
        super(factory);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.posIncAtt = (PositionIncrementAttribute)this.addAttribute((Class)PositionIncrementAttribute.class);
        this.posLenAtt = (PositionLengthAttribute)this.addAttribute((Class)PositionLengthAttribute.class);
        this.offsetAtt = (OffsetAttribute)this.addAttribute((Class)OffsetAttribute.class);
        this.init(minGram, maxGram, edgesOnly);
    }
    
    public NGramTokenizer(final AttributeFactory factory, final int minGram, final int maxGram) {
        this(factory, minGram, maxGram, false);
    }
    
    public NGramTokenizer() {
        this(1, 2);
    }
    
    private void init(final int minGram, final int maxGram, final boolean edgesOnly) {
        this.charUtils = CharacterUtils.getInstance();
        if (minGram < 1) {
            throw new IllegalArgumentException("minGram must be greater than zero");
        }
        if (minGram > maxGram) {
            throw new IllegalArgumentException("minGram must not be greater than maxGram");
        }
        this.minGram = minGram;
        this.maxGram = maxGram;
        this.edgesOnly = edgesOnly;
        this.charBuffer = CharacterUtils.newCharacterBuffer(2 * maxGram + 1024);
        this.buffer = new int[this.charBuffer.getBuffer().length];
        this.termAtt.resizeBuffer(2 * maxGram);
    }
    
    public final boolean incrementToken() throws IOException {
        this.clearAttributes();
        while (true) {
            if (this.bufferStart >= this.bufferEnd - this.maxGram - 1 && !this.exhausted) {
                System.arraycopy(this.buffer, this.bufferStart, this.buffer, 0, this.bufferEnd - this.bufferStart);
                this.bufferEnd -= this.bufferStart;
                this.lastCheckedChar -= this.bufferStart;
                this.lastNonTokenChar -= this.bufferStart;
                this.bufferStart = 0;
                this.exhausted = !this.charUtils.fill(this.charBuffer, this.input, this.buffer.length - this.bufferEnd);
                this.bufferEnd += this.charUtils.toCodePoints(this.charBuffer.getBuffer(), 0, this.charBuffer.getLength(), this.buffer, this.bufferEnd);
            }
            if (this.gramSize > this.maxGram || this.bufferStart + this.gramSize > this.bufferEnd) {
                if (this.bufferStart + 1 + this.minGram > this.bufferEnd) {
                    assert this.exhausted;
                    return false;
                }
                else {
                    this.consume();
                    this.gramSize = this.minGram;
                }
            }
            this.updateLastNonTokenChar();
            final boolean termContainsNonTokenChar = this.lastNonTokenChar >= this.bufferStart && this.lastNonTokenChar < this.bufferStart + this.gramSize;
            final boolean isEdgeAndPreviousCharIsTokenChar = this.edgesOnly && this.lastNonTokenChar != this.bufferStart - 1;
            if (!termContainsNonTokenChar && !isEdgeAndPreviousCharIsTokenChar) {
                final int length = this.charUtils.toChars(this.buffer, this.bufferStart, this.gramSize, this.termAtt.buffer(), 0);
                this.termAtt.setLength(length);
                this.posIncAtt.setPositionIncrement(1);
                this.posLenAtt.setPositionLength(1);
                this.offsetAtt.setOffset(this.correctOffset(this.offset), this.correctOffset(this.offset + length));
                ++this.gramSize;
                return true;
            }
            this.consume();
            this.gramSize = this.minGram;
        }
    }
    
    private void updateLastNonTokenChar() {
        final int termEnd = this.bufferStart + this.gramSize - 1;
        if (termEnd > this.lastCheckedChar) {
            for (int i = termEnd; i > this.lastCheckedChar; --i) {
                if (!this.isTokenChar(this.buffer[i])) {
                    this.lastNonTokenChar = i;
                    break;
                }
            }
            this.lastCheckedChar = termEnd;
        }
    }
    
    private void consume() {
        this.offset += Character.charCount(this.buffer[this.bufferStart++]);
    }
    
    protected boolean isTokenChar(final int chr) {
        return true;
    }
    
    public final void end() throws IOException {
        super.end();
        assert this.bufferStart <= this.bufferEnd;
        int endOffset = this.offset;
        for (int i = this.bufferStart; i < this.bufferEnd; ++i) {
            endOffset += Character.charCount(this.buffer[i]);
        }
        endOffset = this.correctOffset(endOffset);
        this.offsetAtt.setOffset(endOffset, endOffset);
    }
    
    public final void reset() throws IOException {
        super.reset();
        final int length = this.buffer.length;
        this.bufferEnd = length;
        this.bufferStart = length;
        final int n = this.bufferStart - 1;
        this.lastCheckedChar = n;
        this.lastNonTokenChar = n;
        this.offset = 0;
        this.gramSize = this.minGram;
        this.exhausted = false;
        this.charBuffer.reset();
    }
}
