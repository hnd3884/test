package org.apache.lucene.codecs.idversion;

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
import org.apache.lucene.util.fst.PairOutputs;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.SegmentReadState;
import java.util.TreeMap;
import org.apache.lucene.codecs.PostingsReaderBase;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.codecs.FieldsProducer;

public final class VersionBlockTreeTermsReader extends FieldsProducer
{
    final IndexInput in;
    final PostingsReaderBase postingsReader;
    private final TreeMap<String, VersionFieldReader> fields;
    
    public VersionBlockTreeTermsReader(final PostingsReaderBase postingsReader, final SegmentReadState state) throws IOException {
        this.fields = new TreeMap<String, VersionFieldReader>();
        this.postingsReader = postingsReader;
        final String termsFile = IndexFileNames.segmentFileName(state.segmentInfo.name, state.segmentSuffix, "tiv");
        this.in = state.directory.openInput(termsFile, state.context);
        boolean success = false;
        IndexInput indexIn = null;
        try {
            final int termsVersion = CodecUtil.checkIndexHeader((DataInput)this.in, "VersionBlockTreeTermsDict", 1, 1, state.segmentInfo.getId(), state.segmentSuffix);
            final String indexFile = IndexFileNames.segmentFileName(state.segmentInfo.name, state.segmentSuffix, "tipv");
            indexIn = state.directory.openInput(indexFile, state.context);
            final int indexVersion = CodecUtil.checkIndexHeader((DataInput)indexIn, "VersionBlockTreeTermsIndex", 1, 1, state.segmentInfo.getId(), state.segmentSuffix);
            if (indexVersion != termsVersion) {
                throw new CorruptIndexException("mixmatched version files: " + this.in + "=" + termsVersion + "," + indexIn + "=" + indexVersion, (DataInput)indexIn);
            }
            CodecUtil.checksumEntireFile(indexIn);
            postingsReader.init(this.in, state);
            CodecUtil.retrieveChecksum(this.in);
            this.seekDir(this.in);
            this.seekDir(indexIn);
            final int numFields = this.in.readVInt();
            if (numFields < 0) {
                throw new CorruptIndexException("invalid numFields: " + numFields, (DataInput)this.in);
            }
            for (int i = 0; i < numFields; ++i) {
                final int field = this.in.readVInt();
                final long numTerms = this.in.readVLong();
                assert numTerms >= 0L;
                final int numBytes = this.in.readVInt();
                final BytesRef code = new BytesRef(new byte[numBytes]);
                this.in.readBytes(code.bytes, 0, numBytes);
                code.length = numBytes;
                final long version = this.in.readVLong();
                final PairOutputs.Pair<BytesRef, Long> rootCode = (PairOutputs.Pair<BytesRef, Long>)VersionBlockTreeTermsWriter.FST_OUTPUTS.newPair((Object)code, (Object)version);
                final FieldInfo fieldInfo = state.fieldInfos.fieldInfo(field);
                assert fieldInfo != null : "field=" + field;
                final long sumTotalTermFreq = numTerms;
                final long sumDocFreq = numTerms;
                assert numTerms <= 2147483647L;
                final int docCount = (int)numTerms;
                final int longsSize = this.in.readVInt();
                final BytesRef minTerm = readBytesRef(this.in);
                final BytesRef maxTerm = readBytesRef(this.in);
                if (docCount < 0 || docCount > state.segmentInfo.maxDoc()) {
                    throw new CorruptIndexException("invalid docCount: " + docCount + " maxDoc: " + state.segmentInfo.maxDoc(), (DataInput)this.in);
                }
                if (sumDocFreq < docCount) {
                    throw new CorruptIndexException("invalid sumDocFreq: " + sumDocFreq + " docCount: " + docCount, (DataInput)this.in);
                }
                if (sumTotalTermFreq != -1L && sumTotalTermFreq < sumDocFreq) {
                    throw new CorruptIndexException("invalid sumTotalTermFreq: " + sumTotalTermFreq + " sumDocFreq: " + sumDocFreq, (DataInput)this.in);
                }
                final long indexStartFP = indexIn.readVLong();
                final VersionFieldReader previous = this.fields.put(fieldInfo.name, new VersionFieldReader(this, fieldInfo, numTerms, rootCode, sumTotalTermFreq, sumDocFreq, docCount, indexStartFP, longsSize, indexIn, minTerm, maxTerm));
                if (previous != null) {
                    throw new CorruptIndexException("duplicate field: " + fieldInfo.name, (DataInput)this.in);
                }
            }
            indexIn.close();
            success = true;
        }
        finally {
            if (!success) {
                IOUtils.closeWhileHandlingException(new Closeable[] { (Closeable)indexIn, (Closeable)this });
            }
        }
    }
    
    private static BytesRef readBytesRef(final IndexInput in) throws IOException {
        final BytesRef bytes = new BytesRef();
        bytes.length = in.readVInt();
        in.readBytes(bytes.bytes = new byte[bytes.length], 0, bytes.length);
        return bytes;
    }
    
    private void seekDir(final IndexInput input) throws IOException {
        input.seek(input.length() - CodecUtil.footerLength() - 8L);
        final long dirOffset = input.readLong();
        input.seek(dirOffset);
    }
    
    public void close() throws IOException {
        try {
            IOUtils.close(new Closeable[] { (Closeable)this.in, (Closeable)this.postingsReader });
        }
        finally {
            this.fields.clear();
        }
    }
    
    public Iterator<String> iterator() {
        return Collections.unmodifiableSet((Set<? extends String>)this.fields.keySet()).iterator();
    }
    
    public Terms terms(final String field) throws IOException {
        assert field != null;
        return this.fields.get(field);
    }
    
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
    
    public long ramBytesUsed() {
        long sizeInBytes = this.postingsReader.ramBytesUsed();
        for (final VersionFieldReader reader : this.fields.values()) {
            sizeInBytes += reader.ramBytesUsed();
        }
        return sizeInBytes;
    }
    
    public Collection<Accountable> getChildResources() {
        final List<Accountable> resources = new ArrayList<Accountable>();
        resources.addAll(Accountables.namedAccountables("field", (Map)this.fields));
        resources.add(Accountables.namedAccountable("delegate", (Accountable)this.postingsReader));
        return (Collection<Accountable>)Collections.unmodifiableList((List<?>)resources);
    }
    
    public void checkIntegrity() throws IOException {
        CodecUtil.checksumEntireFile(this.in);
        this.postingsReader.checkIntegrity();
    }
    
    public String toString() {
        return this.getClass().getSimpleName() + "(fields=" + this.fields.size() + ",delegate=" + this.postingsReader.toString() + ")";
    }
}
