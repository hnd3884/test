package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.util.Bits;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.util.ToStringUtils;
import java.util.Objects;

public final class FieldValueQuery extends Query
{
    private final String field;
    
    public FieldValueQuery(final String field) {
        this.field = Objects.requireNonNull(field);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        final FieldValueQuery that = (FieldValueQuery)obj;
        return this.field.equals(that.field);
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + this.field.hashCode();
    }
    
    @Override
    public String toString(final String field) {
        return "FieldValueQuery [field=" + this.field + "]" + ToStringUtils.boost(this.getBoost());
    }
    
    @Override
    public Weight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        return new RandomAccessWeight(this) {
            @Override
            protected Bits getMatchingDocs(final LeafReaderContext context) throws IOException {
                return context.reader().getDocsWithField(FieldValueQuery.this.field);
            }
        };
    }
}
