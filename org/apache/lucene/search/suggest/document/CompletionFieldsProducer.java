package org.apache.lucene.search.suggest.document;

import org.apache.lucene.index.Terms;
import java.util.List;
import java.util.Collections;
import org.apache.lucene.util.Accountables;
import java.util.ArrayList;
import org.apache.lucene.util.Accountable;
import java.util.Collection;
import java.util.Iterator;
import java.io.IOException;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.store.ChecksumIndexInput;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import java.util.HashMap;
import org.apache.lucene.codecs.PostingsFormat;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.SegmentReadState;
import org.apache.lucene.store.IndexInput;
import java.util.Map;
import org.apache.lucene.codecs.FieldsProducer;

final class CompletionFieldsProducer extends FieldsProducer
{
    private FieldsProducer delegateFieldsProducer;
    private Map<String, CompletionsTermsReader> readers;
    private IndexInput dictIn;
    
    private CompletionFieldsProducer(final FieldsProducer delegateFieldsProducer, final Map<String, CompletionsTermsReader> readers) {
        this.delegateFieldsProducer = delegateFieldsProducer;
        this.readers = readers;
    }
    
    CompletionFieldsProducer(final SegmentReadState state) throws IOException {
        final String indexFile = IndexFileNames.segmentFileName(state.segmentInfo.name, state.segmentSuffix, "cmp");
        this.delegateFieldsProducer = null;
        boolean success = false;
        try (final ChecksumIndexInput index = state.directory.openChecksumInput(indexFile, state.context)) {
            final String dictFile = IndexFileNames.segmentFileName(state.segmentInfo.name, state.segmentSuffix, "lkp");
            CodecUtil.checkIndexHeader((DataInput)(this.dictIn = state.directory.openInput(dictFile, state.context)), "completion", 1, 1, state.segmentInfo.getId(), state.segmentSuffix);
            CodecUtil.retrieveChecksum(this.dictIn);
            CodecUtil.checkIndexHeader((DataInput)index, "completion", 1, 1, state.segmentInfo.getId(), state.segmentSuffix);
            final PostingsFormat delegatePostingsFormat = PostingsFormat.forName(index.readString());
            this.delegateFieldsProducer = delegatePostingsFormat.fieldsProducer(state);
            final int numFields = index.readVInt();
            this.readers = new HashMap<String, CompletionsTermsReader>(numFields);
            for (int i = 0; i < numFields; ++i) {
                final int fieldNumber = index.readVInt();
                final long offset = index.readVLong();
                final long minWeight = index.readVLong();
                final long maxWeight = index.readVLong();
                final byte type = index.readByte();
                final FieldInfo fieldInfo = state.fieldInfos.fieldInfo(fieldNumber);
                this.readers.put(fieldInfo.name, new CompletionsTermsReader(this.dictIn, offset, minWeight, maxWeight, type));
            }
            CodecUtil.checkFooter(index);
            success = true;
        }
        finally {
            if (!success) {
                IOUtils.closeWhileHandlingException(new Closeable[] { (Closeable)this.delegateFieldsProducer, (Closeable)this.dictIn });
            }
        }
    }
    
    public void close() throws IOException {
        boolean success = false;
        try {
            this.delegateFieldsProducer.close();
            IOUtils.close(new Closeable[] { (Closeable)this.dictIn });
            success = true;
        }
        finally {
            if (!success) {
                IOUtils.closeWhileHandlingException(new Closeable[] { (Closeable)this.delegateFieldsProducer, (Closeable)this.dictIn });
            }
        }
    }
    
    public void checkIntegrity() throws IOException {
        this.delegateFieldsProducer.checkIntegrity();
    }
    
    public FieldsProducer getMergeInstance() throws IOException {
        return new CompletionFieldsProducer(this.delegateFieldsProducer, this.readers);
    }
    
    public long ramBytesUsed() {
        long ramBytesUsed = this.delegateFieldsProducer.ramBytesUsed();
        for (final CompletionsTermsReader reader : this.readers.values()) {
            ramBytesUsed += reader.ramBytesUsed();
        }
        return ramBytesUsed;
    }
    
    public Collection<Accountable> getChildResources() {
        final List<Accountable> accountableList = new ArrayList<Accountable>();
        for (final Map.Entry<String, CompletionsTermsReader> readerEntry : this.readers.entrySet()) {
            accountableList.add(Accountables.namedAccountable((String)readerEntry.getKey(), (Accountable)readerEntry.getValue()));
        }
        return Collections.unmodifiableCollection((Collection<? extends Accountable>)accountableList);
    }
    
    public Iterator<String> iterator() {
        return this.readers.keySet().iterator();
    }
    
    public Terms terms(final String field) throws IOException {
        final Terms terms = this.delegateFieldsProducer.terms(field);
        if (terms == null) {
            return null;
        }
        return (Terms)new CompletionTerms(terms, this.readers.get(field));
    }
    
    public int size() {
        return this.readers.size();
    }
}
