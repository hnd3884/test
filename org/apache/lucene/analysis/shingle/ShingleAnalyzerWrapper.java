package org.apache.lucene.analysis.shingle;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.AnalyzerWrapper;

public final class ShingleAnalyzerWrapper extends AnalyzerWrapper
{
    private final Analyzer delegate;
    private final int maxShingleSize;
    private final int minShingleSize;
    private final String tokenSeparator;
    private final boolean outputUnigrams;
    private final boolean outputUnigramsIfNoShingles;
    private final String fillerToken;
    
    public ShingleAnalyzerWrapper(final Analyzer defaultAnalyzer) {
        this(defaultAnalyzer, 2);
    }
    
    public ShingleAnalyzerWrapper(final Analyzer defaultAnalyzer, final int maxShingleSize) {
        this(defaultAnalyzer, 2, maxShingleSize);
    }
    
    public ShingleAnalyzerWrapper(final Analyzer defaultAnalyzer, final int minShingleSize, final int maxShingleSize) {
        this(defaultAnalyzer, minShingleSize, maxShingleSize, " ", true, false, "_");
    }
    
    public ShingleAnalyzerWrapper(final Analyzer delegate, final int minShingleSize, final int maxShingleSize, final String tokenSeparator, final boolean outputUnigrams, final boolean outputUnigramsIfNoShingles, final String fillerToken) {
        super(delegate.getReuseStrategy());
        this.delegate = delegate;
        if (maxShingleSize < 2) {
            throw new IllegalArgumentException("Max shingle size must be >= 2");
        }
        this.maxShingleSize = maxShingleSize;
        if (minShingleSize < 2) {
            throw new IllegalArgumentException("Min shingle size must be >= 2");
        }
        if (minShingleSize > maxShingleSize) {
            throw new IllegalArgumentException("Min shingle size must be <= max shingle size");
        }
        this.minShingleSize = minShingleSize;
        this.tokenSeparator = ((tokenSeparator == null) ? "" : tokenSeparator);
        this.outputUnigrams = outputUnigrams;
        this.outputUnigramsIfNoShingles = outputUnigramsIfNoShingles;
        this.fillerToken = fillerToken;
    }
    
    public ShingleAnalyzerWrapper() {
        this(2, 2);
    }
    
    public ShingleAnalyzerWrapper(final int minShingleSize, final int maxShingleSize) {
        this(new StandardAnalyzer(), minShingleSize, maxShingleSize);
    }
    
    public int getMaxShingleSize() {
        return this.maxShingleSize;
    }
    
    public int getMinShingleSize() {
        return this.minShingleSize;
    }
    
    public String getTokenSeparator() {
        return this.tokenSeparator;
    }
    
    public boolean isOutputUnigrams() {
        return this.outputUnigrams;
    }
    
    public boolean isOutputUnigramsIfNoShingles() {
        return this.outputUnigramsIfNoShingles;
    }
    
    public String getFillerToken() {
        return this.fillerToken;
    }
    
    public final Analyzer getWrappedAnalyzer(final String fieldName) {
        return this.delegate;
    }
    
    protected Analyzer.TokenStreamComponents wrapComponents(final String fieldName, final Analyzer.TokenStreamComponents components) {
        final ShingleFilter filter = new ShingleFilter(components.getTokenStream(), this.minShingleSize, this.maxShingleSize);
        filter.setMinShingleSize(this.minShingleSize);
        filter.setMaxShingleSize(this.maxShingleSize);
        filter.setTokenSeparator(this.tokenSeparator);
        filter.setOutputUnigrams(this.outputUnigrams);
        filter.setOutputUnigramsIfNoShingles(this.outputUnigramsIfNoShingles);
        filter.setFillerToken(this.fillerToken);
        return new Analyzer.TokenStreamComponents(components.getTokenizer(), (TokenStream)filter);
    }
}
