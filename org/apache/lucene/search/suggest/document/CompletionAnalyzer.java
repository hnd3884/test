package org.apache.lucene.search.suggest.document;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.AnalyzerWrapper;

public final class CompletionAnalyzer extends AnalyzerWrapper
{
    static final int SEP_LABEL = 31;
    static final int HOLE_CHARACTER = 30;
    static final int DEFAULT_MAX_GRAPH_EXPANSIONS = 10000;
    static final boolean DEFAULT_PRESERVE_SEP = true;
    static final boolean DEFAULT_PRESERVE_POSITION_INCREMENTS = true;
    private final Analyzer analyzer;
    private final boolean preserveSep;
    private final boolean preservePositionIncrements;
    private final int maxGraphExpansions;
    
    public CompletionAnalyzer(final Analyzer analyzer, final boolean preserveSep, final boolean preservePositionIncrements, final int maxGraphExpansions) {
        super(CompletionAnalyzer.PER_FIELD_REUSE_STRATEGY);
        this.analyzer = analyzer;
        this.preserveSep = preserveSep;
        this.preservePositionIncrements = preservePositionIncrements;
        this.maxGraphExpansions = maxGraphExpansions;
    }
    
    public CompletionAnalyzer(final Analyzer analyzer) {
        this(analyzer, true, true, 10000);
    }
    
    public CompletionAnalyzer(final Analyzer analyzer, final boolean preserveSep, final boolean preservePositionIncrements) {
        this(analyzer, preserveSep, preservePositionIncrements, 10000);
    }
    
    public CompletionAnalyzer(final Analyzer analyzer, final int maxGraphExpansions) {
        this(analyzer, true, true, maxGraphExpansions);
    }
    
    public boolean preserveSep() {
        return this.preserveSep;
    }
    
    public boolean preservePositionIncrements() {
        return this.preservePositionIncrements;
    }
    
    protected Analyzer getWrappedAnalyzer(final String fieldName) {
        return this.analyzer;
    }
    
    protected Analyzer.TokenStreamComponents wrapComponents(final String fieldName, final Analyzer.TokenStreamComponents components) {
        final CompletionTokenStream tokenStream = new CompletionTokenStream(components.getTokenStream(), this.preserveSep, this.preservePositionIncrements, this.maxGraphExpansions);
        return new Analyzer.TokenStreamComponents(components.getTokenizer(), (TokenStream)tokenStream);
    }
}
