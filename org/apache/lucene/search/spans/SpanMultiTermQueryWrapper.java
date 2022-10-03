package org.apache.lucene.search.spans;

import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.Term;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.search.ScoringRewrite;
import org.apache.lucene.search.Weight;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Query;
import java.io.IOException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopTermsRewrite;
import java.util.Objects;
import org.apache.lucene.search.MultiTermQuery;

public class SpanMultiTermQueryWrapper<Q extends MultiTermQuery> extends SpanQuery
{
    protected final Q query;
    private SpanRewriteMethod rewriteMethod;
    public static final SpanRewriteMethod SCORING_SPAN_QUERY_REWRITE;
    
    public SpanMultiTermQueryWrapper(final Q query) {
        this.query = Objects.requireNonNull(query);
        this.rewriteMethod = selectRewriteMethod(query);
    }
    
    private static SpanRewriteMethod selectRewriteMethod(final MultiTermQuery query) {
        final MultiTermQuery.RewriteMethod method = query.getRewriteMethod();
        if (method instanceof TopTermsRewrite) {
            final int pqsize = ((TopTermsRewrite)method).getSize();
            return new TopTermsSpanBooleanQueryRewrite(pqsize);
        }
        return SpanMultiTermQueryWrapper.SCORING_SPAN_QUERY_REWRITE;
    }
    
    public final SpanRewriteMethod getRewriteMethod() {
        return this.rewriteMethod;
    }
    
    public final void setRewriteMethod(final SpanRewriteMethod rewriteMethod) {
        this.rewriteMethod = rewriteMethod;
    }
    
    @Override
    public String getField() {
        return this.query.getField();
    }
    
    @Override
    public SpanWeight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        throw new IllegalArgumentException("Rewrite first!");
    }
    
    public Query getWrappedQuery() {
        return this.query;
    }
    
    @Override
    public String toString(final String field) {
        final StringBuilder builder = new StringBuilder();
        builder.append("SpanMultiTermQueryWrapper(");
        builder.append(this.query.toString(field));
        builder.append(")");
        return builder.toString();
    }
    
    @Override
    public Query rewrite(final IndexReader reader) throws IOException {
        if (this.getBoost() != 1.0f) {
            return super.rewrite(reader);
        }
        return this.rewriteMethod.rewrite(reader, this.query);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = 31 * result + this.query.hashCode();
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        final SpanMultiTermQueryWrapper<?> other = (SpanMultiTermQueryWrapper<?>)obj;
        return this.query.equals(other.query);
    }
    
    static {
        SCORING_SPAN_QUERY_REWRITE = new SpanRewriteMethod() {
            private final ScoringRewrite<List<SpanQuery>> delegate = new ScoringRewrite<List<SpanQuery>>() {
                @Override
                protected List<SpanQuery> getTopLevelBuilder() {
                    return new ArrayList<SpanQuery>();
                }
                
                @Override
                protected Query build(final List<SpanQuery> builder) {
                    return new SpanOrQuery((SpanQuery[])builder.toArray(new SpanQuery[builder.size()]));
                }
                
                @Override
                protected void checkMaxClauseCount(final int count) {
                }
                
                @Override
                protected void addClause(final List<SpanQuery> topLevel, final Term term, final int docCount, final float boost, final TermContext states) {
                    final SpanTermQuery q = new SpanTermQuery(term, states);
                    topLevel.add(q);
                }
            };
            
            @Override
            public SpanQuery rewrite(final IndexReader reader, final MultiTermQuery query) throws IOException {
                return (SpanQuery)this.delegate.rewrite(reader, query);
            }
        };
    }
    
    public abstract static class SpanRewriteMethod extends MultiTermQuery.RewriteMethod
    {
        @Override
        public abstract SpanQuery rewrite(final IndexReader p0, final MultiTermQuery p1) throws IOException;
    }
    
    public static final class TopTermsSpanBooleanQueryRewrite extends SpanRewriteMethod
    {
        private final TopTermsRewrite<List<SpanQuery>> delegate;
        
        public TopTermsSpanBooleanQueryRewrite(final int size) {
            this.delegate = new TopTermsRewrite<List<SpanQuery>>(size) {
                @Override
                protected int getMaxSize() {
                    return Integer.MAX_VALUE;
                }
                
                @Override
                protected List<SpanQuery> getTopLevelBuilder() {
                    return new ArrayList<SpanQuery>();
                }
                
                @Override
                protected Query build(final List<SpanQuery> builder) {
                    return new SpanOrQuery((SpanQuery[])builder.toArray(new SpanQuery[builder.size()]));
                }
                
                @Override
                protected void addClause(final List<SpanQuery> topLevel, final Term term, final int docFreq, final float boost, final TermContext states) {
                    final SpanTermQuery q = new SpanTermQuery(term, states);
                    topLevel.add(q);
                }
            };
        }
        
        public int getSize() {
            return this.delegate.getSize();
        }
        
        @Override
        public SpanQuery rewrite(final IndexReader reader, final MultiTermQuery query) throws IOException {
            return (SpanQuery)this.delegate.rewrite(reader, query);
        }
        
        @Override
        public int hashCode() {
            return 31 * this.delegate.hashCode();
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
            final TopTermsSpanBooleanQueryRewrite other = (TopTermsSpanBooleanQueryRewrite)obj;
            return this.delegate.equals(other.delegate);
        }
    }
}
