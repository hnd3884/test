package org.apache.lucene.queries;

import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.ToStringUtils;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.TermsEnum;
import java.util.Iterator;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.BooleanQuery;
import java.io.IOException;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.search.MatchNoDocsQuery;
import org.apache.lucene.index.IndexReader;
import java.util.ArrayList;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.index.Term;
import java.util.List;
import org.apache.lucene.search.Query;

public class CommonTermsQuery extends Query
{
    protected final List<Term> terms;
    protected final boolean disableCoord;
    protected final float maxTermFrequency;
    protected final BooleanClause.Occur lowFreqOccur;
    protected final BooleanClause.Occur highFreqOccur;
    protected float lowFreqBoost;
    protected float highFreqBoost;
    protected float lowFreqMinNrShouldMatch;
    protected float highFreqMinNrShouldMatch;
    
    public CommonTermsQuery(final BooleanClause.Occur highFreqOccur, final BooleanClause.Occur lowFreqOccur, final float maxTermFrequency) {
        this(highFreqOccur, lowFreqOccur, maxTermFrequency, false);
    }
    
    public CommonTermsQuery(final BooleanClause.Occur highFreqOccur, final BooleanClause.Occur lowFreqOccur, final float maxTermFrequency, final boolean disableCoord) {
        this.terms = new ArrayList<Term>();
        this.lowFreqBoost = 1.0f;
        this.highFreqBoost = 1.0f;
        this.lowFreqMinNrShouldMatch = 0.0f;
        this.highFreqMinNrShouldMatch = 0.0f;
        if (highFreqOccur == BooleanClause.Occur.MUST_NOT) {
            throw new IllegalArgumentException("highFreqOccur should be MUST or SHOULD but was MUST_NOT");
        }
        if (lowFreqOccur == BooleanClause.Occur.MUST_NOT) {
            throw new IllegalArgumentException("lowFreqOccur should be MUST or SHOULD but was MUST_NOT");
        }
        this.disableCoord = disableCoord;
        this.highFreqOccur = highFreqOccur;
        this.lowFreqOccur = lowFreqOccur;
        this.maxTermFrequency = maxTermFrequency;
    }
    
    public void add(final Term term) {
        if (term == null) {
            throw new IllegalArgumentException("Term must not be null");
        }
        this.terms.add(term);
    }
    
    public Query rewrite(final IndexReader reader) throws IOException {
        if (this.getBoost() != 1.0f) {
            return super.rewrite(reader);
        }
        if (this.terms.isEmpty()) {
            return (Query)new MatchNoDocsQuery();
        }
        if (this.terms.size() == 1) {
            return this.newTermQuery(this.terms.get(0), null);
        }
        final List<LeafReaderContext> leaves = reader.leaves();
        final int maxDoc = reader.maxDoc();
        final TermContext[] contextArray = new TermContext[this.terms.size()];
        final Term[] queryTerms = this.terms.toArray(new Term[0]);
        this.collectTermContext(reader, leaves, contextArray, queryTerms);
        return this.buildQuery(maxDoc, contextArray, queryTerms);
    }
    
    protected int calcLowFreqMinimumNumberShouldMatch(final int numOptional) {
        return this.minNrShouldMatch(this.lowFreqMinNrShouldMatch, numOptional);
    }
    
    protected int calcHighFreqMinimumNumberShouldMatch(final int numOptional) {
        return this.minNrShouldMatch(this.highFreqMinNrShouldMatch, numOptional);
    }
    
    private final int minNrShouldMatch(final float minNrShouldMatch, final int numOptional) {
        if (minNrShouldMatch >= 1.0f || minNrShouldMatch == 0.0f) {
            return (int)minNrShouldMatch;
        }
        return Math.round(minNrShouldMatch * numOptional);
    }
    
    protected Query buildQuery(final int maxDoc, final TermContext[] contextArray, final Term[] queryTerms) {
        final List<Query> lowFreqQueries = new ArrayList<Query>();
        final List<Query> highFreqQueries = new ArrayList<Query>();
        for (int i = 0; i < queryTerms.length; ++i) {
            final TermContext termContext = contextArray[i];
            if (termContext == null) {
                lowFreqQueries.add(this.newTermQuery(queryTerms[i], null));
            }
            else if ((this.maxTermFrequency >= 1.0f && termContext.docFreq() > this.maxTermFrequency) || termContext.docFreq() > (int)Math.ceil(this.maxTermFrequency * maxDoc)) {
                highFreqQueries.add(this.newTermQuery(queryTerms[i], termContext));
            }
            else {
                lowFreqQueries.add(this.newTermQuery(queryTerms[i], termContext));
            }
        }
        final int numLowFreqClauses = lowFreqQueries.size();
        final int numHighFreqClauses = highFreqQueries.size();
        final BooleanClause.Occur lowFreqOccur = this.lowFreqOccur;
        BooleanClause.Occur highFreqOccur = this.highFreqOccur;
        int lowFreqMinShouldMatch = 0;
        int highFreqMinShouldMatch = 0;
        if (lowFreqOccur == BooleanClause.Occur.SHOULD && numLowFreqClauses > 0) {
            lowFreqMinShouldMatch = this.calcLowFreqMinimumNumberShouldMatch(numLowFreqClauses);
        }
        if (highFreqOccur == BooleanClause.Occur.SHOULD && numHighFreqClauses > 0) {
            highFreqMinShouldMatch = this.calcHighFreqMinimumNumberShouldMatch(numHighFreqClauses);
        }
        if (lowFreqQueries.isEmpty() && highFreqMinShouldMatch == 0 && highFreqOccur != BooleanClause.Occur.MUST) {
            highFreqOccur = BooleanClause.Occur.MUST;
        }
        final BooleanQuery.Builder builder = new BooleanQuery.Builder();
        builder.setDisableCoord(true);
        if (!lowFreqQueries.isEmpty()) {
            final BooleanQuery.Builder lowFreq = new BooleanQuery.Builder();
            lowFreq.setDisableCoord(this.disableCoord);
            for (final Query query : lowFreqQueries) {
                lowFreq.add(query, lowFreqOccur);
            }
            lowFreq.setMinimumNumberShouldMatch(lowFreqMinShouldMatch);
            final Query lowFreqQuery = (Query)lowFreq.build();
            builder.add((Query)new BoostQuery(lowFreqQuery, this.lowFreqBoost), BooleanClause.Occur.MUST);
        }
        if (!highFreqQueries.isEmpty()) {
            final BooleanQuery.Builder highFreq = new BooleanQuery.Builder();
            highFreq.setDisableCoord(this.disableCoord);
            for (final Query query : highFreqQueries) {
                highFreq.add(query, highFreqOccur);
            }
            highFreq.setMinimumNumberShouldMatch(highFreqMinShouldMatch);
            final Query highFreqQuery = (Query)highFreq.build();
            builder.add((Query)new BoostQuery(highFreqQuery, this.highFreqBoost), BooleanClause.Occur.SHOULD);
        }
        return (Query)builder.build();
    }
    
    public void collectTermContext(final IndexReader reader, final List<LeafReaderContext> leaves, final TermContext[] contextArray, final Term[] queryTerms) throws IOException {
        TermsEnum termsEnum = null;
        for (final LeafReaderContext context : leaves) {
            final Fields fields = context.reader().fields();
            for (int i = 0; i < queryTerms.length; ++i) {
                final Term term = queryTerms[i];
                final TermContext termContext = contextArray[i];
                final Terms terms = fields.terms(term.field());
                if (terms != null) {
                    termsEnum = terms.iterator();
                    assert termsEnum != null;
                    if (termsEnum != TermsEnum.EMPTY) {
                        if (termsEnum.seekExact(term.bytes())) {
                            if (termContext == null) {
                                contextArray[i] = new TermContext(reader.getContext(), termsEnum.termState(), context.ord, termsEnum.docFreq(), termsEnum.totalTermFreq());
                            }
                            else {
                                termContext.register(termsEnum.termState(), context.ord, termsEnum.docFreq(), termsEnum.totalTermFreq());
                            }
                        }
                    }
                }
            }
        }
    }
    
    public boolean isCoordDisabled() {
        return this.disableCoord;
    }
    
    public void setLowFreqMinimumNumberShouldMatch(final float min) {
        this.lowFreqMinNrShouldMatch = min;
    }
    
    public float getLowFreqMinimumNumberShouldMatch() {
        return this.lowFreqMinNrShouldMatch;
    }
    
    public void setHighFreqMinimumNumberShouldMatch(final float min) {
        this.highFreqMinNrShouldMatch = min;
    }
    
    public float getHighFreqMinimumNumberShouldMatch() {
        return this.highFreqMinNrShouldMatch;
    }
    
    public String toString(final String field) {
        final StringBuilder buffer = new StringBuilder();
        final boolean needParens = this.getLowFreqMinimumNumberShouldMatch() > 0.0f;
        if (needParens) {
            buffer.append("(");
        }
        for (int i = 0; i < this.terms.size(); ++i) {
            final Term t = this.terms.get(i);
            buffer.append(this.newTermQuery(t, null).toString());
            if (i != this.terms.size() - 1) {
                buffer.append(", ");
            }
        }
        if (needParens) {
            buffer.append(")");
        }
        if (this.getLowFreqMinimumNumberShouldMatch() > 0.0f || this.getHighFreqMinimumNumberShouldMatch() > 0.0f) {
            buffer.append('~');
            buffer.append("(");
            buffer.append(this.getLowFreqMinimumNumberShouldMatch());
            buffer.append(this.getHighFreqMinimumNumberShouldMatch());
            buffer.append(")");
        }
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }
    
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.disableCoord ? 1231 : 1237);
        result = 31 * result + Float.floatToIntBits(this.highFreqBoost);
        result = 31 * result + ((this.highFreqOccur == null) ? 0 : this.highFreqOccur.hashCode());
        result = 31 * result + Float.floatToIntBits(this.lowFreqBoost);
        result = 31 * result + ((this.lowFreqOccur == null) ? 0 : this.lowFreqOccur.hashCode());
        result = 31 * result + Float.floatToIntBits(this.maxTermFrequency);
        result = 31 * result + Float.floatToIntBits(this.lowFreqMinNrShouldMatch);
        result = 31 * result + Float.floatToIntBits(this.highFreqMinNrShouldMatch);
        result = 31 * result + ((this.terms == null) ? 0 : this.terms.hashCode());
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
        final CommonTermsQuery other = (CommonTermsQuery)obj;
        if (this.disableCoord != other.disableCoord) {
            return false;
        }
        if (Float.floatToIntBits(this.highFreqBoost) != Float.floatToIntBits(other.highFreqBoost)) {
            return false;
        }
        if (this.highFreqOccur != other.highFreqOccur) {
            return false;
        }
        if (Float.floatToIntBits(this.lowFreqBoost) != Float.floatToIntBits(other.lowFreqBoost)) {
            return false;
        }
        if (this.lowFreqOccur != other.lowFreqOccur) {
            return false;
        }
        if (Float.floatToIntBits(this.maxTermFrequency) != Float.floatToIntBits(other.maxTermFrequency)) {
            return false;
        }
        if (this.lowFreqMinNrShouldMatch != other.lowFreqMinNrShouldMatch) {
            return false;
        }
        if (this.highFreqMinNrShouldMatch != other.highFreqMinNrShouldMatch) {
            return false;
        }
        if (this.terms == null) {
            if (other.terms != null) {
                return false;
            }
        }
        else if (!this.terms.equals(other.terms)) {
            return false;
        }
        return true;
    }
    
    protected Query newTermQuery(final Term term, final TermContext context) {
        return (Query)((context == null) ? new TermQuery(term) : new TermQuery(term, context));
    }
}
