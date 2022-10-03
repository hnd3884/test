package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import org.apache.lucene.util.mutable.MutableValue;
import org.apache.lucene.util.mutable.MutableValueFloat;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.util.Bits;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.queries.function.docvalues.FloatDocValues;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Map;
import org.apache.lucene.search.SortField;

public class FloatFieldSource extends FieldCacheSource
{
    public FloatFieldSource(final String field) {
        super(field);
    }
    
    @Override
    public String description() {
        return "float(" + this.field + ')';
    }
    
    @Override
    public SortField getSortField(final boolean reverse) {
        return new SortField(this.field, SortField.Type.FLOAT, reverse);
    }
    
    @Override
    public FunctionValues getValues(final Map context, final LeafReaderContext readerContext) throws IOException {
        final NumericDocValues arr = DocValues.getNumeric(readerContext.reader(), this.field);
        final Bits valid = DocValues.getDocsWithField(readerContext.reader(), this.field);
        return new FloatDocValues(this) {
            @Override
            public float floatVal(final int doc) {
                return Float.intBitsToFloat((int)arr.get(doc));
            }
            
            @Override
            public boolean exists(final int doc) {
                return arr.get(doc) != 0L || valid.get(doc);
            }
            
            @Override
            public ValueFiller getValueFiller() {
                return new ValueFiller() {
                    private final MutableValueFloat mval = new MutableValueFloat();
                    
                    @Override
                    public MutableValue getValue() {
                        return (MutableValue)this.mval;
                    }
                    
                    @Override
                    public void fillValue(final int doc) {
                        this.mval.value = FloatDocValues.this.floatVal(doc);
                        this.mval.exists = (this.mval.value != 0.0f || valid.get(doc));
                    }
                };
            }
        };
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o.getClass() != FloatFieldSource.class) {
            return false;
        }
        final FloatFieldSource other = (FloatFieldSource)o;
        return super.equals(other);
    }
    
    @Override
    public int hashCode() {
        int h = Float.class.hashCode();
        h += super.hashCode();
        return h;
    }
}
