package org.apache.lucene.index;

import java.util.Collections;
import java.io.FileNotFoundException;
import java.nio.file.NoSuchFileException;
import org.apache.lucene.util.Constants;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.store.AlreadyClosedException;
import java.util.Iterator;
import java.util.regex.Matcher;
import org.apache.lucene.util.CollectionUtil;
import java.io.IOException;
import java.util.Collection;
import java.util.Objects;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.InfoStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.Closeable;

final class IndexFileDeleter implements Closeable
{
    private final Set<String> deletable;
    private Map<String, RefCount> refCounts;
    private List<CommitPoint> commits;
    private final List<String> lastFiles;
    private List<CommitPoint> commitsToDelete;
    private final InfoStream infoStream;
    private final Directory directoryOrig;
    private final Directory directory;
    private final IndexDeletionPolicy policy;
    final boolean startingCommitDeleted;
    private SegmentInfos lastSegmentInfos;
    public static boolean VERBOSE_REF_COUNTS;
    private final IndexWriter writer;
    
    private boolean locked() {
        return this.writer == null || Thread.holdsLock(this.writer);
    }
    
    public IndexFileDeleter(final String[] files, final Directory directoryOrig, final Directory directory, final IndexDeletionPolicy policy, final SegmentInfos segmentInfos, final InfoStream infoStream, final IndexWriter writer, final boolean initialIndexExists, final boolean isReaderInit) throws IOException {
        this.deletable = new HashSet<String>();
        this.refCounts = new HashMap<String, RefCount>();
        this.commits = new ArrayList<CommitPoint>();
        this.lastFiles = new ArrayList<String>();
        this.commitsToDelete = new ArrayList<CommitPoint>();
        Objects.requireNonNull(writer);
        this.infoStream = infoStream;
        this.writer = writer;
        final String currentSegmentsFile = segmentInfos.getSegmentsFileName();
        if (infoStream.isEnabled("IFD")) {
            infoStream.message("IFD", "init: current segments file is \"" + currentSegmentsFile + "\"; deletionPolicy=" + policy);
        }
        this.policy = policy;
        this.directoryOrig = directoryOrig;
        this.directory = directory;
        final long currentGen = segmentInfos.getGeneration();
        CommitPoint currentCommitPoint = null;
        if (currentSegmentsFile != null) {
            final Matcher m = IndexFileNames.CODEC_FILE_PATTERN.matcher("");
            for (final String fileName : files) {
                m.reset(fileName);
                if (!fileName.endsWith("write.lock") && (m.matches() || fileName.startsWith("segments") || fileName.startsWith("pending_segments"))) {
                    this.getRefCount(fileName);
                    if (fileName.startsWith("segments") && !fileName.equals("segments.gen")) {
                        if (infoStream.isEnabled("IFD")) {
                            infoStream.message("IFD", "init: load commit \"" + fileName + "\"");
                        }
                        final SegmentInfos sis = SegmentInfos.readCommit(directoryOrig, fileName);
                        final CommitPoint commitPoint = new CommitPoint(this.commitsToDelete, directoryOrig, sis);
                        if (sis.getGeneration() == segmentInfos.getGeneration()) {
                            currentCommitPoint = commitPoint;
                        }
                        this.commits.add(commitPoint);
                        this.incRef(sis, true);
                        if (this.lastSegmentInfos == null || sis.getGeneration() > this.lastSegmentInfos.getGeneration()) {
                            this.lastSegmentInfos = sis;
                        }
                    }
                }
            }
        }
        if (currentCommitPoint == null && currentSegmentsFile != null && initialIndexExists) {
            SegmentInfos sis2 = null;
            try {
                sis2 = SegmentInfos.readCommit(directoryOrig, currentSegmentsFile);
            }
            catch (final IOException e) {
                throw new CorruptIndexException("unable to read current segments_N file", currentSegmentsFile, e);
            }
            if (infoStream.isEnabled("IFD")) {
                infoStream.message("IFD", "forced open of current segments file " + segmentInfos.getSegmentsFileName());
            }
            currentCommitPoint = new CommitPoint(this.commitsToDelete, directoryOrig, sis2);
            this.commits.add(currentCommitPoint);
            this.incRef(sis2, true);
        }
        if (isReaderInit) {
            this.checkpoint(segmentInfos, false);
        }
        CollectionUtil.timSort(this.commits);
        inflateGens(segmentInfos, this.refCounts.keySet(), infoStream);
        for (final Map.Entry<String, RefCount> entry : this.refCounts.entrySet()) {
            final RefCount rc = entry.getValue();
            final String fileName2 = entry.getKey();
            if (0 == rc.count) {
                if (fileName2.startsWith("segments") && !fileName2.equals("segments.gen")) {
                    throw new IllegalStateException("file \"" + fileName2 + "\" has refCount=0, which should never happen on init");
                }
                if (infoStream.isEnabled("IFD")) {
                    infoStream.message("IFD", "init: removing unreferenced file \"" + fileName2 + "\"");
                }
                this.deleteFile(fileName2);
            }
        }
        policy.onInit(this.commits);
        this.checkpoint(segmentInfos, false);
        if (currentCommitPoint == null) {
            this.startingCommitDeleted = false;
        }
        else {
            this.startingCommitDeleted = currentCommitPoint.isDeleted();
        }
        this.deleteCommits();
    }
    
    static void inflateGens(final SegmentInfos infos, final Collection<String> files, final InfoStream infoStream) {
        long maxSegmentGen = Long.MIN_VALUE;
        int maxSegmentName = Integer.MIN_VALUE;
        final Map<String, Long> maxPerSegmentGen = new HashMap<String, Long>();
        for (final String fileName : files) {
            if (!fileName.equals("segments.gen")) {
                if (fileName.equals("write.lock")) {
                    continue;
                }
                if (fileName.startsWith("segments")) {
                    try {
                        maxSegmentGen = Math.max(SegmentInfos.generationFromSegmentsFileName(fileName), maxSegmentGen);
                    }
                    catch (final NumberFormatException ignore) {}
                }
                else if (fileName.startsWith("pending_segments")) {
                    try {
                        maxSegmentGen = Math.max(SegmentInfos.generationFromSegmentsFileName(fileName.substring(8)), maxSegmentGen);
                    }
                    catch (final NumberFormatException ignore) {}
                }
                else {
                    final String segmentName = IndexFileNames.parseSegmentName(fileName);
                    assert segmentName.startsWith("_") : "wtf? file=" + fileName;
                    maxSegmentName = Math.max(maxSegmentName, Integer.parseInt(segmentName.substring(1), 36));
                    Long curGen = maxPerSegmentGen.get(segmentName);
                    if (curGen == null) {
                        curGen = 0L;
                    }
                    try {
                        curGen = Math.max(curGen, IndexFileNames.parseGeneration(fileName));
                    }
                    catch (final NumberFormatException ex) {}
                    maxPerSegmentGen.put(segmentName, curGen);
                }
            }
        }
        infos.setNextWriteGeneration(Math.max(infos.getGeneration(), maxSegmentGen));
        if (infos.counter < 1 + maxSegmentName) {
            if (infoStream.isEnabled("IFD")) {
                infoStream.message("IFD", "init: inflate infos.counter to " + (1 + maxSegmentName) + " vs current=" + infos.counter);
            }
            infos.counter = 1 + maxSegmentName;
        }
        for (final SegmentCommitInfo info : infos) {
            final Long gen = maxPerSegmentGen.get(info.info.name);
            assert gen != null;
            final long genLong = gen;
            if (info.getNextWriteDelGen() < genLong + 1L) {
                if (infoStream.isEnabled("IFD")) {
                    infoStream.message("IFD", "init: seg=" + info.info.name + " set nextWriteDelGen=" + (genLong + 1L) + " vs current=" + info.getNextWriteDelGen());
                }
                info.setNextWriteDelGen(genLong + 1L);
            }
            if (info.getNextWriteFieldInfosGen() < genLong + 1L) {
                if (infoStream.isEnabled("IFD")) {
                    infoStream.message("IFD", "init: seg=" + info.info.name + " set nextWriteFieldInfosGen=" + (genLong + 1L) + " vs current=" + info.getNextWriteFieldInfosGen());
                }
                info.setNextWriteFieldInfosGen(genLong + 1L);
            }
            if (info.getNextWriteDocValuesGen() >= genLong + 1L) {
                continue;
            }
            if (infoStream.isEnabled("IFD")) {
                infoStream.message("IFD", "init: seg=" + info.info.name + " set nextWriteDocValuesGen=" + (genLong + 1L) + " vs current=" + info.getNextWriteDocValuesGen());
            }
            info.setNextWriteDocValuesGen(genLong + 1L);
        }
    }
    
    void ensureOpen() throws AlreadyClosedException {
        this.writer.ensureOpen(false);
        if (this.writer.tragedy != null) {
            throw new AlreadyClosedException("refusing to delete any files: this IndexWriter hit an unrecoverable exception", this.writer.tragedy);
        }
    }
    
    boolean isClosed() {
        try {
            this.ensureOpen();
            return false;
        }
        catch (final AlreadyClosedException ace) {
            return true;
        }
    }
    
    public SegmentInfos getLastSegmentInfos() {
        return this.lastSegmentInfos;
    }
    
    private void deleteCommits() {
        int size = this.commitsToDelete.size();
        if (size > 0) {
            Throwable firstThrowable = null;
            for (int i = 0; i < size; ++i) {
                final CommitPoint commit = this.commitsToDelete.get(i);
                if (this.infoStream.isEnabled("IFD")) {
                    this.infoStream.message("IFD", "deleteCommits: now decRef commit \"" + commit.getSegmentsFileName() + "\"");
                }
                try {
                    this.decRef(commit.files);
                }
                catch (final Throwable t) {
                    if (firstThrowable == null) {
                        firstThrowable = t;
                    }
                }
            }
            this.commitsToDelete.clear();
            IOUtils.reThrowUnchecked(firstThrowable);
            size = this.commits.size();
            int readFrom = 0;
            int writeTo = 0;
            while (readFrom < size) {
                final CommitPoint commit2 = this.commits.get(readFrom);
                if (!commit2.deleted) {
                    if (writeTo != readFrom) {
                        this.commits.set(writeTo, this.commits.get(readFrom));
                    }
                    ++writeTo;
                }
                ++readFrom;
            }
            while (size > writeTo) {
                this.commits.remove(size - 1);
                --size;
            }
        }
    }
    
    void refresh() throws IOException {
        assert this.locked();
        this.deletable.clear();
        final String[] files = this.directory.listAll();
        final Matcher m = IndexFileNames.CODEC_FILE_PATTERN.matcher("");
        for (int i = 0; i < files.length; ++i) {
            final String fileName = files[i];
            m.reset(fileName);
            if (!fileName.endsWith("write.lock") && !this.refCounts.containsKey(fileName) && (m.matches() || fileName.startsWith("segments") || fileName.startsWith("pending_segments"))) {
                if (this.infoStream.isEnabled("IFD")) {
                    this.infoStream.message("IFD", "refresh: removing newly created unreferenced file \"" + fileName + "\"");
                }
                this.deletable.add(fileName);
            }
        }
        this.deletePendingFiles();
    }
    
    @Override
    public void close() {
        assert this.locked();
        if (!this.lastFiles.isEmpty()) {
            try {
                this.decRef(this.lastFiles);
            }
            finally {
                this.lastFiles.clear();
            }
        }
        this.deletePendingFiles();
    }
    
    void revisitPolicy() throws IOException {
        assert this.locked();
        if (this.infoStream.isEnabled("IFD")) {
            this.infoStream.message("IFD", "now revisitPolicy");
        }
        if (this.commits.size() > 0) {
            this.policy.onCommit(this.commits);
            this.deleteCommits();
        }
    }
    
    public void deletePendingFiles() {
        assert this.locked();
        final List<String> toDelete = new ArrayList<String>(this.deletable);
        for (final String fileName : toDelete) {
            final RefCount rc = this.refCounts.get(fileName);
            if (rc != null && rc.count > 0) {
                throw new IllegalStateException("file \"" + fileName + "\" is in pending delete set but has non-zero refCount=" + rc.count);
            }
            if (fileName.startsWith("segments") && !this.deleteFile(fileName)) {
                if (this.infoStream.isEnabled("IFD")) {
                    this.infoStream.message("IFD", "failed to remove commit point \"" + fileName + "\"; skipping deletion of all other pending files");
                }
                return;
            }
        }
        for (final String fileName : toDelete) {
            if (!fileName.startsWith("segments")) {
                this.deleteFile(fileName);
            }
        }
    }
    
    public void checkpoint(final SegmentInfos segmentInfos, final boolean isCommit) throws IOException {
        assert this.locked();
        assert Thread.holdsLock(this.writer);
        long t0 = 0L;
        if (this.infoStream.isEnabled("IFD")) {
            t0 = System.nanoTime();
            this.infoStream.message("IFD", "now checkpoint \"" + this.writer.segString(this.writer.toLiveInfos(segmentInfos)) + "\" [" + segmentInfos.size() + " segments " + "; isCommit = " + isCommit + "]");
        }
        this.incRef(segmentInfos, isCommit);
        if (isCommit) {
            this.commits.add(new CommitPoint(this.commitsToDelete, this.directoryOrig, segmentInfos));
            this.policy.onCommit(this.commits);
            this.deleteCommits();
        }
        else {
            try {
                this.decRef(this.lastFiles);
            }
            finally {
                this.lastFiles.clear();
            }
            this.lastFiles.addAll(segmentInfos.files(false));
        }
        if (this.infoStream.isEnabled("IFD")) {
            final long t2 = System.nanoTime();
            this.infoStream.message("IFD", (t2 - t0) / 1000000L + " msec to checkpoint");
        }
    }
    
    void incRef(final SegmentInfos segmentInfos, final boolean isCommit) throws IOException {
        assert this.locked();
        for (final String fileName : segmentInfos.files(isCommit)) {
            this.incRef(fileName);
        }
    }
    
    void incRef(final Collection<String> files) {
        assert this.locked();
        for (final String file : files) {
            this.incRef(file);
        }
    }
    
    void incRef(final String fileName) {
        assert this.locked();
        final RefCount rc = this.getRefCount(fileName);
        if (this.infoStream.isEnabled("IFD") && IndexFileDeleter.VERBOSE_REF_COUNTS) {
            this.infoStream.message("IFD", "  IncRef \"" + fileName + "\": pre-incr count is " + rc.count);
        }
        rc.IncRef();
    }
    
    void decRef(final Collection<String> files) {
        assert this.locked();
        Throwable firstThrowable = null;
        for (final String file : files) {
            try {
                this.decRef(file);
            }
            catch (final Throwable t) {
                if (firstThrowable != null) {
                    continue;
                }
                firstThrowable = t;
            }
        }
        try {
            this.deletePendingFiles();
        }
        catch (final Throwable t2) {
            if (firstThrowable == null) {
                firstThrowable = t2;
            }
        }
        IOUtils.reThrowUnchecked(firstThrowable);
    }
    
    void decRefWhileHandlingException(final Collection<String> files) {
        assert this.locked();
        for (final String file : files) {
            try {
                this.decRef(file);
            }
            catch (final Throwable t) {}
        }
        try {
            this.deletePendingFiles();
        }
        catch (final Throwable t2) {}
    }
    
    private void decRef(final String fileName) {
        assert this.locked();
        final RefCount rc = this.getRefCount(fileName);
        if (this.infoStream.isEnabled("IFD") && IndexFileDeleter.VERBOSE_REF_COUNTS) {
            this.infoStream.message("IFD", "  DecRef \"" + fileName + "\": pre-decr count is " + rc.count);
        }
        if (0 == rc.DecRef()) {
            try {
                this.deletable.add(fileName);
            }
            finally {
                this.refCounts.remove(fileName);
            }
        }
    }
    
    void decRef(final SegmentInfos segmentInfos) throws IOException {
        assert this.locked();
        this.decRef(segmentInfos.files(false));
    }
    
    public boolean exists(final String fileName) {
        assert this.locked();
        return this.refCounts.containsKey(fileName) && this.getRefCount(fileName).count > 0;
    }
    
    private RefCount getRefCount(final String fileName) {
        assert this.locked();
        RefCount rc;
        if (!this.refCounts.containsKey(fileName)) {
            rc = new RefCount(fileName);
            assert !this.deletable.contains(fileName) : "file \"" + fileName + "\" cannot be incRef'd: it's already pending delete";
            this.refCounts.put(fileName, rc);
        }
        else {
            rc = this.refCounts.get(fileName);
        }
        return rc;
    }
    
    void deleteNewFiles(final Collection<String> files) throws IOException {
        assert this.locked();
        for (final String fileName : files) {
            if (!this.refCounts.containsKey(fileName) || this.refCounts.get(fileName).count == 0) {
                if (this.infoStream.isEnabled("IFD")) {
                    this.infoStream.message("IFD", "will delete new file \"" + fileName + "\"");
                }
                this.deletable.add(fileName);
            }
        }
        this.deletePendingFiles();
    }
    
    private boolean deleteFile(final String fileName) {
        assert this.locked();
        this.ensureOpen();
        try {
            if (this.infoStream.isEnabled("IFD")) {
                this.infoStream.message("IFD", "delete \"" + fileName + "\"");
            }
            this.directory.deleteFile(fileName);
            this.deletable.remove(fileName);
            return true;
        }
        catch (final IOException e) {
            assert !(e instanceof NoSuchFileException) : "hit unexpected NoSuchFileException: file=" + fileName;
            assert !(e instanceof FileNotFoundException) : "hit unexpected FileNotFoundException: file=" + fileName;
            if (this.infoStream.isEnabled("IFD")) {
                this.infoStream.message("IFD", "unable to remove file \"" + fileName + "\": " + e.toString() + "; Will re-try later.");
            }
            this.deletable.add(fileName);
            return false;
        }
    }
    
    static {
        IndexFileDeleter.VERBOSE_REF_COUNTS = false;
    }
    
    private static final class RefCount
    {
        final String fileName;
        boolean initDone;
        int count;
        
        RefCount(final String fileName) {
            this.fileName = fileName;
        }
        
        public int IncRef() {
            if (!this.initDone) {
                this.initDone = true;
            }
            else {
                assert this.count > 0 : Thread.currentThread().getName() + ": RefCount is 0 pre-increment for file \"" + this.fileName + "\"";
            }
            return ++this.count;
        }
        
        public int DecRef() {
            assert this.count > 0 : Thread.currentThread().getName() + ": RefCount is 0 pre-decrement for file \"" + this.fileName + "\"";
            return --this.count;
        }
    }
    
    private static final class CommitPoint extends IndexCommit
    {
        Collection<String> files;
        String segmentsFileName;
        boolean deleted;
        Directory directoryOrig;
        Collection<CommitPoint> commitsToDelete;
        long generation;
        final Map<String, String> userData;
        private final int segmentCount;
        
        public CommitPoint(final Collection<CommitPoint> commitsToDelete, final Directory directoryOrig, final SegmentInfos segmentInfos) throws IOException {
            this.directoryOrig = directoryOrig;
            this.commitsToDelete = commitsToDelete;
            this.userData = segmentInfos.getUserData();
            this.segmentsFileName = segmentInfos.getSegmentsFileName();
            this.generation = segmentInfos.getGeneration();
            this.files = Collections.unmodifiableCollection((Collection<? extends String>)segmentInfos.files(true));
            this.segmentCount = segmentInfos.size();
        }
        
        @Override
        public String toString() {
            return "IndexFileDeleter.CommitPoint(" + this.segmentsFileName + ")";
        }
        
        @Override
        public int getSegmentCount() {
            return this.segmentCount;
        }
        
        @Override
        public String getSegmentsFileName() {
            return this.segmentsFileName;
        }
        
        @Override
        public Collection<String> getFileNames() {
            return this.files;
        }
        
        @Override
        public Directory getDirectory() {
            return this.directoryOrig;
        }
        
        @Override
        public long getGeneration() {
            return this.generation;
        }
        
        @Override
        public Map<String, String> getUserData() {
            return this.userData;
        }
        
        @Override
        public void delete() {
            if (!this.deleted) {
                this.deleted = true;
                this.commitsToDelete.add(this);
            }
        }
        
        @Override
        public boolean isDeleted() {
            return this.deleted;
        }
    }
}
