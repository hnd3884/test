package org.apache.lucene.queryparser.surround.query;

import java.util.Iterator;
import org.apache.lucene.search.spans.SpanOrQuery;
import org.apache.lucene.search.spans.SpanBoostQuery;
import org.apache.lucene.search.MatchNoDocsQuery;
import org.apache.lucene.search.Query;
import java.io.IOException;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.spans.SpanQuery;
import java.util.HashMap;
import org.apache.lucene.index.IndexReader;

public class SpanNearClauseFactory
{
    private IndexReader reader;
    private String fieldName;
    private HashMap<SpanQuery, Float> weightBySpanQuery;
    private BasicQueryFactory qf;
    
    public SpanNearClauseFactory(final IndexReader reader, final String fieldName, final BasicQueryFactory qf) {
        this.reader = reader;
        this.fieldName = fieldName;
        this.weightBySpanQuery = new HashMap<SpanQuery, Float>();
        this.qf = qf;
    }
    
    public IndexReader getIndexReader() {
        return this.reader;
    }
    
    public String getFieldName() {
        return this.fieldName;
    }
    
    public BasicQueryFactory getBasicQueryFactory() {
        return this.qf;
    }
    
    public int size() {
        return this.weightBySpanQuery.size();
    }
    
    public void clear() {
        this.weightBySpanQuery.clear();
    }
    
    protected void addSpanQueryWeighted(final SpanQuery sq, final float weight) {
        Float w = this.weightBySpanQuery.get(sq);
        if (w != null) {
            w += weight;
        }
        else {
            w = weight;
        }
        this.weightBySpanQuery.put(sq, w);
    }
    
    public void addTermWeighted(final Term t, final float weight) throws IOException {
        final SpanTermQuery stq = this.qf.newSpanTermQuery(t);
        this.addSpanQueryWeighted((SpanQuery)stq, weight);
    }
    
    public void addSpanQuery(Query q) {
        if (q.getClass() == MatchNoDocsQuery.class) {
            return;
        }
        if (!(q instanceof SpanQuery)) {
            throw new AssertionError((Object)("Expected SpanQuery: " + q.toString(this.getFieldName())));
        }
        float boost = 1.0f;
        if (q instanceof SpanBoostQuery) {
            final SpanBoostQuery bq = (SpanBoostQuery)q;
            boost = bq.getBoost();
            q = (Query)bq.getQuery();
        }
        this.addSpanQueryWeighted((SpanQuery)q, boost);
    }
    
    public SpanQuery makeSpanClause() {
        final SpanQuery[] spanQueries = new SpanQuery[this.size()];
        final Iterator<SpanQuery> sqi = this.weightBySpanQuery.keySet().iterator();
        int i = 0;
        while (sqi.hasNext()) {
            SpanQuery sq = sqi.next();
            final float boost = this.weightBySpanQuery.get(sq);
            if (boost != 1.0f) {
                sq = (SpanQuery)new SpanBoostQuery(sq, boost);
            }
            spanQueries[i++] = sq;
        }
        if (spanQueries.length == 1) {
            return spanQueries[0];
        }
        return (SpanQuery)new SpanOrQuery(spanQueries);
    }
}
