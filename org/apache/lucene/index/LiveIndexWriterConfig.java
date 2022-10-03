package org.apache.lucene.index;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.util.InfoStream;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.analysis.Analyzer;

public class LiveIndexWriterConfig
{
    private final Analyzer analyzer;
    private volatile int maxBufferedDocs;
    private volatile double ramBufferSizeMB;
    private volatile int maxBufferedDeleteTerms;
    private volatile IndexWriter.IndexReaderWarmer mergedSegmentWarmer;
    protected volatile IndexDeletionPolicy delPolicy;
    protected volatile IndexCommit commit;
    protected volatile IndexWriterConfig.OpenMode openMode;
    protected volatile Similarity similarity;
    protected volatile MergeScheduler mergeScheduler;
    @Deprecated
    protected volatile long writeLockTimeout;
    protected volatile DocumentsWriterPerThread.IndexingChain indexingChain;
    protected volatile Codec codec;
    protected volatile InfoStream infoStream;
    protected volatile MergePolicy mergePolicy;
    protected volatile DocumentsWriterPerThreadPool indexerThreadPool;
    protected volatile boolean readerPooling;
    protected volatile FlushPolicy flushPolicy;
    protected volatile int perThreadHardLimitMB;
    protected volatile boolean useCompoundFile;
    protected boolean commitOnClose;
    
    LiveIndexWriterConfig(final Analyzer analyzer) {
        this.useCompoundFile = true;
        this.commitOnClose = true;
        this.analyzer = analyzer;
        this.ramBufferSizeMB = 16.0;
        this.maxBufferedDocs = -1;
        this.maxBufferedDeleteTerms = -1;
        this.mergedSegmentWarmer = null;
        this.delPolicy = new KeepOnlyLastCommitDeletionPolicy();
        this.commit = null;
        this.useCompoundFile = true;
        this.openMode = IndexWriterConfig.OpenMode.CREATE_OR_APPEND;
        this.similarity = IndexSearcher.getDefaultSimilarity();
        this.mergeScheduler = new ConcurrentMergeScheduler();
        this.writeLockTimeout = 0L;
        this.indexingChain = DocumentsWriterPerThread.defaultIndexingChain;
        this.codec = Codec.getDefault();
        if (this.codec == null) {
            throw new NullPointerException();
        }
        this.infoStream = InfoStream.getDefault();
        this.mergePolicy = new TieredMergePolicy();
        this.flushPolicy = new FlushByRamOrCountsPolicy();
        this.readerPooling = false;
        this.indexerThreadPool = new DocumentsWriterPerThreadPool();
        this.perThreadHardLimitMB = 1945;
    }
    
    public Analyzer getAnalyzer() {
        return this.analyzer;
    }
    
    public LiveIndexWriterConfig setMaxBufferedDeleteTerms(final int maxBufferedDeleteTerms) {
        if (maxBufferedDeleteTerms != -1 && maxBufferedDeleteTerms < 1) {
            throw new IllegalArgumentException("maxBufferedDeleteTerms must at least be 1 when enabled");
        }
        this.maxBufferedDeleteTerms = maxBufferedDeleteTerms;
        return this;
    }
    
    public int getMaxBufferedDeleteTerms() {
        return this.maxBufferedDeleteTerms;
    }
    
    public synchronized LiveIndexWriterConfig setRAMBufferSizeMB(final double ramBufferSizeMB) {
        if (ramBufferSizeMB != -1.0 && ramBufferSizeMB <= 0.0) {
            throw new IllegalArgumentException("ramBufferSize should be > 0.0 MB when enabled");
        }
        if (ramBufferSizeMB == -1.0 && this.maxBufferedDocs == -1) {
            throw new IllegalArgumentException("at least one of ramBufferSize and maxBufferedDocs must be enabled");
        }
        this.ramBufferSizeMB = ramBufferSizeMB;
        return this;
    }
    
    public double getRAMBufferSizeMB() {
        return this.ramBufferSizeMB;
    }
    
    public synchronized LiveIndexWriterConfig setMaxBufferedDocs(final int maxBufferedDocs) {
        if (maxBufferedDocs != -1 && maxBufferedDocs < 2) {
            throw new IllegalArgumentException("maxBufferedDocs must at least be 2 when enabled");
        }
        if (maxBufferedDocs == -1 && this.ramBufferSizeMB == -1.0) {
            throw new IllegalArgumentException("at least one of ramBufferSize and maxBufferedDocs must be enabled");
        }
        this.maxBufferedDocs = maxBufferedDocs;
        return this;
    }
    
    public int getMaxBufferedDocs() {
        return this.maxBufferedDocs;
    }
    
    public LiveIndexWriterConfig setMergePolicy(final MergePolicy mergePolicy) {
        if (mergePolicy == null) {
            throw new IllegalArgumentException("mergePolicy must not be null");
        }
        this.mergePolicy = mergePolicy;
        return this;
    }
    
    public LiveIndexWriterConfig setMergedSegmentWarmer(final IndexWriter.IndexReaderWarmer mergeSegmentWarmer) {
        this.mergedSegmentWarmer = mergeSegmentWarmer;
        return this;
    }
    
    public IndexWriter.IndexReaderWarmer getMergedSegmentWarmer() {
        return this.mergedSegmentWarmer;
    }
    
    public IndexWriterConfig.OpenMode getOpenMode() {
        return this.openMode;
    }
    
    public IndexDeletionPolicy getIndexDeletionPolicy() {
        return this.delPolicy;
    }
    
    public IndexCommit getIndexCommit() {
        return this.commit;
    }
    
    public Similarity getSimilarity() {
        return this.similarity;
    }
    
    public MergeScheduler getMergeScheduler() {
        return this.mergeScheduler;
    }
    
    @Deprecated
    public long getWriteLockTimeout() {
        return this.writeLockTimeout;
    }
    
    public Codec getCodec() {
        return this.codec;
    }
    
    public MergePolicy getMergePolicy() {
        return this.mergePolicy;
    }
    
    DocumentsWriterPerThreadPool getIndexerThreadPool() {
        return this.indexerThreadPool;
    }
    
    public boolean getReaderPooling() {
        return this.readerPooling;
    }
    
    DocumentsWriterPerThread.IndexingChain getIndexingChain() {
        return this.indexingChain;
    }
    
    public int getRAMPerThreadHardLimitMB() {
        return this.perThreadHardLimitMB;
    }
    
    FlushPolicy getFlushPolicy() {
        return this.flushPolicy;
    }
    
    public InfoStream getInfoStream() {
        return this.infoStream;
    }
    
    public LiveIndexWriterConfig setUseCompoundFile(final boolean useCompoundFile) {
        this.useCompoundFile = useCompoundFile;
        return this;
    }
    
    public boolean getUseCompoundFile() {
        return this.useCompoundFile;
    }
    
    public boolean getCommitOnClose() {
        return this.commitOnClose;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("analyzer=").append((this.analyzer == null) ? "null" : this.analyzer.getClass().getName()).append("\n");
        sb.append("ramBufferSizeMB=").append(this.getRAMBufferSizeMB()).append("\n");
        sb.append("maxBufferedDocs=").append(this.getMaxBufferedDocs()).append("\n");
        sb.append("maxBufferedDeleteTerms=").append(this.getMaxBufferedDeleteTerms()).append("\n");
        sb.append("mergedSegmentWarmer=").append(this.getMergedSegmentWarmer()).append("\n");
        sb.append("delPolicy=").append(this.getIndexDeletionPolicy().getClass().getName()).append("\n");
        final IndexCommit commit = this.getIndexCommit();
        sb.append("commit=").append((commit == null) ? "null" : commit).append("\n");
        sb.append("openMode=").append(this.getOpenMode()).append("\n");
        sb.append("similarity=").append(this.getSimilarity().getClass().getName()).append("\n");
        sb.append("mergeScheduler=").append(this.getMergeScheduler()).append("\n");
        sb.append("default WRITE_LOCK_TIMEOUT=").append(0L).append("\n");
        sb.append("writeLockTimeout=").append(this.getWriteLockTimeout()).append("\n");
        sb.append("codec=").append(this.getCodec()).append("\n");
        sb.append("infoStream=").append(this.getInfoStream().getClass().getName()).append("\n");
        sb.append("mergePolicy=").append(this.getMergePolicy()).append("\n");
        sb.append("indexerThreadPool=").append(this.getIndexerThreadPool()).append("\n");
        sb.append("readerPooling=").append(this.getReaderPooling()).append("\n");
        sb.append("perThreadHardLimitMB=").append(this.getRAMPerThreadHardLimitMB()).append("\n");
        sb.append("useCompoundFile=").append(this.getUseCompoundFile()).append("\n");
        sb.append("commitOnClose=").append(this.getCommitOnClose()).append("\n");
        return sb.toString();
    }
}
