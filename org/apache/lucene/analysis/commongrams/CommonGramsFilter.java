package org.apache.lucene.analysis.commongrams;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.TokenFilter;

public final class CommonGramsFilter extends TokenFilter
{
    public static final String GRAM_TYPE = "gram";
    private static final char SEPARATOR = '_';
    private final CharArraySet commonWords;
    private final StringBuilder buffer;
    private final CharTermAttribute termAttribute;
    private final OffsetAttribute offsetAttribute;
    private final TypeAttribute typeAttribute;
    private final PositionIncrementAttribute posIncAttribute;
    private final PositionLengthAttribute posLenAttribute;
    private int lastStartOffset;
    private boolean lastWasCommon;
    private AttributeSource.State savedState;
    
    public CommonGramsFilter(final TokenStream input, final CharArraySet commonWords) {
        super(input);
        this.buffer = new StringBuilder();
        this.termAttribute = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.offsetAttribute = (OffsetAttribute)this.addAttribute((Class)OffsetAttribute.class);
        this.typeAttribute = (TypeAttribute)this.addAttribute((Class)TypeAttribute.class);
        this.posIncAttribute = (PositionIncrementAttribute)this.addAttribute((Class)PositionIncrementAttribute.class);
        this.posLenAttribute = (PositionLengthAttribute)this.addAttribute((Class)PositionLengthAttribute.class);
        this.commonWords = commonWords;
    }
    
    public boolean incrementToken() throws IOException {
        if (this.savedState != null) {
            this.restoreState(this.savedState);
            this.savedState = null;
            this.saveTermBuffer();
            return true;
        }
        if (!this.input.incrementToken()) {
            return false;
        }
        if (this.lastWasCommon || (this.isCommon() && this.buffer.length() > 0)) {
            this.savedState = this.captureState();
            this.gramToken();
            return true;
        }
        this.saveTermBuffer();
        return true;
    }
    
    public void reset() throws IOException {
        super.reset();
        this.lastWasCommon = false;
        this.savedState = null;
        this.buffer.setLength(0);
    }
    
    private boolean isCommon() {
        return this.commonWords != null && this.commonWords.contains(this.termAttribute.buffer(), 0, this.termAttribute.length());
    }
    
    private void saveTermBuffer() {
        this.buffer.setLength(0);
        this.buffer.append(this.termAttribute.buffer(), 0, this.termAttribute.length());
        this.buffer.append('_');
        this.lastStartOffset = this.offsetAttribute.startOffset();
        this.lastWasCommon = this.isCommon();
    }
    
    private void gramToken() {
        this.buffer.append(this.termAttribute.buffer(), 0, this.termAttribute.length());
        final int endOffset = this.offsetAttribute.endOffset();
        this.clearAttributes();
        final int length = this.buffer.length();
        char[] termText = this.termAttribute.buffer();
        if (length > termText.length) {
            termText = this.termAttribute.resizeBuffer(length);
        }
        this.buffer.getChars(0, length, termText, 0);
        this.termAttribute.setLength(length);
        this.posIncAttribute.setPositionIncrement(0);
        this.posLenAttribute.setPositionLength(2);
        this.offsetAttribute.setOffset(this.lastStartOffset, endOffset);
        this.typeAttribute.setType("gram");
        this.buffer.setLength(0);
    }
}
