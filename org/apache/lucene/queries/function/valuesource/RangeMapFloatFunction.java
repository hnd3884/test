package org.apache.lucene.queries.function.valuesource;

import org.apache.lucene.search.IndexSearcher;
import java.io.IOException;
import org.apache.lucene.queries.function.docvalues.FloatDocValues;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Map;
import org.apache.lucene.queries.function.ValueSource;

public class RangeMapFloatFunction extends ValueSource
{
    protected final ValueSource source;
    protected final float min;
    protected final float max;
    protected final ValueSource target;
    protected final ValueSource defaultVal;
    
    public RangeMapFloatFunction(final ValueSource source, final float min, final float max, final float target, final Float def) {
        this(source, min, max, new ConstValueSource(target), (def == null) ? null : new ConstValueSource(def));
    }
    
    public RangeMapFloatFunction(final ValueSource source, final float min, final float max, final ValueSource target, final ValueSource def) {
        this.source = source;
        this.min = min;
        this.max = max;
        this.target = target;
        this.defaultVal = def;
    }
    
    @Override
    public String description() {
        return "map(" + this.source.description() + "," + this.min + "," + this.max + "," + this.target.description() + "," + ((this.defaultVal == null) ? "null" : this.defaultVal.description()) + ")";
    }
    
    @Override
    public FunctionValues getValues(final Map context, final LeafReaderContext readerContext) throws IOException {
        final FunctionValues vals = this.source.getValues(context, readerContext);
        final FunctionValues targets = this.target.getValues(context, readerContext);
        final FunctionValues defaults = (this.defaultVal == null) ? null : this.defaultVal.getValues(context, readerContext);
        return new FloatDocValues(this) {
            @Override
            public float floatVal(final int doc) {
                final float val = vals.floatVal(doc);
                return (val >= RangeMapFloatFunction.this.min && val <= RangeMapFloatFunction.this.max) ? targets.floatVal(doc) : ((RangeMapFloatFunction.this.defaultVal == null) ? val : defaults.floatVal(doc));
            }
            
            @Override
            public String toString(final int doc) {
                return "map(" + vals.toString(doc) + ",min=" + RangeMapFloatFunction.this.min + ",max=" + RangeMapFloatFunction.this.max + ",target=" + targets.toString(doc) + ",defaultVal=" + ((defaults == null) ? "null" : defaults.toString(doc)) + ")";
            }
        };
    }
    
    @Override
    public void createWeight(final Map context, final IndexSearcher searcher) throws IOException {
        this.source.createWeight(context, searcher);
    }
    
    @Override
    public int hashCode() {
        int h = this.source.hashCode();
        h ^= (h << 10 | h >>> 23);
        h += Float.floatToIntBits(this.min);
        h ^= (h << 14 | h >>> 19);
        h += Float.floatToIntBits(this.max);
        h += this.target.hashCode();
        if (this.defaultVal != null) {
            h += this.defaultVal.hashCode();
        }
        return h;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (RangeMapFloatFunction.class != o.getClass()) {
            return false;
        }
        final RangeMapFloatFunction other = (RangeMapFloatFunction)o;
        return this.min == other.min && this.max == other.max && this.target.equals(other.target) && this.source.equals(other.source) && (this.defaultVal == other.defaultVal || (this.defaultVal != null && this.defaultVal.equals(other.defaultVal)));
    }
}
