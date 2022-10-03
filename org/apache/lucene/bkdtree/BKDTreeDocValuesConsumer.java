package org.apache.lucene.bkdtree;

import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.FieldInfo;
import java.util.Iterator;
import org.apache.lucene.util.IOUtils;
import java.io.IOException;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.index.IndexFileNames;
import java.util.HashMap;
import org.apache.lucene.index.SegmentWriteState;
import java.util.Map;
import org.apache.lucene.store.IndexOutput;
import java.io.Closeable;
import org.apache.lucene.codecs.DocValuesConsumer;

@Deprecated
class BKDTreeDocValuesConsumer extends DocValuesConsumer implements Closeable
{
    final DocValuesConsumer delegate;
    final int maxPointsInLeafNode;
    final int maxPointsSortInHeap;
    final IndexOutput out;
    final Map<Integer, Long> fieldIndexFPs;
    final SegmentWriteState state;
    
    public BKDTreeDocValuesConsumer(final DocValuesConsumer delegate, final SegmentWriteState state, final int maxPointsInLeafNode, final int maxPointsSortInHeap) throws IOException {
        this.fieldIndexFPs = new HashMap<Integer, Long>();
        BKDTreeWriter.verifyParams(maxPointsInLeafNode, maxPointsSortInHeap);
        this.delegate = delegate;
        this.maxPointsInLeafNode = maxPointsInLeafNode;
        this.maxPointsSortInHeap = maxPointsSortInHeap;
        this.state = state;
        final String datFileName = IndexFileNames.segmentFileName(state.segmentInfo.name, state.segmentSuffix, "kdd");
        CodecUtil.writeIndexHeader((DataOutput)(this.out = state.directory.createOutput(datFileName, state.context)), "BKDData", 0, state.segmentInfo.getId(), state.segmentSuffix);
    }
    
    public void close() throws IOException {
        boolean success = false;
        try {
            CodecUtil.writeFooter(this.out);
            success = true;
        }
        finally {
            if (success) {
                IOUtils.close(new Closeable[] { (Closeable)this.delegate, (Closeable)this.out });
            }
            else {
                IOUtils.closeWhileHandlingException(new Closeable[] { (Closeable)this.delegate, (Closeable)this.out });
            }
        }
        final String metaFileName = IndexFileNames.segmentFileName(this.state.segmentInfo.name, this.state.segmentSuffix, "kdm");
        final IndexOutput metaOut = this.state.directory.createOutput(metaFileName, this.state.context);
        success = false;
        try {
            CodecUtil.writeIndexHeader((DataOutput)metaOut, "BKDMeta", 0, this.state.segmentInfo.getId(), this.state.segmentSuffix);
            metaOut.writeVInt(this.fieldIndexFPs.size());
            for (final Map.Entry<Integer, Long> ent : this.fieldIndexFPs.entrySet()) {
                metaOut.writeVInt((int)ent.getKey());
                metaOut.writeVLong((long)ent.getValue());
            }
            CodecUtil.writeFooter(metaOut);
            success = true;
        }
        finally {
            if (success) {
                IOUtils.close(new Closeable[] { (Closeable)metaOut });
            }
            else {
                IOUtils.closeWhileHandlingException(new Closeable[] { (Closeable)metaOut });
            }
        }
    }
    
    public void addSortedNumericField(final FieldInfo field, final Iterable<Number> docToValueCount, final Iterable<Number> values) throws IOException {
        this.delegate.addSortedNumericField(field, (Iterable)docToValueCount, (Iterable)values);
        final BKDTreeWriter writer = new BKDTreeWriter(this.maxPointsInLeafNode, this.maxPointsSortInHeap);
        final Iterator<Number> valueIt = values.iterator();
        final Iterator<Number> valueCountIt = docToValueCount.iterator();
        for (int docID = 0; docID < this.state.segmentInfo.maxDoc(); ++docID) {
            assert valueCountIt.hasNext();
            for (int count = valueCountIt.next().intValue(), i = 0; i < count; ++i) {
                assert valueIt.hasNext();
                final long value = valueIt.next().longValue();
                final int latEnc = (int)(value >> 32);
                final int lonEnc = (int)(value & -1L);
                writer.add(latEnc, lonEnc, docID);
            }
        }
        final long indexStartFP = writer.finish(this.out);
        this.fieldIndexFPs.put(field.number, indexStartFP);
    }
    
    public void addNumericField(final FieldInfo field, final Iterable<Number> values) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    public void addBinaryField(final FieldInfo field, final Iterable<BytesRef> values) {
        throw new UnsupportedOperationException();
    }
    
    public void addSortedField(final FieldInfo field, final Iterable<BytesRef> values, final Iterable<Number> docToOrd) {
        throw new UnsupportedOperationException();
    }
    
    public void addSortedSetField(final FieldInfo field, final Iterable<BytesRef> values, final Iterable<Number> docToOrdCount, final Iterable<Number> ords) {
        throw new UnsupportedOperationException();
    }
}
