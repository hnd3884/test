package org.apache.lucene.analysis.miscellaneous;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;

public final class HyphenatedWordsFilter extends TokenFilter
{
    private final CharTermAttribute termAttribute;
    private final OffsetAttribute offsetAttribute;
    private final StringBuilder hyphenated;
    private AttributeSource.State savedState;
    private boolean exhausted;
    private int lastEndOffset;
    
    public HyphenatedWordsFilter(final TokenStream in) {
        super(in);
        this.termAttribute = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.offsetAttribute = (OffsetAttribute)this.addAttribute((Class)OffsetAttribute.class);
        this.hyphenated = new StringBuilder();
        this.exhausted = false;
        this.lastEndOffset = 0;
    }
    
    public boolean incrementToken() throws IOException {
        while (!this.exhausted && this.input.incrementToken()) {
            final char[] term = this.termAttribute.buffer();
            final int termLength = this.termAttribute.length();
            this.lastEndOffset = this.offsetAttribute.endOffset();
            if (termLength > 0 && term[termLength - 1] == '-') {
                if (this.savedState == null) {
                    this.savedState = this.captureState();
                }
                this.hyphenated.append(term, 0, termLength - 1);
            }
            else {
                if (this.savedState == null) {
                    return true;
                }
                this.hyphenated.append(term, 0, termLength);
                this.unhyphenate();
                return true;
            }
        }
        this.exhausted = true;
        if (this.savedState != null) {
            this.hyphenated.append('-');
            this.unhyphenate();
            return true;
        }
        return false;
    }
    
    public void reset() throws IOException {
        super.reset();
        this.hyphenated.setLength(0);
        this.savedState = null;
        this.exhausted = false;
        this.lastEndOffset = 0;
    }
    
    private void unhyphenate() {
        this.restoreState(this.savedState);
        this.savedState = null;
        char[] term = this.termAttribute.buffer();
        final int length = this.hyphenated.length();
        if (length > this.termAttribute.length()) {
            term = this.termAttribute.resizeBuffer(length);
        }
        this.hyphenated.getChars(0, length, term, 0);
        this.termAttribute.setLength(length);
        this.offsetAttribute.setOffset(this.offsetAttribute.startOffset(), this.lastEndOffset);
        this.hyphenated.setLength(0);
    }
}
