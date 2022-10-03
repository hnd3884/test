package org.apache.lucene.search;

import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.index.TermState;
import org.apache.lucene.util.ByteBlockPool;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRefHash;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.IndexReader;
import java.io.IOException;

public abstract class ScoringRewrite<B> extends TermCollectingRewrite<B>
{
    public static final ScoringRewrite<BooleanQuery.Builder> SCORING_BOOLEAN_REWRITE;
    public static final MultiTermQuery.RewriteMethod CONSTANT_SCORE_BOOLEAN_REWRITE;
    
    protected abstract void checkMaxClauseCount(final int p0) throws IOException;
    
    @Override
    public final Query rewrite(final IndexReader reader, final MultiTermQuery query) throws IOException {
        final B builder = this.getTopLevelBuilder();
        final ParallelArraysTermCollector col = new ParallelArraysTermCollector();
        this.collectTerms(reader, query, col);
        final int size = col.terms.size();
        if (size > 0) {
            final int[] sort = col.terms.sort(BytesRef.getUTF8SortedAsUnicodeComparator());
            final float[] boost = col.array.boost;
            final TermContext[] termStates = col.array.termState;
            for (final int pos : sort) {
                final Term term = new Term(query.getField(), col.terms.get(pos, new BytesRef()));
                assert reader.docFreq(term) == termStates[pos].docFreq();
                this.addClause(builder, term, termStates[pos].docFreq(), boost[pos], termStates[pos]);
            }
        }
        return this.build(builder);
    }
    
    static {
        SCORING_BOOLEAN_REWRITE = new ScoringRewrite<BooleanQuery.Builder>() {
            @Override
            protected BooleanQuery.Builder getTopLevelBuilder() {
                final BooleanQuery.Builder builder = new BooleanQuery.Builder();
                builder.setDisableCoord(true);
                return builder;
            }
            
            @Override
            protected Query build(final BooleanQuery.Builder builder) {
                return builder.build();
            }
            
            @Override
            protected void addClause(final BooleanQuery.Builder topLevel, final Term term, final int docCount, final float boost, final TermContext states) {
                final TermQuery tq = new TermQuery(term, states);
                topLevel.add(new BoostQuery(tq, boost), BooleanClause.Occur.SHOULD);
            }
            
            @Override
            protected void checkMaxClauseCount(final int count) {
                if (count > BooleanQuery.getMaxClauseCount()) {
                    throw new BooleanQuery.TooManyClauses();
                }
            }
        };
        CONSTANT_SCORE_BOOLEAN_REWRITE = new MultiTermQuery.RewriteMethod() {
            @Override
            public Query rewrite(final IndexReader reader, final MultiTermQuery query) throws IOException {
                final Query bq = ScoringRewrite.SCORING_BOOLEAN_REWRITE.rewrite(reader, query);
                return new ConstantScoreQuery(bq);
            }
        };
    }
    
    final class ParallelArraysTermCollector extends TermCollector
    {
        final TermFreqBoostByteStart array;
        final BytesRefHash terms;
        TermsEnum termsEnum;
        private BoostAttribute boostAtt;
        
        ParallelArraysTermCollector() {
            this.array = new TermFreqBoostByteStart(16);
            this.terms = new BytesRefHash(new ByteBlockPool(new ByteBlockPool.DirectAllocator()), 16, this.array);
        }
        
        @Override
        public void setNextEnum(final TermsEnum termsEnum) {
            this.termsEnum = termsEnum;
            this.boostAtt = termsEnum.attributes().addAttribute(BoostAttribute.class);
        }
        
        @Override
        public boolean collect(final BytesRef bytes) throws IOException {
            final int e = this.terms.add(bytes);
            final TermState state = this.termsEnum.termState();
            assert state != null;
            if (e < 0) {
                final int pos = -e - 1;
                this.array.termState[pos].register(state, this.readerContext.ord, this.termsEnum.docFreq(), this.termsEnum.totalTermFreq());
                assert this.array.boost[pos] == this.boostAtt.getBoost() : "boost should be equal in all segment TermsEnums";
            }
            else {
                this.array.boost[e] = this.boostAtt.getBoost();
                this.array.termState[e] = new TermContext(this.topReaderContext, state, this.readerContext.ord, this.termsEnum.docFreq(), this.termsEnum.totalTermFreq());
                ScoringRewrite.this.checkMaxClauseCount(this.terms.size());
            }
            return true;
        }
    }
    
    static final class TermFreqBoostByteStart extends BytesRefHash.DirectBytesStartArray
    {
        float[] boost;
        TermContext[] termState;
        
        public TermFreqBoostByteStart(final int initSize) {
            super(initSize);
        }
        
        @Override
        public int[] init() {
            final int[] ord = super.init();
            this.boost = new float[ArrayUtil.oversize(ord.length, 4)];
            this.termState = new TermContext[ArrayUtil.oversize(ord.length, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
            assert this.termState.length >= ord.length && this.boost.length >= ord.length;
            return ord;
        }
        
        @Override
        public int[] grow() {
            final int[] ord = super.grow();
            this.boost = ArrayUtil.grow(this.boost, ord.length);
            if (this.termState.length < ord.length) {
                final TermContext[] tmpTermState = new TermContext[ArrayUtil.oversize(ord.length, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
                System.arraycopy(this.termState, 0, tmpTermState, 0, this.termState.length);
                this.termState = tmpTermState;
            }
            assert this.termState.length >= ord.length && this.boost.length >= ord.length;
            return ord;
        }
        
        @Override
        public int[] clear() {
            this.boost = null;
            this.termState = null;
            return super.clear();
        }
    }
}
