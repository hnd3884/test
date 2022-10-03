package org.apache.lucene.codecs.idversion;

import java.util.Collections;
import org.apache.lucene.util.Accountable;
import java.util.Collection;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.codecs.BlockTermState;
import java.io.IOException;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.index.SegmentReadState;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.codecs.PostingsReaderBase;

final class IDVersionPostingsReader extends PostingsReaderBase
{
    public void init(final IndexInput termsIn, final SegmentReadState state) throws IOException {
        CodecUtil.checkIndexHeader((DataInput)termsIn, "IDVersionPostingsWriterTerms", 1, 1, state.segmentInfo.getId(), state.segmentSuffix);
    }
    
    public BlockTermState newTermState() {
        return new IDVersionTermState();
    }
    
    public void close() throws IOException {
    }
    
    public void decodeTerm(final long[] longs, final DataInput in, final FieldInfo fieldInfo, final BlockTermState _termState, final boolean absolute) throws IOException {
        final IDVersionTermState termState = (IDVersionTermState)_termState;
        termState.docID = in.readVInt();
        if (absolute) {
            termState.idVersion = in.readVLong();
        }
        else {
            final IDVersionTermState idVersionTermState = termState;
            idVersionTermState.idVersion += in.readZLong();
        }
    }
    
    public PostingsEnum postings(final FieldInfo fieldInfo, final BlockTermState termState, final PostingsEnum reuse, final int flags) throws IOException {
        if (PostingsEnum.featureRequested(flags, (short)24)) {
            SinglePostingsEnum posEnum;
            if (reuse instanceof SinglePostingsEnum) {
                posEnum = (SinglePostingsEnum)reuse;
            }
            else {
                posEnum = new SinglePostingsEnum();
            }
            final IDVersionTermState _termState = (IDVersionTermState)termState;
            posEnum.reset(_termState.docID, _termState.idVersion);
            return posEnum;
        }
        SingleDocsEnum docsEnum;
        if (reuse instanceof SingleDocsEnum) {
            docsEnum = (SingleDocsEnum)reuse;
        }
        else {
            docsEnum = new SingleDocsEnum();
        }
        docsEnum.reset(((IDVersionTermState)termState).docID);
        return docsEnum;
    }
    
    public long ramBytesUsed() {
        return 0L;
    }
    
    public Collection<Accountable> getChildResources() {
        return (Collection<Accountable>)Collections.emptyList();
    }
    
    public void checkIntegrity() throws IOException {
    }
    
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
