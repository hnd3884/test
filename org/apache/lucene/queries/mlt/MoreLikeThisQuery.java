package org.apache.lucene.queries.mlt;

import java.util.Arrays;
import java.io.IOException;
import java.util.Iterator;
import org.apache.lucene.search.BooleanClause;
import java.io.StringReader;
import java.io.Reader;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.index.IndexReader;
import java.util.Set;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.Query;

public class MoreLikeThisQuery extends Query
{
    private String likeText;
    private String[] moreLikeFields;
    private Analyzer analyzer;
    private final String fieldName;
    private float percentTermsToMatch;
    private int minTermFrequency;
    private int maxQueryTerms;
    private Set<?> stopWords;
    private int minDocFreq;
    
    public MoreLikeThisQuery(final String likeText, final String[] moreLikeFields, final Analyzer analyzer, final String fieldName) {
        this.percentTermsToMatch = 0.3f;
        this.minTermFrequency = 1;
        this.maxQueryTerms = 5;
        this.stopWords = null;
        this.minDocFreq = -1;
        this.likeText = likeText;
        this.moreLikeFields = moreLikeFields;
        this.analyzer = analyzer;
        this.fieldName = fieldName;
    }
    
    public Query rewrite(final IndexReader reader) throws IOException {
        if (this.getBoost() != 1.0f) {
            return super.rewrite(reader);
        }
        final MoreLikeThis mlt = new MoreLikeThis(reader);
        mlt.setFieldNames(this.moreLikeFields);
        mlt.setAnalyzer(this.analyzer);
        mlt.setMinTermFreq(this.minTermFrequency);
        if (this.minDocFreq >= 0) {
            mlt.setMinDocFreq(this.minDocFreq);
        }
        mlt.setMaxQueryTerms(this.maxQueryTerms);
        mlt.setStopWords(this.stopWords);
        final BooleanQuery bq = (BooleanQuery)mlt.like(this.fieldName, new StringReader(this.likeText));
        final BooleanQuery.Builder newBq = new BooleanQuery.Builder();
        newBq.setDisableCoord(bq.isCoordDisabled());
        for (final BooleanClause clause : bq) {
            newBq.add(clause);
        }
        newBq.setMinimumNumberShouldMatch((int)(bq.clauses().size() * this.percentTermsToMatch));
        return (Query)newBq.build();
    }
    
    public String toString(final String field) {
        return "like:" + this.likeText;
    }
    
    public float getPercentTermsToMatch() {
        return this.percentTermsToMatch;
    }
    
    public void setPercentTermsToMatch(final float percentTermsToMatch) {
        this.percentTermsToMatch = percentTermsToMatch;
    }
    
    public Analyzer getAnalyzer() {
        return this.analyzer;
    }
    
    public void setAnalyzer(final Analyzer analyzer) {
        this.analyzer = analyzer;
    }
    
    public String getLikeText() {
        return this.likeText;
    }
    
    public void setLikeText(final String likeText) {
        this.likeText = likeText;
    }
    
    public int getMaxQueryTerms() {
        return this.maxQueryTerms;
    }
    
    public void setMaxQueryTerms(final int maxQueryTerms) {
        this.maxQueryTerms = maxQueryTerms;
    }
    
    public int getMinTermFrequency() {
        return this.minTermFrequency;
    }
    
    public void setMinTermFrequency(final int minTermFrequency) {
        this.minTermFrequency = minTermFrequency;
    }
    
    public String[] getMoreLikeFields() {
        return this.moreLikeFields;
    }
    
    public void setMoreLikeFields(final String[] moreLikeFields) {
        this.moreLikeFields = moreLikeFields;
    }
    
    public Set<?> getStopWords() {
        return this.stopWords;
    }
    
    public void setStopWords(final Set<?> stopWords) {
        this.stopWords = stopWords;
    }
    
    public int getMinDocFreq() {
        return this.minDocFreq;
    }
    
    public void setMinDocFreq(final int minDocFreq) {
        this.minDocFreq = minDocFreq;
    }
    
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = 31 * result + ((this.analyzer == null) ? 0 : this.analyzer.hashCode());
        result = 31 * result + ((this.fieldName == null) ? 0 : this.fieldName.hashCode());
        result = 31 * result + ((this.likeText == null) ? 0 : this.likeText.hashCode());
        result = 31 * result + this.maxQueryTerms;
        result = 31 * result + this.minDocFreq;
        result = 31 * result + this.minTermFrequency;
        result = 31 * result + Arrays.hashCode(this.moreLikeFields);
        result = 31 * result + Float.floatToIntBits(this.percentTermsToMatch);
        result = 31 * result + ((this.stopWords == null) ? 0 : this.stopWords.hashCode());
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
        final MoreLikeThisQuery other = (MoreLikeThisQuery)obj;
        if (this.analyzer == null) {
            if (other.analyzer != null) {
                return false;
            }
        }
        else if (!this.analyzer.equals(other.analyzer)) {
            return false;
        }
        if (this.fieldName == null) {
            if (other.fieldName != null) {
                return false;
            }
        }
        else if (!this.fieldName.equals(other.fieldName)) {
            return false;
        }
        if (this.likeText == null) {
            if (other.likeText != null) {
                return false;
            }
        }
        else if (!this.likeText.equals(other.likeText)) {
            return false;
        }
        if (this.maxQueryTerms != other.maxQueryTerms) {
            return false;
        }
        if (this.minDocFreq != other.minDocFreq) {
            return false;
        }
        if (this.minTermFrequency != other.minTermFrequency) {
            return false;
        }
        if (!Arrays.equals(this.moreLikeFields, other.moreLikeFields)) {
            return false;
        }
        if (Float.floatToIntBits(this.percentTermsToMatch) != Float.floatToIntBits(other.percentTermsToMatch)) {
            return false;
        }
        if (this.stopWords == null) {
            if (other.stopWords != null) {
                return false;
            }
        }
        else if (!this.stopWords.equals(other.stopWords)) {
            return false;
        }
        return true;
    }
}
