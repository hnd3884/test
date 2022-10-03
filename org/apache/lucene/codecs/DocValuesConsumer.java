package org.apache.lucene.codecs;

import org.apache.lucene.index.FilteredTermsEnum;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.LongValues;
import org.apache.lucene.index.MultiDocValues;
import org.apache.lucene.util.LongBitSet;
import org.apache.lucene.index.TermsEnum;
import java.util.NoSuchElementException;
import java.util.List;
import java.util.Iterator;
import org.apache.lucene.index.SortedNumericDocValues;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.util.Bits;
import org.apache.lucene.index.NumericDocValues;
import java.util.ArrayList;
import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.index.MergeState;
import org.apache.lucene.util.BytesRef;
import java.io.IOException;
import org.apache.lucene.index.FieldInfo;
import java.io.Closeable;

public abstract class DocValuesConsumer implements Closeable
{
    protected DocValuesConsumer() {
    }
    
    public abstract void addNumericField(final FieldInfo p0, final Iterable<Number> p1) throws IOException;
    
    public abstract void addBinaryField(final FieldInfo p0, final Iterable<BytesRef> p1) throws IOException;
    
    public abstract void addSortedField(final FieldInfo p0, final Iterable<BytesRef> p1, final Iterable<Number> p2) throws IOException;
    
    public abstract void addSortedNumericField(final FieldInfo p0, final Iterable<Number> p1, final Iterable<Number> p2) throws IOException;
    
    public abstract void addSortedSetField(final FieldInfo p0, final Iterable<BytesRef> p1, final Iterable<Number> p2, final Iterable<Number> p3) throws IOException;
    
    public void merge(final MergeState mergeState) throws IOException {
        for (final DocValuesProducer docValuesProducer : mergeState.docValuesProducers) {
            if (docValuesProducer != null) {
                docValuesProducer.checkIntegrity();
            }
        }
        for (final FieldInfo mergeFieldInfo : mergeState.mergeFieldInfos) {
            final DocValuesType type = mergeFieldInfo.getDocValuesType();
            if (type != DocValuesType.NONE) {
                if (type == DocValuesType.NUMERIC) {
                    final List<NumericDocValues> toMerge = new ArrayList<NumericDocValues>();
                    final List<Bits> docsWithField = new ArrayList<Bits>();
                    for (int i = 0; i < mergeState.docValuesProducers.length; ++i) {
                        NumericDocValues values = null;
                        Bits bits = null;
                        final DocValuesProducer docValuesProducer2 = mergeState.docValuesProducers[i];
                        if (docValuesProducer2 != null) {
                            final FieldInfo fieldInfo = mergeState.fieldInfos[i].fieldInfo(mergeFieldInfo.name);
                            if (fieldInfo != null && fieldInfo.getDocValuesType() == DocValuesType.NUMERIC) {
                                values = docValuesProducer2.getNumeric(fieldInfo);
                                bits = docValuesProducer2.getDocsWithField(fieldInfo);
                            }
                        }
                        if (values == null) {
                            values = DocValues.emptyNumeric();
                            bits = new Bits.MatchNoBits(mergeState.maxDocs[i]);
                        }
                        toMerge.add(values);
                        docsWithField.add(bits);
                    }
                    this.mergeNumericField(mergeFieldInfo, mergeState, toMerge, docsWithField);
                }
                else if (type == DocValuesType.BINARY) {
                    final List<BinaryDocValues> toMerge2 = new ArrayList<BinaryDocValues>();
                    final List<Bits> docsWithField = new ArrayList<Bits>();
                    for (int i = 0; i < mergeState.docValuesProducers.length; ++i) {
                        BinaryDocValues values2 = null;
                        Bits bits = null;
                        final DocValuesProducer docValuesProducer2 = mergeState.docValuesProducers[i];
                        if (docValuesProducer2 != null) {
                            final FieldInfo fieldInfo = mergeState.fieldInfos[i].fieldInfo(mergeFieldInfo.name);
                            if (fieldInfo != null && fieldInfo.getDocValuesType() == DocValuesType.BINARY) {
                                values2 = docValuesProducer2.getBinary(fieldInfo);
                                bits = docValuesProducer2.getDocsWithField(fieldInfo);
                            }
                        }
                        if (values2 == null) {
                            values2 = DocValues.emptyBinary();
                            bits = new Bits.MatchNoBits(mergeState.maxDocs[i]);
                        }
                        toMerge2.add(values2);
                        docsWithField.add(bits);
                    }
                    this.mergeBinaryField(mergeFieldInfo, mergeState, toMerge2, docsWithField);
                }
                else if (type == DocValuesType.SORTED) {
                    final List<SortedDocValues> toMerge3 = new ArrayList<SortedDocValues>();
                    for (int j = 0; j < mergeState.docValuesProducers.length; ++j) {
                        SortedDocValues values3 = null;
                        final DocValuesProducer docValuesProducer3 = mergeState.docValuesProducers[j];
                        if (docValuesProducer3 != null) {
                            final FieldInfo fieldInfo2 = mergeState.fieldInfos[j].fieldInfo(mergeFieldInfo.name);
                            if (fieldInfo2 != null && fieldInfo2.getDocValuesType() == DocValuesType.SORTED) {
                                values3 = docValuesProducer3.getSorted(fieldInfo2);
                            }
                        }
                        if (values3 == null) {
                            values3 = DocValues.emptySorted();
                        }
                        toMerge3.add(values3);
                    }
                    this.mergeSortedField(mergeFieldInfo, mergeState, toMerge3);
                }
                else if (type == DocValuesType.SORTED_SET) {
                    final List<SortedSetDocValues> toMerge4 = new ArrayList<SortedSetDocValues>();
                    for (int j = 0; j < mergeState.docValuesProducers.length; ++j) {
                        SortedSetDocValues values4 = null;
                        final DocValuesProducer docValuesProducer3 = mergeState.docValuesProducers[j];
                        if (docValuesProducer3 != null) {
                            final FieldInfo fieldInfo2 = mergeState.fieldInfos[j].fieldInfo(mergeFieldInfo.name);
                            if (fieldInfo2 != null && fieldInfo2.getDocValuesType() == DocValuesType.SORTED_SET) {
                                values4 = docValuesProducer3.getSortedSet(fieldInfo2);
                            }
                        }
                        if (values4 == null) {
                            values4 = DocValues.emptySortedSet();
                        }
                        toMerge4.add(values4);
                    }
                    this.mergeSortedSetField(mergeFieldInfo, mergeState, toMerge4);
                }
                else {
                    if (type != DocValuesType.SORTED_NUMERIC) {
                        throw new AssertionError((Object)("type=" + type));
                    }
                    final List<SortedNumericDocValues> toMerge5 = new ArrayList<SortedNumericDocValues>();
                    for (int j = 0; j < mergeState.docValuesProducers.length; ++j) {
                        SortedNumericDocValues values5 = null;
                        final DocValuesProducer docValuesProducer3 = mergeState.docValuesProducers[j];
                        if (docValuesProducer3 != null) {
                            final FieldInfo fieldInfo2 = mergeState.fieldInfos[j].fieldInfo(mergeFieldInfo.name);
                            if (fieldInfo2 != null && fieldInfo2.getDocValuesType() == DocValuesType.SORTED_NUMERIC) {
                                values5 = docValuesProducer3.getSortedNumeric(fieldInfo2);
                            }
                        }
                        if (values5 == null) {
                            values5 = DocValues.emptySortedNumeric(mergeState.maxDocs[j]);
                        }
                        toMerge5.add(values5);
                    }
                    this.mergeSortedNumericField(mergeFieldInfo, mergeState, toMerge5);
                }
            }
        }
    }
    
    public void mergeNumericField(final FieldInfo fieldInfo, final MergeState mergeState, final List<NumericDocValues> toMerge, final List<Bits> docsWithField) throws IOException {
        this.addNumericField(fieldInfo, new Iterable<Number>() {
            @Override
            public Iterator<Number> iterator() {
                return new Iterator<Number>() {
                    int readerUpto = -1;
                    int docIDUpto;
                    long nextValue;
                    boolean nextHasValue;
                    int currentMaxDoc;
                    NumericDocValues currentValues;
                    Bits currentLiveDocs;
                    Bits currentDocsWithField;
                    boolean nextIsSet;
                    
                    @Override
                    public boolean hasNext() {
                        return this.nextIsSet || this.setNext();
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                    
                    @Override
                    public Number next() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        assert this.nextIsSet;
                        this.nextIsSet = false;
                        return this.nextHasValue ? Long.valueOf(this.nextValue) : null;
                    }
                    
                    private boolean setNext() {
                        while (this.readerUpto != toMerge.size()) {
                            if (this.docIDUpto == this.currentMaxDoc) {
                                ++this.readerUpto;
                                if (this.readerUpto < toMerge.size()) {
                                    this.currentValues = toMerge.get(this.readerUpto);
                                    this.currentDocsWithField = docsWithField.get(this.readerUpto);
                                    this.currentLiveDocs = mergeState.liveDocs[this.readerUpto];
                                    this.currentMaxDoc = mergeState.maxDocs[this.readerUpto];
                                }
                                this.docIDUpto = 0;
                            }
                            else {
                                if (this.currentLiveDocs == null || this.currentLiveDocs.get(this.docIDUpto)) {
                                    this.nextIsSet = true;
                                    this.nextValue = this.currentValues.get(this.docIDUpto);
                                    if (this.nextValue == 0L && !this.currentDocsWithField.get(this.docIDUpto)) {
                                        this.nextHasValue = false;
                                    }
                                    else {
                                        this.nextHasValue = true;
                                    }
                                    ++this.docIDUpto;
                                    return true;
                                }
                                ++this.docIDUpto;
                            }
                        }
                        return false;
                    }
                };
            }
        });
    }
    
    public void mergeBinaryField(final FieldInfo fieldInfo, final MergeState mergeState, final List<BinaryDocValues> toMerge, final List<Bits> docsWithField) throws IOException {
        this.addBinaryField(fieldInfo, new Iterable<BytesRef>() {
            @Override
            public Iterator<BytesRef> iterator() {
                return new Iterator<BytesRef>() {
                    int readerUpto = -1;
                    int docIDUpto;
                    BytesRef nextValue;
                    BytesRef nextPointer;
                    int currentMaxDoc;
                    BinaryDocValues currentValues;
                    Bits currentLiveDocs;
                    Bits currentDocsWithField;
                    boolean nextIsSet;
                    
                    @Override
                    public boolean hasNext() {
                        return this.nextIsSet || this.setNext();
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                    
                    @Override
                    public BytesRef next() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        assert this.nextIsSet;
                        this.nextIsSet = false;
                        return this.nextPointer;
                    }
                    
                    private boolean setNext() {
                        while (this.readerUpto != toMerge.size()) {
                            if (this.docIDUpto == this.currentMaxDoc) {
                                ++this.readerUpto;
                                if (this.readerUpto < toMerge.size()) {
                                    this.currentValues = toMerge.get(this.readerUpto);
                                    this.currentDocsWithField = docsWithField.get(this.readerUpto);
                                    this.currentLiveDocs = mergeState.liveDocs[this.readerUpto];
                                    this.currentMaxDoc = mergeState.maxDocs[this.readerUpto];
                                }
                                this.docIDUpto = 0;
                            }
                            else {
                                if (this.currentLiveDocs == null || this.currentLiveDocs.get(this.docIDUpto)) {
                                    this.nextIsSet = true;
                                    if (this.currentDocsWithField.get(this.docIDUpto)) {
                                        this.nextValue = this.currentValues.get(this.docIDUpto);
                                        this.nextPointer = this.nextValue;
                                    }
                                    else {
                                        this.nextPointer = null;
                                    }
                                    ++this.docIDUpto;
                                    return true;
                                }
                                ++this.docIDUpto;
                            }
                        }
                        return false;
                    }
                };
            }
        });
    }
    
    public void mergeSortedNumericField(final FieldInfo fieldInfo, final MergeState mergeState, final List<SortedNumericDocValues> toMerge) throws IOException {
        final int numReaders = toMerge.size();
        final SortedNumericDocValues[] dvs = toMerge.toArray(new SortedNumericDocValues[numReaders]);
        this.addSortedNumericField(fieldInfo, new Iterable<Number>() {
            @Override
            public Iterator<Number> iterator() {
                return new Iterator<Number>() {
                    int readerUpto = -1;
                    int docIDUpto;
                    int nextValue;
                    int currentMaxDoc;
                    Bits currentLiveDocs;
                    boolean nextIsSet;
                    
                    @Override
                    public boolean hasNext() {
                        return this.nextIsSet || this.setNext();
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                    
                    @Override
                    public Number next() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        assert this.nextIsSet;
                        this.nextIsSet = false;
                        return this.nextValue;
                    }
                    
                    private boolean setNext() {
                        while (this.readerUpto != numReaders) {
                            if (this.docIDUpto == this.currentMaxDoc) {
                                ++this.readerUpto;
                                if (this.readerUpto < numReaders) {
                                    this.currentLiveDocs = mergeState.liveDocs[this.readerUpto];
                                    this.currentMaxDoc = mergeState.maxDocs[this.readerUpto];
                                }
                                this.docIDUpto = 0;
                            }
                            else {
                                if (this.currentLiveDocs == null || this.currentLiveDocs.get(this.docIDUpto)) {
                                    this.nextIsSet = true;
                                    final SortedNumericDocValues dv = dvs[this.readerUpto];
                                    dv.setDocument(this.docIDUpto);
                                    this.nextValue = dv.count();
                                    ++this.docIDUpto;
                                    return true;
                                }
                                ++this.docIDUpto;
                            }
                        }
                        return false;
                    }
                };
            }
        }, new Iterable<Number>() {
            @Override
            public Iterator<Number> iterator() {
                return new Iterator<Number>() {
                    int readerUpto = -1;
                    int docIDUpto;
                    long nextValue;
                    int currentMaxDoc;
                    Bits currentLiveDocs;
                    boolean nextIsSet;
                    int valueUpto;
                    int valueLength;
                    
                    @Override
                    public boolean hasNext() {
                        return this.nextIsSet || this.setNext();
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                    
                    @Override
                    public Number next() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        assert this.nextIsSet;
                        this.nextIsSet = false;
                        return this.nextValue;
                    }
                    
                    private boolean setNext() {
                        while (this.readerUpto != numReaders) {
                            if (this.valueUpto < this.valueLength) {
                                this.nextValue = dvs[this.readerUpto].valueAt(this.valueUpto);
                                ++this.valueUpto;
                                return this.nextIsSet = true;
                            }
                            if (this.docIDUpto == this.currentMaxDoc) {
                                ++this.readerUpto;
                                if (this.readerUpto < numReaders) {
                                    this.currentLiveDocs = mergeState.liveDocs[this.readerUpto];
                                    this.currentMaxDoc = mergeState.maxDocs[this.readerUpto];
                                }
                                this.docIDUpto = 0;
                            }
                            else if (this.currentLiveDocs == null || this.currentLiveDocs.get(this.docIDUpto)) {
                                assert this.docIDUpto < this.currentMaxDoc;
                                final SortedNumericDocValues dv = dvs[this.readerUpto];
                                dv.setDocument(this.docIDUpto);
                                this.valueUpto = 0;
                                this.valueLength = dv.count();
                                ++this.docIDUpto;
                            }
                            else {
                                ++this.docIDUpto;
                            }
                        }
                        return false;
                    }
                };
            }
        });
    }
    
    public void mergeSortedField(final FieldInfo fieldInfo, final MergeState mergeState, final List<SortedDocValues> toMerge) throws IOException {
        final int numReaders = toMerge.size();
        final SortedDocValues[] dvs = toMerge.toArray(new SortedDocValues[numReaders]);
        final TermsEnum[] liveTerms = new TermsEnum[dvs.length];
        final long[] weights = new long[liveTerms.length];
        for (int sub = 0; sub < numReaders; ++sub) {
            final SortedDocValues dv = dvs[sub];
            final Bits liveDocs = mergeState.liveDocs[sub];
            final int maxDoc = mergeState.maxDocs[sub];
            if (liveDocs == null) {
                liveTerms[sub] = dv.termsEnum();
                weights[sub] = dv.getValueCount();
            }
            else {
                final LongBitSet bitset = new LongBitSet(dv.getValueCount());
                for (int i = 0; i < maxDoc; ++i) {
                    if (liveDocs.get(i)) {
                        final int ord = dv.getOrd(i);
                        if (ord >= 0) {
                            bitset.set(ord);
                        }
                    }
                }
                liveTerms[sub] = new BitsFilteredTermsEnum(dv.termsEnum(), bitset);
                weights[sub] = bitset.cardinality();
            }
        }
        final MultiDocValues.OrdinalMap map = MultiDocValues.OrdinalMap.build(this, liveTerms, weights, 0.0f);
        this.addSortedField(fieldInfo, new Iterable<BytesRef>() {
            @Override
            public Iterator<BytesRef> iterator() {
                return new Iterator<BytesRef>() {
                    int currentOrd;
                    
                    @Override
                    public boolean hasNext() {
                        return this.currentOrd < map.getValueCount();
                    }
                    
                    @Override
                    public BytesRef next() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        final int segmentNumber = map.getFirstSegmentNumber(this.currentOrd);
                        final int segmentOrd = (int)map.getFirstSegmentOrd(this.currentOrd);
                        final BytesRef term = dvs[segmentNumber].lookupOrd(segmentOrd);
                        ++this.currentOrd;
                        return term;
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        }, new Iterable<Number>() {
            @Override
            public Iterator<Number> iterator() {
                return new Iterator<Number>() {
                    int readerUpto = -1;
                    int docIDUpto;
                    int nextValue;
                    int currentMaxDoc;
                    Bits currentLiveDocs;
                    LongValues currentMap;
                    boolean nextIsSet;
                    
                    @Override
                    public boolean hasNext() {
                        return this.nextIsSet || this.setNext();
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                    
                    @Override
                    public Number next() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        assert this.nextIsSet;
                        this.nextIsSet = false;
                        return this.nextValue;
                    }
                    
                    private boolean setNext() {
                        while (this.readerUpto != numReaders) {
                            if (this.docIDUpto == this.currentMaxDoc) {
                                ++this.readerUpto;
                                if (this.readerUpto < numReaders) {
                                    this.currentMap = map.getGlobalOrds(this.readerUpto);
                                    this.currentLiveDocs = mergeState.liveDocs[this.readerUpto];
                                    this.currentMaxDoc = mergeState.maxDocs[this.readerUpto];
                                }
                                this.docIDUpto = 0;
                            }
                            else {
                                if (this.currentLiveDocs == null || this.currentLiveDocs.get(this.docIDUpto)) {
                                    this.nextIsSet = true;
                                    final int segOrd = dvs[this.readerUpto].getOrd(this.docIDUpto);
                                    this.nextValue = ((segOrd == -1) ? -1 : ((int)this.currentMap.get(segOrd)));
                                    ++this.docIDUpto;
                                    return true;
                                }
                                ++this.docIDUpto;
                            }
                        }
                        return false;
                    }
                };
            }
        });
    }
    
    public void mergeSortedSetField(final FieldInfo fieldInfo, final MergeState mergeState, final List<SortedSetDocValues> toMerge) throws IOException {
        final SortedSetDocValues[] dvs = toMerge.toArray(new SortedSetDocValues[toMerge.size()]);
        final int numReaders = mergeState.maxDocs.length;
        final TermsEnum[] liveTerms = new TermsEnum[dvs.length];
        final long[] weights = new long[liveTerms.length];
        for (int sub = 0; sub < liveTerms.length; ++sub) {
            final SortedSetDocValues dv = dvs[sub];
            final Bits liveDocs = mergeState.liveDocs[sub];
            final int maxDoc = mergeState.maxDocs[sub];
            if (liveDocs == null) {
                liveTerms[sub] = dv.termsEnum();
                weights[sub] = dv.getValueCount();
            }
            else {
                final LongBitSet bitset = new LongBitSet(dv.getValueCount());
                for (int i = 0; i < maxDoc; ++i) {
                    if (liveDocs.get(i)) {
                        dv.setDocument(i);
                        long ord;
                        while ((ord = dv.nextOrd()) != -1L) {
                            bitset.set(ord);
                        }
                    }
                }
                liveTerms[sub] = new BitsFilteredTermsEnum(dv.termsEnum(), bitset);
                weights[sub] = bitset.cardinality();
            }
        }
        final MultiDocValues.OrdinalMap map = MultiDocValues.OrdinalMap.build(this, liveTerms, weights, 0.0f);
        this.addSortedSetField(fieldInfo, new Iterable<BytesRef>() {
            @Override
            public Iterator<BytesRef> iterator() {
                return new Iterator<BytesRef>() {
                    long currentOrd;
                    
                    @Override
                    public boolean hasNext() {
                        return this.currentOrd < map.getValueCount();
                    }
                    
                    @Override
                    public BytesRef next() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        final int segmentNumber = map.getFirstSegmentNumber(this.currentOrd);
                        final long segmentOrd = map.getFirstSegmentOrd(this.currentOrd);
                        final BytesRef term = dvs[segmentNumber].lookupOrd(segmentOrd);
                        ++this.currentOrd;
                        return term;
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        }, new Iterable<Number>() {
            @Override
            public Iterator<Number> iterator() {
                return new Iterator<Number>() {
                    int readerUpto = -1;
                    int docIDUpto;
                    int nextValue;
                    int currentMaxDoc;
                    Bits currentLiveDocs;
                    boolean nextIsSet;
                    
                    @Override
                    public boolean hasNext() {
                        return this.nextIsSet || this.setNext();
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                    
                    @Override
                    public Number next() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        assert this.nextIsSet;
                        this.nextIsSet = false;
                        return this.nextValue;
                    }
                    
                    private boolean setNext() {
                        while (this.readerUpto != numReaders) {
                            if (this.docIDUpto == this.currentMaxDoc) {
                                ++this.readerUpto;
                                if (this.readerUpto < numReaders) {
                                    this.currentLiveDocs = mergeState.liveDocs[this.readerUpto];
                                    this.currentMaxDoc = mergeState.maxDocs[this.readerUpto];
                                }
                                this.docIDUpto = 0;
                            }
                            else {
                                if (this.currentLiveDocs == null || this.currentLiveDocs.get(this.docIDUpto)) {
                                    this.nextIsSet = true;
                                    final SortedSetDocValues dv = dvs[this.readerUpto];
                                    dv.setDocument(this.docIDUpto);
                                    this.nextValue = 0;
                                    while (dv.nextOrd() != -1L) {
                                        ++this.nextValue;
                                    }
                                    ++this.docIDUpto;
                                    return true;
                                }
                                ++this.docIDUpto;
                            }
                        }
                        return false;
                    }
                };
            }
        }, new Iterable<Number>() {
            @Override
            public Iterator<Number> iterator() {
                return new Iterator<Number>() {
                    int readerUpto = -1;
                    int docIDUpto;
                    long nextValue;
                    int currentMaxDoc;
                    Bits currentLiveDocs;
                    LongValues currentMap;
                    boolean nextIsSet;
                    long[] ords = new long[8];
                    int ordUpto;
                    int ordLength;
                    
                    @Override
                    public boolean hasNext() {
                        return this.nextIsSet || this.setNext();
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                    
                    @Override
                    public Number next() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        assert this.nextIsSet;
                        this.nextIsSet = false;
                        return this.nextValue;
                    }
                    
                    private boolean setNext() {
                        while (this.readerUpto != numReaders) {
                            if (this.ordUpto < this.ordLength) {
                                this.nextValue = this.ords[this.ordUpto];
                                ++this.ordUpto;
                                return this.nextIsSet = true;
                            }
                            if (this.docIDUpto == this.currentMaxDoc) {
                                ++this.readerUpto;
                                if (this.readerUpto < numReaders) {
                                    this.currentMap = map.getGlobalOrds(this.readerUpto);
                                    this.currentLiveDocs = mergeState.liveDocs[this.readerUpto];
                                    this.currentMaxDoc = mergeState.maxDocs[this.readerUpto];
                                }
                                this.docIDUpto = 0;
                            }
                            else if (this.currentLiveDocs == null || this.currentLiveDocs.get(this.docIDUpto)) {
                                assert this.docIDUpto < this.currentMaxDoc;
                                final SortedSetDocValues dv = dvs[this.readerUpto];
                                dv.setDocument(this.docIDUpto);
                                final int n = 0;
                                this.ordLength = n;
                                this.ordUpto = n;
                                long ord;
                                while ((ord = dv.nextOrd()) != -1L) {
                                    if (this.ordLength == this.ords.length) {
                                        this.ords = ArrayUtil.grow(this.ords, this.ordLength + 1);
                                    }
                                    this.ords[this.ordLength] = this.currentMap.get(ord);
                                    ++this.ordLength;
                                }
                                ++this.docIDUpto;
                            }
                            else {
                                ++this.docIDUpto;
                            }
                        }
                        return false;
                    }
                };
            }
        });
    }
    
    public static boolean isSingleValued(final Iterable<Number> docToValueCount) {
        for (final Number count : docToValueCount) {
            if (count.longValue() > 1L) {
                return false;
            }
        }
        return true;
    }
    
    public static Iterable<Number> singletonView(final Iterable<Number> docToValueCount, final Iterable<Number> values, final Number missingValue) {
        assert isSingleValued(docToValueCount);
        return new Iterable<Number>() {
            @Override
            public Iterator<Number> iterator() {
                final Iterator<Number> countIterator = docToValueCount.iterator();
                final Iterator<Number> valuesIterator = values.iterator();
                return new Iterator<Number>() {
                    @Override
                    public boolean hasNext() {
                        return countIterator.hasNext();
                    }
                    
                    @Override
                    public Number next() {
                        final int count = countIterator.next().intValue();
                        if (count == 0) {
                            return missingValue;
                        }
                        return valuesIterator.next();
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }
    
    static class BitsFilteredTermsEnum extends FilteredTermsEnum
    {
        final LongBitSet liveTerms;
        
        BitsFilteredTermsEnum(final TermsEnum in, final LongBitSet liveTerms) {
            super(in, false);
            assert liveTerms != null;
            this.liveTerms = liveTerms;
        }
        
        @Override
        protected AcceptStatus accept(final BytesRef term) throws IOException {
            if (this.liveTerms.get(this.ord())) {
                return AcceptStatus.YES;
            }
            return AcceptStatus.NO;
        }
    }
}
