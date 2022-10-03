package org.apache.lucene.analysis.miscellaneous;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.AnalyzerWrapper;

public final class LimitTokenCountAnalyzer extends AnalyzerWrapper
{
    private final Analyzer delegate;
    private final int maxTokenCount;
    private final boolean consumeAllTokens;
    
    public LimitTokenCountAnalyzer(final Analyzer delegate, final int maxTokenCount) {
        this(delegate, maxTokenCount, false);
    }
    
    public LimitTokenCountAnalyzer(final Analyzer delegate, final int maxTokenCount, final boolean consumeAllTokens) {
        super(delegate.getReuseStrategy());
        this.delegate = delegate;
        this.maxTokenCount = maxTokenCount;
        this.consumeAllTokens = consumeAllTokens;
    }
    
    protected Analyzer getWrappedAnalyzer(final String fieldName) {
        return this.delegate;
    }
    
    protected Analyzer.TokenStreamComponents wrapComponents(final String fieldName, final Analyzer.TokenStreamComponents components) {
        return new Analyzer.TokenStreamComponents(components.getTokenizer(), (TokenStream)new LimitTokenCountFilter(components.getTokenStream(), this.maxTokenCount, this.consumeAllTokens));
    }
    
    public String toString() {
        return "LimitTokenCountAnalyzer(" + this.delegate.toString() + ", maxTokenCount=" + this.maxTokenCount + ", consumeAllTokens=" + this.consumeAllTokens + ")";
    }
}
