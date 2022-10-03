package org.apache.lucene.index;

import java.util.Iterator;
import org.apache.lucene.util.Bits;
import java.util.HashMap;
import java.io.IOException;
import java.util.Map;

public final class SlowCompositeReaderWrapper extends LeafReader
{
    private final CompositeReader in;
    private final Fields fields;
    private final boolean merging;
    private final Map<String, MultiDocValues.OrdinalMap> cachedOrdMaps;
    
    public static LeafReader wrap(final IndexReader reader) throws IOException {
        if (reader instanceof CompositeReader) {
            return new SlowCompositeReaderWrapper((CompositeReader)reader, false);
        }
        assert reader instanceof LeafReader;
        return (LeafReader)reader;
    }
    
    SlowCompositeReaderWrapper(final CompositeReader reader, final boolean merging) throws IOException {
        this.cachedOrdMaps = new HashMap<String, MultiDocValues.OrdinalMap>();
        this.in = reader;
        this.fields = MultiFields.getFields(this.in);
        this.in.registerParentReader(this);
        this.merging = merging;
    }
    
    @Override
    public String toString() {
        return "SlowCompositeReaderWrapper(" + this.in + ")";
    }
    
    @Override
    public void addCoreClosedListener(final CoreClosedListener listener) {
        LeafReader.addCoreClosedListenerAsReaderClosedListener(this.in, listener);
    }
    
    @Override
    public void removeCoreClosedListener(final CoreClosedListener listener) {
        LeafReader.removeCoreClosedListenerAsReaderClosedListener(this.in, listener);
    }
    
    @Override
    public Fields fields() {
        this.ensureOpen();
        return this.fields;
    }
    
    @Override
    public NumericDocValues getNumericDocValues(final String field) throws IOException {
        this.ensureOpen();
        return MultiDocValues.getNumericValues(this.in, field);
    }
    
    @Override
    public Bits getDocsWithField(final String field) throws IOException {
        this.ensureOpen();
        return MultiDocValues.getDocsWithField(this.in, field);
    }
    
    @Override
    public BinaryDocValues getBinaryDocValues(final String field) throws IOException {
        this.ensureOpen();
        return MultiDocValues.getBinaryValues(this.in, field);
    }
    
    @Override
    public SortedNumericDocValues getSortedNumericDocValues(final String field) throws IOException {
        this.ensureOpen();
        return MultiDocValues.getSortedNumericValues(this.in, field);
    }
    
    @Override
    public SortedDocValues getSortedDocValues(final String field) throws IOException {
        this.ensureOpen();
        MultiDocValues.OrdinalMap map = null;
        synchronized (this.cachedOrdMaps) {
            map = this.cachedOrdMaps.get(field);
            if (map == null) {
                final SortedDocValues dv = MultiDocValues.getSortedValues(this.in, field);
                if (dv instanceof MultiDocValues.MultiSortedDocValues) {
                    map = ((MultiDocValues.MultiSortedDocValues)dv).mapping;
                    if (map.owner == this.getCoreCacheKey() && !this.merging) {
                        this.cachedOrdMaps.put(field, map);
                    }
                }
                return dv;
            }
        }
        final int size = this.in.leaves().size();
        final SortedDocValues[] values = new SortedDocValues[size];
        final int[] starts = new int[size + 1];
        for (int i = 0; i < size; ++i) {
            final LeafReaderContext context = this.in.leaves().get(i);
            final LeafReader reader = context.reader();
            final FieldInfo fieldInfo = reader.getFieldInfos().fieldInfo(field);
            if (fieldInfo != null && fieldInfo.getDocValuesType() != DocValuesType.SORTED) {
                return null;
            }
            SortedDocValues v = reader.getSortedDocValues(field);
            if (v == null) {
                v = DocValues.emptySorted();
            }
            values[i] = v;
            starts[i] = context.docBase;
        }
        starts[size] = this.maxDoc();
        return new MultiDocValues.MultiSortedDocValues(values, starts, map);
    }
    
    @Override
    public SortedSetDocValues getSortedSetDocValues(final String field) throws IOException {
        this.ensureOpen();
        MultiDocValues.OrdinalMap map = null;
        synchronized (this.cachedOrdMaps) {
            map = this.cachedOrdMaps.get(field);
            if (map == null) {
                final SortedSetDocValues dv = MultiDocValues.getSortedSetValues(this.in, field);
                if (dv instanceof MultiDocValues.MultiSortedSetDocValues) {
                    map = ((MultiDocValues.MultiSortedSetDocValues)dv).mapping;
                    if (map.owner == this.getCoreCacheKey() && !this.merging) {
                        this.cachedOrdMaps.put(field, map);
                    }
                }
                return dv;
            }
        }
        assert map != null;
        final int size = this.in.leaves().size();
        final SortedSetDocValues[] values = new SortedSetDocValues[size];
        final int[] starts = new int[size + 1];
        for (int i = 0; i < size; ++i) {
            final LeafReaderContext context = this.in.leaves().get(i);
            final LeafReader reader = context.reader();
            final FieldInfo fieldInfo = reader.getFieldInfos().fieldInfo(field);
            if (fieldInfo != null && fieldInfo.getDocValuesType() != DocValuesType.SORTED_SET) {
                return null;
            }
            SortedSetDocValues v = reader.getSortedSetDocValues(field);
            if (v == null) {
                v = DocValues.emptySortedSet();
            }
            values[i] = v;
            starts[i] = context.docBase;
        }
        starts[size] = this.maxDoc();
        return new MultiDocValues.MultiSortedSetDocValues(values, starts, map);
    }
    
    @Override
    public NumericDocValues getNormValues(final String field) throws IOException {
        this.ensureOpen();
        return MultiDocValues.getNormValues(this.in, field);
    }
    
    @Override
    public Fields getTermVectors(final int docID) throws IOException {
        this.ensureOpen();
        return this.in.getTermVectors(docID);
    }
    
    @Override
    public int numDocs() {
        return this.in.numDocs();
    }
    
    @Override
    public int maxDoc() {
        return this.in.maxDoc();
    }
    
    @Override
    public void document(final int docID, final StoredFieldVisitor visitor) throws IOException {
        this.ensureOpen();
        this.in.document(docID, visitor);
    }
    
    @Override
    public Bits getLiveDocs() {
        this.ensureOpen();
        return MultiFields.getLiveDocs(this.in);
    }
    
    @Override
    public FieldInfos getFieldInfos() {
        this.ensureOpen();
        return MultiFields.getMergedFieldInfos(this.in);
    }
    
    @Override
    public Object getCoreCacheKey() {
        return this.in.getCoreCacheKey();
    }
    
    @Override
    public Object getCombinedCoreAndDeletesKey() {
        return this.in.getCombinedCoreAndDeletesKey();
    }
    
    @Override
    protected void doClose() throws IOException {
        this.in.close();
    }
    
    @Override
    public void checkIntegrity() throws IOException {
        this.ensureOpen();
        for (final LeafReaderContext ctx : this.in.leaves()) {
            ctx.reader().checkIntegrity();
        }
    }
}
