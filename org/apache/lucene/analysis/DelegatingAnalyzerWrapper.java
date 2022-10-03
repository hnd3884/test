package org.apache.lucene.analysis;

import java.io.Reader;

public abstract class DelegatingAnalyzerWrapper extends AnalyzerWrapper
{
    protected DelegatingAnalyzerWrapper(final ReuseStrategy fallbackStrategy) {
        super(new DelegatingReuseStrategy(fallbackStrategy));
        ((DelegatingReuseStrategy)this.getReuseStrategy()).wrapper = this;
    }
    
    @Override
    protected final TokenStreamComponents wrapComponents(final String fieldName, final TokenStreamComponents components) {
        return super.wrapComponents(fieldName, components);
    }
    
    @Override
    protected final Reader wrapReader(final String fieldName, final Reader reader) {
        return super.wrapReader(fieldName, reader);
    }
    
    private static final class DelegatingReuseStrategy extends ReuseStrategy
    {
        DelegatingAnalyzerWrapper wrapper;
        private final ReuseStrategy fallbackStrategy;
        
        DelegatingReuseStrategy(final ReuseStrategy fallbackStrategy) {
            this.fallbackStrategy = fallbackStrategy;
        }
        
        @Override
        public TokenStreamComponents getReusableComponents(final Analyzer analyzer, final String fieldName) {
            if (analyzer == this.wrapper) {
                final Analyzer wrappedAnalyzer = this.wrapper.getWrappedAnalyzer(fieldName);
                return wrappedAnalyzer.getReuseStrategy().getReusableComponents(wrappedAnalyzer, fieldName);
            }
            return this.fallbackStrategy.getReusableComponents(analyzer, fieldName);
        }
        
        @Override
        public void setReusableComponents(final Analyzer analyzer, final String fieldName, final TokenStreamComponents components) {
            if (analyzer == this.wrapper) {
                final Analyzer wrappedAnalyzer = this.wrapper.getWrappedAnalyzer(fieldName);
                wrappedAnalyzer.getReuseStrategy().setReusableComponents(wrappedAnalyzer, fieldName, components);
            }
            else {
                this.fallbackStrategy.setReusableComponents(analyzer, fieldName, components);
            }
        }
    }
}
