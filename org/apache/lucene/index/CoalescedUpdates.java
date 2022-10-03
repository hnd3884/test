package org.apache.lucene.index;

import java.util.Iterator;
import org.apache.lucene.util.BytesRef;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.lucene.search.Query;
import java.util.Map;

class CoalescedUpdates
{
    final Map<Query, Integer> queries;
    final List<PrefixCodedTerms> terms;
    final List<List<DocValuesUpdate>> numericDVUpdates;
    final List<List<DocValuesUpdate>> binaryDVUpdates;
    long totalTermCount;
    
    CoalescedUpdates() {
        this.queries = new HashMap<Query, Integer>();
        this.terms = new ArrayList<PrefixCodedTerms>();
        this.numericDVUpdates = new ArrayList<List<DocValuesUpdate>>();
        this.binaryDVUpdates = new ArrayList<List<DocValuesUpdate>>();
    }
    
    @Override
    public String toString() {
        return "CoalescedUpdates(termSets=" + this.terms.size() + ",totalTermCount=" + this.totalTermCount + ",queries=" + this.queries.size() + ",numericDVUpdates=" + this.numericDVUpdates.size() + ",binaryDVUpdates=" + this.binaryDVUpdates.size() + ")";
    }
    
    void update(final FrozenBufferedUpdates in) {
        this.totalTermCount += in.terms.size();
        this.terms.add(in.terms);
        for (int queryIdx = 0; queryIdx < in.queries.length; ++queryIdx) {
            final Query query = in.queries[queryIdx];
            this.queries.put(query, BufferedUpdates.MAX_INT);
        }
        final List<DocValuesUpdate> numericPacket = new ArrayList<DocValuesUpdate>();
        this.numericDVUpdates.add(numericPacket);
        for (final DocValuesUpdate.NumericDocValuesUpdate nu : in.numericDVUpdates) {
            final DocValuesUpdate.NumericDocValuesUpdate clone = new DocValuesUpdate.NumericDocValuesUpdate(nu.term, nu.field, (Long)nu.value);
            clone.docIDUpto = Integer.MAX_VALUE;
            numericPacket.add(clone);
        }
        final List<DocValuesUpdate> binaryPacket = new ArrayList<DocValuesUpdate>();
        this.binaryDVUpdates.add(binaryPacket);
        for (final DocValuesUpdate.BinaryDocValuesUpdate bu : in.binaryDVUpdates) {
            final DocValuesUpdate.BinaryDocValuesUpdate clone2 = new DocValuesUpdate.BinaryDocValuesUpdate(bu.term, bu.field, (BytesRef)bu.value);
            clone2.docIDUpto = Integer.MAX_VALUE;
            binaryPacket.add(clone2);
        }
    }
    
    public FieldTermIterator termIterator() {
        if (this.terms.size() == 1) {
            return this.terms.get(0).iterator();
        }
        return new MergedPrefixCodedTermsIterator(this.terms);
    }
    
    public Iterable<BufferedUpdatesStream.QueryAndLimit> queriesIterable() {
        return new Iterable<BufferedUpdatesStream.QueryAndLimit>() {
            @Override
            public Iterator<BufferedUpdatesStream.QueryAndLimit> iterator() {
                return new Iterator<BufferedUpdatesStream.QueryAndLimit>() {
                    private final Iterator<Map.Entry<Query, Integer>> iter = CoalescedUpdates.this.queries.entrySet().iterator();
                    
                    @Override
                    public boolean hasNext() {
                        return this.iter.hasNext();
                    }
                    
                    @Override
                    public BufferedUpdatesStream.QueryAndLimit next() {
                        final Map.Entry<Query, Integer> ent = this.iter.next();
                        return new BufferedUpdatesStream.QueryAndLimit(ent.getKey(), ent.getValue());
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }
}
