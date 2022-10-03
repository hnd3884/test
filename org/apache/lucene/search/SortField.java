package org.apache.lucene.search;

import java.io.IOException;
import java.util.Objects;
import org.apache.lucene.util.BytesRef;
import java.util.Comparator;

public class SortField
{
    public static final SortField FIELD_SCORE;
    public static final SortField FIELD_DOC;
    private String field;
    private Type type;
    boolean reverse;
    private FieldComparatorSource comparatorSource;
    public Object missingValue;
    public static final Object STRING_FIRST;
    public static final Object STRING_LAST;
    private Comparator<BytesRef> bytesComparator;
    
    public SortField(final String field, final Type type) {
        this.reverse = false;
        this.missingValue = null;
        this.bytesComparator = BytesRef.getUTF8SortedAsUnicodeComparator();
        this.initFieldType(field, type);
    }
    
    public SortField(final String field, final Type type, final boolean reverse) {
        this.reverse = false;
        this.missingValue = null;
        this.bytesComparator = BytesRef.getUTF8SortedAsUnicodeComparator();
        this.initFieldType(field, type);
        this.reverse = reverse;
    }
    
    public void setMissingValue(final Object missingValue) {
        if (this.type == Type.STRING || this.type == Type.STRING_VAL) {
            if (missingValue != SortField.STRING_FIRST && missingValue != SortField.STRING_LAST) {
                throw new IllegalArgumentException("For STRING type, missing value must be either STRING_FIRST or STRING_LAST");
            }
        }
        else if (this.type != Type.INT && this.type != Type.FLOAT && this.type != Type.LONG && this.type != Type.DOUBLE) {
            throw new IllegalArgumentException("Missing value only works for numeric or STRING types");
        }
        this.missingValue = missingValue;
    }
    
    public SortField(final String field, final FieldComparatorSource comparator) {
        this.reverse = false;
        this.missingValue = null;
        this.bytesComparator = BytesRef.getUTF8SortedAsUnicodeComparator();
        this.initFieldType(field, Type.CUSTOM);
        this.comparatorSource = comparator;
    }
    
    public SortField(final String field, final FieldComparatorSource comparator, final boolean reverse) {
        this.reverse = false;
        this.missingValue = null;
        this.bytesComparator = BytesRef.getUTF8SortedAsUnicodeComparator();
        this.initFieldType(field, Type.CUSTOM);
        this.reverse = reverse;
        this.comparatorSource = comparator;
    }
    
    private void initFieldType(final String field, final Type type) {
        this.type = type;
        if (field == null) {
            if (type != Type.SCORE && type != Type.DOC) {
                throw new IllegalArgumentException("field can only be null when type is SCORE or DOC");
            }
        }
        else {
            this.field = field;
        }
    }
    
    public String getField() {
        return this.field;
    }
    
    public Type getType() {
        return this.type;
    }
    
    public boolean getReverse() {
        return this.reverse;
    }
    
    public FieldComparatorSource getComparatorSource() {
        return this.comparatorSource;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        switch (this.type) {
            case SCORE: {
                buffer.append("<score>");
                break;
            }
            case DOC: {
                buffer.append("<doc>");
                break;
            }
            case STRING: {
                buffer.append("<string: \"").append(this.field).append("\">");
                break;
            }
            case STRING_VAL: {
                buffer.append("<string_val: \"").append(this.field).append("\">");
                break;
            }
            case INT: {
                buffer.append("<int: \"").append(this.field).append("\">");
                break;
            }
            case LONG: {
                buffer.append("<long: \"").append(this.field).append("\">");
                break;
            }
            case FLOAT: {
                buffer.append("<float: \"").append(this.field).append("\">");
                break;
            }
            case DOUBLE: {
                buffer.append("<double: \"").append(this.field).append("\">");
                break;
            }
            case CUSTOM: {
                buffer.append("<custom:\"").append(this.field).append("\": ").append(this.comparatorSource).append('>');
                break;
            }
            case REWRITEABLE: {
                buffer.append("<rewriteable: \"").append(this.field).append("\">");
                break;
            }
            default: {
                buffer.append("<???: \"").append(this.field).append("\">");
                break;
            }
        }
        if (this.reverse) {
            buffer.append('!');
        }
        if (this.missingValue != null) {
            buffer.append(" missingValue=");
            buffer.append(this.missingValue);
        }
        return buffer.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SortField)) {
            return false;
        }
        final SortField other = (SortField)o;
        return Objects.equals(other.field, this.field) && other.type == this.type && other.reverse == this.reverse && Objects.equals(this.comparatorSource, other.comparatorSource) && Objects.equals(this.missingValue, other.missingValue);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.field, this.type, this.reverse, this.comparatorSource, this.missingValue);
    }
    
    public void setBytesComparator(final Comparator<BytesRef> b) {
        this.bytesComparator = b;
    }
    
    public Comparator<BytesRef> getBytesComparator() {
        return this.bytesComparator;
    }
    
    public FieldComparator<?> getComparator(final int numHits, final int sortPos) throws IOException {
        switch (this.type) {
            case SCORE: {
                return new FieldComparator.RelevanceComparator(numHits);
            }
            case DOC: {
                return new FieldComparator.DocComparator(numHits);
            }
            case INT: {
                return new FieldComparator.IntComparator(numHits, this.field, (Integer)this.missingValue);
            }
            case FLOAT: {
                return new FieldComparator.FloatComparator(numHits, this.field, (Float)this.missingValue);
            }
            case LONG: {
                return new FieldComparator.LongComparator(numHits, this.field, (Long)this.missingValue);
            }
            case DOUBLE: {
                return new FieldComparator.DoubleComparator(numHits, this.field, (Double)this.missingValue);
            }
            case CUSTOM: {
                assert this.comparatorSource != null;
                return this.comparatorSource.newComparator(this.field, numHits, sortPos, this.reverse);
            }
            case STRING: {
                return new FieldComparator.TermOrdValComparator(numHits, this.field, this.missingValue == SortField.STRING_LAST);
            }
            case STRING_VAL: {
                return new FieldComparator.TermValComparator(numHits, this.field, this.missingValue == SortField.STRING_LAST);
            }
            case REWRITEABLE: {
                throw new IllegalStateException("SortField needs to be rewritten through Sort.rewrite(..) and SortField.rewrite(..)");
            }
            default: {
                throw new IllegalStateException("Illegal sort type: " + this.type);
            }
        }
    }
    
    public SortField rewrite(final IndexSearcher searcher) throws IOException {
        return this;
    }
    
    public boolean needsScores() {
        return this.type == Type.SCORE;
    }
    
    static {
        FIELD_SCORE = new SortField(null, Type.SCORE);
        FIELD_DOC = new SortField(null, Type.DOC);
        STRING_FIRST = new Object() {
            @Override
            public String toString() {
                return "SortField.STRING_FIRST";
            }
        };
        STRING_LAST = new Object() {
            @Override
            public String toString() {
                return "SortField.STRING_LAST";
            }
        };
    }
    
    public enum Type
    {
        SCORE, 
        DOC, 
        STRING, 
        INT, 
        FLOAT, 
        LONG, 
        DOUBLE, 
        CUSTOM, 
        STRING_VAL, 
        BYTES, 
        REWRITEABLE;
    }
}
