package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.LeafReaderContext;

public class SortedSetSortField extends SortField
{
    private final SortedSetSelector.Type selector;
    
    public SortedSetSortField(final String field, final boolean reverse) {
        this(field, reverse, SortedSetSelector.Type.MIN);
    }
    
    public SortedSetSortField(final String field, final boolean reverse, final SortedSetSelector.Type selector) {
        super(field, Type.CUSTOM, reverse);
        if (selector == null) {
            throw new NullPointerException();
        }
        this.selector = selector;
    }
    
    public SortedSetSelector.Type getSelector() {
        return this.selector;
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + this.selector.hashCode();
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
        final SortedSetSortField other = (SortedSetSortField)obj;
        return this.selector == other.selector;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("<sortedset: \"").append(this.getField()).append("\">");
        if (this.getReverse()) {
            buffer.append('!');
        }
        if (this.missingValue != null) {
            buffer.append(" missingValue=");
            buffer.append(this.missingValue);
        }
        buffer.append(" selector=");
        buffer.append(this.selector);
        return buffer.toString();
    }
    
    @Override
    public void setMissingValue(final Object missingValue) {
        if (missingValue != SortedSetSortField.STRING_FIRST && missingValue != SortedSetSortField.STRING_LAST) {
            throw new IllegalArgumentException("For SORTED_SET type, missing value must be either STRING_FIRST or STRING_LAST");
        }
        this.missingValue = missingValue;
    }
    
    @Override
    public FieldComparator<?> getComparator(final int numHits, final int sortPos) throws IOException {
        return new FieldComparator.TermOrdValComparator(numHits, this.getField(), this.missingValue == SortedSetSortField.STRING_LAST) {
            @Override
            protected SortedDocValues getSortedDocValues(final LeafReaderContext context, final String field) throws IOException {
                final SortedSetDocValues sortedSet = DocValues.getSortedSet(context.reader(), field);
                return SortedSetSelector.wrap(sortedSet, SortedSetSortField.this.selector);
            }
        };
    }
}
