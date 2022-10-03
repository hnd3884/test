package org.apache.lucene.index;

import org.apache.lucene.codecs.FieldInfosFormat;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.codecs.DocValuesConsumer;
import java.util.NoSuchElementException;
import org.apache.lucene.util.InfoStream;
import org.apache.lucene.store.FlushInfo;
import java.util.Set;
import org.apache.lucene.codecs.DocValuesFormat;
import java.util.Iterator;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.store.TrackingDirectoryWrapper;
import org.apache.lucene.store.Directory;
import org.apache.lucene.codecs.LiveDocsFormat;
import org.apache.lucene.util.MutableBits;
import java.io.IOException;
import org.apache.lucene.store.IOContext;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.util.Bits;
import java.util.concurrent.atomic.AtomicInteger;

class ReadersAndUpdates
{
    public final SegmentCommitInfo info;
    private final AtomicInteger refCount;
    private final IndexWriter writer;
    private SegmentReader reader;
    private Bits liveDocs;
    private int pendingDeleteCount;
    private boolean liveDocsShared;
    private boolean isMerging;
    private final Map<String, DocValuesFieldUpdates> mergingDVUpdates;
    
    public ReadersAndUpdates(final IndexWriter writer, final SegmentCommitInfo info) {
        this.refCount = new AtomicInteger(1);
        this.isMerging = false;
        this.mergingDVUpdates = new HashMap<String, DocValuesFieldUpdates>();
        this.writer = writer;
        this.info = info;
        this.liveDocsShared = true;
    }
    
    public ReadersAndUpdates(final IndexWriter writer, final SegmentReader reader) {
        this.refCount = new AtomicInteger(1);
        this.isMerging = false;
        this.mergingDVUpdates = new HashMap<String, DocValuesFieldUpdates>();
        this.writer = writer;
        this.reader = reader;
        this.info = reader.getSegmentInfo();
        this.liveDocs = reader.getLiveDocs();
        this.liveDocsShared = true;
        this.pendingDeleteCount = reader.numDeletedDocs() - this.info.getDelCount();
        assert this.pendingDeleteCount >= 0 : "got " + this.pendingDeleteCount + " reader.numDeletedDocs()=" + reader.numDeletedDocs() + " info.getDelCount()=" + this.info.getDelCount() + " maxDoc=" + reader.maxDoc() + " numDocs=" + reader.numDocs();
    }
    
    public void incRef() {
        final int rc = this.refCount.incrementAndGet();
        assert rc > 1;
    }
    
    public void decRef() {
        final int rc = this.refCount.decrementAndGet();
        assert rc >= 0;
    }
    
    public int refCount() {
        final int rc = this.refCount.get();
        assert rc >= 0;
        return rc;
    }
    
    public synchronized int getPendingDeleteCount() {
        return this.pendingDeleteCount;
    }
    
    public synchronized boolean verifyDocCounts() {
        int count;
        if (this.liveDocs != null) {
            count = 0;
            for (int docID = 0; docID < this.info.info.maxDoc(); ++docID) {
                if (this.liveDocs.get(docID)) {
                    ++count;
                }
            }
        }
        else {
            count = this.info.info.maxDoc();
        }
        assert this.info.info.maxDoc() - this.info.getDelCount() - this.pendingDeleteCount == count : "info.maxDoc=" + this.info.info.maxDoc() + " info.getDelCount()=" + this.info.getDelCount() + " pendingDeleteCount=" + this.pendingDeleteCount + " count=" + count;
        return true;
    }
    
    public SegmentReader getReader(final IOContext context) throws IOException {
        if (this.reader == null) {
            this.reader = new SegmentReader(this.info, context);
            if (this.liveDocs == null) {
                this.liveDocs = this.reader.getLiveDocs();
            }
        }
        this.reader.incRef();
        return this.reader;
    }
    
    public synchronized void release(final SegmentReader sr) throws IOException {
        assert this.info == sr.getSegmentInfo();
        sr.decRef();
    }
    
    public synchronized boolean delete(final int docID) {
        assert this.liveDocs != null;
        assert Thread.holdsLock(this.writer);
        assert docID >= 0 && docID < this.liveDocs.length() : "out of bounds: docid=" + docID + " liveDocsLength=" + this.liveDocs.length() + " seg=" + this.info.info.name + " maxDoc=" + this.info.info.maxDoc();
        assert !this.liveDocsShared;
        final boolean didDelete = this.liveDocs.get(docID);
        if (didDelete) {
            ((MutableBits)this.liveDocs).clear(docID);
            ++this.pendingDeleteCount;
        }
        return didDelete;
    }
    
    public synchronized void dropReaders() throws IOException {
        if (this.reader != null) {
            try {
                this.reader.decRef();
            }
            finally {
                this.reader = null;
            }
        }
        this.decRef();
    }
    
    public synchronized SegmentReader getReadOnlyClone(final IOContext context) throws IOException {
        if (this.reader == null) {
            this.getReader(context).decRef();
            assert this.reader != null;
        }
        this.liveDocsShared = true;
        if (this.liveDocs != null) {
            return new SegmentReader(this.reader.getSegmentInfo(), this.reader, this.liveDocs, this.info.info.maxDoc() - this.info.getDelCount() - this.pendingDeleteCount);
        }
        assert this.reader.getLiveDocs() == null;
        this.reader.incRef();
        return this.reader;
    }
    
    public synchronized void initWritableLiveDocs() throws IOException {
        assert Thread.holdsLock(this.writer);
        assert this.info.info.maxDoc() > 0;
        if (this.liveDocsShared) {
            final LiveDocsFormat liveDocsFormat = this.info.info.getCodec().liveDocsFormat();
            if (this.liveDocs == null) {
                this.liveDocs = liveDocsFormat.newLiveDocs(this.info.info.maxDoc());
            }
            else {
                this.liveDocs = liveDocsFormat.newLiveDocs(this.liveDocs);
            }
            this.liveDocsShared = false;
        }
    }
    
    public synchronized Bits getLiveDocs() {
        assert Thread.holdsLock(this.writer);
        return this.liveDocs;
    }
    
    public synchronized Bits getReadOnlyLiveDocs() {
        assert Thread.holdsLock(this.writer);
        this.liveDocsShared = true;
        return this.liveDocs;
    }
    
    public synchronized void dropChanges() {
        this.pendingDeleteCount = 0;
        this.dropMergingUpdates();
    }
    
    public synchronized boolean writeLiveDocs(final Directory dir) throws IOException {
        assert Thread.holdsLock(this.writer);
        if (this.pendingDeleteCount == 0) {
            return false;
        }
        assert this.liveDocs.length() == this.info.info.maxDoc();
        final TrackingDirectoryWrapper trackingDir = new TrackingDirectoryWrapper(dir);
        boolean success = false;
        try {
            final Codec codec = this.info.info.getCodec();
            codec.liveDocsFormat().writeLiveDocs((MutableBits)this.liveDocs, trackingDir, this.info, this.pendingDeleteCount, IOContext.DEFAULT);
            success = true;
        }
        finally {
            if (!success) {
                this.info.advanceNextWriteDelGen();
                for (final String fileName : trackingDir.getCreatedFiles()) {
                    IOUtils.deleteFilesIgnoringExceptions(dir, fileName);
                }
            }
        }
        this.info.advanceDelGen();
        this.info.setDelCount(this.info.getDelCount() + this.pendingDeleteCount);
        this.pendingDeleteCount = 0;
        return true;
    }
    
    private void handleNumericDVUpdates(final FieldInfos infos, final Map<String, NumericDocValuesFieldUpdates> updates, final Directory dir, final DocValuesFormat dvFormat, final SegmentReader reader, final Map<Integer, Set<String>> fieldFiles) throws IOException {
        for (final Map.Entry<String, NumericDocValuesFieldUpdates> e : updates.entrySet()) {
            final String field = e.getKey();
            final NumericDocValuesFieldUpdates fieldUpdates = e.getValue();
            final long nextDocValuesGen = this.info.getNextDocValuesGen();
            final String segmentSuffix = Long.toString(nextDocValuesGen, 36);
            final long estUpdatesSize = fieldUpdates.ramBytesPerDoc() * this.info.info.maxDoc();
            final IOContext updatesContext = new IOContext(new FlushInfo(this.info.info.maxDoc(), estUpdatesSize));
            final FieldInfo fieldInfo = infos.fieldInfo(field);
            assert fieldInfo != null;
            fieldInfo.setDocValuesGen(nextDocValuesGen);
            final FieldInfos fieldInfos = new FieldInfos(new FieldInfo[] { fieldInfo });
            final TrackingDirectoryWrapper trackingDir = new TrackingDirectoryWrapper(dir);
            final SegmentWriteState state = new SegmentWriteState(null, trackingDir, this.info.info, fieldInfos, null, updatesContext, segmentSuffix);
            try (final DocValuesConsumer fieldsConsumer = dvFormat.fieldsConsumer(state)) {
                fieldsConsumer.addNumericField(fieldInfo, new Iterable<Number>() {
                    final NumericDocValues currentValues = reader.getNumericDocValues(field);
                    final Bits docsWithField = reader.getDocsWithField(field);
                    final int maxDoc = reader.maxDoc();
                    final NumericDocValuesFieldUpdates.Iterator updatesIter = fieldUpdates.iterator();
                    
                    @Override
                    public Iterator<Number> iterator() {
                        this.updatesIter.reset();
                        return new Iterator<Number>() {
                            int curDoc = -1;
                            int updateDoc = Iterable.this.updatesIter.nextDoc();
                            
                            @Override
                            public boolean hasNext() {
                                return this.curDoc < Iterable.this.maxDoc - 1;
                            }
                            
                            @Override
                            public Number next() {
                                if (++this.curDoc >= Iterable.this.maxDoc) {
                                    throw new NoSuchElementException("no more documents to return values for");
                                }
                                if (this.curDoc == this.updateDoc) {
                                    final Long value = Iterable.this.updatesIter.value();
                                    this.updateDoc = Iterable.this.updatesIter.nextDoc();
                                    return value;
                                }
                                assert this.curDoc < this.updateDoc;
                                if (Iterable.this.currentValues != null && Iterable.this.docsWithField.get(this.curDoc)) {
                                    return Iterable.this.currentValues.get(this.curDoc);
                                }
                                return null;
                            }
                            
                            @Override
                            public void remove() {
                                throw new UnsupportedOperationException("this iterator does not support removing elements");
                            }
                        };
                    }
                });
            }
            this.info.advanceDocValuesGen();
            assert !fieldFiles.containsKey(fieldInfo.number);
            fieldFiles.put(fieldInfo.number, trackingDir.getCreatedFiles());
        }
    }
    
    private void handleBinaryDVUpdates(final FieldInfos infos, final Map<String, BinaryDocValuesFieldUpdates> updates, final TrackingDirectoryWrapper dir, final DocValuesFormat dvFormat, final SegmentReader reader, final Map<Integer, Set<String>> fieldFiles) throws IOException {
        for (final Map.Entry<String, BinaryDocValuesFieldUpdates> e : updates.entrySet()) {
            final String field = e.getKey();
            final BinaryDocValuesFieldUpdates fieldUpdates = e.getValue();
            final long nextDocValuesGen = this.info.getNextDocValuesGen();
            final String segmentSuffix = Long.toString(nextDocValuesGen, 36);
            final long estUpdatesSize = fieldUpdates.ramBytesPerDoc() * this.info.info.maxDoc();
            final IOContext updatesContext = new IOContext(new FlushInfo(this.info.info.maxDoc(), estUpdatesSize));
            final FieldInfo fieldInfo = infos.fieldInfo(field);
            assert fieldInfo != null;
            fieldInfo.setDocValuesGen(nextDocValuesGen);
            final FieldInfos fieldInfos = new FieldInfos(new FieldInfo[] { fieldInfo });
            final TrackingDirectoryWrapper trackingDir = new TrackingDirectoryWrapper(dir);
            final SegmentWriteState state = new SegmentWriteState(null, trackingDir, this.info.info, fieldInfos, null, updatesContext, segmentSuffix);
            try (final DocValuesConsumer fieldsConsumer = dvFormat.fieldsConsumer(state)) {
                fieldsConsumer.addBinaryField(fieldInfo, new Iterable<BytesRef>() {
                    final BinaryDocValues currentValues = reader.getBinaryDocValues(field);
                    final Bits docsWithField = reader.getDocsWithField(field);
                    final int maxDoc = reader.maxDoc();
                    final BinaryDocValuesFieldUpdates.Iterator updatesIter = fieldUpdates.iterator();
                    
                    @Override
                    public Iterator<BytesRef> iterator() {
                        this.updatesIter.reset();
                        return new Iterator<BytesRef>() {
                            int curDoc = -1;
                            int updateDoc = Iterable.this.updatesIter.nextDoc();
                            
                            @Override
                            public boolean hasNext() {
                                return this.curDoc < Iterable.this.maxDoc - 1;
                            }
                            
                            @Override
                            public BytesRef next() {
                                if (++this.curDoc >= Iterable.this.maxDoc) {
                                    throw new NoSuchElementException("no more documents to return values for");
                                }
                                if (this.curDoc == this.updateDoc) {
                                    final BytesRef value = Iterable.this.updatesIter.value();
                                    this.updateDoc = Iterable.this.updatesIter.nextDoc();
                                    return value;
                                }
                                assert this.curDoc < this.updateDoc;
                                if (Iterable.this.currentValues != null && Iterable.this.docsWithField.get(this.curDoc)) {
                                    return Iterable.this.currentValues.get(this.curDoc);
                                }
                                return null;
                            }
                            
                            @Override
                            public void remove() {
                                throw new UnsupportedOperationException("this iterator does not support removing elements");
                            }
                        };
                    }
                });
            }
            this.info.advanceDocValuesGen();
            assert !fieldFiles.containsKey(fieldInfo.number);
            fieldFiles.put(fieldInfo.number, trackingDir.getCreatedFiles());
        }
    }
    
    private Set<String> writeFieldInfosGen(final FieldInfos fieldInfos, final Directory dir, final DocValuesFormat dvFormat, final FieldInfosFormat infosFormat) throws IOException {
        final long nextFieldInfosGen = this.info.getNextFieldInfosGen();
        final String segmentSuffix = Long.toString(nextFieldInfosGen, 36);
        final long estInfosSize = 40 + 90 * fieldInfos.size();
        final IOContext infosContext = new IOContext(new FlushInfo(this.info.info.maxDoc(), estInfosSize));
        final TrackingDirectoryWrapper trackingDir = new TrackingDirectoryWrapper(dir);
        infosFormat.write(trackingDir, this.info.info, segmentSuffix, fieldInfos, infosContext);
        this.info.advanceFieldInfosGen();
        return trackingDir.getCreatedFiles();
    }
    
    public synchronized void writeFieldUpdates(final Directory dir, final DocValuesFieldUpdates.Container dvUpdates) throws IOException {
        assert Thread.holdsLock(this.writer);
        assert dvUpdates.any();
        final TrackingDirectoryWrapper trackingDir = new TrackingDirectoryWrapper(dir);
        final Map<Integer, Set<String>> newDVFiles = new HashMap<Integer, Set<String>>();
        Set<String> fieldInfosFiles = null;
        FieldInfos fieldInfos = null;
        boolean success = false;
        try {
            final Codec codec = this.info.info.getCodec();
            final SegmentReader reader = (this.reader == null) ? new SegmentReader(this.info, IOContext.READONCE) : this.reader;
            try {
                final FieldInfos.Builder builder = new FieldInfos.Builder(this.writer.globalFieldNumberMap);
                for (final FieldInfo fi : reader.getFieldInfos()) {
                    final FieldInfo clone = builder.add(fi);
                    for (final Map.Entry<String, String> e : fi.attributes().entrySet()) {
                        clone.putAttribute(e.getKey(), e.getValue());
                    }
                    clone.setDocValuesGen(fi.getDocValuesGen());
                }
                for (final String f : dvUpdates.numericDVUpdates.keySet()) {
                    final FieldInfo fieldInfo = builder.getOrAdd(f);
                    fieldInfo.setDocValuesType(DocValuesType.NUMERIC);
                }
                for (final String f : dvUpdates.binaryDVUpdates.keySet()) {
                    final FieldInfo fieldInfo = builder.getOrAdd(f);
                    fieldInfo.setDocValuesType(DocValuesType.BINARY);
                }
                fieldInfos = builder.finish();
                final DocValuesFormat docValuesFormat = codec.docValuesFormat();
                this.handleNumericDVUpdates(fieldInfos, dvUpdates.numericDVUpdates, trackingDir, docValuesFormat, reader, newDVFiles);
                this.handleBinaryDVUpdates(fieldInfos, dvUpdates.binaryDVUpdates, trackingDir, docValuesFormat, reader, newDVFiles);
                fieldInfosFiles = this.writeFieldInfosGen(fieldInfos, trackingDir, docValuesFormat, codec.fieldInfosFormat());
            }
            finally {
                if (reader != this.reader) {
                    reader.close();
                }
            }
            success = true;
        }
        finally {
            if (!success) {
                this.info.advanceNextWriteFieldInfosGen();
                this.info.advanceNextWriteDocValuesGen();
                for (final String fileName : trackingDir.getCreatedFiles()) {
                    IOUtils.deleteFilesIgnoringExceptions(dir, fileName);
                }
            }
        }
        if (this.isMerging) {
            for (final Map.Entry<String, NumericDocValuesFieldUpdates> e2 : dvUpdates.numericDVUpdates.entrySet()) {
                final DocValuesFieldUpdates updates = this.mergingDVUpdates.get(e2.getKey());
                if (updates == null) {
                    this.mergingDVUpdates.put(e2.getKey(), e2.getValue());
                }
                else {
                    updates.merge(e2.getValue());
                }
            }
            for (final Map.Entry<String, BinaryDocValuesFieldUpdates> e3 : dvUpdates.binaryDVUpdates.entrySet()) {
                final DocValuesFieldUpdates updates = this.mergingDVUpdates.get(e3.getKey());
                if (updates == null) {
                    this.mergingDVUpdates.put(e3.getKey(), e3.getValue());
                }
                else {
                    updates.merge(e3.getValue());
                }
            }
        }
        assert fieldInfosFiles != null;
        this.info.setFieldInfosFiles(fieldInfosFiles);
        assert !newDVFiles.isEmpty();
        for (final Map.Entry<Integer, Set<String>> e4 : this.info.getDocValuesUpdatesFiles().entrySet()) {
            if (!newDVFiles.containsKey(e4.getKey())) {
                newDVFiles.put(e4.getKey(), e4.getValue());
            }
        }
        this.info.setDocValuesUpdatesFiles(newDVFiles);
        this.writer.checkpoint();
        if (this.reader != null) {
            final SegmentReader newReader = new SegmentReader(this.info, this.reader, this.liveDocs, this.info.info.maxDoc() - this.info.getDelCount() - this.pendingDeleteCount);
            boolean reopened = false;
            try {
                this.reader.decRef();
                this.reader = newReader;
                reopened = true;
            }
            finally {
                if (!reopened) {
                    newReader.decRef();
                }
            }
        }
    }
    
    synchronized SegmentReader getReaderForMerge(final IOContext context) throws IOException {
        assert Thread.holdsLock(this.writer);
        this.isMerging = true;
        return this.getReader(context);
    }
    
    public synchronized void dropMergingUpdates() {
        this.mergingDVUpdates.clear();
        this.isMerging = false;
    }
    
    public synchronized Map<String, DocValuesFieldUpdates> getMergingFieldUpdates() {
        return this.mergingDVUpdates;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ReadersAndLiveDocs(seg=").append(this.info);
        sb.append(" pendingDeleteCount=").append(this.pendingDeleteCount);
        sb.append(" liveDocsShared=").append(this.liveDocsShared);
        return sb.toString();
    }
}
