package org.apache.lucene.queries;

import java.util.Collections;
import java.util.Collection;
import org.apache.lucene.search.FilterScorer;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.index.Term;
import java.util.Set;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Arrays;
import org.apache.lucene.util.ToStringUtils;
import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queries.function.FunctionQuery;
import org.apache.lucene.search.Query;

public class CustomScoreQuery extends Query implements Cloneable
{
    private Query subQuery;
    private Query[] scoringQueries;
    private boolean strict;
    
    public CustomScoreQuery(final Query subQuery) {
        this(subQuery, new FunctionQuery[0]);
    }
    
    public CustomScoreQuery(final Query subQuery, final FunctionQuery scoringQuery) {
        this(subQuery, (scoringQuery != null) ? new FunctionQuery[] { scoringQuery } : new FunctionQuery[0]);
    }
    
    public CustomScoreQuery(final Query subQuery, final FunctionQuery... scoringQueries) {
        this.strict = false;
        this.subQuery = subQuery;
        this.scoringQueries = ((scoringQueries != null) ? scoringQueries : new Query[0]);
        if (subQuery == null) {
            throw new IllegalArgumentException("<subquery> must not be null!");
        }
    }
    
    public Query rewrite(final IndexReader reader) throws IOException {
        if (this.getBoost() != 1.0f) {
            return super.rewrite(reader);
        }
        CustomScoreQuery clone = null;
        final Query sq = this.subQuery.rewrite(reader);
        if (sq != this.subQuery) {
            clone = this.clone();
            clone.subQuery = sq;
        }
        for (int i = 0; i < this.scoringQueries.length; ++i) {
            final Query v = this.scoringQueries[i].rewrite(reader);
            if (v != this.scoringQueries[i]) {
                if (clone == null) {
                    clone = this.clone();
                }
                clone.scoringQueries[i] = v;
            }
        }
        return (clone == null) ? this : clone;
    }
    
    public CustomScoreQuery clone() {
        final CustomScoreQuery clone = (CustomScoreQuery)super.clone();
        clone.subQuery = this.subQuery;
        clone.scoringQueries = new Query[this.scoringQueries.length];
        for (int i = 0; i < this.scoringQueries.length; ++i) {
            clone.scoringQueries[i] = this.scoringQueries[i];
        }
        return clone;
    }
    
    public String toString(final String field) {
        final StringBuilder sb = new StringBuilder(this.name()).append("(");
        sb.append(this.subQuery.toString(field));
        for (final Query scoringQuery : this.scoringQueries) {
            sb.append(", ").append(scoringQuery.toString(field));
        }
        sb.append(")");
        sb.append(this.strict ? " STRICT" : "");
        sb.append(ToStringUtils.boost(this.getBoost()));
        return sb.toString();
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!super.equals(o)) {
            return false;
        }
        final CustomScoreQuery other = (CustomScoreQuery)o;
        return this.subQuery.equals((Object)other.subQuery) && this.strict == other.strict && this.scoringQueries.length == other.scoringQueries.length && Arrays.equals(this.scoringQueries, other.scoringQueries);
    }
    
    public int hashCode() {
        return super.hashCode() + this.subQuery.hashCode() + Arrays.hashCode(this.scoringQueries) ^ (this.strict ? 1234 : 4321);
    }
    
    protected CustomScoreProvider getCustomScoreProvider(final LeafReaderContext context) throws IOException {
        return new CustomScoreProvider(context);
    }
    
    public Weight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        return new CustomWeight(searcher, needsScores);
    }
    
    public boolean isStrict() {
        return this.strict;
    }
    
    public void setStrict(final boolean strict) {
        this.strict = strict;
    }
    
    public Query getSubQuery() {
        return this.subQuery;
    }
    
    public Query[] getScoringQueries() {
        return this.scoringQueries;
    }
    
    public String name() {
        return "custom";
    }
    
    private class CustomWeight extends Weight
    {
        Weight subQueryWeight;
        Weight[] valSrcWeights;
        boolean qStrict;
        float queryWeight;
        
        public CustomWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
            super((Query)CustomScoreQuery.this);
            this.subQueryWeight = CustomScoreQuery.this.subQuery.createWeight(searcher, needsScores);
            this.valSrcWeights = new Weight[CustomScoreQuery.this.scoringQueries.length];
            for (int i = 0; i < CustomScoreQuery.this.scoringQueries.length; ++i) {
                this.valSrcWeights[i] = CustomScoreQuery.this.scoringQueries[i].createWeight(searcher, needsScores);
            }
            this.qStrict = CustomScoreQuery.this.strict;
        }
        
        public void extractTerms(final Set<Term> terms) {
            this.subQueryWeight.extractTerms((Set)terms);
            for (final Weight scoringWeight : this.valSrcWeights) {
                scoringWeight.extractTerms((Set)terms);
            }
        }
        
        public float getValueForNormalization() throws IOException {
            float sum = this.subQueryWeight.getValueForNormalization();
            for (final Weight valSrcWeight : this.valSrcWeights) {
                if (!this.qStrict) {
                    sum += valSrcWeight.getValueForNormalization();
                }
            }
            return sum;
        }
        
        public void normalize(final float norm, final float boost) {
            this.subQueryWeight.normalize(norm, 1.0f);
            for (final Weight valSrcWeight : this.valSrcWeights) {
                if (this.qStrict) {
                    valSrcWeight.normalize(1.0f, 1.0f);
                }
                else {
                    valSrcWeight.normalize(norm, 1.0f);
                }
            }
            this.queryWeight = boost;
        }
        
        public Scorer scorer(final LeafReaderContext context) throws IOException {
            final Scorer subQueryScorer = this.subQueryWeight.scorer(context);
            if (subQueryScorer == null) {
                return null;
            }
            final Scorer[] valSrcScorers = new Scorer[this.valSrcWeights.length];
            for (int i = 0; i < valSrcScorers.length; ++i) {
                valSrcScorers[i] = this.valSrcWeights[i].scorer(context);
            }
            return (Scorer)new CustomScorer(CustomScoreQuery.this.getCustomScoreProvider(context), this, this.queryWeight, subQueryScorer, valSrcScorers);
        }
        
        public Explanation explain(final LeafReaderContext context, final int doc) throws IOException {
            final Explanation explain = this.doExplain(context, doc);
            return (explain == null) ? Explanation.noMatch("no matching docs", new Explanation[0]) : explain;
        }
        
        private Explanation doExplain(final LeafReaderContext info, final int doc) throws IOException {
            final Explanation subQueryExpl = this.subQueryWeight.explain(info, doc);
            if (!subQueryExpl.isMatch()) {
                return subQueryExpl;
            }
            final Explanation[] valSrcExpls = new Explanation[this.valSrcWeights.length];
            for (int i = 0; i < this.valSrcWeights.length; ++i) {
                valSrcExpls[i] = this.valSrcWeights[i].explain(info, doc);
            }
            final Explanation customExp = CustomScoreQuery.this.getCustomScoreProvider(info).customExplain(doc, subQueryExpl, valSrcExpls);
            final float sc = this.queryWeight * customExp.getValue();
            return Explanation.match(sc, CustomScoreQuery.this.toString() + ", product of:", new Explanation[] { customExp, Explanation.match(this.queryWeight, "queryWeight", new Explanation[0]) });
        }
    }
    
    private class CustomScorer extends FilterScorer
    {
        private final float qWeight;
        private final Scorer subQueryScorer;
        private final Scorer[] valSrcScorers;
        private final CustomScoreProvider provider;
        private final float[] vScores;
        private int valSrcDocID;
        
        private CustomScorer(final CustomScoreProvider provider, final CustomWeight w, final float qWeight, final Scorer subQueryScorer, final Scorer[] valSrcScorers) {
            super(subQueryScorer, (Weight)w);
            this.valSrcDocID = -1;
            this.qWeight = qWeight;
            this.subQueryScorer = subQueryScorer;
            this.valSrcScorers = valSrcScorers;
            this.vScores = new float[valSrcScorers.length];
            this.provider = provider;
        }
        
        public float score() throws IOException {
            final int doc = this.docID();
            if (doc > this.valSrcDocID) {
                for (final Scorer valSrcScorer : this.valSrcScorers) {
                    valSrcScorer.iterator().advance(doc);
                }
                this.valSrcDocID = doc;
            }
            for (int i = 0; i < this.valSrcScorers.length; ++i) {
                this.vScores[i] = this.valSrcScorers[i].score();
            }
            return this.qWeight * this.provider.customScore(this.subQueryScorer.docID(), this.subQueryScorer.score(), this.vScores);
        }
        
        public Collection<Scorer.ChildScorer> getChildren() {
            return Collections.singleton(new Scorer.ChildScorer(this.subQueryScorer, "CUSTOM"));
        }
    }
}
