package org.apache.lucene.analysis.ngram;

import java.io.IOException;
import org.apache.lucene.analysis.miscellaneous.CodepointCountFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharacterUtils;
import org.apache.lucene.analysis.TokenFilter;

@Deprecated
public final class Lucene43NGramTokenFilter extends TokenFilter
{
    public static final int DEFAULT_MIN_NGRAM_SIZE = 1;
    public static final int DEFAULT_MAX_NGRAM_SIZE = 2;
    private final int minGram;
    private final int maxGram;
    private char[] curTermBuffer;
    private int curTermLength;
    private int curCodePointCount;
    private int curGramSize;
    private int curPos;
    private int curPosInc;
    private int curPosLen;
    private int tokStart;
    private int tokEnd;
    private boolean hasIllegalOffsets;
    private final CharacterUtils charUtils;
    private final CharTermAttribute termAtt;
    private final PositionIncrementAttribute posIncAtt;
    private final PositionLengthAttribute posLenAtt;
    private final OffsetAttribute offsetAtt;
    
    public Lucene43NGramTokenFilter(final TokenStream input, final int minGram, final int maxGram) {
        super((TokenStream)new CodepointCountFilter(input, minGram, Integer.MAX_VALUE));
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.offsetAtt = (OffsetAttribute)this.addAttribute((Class)OffsetAttribute.class);
        this.charUtils = CharacterUtils.getJava4Instance();
        if (minGram < 1) {
            throw new IllegalArgumentException("minGram must be greater than zero");
        }
        if (minGram > maxGram) {
            throw new IllegalArgumentException("minGram must not be greater than maxGram");
        }
        this.minGram = minGram;
        this.maxGram = maxGram;
        this.posIncAtt = (PositionIncrementAttribute)new PositionIncrementAttribute() {
            public void setPositionIncrement(final int positionIncrement) {
            }
            
            public int getPositionIncrement() {
                return 0;
            }
        };
        this.posLenAtt = (PositionLengthAttribute)new PositionLengthAttribute() {
            public void setPositionLength(final int positionLength) {
            }
            
            public int getPositionLength() {
                return 0;
            }
        };
    }
    
    public Lucene43NGramTokenFilter(final TokenStream input) {
        this(input, 1, 2);
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
                this.curPos = 0;
                this.curPosInc = this.posIncAtt.getPositionIncrement();
                this.curPosLen = this.posLenAtt.getPositionLength();
                this.tokStart = this.offsetAtt.startOffset();
                this.tokEnd = this.offsetAtt.endOffset();
                this.hasIllegalOffsets = (this.tokStart + this.curTermLength != this.tokEnd);
            }
            while (this.curGramSize <= this.maxGram) {
                if (this.curPos + this.curGramSize <= this.curTermLength) {
                    this.clearAttributes();
                    this.termAtt.copyBuffer(this.curTermBuffer, this.curPos, this.curGramSize);
                    if (this.hasIllegalOffsets) {
                        this.offsetAtt.setOffset(this.tokStart, this.tokEnd);
                    }
                    else {
                        this.offsetAtt.setOffset(this.tokStart + this.curPos, this.tokStart + this.curPos + this.curGramSize);
                    }
                    ++this.curPos;
                    return true;
                }
                ++this.curGramSize;
                this.curPos = 0;
            }
            this.curTermBuffer = null;
        }
    }
    
    public void reset() throws IOException {
        super.reset();
        this.curTermBuffer = null;
    }
}
