package org.apache.lucene.codecs.lucene54;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.lucene.util.LongsRef;
import java.util.SortedSet;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.util.PagedBytes;
import org.apache.lucene.util.StringHelper;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.packed.MonotonicBlockPackedWriter;
import org.apache.lucene.store.RAMOutputStream;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.packed.DirectMonotonicWriter;
import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.apache.lucene.util.packed.DirectWriter;
import org.apache.lucene.util.MathUtil;
import java.util.HashSet;
import org.apache.lucene.index.FieldInfo;
import java.io.IOException;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.store.IndexOutput;
import java.io.Closeable;
import org.apache.lucene.codecs.DocValuesConsumer;

final class Lucene54DocValuesConsumer extends DocValuesConsumer implements Closeable
{
    IndexOutput data;
    IndexOutput meta;
    final int maxDoc;
    
    public Lucene54DocValuesConsumer(final SegmentWriteState state, final String dataCodec, final String dataExtension, final String metaCodec, final String metaExtension) throws IOException {
        boolean success = false;
        try {
            final String dataName = IndexFileNames.segmentFileName(state.segmentInfo.name, state.segmentSuffix, dataExtension);
            CodecUtil.writeIndexHeader(this.data = state.directory.createOutput(dataName, state.context), dataCodec, 0, state.segmentInfo.getId(), state.segmentSuffix);
            final String metaName = IndexFileNames.segmentFileName(state.segmentInfo.name, state.segmentSuffix, metaExtension);
            CodecUtil.writeIndexHeader(this.meta = state.directory.createOutput(metaName, state.context), metaCodec, 0, state.segmentInfo.getId(), state.segmentSuffix);
            this.maxDoc = state.segmentInfo.maxDoc();
            success = true;
        }
        finally {
            if (!success) {
                IOUtils.closeWhileHandlingException(this);
            }
        }
    }
    
    @Override
    public void addNumericField(final FieldInfo field, final Iterable<Number> values) throws IOException {
        this.addNumericField(field, values, NumberType.VALUE);
    }
    
    void addNumericField(final FieldInfo field, final Iterable<Number> values, final NumberType numberType) throws IOException {
        long count = 0L;
        long minValue = Long.MAX_VALUE;
        long maxValue = Long.MIN_VALUE;
        long gcd = 0L;
        long missingCount = 0L;
        long zeroCount = 0L;
        HashSet<Long> uniqueValues = null;
        long missingOrdCount = 0L;
        if (numberType == NumberType.VALUE) {
            uniqueValues = new HashSet<Long>();
            for (final Number nv : values) {
                long v;
                if (nv == null) {
                    v = 0L;
                    ++missingCount;
                    ++zeroCount;
                }
                else {
                    v = nv.longValue();
                    if (v == 0L) {
                        ++zeroCount;
                    }
                }
                if (gcd != 1L) {
                    if (v < -4611686018427387904L || v > 4611686018427387903L) {
                        gcd = 1L;
                    }
                    else if (count != 0L) {
                        gcd = MathUtil.gcd(gcd, v - minValue);
                    }
                }
                minValue = Math.min(minValue, v);
                maxValue = Math.max(maxValue, v);
                if (uniqueValues != null && uniqueValues.add(v) && uniqueValues.size() > 256) {
                    uniqueValues = null;
                }
                ++count;
            }
        }
        else {
            for (final Number nv : values) {
                final long v = nv.longValue();
                if (v == -1L) {
                    ++missingOrdCount;
                }
                minValue = Math.min(minValue, v);
                maxValue = Math.max(maxValue, v);
                ++count;
            }
        }
        final long delta = maxValue - minValue;
        final int deltaBitsRequired = DirectWriter.unsignedBitsRequired(delta);
        final int tableBitsRequired = (uniqueValues == null) ? Integer.MAX_VALUE : DirectWriter.bitsRequired(uniqueValues.size() - 1);
        boolean sparse = false;
        switch (numberType) {
            case VALUE: {
                sparse = (missingCount / (double)count >= 0.99);
                break;
            }
            case ORDINAL: {
                sparse = (missingOrdCount / (double)count >= 0.99);
                break;
            }
            default: {
                throw new AssertionError();
            }
        }
        int format;
        if (uniqueValues != null && count <= 2147483647L && (uniqueValues.size() == 1 || (uniqueValues.size() == 2 && missingCount > 0L && zeroCount == missingCount))) {
            format = 4;
        }
        else if (sparse && count >= 1024L) {
            format = 5;
        }
        else if (uniqueValues != null && tableBitsRequired < deltaBitsRequired) {
            format = 2;
        }
        else if (gcd != 0L && gcd != 1L) {
            final long gcdDelta = (maxValue - minValue) / gcd;
            final long gcdBitsRequired = DirectWriter.unsignedBitsRequired(gcdDelta);
            format = ((gcdBitsRequired < deltaBitsRequired) ? 1 : 0);
        }
        else {
            format = 0;
        }
        this.meta.writeVInt(field.number);
        this.meta.writeByte((byte)0);
        this.meta.writeVInt(format);
        if (format == 5) {
            this.meta.writeLong(this.data.getFilePointer());
            long numDocsWithValue = 0L;
            switch (numberType) {
                case VALUE: {
                    numDocsWithValue = count - missingCount;
                    break;
                }
                case ORDINAL: {
                    numDocsWithValue = count - missingOrdCount;
                    break;
                }
                default: {
                    throw new AssertionError();
                }
            }
            final long maxDoc = this.writeSparseMissingBitset(values, numberType, numDocsWithValue);
            assert maxDoc == count;
        }
        else if (missingCount == 0L) {
            this.meta.writeLong(-1L);
        }
        else if (missingCount == count) {
            this.meta.writeLong(-2L);
        }
        else {
            this.meta.writeLong(this.data.getFilePointer());
            this.writeMissingBitset(values);
        }
        this.meta.writeLong(this.data.getFilePointer());
        this.meta.writeVLong(count);
        switch (format) {
            case 4: {
                this.meta.writeLong((minValue < 0L) ? Collections.min((Collection<? extends Long>)uniqueValues) : Collections.max((Collection<? extends Long>)uniqueValues));
                break;
            }
            case 1: {
                this.meta.writeLong(minValue);
                this.meta.writeLong(gcd);
                final long maxDelta = (maxValue - minValue) / gcd;
                final int bits = DirectWriter.unsignedBitsRequired(maxDelta);
                this.meta.writeVInt(bits);
                final DirectWriter quotientWriter = DirectWriter.getInstance(this.data, count, bits);
                for (final Number nv2 : values) {
                    final long value = (nv2 == null) ? 0L : nv2.longValue();
                    quotientWriter.add((value - minValue) / gcd);
                }
                quotientWriter.finish();
                break;
            }
            case 0: {
                final long minDelta = (delta < 0L) ? 0L : minValue;
                this.meta.writeLong(minDelta);
                this.meta.writeVInt(deltaBitsRequired);
                final DirectWriter writer = DirectWriter.getInstance(this.data, count, deltaBitsRequired);
                for (final Number nv3 : values) {
                    final long v2 = (nv3 == null) ? 0L : nv3.longValue();
                    writer.add(v2 - minDelta);
                }
                writer.finish();
                break;
            }
            case 2: {
                final Long[] decode = uniqueValues.toArray(new Long[uniqueValues.size()]);
                Arrays.sort(decode);
                final HashMap<Long, Integer> encode = new HashMap<Long, Integer>();
                this.meta.writeVInt(decode.length);
                for (int i = 0; i < decode.length; ++i) {
                    this.meta.writeLong(decode[i]);
                    encode.put(decode[i], i);
                }
                this.meta.writeVInt(tableBitsRequired);
                final DirectWriter ordsWriter = DirectWriter.getInstance(this.data, count, tableBitsRequired);
                for (final Number nv4 : values) {
                    ordsWriter.add(encode.get((nv4 == null) ? 0L : nv4.longValue()));
                }
                ordsWriter.finish();
                break;
            }
            case 5: {
                Iterable<Number> filteredMissingValues = null;
                switch (numberType) {
                    case VALUE: {
                        this.meta.writeByte((byte)0);
                        filteredMissingValues = new Iterable<Number>() {
                            @Override
                            public Iterator<Number> iterator() {
                                return (Iterator<Number>)filter((Iterator<?>)values.iterator(), (Predicate<? super Object>)new Predicate<Number>() {
                                    @Override
                                    boolean apply(final Number input) {
                                        return input != null;
                                    }
                                });
                            }
                        };
                        break;
                    }
                    case ORDINAL: {
                        this.meta.writeByte((byte)1);
                        filteredMissingValues = new Iterable<Number>() {
                            @Override
                            public Iterator<Number> iterator() {
                                return (Iterator<Number>)filter((Iterator<?>)values.iterator(), (Predicate<? super Object>)new Predicate<Number>() {
                                    @Override
                                    boolean apply(final Number input) {
                                        return input.longValue() != -1L;
                                    }
                                });
                            }
                        };
                        break;
                    }
                    default: {
                        throw new AssertionError();
                    }
                }
                this.addNumericField(field, filteredMissingValues, numberType);
                break;
            }
            default: {
                throw new AssertionError();
            }
        }
        this.meta.writeLong(this.data.getFilePointer());
    }
    
    private static <T> Iterator<T> filter(final Iterator<? extends T> iterator, final Predicate<? super T> predicate) {
        return new Iterator<T>() {
            boolean hasNext = false;
            T next;
            
            @Override
            public boolean hasNext() {
                if (this.hasNext) {
                    return true;
                }
                while (iterator.hasNext()) {
                    this.next = iterator.next();
                    if (predicate.apply(this.next)) {
                        return this.hasNext = true;
                    }
                }
                this.next = null;
                return this.hasNext = false;
            }
            
            @Override
            public T next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                final T next = this.next;
                this.hasNext = false;
                this.next = null;
                return next;
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    void writeMissingBitset(final Iterable<?> values) throws IOException {
        byte bits = 0;
        int count = 0;
        for (final Object v : values) {
            if (count == 8) {
                this.data.writeByte(bits);
                count = 0;
                bits = 0;
            }
            if (v != null) {
                bits |= (byte)(1 << (count & 0x7));
            }
            ++count;
        }
        if (count > 0) {
            this.data.writeByte(bits);
        }
    }
    
    long writeSparseMissingBitset(final Iterable<Number> values, final NumberType numberType, final long numDocsWithValue) throws IOException {
        this.meta.writeVLong(numDocsWithValue);
        this.meta.writeVInt(16);
        final DirectMonotonicWriter docIdsWriter = DirectMonotonicWriter.getInstance(this.meta, this.data, numDocsWithValue, 16);
        long docID = 0L;
        for (final Number nv : values) {
            switch (numberType) {
                case VALUE: {
                    if (nv != null) {
                        docIdsWriter.add(docID);
                        break;
                    }
                    break;
                }
                case ORDINAL: {
                    if (nv.longValue() != -1L) {
                        docIdsWriter.add(docID);
                        break;
                    }
                    break;
                }
                default: {
                    throw new AssertionError();
                }
            }
            ++docID;
        }
        docIdsWriter.finish();
        return docID;
    }
    
    @Override
    public void addBinaryField(final FieldInfo field, final Iterable<BytesRef> values) throws IOException {
        this.meta.writeVInt(field.number);
        this.meta.writeByte((byte)1);
        int minLength = Integer.MAX_VALUE;
        int maxLength = Integer.MIN_VALUE;
        final long startFP = this.data.getFilePointer();
        long count = 0L;
        long missingCount = 0L;
        for (final BytesRef v : values) {
            int length;
            if (v == null) {
                length = 0;
                ++missingCount;
            }
            else {
                length = v.length;
            }
            minLength = Math.min(minLength, length);
            maxLength = Math.max(maxLength, length);
            if (v != null) {
                this.data.writeBytes(v.bytes, v.offset, v.length);
            }
            ++count;
        }
        this.meta.writeVInt((minLength != maxLength) ? 1 : 0);
        if (missingCount == 0L) {
            this.meta.writeLong(-1L);
        }
        else if (missingCount == count) {
            this.meta.writeLong(-2L);
        }
        else {
            this.meta.writeLong(this.data.getFilePointer());
            this.writeMissingBitset(values);
        }
        this.meta.writeVInt(minLength);
        this.meta.writeVInt(maxLength);
        this.meta.writeVLong(count);
        this.meta.writeLong(startFP);
        if (minLength != maxLength) {
            this.meta.writeLong(this.data.getFilePointer());
            this.meta.writeVInt(16);
            final DirectMonotonicWriter writer = DirectMonotonicWriter.getInstance(this.meta, this.data, count + 1L, 16);
            long addr = 0L;
            writer.add(addr);
            for (final BytesRef v2 : values) {
                if (v2 != null) {
                    addr += v2.length;
                }
                writer.add(addr);
            }
            writer.finish();
            this.meta.writeLong(this.data.getFilePointer());
        }
    }
    
    private void addTermsDict(final FieldInfo field, final Iterable<BytesRef> values) throws IOException {
        int minLength = Integer.MAX_VALUE;
        int maxLength = Integer.MIN_VALUE;
        long numValues = 0L;
        for (final BytesRef v : values) {
            minLength = Math.min(minLength, v.length);
            maxLength = Math.max(maxLength, v.length);
            ++numValues;
        }
        if (minLength == maxLength) {
            this.addBinaryField(field, values);
        }
        else if (numValues < 1024L) {
            this.addBinaryField(field, values);
        }
        else {
            assert numValues > 0L;
            this.meta.writeVInt(field.number);
            this.meta.writeByte((byte)1);
            this.meta.writeVInt(2);
            this.meta.writeLong(-1L);
            final long startFP = this.data.getFilePointer();
            RAMOutputStream addressBuffer = new RAMOutputStream();
            MonotonicBlockPackedWriter termAddresses = new MonotonicBlockPackedWriter(addressBuffer, 16384);
            final RAMOutputStream bytesBuffer = new RAMOutputStream();
            final RAMOutputStream headerBuffer = new RAMOutputStream();
            final BytesRefBuilder lastTerm = new BytesRefBuilder();
            lastTerm.grow(maxLength);
            long count = 0L;
            final int[] suffixDeltas = new int[16];
            for (final BytesRef v2 : values) {
                final int termPosition = (int)(count & 0xFL);
                if (termPosition == 0) {
                    termAddresses.add(this.data.getFilePointer() - startFP);
                    headerBuffer.writeVInt(v2.length);
                    headerBuffer.writeBytes(v2.bytes, v2.offset, v2.length);
                    lastTerm.copyBytes(v2);
                }
                else {
                    final int sharedPrefix = Math.min(255, StringHelper.bytesDifference(lastTerm.get(), v2));
                    bytesBuffer.writeByte((byte)sharedPrefix);
                    bytesBuffer.writeBytes(v2.bytes, v2.offset + sharedPrefix, v2.length - sharedPrefix);
                    suffixDeltas[termPosition] = v2.length - sharedPrefix - 1;
                }
                ++count;
                if ((count & 0xFL) == 0x0L) {
                    this.flushTermsDictBlock(headerBuffer, bytesBuffer, suffixDeltas);
                }
            }
            final int leftover = (int)(count & 0xFL);
            if (leftover > 0) {
                Arrays.fill(suffixDeltas, leftover, suffixDeltas.length, 0);
                this.flushTermsDictBlock(headerBuffer, bytesBuffer, suffixDeltas);
            }
            final long indexStartFP = this.data.getFilePointer();
            termAddresses.finish();
            addressBuffer.writeTo(this.data);
            addressBuffer = null;
            termAddresses = null;
            this.meta.writeVInt(minLength);
            this.meta.writeVInt(maxLength);
            this.meta.writeVLong(count);
            this.meta.writeLong(startFP);
            this.meta.writeLong(indexStartFP);
            this.meta.writeVInt(2);
            this.meta.writeVInt(16384);
            this.addReverseTermIndex(field, values, maxLength);
        }
    }
    
    private void flushTermsDictBlock(final RAMOutputStream headerBuffer, final RAMOutputStream bytesBuffer, final int[] suffixDeltas) throws IOException {
        boolean twoByte = false;
        for (int i = 1; i < suffixDeltas.length; ++i) {
            if (suffixDeltas[i] > 254) {
                twoByte = true;
            }
        }
        if (twoByte) {
            headerBuffer.writeByte((byte)(-1));
            for (int i = 1; i < suffixDeltas.length; ++i) {
                headerBuffer.writeShort((short)suffixDeltas[i]);
            }
        }
        else {
            for (int i = 1; i < suffixDeltas.length; ++i) {
                headerBuffer.writeByte((byte)suffixDeltas[i]);
            }
        }
        headerBuffer.writeTo(this.data);
        headerBuffer.reset();
        bytesBuffer.writeTo(this.data);
        bytesBuffer.reset();
    }
    
    private void addReverseTermIndex(final FieldInfo field, final Iterable<BytesRef> values, final int maxLength) throws IOException {
        long count = 0L;
        final BytesRefBuilder priorTerm = new BytesRefBuilder();
        priorTerm.grow(maxLength);
        final BytesRef indexTerm = new BytesRef();
        final long startFP = this.data.getFilePointer();
        final PagedBytes pagedBytes = new PagedBytes(15);
        final MonotonicBlockPackedWriter addresses = new MonotonicBlockPackedWriter(this.data, 16384);
        for (final BytesRef b : values) {
            final int termPosition = (int)(count & 0x3FFL);
            if (termPosition == 0) {
                final int len = StringHelper.sortKeyLength(priorTerm.get(), b);
                indexTerm.bytes = b.bytes;
                indexTerm.offset = b.offset;
                indexTerm.length = len;
                addresses.add(pagedBytes.copyUsingLengthPrefix(indexTerm));
            }
            else if (termPosition == 1023) {
                priorTerm.copyBytes(b);
            }
            ++count;
        }
        addresses.finish();
        final long numBytes = pagedBytes.getPointer();
        pagedBytes.freeze(true);
        final PagedBytes.PagedBytesDataInput in = pagedBytes.getDataInput();
        this.meta.writeLong(startFP);
        this.data.writeVLong(numBytes);
        this.data.copyBytes(in, numBytes);
    }
    
    @Override
    public void addSortedField(final FieldInfo field, final Iterable<BytesRef> values, final Iterable<Number> docToOrd) throws IOException {
        this.meta.writeVInt(field.number);
        this.meta.writeByte((byte)2);
        this.addTermsDict(field, values);
        this.addNumericField(field, docToOrd, NumberType.ORDINAL);
    }
    
    @Override
    public void addSortedNumericField(final FieldInfo field, final Iterable<Number> docToValueCount, final Iterable<Number> values) throws IOException {
        this.meta.writeVInt(field.number);
        this.meta.writeByte((byte)4);
        if (DocValuesConsumer.isSingleValued(docToValueCount)) {
            this.meta.writeVInt(1);
            this.addNumericField(field, DocValuesConsumer.singletonView(docToValueCount, values, null));
        }
        else {
            final SortedSet<LongsRef> uniqueValueSets = this.uniqueValueSets(docToValueCount, values);
            if (uniqueValueSets != null) {
                this.meta.writeVInt(2);
                this.writeDictionary(uniqueValueSets);
                this.addNumericField(field, this.docToSetId(uniqueValueSets, docToValueCount, values), NumberType.ORDINAL);
            }
            else {
                this.meta.writeVInt(0);
                this.addNumericField(field, values, NumberType.VALUE);
                this.addOrdIndex(field, docToValueCount);
            }
        }
    }
    
    @Override
    public void addSortedSetField(final FieldInfo field, final Iterable<BytesRef> values, final Iterable<Number> docToOrdCount, final Iterable<Number> ords) throws IOException {
        this.meta.writeVInt(field.number);
        this.meta.writeByte((byte)3);
        if (DocValuesConsumer.isSingleValued(docToOrdCount)) {
            this.meta.writeVInt(1);
            this.addSortedField(field, values, DocValuesConsumer.singletonView(docToOrdCount, ords, -1L));
        }
        else {
            final SortedSet<LongsRef> uniqueValueSets = this.uniqueValueSets(docToOrdCount, ords);
            if (uniqueValueSets != null) {
                this.meta.writeVInt(2);
                this.writeDictionary(uniqueValueSets);
                this.addTermsDict(field, values);
                this.addNumericField(field, this.docToSetId(uniqueValueSets, docToOrdCount, ords), NumberType.ORDINAL);
            }
            else {
                this.meta.writeVInt(0);
                this.addTermsDict(field, values);
                this.addNumericField(field, ords, NumberType.ORDINAL);
                this.addOrdIndex(field, docToOrdCount);
            }
        }
    }
    
    private SortedSet<LongsRef> uniqueValueSets(final Iterable<Number> docToValueCount, final Iterable<Number> values) {
        final Set<LongsRef> uniqueValueSet = new HashSet<LongsRef>();
        final LongsRef docValues = new LongsRef(256);
        final Iterator<Number> valueCountIterator = docToValueCount.iterator();
        final Iterator<Number> valueIterator = values.iterator();
        int totalDictSize = 0;
        while (valueCountIterator.hasNext()) {
            docValues.length = valueCountIterator.next().intValue();
            if (docValues.length > 256) {
                return null;
            }
            for (int i = 0; i < docValues.length; ++i) {
                docValues.longs[i] = valueIterator.next().longValue();
            }
            if (uniqueValueSet.contains(docValues)) {
                continue;
            }
            totalDictSize += docValues.length;
            if (totalDictSize > 256) {
                return null;
            }
            uniqueValueSet.add(new LongsRef(Arrays.copyOf(docValues.longs, docValues.length), 0, docValues.length));
        }
        assert !valueIterator.hasNext();
        return new TreeSet<LongsRef>(uniqueValueSet);
    }
    
    private void writeDictionary(final SortedSet<LongsRef> uniqueValueSets) throws IOException {
        int lengthSum = 0;
        for (final LongsRef longs : uniqueValueSets) {
            lengthSum += longs.length;
        }
        this.meta.writeInt(lengthSum);
        for (final LongsRef valueSet : uniqueValueSets) {
            for (int i = 0; i < valueSet.length; ++i) {
                this.meta.writeLong(valueSet.longs[valueSet.offset + i]);
            }
        }
        this.meta.writeInt(uniqueValueSets.size());
        for (final LongsRef valueSet : uniqueValueSets) {
            this.meta.writeInt(valueSet.length);
        }
    }
    
    private Iterable<Number> docToSetId(final SortedSet<LongsRef> uniqueValueSets, final Iterable<Number> docToValueCount, final Iterable<Number> values) {
        final Map<LongsRef, Integer> setIds = new HashMap<LongsRef, Integer>();
        int i = 0;
        for (final LongsRef set : uniqueValueSets) {
            setIds.put(set, i++);
        }
        assert i == uniqueValueSets.size();
        return new Iterable<Number>() {
            @Override
            public Iterator<Number> iterator() {
                final Iterator<Number> valueCountIterator = docToValueCount.iterator();
                final Iterator<Number> valueIterator = values.iterator();
                final LongsRef docValues = new LongsRef(256);
                return new Iterator<Number>() {
                    @Override
                    public boolean hasNext() {
                        return valueCountIterator.hasNext();
                    }
                    
                    @Override
                    public Number next() {
                        docValues.length = valueCountIterator.next().intValue();
                        for (int i = 0; i < docValues.length; ++i) {
                            docValues.longs[i] = valueIterator.next().longValue();
                        }
                        final Integer id = setIds.get(docValues);
                        assert id != null;
                        return id;
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }
    
    private void addOrdIndex(final FieldInfo field, final Iterable<Number> values) throws IOException {
        this.meta.writeVInt(field.number);
        this.meta.writeByte((byte)0);
        this.meta.writeVInt(3);
        this.meta.writeLong(-1L);
        this.meta.writeLong(this.data.getFilePointer());
        this.meta.writeVLong(this.maxDoc);
        this.meta.writeVInt(16);
        final DirectMonotonicWriter writer = DirectMonotonicWriter.getInstance(this.meta, this.data, this.maxDoc + 1, 16);
        long addr = 0L;
        writer.add(addr);
        for (final Number v : values) {
            addr += v.longValue();
            writer.add(addr);
        }
        writer.finish();
        this.meta.writeLong(this.data.getFilePointer());
    }
    
    @Override
    public void close() throws IOException {
        boolean success = false;
        try {
            if (this.meta != null) {
                this.meta.writeVInt(-1);
                CodecUtil.writeFooter(this.meta);
            }
            if (this.data != null) {
                CodecUtil.writeFooter(this.data);
            }
            success = true;
        }
        finally {
            if (success) {
                IOUtils.close(this.data, this.meta);
            }
            else {
                IOUtils.closeWhileHandlingException(this.data, this.meta);
            }
            final IndexOutput indexOutput = null;
            this.data = indexOutput;
            this.meta = indexOutput;
        }
    }
    
    enum NumberType
    {
        ORDINAL, 
        VALUE;
    }
    
    private abstract static class Predicate<T>
    {
        abstract boolean apply(final T p0);
    }
}
