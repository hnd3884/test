package org.apache.lucene.search;

import org.apache.lucene.util.PriorityQueue;
import java.io.IOException;

public class TopDocs
{
    public int totalHits;
    public ScoreDoc[] scoreDocs;
    private float maxScore;
    
    public float getMaxScore() {
        return this.maxScore;
    }
    
    public void setMaxScore(final float maxScore) {
        this.maxScore = maxScore;
    }
    
    TopDocs(final int totalHits, final ScoreDoc[] scoreDocs) {
        this(totalHits, scoreDocs, Float.NaN);
    }
    
    public TopDocs(final int totalHits, final ScoreDoc[] scoreDocs, final float maxScore) {
        this.totalHits = totalHits;
        this.scoreDocs = scoreDocs;
        this.maxScore = maxScore;
    }
    
    public static TopDocs merge(final int topN, final TopDocs[] shardHits) throws IOException {
        return merge(0, topN, shardHits);
    }
    
    public static TopDocs merge(final int start, final int topN, final TopDocs[] shardHits) throws IOException {
        return mergeAux(null, start, topN, shardHits);
    }
    
    public static TopFieldDocs merge(final Sort sort, final int topN, final TopFieldDocs[] shardHits) throws IOException {
        return merge(sort, 0, topN, shardHits);
    }
    
    public static TopFieldDocs merge(final Sort sort, final int start, final int topN, final TopFieldDocs[] shardHits) throws IOException {
        if (sort == null) {
            throw new IllegalArgumentException("sort must be non-null when merging field-docs");
        }
        return (TopFieldDocs)mergeAux(sort, start, topN, shardHits);
    }
    
    private static TopDocs mergeAux(final Sort sort, final int start, final int size, final TopDocs[] shardHits) throws IOException {
        PriorityQueue<ShardRef> queue;
        if (sort == null) {
            queue = new ScoreMergeSortQueue(shardHits);
        }
        else {
            queue = new MergeSortQueue(sort, shardHits);
        }
        int totalHitCount = 0;
        int availHitCount = 0;
        float maxScore = Float.MIN_VALUE;
        for (int shardIDX = 0; shardIDX < shardHits.length; ++shardIDX) {
            final TopDocs shard = shardHits[shardIDX];
            totalHitCount += shard.totalHits;
            if (shard.scoreDocs != null && shard.scoreDocs.length > 0) {
                availHitCount += shard.scoreDocs.length;
                queue.add(new ShardRef(shardIDX));
                maxScore = Math.max(maxScore, shard.getMaxScore());
            }
        }
        if (availHitCount == 0) {
            maxScore = Float.NaN;
        }
        ScoreDoc[] hits;
        if (availHitCount <= start) {
            hits = new ScoreDoc[0];
        }
        else {
            hits = new ScoreDoc[Math.min(size, availHitCount - start)];
            final int requestedResultWindow = start + size;
            final int numIterOnHits = Math.min(availHitCount, requestedResultWindow);
            int hitUpto = 0;
            while (hitUpto < numIterOnHits) {
                assert queue.size() > 0;
                final ShardRef ref = queue.top();
                final ScoreDoc hit = shardHits[ref.shardIndex].scoreDocs[ref.hitIndex++];
                hit.shardIndex = ref.shardIndex;
                if (hitUpto >= start) {
                    hits[hitUpto - start] = hit;
                }
                ++hitUpto;
                if (ref.hitIndex < shardHits[ref.shardIndex].scoreDocs.length) {
                    queue.updateTop();
                }
                else {
                    queue.pop();
                }
            }
        }
        if (sort == null) {
            return new TopDocs(totalHitCount, hits, maxScore);
        }
        return new TopFieldDocs(totalHitCount, hits, sort.getSort(), maxScore);
    }
    
    private static class ShardRef
    {
        final int shardIndex;
        int hitIndex;
        
        public ShardRef(final int shardIndex) {
            this.shardIndex = shardIndex;
        }
        
        @Override
        public String toString() {
            return "ShardRef(shardIndex=" + this.shardIndex + " hitIndex=" + this.hitIndex + ")";
        }
    }
    
    private static class ScoreMergeSortQueue extends PriorityQueue<ShardRef>
    {
        final ScoreDoc[][] shardHits;
        
        public ScoreMergeSortQueue(final TopDocs[] shardHits) {
            super(shardHits.length);
            this.shardHits = new ScoreDoc[shardHits.length][];
            for (int shardIDX = 0; shardIDX < shardHits.length; ++shardIDX) {
                this.shardHits[shardIDX] = shardHits[shardIDX].scoreDocs;
            }
        }
        
        public boolean lessThan(final ShardRef first, final ShardRef second) {
            assert first != second;
            final float firstScore = this.shardHits[first.shardIndex][first.hitIndex].score;
            final float secondScore = this.shardHits[second.shardIndex][second.hitIndex].score;
            if (firstScore < secondScore) {
                return false;
            }
            if (firstScore > secondScore) {
                return true;
            }
            if (first.shardIndex < second.shardIndex) {
                return true;
            }
            if (first.shardIndex > second.shardIndex) {
                return false;
            }
            assert first.hitIndex != second.hitIndex;
            return first.hitIndex < second.hitIndex;
        }
    }
    
    private static class MergeSortQueue extends PriorityQueue<ShardRef>
    {
        final ScoreDoc[][] shardHits;
        final FieldComparator<?>[] comparators;
        final int[] reverseMul;
        
        public MergeSortQueue(final Sort sort, final TopDocs[] shardHits) throws IOException {
            super(shardHits.length);
            this.shardHits = new ScoreDoc[shardHits.length][];
            for (int shardIDX = 0; shardIDX < shardHits.length; ++shardIDX) {
                final ScoreDoc[] shard = shardHits[shardIDX].scoreDocs;
                if (shard != null) {
                    this.shardHits[shardIDX] = shard;
                    for (int hitIDX = 0; hitIDX < shard.length; ++hitIDX) {
                        final ScoreDoc sd = shard[hitIDX];
                        if (!(sd instanceof FieldDoc)) {
                            throw new IllegalArgumentException("shard " + shardIDX + " was not sorted by the provided Sort (expected FieldDoc but got ScoreDoc)");
                        }
                        final FieldDoc fd = (FieldDoc)sd;
                        if (fd.fields == null) {
                            throw new IllegalArgumentException("shard " + shardIDX + " did not set sort field values (FieldDoc.fields is null); you must pass fillFields=true to IndexSearcher.search on each shard");
                        }
                    }
                }
            }
            final SortField[] sortFields = sort.getSort();
            this.comparators = new FieldComparator[sortFields.length];
            this.reverseMul = new int[sortFields.length];
            for (int compIDX = 0; compIDX < sortFields.length; ++compIDX) {
                final SortField sortField = sortFields[compIDX];
                this.comparators[compIDX] = sortField.getComparator(1, compIDX);
                this.reverseMul[compIDX] = (sortField.getReverse() ? -1 : 1);
            }
        }
        
        public boolean lessThan(final ShardRef first, final ShardRef second) {
            assert first != second;
            final FieldDoc firstFD = (FieldDoc)this.shardHits[first.shardIndex][first.hitIndex];
            final FieldDoc secondFD = (FieldDoc)this.shardHits[second.shardIndex][second.hitIndex];
            for (int compIDX = 0; compIDX < this.comparators.length; ++compIDX) {
                final FieldComparator comp = this.comparators[compIDX];
                final int cmp = this.reverseMul[compIDX] * comp.compareValues(firstFD.fields[compIDX], secondFD.fields[compIDX]);
                if (cmp != 0) {
                    return cmp < 0;
                }
            }
            if (first.shardIndex < second.shardIndex) {
                return true;
            }
            if (first.shardIndex > second.shardIndex) {
                return false;
            }
            assert first.hitIndex != second.hitIndex;
            return first.hitIndex < second.hitIndex;
        }
    }
}
