package org.apache.lucene.index;

import java.util.List;
import java.util.Iterator;
import org.apache.lucene.util.RamUsageEstimator;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Map;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.search.Query;

class FrozenBufferedUpdates
{
    static final int BYTES_PER_DEL_QUERY;
    final PrefixCodedTerms terms;
    final Query[] queries;
    final int[] queryLimits;
    final DocValuesUpdate.NumericDocValuesUpdate[] numericDVUpdates;
    final DocValuesUpdate.BinaryDocValuesUpdate[] binaryDVUpdates;
    final int bytesUsed;
    final int numTermDeletes;
    private long gen;
    final boolean isSegmentPrivate;
    
    public FrozenBufferedUpdates(final BufferedUpdates deletes, final boolean isSegmentPrivate) {
        this.gen = -1L;
        this.isSegmentPrivate = isSegmentPrivate;
        assert deletes.terms.size() == 0 : "segment private package should only have del queries";
        final Term[] termsArray = deletes.terms.keySet().toArray(new Term[deletes.terms.size()]);
        ArrayUtil.timSort(termsArray);
        final PrefixCodedTerms.Builder builder = new PrefixCodedTerms.Builder();
        for (final Term term : termsArray) {
            builder.add(term);
        }
        this.terms = builder.finish();
        this.queries = new Query[deletes.queries.size()];
        this.queryLimits = new int[deletes.queries.size()];
        int upto = 0;
        for (final Map.Entry<Query, Integer> ent : deletes.queries.entrySet()) {
            this.queries[upto] = ent.getKey();
            this.queryLimits[upto] = ent.getValue();
            ++upto;
        }
        final List<DocValuesUpdate.NumericDocValuesUpdate> allNumericUpdates = new ArrayList<DocValuesUpdate.NumericDocValuesUpdate>();
        int numericUpdatesSize = 0;
        for (final LinkedHashMap<Term, DocValuesUpdate.NumericDocValuesUpdate> numericUpdates : deletes.numericUpdates.values()) {
            for (final DocValuesUpdate.NumericDocValuesUpdate update : numericUpdates.values()) {
                allNumericUpdates.add(update);
                numericUpdatesSize += update.sizeInBytes();
            }
        }
        this.numericDVUpdates = allNumericUpdates.toArray(new DocValuesUpdate.NumericDocValuesUpdate[allNumericUpdates.size()]);
        final List<DocValuesUpdate.BinaryDocValuesUpdate> allBinaryUpdates = new ArrayList<DocValuesUpdate.BinaryDocValuesUpdate>();
        int binaryUpdatesSize = 0;
        for (final LinkedHashMap<Term, DocValuesUpdate.BinaryDocValuesUpdate> binaryUpdates : deletes.binaryUpdates.values()) {
            for (final DocValuesUpdate.BinaryDocValuesUpdate update2 : binaryUpdates.values()) {
                allBinaryUpdates.add(update2);
                binaryUpdatesSize += update2.sizeInBytes();
            }
        }
        this.binaryDVUpdates = allBinaryUpdates.toArray(new DocValuesUpdate.BinaryDocValuesUpdate[allBinaryUpdates.size()]);
        this.bytesUsed = (int)(this.terms.ramBytesUsed() + this.queries.length * FrozenBufferedUpdates.BYTES_PER_DEL_QUERY + numericUpdatesSize + RamUsageEstimator.shallowSizeOf(this.numericDVUpdates) + binaryUpdatesSize + RamUsageEstimator.shallowSizeOf(this.binaryDVUpdates));
        this.numTermDeletes = deletes.numTermDeletes.get();
    }
    
    public void setDelGen(final long gen) {
        assert this.gen == -1L;
        this.gen = gen;
        this.terms.setDelGen(gen);
    }
    
    public long delGen() {
        assert this.gen != -1L;
        return this.gen;
    }
    
    public PrefixCodedTerms.TermIterator termIterator() {
        return this.terms.iterator();
    }
    
    public Iterable<BufferedUpdatesStream.QueryAndLimit> queriesIterable() {
        return new Iterable<BufferedUpdatesStream.QueryAndLimit>() {
            @Override
            public Iterator<BufferedUpdatesStream.QueryAndLimit> iterator() {
                return new Iterator<BufferedUpdatesStream.QueryAndLimit>() {
                    private int upto;
                    
                    @Override
                    public boolean hasNext() {
                        return this.upto < FrozenBufferedUpdates.this.queries.length;
                    }
                    
                    @Override
                    public BufferedUpdatesStream.QueryAndLimit next() {
                        final BufferedUpdatesStream.QueryAndLimit ret = new BufferedUpdatesStream.QueryAndLimit(FrozenBufferedUpdates.this.queries[this.upto], FrozenBufferedUpdates.this.queryLimits[this.upto]);
                        ++this.upto;
                        return ret;
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }
    
    @Override
    public String toString() {
        String s = "";
        if (this.numTermDeletes != 0) {
            s = s + " " + this.numTermDeletes + " deleted terms (unique count=" + this.terms.size() + ")";
        }
        if (this.queries.length != 0) {
            s = s + " " + this.queries.length + " deleted queries";
        }
        if (this.bytesUsed != 0) {
            s = s + " bytesUsed=" + this.bytesUsed;
        }
        return s;
    }
    
    boolean any() {
        return this.terms.size() > 0L || this.queries.length > 0 || this.numericDVUpdates.length > 0 || this.binaryDVUpdates.length > 0;
    }
    
    static {
        BYTES_PER_DEL_QUERY = RamUsageEstimator.NUM_BYTES_OBJECT_REF + 4 + 24;
    }
}
