package org.apache.lucene.search;

import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexReader;
import java.io.IOException;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.index.Terms;
import java.util.Objects;

public abstract class MultiTermQuery extends Query
{
    protected final String field;
    protected RewriteMethod rewriteMethod;
    public static final RewriteMethod CONSTANT_SCORE_REWRITE;
    @Deprecated
    public static final RewriteMethod CONSTANT_SCORE_FILTER_REWRITE;
    public static final RewriteMethod SCORING_BOOLEAN_REWRITE;
    @Deprecated
    public static final RewriteMethod SCORING_BOOLEAN_QUERY_REWRITE;
    public static final RewriteMethod CONSTANT_SCORE_BOOLEAN_REWRITE;
    @Deprecated
    public static final RewriteMethod CONSTANT_SCORE_BOOLEAN_QUERY_REWRITE;
    
    public MultiTermQuery(final String field) {
        this.rewriteMethod = MultiTermQuery.CONSTANT_SCORE_REWRITE;
        this.field = Objects.requireNonNull(field, "field must not be null");
    }
    
    public final String getField() {
        return this.field;
    }
    
    protected abstract TermsEnum getTermsEnum(final Terms p0, final AttributeSource p1) throws IOException;
    
    protected final TermsEnum getTermsEnum(final Terms terms) throws IOException {
        return this.getTermsEnum(terms, new AttributeSource());
    }
    
    @Override
    public final Query rewrite(final IndexReader reader) throws IOException {
        if (this.getBoost() != 1.0f) {
            return super.rewrite(reader);
        }
        return this.rewriteMethod.rewrite(reader, this);
    }
    
    public RewriteMethod getRewriteMethod() {
        return this.rewriteMethod;
    }
    
    public void setRewriteMethod(final RewriteMethod method) {
        this.rewriteMethod = method;
    }
    
    @Override
    public int hashCode() {
        int h = super.hashCode();
        h = 31 * h + this.rewriteMethod.hashCode();
        h = 31 * h + Objects.hashCode(this.field);
        return h;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final MultiTermQuery other = (MultiTermQuery)obj;
        return super.equals(obj) && this.rewriteMethod.equals(other.rewriteMethod) && ((other.field == null) ? (this.field == null) : other.field.equals(this.field));
    }
    
    static {
        CONSTANT_SCORE_REWRITE = new RewriteMethod() {
            @Override
            public Query rewrite(final IndexReader reader, final MultiTermQuery query) {
                return new MultiTermQueryConstantScoreWrapper<Object>(query);
            }
        };
        CONSTANT_SCORE_FILTER_REWRITE = MultiTermQuery.CONSTANT_SCORE_REWRITE;
        SCORING_BOOLEAN_REWRITE = ScoringRewrite.SCORING_BOOLEAN_REWRITE;
        SCORING_BOOLEAN_QUERY_REWRITE = MultiTermQuery.SCORING_BOOLEAN_REWRITE;
        CONSTANT_SCORE_BOOLEAN_REWRITE = ScoringRewrite.CONSTANT_SCORE_BOOLEAN_REWRITE;
        CONSTANT_SCORE_BOOLEAN_QUERY_REWRITE = MultiTermQuery.CONSTANT_SCORE_BOOLEAN_REWRITE;
    }
    
    public abstract static class RewriteMethod
    {
        public abstract Query rewrite(final IndexReader p0, final MultiTermQuery p1) throws IOException;
        
        protected TermsEnum getTermsEnum(final MultiTermQuery query, final Terms terms, final AttributeSource atts) throws IOException {
            return query.getTermsEnum(terms, atts);
        }
    }
    
    public static final class TopTermsScoringBooleanQueryRewrite extends TopTermsRewrite<BooleanQuery.Builder>
    {
        public TopTermsScoringBooleanQueryRewrite(final int size) {
            super(size);
        }
        
        @Override
        protected int getMaxSize() {
            return BooleanQuery.getMaxClauseCount();
        }
        
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
    }
    
    public static final class TopTermsBlendedFreqScoringRewrite extends TopTermsRewrite<BlendedTermQuery.Builder>
    {
        public TopTermsBlendedFreqScoringRewrite(final int size) {
            super(size);
        }
        
        @Override
        protected int getMaxSize() {
            return BooleanQuery.getMaxClauseCount();
        }
        
        @Override
        protected BlendedTermQuery.Builder getTopLevelBuilder() {
            final BlendedTermQuery.Builder builder = new BlendedTermQuery.Builder();
            builder.setRewriteMethod(BlendedTermQuery.BOOLEAN_REWRITE);
            return builder;
        }
        
        @Override
        protected Query build(final BlendedTermQuery.Builder builder) {
            return builder.build();
        }
        
        @Override
        protected void addClause(final BlendedTermQuery.Builder topLevel, final Term term, final int docCount, final float boost, final TermContext states) {
            topLevel.add(term, boost, states);
        }
    }
    
    public static final class TopTermsBoostOnlyBooleanQueryRewrite extends TopTermsRewrite<BooleanQuery.Builder>
    {
        public TopTermsBoostOnlyBooleanQueryRewrite(final int size) {
            super(size);
        }
        
        @Override
        protected int getMaxSize() {
            return BooleanQuery.getMaxClauseCount();
        }
        
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
        protected void addClause(final BooleanQuery.Builder topLevel, final Term term, final int docFreq, final float boost, final TermContext states) {
            final Query q = new ConstantScoreQuery(new TermQuery(term, states));
            topLevel.add(new BoostQuery(q, boost), BooleanClause.Occur.SHOULD);
        }
    }
}
