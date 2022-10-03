package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.queries.function.docvalues.StrDocValues;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Map;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.queries.function.ValueSource;

public class LiteralValueSource extends ValueSource
{
    protected final String string;
    protected final BytesRef bytesRef;
    public static final int hash;
    
    public LiteralValueSource(final String string) {
        this.string = string;
        this.bytesRef = new BytesRef((CharSequence)string);
    }
    
    public String getValue() {
        return this.string;
    }
    
    @Override
    public FunctionValues getValues(final Map context, final LeafReaderContext readerContext) throws IOException {
        return new StrDocValues(this) {
            @Override
            public String strVal(final int doc) {
                return LiteralValueSource.this.string;
            }
            
            @Override
            public boolean bytesVal(final int doc, final BytesRefBuilder target) {
                target.copyBytes(LiteralValueSource.this.bytesRef);
                return true;
            }
            
            @Override
            public String toString(final int doc) {
                return LiteralValueSource.this.string;
            }
        };
    }
    
    @Override
    public String description() {
        return "literal(" + this.string + ")";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LiteralValueSource)) {
            return false;
        }
        final LiteralValueSource that = (LiteralValueSource)o;
        return this.string.equals(that.string);
    }
    
    @Override
    public int hashCode() {
        return LiteralValueSource.hash + this.string.hashCode();
    }
    
    static {
        hash = LiteralValueSource.class.hashCode();
    }
}
