package org.apache.lucene.index;

import java.util.Collection;
import org.apache.lucene.store.AlreadyClosedException;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.io.Closeable;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.store.IOContext;
import java.io.IOException;
import org.apache.lucene.store.Directory;

final class StandardDirectoryReader extends DirectoryReader
{
    final IndexWriter writer;
    final SegmentInfos segmentInfos;
    private final boolean applyAllDeletes;
    
    StandardDirectoryReader(final Directory directory, final LeafReader[] readers, final IndexWriter writer, final SegmentInfos sis, final boolean applyAllDeletes) throws IOException {
        super(directory, readers);
        this.writer = writer;
        this.segmentInfos = sis;
        this.applyAllDeletes = applyAllDeletes;
    }
    
    static DirectoryReader open(final Directory directory, final IndexCommit commit) throws IOException {
        return new SegmentInfos.FindSegmentsFile<DirectoryReader>(directory) {
            @Override
            protected DirectoryReader doBody(final String segmentFileName) throws IOException {
                final SegmentInfos sis = SegmentInfos.readCommit(this.directory, segmentFileName);
                final SegmentReader[] readers = new SegmentReader[sis.size()];
                boolean success = false;
                try {
                    for (int i = sis.size() - 1; i >= 0; --i) {
                        readers[i] = new SegmentReader(sis.info(i), IOContext.READ);
                    }
                    final DirectoryReader reader = new StandardDirectoryReader(this.directory, readers, null, sis, false);
                    success = true;
                    return reader;
                }
                finally {
                    if (!success) {
                        IOUtils.closeWhileHandlingException((Closeable[])readers);
                    }
                }
            }
        }.run(commit);
    }
    
    static DirectoryReader open(final IndexWriter writer, final SegmentInfos infos, final boolean applyAllDeletes) throws IOException {
        final int numSegments = infos.size();
        final List<SegmentReader> readers = new ArrayList<SegmentReader>(numSegments);
        final Directory dir = writer.getDirectory();
        final SegmentInfos segmentInfos = infos.clone();
        int infosUpto = 0;
        boolean success = false;
        try {
            for (int i = 0; i < numSegments; ++i) {
                final SegmentCommitInfo info = infos.info(i);
                assert info.info.dir == dir;
                final ReadersAndUpdates rld = writer.readerPool.get(info, true);
                try {
                    final SegmentReader reader = rld.getReadOnlyClone(IOContext.READ);
                    if (reader.numDocs() > 0 || writer.getKeepFullyDeletedSegments()) {
                        readers.add(reader);
                        ++infosUpto;
                    }
                    else {
                        reader.decRef();
                        segmentInfos.remove(infosUpto);
                    }
                }
                finally {
                    writer.readerPool.release(rld);
                }
            }
            writer.incRefDeleter(segmentInfos);
            final StandardDirectoryReader result = new StandardDirectoryReader(dir, readers.toArray(new SegmentReader[readers.size()]), writer, segmentInfos, applyAllDeletes);
            success = true;
            return result;
        }
        finally {
            if (!success) {
                for (final SegmentReader r : readers) {
                    try {
                        r.decRef();
                    }
                    catch (final Throwable t) {}
                }
            }
        }
    }
    
    private static DirectoryReader open(final Directory directory, final SegmentInfos infos, final List<? extends LeafReader> oldReaders) throws IOException {
        final Map<String, Integer> segmentReaders = (oldReaders == null) ? Collections.emptyMap() : new HashMap<String, Integer>(oldReaders.size());
        if (oldReaders != null) {
            for (int i = 0, c = oldReaders.size(); i < c; ++i) {
                final SegmentReader sr = (SegmentReader)oldReaders.get(i);
                segmentReaders.put(sr.getSegmentName(), i);
            }
        }
        final SegmentReader[] newReaders = new SegmentReader[infos.size()];
        for (int j = infos.size() - 1; j >= 0; --j) {
            final SegmentCommitInfo commitInfo = infos.info(j);
            final Integer oldReaderIndex = segmentReaders.get(commitInfo.info.name);
            SegmentReader oldReader;
            if (oldReaderIndex == null) {
                oldReader = null;
            }
            else {
                oldReader = (SegmentReader)oldReaders.get(oldReaderIndex);
            }
            boolean success = false;
            try {
                if (oldReader == null || commitInfo.info.getUseCompoundFile() != oldReader.getSegmentInfo().info.getUseCompoundFile()) {
                    final SegmentReader newReader = new SegmentReader(commitInfo, IOContext.READ);
                    newReaders[j] = newReader;
                }
                else if (oldReader.getSegmentInfo().getDelGen() == commitInfo.getDelGen() && oldReader.getSegmentInfo().getFieldInfosGen() == commitInfo.getFieldInfosGen()) {
                    oldReader.incRef();
                    newReaders[j] = oldReader;
                }
                else {
                    assert commitInfo.info.dir == oldReader.getSegmentInfo().info.dir;
                    final boolean illegalDocCountChange = commitInfo.info.maxDoc() != oldReader.getSegmentInfo().info.maxDoc();
                    final boolean hasNeitherDeletionsNorUpdates = !commitInfo.hasDeletions() && !commitInfo.hasFieldUpdates();
                    final boolean deletesWereLost = commitInfo.getDelGen() == -1L && oldReader.getSegmentInfo().getDelGen() != -1L;
                    if (illegalDocCountChange || hasNeitherDeletionsNorUpdates || deletesWereLost) {
                        throw new IllegalStateException("same segment " + commitInfo.info.name + " has invalid changes; likely you are re-opening a reader after illegally removing index files yourself and building a new index in their place.  Use IndexWriter.deleteAll or OpenMode.CREATE instead");
                    }
                    if (oldReader.getSegmentInfo().getDelGen() == commitInfo.getDelGen()) {
                        newReaders[j] = new SegmentReader(commitInfo, oldReader, oldReader.getLiveDocs(), oldReader.numDocs());
                    }
                    else {
                        newReaders[j] = new SegmentReader(commitInfo, oldReader);
                    }
                }
                success = true;
            }
            finally {
                if (!success) {
                    decRefWhileHandlingException(newReaders);
                }
            }
        }
        return new StandardDirectoryReader(directory, newReaders, null, infos, false);
    }
    
    private static void decRefWhileHandlingException(final SegmentReader[] readers) {
        for (final SegmentReader reader : readers) {
            if (reader != null) {
                try {
                    reader.decRef();
                }
                catch (final Throwable t) {}
            }
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(this.getClass().getSimpleName());
        buffer.append('(');
        final String segmentsFile = this.segmentInfos.getSegmentsFileName();
        if (segmentsFile != null) {
            buffer.append(segmentsFile).append(":").append(this.segmentInfos.getVersion());
        }
        if (this.writer != null) {
            buffer.append(":nrt");
        }
        for (final LeafReader r : this.getSequentialSubReaders()) {
            buffer.append(' ');
            buffer.append(r);
        }
        buffer.append(')');
        return buffer.toString();
    }
    
    @Override
    protected DirectoryReader doOpenIfChanged() throws IOException {
        return this.doOpenIfChanged((IndexCommit)null);
    }
    
    @Override
    protected DirectoryReader doOpenIfChanged(final IndexCommit commit) throws IOException {
        this.ensureOpen();
        if (this.writer != null) {
            return this.doOpenFromWriter(commit);
        }
        return this.doOpenNoWriter(commit);
    }
    
    @Override
    protected DirectoryReader doOpenIfChanged(final IndexWriter writer, final boolean applyAllDeletes) throws IOException {
        this.ensureOpen();
        if (writer == this.writer && applyAllDeletes == this.applyAllDeletes) {
            return this.doOpenFromWriter(null);
        }
        return writer.getReader(applyAllDeletes);
    }
    
    private DirectoryReader doOpenFromWriter(final IndexCommit commit) throws IOException {
        if (commit != null) {
            return this.doOpenFromCommit(commit);
        }
        if (this.writer.nrtIsCurrent(this.segmentInfos)) {
            return null;
        }
        final DirectoryReader reader = this.writer.getReader(this.applyAllDeletes);
        if (reader.getVersion() == this.segmentInfos.getVersion()) {
            reader.decRef();
            return null;
        }
        return reader;
    }
    
    private DirectoryReader doOpenNoWriter(final IndexCommit commit) throws IOException {
        if (commit == null) {
            if (this.isCurrent()) {
                return null;
            }
        }
        else {
            if (this.directory != commit.getDirectory()) {
                throw new IOException("the specified commit does not match the specified Directory");
            }
            if (this.segmentInfos != null && commit.getSegmentsFileName().equals(this.segmentInfos.getSegmentsFileName())) {
                return null;
            }
        }
        return this.doOpenFromCommit(commit);
    }
    
    private DirectoryReader doOpenFromCommit(final IndexCommit commit) throws IOException {
        return new SegmentInfos.FindSegmentsFile<DirectoryReader>(this.directory) {
            @Override
            protected DirectoryReader doBody(final String segmentFileName) throws IOException {
                final SegmentInfos infos = SegmentInfos.readCommit(this.directory, segmentFileName);
                return StandardDirectoryReader.this.doOpenIfChanged(infos);
            }
        }.run(commit);
    }
    
    DirectoryReader doOpenIfChanged(final SegmentInfos infos) throws IOException {
        return open(this.directory, infos, this.getSequentialSubReaders());
    }
    
    @Override
    public long getVersion() {
        this.ensureOpen();
        return this.segmentInfos.getVersion();
    }
    
    @Override
    public boolean isCurrent() throws IOException {
        this.ensureOpen();
        if (this.writer == null || this.writer.isClosed()) {
            final SegmentInfos sis = SegmentInfos.readLatestCommit(this.directory);
            return sis.getVersion() == this.segmentInfos.getVersion();
        }
        return this.writer.nrtIsCurrent(this.segmentInfos);
    }
    
    @Override
    protected void doClose() throws IOException {
        Throwable firstExc = null;
        for (final LeafReader r : this.getSequentialSubReaders()) {
            try {
                r.decRef();
            }
            catch (final Throwable t) {
                if (firstExc != null) {
                    continue;
                }
                firstExc = t;
            }
        }
        if (this.writer != null) {
            try {
                this.writer.decRefDeleter(this.segmentInfos);
            }
            catch (final AlreadyClosedException ex) {}
        }
        IOUtils.reThrow(firstExc);
    }
    
    @Override
    public IndexCommit getIndexCommit() throws IOException {
        this.ensureOpen();
        return new ReaderCommit(this, this.segmentInfos, this.directory);
    }
    
    static final class ReaderCommit extends IndexCommit
    {
        private String segmentsFileName;
        Collection<String> files;
        Directory dir;
        long generation;
        final Map<String, String> userData;
        private final int segmentCount;
        private final StandardDirectoryReader reader;
        
        ReaderCommit(final StandardDirectoryReader reader, final SegmentInfos infos, final Directory dir) throws IOException {
            this.segmentsFileName = infos.getSegmentsFileName();
            this.dir = dir;
            this.userData = infos.getUserData();
            this.files = Collections.unmodifiableCollection((Collection<? extends String>)infos.files(true));
            this.generation = infos.getGeneration();
            this.segmentCount = infos.size();
            this.reader = reader;
        }
        
        @Override
        public String toString() {
            return "DirectoryReader.ReaderCommit(" + this.segmentsFileName + ")";
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
            return this.dir;
        }
        
        @Override
        public long getGeneration() {
            return this.generation;
        }
        
        @Override
        public boolean isDeleted() {
            return false;
        }
        
        @Override
        public Map<String, String> getUserData() {
            return this.userData;
        }
        
        @Override
        public void delete() {
            throw new UnsupportedOperationException("This IndexCommit does not support deletions");
        }
        
        @Override
        StandardDirectoryReader getReader() {
            return this.reader;
        }
    }
}
