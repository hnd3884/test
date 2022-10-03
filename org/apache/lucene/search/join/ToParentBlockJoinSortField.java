package org.apache.lucene.search.join;

import org.apache.lucene.util.NumericUtils;
import org.apache.lucene.util.Bits;
import org.apache.lucene.index.SortedNumericDocValues;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.util.BitSet;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.LeafReaderContext;
import java.io.IOException;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.SortField;

public class ToParentBlockJoinSortField extends SortField
{
    private final boolean order;
    private final BitSetProducer parentFilter;
    private final BitSetProducer childFilter;
    
    public ToParentBlockJoinSortField(final String field, final SortField.Type type, final boolean reverse, final BitSetProducer parentFilter, final BitSetProducer childFilter) {
        super(field, type, reverse);
        switch (this.getType()) {
            case STRING:
            case DOUBLE:
            case FLOAT:
            case LONG:
            case INT: {
                this.order = reverse;
                this.parentFilter = parentFilter;
                this.childFilter = childFilter;
                return;
            }
            default: {
                throw new UnsupportedOperationException("Sort type " + type + " is not supported");
            }
        }
    }
    
    public ToParentBlockJoinSortField(final String field, final SortField.Type type, final boolean reverse, final boolean order, final BitSetProducer parentFilter, final BitSetProducer childFilter) {
        super(field, type, reverse);
        this.order = order;
        this.parentFilter = parentFilter;
        this.childFilter = childFilter;
    }
    
    public FieldComparator<?> getComparator(final int numHits, final int sortPos) throws IOException {
        switch (this.getType()) {
            case STRING: {
                return this.getStringComparator(numHits);
            }
            case DOUBLE: {
                return this.getDoubleComparator(numHits);
            }
            case FLOAT: {
                return this.getFloatComparator(numHits);
            }
            case LONG: {
                return this.getLongComparator(numHits);
            }
            case INT: {
                return this.getIntComparator(numHits);
            }
            default: {
                throw new UnsupportedOperationException("Sort type " + this.getType() + " is not supported");
            }
        }
    }
    
    private FieldComparator<?> getStringComparator(final int numHits) {
        return (FieldComparator<?>)new FieldComparator.TermOrdValComparator(numHits, this.getField(), this.missingValue == ToParentBlockJoinSortField.STRING_LAST) {
            protected SortedDocValues getSortedDocValues(final LeafReaderContext context, final String field) throws IOException {
                final SortedSetDocValues sortedSet = DocValues.getSortedSet(context.reader(), field);
                final BlockJoinSelector.Type type = ToParentBlockJoinSortField.this.order ? BlockJoinSelector.Type.MAX : BlockJoinSelector.Type.MIN;
                final BitSet parents = ToParentBlockJoinSortField.this.parentFilter.getBitSet(context);
                final BitSet children = ToParentBlockJoinSortField.this.childFilter.getBitSet(context);
                if (children == null) {
                    return DocValues.emptySorted();
                }
                return BlockJoinSelector.wrap(sortedSet, type, parents, children);
            }
        };
    }
    
    private FieldComparator<?> getIntComparator(final int numHits) {
        return (FieldComparator<?>)new FieldComparator.IntComparator(numHits, this.getField(), (Integer)this.missingValue) {
            protected NumericDocValues getNumericDocValues(final LeafReaderContext context, final String field) throws IOException {
                final SortedNumericDocValues sortedNumeric = DocValues.getSortedNumeric(context.reader(), field);
                final BlockJoinSelector.Type type = ToParentBlockJoinSortField.this.order ? BlockJoinSelector.Type.MAX : BlockJoinSelector.Type.MIN;
                final BitSet parents = ToParentBlockJoinSortField.this.parentFilter.getBitSet(context);
                final BitSet children = ToParentBlockJoinSortField.this.childFilter.getBitSet(context);
                if (children == null) {
                    return DocValues.emptyNumeric();
                }
                return BlockJoinSelector.wrap(sortedNumeric, type, parents, children);
            }
            
            protected Bits getDocsWithValue(final LeafReaderContext context, final String field) throws IOException {
                final Bits docsWithValue = DocValues.getDocsWithField(context.reader(), field);
                final BitSet parents = ToParentBlockJoinSortField.this.parentFilter.getBitSet(context);
                final BitSet children = ToParentBlockJoinSortField.this.childFilter.getBitSet(context);
                if (children == null) {
                    return (Bits)new Bits.MatchNoBits(context.reader().maxDoc());
                }
                return BlockJoinSelector.wrap(docsWithValue, parents, children);
            }
        };
    }
    
    private FieldComparator<?> getLongComparator(final int numHits) {
        return (FieldComparator<?>)new FieldComparator.LongComparator(numHits, this.getField(), (Long)this.missingValue) {
            protected NumericDocValues getNumericDocValues(final LeafReaderContext context, final String field) throws IOException {
                final SortedNumericDocValues sortedNumeric = DocValues.getSortedNumeric(context.reader(), field);
                final BlockJoinSelector.Type type = ToParentBlockJoinSortField.this.order ? BlockJoinSelector.Type.MAX : BlockJoinSelector.Type.MIN;
                final BitSet parents = ToParentBlockJoinSortField.this.parentFilter.getBitSet(context);
                final BitSet children = ToParentBlockJoinSortField.this.childFilter.getBitSet(context);
                if (children == null) {
                    return DocValues.emptyNumeric();
                }
                return BlockJoinSelector.wrap(sortedNumeric, type, parents, children);
            }
            
            protected Bits getDocsWithValue(final LeafReaderContext context, final String field) throws IOException {
                final Bits docsWithValue = DocValues.getDocsWithField(context.reader(), field);
                final BitSet parents = ToParentBlockJoinSortField.this.parentFilter.getBitSet(context);
                final BitSet children = ToParentBlockJoinSortField.this.childFilter.getBitSet(context);
                if (children == null) {
                    return (Bits)new Bits.MatchNoBits(context.reader().maxDoc());
                }
                return BlockJoinSelector.wrap(docsWithValue, parents, children);
            }
        };
    }
    
    private FieldComparator<?> getFloatComparator(final int numHits) {
        return (FieldComparator<?>)new FieldComparator.FloatComparator(numHits, this.getField(), (Float)this.missingValue) {
            protected NumericDocValues getNumericDocValues(final LeafReaderContext context, final String field) throws IOException {
                final SortedNumericDocValues sortedNumeric = DocValues.getSortedNumeric(context.reader(), field);
                final BlockJoinSelector.Type type = ToParentBlockJoinSortField.this.order ? BlockJoinSelector.Type.MAX : BlockJoinSelector.Type.MIN;
                final BitSet parents = ToParentBlockJoinSortField.this.parentFilter.getBitSet(context);
                final BitSet children = ToParentBlockJoinSortField.this.childFilter.getBitSet(context);
                if (children == null) {
                    return DocValues.emptyNumeric();
                }
                final NumericDocValues view = BlockJoinSelector.wrap(sortedNumeric, type, parents, children);
                return new NumericDocValues() {
                    public long get(final int docID) {
                        return NumericUtils.sortableFloatBits((int)view.get(docID));
                    }
                };
            }
        };
    }
    
    private FieldComparator<?> getDoubleComparator(final int numHits) {
        return (FieldComparator<?>)new FieldComparator.DoubleComparator(numHits, this.getField(), (Double)this.missingValue) {
            protected NumericDocValues getNumericDocValues(final LeafReaderContext context, final String field) throws IOException {
                final SortedNumericDocValues sortedNumeric = DocValues.getSortedNumeric(context.reader(), field);
                final BlockJoinSelector.Type type = ToParentBlockJoinSortField.this.order ? BlockJoinSelector.Type.MAX : BlockJoinSelector.Type.MIN;
                final BitSet parents = ToParentBlockJoinSortField.this.parentFilter.getBitSet(context);
                final BitSet children = ToParentBlockJoinSortField.this.childFilter.getBitSet(context);
                if (children == null) {
                    return DocValues.emptyNumeric();
                }
                final NumericDocValues view = BlockJoinSelector.wrap(sortedNumeric, type, parents, children);
                return new NumericDocValues() {
                    public long get(final int docID) {
                        return NumericUtils.sortableDoubleBits(view.get(docID));
                    }
                };
            }
            
            protected Bits getDocsWithValue(final LeafReaderContext context, final String field) throws IOException {
                final Bits docsWithValue = DocValues.getDocsWithField(context.reader(), field);
                final BitSet parents = ToParentBlockJoinSortField.this.parentFilter.getBitSet(context);
                final BitSet children = ToParentBlockJoinSortField.this.childFilter.getBitSet(context);
                if (children == null) {
                    return (Bits)new Bits.MatchNoBits(context.reader().maxDoc());
                }
                return BlockJoinSelector.wrap(docsWithValue, parents, children);
            }
        };
    }
}
