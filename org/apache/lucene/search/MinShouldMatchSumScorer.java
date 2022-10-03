package org.apache.lucene.search;

import java.io.IOException;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.lucene.util.PriorityQueue;
import java.util.Collection;

final class MinShouldMatchSumScorer extends Scorer
{
    final int minShouldMatch;
    final float[] coord;
    DisiWrapper lead;
    int doc;
    int freq;
    final DisiPriorityQueue head;
    final DisiWrapper[] tail;
    int tailSize;
    final Collection<ChildScorer> childScorers;
    final long cost;
    
    private static long cost(final Collection<Scorer> scorers, final int minShouldMatch) {
        final PriorityQueue<Scorer> pq = new PriorityQueue<Scorer>(scorers.size() - minShouldMatch + 1) {
            @Override
            protected boolean lessThan(final Scorer a, final Scorer b) {
                return a.iterator().cost() > b.iterator().cost();
            }
        };
        for (final Scorer scorer : scorers) {
            pq.insertWithOverflow(scorer);
        }
        long cost = 0L;
        for (Scorer scorer2 = pq.pop(); scorer2 != null; scorer2 = pq.pop()) {
            cost += scorer2.iterator().cost();
        }
        return cost;
    }
    
    MinShouldMatchSumScorer(final Weight weight, final Collection<Scorer> scorers, final int minShouldMatch, final float[] coord) {
        super(weight);
        if (minShouldMatch > scorers.size()) {
            throw new IllegalArgumentException("minShouldMatch should be <= the number of scorers");
        }
        if (minShouldMatch < 1) {
            throw new IllegalArgumentException("minShouldMatch should be >= 1");
        }
        this.minShouldMatch = minShouldMatch;
        this.coord = coord;
        this.doc = -1;
        this.head = new DisiPriorityQueue(scorers.size() - minShouldMatch + 1);
        this.tail = new DisiWrapper[minShouldMatch - 1];
        for (final Scorer scorer : scorers) {
            this.addLead(new DisiWrapper(scorer));
        }
        final List<ChildScorer> children = new ArrayList<ChildScorer>();
        for (final Scorer scorer2 : scorers) {
            children.add(new ChildScorer(scorer2, "SHOULD"));
        }
        this.childScorers = Collections.unmodifiableCollection((Collection<? extends ChildScorer>)children);
        this.cost = cost(scorers, minShouldMatch);
    }
    
    @Override
    public final Collection<ChildScorer> getChildren() {
        return this.childScorers;
    }
    
    @Override
    public DocIdSetIterator iterator() {
        return new DocIdSetIterator() {
            @Override
            public int docID() {
                assert MinShouldMatchSumScorer.this.doc == MinShouldMatchSumScorer.this.lead.doc;
                return MinShouldMatchSumScorer.this.doc;
            }
            
            @Override
            public int nextDoc() throws IOException {
                for (DisiWrapper s = MinShouldMatchSumScorer.this.lead; s != null; s = s.next) {
                    final DisiWrapper evicted = MinShouldMatchSumScorer.this.insertTailWithOverFlow(s);
                    if (evicted != null) {
                        if (evicted.doc == MinShouldMatchSumScorer.this.doc) {
                            evicted.doc = evicted.iterator.nextDoc();
                        }
                        else {
                            evicted.doc = evicted.iterator.advance(MinShouldMatchSumScorer.this.doc + 1);
                        }
                        MinShouldMatchSumScorer.this.head.add(evicted);
                    }
                }
                MinShouldMatchSumScorer.this.setDocAndFreq();
                return MinShouldMatchSumScorer.this.doNext();
            }
            
            @Override
            public int advance(final int target) throws IOException {
                for (DisiWrapper s = MinShouldMatchSumScorer.this.lead; s != null; s = s.next) {
                    final DisiWrapper evicted = MinShouldMatchSumScorer.this.insertTailWithOverFlow(s);
                    if (evicted != null) {
                        evicted.doc = evicted.iterator.advance(target);
                        MinShouldMatchSumScorer.this.head.add(evicted);
                    }
                }
                DisiWrapper evicted;
                for (DisiWrapper headTop = MinShouldMatchSumScorer.this.head.top(); headTop.doc < target; headTop = MinShouldMatchSumScorer.this.head.updateTop(evicted)) {
                    evicted = MinShouldMatchSumScorer.this.insertTailWithOverFlow(headTop);
                    evicted.doc = evicted.iterator.advance(target);
                }
                MinShouldMatchSumScorer.this.setDocAndFreq();
                return MinShouldMatchSumScorer.this.doNext();
            }
            
            @Override
            public long cost() {
                return MinShouldMatchSumScorer.this.cost;
            }
        };
    }
    
    private void addLead(final DisiWrapper lead) {
        lead.next = this.lead;
        this.lead = lead;
        ++this.freq;
    }
    
    private void pushBackLeads() throws IOException {
        for (DisiWrapper s = this.lead; s != null; s = s.next) {
            this.addTail(s);
        }
    }
    
    private void advanceTail(final DisiWrapper top) throws IOException {
        top.doc = top.iterator.advance(this.doc);
        if (top.doc == this.doc) {
            this.addLead(top);
        }
        else {
            this.head.add(top);
        }
    }
    
    private void advanceTail() throws IOException {
        final DisiWrapper top = this.popTail();
        this.advanceTail(top);
    }
    
    private void setDocAndFreq() {
        assert this.head.size() > 0;
        this.lead = this.head.pop();
        this.lead.next = null;
        this.freq = 1;
        this.doc = this.lead.doc;
        while (this.head.size() > 0 && this.head.top().doc == this.doc) {
            this.addLead(this.head.pop());
        }
    }
    
    private int doNext() throws IOException {
        while (this.freq < this.minShouldMatch) {
            assert this.freq > 0;
            if (this.freq + this.tailSize >= this.minShouldMatch) {
                this.advanceTail();
            }
            else {
                this.pushBackLeads();
                this.setDocAndFreq();
            }
        }
        return this.doc;
    }
    
    private void updateFreq() throws IOException {
        assert this.freq >= this.minShouldMatch;
        for (int i = this.tailSize - 1; i >= 0; --i) {
            this.advanceTail(this.tail[i]);
        }
        this.tailSize = 0;
    }
    
    @Override
    public int freq() throws IOException {
        this.updateFreq();
        return this.freq;
    }
    
    @Override
    public float score() throws IOException {
        this.updateFreq();
        double score = 0.0;
        for (DisiWrapper s = this.lead; s != null; s = s.next) {
            score += s.scorer.score();
        }
        return this.coord[this.freq] * (float)score;
    }
    
    @Override
    public int docID() {
        assert this.doc == this.lead.doc;
        return this.doc;
    }
    
    private DisiWrapper insertTailWithOverFlow(final DisiWrapper s) {
        if (this.tailSize < this.tail.length) {
            this.addTail(s);
            return null;
        }
        if (this.tail.length >= 1) {
            final DisiWrapper top = this.tail[0];
            if (top.cost < s.cost) {
                this.tail[0] = s;
                downHeapCost(this.tail, this.tailSize);
                return top;
            }
        }
        return s;
    }
    
    private void addTail(final DisiWrapper s) {
        this.tail[this.tailSize] = s;
        upHeapCost(this.tail, this.tailSize);
        ++this.tailSize;
    }
    
    private DisiWrapper popTail() {
        assert this.tailSize > 0;
        final DisiWrapper result = this.tail[0];
        final DisiWrapper[] tail = this.tail;
        final int n = 0;
        final DisiWrapper[] tail2 = this.tail;
        final int tailSize = this.tailSize - 1;
        this.tailSize = tailSize;
        tail[n] = tail2[tailSize];
        downHeapCost(this.tail, this.tailSize);
        return result;
    }
    
    private static void upHeapCost(final DisiWrapper[] heap, int i) {
        final DisiWrapper node = heap[i];
        final long nodeCost = node.cost;
        for (int j = DisiPriorityQueue.parentNode(i); j >= 0 && nodeCost < heap[j].cost; j = DisiPriorityQueue.parentNode(j)) {
            heap[i] = heap[j];
            i = j;
        }
        heap[i] = node;
    }
    
    private static void downHeapCost(final DisiWrapper[] heap, final int size) {
        int i = 0;
        final DisiWrapper node = heap[0];
        int j = DisiPriorityQueue.leftNode(i);
        if (j < size) {
            int k = DisiPriorityQueue.rightNode(j);
            if (k < size && heap[k].cost < heap[j].cost) {
                j = k;
            }
            if (heap[j].cost < node.cost) {
                do {
                    heap[i] = heap[j];
                    i = j;
                    j = DisiPriorityQueue.leftNode(i);
                    k = DisiPriorityQueue.rightNode(j);
                    if (k < size && heap[k].cost < heap[j].cost) {
                        j = k;
                    }
                } while (j < size && heap[j].cost < node.cost);
                heap[i] = node;
            }
        }
    }
}
