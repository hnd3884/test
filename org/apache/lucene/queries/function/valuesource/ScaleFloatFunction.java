package org.apache.lucene.queries.function.valuesource;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.queries.function.docvalues.FloatDocValues;
import java.io.IOException;
import org.apache.lucene.queries.function.FunctionValues;
import java.util.Iterator;
import java.util.List;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.ReaderUtil;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Map;
import org.apache.lucene.queries.function.ValueSource;

public class ScaleFloatFunction extends ValueSource
{
    protected final ValueSource source;
    protected final float min;
    protected final float max;
    
    public ScaleFloatFunction(final ValueSource source, final float min, final float max) {
        this.source = source;
        this.min = min;
        this.max = max;
    }
    
    @Override
    public String description() {
        return "scale(" + this.source.description() + "," + this.min + "," + this.max + ")";
    }
    
    private ScaleInfo createScaleInfo(final Map context, final LeafReaderContext readerContext) throws IOException {
        final List<LeafReaderContext> leaves = ReaderUtil.getTopLevelContext((IndexReaderContext)readerContext).leaves();
        float minVal = Float.POSITIVE_INFINITY;
        float maxVal = Float.NEGATIVE_INFINITY;
        for (final LeafReaderContext leaf : leaves) {
            final int maxDoc = leaf.reader().maxDoc();
            final FunctionValues vals = this.source.getValues(context, leaf);
            for (int i = 0; i < maxDoc; ++i) {
                if (vals.exists(i)) {
                    final float val = vals.floatVal(i);
                    if ((Float.floatToRawIntBits(val) & 0x7F800000) != 0x7F800000) {
                        if (val < minVal) {
                            minVal = val;
                        }
                        if (val > maxVal) {
                            maxVal = val;
                        }
                    }
                }
            }
        }
        if (minVal == Float.POSITIVE_INFINITY) {
            maxVal = (minVal = 0.0f);
        }
        final ScaleInfo scaleInfo = new ScaleInfo();
        scaleInfo.minVal = minVal;
        scaleInfo.maxVal = maxVal;
        context.put(this, scaleInfo);
        return scaleInfo;
    }
    
    @Override
    public FunctionValues getValues(final Map context, final LeafReaderContext readerContext) throws IOException {
        ScaleInfo scaleInfo = context.get(this);
        if (scaleInfo == null) {
            scaleInfo = this.createScaleInfo(context, readerContext);
        }
        final float scale = (scaleInfo.maxVal - scaleInfo.minVal == 0.0f) ? 0.0f : ((this.max - this.min) / (scaleInfo.maxVal - scaleInfo.minVal));
        final float minSource = scaleInfo.minVal;
        final float maxSource = scaleInfo.maxVal;
        final FunctionValues vals = this.source.getValues(context, readerContext);
        return new FloatDocValues(this) {
            @Override
            public boolean exists(final int doc) {
                return vals.exists(doc);
            }
            
            @Override
            public float floatVal(final int doc) {
                return (vals.floatVal(doc) - minSource) * scale + ScaleFloatFunction.this.min;
            }
            
            @Override
            public String toString(final int doc) {
                return "scale(" + vals.toString(doc) + ",toMin=" + ScaleFloatFunction.this.min + ",toMax=" + ScaleFloatFunction.this.max + ",fromMin=" + minSource + ",fromMax=" + maxSource + ")";
            }
        };
    }
    
    @Override
    public void createWeight(final Map context, final IndexSearcher searcher) throws IOException {
        this.source.createWeight(context, searcher);
    }
    
    @Override
    public int hashCode() {
        int h = Float.floatToIntBits(this.min);
        h *= 29;
        h += Float.floatToIntBits(this.max);
        h *= 29;
        h += this.source.hashCode();
        return h;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (ScaleFloatFunction.class != o.getClass()) {
            return false;
        }
        final ScaleFloatFunction other = (ScaleFloatFunction)o;
        return this.min == other.min && this.max == other.max && this.source.equals(other.source);
    }
    
    private static class ScaleInfo
    {
        float minVal;
        float maxVal;
    }
}
