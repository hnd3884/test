package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Map;
import org.apache.lucene.queries.function.ValueSource;
import java.util.List;

public class DefFunction extends MultiFunction
{
    public DefFunction(final List<ValueSource> sources) {
        super(sources);
    }
    
    @Override
    protected String name() {
        return "def";
    }
    
    @Override
    public FunctionValues getValues(final Map fcontext, final LeafReaderContext readerContext) throws IOException {
        return new Values(MultiFunction.valsArr(this.sources, fcontext, readerContext)) {
            final int upto = this.valsArr.length - 1;
            
            private FunctionValues get(final int doc) {
                for (int i = 0; i < this.upto; ++i) {
                    final FunctionValues vals = this.valsArr[i];
                    if (vals.exists(doc)) {
                        return vals;
                    }
                }
                return this.valsArr[this.upto];
            }
            
            @Override
            public byte byteVal(final int doc) {
                return this.get(doc).byteVal(doc);
            }
            
            @Override
            public short shortVal(final int doc) {
                return this.get(doc).shortVal(doc);
            }
            
            @Override
            public float floatVal(final int doc) {
                return this.get(doc).floatVal(doc);
            }
            
            @Override
            public int intVal(final int doc) {
                return this.get(doc).intVal(doc);
            }
            
            @Override
            public long longVal(final int doc) {
                return this.get(doc).longVal(doc);
            }
            
            @Override
            public double doubleVal(final int doc) {
                return this.get(doc).doubleVal(doc);
            }
            
            @Override
            public String strVal(final int doc) {
                return this.get(doc).strVal(doc);
            }
            
            @Override
            public boolean boolVal(final int doc) {
                return this.get(doc).boolVal(doc);
            }
            
            @Override
            public boolean bytesVal(final int doc, final BytesRefBuilder target) {
                return this.get(doc).bytesVal(doc, target);
            }
            
            @Override
            public Object objectVal(final int doc) {
                return this.get(doc).objectVal(doc);
            }
            
            @Override
            public boolean exists(final int doc) {
                for (final FunctionValues vals : this.valsArr) {
                    if (vals.exists(doc)) {
                        return true;
                    }
                }
                return false;
            }
            
            @Override
            public ValueFiller getValueFiller() {
                return super.getValueFiller();
            }
        };
    }
}
