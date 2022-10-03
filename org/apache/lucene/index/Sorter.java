package org.apache.lucene.index;

import org.apache.lucene.util.TimSorter;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.SortField;
import java.io.IOException;
import org.apache.lucene.search.LeafFieldComparator;
import org.apache.lucene.util.packed.PackedLongValues;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Sort;

final class Sorter
{
    final Sort sort;
    static final Scorer FAKESCORER;
    
    Sorter(final Sort sort) {
        if (sort.needsScores()) {
            throw new IllegalArgumentException("Cannot sort an index with a Sort that refers to the relevance score");
        }
        this.sort = sort;
    }
    
    static boolean isConsistent(final DocMap docMap) {
        for (int maxDoc = docMap.size(), i = 0; i < maxDoc; ++i) {
            final int newID = docMap.oldToNew(i);
            final int oldID = docMap.newToOld(newID);
            assert newID >= 0 && newID < maxDoc : "doc IDs must be in [0-" + maxDoc + "[, got " + newID;
            assert i == oldID : "mapping is inconsistent: " + i + " --oldToNew--> " + newID + " --newToOld--> " + oldID;
            if (i != oldID || newID < 0 || newID >= maxDoc) {
                return false;
            }
        }
        return true;
    }
    
    private static DocMap sort(final int maxDoc, final DocComparator comparator) {
        boolean sorted = true;
        for (int i = 1; i < maxDoc; ++i) {
            if (comparator.compare(i - 1, i) > 0) {
                sorted = false;
                break;
            }
        }
        if (sorted) {
            return null;
        }
        final int[] docs = new int[maxDoc];
        for (int j = 0; j < maxDoc; ++j) {
            docs[j] = j;
        }
        final DocValueSorter sorter = new DocValueSorter(docs, comparator);
        sorter.sort(0, docs.length);
        final PackedLongValues.Builder newToOldBuilder = PackedLongValues.monotonicBuilder(0.0f);
        for (int k = 0; k < maxDoc; ++k) {
            newToOldBuilder.add((long)docs[k]);
        }
        final PackedLongValues newToOld = newToOldBuilder.build();
        for (int l = 0; l < maxDoc; ++l) {
            docs[(int)newToOld.get(l)] = l;
        }
        final PackedLongValues.Builder oldToNewBuilder = PackedLongValues.monotonicBuilder(0.0f);
        for (int m = 0; m < maxDoc; ++m) {
            oldToNewBuilder.add((long)docs[m]);
        }
        final PackedLongValues oldToNew = oldToNewBuilder.build();
        return new DocMap() {
            public int oldToNew(final int docID) {
                return (int)oldToNew.get(docID);
            }
            
            public int newToOld(final int docID) {
                return (int)newToOld.get(docID);
            }
            
            public int size() {
                return maxDoc;
            }
        };
    }
    
    DocMap sort(final LeafReader reader) throws IOException {
        final SortField[] fields = this.sort.getSort();
        final int[] reverseMul = new int[fields.length];
        final LeafFieldComparator[] comparators = new LeafFieldComparator[fields.length];
        for (int i = 0; i < fields.length; ++i) {
            reverseMul[i] = (fields[i].getReverse() ? -1 : 1);
            (comparators[i] = fields[i].getComparator(1, i).getLeafComparator(reader.getContext())).setScorer(Sorter.FAKESCORER);
        }
        final DocComparator comparator = new DocComparator() {
            @Override
            public int compare(final int docID1, final int docID2) {
                try {
                    for (int i = 0; i < comparators.length; ++i) {
                        comparators[i].copy(0, docID1);
                        comparators[i].setBottom(0);
                        final int comp = reverseMul[i] * comparators[i].compareBottom(docID2);
                        if (comp != 0) {
                            return comp;
                        }
                    }
                    return Integer.compare(docID1, docID2);
                }
                catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        return sort(reader.maxDoc(), comparator);
    }
    
    public String getID() {
        return this.sort.toString();
    }
    
    @Override
    public String toString() {
        return this.getID();
    }
    
    static {
        FAKESCORER = new Scorer(null) {
            float score;
            int doc = -1;
            int freq = 1;
            
            public int docID() {
                return this.doc;
            }
            
            public DocIdSetIterator iterator() {
                throw new UnsupportedOperationException();
            }
            
            public int freq() throws IOException {
                return this.freq;
            }
            
            public float score() throws IOException {
                return this.score;
            }
        };
    }
    
    abstract static class DocMap
    {
        abstract int oldToNew(final int p0);
        
        abstract int newToOld(final int p0);
        
        abstract int size();
    }
    
    abstract static class DocComparator
    {
        public abstract int compare(final int p0, final int p1);
    }
    
    private static final class DocValueSorter extends TimSorter
    {
        private final int[] docs;
        private final DocComparator comparator;
        private final int[] tmp;
        
        DocValueSorter(final int[] docs, final DocComparator comparator) {
            super(docs.length / 64);
            this.docs = docs;
            this.comparator = comparator;
            this.tmp = new int[docs.length / 64];
        }
        
        protected int compare(final int i, final int j) {
            return this.comparator.compare(this.docs[i], this.docs[j]);
        }
        
        protected void swap(final int i, final int j) {
            final int tmpDoc = this.docs[i];
            this.docs[i] = this.docs[j];
            this.docs[j] = tmpDoc;
        }
        
        protected void copy(final int src, final int dest) {
            this.docs[dest] = this.docs[src];
        }
        
        protected void save(final int i, final int len) {
            System.arraycopy(this.docs, i, this.tmp, 0, len);
        }
        
        protected void restore(final int i, final int j) {
            this.docs[j] = this.tmp[i];
        }
        
        protected int compareSaved(final int i, final int j) {
            return this.comparator.compare(this.tmp[i], this.docs[j]);
        }
    }
}
