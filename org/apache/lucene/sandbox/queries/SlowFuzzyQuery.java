package org.apache.lucene.sandbox.queries;

import org.apache.lucene.util.ToStringUtils;
import java.io.IOException;
import org.apache.lucene.index.SingleTermsEnum;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.MultiTermQuery;

@Deprecated
public class SlowFuzzyQuery extends MultiTermQuery
{
    public static final float defaultMinSimilarity = 2.0f;
    public static final int defaultPrefixLength = 0;
    public static final int defaultMaxExpansions = 50;
    private float minimumSimilarity;
    private int prefixLength;
    private boolean termLongEnough;
    protected Term term;
    
    public SlowFuzzyQuery(final Term term, final float minimumSimilarity, final int prefixLength, final int maxExpansions) {
        super(term.field());
        this.termLongEnough = false;
        this.term = term;
        if (minimumSimilarity >= 1.0f && minimumSimilarity != (int)minimumSimilarity) {
            throw new IllegalArgumentException("fractional edit distances are not allowed");
        }
        if (minimumSimilarity < 0.0f) {
            throw new IllegalArgumentException("minimumSimilarity < 0");
        }
        if (prefixLength < 0) {
            throw new IllegalArgumentException("prefixLength < 0");
        }
        if (maxExpansions < 0) {
            throw new IllegalArgumentException("maxExpansions < 0");
        }
        this.setRewriteMethod((MultiTermQuery.RewriteMethod)new MultiTermQuery.TopTermsScoringBooleanQueryRewrite(maxExpansions));
        final String text = term.text();
        final int len = text.codePointCount(0, text.length());
        if (len > 0 && (minimumSimilarity >= 1.0f || len > 1.0f / (1.0f - minimumSimilarity))) {
            this.termLongEnough = true;
        }
        this.minimumSimilarity = minimumSimilarity;
        this.prefixLength = prefixLength;
    }
    
    public SlowFuzzyQuery(final Term term, final float minimumSimilarity, final int prefixLength) {
        this(term, minimumSimilarity, prefixLength, 50);
    }
    
    public SlowFuzzyQuery(final Term term, final float minimumSimilarity) {
        this(term, minimumSimilarity, 0, 50);
    }
    
    public SlowFuzzyQuery(final Term term) {
        this(term, 2.0f, 0, 50);
    }
    
    public float getMinSimilarity() {
        return this.minimumSimilarity;
    }
    
    public int getPrefixLength() {
        return this.prefixLength;
    }
    
    protected TermsEnum getTermsEnum(final Terms terms, final AttributeSource atts) throws IOException {
        if (!this.termLongEnough) {
            return (TermsEnum)new SingleTermsEnum(terms.iterator(), this.term.bytes());
        }
        return (TermsEnum)new SlowFuzzyTermsEnum(terms, atts, this.getTerm(), this.minimumSimilarity, this.prefixLength);
    }
    
    public Term getTerm() {
        return this.term;
    }
    
    public String toString(final String field) {
        final StringBuilder buffer = new StringBuilder();
        if (!this.term.field().equals(field)) {
            buffer.append(this.term.field());
            buffer.append(":");
        }
        buffer.append(this.term.text());
        buffer.append('~');
        buffer.append(Float.toString(this.minimumSimilarity));
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }
    
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = 31 * result + Float.floatToIntBits(this.minimumSimilarity);
        result = 31 * result + this.prefixLength;
        result = 31 * result + ((this.term == null) ? 0 : this.term.hashCode());
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
        final SlowFuzzyQuery other = (SlowFuzzyQuery)obj;
        if (Float.floatToIntBits(this.minimumSimilarity) != Float.floatToIntBits(other.minimumSimilarity)) {
            return false;
        }
        if (this.prefixLength != other.prefixLength) {
            return false;
        }
        if (this.term == null) {
            if (other.term != null) {
                return false;
            }
        }
        else if (!this.term.equals((Object)other.term)) {
            return false;
        }
        return true;
    }
}
