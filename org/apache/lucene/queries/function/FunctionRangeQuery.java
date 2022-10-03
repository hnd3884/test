package org.apache.lucene.queries.function;

import org.apache.lucene.search.Scorer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Term;
import java.util.Set;
import java.util.Map;
import java.io.IOException;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.IndexSearcher;
import java.util.Objects;
import org.apache.lucene.util.ToStringUtils;
import org.apache.lucene.search.Query;

public class FunctionRangeQuery extends Query
{
    private final ValueSource valueSource;
    private final String lowerVal;
    private final String upperVal;
    private final boolean includeLower;
    private final boolean includeUpper;
    
    public FunctionRangeQuery(final ValueSource valueSource, final Number lowerVal, final Number upperVal, final boolean includeLower, final boolean includeUpper) {
        this(valueSource, (lowerVal == null) ? null : lowerVal.toString(), (upperVal == null) ? null : upperVal.toString(), includeLower, includeUpper);
    }
    
    public FunctionRangeQuery(final ValueSource valueSource, final String lowerVal, final String upperVal, final boolean includeLower, final boolean includeUpper) {
        this.valueSource = valueSource;
        this.lowerVal = lowerVal;
        this.upperVal = upperVal;
        this.includeLower = includeLower;
        this.includeUpper = includeUpper;
    }
    
    public String toString(final String field) {
        return "frange(" + this.valueSource + "):" + (this.includeLower ? '[' : '{') + ((this.lowerVal == null) ? "*" : this.lowerVal) + " TO " + ((this.upperVal == null) ? "*" : this.upperVal) + (this.includeUpper ? ']' : '}') + ToStringUtils.boost(this.getBoost());
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FunctionRangeQuery)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final FunctionRangeQuery that = (FunctionRangeQuery)o;
        return Objects.equals(this.includeLower, that.includeLower) && Objects.equals(this.includeUpper, that.includeUpper) && Objects.equals(this.valueSource, that.valueSource) && Objects.equals(this.lowerVal, that.lowerVal) && Objects.equals(this.upperVal, that.upperVal);
    }
    
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.valueSource, this.lowerVal, this.upperVal, this.includeLower, this.includeUpper);
    }
    
    public Weight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        return new FunctionRangeWeight(searcher);
    }
    
    private class FunctionRangeWeight extends Weight
    {
        private final Map vsContext;
        
        public FunctionRangeWeight(final IndexSearcher searcher) throws IOException {
            super((Query)FunctionRangeQuery.this);
            this.vsContext = ValueSource.newContext(searcher);
            FunctionRangeQuery.this.valueSource.createWeight(this.vsContext, searcher);
        }
        
        public void extractTerms(final Set<Term> terms) {
        }
        
        public float getValueForNormalization() throws IOException {
            return 1.0f;
        }
        
        public void normalize(final float norm, final float topLevelBoost) {
        }
        
        public Explanation explain(final LeafReaderContext context, final int doc) throws IOException {
            final FunctionValues functionValues = FunctionRangeQuery.this.valueSource.getValues(this.vsContext, context);
            final ValueSourceScorer scorer = this.scorer(context);
            if (scorer.matches(doc)) {
                scorer.iterator().advance(doc);
                return Explanation.match(scorer.score(), FunctionRangeQuery.this.toString(), new Explanation[] { functionValues.explain(doc) });
            }
            return Explanation.noMatch(FunctionRangeQuery.this.toString(), new Explanation[] { functionValues.explain(doc) });
        }
        
        public ValueSourceScorer scorer(final LeafReaderContext context) throws IOException {
            final FunctionValues functionValues = FunctionRangeQuery.this.valueSource.getValues(this.vsContext, context);
            return functionValues.getRangeScorer((IndexReader)context.reader(), FunctionRangeQuery.this.lowerVal, FunctionRangeQuery.this.upperVal, FunctionRangeQuery.this.includeLower, FunctionRangeQuery.this.includeUpper);
        }
    }
}
