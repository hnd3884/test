package org.apache.lucene.queries.function;

import org.apache.lucene.search.SimpleFieldComparator;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.FieldComparatorSource;
import org.apache.lucene.search.SortField;
import java.util.IdentityHashMap;
import org.apache.lucene.search.IndexSearcher;
import java.io.IOException;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Map;

public abstract class ValueSource
{
    public abstract FunctionValues getValues(final Map p0, final LeafReaderContext p1) throws IOException;
    
    @Override
    public abstract boolean equals(final Object p0);
    
    @Override
    public abstract int hashCode();
    
    public abstract String description();
    
    @Override
    public String toString() {
        return this.description();
    }
    
    public void createWeight(final Map context, final IndexSearcher searcher) throws IOException {
    }
    
    public static Map newContext(final IndexSearcher searcher) {
        final Map context = new IdentityHashMap();
        context.put("searcher", searcher);
        return context;
    }
    
    public SortField getSortField(final boolean reverse) {
        return new ValueSourceSortField(reverse);
    }
    
    class ValueSourceSortField extends SortField
    {
        public ValueSourceSortField(final boolean reverse) {
            super(ValueSource.this.description(), SortField.Type.REWRITEABLE, reverse);
        }
        
        public SortField rewrite(final IndexSearcher searcher) throws IOException {
            final Map context = ValueSource.newContext(searcher);
            ValueSource.this.createWeight(context, searcher);
            return new SortField(this.getField(), (FieldComparatorSource)new ValueSourceComparatorSource(context), this.getReverse());
        }
    }
    
    class ValueSourceComparatorSource extends FieldComparatorSource
    {
        private final Map context;
        
        public ValueSourceComparatorSource(final Map context) {
            this.context = context;
        }
        
        public FieldComparator<Double> newComparator(final String fieldname, final int numHits, final int sortPos, final boolean reversed) throws IOException {
            return (FieldComparator<Double>)new ValueSourceComparator(this.context, numHits);
        }
    }
    
    class ValueSourceComparator extends SimpleFieldComparator<Double>
    {
        private final double[] values;
        private FunctionValues docVals;
        private double bottom;
        private final Map fcontext;
        private double topValue;
        
        ValueSourceComparator(final Map fcontext, final int numHits) {
            this.fcontext = fcontext;
            this.values = new double[numHits];
        }
        
        public int compare(final int slot1, final int slot2) {
            return Double.compare(this.values[slot1], this.values[slot2]);
        }
        
        public int compareBottom(final int doc) {
            return Double.compare(this.bottom, this.docVals.doubleVal(doc));
        }
        
        public void copy(final int slot, final int doc) {
            this.values[slot] = this.docVals.doubleVal(doc);
        }
        
        public void doSetNextReader(final LeafReaderContext context) throws IOException {
            this.docVals = ValueSource.this.getValues(this.fcontext, context);
        }
        
        public void setBottom(final int bottom) {
            this.bottom = this.values[bottom];
        }
        
        public void setTopValue(final Double value) {
            this.topValue = value;
        }
        
        public Double value(final int slot) {
            return this.values[slot];
        }
        
        public int compareTop(final int doc) {
            final double docValue = this.docVals.doubleVal(doc);
            return Double.compare(this.topValue, docValue);
        }
    }
}
