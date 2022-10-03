package org.apache.lucene.search.suggest.document;

import org.apache.lucene.store.ByteArrayDataInput;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.Terms;
import java.util.Iterator;
import org.apache.lucene.index.Fields;
import java.io.IOException;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.index.IndexFileNames;
import java.util.HashMap;
import org.apache.lucene.codecs.PostingsFormat;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.index.SegmentWriteState;
import java.util.Map;
import org.apache.lucene.codecs.FieldsConsumer;

final class CompletionFieldsConsumer extends FieldsConsumer
{
    private final String delegatePostingsFormatName;
    private final Map<String, CompletionMetaData> seenFields;
    private final SegmentWriteState state;
    private IndexOutput dictOut;
    private FieldsConsumer delegateFieldsConsumer;
    private boolean closed;
    
    CompletionFieldsConsumer(final PostingsFormat delegatePostingsFormat, final SegmentWriteState state) throws IOException {
        this.seenFields = new HashMap<String, CompletionMetaData>();
        this.closed = false;
        this.delegatePostingsFormatName = delegatePostingsFormat.getName();
        this.state = state;
        final String dictFile = IndexFileNames.segmentFileName(state.segmentInfo.name, state.segmentSuffix, "lkp");
        boolean success = false;
        try {
            this.delegateFieldsConsumer = delegatePostingsFormat.fieldsConsumer(state);
            CodecUtil.writeIndexHeader((DataOutput)(this.dictOut = state.directory.createOutput(dictFile, state.context)), "completion", 1, state.segmentInfo.getId(), state.segmentSuffix);
            success = true;
        }
        finally {
            if (!success) {
                IOUtils.closeWhileHandlingException(new Closeable[] { (Closeable)this.dictOut, (Closeable)this.delegateFieldsConsumer });
            }
        }
    }
    
    public void write(final Fields fields) throws IOException {
        this.delegateFieldsConsumer.write(fields);
        for (final String field : fields) {
            final CompletionTermWriter termWriter = new CompletionTermWriter();
            final Terms terms = fields.terms(field);
            final TermsEnum termsEnum = terms.iterator();
            BytesRef term;
            while ((term = termsEnum.next()) != null) {
                termWriter.write(term, termsEnum);
            }
            final long filePointer = this.dictOut.getFilePointer();
            if (termWriter.finish(this.dictOut)) {
                this.seenFields.put(field, new CompletionMetaData(filePointer, termWriter.minWeight, termWriter.maxWeight, termWriter.type));
            }
        }
    }
    
    public void close() throws IOException {
        if (this.closed) {
            return;
        }
        this.closed = true;
        final String indexFile = IndexFileNames.segmentFileName(this.state.segmentInfo.name, this.state.segmentSuffix, "cmp");
        boolean success = false;
        try (final IndexOutput indexOut = this.state.directory.createOutput(indexFile, this.state.context)) {
            this.delegateFieldsConsumer.close();
            CodecUtil.writeIndexHeader((DataOutput)indexOut, "completion", 1, this.state.segmentInfo.getId(), this.state.segmentSuffix);
            indexOut.writeString(this.delegatePostingsFormatName);
            indexOut.writeVInt(this.seenFields.size());
            for (final Map.Entry<String, CompletionMetaData> seenField : this.seenFields.entrySet()) {
                final FieldInfo fieldInfo = this.state.fieldInfos.fieldInfo((String)seenField.getKey());
                indexOut.writeVInt(fieldInfo.number);
                final CompletionMetaData metaData = seenField.getValue();
                indexOut.writeVLong(metaData.filePointer);
                indexOut.writeVLong(metaData.minWeight);
                indexOut.writeVLong(metaData.maxWeight);
                indexOut.writeByte(metaData.type);
            }
            CodecUtil.writeFooter(indexOut);
            CodecUtil.writeFooter(this.dictOut);
            IOUtils.close(new Closeable[] { (Closeable)this.dictOut });
            success = true;
        }
        finally {
            if (!success) {
                IOUtils.closeWhileHandlingException(new Closeable[] { (Closeable)this.dictOut, (Closeable)this.delegateFieldsConsumer });
            }
        }
    }
    
    private static class CompletionMetaData
    {
        private final long filePointer;
        private final long minWeight;
        private final long maxWeight;
        private final byte type;
        
        private CompletionMetaData(final long filePointer, final long minWeight, final long maxWeight, final byte type) {
            this.filePointer = filePointer;
            this.minWeight = minWeight;
            this.maxWeight = maxWeight;
            this.type = type;
        }
    }
    
    private static class CompletionTermWriter
    {
        private PostingsEnum postingsEnum;
        private int docCount;
        private long maxWeight;
        private long minWeight;
        private byte type;
        private boolean first;
        private final BytesRefBuilder scratch;
        private final NRTSuggesterBuilder builder;
        
        public CompletionTermWriter() {
            this.postingsEnum = null;
            this.docCount = 0;
            this.maxWeight = 0L;
            this.minWeight = Long.MAX_VALUE;
            this.scratch = new BytesRefBuilder();
            this.builder = new NRTSuggesterBuilder();
            this.first = true;
        }
        
        public boolean finish(final IndexOutput output) throws IOException {
            final boolean stored = this.builder.store((DataOutput)output);
            assert this.docCount == 0 : "the FST is null but docCount is != 0 actual value: [" + this.docCount + "]";
            if (this.docCount == 0) {
                this.minWeight = 0L;
            }
            return stored;
        }
        
        public void write(final BytesRef term, final TermsEnum termsEnum) throws IOException {
            this.postingsEnum = termsEnum.postings(this.postingsEnum, 88);
            this.builder.startTerm(term);
            int docFreq = 0;
            while (this.postingsEnum.nextDoc() != Integer.MAX_VALUE) {
                final int docID = this.postingsEnum.docID();
                for (int i = 0; i < this.postingsEnum.freq(); ++i) {
                    this.postingsEnum.nextPosition();
                    assert this.postingsEnum.getPayload() != null;
                    final BytesRef payload = this.postingsEnum.getPayload();
                    final ByteArrayDataInput input = new ByteArrayDataInput(payload.bytes, payload.offset, payload.length);
                    final int len = input.readVInt();
                    this.scratch.grow(len);
                    this.scratch.setLength(len);
                    input.readBytes(this.scratch.bytes(), 0, this.scratch.length());
                    final long weight = input.readVInt() - 1;
                    this.maxWeight = Math.max(this.maxWeight, weight);
                    this.minWeight = Math.min(this.minWeight, weight);
                    final byte type = input.readByte();
                    if (this.first) {
                        this.type = type;
                        this.first = false;
                    }
                    else if (this.type != type) {
                        throw new IllegalArgumentException("single field name has mixed types");
                    }
                    this.builder.addEntry(docID, this.scratch.get(), weight);
                }
                ++docFreq;
                this.docCount = Math.max(this.docCount, docFreq + 1);
            }
            this.builder.finishTerm();
        }
    }
}
