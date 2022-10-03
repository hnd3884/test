package org.apache.lucene.codecs.lucene53;

import java.util.Collections;
import org.apache.lucene.util.Accountable;
import java.util.Collection;
import org.apache.lucene.store.RandomAccessInput;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import java.io.IOException;
import org.apache.lucene.store.ChecksumIndexInput;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.index.IndexFileNames;
import java.util.HashMap;
import org.apache.lucene.index.SegmentReadState;
import org.apache.lucene.store.IndexInput;
import java.util.Map;
import org.apache.lucene.codecs.NormsProducer;

class Lucene53NormsProducer extends NormsProducer
{
    private final Map<Integer, NormsEntry> norms;
    private final IndexInput data;
    private final int maxDoc;
    
    Lucene53NormsProducer(final SegmentReadState state, final String dataCodec, final String dataExtension, final String metaCodec, final String metaExtension) throws IOException {
        this.norms = new HashMap<Integer, NormsEntry>();
        this.maxDoc = state.segmentInfo.maxDoc();
        final String metaName = IndexFileNames.segmentFileName(state.segmentInfo.name, state.segmentSuffix, metaExtension);
        int version = -1;
        try (final ChecksumIndexInput in = state.directory.openChecksumInput(metaName, state.context)) {
            Throwable priorE = null;
            try {
                version = CodecUtil.checkIndexHeader(in, metaCodec, 0, 0, state.segmentInfo.getId(), state.segmentSuffix);
                this.readFields(in, state.fieldInfos);
            }
            catch (final Throwable exception) {
                priorE = exception;
            }
            finally {
                CodecUtil.checkFooter(in, priorE);
            }
        }
        final String dataName = IndexFileNames.segmentFileName(state.segmentInfo.name, state.segmentSuffix, dataExtension);
        this.data = state.directory.openInput(dataName, state.context);
        boolean success = false;
        try {
            final int version2 = CodecUtil.checkIndexHeader(this.data, dataCodec, 0, 0, state.segmentInfo.getId(), state.segmentSuffix);
            if (version != version2) {
                throw new CorruptIndexException("Format versions mismatch: meta=" + version + ",data=" + version2, this.data);
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
    
    private void readFields(final IndexInput meta, final FieldInfos infos) throws IOException {
        int fieldNumber = meta.readVInt();
        while (fieldNumber != -1) {
            final FieldInfo info = infos.fieldInfo(fieldNumber);
            if (info == null) {
                throw new CorruptIndexException("Invalid field number: " + fieldNumber, meta);
            }
            if (!info.hasNorms()) {
                throw new CorruptIndexException("Invalid field: " + info.name, meta);
            }
            final NormsEntry entry = new NormsEntry();
            switch (entry.bytesPerValue = meta.readByte()) {
                case 0:
                case 1:
                case 2:
                case 4:
                case 8: {
                    entry.offset = meta.readLong();
                    this.norms.put(info.number, entry);
                    fieldNumber = meta.readVInt();
                    continue;
                }
                default: {
                    throw new CorruptIndexException("Invalid bytesPerValue: " + entry.bytesPerValue + ", field: " + info.name, meta);
                }
            }
        }
    }
    
    @Override
    public NumericDocValues getNorms(final FieldInfo field) throws IOException {
        final NormsEntry entry = this.norms.get(field.number);
        if (entry.bytesPerValue == 0) {
            final long value = entry.offset;
            return new NumericDocValues() {
                @Override
                public long get(final int docID) {
                    return value;
                }
            };
        }
        synchronized (this.data) {
            switch (entry.bytesPerValue) {
                case 1: {
                    final RandomAccessInput slice = this.data.randomAccessSlice(entry.offset, this.maxDoc);
                    return new NumericDocValues() {
                        @Override
                        public long get(final int docID) {
                            try {
                                return slice.readByte(docID);
                            }
                            catch (final IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    };
                }
                case 2: {
                    final RandomAccessInput slice = this.data.randomAccessSlice(entry.offset, this.maxDoc * 2L);
                    return new NumericDocValues() {
                        @Override
                        public long get(final int docID) {
                            try {
                                return slice.readShort((long)docID << 1);
                            }
                            catch (final IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    };
                }
                case 4: {
                    final RandomAccessInput slice = this.data.randomAccessSlice(entry.offset, this.maxDoc * 4L);
                    return new NumericDocValues() {
                        @Override
                        public long get(final int docID) {
                            try {
                                return slice.readInt((long)docID << 2);
                            }
                            catch (final IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    };
                }
                case 8: {
                    final RandomAccessInput slice = this.data.randomAccessSlice(entry.offset, this.maxDoc * 8L);
                    return new NumericDocValues() {
                        @Override
                        public long get(final int docID) {
                            try {
                                return slice.readLong((long)docID << 3);
                            }
                            catch (final IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    };
                }
                default: {
                    throw new AssertionError();
                }
            }
        }
    }
    
    @Override
    public void close() throws IOException {
        this.data.close();
    }
    
    @Override
    public long ramBytesUsed() {
        return 64L * this.norms.size();
    }
    
    @Override
    public Collection<Accountable> getChildResources() {
        return (Collection<Accountable>)Collections.emptyList();
    }
    
    @Override
    public void checkIntegrity() throws IOException {
        CodecUtil.checksumEntireFile(this.data);
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(fields=" + this.norms.size() + ")";
    }
    
    static class NormsEntry
    {
        byte bytesPerValue;
        long offset;
    }
}
