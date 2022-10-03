package org.apache.lucene.analysis.miscellaneous;

import java.util.Collections;
import java.util.Map;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.DelegatingAnalyzerWrapper;

public final class PerFieldAnalyzerWrapper extends DelegatingAnalyzerWrapper
{
    private final Analyzer defaultAnalyzer;
    private final Map<String, Analyzer> fieldAnalyzers;
    
    public PerFieldAnalyzerWrapper(final Analyzer defaultAnalyzer) {
        this(defaultAnalyzer, null);
    }
    
    public PerFieldAnalyzerWrapper(final Analyzer defaultAnalyzer, final Map<String, Analyzer> fieldAnalyzers) {
        super(PerFieldAnalyzerWrapper.PER_FIELD_REUSE_STRATEGY);
        this.defaultAnalyzer = defaultAnalyzer;
        this.fieldAnalyzers = ((fieldAnalyzers != null) ? fieldAnalyzers : Collections.emptyMap());
    }
    
    protected Analyzer getWrappedAnalyzer(final String fieldName) {
        final Analyzer analyzer = this.fieldAnalyzers.get(fieldName);
        return (analyzer != null) ? analyzer : this.defaultAnalyzer;
    }
    
    public String toString() {
        return "PerFieldAnalyzerWrapper(" + this.fieldAnalyzers + ", default=" + this.defaultAnalyzer + ")";
    }
}
