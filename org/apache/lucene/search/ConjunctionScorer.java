package org.apache.lucene.search;

import java.util.ArrayList;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

class ConjunctionScorer extends Scorer
{
    final ConjunctionDISI disi;
    final Scorer[] scorers;
    final float coord;
    
    ConjunctionScorer(final Weight weight, final List<Scorer> required, final List<Scorer> scorers) {
        this(weight, required, scorers, 1.0f);
    }
    
    ConjunctionScorer(final Weight weight, final List<Scorer> required, final List<Scorer> scorers, final float coord) {
        super(weight);
        assert required.containsAll(scorers);
        this.coord = coord;
        this.disi = ConjunctionDISI.intersectScorers(required);
        this.scorers = scorers.toArray(new Scorer[scorers.size()]);
    }
    
    @Override
    public TwoPhaseIterator twoPhaseIterator() {
        return this.disi.asTwoPhaseIterator();
    }
    
    @Override
    public DocIdSetIterator iterator() {
        return this.disi;
    }
    
    @Override
    public int docID() {
        return this.disi.docID();
    }
    
    @Override
    public float score() throws IOException {
        double sum = 0.0;
        for (final Scorer scorer : this.scorers) {
            sum += scorer.score();
        }
        return this.coord * (float)sum;
    }
    
    @Override
    public int freq() {
        return this.scorers.length;
    }
    
    @Override
    public Collection<ChildScorer> getChildren() {
        final ArrayList<ChildScorer> children = new ArrayList<ChildScorer>();
        for (final Scorer scorer : this.scorers) {
            children.add(new ChildScorer(scorer, "MUST"));
        }
        return children;
    }
    
    static final class DocsAndFreqs
    {
        final long cost;
        final DocIdSetIterator iterator;
        int doc;
        
        DocsAndFreqs(final DocIdSetIterator iterator) {
            this.doc = -1;
            this.iterator = iterator;
            this.cost = iterator.cost();
        }
    }
}
