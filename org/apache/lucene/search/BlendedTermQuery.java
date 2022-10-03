package org.apache.lucene.search;

import java.util.Collection;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.index.TermState;
import org.apache.lucene.index.LeafReaderContext;
import java.util.List;
import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.util.ToStringUtils;
import java.util.Arrays;
import org.apache.lucene.util.InPlaceMergeSorter;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.Term;

public final class BlendedTermQuery extends Query
{
    public static final RewriteMethod BOOLEAN_REWRITE;
    public static final RewriteMethod DISJUNCTION_MAX_REWRITE;
    private final Term[] terms;
    private final float[] boosts;
    private final TermContext[] contexts;
    private final RewriteMethod rewriteMethod;
    
    private BlendedTermQuery(final Term[] terms, final float[] boosts, final TermContext[] contexts, final RewriteMethod rewriteMethod) {
        assert terms.length == boosts.length;
        assert terms.length == contexts.length;
        this.terms = terms;
        this.boosts = boosts;
        this.contexts = contexts;
        this.rewriteMethod = rewriteMethod;
        new InPlaceMergeSorter() {
            @Override
            protected void swap(final int i, final int j) {
                final Term tmpTerm = terms[i];
                terms[i] = terms[j];
                terms[j] = tmpTerm;
                final TermContext tmpContext = contexts[i];
                contexts[i] = contexts[j];
                contexts[j] = tmpContext;
                final float tmpBoost = boosts[i];
                boosts[i] = boosts[j];
                boosts[j] = tmpBoost;
            }
            
            @Override
            protected int compare(final int i, final int j) {
                return terms[i].compareTo(terms[j]);
            }
        }.sort(0, terms.length);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        final BlendedTermQuery that = (BlendedTermQuery)obj;
        return Arrays.equals(this.terms, that.terms) && Arrays.equals(this.contexts, that.contexts) && Arrays.equals(this.boosts, that.boosts) && this.rewriteMethod.equals(that.rewriteMethod);
    }
    
    @Override
    public int hashCode() {
        int h = super.hashCode();
        h = 31 * h + Arrays.hashCode(this.terms);
        h = 31 * h + Arrays.hashCode(this.contexts);
        h = 31 * h + Arrays.hashCode(this.boosts);
        h = 31 * h + this.rewriteMethod.hashCode();
        return h;
    }
    
    @Override
    public String toString(final String field) {
        final StringBuilder builder = new StringBuilder("Blended(");
        for (int i = 0; i < this.terms.length; ++i) {
            if (i != 0) {
                builder.append(" ");
            }
            Query termQuery = new TermQuery(this.terms[i]);
            if (this.boosts[i] != 1.0f) {
                termQuery = new BoostQuery(termQuery, this.boosts[i]);
            }
            builder.append(termQuery.toString(field));
        }
        builder.append(")");
        builder.append(ToStringUtils.boost(this.getBoost()));
        return builder.toString();
    }
    
    @Override
    public final Query rewrite(final IndexReader reader) throws IOException {
        if (this.getBoost() != 1.0f) {
            return super.rewrite(reader);
        }
        final TermContext[] contexts = Arrays.copyOf(this.contexts, this.contexts.length);
        for (int i = 0; i < contexts.length; ++i) {
            if (contexts[i] == null || contexts[i].topReaderContext != reader.getContext()) {
                contexts[i] = TermContext.build(reader.getContext(), this.terms[i]);
            }
        }
        int df = 0;
        long ttf = 0L;
        for (final TermContext ctx : contexts) {
            df = Math.max(df, ctx.docFreq());
            if (ctx.totalTermFreq() == -1L) {
                ttf = -1L;
            }
            else if (ttf != -1L) {
                ttf += ctx.totalTermFreq();
            }
        }
        for (int j = 0; j < contexts.length; ++j) {
            contexts[j] = adjustFrequencies(contexts[j], df, ttf);
        }
        final Query[] termQueries = new Query[this.terms.length];
        for (int k = 0; k < this.terms.length; ++k) {
            termQueries[k] = new TermQuery(this.terms[k], contexts[k]);
            if (this.boosts[k] != 1.0f) {
                termQueries[k] = new BoostQuery(termQueries[k], this.boosts[k]);
            }
        }
        return this.rewriteMethod.rewrite(termQueries);
    }
    
    private static TermContext adjustFrequencies(final TermContext ctx, final int artificialDf, final long artificialTtf) {
        final List<LeafReaderContext> leaves = ctx.topReaderContext.leaves();
        int len;
        if (leaves == null) {
            len = 1;
        }
        else {
            len = leaves.size();
        }
        final TermContext newCtx = new TermContext(ctx.topReaderContext);
        for (int i = 0; i < len; ++i) {
            final TermState termState = ctx.get(i);
            if (termState != null) {
                newCtx.register(termState, i);
            }
        }
        newCtx.accumulateStatistics(artificialDf, artificialTtf);
        return newCtx;
    }
    
    static {
        BOOLEAN_REWRITE = new RewriteMethod() {
            @Override
            public Query rewrite(final Query[] subQueries) {
                final BooleanQuery.Builder merged = new BooleanQuery.Builder();
                merged.setDisableCoord(true);
                for (final Query query : subQueries) {
                    merged.add(query, BooleanClause.Occur.SHOULD);
                }
                return merged.build();
            }
        };
        DISJUNCTION_MAX_REWRITE = new DisjunctionMaxRewrite(0.01f);
    }
    
    public static class Builder
    {
        private int numTerms;
        private Term[] terms;
        private float[] boosts;
        private TermContext[] contexts;
        private RewriteMethod rewriteMethod;
        
        public Builder() {
            this.numTerms = 0;
            this.terms = new Term[0];
            this.boosts = new float[0];
            this.contexts = new TermContext[0];
            this.rewriteMethod = BlendedTermQuery.DISJUNCTION_MAX_REWRITE;
        }
        
        public Builder setRewriteMethod(final RewriteMethod rewiteMethod) {
            this.rewriteMethod = rewiteMethod;
            return this;
        }
        
        public Builder add(final Term term) {
            return this.add(term, 1.0f);
        }
        
        public Builder add(final Term term, final float boost) {
            return this.add(term, boost, null);
        }
        
        public Builder add(final Term term, final float boost, final TermContext context) {
            if (this.numTerms >= BooleanQuery.getMaxClauseCount()) {
                throw new BooleanQuery.TooManyClauses();
            }
            this.terms = ArrayUtil.grow(this.terms, this.numTerms + 1);
            this.boosts = ArrayUtil.grow(this.boosts, this.numTerms + 1);
            this.contexts = ArrayUtil.grow(this.contexts, this.numTerms + 1);
            this.terms[this.numTerms] = term;
            this.boosts[this.numTerms] = boost;
            this.contexts[this.numTerms] = context;
            ++this.numTerms;
            return this;
        }
        
        public BlendedTermQuery build() {
            return new BlendedTermQuery(Arrays.copyOf(this.terms, this.numTerms), Arrays.copyOf(this.boosts, this.numTerms), Arrays.copyOf(this.contexts, this.numTerms), this.rewriteMethod, null);
        }
    }
    
    public abstract static class RewriteMethod
    {
        protected RewriteMethod() {
        }
        
        public abstract Query rewrite(final Query[] p0);
    }
    
    public static class DisjunctionMaxRewrite extends RewriteMethod
    {
        private final float tieBreakerMultiplier;
        
        public DisjunctionMaxRewrite(final float tieBreakerMultiplier) {
            this.tieBreakerMultiplier = tieBreakerMultiplier;
        }
        
        @Override
        public Query rewrite(final Query[] subQueries) {
            return new DisjunctionMaxQuery(Arrays.asList(subQueries), this.tieBreakerMultiplier);
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj == null || this.getClass() != obj.getClass()) {
                return false;
            }
            final DisjunctionMaxRewrite that = (DisjunctionMaxRewrite)obj;
            return this.tieBreakerMultiplier == that.tieBreakerMultiplier;
        }
        
        @Override
        public int hashCode() {
            return 31 * this.getClass().hashCode() + Float.floatToIntBits(this.tieBreakerMultiplier);
        }
    }
}
