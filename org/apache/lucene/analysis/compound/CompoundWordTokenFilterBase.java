package org.apache.lucene.analysis.compound;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import java.util.LinkedList;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.TokenFilter;

public abstract class CompoundWordTokenFilterBase extends TokenFilter
{
    public static final int DEFAULT_MIN_WORD_SIZE = 5;
    public static final int DEFAULT_MIN_SUBWORD_SIZE = 2;
    public static final int DEFAULT_MAX_SUBWORD_SIZE = 15;
    protected final CharArraySet dictionary;
    protected final LinkedList<CompoundToken> tokens;
    protected final int minWordSize;
    protected final int minSubwordSize;
    protected final int maxSubwordSize;
    protected final boolean onlyLongestMatch;
    protected final CharTermAttribute termAtt;
    protected final OffsetAttribute offsetAtt;
    private final PositionIncrementAttribute posIncAtt;
    private AttributeSource.State current;
    
    protected CompoundWordTokenFilterBase(final TokenStream input, final CharArraySet dictionary, final boolean onlyLongestMatch) {
        this(input, dictionary, 5, 2, 15, onlyLongestMatch);
    }
    
    protected CompoundWordTokenFilterBase(final TokenStream input, final CharArraySet dictionary) {
        this(input, dictionary, 5, 2, 15, false);
    }
    
    protected CompoundWordTokenFilterBase(final TokenStream input, final CharArraySet dictionary, final int minWordSize, final int minSubwordSize, final int maxSubwordSize, final boolean onlyLongestMatch) {
        super(input);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.offsetAtt = (OffsetAttribute)this.addAttribute((Class)OffsetAttribute.class);
        this.posIncAtt = (PositionIncrementAttribute)this.addAttribute((Class)PositionIncrementAttribute.class);
        this.tokens = new LinkedList<CompoundToken>();
        if (minWordSize < 0) {
            throw new IllegalArgumentException("minWordSize cannot be negative");
        }
        this.minWordSize = minWordSize;
        if (minSubwordSize < 0) {
            throw new IllegalArgumentException("minSubwordSize cannot be negative");
        }
        this.minSubwordSize = minSubwordSize;
        if (maxSubwordSize < 0) {
            throw new IllegalArgumentException("maxSubwordSize cannot be negative");
        }
        this.maxSubwordSize = maxSubwordSize;
        this.onlyLongestMatch = onlyLongestMatch;
        this.dictionary = dictionary;
    }
    
    public final boolean incrementToken() throws IOException {
        if (!this.tokens.isEmpty()) {
            assert this.current != null;
            final CompoundToken token = this.tokens.removeFirst();
            this.restoreState(this.current);
            this.termAtt.setEmpty().append(token.txt);
            this.offsetAtt.setOffset(token.startOffset, token.endOffset);
            this.posIncAtt.setPositionIncrement(0);
            return true;
        }
        else {
            this.current = null;
            if (this.input.incrementToken()) {
                if (this.termAtt.length() >= this.minWordSize) {
                    this.decompose();
                    if (!this.tokens.isEmpty()) {
                        this.current = this.captureState();
                    }
                }
                return true;
            }
            return false;
        }
    }
    
    protected abstract void decompose();
    
    public void reset() throws IOException {
        super.reset();
        this.tokens.clear();
        this.current = null;
    }
    
    protected class CompoundToken
    {
        public final CharSequence txt;
        public final int startOffset;
        public final int endOffset;
        
        public CompoundToken(final int offset, final int length) {
            this.txt = CompoundWordTokenFilterBase.this.termAtt.subSequence(offset, offset + length);
            this.startOffset = CompoundWordTokenFilterBase.this.offsetAtt.startOffset();
            this.endOffset = CompoundWordTokenFilterBase.this.offsetAtt.endOffset();
        }
    }
}
