package org.apache.lucene.search.similarities;

import org.apache.lucene.search.Explanation;

public abstract class BasicModel
{
    public abstract float score(final BasicStats p0, final float p1);
    
    public Explanation explain(final BasicStats stats, final float tfn) {
        return Explanation.match(this.score(stats, tfn), this.getClass().getSimpleName() + ", computed from: ", Explanation.match((float)stats.getNumberOfDocuments(), "numberOfDocuments", new Explanation[0]), Explanation.match((float)stats.getTotalTermFreq(), "totalTermFreq", new Explanation[0]));
    }
    
    @Override
    public abstract String toString();
}
