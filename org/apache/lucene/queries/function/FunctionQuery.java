package org.apache.lucene.queries.function;

import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Term;
import java.util.Set;
import java.util.Map;
import org.apache.lucene.util.ToStringUtils;
import java.io.IOException;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

public class FunctionQuery extends Query
{
    final ValueSource func;
    
    public FunctionQuery(final ValueSource func) {
        this.func = func;
    }
    
    public ValueSource getValueSource() {
        return this.func;
    }
    
    public Weight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        return new FunctionWeight(searcher);
    }
    
    public String toString(final String field) {
        return this.func.toString() + ToStringUtils.boost(this.getBoost());
    }
    
    public boolean equals(final Object o) {
        if (!FunctionQuery.class.isInstance(o)) {
            return false;
        }
        final FunctionQuery other = (FunctionQuery)o;
        return super.equals(o) && this.func.equals(other.func);
    }
    
    public int hashCode() {
        return super.hashCode() ^ this.func.hashCode();
    }
    
    protected class FunctionWeight extends Weight
    {
        protected final IndexSearcher searcher;
        protected float queryNorm;
        protected float boost;
        protected float queryWeight;
        protected final Map context;
        
        public FunctionWeight(final IndexSearcher searcher) throws IOException {
            super((Query)FunctionQuery.this);
            this.searcher = searcher;
            this.context = ValueSource.newContext(searcher);
            FunctionQuery.this.func.createWeight(this.context, searcher);
            this.normalize(1.0f, 1.0f);
        }
        
        public void extractTerms(final Set<Term> terms) {
        }
        
        public float getValueForNormalization() throws IOException {
            return this.queryWeight * this.queryWeight;
        }
        
        public void normalize(final float norm, final float boost) {
            this.queryNorm = norm;
            this.boost = boost;
            this.queryWeight = norm * boost;
        }
        
        public Scorer scorer(final LeafReaderContext context) throws IOException {
            return new AllScorer(context, this, this.queryWeight);
        }
        
        public Explanation explain(final LeafReaderContext context, final int doc) throws IOException {
            return ((AllScorer)this.scorer(context)).explain(doc);
        }
    }
    
    protected class AllScorer extends Scorer
    {
        final IndexReader reader;
        final FunctionWeight weight;
        final int maxDoc;
        final float qWeight;
        final DocIdSetIterator iterator;
        final FunctionValues vals;
        
        public AllScorer(final LeafReaderContext context, final FunctionWeight w, final float qWeight) throws IOException {
            super((Weight)w);
            this.weight = w;
            this.qWeight = qWeight;
            this.reader = (IndexReader)context.reader();
            this.maxDoc = this.reader.maxDoc();
            this.iterator = DocIdSetIterator.all(context.reader().maxDoc());
            this.vals = FunctionQuery.this.func.getValues(this.weight.context, context);
        }
        
        public DocIdSetIterator iterator() {
            return this.iterator;
        }
        
        public int docID() {
            return this.iterator.docID();
        }
        
        public float score() throws IOException {
            final float score = this.qWeight * this.vals.floatVal(this.docID());
            return (score > Float.NEGATIVE_INFINITY) ? score : -3.4028235E38f;
        }
        
        public int freq() throws IOException {
            return 1;
        }
        
        public Explanation explain(final int doc) throws IOException {
            final float sc = this.qWeight * this.vals.floatVal(doc);
            return Explanation.match(sc, "FunctionQuery(" + FunctionQuery.this.func + "), product of:", new Explanation[] { this.vals.explain(doc), Explanation.match(this.weight.boost, "boost", new Explanation[0]), Explanation.match(this.weight.queryNorm, "queryNorm", new Explanation[0]) });
        }
    }
}
