package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import org.apache.lucene.util.mutable.MutableValue;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.util.Bits;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.util.mutable.MutableValueInt;
import org.apache.lucene.queries.function.docvalues.IntDocValues;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Map;
import org.apache.lucene.search.SortField;

public class IntFieldSource extends FieldCacheSource
{
    public IntFieldSource(final String field) {
        super(field);
    }
    
    @Override
    public String description() {
        return "int(" + this.field + ')';
    }
    
    @Override
    public SortField getSortField(final boolean reverse) {
        return new SortField(this.field, SortField.Type.INT, reverse);
    }
    
    @Override
    public FunctionValues getValues(final Map context, final LeafReaderContext readerContext) throws IOException {
        final NumericDocValues arr = DocValues.getNumeric(readerContext.reader(), this.field);
        final Bits valid = DocValues.getDocsWithField(readerContext.reader(), this.field);
        return new IntDocValues(this) {
            final MutableValueInt val = new MutableValueInt();
            
            @Override
            public int intVal(final int doc) {
                return (int)arr.get(doc);
            }
            
            @Override
            public String strVal(final int doc) {
                return Integer.toString(this.intVal(doc));
            }
            
            @Override
            public boolean exists(final int doc) {
                return arr.get(doc) != 0L || valid.get(doc);
            }
            
            @Override
            public ValueFiller getValueFiller() {
                return new ValueFiller() {
                    private final MutableValueInt mval = new MutableValueInt();
                    
                    @Override
                    public MutableValue getValue() {
                        return (MutableValue)this.mval;
                    }
                    
                    @Override
                    public void fillValue(final int doc) {
                        this.mval.value = IntDocValues.this.intVal(doc);
                        this.mval.exists = (this.mval.value != 0 || valid.get(doc));
                    }
                };
            }
        };
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o.getClass() != IntFieldSource.class) {
            return false;
        }
        final IntFieldSource other = (IntFieldSource)o;
        return super.equals(other);
    }
    
    @Override
    public int hashCode() {
        int h = Integer.class.hashCode();
        h += super.hashCode();
        return h;
    }
}
