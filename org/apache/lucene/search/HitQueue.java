package org.apache.lucene.search;

import org.apache.lucene.util.PriorityQueue;

final class HitQueue extends PriorityQueue<ScoreDoc>
{
    HitQueue(final int size, final boolean prePopulate) {
        super(size, prePopulate);
    }
    
    @Override
    protected ScoreDoc getSentinelObject() {
        return new ScoreDoc(Integer.MAX_VALUE, Float.NEGATIVE_INFINITY);
    }
    
    @Override
    protected final boolean lessThan(final ScoreDoc hitA, final ScoreDoc hitB) {
        if (hitA.score == hitB.score) {
            return hitA.doc > hitB.doc;
        }
        return hitA.score < hitB.score;
    }
}
