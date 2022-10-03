package org.apache.lucene.search;

import org.apache.lucene.util.ToStringUtils;
import java.io.IOException;
import org.apache.lucene.index.SingleTermsEnum;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.Term;

public class FuzzyQuery extends MultiTermQuery
{
    public static final int defaultMaxEdits = 2;
    public static final int defaultPrefixLength = 0;
    public static final int defaultMaxExpansions = 50;
    public static final boolean defaultTranspositions = true;
    private final int maxEdits;
    private final int maxExpansions;
    private final boolean transpositions;
    private final int prefixLength;
    private final Term term;
    @Deprecated
    public static final float defaultMinSimilarity = 2.0f;
    
    public FuzzyQuery(final Term term, final int maxEdits, final int prefixLength, final int maxExpansions, final boolean transpositions) {
        super(term.field());
        if (maxEdits < 0 || maxEdits > 2) {
            throw new IllegalArgumentException("maxEdits must be between 0 and 2");
        }
        if (prefixLength < 0) {
            throw new IllegalArgumentException("prefixLength cannot be negative.");
        }
        if (maxExpansions <= 0) {
            throw new IllegalArgumentException("maxExpansions must be positive.");
        }
        this.term = term;
        this.maxEdits = maxEdits;
        this.prefixLength = prefixLength;
        this.transpositions = transpositions;
        this.maxExpansions = maxExpansions;
        this.setRewriteMethod(new TopTermsBlendedFreqScoringRewrite(maxExpansions));
    }
    
    public FuzzyQuery(final Term term, final int maxEdits, final int prefixLength) {
        this(term, maxEdits, prefixLength, 50, true);
    }
    
    public FuzzyQuery(final Term term, final int maxEdits) {
        this(term, maxEdits, 0);
    }
    
    public FuzzyQuery(final Term term) {
        this(term, 2);
    }
    
    public int getMaxEdits() {
        return this.maxEdits;
    }
    
    public int getPrefixLength() {
        return this.prefixLength;
    }
    
    public boolean getTranspositions() {
        return this.transpositions;
    }
    
    @Override
    protected TermsEnum getTermsEnum(final Terms terms, final AttributeSource atts) throws IOException {
        if (this.maxEdits == 0 || this.prefixLength >= this.term.text().length()) {
            return new SingleTermsEnum(terms.iterator(), this.term.bytes());
        }
        return new FuzzyTermsEnum(terms, atts, this.getTerm(), (float)this.maxEdits, this.prefixLength, this.transpositions);
    }
    
    public Term getTerm() {
        return this.term;
    }
    
    @Override
    public String toString(final String field) {
        final StringBuilder buffer = new StringBuilder();
        if (!this.term.field().equals(field)) {
            buffer.append(this.term.field());
            buffer.append(":");
        }
        buffer.append(this.term.text());
        buffer.append('~');
        buffer.append(Integer.toString(this.maxEdits));
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = 31 * result + this.maxEdits;
        result = 31 * result + this.prefixLength;
        result = 31 * result + this.maxExpansions;
        result = 31 * result + (this.transpositions ? 0 : 1);
        result = 31 * result + ((this.term == null) ? 0 : this.term.hashCode());
        return result;
    }
    
    @Override
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
        final FuzzyQuery other = (FuzzyQuery)obj;
        if (this.maxEdits != other.maxEdits) {
            return false;
        }
        if (this.prefixLength != other.prefixLength) {
            return false;
        }
        if (this.maxExpansions != other.maxExpansions) {
            return false;
        }
        if (this.transpositions != other.transpositions) {
            return false;
        }
        if (this.term == null) {
            if (other.term != null) {
                return false;
            }
        }
        else if (!this.term.equals(other.term)) {
            return false;
        }
        return true;
    }
    
    @Deprecated
    public static int floatToEdits(final float minimumSimilarity, final int termLen) {
        if (minimumSimilarity >= 1.0f) {
            return (int)Math.min(minimumSimilarity, 2.0f);
        }
        if (minimumSimilarity == 0.0f) {
            return 0;
        }
        return Math.min((int)((1.0 - minimumSimilarity) * termLen), 2);
    }
}
