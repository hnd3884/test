package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import org.apache.lucene.util.mutable.MutableValue;
import org.apache.lucene.util.mutable.MutableValueLong;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.util.Bits;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.queries.function.docvalues.LongDocValues;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Map;
import org.apache.lucene.search.SortField;

public class LongFieldSource extends FieldCacheSource
{
    public LongFieldSource(final String field) {
        super(field);
    }
    
    @Override
    public String description() {
        return "long(" + this.field + ')';
    }
    
    public long externalToLong(final String extVal) {
        return Long.parseLong(extVal);
    }
    
    public Object longToObject(final long val) {
        return val;
    }
    
    public String longToString(final long val) {
        return this.longToObject(val).toString();
    }
    
    @Override
    public SortField getSortField(final boolean reverse) {
        return new SortField(this.field, SortField.Type.LONG, reverse);
    }
    
    @Override
    public FunctionValues getValues(final Map context, final LeafReaderContext readerContext) throws IOException {
        final NumericDocValues arr = DocValues.getNumeric(readerContext.reader(), this.field);
        final Bits valid = DocValues.getDocsWithField(readerContext.reader(), this.field);
        return new LongDocValues(this) {
            @Override
            public long longVal(final int doc) {
                return arr.get(doc);
            }
            
            @Override
            public boolean exists(final int doc) {
                return arr.get(doc) != 0L || valid.get(doc);
            }
            
            @Override
            public Object objectVal(final int doc) {
                return valid.get(doc) ? LongFieldSource.this.longToObject(arr.get(doc)) : null;
            }
            
            @Override
            public String strVal(final int doc) {
                return valid.get(doc) ? LongFieldSource.this.longToString(arr.get(doc)) : null;
            }
            
            @Override
            protected long externalToLong(final String extVal) {
                return LongFieldSource.this.externalToLong(extVal);
            }
            
            @Override
            public ValueFiller getValueFiller() {
                return new ValueFiller() {
                    private final MutableValueLong mval = LongFieldSource.this.newMutableValueLong();
                    
                    @Override
                    public MutableValue getValue() {
                        return (MutableValue)this.mval;
                    }
                    
                    @Override
                    public void fillValue(final int doc) {
                        this.mval.value = arr.get(doc);
                        this.mval.exists = (this.mval.value != 0L || valid.get(doc));
                    }
                };
            }
        };
    }
    
    protected MutableValueLong newMutableValueLong() {
        return new MutableValueLong();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o.getClass() != this.getClass()) {
            return false;
        }
        final LongFieldSource other = (LongFieldSource)o;
        return super.equals(other);
    }
    
    @Override
    public int hashCode() {
        int h = this.getClass().hashCode();
        h += super.hashCode();
        return h;
    }
}
