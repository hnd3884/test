package org.apache.lucene.queries.payloads;

import org.apache.lucene.search.Explanation;

public abstract class PayloadFunction
{
    public abstract float currentScore(final int p0, final String p1, final int p2, final int p3, final int p4, final float p5, final float p6);
    
    public abstract float docScore(final int p0, final String p1, final int p2, final float p3);
    
    public Explanation explain(final int docId, final String field, final int numPayloadsSeen, final float payloadScore) {
        return Explanation.match(this.docScore(docId, field, numPayloadsSeen, payloadScore), this.getClass().getSimpleName() + ".docScore()", new Explanation[0]);
    }
    
    @Override
    public abstract int hashCode();
    
    @Override
    public abstract boolean equals(final Object p0);
}
