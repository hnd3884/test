package org.apache.lucene.facet;

import java.util.Arrays;
import java.util.Comparator;
import org.apache.lucene.search.ConstantScoreScorer;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.BulkScorer;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Term;
import java.util.Set;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.IndexSearcher;
import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import java.util.Objects;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.Query;

class DrillSidewaysQuery extends Query
{
    final Query baseQuery;
    final Collector drillDownCollector;
    final Collector[] drillSidewaysCollectors;
    final Query[] drillDownQueries;
    final boolean scoreSubDocsAtOnce;
    
    DrillSidewaysQuery(final Query baseQuery, final Collector drillDownCollector, final Collector[] drillSidewaysCollectors, final Query[] drillDownQueries, final boolean scoreSubDocsAtOnce) {
        this.baseQuery = Objects.requireNonNull(baseQuery);
        this.drillDownCollector = drillDownCollector;
        this.drillSidewaysCollectors = drillSidewaysCollectors;
        this.drillDownQueries = drillDownQueries;
        this.scoreSubDocsAtOnce = scoreSubDocsAtOnce;
    }
    
    public String toString(final String field) {
        return "DrillSidewaysQuery";
    }
    
    public Query rewrite(final IndexReader reader) throws IOException {
        if (this.getBoost() != 1.0f) {
            return super.rewrite(reader);
        }
        Query newQuery = this.baseQuery;
        while (true) {
            final Query rewrittenQuery = newQuery.rewrite(reader);
            if (rewrittenQuery == newQuery) {
                break;
            }
            newQuery = rewrittenQuery;
        }
        if (newQuery == this.baseQuery) {
            return super.rewrite(reader);
        }
        return new DrillSidewaysQuery(newQuery, this.drillDownCollector, this.drillSidewaysCollectors, this.drillDownQueries, this.scoreSubDocsAtOnce);
    }
    
    public Weight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        final Weight baseWeight = this.baseQuery.createWeight(searcher, needsScores);
        final Weight[] drillDowns = new Weight[this.drillDownQueries.length];
        for (int dim = 0; dim < this.drillDownQueries.length; ++dim) {
            drillDowns[dim] = searcher.createNormalizedWeight(this.drillDownQueries[dim], false);
        }
        return new Weight(this) {
            public void extractTerms(final Set<Term> terms) {
            }
            
            public Explanation explain(final LeafReaderContext context, final int doc) throws IOException {
                return baseWeight.explain(context, doc);
            }
            
            public float getValueForNormalization() throws IOException {
                return baseWeight.getValueForNormalization();
            }
            
            public void normalize(final float norm, final float boost) {
                baseWeight.normalize(norm, boost);
            }
            
            public Scorer scorer(final LeafReaderContext context) throws IOException {
                throw new UnsupportedOperationException();
            }
            
            public BulkScorer bulkScorer(final LeafReaderContext context) throws IOException {
                final Scorer baseScorer = baseWeight.scorer(context);
                final DrillSidewaysScorer.DocsAndCost[] dims = new DrillSidewaysScorer.DocsAndCost[drillDowns.length];
                int nullCount = 0;
                for (int dim = 0; dim < dims.length; ++dim) {
                    Scorer scorer = drillDowns[dim].scorer(context);
                    if (scorer == null) {
                        ++nullCount;
                        scorer = (Scorer)new ConstantScoreScorer(drillDowns[dim], 0.0f, DocIdSetIterator.empty());
                    }
                    dims[dim] = new DrillSidewaysScorer.DocsAndCost(scorer, DrillSidewaysQuery.this.drillSidewaysCollectors[dim]);
                }
                if (nullCount > 1) {
                    return null;
                }
                Arrays.sort(dims, new Comparator<DrillSidewaysScorer.DocsAndCost>() {
                    @Override
                    public int compare(final DrillSidewaysScorer.DocsAndCost o1, final DrillSidewaysScorer.DocsAndCost o2) {
                        return Long.compare(o1.approximation.cost(), o2.approximation.cost());
                    }
                });
                if (baseScorer == null) {
                    return null;
                }
                return new DrillSidewaysScorer(context, baseScorer, DrillSidewaysQuery.this.drillDownCollector, dims, DrillSidewaysQuery.this.scoreSubDocsAtOnce);
            }
        };
    }
    
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = 31 * result + ((this.baseQuery == null) ? 0 : this.baseQuery.hashCode());
        result = 31 * result + ((this.drillDownCollector == null) ? 0 : this.drillDownCollector.hashCode());
        result = 31 * result + Arrays.hashCode(this.drillDownQueries);
        result = 31 * result + Arrays.hashCode(this.drillSidewaysCollectors);
        return result;
    }
    
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final DrillSidewaysQuery other = (DrillSidewaysQuery)obj;
        if (this.baseQuery == null) {
            if (other.baseQuery != null) {
                return false;
            }
        }
        else if (!this.baseQuery.equals((Object)other.baseQuery)) {
            return false;
        }
        if (this.drillDownCollector == null) {
            if (other.drillDownCollector != null) {
                return false;
            }
        }
        else if (!this.drillDownCollector.equals(other.drillDownCollector)) {
            return false;
        }
        return Arrays.equals(this.drillDownQueries, other.drillDownQueries) && Arrays.equals(this.drillSidewaysCollectors, other.drillSidewaysCollectors);
    }
}
