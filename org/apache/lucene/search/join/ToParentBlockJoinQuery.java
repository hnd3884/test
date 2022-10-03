package org.apache.lucene.search.join;

import java.util.Locale;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.search.DocIdSetIterator;
import java.util.Collections;
import java.util.Collection;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.util.BitSet;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Term;
import java.util.Set;
import org.apache.lucene.index.IndexReader;
import java.io.IOException;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

public class ToParentBlockJoinQuery extends Query
{
    private final BitSetProducer parentsFilter;
    private final Query childQuery;
    private final Query origChildQuery;
    private final ScoreMode scoreMode;
    
    public ToParentBlockJoinQuery(final Query childQuery, final BitSetProducer parentsFilter, final ScoreMode scoreMode) {
        this.origChildQuery = childQuery;
        this.childQuery = childQuery;
        this.parentsFilter = parentsFilter;
        this.scoreMode = scoreMode;
    }
    
    private ToParentBlockJoinQuery(final Query origChildQuery, final Query childQuery, final BitSetProducer parentsFilter, final ScoreMode scoreMode) {
        this.origChildQuery = origChildQuery;
        this.childQuery = childQuery;
        this.parentsFilter = parentsFilter;
        this.scoreMode = scoreMode;
    }
    
    public Weight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        return new BlockJoinWeight(this, this.childQuery.createWeight(searcher, needsScores), this.parentsFilter, needsScores ? this.scoreMode : ScoreMode.None);
    }
    
    public Query getChildQuery() {
        return this.childQuery;
    }
    
    public Query rewrite(final IndexReader reader) throws IOException {
        if (this.getBoost() != 1.0f) {
            return super.rewrite(reader);
        }
        final Query childRewrite = this.childQuery.rewrite(reader);
        if (childRewrite != this.childQuery) {
            return new ToParentBlockJoinQuery(this.origChildQuery, childRewrite, this.parentsFilter, this.scoreMode);
        }
        return super.rewrite(reader);
    }
    
    public String toString(final String field) {
        return "ToParentBlockJoinQuery (" + this.childQuery.toString() + ")";
    }
    
    public boolean equals(final Object _other) {
        if (_other instanceof ToParentBlockJoinQuery) {
            final ToParentBlockJoinQuery other = (ToParentBlockJoinQuery)_other;
            return this.origChildQuery.equals((Object)other.origChildQuery) && this.parentsFilter.equals(other.parentsFilter) && this.scoreMode == other.scoreMode && super.equals((Object)other);
        }
        return false;
    }
    
    public int hashCode() {
        final int prime = 31;
        int hash = super.hashCode();
        hash = 31 * hash + this.origChildQuery.hashCode();
        hash = 31 * hash + this.scoreMode.hashCode();
        hash = 31 * hash + this.parentsFilter.hashCode();
        return hash;
    }
    
    private static class BlockJoinWeight extends Weight
    {
        private final Weight childWeight;
        private final BitSetProducer parentsFilter;
        private final ScoreMode scoreMode;
        
        public BlockJoinWeight(final Query joinQuery, final Weight childWeight, final BitSetProducer parentsFilter, final ScoreMode scoreMode) {
            super(joinQuery);
            this.childWeight = childWeight;
            this.parentsFilter = parentsFilter;
            this.scoreMode = scoreMode;
        }
        
        public void extractTerms(final Set<Term> terms) {
            this.childWeight.extractTerms((Set)terms);
        }
        
        public float getValueForNormalization() throws IOException {
            return this.childWeight.getValueForNormalization();
        }
        
        public void normalize(final float norm, final float boost) {
            this.childWeight.normalize(norm, boost);
        }
        
        public Scorer scorer(final LeafReaderContext readerContext) throws IOException {
            final Scorer childScorer = this.childWeight.scorer(readerContext);
            if (childScorer == null) {
                return null;
            }
            final int firstChildDoc = childScorer.iterator().nextDoc();
            if (firstChildDoc == Integer.MAX_VALUE) {
                return null;
            }
            final BitSet parents = this.parentsFilter.getBitSet(readerContext);
            if (parents == null) {
                return null;
            }
            return new BlockJoinScorer(this, childScorer, parents, firstChildDoc, this.scoreMode);
        }
        
        public Explanation explain(final LeafReaderContext context, final int doc) throws IOException {
            final BlockJoinScorer scorer = (BlockJoinScorer)this.scorer(context);
            if (scorer != null && scorer.iterator().advance(doc) == doc) {
                return scorer.explain(context.docBase);
            }
            return Explanation.noMatch("Not a match", new Explanation[0]);
        }
    }
    
    public abstract static class ChildrenMatchesScorer extends Scorer
    {
        protected ChildrenMatchesScorer(final Weight weight) {
            super(weight);
        }
        
        public abstract void trackPendingChildHits();
        
        public abstract int getChildCount();
        
        public abstract int[] swapChildDocs(final int[] p0);
    }
    
    static class BlockJoinScorer extends ChildrenMatchesScorer
    {
        private final Scorer childScorer;
        private final BitSet parentBits;
        private final ScoreMode scoreMode;
        private int parentDoc;
        private int prevParentDoc;
        private float parentScore;
        private int parentFreq;
        private int nextChildDoc;
        private int[] pendingChildDocs;
        private float[] pendingChildScores;
        private int childDocUpto;
        
        public BlockJoinScorer(final Weight weight, final Scorer childScorer, final BitSet parentBits, final int firstChildDoc, final ScoreMode scoreMode) {
            super(weight);
            this.parentDoc = -1;
            this.parentBits = parentBits;
            this.childScorer = childScorer;
            this.scoreMode = scoreMode;
            this.nextChildDoc = firstChildDoc;
        }
        
        public Collection<Scorer.ChildScorer> getChildren() {
            return Collections.singleton(new Scorer.ChildScorer(this.childScorer, "BLOCK_JOIN"));
        }
        
        @Override
        public int getChildCount() {
            return this.childDocUpto;
        }
        
        int getParentDoc() {
            return this.parentDoc;
        }
        
        @Override
        public int[] swapChildDocs(final int[] other) {
            final int[] ret = this.pendingChildDocs;
            if (other == null) {
                this.pendingChildDocs = new int[5];
            }
            else {
                this.pendingChildDocs = other;
            }
            return ret;
        }
        
        float[] swapChildScores(final float[] other) {
            if (this.scoreMode == ScoreMode.None) {
                throw new IllegalStateException("ScoreMode is None; you must pass trackScores=false to ToParentBlockJoinCollector");
            }
            final float[] ret = this.pendingChildScores;
            if (other == null) {
                this.pendingChildScores = new float[5];
            }
            else {
                this.pendingChildScores = other;
            }
            return ret;
        }
        
        public DocIdSetIterator iterator() {
            return new DocIdSetIterator() {
                final DocIdSetIterator childIt = BlockJoinScorer.this.childScorer.iterator();
                
                public int nextDoc() throws IOException {
                    if (BlockJoinScorer.this.nextChildDoc == Integer.MAX_VALUE) {
                        return BlockJoinScorer.this.parentDoc = Integer.MAX_VALUE;
                    }
                    BlockJoinScorer.this.parentDoc = BlockJoinScorer.this.parentBits.nextSetBit(BlockJoinScorer.this.nextChildDoc);
                    if (BlockJoinScorer.this.nextChildDoc == BlockJoinScorer.this.parentDoc) {
                        throw new IllegalStateException("child query must only match non-parent docs, but parent docID=" + BlockJoinScorer.this.nextChildDoc + " matched childScorer=" + BlockJoinScorer.this.childScorer.getClass());
                    }
                    assert BlockJoinScorer.this.parentDoc != Integer.MAX_VALUE;
                    float totalScore = 0.0f;
                    float maxScore = Float.NEGATIVE_INFINITY;
                    float minScore = Float.POSITIVE_INFINITY;
                    BlockJoinScorer.this.childDocUpto = 0;
                    BlockJoinScorer.this.parentFreq = 0;
                    do {
                        if (BlockJoinScorer.this.pendingChildDocs != null && BlockJoinScorer.this.pendingChildDocs.length == BlockJoinScorer.this.childDocUpto) {
                            BlockJoinScorer.this.pendingChildDocs = ArrayUtil.grow(BlockJoinScorer.this.pendingChildDocs);
                        }
                        if (BlockJoinScorer.this.pendingChildScores != null && BlockJoinScorer.this.scoreMode != ScoreMode.None && BlockJoinScorer.this.pendingChildScores.length == BlockJoinScorer.this.childDocUpto) {
                            BlockJoinScorer.this.pendingChildScores = ArrayUtil.grow(BlockJoinScorer.this.pendingChildScores);
                        }
                        if (BlockJoinScorer.this.pendingChildDocs != null) {
                            BlockJoinScorer.this.pendingChildDocs[BlockJoinScorer.this.childDocUpto] = BlockJoinScorer.this.nextChildDoc;
                        }
                        if (BlockJoinScorer.this.scoreMode != ScoreMode.None) {
                            final float childScore = BlockJoinScorer.this.childScorer.score();
                            final int childFreq = BlockJoinScorer.this.childScorer.freq();
                            if (BlockJoinScorer.this.pendingChildScores != null) {
                                BlockJoinScorer.this.pendingChildScores[BlockJoinScorer.this.childDocUpto] = childScore;
                            }
                            maxScore = Math.max(childScore, maxScore);
                            minScore = Math.min(childScore, minScore);
                            totalScore += childScore;
                            BlockJoinScorer.this.parentFreq += childFreq;
                        }
                        BlockJoinScorer.this.childDocUpto++;
                        BlockJoinScorer.this.nextChildDoc = this.childIt.nextDoc();
                    } while (BlockJoinScorer.this.nextChildDoc < BlockJoinScorer.this.parentDoc);
                    if (BlockJoinScorer.this.nextChildDoc == BlockJoinScorer.this.parentDoc) {
                        throw new IllegalStateException("child query must only match non-parent docs, but parent docID=" + BlockJoinScorer.this.nextChildDoc + " matched childScorer=" + BlockJoinScorer.this.childScorer.getClass());
                    }
                    switch (BlockJoinScorer.this.scoreMode) {
                        case Avg: {
                            BlockJoinScorer.this.parentScore = totalScore / BlockJoinScorer.this.childDocUpto;
                            break;
                        }
                        case Max: {
                            BlockJoinScorer.this.parentScore = maxScore;
                            break;
                        }
                        case Min: {
                            BlockJoinScorer.this.parentScore = minScore;
                            break;
                        }
                        case Total: {
                            BlockJoinScorer.this.parentScore = totalScore;
                            break;
                        }
                    }
                    return BlockJoinScorer.this.parentDoc;
                }
                
                public int advance(final int parentTarget) throws IOException {
                    if (parentTarget == Integer.MAX_VALUE) {
                        return BlockJoinScorer.this.parentDoc = Integer.MAX_VALUE;
                    }
                    if (parentTarget == 0) {
                        return this.nextDoc();
                    }
                    BlockJoinScorer.this.prevParentDoc = BlockJoinScorer.this.parentBits.prevSetBit(parentTarget - 1);
                    assert BlockJoinScorer.this.prevParentDoc >= BlockJoinScorer.this.parentDoc;
                    if (BlockJoinScorer.this.prevParentDoc > BlockJoinScorer.this.nextChildDoc) {
                        BlockJoinScorer.this.nextChildDoc = this.childIt.advance(BlockJoinScorer.this.prevParentDoc);
                    }
                    if (BlockJoinScorer.this.nextChildDoc == BlockJoinScorer.this.prevParentDoc) {
                        throw new IllegalStateException("child query must only match non-parent docs, but parent docID=" + BlockJoinScorer.this.nextChildDoc + " matched childScorer=" + BlockJoinScorer.this.childScorer.getClass());
                    }
                    final int nd = this.nextDoc();
                    return nd;
                }
                
                public int docID() {
                    return BlockJoinScorer.this.parentDoc;
                }
                
                public long cost() {
                    return this.childIt.cost();
                }
            };
        }
        
        public int docID() {
            return this.parentDoc;
        }
        
        public float score() throws IOException {
            return this.parentScore;
        }
        
        public int freq() {
            return this.parentFreq;
        }
        
        public Explanation explain(final int docBase) throws IOException {
            final int start = docBase + this.prevParentDoc + 1;
            final int end = docBase + this.parentDoc - 1;
            return Explanation.match(this.score(), String.format(Locale.ROOT, "Score based on child doc range from %d to %d", start, end), new Explanation[0]);
        }
        
        @Override
        public void trackPendingChildHits() {
            this.pendingChildDocs = new int[5];
            if (this.scoreMode != ScoreMode.None) {
                this.pendingChildScores = new float[5];
            }
        }
    }
}
