package org.apache.lucene.search;

import org.apache.lucene.util.BytesRef;

public class TermStatistics
{
    private final BytesRef term;
    private final long docFreq;
    private final long totalTermFreq;
    
    public TermStatistics(final BytesRef term, final long docFreq, final long totalTermFreq) {
        assert docFreq >= 0L;
        assert totalTermFreq >= docFreq;
        this.term = term;
        this.docFreq = docFreq;
        this.totalTermFreq = totalTermFreq;
    }
    
    public final BytesRef term() {
        return this.term;
    }
    
    public final long docFreq() {
        return this.docFreq;
    }
    
    public final long totalTermFreq() {
        return this.totalTermFreq;
    }
}
