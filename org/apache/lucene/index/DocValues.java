package org.apache.lucene.index;

import java.io.IOException;
import java.util.Arrays;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;

public final class DocValues
{
    private DocValues() {
    }
    
    public static final BinaryDocValues emptyBinary() {
        final BytesRef empty = new BytesRef();
        return new BinaryDocValues() {
            @Override
            public BytesRef get(final int docID) {
                return empty;
            }
        };
    }
    
    public static final NumericDocValues emptyNumeric() {
        return new NumericDocValues() {
            @Override
            public long get(final int docID) {
                return 0L;
            }
        };
    }
    
    public static final SortedDocValues emptySorted() {
        final BytesRef empty = new BytesRef();
        return new SortedDocValues() {
            @Override
            public int getOrd(final int docID) {
                return -1;
            }
            
            @Override
            public BytesRef lookupOrd(final int ord) {
                return empty;
            }
            
            @Override
            public int getValueCount() {
                return 0;
            }
        };
    }
    
    public static final SortedNumericDocValues emptySortedNumeric(final int maxDoc) {
        return singleton(emptyNumeric(), new Bits.MatchNoBits(maxDoc));
    }
    
    public static final RandomAccessOrds emptySortedSet() {
        return singleton(emptySorted());
    }
    
    public static RandomAccessOrds singleton(final SortedDocValues dv) {
        return new SingletonSortedSetDocValues(dv);
    }
    
    public static SortedDocValues unwrapSingleton(final SortedSetDocValues dv) {
        if (dv instanceof SingletonSortedSetDocValues) {
            return ((SingletonSortedSetDocValues)dv).getSortedDocValues();
        }
        return null;
    }
    
    public static NumericDocValues unwrapSingleton(final SortedNumericDocValues dv) {
        if (dv instanceof SingletonSortedNumericDocValues) {
            return ((SingletonSortedNumericDocValues)dv).getNumericDocValues();
        }
        return null;
    }
    
    public static Bits unwrapSingletonBits(final SortedNumericDocValues dv) {
        if (dv instanceof SingletonSortedNumericDocValues) {
            return ((SingletonSortedNumericDocValues)dv).getDocsWithField();
        }
        return null;
    }
    
    public static SortedNumericDocValues singleton(final NumericDocValues dv, final Bits docsWithField) {
        return new SingletonSortedNumericDocValues(dv, docsWithField);
    }
    
    public static Bits docsWithValue(final SortedDocValues dv, final int maxDoc) {
        return new Bits() {
            @Override
            public boolean get(final int index) {
                return dv.getOrd(index) >= 0;
            }
            
            @Override
            public int length() {
                return maxDoc;
            }
        };
    }
    
    public static Bits docsWithValue(final SortedSetDocValues dv, final int maxDoc) {
        return new Bits() {
            @Override
            public boolean get(final int index) {
                dv.setDocument(index);
                return dv.nextOrd() != -1L;
            }
            
            @Override
            public int length() {
                return maxDoc;
            }
        };
    }
    
    public static Bits docsWithValue(final SortedNumericDocValues dv, final int maxDoc) {
        return new Bits() {
            @Override
            public boolean get(final int index) {
                dv.setDocument(index);
                return dv.count() != 0;
            }
            
            @Override
            public int length() {
                return maxDoc;
            }
        };
    }
    
    private static void checkField(final LeafReader in, final String field, final DocValuesType... expected) {
        final FieldInfo fi = in.getFieldInfos().fieldInfo(field);
        if (fi != null) {
            final DocValuesType actual = fi.getDocValuesType();
            throw new IllegalStateException("unexpected docvalues type " + actual + " for field '" + field + "' " + ((expected.length == 1) ? ("(expected=" + expected[0]) : ("(expected one of " + Arrays.toString(expected))) + "). " + "Use UninvertingReader or index with docvalues.");
        }
    }
    
    public static NumericDocValues getNumeric(final LeafReader reader, final String field) throws IOException {
        final NumericDocValues dv = reader.getNumericDocValues(field);
        if (dv == null) {
            checkField(reader, field, DocValuesType.NUMERIC);
            return emptyNumeric();
        }
        return dv;
    }
    
    public static BinaryDocValues getBinary(final LeafReader reader, final String field) throws IOException {
        BinaryDocValues dv = reader.getBinaryDocValues(field);
        if (dv == null) {
            dv = reader.getSortedDocValues(field);
            if (dv == null) {
                checkField(reader, field, DocValuesType.BINARY, DocValuesType.SORTED);
                return emptyBinary();
            }
        }
        return dv;
    }
    
    public static SortedDocValues getSorted(final LeafReader reader, final String field) throws IOException {
        final SortedDocValues dv = reader.getSortedDocValues(field);
        if (dv == null) {
            checkField(reader, field, DocValuesType.SORTED);
            return emptySorted();
        }
        return dv;
    }
    
    public static SortedNumericDocValues getSortedNumeric(final LeafReader reader, final String field) throws IOException {
        final SortedNumericDocValues dv = reader.getSortedNumericDocValues(field);
        if (dv != null) {
            return dv;
        }
        final NumericDocValues single = reader.getNumericDocValues(field);
        if (single == null) {
            checkField(reader, field, DocValuesType.SORTED_NUMERIC, DocValuesType.NUMERIC);
            return emptySortedNumeric(reader.maxDoc());
        }
        final Bits bits = reader.getDocsWithField(field);
        return singleton(single, bits);
    }
    
    public static SortedSetDocValues getSortedSet(final LeafReader reader, final String field) throws IOException {
        final SortedSetDocValues dv = reader.getSortedSetDocValues(field);
        if (dv != null) {
            return dv;
        }
        final SortedDocValues sorted = reader.getSortedDocValues(field);
        if (sorted == null) {
            checkField(reader, field, DocValuesType.SORTED, DocValuesType.SORTED_SET);
            return emptySortedSet();
        }
        return singleton(sorted);
    }
    
    public static Bits getDocsWithField(final LeafReader reader, final String field) throws IOException {
        final Bits dv = reader.getDocsWithField(field);
        if (dv != null) {
            return dv;
        }
        assert DocValuesType.values().length == 6;
        checkField(reader, field, DocValuesType.BINARY, DocValuesType.NUMERIC, DocValuesType.SORTED, DocValuesType.SORTED_NUMERIC, DocValuesType.SORTED_SET);
        return new Bits.MatchNoBits(reader.maxDoc());
    }
}
