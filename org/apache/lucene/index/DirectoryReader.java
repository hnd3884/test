package org.apache.lucene.index;

import java.util.Collections;
import java.nio.file.NoSuchFileException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import org.apache.lucene.store.Directory;

public abstract class DirectoryReader extends BaseCompositeReader<LeafReader>
{
    protected final Directory directory;
    
    public static DirectoryReader open(final Directory directory) throws IOException {
        return StandardDirectoryReader.open(directory, null);
    }
    
    public static DirectoryReader open(final IndexWriter writer) throws IOException {
        return open(writer, true);
    }
    
    public static DirectoryReader open(final IndexWriter writer, final boolean applyAllDeletes) throws IOException {
        return writer.getReader(applyAllDeletes);
    }
    
    public static DirectoryReader open(final IndexCommit commit) throws IOException {
        return StandardDirectoryReader.open(commit.getDirectory(), commit);
    }
    
    public static DirectoryReader openIfChanged(final DirectoryReader oldReader) throws IOException {
        final DirectoryReader newReader = oldReader.doOpenIfChanged();
        assert newReader != oldReader;
        return newReader;
    }
    
    public static DirectoryReader openIfChanged(final DirectoryReader oldReader, final IndexCommit commit) throws IOException {
        final DirectoryReader newReader = oldReader.doOpenIfChanged(commit);
        assert newReader != oldReader;
        return newReader;
    }
    
    public static DirectoryReader openIfChanged(final DirectoryReader oldReader, final IndexWriter writer) throws IOException {
        return openIfChanged(oldReader, writer, true);
    }
    
    public static DirectoryReader openIfChanged(final DirectoryReader oldReader, final IndexWriter writer, final boolean applyAllDeletes) throws IOException {
        final DirectoryReader newReader = oldReader.doOpenIfChanged(writer, applyAllDeletes);
        assert newReader != oldReader;
        return newReader;
    }
    
    public static List<IndexCommit> listCommits(final Directory dir) throws IOException {
        final String[] files = dir.listAll();
        final List<IndexCommit> commits = new ArrayList<IndexCommit>();
        final SegmentInfos latest = SegmentInfos.readLatestCommit(dir);
        final long currentGen = latest.getGeneration();
        commits.add(new StandardDirectoryReader.ReaderCommit(null, latest, dir));
        for (int i = 0; i < files.length; ++i) {
            final String fileName = files[i];
            if (fileName.startsWith("segments") && !fileName.equals("segments.gen") && SegmentInfos.generationFromSegmentsFileName(fileName) < currentGen) {
                SegmentInfos sis = null;
                try {
                    sis = SegmentInfos.readCommit(dir, fileName);
                }
                catch (final FileNotFoundException | NoSuchFileException ex) {}
                if (sis != null) {
                    commits.add(new StandardDirectoryReader.ReaderCommit(null, sis, dir));
                }
            }
        }
        Collections.sort(commits);
        return commits;
    }
    
    public static boolean indexExists(final Directory directory) throws IOException {
        final String[] files = directory.listAll();
        final String prefix = "segments_";
        for (final String file : files) {
            if (file.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
    
    protected DirectoryReader(final Directory directory, final LeafReader[] segmentReaders) throws IOException {
        super(segmentReaders);
        this.directory = directory;
    }
    
    public final Directory directory() {
        return this.directory;
    }
    
    protected abstract DirectoryReader doOpenIfChanged() throws IOException;
    
    protected abstract DirectoryReader doOpenIfChanged(final IndexCommit p0) throws IOException;
    
    protected abstract DirectoryReader doOpenIfChanged(final IndexWriter p0, final boolean p1) throws IOException;
    
    public abstract long getVersion();
    
    public abstract boolean isCurrent() throws IOException;
    
    public abstract IndexCommit getIndexCommit() throws IOException;
}
