package org.apache.lucene.search.suggest.analyzing;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;

public final class SuggestStopFilter extends TokenFilter
{
    private final CharTermAttribute termAtt;
    private final PositionIncrementAttribute posIncAtt;
    private final KeywordAttribute keywordAtt;
    private final OffsetAttribute offsetAtt;
    private final CharArraySet stopWords;
    private AttributeSource.State endState;
    
    public SuggestStopFilter(final TokenStream input, final CharArraySet stopWords) {
        super(input);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.posIncAtt = (PositionIncrementAttribute)this.addAttribute((Class)PositionIncrementAttribute.class);
        this.keywordAtt = (KeywordAttribute)this.addAttribute((Class)KeywordAttribute.class);
        this.offsetAtt = (OffsetAttribute)this.addAttribute((Class)OffsetAttribute.class);
        this.stopWords = stopWords;
    }
    
    public void reset() throws IOException {
        super.reset();
        this.endState = null;
    }
    
    public void end() throws IOException {
        if (this.endState == null) {
            super.end();
        }
        else {
            this.restoreState(this.endState);
        }
    }
    
    public boolean incrementToken() throws IOException {
        if (this.endState != null) {
            return false;
        }
        if (!this.input.incrementToken()) {
            return false;
        }
        int skippedPositions = 0;
        while (this.stopWords.contains(this.termAtt.buffer(), 0, this.termAtt.length())) {
            final int posInc = this.posIncAtt.getPositionIncrement();
            final int endOffset = this.offsetAtt.endOffset();
            final AttributeSource.State sav = this.captureState();
            if (this.input.incrementToken()) {
                skippedPositions += posInc;
            }
            else {
                this.clearAttributes();
                this.input.end();
                this.endState = this.captureState();
                final int finalEndOffset = this.offsetAtt.endOffset();
                assert finalEndOffset >= endOffset;
                if (finalEndOffset > endOffset) {
                    return false;
                }
                this.restoreState(sav);
                this.posIncAtt.setPositionIncrement(skippedPositions + this.posIncAtt.getPositionIncrement());
                this.keywordAtt.setKeyword(true);
                return true;
            }
        }
        this.posIncAtt.setPositionIncrement(skippedPositions + this.posIncAtt.getPositionIncrement());
        return true;
    }
}
