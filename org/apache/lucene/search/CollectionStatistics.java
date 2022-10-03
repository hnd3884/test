package org.apache.lucene.search;

public class CollectionStatistics
{
    private final String field;
    private final long maxDoc;
    private final long docCount;
    private final long sumTotalTermFreq;
    private final long sumDocFreq;
    
    public CollectionStatistics(final String field, final long maxDoc, final long docCount, final long sumTotalTermFreq, final long sumDocFreq) {
        assert maxDoc >= 0L;
        assert docCount >= -1L && docCount <= maxDoc;
        assert sumDocFreq >= docCount;
        assert sumTotalTermFreq >= sumDocFreq;
        this.field = field;
        this.maxDoc = maxDoc;
        this.docCount = docCount;
        this.sumTotalTermFreq = sumTotalTermFreq;
        this.sumDocFreq = sumDocFreq;
    }
    
    public final String field() {
        return this.field;
    }
    
    public final long maxDoc() {
        return this.maxDoc;
    }
    
    public final long docCount() {
        return this.docCount;
    }
    
    public final long sumTotalTermFreq() {
        return this.sumTotalTermFreq;
    }
    
    public final long sumDocFreq() {
        return this.sumDocFreq;
    }
}
