package org.apache.lucene.facet.sortedset;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.ArrayList;
import org.apache.lucene.util.LongValues;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.index.LeafReader;
import java.util.Iterator;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.ReaderUtil;
import org.apache.lucene.index.MultiDocValues;
import java.util.List;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.LabelAndValue;
import org.apache.lucene.facet.TopOrdAndIntQueue;
import org.apache.lucene.facet.FacetResult;
import java.io.IOException;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.facet.Facets;

public class SortedSetDocValuesFacetCounts extends Facets
{
    final SortedSetDocValuesReaderState state;
    final SortedSetDocValues dv;
    final String field;
    final int[] counts;
    
    public SortedSetDocValuesFacetCounts(final SortedSetDocValuesReaderState state, final FacetsCollector hits) throws IOException {
        this.state = state;
        this.field = state.getField();
        this.dv = state.getDocValues();
        this.counts = new int[state.getSize()];
        this.count(hits.getMatchingDocs());
    }
    
    @Override
    public FacetResult getTopChildren(final int topN, final String dim, final String... path) throws IOException {
        if (topN <= 0) {
            throw new IllegalArgumentException("topN must be > 0 (got: " + topN + ")");
        }
        if (path.length > 0) {
            throw new IllegalArgumentException("path should be 0 length");
        }
        final SortedSetDocValuesReaderState.OrdRange ordRange = this.state.getOrdRange(dim);
        if (ordRange == null) {
            throw new IllegalArgumentException("dimension \"" + dim + "\" was not indexed");
        }
        return this.getDim(dim, ordRange, topN);
    }
    
    private final FacetResult getDim(final String dim, final SortedSetDocValuesReaderState.OrdRange ordRange, final int topN) {
        TopOrdAndIntQueue q = null;
        int bottomCount = 0;
        int dimCount = 0;
        int childCount = 0;
        TopOrdAndIntQueue.OrdAndValue reuse = null;
        for (int ord = ordRange.start; ord <= ordRange.end; ++ord) {
            if (this.counts[ord] > 0) {
                dimCount += this.counts[ord];
                ++childCount;
                if (this.counts[ord] > bottomCount) {
                    if (reuse == null) {
                        reuse = new TopOrdAndIntQueue.OrdAndValue();
                    }
                    reuse.ord = ord;
                    reuse.value = this.counts[ord];
                    if (q == null) {
                        q = new TopOrdAndIntQueue(topN);
                    }
                    reuse = (TopOrdAndIntQueue.OrdAndValue)q.insertWithOverflow((Object)reuse);
                    if (q.size() == topN) {
                        bottomCount = ((TopOrdAndIntQueue.OrdAndValue)q.top()).value;
                    }
                }
            }
        }
        if (q == null) {
            return null;
        }
        final LabelAndValue[] labelValues = new LabelAndValue[q.size()];
        for (int i = labelValues.length - 1; i >= 0; --i) {
            final TopOrdAndIntQueue.OrdAndValue ordAndValue = (TopOrdAndIntQueue.OrdAndValue)q.pop();
            final BytesRef term = this.dv.lookupOrd((long)ordAndValue.ord);
            final String[] parts = FacetsConfig.stringToPath(term.utf8ToString());
            labelValues[i] = new LabelAndValue(parts[1], ordAndValue.value);
        }
        return new FacetResult(dim, new String[0], dimCount, labelValues, childCount);
    }
    
    private final void count(final List<FacetsCollector.MatchingDocs> matchingDocs) throws IOException {
        MultiDocValues.OrdinalMap ordinalMap;
        if (this.dv instanceof MultiDocValues.MultiSortedSetDocValues && matchingDocs.size() > 1) {
            ordinalMap = ((MultiDocValues.MultiSortedSetDocValues)this.dv).mapping;
        }
        else {
            ordinalMap = null;
        }
        final IndexReader origReader = this.state.getOrigReader();
        for (final FacetsCollector.MatchingDocs hits : matchingDocs) {
            final LeafReader reader = hits.context.reader();
            if (ReaderUtil.getTopLevelContext((IndexReaderContext)hits.context).reader() != origReader) {
                throw new IllegalStateException("the SortedSetDocValuesReaderState provided to this class does not match the reader being searched; you must create a new SortedSetDocValuesReaderState every time you open a new IndexReader");
            }
            final SortedSetDocValues segValues = reader.getSortedSetDocValues(this.field);
            if (segValues == null) {
                continue;
            }
            final DocIdSetIterator docs = hits.bits.iterator();
            if (ordinalMap != null) {
                final int segOrd = hits.context.ord;
                final LongValues ordMap = ordinalMap.getGlobalOrds(segOrd);
                final int numSegOrds = (int)segValues.getValueCount();
                if (hits.totalHits < numSegOrds / 10) {
                    int doc;
                    while ((doc = docs.nextDoc()) != Integer.MAX_VALUE) {
                        segValues.setDocument(doc);
                        for (int term = (int)segValues.nextOrd(); term != -1L; term = (int)segValues.nextOrd()) {
                            final int[] counts = this.counts;
                            final int n = (int)ordMap.get(term);
                            ++counts[n];
                        }
                    }
                }
                else {
                    final int[] segCounts = new int[numSegOrds];
                    int doc2;
                    while ((doc2 = docs.nextDoc()) != Integer.MAX_VALUE) {
                        segValues.setDocument(doc2);
                        for (int term2 = (int)segValues.nextOrd(); term2 != -1L; term2 = (int)segValues.nextOrd()) {
                            final int[] array = segCounts;
                            final int n2 = term2;
                            ++array[n2];
                        }
                    }
                    for (int ord = 0; ord < numSegOrds; ++ord) {
                        final int count = segCounts[ord];
                        if (count != 0) {
                            final int[] counts2 = this.counts;
                            final int n3 = (int)ordMap.get(ord);
                            counts2[n3] += count;
                        }
                    }
                }
            }
            else {
                int doc3;
                while ((doc3 = docs.nextDoc()) != Integer.MAX_VALUE) {
                    segValues.setDocument(doc3);
                    for (int term3 = (int)segValues.nextOrd(); term3 != -1L; term3 = (int)segValues.nextOrd()) {
                        final int[] counts3 = this.counts;
                        final int n4 = term3;
                        ++counts3[n4];
                    }
                }
            }
        }
    }
    
    @Override
    public Number getSpecificValue(final String dim, final String... path) {
        if (path.length != 1) {
            throw new IllegalArgumentException("path must be length=1");
        }
        final int ord = (int)this.dv.lookupTerm(new BytesRef((CharSequence)FacetsConfig.pathToString(dim, path)));
        if (ord < 0) {
            return -1;
        }
        return this.counts[ord];
    }
    
    @Override
    public List<FacetResult> getAllDims(final int topN) throws IOException {
        final List<FacetResult> results = new ArrayList<FacetResult>();
        for (final Map.Entry<String, SortedSetDocValuesReaderState.OrdRange> ent : this.state.getPrefixToOrdRange().entrySet()) {
            final FacetResult fr = this.getDim(ent.getKey(), ent.getValue(), topN);
            if (fr != null) {
                results.add(fr);
            }
        }
        Collections.sort(results, new Comparator<FacetResult>() {
            @Override
            public int compare(final FacetResult a, final FacetResult b) {
                if (a.value.intValue() > b.value.intValue()) {
                    return -1;
                }
                if (b.value.intValue() > a.value.intValue()) {
                    return 1;
                }
                return a.dim.compareTo(b.dim);
            }
        });
        return results;
    }
}
