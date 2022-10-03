package org.apache.lucene.search;

import org.apache.lucene.util.ToStringUtils;
import java.io.IOException;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexReader;
import java.util.Objects;

public class NGramPhraseQuery extends Query
{
    private final int n;
    private final PhraseQuery phraseQuery;
    
    public NGramPhraseQuery(final int n, final PhraseQuery query) {
        this.n = n;
        this.phraseQuery = Objects.requireNonNull(query);
    }
    
    @Override
    public Query rewrite(final IndexReader reader) throws IOException {
        if (this.getBoost() != 1.0f) {
            return super.rewrite(reader);
        }
        final Term[] terms = this.phraseQuery.getTerms();
        final int[] positions = this.phraseQuery.getPositions();
        boolean isOptimizable = this.phraseQuery.getSlop() == 0 && this.n >= 2 && terms.length >= 3;
        if (isOptimizable) {
            for (int i = 1; i < positions.length; ++i) {
                if (positions[i] != positions[i - 1] + 1) {
                    isOptimizable = false;
                    break;
                }
            }
        }
        if (!isOptimizable) {
            return this.phraseQuery.rewrite(reader);
        }
        final PhraseQuery.Builder builder = new PhraseQuery.Builder();
        for (int j = 0; j < terms.length; ++j) {
            if (j % this.n == 0 || j == terms.length - 1) {
                builder.add(terms[j], j);
            }
        }
        return builder.build();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!super.equals(o)) {
            return false;
        }
        final NGramPhraseQuery other = (NGramPhraseQuery)o;
        return this.n == other.n && this.phraseQuery.equals(other.phraseQuery);
    }
    
    @Override
    public int hashCode() {
        int h = super.hashCode();
        h = 31 * h + this.phraseQuery.hashCode();
        h = 31 * h + this.n;
        return h;
    }
    
    public Term[] getTerms() {
        return this.phraseQuery.getTerms();
    }
    
    public int[] getPositions() {
        return this.phraseQuery.getPositions();
    }
    
    @Override
    public String toString(final String field) {
        return this.phraseQuery.toString(field) + ToStringUtils.boost(this.getBoost());
    }
}
