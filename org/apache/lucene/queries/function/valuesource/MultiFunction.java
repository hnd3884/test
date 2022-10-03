package org.apache.lucene.queries.function.valuesource;

import org.apache.lucene.search.IndexSearcher;
import java.io.IOException;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Map;
import java.util.Iterator;
import org.apache.lucene.queries.function.FunctionValues;
import java.util.List;
import org.apache.lucene.queries.function.ValueSource;

public abstract class MultiFunction extends ValueSource
{
    protected final List<ValueSource> sources;
    
    public MultiFunction(final List<ValueSource> sources) {
        this.sources = sources;
    }
    
    protected abstract String name();
    
    @Override
    public String description() {
        return description(this.name(), this.sources);
    }
    
    public static boolean allExists(final int doc, final FunctionValues[] values) {
        for (final FunctionValues v : values) {
            if (!v.exists(doc)) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean anyExists(final int doc, final FunctionValues[] values) {
        for (final FunctionValues v : values) {
            if (v.exists(doc)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean allExists(final int doc, final FunctionValues values1, final FunctionValues values2) {
        return values1.exists(doc) && values2.exists(doc);
    }
    
    public static boolean anyExists(final int doc, final FunctionValues values1, final FunctionValues values2) {
        return values1.exists(doc) || values2.exists(doc);
    }
    
    public static String description(final String name, final List<ValueSource> sources) {
        final StringBuilder sb = new StringBuilder();
        sb.append(name).append('(');
        boolean firstTime = true;
        for (final ValueSource source : sources) {
            if (firstTime) {
                firstTime = false;
            }
            else {
                sb.append(',');
            }
            sb.append(source);
        }
        sb.append(')');
        return sb.toString();
    }
    
    public static FunctionValues[] valsArr(final List<ValueSource> sources, final Map fcontext, final LeafReaderContext readerContext) throws IOException {
        final FunctionValues[] valsArr = new FunctionValues[sources.size()];
        int i = 0;
        for (final ValueSource source : sources) {
            valsArr[i++] = source.getValues(fcontext, readerContext);
        }
        return valsArr;
    }
    
    public static String toString(final String name, final FunctionValues[] valsArr, final int doc) {
        final StringBuilder sb = new StringBuilder();
        sb.append(name).append('(');
        boolean firstTime = true;
        for (final FunctionValues vals : valsArr) {
            if (firstTime) {
                firstTime = false;
            }
            else {
                sb.append(',');
            }
            sb.append(vals.toString(doc));
        }
        sb.append(')');
        return sb.toString();
    }
    
    @Override
    public void createWeight(final Map context, final IndexSearcher searcher) throws IOException {
        for (final ValueSource source : this.sources) {
            source.createWeight(context, searcher);
        }
    }
    
    @Override
    public int hashCode() {
        return this.sources.hashCode() + this.name().hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this.getClass() != o.getClass()) {
            return false;
        }
        final MultiFunction other = (MultiFunction)o;
        return this.sources.equals(other.sources);
    }
    
    public class Values extends FunctionValues
    {
        final FunctionValues[] valsArr;
        
        public Values(final FunctionValues[] valsArr) {
            this.valsArr = valsArr;
        }
        
        @Override
        public String toString(final int doc) {
            return MultiFunction.toString(MultiFunction.this.name(), this.valsArr, doc);
        }
        
        @Override
        public ValueFiller getValueFiller() {
            return super.getValueFiller();
        }
    }
}
