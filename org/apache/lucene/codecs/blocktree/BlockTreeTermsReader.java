package org.apache.lucene.codecs.blocktree;

import org.apache.lucene.util.fst.ByteSequenceOutputs;
import java.util.List;
import java.util.Map;
import org.apache.lucene.util.Accountables;
import java.util.ArrayList;
import org.apache.lucene.util.Accountable;
import java.util.Collection;
import org.apache.lucene.index.Terms;
import java.util.Set;
import java.util.Collections;
import java.util.Iterator;
import java.io.IOException;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.SegmentReadState;
import java.util.TreeMap;
import org.apache.lucene.codecs.PostingsReaderBase;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.fst.Outputs;
import org.apache.lucene.codecs.FieldsProducer;

public final class BlockTreeTermsReader extends FieldsProducer
{
    static final Outputs<BytesRef> FST_OUTPUTS;
    static final BytesRef NO_OUTPUT;
    static final int OUTPUT_FLAGS_NUM_BITS = 2;
    static final int OUTPUT_FLAGS_MASK = 3;
    static final int OUTPUT_FLAG_IS_FLOOR = 1;
    static final int OUTPUT_FLAG_HAS_TERMS = 2;
    static final String TERMS_EXTENSION = "tim";
    static final String TERMS_CODEC_NAME = "BlockTreeTermsDict";
    public static final int VERSION_START = 0;
    public static final int VERSION_AUTO_PREFIX_TERMS = 1;
    public static final int VERSION_AUTO_PREFIX_TERMS_COND = 2;
    public static final int VERSION_CURRENT = 2;
    static final String TERMS_INDEX_EXTENSION = "tip";
    static final String TERMS_INDEX_CODEC_NAME = "BlockTreeTermsIndex";
    final IndexInput termsIn;
    final PostingsReaderBase postingsReader;
    private final TreeMap<String, FieldReader> fields;
    private long dirOffset;
    private long indexDirOffset;
    final String segment;
    final int version;
    final boolean anyAutoPrefixTerms;
    
    public BlockTreeTermsReader(final PostingsReaderBase postingsReader, final SegmentReadState state) throws IOException {
        this.fields = new TreeMap<String, FieldReader>();
        boolean success = false;
        IndexInput indexIn = null;
        this.postingsReader = postingsReader;
        this.segment = state.segmentInfo.name;
        final String termsName = IndexFileNames.segmentFileName(this.segment, state.segmentSuffix, "tim");
        try {
            this.termsIn = state.directory.openInput(termsName, state.context);
            this.version = CodecUtil.checkIndexHeader(this.termsIn, "BlockTreeTermsDict", 0, 2, state.segmentInfo.getId(), state.segmentSuffix);
            if (this.version < 1) {
                this.anyAutoPrefixTerms = false;
            }
            else if (this.version == 1) {
                this.anyAutoPrefixTerms = true;
            }
            else {
                assert this.version >= 2;
                final byte b = this.termsIn.readByte();
                if (b == 0) {
                    this.anyAutoPrefixTerms = false;
                }
                else {
                    if (b != 1) {
                        throw new CorruptIndexException("invalid anyAutoPrefixTerms: expected 0 or 1 but got " + b, this.termsIn);
                    }
                    this.anyAutoPrefixTerms = true;
                }
            }
            final String indexName = IndexFileNames.segmentFileName(this.segment, state.segmentSuffix, "tip");
            indexIn = state.directory.openInput(indexName, state.context);
            CodecUtil.checkIndexHeader(indexIn, "BlockTreeTermsIndex", this.version, this.version, state.segmentInfo.getId(), state.segmentSuffix);
            CodecUtil.checksumEntireFile(indexIn);
            postingsReader.init(this.termsIn, state);
            CodecUtil.retrieveChecksum(this.termsIn);
            this.seekDir(this.termsIn, this.dirOffset);
            this.seekDir(indexIn, this.indexDirOffset);
            final int numFields = this.termsIn.readVInt();
            if (numFields < 0) {
                throw new CorruptIndexException("invalid numFields: " + numFields, this.termsIn);
            }
            for (int i = 0; i < numFields; ++i) {
                final int field = this.termsIn.readVInt();
                final long numTerms = this.termsIn.readVLong();
                if (numTerms <= 0L) {
                    throw new CorruptIndexException("Illegal numTerms for field number: " + field, this.termsIn);
                }
                final int numBytes = this.termsIn.readVInt();
                if (numBytes < 0) {
                    throw new CorruptIndexException("invalid rootCode for field number: " + field + ", numBytes=" + numBytes, this.termsIn);
                }
                final BytesRef rootCode = new BytesRef(new byte[numBytes]);
                this.termsIn.readBytes(rootCode.bytes, 0, numBytes);
                rootCode.length = numBytes;
                final FieldInfo fieldInfo = state.fieldInfos.fieldInfo(field);
                if (fieldInfo == null) {
                    throw new CorruptIndexException("invalid field number: " + field, this.termsIn);
                }
                final long sumTotalTermFreq = (fieldInfo.getIndexOptions() == IndexOptions.DOCS) ? -1L : this.termsIn.readVLong();
                final long sumDocFreq = this.termsIn.readVLong();
                final int docCount = this.termsIn.readVInt();
                final int longsSize = this.termsIn.readVInt();
                if (longsSize < 0) {
                    throw new CorruptIndexException("invalid longsSize for field: " + fieldInfo.name + ", longsSize=" + longsSize, this.termsIn);
                }
                final BytesRef minTerm = readBytesRef(this.termsIn);
                final BytesRef maxTerm = readBytesRef(this.termsIn);
                if (docCount < 0 || docCount > state.segmentInfo.maxDoc()) {
                    throw new CorruptIndexException("invalid docCount: " + docCount + " maxDoc: " + state.segmentInfo.maxDoc(), this.termsIn);
                }
                if (sumDocFreq < docCount) {
                    throw new CorruptIndexException("invalid sumDocFreq: " + sumDocFreq + " docCount: " + docCount, this.termsIn);
                }
                if (sumTotalTermFreq != -1L && sumTotalTermFreq < sumDocFreq) {
                    throw new CorruptIndexException("invalid sumTotalTermFreq: " + sumTotalTermFreq + " sumDocFreq: " + sumDocFreq, this.termsIn);
                }
                final long indexStartFP = indexIn.readVLong();
                final FieldReader previous = this.fields.put(fieldInfo.name, new FieldReader(this, fieldInfo, numTerms, rootCode, sumTotalTermFreq, sumDocFreq, docCount, indexStartFP, longsSize, indexIn, minTerm, maxTerm));
                if (previous != null) {
                    throw new CorruptIndexException("duplicate field: " + fieldInfo.name, this.termsIn);
                }
            }
            indexIn.close();
            success = true;
        }
        finally {
            if (!success) {
                IOUtils.closeWhileHandlingException(indexIn, this);
            }
        }
    }
    
    private static BytesRef readBytesRef(final IndexInput in) throws IOException {
        final BytesRef bytes = new BytesRef();
        bytes.length = in.readVInt();
        in.readBytes(bytes.bytes = new byte[bytes.length], 0, bytes.length);
        return bytes;
    }
    
    private void seekDir(final IndexInput input, long dirOffset) throws IOException {
        input.seek(input.length() - CodecUtil.footerLength() - 8L);
        dirOffset = input.readLong();
        input.seek(dirOffset);
    }
    
    @Override
    public void close() throws IOException {
        try {
            IOUtils.close(this.termsIn, this.postingsReader);
        }
        finally {
            this.fields.clear();
        }
    }
    
    @Override
    public Iterator<String> iterator() {
        return Collections.unmodifiableSet((Set<? extends String>)this.fields.keySet()).iterator();
    }
    
    @Override
    public Terms terms(final String field) throws IOException {
        assert field != null;
        return this.fields.get(field);
    }
    
    @Override
    public int size() {
        return this.fields.size();
    }
    
    String brToString(final BytesRef b) {
        if (b == null) {
            return "null";
        }
        try {
            return b.utf8ToString() + " " + b;
        }
        catch (final Throwable t) {
            return b.toString();
        }
    }
    
    @Override
    public long ramBytesUsed() {
        long sizeInBytes = this.postingsReader.ramBytesUsed();
        for (final FieldReader reader : this.fields.values()) {
            sizeInBytes += reader.ramBytesUsed();
        }
        return sizeInBytes;
    }
    
    @Override
    public Collection<Accountable> getChildResources() {
        final List<Accountable> resources = new ArrayList<Accountable>();
        resources.addAll(Accountables.namedAccountables("field", this.fields));
        resources.add(Accountables.namedAccountable("delegate", this.postingsReader));
        return (Collection<Accountable>)Collections.unmodifiableList((List<?>)resources);
    }
    
    @Override
    public void checkIntegrity() throws IOException {
        CodecUtil.checksumEntireFile(this.termsIn);
        this.postingsReader.checkIntegrity();
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(fields=" + this.fields.size() + ",delegate=" + this.postingsReader + ")";
    }
    
    static {
        FST_OUTPUTS = ByteSequenceOutputs.getSingleton();
        NO_OUTPUT = BlockTreeTermsReader.FST_OUTPUTS.getNoOutput();
    }
}
