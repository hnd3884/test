package org.apache.lucene.search;

import java.util.ArrayList;
import java.util.Collection;
import java.io.IOException;

class ReqOptSumScorer extends Scorer
{
    protected final Scorer reqScorer;
    protected final Scorer optScorer;
    protected final DocIdSetIterator optIterator;
    
    public ReqOptSumScorer(final Scorer reqScorer, final Scorer optScorer) {
        super(reqScorer.weight);
        assert reqScorer != null;
        assert optScorer != null;
        this.reqScorer = reqScorer;
        this.optScorer = optScorer;
        this.optIterator = optScorer.iterator();
    }
    
    @Override
    public TwoPhaseIterator twoPhaseIterator() {
        return this.reqScorer.twoPhaseIterator();
    }
    
    @Override
    public DocIdSetIterator iterator() {
        return this.reqScorer.iterator();
    }
    
    @Override
    public int docID() {
        return this.reqScorer.docID();
    }
    
    @Override
    public float score() throws IOException {
        final int curDoc = this.reqScorer.docID();
        float score = this.reqScorer.score();
        int optScorerDoc = this.optIterator.docID();
        if (optScorerDoc < curDoc) {
            optScorerDoc = this.optIterator.advance(curDoc);
        }
        if (optScorerDoc == curDoc) {
            score += this.optScorer.score();
        }
        return score;
    }
    
    @Override
    public int freq() throws IOException {
        this.score();
        return (this.optIterator.docID() == this.reqScorer.docID()) ? 2 : 1;
    }
    
    @Override
    public Collection<ChildScorer> getChildren() {
        final ArrayList<ChildScorer> children = new ArrayList<ChildScorer>(2);
        children.add(new ChildScorer(this.reqScorer, "MUST"));
        children.add(new ChildScorer(this.optScorer, "SHOULD"));
        return children;
    }
}
