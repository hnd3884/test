package org.apache.lucene.rangetree;

import java.util.Iterator;
import java.util.List;
import org.apache.lucene.util.Accountables;
import java.util.ArrayList;
import org.apache.lucene.util.Accountable;
import java.util.Collection;
import org.apache.lucene.util.Bits;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import org.apache.lucene.index.SortedNumericDocValues;
import org.apache.lucene.index.FieldInfo;
import java.io.IOException;
import org.apache.lucene.store.ChecksumIndexInput;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.index.IndexFileNames;
import java.util.HashMap;
import org.apache.lucene.index.SegmentReadState;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.lucene.store.IndexInput;
import java.util.Map;
import org.apache.lucene.codecs.DocValuesProducer;

class RangeTreeDocValuesProducer extends DocValuesProducer
{
    private final Map<String, RangeTreeReader> treeReaders;
    private final Map<Integer, Long> fieldToIndexFPs;
    private final IndexInput datIn;
    private final AtomicLong ramBytesUsed;
    private final int maxDoc;
    private final DocValuesProducer delegate;
    private final boolean merging;
    
    public RangeTreeDocValuesProducer(final DocValuesProducer delegate, final SegmentReadState state) throws IOException {
        this.treeReaders = new HashMap<String, RangeTreeReader>();
        this.fieldToIndexFPs = new HashMap<Integer, Long>();
        final String metaFileName = IndexFileNames.segmentFileName(state.segmentInfo.name, state.segmentSuffix, "ndm");
        final ChecksumIndexInput metaIn = state.directory.openChecksumInput(metaFileName, state.context);
        CodecUtil.checkIndexHeader((DataInput)metaIn, "RangeTreeMeta", 0, 0, state.segmentInfo.getId(), state.segmentSuffix);
        for (int fieldCount = metaIn.readVInt(), i = 0; i < fieldCount; ++i) {
            final int fieldNumber = metaIn.readVInt();
            final long indexFP = metaIn.readVLong();
            this.fieldToIndexFPs.put(fieldNumber, indexFP);
        }
        CodecUtil.checkFooter(metaIn);
        metaIn.close();
        final String datFileName = IndexFileNames.segmentFileName(state.segmentInfo.name, state.segmentSuffix, "ndd");
        CodecUtil.checkIndexHeader((DataInput)(this.datIn = state.directory.openInput(datFileName, state.context)), "RangeTreeData", 0, 0, state.segmentInfo.getId(), state.segmentSuffix);
        this.ramBytesUsed = new AtomicLong(RamUsageEstimator.shallowSizeOfInstance((Class)this.getClass()));
        this.maxDoc = state.segmentInfo.maxDoc();
        this.delegate = delegate;
        this.merging = false;
    }
    
    RangeTreeDocValuesProducer(final RangeTreeDocValuesProducer orig) throws IOException {
        this.treeReaders = new HashMap<String, RangeTreeReader>();
        this.fieldToIndexFPs = new HashMap<Integer, Long>();
        assert Thread.holdsLock(orig);
        this.datIn = orig.datIn.clone();
        this.ramBytesUsed = new AtomicLong(orig.ramBytesUsed.get());
        this.delegate = orig.delegate.getMergeInstance();
        this.fieldToIndexFPs.putAll(orig.fieldToIndexFPs);
        this.treeReaders.putAll(orig.treeReaders);
        this.merging = true;
        this.maxDoc = orig.maxDoc;
    }
    
    public synchronized SortedNumericDocValues getSortedNumeric(final FieldInfo field) throws IOException {
        RangeTreeReader treeReader = this.treeReaders.get(field.name);
        if (treeReader == null) {
            final Long fp = this.fieldToIndexFPs.get(field.number);
            assert fp != null;
            final IndexInput clone = this.datIn.clone();
            clone.seek((long)fp);
            treeReader = new RangeTreeReader(clone);
            if (!this.merging) {
                this.treeReaders.put(field.name, treeReader);
                this.ramBytesUsed.addAndGet(treeReader.ramBytesUsed());
            }
        }
        return new RangeTreeSortedNumericDocValues(treeReader, this.delegate.getSortedNumeric(field));
    }
    
    public void close() throws IOException {
        IOUtils.close(new Closeable[] { (Closeable)this.datIn, (Closeable)this.delegate });
    }
    
    public void checkIntegrity() throws IOException {
        CodecUtil.checksumEntireFile(this.datIn);
    }
    
    public NumericDocValues getNumeric(final FieldInfo field) {
        throw new UnsupportedOperationException();
    }
    
    public BinaryDocValues getBinary(final FieldInfo field) {
        throw new UnsupportedOperationException();
    }
    
    public SortedDocValues getSorted(final FieldInfo field) {
        throw new UnsupportedOperationException();
    }
    
    public synchronized SortedSetDocValues getSortedSet(final FieldInfo field) throws IOException {
        RangeTreeReader treeReader = this.treeReaders.get(field.name);
        if (treeReader == null) {
            final Long fp = this.fieldToIndexFPs.get(field.number);
            assert fp != null;
            final IndexInput clone = this.datIn.clone();
            clone.seek((long)fp);
            treeReader = new RangeTreeReader(clone);
            if (!this.merging) {
                this.treeReaders.put(field.name, treeReader);
                this.ramBytesUsed.addAndGet(treeReader.ramBytesUsed());
            }
        }
        return new RangeTreeSortedSetDocValues(treeReader, this.delegate.getSortedSet(field));
    }
    
    public Bits getDocsWithField(final FieldInfo field) throws IOException {
        return this.delegate.getDocsWithField(field);
    }
    
    public synchronized Collection<Accountable> getChildResources() {
        final List<Accountable> resources = new ArrayList<Accountable>();
        for (final Map.Entry<String, RangeTreeReader> ent : this.treeReaders.entrySet()) {
            resources.add(Accountables.namedAccountable("field " + ent.getKey(), (Accountable)ent.getValue()));
        }
        resources.add(Accountables.namedAccountable("delegate", (Accountable)this.delegate));
        return resources;
    }
    
    public synchronized DocValuesProducer getMergeInstance() throws IOException {
        return new RangeTreeDocValuesProducer(this);
    }
    
    public long ramBytesUsed() {
        return this.ramBytesUsed.get() + this.delegate.ramBytesUsed();
    }
}
