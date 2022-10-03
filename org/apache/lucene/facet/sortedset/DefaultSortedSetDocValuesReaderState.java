package org.apache.lucene.facet.sortedset;

import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.SortedSetDocValues;
import java.util.Arrays;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.index.SlowCompositeReaderWrapper;
import java.util.HashMap;
import java.io.IOException;
import java.util.Map;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReader;

public class DefaultSortedSetDocValuesReaderState extends SortedSetDocValuesReaderState
{
    private final String field;
    private final LeafReader topReader;
    private final int valueCount;
    public final IndexReader origReader;
    private final Map<String, OrdRange> prefixToOrdRange;
    
    public DefaultSortedSetDocValuesReaderState(final IndexReader reader) throws IOException {
        this(reader, "$facets");
    }
    
    public DefaultSortedSetDocValuesReaderState(final IndexReader reader, final String field) throws IOException {
        this.prefixToOrdRange = new HashMap<String, OrdRange>();
        this.field = field;
        this.origReader = reader;
        this.topReader = SlowCompositeReaderWrapper.wrap(reader);
        final SortedSetDocValues dv = this.topReader.getSortedSetDocValues(field);
        if (dv == null) {
            throw new IllegalArgumentException("field \"" + field + "\" was not indexed with SortedSetDocValues");
        }
        if (dv.getValueCount() > 2147483647L) {
            throw new IllegalArgumentException("can only handle valueCount < Integer.MAX_VALUE; got " + dv.getValueCount());
        }
        this.valueCount = (int)dv.getValueCount();
        String lastDim = null;
        int startOrd = -1;
        for (int ord = 0; ord < this.valueCount; ++ord) {
            final BytesRef term = dv.lookupOrd((long)ord);
            final String[] components = FacetsConfig.stringToPath(term.utf8ToString());
            if (components.length != 2) {
                throw new IllegalArgumentException("this class can only handle 2 level hierarchy (dim/value); got: " + Arrays.toString(components) + " " + term.utf8ToString());
            }
            if (!components[0].equals(lastDim)) {
                if (lastDim != null) {
                    this.prefixToOrdRange.put(lastDim, new OrdRange(startOrd, ord - 1));
                }
                startOrd = ord;
                lastDim = components[0];
            }
        }
        if (lastDim != null) {
            this.prefixToOrdRange.put(lastDim, new OrdRange(startOrd, this.valueCount - 1));
        }
    }
    
    @Override
    public SortedSetDocValues getDocValues() throws IOException {
        return this.topReader.getSortedSetDocValues(this.field);
    }
    
    @Override
    public Map<String, OrdRange> getPrefixToOrdRange() {
        return this.prefixToOrdRange;
    }
    
    @Override
    public OrdRange getOrdRange(final String dim) {
        return this.prefixToOrdRange.get(dim);
    }
    
    @Override
    public String getField() {
        return this.field;
    }
    
    @Override
    public IndexReader getOrigReader() {
        return this.origReader;
    }
    
    @Override
    public int getSize() {
        return this.valueCount;
    }
}
