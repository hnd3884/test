package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.LeafReaderContext;

public class SortedNumericSortField extends SortField
{
    private final SortedNumericSelector.Type selector;
    private final Type type;
    
    public SortedNumericSortField(final String field, final Type type) {
        this(field, type, false);
    }
    
    public SortedNumericSortField(final String field, final Type type, final boolean reverse) {
        this(field, type, reverse, SortedNumericSelector.Type.MIN);
    }
    
    public SortedNumericSortField(final String field, final Type type, final boolean reverse, final SortedNumericSelector.Type selector) {
        super(field, Type.CUSTOM, reverse);
        if (selector == null) {
            throw new NullPointerException();
        }
        if (type == null) {
            throw new NullPointerException();
        }
        this.selector = selector;
        this.type = type;
    }
    
    public SortedNumericSelector.Type getSelector() {
        return this.selector;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = 31 * result + this.selector.hashCode();
        result = 31 * result + this.type.hashCode();
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
        final SortedNumericSortField other = (SortedNumericSortField)obj;
        return this.selector == other.selector && this.type == other.type;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("<sortednumeric: \"").append(this.getField()).append("\">");
        if (this.getReverse()) {
            buffer.append('!');
        }
        if (this.missingValue != null) {
            buffer.append(" missingValue=");
            buffer.append(this.missingValue);
        }
        buffer.append(" selector=");
        buffer.append(this.selector);
        buffer.append(" type=");
        buffer.append(this.type);
        return buffer.toString();
    }
    
    @Override
    public void setMissingValue(final Object missingValue) {
        this.missingValue = missingValue;
    }
    
    @Override
    public FieldComparator<?> getComparator(final int numHits, final int sortPos) throws IOException {
        switch (this.type) {
            case INT: {
                return new FieldComparator.IntComparator(numHits, this.getField(), (Integer)this.missingValue) {
                    @Override
                    protected NumericDocValues getNumericDocValues(final LeafReaderContext context, final String field) throws IOException {
                        return SortedNumericSelector.wrap(DocValues.getSortedNumeric(context.reader(), field), SortedNumericSortField.this.selector, SortedNumericSortField.this.type);
                    }
                };
            }
            case FLOAT: {
                return new FieldComparator.FloatComparator(numHits, this.getField(), (Float)this.missingValue) {
                    @Override
                    protected NumericDocValues getNumericDocValues(final LeafReaderContext context, final String field) throws IOException {
                        return SortedNumericSelector.wrap(DocValues.getSortedNumeric(context.reader(), field), SortedNumericSortField.this.selector, SortedNumericSortField.this.type);
                    }
                };
            }
            case LONG: {
                return new FieldComparator.LongComparator(numHits, this.getField(), (Long)this.missingValue) {
                    @Override
                    protected NumericDocValues getNumericDocValues(final LeafReaderContext context, final String field) throws IOException {
                        return SortedNumericSelector.wrap(DocValues.getSortedNumeric(context.reader(), field), SortedNumericSortField.this.selector, SortedNumericSortField.this.type);
                    }
                };
            }
            case DOUBLE: {
                return new FieldComparator.DoubleComparator(numHits, this.getField(), (Double)this.missingValue) {
                    @Override
                    protected NumericDocValues getNumericDocValues(final LeafReaderContext context, final String field) throws IOException {
                        return SortedNumericSelector.wrap(DocValues.getSortedNumeric(context.reader(), field), SortedNumericSortField.this.selector, SortedNumericSortField.this.type);
                    }
                };
            }
            default: {
                throw new AssertionError();
            }
        }
    }
}
