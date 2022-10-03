package org.apache.lucene.search.similarities;

import org.apache.lucene.search.Explanation;

public class LambdaDF extends Lambda
{
    @Override
    public final float lambda(final BasicStats stats) {
        return (stats.getDocFreq() + 1.0f) / (stats.getNumberOfDocuments() + 1.0f);
    }
    
    @Override
    public final Explanation explain(final BasicStats stats) {
        return Explanation.match(this.lambda(stats), this.getClass().getSimpleName() + ", computed from: ", Explanation.match((float)stats.getDocFreq(), "docFreq", new Explanation[0]), Explanation.match((float)stats.getNumberOfDocuments(), "numberOfDocuments", new Explanation[0]));
    }
    
    @Override
    public String toString() {
        return "D";
    }
}
