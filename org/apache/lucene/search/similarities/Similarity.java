package org.apache.lucene.search.similarities;

import java.util.Collection;
import java.util.Collections;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.util.BytesRef;
import java.io.IOException;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.index.FieldInvertState;

public abstract class Similarity
{
    public float coord(final int overlap, final int maxOverlap) {
        return 1.0f;
    }
    
    public float queryNorm(final float valueForNormalization) {
        return 1.0f;
    }
    
    public abstract long computeNorm(final FieldInvertState p0);
    
    public abstract SimWeight computeWeight(final CollectionStatistics p0, final TermStatistics... p1);
    
    public abstract SimScorer simScorer(final SimWeight p0, final LeafReaderContext p1) throws IOException;
    
    public abstract static class SimScorer
    {
        public abstract float score(final int p0, final float p1);
        
        public abstract float computeSlopFactor(final int p0);
        
        public abstract float computePayloadFactor(final int p0, final int p1, final int p2, final BytesRef p3);
        
        public Explanation explain(final int doc, final Explanation freq) {
            return Explanation.match(this.score(doc, freq.getValue()), "score(doc=" + doc + ",freq=" + freq.getValue() + "), with freq of:", Collections.singleton(freq));
        }
    }
    
    public abstract static class SimWeight
    {
        public abstract float getValueForNormalization();
        
        public abstract void normalize(final float p0, final float p1);
    }
}
