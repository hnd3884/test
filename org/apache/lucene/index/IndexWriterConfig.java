package org.apache.lucene.index;

import org.apache.lucene.util.PrintStreamInfoStream;
import java.io.PrintStream;
import org.apache.lucene.util.InfoStream;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.util.SetOnce;

public final class IndexWriterConfig extends LiveIndexWriterConfig
{
    public static final int DISABLE_AUTO_FLUSH = -1;
    public static final int DEFAULT_MAX_BUFFERED_DELETE_TERMS = -1;
    public static final int DEFAULT_MAX_BUFFERED_DOCS = -1;
    public static final double DEFAULT_RAM_BUFFER_SIZE_MB = 16.0;
    @Deprecated
    public static final long WRITE_LOCK_TIMEOUT = 0L;
    public static final boolean DEFAULT_READER_POOLING = false;
    public static final int DEFAULT_RAM_PER_THREAD_HARD_LIMIT_MB = 1945;
    public static final boolean DEFAULT_USE_COMPOUND_FILE_SYSTEM = true;
    public static final boolean DEFAULT_COMMIT_ON_CLOSE = true;
    private SetOnce<IndexWriter> writer;
    
    IndexWriterConfig setIndexWriter(final IndexWriter writer) {
        if (this.writer.get() != null) {
            throw new IllegalStateException("do not share IndexWriterConfig instances across IndexWriters");
        }
        this.writer.set(writer);
        return this;
    }
    
    public IndexWriterConfig(final Analyzer analyzer) {
        super(analyzer);
        this.writer = new SetOnce<IndexWriter>();
    }
    
    public IndexWriterConfig setOpenMode(final OpenMode openMode) {
        if (openMode == null) {
            throw new IllegalArgumentException("openMode must not be null");
        }
        this.openMode = openMode;
        return this;
    }
    
    @Override
    public OpenMode getOpenMode() {
        return this.openMode;
    }
    
    public IndexWriterConfig setIndexDeletionPolicy(final IndexDeletionPolicy delPolicy) {
        if (delPolicy == null) {
            throw new IllegalArgumentException("indexDeletionPolicy must not be null");
        }
        this.delPolicy = delPolicy;
        return this;
    }
    
    @Override
    public IndexDeletionPolicy getIndexDeletionPolicy() {
        return this.delPolicy;
    }
    
    public IndexWriterConfig setIndexCommit(final IndexCommit commit) {
        this.commit = commit;
        return this;
    }
    
    @Override
    public IndexCommit getIndexCommit() {
        return this.commit;
    }
    
    public IndexWriterConfig setSimilarity(final Similarity similarity) {
        if (similarity == null) {
            throw new IllegalArgumentException("similarity must not be null");
        }
        this.similarity = similarity;
        return this;
    }
    
    @Override
    public Similarity getSimilarity() {
        return this.similarity;
    }
    
    public IndexWriterConfig setMergeScheduler(final MergeScheduler mergeScheduler) {
        if (mergeScheduler == null) {
            throw new IllegalArgumentException("mergeScheduler must not be null");
        }
        this.mergeScheduler = mergeScheduler;
        return this;
    }
    
    @Override
    public MergeScheduler getMergeScheduler() {
        return this.mergeScheduler;
    }
    
    @Deprecated
    public IndexWriterConfig setWriteLockTimeout(final long writeLockTimeout) {
        this.writeLockTimeout = writeLockTimeout;
        return this;
    }
    
    @Override
    public long getWriteLockTimeout() {
        return this.writeLockTimeout;
    }
    
    public IndexWriterConfig setCodec(final Codec codec) {
        if (codec == null) {
            throw new IllegalArgumentException("codec must not be null");
        }
        this.codec = codec;
        return this;
    }
    
    @Override
    public Codec getCodec() {
        return this.codec;
    }
    
    @Override
    public MergePolicy getMergePolicy() {
        return this.mergePolicy;
    }
    
    IndexWriterConfig setIndexerThreadPool(final DocumentsWriterPerThreadPool threadPool) {
        if (threadPool == null) {
            throw new IllegalArgumentException("threadPool must not be null");
        }
        this.indexerThreadPool = threadPool;
        return this;
    }
    
    @Override
    DocumentsWriterPerThreadPool getIndexerThreadPool() {
        return this.indexerThreadPool;
    }
    
    public IndexWriterConfig setReaderPooling(final boolean readerPooling) {
        this.readerPooling = readerPooling;
        return this;
    }
    
    @Override
    public boolean getReaderPooling() {
        return this.readerPooling;
    }
    
    IndexWriterConfig setFlushPolicy(final FlushPolicy flushPolicy) {
        if (flushPolicy == null) {
            throw new IllegalArgumentException("flushPolicy must not be null");
        }
        this.flushPolicy = flushPolicy;
        return this;
    }
    
    public IndexWriterConfig setRAMPerThreadHardLimitMB(final int perThreadHardLimitMB) {
        if (perThreadHardLimitMB <= 0 || perThreadHardLimitMB >= 2048) {
            throw new IllegalArgumentException("PerThreadHardLimit must be greater than 0 and less than 2048MB");
        }
        this.perThreadHardLimitMB = perThreadHardLimitMB;
        return this;
    }
    
    @Override
    public int getRAMPerThreadHardLimitMB() {
        return this.perThreadHardLimitMB;
    }
    
    @Override
    FlushPolicy getFlushPolicy() {
        return this.flushPolicy;
    }
    
    @Override
    public InfoStream getInfoStream() {
        return this.infoStream;
    }
    
    @Override
    public Analyzer getAnalyzer() {
        return super.getAnalyzer();
    }
    
    @Override
    public int getMaxBufferedDeleteTerms() {
        return super.getMaxBufferedDeleteTerms();
    }
    
    @Override
    public int getMaxBufferedDocs() {
        return super.getMaxBufferedDocs();
    }
    
    @Override
    public IndexWriter.IndexReaderWarmer getMergedSegmentWarmer() {
        return super.getMergedSegmentWarmer();
    }
    
    @Override
    public double getRAMBufferSizeMB() {
        return super.getRAMBufferSizeMB();
    }
    
    public IndexWriterConfig setInfoStream(final InfoStream infoStream) {
        if (infoStream == null) {
            throw new IllegalArgumentException("Cannot set InfoStream implementation to null. To disable logging use InfoStream.NO_OUTPUT");
        }
        this.infoStream = infoStream;
        return this;
    }
    
    public IndexWriterConfig setInfoStream(final PrintStream printStream) {
        if (printStream == null) {
            throw new IllegalArgumentException("printStream must not be null");
        }
        return this.setInfoStream(new PrintStreamInfoStream(printStream));
    }
    
    @Override
    public IndexWriterConfig setMergePolicy(final MergePolicy mergePolicy) {
        return (IndexWriterConfig)super.setMergePolicy(mergePolicy);
    }
    
    @Override
    public IndexWriterConfig setMaxBufferedDeleteTerms(final int maxBufferedDeleteTerms) {
        return (IndexWriterConfig)super.setMaxBufferedDeleteTerms(maxBufferedDeleteTerms);
    }
    
    @Override
    public IndexWriterConfig setMaxBufferedDocs(final int maxBufferedDocs) {
        return (IndexWriterConfig)super.setMaxBufferedDocs(maxBufferedDocs);
    }
    
    @Override
    public IndexWriterConfig setMergedSegmentWarmer(final IndexWriter.IndexReaderWarmer mergeSegmentWarmer) {
        return (IndexWriterConfig)super.setMergedSegmentWarmer(mergeSegmentWarmer);
    }
    
    @Override
    public IndexWriterConfig setRAMBufferSizeMB(final double ramBufferSizeMB) {
        return (IndexWriterConfig)super.setRAMBufferSizeMB(ramBufferSizeMB);
    }
    
    @Override
    public IndexWriterConfig setUseCompoundFile(final boolean useCompoundFile) {
        return (IndexWriterConfig)super.setUseCompoundFile(useCompoundFile);
    }
    
    public IndexWriterConfig setCommitOnClose(final boolean commitOnClose) {
        this.commitOnClose = commitOnClose;
        return this;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        sb.append("writer=").append(this.writer.get()).append("\n");
        return sb.toString();
    }
    
    public enum OpenMode
    {
        CREATE, 
        APPEND, 
        CREATE_OR_APPEND;
    }
}
