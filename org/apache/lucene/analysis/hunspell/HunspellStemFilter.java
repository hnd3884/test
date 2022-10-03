package org.apache.lucene.analysis.hunspell;

import java.io.IOException;
import java.util.Collections;
import org.apache.lucene.analysis.TokenStream;
import java.util.Comparator;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.CharsRef;
import java.util.List;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;

public final class HunspellStemFilter extends TokenFilter
{
    private final CharTermAttribute termAtt;
    private final PositionIncrementAttribute posIncAtt;
    private final KeywordAttribute keywordAtt;
    private final Stemmer stemmer;
    private List<CharsRef> buffer;
    private AttributeSource.State savedState;
    private final boolean dedup;
    private final boolean longestOnly;
    static final Comparator<CharsRef> lengthComparator;
    
    public HunspellStemFilter(final TokenStream input, final Dictionary dictionary) {
        this(input, dictionary, true);
    }
    
    public HunspellStemFilter(final TokenStream input, final Dictionary dictionary, final boolean dedup) {
        this(input, dictionary, dedup, false);
    }
    
    public HunspellStemFilter(final TokenStream input, final Dictionary dictionary, final boolean dedup, final boolean longestOnly) {
        super(input);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.posIncAtt = (PositionIncrementAttribute)this.addAttribute((Class)PositionIncrementAttribute.class);
        this.keywordAtt = (KeywordAttribute)this.addAttribute((Class)KeywordAttribute.class);
        this.dedup = (dedup && !longestOnly);
        this.stemmer = new Stemmer(dictionary);
        this.longestOnly = longestOnly;
    }
    
    public boolean incrementToken() throws IOException {
        if (this.buffer != null && !this.buffer.isEmpty()) {
            final CharsRef nextStem = this.buffer.remove(0);
            this.restoreState(this.savedState);
            this.posIncAtt.setPositionIncrement(0);
            this.termAtt.setEmpty().append((CharSequence)nextStem);
            return true;
        }
        if (!this.input.incrementToken()) {
            return false;
        }
        if (this.keywordAtt.isKeyword()) {
            return true;
        }
        this.buffer = (this.dedup ? this.stemmer.uniqueStems(this.termAtt.buffer(), this.termAtt.length()) : this.stemmer.stem(this.termAtt.buffer(), this.termAtt.length()));
        if (this.buffer.isEmpty()) {
            return true;
        }
        if (this.longestOnly && this.buffer.size() > 1) {
            Collections.sort(this.buffer, HunspellStemFilter.lengthComparator);
        }
        final CharsRef stem = this.buffer.remove(0);
        this.termAtt.setEmpty().append((CharSequence)stem);
        if (this.longestOnly) {
            this.buffer.clear();
        }
        else if (!this.buffer.isEmpty()) {
            this.savedState = this.captureState();
        }
        return true;
    }
    
    public void reset() throws IOException {
        super.reset();
        this.buffer = null;
    }
    
    static {
        lengthComparator = new Comparator<CharsRef>() {
            @Override
            public int compare(final CharsRef o1, final CharsRef o2) {
                final int cmp = Integer.compare(o2.length, o1.length);
                if (cmp == 0) {
                    return o2.compareTo(o1);
                }
                return cmp;
            }
        };
    }
}
