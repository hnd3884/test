package org.apache.lucene.queries.function.valuesource;

import org.apache.lucene.util.mutable.MutableValue;
import org.apache.lucene.util.mutable.MutableValueFloat;
import org.apache.lucene.queries.function.FunctionValues;
import java.io.IOException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.ReaderUtil;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Query;
import java.util.Map;
import org.apache.lucene.search.Weight;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.queries.function.docvalues.FloatDocValues;

class QueryDocValues extends FloatDocValues
{
    final LeafReaderContext readerContext;
    final Weight weight;
    final float defVal;
    final Map fcontext;
    final Query q;
    Scorer scorer;
    DocIdSetIterator it;
    int scorerDoc;
    boolean noMatches;
    int lastDocRequested;
    
    public QueryDocValues(final QueryValueSource vs, final LeafReaderContext readerContext, final Map fcontext) throws IOException {
        super(vs);
        this.noMatches = false;
        this.lastDocRequested = Integer.MAX_VALUE;
        this.readerContext = readerContext;
        this.defVal = vs.defVal;
        this.q = vs.q;
        this.fcontext = fcontext;
        Weight w = (fcontext == null) ? null : fcontext.get(vs);
        if (w == null) {
            IndexSearcher weightSearcher;
            if (fcontext == null) {
                weightSearcher = new IndexSearcher(ReaderUtil.getTopLevelContext((IndexReaderContext)readerContext));
            }
            else {
                weightSearcher = fcontext.get("searcher");
                if (weightSearcher == null) {
                    weightSearcher = new IndexSearcher(ReaderUtil.getTopLevelContext((IndexReaderContext)readerContext));
                }
            }
            vs.createWeight(fcontext, weightSearcher);
            w = fcontext.get(vs);
        }
        this.weight = w;
    }
    
    @Override
    public float floatVal(final int doc) {
        try {
            if (doc < this.lastDocRequested) {
                if (this.noMatches) {
                    return this.defVal;
                }
                this.scorer = this.weight.scorer(this.readerContext);
                if (this.scorer == null) {
                    this.noMatches = true;
                    return this.defVal;
                }
                this.it = this.scorer.iterator();
                this.scorerDoc = -1;
            }
            if (this.scorerDoc < (this.lastDocRequested = doc)) {
                this.scorerDoc = this.it.advance(doc);
            }
            if (this.scorerDoc > doc) {
                return this.defVal;
            }
            return this.scorer.score();
        }
        catch (final IOException e) {
            throw new RuntimeException("caught exception in QueryDocVals(" + this.q + ") doc=" + doc, e);
        }
    }
    
    @Override
    public boolean exists(final int doc) {
        try {
            if (doc < this.lastDocRequested) {
                if (this.noMatches) {
                    return false;
                }
                this.scorer = this.weight.scorer(this.readerContext);
                this.scorerDoc = -1;
                if (this.scorer == null) {
                    this.noMatches = true;
                    return false;
                }
                this.it = this.scorer.iterator();
            }
            if (this.scorerDoc < (this.lastDocRequested = doc)) {
                this.scorerDoc = this.it.advance(doc);
            }
            return this.scorerDoc <= doc;
        }
        catch (final IOException e) {
            throw new RuntimeException("caught exception in QueryDocVals(" + this.q + ") doc=" + doc, e);
        }
    }
    
    @Override
    public Object objectVal(final int doc) {
        try {
            return this.exists(doc) ? Float.valueOf(this.scorer.score()) : null;
        }
        catch (final IOException e) {
            throw new RuntimeException("caught exception in QueryDocVals(" + this.q + ") doc=" + doc, e);
        }
    }
    
    @Override
    public ValueFiller getValueFiller() {
        return new ValueFiller() {
            private final MutableValueFloat mval = new MutableValueFloat();
            
            @Override
            public MutableValue getValue() {
                return (MutableValue)this.mval;
            }
            
            @Override
            public void fillValue(final int doc) {
                try {
                    if (QueryDocValues.this.noMatches) {
                        this.mval.value = QueryDocValues.this.defVal;
                        this.mval.exists = false;
                        return;
                    }
                    QueryDocValues.this.scorer = QueryDocValues.this.weight.scorer(QueryDocValues.this.readerContext);
                    QueryDocValues.this.scorerDoc = -1;
                    if (QueryDocValues.this.scorer == null) {
                        QueryDocValues.this.noMatches = true;
                        this.mval.value = QueryDocValues.this.defVal;
                        this.mval.exists = false;
                        return;
                    }
                    QueryDocValues.this.it = QueryDocValues.this.scorer.iterator();
                    if (QueryDocValues.this.scorerDoc < (QueryDocValues.this.lastDocRequested = doc)) {
                        QueryDocValues.this.scorerDoc = QueryDocValues.this.it.advance(doc);
                    }
                    if (QueryDocValues.this.scorerDoc > doc) {
                        this.mval.value = QueryDocValues.this.defVal;
                        this.mval.exists = false;
                        return;
                    }
                    this.mval.value = QueryDocValues.this.scorer.score();
                    this.mval.exists = true;
                }
                catch (final IOException e) {
                    throw new RuntimeException("caught exception in QueryDocVals(" + QueryDocValues.this.q + ") doc=" + doc, e);
                }
            }
        };
    }
    
    @Override
    public String toString(final int doc) {
        return "query(" + this.q + ",def=" + this.defVal + ")=" + this.floatVal(doc);
    }
}
