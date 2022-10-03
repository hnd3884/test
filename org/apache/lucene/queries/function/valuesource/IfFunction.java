package org.apache.lucene.queries.function.valuesource;

import org.apache.lucene.search.IndexSearcher;
import java.io.IOException;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Map;
import org.apache.lucene.queries.function.ValueSource;

public class IfFunction extends BoolFunction
{
    private final ValueSource ifSource;
    private final ValueSource trueSource;
    private final ValueSource falseSource;
    
    public IfFunction(final ValueSource ifSource, final ValueSource trueSource, final ValueSource falseSource) {
        this.ifSource = ifSource;
        this.trueSource = trueSource;
        this.falseSource = falseSource;
    }
    
    @Override
    public FunctionValues getValues(final Map context, final LeafReaderContext readerContext) throws IOException {
        final FunctionValues ifVals = this.ifSource.getValues(context, readerContext);
        final FunctionValues trueVals = this.trueSource.getValues(context, readerContext);
        final FunctionValues falseVals = this.falseSource.getValues(context, readerContext);
        return new FunctionValues() {
            @Override
            public byte byteVal(final int doc) {
                return ifVals.boolVal(doc) ? trueVals.byteVal(doc) : falseVals.byteVal(doc);
            }
            
            @Override
            public short shortVal(final int doc) {
                return ifVals.boolVal(doc) ? trueVals.shortVal(doc) : falseVals.shortVal(doc);
            }
            
            @Override
            public float floatVal(final int doc) {
                return ifVals.boolVal(doc) ? trueVals.floatVal(doc) : falseVals.floatVal(doc);
            }
            
            @Override
            public int intVal(final int doc) {
                return ifVals.boolVal(doc) ? trueVals.intVal(doc) : falseVals.intVal(doc);
            }
            
            @Override
            public long longVal(final int doc) {
                return ifVals.boolVal(doc) ? trueVals.longVal(doc) : falseVals.longVal(doc);
            }
            
            @Override
            public double doubleVal(final int doc) {
                return ifVals.boolVal(doc) ? trueVals.doubleVal(doc) : falseVals.doubleVal(doc);
            }
            
            @Override
            public String strVal(final int doc) {
                return ifVals.boolVal(doc) ? trueVals.strVal(doc) : falseVals.strVal(doc);
            }
            
            @Override
            public boolean boolVal(final int doc) {
                return ifVals.boolVal(doc) ? trueVals.boolVal(doc) : falseVals.boolVal(doc);
            }
            
            @Override
            public boolean bytesVal(final int doc, final BytesRefBuilder target) {
                return ifVals.boolVal(doc) ? trueVals.bytesVal(doc, target) : falseVals.bytesVal(doc, target);
            }
            
            @Override
            public Object objectVal(final int doc) {
                return ifVals.boolVal(doc) ? trueVals.objectVal(doc) : falseVals.objectVal(doc);
            }
            
            @Override
            public boolean exists(final int doc) {
                return ifVals.boolVal(doc) ? trueVals.exists(doc) : falseVals.exists(doc);
            }
            
            @Override
            public ValueFiller getValueFiller() {
                return super.getValueFiller();
            }
            
            @Override
            public String toString(final int doc) {
                return "if(" + ifVals.toString(doc) + ',' + trueVals.toString(doc) + ',' + falseVals.toString(doc) + ')';
            }
        };
    }
    
    @Override
    public String description() {
        return "if(" + this.ifSource.description() + ',' + this.trueSource.description() + ',' + this.falseSource + ')';
    }
    
    @Override
    public int hashCode() {
        int h = this.ifSource.hashCode();
        h = h * 31 + this.trueSource.hashCode();
        h = h * 31 + this.falseSource.hashCode();
        return h;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof IfFunction)) {
            return false;
        }
        final IfFunction other = (IfFunction)o;
        return this.ifSource.equals(other.ifSource) && this.trueSource.equals(other.trueSource) && this.falseSource.equals(other.falseSource);
    }
    
    @Override
    public void createWeight(final Map context, final IndexSearcher searcher) throws IOException {
        this.ifSource.createWeight(context, searcher);
        this.trueSource.createWeight(context, searcher);
        this.falseSource.createWeight(context, searcher);
    }
}
