package org.apache.lucene.analysis.core;

import java.util.Collection;
import java.util.Arrays;
import java.util.List;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.FilteringTokenFilter;

public final class StopFilter extends FilteringTokenFilter
{
    private final CharArraySet stopWords;
    private final CharTermAttribute termAtt;
    
    public StopFilter(final TokenStream in, final CharArraySet stopWords) {
        super(in);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.stopWords = stopWords;
    }
    
    public static CharArraySet makeStopSet(final String... stopWords) {
        return makeStopSet(stopWords, false);
    }
    
    public static CharArraySet makeStopSet(final List<?> stopWords) {
        return makeStopSet(stopWords, false);
    }
    
    public static CharArraySet makeStopSet(final String[] stopWords, final boolean ignoreCase) {
        final CharArraySet stopSet = new CharArraySet(stopWords.length, ignoreCase);
        stopSet.addAll(Arrays.asList(stopWords));
        return stopSet;
    }
    
    public static CharArraySet makeStopSet(final List<?> stopWords, final boolean ignoreCase) {
        final CharArraySet stopSet = new CharArraySet(stopWords.size(), ignoreCase);
        stopSet.addAll(stopWords);
        return stopSet;
    }
    
    @Override
    protected boolean accept() {
        return !this.stopWords.contains(this.termAtt.buffer(), 0, this.termAtt.length());
    }
}
