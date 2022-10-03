package org.apache.lucene.codecs;

public class TermStats
{
    public final int docFreq;
    public final long totalTermFreq;
    
    public TermStats(final int docFreq, final long totalTermFreq) {
        this.docFreq = docFreq;
        this.totalTermFreq = totalTermFreq;
    }
}
