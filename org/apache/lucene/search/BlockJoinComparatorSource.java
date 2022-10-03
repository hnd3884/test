package org.apache.lucene.search;

import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.ReaderUtil;
import org.apache.lucene.index.LeafReaderContext;
import java.io.IOException;
import org.apache.lucene.util.BitSet;

public class BlockJoinComparatorSource extends FieldComparatorSource
{
    final Query parentsFilter;
    final Sort parentSort;
    final Sort childSort;
    
    public BlockJoinComparatorSource(final Query parentsFilter, final Sort parentSort) {
        this(parentsFilter, parentSort, new Sort(SortField.FIELD_DOC));
    }
    
    public BlockJoinComparatorSource(final Query parentsFilter, final Sort parentSort, final Sort childSort) {
        this.parentsFilter = parentsFilter;
        this.parentSort = parentSort;
        this.childSort = childSort;
    }
    
    public FieldComparator<Integer> newComparator(final String fieldname, final int numHits, final int sortPos, final boolean reversed) throws IOException {
        final int[] parentSlots = new int[numHits];
        final int[] childSlots = new int[numHits];
        final SortField[] parentFields = this.parentSort.getSort();
        final int[] parentReverseMul = new int[parentFields.length];
        final FieldComparator<?>[] parentComparators = (FieldComparator<?>[])new FieldComparator[parentFields.length];
        for (int i = 0; i < parentFields.length; ++i) {
            parentReverseMul[i] = (parentFields[i].getReverse() ? -1 : 1);
            parentComparators[i] = (FieldComparator<?>)parentFields[i].getComparator(1, i);
        }
        final SortField[] childFields = this.childSort.getSort();
        final int[] childReverseMul = new int[childFields.length];
        final FieldComparator<?>[] childComparators = (FieldComparator<?>[])new FieldComparator[childFields.length];
        for (int j = 0; j < childFields.length; ++j) {
            childReverseMul[j] = (childFields[j].getReverse() ? -1 : 1);
            childComparators[j] = (FieldComparator<?>)childFields[j].getComparator(1, j);
        }
        return new FieldComparator<Integer>() {
            int bottomParent;
            int bottomChild;
            BitSet parentBits;
            LeafFieldComparator[] parentLeafComparators;
            LeafFieldComparator[] childLeafComparators;
            
            public int compare(final int slot1, final int slot2) {
                try {
                    return this.compare(childSlots[slot1], parentSlots[slot1], childSlots[slot2], parentSlots[slot2]);
                }
                catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
            
            public void setTopValue(final Integer value) {
                throw new UnsupportedOperationException("this comparator cannot be used with deep paging");
            }
            
            public LeafFieldComparator getLeafComparator(final LeafReaderContext context) throws IOException {
                if (this.parentBits != null) {
                    throw new IllegalStateException("This comparator can only be used on a single segment");
                }
                final IndexSearcher searcher = new IndexSearcher(ReaderUtil.getTopLevelContext((IndexReaderContext)context));
                searcher.setQueryCache((QueryCache)null);
                final Weight weight = searcher.createNormalizedWeight(BlockJoinComparatorSource.this.parentsFilter, false);
                final Scorer parents = weight.scorer(context);
                if (parents == null) {
                    throw new IllegalStateException("LeafReader " + context.reader() + " contains no parents!");
                }
                this.parentBits = BitSet.of(parents.iterator(), context.reader().maxDoc());
                this.parentLeafComparators = new LeafFieldComparator[parentComparators.length];
                for (int i = 0; i < parentComparators.length; ++i) {
                    this.parentLeafComparators[i] = parentComparators[i].getLeafComparator(context);
                }
                this.childLeafComparators = new LeafFieldComparator[childComparators.length];
                for (int i = 0; i < childComparators.length; ++i) {
                    this.childLeafComparators[i] = childComparators[i].getLeafComparator(context);
                }
                return (LeafFieldComparator)new LeafFieldComparator() {
                    public int compareBottom(final int doc) throws IOException {
                        return FieldComparator.this.compare(FieldComparator.this.bottomChild, FieldComparator.this.bottomParent, doc, FieldComparator.this.parent(doc));
                    }
                    
                    public int compareTop(final int doc) throws IOException {
                        throw new UnsupportedOperationException("this comparator cannot be used with deep paging");
                    }
                    
                    public void copy(final int slot, final int doc) throws IOException {
                        childSlots[slot] = doc;
                        parentSlots[slot] = FieldComparator.this.parent(doc);
                    }
                    
                    public void setBottom(final int slot) {
                        FieldComparator.this.bottomParent = parentSlots[slot];
                        FieldComparator.this.bottomChild = childSlots[slot];
                    }
                    
                    public void setScorer(final Scorer scorer) {
                        for (final LeafFieldComparator comp : FieldComparator.this.parentLeafComparators) {
                            comp.setScorer(scorer);
                        }
                        for (final LeafFieldComparator comp : FieldComparator.this.childLeafComparators) {
                            comp.setScorer(scorer);
                        }
                    }
                };
            }
            
            public Integer value(final int slot) {
                throw new UnsupportedOperationException("filling sort field values is not yet supported");
            }
            
            int parent(final int doc) {
                return this.parentBits.nextSetBit(doc);
            }
            
            int compare(final int docID1, final int parent1, final int docID2, final int parent2) throws IOException {
                if (parent1 == parent2) {
                    if (docID1 == parent1 || docID2 == parent2) {
                        return docID1 - docID2;
                    }
                    return this.compare(docID1, docID2, this.childLeafComparators, childReverseMul);
                }
                else {
                    final int cmp = this.compare(parent1, parent2, this.parentLeafComparators, parentReverseMul);
                    if (cmp == 0) {
                        return parent1 - parent2;
                    }
                    return cmp;
                }
            }
            
            int compare(final int docID1, final int docID2, final LeafFieldComparator[] comparators, final int[] reverseMul) throws IOException {
                for (int i = 0; i < comparators.length; ++i) {
                    comparators[i].copy(0, docID1);
                    comparators[i].setBottom(0);
                    final int comp = reverseMul[i] * comparators[i].compareBottom(docID2);
                    if (comp != 0) {
                        return comp;
                    }
                }
                return 0;
            }
        };
    }
    
    public String toString() {
        return "blockJoin(parentSort=" + this.parentSort + ",childSort=" + this.childSort + ")";
    }
}
