package org.apache.lucene.rangetree;

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

class RangeTreeDocValuesConsumer extends DocValuesConsumer implements Closeable
{
    final DocValuesConsumer delegate;
    final int maxPointsInLeafNode;
    final int maxPointsSortInHeap;
    final IndexOutput out;
    final Map<Integer, Long> fieldIndexFPs;
    final SegmentWriteState state;
    
    public RangeTreeDocValuesConsumer(final DocValuesConsumer delegate, final SegmentWriteState state, final int maxPointsInLeafNode, final int maxPointsSortInHeap) throws IOException {
        this.fieldIndexFPs = new HashMap<Integer, Long>();
        RangeTreeWriter.verifyParams(maxPointsInLeafNode, maxPointsSortInHeap);
        this.delegate = delegate;
        this.maxPointsInLeafNode = maxPointsInLeafNode;
        this.maxPointsSortInHeap = maxPointsSortInHeap;
        this.state = state;
        final String datFileName = IndexFileNames.segmentFileName(state.segmentInfo.name, state.segmentSuffix, "ndd");
        CodecUtil.writeIndexHeader((DataOutput)(this.out = state.directory.createOutput(datFileName, state.context)), "RangeTreeData", 0, state.segmentInfo.getId(), state.segmentSuffix);
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
        final String metaFileName = IndexFileNames.segmentFileName(this.state.segmentInfo.name, this.state.segmentSuffix, "ndm");
        final IndexOutput metaOut = this.state.directory.createOutput(metaFileName, this.state.context);
        success = false;
        try {
            CodecUtil.writeIndexHeader((DataOutput)metaOut, "RangeTreeMeta", 0, this.state.segmentInfo.getId(), this.state.segmentSuffix);
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
        final RangeTreeWriter writer = new RangeTreeWriter(this.maxPointsInLeafNode, this.maxPointsSortInHeap);
        final Iterator<Number> valueIt = values.iterator();
        final Iterator<Number> valueCountIt = docToValueCount.iterator();
        for (int docID = 0; docID < this.state.segmentInfo.maxDoc(); ++docID) {
            assert valueCountIt.hasNext();
            for (int count = valueCountIt.next().intValue(), i = 0; i < count; ++i) {
                assert valueIt.hasNext();
                writer.add(valueIt.next().longValue(), docID);
            }
        }
        final long indexStartFP = writer.finish(this.out);
        this.fieldIndexFPs.put(field.number, indexStartFP);
    }
    
    public void addNumericField(final FieldInfo field, final Iterable<Number> values) throws IOException {
        throw new UnsupportedOperationException("use either SortedNumericDocValuesField or SortedSetDocValuesField");
    }
    
    public void addBinaryField(final FieldInfo field, final Iterable<BytesRef> values) {
        throw new UnsupportedOperationException("use either SortedNumericDocValuesField or SortedSetDocValuesField");
    }
    
    public void addSortedField(final FieldInfo field, final Iterable<BytesRef> values, final Iterable<Number> docToOrd) {
        throw new UnsupportedOperationException("use either SortedNumericDocValuesField or SortedSetDocValuesField");
    }
    
    public void addSortedSetField(final FieldInfo field, final Iterable<BytesRef> values, final Iterable<Number> docToOrdCount, final Iterable<Number> ords) throws IOException {
        this.delegate.addSortedSetField(field, (Iterable)values, (Iterable)docToOrdCount, (Iterable)ords);
        final RangeTreeWriter writer = new RangeTreeWriter(this.maxPointsInLeafNode, this.maxPointsSortInHeap);
        final Iterator<Number> docToOrdCountIt = docToOrdCount.iterator();
        final Iterator<Number> ordsIt = ords.iterator();
        for (int docID = 0; docID < this.state.segmentInfo.maxDoc(); ++docID) {
            assert docToOrdCountIt.hasNext();
            for (int count = docToOrdCountIt.next().intValue(), i = 0; i < count; ++i) {
                assert ordsIt.hasNext();
                final long ord = ordsIt.next().longValue();
                writer.add(ord, docID);
            }
        }
        final long indexStartFP = writer.finish(this.out);
        this.fieldIndexFPs.put(field.number, indexStartFP);
    }
}
