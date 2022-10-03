package org.apache.lucene.codecs.compressing;

import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.store.ByteArrayDataInput;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.Terms;
import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.Collections;
import org.apache.lucene.util.Accountables;
import org.apache.lucene.util.Accountable;
import java.util.Collection;
import org.apache.lucene.util.LongsRef;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.packed.PackedInts;
import org.apache.lucene.index.Fields;
import org.apache.lucene.store.AlreadyClosedException;
import java.io.IOException;
import org.apache.lucene.store.ChecksumIndexInput;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.util.packed.BlockPackedReaderIterator;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.index.FieldInfos;
import java.io.Closeable;
import org.apache.lucene.codecs.TermVectorsReader;

public final class CompressingTermVectorsReader extends TermVectorsReader implements Closeable
{
    private final FieldInfos fieldInfos;
    final CompressingStoredFieldsIndexReader indexReader;
    final IndexInput vectorsStream;
    private final int version;
    private final int packedIntsVersion;
    private final CompressionMode compressionMode;
    private final Decompressor decompressor;
    private final int chunkSize;
    private final int numDocs;
    private boolean closed;
    private final BlockPackedReaderIterator reader;
    private final long numChunks;
    private final long numDirtyChunks;
    private final long maxPointer;
    
    private CompressingTermVectorsReader(final CompressingTermVectorsReader reader) {
        this.fieldInfos = reader.fieldInfos;
        this.vectorsStream = reader.vectorsStream.clone();
        this.indexReader = reader.indexReader.clone();
        this.packedIntsVersion = reader.packedIntsVersion;
        this.compressionMode = reader.compressionMode;
        this.decompressor = reader.decompressor.clone();
        this.chunkSize = reader.chunkSize;
        this.numDocs = reader.numDocs;
        this.reader = new BlockPackedReaderIterator(this.vectorsStream, this.packedIntsVersion, 64, 0L);
        this.version = reader.version;
        this.numChunks = reader.numChunks;
        this.numDirtyChunks = reader.numDirtyChunks;
        this.maxPointer = reader.maxPointer;
        this.closed = false;
    }
    
    public CompressingTermVectorsReader(final Directory d, final SegmentInfo si, final String segmentSuffix, final FieldInfos fn, final IOContext context, final String formatName, final CompressionMode compressionMode) throws IOException {
        this.compressionMode = compressionMode;
        final String segment = si.name;
        boolean success = false;
        this.fieldInfos = fn;
        this.numDocs = si.maxDoc();
        int version = -1;
        CompressingStoredFieldsIndexReader indexReader = null;
        long maxPointer = -1L;
        final String indexName = IndexFileNames.segmentFileName(segment, segmentSuffix, "tvx");
        try (final ChecksumIndexInput input = d.openChecksumInput(indexName, context)) {
            Throwable priorE = null;
            try {
                final String codecNameIdx = formatName + "Index";
                version = CodecUtil.checkIndexHeader(input, codecNameIdx, 0, 1, si.getId(), segmentSuffix);
                assert CodecUtil.indexHeaderLength(codecNameIdx, segmentSuffix) == input.getFilePointer();
                indexReader = new CompressingStoredFieldsIndexReader(input, si);
                maxPointer = input.readVLong();
            }
            catch (final Throwable exception) {
                priorE = exception;
            }
            finally {
                CodecUtil.checkFooter(input, priorE);
            }
        }
        this.version = version;
        this.indexReader = indexReader;
        this.maxPointer = maxPointer;
        try {
            final String vectorsStreamFN = IndexFileNames.segmentFileName(segment, segmentSuffix, "tvd");
            this.vectorsStream = d.openInput(vectorsStreamFN, context);
            final String codecNameDat = formatName + "Data";
            final int version2 = CodecUtil.checkIndexHeader(this.vectorsStream, codecNameDat, 0, 1, si.getId(), segmentSuffix);
            if (version != version2) {
                throw new CorruptIndexException("Version mismatch between stored fields index and data: " + version + " != " + version2, this.vectorsStream);
            }
            assert CodecUtil.indexHeaderLength(codecNameDat, segmentSuffix) == this.vectorsStream.getFilePointer();
            final long pos = this.vectorsStream.getFilePointer();
            if (version >= 1) {
                this.vectorsStream.seek(maxPointer);
                this.numChunks = this.vectorsStream.readVLong();
                this.numDirtyChunks = this.vectorsStream.readVLong();
                if (this.numDirtyChunks > this.numChunks) {
                    throw new CorruptIndexException("invalid chunk counts: dirty=" + this.numDirtyChunks + ", total=" + this.numChunks, this.vectorsStream);
                }
            }
            else {
                final long n = -1L;
                this.numDirtyChunks = n;
                this.numChunks = n;
            }
            CodecUtil.retrieveChecksum(this.vectorsStream);
            this.vectorsStream.seek(pos);
            this.packedIntsVersion = this.vectorsStream.readVInt();
            this.chunkSize = this.vectorsStream.readVInt();
            this.decompressor = compressionMode.newDecompressor();
            this.reader = new BlockPackedReaderIterator(this.vectorsStream, this.packedIntsVersion, 64, 0L);
            success = true;
        }
        finally {
            if (!success) {
                IOUtils.closeWhileHandlingException(this);
            }
        }
    }
    
    CompressionMode getCompressionMode() {
        return this.compressionMode;
    }
    
    int getChunkSize() {
        return this.chunkSize;
    }
    
    int getPackedIntsVersion() {
        return this.packedIntsVersion;
    }
    
    int getVersion() {
        return this.version;
    }
    
    CompressingStoredFieldsIndexReader getIndexReader() {
        return this.indexReader;
    }
    
    IndexInput getVectorsStream() {
        return this.vectorsStream;
    }
    
    long getMaxPointer() {
        return this.maxPointer;
    }
    
    long getNumChunks() {
        return this.numChunks;
    }
    
    long getNumDirtyChunks() {
        return this.numDirtyChunks;
    }
    
    private void ensureOpen() throws AlreadyClosedException {
        if (this.closed) {
            throw new AlreadyClosedException("this FieldsReader is closed");
        }
    }
    
    @Override
    public void close() throws IOException {
        if (!this.closed) {
            IOUtils.close(this.vectorsStream);
            this.closed = true;
        }
    }
    
    @Override
    public TermVectorsReader clone() {
        return new CompressingTermVectorsReader(this);
    }
    
    @Override
    public Fields get(final int doc) throws IOException {
        this.ensureOpen();
        final long startPointer = this.indexReader.getStartPointer(doc);
        this.vectorsStream.seek(startPointer);
        final int docBase = this.vectorsStream.readVInt();
        final int chunkDocs = this.vectorsStream.readVInt();
        if (doc < docBase || doc >= docBase + chunkDocs || docBase + chunkDocs > this.numDocs) {
            throw new CorruptIndexException("docBase=" + docBase + ",chunkDocs=" + chunkDocs + ",doc=" + doc, this.vectorsStream);
        }
        int skip;
        int numFields;
        int totalFields;
        if (chunkDocs == 1) {
            skip = 0;
            totalFields = (numFields = this.vectorsStream.readVInt());
        }
        else {
            this.reader.reset(this.vectorsStream, chunkDocs);
            int sum = 0;
            for (int i = docBase; i < doc; ++i) {
                sum += (int)this.reader.next();
            }
            skip = sum;
            numFields = (int)this.reader.next();
            sum += numFields;
            for (int i = doc + 1; i < docBase + chunkDocs; ++i) {
                sum += (int)this.reader.next();
            }
            totalFields = sum;
        }
        if (numFields == 0) {
            return null;
        }
        final int token = this.vectorsStream.readByte() & 0xFF;
        assert token != 0;
        final int bitsPerFieldNum = token & 0x1F;
        int totalDistinctFields = token >>> 5;
        if (totalDistinctFields == 7) {
            totalDistinctFields += this.vectorsStream.readVInt();
        }
        ++totalDistinctFields;
        final PackedInts.ReaderIterator it = PackedInts.getReaderIteratorNoHeader(this.vectorsStream, PackedInts.Format.PACKED, this.packedIntsVersion, totalDistinctFields, bitsPerFieldNum, 1);
        final int[] fieldNums = new int[totalDistinctFields];
        for (int j = 0; j < totalDistinctFields; ++j) {
            fieldNums[j] = (int)it.next();
        }
        final int[] fieldNumOffs = new int[numFields];
        final int bitsPerOff = PackedInts.bitsRequired(fieldNums.length - 1);
        final PackedInts.Reader allFieldNumOffs = PackedInts.getReaderNoHeader(this.vectorsStream, PackedInts.Format.PACKED, this.packedIntsVersion, totalFields, bitsPerOff);
        PackedInts.Reader flags = null;
        switch (this.vectorsStream.readVInt()) {
            case 0: {
                final PackedInts.Reader fieldFlags = PackedInts.getReaderNoHeader(this.vectorsStream, PackedInts.Format.PACKED, this.packedIntsVersion, fieldNums.length, CompressingTermVectorsWriter.FLAGS_BITS);
                final PackedInts.Mutable f = PackedInts.getMutable(totalFields, CompressingTermVectorsWriter.FLAGS_BITS, 0.0f);
                for (int k = 0; k < totalFields; ++k) {
                    final int fieldNumOff = (int)allFieldNumOffs.get(k);
                    assert fieldNumOff >= 0 && fieldNumOff < fieldNums.length;
                    final int fgs = (int)fieldFlags.get(fieldNumOff);
                    f.set(k, fgs);
                }
                flags = f;
                break;
            }
            case 1: {
                flags = PackedInts.getReaderNoHeader(this.vectorsStream, PackedInts.Format.PACKED, this.packedIntsVersion, totalFields, CompressingTermVectorsWriter.FLAGS_BITS);
                break;
            }
            default: {
                throw new AssertionError();
            }
        }
        for (int j = 0; j < numFields; ++j) {
            fieldNumOffs[j] = (int)allFieldNumOffs.get(skip + j);
        }
        final int bitsRequired = this.vectorsStream.readVInt();
        final PackedInts.Reader numTerms = PackedInts.getReaderNoHeader(this.vectorsStream, PackedInts.Format.PACKED, this.packedIntsVersion, totalFields, bitsRequired);
        int sum2 = 0;
        for (int k = 0; k < totalFields; ++k) {
            sum2 += (int)numTerms.get(k);
        }
        final int totalTerms = sum2;
        int docOff = 0;
        int docLen = 0;
        final int[] fieldLengths = new int[numFields];
        final int[][] prefixLengths = new int[numFields][];
        final int[][] suffixLengths = new int[numFields][];
        this.reader.reset(this.vectorsStream, totalTerms);
        int toSkip = 0;
        for (int l = 0; l < skip; ++l) {
            toSkip += (int)numTerms.get(l);
        }
        this.reader.skip(toSkip);
        for (int l = 0; l < numFields; ++l) {
            final int termCount = (int)numTerms.get(skip + l);
            final int[] fieldPrefixLengths = new int[termCount];
            prefixLengths[l] = fieldPrefixLengths;
            int m = 0;
            while (m < termCount) {
                final LongsRef next = this.reader.next(termCount - m);
                for (int k2 = 0; k2 < next.length; ++k2) {
                    fieldPrefixLengths[m++] = (int)next.longs[next.offset + k2];
                }
            }
        }
        this.reader.skip(totalTerms - this.reader.ord());
        this.reader.reset(this.vectorsStream, totalTerms);
        toSkip = 0;
        for (int l = 0; l < skip; ++l) {
            for (int j2 = 0; j2 < numTerms.get(l); ++j2) {
                docOff += (int)this.reader.next();
            }
        }
        for (int l = 0; l < numFields; ++l) {
            final int termCount = (int)numTerms.get(skip + l);
            final int[] fieldSuffixLengths = new int[termCount];
            suffixLengths[l] = fieldSuffixLengths;
            int m = 0;
            while (m < termCount) {
                final LongsRef next = this.reader.next(termCount - m);
                for (int k2 = 0; k2 < next.length; ++k2) {
                    fieldSuffixLengths[m++] = (int)next.longs[next.offset + k2];
                }
            }
            fieldLengths[l] = sum(suffixLengths[l]);
            docLen += fieldLengths[l];
        }
        int totalLen = docOff + docLen;
        for (int l = skip + numFields; l < totalFields; ++l) {
            for (int j2 = 0; j2 < numTerms.get(l); ++j2) {
                totalLen += (int)this.reader.next();
            }
        }
        final int[] termFreqs = new int[totalTerms];
        this.reader.reset(this.vectorsStream, totalTerms);
        int l = 0;
        while (l < totalTerms) {
            final LongsRef next2 = this.reader.next(totalTerms - l);
            for (int k3 = 0; k3 < next2.length; ++k3) {
                termFreqs[l++] = 1 + (int)next2.longs[next2.offset + k3];
            }
        }
        int totalPositions = 0;
        int totalOffsets = 0;
        int totalPayloads = 0;
        int i2 = 0;
        int termIndex = 0;
        while (i2 < totalFields) {
            final int f2 = (int)flags.get(i2);
            for (int termCount2 = (int)numTerms.get(i2), j3 = 0; j3 < termCount2; ++j3) {
                final int freq = termFreqs[termIndex++];
                if ((f2 & 0x1) != 0x0) {
                    totalPositions += freq;
                }
                if ((f2 & 0x2) != 0x0) {
                    totalOffsets += freq;
                }
                if ((f2 & 0x4) != 0x0) {
                    totalPayloads += freq;
                }
            }
            assert termIndex == totalTerms : termIndex + " " + totalTerms;
            ++i2;
        }
        final int[][] positionIndex = this.positionIndex(skip, numFields, numTerms, termFreqs);
        int[][] positions;
        if (totalPositions > 0) {
            positions = this.readPositions(skip, numFields, flags, numTerms, termFreqs, 1, totalPositions, positionIndex);
        }
        else {
            positions = new int[numFields][];
        }
        int[][] startOffsets;
        int[][] lengths;
        if (totalOffsets > 0) {
            final float[] charsPerTerm = new float[fieldNums.length];
            for (int i3 = 0; i3 < charsPerTerm.length; ++i3) {
                charsPerTerm[i3] = Float.intBitsToFloat(this.vectorsStream.readInt());
            }
            startOffsets = this.readPositions(skip, numFields, flags, numTerms, termFreqs, 2, totalOffsets, positionIndex);
            lengths = this.readPositions(skip, numFields, flags, numTerms, termFreqs, 2, totalOffsets, positionIndex);
            for (int i3 = 0; i3 < numFields; ++i3) {
                final int[] fStartOffsets = startOffsets[i3];
                final int[] fPositions = positions[i3];
                if (fStartOffsets != null && fPositions != null) {
                    final float fieldCharsPerTerm = charsPerTerm[fieldNumOffs[i3]];
                    for (int j4 = 0; j4 < startOffsets[i3].length; ++j4) {
                        final int[] array = fStartOffsets;
                        final int n = j4;
                        array[n] += (int)(fieldCharsPerTerm * fPositions[j4]);
                    }
                }
                if (fStartOffsets != null) {
                    final int[] fPrefixLengths = prefixLengths[i3];
                    final int[] fSuffixLengths = suffixLengths[i3];
                    final int[] fLengths = lengths[i3];
                    for (int j5 = 0, end = (int)numTerms.get(skip + i3); j5 < end; ++j5) {
                        final int termLength = fPrefixLengths[j5] + fSuffixLengths[j5];
                        final int[] array2 = lengths[i3];
                        final int n2 = positionIndex[i3][j5];
                        array2[n2] += termLength;
                        for (int k4 = positionIndex[i3][j5] + 1; k4 < positionIndex[i3][j5 + 1]; ++k4) {
                            final int[] array3 = fStartOffsets;
                            final int n3 = k4;
                            array3[n3] += fStartOffsets[k4 - 1];
                            final int[] array4 = fLengths;
                            final int n4 = k4;
                            array4[n4] += termLength;
                        }
                    }
                }
            }
        }
        else {
            lengths = (startOffsets = new int[numFields][]);
        }
        if (totalPositions > 0) {
            for (int i4 = 0; i4 < numFields; ++i4) {
                final int[] fPositions2 = positions[i4];
                final int[] fpositionIndex = positionIndex[i4];
                if (fPositions2 != null) {
                    for (int j6 = 0, end2 = (int)numTerms.get(skip + i4); j6 < end2; ++j6) {
                        for (int k5 = fpositionIndex[j6] + 1; k5 < fpositionIndex[j6 + 1]; ++k5) {
                            final int[] array5 = fPositions2;
                            final int n5 = k5;
                            array5[n5] += fPositions2[k5 - 1];
                        }
                    }
                }
            }
        }
        final int[][] payloadIndex = new int[numFields][];
        int totalPayloadLength = 0;
        int payloadOff = 0;
        int payloadLen = 0;
        if (totalPayloads > 0) {
            this.reader.reset(this.vectorsStream, totalPayloads);
            int termIndex2 = 0;
            for (int i5 = 0; i5 < skip; ++i5) {
                final int f3 = (int)flags.get(i5);
                final int termCount3 = (int)numTerms.get(i5);
                if ((f3 & 0x4) != 0x0) {
                    for (int j7 = 0; j7 < termCount3; ++j7) {
                        for (int freq2 = termFreqs[termIndex2 + j7], k4 = 0; k4 < freq2; ++k4) {
                            final int l2 = (int)this.reader.next();
                            payloadOff += l2;
                        }
                    }
                }
                termIndex2 += termCount3;
            }
            totalPayloadLength = payloadOff;
            for (int i5 = 0; i5 < numFields; ++i5) {
                final int f3 = (int)flags.get(skip + i5);
                final int termCount3 = (int)numTerms.get(skip + i5);
                if ((f3 & 0x4) != 0x0) {
                    final int totalFreq = positionIndex[i5][termCount3];
                    payloadIndex[i5] = new int[totalFreq + 1];
                    int posIdx = 0;
                    payloadIndex[i5][posIdx] = payloadLen;
                    for (int j8 = 0; j8 < termCount3; ++j8) {
                        for (int freq3 = termFreqs[termIndex2 + j8], k6 = 0; k6 < freq3; ++k6) {
                            final int payloadLength = (int)this.reader.next();
                            payloadLen += payloadLength;
                            payloadIndex[i5][posIdx + 1] = payloadLen;
                            ++posIdx;
                        }
                    }
                    assert posIdx == totalFreq;
                }
                termIndex2 += termCount3;
            }
            totalPayloadLength += payloadLen;
            for (int i5 = skip + numFields; i5 < totalFields; ++i5) {
                final int f3 = (int)flags.get(i5);
                final int termCount3 = (int)numTerms.get(i5);
                if ((f3 & 0x4) != 0x0) {
                    for (int j7 = 0; j7 < termCount3; ++j7) {
                        for (int freq2 = termFreqs[termIndex2 + j7], k4 = 0; k4 < freq2; ++k4) {
                            totalPayloadLength += (int)this.reader.next();
                        }
                    }
                }
                termIndex2 += termCount3;
            }
            assert termIndex2 == totalTerms : termIndex2 + " " + totalTerms;
        }
        final BytesRef suffixBytes = new BytesRef();
        this.decompressor.decompress(this.vectorsStream, totalLen + totalPayloadLength, docOff + payloadOff, docLen + payloadLen, suffixBytes);
        suffixBytes.length = docLen;
        final BytesRef payloadBytes = new BytesRef(suffixBytes.bytes, suffixBytes.offset + docLen, payloadLen);
        final int[] fieldFlags2 = new int[numFields];
        for (int i6 = 0; i6 < numFields; ++i6) {
            fieldFlags2[i6] = (int)flags.get(skip + i6);
        }
        final int[] fieldNumTerms = new int[numFields];
        for (int i7 = 0; i7 < numFields; ++i7) {
            fieldNumTerms[i7] = (int)numTerms.get(skip + i7);
        }
        final int[][] fieldTermFreqs = new int[numFields][];
        int termIdx = 0;
        for (int i8 = 0; i8 < skip; ++i8) {
            termIdx += (int)numTerms.get(i8);
        }
        for (int i8 = 0; i8 < numFields; ++i8) {
            final int termCount4 = (int)numTerms.get(skip + i8);
            fieldTermFreqs[i8] = new int[termCount4];
            for (int j9 = 0; j9 < termCount4; ++j9) {
                fieldTermFreqs[i8][j9] = termFreqs[termIdx++];
            }
        }
        assert sum(fieldLengths) == docLen : sum(fieldLengths) + " != " + docLen;
        return new TVFields(fieldNums, fieldFlags2, fieldNumOffs, fieldNumTerms, fieldLengths, prefixLengths, suffixLengths, fieldTermFreqs, positionIndex, positions, startOffsets, lengths, payloadBytes, payloadIndex, suffixBytes);
    }
    
    private int[][] positionIndex(final int skip, final int numFields, final PackedInts.Reader numTerms, final int[] termFreqs) {
        final int[][] positionIndex = new int[numFields][];
        int termIndex = 0;
        for (int i = 0; i < skip; ++i) {
            final int termCount = (int)numTerms.get(i);
            termIndex += termCount;
        }
        for (int i = 0; i < numFields; ++i) {
            final int termCount = (int)numTerms.get(skip + i);
            positionIndex[i] = new int[termCount + 1];
            for (int j = 0; j < termCount; ++j) {
                final int freq = termFreqs[termIndex + j];
                positionIndex[i][j + 1] = positionIndex[i][j] + freq;
            }
            termIndex += termCount;
        }
        return positionIndex;
    }
    
    private int[][] readPositions(final int skip, final int numFields, final PackedInts.Reader flags, final PackedInts.Reader numTerms, final int[] termFreqs, final int flag, final int totalPositions, final int[][] positionIndex) throws IOException {
        final int[][] positions = new int[numFields][];
        this.reader.reset(this.vectorsStream, totalPositions);
        int toSkip = 0;
        int termIndex = 0;
        for (int i = 0; i < skip; ++i) {
            final int f = (int)flags.get(i);
            final int termCount = (int)numTerms.get(i);
            if ((f & flag) != 0x0) {
                for (int j = 0; j < termCount; ++j) {
                    final int freq = termFreqs[termIndex + j];
                    toSkip += freq;
                }
            }
            termIndex += termCount;
        }
        this.reader.skip(toSkip);
        for (int i = 0; i < numFields; ++i) {
            final int f = (int)flags.get(skip + i);
            final int termCount = (int)numTerms.get(skip + i);
            if ((f & flag) != 0x0) {
                final int totalFreq = positionIndex[i][termCount];
                final int[] fieldPositions = new int[totalFreq];
                positions[i] = fieldPositions;
                int k = 0;
                while (k < totalFreq) {
                    final LongsRef nextPositions = this.reader.next(totalFreq - k);
                    for (int l = 0; l < nextPositions.length; ++l) {
                        fieldPositions[k++] = (int)nextPositions.longs[nextPositions.offset + l];
                    }
                }
            }
            termIndex += termCount;
        }
        this.reader.skip(totalPositions - this.reader.ord());
        return positions;
    }
    
    private static int sum(final int[] arr) {
        int sum = 0;
        for (final int el : arr) {
            sum += el;
        }
        return sum;
    }
    
    @Override
    public long ramBytesUsed() {
        return this.indexReader.ramBytesUsed();
    }
    
    @Override
    public Collection<Accountable> getChildResources() {
        return Collections.singleton(Accountables.namedAccountable("term vector index", this.indexReader));
    }
    
    @Override
    public void checkIntegrity() throws IOException {
        CodecUtil.checksumEntireFile(this.vectorsStream);
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(mode=" + this.compressionMode + ",chunksize=" + this.chunkSize + ")";
    }
    
    private class TVFields extends Fields
    {
        private final int[] fieldNums;
        private final int[] fieldFlags;
        private final int[] fieldNumOffs;
        private final int[] numTerms;
        private final int[] fieldLengths;
        private final int[][] prefixLengths;
        private final int[][] suffixLengths;
        private final int[][] termFreqs;
        private final int[][] positionIndex;
        private final int[][] positions;
        private final int[][] startOffsets;
        private final int[][] lengths;
        private final int[][] payloadIndex;
        private final BytesRef suffixBytes;
        private final BytesRef payloadBytes;
        
        public TVFields(final int[] fieldNums, final int[] fieldFlags, final int[] fieldNumOffs, final int[] numTerms, final int[] fieldLengths, final int[][] prefixLengths, final int[][] suffixLengths, final int[][] termFreqs, final int[][] positionIndex, final int[][] positions, final int[][] startOffsets, final int[][] lengths, final BytesRef payloadBytes, final int[][] payloadIndex, final BytesRef suffixBytes) {
            this.fieldNums = fieldNums;
            this.fieldFlags = fieldFlags;
            this.fieldNumOffs = fieldNumOffs;
            this.numTerms = numTerms;
            this.fieldLengths = fieldLengths;
            this.prefixLengths = prefixLengths;
            this.suffixLengths = suffixLengths;
            this.termFreqs = termFreqs;
            this.positionIndex = positionIndex;
            this.positions = positions;
            this.startOffsets = startOffsets;
            this.lengths = lengths;
            this.payloadBytes = payloadBytes;
            this.payloadIndex = payloadIndex;
            this.suffixBytes = suffixBytes;
        }
        
        @Override
        public Iterator<String> iterator() {
            return new Iterator<String>() {
                int i = 0;
                
                @Override
                public boolean hasNext() {
                    return this.i < TVFields.this.fieldNumOffs.length;
                }
                
                @Override
                public String next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    final int fieldNum = TVFields.this.fieldNums[TVFields.this.fieldNumOffs[this.i++]];
                    return CompressingTermVectorsReader.this.fieldInfos.fieldInfo(fieldNum).name;
                }
                
                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
        
        @Override
        public Terms terms(final String field) throws IOException {
            final FieldInfo fieldInfo = CompressingTermVectorsReader.this.fieldInfos.fieldInfo(field);
            if (fieldInfo == null) {
                return null;
            }
            int idx = -1;
            for (int i = 0; i < this.fieldNumOffs.length; ++i) {
                if (this.fieldNums[this.fieldNumOffs[i]] == fieldInfo.number) {
                    idx = i;
                    break;
                }
            }
            if (idx == -1 || this.numTerms[idx] == 0) {
                return null;
            }
            int fieldOff = 0;
            int fieldLen = -1;
            for (int j = 0; j < this.fieldNumOffs.length; ++j) {
                if (j >= idx) {
                    fieldLen = this.fieldLengths[j];
                    break;
                }
                fieldOff += this.fieldLengths[j];
            }
            assert fieldLen >= 0;
            return new TVTerms(this.numTerms[idx], this.fieldFlags[idx], this.prefixLengths[idx], this.suffixLengths[idx], this.termFreqs[idx], this.positionIndex[idx], this.positions[idx], this.startOffsets[idx], this.lengths[idx], this.payloadIndex[idx], this.payloadBytes, new BytesRef(this.suffixBytes.bytes, this.suffixBytes.offset + fieldOff, fieldLen));
        }
        
        @Override
        public int size() {
            return this.fieldNumOffs.length;
        }
    }
    
    private class TVTerms extends Terms
    {
        private final int numTerms;
        private final int flags;
        private final int[] prefixLengths;
        private final int[] suffixLengths;
        private final int[] termFreqs;
        private final int[] positionIndex;
        private final int[] positions;
        private final int[] startOffsets;
        private final int[] lengths;
        private final int[] payloadIndex;
        private final BytesRef termBytes;
        private final BytesRef payloadBytes;
        
        TVTerms(final int numTerms, final int flags, final int[] prefixLengths, final int[] suffixLengths, final int[] termFreqs, final int[] positionIndex, final int[] positions, final int[] startOffsets, final int[] lengths, final int[] payloadIndex, final BytesRef payloadBytes, final BytesRef termBytes) {
            this.numTerms = numTerms;
            this.flags = flags;
            this.prefixLengths = prefixLengths;
            this.suffixLengths = suffixLengths;
            this.termFreqs = termFreqs;
            this.positionIndex = positionIndex;
            this.positions = positions;
            this.startOffsets = startOffsets;
            this.lengths = lengths;
            this.payloadIndex = payloadIndex;
            this.payloadBytes = payloadBytes;
            this.termBytes = termBytes;
        }
        
        @Override
        public TermsEnum iterator() throws IOException {
            final TVTermsEnum termsEnum = new TVTermsEnum();
            termsEnum.reset(this.numTerms, this.flags, this.prefixLengths, this.suffixLengths, this.termFreqs, this.positionIndex, this.positions, this.startOffsets, this.lengths, this.payloadIndex, this.payloadBytes, new ByteArrayDataInput(this.termBytes.bytes, this.termBytes.offset, this.termBytes.length));
            return termsEnum;
        }
        
        @Override
        public long size() throws IOException {
            return this.numTerms;
        }
        
        @Override
        public long getSumTotalTermFreq() throws IOException {
            return -1L;
        }
        
        @Override
        public long getSumDocFreq() throws IOException {
            return this.numTerms;
        }
        
        @Override
        public int getDocCount() throws IOException {
            return 1;
        }
        
        @Override
        public boolean hasFreqs() {
            return true;
        }
        
        @Override
        public boolean hasOffsets() {
            return (this.flags & 0x2) != 0x0;
        }
        
        @Override
        public boolean hasPositions() {
            return (this.flags & 0x1) != 0x0;
        }
        
        @Override
        public boolean hasPayloads() {
            return (this.flags & 0x4) != 0x0;
        }
    }
    
    private static class TVTermsEnum extends TermsEnum
    {
        private int numTerms;
        private int startPos;
        private int ord;
        private int[] prefixLengths;
        private int[] suffixLengths;
        private int[] termFreqs;
        private int[] positionIndex;
        private int[] positions;
        private int[] startOffsets;
        private int[] lengths;
        private int[] payloadIndex;
        private ByteArrayDataInput in;
        private BytesRef payloads;
        private final BytesRef term;
        
        private TVTermsEnum() {
            this.term = new BytesRef(16);
        }
        
        void reset(final int numTerms, final int flags, final int[] prefixLengths, final int[] suffixLengths, final int[] termFreqs, final int[] positionIndex, final int[] positions, final int[] startOffsets, final int[] lengths, final int[] payloadIndex, final BytesRef payloads, final ByteArrayDataInput in) {
            this.numTerms = numTerms;
            this.prefixLengths = prefixLengths;
            this.suffixLengths = suffixLengths;
            this.termFreqs = termFreqs;
            this.positionIndex = positionIndex;
            this.positions = positions;
            this.startOffsets = startOffsets;
            this.lengths = lengths;
            this.payloadIndex = payloadIndex;
            this.payloads = payloads;
            this.in = in;
            this.startPos = in.getPosition();
            this.reset();
        }
        
        void reset() {
            this.term.length = 0;
            this.in.setPosition(this.startPos);
            this.ord = -1;
        }
        
        @Override
        public BytesRef next() throws IOException {
            if (this.ord == this.numTerms - 1) {
                return null;
            }
            assert this.ord < this.numTerms;
            ++this.ord;
            this.term.offset = 0;
            this.term.length = this.prefixLengths[this.ord] + this.suffixLengths[this.ord];
            if (this.term.length > this.term.bytes.length) {
                this.term.bytes = ArrayUtil.grow(this.term.bytes, this.term.length);
            }
            this.in.readBytes(this.term.bytes, this.prefixLengths[this.ord], this.suffixLengths[this.ord]);
            return this.term;
        }
        
        @Override
        public SeekStatus seekCeil(final BytesRef text) throws IOException {
            if (this.ord < this.numTerms && this.ord >= 0) {
                final int cmp = this.term().compareTo(text);
                if (cmp == 0) {
                    return SeekStatus.FOUND;
                }
                if (cmp > 0) {
                    this.reset();
                }
            }
            while (true) {
                final BytesRef term = this.next();
                if (term == null) {
                    return SeekStatus.END;
                }
                final int cmp2 = term.compareTo(text);
                if (cmp2 > 0) {
                    return SeekStatus.NOT_FOUND;
                }
                if (cmp2 == 0) {
                    return SeekStatus.FOUND;
                }
            }
        }
        
        @Override
        public void seekExact(final long ord) throws IOException {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public BytesRef term() throws IOException {
            return this.term;
        }
        
        @Override
        public long ord() throws IOException {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public int docFreq() throws IOException {
            return 1;
        }
        
        @Override
        public long totalTermFreq() throws IOException {
            return this.termFreqs[this.ord];
        }
        
        @Override
        public final PostingsEnum postings(final PostingsEnum reuse, final int flags) throws IOException {
            if (PostingsEnum.featureRequested(flags, (short)16384) && this.positions == null && this.startOffsets == null) {
                return null;
            }
            TVPostingsEnum docsEnum;
            if (reuse != null && reuse instanceof TVPostingsEnum) {
                docsEnum = (TVPostingsEnum)reuse;
            }
            else {
                docsEnum = new TVPostingsEnum();
            }
            docsEnum.reset(this.termFreqs[this.ord], this.positionIndex[this.ord], this.positions, this.startOffsets, this.lengths, this.payloads, this.payloadIndex);
            return docsEnum;
        }
    }
    
    private static class TVPostingsEnum extends PostingsEnum
    {
        private int doc;
        private int termFreq;
        private int positionIndex;
        private int[] positions;
        private int[] startOffsets;
        private int[] lengths;
        private final BytesRef payload;
        private int[] payloadIndex;
        private int basePayloadOffset;
        private int i;
        
        TVPostingsEnum() {
            this.doc = -1;
            this.payload = new BytesRef();
        }
        
        public void reset(final int freq, final int positionIndex, final int[] positions, final int[] startOffsets, final int[] lengths, final BytesRef payloads, final int[] payloadIndex) {
            this.termFreq = freq;
            this.positionIndex = positionIndex;
            this.positions = positions;
            this.startOffsets = startOffsets;
            this.lengths = lengths;
            this.basePayloadOffset = payloads.offset;
            this.payload.bytes = payloads.bytes;
            final BytesRef payload = this.payload;
            final BytesRef payload2 = this.payload;
            final int n = 0;
            payload2.length = n;
            payload.offset = n;
            this.payloadIndex = payloadIndex;
            final int n2 = -1;
            this.i = n2;
            this.doc = n2;
        }
        
        private void checkDoc() {
            if (this.doc == Integer.MAX_VALUE) {
                throw new IllegalStateException("DocsEnum exhausted");
            }
            if (this.doc == -1) {
                throw new IllegalStateException("DocsEnum not started");
            }
        }
        
        private void checkPosition() {
            this.checkDoc();
            if (this.i < 0) {
                throw new IllegalStateException("Position enum not started");
            }
            if (this.i >= this.termFreq) {
                throw new IllegalStateException("Read past last position");
            }
        }
        
        @Override
        public int nextPosition() throws IOException {
            if (this.doc != 0) {
                throw new IllegalStateException();
            }
            if (this.i >= this.termFreq - 1) {
                throw new IllegalStateException("Read past last position");
            }
            ++this.i;
            if (this.payloadIndex != null) {
                this.payload.offset = this.basePayloadOffset + this.payloadIndex[this.positionIndex + this.i];
                this.payload.length = this.payloadIndex[this.positionIndex + this.i + 1] - this.payloadIndex[this.positionIndex + this.i];
            }
            if (this.positions == null) {
                return -1;
            }
            return this.positions[this.positionIndex + this.i];
        }
        
        @Override
        public int startOffset() throws IOException {
            this.checkPosition();
            if (this.startOffsets == null) {
                return -1;
            }
            return this.startOffsets[this.positionIndex + this.i];
        }
        
        @Override
        public int endOffset() throws IOException {
            this.checkPosition();
            if (this.startOffsets == null) {
                return -1;
            }
            return this.startOffsets[this.positionIndex + this.i] + this.lengths[this.positionIndex + this.i];
        }
        
        @Override
        public BytesRef getPayload() throws IOException {
            this.checkPosition();
            if (this.payloadIndex == null || this.payload.length == 0) {
                return null;
            }
            return this.payload;
        }
        
        @Override
        public int freq() throws IOException {
            this.checkDoc();
            return this.termFreq;
        }
        
        @Override
        public int docID() {
            return this.doc;
        }
        
        @Override
        public int nextDoc() throws IOException {
            if (this.doc == -1) {
                return this.doc = 0;
            }
            return this.doc = Integer.MAX_VALUE;
        }
        
        @Override
        public int advance(final int target) throws IOException {
            return this.slowAdvance(target);
        }
        
        @Override
        public long cost() {
            return 1L;
        }
    }
}
