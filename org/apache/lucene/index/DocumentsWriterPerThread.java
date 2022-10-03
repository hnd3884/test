package org.apache.lucene.index;

import org.apache.lucene.util.MutableBits;
import org.apache.lucene.search.similarities.Similarity;
import java.util.Collection;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.FlushInfo;
import java.util.Iterator;
import org.apache.lucene.analysis.Analyzer;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import org.apache.lucene.util.StringHelper;
import java.util.Collections;
import org.apache.lucene.util.Version;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.lucene.util.IntBlockPool;
import org.apache.lucene.util.ByteBlockPool;
import java.text.NumberFormat;
import org.apache.lucene.util.InfoStream;
import org.apache.lucene.util.Counter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.TrackingDirectoryWrapper;
import org.apache.lucene.codecs.Codec;

class DocumentsWriterPerThread
{
    static final IndexingChain defaultIndexingChain;
    private static final boolean INFO_VERBOSE = false;
    final Codec codec;
    final TrackingDirectoryWrapper directory;
    final Directory directoryOrig;
    final DocState docState;
    final DocConsumer consumer;
    final Counter bytesUsed;
    SegmentWriteState flushState;
    final BufferedUpdates pendingUpdates;
    private final SegmentInfo segmentInfo;
    boolean aborted;
    private final FieldInfos.Builder fieldInfos;
    private final InfoStream infoStream;
    private int numDocsInRAM;
    final DocumentsWriterDeleteQueue deleteQueue;
    private final DocumentsWriterDeleteQueue.DeleteSlice deleteSlice;
    private final NumberFormat nf;
    final ByteBlockPool.Allocator byteBlockAllocator;
    final IntBlockPool.Allocator intBlockAllocator;
    private final AtomicLong pendingNumDocs;
    private final LiveIndexWriterConfig indexWriterConfig;
    private final boolean enableTestPoints;
    private final IndexWriter indexWriter;
    private final Set<String> filesToDelete;
    static final int BYTE_BLOCK_NOT_MASK = -32768;
    static final int MAX_TERM_LENGTH_UTF8 = 32766;
    
    void abort() {
        this.aborted = true;
        try {
            if (this.infoStream.isEnabled("DWPT")) {
                this.infoStream.message("DWPT", "now abort");
            }
            try {
                this.consumer.abort();
            }
            catch (final Throwable t) {}
            this.pendingUpdates.clear();
        }
        finally {
            if (this.infoStream.isEnabled("DWPT")) {
                this.infoStream.message("DWPT", "done abort");
            }
        }
    }
    
    public DocumentsWriterPerThread(final IndexWriter writer, final String segmentName, final Directory directoryOrig, final Directory directory, final LiveIndexWriterConfig indexWriterConfig, final InfoStream infoStream, final DocumentsWriterDeleteQueue deleteQueue, final FieldInfos.Builder fieldInfos, final AtomicLong pendingNumDocs, final boolean enableTestPoints) throws IOException {
        this.aborted = false;
        this.nf = NumberFormat.getInstance(Locale.ROOT);
        this.filesToDelete = new HashSet<String>();
        this.indexWriter = writer;
        this.directoryOrig = directoryOrig;
        this.directory = new TrackingDirectoryWrapper(directory);
        this.fieldInfos = fieldInfos;
        this.indexWriterConfig = indexWriterConfig;
        this.infoStream = infoStream;
        this.codec = indexWriterConfig.getCodec();
        this.docState = new DocState(this, infoStream);
        this.docState.similarity = indexWriterConfig.getSimilarity();
        this.pendingNumDocs = pendingNumDocs;
        this.bytesUsed = Counter.newCounter();
        this.byteBlockAllocator = new ByteBlockPool.DirectTrackingAllocator(this.bytesUsed);
        this.pendingUpdates = new BufferedUpdates();
        this.intBlockAllocator = new IntBlockAllocator(this.bytesUsed);
        this.deleteQueue = deleteQueue;
        assert this.numDocsInRAM == 0 : "num docs " + this.numDocsInRAM;
        this.pendingUpdates.clear();
        this.deleteSlice = deleteQueue.newSlice();
        this.segmentInfo = new SegmentInfo(directoryOrig, Version.LATEST, segmentName, -1, false, this.codec, Collections.emptyMap(), StringHelper.randomId(), new HashMap<String, String>());
        assert this.numDocsInRAM == 0;
        this.consumer = indexWriterConfig.getIndexingChain().getChain(this);
        this.enableTestPoints = enableTestPoints;
    }
    
    public FieldInfos.Builder getFieldInfosBuilder() {
        return this.fieldInfos;
    }
    
    final void testPoint(final String message) {
        if (this.enableTestPoints) {
            assert this.infoStream.isEnabled("TP");
            this.infoStream.message("TP", message);
        }
    }
    
    private void reserveOneDoc() {
        if (this.pendingNumDocs.incrementAndGet() > IndexWriter.getActualMaxDocs()) {
            this.pendingNumDocs.decrementAndGet();
            throw new IllegalArgumentException("number of documents in the index cannot exceed " + IndexWriter.getActualMaxDocs());
        }
    }
    
    public void updateDocument(final Iterable<? extends IndexableField> doc, final Analyzer analyzer, final Term delTerm) throws IOException, AbortingException {
        this.testPoint("DocumentsWriterPerThread addDocument start");
        assert this.deleteQueue != null;
        this.reserveOneDoc();
        this.docState.doc = doc;
        this.docState.analyzer = analyzer;
        this.docState.docID = this.numDocsInRAM;
        boolean success = false;
        try {
            try {
                this.consumer.processDocument();
            }
            finally {
                this.docState.clear();
            }
            success = true;
        }
        finally {
            if (!success) {
                this.deleteDocID(this.docState.docID);
                ++this.numDocsInRAM;
            }
        }
        this.finishDocument(delTerm);
    }
    
    public int updateDocuments(final Iterable<? extends Iterable<? extends IndexableField>> docs, final Analyzer analyzer, final Term delTerm) throws IOException, AbortingException {
        this.testPoint("DocumentsWriterPerThread addDocuments start");
        assert this.deleteQueue != null;
        this.docState.analyzer = analyzer;
        int docCount = 0;
        boolean allDocsIndexed = false;
        try {
            for (final Iterable<? extends IndexableField> doc : docs) {
                this.reserveOneDoc();
                this.docState.doc = doc;
                this.docState.docID = this.numDocsInRAM;
                ++docCount;
                boolean success = false;
                try {
                    this.consumer.processDocument();
                    success = true;
                }
                finally {
                    if (!success) {
                        ++this.numDocsInRAM;
                    }
                }
                this.finishDocument(null);
            }
            allDocsIndexed = true;
            if (delTerm != null) {
                this.deleteQueue.add(delTerm, this.deleteSlice);
                assert this.deleteSlice.isTailItem(delTerm) : "expected the delete term as the tail item";
                this.deleteSlice.apply(this.pendingUpdates, this.numDocsInRAM - docCount);
            }
        }
        finally {
            if (!allDocsIndexed && !this.aborted) {
                for (int docID = this.numDocsInRAM - 1, endDocID = docID - docCount; docID > endDocID; --docID) {
                    this.deleteDocID(docID);
                }
            }
            this.docState.clear();
        }
        return docCount;
    }
    
    private void finishDocument(final Term delTerm) {
        boolean applySlice = this.numDocsInRAM != 0;
        if (delTerm != null) {
            this.deleteQueue.add(delTerm, this.deleteSlice);
            assert this.deleteSlice.isTailItem(delTerm) : "expected the delete term as the tail item";
        }
        else {
            applySlice &= this.deleteQueue.updateSlice(this.deleteSlice);
        }
        if (applySlice) {
            this.deleteSlice.apply(this.pendingUpdates, this.numDocsInRAM);
        }
        else {
            this.deleteSlice.reset();
        }
        ++this.numDocsInRAM;
    }
    
    void deleteDocID(final int docIDUpto) {
        this.pendingUpdates.addDocID(docIDUpto);
    }
    
    public int numDeleteTerms() {
        return this.pendingUpdates.numTermDeletes.get();
    }
    
    public int getNumDocsInRAM() {
        return this.numDocsInRAM;
    }
    
    FrozenBufferedUpdates prepareFlush() {
        assert this.numDocsInRAM > 0;
        final FrozenBufferedUpdates globalUpdates = this.deleteQueue.freezeGlobalBuffer(this.deleteSlice);
        if (this.deleteSlice != null) {
            this.deleteSlice.apply(this.pendingUpdates, this.numDocsInRAM);
            assert this.deleteSlice.isEmpty();
            this.deleteSlice.reset();
        }
        return globalUpdates;
    }
    
    FlushedSegment flush() throws IOException, AbortingException {
        assert this.numDocsInRAM > 0;
        assert this.deleteSlice.isEmpty() : "all deletes must be applied in prepareFlush";
        this.segmentInfo.setMaxDoc(this.numDocsInRAM);
        final SegmentWriteState flushState = new SegmentWriteState(this.infoStream, this.directory, this.segmentInfo, this.fieldInfos.finish(), this.pendingUpdates, new IOContext(new FlushInfo(this.numDocsInRAM, this.bytesUsed())));
        final double startMBUsed = this.bytesUsed() / 1024.0 / 1024.0;
        if (this.pendingUpdates.docIDs.size() > 0) {
            flushState.liveDocs = this.codec.liveDocsFormat().newLiveDocs(this.numDocsInRAM);
            for (final int delDocID : this.pendingUpdates.docIDs) {
                flushState.liveDocs.clear(delDocID);
            }
            flushState.delCountOnFlush = this.pendingUpdates.docIDs.size();
            this.pendingUpdates.bytesUsed.addAndGet(-this.pendingUpdates.docIDs.size() * BufferedUpdates.BYTES_PER_DEL_DOCID);
            this.pendingUpdates.docIDs.clear();
        }
        if (this.aborted) {
            if (this.infoStream.isEnabled("DWPT")) {
                this.infoStream.message("DWPT", "flush: skip because aborting is set");
            }
            return null;
        }
        if (this.infoStream.isEnabled("DWPT")) {
            this.infoStream.message("DWPT", "flush postings as segment " + flushState.segmentInfo.name + " numDocs=" + this.numDocsInRAM);
        }
        try {
            this.consumer.flush(flushState);
            this.pendingUpdates.terms.clear();
            this.segmentInfo.setFiles(new HashSet<String>(this.directory.getCreatedFiles()));
            final SegmentCommitInfo segmentInfoPerCommit = new SegmentCommitInfo(this.segmentInfo, 0, -1L, -1L, -1L);
            if (this.infoStream.isEnabled("DWPT")) {
                this.infoStream.message("DWPT", "new segment has " + ((flushState.liveDocs == null) ? 0 : flushState.delCountOnFlush) + " deleted docs");
                this.infoStream.message("DWPT", "new segment has " + (flushState.fieldInfos.hasVectors() ? "vectors" : "no vectors") + "; " + (flushState.fieldInfos.hasNorms() ? "norms" : "no norms") + "; " + (flushState.fieldInfos.hasDocValues() ? "docValues" : "no docValues") + "; " + (flushState.fieldInfos.hasProx() ? "prox" : "no prox") + "; " + (flushState.fieldInfos.hasFreq() ? "freqs" : "no freqs"));
                this.infoStream.message("DWPT", "flushedFiles=" + segmentInfoPerCommit.files());
                this.infoStream.message("DWPT", "flushed codec=" + this.codec);
            }
            BufferedUpdates segmentDeletes;
            if (this.pendingUpdates.queries.isEmpty() && this.pendingUpdates.numericUpdates.isEmpty() && this.pendingUpdates.binaryUpdates.isEmpty()) {
                this.pendingUpdates.clear();
                segmentDeletes = null;
            }
            else {
                segmentDeletes = this.pendingUpdates;
            }
            if (this.infoStream.isEnabled("DWPT")) {
                final double newSegmentSize = segmentInfoPerCommit.sizeInBytes() / 1024.0 / 1024.0;
                this.infoStream.message("DWPT", "flushed: segment=" + this.segmentInfo.name + " ramUsed=" + this.nf.format(startMBUsed) + " MB" + " newFlushedSize=" + this.nf.format(newSegmentSize) + " MB" + " docs/MB=" + this.nf.format(flushState.segmentInfo.maxDoc() / newSegmentSize));
            }
            assert this.segmentInfo != null;
            final FlushedSegment fs = new FlushedSegment(segmentInfoPerCommit, flushState.fieldInfos, segmentDeletes, flushState.liveDocs, flushState.delCountOnFlush);
            this.sealFlushedSegment(fs);
            return fs;
        }
        catch (final Throwable th) {
            this.abort();
            throw AbortingException.wrap(th);
        }
    }
    
    public Set<String> pendingFilesToDelete() {
        return this.filesToDelete;
    }
    
    void sealFlushedSegment(final FlushedSegment flushedSegment) throws IOException {
        assert flushedSegment != null;
        final SegmentCommitInfo newSegment = flushedSegment.segmentInfo;
        IndexWriter.setDiagnostics(newSegment.info, "flush");
        final IOContext context = new IOContext(new FlushInfo(newSegment.info.maxDoc(), newSegment.sizeInBytes()));
        boolean success = false;
        try {
            if (this.indexWriterConfig.getUseCompoundFile()) {
                final Set<String> originalFiles = newSegment.info.files();
                this.indexWriter.createCompoundFile(this.infoStream, new TrackingDirectoryWrapper(this.directory), newSegment.info, context);
                this.filesToDelete.addAll(originalFiles);
                newSegment.info.setUseCompoundFile(true);
            }
            this.codec.segmentInfoFormat().write(this.directory, newSegment.info, context);
            if (flushedSegment.liveDocs != null) {
                final int delCount = flushedSegment.delCount;
                assert delCount > 0;
                if (this.infoStream.isEnabled("DWPT")) {
                    this.infoStream.message("DWPT", "flush: write " + delCount + " deletes gen=" + flushedSegment.segmentInfo.getDelGen());
                }
                final SegmentCommitInfo info = flushedSegment.segmentInfo;
                final Codec codec = info.info.getCodec();
                codec.liveDocsFormat().writeLiveDocs(flushedSegment.liveDocs, this.directory, info, delCount, context);
                newSegment.setDelCount(delCount);
                newSegment.advanceDelGen();
            }
            success = true;
        }
        finally {
            if (!success && this.infoStream.isEnabled("DWPT")) {
                this.infoStream.message("DWPT", "hit exception creating compound file for newly flushed segment " + newSegment.info.name);
            }
        }
    }
    
    SegmentInfo getSegmentInfo() {
        return this.segmentInfo;
    }
    
    long bytesUsed() {
        return this.bytesUsed.get() + this.pendingUpdates.bytesUsed.get();
    }
    
    @Override
    public String toString() {
        return "DocumentsWriterPerThread [pendingDeletes=" + this.pendingUpdates + ", segment=" + ((this.segmentInfo != null) ? this.segmentInfo.name : "null") + ", aborted=" + this.aborted + ", numDocsInRAM=" + this.numDocsInRAM + ", deleteQueue=" + this.deleteQueue + "]";
    }
    
    static {
        defaultIndexingChain = new IndexingChain() {
            @Override
            DocConsumer getChain(final DocumentsWriterPerThread documentsWriterPerThread) throws IOException {
                return new DefaultIndexingChain(documentsWriterPerThread);
            }
        };
    }
    
    abstract static class IndexingChain
    {
        abstract DocConsumer getChain(final DocumentsWriterPerThread p0) throws IOException;
    }
    
    static class DocState
    {
        final DocumentsWriterPerThread docWriter;
        Analyzer analyzer;
        InfoStream infoStream;
        Similarity similarity;
        int docID;
        Iterable<? extends IndexableField> doc;
        
        DocState(final DocumentsWriterPerThread docWriter, final InfoStream infoStream) {
            this.docWriter = docWriter;
            this.infoStream = infoStream;
        }
        
        public void testPoint(final String name) {
            this.docWriter.testPoint(name);
        }
        
        public void clear() {
            this.doc = null;
            this.analyzer = null;
        }
    }
    
    static class FlushedSegment
    {
        final SegmentCommitInfo segmentInfo;
        final FieldInfos fieldInfos;
        final FrozenBufferedUpdates segmentUpdates;
        final MutableBits liveDocs;
        final int delCount;
        
        private FlushedSegment(final SegmentCommitInfo segmentInfo, final FieldInfos fieldInfos, final BufferedUpdates segmentUpdates, final MutableBits liveDocs, final int delCount) {
            this.segmentInfo = segmentInfo;
            this.fieldInfos = fieldInfos;
            this.segmentUpdates = ((segmentUpdates != null && segmentUpdates.any()) ? new FrozenBufferedUpdates(segmentUpdates, true) : null);
            this.liveDocs = liveDocs;
            this.delCount = delCount;
        }
    }
    
    private static class IntBlockAllocator extends IntBlockPool.Allocator
    {
        private final Counter bytesUsed;
        
        public IntBlockAllocator(final Counter bytesUsed) {
            super(8192);
            this.bytesUsed = bytesUsed;
        }
        
        @Override
        public int[] getIntBlock() {
            final int[] b = new int[8192];
            this.bytesUsed.addAndGet(32768L);
            return b;
        }
        
        @Override
        public void recycleIntBlocks(final int[][] blocks, final int offset, final int length) {
            this.bytesUsed.addAndGet(-(length * 32768));
        }
    }
}
