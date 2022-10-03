package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.util.CollectionUtil;
import java.util.Comparator;
import java.util.Collection;
import java.util.Collections;
import org.apache.lucene.search.spans.Spans;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class ConjunctionDISI extends DocIdSetIterator
{
    final DocIdSetIterator lead;
    final DocIdSetIterator[] others;
    
    public static ConjunctionDISI intersectScorers(final List<Scorer> scorers) {
        if (scorers.size() < 2) {
            throw new IllegalArgumentException("Cannot make a ConjunctionDISI of less than 2 iterators");
        }
        final List<DocIdSetIterator> allIterators = new ArrayList<DocIdSetIterator>();
        final List<TwoPhaseIterator> twoPhaseIterators = new ArrayList<TwoPhaseIterator>();
        for (final Scorer scorer : scorers) {
            addScorer(scorer, allIterators, twoPhaseIterators);
        }
        if (twoPhaseIterators.isEmpty()) {
            return new ConjunctionDISI(allIterators);
        }
        return new TwoPhase((List)allIterators, (List)twoPhaseIterators);
    }
    
    public static ConjunctionDISI intersectIterators(final List<DocIdSetIterator> iterators) {
        if (iterators.size() < 2) {
            throw new IllegalArgumentException("Cannot make a ConjunctionDISI of less than 2 iterators");
        }
        final List<DocIdSetIterator> allIterators = new ArrayList<DocIdSetIterator>();
        final List<TwoPhaseIterator> twoPhaseIterators = new ArrayList<TwoPhaseIterator>();
        for (final DocIdSetIterator iterator : iterators) {
            addIterator(iterator, allIterators, twoPhaseIterators);
        }
        if (twoPhaseIterators.isEmpty()) {
            return new ConjunctionDISI(allIterators);
        }
        return new TwoPhase((List)allIterators, (List)twoPhaseIterators);
    }
    
    public static ConjunctionDISI intersectSpans(final List<Spans> spanList) {
        if (spanList.size() < 2) {
            throw new IllegalArgumentException("Cannot make a ConjunctionDISI of less than 2 iterators");
        }
        final List<DocIdSetIterator> allIterators = new ArrayList<DocIdSetIterator>();
        final List<TwoPhaseIterator> twoPhaseIterators = new ArrayList<TwoPhaseIterator>();
        for (final Spans spans : spanList) {
            addSpans(spans, allIterators, twoPhaseIterators);
        }
        if (twoPhaseIterators.isEmpty()) {
            return new ConjunctionDISI(allIterators);
        }
        return new TwoPhase((List)allIterators, (List)twoPhaseIterators);
    }
    
    private static void addScorer(final Scorer scorer, final List<DocIdSetIterator> allIterators, final List<TwoPhaseIterator> twoPhaseIterators) {
        final TwoPhaseIterator twoPhaseIter = scorer.twoPhaseIterator();
        if (twoPhaseIter != null) {
            addTwoPhaseIterator(twoPhaseIter, allIterators, twoPhaseIterators);
        }
        else {
            addIterator(scorer.iterator(), allIterators, twoPhaseIterators);
        }
    }
    
    private static void addSpans(final Spans spans, final List<DocIdSetIterator> allIterators, final List<TwoPhaseIterator> twoPhaseIterators) {
        final TwoPhaseIterator twoPhaseIter = spans.asTwoPhaseIterator();
        if (twoPhaseIter != null) {
            addTwoPhaseIterator(twoPhaseIter, allIterators, twoPhaseIterators);
        }
        else {
            addIterator(spans, allIterators, twoPhaseIterators);
        }
    }
    
    private static void addIterator(final DocIdSetIterator disi, final List<DocIdSetIterator> allIterators, final List<TwoPhaseIterator> twoPhaseIterators) {
        if (disi.getClass() == ConjunctionDISI.class || disi.getClass() == TwoPhase.class) {
            final ConjunctionDISI conjunction = (ConjunctionDISI)disi;
            allIterators.add(conjunction.lead);
            Collections.addAll(allIterators, conjunction.others);
            if (conjunction.getClass() == TwoPhase.class) {
                final TwoPhase twoPhase = (TwoPhase)conjunction;
                Collections.addAll(twoPhaseIterators, twoPhase.twoPhaseView.twoPhaseIterators);
            }
        }
        else {
            allIterators.add(disi);
        }
    }
    
    private static void addTwoPhaseIterator(final TwoPhaseIterator twoPhaseIter, final List<DocIdSetIterator> allIterators, final List<TwoPhaseIterator> twoPhaseIterators) {
        addIterator(twoPhaseIter.approximation(), allIterators, twoPhaseIterators);
        twoPhaseIterators.add(twoPhaseIter);
    }
    
    ConjunctionDISI(final List<? extends DocIdSetIterator> iterators) {
        assert iterators.size() >= 2;
        CollectionUtil.timSort(iterators, new Comparator<DocIdSetIterator>() {
            @Override
            public int compare(final DocIdSetIterator o1, final DocIdSetIterator o2) {
                return Long.compare(o1.cost(), o2.cost());
            }
        });
        this.lead = (DocIdSetIterator)iterators.get(0);
        this.others = iterators.subList(1, iterators.size()).toArray(new DocIdSetIterator[0]);
    }
    
    protected boolean matches() throws IOException {
        return true;
    }
    
    TwoPhaseIterator asTwoPhaseIterator() {
        return null;
    }
    
    private int doNext(int doc) throws IOException {
    Label_0000:
        while (doc != Integer.MAX_VALUE) {
            for (final DocIdSetIterator other : this.others) {
                if (other.docID() < doc) {
                    final int next = other.advance(doc);
                    if (next > doc) {
                        doc = this.lead.advance(next);
                        continue Label_0000;
                    }
                }
            }
            if (this.matches()) {
                return doc;
            }
            doc = this.lead.nextDoc();
        }
        return Integer.MAX_VALUE;
    }
    
    @Override
    public int advance(final int target) throws IOException {
        return this.doNext(this.lead.advance(target));
    }
    
    @Override
    public int docID() {
        return this.lead.docID();
    }
    
    @Override
    public int nextDoc() throws IOException {
        return this.doNext(this.lead.nextDoc());
    }
    
    @Override
    public long cost() {
        return this.lead.cost();
    }
    
    private static class TwoPhaseConjunctionDISI extends TwoPhaseIterator
    {
        private final TwoPhaseIterator[] twoPhaseIterators;
        private final float matchCost;
        
        private TwoPhaseConjunctionDISI(final List<? extends DocIdSetIterator> iterators, final List<TwoPhaseIterator> twoPhaseIterators) {
            super(new ConjunctionDISI(iterators));
            assert twoPhaseIterators.size() > 0;
            CollectionUtil.timSort(twoPhaseIterators, new Comparator<TwoPhaseIterator>() {
                @Override
                public int compare(final TwoPhaseIterator o1, final TwoPhaseIterator o2) {
                    return Float.compare(o1.matchCost(), o2.matchCost());
                }
            });
            this.twoPhaseIterators = twoPhaseIterators.toArray(new TwoPhaseIterator[twoPhaseIterators.size()]);
            float totalMatchCost = 0.0f;
            for (final TwoPhaseIterator tpi : twoPhaseIterators) {
                totalMatchCost += tpi.matchCost();
            }
            this.matchCost = totalMatchCost;
        }
        
        @Override
        public boolean matches() throws IOException {
            for (final TwoPhaseIterator twoPhaseIterator : this.twoPhaseIterators) {
                if (!twoPhaseIterator.matches()) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public float matchCost() {
            return this.matchCost;
        }
    }
    
    private static class TwoPhase extends ConjunctionDISI
    {
        final TwoPhaseConjunctionDISI twoPhaseView;
        
        private TwoPhase(final List<? extends DocIdSetIterator> iterators, final List<TwoPhaseIterator> twoPhaseIterators) {
            super(iterators);
            this.twoPhaseView = new TwoPhaseConjunctionDISI((List)iterators, (List)twoPhaseIterators);
        }
        
        public TwoPhaseConjunctionDISI asTwoPhaseIterator() {
            return this.twoPhaseView;
        }
        
        @Override
        protected boolean matches() throws IOException {
            return this.twoPhaseView.matches();
        }
    }
}
