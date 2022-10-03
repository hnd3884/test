package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.LeafReaderContext;
import java.util.HashMap;
import org.apache.lucene.util.PriorityQueue;
import java.util.Stack;
import java.util.Map;

public abstract class DiversifiedTopDocsCollector extends TopDocsCollector<ScoreDocKey>
{
    ScoreDocKey spare;
    private ScoreDocKeyQueue globalQueue;
    private int numHits;
    private Map<Long, ScoreDocKeyQueue> perKeyQueues;
    protected int maxNumPerKey;
    private Stack<ScoreDocKeyQueue> sparePerKeyQueues;
    
    public DiversifiedTopDocsCollector(final int numHits, final int maxHitsPerKey) {
        super((PriorityQueue)new ScoreDocKeyQueue(numHits));
        this.sparePerKeyQueues = new Stack<ScoreDocKeyQueue>();
        this.globalQueue = (ScoreDocKeyQueue)this.pq;
        this.perKeyQueues = new HashMap<Long, ScoreDocKeyQueue>();
        this.numHits = numHits;
        this.maxNumPerKey = maxHitsPerKey;
    }
    
    protected abstract NumericDocValues getKeys(final LeafReaderContext p0);
    
    public boolean needsScores() {
        return true;
    }
    
    protected TopDocs newTopDocs(final ScoreDoc[] results, final int start) {
        if (results == null) {
            return DiversifiedTopDocsCollector.EMPTY_TOPDOCS;
        }
        float maxScore = Float.NaN;
        if (start == 0) {
            maxScore = results[0].score;
        }
        else {
            for (int i = this.globalQueue.size(); i > 1; --i) {
                this.globalQueue.pop();
            }
            maxScore = ((ScoreDocKey)this.globalQueue.pop()).score;
        }
        return new TopDocs(this.totalHits, results, maxScore);
    }
    
    protected ScoreDocKey insert(final ScoreDocKey addition, final int docBase, final NumericDocValues keys) {
        if (this.globalQueue.size() >= this.numHits && this.globalQueue.lessThan(addition, (ScoreDocKey)this.globalQueue.top())) {
            return addition;
        }
        addition.key = keys.get(addition.doc - docBase);
        ScoreDocKeyQueue thisKeyQ = this.perKeyQueues.get(addition.key);
        if (thisKeyQ == null) {
            if (this.sparePerKeyQueues.size() == 0) {
                thisKeyQ = new ScoreDocKeyQueue(this.maxNumPerKey);
            }
            else {
                thisKeyQ = this.sparePerKeyQueues.pop();
            }
            this.perKeyQueues.put(addition.key, thisKeyQ);
        }
        final ScoreDocKey perKeyOverflow = (ScoreDocKey)thisKeyQ.insertWithOverflow((Object)addition);
        if (perKeyOverflow == addition) {
            return addition;
        }
        if (perKeyOverflow == null) {
            final ScoreDocKey globalOverflow = (ScoreDocKey)this.globalQueue.insertWithOverflow((Object)addition);
            this.perKeyGroupRemove(globalOverflow);
            return globalOverflow;
        }
        this.globalQueue.remove((Object)perKeyOverflow);
        this.globalQueue.add((Object)addition);
        return perKeyOverflow;
    }
    
    private void perKeyGroupRemove(final ScoreDocKey globalOverflow) {
        if (globalOverflow == null) {
            return;
        }
        final ScoreDocKeyQueue q = this.perKeyQueues.get(globalOverflow.key);
        final ScoreDocKey perKeyLowest = (ScoreDocKey)q.pop();
        assert globalOverflow == perKeyLowest;
        if (q.size() == 0) {
            this.perKeyQueues.remove(globalOverflow.key);
            this.sparePerKeyQueues.push(q);
        }
    }
    
    public LeafCollector getLeafCollector(final LeafReaderContext context) throws IOException {
        final int base = context.docBase;
        final NumericDocValues keySource = this.getKeys(context);
        return (LeafCollector)new LeafCollector() {
            Scorer scorer;
            
            public void setScorer(final Scorer scorer) throws IOException {
                this.scorer = scorer;
            }
            
            public void collect(int doc) throws IOException {
                final float score = this.scorer.score();
                assert !Float.isNaN(score);
                final DiversifiedTopDocsCollector this$0 = DiversifiedTopDocsCollector.this;
                ++this$0.totalHits;
                doc += base;
                if (DiversifiedTopDocsCollector.this.spare == null) {
                    DiversifiedTopDocsCollector.this.spare = new ScoreDocKey(doc, score);
                }
                else {
                    DiversifiedTopDocsCollector.this.spare.doc = doc;
                    DiversifiedTopDocsCollector.this.spare.score = score;
                }
                DiversifiedTopDocsCollector.this.spare = DiversifiedTopDocsCollector.this.insert(DiversifiedTopDocsCollector.this.spare, base, keySource);
            }
        };
    }
    
    static class ScoreDocKeyQueue extends PriorityQueue<ScoreDocKey>
    {
        ScoreDocKeyQueue(final int size) {
            super(size);
        }
        
        protected final boolean lessThan(final ScoreDocKey hitA, final ScoreDocKey hitB) {
            if (hitA.score == hitB.score) {
                return hitA.doc > hitB.doc;
            }
            return hitA.score < hitB.score;
        }
    }
    
    public static class ScoreDocKey extends ScoreDoc
    {
        Long key;
        
        protected ScoreDocKey(final int doc, final float score) {
            super(doc, score);
        }
        
        public Long getKey() {
            return this.key;
        }
        
        public String toString() {
            return "key:" + this.key + " doc=" + this.doc + " s=" + this.score;
        }
    }
}
