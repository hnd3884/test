package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.SortedNumericDocValues;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.util.Bits;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Iterator;
import org.apache.lucene.util.ToStringUtils;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

public class DocValuesNumbersQuery extends Query
{
    private final String field;
    private final Set<Long> numbers;
    
    public DocValuesNumbersQuery(final String field, final Set<Long> numbers) {
        this.field = Objects.requireNonNull(field);
        this.numbers = Objects.requireNonNull(numbers, "Set of numbers must not be null");
    }
    
    public DocValuesNumbersQuery(final String field, final Long... numbers) {
        this(field, new HashSet<Long>(Arrays.asList(numbers)));
    }
    
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        final DocValuesNumbersQuery that = (DocValuesNumbersQuery)obj;
        return this.field.equals(that.field) && this.numbers.equals(that.numbers);
    }
    
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(this.field, this.numbers);
    }
    
    public String toString(final String defaultField) {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.field).append(": [");
        for (final Long number : this.numbers) {
            sb.append(number).append(", ");
        }
        if (this.numbers.size() > 0) {
            sb.setLength(sb.length() - 2);
        }
        return sb.append(']').append(ToStringUtils.boost(this.getBoost())).toString();
    }
    
    public Weight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        return (Weight)new RandomAccessWeight(this) {
            protected Bits getMatchingDocs(final LeafReaderContext context) throws IOException {
                final SortedNumericDocValues values = DocValues.getSortedNumeric(context.reader(), DocValuesNumbersQuery.this.field);
                return (Bits)new Bits() {
                    public boolean get(final int doc) {
                        values.setDocument(doc);
                        for (int count = values.count(), i = 0; i < count; ++i) {
                            if (DocValuesNumbersQuery.this.numbers.contains(values.valueAt(i))) {
                                return true;
                            }
                        }
                        return false;
                    }
                    
                    public int length() {
                        return context.reader().maxDoc();
                    }
                };
            }
        };
    }
}
