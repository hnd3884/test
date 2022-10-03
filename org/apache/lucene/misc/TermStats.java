package org.apache.lucene.misc;

import org.apache.lucene.util.BytesRef;

public final class TermStats
{
    public BytesRef termtext;
    public String field;
    public int docFreq;
    public long totalTermFreq;
    
    TermStats(final String field, final BytesRef termtext, final int df, final long tf) {
        this.termtext = BytesRef.deepCopyOf(termtext);
        this.field = field;
        this.docFreq = df;
        this.totalTermFreq = tf;
    }
    
    String getTermText() {
        return this.termtext.utf8ToString();
    }
    
    @Override
    public String toString() {
        return "TermStats: term=" + this.termtext.utf8ToString() + " docFreq=" + this.docFreq + " totalTermFreq=" + this.totalTermFreq;
    }
}
