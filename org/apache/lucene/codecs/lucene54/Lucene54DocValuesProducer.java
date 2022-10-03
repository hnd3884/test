package org.apache.lucene.codecs.lucene54;

import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.RandomAccessOrds;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.SortedNumericDocValues;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.util.PagedBytes;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.store.RandomAccessInput;
import org.apache.lucene.util.packed.DirectReader;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.LongValues;
import java.util.List;
import java.util.Collections;
import org.apache.lucene.util.Accountables;
import java.util.ArrayList;
import org.apache.lucene.util.Accountable;
import java.util.Collection;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.store.ChecksumIndexInput;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.SegmentReadState;
import java.io.IOException;
import java.util.HashMap;
import org.apache.lucene.util.packed.DirectMonotonicReader;
import org.apache.lucene.util.packed.MonotonicBlockPackedReader;
import org.apache.lucene.store.IndexInput;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Map;
import java.io.Closeable;
import org.apache.lucene.codecs.DocValuesProducer;

final class Lucene54DocValuesProducer extends DocValuesProducer implements Closeable
{
    private final Map<String, NumericEntry> numerics;
    private final Map<String, BinaryEntry> binaries;
    private final Map<String, SortedSetEntry> sortedSets;
    private final Map<String, SortedSetEntry> sortedNumerics;
    private final Map<String, NumericEntry> ords;
    private final Map<String, NumericEntry> ordIndexes;
    private final int numFields;
    private final AtomicLong ramBytesUsed;
    private final IndexInput data;
    private final int maxDoc;
    private final Map<String, MonotonicBlockPackedReader> addressInstances;
    private final Map<String, ReverseTermsIndex> reverseIndexInstances;
    private final Map<String, DirectMonotonicReader.Meta> directAddressesMeta;
    private final boolean merging;
    
    Lucene54DocValuesProducer(final Lucene54DocValuesProducer original) throws IOException {
        this.numerics = new HashMap<String, NumericEntry>();
        this.binaries = new HashMap<String, BinaryEntry>();
        this.sortedSets = new HashMap<String, SortedSetEntry>();
        this.sortedNumerics = new HashMap<String, SortedSetEntry>();
        this.ords = new HashMap<String, NumericEntry>();
        this.ordIndexes = new HashMap<String, NumericEntry>();
        this.addressInstances = new HashMap<String, MonotonicBlockPackedReader>();
        this.reverseIndexInstances = new HashMap<String, ReverseTermsIndex>();
        this.directAddressesMeta = new HashMap<String, DirectMonotonicReader.Meta>();
        assert Thread.holdsLock(original);
        this.numerics.putAll(original.numerics);
        this.binaries.putAll(original.binaries);
        this.sortedSets.putAll(original.sortedSets);
        this.sortedNumerics.putAll(original.sortedNumerics);
        this.ords.putAll(original.ords);
        this.ordIndexes.putAll(original.ordIndexes);
        this.numFields = original.numFields;
        this.ramBytesUsed = new AtomicLong(original.ramBytesUsed.get());
        this.data = original.data.clone();
        this.maxDoc = original.maxDoc;
        this.addressInstances.putAll(original.addressInstances);
        this.reverseIndexInstances.putAll(original.reverseIndexInstances);
        this.merging = true;
    }
    
    Lucene54DocValuesProducer(final SegmentReadState state, final String dataCodec, final String dataExtension, final String metaCodec, final String metaExtension) throws IOException {
        this.numerics = new HashMap<String, NumericEntry>();
        this.binaries = new HashMap<String, BinaryEntry>();
        this.sortedSets = new HashMap<String, SortedSetEntry>();
        this.sortedNumerics = new HashMap<String, SortedSetEntry>();
        this.ords = new HashMap<String, NumericEntry>();
        this.ordIndexes = new HashMap<String, NumericEntry>();
        this.addressInstances = new HashMap<String, MonotonicBlockPackedReader>();
        this.reverseIndexInstances = new HashMap<String, ReverseTermsIndex>();
        this.directAddressesMeta = new HashMap<String, DirectMonotonicReader.Meta>();
        final String metaName = IndexFileNames.segmentFileName(state.segmentInfo.name, state.segmentSuffix, metaExtension);
        this.maxDoc = state.segmentInfo.maxDoc();
        this.merging = false;
        this.ramBytesUsed = new AtomicLong(RamUsageEstimator.shallowSizeOfInstance(this.getClass()));
        int version = -1;
        int numFields = -1;
        try (final ChecksumIndexInput in = state.directory.openChecksumInput(metaName, state.context)) {
            Throwable priorE = null;
            try {
                version = CodecUtil.checkIndexHeader(in, metaCodec, 0, 0, state.segmentInfo.getId(), state.segmentSuffix);
                numFields = this.readFields(in, state.fieldInfos);
            }
            catch (final Throwable exception) {
                priorE = exception;
            }
            finally {
                CodecUtil.checkFooter(in, priorE);
            }
        }
        this.numFields = numFields;
        final String dataName = IndexFileNames.segmentFileName(state.segmentInfo.name, state.segmentSuffix, dataExtension);
        this.data = state.directory.openInput(dataName, state.context);
        boolean success = false;
        try {
            final int version2 = CodecUtil.checkIndexHeader(this.data, dataCodec, 0, 0, state.segmentInfo.getId(), state.segmentSuffix);
            if (version != version2) {
                throw new CorruptIndexException("Format versions mismatch: meta=" + version + ", data=" + version2, this.data);
            }
            CodecUtil.retrieveChecksum(this.data);
            success = true;
        }
        finally {
            if (!success) {
                IOUtils.closeWhileHandlingException(this.data);
            }
        }
    }
    
    private void readSortedField(final FieldInfo info, final IndexInput meta) throws IOException {
        if (meta.readVInt() != info.number) {
            throw new CorruptIndexException("sorted entry for field: " + info.name + " is corrupt", meta);
        }
        if (meta.readByte() != 1) {
            throw new CorruptIndexException("sorted entry for field: " + info.name + " is corrupt", meta);
        }
        final BinaryEntry b = this.readBinaryEntry(info, meta);
        this.binaries.put(info.name, b);
        if (meta.readVInt() != info.number) {
            throw new CorruptIndexException("sorted entry for field: " + info.name + " is corrupt", meta);
        }
        if (meta.readByte() != 0) {
            throw new CorruptIndexException("sorted entry for field: " + info.name + " is corrupt", meta);
        }
        final NumericEntry n = this.readNumericEntry(info, meta);
        this.ords.put(info.name, n);
    }
    
    private void readSortedSetFieldWithAddresses(final FieldInfo info, final IndexInput meta) throws IOException {
        if (meta.readVInt() != info.number) {
            throw new CorruptIndexException("sortedset entry for field: " + info.name + " is corrupt", meta);
        }
        if (meta.readByte() != 1) {
            throw new CorruptIndexException("sortedset entry for field: " + info.name + " is corrupt", meta);
        }
        final BinaryEntry b = this.readBinaryEntry(info, meta);
        this.binaries.put(info.name, b);
        if (meta.readVInt() != info.number) {
            throw new CorruptIndexException("sortedset entry for field: " + info.name + " is corrupt", meta);
        }
        if (meta.readByte() != 0) {
            throw new CorruptIndexException("sortedset entry for field: " + info.name + " is corrupt", meta);
        }
        final NumericEntry n1 = this.readNumericEntry(info, meta);
        this.ords.put(info.name, n1);
        if (meta.readVInt() != info.number) {
            throw new CorruptIndexException("sortedset entry for field: " + info.name + " is corrupt", meta);
        }
        if (meta.readByte() != 0) {
            throw new CorruptIndexException("sortedset entry for field: " + info.name + " is corrupt", meta);
        }
        final NumericEntry n2 = this.readNumericEntry(info, meta);
        this.ordIndexes.put(info.name, n2);
    }
    
    private void readSortedSetFieldWithTable(final FieldInfo info, final IndexInput meta) throws IOException {
        if (meta.readVInt() != info.number) {
            throw new CorruptIndexException("sortedset entry for field: " + info.name + " is corrupt", meta);
        }
        if (meta.readByte() != 1) {
            throw new CorruptIndexException("sortedset entry for field: " + info.name + " is corrupt", meta);
        }
        final BinaryEntry b = this.readBinaryEntry(info, meta);
        this.binaries.put(info.name, b);
        if (meta.readVInt() != info.number) {
            throw new CorruptIndexException("sortedset entry for field: " + info.name + " is corrupt", meta);
        }
        if (meta.readByte() != 0) {
            throw new CorruptIndexException("sortedset entry for field: " + info.name + " is corrupt", meta);
        }
        final NumericEntry n = this.readNumericEntry(info, meta);
        this.ords.put(info.name, n);
    }
    
    private int readFields(final IndexInput meta, final FieldInfos infos) throws IOException {
        int numFields = 0;
        for (int fieldNumber = meta.readVInt(); fieldNumber != -1; fieldNumber = meta.readVInt()) {
            ++numFields;
            final FieldInfo info = infos.fieldInfo(fieldNumber);
            if (info == null) {
                throw new CorruptIndexException("Invalid field number: " + fieldNumber, meta);
            }
            final byte type = meta.readByte();
            if (type == 0) {
                this.numerics.put(info.name, this.readNumericEntry(info, meta));
            }
            else if (type == 1) {
                final BinaryEntry b = this.readBinaryEntry(info, meta);
                this.binaries.put(info.name, b);
            }
            else if (type == 2) {
                this.readSortedField(info, meta);
            }
            else if (type == 3) {
                final SortedSetEntry ss = this.readSortedSetEntry(meta);
                this.sortedSets.put(info.name, ss);
                if (ss.format == 0) {
                    this.readSortedSetFieldWithAddresses(info, meta);
                }
                else if (ss.format == 2) {
                    this.readSortedSetFieldWithTable(info, meta);
                }
                else {
                    if (ss.format != 1) {
                        throw new AssertionError();
                    }
                    if (meta.readVInt() != fieldNumber) {
                        throw new CorruptIndexException("sortedset entry for field: " + info.name + " is corrupt", meta);
                    }
                    if (meta.readByte() != 2) {
                        throw new CorruptIndexException("sortedset entry for field: " + info.name + " is corrupt", meta);
                    }
                    this.readSortedField(info, meta);
                }
            }
            else {
                if (type != 4) {
                    throw new CorruptIndexException("invalid type: " + type, meta);
                }
                final SortedSetEntry ss = this.readSortedSetEntry(meta);
                this.sortedNumerics.put(info.name, ss);
                if (ss.format == 0) {
                    if (meta.readVInt() != fieldNumber) {
                        throw new CorruptIndexException("sortednumeric entry for field: " + info.name + " is corrupt", meta);
                    }
                    if (meta.readByte() != 0) {
                        throw new CorruptIndexException("sortednumeric entry for field: " + info.name + " is corrupt", meta);
                    }
                    this.numerics.put(info.name, this.readNumericEntry(info, meta));
                    if (meta.readVInt() != fieldNumber) {
                        throw new CorruptIndexException("sortednumeric entry for field: " + info.name + " is corrupt", meta);
                    }
                    if (meta.readByte() != 0) {
                        throw new CorruptIndexException("sortednumeric entry for field: " + info.name + " is corrupt", meta);
                    }
                    final NumericEntry ordIndex = this.readNumericEntry(info, meta);
                    this.ordIndexes.put(info.name, ordIndex);
                }
                else if (ss.format == 2) {
                    if (meta.readVInt() != info.number) {
                        throw new CorruptIndexException("sortednumeric entry for field: " + info.name + " is corrupt", meta);
                    }
                    if (meta.readByte() != 0) {
                        throw new CorruptIndexException("sortednumeric entry for field: " + info.name + " is corrupt", meta);
                    }
                    final NumericEntry n = this.readNumericEntry(info, meta);
                    this.ords.put(info.name, n);
                }
                else {
                    if (ss.format != 1) {
                        throw new AssertionError();
                    }
                    if (meta.readVInt() != fieldNumber) {
                        throw new CorruptIndexException("sortednumeric entry for field: " + info.name + " is corrupt", meta);
                    }
                    if (meta.readByte() != 0) {
                        throw new CorruptIndexException("sortednumeric entry for field: " + info.name + " is corrupt", meta);
                    }
                    this.numerics.put(info.name, this.readNumericEntry(info, meta));
                }
            }
        }
        return numFields;
    }
    
    private NumericEntry readNumericEntry(final FieldInfo info, final IndexInput meta) throws IOException {
        final NumericEntry entry = new NumericEntry();
        entry.format = meta.readVInt();
        entry.missingOffset = meta.readLong();
        if (entry.format == 5) {
            entry.numDocsWithValue = meta.readVLong();
            final int blockShift = meta.readVInt();
            entry.monotonicMeta = DirectMonotonicReader.loadMeta(meta, entry.numDocsWithValue, blockShift);
            this.ramBytesUsed.addAndGet(entry.monotonicMeta.ramBytesUsed());
            this.directAddressesMeta.put(info.name, entry.monotonicMeta);
        }
        entry.offset = meta.readLong();
        entry.count = meta.readVLong();
        switch (entry.format) {
            case 4: {
                entry.minValue = meta.readLong();
                if (entry.count > 2147483647L) {
                    throw new CorruptIndexException("illegal CONST_COMPRESSED count: " + entry.count, meta);
                }
                break;
            }
            case 1: {
                entry.minValue = meta.readLong();
                entry.gcd = meta.readLong();
                entry.bitsPerValue = meta.readVInt();
                break;
            }
            case 2: {
                final int uniqueValues = meta.readVInt();
                if (uniqueValues > 256) {
                    throw new CorruptIndexException("TABLE_COMPRESSED cannot have more than 256 distinct values, got=" + uniqueValues, meta);
                }
                entry.table = new long[uniqueValues];
                for (int i = 0; i < uniqueValues; ++i) {
                    entry.table[i] = meta.readLong();
                }
                this.ramBytesUsed.addAndGet(RamUsageEstimator.sizeOf(entry.table));
                entry.bitsPerValue = meta.readVInt();
                break;
            }
            case 0: {
                entry.minValue = meta.readLong();
                entry.bitsPerValue = meta.readVInt();
                break;
            }
            case 3: {
                final int blockShift2 = meta.readVInt();
                entry.monotonicMeta = DirectMonotonicReader.loadMeta(meta, this.maxDoc + 1, blockShift2);
                this.ramBytesUsed.addAndGet(entry.monotonicMeta.ramBytesUsed());
                this.directAddressesMeta.put(info.name, entry.monotonicMeta);
                break;
            }
            case 5: {
                final byte numberType = meta.readByte();
                switch (numberType) {
                    case 0: {
                        entry.numberType = Lucene54DocValuesConsumer.NumberType.VALUE;
                        break;
                    }
                    case 1: {
                        entry.numberType = Lucene54DocValuesConsumer.NumberType.ORDINAL;
                        break;
                    }
                    default: {
                        throw new CorruptIndexException("Number type can only be 0 or 1, got=" + numberType, meta);
                    }
                }
                final int fieldNumber = meta.readVInt();
                if (fieldNumber != info.number) {
                    throw new CorruptIndexException("Field numbers mistmatch: " + fieldNumber + " != " + info.number, meta);
                }
                final int dvFormat = meta.readByte();
                if (dvFormat != 0) {
                    throw new CorruptIndexException("Formats mistmatch: " + dvFormat + " != " + 0, meta);
                }
                entry.nonMissingValues = this.readNumericEntry(info, meta);
                break;
            }
            default: {
                throw new CorruptIndexException("Unknown format: " + entry.format + ", input=", meta);
            }
        }
        entry.endOffset = meta.readLong();
        return entry;
    }
    
    private BinaryEntry readBinaryEntry(final FieldInfo info, final IndexInput meta) throws IOException {
        final BinaryEntry entry = new BinaryEntry();
        entry.format = meta.readVInt();
        entry.missingOffset = meta.readLong();
        entry.minLength = meta.readVInt();
        entry.maxLength = meta.readVInt();
        entry.count = meta.readVLong();
        entry.offset = meta.readLong();
        switch (entry.format) {
            case 0: {
                break;
            }
            case 2: {
                entry.addressesOffset = meta.readLong();
                entry.packedIntsVersion = meta.readVInt();
                entry.blockSize = meta.readVInt();
                entry.reverseIndexOffset = meta.readLong();
                break;
            }
            case 1: {
                entry.addressesOffset = meta.readLong();
                final int blockShift = meta.readVInt();
                entry.addressesMeta = DirectMonotonicReader.loadMeta(meta, entry.count + 1L, blockShift);
                this.ramBytesUsed.addAndGet(entry.addressesMeta.ramBytesUsed());
                this.directAddressesMeta.put(info.name, entry.addressesMeta);
                entry.addressesEndOffset = meta.readLong();
                break;
            }
            default: {
                throw new CorruptIndexException("Unknown format: " + entry.format, meta);
            }
        }
        return entry;
    }
    
    SortedSetEntry readSortedSetEntry(final IndexInput meta) throws IOException {
        final SortedSetEntry entry = new SortedSetEntry();
        entry.format = meta.readVInt();
        if (entry.format == 2) {
            final int totalTableLength = meta.readInt();
            if (totalTableLength > 256) {
                throw new CorruptIndexException("SORTED_SET_TABLE cannot have more than 256 values in its dictionary, got=" + totalTableLength, meta);
            }
            entry.table = new long[totalTableLength];
            for (int i = 0; i < totalTableLength; ++i) {
                entry.table[i] = meta.readLong();
            }
            this.ramBytesUsed.addAndGet(RamUsageEstimator.sizeOf(entry.table));
            final int tableSize = meta.readInt();
            if (tableSize > totalTableLength + 1) {
                throw new CorruptIndexException("SORTED_SET_TABLE cannot have more set ids than ords in its dictionary, got " + totalTableLength + " ords and " + tableSize + " sets", meta);
            }
            entry.tableOffsets = new int[tableSize + 1];
            for (int j = 1; j < entry.tableOffsets.length; ++j) {
                entry.tableOffsets[j] = entry.tableOffsets[j - 1] + meta.readInt();
            }
            this.ramBytesUsed.addAndGet(RamUsageEstimator.sizeOf(entry.tableOffsets));
        }
        else if (entry.format != 1 && entry.format != 0) {
            throw new CorruptIndexException("Unknown format: " + entry.format, meta);
        }
        return entry;
    }
    
    @Override
    public NumericDocValues getNumeric(final FieldInfo field) throws IOException {
        final NumericEntry entry = this.numerics.get(field.name);
        return this.getNumeric(entry);
    }
    
    @Override
    public long ramBytesUsed() {
        return this.ramBytesUsed.get();
    }
    
    @Override
    public synchronized Collection<Accountable> getChildResources() {
        final List<Accountable> resources = new ArrayList<Accountable>();
        resources.addAll(Accountables.namedAccountables("addresses field", this.addressInstances));
        resources.addAll(Accountables.namedAccountables("reverse index field", this.reverseIndexInstances));
        resources.addAll(Accountables.namedAccountables("direct addresses meta field", this.directAddressesMeta));
        return (Collection<Accountable>)Collections.unmodifiableList((List<?>)resources);
    }
    
    @Override
    public void checkIntegrity() throws IOException {
        CodecUtil.checksumEntireFile(this.data);
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(fields=" + this.numFields + ")";
    }
    
    LongValues getNumeric(final NumericEntry entry) throws IOException {
        switch (entry.format) {
            case 4: {
                final long constant = entry.minValue;
                final Bits live = this.getLiveBits(entry.missingOffset, (int)entry.count);
                return new LongValues() {
                    @Override
                    public long get(final long index) {
                        return live.get((int)index) ? constant : 0L;
                    }
                };
            }
            case 0: {
                final RandomAccessInput slice = this.data.randomAccessSlice(entry.offset, entry.endOffset - entry.offset);
                final long delta = entry.minValue;
                final LongValues values = DirectReader.getInstance(slice, entry.bitsPerValue, 0L);
                return new LongValues() {
                    @Override
                    public long get(final long id) {
                        return delta + values.get(id);
                    }
                };
            }
            case 1: {
                final RandomAccessInput slice = this.data.randomAccessSlice(entry.offset, entry.endOffset - entry.offset);
                final long min = entry.minValue;
                final long mult = entry.gcd;
                final LongValues quotientReader = DirectReader.getInstance(slice, entry.bitsPerValue, 0L);
                return new LongValues() {
                    @Override
                    public long get(final long id) {
                        return min + mult * quotientReader.get(id);
                    }
                };
            }
            case 2: {
                final RandomAccessInput slice = this.data.randomAccessSlice(entry.offset, entry.endOffset - entry.offset);
                final long[] table = entry.table;
                final LongValues ords = DirectReader.getInstance(slice, entry.bitsPerValue, 0L);
                return new LongValues() {
                    @Override
                    public long get(final long id) {
                        return table[(int)ords.get(id)];
                    }
                };
            }
            case 5: {
                final SparseBits docsWithField = this.getSparseLiveBits(entry);
                final LongValues values2 = this.getNumeric(entry.nonMissingValues);
                long missingValue = 0L;
                switch (entry.numberType) {
                    case ORDINAL: {
                        missingValue = -1L;
                        break;
                    }
                    case VALUE: {
                        missingValue = 0L;
                        break;
                    }
                    default: {
                        throw new AssertionError();
                    }
                }
                return new SparseLongValues(docsWithField, values2, missingValue);
            }
            default: {
                throw new AssertionError();
            }
        }
    }
    
    @Override
    public BinaryDocValues getBinary(final FieldInfo field) throws IOException {
        final BinaryEntry bytes = this.binaries.get(field.name);
        switch (bytes.format) {
            case 0: {
                return this.getFixedBinary(field, bytes);
            }
            case 1: {
                return this.getVariableBinary(field, bytes);
            }
            case 2: {
                return this.getCompressedBinary(field, bytes);
            }
            default: {
                throw new AssertionError();
            }
        }
    }
    
    private BinaryDocValues getFixedBinary(final FieldInfo field, final BinaryEntry bytes) throws IOException {
        final IndexInput data = this.data.slice("fixed-binary", bytes.offset, bytes.count * bytes.maxLength);
        final BytesRef term = new BytesRef(bytes.maxLength);
        final byte[] buffer = term.bytes;
        final BytesRef bytesRef = term;
        final int maxLength = bytes.maxLength;
        bytesRef.length = maxLength;
        final int length = maxLength;
        return new LongBinaryDocValues() {
            public BytesRef get(final long id) {
                try {
                    data.seek(id * length);
                    data.readBytes(buffer, 0, buffer.length);
                    return term;
                }
                catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
    
    private BinaryDocValues getVariableBinary(final FieldInfo field, final BinaryEntry bytes) throws IOException {
        final RandomAccessInput addressesData = this.data.randomAccessSlice(bytes.addressesOffset, bytes.addressesEndOffset - bytes.addressesOffset);
        final LongValues addresses = DirectMonotonicReader.getInstance(bytes.addressesMeta, addressesData);
        final IndexInput data = this.data.slice("var-binary", bytes.offset, bytes.addressesOffset - bytes.offset);
        final BytesRef term = new BytesRef(Math.max(0, bytes.maxLength));
        final byte[] buffer = term.bytes;
        return new LongBinaryDocValues() {
            public BytesRef get(final long id) {
                final long startAddress = addresses.get(id);
                final long endAddress = addresses.get(id + 1L);
                final int length = (int)(endAddress - startAddress);
                try {
                    data.seek(startAddress);
                    data.readBytes(buffer, 0, length);
                    term.length = length;
                    return term;
                }
                catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
    
    private synchronized MonotonicBlockPackedReader getIntervalInstance(final FieldInfo field, final BinaryEntry bytes) throws IOException {
        MonotonicBlockPackedReader addresses = this.addressInstances.get(field.name);
        if (addresses == null) {
            this.data.seek(bytes.addressesOffset);
            final long size = bytes.count + 15L >>> 4;
            addresses = MonotonicBlockPackedReader.of(this.data, bytes.packedIntsVersion, bytes.blockSize, size, false);
            if (!this.merging) {
                this.addressInstances.put(field.name, addresses);
                this.ramBytesUsed.addAndGet(addresses.ramBytesUsed() + 4L);
            }
        }
        return addresses;
    }
    
    private synchronized ReverseTermsIndex getReverseIndexInstance(final FieldInfo field, final BinaryEntry bytes) throws IOException {
        ReverseTermsIndex index = this.reverseIndexInstances.get(field.name);
        if (index == null) {
            index = new ReverseTermsIndex();
            this.data.seek(bytes.reverseIndexOffset);
            final long size = bytes.count + 1023L >>> 10;
            index.termAddresses = MonotonicBlockPackedReader.of(this.data, bytes.packedIntsVersion, bytes.blockSize, size, false);
            final long dataSize = this.data.readVLong();
            final PagedBytes pagedBytes = new PagedBytes(15);
            pagedBytes.copy(this.data, dataSize);
            index.terms = pagedBytes.freeze(true);
            if (!this.merging) {
                this.reverseIndexInstances.put(field.name, index);
                this.ramBytesUsed.addAndGet(index.ramBytesUsed());
            }
        }
        return index;
    }
    
    private BinaryDocValues getCompressedBinary(final FieldInfo field, final BinaryEntry bytes) throws IOException {
        final MonotonicBlockPackedReader addresses = this.getIntervalInstance(field, bytes);
        final ReverseTermsIndex index = this.getReverseIndexInstance(field, bytes);
        assert addresses.size() > 0L;
        final IndexInput slice = this.data.slice("terms", bytes.offset, bytes.addressesOffset - bytes.offset);
        return new CompressedBinaryDocValues(bytes, addresses, index, slice);
    }
    
    @Override
    public SortedDocValues getSorted(final FieldInfo field) throws IOException {
        final int valueCount = (int)this.binaries.get(field.name).count;
        final BinaryDocValues binary = this.getBinary(field);
        final NumericEntry entry = this.ords.get(field.name);
        final LongValues ordinals = this.getNumeric(entry);
        return new SortedDocValues() {
            @Override
            public int getOrd(final int docID) {
                return (int)ordinals.get(docID);
            }
            
            @Override
            public BytesRef lookupOrd(final int ord) {
                return binary.get(ord);
            }
            
            @Override
            public int getValueCount() {
                return valueCount;
            }
            
            @Override
            public int lookupTerm(final BytesRef key) {
                if (binary instanceof CompressedBinaryDocValues) {
                    return (int)((CompressedBinaryDocValues)binary).lookupTerm(key);
                }
                return super.lookupTerm(key);
            }
            
            @Override
            public TermsEnum termsEnum() {
                if (binary instanceof CompressedBinaryDocValues) {
                    return ((CompressedBinaryDocValues)binary).getTermsEnum();
                }
                return super.termsEnum();
            }
        };
    }
    
    private LongValues getOrdIndexInstance(final FieldInfo field, final NumericEntry entry) throws IOException {
        final RandomAccessInput data = this.data.randomAccessSlice(entry.offset, entry.endOffset - entry.offset);
        return DirectMonotonicReader.getInstance(entry.monotonicMeta, data);
    }
    
    @Override
    public SortedNumericDocValues getSortedNumeric(final FieldInfo field) throws IOException {
        final SortedSetEntry ss = this.sortedNumerics.get(field.name);
        if (ss.format == 1) {
            final NumericEntry numericEntry = this.numerics.get(field.name);
            final LongValues values = this.getNumeric(numericEntry);
            Bits docsWithField;
            if (numericEntry.format == 5) {
                docsWithField = ((SparseLongValues)values).docsWithField;
            }
            else {
                docsWithField = this.getLiveBits(numericEntry.missingOffset, this.maxDoc);
            }
            return DocValues.singleton(values, docsWithField);
        }
        if (ss.format == 0) {
            final NumericEntry numericEntry = this.numerics.get(field.name);
            final LongValues values = this.getNumeric(numericEntry);
            final LongValues ordIndex = this.getOrdIndexInstance(field, this.ordIndexes.get(field.name));
            return new SortedNumericDocValues() {
                long startOffset;
                long endOffset;
                
                @Override
                public void setDocument(final int doc) {
                    this.startOffset = ordIndex.get(doc);
                    this.endOffset = ordIndex.get(doc + 1L);
                }
                
                @Override
                public long valueAt(final int index) {
                    return values.get(this.startOffset + index);
                }
                
                @Override
                public int count() {
                    return (int)(this.endOffset - this.startOffset);
                }
            };
        }
        if (ss.format == 2) {
            final NumericEntry entry = this.ords.get(field.name);
            final LongValues ordinals = this.getNumeric(entry);
            final long[] table = ss.table;
            final int[] offsets = ss.tableOffsets;
            return new SortedNumericDocValues() {
                int startOffset;
                int endOffset;
                
                @Override
                public void setDocument(final int doc) {
                    final int ord = (int)ordinals.get(doc);
                    this.startOffset = offsets[ord];
                    this.endOffset = offsets[ord + 1];
                }
                
                @Override
                public long valueAt(final int index) {
                    return table[this.startOffset + index];
                }
                
                @Override
                public int count() {
                    return this.endOffset - this.startOffset;
                }
            };
        }
        throw new AssertionError();
    }
    
    @Override
    public SortedSetDocValues getSortedSet(final FieldInfo field) throws IOException {
        final SortedSetEntry ss = this.sortedSets.get(field.name);
        switch (ss.format) {
            case 1: {
                final SortedDocValues values = this.getSorted(field);
                return DocValues.singleton(values);
            }
            case 0: {
                return this.getSortedSetWithAddresses(field);
            }
            case 2: {
                return this.getSortedSetTable(field, ss);
            }
            default: {
                throw new AssertionError();
            }
        }
    }
    
    private SortedSetDocValues getSortedSetWithAddresses(final FieldInfo field) throws IOException {
        final long valueCount = this.binaries.get(field.name).count;
        final LongBinaryDocValues binary = (LongBinaryDocValues)this.getBinary(field);
        final LongValues ordinals = this.getNumeric(this.ords.get(field.name));
        final LongValues ordIndex = this.getOrdIndexInstance(field, this.ordIndexes.get(field.name));
        return new RandomAccessOrds() {
            long startOffset;
            long offset;
            long endOffset;
            
            @Override
            public long nextOrd() {
                if (this.offset == this.endOffset) {
                    return -1L;
                }
                final long ord = ordinals.get(this.offset);
                ++this.offset;
                return ord;
            }
            
            @Override
            public void setDocument(final int docID) {
                final long value = ordIndex.get(docID);
                this.offset = value;
                this.startOffset = value;
                this.endOffset = ordIndex.get(docID + 1L);
            }
            
            @Override
            public BytesRef lookupOrd(final long ord) {
                return binary.get(ord);
            }
            
            @Override
            public long getValueCount() {
                return valueCount;
            }
            
            @Override
            public long lookupTerm(final BytesRef key) {
                if (binary instanceof CompressedBinaryDocValues) {
                    return ((CompressedBinaryDocValues)binary).lookupTerm(key);
                }
                return super.lookupTerm(key);
            }
            
            @Override
            public TermsEnum termsEnum() {
                if (binary instanceof CompressedBinaryDocValues) {
                    return ((CompressedBinaryDocValues)binary).getTermsEnum();
                }
                return super.termsEnum();
            }
            
            @Override
            public long ordAt(final int index) {
                return ordinals.get(this.startOffset + index);
            }
            
            @Override
            public int cardinality() {
                return (int)(this.endOffset - this.startOffset);
            }
        };
    }
    
    private SortedSetDocValues getSortedSetTable(final FieldInfo field, final SortedSetEntry ss) throws IOException {
        final long valueCount = this.binaries.get(field.name).count;
        final LongBinaryDocValues binary = (LongBinaryDocValues)this.getBinary(field);
        final LongValues ordinals = this.getNumeric(this.ords.get(field.name));
        final long[] table = ss.table;
        final int[] offsets = ss.tableOffsets;
        return new RandomAccessOrds() {
            int offset;
            int startOffset;
            int endOffset;
            
            @Override
            public void setDocument(final int docID) {
                final int ord = (int)ordinals.get(docID);
                final int n = offsets[ord];
                this.startOffset = n;
                this.offset = n;
                this.endOffset = offsets[ord + 1];
            }
            
            @Override
            public long ordAt(final int index) {
                return table[this.startOffset + index];
            }
            
            @Override
            public long nextOrd() {
                if (this.offset == this.endOffset) {
                    return -1L;
                }
                return table[this.offset++];
            }
            
            @Override
            public int cardinality() {
                return this.endOffset - this.startOffset;
            }
            
            @Override
            public BytesRef lookupOrd(final long ord) {
                return binary.get(ord);
            }
            
            @Override
            public long getValueCount() {
                return valueCount;
            }
            
            @Override
            public long lookupTerm(final BytesRef key) {
                if (binary instanceof CompressedBinaryDocValues) {
                    return ((CompressedBinaryDocValues)binary).lookupTerm(key);
                }
                return super.lookupTerm(key);
            }
            
            @Override
            public TermsEnum termsEnum() {
                if (binary instanceof CompressedBinaryDocValues) {
                    return ((CompressedBinaryDocValues)binary).getTermsEnum();
                }
                return super.termsEnum();
            }
        };
    }
    
    private Bits getLiveBits(final long offset, final int count) throws IOException {
        if (offset == -2L) {
            return new Bits.MatchNoBits(count);
        }
        if (offset == -1L) {
            return new Bits.MatchAllBits(count);
        }
        final int length = (int)(count + 7L >>> 3);
        final RandomAccessInput in = this.data.randomAccessSlice(offset, length);
        return new Bits() {
            @Override
            public boolean get(final int index) {
                try {
                    return (in.readByte(index >> 3) & 1 << (index & 0x7)) != 0x0;
                }
                catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
            
            @Override
            public int length() {
                return count;
            }
        };
    }
    
    private SparseBits getSparseLiveBits(final NumericEntry entry) throws IOException {
        final RandomAccessInput docIdsData = this.data.randomAccessSlice(entry.missingOffset, entry.offset - entry.missingOffset);
        final LongValues docIDs = DirectMonotonicReader.getInstance(entry.monotonicMeta, docIdsData);
        return new SparseBits(this.maxDoc, entry.numDocsWithValue, docIDs);
    }
    
    @Override
    public Bits getDocsWithField(final FieldInfo field) throws IOException {
        switch (field.getDocValuesType()) {
            case SORTED_SET: {
                return DocValues.docsWithValue(this.getSortedSet(field), this.maxDoc);
            }
            case SORTED_NUMERIC: {
                return DocValues.docsWithValue(this.getSortedNumeric(field), this.maxDoc);
            }
            case SORTED: {
                return DocValues.docsWithValue(this.getSorted(field), this.maxDoc);
            }
            case BINARY: {
                final BinaryEntry be = this.binaries.get(field.name);
                return this.getLiveBits(be.missingOffset, this.maxDoc);
            }
            case NUMERIC: {
                final NumericEntry ne = this.numerics.get(field.name);
                if (ne.format == 5) {
                    return this.getSparseLiveBits(ne);
                }
                return this.getLiveBits(ne.missingOffset, this.maxDoc);
            }
            default: {
                throw new AssertionError();
            }
        }
    }
    
    @Override
    public synchronized DocValuesProducer getMergeInstance() throws IOException {
        return new Lucene54DocValuesProducer(this);
    }
    
    @Override
    public void close() throws IOException {
        this.data.close();
    }
    
    static class SparseBits implements Bits
    {
        final long maxDoc;
        final long docIDsLength;
        final long firstDocId;
        final LongValues docIds;
        long index;
        long docId;
        long nextDocId;
        
        SparseBits(final long maxDoc, final long docIDsLength, final LongValues docIDs) {
            if (docIDsLength > 0L && maxDoc <= docIDs.get(docIDsLength - 1L)) {
                throw new IllegalArgumentException("maxDoc must be > the last element of docIDs");
            }
            this.maxDoc = maxDoc;
            this.docIDsLength = docIDsLength;
            this.docIds = docIDs;
            this.firstDocId = ((docIDsLength == 0L) ? maxDoc : docIDs.get(0));
            this.reset();
        }
        
        private void reset() {
            this.index = -1L;
            this.docId = -1L;
            this.nextDocId = this.firstDocId;
        }
        
        private long gallop(final long docId) {
            ++this.index;
            this.docId = this.nextDocId;
            long delta;
            for (long hiIndex = this.index + 1L; hiIndex < this.docIDsLength; hiIndex += delta << 1) {
                final long hiDocId = this.docIds.get(hiIndex);
                if (hiDocId > docId) {
                    this.nextDocId = hiDocId;
                    return hiIndex;
                }
                delta = hiIndex - this.index;
                this.index = hiIndex;
                this.docId = hiDocId;
            }
            long hiIndex = this.docIDsLength;
            this.nextDocId = this.maxDoc;
            return hiIndex;
        }
        
        private void binarySearch(long hiIndex, final long docId) {
            while (this.index + 1L < hiIndex) {
                final long midIndex = this.index + hiIndex >>> 1;
                final long midDocId = this.docIds.get(midIndex);
                if (midDocId > docId) {
                    hiIndex = midIndex;
                    this.nextDocId = midDocId;
                }
                else {
                    this.index = midIndex;
                    this.docId = midDocId;
                }
            }
        }
        
        private boolean checkInvariants(final long nextIndex, final long docId) {
            assert this.docId <= docId;
            assert this.nextDocId > docId;
            assert this.docId == this.docIds.get(this.index);
            assert this.nextDocId == this.docIds.get(nextIndex);
            return true;
        }
        
        private void exponentialSearch(final long docId) {
            final long hiIndex = this.gallop(docId);
            assert this.checkInvariants(hiIndex, docId);
            this.binarySearch(hiIndex, docId);
        }
        
        boolean get(final long docId) {
            if (docId < this.docId) {
                this.reset();
            }
            if (docId >= this.nextDocId) {
                this.exponentialSearch(docId);
            }
            assert this.checkInvariants(this.index + 1L, docId);
            return docId == this.docId;
        }
        
        @Override
        public boolean get(final int index) {
            return this.get((long)index);
        }
        
        @Override
        public int length() {
            return (int)this.maxDoc;
        }
    }
    
    static class SparseLongValues extends LongValues
    {
        final SparseBits docsWithField;
        final LongValues values;
        final long missingValue;
        
        SparseLongValues(final SparseBits docsWithField, final LongValues values, final long missingValue) {
            this.docsWithField = docsWithField;
            this.values = values;
            this.missingValue = missingValue;
        }
        
        @Override
        public long get(final long docId) {
            if (this.docsWithField.get(docId)) {
                return this.values.get(this.docsWithField.index);
            }
            return this.missingValue;
        }
    }
    
    static class NumericEntry
    {
        long missingOffset;
        public long offset;
        public long endOffset;
        public int bitsPerValue;
        int format;
        public long count;
        public DirectMonotonicReader.Meta monotonicMeta;
        long minValue;
        long gcd;
        long[] table;
        long numDocsWithValue;
        NumericEntry nonMissingValues;
        Lucene54DocValuesConsumer.NumberType numberType;
        
        private NumericEntry() {
        }
    }
    
    static class BinaryEntry
    {
        long missingOffset;
        long offset;
        int format;
        public long count;
        int minLength;
        int maxLength;
        public long addressesOffset;
        public long addressesEndOffset;
        public DirectMonotonicReader.Meta addressesMeta;
        public long reverseIndexOffset;
        public int packedIntsVersion;
        public int blockSize;
        
        private BinaryEntry() {
        }
    }
    
    static class SortedSetEntry
    {
        int format;
        long[] table;
        int[] tableOffsets;
        
        private SortedSetEntry() {
        }
    }
    
    abstract static class LongBinaryDocValues extends BinaryDocValues
    {
        @Override
        public final BytesRef get(final int docID) {
            return this.get((long)docID);
        }
        
        abstract BytesRef get(final long p0);
    }
    
    static class ReverseTermsIndex implements Accountable
    {
        public MonotonicBlockPackedReader termAddresses;
        public PagedBytes.Reader terms;
        
        @Override
        public long ramBytesUsed() {
            return this.termAddresses.ramBytesUsed() + this.terms.ramBytesUsed();
        }
        
        @Override
        public Collection<Accountable> getChildResources() {
            final List<Accountable> resources = new ArrayList<Accountable>();
            resources.add(Accountables.namedAccountable("term bytes", this.terms));
            resources.add(Accountables.namedAccountable("term addresses", this.termAddresses));
            return (Collection<Accountable>)Collections.unmodifiableList((List<?>)resources);
        }
        
        @Override
        public String toString() {
            return this.getClass().getSimpleName() + "(size=" + this.termAddresses.size() + ")";
        }
    }
    
    static final class CompressedBinaryDocValues extends LongBinaryDocValues
    {
        final long numValues;
        final long numIndexValues;
        final int maxTermLength;
        final MonotonicBlockPackedReader addresses;
        final IndexInput data;
        final CompressedBinaryTermsEnum termsEnum;
        final PagedBytes.Reader reverseTerms;
        final MonotonicBlockPackedReader reverseAddresses;
        final long numReverseIndexValues;
        
        public CompressedBinaryDocValues(final BinaryEntry bytes, final MonotonicBlockPackedReader addresses, final ReverseTermsIndex index, final IndexInput data) throws IOException {
            this.maxTermLength = bytes.maxLength;
            this.numValues = bytes.count;
            this.addresses = addresses;
            this.numIndexValues = addresses.size();
            this.data = data;
            this.reverseTerms = index.terms;
            this.reverseAddresses = index.termAddresses;
            this.numReverseIndexValues = this.reverseAddresses.size();
            this.termsEnum = this.getTermsEnum(data);
        }
        
        public BytesRef get(final long id) {
            try {
                this.termsEnum.seekExact(id);
                return this.termsEnum.term();
            }
            catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
        
        long lookupTerm(final BytesRef key) {
            try {
                switch (this.termsEnum.seekCeil(key)) {
                    case FOUND: {
                        return this.termsEnum.ord();
                    }
                    case NOT_FOUND: {
                        return -this.termsEnum.ord() - 1L;
                    }
                    default: {
                        return -this.numValues - 1L;
                    }
                }
            }
            catch (final IOException bogus) {
                throw new RuntimeException(bogus);
            }
        }
        
        TermsEnum getTermsEnum() {
            try {
                return this.getTermsEnum(this.data.clone());
            }
            catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
        
        private CompressedBinaryTermsEnum getTermsEnum(final IndexInput input) throws IOException {
            return new CompressedBinaryTermsEnum(input);
        }
        
        class CompressedBinaryTermsEnum extends TermsEnum
        {
            private long currentOrd;
            private long currentBlockStart;
            private final IndexInput input;
            private final int[] offsets;
            private final byte[] buffer;
            private final BytesRef term;
            private final BytesRef firstTerm;
            private final BytesRef scratch;
            
            CompressedBinaryTermsEnum(final IndexInput input) throws IOException {
                this.currentOrd = -1L;
                this.offsets = new int[16];
                this.buffer = new byte[31];
                this.term = new BytesRef(CompressedBinaryDocValues.this.maxTermLength);
                this.firstTerm = new BytesRef(CompressedBinaryDocValues.this.maxTermLength);
                this.scratch = new BytesRef();
                (this.input = input).seek(0L);
            }
            
            private void readHeader() throws IOException {
                this.firstTerm.length = this.input.readVInt();
                this.input.readBytes(this.firstTerm.bytes, 0, this.firstTerm.length);
                this.input.readBytes(this.buffer, 0, 15);
                if (this.buffer[0] == -1) {
                    this.readShortAddresses();
                }
                else {
                    this.readByteAddresses();
                }
                this.currentBlockStart = this.input.getFilePointer();
            }
            
            private void readByteAddresses() throws IOException {
                int addr = 0;
                for (int i = 1; i < this.offsets.length; ++i) {
                    addr += 2 + (this.buffer[i - 1] & 0xFF);
                    this.offsets[i] = addr;
                }
            }
            
            private void readShortAddresses() throws IOException {
                this.input.readBytes(this.buffer, 15, 16);
                int addr = 0;
                for (int i = 1; i < this.offsets.length; ++i) {
                    final int x = i << 1;
                    addr += 2 + (this.buffer[x - 1] << 8 | (this.buffer[x] & 0xFF));
                    this.offsets[i] = addr;
                }
            }
            
            private void readFirstTerm() throws IOException {
                this.term.length = this.firstTerm.length;
                System.arraycopy(this.firstTerm.bytes, this.firstTerm.offset, this.term.bytes, 0, this.term.length);
            }
            
            private void readTerm(final int offset) throws IOException {
                final int start = this.input.readByte() & 0xFF;
                System.arraycopy(this.firstTerm.bytes, this.firstTerm.offset, this.term.bytes, 0, start);
                final int suffix = this.offsets[offset] - this.offsets[offset - 1] - 1;
                this.input.readBytes(this.term.bytes, start, suffix);
                this.term.length = start + suffix;
            }
            
            @Override
            public BytesRef next() throws IOException {
                ++this.currentOrd;
                if (this.currentOrd >= CompressedBinaryDocValues.this.numValues) {
                    return null;
                }
                final int offset = (int)(this.currentOrd & 0xFL);
                if (offset == 0) {
                    this.readHeader();
                    this.readFirstTerm();
                }
                else {
                    this.readTerm(offset);
                }
                return this.term;
            }
            
            long binarySearchIndex(final BytesRef text) throws IOException {
                long low = 0L;
                long high = CompressedBinaryDocValues.this.numReverseIndexValues - 1L;
                while (low <= high) {
                    final long mid = low + high >>> 1;
                    CompressedBinaryDocValues.this.reverseTerms.fill(this.scratch, CompressedBinaryDocValues.this.reverseAddresses.get(mid));
                    final int cmp = this.scratch.compareTo(text);
                    if (cmp < 0) {
                        low = mid + 1L;
                    }
                    else {
                        if (cmp <= 0) {
                            return mid;
                        }
                        high = mid - 1L;
                    }
                }
                return high;
            }
            
            long binarySearchBlock(final BytesRef text, long low, long high) throws IOException {
                while (low <= high) {
                    final long mid = low + high >>> 1;
                    this.input.seek(CompressedBinaryDocValues.this.addresses.get(mid));
                    this.term.length = this.input.readVInt();
                    this.input.readBytes(this.term.bytes, 0, this.term.length);
                    final int cmp = this.term.compareTo(text);
                    if (cmp < 0) {
                        low = mid + 1L;
                    }
                    else {
                        if (cmp <= 0) {
                            return mid;
                        }
                        high = mid - 1L;
                    }
                }
                return high;
            }
            
            @Override
            public SeekStatus seekCeil(final BytesRef text) throws IOException {
                final long indexPos = this.binarySearchIndex(text);
                long block;
                if (indexPos < 0L) {
                    block = 0L;
                }
                else {
                    final long low = indexPos << 6;
                    final long high = Math.min(CompressedBinaryDocValues.this.numIndexValues - 1L, low + 63L);
                    block = Math.max(low, this.binarySearchBlock(text, low, high));
                }
                this.input.seek(CompressedBinaryDocValues.this.addresses.get(block));
                this.currentOrd = (block << 4) - 1L;
                while (this.next() != null) {
                    final int cmp = this.term.compareTo(text);
                    if (cmp == 0) {
                        return SeekStatus.FOUND;
                    }
                    if (cmp > 0) {
                        return SeekStatus.NOT_FOUND;
                    }
                }
                return SeekStatus.END;
            }
            
            @Override
            public void seekExact(final long ord) throws IOException {
                final long block = ord >>> 4;
                if (block != this.currentOrd >>> 4) {
                    this.input.seek(CompressedBinaryDocValues.this.addresses.get(block));
                    this.readHeader();
                }
                this.currentOrd = ord;
                final int offset = (int)(ord & 0xFL);
                if (offset == 0) {
                    this.readFirstTerm();
                }
                else {
                    this.input.seek(this.currentBlockStart + this.offsets[offset - 1]);
                    this.readTerm(offset);
                }
            }
            
            @Override
            public BytesRef term() throws IOException {
                return this.term;
            }
            
            @Override
            public long ord() throws IOException {
                return this.currentOrd;
            }
            
            @Override
            public int docFreq() throws IOException {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public long totalTermFreq() throws IOException {
                return -1L;
            }
            
            @Override
            public PostingsEnum postings(final PostingsEnum reuse, final int flags) throws IOException {
                throw new UnsupportedOperationException();
            }
        }
    }
}
