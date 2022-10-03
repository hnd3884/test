package org.apache.lucene.search.spans;

import org.apache.lucene.search.Weight;
import org.apache.lucene.util.ToStringUtils;
import org.apache.lucene.search.Query;
import org.apache.lucene.index.IndexReader;
import java.io.IOException;
import org.apache.lucene.search.IndexSearcher;
import java.util.Objects;

public final class FieldMaskingSpanQuery extends SpanQuery
{
    private final SpanQuery maskedQuery;
    private final String field;
    
    public FieldMaskingSpanQuery(final SpanQuery maskedQuery, final String maskedField) {
        this.maskedQuery = Objects.requireNonNull(maskedQuery);
        this.field = Objects.requireNonNull(maskedField);
    }
    
    @Override
    public String getField() {
        return this.field;
    }
    
    public SpanQuery getMaskedQuery() {
        return this.maskedQuery;
    }
    
    @Override
    public SpanWeight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        return this.maskedQuery.createWeight(searcher, needsScores);
    }
    
    @Override
    public Query rewrite(final IndexReader reader) throws IOException {
        if (this.getBoost() != 1.0f) {
            return super.rewrite(reader);
        }
        final FieldMaskingSpanQuery clone = null;
        final SpanQuery rewritten = (SpanQuery)this.maskedQuery.rewrite(reader);
        if (rewritten != this.maskedQuery) {
            return new FieldMaskingSpanQuery(rewritten, this.field);
        }
        return super.rewrite(reader);
    }
    
    @Override
    public String toString(final String field) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("mask(");
        buffer.append(this.maskedQuery.toString(field));
        buffer.append(")");
        buffer.append(" as ");
        buffer.append(this.field);
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!super.equals(o)) {
            return false;
        }
        final FieldMaskingSpanQuery other = (FieldMaskingSpanQuery)o;
        return this.getField().equals(other.getField()) && this.getMaskedQuery().equals(other.getMaskedQuery());
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() ^ this.getMaskedQuery().hashCode() ^ this.getField().hashCode();
    }
}
