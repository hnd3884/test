package org.apache.lucene.analysis.miscellaneous;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;

public final class RemoveDuplicatesTokenFilter extends TokenFilter
{
    private final CharTermAttribute termAttribute;
    private final PositionIncrementAttribute posIncAttribute;
    private final CharArraySet previous;
    
    public RemoveDuplicatesTokenFilter(final TokenStream in) {
        super(in);
        this.termAttribute = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.posIncAttribute = (PositionIncrementAttribute)this.addAttribute((Class)PositionIncrementAttribute.class);
        this.previous = new CharArraySet(8, false);
    }
    
    public boolean incrementToken() throws IOException {
        while (this.input.incrementToken()) {
            final char[] term = this.termAttribute.buffer();
            final int length = this.termAttribute.length();
            final int posIncrement = this.posIncAttribute.getPositionIncrement();
            if (posIncrement > 0) {
                this.previous.clear();
            }
            final boolean duplicate = posIncrement == 0 && this.previous.contains(term, 0, length);
            final char[] saved = new char[length];
            System.arraycopy(term, 0, saved, 0, length);
            this.previous.add(saved);
            if (!duplicate) {
                return true;
            }
        }
        return false;
    }
    
    public void reset() throws IOException {
        super.reset();
        this.previous.clear();
    }
}
