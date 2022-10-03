package org.apache.lucene.index;

import org.apache.lucene.store.RateLimiter;
import org.apache.lucene.store.RateLimitedIndexOutput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.FilterDirectory;
import java.io.FileNotFoundException;
import java.nio.file.NoSuchFileException;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.ThreadInterruptedException;
import java.util.Date;
import org.apache.lucene.util.Constants;
import org.apache.lucene.util.Bits;
import java.util.Locale;
import org.apache.lucene.util.StringHelper;
import org.apache.lucene.store.TrackingDirectoryWrapper;
import org.apache.lucene.store.MergeInfo;
import org.apache.lucene.store.FlushInfo;
import org.apache.lucene.document.Field;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.util.Version;
import java.util.Iterator;
import org.apache.lucene.codecs.FieldInfosFormat;
import org.apache.lucene.store.IOContext;
import java.util.Arrays;
import org.apache.lucene.store.LockValidatingDirectoryWrapper;
import org.apache.lucene.store.SleepingLockWrapper;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.lucene.store.AlreadyClosedException;
import java.util.Collections;
import org.apache.lucene.util.IOUtils;
import java.io.IOException;
import org.apache.lucene.util.InfoStream;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.util.CloseableThreadLocal;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Set;
import java.util.LinkedList;
import java.util.HashSet;
import org.apache.lucene.store.Lock;
import java.util.Map;
import java.util.Queue;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Accountable;
import java.io.Closeable;

public class IndexWriter implements Closeable, TwoPhaseCommit, Accountable
{
    public static final int MAX_DOCS = 2147483519;
    public static final int MAX_POSITION = 2147483519;
    private static int actualMaxDocs;
    boolean enableTestPoints;
    private static final int UNBOUNDED_MAX_MERGE_SEGMENTS = -1;
    public static final String WRITE_LOCK_NAME = "write.lock";
    public static final String SOURCE = "source";
    public static final String SOURCE_MERGE = "merge";
    public static final String SOURCE_FLUSH = "flush";
    public static final String SOURCE_ADDINDEXES_READERS = "addIndexes(CodecReader...)";
    public static final int MAX_TERM_LENGTH = 32766;
    volatile Throwable tragedy;
    private final Directory directoryOrig;
    private final Directory directory;
    private final Directory mergeDirectory;
    private final Analyzer analyzer;
    private final AtomicLong changeCount;
    private volatile long lastCommitChangeCount;
    private List<SegmentCommitInfo> rollbackSegments;
    volatile SegmentInfos pendingCommit;
    volatile long pendingCommitChangeCount;
    private Collection<String> filesToCommit;
    final SegmentInfos segmentInfos;
    final FieldInfos.FieldNumbers globalFieldNumberMap;
    private final DocumentsWriter docWriter;
    private final Queue<Event> eventQueue;
    final IndexFileDeleter deleter;
    private Map<SegmentCommitInfo, Boolean> segmentsToMerge;
    private int mergeMaxNumSegments;
    private Lock writeLock;
    private volatile boolean closed;
    private volatile boolean closing;
    private HashSet<SegmentCommitInfo> mergingSegments;
    private final MergeScheduler mergeScheduler;
    private LinkedList<MergePolicy.OneMerge> pendingMerges;
    private Set<MergePolicy.OneMerge> runningMerges;
    private List<MergePolicy.OneMerge> mergeExceptions;
    private long mergeGen;
    private boolean stopMerges;
    private boolean didMessageState;
    final AtomicInteger flushCount;
    final AtomicInteger flushDeletesCount;
    final ReaderPool readerPool;
    final BufferedUpdatesStream bufferedUpdatesStream;
    private volatile boolean poolReaders;
    private final LiveIndexWriterConfig config;
    private long startCommitTime;
    final AtomicLong pendingNumDocs;
    final CloseableThreadLocal<MergeRateLimiter> rateLimiters;
    final Codec codec;
    final InfoStream infoStream;
    private final Object commitLock;
    private final Object fullFlushLock;
    private boolean keepFullyDeletedSegments;
    
    static void setMaxDocs(final int maxDocs) {
        if (maxDocs > 2147483519) {
            throw new IllegalArgumentException("maxDocs must be <= IndexWriter.MAX_DOCS=2147483519; got: " + maxDocs);
        }
        IndexWriter.actualMaxDocs = maxDocs;
    }
    
    static int getActualMaxDocs() {
        return IndexWriter.actualMaxDocs;
    }
    
    DirectoryReader getReader() throws IOException {
        return this.getReader(true);
    }
    
    DirectoryReader getReader(final boolean applyAllDeletes) throws IOException {
        this.ensureOpen();
        final long tStart = System.currentTimeMillis();
        if (this.infoStream.isEnabled("IW")) {
            this.infoStream.message("IW", "flush at getReader");
        }
        this.poolReaders = true;
        DirectoryReader r = null;
        this.doBeforeFlush();
        boolean anyChanges = false;
        boolean success2 = false;
        try {
            boolean success3 = false;
            synchronized (this.fullFlushLock) {
                try {
                    anyChanges = this.docWriter.flushAllThreads();
                    if (!anyChanges) {
                        this.flushCount.incrementAndGet();
                    }
                    synchronized (this) {
                        anyChanges |= this.maybeApplyDeletes(applyAllDeletes);
                        r = StandardDirectoryReader.open(this, this.segmentInfos, applyAllDeletes);
                        if (this.infoStream.isEnabled("IW")) {
                            this.infoStream.message("IW", "return reader version=" + r.getVersion() + " reader=" + r);
                        }
                    }
                    success3 = true;
                }
                finally {
                    this.docWriter.finishFullFlush(this, success3);
                    if (success3) {
                        this.processEvents(false, true);
                        this.doAfterFlush();
                    }
                    else if (this.infoStream.isEnabled("IW")) {
                        this.infoStream.message("IW", "hit exception during NRT reader");
                    }
                }
            }
            if (anyChanges) {
                this.maybeMerge(this.config.getMergePolicy(), MergeTrigger.FULL_FLUSH, -1);
            }
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "getReader took " + (System.currentTimeMillis() - tStart) + " msec");
            }
            success2 = true;
        }
        catch (final AbortingException | VirtualMachineError tragedy) {
            this.tragicEvent(tragedy, "getReader");
            return null;
        }
        finally {
            if (!success2) {
                IOUtils.closeWhileHandlingException(r);
            }
        }
        return r;
    }
    
    @Override
    public final long ramBytesUsed() {
        this.ensureOpen();
        return this.docWriter.ramBytesUsed();
    }
    
    @Override
    public Collection<Accountable> getChildResources() {
        return (Collection<Accountable>)Collections.emptyList();
    }
    
    public int numDeletedDocs(final SegmentCommitInfo info) {
        this.ensureOpen(false);
        int delCount = info.getDelCount();
        final ReadersAndUpdates rld = this.readerPool.get(info, false);
        if (rld != null) {
            delCount += rld.getPendingDeleteCount();
        }
        return delCount;
    }
    
    protected final void ensureOpen(final boolean failIfClosing) throws AlreadyClosedException {
        if (this.closed || (failIfClosing && this.closing)) {
            throw new AlreadyClosedException("this IndexWriter is closed", this.tragedy);
        }
    }
    
    protected final void ensureOpen() throws AlreadyClosedException {
        this.ensureOpen(true);
    }
    
    public IndexWriter(final Directory d, final IndexWriterConfig conf) throws IOException {
        this.enableTestPoints = false;
        this.changeCount = new AtomicLong();
        this.segmentsToMerge = new HashMap<SegmentCommitInfo, Boolean>();
        this.mergingSegments = new HashSet<SegmentCommitInfo>();
        this.pendingMerges = new LinkedList<MergePolicy.OneMerge>();
        this.runningMerges = new HashSet<MergePolicy.OneMerge>();
        this.mergeExceptions = new ArrayList<MergePolicy.OneMerge>();
        this.flushCount = new AtomicInteger();
        this.flushDeletesCount = new AtomicInteger();
        this.readerPool = new ReaderPool();
        this.pendingNumDocs = new AtomicLong();
        this.rateLimiters = new CloseableThreadLocal<MergeRateLimiter>();
        this.commitLock = new Object();
        this.fullFlushLock = new Object();
        conf.setIndexWriter(this);
        this.config = conf;
        this.infoStream = this.config.getInfoStream();
        final long timeout = this.config.getWriteLockTimeout();
        Directory lockDir;
        if (timeout == 0L) {
            lockDir = d;
        }
        else {
            lockDir = new SleepingLockWrapper(d, timeout);
        }
        this.writeLock = lockDir.obtainLock("write.lock");
        boolean success = false;
        try {
            this.directoryOrig = d;
            this.directory = new LockValidatingDirectoryWrapper(d, this.writeLock);
            this.mergeDirectory = this.addMergeRateLimiters(this.directory);
            this.analyzer = this.config.getAnalyzer();
            (this.mergeScheduler = this.config.getMergeScheduler()).setInfoStream(this.infoStream);
            this.codec = this.config.getCodec();
            this.bufferedUpdatesStream = new BufferedUpdatesStream(this.infoStream);
            this.poolReaders = this.config.getReaderPooling();
            final IndexWriterConfig.OpenMode mode = this.config.getOpenMode();
            final boolean create = mode == IndexWriterConfig.OpenMode.CREATE || (mode != IndexWriterConfig.OpenMode.APPEND && !DirectoryReader.indexExists(this.directory));
            boolean initialIndexExists = true;
            final String[] files = this.directory.listAll();
            final IndexCommit commit = this.config.getIndexCommit();
            StandardDirectoryReader reader;
            if (commit == null) {
                reader = null;
            }
            else {
                reader = commit.getReader();
            }
            if (create) {
                if (this.config.getIndexCommit() != null) {
                    if (mode == IndexWriterConfig.OpenMode.CREATE) {
                        throw new IllegalArgumentException("cannot use IndexWriterConfig.setIndexCommit() with OpenMode.CREATE");
                    }
                    throw new IllegalArgumentException("cannot use IndexWriterConfig.setIndexCommit() when index has no commit");
                }
                else {
                    SegmentInfos sis = null;
                    try {
                        sis = SegmentInfos.readLatestCommit(this.directory);
                        sis.clear();
                    }
                    catch (final IOException e) {
                        initialIndexExists = false;
                        sis = new SegmentInfos();
                    }
                    this.segmentInfos = sis;
                    this.rollbackSegments = this.segmentInfos.createBackupSegmentInfos();
                    this.changed();
                }
            }
            else if (reader != null) {
                if (reader.directory() != commit.getDirectory()) {
                    throw new IllegalArgumentException("IndexCommit's reader must have the same directory as the IndexCommit");
                }
                if (reader.directory() != this.directoryOrig) {
                    throw new IllegalArgumentException("IndexCommit's reader must have the same directory passed to IndexWriter");
                }
                if (reader.segmentInfos.getLastGeneration() == 0L) {
                    throw new IllegalArgumentException("index must already have an initial commit to open from reader");
                }
                this.segmentInfos = reader.segmentInfos.clone();
                SegmentInfos lastCommit;
                try {
                    lastCommit = SegmentInfos.readCommit(this.directoryOrig, this.segmentInfos.getSegmentsFileName());
                }
                catch (final IOException ioe) {
                    throw new IllegalArgumentException("the provided reader is stale: its prior commit file \"" + this.segmentInfos.getSegmentsFileName() + "\" is missing from index");
                }
                if (reader.writer != null) {
                    assert reader.writer.closed;
                    this.segmentInfos.updateGenerationVersionAndCounter(reader.writer.segmentInfos);
                    lastCommit.updateGenerationVersionAndCounter(reader.writer.segmentInfos);
                }
                this.rollbackSegments = lastCommit.createBackupSegmentInfos();
                if (this.infoStream.isEnabled("IW")) {
                    this.infoStream.message("IW", "init from reader " + reader);
                    this.messageState();
                }
            }
            else {
                final String lastSegmentsFile = SegmentInfos.getLastCommitSegmentsFileName(files);
                if (lastSegmentsFile == null) {
                    throw new IndexNotFoundException("no segments* file found in " + this.directory + ": files: " + Arrays.toString(files));
                }
                this.segmentInfos = SegmentInfos.readCommit(this.directoryOrig, lastSegmentsFile);
                if (commit != null) {
                    if (commit.getDirectory() != this.directoryOrig) {
                        throw new IllegalArgumentException("IndexCommit's directory doesn't match my directory, expected=" + this.directoryOrig + ", got=" + commit.getDirectory());
                    }
                    final SegmentInfos oldInfos = SegmentInfos.readCommit(this.directoryOrig, commit.getSegmentsFileName());
                    this.segmentInfos.replace(oldInfos);
                    this.changed();
                    if (this.infoStream.isEnabled("IW")) {
                        this.infoStream.message("IW", "init: loaded commit \"" + commit.getSegmentsFileName() + "\"");
                    }
                }
                this.rollbackSegments = this.segmentInfos.createBackupSegmentInfos();
            }
            this.pendingNumDocs.set(this.segmentInfos.totalMaxDoc());
            this.globalFieldNumberMap = this.getFieldNumberMap();
            this.config.getFlushPolicy().init(this.config);
            this.docWriter = new DocumentsWriter(this, this.config, this.directoryOrig, this.directory);
            this.eventQueue = this.docWriter.eventQueue();
            synchronized (this) {
                this.deleter = new IndexFileDeleter(files, this.directoryOrig, this.directory, this.config.getIndexDeletionPolicy(), this.segmentInfos, this.infoStream, this, initialIndexExists, reader != null);
                assert create || this.filesExist(this.segmentInfos);
            }
            if (this.deleter.startingCommitDeleted) {
                this.changed();
            }
            if (reader != null) {
                final List<LeafReaderContext> leaves = reader.leaves();
                assert this.segmentInfos.size() == leaves.size();
                for (int i = 0; i < leaves.size(); ++i) {
                    final LeafReaderContext leaf = leaves.get(i);
                    final SegmentReader segReader = (SegmentReader)leaf.reader();
                    final SegmentReader newReader = new SegmentReader(this.segmentInfos.info(i), segReader, segReader.getLiveDocs(), segReader.numDocs());
                    this.readerPool.readerMap.put(newReader.getSegmentInfo(), new ReadersAndUpdates(this, newReader));
                }
                this.segmentInfos.changed();
                this.changed();
            }
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "init: create=" + create);
                this.messageState();
            }
            success = true;
        }
        finally {
            if (!success) {
                if (this.infoStream.isEnabled("IW")) {
                    this.infoStream.message("IW", "init: hit exception on init; releasing write lock");
                }
                IOUtils.closeWhileHandlingException(this.writeLock);
                this.writeLock = null;
            }
        }
    }
    
    static FieldInfos readFieldInfos(final SegmentCommitInfo si) throws IOException {
        final Codec codec = si.info.getCodec();
        final FieldInfosFormat reader = codec.fieldInfosFormat();
        if (si.hasFieldUpdates()) {
            final String segmentSuffix = Long.toString(si.getFieldInfosGen(), 36);
            return reader.read(si.info.dir, si.info, segmentSuffix, IOContext.READONCE);
        }
        if (si.info.getUseCompoundFile()) {
            try (final Directory cfs = codec.compoundFormat().getCompoundReader(si.info.dir, si.info, IOContext.DEFAULT)) {
                return reader.read(cfs, si.info, "", IOContext.READONCE);
            }
        }
        return reader.read(si.info.dir, si.info, "", IOContext.READONCE);
    }
    
    private FieldInfos.FieldNumbers getFieldNumberMap() throws IOException {
        final FieldInfos.FieldNumbers map = new FieldInfos.FieldNumbers();
        for (final SegmentCommitInfo info : this.segmentInfos) {
            final FieldInfos fis = readFieldInfos(info);
            for (final FieldInfo fi : fis) {
                map.addOrGet(fi.name, fi.number, fi.getDocValuesType());
            }
        }
        return map;
    }
    
    public LiveIndexWriterConfig getConfig() {
        this.ensureOpen(false);
        return this.config;
    }
    
    private void messageState() {
        if (this.infoStream.isEnabled("IW") && !this.didMessageState) {
            this.didMessageState = true;
            this.infoStream.message("IW", "\ndir=" + this.directoryOrig + "\n" + "index=" + this.segString() + "\n" + "version=" + Version.LATEST.toString() + "\n" + this.config.toString());
            this.infoStream.message("IW", "MMapDirectory.UNMAP_SUPPORTED=" + MMapDirectory.UNMAP_SUPPORTED);
        }
    }
    
    private void shutdown() throws IOException {
        if (this.pendingCommit != null) {
            throw new IllegalStateException("cannot close: prepareCommit was already called with no corresponding call to commit");
        }
        if (this.shouldClose(true)) {
            boolean success = false;
            try {
                if (this.infoStream.isEnabled("IW")) {
                    this.infoStream.message("IW", "now flush at close");
                }
                this.flush(true, true);
                this.waitForMerges();
                this.commitInternal(this.config.getMergePolicy());
                this.rollbackInternal();
                success = true;
            }
            finally {
                if (!success) {
                    try {
                        this.rollbackInternal();
                    }
                    catch (final Throwable t) {}
                }
            }
        }
    }
    
    @Override
    public void close() throws IOException {
        if (this.config.getCommitOnClose()) {
            this.shutdown();
        }
        else {
            this.rollback();
        }
    }
    
    private synchronized boolean shouldClose(final boolean waitForClose) {
        while (!this.closed) {
            if (!this.closing) {
                return this.closing = true;
            }
            if (!waitForClose) {
                return false;
            }
            this.doWait();
        }
        return false;
    }
    
    public Directory getDirectory() {
        return this.directoryOrig;
    }
    
    public Analyzer getAnalyzer() {
        this.ensureOpen();
        return this.analyzer;
    }
    
    public synchronized int maxDoc() {
        this.ensureOpen();
        return this.docWriter.getNumDocs() + this.segmentInfos.totalMaxDoc();
    }
    
    public synchronized int numDocs() {
        this.ensureOpen();
        int count = this.docWriter.getNumDocs();
        for (final SegmentCommitInfo info : this.segmentInfos) {
            count += info.info.maxDoc() - this.numDeletedDocs(info);
        }
        return count;
    }
    
    public synchronized boolean hasDeletions() {
        this.ensureOpen();
        if (this.bufferedUpdatesStream.any()) {
            return true;
        }
        if (this.docWriter.anyDeletions()) {
            return true;
        }
        if (this.readerPool.anyPendingDeletes()) {
            return true;
        }
        for (final SegmentCommitInfo info : this.segmentInfos) {
            if (info.hasDeletions()) {
                return true;
            }
        }
        return false;
    }
    
    public void addDocument(final Iterable<? extends IndexableField> doc) throws IOException {
        this.updateDocument(null, doc);
    }
    
    public void addDocuments(final Iterable<? extends Iterable<? extends IndexableField>> docs) throws IOException {
        this.updateDocuments(null, docs);
    }
    
    public void updateDocuments(final Term delTerm, final Iterable<? extends Iterable<? extends IndexableField>> docs) throws IOException {
        this.ensureOpen();
        try {
            boolean success = false;
            try {
                if (this.docWriter.updateDocuments(docs, this.analyzer, delTerm)) {
                    this.processEvents(true, false);
                }
                success = true;
            }
            finally {
                if (!success && this.infoStream.isEnabled("IW")) {
                    this.infoStream.message("IW", "hit exception updating document");
                }
            }
        }
        catch (final AbortingException | VirtualMachineError tragedy) {
            this.tragicEvent(tragedy, "updateDocuments");
        }
    }
    
    public synchronized boolean tryDeleteDocument(final IndexReader readerIn, int docID) throws IOException {
        LeafReader reader;
        if (readerIn instanceof LeafReader) {
            reader = (LeafReader)readerIn;
        }
        else {
            final List<LeafReaderContext> leaves = readerIn.leaves();
            final int subIndex = ReaderUtil.subIndex(docID, leaves);
            reader = leaves.get(subIndex).reader();
            docID -= leaves.get(subIndex).docBase;
            assert docID >= 0;
            assert docID < reader.maxDoc();
        }
        if (!(reader instanceof SegmentReader)) {
            throw new IllegalArgumentException("the reader must be a SegmentReader or composite reader containing only SegmentReaders");
        }
        final SegmentCommitInfo info = ((SegmentReader)reader).getSegmentInfo();
        if (this.segmentInfos.indexOf(info) != -1) {
            final ReadersAndUpdates rld = this.readerPool.get(info, false);
            if (rld != null) {
                synchronized (this.bufferedUpdatesStream) {
                    rld.initWritableLiveDocs();
                    if (rld.delete(docID)) {
                        final int fullDelCount = rld.info.getDelCount() + rld.getPendingDeleteCount();
                        if (fullDelCount == rld.info.info.maxDoc() && !this.mergingSegments.contains(rld.info)) {
                            this.segmentInfos.remove(rld.info);
                            this.readerPool.drop(rld.info);
                            this.checkpoint();
                        }
                        this.changed();
                    }
                    return true;
                }
            }
        }
        return false;
    }
    
    public void deleteDocuments(final Term... terms) throws IOException {
        this.ensureOpen();
        try {
            if (this.docWriter.deleteTerms(terms)) {
                this.processEvents(true, false);
            }
        }
        catch (final VirtualMachineError tragedy) {
            this.tragicEvent(tragedy, "deleteDocuments(Term..)");
        }
    }
    
    public void deleteDocuments(final Query... queries) throws IOException {
        this.ensureOpen();
        for (final Query query : queries) {
            if (query.getClass() == MatchAllDocsQuery.class) {
                this.deleteAll();
                return;
            }
        }
        try {
            if (this.docWriter.deleteQueries(queries)) {
                this.processEvents(true, false);
            }
        }
        catch (final VirtualMachineError tragedy) {
            this.tragicEvent(tragedy, "deleteDocuments(Query..)");
        }
    }
    
    public void updateDocument(final Term term, final Iterable<? extends IndexableField> doc) throws IOException {
        this.ensureOpen();
        try {
            boolean success = false;
            try {
                if (this.docWriter.updateDocument(doc, this.analyzer, term)) {
                    this.processEvents(true, false);
                }
                success = true;
            }
            finally {
                if (!success && this.infoStream.isEnabled("IW")) {
                    this.infoStream.message("IW", "hit exception updating document");
                }
            }
        }
        catch (final AbortingException | VirtualMachineError tragedy) {
            this.tragicEvent(tragedy, "updateDocument");
        }
    }
    
    public void updateNumericDocValue(final Term term, final String field, final long value) throws IOException {
        this.ensureOpen();
        if (!this.globalFieldNumberMap.contains(field, DocValuesType.NUMERIC)) {
            throw new IllegalArgumentException("can only update existing numeric-docvalues fields!");
        }
        try {
            if (this.docWriter.updateDocValues(new DocValuesUpdate.NumericDocValuesUpdate(term, field, value))) {
                this.processEvents(true, false);
            }
        }
        catch (final VirtualMachineError tragedy) {
            this.tragicEvent(tragedy, "updateNumericDocValue");
        }
    }
    
    public void updateBinaryDocValue(final Term term, final String field, final BytesRef value) throws IOException {
        this.ensureOpen();
        if (value == null) {
            throw new IllegalArgumentException("cannot update a field to a null value: " + field);
        }
        if (!this.globalFieldNumberMap.contains(field, DocValuesType.BINARY)) {
            throw new IllegalArgumentException("can only update existing binary-docvalues fields!");
        }
        try {
            if (this.docWriter.updateDocValues(new DocValuesUpdate.BinaryDocValuesUpdate(term, field, value))) {
                this.processEvents(true, false);
            }
        }
        catch (final VirtualMachineError tragedy) {
            this.tragicEvent(tragedy, "updateBinaryDocValue");
        }
    }
    
    public void updateDocValues(final Term term, final Field... updates) throws IOException {
        this.ensureOpen();
        final DocValuesUpdate[] dvUpdates = new DocValuesUpdate[updates.length];
        for (int i = 0; i < updates.length; ++i) {
            final Field f = updates[i];
            final DocValuesType dvType = f.fieldType().docValuesType();
            if (dvType == null) {
                throw new NullPointerException("DocValuesType cannot be null (field: \"" + f.name() + "\")");
            }
            if (dvType == DocValuesType.NONE) {
                throw new IllegalArgumentException("can only update NUMERIC or BINARY fields! field=" + f.name());
            }
            if (!this.globalFieldNumberMap.contains(f.name(), dvType)) {
                throw new IllegalArgumentException("can only update existing docvalues fields! field=" + f.name() + ", type=" + dvType);
            }
            switch (dvType) {
                case NUMERIC: {
                    dvUpdates[i] = new DocValuesUpdate.NumericDocValuesUpdate(term, f.name(), (Long)f.numericValue());
                    break;
                }
                case BINARY: {
                    dvUpdates[i] = new DocValuesUpdate.BinaryDocValuesUpdate(term, f.name(), f.binaryValue());
                    break;
                }
                default: {
                    throw new IllegalArgumentException("can only update NUMERIC or BINARY fields: field=" + f.name() + ", type=" + dvType);
                }
            }
        }
        try {
            if (this.docWriter.updateDocValues(dvUpdates)) {
                this.processEvents(true, false);
            }
        }
        catch (final VirtualMachineError tragedy) {
            this.tragicEvent(tragedy, "updateDocValues");
        }
    }
    
    final synchronized int getSegmentCount() {
        return this.segmentInfos.size();
    }
    
    final synchronized int getNumBufferedDocuments() {
        return this.docWriter.getNumDocs();
    }
    
    final synchronized Collection<String> getIndexFileNames() throws IOException {
        return this.segmentInfos.files(true);
    }
    
    final synchronized int maxDoc(final int i) {
        if (i >= 0 && i < this.segmentInfos.size()) {
            return this.segmentInfos.info(i).info.maxDoc();
        }
        return -1;
    }
    
    final int getFlushCount() {
        return this.flushCount.get();
    }
    
    final int getFlushDeletesCount() {
        return this.flushDeletesCount.get();
    }
    
    final String newSegmentName() {
        synchronized (this.segmentInfos) {
            this.changeCount.incrementAndGet();
            this.segmentInfos.changed();
            return "_" + Integer.toString(this.segmentInfos.counter++, 36);
        }
    }
    
    public void forceMerge(final int maxNumSegments) throws IOException {
        this.forceMerge(maxNumSegments, true);
    }
    
    public void forceMerge(final int maxNumSegments, final boolean doWait) throws IOException {
        this.ensureOpen();
        if (maxNumSegments < 1) {
            throw new IllegalArgumentException("maxNumSegments must be >= 1; got " + maxNumSegments);
        }
        if (this.infoStream.isEnabled("IW")) {
            this.infoStream.message("IW", "forceMerge: index now " + this.segString());
            this.infoStream.message("IW", "now flush at forceMerge");
        }
        this.flush(true, true);
        synchronized (this) {
            this.resetMergeExceptions();
            this.segmentsToMerge.clear();
            for (final SegmentCommitInfo info : this.segmentInfos) {
                this.segmentsToMerge.put(info, Boolean.TRUE);
            }
            this.mergeMaxNumSegments = maxNumSegments;
            for (final MergePolicy.OneMerge merge : this.pendingMerges) {
                merge.maxNumSegments = maxNumSegments;
                this.segmentsToMerge.put(merge.info, Boolean.TRUE);
            }
            for (final MergePolicy.OneMerge merge : this.runningMerges) {
                merge.maxNumSegments = maxNumSegments;
                this.segmentsToMerge.put(merge.info, Boolean.TRUE);
            }
        }
        this.maybeMerge(this.config.getMergePolicy(), MergeTrigger.EXPLICIT, maxNumSegments);
        if (doWait) {
            Label_0472: {
                synchronized (this) {
                    while (this.tragedy == null) {
                        if (this.mergeExceptions.size() > 0) {
                            for (int size = this.mergeExceptions.size(), i = 0; i < size; ++i) {
                                final MergePolicy.OneMerge merge2 = this.mergeExceptions.get(i);
                                if (merge2.maxNumSegments != -1) {
                                    throw new IOException("background merge hit exception: " + merge2.segString(), merge2.getException());
                                }
                            }
                        }
                        if (!this.maxNumSegmentsMergesPending()) {
                            break Label_0472;
                        }
                        this.doWait();
                    }
                    throw new IllegalStateException("this writer hit an unrecoverable error; cannot complete forceMerge", this.tragedy);
                }
            }
            this.ensureOpen();
        }
    }
    
    private synchronized boolean maxNumSegmentsMergesPending() {
        for (final MergePolicy.OneMerge merge : this.pendingMerges) {
            if (merge.maxNumSegments != -1) {
                return true;
            }
        }
        for (final MergePolicy.OneMerge merge : this.runningMerges) {
            if (merge.maxNumSegments != -1) {
                return true;
            }
        }
        return false;
    }
    
    public void forceMergeDeletes(final boolean doWait) throws IOException {
        this.ensureOpen();
        this.flush(true, true);
        if (this.infoStream.isEnabled("IW")) {
            this.infoStream.message("IW", "forceMergeDeletes: index now " + this.segString());
        }
        final MergePolicy mergePolicy = this.config.getMergePolicy();
        boolean newMergesFound = false;
        final MergePolicy.MergeSpecification spec;
        synchronized (this) {
            spec = mergePolicy.findForcedDeletesMerges(this.segmentInfos, this);
            newMergesFound = (spec != null);
            if (newMergesFound) {
                for (int numMerges = spec.merges.size(), i = 0; i < numMerges; ++i) {
                    this.registerMerge(spec.merges.get(i));
                }
            }
        }
        this.mergeScheduler.merge(this, MergeTrigger.EXPLICIT, newMergesFound);
        if (spec != null && doWait) {
            final int numMerges2 = spec.merges.size();
            synchronized (this) {
                boolean running = true;
                while (running) {
                    if (this.tragedy != null) {
                        throw new IllegalStateException("this writer hit an unrecoverable error; cannot complete forceMergeDeletes", this.tragedy);
                    }
                    running = false;
                    for (int j = 0; j < numMerges2; ++j) {
                        final MergePolicy.OneMerge merge = spec.merges.get(j);
                        if (this.pendingMerges.contains(merge) || this.runningMerges.contains(merge)) {
                            running = true;
                        }
                        final Throwable t = merge.getException();
                        if (t != null) {
                            throw new IOException("background merge hit exception: " + merge.segString(), t);
                        }
                    }
                    if (!running) {
                        continue;
                    }
                    this.doWait();
                }
            }
        }
    }
    
    public void forceMergeDeletes() throws IOException {
        this.forceMergeDeletes(true);
    }
    
    public final void maybeMerge() throws IOException {
        this.maybeMerge(this.config.getMergePolicy(), MergeTrigger.EXPLICIT, -1);
    }
    
    private final void maybeMerge(final MergePolicy mergePolicy, final MergeTrigger trigger, final int maxNumSegments) throws IOException {
        this.ensureOpen(false);
        final boolean newMergesFound = this.updatePendingMerges(mergePolicy, trigger, maxNumSegments);
        this.mergeScheduler.merge(this, trigger, newMergesFound);
    }
    
    private synchronized boolean updatePendingMerges(final MergePolicy mergePolicy, final MergeTrigger trigger, final int maxNumSegments) throws IOException {
        this.messageState();
        assert maxNumSegments > 0;
        assert trigger != null;
        if (this.stopMerges) {
            return false;
        }
        if (this.tragedy != null) {
            return false;
        }
        boolean newMergesFound = false;
        MergePolicy.MergeSpecification spec;
        if (maxNumSegments != -1) {
            assert trigger == MergeTrigger.MERGE_FINISHED : "Expected EXPLICT or MERGE_FINISHED as trigger even with maxNumSegments set but was: " + trigger.name();
            spec = mergePolicy.findForcedMerges(this.segmentInfos, maxNumSegments, Collections.unmodifiableMap((Map<? extends SegmentCommitInfo, ? extends Boolean>)this.segmentsToMerge), this);
            newMergesFound = (spec != null);
            if (newMergesFound) {
                for (int numMerges = spec.merges.size(), i = 0; i < numMerges; ++i) {
                    final MergePolicy.OneMerge merge = spec.merges.get(i);
                    merge.maxNumSegments = maxNumSegments;
                }
            }
        }
        else {
            spec = mergePolicy.findMerges(trigger, this.segmentInfos, this);
        }
        newMergesFound = (spec != null);
        if (newMergesFound) {
            for (int numMerges = spec.merges.size(), i = 0; i < numMerges; ++i) {
                this.registerMerge(spec.merges.get(i));
            }
        }
        return newMergesFound;
    }
    
    public synchronized Collection<SegmentCommitInfo> getMergingSegments() {
        return this.mergingSegments;
    }
    
    public synchronized MergePolicy.OneMerge getNextMerge() {
        if (this.pendingMerges.size() == 0) {
            return null;
        }
        final MergePolicy.OneMerge merge = this.pendingMerges.removeFirst();
        this.runningMerges.add(merge);
        return merge;
    }
    
    public synchronized boolean hasPendingMerges() {
        return this.pendingMerges.size() != 0;
    }
    
    @Override
    public void rollback() throws IOException {
        if (this.shouldClose(true)) {
            this.rollbackInternal();
        }
    }
    
    private void rollbackInternal() throws IOException {
        synchronized (this.commitLock) {
            this.rollbackInternalNoCommit();
        }
    }
    
    private void rollbackInternalNoCommit() throws IOException {
        boolean success = false;
        if (this.infoStream.isEnabled("IW")) {
            this.infoStream.message("IW", "rollback");
        }
        try {
            this.abortMerges();
            this.rateLimiters.close();
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "rollback: done finish merges");
            }
            this.mergeScheduler.close();
            this.bufferedUpdatesStream.clear();
            this.docWriter.close();
            this.docWriter.abort(this);
            synchronized (this) {
                if (this.pendingCommit != null) {
                    this.pendingCommit.rollbackCommit(this.directory);
                    try {
                        this.deleter.decRef(this.pendingCommit);
                    }
                    finally {
                        this.pendingCommit = null;
                        this.notifyAll();
                    }
                }
                this.readerPool.dropAll(false);
                this.segmentInfos.rollbackSegmentInfos(this.rollbackSegments);
                if (this.infoStream.isEnabled("IW")) {
                    this.infoStream.message("IW", "rollback: infos=" + this.segString(this.segmentInfos));
                }
                this.testPoint("rollback before checkpoint");
                if (this.tragedy == null) {
                    this.deleter.checkpoint(this.segmentInfos, false);
                    this.deleter.refresh();
                    this.deleter.close();
                }
                this.lastCommitChangeCount = this.changeCount.get();
                this.closed = true;
                IOUtils.close(this.writeLock);
                this.writeLock = null;
            }
            success = true;
        }
        catch (final VirtualMachineError tragedy) {
            this.tragicEvent(tragedy, "rollbackInternal");
            if (!success) {
                IOUtils.closeWhileHandlingException(this.mergeScheduler);
            }
            synchronized (this) {
                if (!success) {
                    if (this.pendingCommit != null) {
                        try {
                            this.pendingCommit.rollbackCommit(this.directory);
                            this.deleter.decRef(this.pendingCommit);
                        }
                        catch (final Throwable t) {}
                        this.pendingCommit = null;
                    }
                    IOUtils.closeWhileHandlingException(this.readerPool, this.deleter, this.writeLock);
                    this.writeLock = null;
                }
                this.closed = true;
                this.closing = false;
                this.notifyAll();
            }
        }
        finally {
            if (!success) {
                IOUtils.closeWhileHandlingException(this.mergeScheduler);
            }
            synchronized (this) {
                if (!success) {
                    if (this.pendingCommit != null) {
                        try {
                            this.pendingCommit.rollbackCommit(this.directory);
                            this.deleter.decRef(this.pendingCommit);
                        }
                        catch (final Throwable t2) {}
                        this.pendingCommit = null;
                    }
                    IOUtils.closeWhileHandlingException(this.readerPool, this.deleter, this.writeLock);
                    this.writeLock = null;
                }
                this.closed = true;
                this.closing = false;
                this.notifyAll();
            }
        }
    }
    
    public void deleteAll() throws IOException {
        this.ensureOpen();
        boolean success = false;
        try {
            synchronized (this.fullFlushLock) {
                final long abortedDocCount = this.docWriter.lockAndAbortAll(this);
                this.pendingNumDocs.addAndGet(-abortedDocCount);
                this.processEvents(false, true);
                synchronized (this) {
                    try {
                        this.abortMerges();
                        this.stopMerges = false;
                        this.pendingNumDocs.addAndGet(-this.segmentInfos.totalMaxDoc());
                        this.segmentInfos.clear();
                        this.deleter.checkpoint(this.segmentInfos, false);
                        this.readerPool.dropAll(false);
                        this.changeCount.incrementAndGet();
                        this.segmentInfos.changed();
                        this.globalFieldNumberMap.clear();
                        success = true;
                    }
                    finally {
                        this.docWriter.unlockAllAfterAbortAll(this);
                        if (!success && this.infoStream.isEnabled("IW")) {
                            this.infoStream.message("IW", "hit exception during deleteAll");
                        }
                    }
                }
            }
        }
        catch (final VirtualMachineError tragedy) {
            this.tragicEvent(tragedy, "deleteAll");
        }
    }
    
    private synchronized void abortMerges() {
        this.stopMerges = true;
        for (final MergePolicy.OneMerge merge : this.pendingMerges) {
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "now abort pending merge " + this.segString(merge.segments));
            }
            merge.rateLimiter.setAbort();
            this.mergeFinish(merge);
        }
        this.pendingMerges.clear();
        for (final MergePolicy.OneMerge merge : this.runningMerges) {
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "now abort running merge " + this.segString(merge.segments));
            }
            merge.rateLimiter.setAbort();
        }
        while (this.runningMerges.size() != 0) {
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "now wait for " + this.runningMerges.size() + " running merge/s to abort");
            }
            this.doWait();
        }
        this.notifyAll();
        assert 0 == this.mergingSegments.size();
        if (this.infoStream.isEnabled("IW")) {
            this.infoStream.message("IW", "all running merges have aborted");
        }
    }
    
    void waitForMerges() throws IOException {
        this.mergeScheduler.merge(this, MergeTrigger.CLOSING, false);
        synchronized (this) {
            this.ensureOpen(false);
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "waitForMerges");
            }
            while (this.pendingMerges.size() > 0 || this.runningMerges.size() > 0) {
                this.doWait();
            }
            assert 0 == this.mergingSegments.size();
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "waitForMerges done");
            }
        }
    }
    
    synchronized void checkpoint() throws IOException {
        this.changed();
        this.deleter.checkpoint(this.segmentInfos, false);
    }
    
    synchronized void checkpointNoSIS() throws IOException {
        this.changeCount.incrementAndGet();
        this.deleter.checkpoint(this.segmentInfos, false);
    }
    
    synchronized void changed() {
        this.changeCount.incrementAndGet();
        this.segmentInfos.changed();
    }
    
    synchronized void publishFrozenUpdates(final FrozenBufferedUpdates packet) {
        assert packet != null && packet.any();
        synchronized (this.bufferedUpdatesStream) {
            this.bufferedUpdatesStream.push(packet);
        }
    }
    
    void publishFlushedSegment(final SegmentCommitInfo newSegment, final FrozenBufferedUpdates packet, final FrozenBufferedUpdates globalPacket) throws IOException {
        try {
            synchronized (this) {
                this.ensureOpen(false);
                synchronized (this.bufferedUpdatesStream) {
                    if (this.infoStream.isEnabled("IW")) {
                        this.infoStream.message("IW", "publishFlushedSegment");
                    }
                    if (globalPacket != null && globalPacket.any()) {
                        this.bufferedUpdatesStream.push(globalPacket);
                    }
                    long nextGen;
                    if (packet != null && packet.any()) {
                        nextGen = this.bufferedUpdatesStream.push(packet);
                    }
                    else {
                        nextGen = this.bufferedUpdatesStream.getNextGen();
                    }
                    if (this.infoStream.isEnabled("IW")) {
                        this.infoStream.message("IW", "publish sets newSegment delGen=" + nextGen + " seg=" + this.segString(newSegment));
                    }
                    newSegment.setBufferedDeletesGen(nextGen);
                    this.segmentInfos.add(newSegment);
                    this.checkpoint();
                }
            }
        }
        finally {
            this.flushCount.incrementAndGet();
            this.doAfterFlush();
        }
    }
    
    private synchronized void resetMergeExceptions() {
        this.mergeExceptions = new ArrayList<MergePolicy.OneMerge>();
        ++this.mergeGen;
    }
    
    private void noDupDirs(final Directory... dirs) {
        final HashSet<Directory> dups = new HashSet<Directory>();
        for (int i = 0; i < dirs.length; ++i) {
            if (dups.contains(dirs[i])) {
                throw new IllegalArgumentException("Directory " + dirs[i] + " appears more than once");
            }
            if (dirs[i] == this.directoryOrig) {
                throw new IllegalArgumentException("Cannot add directory to itself");
            }
            dups.add(dirs[i]);
        }
    }
    
    private List<Lock> acquireWriteLocks(final Directory... dirs) throws IOException {
        final List<Lock> locks = new ArrayList<Lock>(dirs.length);
        for (int i = 0; i < dirs.length; ++i) {
            boolean success = false;
            try {
                final Lock lock = dirs[i].obtainLock("write.lock");
                locks.add(lock);
                success = true;
            }
            finally {
                if (!success) {
                    IOUtils.closeWhileHandlingException(locks);
                }
            }
        }
        return locks;
    }
    
    public void addIndexes(final Directory... dirs) throws IOException {
        this.ensureOpen();
        this.noDupDirs(dirs);
        final List<Lock> locks = this.acquireWriteLocks(dirs);
        boolean successTop = false;
        try {
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "flush at addIndexes(Directory...)");
            }
            this.flush(false, true);
            final List<SegmentCommitInfo> infos = new ArrayList<SegmentCommitInfo>();
            long totalMaxDoc = 0L;
            final List<SegmentInfos> commits = new ArrayList<SegmentInfos>(dirs.length);
            for (final Directory dir : dirs) {
                if (this.infoStream.isEnabled("IW")) {
                    this.infoStream.message("IW", "addIndexes: process directory " + dir);
                }
                final SegmentInfos sis = SegmentInfos.readLatestCommit(dir);
                totalMaxDoc += sis.totalMaxDoc();
                commits.add(sis);
            }
            this.testReserveDocs(totalMaxDoc);
            boolean success = false;
            try {
                for (final SegmentInfos sis2 : commits) {
                    for (final SegmentCommitInfo info : sis2) {
                        assert !infos.contains(info) : "dup info dir=" + info.info.dir + " name=" + info.info.name;
                        final String newSegName = this.newSegmentName();
                        if (this.infoStream.isEnabled("IW")) {
                            this.infoStream.message("IW", "addIndexes: process segment origName=" + info.info.name + " newName=" + newSegName + " info=" + info);
                        }
                        final IOContext context = new IOContext(new FlushInfo(info.info.maxDoc(), info.sizeInBytes()));
                        final FieldInfos fis = readFieldInfos(info);
                        for (final FieldInfo fi : fis) {
                            this.globalFieldNumberMap.addOrGet(fi.name, fi.number, fi.getDocValuesType());
                        }
                        infos.add(this.copySegmentAsIs(info, newSegName, context));
                    }
                }
                success = true;
            }
            finally {
                if (!success) {
                    for (final SegmentCommitInfo sipc : infos) {
                        this.deleteNewFiles(sipc.files());
                    }
                }
            }
            synchronized (this) {
                success = false;
                try {
                    this.ensureOpen();
                    this.reserveDocs(totalMaxDoc);
                    success = true;
                }
                finally {
                    if (!success) {
                        for (final SegmentCommitInfo sipc2 : infos) {
                            this.deleteNewFiles(sipc2.files());
                        }
                    }
                }
                this.segmentInfos.addAll(infos);
                this.checkpoint();
            }
            successTop = true;
        }
        catch (final VirtualMachineError tragedy) {
            this.tragicEvent(tragedy, "addIndexes(Directory...)");
        }
        finally {
            if (successTop) {
                IOUtils.close(locks);
            }
            else {
                IOUtils.closeWhileHandlingException(locks);
            }
        }
        this.maybeMerge();
    }
    
    public void addIndexes(final CodecReader... readers) throws IOException {
        this.ensureOpen();
        long numDocs = 0L;
        try {
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "flush at addIndexes(CodecReader...)");
            }
            this.flush(false, true);
            final String mergedName = this.newSegmentName();
            for (final CodecReader leaf : readers) {
                numDocs += leaf.numDocs();
            }
            this.testReserveDocs(numDocs);
            final IOContext context = new IOContext(new MergeInfo((int)numDocs, -1L, false, -1));
            final TrackingDirectoryWrapper trackingDir = new TrackingDirectoryWrapper(this.directory);
            final SegmentInfo info = new SegmentInfo(this.directoryOrig, Version.LATEST, mergedName, -1, false, this.codec, Collections.emptyMap(), StringHelper.randomId(), new HashMap<String, String>());
            final SegmentMerger merger = new SegmentMerger(Arrays.asList(readers), info, this.infoStream, trackingDir, this.globalFieldNumberMap, context);
            this.rateLimiters.set(new MergeRateLimiter(null));
            if (!merger.shouldMerge()) {
                return;
            }
            merger.merge();
            final SegmentCommitInfo infoPerCommit = new SegmentCommitInfo(info, 0, -1L, -1L, -1L);
            info.setFiles(new HashSet<String>(trackingDir.getCreatedFiles()));
            trackingDir.getCreatedFiles().clear();
            setDiagnostics(info, "addIndexes(CodecReader...)");
            final MergePolicy mergePolicy = this.config.getMergePolicy();
            final boolean useCompoundFile;
            synchronized (this) {
                if (this.stopMerges) {
                    this.deleteNewFiles(infoPerCommit.files());
                    return;
                }
                this.ensureOpen();
                useCompoundFile = mergePolicy.useCompoundFile(this.segmentInfos, infoPerCommit, this);
            }
            if (useCompoundFile) {
                final Collection<String> filesToDelete = infoPerCommit.files();
                final TrackingDirectoryWrapper trackingCFSDir = new TrackingDirectoryWrapper(this.mergeDirectory);
                try {
                    this.createCompoundFile(this.infoStream, trackingCFSDir, info, context);
                }
                finally {
                    this.deleteNewFiles(filesToDelete);
                }
                info.setUseCompoundFile(true);
            }
            this.codec.segmentInfoFormat().write(trackingDir, info, context);
            info.addFiles(trackingDir.getCreatedFiles());
            synchronized (this) {
                if (this.stopMerges) {
                    this.deleteNewFiles(infoPerCommit.files());
                    return;
                }
                this.ensureOpen();
                this.reserveDocs(numDocs);
                this.segmentInfos.add(infoPerCommit);
                this.checkpoint();
            }
        }
        catch (final VirtualMachineError tragedy) {
            this.tragicEvent(tragedy, "addIndexes(CodecReader...)");
        }
        this.maybeMerge();
    }
    
    private SegmentCommitInfo copySegmentAsIs(final SegmentCommitInfo info, final String segName, final IOContext context) throws IOException {
        final SegmentInfo newInfo = new SegmentInfo(this.directoryOrig, info.info.getVersion(), segName, info.info.maxDoc(), info.info.getUseCompoundFile(), info.info.getCodec(), info.info.getDiagnostics(), info.info.getId(), info.info.getAttributes());
        final SegmentCommitInfo newInfoPerCommit = new SegmentCommitInfo(newInfo, info.getDelCount(), info.getDelGen(), info.getFieldInfosGen(), info.getDocValuesGen());
        newInfo.setFiles(info.files());
        boolean success = false;
        final Set<String> copiedFiles = new HashSet<String>();
        try {
            for (final String file : info.files()) {
                final String newFileName = newInfo.namedForThisSegment(file);
                assert !slowFileExists(this.directory, newFileName) : "file \"" + newFileName + "\" already exists; newInfo.files=" + newInfo.files();
                this.directory.copyFrom(info.info.dir, file, newFileName, context);
                copiedFiles.add(newFileName);
            }
            success = true;
        }
        finally {
            if (!success) {
                this.deleteNewFiles(copiedFiles);
            }
        }
        assert copiedFiles.equals(newInfoPerCommit.files());
        return newInfoPerCommit;
    }
    
    protected void doAfterFlush() throws IOException {
    }
    
    protected void doBeforeFlush() throws IOException {
    }
    
    @Override
    public final void prepareCommit() throws IOException {
        this.ensureOpen();
        this.prepareCommitInternal(this.config.getMergePolicy());
    }
    
    private void prepareCommitInternal(final MergePolicy mergePolicy) throws IOException {
        this.startCommitTime = System.nanoTime();
        synchronized (this.commitLock) {
            this.ensureOpen(false);
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "prepareCommit: flush");
                this.infoStream.message("IW", "  index before flush " + this.segString());
            }
            if (this.tragedy != null) {
                throw new IllegalStateException("this writer hit an unrecoverable error; cannot commit", this.tragedy);
            }
            if (this.pendingCommit != null) {
                throw new IllegalStateException("prepareCommit was already called with no corresponding call to commit");
            }
            this.doBeforeFlush();
            this.testPoint("startDoFlush");
            SegmentInfos toCommit = null;
            boolean anySegmentsFlushed = false;
            try {
                synchronized (this.fullFlushLock) {
                    boolean flushSuccess = false;
                    boolean success = false;
                    try {
                        anySegmentsFlushed = this.docWriter.flushAllThreads();
                        if (!anySegmentsFlushed) {
                            this.flushCount.incrementAndGet();
                        }
                        this.processEvents(false, true);
                        flushSuccess = true;
                        synchronized (this) {
                            this.maybeApplyDeletes(true);
                            this.readerPool.commit(this.segmentInfos);
                            if (this.changeCount.get() != this.lastCommitChangeCount) {
                                this.changeCount.incrementAndGet();
                                this.segmentInfos.changed();
                            }
                            toCommit = this.segmentInfos.clone();
                            this.pendingCommitChangeCount = this.changeCount.get();
                            this.filesToCommit = toCommit.files(false);
                            this.deleter.incRef(this.filesToCommit);
                        }
                        success = true;
                    }
                    finally {
                        if (!success && this.infoStream.isEnabled("IW")) {
                            this.infoStream.message("IW", "hit exception during prepareCommit");
                        }
                        this.docWriter.finishFullFlush(this, flushSuccess);
                        this.doAfterFlush();
                    }
                }
            }
            catch (final AbortingException | VirtualMachineError tragedy) {
                this.tragicEvent(tragedy, "prepareCommit");
            }
            boolean success2 = false;
            try {
                if (anySegmentsFlushed) {
                    this.maybeMerge(mergePolicy, MergeTrigger.FULL_FLUSH, -1);
                }
                this.startCommit(toCommit);
                success2 = true;
            }
            finally {
                if (!success2) {
                    synchronized (this) {
                        if (this.filesToCommit != null) {
                            this.deleter.decRefWhileHandlingException(this.filesToCommit);
                            this.filesToCommit = null;
                        }
                    }
                }
            }
        }
    }
    
    public final synchronized void setCommitData(final Map<String, String> commitUserData) {
        this.segmentInfos.setUserData(new HashMap<String, String>(commitUserData));
        this.changeCount.incrementAndGet();
    }
    
    public final synchronized Map<String, String> getCommitData() {
        return this.segmentInfos.getUserData();
    }
    
    @Override
    public final void commit() throws IOException {
        this.ensureOpen();
        this.commitInternal(this.config.getMergePolicy());
    }
    
    public final boolean hasUncommittedChanges() {
        return this.changeCount.get() != this.lastCommitChangeCount || this.docWriter.anyChanges() || this.bufferedUpdatesStream.any();
    }
    
    private final void commitInternal(final MergePolicy mergePolicy) throws IOException {
        if (this.infoStream.isEnabled("IW")) {
            this.infoStream.message("IW", "commit: start");
        }
        synchronized (this.commitLock) {
            this.ensureOpen(false);
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "commit: enter lock");
            }
            if (this.pendingCommit == null) {
                if (this.infoStream.isEnabled("IW")) {
                    this.infoStream.message("IW", "commit: now prepare");
                }
                this.prepareCommitInternal(mergePolicy);
            }
            else if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "commit: already prepared");
            }
            this.finishCommit();
        }
    }
    
    private final void finishCommit() throws IOException {
        boolean commitCompleted = false;
        boolean finished = false;
        String committedSegmentsFileName = null;
        try {
            synchronized (this) {
                this.ensureOpen(false);
                if (this.tragedy != null) {
                    throw new IllegalStateException("this writer hit an unrecoverable error; cannot complete commit", this.tragedy);
                }
                if (this.pendingCommit != null) {
                    try {
                        if (this.infoStream.isEnabled("IW")) {
                            this.infoStream.message("IW", "commit: pendingCommit != null");
                        }
                        committedSegmentsFileName = this.pendingCommit.finishCommit(this.directory);
                        commitCompleted = true;
                        if (this.infoStream.isEnabled("IW")) {
                            this.infoStream.message("IW", "commit: done writing segments file \"" + committedSegmentsFileName + "\"");
                        }
                        this.deleter.checkpoint(this.pendingCommit, true);
                        this.segmentInfos.updateGeneration(this.pendingCommit);
                        this.lastCommitChangeCount = this.pendingCommitChangeCount;
                        this.rollbackSegments = this.pendingCommit.createBackupSegmentInfos();
                        finished = true;
                    }
                    finally {
                        this.notifyAll();
                        try {
                            if (finished) {
                                this.deleter.decRef(this.filesToCommit);
                            }
                            else if (!commitCompleted) {
                                this.deleter.decRefWhileHandlingException(this.filesToCommit);
                            }
                        }
                        finally {
                            this.pendingCommit = null;
                            this.filesToCommit = null;
                        }
                    }
                }
                else {
                    assert this.filesToCommit == null;
                    if (this.infoStream.isEnabled("IW")) {
                        this.infoStream.message("IW", "commit: pendingCommit == null; skip");
                    }
                }
            }
        }
        catch (final Throwable t) {
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "hit exception during finishCommit: " + t.getMessage());
            }
            if (commitCompleted) {
                this.tragicEvent(t, "finishCommit");
            }
            else {
                IOUtils.reThrow(t);
            }
        }
        if (this.infoStream.isEnabled("IW")) {
            this.infoStream.message("IW", String.format(Locale.ROOT, "commit: took %.1f msec", (System.nanoTime() - this.startCommitTime) / 1000000.0));
            this.infoStream.message("IW", "commit: done");
        }
    }
    
    boolean holdsFullFlushLock() {
        return Thread.holdsLock(this.fullFlushLock);
    }
    
    public final void flush() throws IOException {
        this.flush(true, true);
    }
    
    final void flush(final boolean triggerMerge, final boolean applyAllDeletes) throws IOException {
        this.ensureOpen(false);
        if (this.doFlush(applyAllDeletes) && triggerMerge) {
            this.maybeMerge(this.config.getMergePolicy(), MergeTrigger.FULL_FLUSH, -1);
        }
    }
    
    private boolean doFlush(final boolean applyAllDeletes) throws IOException {
        if (this.tragedy != null) {
            throw new IllegalStateException("this writer hit an unrecoverable error; cannot flush", this.tragedy);
        }
        this.doBeforeFlush();
        this.testPoint("startDoFlush");
        boolean success = false;
        try {
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "  start flush: applyAllDeletes=" + applyAllDeletes);
                this.infoStream.message("IW", "  index before flush " + this.segString());
            }
            boolean anyChanges = false;
            synchronized (this.fullFlushLock) {
                boolean flushSuccess = false;
                try {
                    anyChanges = this.docWriter.flushAllThreads();
                    if (!anyChanges) {
                        this.flushCount.incrementAndGet();
                    }
                    flushSuccess = true;
                }
                finally {
                    this.docWriter.finishFullFlush(this, flushSuccess);
                    this.processEvents(false, true);
                }
            }
            synchronized (this) {
                anyChanges |= this.maybeApplyDeletes(applyAllDeletes);
                this.doAfterFlush();
                success = true;
                return anyChanges;
            }
        }
        catch (final AbortingException | VirtualMachineError tragedy) {
            this.tragicEvent(tragedy, "doFlush");
            return false;
        }
        finally {
            if (!success && this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "hit exception during flush");
            }
        }
    }
    
    final synchronized boolean maybeApplyDeletes(final boolean applyAllDeletes) throws IOException {
        if (applyAllDeletes) {
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "apply all deletes during flush");
            }
            return this.applyAllDeletesAndUpdates();
        }
        if (this.infoStream.isEnabled("IW")) {
            this.infoStream.message("IW", "don't apply deletes now delTermCount=" + this.bufferedUpdatesStream.numTerms() + " bytesUsed=" + this.bufferedUpdatesStream.ramBytesUsed());
        }
        return false;
    }
    
    final synchronized boolean applyAllDeletesAndUpdates() throws IOException {
        this.flushDeletesCount.incrementAndGet();
        if (this.infoStream.isEnabled("IW")) {
            this.infoStream.message("IW", "now apply all deletes for all segments maxDoc=" + (this.docWriter.getNumDocs() + this.segmentInfos.totalMaxDoc()));
        }
        final BufferedUpdatesStream.ApplyDeletesResult result = this.bufferedUpdatesStream.applyDeletesAndUpdates(this.readerPool, this.segmentInfos.asList());
        if (result.anyDeletes) {
            this.checkpoint();
        }
        if (!this.keepFullyDeletedSegments && result.allDeleted != null) {
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "drop 100% deleted segments: " + this.segString(result.allDeleted));
            }
            for (final SegmentCommitInfo info : result.allDeleted) {
                if (!this.mergingSegments.contains(info)) {
                    this.segmentInfos.remove(info);
                    this.pendingNumDocs.addAndGet(-info.info.maxDoc());
                    this.readerPool.drop(info);
                }
            }
            this.checkpoint();
        }
        this.bufferedUpdatesStream.prune(this.segmentInfos);
        return result.anyDeletes;
    }
    
    DocumentsWriter getDocsWriter() {
        return this.docWriter;
    }
    
    public final synchronized int numRamDocs() {
        this.ensureOpen();
        return this.docWriter.getNumDocs();
    }
    
    private synchronized void ensureValidMerge(final MergePolicy.OneMerge merge) {
        for (final SegmentCommitInfo info : merge.segments) {
            if (!this.segmentInfos.contains(info)) {
                throw new MergePolicy.MergeException("MergePolicy selected a segment (" + info.info.name + ") that is not in the current index " + this.segString(), this.directoryOrig);
            }
        }
    }
    
    private void skipDeletedDoc(final DocValuesFieldUpdates.Iterator[] updatesIters, final int deletedDoc) {
        for (final DocValuesFieldUpdates.Iterator iter : updatesIters) {
            if (iter.doc() == deletedDoc) {
                iter.nextDoc();
            }
            assert iter.doc() > deletedDoc : "updateDoc=" + iter.doc() + " deletedDoc=" + deletedDoc;
        }
    }
    
    private void maybeApplyMergedDVUpdates(final MergePolicy.OneMerge merge, final MergeState mergeState, final int docUpto, final MergedDeletesAndUpdates holder, final String[] mergingFields, final DocValuesFieldUpdates[] dvFieldUpdates, final DocValuesFieldUpdates.Iterator[] updatesIters, final int curDoc) throws IOException {
        int newDoc = -1;
        for (int idx = 0; idx < mergingFields.length; ++idx) {
            final DocValuesFieldUpdates.Iterator updatesIter = updatesIters[idx];
            if (updatesIter.doc() == curDoc) {
                if (holder.mergedDeletesAndUpdates == null) {
                    holder.init(this.readerPool, merge, mergeState, false);
                }
                if (newDoc == -1) {
                    newDoc = holder.docMap.map(docUpto);
                }
                final DocValuesFieldUpdates dvUpdates = dvFieldUpdates[idx];
                dvUpdates.add(newDoc, updatesIter.value());
                updatesIter.nextDoc();
            }
            else {
                assert updatesIter.doc() > curDoc : "field=" + mergingFields[idx] + " updateDoc=" + updatesIter.doc() + " curDoc=" + curDoc;
            }
        }
    }
    
    private synchronized ReadersAndUpdates commitMergedDeletesAndUpdates(final MergePolicy.OneMerge merge, final MergeState mergeState) throws IOException {
        this.testPoint("startCommitMergeDeletes");
        final List<SegmentCommitInfo> sourceSegments = merge.segments;
        if (this.infoStream.isEnabled("IW")) {
            this.infoStream.message("IW", "commitMergeDeletes " + this.segString(merge.segments));
        }
        int docUpto = 0;
        long minGen = Long.MAX_VALUE;
        final MergedDeletesAndUpdates holder = new MergedDeletesAndUpdates();
        final DocValuesFieldUpdates.Container mergedDVUpdates = new DocValuesFieldUpdates.Container();
        for (int i = 0; i < sourceSegments.size(); ++i) {
            final SegmentCommitInfo info = sourceSegments.get(i);
            minGen = Math.min(info.getBufferedDeletesGen(), minGen);
            final int maxDoc = info.info.maxDoc();
            final Bits prevLiveDocs = merge.readers.get(i).getLiveDocs();
            final ReadersAndUpdates rld = this.readerPool.get(info, false);
            assert rld != null : "seg=" + info.info.name;
            final Bits currentLiveDocs = rld.getLiveDocs();
            final Map<String, DocValuesFieldUpdates> mergingFieldUpdates = rld.getMergingFieldUpdates();
            String[] mergingFields;
            DocValuesFieldUpdates.Iterator[] updatesIters;
            DocValuesFieldUpdates[] dvFieldUpdates;
            if (mergingFieldUpdates.isEmpty()) {
                mergingFields = null;
                updatesIters = null;
                dvFieldUpdates = null;
            }
            else {
                mergingFields = new String[mergingFieldUpdates.size()];
                dvFieldUpdates = new DocValuesFieldUpdates[mergingFieldUpdates.size()];
                updatesIters = new DocValuesFieldUpdates.Iterator[mergingFieldUpdates.size()];
                int idx = 0;
                for (final Map.Entry<String, DocValuesFieldUpdates> e : mergingFieldUpdates.entrySet()) {
                    final String field = e.getKey();
                    final DocValuesFieldUpdates updates = e.getValue();
                    mergingFields[idx] = field;
                    dvFieldUpdates[idx] = mergedDVUpdates.getUpdates(field, updates.type);
                    if (dvFieldUpdates[idx] == null) {
                        dvFieldUpdates[idx] = mergedDVUpdates.newUpdates(field, updates.type, mergeState.segmentInfo.maxDoc());
                    }
                    (updatesIters[idx] = updates.iterator()).nextDoc();
                    ++idx;
                }
            }
            if (prevLiveDocs != null) {
                assert currentLiveDocs != null;
                assert prevLiveDocs.length() == maxDoc;
                assert currentLiveDocs.length() == maxDoc;
                if (currentLiveDocs != prevLiveDocs) {
                    for (int j = 0; j < maxDoc; ++j) {
                        if (!prevLiveDocs.get(j)) {
                            assert !currentLiveDocs.get(j);
                        }
                        else {
                            if (!currentLiveDocs.get(j)) {
                                if (holder.mergedDeletesAndUpdates == null || !holder.initializedWritableLiveDocs) {
                                    holder.init(this.readerPool, merge, mergeState, true);
                                }
                                holder.mergedDeletesAndUpdates.delete(holder.docMap.map(docUpto));
                                if (mergingFields != null) {
                                    this.skipDeletedDoc(updatesIters, j);
                                }
                            }
                            else if (mergingFields != null) {
                                this.maybeApplyMergedDVUpdates(merge, mergeState, docUpto, holder, mergingFields, dvFieldUpdates, updatesIters, j);
                            }
                            ++docUpto;
                        }
                    }
                }
                else if (mergingFields != null) {
                    for (int j = 0; j < maxDoc; ++j) {
                        if (prevLiveDocs.get(j)) {
                            this.maybeApplyMergedDVUpdates(merge, mergeState, docUpto, holder, mergingFields, dvFieldUpdates, updatesIters, j);
                            ++docUpto;
                        }
                        else {
                            this.skipDeletedDoc(updatesIters, j);
                        }
                    }
                }
                else {
                    docUpto += info.info.maxDoc() - info.getDelCount() - rld.getPendingDeleteCount();
                }
            }
            else if (currentLiveDocs != null) {
                assert currentLiveDocs.length() == maxDoc;
                for (int j = 0; j < maxDoc; ++j) {
                    if (!currentLiveDocs.get(j)) {
                        if (holder.mergedDeletesAndUpdates == null || !holder.initializedWritableLiveDocs) {
                            holder.init(this.readerPool, merge, mergeState, true);
                        }
                        holder.mergedDeletesAndUpdates.delete(holder.docMap.map(docUpto));
                        if (mergingFields != null) {
                            this.skipDeletedDoc(updatesIters, j);
                        }
                    }
                    else if (mergingFields != null) {
                        this.maybeApplyMergedDVUpdates(merge, mergeState, docUpto, holder, mergingFields, dvFieldUpdates, updatesIters, j);
                    }
                    ++docUpto;
                }
            }
            else if (mergingFields != null) {
                for (int j = 0; j < maxDoc; ++j) {
                    this.maybeApplyMergedDVUpdates(merge, mergeState, docUpto, holder, mergingFields, dvFieldUpdates, updatesIters, j);
                    ++docUpto;
                }
            }
            else {
                docUpto += info.info.maxDoc();
            }
        }
        assert docUpto == merge.info.info.maxDoc();
        if (mergedDVUpdates.any()) {
            boolean success = false;
            try {
                holder.mergedDeletesAndUpdates.writeFieldUpdates(this.directory, mergedDVUpdates);
                success = true;
            }
            finally {
                if (!success) {
                    holder.mergedDeletesAndUpdates.dropChanges();
                    this.readerPool.drop(merge.info);
                }
            }
        }
        if (this.infoStream.isEnabled("IW")) {
            if (holder.mergedDeletesAndUpdates == null) {
                this.infoStream.message("IW", "no new deletes or field updates since merge started");
            }
            else {
                String msg = holder.mergedDeletesAndUpdates.getPendingDeleteCount() + " new deletes";
                if (mergedDVUpdates.any()) {
                    msg = msg + " and " + mergedDVUpdates.size() + " new field updates";
                }
                msg += " since merge started";
                this.infoStream.message("IW", msg);
            }
        }
        merge.info.setBufferedDeletesGen(minGen);
        return holder.mergedDeletesAndUpdates;
    }
    
    private synchronized boolean commitMerge(final MergePolicy.OneMerge merge, final MergeState mergeState) throws IOException {
        this.testPoint("startCommitMerge");
        if (this.tragedy != null) {
            throw new IllegalStateException("this writer hit an unrecoverable error; cannot complete merge", this.tragedy);
        }
        if (this.infoStream.isEnabled("IW")) {
            this.infoStream.message("IW", "commitMerge: " + this.segString(merge.segments) + " index=" + this.segString());
        }
        assert merge.registerDone;
        if (merge.rateLimiter.getAbort()) {
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "commitMerge: skip: it was aborted");
            }
            this.readerPool.drop(merge.info);
            this.deleteNewFiles(merge.info.files());
            return false;
        }
        final ReadersAndUpdates mergedUpdates = (merge.info.info.maxDoc() == 0) ? null : this.commitMergedDeletesAndUpdates(merge, mergeState);
        assert !this.segmentInfos.contains(merge.info);
        final boolean allDeleted = merge.segments.size() == 0 || merge.info.info.maxDoc() == 0 || (mergedUpdates != null && mergedUpdates.getPendingDeleteCount() == merge.info.info.maxDoc());
        if (this.infoStream.isEnabled("IW") && allDeleted) {
            this.infoStream.message("IW", "merged segment " + merge.info + " is 100% deleted" + (this.keepFullyDeletedSegments ? "" : "; skipping insert"));
        }
        final boolean dropSegment = allDeleted && !this.keepFullyDeletedSegments;
        assert !(!dropSegment);
        assert !(!dropSegment);
        if (mergedUpdates != null) {
            boolean success = false;
            try {
                if (dropSegment) {
                    mergedUpdates.dropChanges();
                }
                this.readerPool.release(mergedUpdates, false);
                success = true;
            }
            finally {
                if (!success) {
                    mergedUpdates.dropChanges();
                    this.readerPool.drop(merge.info);
                }
            }
        }
        this.segmentInfos.applyMergeChanges(merge, dropSegment);
        final int delDocCount = merge.totalMaxDoc - merge.info.info.maxDoc();
        assert delDocCount >= 0;
        this.pendingNumDocs.addAndGet(-delDocCount);
        if (dropSegment) {
            assert !this.segmentInfos.contains(merge.info);
            this.readerPool.drop(merge.info);
            this.deleteNewFiles(merge.info.files());
        }
        boolean success2 = false;
        try {
            this.closeMergeReaders(merge, false);
            success2 = true;
        }
        finally {
            if (success2) {
                this.checkpoint();
            }
            else {
                try {
                    this.checkpoint();
                }
                catch (final Throwable t) {}
            }
        }
        this.deleter.deletePendingFiles();
        if (this.infoStream.isEnabled("IW")) {
            this.infoStream.message("IW", "after commitMerge: " + this.segString());
        }
        if (merge.maxNumSegments != -1 && !dropSegment && !this.segmentsToMerge.containsKey(merge.info)) {
            this.segmentsToMerge.put(merge.info, Boolean.FALSE);
        }
        return true;
    }
    
    private final void handleMergeException(final Throwable t, final MergePolicy.OneMerge merge) throws IOException {
        if (this.infoStream.isEnabled("IW")) {
            this.infoStream.message("IW", "handleMergeException: merge=" + this.segString(merge.segments) + " exc=" + t);
        }
        merge.setException(t);
        this.addMergeException(merge);
        if (t instanceof MergePolicy.MergeAbortedException) {
            if (merge.isExternal) {
                throw (MergePolicy.MergeAbortedException)t;
            }
        }
        else {
            IOUtils.reThrow(t);
        }
    }
    
    public void merge(final MergePolicy.OneMerge merge) throws IOException {
        boolean success = false;
        this.rateLimiters.set(merge.rateLimiter);
        final long t0 = System.currentTimeMillis();
        final MergePolicy mergePolicy = this.config.getMergePolicy();
        try {
            try {
                this.mergeInit(merge);
                if (this.infoStream.isEnabled("IW")) {
                    this.infoStream.message("IW", "now merge\n  merge=" + this.segString(merge.segments) + "\n  index=" + this.segString());
                }
                this.mergeMiddle(merge, mergePolicy);
                this.mergeSuccess(merge);
                success = true;
            }
            catch (final Throwable t2) {
                this.handleMergeException(t2, merge);
            }
            finally {
                synchronized (this) {
                    this.mergeFinish(merge);
                    if (!success) {
                        if (this.infoStream.isEnabled("IW")) {
                            this.infoStream.message("IW", "hit exception during merge");
                        }
                    }
                    else if (!merge.rateLimiter.getAbort() && (merge.maxNumSegments != -1 || (!this.closed && !this.closing))) {
                        this.updatePendingMerges(mergePolicy, MergeTrigger.MERGE_FINISHED, merge.maxNumSegments);
                    }
                }
            }
        }
        catch (final Throwable t2) {
            this.tragicEvent(t2, "merge");
        }
        if (merge.info != null && !merge.rateLimiter.getAbort() && this.infoStream.isEnabled("IW")) {
            this.infoStream.message("IW", "merge time " + (System.currentTimeMillis() - t0) + " msec for " + merge.info.info.maxDoc() + " docs");
        }
    }
    
    void mergeSuccess(final MergePolicy.OneMerge merge) {
    }
    
    final synchronized boolean registerMerge(final MergePolicy.OneMerge merge) throws IOException {
        if (merge.registerDone) {
            return true;
        }
        assert merge.segments.size() > 0;
        if (this.stopMerges) {
            merge.rateLimiter.setAbort();
            throw new MergePolicy.MergeAbortedException("merge is aborted: " + this.segString(merge.segments));
        }
        boolean isExternal = false;
        for (final SegmentCommitInfo info : merge.segments) {
            if (this.mergingSegments.contains(info)) {
                if (this.infoStream.isEnabled("IW")) {
                    this.infoStream.message("IW", "reject merge " + this.segString(merge.segments) + ": segment " + this.segString(info) + " is already marked for merge");
                }
                return false;
            }
            if (!this.segmentInfos.contains(info)) {
                if (this.infoStream.isEnabled("IW")) {
                    this.infoStream.message("IW", "reject merge " + this.segString(merge.segments) + ": segment " + this.segString(info) + " does not exist in live infos");
                }
                return false;
            }
            if (info.info.dir != this.directoryOrig) {
                isExternal = true;
            }
            if (!this.segmentsToMerge.containsKey(info)) {
                continue;
            }
            merge.maxNumSegments = this.mergeMaxNumSegments;
        }
        this.ensureValidMerge(merge);
        this.pendingMerges.add(merge);
        if (this.infoStream.isEnabled("IW")) {
            this.infoStream.message("IW", "add merge to pendingMerges: " + this.segString(merge.segments) + " [total " + this.pendingMerges.size() + " pending]");
        }
        merge.mergeGen = this.mergeGen;
        merge.isExternal = isExternal;
        if (this.infoStream.isEnabled("IW")) {
            final StringBuilder builder = new StringBuilder("registerMerge merging= [");
            for (final SegmentCommitInfo info2 : this.mergingSegments) {
                builder.append(info2.info.name).append(", ");
            }
            builder.append("]");
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", builder.toString());
            }
        }
        for (final SegmentCommitInfo info : merge.segments) {
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "registerMerge info=" + this.segString(info));
            }
            this.mergingSegments.add(info);
        }
        assert merge.estimatedMergeBytes == 0L;
        assert merge.totalMergeBytes == 0L;
        for (final SegmentCommitInfo info : merge.segments) {
            if (info.info.maxDoc() > 0) {
                final int delCount = this.numDeletedDocs(info);
                assert delCount <= info.info.maxDoc();
                final double delRatio = delCount / (double)info.info.maxDoc();
                merge.estimatedMergeBytes += (long)(info.sizeInBytes() * (1.0 - delRatio));
                merge.totalMergeBytes += info.sizeInBytes();
            }
        }
        return merge.registerDone = true;
    }
    
    final synchronized void mergeInit(final MergePolicy.OneMerge merge) throws IOException {
        boolean success = false;
        try {
            this._mergeInit(merge);
            success = true;
        }
        finally {
            if (!success) {
                if (this.infoStream.isEnabled("IW")) {
                    this.infoStream.message("IW", "hit exception in mergeInit");
                }
                this.mergeFinish(merge);
            }
        }
    }
    
    private synchronized void _mergeInit(final MergePolicy.OneMerge merge) throws IOException {
        this.testPoint("startMergeInit");
        assert merge.registerDone;
        assert merge.maxNumSegments > 0;
        if (this.tragedy != null) {
            throw new IllegalStateException("this writer hit an unrecoverable error; cannot merge", this.tragedy);
        }
        if (merge.info != null) {
            return;
        }
        if (merge.rateLimiter.getAbort()) {
            return;
        }
        if (this.infoStream.isEnabled("IW")) {
            this.infoStream.message("IW", "now apply deletes for " + merge.segments.size() + " merging segments");
        }
        final BufferedUpdatesStream.ApplyDeletesResult result = this.bufferedUpdatesStream.applyDeletesAndUpdates(this.readerPool, merge.segments);
        if (result.anyDeletes) {
            this.checkpoint();
        }
        if (!this.keepFullyDeletedSegments && result.allDeleted != null) {
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "drop 100% deleted segments: " + result.allDeleted);
            }
            for (final SegmentCommitInfo info : result.allDeleted) {
                this.segmentInfos.remove(info);
                this.pendingNumDocs.addAndGet(-info.info.maxDoc());
                if (merge.segments.contains(info)) {
                    this.mergingSegments.remove(info);
                    merge.segments.remove(info);
                }
                this.readerPool.drop(info);
            }
            this.checkpoint();
        }
        final String mergeSegmentName = this.newSegmentName();
        final SegmentInfo si = new SegmentInfo(this.directoryOrig, Version.LATEST, mergeSegmentName, -1, false, this.codec, Collections.emptyMap(), StringHelper.randomId(), new HashMap<String, String>());
        final Map<String, String> details = new HashMap<String, String>();
        details.put("mergeMaxNumSegments", "" + merge.maxNumSegments);
        details.put("mergeFactor", Integer.toString(merge.segments.size()));
        setDiagnostics(si, "merge", details);
        merge.setMergeInfo(new SegmentCommitInfo(si, 0, -1L, -1L, -1L));
        this.bufferedUpdatesStream.prune(this.segmentInfos);
        if (this.infoStream.isEnabled("IW")) {
            this.infoStream.message("IW", "merge seg=" + merge.info.info.name + " " + this.segString(merge.segments));
        }
    }
    
    static void setDiagnostics(final SegmentInfo info, final String source) {
        setDiagnostics(info, source, null);
    }
    
    private static void setDiagnostics(final SegmentInfo info, final String source, final Map<String, String> details) {
        final Map<String, String> diagnostics = new HashMap<String, String>();
        diagnostics.put("source", source);
        diagnostics.put("lucene.version", Version.LATEST.toString());
        diagnostics.put("os", Constants.OS_NAME);
        diagnostics.put("os.arch", Constants.OS_ARCH);
        diagnostics.put("os.version", Constants.OS_VERSION);
        diagnostics.put("java.version", Constants.JAVA_VERSION);
        diagnostics.put("java.vendor", Constants.JAVA_VENDOR);
        diagnostics.put("java.runtime.version", System.getProperty("java.runtime.version", "undefined"));
        diagnostics.put("java.vm.version", System.getProperty("java.vm.version", "undefined"));
        diagnostics.put("timestamp", Long.toString(new Date().getTime()));
        if (details != null) {
            diagnostics.putAll(details);
        }
        info.setDiagnostics(diagnostics);
    }
    
    final synchronized void mergeFinish(final MergePolicy.OneMerge merge) {
        this.notifyAll();
        if (merge.registerDone) {
            final List<SegmentCommitInfo> sourceSegments = merge.segments;
            for (final SegmentCommitInfo info : sourceSegments) {
                this.mergingSegments.remove(info);
            }
            merge.registerDone = false;
        }
        this.runningMerges.remove(merge);
    }
    
    private final synchronized void closeMergeReaders(final MergePolicy.OneMerge merge, final boolean suppressExceptions) throws IOException {
        final int numSegments = merge.readers.size();
        Throwable th = null;
        final boolean drop = !suppressExceptions;
        for (int i = 0; i < numSegments; ++i) {
            final SegmentReader sr = merge.readers.get(i);
            if (sr != null) {
                try {
                    final ReadersAndUpdates rld = this.readerPool.get(sr.getSegmentInfo(), false);
                    assert rld != null;
                    if (drop) {
                        rld.dropChanges();
                    }
                    else {
                        rld.dropMergingUpdates();
                    }
                    rld.release(sr);
                    this.readerPool.release(rld);
                    if (drop) {
                        this.readerPool.drop(rld.info);
                    }
                }
                catch (final Throwable t) {
                    if (th == null) {
                        th = t;
                    }
                }
                merge.readers.set(i, null);
            }
        }
        try {
            merge.mergeFinished();
        }
        catch (final Throwable t2) {
            if (th == null) {
                th = t2;
            }
        }
        if (!suppressExceptions) {
            IOUtils.reThrow(th);
        }
    }
    
    private int mergeMiddle(final MergePolicy.OneMerge merge, final MergePolicy mergePolicy) throws IOException {
        merge.rateLimiter.checkAbort();
        final List<SegmentCommitInfo> sourceSegments = merge.segments;
        final IOContext context = new IOContext(merge.getStoreMergeInfo());
        final TrackingDirectoryWrapper dirWrapper = new TrackingDirectoryWrapper(this.mergeDirectory);
        if (this.infoStream.isEnabled("IW")) {
            this.infoStream.message("IW", "merging " + this.segString(merge.segments));
        }
        merge.readers = new ArrayList<SegmentReader>(sourceSegments.size());
        boolean success = false;
        try {
            for (int segUpto = 0; segUpto < sourceSegments.size(); ++segUpto) {
                final SegmentCommitInfo info = sourceSegments.get(segUpto);
                final ReadersAndUpdates rld = this.readerPool.get(info, true);
                SegmentReader reader;
                final Bits liveDocs;
                final int delCount;
                synchronized (this) {
                    reader = rld.getReaderForMerge(context);
                    liveDocs = rld.getReadOnlyLiveDocs();
                    delCount = rld.getPendingDeleteCount() + info.getDelCount();
                    assert reader != null;
                    assert rld.verifyDocCounts();
                    if (this.infoStream.isEnabled("IW")) {
                        if (rld.getPendingDeleteCount() != 0) {
                            this.infoStream.message("IW", "seg=" + this.segString(info) + " delCount=" + info.getDelCount() + " pendingDelCount=" + rld.getPendingDeleteCount());
                        }
                        else if (info.getDelCount() != 0) {
                            this.infoStream.message("IW", "seg=" + this.segString(info) + " delCount=" + info.getDelCount());
                        }
                        else {
                            this.infoStream.message("IW", "seg=" + this.segString(info) + " no deletes");
                        }
                    }
                }
                if (reader.numDeletedDocs() != delCount) {
                    assert delCount > reader.numDeletedDocs();
                    final SegmentReader newReader;
                    synchronized (this) {
                        newReader = new SegmentReader(info, reader, liveDocs, info.info.maxDoc() - delCount);
                    }
                    boolean released = false;
                    try {
                        rld.release(reader);
                        released = true;
                    }
                    finally {
                        if (!released) {
                            newReader.decRef();
                        }
                    }
                    reader = newReader;
                }
                merge.readers.add(reader);
                assert delCount <= info.info.maxDoc() : "delCount=" + delCount + " info.maxDoc=" + info.info.maxDoc() + " rld.pendingDeleteCount=" + rld.getPendingDeleteCount() + " info.getDelCount()=" + info.getDelCount();
            }
            final SegmentMerger merger = new SegmentMerger(merge.getMergeReaders(), merge.info.info, this.infoStream, dirWrapper, this.globalFieldNumberMap, context);
            merge.rateLimiter.checkAbort();
            merge.mergeStartNS = System.nanoTime();
            if (merger.shouldMerge()) {
                merger.merge();
            }
            final MergeState mergeState = merger.mergeState;
            assert mergeState.segmentInfo == merge.info.info;
            merge.info.info.setFiles(new HashSet<String>(dirWrapper.getCreatedFiles()));
            if (this.infoStream.isEnabled("IW")) {
                if (merger.shouldMerge()) {
                    final long t1 = System.nanoTime();
                    final double sec = (t1 - merge.mergeStartNS) / 1.0E9;
                    final double segmentMB = merge.info.sizeInBytes() / 1024.0 / 1024.0;
                    final double stoppedSec = merge.rateLimiter.getTotalStoppedNS() / 1.0E9;
                    final double throttleSec = merge.rateLimiter.getTotalPausedNS() / 1.0E9;
                    this.infoStream.message("IW", "merge codec=" + this.codec + " maxDoc=" + merge.info.info.maxDoc() + "; merged segment has " + (mergeState.mergeFieldInfos.hasVectors() ? "vectors" : "no vectors") + "; " + (mergeState.mergeFieldInfos.hasNorms() ? "norms" : "no norms") + "; " + (mergeState.mergeFieldInfos.hasDocValues() ? "docValues" : "no docValues") + "; " + (mergeState.mergeFieldInfos.hasProx() ? "prox" : "no prox") + "; " + (mergeState.mergeFieldInfos.hasProx() ? "freqs" : "no freqs") + "; " + String.format(Locale.ROOT, "%.1f sec (%.1f sec stopped, %.1f sec paused) to merge segment [%.2f MB, %.2f MB/sec]", sec, stoppedSec, throttleSec, segmentMB, segmentMB / sec));
                }
                else {
                    this.infoStream.message("IW", "skip merging fully deleted segments");
                }
            }
            if (!merger.shouldMerge()) {
                assert merge.info.info.maxDoc() == 0;
                this.commitMerge(merge, mergeState);
                return 0;
            }
            else {
                assert merge.info.info.maxDoc() > 0;
                final boolean useCompoundFile;
                synchronized (this) {
                    useCompoundFile = mergePolicy.useCompoundFile(this.segmentInfos, merge.info, this);
                }
                if (useCompoundFile) {
                    success = false;
                    final Collection<String> filesToRemove = merge.info.files();
                    final TrackingDirectoryWrapper trackingCFSDir = new TrackingDirectoryWrapper(this.mergeDirectory);
                    try {
                        this.createCompoundFile(this.infoStream, trackingCFSDir, merge.info.info, context);
                        success = true;
                    }
                    catch (final Throwable t2) {
                        synchronized (this) {
                            if (merge.rateLimiter.getAbort()) {
                                if (this.infoStream.isEnabled("IW")) {
                                    this.infoStream.message("IW", "hit merge abort exception creating compound file during merge");
                                }
                                final int n = 0;
                                monitorexit(this);
                                if (!success) {
                                    if (this.infoStream.isEnabled("IW")) {
                                        this.infoStream.message("IW", "hit exception creating compound file during merge");
                                    }
                                    this.deleteNewFiles(merge.info.files());
                                }
                                return n;
                            }
                            this.handleMergeException(t2, merge);
                        }
                    }
                    finally {
                        if (!success) {
                            if (this.infoStream.isEnabled("IW")) {
                                this.infoStream.message("IW", "hit exception creating compound file during merge");
                            }
                            this.deleteNewFiles(merge.info.files());
                        }
                    }
                    success = false;
                    synchronized (this) {
                        this.deleteNewFiles(filesToRemove);
                        if (merge.rateLimiter.getAbort()) {
                            if (this.infoStream.isEnabled("IW")) {
                                this.infoStream.message("IW", "abort merge after building CFS");
                            }
                            this.deleteNewFiles(merge.info.files());
                            return 0;
                        }
                    }
                    merge.info.info.setUseCompoundFile(true);
                }
                else {
                    success = false;
                }
                boolean success2 = false;
                try {
                    this.codec.segmentInfoFormat().write(this.directory, merge.info.info, context);
                    success2 = true;
                }
                finally {
                    if (!success2) {
                        this.deleteNewFiles(merge.info.files());
                    }
                }
                if (this.infoStream.isEnabled("IW")) {
                    this.infoStream.message("IW", String.format(Locale.ROOT, "merged segment size=%.3f MB vs estimate=%.3f MB", merge.info.sizeInBytes() / 1024.0 / 1024.0, merge.estimatedMergeBytes / 1024L / 1024.0));
                }
                final IndexReaderWarmer mergedSegmentWarmer = this.config.getMergedSegmentWarmer();
                if (this.poolReaders && mergedSegmentWarmer != null) {
                    final ReadersAndUpdates rld2 = this.readerPool.get(merge.info, true);
                    final SegmentReader sr = rld2.getReader(IOContext.READ);
                    try {
                        mergedSegmentWarmer.warm(sr);
                    }
                    finally {
                        synchronized (this) {
                            rld2.release(sr);
                            this.readerPool.release(rld2);
                        }
                    }
                }
                if (!this.commitMerge(merge, mergeState)) {
                    return 0;
                }
                success = true;
            }
        }
        finally {
            if (!success) {
                this.closeMergeReaders(merge, true);
            }
        }
        return merge.info.info.maxDoc();
    }
    
    synchronized void addMergeException(final MergePolicy.OneMerge merge) {
        assert merge.getException() != null;
        if (!this.mergeExceptions.contains(merge) && this.mergeGen == merge.mergeGen) {
            this.mergeExceptions.add(merge);
        }
    }
    
    final int getBufferedDeleteTermsSize() {
        return this.docWriter.getBufferedDeleteTermsSize();
    }
    
    final int getNumBufferedDeleteTerms() {
        return this.docWriter.getNumBufferedDeleteTerms();
    }
    
    synchronized SegmentCommitInfo newestSegment() {
        return (this.segmentInfos.size() > 0) ? this.segmentInfos.info(this.segmentInfos.size() - 1) : null;
    }
    
    synchronized String segString() {
        return this.segString(this.segmentInfos);
    }
    
    synchronized String segString(final Iterable<SegmentCommitInfo> infos) {
        final StringBuilder buffer = new StringBuilder();
        for (final SegmentCommitInfo info : infos) {
            if (buffer.length() > 0) {
                buffer.append(' ');
            }
            buffer.append(this.segString(info));
        }
        return buffer.toString();
    }
    
    synchronized String segString(final SegmentCommitInfo info) {
        return info.toString(this.numDeletedDocs(info) - info.getDelCount());
    }
    
    private synchronized void doWait() {
        try {
            this.wait(1000L);
        }
        catch (final InterruptedException ie) {
            throw new ThreadInterruptedException(ie);
        }
    }
    
    void setKeepFullyDeletedSegments(final boolean v) {
        this.keepFullyDeletedSegments = v;
    }
    
    boolean getKeepFullyDeletedSegments() {
        return this.keepFullyDeletedSegments;
    }
    
    private boolean filesExist(final SegmentInfos toSync) throws IOException {
        final Collection<String> files = toSync.files(false);
        for (final String fileName : files) {
            assert slowFileExists(this.directory, fileName) : "file " + fileName + " does not exist; files=" + Arrays.toString(this.directory.listAll());
            assert this.deleter.exists(fileName) : "IndexFileDeleter doesn't know about file " + fileName;
        }
        return true;
    }
    
    synchronized SegmentInfos toLiveInfos(final SegmentInfos sis) {
        final SegmentInfos newSIS = new SegmentInfos();
        final Map<SegmentCommitInfo, SegmentCommitInfo> liveSIS = new HashMap<SegmentCommitInfo, SegmentCommitInfo>();
        for (final SegmentCommitInfo info : this.segmentInfos) {
            liveSIS.put(info, info);
        }
        for (SegmentCommitInfo info : sis) {
            final SegmentCommitInfo liveInfo = liveSIS.get(info);
            if (liveInfo != null) {
                info = liveInfo;
            }
            newSIS.add(info);
        }
        return newSIS;
    }
    
    private void startCommit(final SegmentInfos toSync) throws IOException {
        this.testPoint("startStartCommit");
        assert this.pendingCommit == null;
        if (this.tragedy != null) {
            throw new IllegalStateException("this writer hit an unrecoverable error; cannot commit", this.tragedy);
        }
        try {
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "startCommit(): start");
            }
            synchronized (this) {
                if (this.lastCommitChangeCount > this.changeCount.get()) {
                    throw new IllegalStateException("lastCommitChangeCount=" + this.lastCommitChangeCount + ",changeCount=" + this.changeCount);
                }
                if (this.pendingCommitChangeCount == this.lastCommitChangeCount) {
                    if (this.infoStream.isEnabled("IW")) {
                        this.infoStream.message("IW", "  skip startCommit(): no changes pending");
                    }
                    try {
                        this.deleter.decRef(this.filesToCommit);
                    }
                    finally {
                        this.filesToCommit = null;
                    }
                    return;
                }
                if (this.infoStream.isEnabled("IW")) {
                    this.infoStream.message("IW", "startCommit index=" + this.segString(this.toLiveInfos(toSync)) + " changeCount=" + this.changeCount);
                }
                assert this.filesExist(toSync);
            }
            this.testPoint("midStartCommit");
            boolean pendingCommitSet = false;
            try {
                this.testPoint("midStartCommit2");
                synchronized (this) {
                    assert this.pendingCommit == null;
                    assert this.segmentInfos.getGeneration() == toSync.getGeneration();
                    toSync.prepareCommit(this.directory);
                    if (this.infoStream.isEnabled("IW")) {
                        this.infoStream.message("IW", "startCommit: wrote pending segments file \"" + IndexFileNames.fileNameFromGeneration("pending_segments", "", toSync.getGeneration()) + "\"");
                    }
                    pendingCommitSet = true;
                    this.pendingCommit = toSync;
                }
                boolean success = false;
                Collection<String> filesToSync;
                try {
                    filesToSync = toSync.files(false);
                    this.directory.sync(filesToSync);
                    success = true;
                }
                finally {
                    if (!success) {
                        pendingCommitSet = false;
                        this.pendingCommit = null;
                        toSync.rollbackCommit(this.directory);
                    }
                }
                if (this.infoStream.isEnabled("IW")) {
                    this.infoStream.message("IW", "done all syncs: " + filesToSync);
                }
                this.testPoint("midStartCommitSuccess");
            }
            finally {
                synchronized (this) {
                    this.segmentInfos.updateGeneration(toSync);
                    if (!pendingCommitSet) {
                        if (this.infoStream.isEnabled("IW")) {
                            this.infoStream.message("IW", "hit exception committing segments file");
                        }
                        this.deleter.decRefWhileHandlingException(this.filesToCommit);
                        this.filesToCommit = null;
                    }
                }
            }
        }
        catch (final VirtualMachineError tragedy) {
            this.tragicEvent(tragedy, "startCommit");
        }
        this.testPoint("finishStartCommit");
    }
    
    @Deprecated
    public static boolean isLocked(final Directory directory) throws IOException {
        try {
            directory.obtainLock("write.lock").close();
            return false;
        }
        catch (final LockObtainFailedException failed) {
            return true;
        }
    }
    
    void tragicEvent(Throwable tragedy, final String location) throws IOException {
        if (tragedy instanceof AbortingException) {
            tragedy = tragedy.getCause();
        }
        assert !(tragedy instanceof MergePolicy.MergeAbortedException);
        assert !Thread.holdsLock(this);
        assert tragedy != null;
        if (this.infoStream.isEnabled("IW")) {
            this.infoStream.message("IW", "hit tragic " + tragedy.getClass().getSimpleName() + " inside " + location);
        }
        synchronized (this) {
            if (this.tragedy != null) {
                IOUtils.reThrow(tragedy);
            }
            this.tragedy = tragedy;
        }
        if (this.shouldClose(false)) {
            this.rollbackInternal();
        }
        IOUtils.reThrow(tragedy);
    }
    
    public Throwable getTragicException() {
        return this.tragedy;
    }
    
    public boolean isOpen() {
        return !this.closing && !this.closed;
    }
    
    private final void testPoint(final String message) {
        if (this.enableTestPoints) {
            assert this.infoStream.isEnabled("TP");
            this.infoStream.message("TP", message);
        }
    }
    
    synchronized boolean nrtIsCurrent(final SegmentInfos infos) {
        this.ensureOpen();
        final boolean isCurrent = infos.version == this.segmentInfos.version && !this.docWriter.anyChanges() && !this.bufferedUpdatesStream.any();
        if (this.infoStream.isEnabled("IW") && !isCurrent) {
            this.infoStream.message("IW", "nrtIsCurrent: infoVersion matches: " + (infos.version == this.segmentInfos.version) + "; DW changes: " + this.docWriter.anyChanges() + "; BD changes: " + this.bufferedUpdatesStream.any());
        }
        return isCurrent;
    }
    
    synchronized boolean isClosed() {
        return this.closed;
    }
    
    public synchronized void deleteUnusedFiles() throws IOException {
        this.ensureOpen(false);
        this.deleter.deletePendingFiles();
        this.deleter.revisitPolicy();
    }
    
    private synchronized void deletePendingFiles() throws IOException {
        this.deleter.deletePendingFiles();
    }
    
    final void createCompoundFile(final InfoStream infoStream, final TrackingDirectoryWrapper directory, final SegmentInfo info, final IOContext context) throws IOException {
        if (!directory.getCreatedFiles().isEmpty()) {
            throw new IllegalStateException("pass a clean trackingdir for CFS creation");
        }
        if (infoStream.isEnabled("IW")) {
            infoStream.message("IW", "create compound file");
        }
        boolean success = false;
        try {
            info.getCodec().compoundFormat().write(directory, info, context);
            success = true;
        }
        finally {
            if (!success) {
                this.deleteNewFiles(directory.getCreatedFiles());
            }
        }
        info.setFiles(new HashSet<String>(directory.getCreatedFiles()));
    }
    
    final synchronized void deleteNewFiles(final Collection<String> files) throws IOException {
        this.deleter.deleteNewFiles(files);
    }
    
    final synchronized void flushFailed(final SegmentInfo info) throws IOException {
        Collection<String> files;
        try {
            files = info.files();
        }
        catch (final IllegalStateException ise) {
            files = null;
        }
        if (files != null) {
            this.deleter.deleteNewFiles(files);
        }
    }
    
    final int purge(final boolean forced) throws IOException {
        return this.docWriter.purgeBuffer(this, forced);
    }
    
    final void applyDeletesAndPurge(final boolean forcePurge) throws IOException {
        try {
            this.purge(forcePurge);
        }
        finally {
            if (this.applyAllDeletesAndUpdates()) {
                this.maybeMerge(this.config.getMergePolicy(), MergeTrigger.SEGMENT_FLUSH, -1);
            }
            this.flushCount.incrementAndGet();
        }
    }
    
    final void doAfterSegmentFlushed(final boolean triggerMerge, final boolean forcePurge) throws IOException {
        try {
            this.purge(forcePurge);
        }
        finally {
            if (triggerMerge) {
                this.maybeMerge(this.config.getMergePolicy(), MergeTrigger.SEGMENT_FLUSH, -1);
            }
        }
    }
    
    synchronized void incRefDeleter(final SegmentInfos segmentInfos) throws IOException {
        this.ensureOpen();
        this.deleter.incRef(segmentInfos, false);
        if (this.infoStream.isEnabled("IW")) {
            this.infoStream.message("IW", "incRefDeleter for NRT reader version=" + segmentInfos.getVersion() + " segments=" + this.segString(segmentInfos));
        }
    }
    
    synchronized void decRefDeleter(final SegmentInfos segmentInfos) throws IOException {
        this.ensureOpen();
        this.deleter.decRef(segmentInfos);
        if (this.infoStream.isEnabled("IW")) {
            this.infoStream.message("IW", "decRefDeleter for NRT reader version=" + segmentInfos.getVersion() + " segments=" + this.segString(segmentInfos));
        }
    }
    
    private boolean processEvents(final boolean triggerMerge, final boolean forcePurge) throws IOException {
        return this.processEvents(this.eventQueue, triggerMerge, forcePurge);
    }
    
    private boolean processEvents(final Queue<Event> queue, final boolean triggerMerge, final boolean forcePurge) throws IOException {
        boolean processed = false;
        if (this.tragedy == null) {
            Event event;
            while ((event = queue.poll()) != null) {
                processed = true;
                event.process(this, triggerMerge, forcePurge);
            }
        }
        return processed;
    }
    
    static boolean slowFileExists(final Directory dir, final String fileName) throws IOException {
        try {
            dir.openInput(fileName, IOContext.DEFAULT).close();
            return true;
        }
        catch (final NoSuchFileException | FileNotFoundException e) {
            return false;
        }
    }
    
    private void reserveDocs(final long addedNumDocs) {
        assert addedNumDocs >= 0L;
        if (this.pendingNumDocs.addAndGet(addedNumDocs) > IndexWriter.actualMaxDocs) {
            this.pendingNumDocs.addAndGet(-addedNumDocs);
            this.tooManyDocs(addedNumDocs);
        }
    }
    
    private void testReserveDocs(final long addedNumDocs) {
        assert addedNumDocs >= 0L;
        if (this.pendingNumDocs.get() + addedNumDocs > IndexWriter.actualMaxDocs) {
            this.tooManyDocs(addedNumDocs);
        }
    }
    
    private void tooManyDocs(final long addedNumDocs) {
        assert addedNumDocs >= 0L;
        throw new IllegalArgumentException("number of documents in the index cannot exceed " + IndexWriter.actualMaxDocs + " (current document count is " + this.pendingNumDocs.get() + "; added numDocs is " + addedNumDocs + ")");
    }
    
    private Directory addMergeRateLimiters(final Directory in) {
        return new FilterDirectory(in) {
            @Override
            public IndexOutput createOutput(final String name, final IOContext context) throws IOException {
                this.ensureOpen();
                IndexWriter.this.ensureOpen(false);
                assert context.context == IOContext.Context.MERGE : "got context=" + context.context;
                final MergeRateLimiter rateLimiter = IndexWriter.this.rateLimiters.get();
                assert rateLimiter != null;
                return new RateLimitedIndexOutput(rateLimiter, this.in.createOutput(name, context));
            }
        };
    }
    
    static {
        IndexWriter.actualMaxDocs = 2147483519;
    }
    
    class ReaderPool implements Closeable
    {
        private final Map<SegmentCommitInfo, ReadersAndUpdates> readerMap;
        
        ReaderPool() {
            this.readerMap = new HashMap<SegmentCommitInfo, ReadersAndUpdates>();
        }
        
        public synchronized boolean infoIsLive(final SegmentCommitInfo info) {
            final int idx = IndexWriter.this.segmentInfos.indexOf(info);
            assert idx != -1 : "info=" + info + " isn't live";
            assert IndexWriter.this.segmentInfos.info(idx) == info : "info=" + info + " doesn't match live info in segmentInfos";
            return true;
        }
        
        public synchronized void drop(final SegmentCommitInfo info) throws IOException {
            final ReadersAndUpdates rld = this.readerMap.get(info);
            if (rld != null) {
                assert info == rld.info;
                this.readerMap.remove(info);
                rld.dropReaders();
            }
        }
        
        public synchronized boolean anyPendingDeletes() {
            for (final ReadersAndUpdates rld : this.readerMap.values()) {
                if (rld.getPendingDeleteCount() != 0) {
                    return true;
                }
            }
            return false;
        }
        
        public synchronized void release(final ReadersAndUpdates rld) throws IOException {
            this.release(rld, true);
        }
        
        public synchronized void release(final ReadersAndUpdates rld, final boolean assertInfoLive) throws IOException {
            rld.decRef();
            assert rld.refCount() >= 1;
            if (!IndexWriter.this.poolReaders && rld.refCount() == 1) {
                if (rld.writeLiveDocs(IndexWriter.this.directory)) {
                    assert !(!this.infoIsLive(rld.info));
                    IndexWriter.this.checkpointNoSIS();
                }
                rld.dropReaders();
                this.readerMap.remove(rld.info);
            }
        }
        
        @Override
        public void close() throws IOException {
            this.dropAll(false);
        }
        
        synchronized void dropAll(final boolean doSave) throws IOException {
            Throwable priorE = null;
            final Iterator<Map.Entry<SegmentCommitInfo, ReadersAndUpdates>> it = this.readerMap.entrySet().iterator();
            while (it.hasNext()) {
                final ReadersAndUpdates rld = (ReadersAndUpdates)it.next().getValue();
                try {
                    if (doSave && rld.writeLiveDocs(IndexWriter.this.directory)) {
                        assert this.infoIsLive(rld.info);
                        IndexWriter.this.checkpointNoSIS();
                    }
                }
                catch (final Throwable t) {
                    if (doSave) {
                        IOUtils.reThrow(t);
                    }
                    else if (priorE == null) {
                        priorE = t;
                    }
                }
                it.remove();
                try {
                    rld.dropReaders();
                }
                catch (final Throwable t) {
                    if (doSave) {
                        IOUtils.reThrow(t);
                    }
                    else {
                        if (priorE != null) {
                            continue;
                        }
                        priorE = t;
                    }
                }
            }
            assert this.readerMap.size() == 0;
            IOUtils.reThrow(priorE);
        }
        
        public synchronized void commit(final SegmentInfos infos) throws IOException {
            for (final SegmentCommitInfo info : infos) {
                final ReadersAndUpdates rld = this.readerMap.get(info);
                if (rld != null) {
                    assert rld.info == info;
                    if (!rld.writeLiveDocs(IndexWriter.this.directory)) {
                        continue;
                    }
                    assert this.infoIsLive(info);
                    IndexWriter.this.checkpointNoSIS();
                }
            }
        }
        
        public synchronized ReadersAndUpdates get(final SegmentCommitInfo info, final boolean create) {
            IndexWriter.this.ensureOpen(false);
            assert info.info.dir == IndexWriter.this.directoryOrig : "info.dir=" + info.info.dir + " vs " + IndexWriter.this.directoryOrig;
            ReadersAndUpdates rld = this.readerMap.get(info);
            if (rld == null) {
                if (!create) {
                    return null;
                }
                rld = new ReadersAndUpdates(IndexWriter.this, info);
                this.readerMap.put(info, rld);
            }
            else {
                assert rld.info == info : "rld.info=" + rld.info + " info=" + info + " isLive?=" + this.infoIsLive(rld.info) + " vs " + this.infoIsLive(info);
            }
            if (create) {
                rld.incRef();
            }
            assert this.noDups();
            return rld;
        }
        
        private boolean noDups() {
            final Set<String> seen = new HashSet<String>();
            for (final SegmentCommitInfo info : this.readerMap.keySet()) {
                assert !seen.contains(info.info.name);
                seen.add(info.info.name);
            }
            return true;
        }
    }
    
    private static class MergedDeletesAndUpdates
    {
        ReadersAndUpdates mergedDeletesAndUpdates;
        MergePolicy.DocMap docMap;
        boolean initializedWritableLiveDocs;
        
        MergedDeletesAndUpdates() {
            this.mergedDeletesAndUpdates = null;
            this.docMap = null;
            this.initializedWritableLiveDocs = false;
        }
        
        final void init(final ReaderPool readerPool, final MergePolicy.OneMerge merge, final MergeState mergeState, final boolean initWritableLiveDocs) throws IOException {
            if (this.mergedDeletesAndUpdates == null) {
                this.mergedDeletesAndUpdates = readerPool.get(merge.info, true);
                this.docMap = merge.getDocMap(mergeState);
                assert this.docMap.isConsistent(merge.info.info.maxDoc());
            }
            if (initWritableLiveDocs && !this.initializedWritableLiveDocs) {
                this.mergedDeletesAndUpdates.initWritableLiveDocs();
                this.initializedWritableLiveDocs = true;
            }
        }
    }
    
    public abstract static class IndexReaderWarmer
    {
        protected IndexReaderWarmer() {
        }
        
        public abstract void warm(final LeafReader p0) throws IOException;
    }
    
    interface Event
    {
        void process(final IndexWriter p0, final boolean p1, final boolean p2) throws IOException;
    }
}
