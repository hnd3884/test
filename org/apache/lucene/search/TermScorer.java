package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.index.PostingsEnum;

final class TermScorer extends Scorer
{
    private final PostingsEnum postingsEnum;
    private final Similarity.SimScorer docScorer;
    
    TermScorer(final Weight weight, final PostingsEnum td, final Similarity.SimScorer docScorer) {
        super(weight);
        this.docScorer = docScorer;
        this.postingsEnum = td;
    }
    
    @Override
    public int docID() {
        return this.postingsEnum.docID();
    }
    
    @Override
    public int freq() throws IOException {
        return this.postingsEnum.freq();
    }
    
    @Override
    public DocIdSetIterator iterator() {
        return this.postingsEnum;
    }
    
    @Override
    public float score() throws IOException {
        assert this.docID() != Integer.MAX_VALUE;
        return this.docScorer.score(this.postingsEnum.docID(), (float)this.postingsEnum.freq());
    }
    
    @Override
    public String toString() {
        return "scorer(" + this.weight + ")[" + super.toString() + "]";
    }
}
