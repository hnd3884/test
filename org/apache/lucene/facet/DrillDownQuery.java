package org.apache.lucene.facet;

import java.util.Iterator;
import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import java.util.Objects;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.MatchAllDocsQuery;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import org.apache.lucene.index.Term;
import java.util.Map;
import org.apache.lucene.search.BooleanQuery;
import java.util.List;
import org.apache.lucene.search.Query;

public final class DrillDownQuery extends Query
{
    private final FacetsConfig config;
    private final Query baseQuery;
    private final List<BooleanQuery.Builder> dimQueries;
    private final Map<String, Integer> drillDownDims;
    
    public static Term term(final String field, final String dim, final String... path) {
        return new Term(field, FacetsConfig.pathToString(dim, path));
    }
    
    DrillDownQuery(final FacetsConfig config, final Query baseQuery, final List<BooleanQuery.Builder> dimQueries, final Map<String, Integer> drillDownDims) {
        this.dimQueries = new ArrayList<BooleanQuery.Builder>();
        this.drillDownDims = new LinkedHashMap<String, Integer>();
        this.baseQuery = baseQuery;
        this.dimQueries.addAll(dimQueries);
        this.drillDownDims.putAll(drillDownDims);
        this.config = config;
    }
    
    DrillDownQuery(final FacetsConfig config, final Query filter, final DrillDownQuery other) {
        this.dimQueries = new ArrayList<BooleanQuery.Builder>();
        this.drillDownDims = new LinkedHashMap<String, Integer>();
        final BooleanQuery.Builder baseQuery = new BooleanQuery.Builder();
        baseQuery.add((Query)((other.baseQuery == null) ? new MatchAllDocsQuery() : other.baseQuery), BooleanClause.Occur.MUST);
        baseQuery.add(filter, BooleanClause.Occur.FILTER);
        this.baseQuery = (Query)baseQuery.build();
        this.dimQueries.addAll(other.dimQueries);
        this.drillDownDims.putAll(other.drillDownDims);
        this.config = config;
    }
    
    public DrillDownQuery(final FacetsConfig config) {
        this(config, null);
    }
    
    public DrillDownQuery(final FacetsConfig config, final Query baseQuery) {
        this.dimQueries = new ArrayList<BooleanQuery.Builder>();
        this.drillDownDims = new LinkedHashMap<String, Integer>();
        this.baseQuery = baseQuery;
        this.config = config;
    }
    
    public void add(final String dim, final String... path) {
        final String indexedField = this.config.getDimConfig(dim).indexFieldName;
        this.add(dim, (Query)new TermQuery(term(indexedField, dim, path)));
    }
    
    public void add(final String dim, final Query subQuery) {
        assert this.drillDownDims.size() == this.dimQueries.size();
        if (!this.drillDownDims.containsKey(dim)) {
            this.drillDownDims.put(dim, this.drillDownDims.size());
            final BooleanQuery.Builder builder = new BooleanQuery.Builder();
            builder.setDisableCoord(true);
            this.dimQueries.add(builder);
        }
        final int index = this.drillDownDims.get(dim);
        this.dimQueries.get(index).add(subQuery, BooleanClause.Occur.SHOULD);
    }
    
    public DrillDownQuery clone() {
        return new DrillDownQuery(this.config, this.baseQuery, this.dimQueries, this.drillDownDims);
    }
    
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(this.baseQuery, this.dimQueries);
    }
    
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        final DrillDownQuery other = (DrillDownQuery)obj;
        return Objects.equals(this.baseQuery, other.baseQuery) && this.dimQueries.equals(other.dimQueries);
    }
    
    public Query rewrite(final IndexReader r) throws IOException {
        if (this.getBoost() != 1.0f) {
            return super.rewrite(r);
        }
        final BooleanQuery rewritten = this.getBooleanQuery();
        if (rewritten.clauses().isEmpty()) {
            return (Query)new MatchAllDocsQuery();
        }
        return (Query)rewritten;
    }
    
    public String toString(final String field) {
        return this.getBooleanQuery().toString(field);
    }
    
    private BooleanQuery getBooleanQuery() {
        final BooleanQuery.Builder bq = new BooleanQuery.Builder();
        if (this.baseQuery != null) {
            bq.add(this.baseQuery, BooleanClause.Occur.MUST);
        }
        for (final BooleanQuery.Builder builder : this.dimQueries) {
            bq.add((Query)builder.build(), BooleanClause.Occur.FILTER);
        }
        return bq.build();
    }
    
    Query getBaseQuery() {
        return this.baseQuery;
    }
    
    Query[] getDrillDownQueries() {
        final Query[] dimQueries = new Query[this.dimQueries.size()];
        for (int i = 0; i < dimQueries.length; ++i) {
            dimQueries[i] = (Query)this.dimQueries.get(i).build();
        }
        return dimQueries;
    }
    
    Map<String, Integer> getDims() {
        return this.drillDownDims;
    }
}
