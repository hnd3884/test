package org.apache.lucene.analysis.ngram;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharacterUtils;
import org.apache.lucene.analysis.TokenFilter;

@Deprecated
public final class Lucene43EdgeNGramTokenFilter extends TokenFilter
{
    public static final int DEFAULT_MAX_GRAM_SIZE = 1;
    public static final int DEFAULT_MIN_GRAM_SIZE = 1;
    private final CharacterUtils charUtils;
    private final int minGram;
    private final int maxGram;
    private char[] curTermBuffer;
    private int curTermLength;
    private int curCodePointCount;
    private int curGramSize;
    private int tokStart;
    private int tokEnd;
    private int savePosIncr;
    private int savePosLen;
    private final CharTermAttribute termAtt;
    private final OffsetAttribute offsetAtt;
    private final PositionIncrementAttribute posIncrAtt;
    private final PositionLengthAttribute posLenAtt;
    
    public Lucene43EdgeNGramTokenFilter(final TokenStream input, final int minGram, final int maxGram) {
        super(input);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.offsetAtt = (OffsetAttribute)this.addAttribute((Class)OffsetAttribute.class);
        this.posIncrAtt = (PositionIncrementAttribute)this.addAttribute((Class)PositionIncrementAttribute.class);
        this.posLenAtt = (PositionLengthAttribute)this.addAttribute((Class)PositionLengthAttribute.class);
        if (minGram < 1) {
            throw new IllegalArgumentException("minGram must be greater than zero");
        }
        if (minGram > maxGram) {
            throw new IllegalArgumentException("minGram must not be greater than maxGram");
        }
        this.charUtils = CharacterUtils.getJava4Instance();
        this.minGram = minGram;
        this.maxGram = maxGram;
    }
    
    public final boolean incrementToken() throws IOException {
        while (true) {
            if (this.curTermBuffer == null) {
                if (!this.input.incrementToken()) {
                    return false;
                }
                this.curTermBuffer = this.termAtt.buffer().clone();
                this.curTermLength = this.termAtt.length();
                this.curCodePointCount = this.charUtils.codePointCount((CharSequence)this.termAtt);
                this.curGramSize = this.minGram;
                this.tokStart = this.offsetAtt.startOffset();
                this.tokEnd = this.offsetAtt.endOffset();
                this.savePosIncr += this.posIncrAtt.getPositionIncrement();
                this.savePosLen = this.posLenAtt.getPositionLength();
            }
            if (this.curGramSize <= this.maxGram && this.curGramSize <= this.curCodePointCount) {
                this.clearAttributes();
                this.offsetAtt.setOffset(this.tokStart, this.tokEnd);
                if (this.curGramSize == this.minGram) {
                    this.posIncrAtt.setPositionIncrement(this.savePosIncr);
                    this.savePosIncr = 0;
                }
                else {
                    this.posIncrAtt.setPositionIncrement(0);
                }
                this.posLenAtt.setPositionLength(this.savePosLen);
                final int charLength = this.charUtils.offsetByCodePoints(this.curTermBuffer, 0, this.curTermLength, 0, this.curGramSize);
                this.termAtt.copyBuffer(this.curTermBuffer, 0, charLength);
                ++this.curGramSize;
                return true;
            }
            this.curTermBuffer = null;
        }
    }
    
    public void reset() throws IOException {
        super.reset();
        this.curTermBuffer = null;
        this.savePosIncr = 0;
    }
}
