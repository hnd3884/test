package org.apache.lucene.analysis;

import java.io.Reader;

public abstract class AnalyzerWrapper extends Analyzer
{
    protected AnalyzerWrapper(final ReuseStrategy reuseStrategy) {
        super(reuseStrategy);
    }
    
    protected abstract Analyzer getWrappedAnalyzer(final String p0);
    
    protected TokenStreamComponents wrapComponents(final String fieldName, final TokenStreamComponents components) {
        return components;
    }
    
    protected Reader wrapReader(final String fieldName, final Reader reader) {
        return reader;
    }
    
    @Override
    protected final TokenStreamComponents createComponents(final String fieldName) {
        return this.wrapComponents(fieldName, this.getWrappedAnalyzer(fieldName).createComponents(fieldName));
    }
    
    @Override
    public int getPositionIncrementGap(final String fieldName) {
        return this.getWrappedAnalyzer(fieldName).getPositionIncrementGap(fieldName);
    }
    
    @Override
    public int getOffsetGap(final String fieldName) {
        return this.getWrappedAnalyzer(fieldName).getOffsetGap(fieldName);
    }
    
    public final Reader initReader(final String fieldName, final Reader reader) {
        return this.getWrappedAnalyzer(fieldName).initReader(fieldName, this.wrapReader(fieldName, reader));
    }
}
