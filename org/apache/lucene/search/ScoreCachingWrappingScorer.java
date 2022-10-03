package org.apache.lucene.search;

import java.util.Collections;
import java.util.Collection;
import java.io.IOException;

public class ScoreCachingWrappingScorer extends FilterScorer
{
    private int curDoc;
    private float curScore;
    
    public ScoreCachingWrappingScorer(final Scorer scorer) {
        super(scorer);
        this.curDoc = -1;
    }
    
    @Override
    public float score() throws IOException {
        final int doc = this.in.docID();
        if (doc != this.curDoc) {
            this.curScore = this.in.score();
            this.curDoc = doc;
        }
        return this.curScore;
    }
    
    @Override
    public Collection<ChildScorer> getChildren() {
        return Collections.singleton(new ChildScorer(this.in, "CACHED"));
    }
}
